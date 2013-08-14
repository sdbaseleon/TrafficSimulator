
import java.util.*;

/**
 * This is the class where all the work is done.
 * @author Stephen Baseleon
 */
public class Main {

    private int numBooths,  maxCars;
    private double mtime,  vtime,  flow;

    private QueueArray openRoad;
    private QueueArray tollLines[];
    private ArrayList<Car> finishedCars;

    /**
     * The whole program is executed in this constructor.
     * @param numBooths
     * @param mtime
     * @param vtime
     * @param flow
     */
    public Main(int numBooths, double mtime, double vtime, double flow) {
        //Prepare everything in the constructor before the simulation starts
        this.numBooths = numBooths;
        this.mtime = mtime;
        this.vtime = vtime;
        this.flow = flow;
        this.maxCars = 0;
        /**because there cannot be an array of unlimited size,
         * the average queue would be the flow * time, so twice that will be
         * more than enough
         */
        openRoad = new QueueArray((int) (flow * 3600 + 1));
        /**use an array to keep track of our toll lines
         */
        tollLines = new QueueArray[numBooths];
        /**when the simulation is finished, all of the cars that make it out of
         * the toll system will be stored in this ArrayList
         */
        finishedCars = new ArrayList<Car>();
        /**Now that we have the tollLines, it must be filled with queues of type Car.
         * Each toll queue should be 20 cars in length.
         */
        for (int i = 0; i < numBooths; i++) {
            QueueArray<Car> line = new QueueArray<Car>(20);
            tollLines[i] = line;
        }

        //entire simulation is done here
        doSimulation();

        //Display Report
        System.out.println();
        System.out.println("Parameters:");
        System.out.println("# of tolls = " + numBooths);
        System.out.println("mtime = " + mtime);
        System.out.println("vtime = " + vtime);
        System.out.println("flow = " + flow);
        System.out.println();
        double avgArrivalTime = 0, avgBoothTime = 0;
        int totalEasyPasses = 0, avgWaitTime = 0;
        /**Use a loop to calculate the data contained in all of the
         * cars that made it out of the toll system.
         */
        for (Car car : finishedCars) {
            avgArrivalTime += car.expectedArrival;
            avgWaitTime += (car.timeLeftTrack - car.timeEnteredTrack);
            if (car.originalBoothTime == 1.0) {
                totalEasyPasses++;
            } else {
                avgBoothTime += car.originalBoothTime;
            }
        }

        System.out.println("Calculations:");
        System.out.println("Average expected time for car to arrive = " + (avgArrivalTime / finishedCars.size()));
        System.out.println("Total number of easy-pass cars = " + totalEasyPasses);
        System.out.println("Average time for passing booth without easy-pass = " + (avgBoothTime / (finishedCars.size() - totalEasyPasses)));
        System.out.println("Average wait time = " + (avgWaitTime / finishedCars.size()) + "s   (must be < 180)");
        System.out.println("Max cars waiting on open road = " + maxCars);
        System.out.println("________________");
        System.out.println();
        System.out.println();
    }

    private void doSimulation() {
        /**These are declared before the simulation starts
         */
        //we use currentFlow to account for the extra time < 1 second
        double currentFlow = -1;
        double calcFlow = 1 / flow;
        boolean firstPass = true;
        /**I kept the expDistribution and normalDistribution internal classes in
         * the Tester class.
         */
        Tester.expDistribution carSampling = new Tester.expDistribution(calcFlow);
        Tester.normalDistribution pass = new Tester.normalDistribution(mtime, vtime);

        /**This is the loop where everything is done.
         * Each iteration of the loop is the equvilant of one second.
         */
        for (int sec = 1; sec <= 3600; sec++) {

            /**This loop updates information about the cars at the front
             * of the toll lines.
             */
            for (int i = 0; i < tollLines.length; i++) {

                //This if statement makes sure there are cars in the toll line
                if (!tollLines[i].isEmpty()) {

                    /**currentCar is a car at the front of the toll line.
                     * This variable makes it easier so we dont always have to peek.
                     */
                    Car currentCar = (Car) tollLines[i].peek();

                    /**If there is a car in the line, each second it waits we
                     * want to take one second away from booth time
                     */
                    currentCar.boothTime -= 1;

                    /**Eventually this front car will have waited its boothTime.
                     * When it does it can leave the toll line.
                     */
                    if (currentCar.boothTime <= 0) {

                        //remove the front car from the line
                        tollLines[i].poll();

                        /**We are still using currentCar because it is easier.
                         * Update the time currentCar leaves the track.
                         */
                        currentCar.timeLeftTrack = sec;

                        /**Add currentCar to the ArrayList of cars that have
                         * left the tolls.
                         */
                        finishedCars.add(currentCar);

                        /**If the boothTime is negative, we want to account for
                         * the time that is less than one second, so we take
                         * time away from the new car at the front of the line
                         */
                        if (currentCar.boothTime < 0 && !tollLines[i].isEmpty()) {
                            Car temp = (Car) tollLines[i].peek();
                            temp.boothTime += currentCar.boothTime;
                        }
                    }
                }
            }

            /**This loop adds cars to the open road based on the flow.
             * It also moves cars from the open road to the toll lines.
             */
            while (currentFlow <= 0) {

                //This is the expDistribution time for the next car to arrive
                double sample = carSampling.next();

                /**If it is the first pass, we dont want to add two samples together,
                 * so we set currentFlow = to sample
                 */
                if (firstPass) {
                    firstPass = false;
                    currentFlow = sample;
                } else {
                    currentFlow += sample;
                }

                /**This calculates what the booth time will be for the new car
                 */
                double boothTime = pass.sample();
                if (boothTime < mtime) {
                    boothTime = 1;
                }

                /**Create the new car put it on the open road
                 */
                Car enqueue = new Car(sec, boothTime, sample);
                openRoad.offer(enqueue);

                /**Insert the new car (and any others) to the shortest
                 * toll line if possible.
                 */
                insertCars();
            }

            /**If the currentFlow > 0 the car must wait longer before it can
             * go to the open road.
             */
            if (currentFlow > 0) {
                currentFlow = currentFlow - 1;
            }

            /**This keeps track of the most cars ever on the open road
             */
            if (this.maxCars < openRoad.size()) {
                this.maxCars = openRoad.size();
            }

        }
    }

    /**
     * Takes cars off of open road and inserts them into the best possible line
     */
    private void insertCars() {

        /**While their are cars on the open road, try to get all of them into
         * a toll line.
         */
        while (!openRoad.isEmpty()) {

            int useLine = 0;
            //Start smallestLine at the largest possible value for a toll line
            int smallestLine = 20;

            /**Iterate through each toll line. If the current line has less cars
             * than smallestLine, we want the car on the open road to go to the
             * shorter lane.
             */
            for (int i = 0; i < tollLines.length; i++) {
                if (tollLines[i].size() < smallestLine) {
                    smallestLine = tollLines[i].size();
                    useLine = i + 1;
                }
            }

            /**If useLine is still 0, every toll line is full, break the loop.
             * If useLine is > 0 we can move the car from the open road to the
             * back of shortest line.
             */
            if (useLine > 0) {
                useLine = useLine - 1;
                tollLines[useLine].offer(openRoad.poll());
            } else {
                break;
            }

        }
    }
}
