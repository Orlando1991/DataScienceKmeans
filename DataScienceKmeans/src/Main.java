import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;


public class Main {

    boolean debug = false;
    static List<Point> datapoints;
    static Cluster[] centroids;
    static double SSE;
    static int clusters = 4;
    static int maxIterations= 10;
    boolean finish = false;

    public static void main(String[] args) {
//        System.out.println("Input amount of clusters: ");
//        Scanner scanner = new Scanner(System.in);
//        clusters = scanner.nextInt();
//        System.out.println("Input max iterations: ");
//        maxIterations = scanner.nextInt();

        Main m = new Main();
        int[][] data = m.readCSV();
        datapoints = m.createDataPoints(data);
        centroids = m.buildCentroids(clusters, datapoints);
        m.kMeans(centroids);
//        for(int i=0; i < clusters; i++)
//        {
//            System.out.println(" Testing datapoint: "+  i + " " + Arrays.toString(buildCentroids(clusters, datapoints)[i].getCenter()));
//        }
    }

    public void kMeans(Cluster[] centroids)
    {
        int iteration = 0;

        while(!finish && iteration < maxIterations)
        {
            clearCluster(); //empties the list with assigned points to the cluster
            Cluster[] oldCentroids = centroids;

            assignCluster(); //assigns every point to a cluster id

            calculateCentroid(oldCentroids, iteration); //takes the average of all the points added and centers centroid in it

            iteration++;
        }

//        System.out.println("Done after: " + iteration);
        double tempSSE = getSSE();
        double totalDistance = getTotalDistance();
        System.out.println("Total distance to cluster: " + totalDistance + " , SSE (distance squared): " + tempSSE);
        System.out.println();
        analyseData();
    }
    public double getTotalDistance()
    {
        double total = 0;
        for(Point p : datapoints)
        {
            total += p.distanceToCluster;
        }
        return total;
    }

    public double getSSE()
    {
        double sumOfSE = 0;
        for(Point p : datapoints)
        {
            double SE = p.getSquaredError();
            sumOfSE += SE;
        }
        return sumOfSE;
    }

    public void analyseData()
    {

//        int sum = 0;
//        for(Cluster c : centroids)
//        {
//            sum += c.getPoints().size();
//            System.out.println("testing centroids: " + c.getId() + " points assigned: " + c.getPoints().size());
//        }
//        System.out.println(" sum: " + sum);

        for(Cluster c : centroids)
       {
           List<Point> clients = c.getPoints();
           int amountOfOffers = clients.get(0).getPoint().length;
           Integer[] boughtOffers = new Integer[amountOfOffers + 1]; //because boughtoffers doesnt have 0 else it would give outofindex
           for(int i = 0; i <= amountOfOffers; i++)
           {
               boughtOffers[i] = 0;
           }

           //go over the assigned clients of cluster and sum up all their purchases at the right index
           for(Point client : clients)
           {
               List<Integer> bought = client.getBoughtOffers();
               for(int offer : bought)
               {
//                   System.out.println("offer: " + offer);
                   boughtOffers[offer] += 1;
               }
           }
           //logic to print here
           if(debug){System.out.println("boughtoffers: " + Arrays.toString(boughtOffers));}

           //sort the data and print it
           sortLogic(boughtOffers, c.getId());

       }

    }

    private void sortLogic(Integer[] boughtOffers, int id)
    {
        //sorts based on value, but returns the corresponding indices (sorted from high to low)
        int[] sortedIndices = IntStream.range(0, boughtOffers.length)
                .boxed().sorted((j, i) -> boughtOffers[i].compareTo(boughtOffers[j]) )
                .mapToInt(ele -> ele).toArray();

        int minimumBuys = 3;
        System.out.println("CLUSTER: " + id);
        for(int i =0; i< sortedIndices.length; i++)
        {
            int index = sortedIndices[i]; //the offer
            int value = boughtOffers[index]; //amount of times offer was bought
            if(value > minimumBuys)
            {
                System.out.println("OFFER: " + index + " -> bought " + value + " times");
            }
        }
        if(debug){System.out.println("sorted index: " + Arrays.toString(sortedIndices) + " cluster: " + id);}
        System.out.println("--------------------");
    }

    public  void clearCluster()
    {
        for(Cluster centroid : centroids)
        {
            centroid.clearPoints();
        }
    }

