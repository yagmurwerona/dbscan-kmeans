package dbscan;
import KDTree.*;
import core.*;
import java.util.*;
public class KDTreeStorage {

	private KDTree tree;
	private int targetId;
	
	public KDTreeStorage(Instance[] data, int targetId, String distfunc)
	{
		this.tree= new KDTree(data[0].numAttribute);
		this.tree.setDistanceFunction(distfunc);
		this.targetId=targetId;
		
		
		for(int i=0;i<data.length;i++)
		{
			double[] db= new double[data[i].numAttribute];
			for (int j=0;j<data[i].numAttribute;j++)
				db[j]=data[i].data.elementAt(j);
			tree.Insert(db, i);
		}
		
		
	}
	
	public void Insert(Instance ins, int id)
	{
		double[] db= new double[ins.numAttribute];
		for (int j=0;j<ins.numAttribute;j++)
			db[j]=ins.data.elementAt(j);
		
		this.tree.Insert(db, id);
		
	}
	public Vector<Integer> FindEpsNeighborhood(Instance ins, double eps)
	{
		
		double[] db= new double[ins.numAttribute];
		for (int j=0;j<ins.numAttribute;j++)
			db[j]=ins.data.elementAt(j);
	     return	this.tree.FindAllPointsInRadius(db, eps, this.targetId);
	}
	
}
