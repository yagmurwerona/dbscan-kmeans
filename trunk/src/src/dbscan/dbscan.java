package dbscan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import core.DistanceFunction;
import core.InputReader;
import core.Instance;
import core.Output;

public class dbscan {

	private Output out;
	private InputReader reader;
	private InputReader testrd;
	private double Eps;       
	private int MinPts;
	private String DistFunc; //distance function
	
	private int targetId;
	private int experimentType; 
	private Instance[] trainSet;
	private Instance[] testSet;
	
	//using KDTree
	private KDTreeStorage trainstore;
	private KDTreeStorage teststore;
	private int usingkd;
	
	public dbscan(InputReader reader, String distFunc){
		this.DistFunc = distFunc;
		if (0 < this.experimentType && this.experimentType < 100) {
			int total = this.reader.numInstances;
			int beTrained = total * this.experimentType / 100;
			trainSet = new Instance[beTrained];
		}else {
			// the training is all data
			int total = reader.data.numInstance;
			trainSet = new Instance[total];
			
			for (int i = 0; i< total; i++) {
				trainSet[i] = reader.data.getInstance(i);
			}
		}
	}
	
	public dbscan(InputReader reader, String eps, String minPts, String distFunc, String exptype)
	{
		this.reader = reader;
		Eps= Double.parseDouble(eps);
		MinPts = Integer.parseInt(minPts);
		DistFunc = distFunc;
		experimentType=Integer.parseInt(exptype);
	}
		
	public void setEps(double eps)
	{
		Eps = eps;
		
	}
	
	public double getEps()
	{
		return Eps;
		
	}
	
	public void setMinPts(int minpts)
	{
		MinPts = minpts;
		
	}
	
	public int getMinPts()
	{
		return MinPts;
		
	}
	
	public void setDistanceFunction(String distfunc)
	{
		DistFunc = distfunc.toUpperCase();
		
	}
	
	public String getDistanceFunction()
	{
		return DistFunc;
		
	}
	public void setExperimentType(String type)
	{
		experimentType = Integer.parseInt(type);
		
	}
	
	public int getExperimentType()
	{
		return experimentType;
		
	}

	public void setTargetId(int target)
	{
		targetId = target;
	}
	public void setTarget (String target) {
	
		this.targetId=-1;
		for (int i = 0; i<this.reader.numAttribute; i++) {
			if (this.reader.attributeNames.get(i).compareTo(target)==0) {
				this.targetId = i; 
				break;
			}
		}
	}

	public int getTargetAttribute()
	{
		return targetId;
	}
	
	public void setTestfile(InputReader reader)
	{
		this.testrd = reader;
	}
	
	
	public void setUsingKD(String i)
	{
		usingkd = Integer.parseInt(i);
		
	}
	
	public int getUsingKD()
	{
		return usingkd ;
		
	}
	
	
	
