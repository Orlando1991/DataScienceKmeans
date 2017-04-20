import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Orlando on 2-3-2017.
 */
public class Cluster {
    private double[] center = new double[32]; //needs to be float for recalculating center
    private int id;
    private List<Point> points = new ArrayList<>();
    private Map<Integer, Integer>offerBoughtCount= new HashMap<>();

    public double[] getCenter() {
        return center;
    }

    public void setCenter(double[] center) {
        this.center = center;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOfferBoughtCount(int key)
    {
        offerBoughtCount.put(key, offerBoughtCount.get(key) + 1);
    }

    public Map<Integer, Integer> getOfferBoughtCount()
    {
        return offerBoughtCount;
    }

    public void addPoint(Point point)
    {
        points.add(point);
    }

    public void clearPoints()
    {
        points.clear();
    }

    public List<Point> getPoints()
    {
        return points;
    }
}
