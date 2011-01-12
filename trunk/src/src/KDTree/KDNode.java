package KDTree;

/***
 * Node of KD tree
 * @author quynhdtn
 * 
 */
public class KDNode {

	private double[] data; //data in each node
	private int axis;      //id of dimension used in each level of KD tree
    private KDNode right;  //right child
	private KDNode left;   //left child
	private int id=-1;     //id of node
	
	public void setAxis(int i)
	{
		this.axis=i;
		
	}
	
	public int getAxis()
	{
		return this.axis;
		
	}
	public int getId()
	{
		return this.id;
		
	}
	public void setId(int i)
	{
		this.id=i;
		
	}
	public double[] getData()
	{
		return this.data;
	}
	
	public KDNode getRight()
	{
		return this.right;
	}
	
	public KDNode getLeft()
	{
		return this.left;
	}
	
	public void setRight(double[] dt, int ax, int idd)
	{
		this.right = new KDNode(dt, ax,idd);
	}
	public void setLeft(double[] dt, int ax, int idd)
	{
		this.left = new KDNode(dt, ax, idd);
	}
	public void setData(double[] dt)
	{
		int n=dt.length;
		this.data = new double[n];
		this.data = dt;
	}
	public KDNode()
	{
		
	}
	
	public KDNode(double[] dt, int ax, int idd)
	{
		int n=dt.length;
		this.data = new double[n];
		this.data = dt;
		this.axis = ax;
		this.id=idd;
	}
	
}
