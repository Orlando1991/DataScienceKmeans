import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orlando on 19-2-2017.
 */
public class Point {

    private int[] point = new int[32];
    private int clusterId;
    public List<Integer> boughtOffers = new ArrayList<>();



    public void setPoint(int[] data){
        this.point = data;
    }

    public int[] getPoint(){
        return point;
    }

    public void setClusterId(int id)
    {
        this.clusterId = id;
    }

    public int getClusterId()
    {
        return clusterId;
    }

    public void setBoughtOffers()
    {
        {
            for(int i = 0; i < point.length; i++)
            {
                if(point[i] == 1)
                {
                    boughtOffers.add(i + 1);
                }
            }
        }
    }

    public List<Integer> getBoughtOffers() {
        return boughtOffers;
    }
}
