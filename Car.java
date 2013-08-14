
/**
 * This class represents one car.
 * @author Stephen Baseleon
 */
public class Car{

    /**These values are kept in the car class to stay independent of other car
     * values.
     */
    public int timeEnteredTrack, timeLeftTrack;
    public double boothTime, originalBoothTime, expectedArrival;

    /**Constructor for car class. This constructor must be called in order 
     * to have the correct values stored in each variable.
     * @param timeEnteredTrack
     * @param boothTime
     * @param expectedArrival
     */
    public Car(int timeEnteredTrack, double boothTime, double expectedArrival){
        this.timeEnteredTrack = timeEnteredTrack;
        this.boothTime = boothTime;
        this.originalBoothTime = boothTime;
        this.expectedArrival = expectedArrival;
        this.timeLeftTrack = 0;
    }


}