	/** 
	 * Run dbscan with input parameters
	 * 
	 */
	public void RunDBSCAN()
	{
//		System.out.print("Start\n");
		String output="";
		
		if (experimentType == 101) //test on all train data
		{
			int[] cluster_train;
			if (this.usingkd == 0)
			{
				cluster_train = Clustering(trainSet);
			}
			else
			{
				cluster_train = ClusteringKD(trainSet,trainstore);
			}
			output += OutputHeader();
			output += "\n Test mode: testing on all data";
			output += "\n Result of testing";
			output +=OutputClusterResult(cluster_train, trainSet);
			
		}
		
		if (0 < this.experimentType && this.experimentType < 100) //randomly split to training and testing sets 
		{
			int[] cluster_train;
			int[] cluster_test;
			if (this.usingkd == 0)
			{
				cluster_train= Clustering(trainSet);
				cluster_test= ClusteringTest(testSet,trainSet,cluster_train);
			}
			else
			{
				cluster_train= ClusteringKD(trainSet,trainstore);
				cluster_test= ClusteringTestKD(testSet,trainSet,cluster_train,trainstore);
			}
			output += OutputHeader();
			output += "\n Test mode: Randomly split to training and testing sets, testing set = ";
			output +=this.experimentType;
			output += "%\n";
			output += "\n---- Training -----\n";
			output +=OutputClusterTrainingResult(cluster_train, trainSet);
			output += "\n\n---- Testing result -----";
			output +=OutputClusterTestResult(cluster_test, testSet);
			
			
			
		}
		
		if (experimentType == 102) //test on all train data
		{
			int[] cluster_train;
					
			int[] cluster_test;
			
			if (this.usingkd == 0)
			{
				cluster_train= Clustering(trainSet);
				cluster_test= Clustering(testSet);
			}
			else
			{
				cluster_train= ClusteringKD(trainSet,trainstore);
				cluster_test= ClusteringTestKD(testSet,trainSet,cluster_train,trainstore);
			}
			output += OutputHeader();
			output += "\n Test mode: testing on testing test";
			output += "\n---- Training -----\n";
			output +=OutputClusterTrainingResult(cluster_train, trainSet);
			output += "\n---- Testing result -----\n";
			output +=OutputClusterTestResult(cluster_test, testSet);
			
			
		}
		this.out= new Output(output);
		
//		System.out.print(output);
	}
	
	/** 
	 * pre-processing data for clustering
	 */
	public void preProcessing()
	{
		if (0 < this.experimentType && this.experimentType < 100) {
			//randomly split to training and testing sets 
			int total = this.reader.numInstances;
			int beTrained = total * this.experimentType / 100;
			
			trainSet = new Instance[beTrained];
			testSet = new Instance[total-beTrained];
			
			Random rn = new  Random (1234);
			int flag[] = new int[total];
			
			while (beTrained -- >0) {
				int selected;
				do {
					selected = rn.nextInt(total);
				} while (flag[selected] == 1);
				flag[selected] =1;
				
			}
			int u = 0;
			int v = 0;
			for (int i = 0; i<total; i++) {
				if (flag[i] == 0) {
					//belong to the test set
					testSet[u] = reader.data.getInstance(i);
					u++;
					
				}
				else {
					trainSet[v] = reader.data.getInstance(i);
					v ++;
				}
			}
		}
		else {
			// the training is all data
			int total = reader.data.numInstance;
			trainSet = new Instance[total];
			
			for (int i = 0; i< total; i++) {
				trainSet[i] = reader.data.getInstance(i);
			}
			
			// and testing varies dependings on experiment type
			if (this.experimentType ==0 || this.experimentType == 101) { //test all data
				testSet = new Instance[total];
				for (int i = 0; i< total; i++) {
					testSet[i] = reader.data.getInstance(i);
				}
			}
			else { // test on another test set type = 102
				testSet = new Instance[testrd.data.numInstance];
				for (int i = 0; i< testrd.data.numInstance; i++) {
					testSet[i] = testrd.data.getInstance(i);
				}
			}
		}
		if(this.usingkd==1)
		{
			trainstore= new KDTreeStorage(trainSet, this.targetId, this.DistFunc);
			teststore= new KDTreeStorage(trainSet, this.targetId, this.DistFunc);
		}
		
	}
	/** 
	 * Clustering on data (don't use KD Tree to store data) 
	 * @PARAMS: DATA FOR CLUSTERING
	 */
	private int[] Clustering(Instance[] data)
	{
		int n= data.length;
		int[] clusterId= new int[n+1];
		for (int j=0;j<n;j++)
			clusterId[j]=0;  //unclassified
		
		int count=0;
		int ClId= NextID(-1);
		int i;
		for(i=0;i<data.length;i++)
			if (clusterId[i]==0)
				if ( ExpandCluster(ClId, i,data, clusterId)) 
					{
						ClId=NextID(ClId);
						count++; //number of clusters
					}
		
		clusterId[n]=count;
		return clusterId;
		
	}
	
