
/***
 * KD tree
 * 
 */
package KDTree;
import java.util.*;

public class KDTree {

	private KDNode root; //root of the tree
	private int dimension; //dimension
	private String distancefunc; //distance function
	
	public void setDistanceFunction(String s)
	{
		distancefunc =s.toUpperCase();
	}
	public String getDistanceFunction()
	{
		return distancefunc;
	}
	public void setDimension(int i)
	{
		this.dimension = i;
		
	}
	public int getDimension(int i)
	{
		return this.dimension;
		
	}
	
	//initiate KD tree
	public KDTree(int dim)
	{
		this.dimension = dim;
		this.root = new KDNode();
		this.root.setAxis(-1);
		this.distancefunc="EUCLIDEAN";
	}
	
	/***
	 * 
	 * @param data insert data to KDtree
	 * @param id id of new node
	 * @return
	 */
	public boolean Insert(double[] data, int id)
	{
		if (this.root.getAxis() == -1) // null tree
		{
			//create root
			if (data.length == this.dimension)
			this.root.setData(data);
			else return false;
			
			this.root.setAxis(0); //root has axis = 0
			this.root.setId(id);
		}
		else //not null tree
		{
			
			KDNode r = FindParent(this.root,data);
			 if (r.getData()[r.getAxis()] < data[r.getAxis()])
			 {
				 r.setRight(data, CalculateAxis(r.getAxis()),id);
			 }
			 else
			 {
				 r.setLeft(data, CalculateAxis(r.getAxis()),id);
			 }
			 
			 
		}
		return true;
	}
	
	/***
	 * Calculate axis for each new node
	 * @param x axis of parent node
	 * @return axis of child node
	 */
	private int CalculateAxis(int x)
	{
		if ( x < (this.dimension - 1)) return x+1;
		else
			return 0;
	}
	/***
	 * Find parent for new data inserted to KD tree
	 * @param start start find from this node
	 * @param ndData new data
	 * @return parent node
	 */
	 private KDNode FindParent(KDNode start, double[] ndData)
	 {
		 KDNode r = start;
		 		 
		 if (r == null) return null;
		 
		 if (r.getData()[r.getAxis()] < ndData[r.getAxis()])
		 {
			 if (r.getRight()==null)  return r;
			 else return FindParent(r.getRight(),ndData);
			 
		 }
			
		 else
		 {
			 if (r.getLeft()== null)  return r;
			 else return FindParent(r.getLeft(), ndData);
		 }
		 
		 
		 
	 }
	 
	 /***
	  * Find all nodes within a radius square of a node ND
	  * @param dt data of node ND
	  * @param eps radius
	  * @param without id of target attribute of data
	  * @return vector including ids of all nodes found
	  */
	 public Vector<Integer> FindAllPointsInRadius(double[] dt, double eps, int without)
	 {
		 Vector<Integer> v= new Vector<Integer>();
		 RadiusSquare(this.root,dt,eps,v, without);
		// System.out.print(v);
		 return v;
	 }
	 
	/***
	 * Find all nodes within a radius square of a node ND, start from a given node
	 * @param start 
	 * @param ndData
	 * @param eps
	 * @param v  result vector including ids of all nodes found
	 * @param without
	 */
	 private void RadiusSquare(KDNode start, double[] ndData, double eps, Vector<Integer> v, int without)
	 {
		 if (start == null) return;
		 int dim= start.getAxis();
		 if (dim<0) return;
		 double d=0;
		 if (this.distancefunc.equals("EUCLIDEAN"))
		 {
			 d = (ndData[dim]-start.getData()[dim]) * (ndData[dim]-start.getData()[dim]);
			 if (d > eps*eps)
			 {
				 if (ndData[dim] > start.getData()[dim])
					 RadiusSquare(start.getRight(),ndData,eps,v,without);
				 else
					RadiusSquare(start.getLeft(),ndData,eps,v,without); 
			 }
			 else
			 {
				 RadiusSquare(start.getRight(),ndData,eps,v,without);
				 RadiusSquare(start.getLeft(),ndData,eps,v,without); 
			 }
			 if (checkEpsRadius(start.getData(),ndData,without,eps)) v.add(start.getId());
		 }
		 else
		 {

			 d = Math.abs(ndData[dim]-start.getData()[dim]);
			 if (d > eps)
			 {
				 if (ndData[dim] > start.getData()[dim])
					 RadiusSquare(start.getRight(),ndData,eps,v,without);
				 else
					RadiusSquare(start.getLeft(),ndData,eps,v,without); 
			 }
			 else
			 {
				 RadiusSquare(start.getRight(),ndData,eps,v,without);
				 RadiusSquare(start.getLeft(),ndData,eps,v,without); 
			 }
			 if (checkEpsRadius(start.getData(),ndData,without,eps)) v.add(start.getId());
		 }
		 
	 }
	 
	 /***
	  * check whether nd1,nd2 are eps neighborhoods
	  * @param nd1
	  * @param nd2
	  * @param without
	  * @param eps
	  * @return
	  */
	 private boolean checkEpsRadius(double[] nd1, double[] nd2, int without, double eps)
	 {
		 int n= nd1.length;
		 if (distancefunc.equals("EUCLIDEAN"))
		 {
			 double d=0;
			 for(int j=0;j<n;j++)
				 if (j!= without)
					 d +=(nd1[j]-nd2[j])*(nd1[j]-nd2[j]);
			 if (Math.sqrt(d)<=eps) return true;
			 else return false;
		 }
		 else
		 {
			 double d=0;
			 for(int j=0;j<n;j++)
				 if (j!= without)
					 d += Math.abs(nd1[j]-nd2[j]);
			 if (d <= eps) return true;
			 else return false;
		 }
		 
	 }
	 
	 
	
}
