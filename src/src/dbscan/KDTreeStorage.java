package dbscan;
import KDTree.*;
import core.*;
import java.util.*;
public class KDTreeStorage {

	private KDTree tree;
	private int target;
	private int num; //dimension
	
	public KDTree getTree()
	{
		return tree;
	}
	public KDTreeStorage(Instance[] data, int targetId, String distfunc)
	{
		int n;
		if(targetId>=0) n=data[0].numAttribute-1;
		else n=data[0].numAttribute;
		
		this.num=n;
		this.tree= new KDTree(this.num);
		this.tree.setDistanceFunction(distfunc);
		this.target=targetId;
		
		for(int i=0;i<data.length;i++)
		{
			double[] db= new double[this.num];
			int q=0;
			for (int j=0;j<data[i].numAttribute;j++)
				if(j!=targetId)
				{
					db[q]=data[i].data.elementAt(j);
					q++;
				}
			tree.Insert(db, i);
		}
		
		
	}
	
	public void Insert(Instance ins, int id)
	{
		double[] db= new double[this.num];
		int q=0;
		for (int j=0;j<ins.numAttribute;j++)
			if(j!=this.target)
			{
				db[q]=ins.data.elementAt(j);
				q++;
			}
		
		this.tree.Insert(db, id);
		
	}
	public Vector<Integer> FindEpsNeighborhood(Instance ins, double eps)
	{
		
		double[] db= new double[ins.numAttribute];
		for (int j=0;j<ins.numAttribute;j++)
			db[j]=ins.data.elementAt(j);
	     return	this.tree.FindAllPointsInRadius(db, eps);
	}
	public double GetkthDistance(Instance ins, int k)
	{
		double[] db= new double[ins.numAttribute];
		for (int j=0;j<ins.numAttribute;j++)
			db[j]=ins.data.elementAt(j);
		return this.tree.kthDistance(db, k);
	}
	
}
