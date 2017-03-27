import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {

    static List<Point> datapoints;
    static Cluster[] centroids;
    static int clusters = 4;
    boolean finish = false;

    public static void main(String[] args) {
        Main m = new Main();
        int[][] data = m.readCSV();
        datapoints = m.createDataPoints(data);
        centroids = m.buildCentroids(clusters, datapoints);
        m.kMeans();
//        for(int i=0; i < clusters; i++)
//        {
//            System.out.println(" Testing datapoint: "+  i + " " + Arrays.toString(buildCentroids(clusters, datapoints)[i].getCenter()));
//        }
    }

    public void kMeans()
    {
        int iteration = 0;

        while(!finish)
        {
            clearCluster(); //empties the list with assigned points to the cluster
            Cluster[] oldCentroids = centroids;

            assignCluster(); //assigns every point to a cluster id


            System.out.println("testing" );
            calculateCentroid(oldCentroids); //takes the average of all the points added and centers centroid in it
            System.out.println("after" );
            iteration++;
        }

        System.out.println("test: " + iteration);

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


    public  void calculateCentroid(Cluster[] oldcentroids)
    {
        int same = 0;
        Cluster[] newCentroids = new Cluster[clusters];

        for(int i =0; i <centroids.length; i++)
        {
            float[] newCenter = new float[32];

            List<Point> points = centroids[i].getPoints();  //points assigned to this cluster
            int npoints = points.size(); // sumOfPoints/npoints for average

            for(Point p: points) //loops through all the points in current centroid
            {
                int[] point = p.getPoint();
                int pointsize = point.length;

                for(int j = 0; j < pointsize; j++)
                {
                    newCenter[j] = newCenter[j] + (float)point[j];
                }
            }
            for(int k = 0; k < newCenter.length; k++) {
                newCenter[k] = newCenter[k] / npoints;
            }
            Cluster c = new Cluster();
            c.setCenter(newCenter);
            newCentroids[i] = c;

            if(Arrays.equals(newCentroids[i].getCenter(), oldcentroids[i].getCenter()))
            {
                System.out.println("Old " + Arrays.toString(oldcentroids[i].getCenter()));
                System.out.println( " new " + Arrays.toString(centroids[i].getCenter()));
                same = same + 1;

                if(same == clusters) //so if all clusters remained the same this iteration
                {
                    finish = true;
                }
            }
            else
            {
                centroids[i] = newCentroids[i];
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
            float[] randomPoint = new float[size];
            for(int j = 0; j< size; j++)
            {
                randomPoint[j] = (float) chosenPoint[j];
            }

            Cluster newPoint = new Cluster();
            newPoint.setId(i);
            newPoint.setCenter(randomPoint);
            centroids[i] = newPoint;
        }
        return centroids;
    }

    private  double euclideanDistance(int[] client, float[] centroid)
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