	/** 
	 * Testing (don't use KD Tree to store data) 
	 * @PARAMS: testSet
	 * @param: trainSet
	 * @param: result of training (cluster id of all instances on train set)
	 * return: the array of cluster ids for all instances on test set
	 */
	private int[] ClusteringTest(Instance[] test, Instance[] train, int[] clusterTrain)
	{
		int[] clusterTest= new int[test.length];
		for (int i=0;i<test.length;i++)
		{
			
			Test(test[i],i,clusterTest,train,clusterTrain);
		
		}
		
		return clusterTest;
	}
		

	/** 
	 * Testing instance ins base on clustering result of samples
	 * @param: ins - instance for testing
	 * @param: id- id of testing instance on testSet
	 * @param: clusterId - array of cluster ids each element of testSet
	 * @param: samples - training set
	 * @param: cluster_samples - result of clustering on training set (array of cluster id)
	 */
	private void Test(Instance ins, int id, int[] clusterId, Instance[] samples,int[] cluster_samples)
	{
		Vector<Integer> v= FindEpsNeighborhood(ins, samples);
		for (int i=0;i<v.size();i++)
		{
			Instance x= samples[v.get(i)];
			Vector<Integer> vx = FindEpsNeighborhood(x, samples);
			if ((vx.size() >= this.MinPts)&&(cluster_samples[v.get(i)] != -1))
				{
					clusterId[id]=cluster_samples[v.get(i)];
					return;
				}
		}
		clusterId[id] = -1;
	}
	
	/** 
	 * Expand cluster function on DBSCAN
	 * @param: cluster id
	 * @param: id of testing instance
	 * @param: data for clustering
	 * @param: array of clustering result of all instances (store cluster id that instance belongs to)
	
	 */

	private boolean ExpandCluster(int ClId, int insId, Instance[] data, int[] clusterId)
	{
			
		Vector <Integer> seeds= FindEpsNeighborhood(insId,data);

		
		int sz= seeds.size();
		int i;
		if (sz < MinPts) //no core point
		{	clusterId[insId]=-1; //noise
			return false;
		}
		
		int p;

		for(i=0;i<seeds.size();i++)
		{
				
				if (clusterId[seeds.elementAt(i)]<=0)
					clusterId[seeds.elementAt(i)]=ClId;
				else
					{
						seeds.removeElementAt(i);
						i--;
						
					}
					
		}
		//remove insId
		seeds.removeElement(insId);
		
		while (seeds.size()!=0)
		{
			int cur= seeds.firstElement();
			
			Vector <Integer> result= FindEpsNeighborhood(cur,data);
				
			if (result.size()>= MinPts)
			{
				for (i=0;i<result.size();i++)
					{
						int resultP = result.elementAt(i);
						if ((clusterId[resultP]==0)||(clusterId[resultP]==-1))
						{
							if (clusterId[resultP]==0)
								seeds.add(resultP); 
							clusterId[resultP]=ClId;
						}
					}
				
			}
			seeds.removeElement(cur);
			
		}
		
		return true;
		
	}
	
	/** 
	 * Finding EPS neighborhoods for DBSCAN
	 * We don't use KD tree on this function
	 * @param: id of instance that we have to find neighborhoods
	 * @param: set of all instances
	 
	
	 */
	private Vector<Integer> FindEpsNeighborhood(int insid, Instance[] data) 
	{
		Vector<Integer> nei= new Vector<Integer>();
		
		int num= data.length;
		for(int i=0; i<num;i++)
			if (getDistance(data[insid], data[i]) <= Eps)
				nei.add(i);
		return nei;
		
		
	}
	
	/** 
	 * Finding EPS neighborhoods for DBSCAN
	 * We don't use KD tree on this function
	 * @param: instance that we have to find neighborhoods
	 * @param: set of all instances
	 
	
	 */
	private Vector<Integer> FindEpsNeighborhood(Instance ins, Instance[] data) 
	{
		Vector<Integer> nei= new Vector<Integer>();
		
		int num= data.length;
		for(int i=0; i<num;i++)
			if (getDistance(ins, data[i]) <= Eps)
				nei.add(i);
		return nei;
		
		
	}
	
