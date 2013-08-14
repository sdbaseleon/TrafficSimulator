
/**
 * This is the tester class that contains the main method
 * @author Stephen Baseleon
 */
public class Tester{

    /**
     * main method calls the Main class to start the simulation. We provide the
     * # of booths, mtime, vtime, and flow of traffic in the Main constructor.
     */
    public static void main(String[] args){

        //Parameters: (# tolls, mtime, vtime, flow)
        Main test = new Main(12, 2.5, 4, 4);

    }

    /* by Chung-Chih Li at ISU for ITK 179's Trafic simulation           */
    /* Put the following classes as internal classes in your main class  */
    /*******************************************************************************/
    /* This class implements the exponential distribution,                         */
    /* where theta is the mean of the time for waiting for the 1st events.         */
    /*******************************************************************************/
    public static class expDistribution{

        private double theta;

        public expDistribution(double theta){
            this.theta = theta;
        }
        // This is the density function
        public double pdf(double x){
            return (1 / Math.exp(x / theta)) / theta;
        }
        // The probability that there is no event before time x 
        public double no(double x){
            return 1 / Math.exp(x / theta);
        }
        // Time to next random change  
        public double next(){
            double p = Math.random();
            return Math.log(1 - p) * (-theta);
        }

    }

    /*******************************************************************************/
    /* This class implements the normal distribution,                              */
    /* where mu is the mean and roh the variance.                                  */
    /*******************************************************************************/
    public static class normalDistribution{

        private double mu,  roh;
        private double xs; // x less than xs is too small to be considered for sampling
        private double xe; // x greater than xe is too big to be considered for sampling 
        private double dx;
        private double[] dist = new double[50];
        private double iI; // the initial segment of the interval 

        public normalDistribution(double mu, double roh){
            this.mu = mu;
            this.roh = roh;
            // roh will be reference value to determine the precision. 
            // roh/100,000 will be considered too small for sampling. 
            dx = roh / 10000.0;
            double x, y = roh / 100000.0;
            //xs = mu - rpdf(y);  // rpdf is the reverse pdf;
            //xe = mu + rpdf(y);
            xs = mu - 5 * Math.abs(mu);
            xe = mu + 5 * Math.abs(mu);

            double I = dx * y;
            x = xs;
            iI = 0;
            while(I - iI > 1.0E-25){
                iI = I;
                x = x - dx;
                I += dx * pdf(x);
            }
            x = xs;
            int i = 1;
            dist[0] = 0;
            while(x < xe){
                if(I > (double)i / (double)dist.length){
                    dist[i] = x;
                    i++;
                    if(i == dist.length){
                        break;
                    }
                }
                x += dx;
                I += dx * pdf(x);
            }
        }
        // This is the density function
        public double pdf(double x){
            return 1 / roh / Math.pow(2 * Math.PI, 0.5) / Math.exp((Math.pow((x - mu) / roh, 2)) / 2);
        }
        // The reverse pdf
        public double rpdf(double y){
            return mu + roh * Math.pow(-2 * Math.log(y * roh * Math.pow(2 * Math.PI, 0.5)), 0.5);
        }
        // A random sample in this distribution
        // Numerical approximation
        public double sample(){
            double I, x, p = Math.random();
            // found x that the integral from - infinity to x is bigger than p

            int i = (int)(p * 50);
            if(i == 0){
                x = xs;
                I = iI;
            }
            else{
                x = dist[i];
                I = (double)i / 50.0;
            }
            while(I < p && x < xe){
                x += dx;
                I += dx * pdf(x);
            }
            return x;
        }

    }

}