    public void assignCluster()
    {
        for(int i = 0; i < datapoints.size(); i++)
        {
            int currentCluster = 0;
            double minimum = 1000;  //ensures the first cluster will always get chosen

            for(int j= 0; j < centroids.length; j++)
            {
                //loop through all centroids, calculate the distance from datapoints.get(i) and save shortest
                double distance = euclideanDistance(datapoints.get(i).getPoint(), centroids[j].getCenter());
                if(distance < minimum)
                {
                    minimum = distance;
                    currentCluster = j;
                }
            }
            datapoints.get(i).setClusterId(currentCluster);
            datapoints.get(i).distanceToCluster = minimum;
            centroids[currentCluster].addPoint(datapoints.get(i));
        }
//        for(int i = 0; i < datapoints.size(); i++)
//        {
//            System.out.println("ClientId: " + i + " assigned cluster: " + datapoints.get(i).getClusterId());
//        }
//        for(int i = 0; i < centroids.length; i++)
//        {
//            System.out.println("ClusterId: " + i + " assigned points: " + centroids[i].getPoints());
//        }
    }


    public  void calculateCentroid(Cluster[] oldcentroids, int iteration)
    {
        int same = 0;
        Cluster[] newCentroids = new Cluster[clusters];

        for(int i =0; i <centroids.length; i++)
        {
            double[] newCenter = new double[32];

            List<Point> points = centroids[i].getPoints();  //points assigned to this cluster
            int npoints = points.size(); // sumOfPoints/npoints for average

            for(Point p: points) //loops through all the points in current centroid
            {
                int[] point = p.getPoint();
                int pointsize = point.length;

                for(int j = 0; j < pointsize; j++)
                {
                    newCenter[j] = newCenter[j] + (double)point[j];
                }
            }
            for(int k = 0; k < newCenter.length; k++) {
                newCenter[k] = newCenter[k] / npoints;
            }
            Cluster c = new Cluster();
            c.setCenter(newCenter);
            c.setId(i);
            newCentroids[i] = c;

            if(Arrays.equals(newCentroids[i].getCenter(), oldcentroids[i].getCenter()))
            {
                if(debug){System.out.println("Old " + Arrays.toString(oldcentroids[i].getCenter()));}
                if(debug){System.out.println( " new " + Arrays.toString(centroids[i].getCenter()));}
                same = same + 1;

                if(same == clusters) //so if all clusters remained the same this iteration
                {
                    finish = true;
                }
            }
            else
            {
                if(iteration != maxIterations -1) //makes sure you keep the data if it's in its last iteration and the clusters are still moving
                {
                    centroids[i] = newCentroids[i];
                }
            }
        }

    }

    public Cluster[] buildCentroids(int count, List<Point> datapoints)
    {
        Cluster[] centroids = new Cluster[count];

        for(int i = 0; i < count; i++)
        {
            int random = 0 + (int)(Math.random()* datapoints.size());
            int[] chosenPoint = datapoints.get(random).getPoint(); //returns int[]
            int size = datapoints.get(random).getPoint().length;
            double[] randomPoint = new double[size];
            for(int j = 0; j< size; j++)
            {
                randomPoint[j] = (double) chosenPoint[j];
            }

            Cluster newPoint = new Cluster();
            newPoint.setId(i);
            newPoint.setCenter(randomPoint);
            centroids[i] = newPoint;
        }
        return centroids;
    }

    private  double euclideanDistance(int[] client, double[] centroid)
    {
        double sum = 0;
        for (int i = 0; i < client.length  ; i++)
        {
            double power = Math.pow(client[i] - centroid[i], 2);
            sum += power;
        }

        return Math.sqrt(sum);
    }

    public int[][] readCSV(){
        String csvFile = "C:\\Users\\Orlando\\Documents\\HRO\\INF3B\\OP3\\Data Science\\WineData.csv";
        String line;
        String cvsSplitBy = ",";
        int[][] allDataArray = new int[100][32];
        int linenumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                for(int i = 0; i < data.length; i++ )
                {
                   allDataArray[i][linenumber] = Integer.parseInt(data[i]);
                }
                linenumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allDataArray;
    }

    public  List<Point> createDataPoints(int[][] dataArray) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < dataArray.length; i++)
        {
            Point point = new Point();
            point.setPoint(dataArray[i]);
            point.setBoughtOffers();
            points.add(point);
        }
        return points;
    }
}