	/** 
	 * Get distance between two instances
	
	 */
	private Double getDistance(Instance ins1, Instance ins2)
	{
		
		if (this.DistFunc.toUpperCase().equals("EUCLIDEAN"))
			return DistanceFunction.euclideanDistance(ins1, ins2,targetId);
		else //if (this.DistFunc.toUpperCase().equals("MANHATTAN"))
			return DistanceFunction.mahattanDistance(ins1, ins2,targetId);
		
		
	}

	/** 
	 * create id for clusters
	 * returns the id of the next cluster 
		@param: id of the present cluster
	 */
	private int NextID(int n)
	{
		if (n==-1) return 1; // the first cluster ID
		return (n+1); // other ids: 2,3 ,4,...
		
	}
	
	
	/** 
	 * output parameters of dbscan
	 
	 */
	private String OutputHeader()
	{
		String output="";
		output +=" Run DBSCAN with parameters: \n EPS:" + this.Eps + "\n Min Points: "+this.MinPts;
		output += "\n Target class: ";
		if (this.targetId>=0) 
			output += this.reader.attributeNames.get(this.targetId);
		else
			output +="none";
		output +="\n Distance function: ";
		output +=this.DistFunc;
		
		return output;
	}
	/** 
	 * 	output the result of training
	 * 	returns the id of the next cluster 
		@param: result of training (cluster ids)
		@param: data used on training
	 */
	private String OutputClusterTrainingResult(int[] clusterId, Instance[] data)
	{
		int numcluster=clusterId[clusterId.length-1];
		String output="\nResult of training";
		output +="\n Number of Clusters: ";
		output += numcluster;
			
		output +="\n Number of Instances: ";
		output += data.length;
		
		
		//count number of instances in each cluster
		
		int[] v= new int[numcluster];
		for (int i=0; i<numcluster;i++)
		{
			v[i]=0;
			
		}
		int unclusteredcount=0;
		for(int i=0;i<data.length;i++)
		{
			if (clusterId[i]!=-1)
				v[clusterId[i]-1]++;
			else unclusteredcount++;
		}
		
		output += "\n Number of unclustered Instances: ";
		output += unclusteredcount;
		output += " ~ ";
		output += (double)(unclusteredcount*100/data.length);
		output += "%";
		
		for (int i=0; i<numcluster;i++)
		{
			output += "\n Cluster ";
			output += i+1;
			output += ": ";
			output += v[i];
			output += " instances";
		}
		
		return output;
		
		
	}
		
	/** 
	 * 	output the result of testing
	
		@param: result of testing (cluster ids)
		@param: data used on testing
	 */
	private String OutputClusterTestResult(int[] clusterId, Instance[] data)
	{
		int count=0;
		for(int i=0;i<clusterId.length;i++)
			if (clusterId[i]==-1) count++;
		String output="";
		
	
	//	for(int i=0;i<data.length;i++)
		//{
		//	output += "\n";
		///	output += data[i].originalData;
		//	output += " => Cluster ";
		//	output += clusterId[i];
		//	output += "\n";
			
		//}
		
		output +="\n-------------------------";
		output +="\n Number of Testing Instances: ";
		output += data.length;
		output += "\n Number of unclustered Instances: ";
		output += count;
		output += " ~ ";
		output += (double)(count*100/data.length);
		output += "%";
		return output;
		
	}
	
	/** 
	 * 	output the result of clustering 
	 
		@param: result of clustering (cluster ids)
		@param: data used on clustering
	 */
	private String OutputClusterResult(int[] clusterId, Instance[] data)
	{
		int numcluster=clusterId[clusterId.length-1];
		String output="";
		output +="\n Number of Clusters: ";
		output += numcluster;
			
		/*
		for(int i=0;i<data.length;i++)
		{
			output += data[i].originalData;
			output += " => Cluster ";
			output += clusterId[i];
			output += "\n";
			
		}
		*/
		output +="\n Number of Instances: ";
		output += data.length;
		
		
		//count number of instances in each cluster
		
		int[] v= new int[numcluster];
		for (int i=0; i<numcluster;i++)
		{
			v[i]=0;
			
		}
		int unclusteredcount=0;
		for(int i=0;i<data.length;i++)
		{
			if (clusterId[i]!=-1)
				v[clusterId[i]-1]++;
			else unclusteredcount++;
		}
		
		output += "\n Number of unclustered Instances: ";
		output += unclusteredcount;
		output += " ~ ";
		output += (double)(unclusteredcount*100/data.length);
		output += "%";
		
		for (int i=0; i<numcluster;i++)
		{
			output += "\n Cluster ";
			output += i+1;
			output += ": ";
			output += v[i];
			output += " instances";
		}
		
		return output;
		
	
		
	}
	
	/** 
	 * 	suggest eps 
	 
		@param: k used on calculate the distances to kth nearest neighbour of each instance 
		
	 */
	public void SuggestEps(int k,String resultfilename)
	{
		
		 CalculateEps(k,trainSet, resultfilename);
	}
	/** 
	* calculate eps 
	*
	* @param: k used on calculate the distances to kth nearest neighbour of each instance 
	* @param: data for clustering
	*/
	private void CalculateEps(int k, Instance[] data,String resultfilename)
	{
		//double e=0;
		int thre=10;
		ArrayList<Double> arr= new ArrayList<Double>();
		
		int i;
		for(i=0;i<data.length;i++)
			arr.add(KDist(i,k,data));
		
		Collections.sort(arr,Collections.reverseOrder());
		try
		{	
			WritableWorkbook workbook = Workbook.createWorkbook(new File(resultfilename));
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);
			
			Label label2 = new Label(0, 0, "Distance to kth Nearest Neighbour");
			sheet.addCell(label2); 
			for(int j=0; j<arr.size();j++)
			{
				
				Number number1 = new Number(0, j+1, arr.get(j));
				sheet.addCell(number1);
			}
			workbook.write();
			workbook.close(); 
			
		
		}
		catch(Exception e)
		{
			System.out.print(e);
			
		}
		
	}
	/** 
	 * calculate distance to kth nearest neighbour
	 *	@param: instance id that we have to find its neighbour
	 * 	@param: k used on calculate the distances to kth nearest neighbour of each instance 
	 *	@param: data for clustering		
	 */
	private double KDist(int insid,int k, Instance[] data) // calculate the distance from a point to kth nearest neighbour
	{
		double d=0;
		ArrayList arr= new ArrayList();

		int j,loc;
		double r;
		for (j=0;j<insid;j++)
		{
			
			r=getDistance(data[insid], data[j]);
			loc = Collections.binarySearch(arr,r);
			if (loc >= 0) arr.add(loc, r);
			else arr.add((-loc - 1), r);
			
		}
		for (j=insid;j<data.length;j++)
		{
			
			r=getDistance(data[insid], data[j]);;
			loc = Collections.binarySearch(arr,r);
			if (loc >= 0) arr.add(loc, r);
			else arr.add((-loc - 1), r);
			
		}
		
		d =  Double.parseDouble((arr.get(k-1).toString()));
		return d;
		
	}
	
	public Output getOutput () {
		return this.out;
	}
	
	/** 
	 * USING KD TREE TO SPEED UP DBSCAN
	 	
	 */

	/** 
	 * Testing ( use KD Tree to store data) 
	 * @PARAMS testing Set
	 * @param training Set
	 * @param result of training (cluster id of all instances on train set)
	 * @param KD tree for training set
	 * @return the array of cluster ids for all instances on test set
	 */
	
	
	
	private int[] ClusteringTestKD(Instance[] test, Instance[] train, int[] clusterTrain, KDTreeStorage store)
	{
		int[] clusterTest= new int[test.length];
		for (int i=0;i<test.length;i++)
		{
			
			TestKD(test[i],i,clusterTest,train,clusterTrain,store);
		
		}
		
		return clusterTest;
	}
	
	/** 
	 * Clustering on data (use KD Tree to store data) 
	 * @PARAMS: DATA FOR CLUSTERING
	 * @param: KD tree for storing data
	 * @return: array of cluster ids for all instances on data
	 */
	private int[] ClusteringKD(Instance[] data, KDTreeStorage store)
	{
		
		
		int n= data.length;
		int[] clusterId= new int[n+1];
		for (int j=0;j<n;j++)
			clusterId[j]=0;  //unclassified
		
		int count=0;
		int ClId= NextID(-1);
		int i;
		for(i=0;i<data.length;i++)
			if (clusterId[i]==0)
				if ( ExpandClusterKD(ClId, i,data,clusterId,store)) 
					{
						ClId=NextID(ClId);
						count++; //number of clusters
					}
		
		clusterId[n]=count;
		return clusterId;
		
	}
	
	/** 
	 * Expand cluster function on DBSCAN using KD tree
	 * @param: cluster id
	 * @param: id of testing instance
	 * @param: data for clustering
	 * @param: array of clustering result of all instances (store cluster id that instance belongs to)
	 * @param: kd tree storing data
	 */
	private boolean ExpandClusterKD(int ClId, int insId, Instance[] data, int[] clusterId, KDTreeStorage store)
	{
		

		Vector <Integer> seeds= store.FindEpsNeighborhood(data[insId],this.Eps);
	
	
		int i;
		if (seeds.size() < MinPts) //no core point
		{	clusterId[insId]=-1; //noise
			return false;
		}
		
	
		for(i=0;i<seeds.size();i++)
		{
				
				if (clusterId[seeds.elementAt(i)]<=0)
					clusterId[seeds.elementAt(i)]=ClId;
				else
					{
						seeds.removeElementAt(i);
						i--;
						
					}
					
		}
		
		//remove insId
		seeds.removeElement(insId);
		
		while (seeds.size()!=0)
		{
			int cur= seeds.firstElement();
			
			Vector <Integer> result= store.FindEpsNeighborhood(data[cur],this.Eps);
		
			if (result.size()>= MinPts)
			{
				for (i=0;i<result.size();i++)
					{
						int resultP = result.elementAt(i);
						if ((clusterId[resultP]==0)||(clusterId[resultP]==-1))
						{
							if (clusterId[resultP]==0)
								seeds.add(resultP); 
							clusterId[resultP]=ClId;
						}
					}
				
			}
			seeds.removeElement(cur);
			
		}
		
		return true;
		
	}
	
	/** 
	 * Testing instance ins base on clustering result of samples using KD
	 * @param: ins - instance for testing
	 * @param: id- id of testing instance on testSet
	 * @param: clusterId - array of cluster ids each element of testSet
	 * @param: samples - training set
	 * @param: cluster_samples - result of clustering on training set (array of cluster id)
	 * @param: kd tree storing training set
	 */
	private void TestKD(Instance ins, int id, int[] clusterId, Instance[] samples,int[] cluster_samples, KDTreeStorage store)
	{
		store.Insert(ins, id);
		Vector<Integer> v= store.FindEpsNeighborhood(ins, this.Eps);
		for (int i=0;i<v.size();i++)
		{
			Instance x= samples[v.get(i)];
			Vector<Integer> vx = store.FindEpsNeighborhood(x, this.Eps);
			if ((vx.size() >= this.MinPts)&&(cluster_samples[v.get(i)] != -1))
				{
					clusterId[id]=cluster_samples[v.get(i)];
					return;
				}
		}
		clusterId[id] = -1;
	}
}
