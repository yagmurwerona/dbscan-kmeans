package Kmeans;

import java.util.*;

import core.*;

/**
 * This class is to implemetn Kmeans algorithm
 * For the convinience of distribution, I set input format same to ARFF weka input format
 * The input is read throught Reader
 * the output of experiemnt is stored in the Output 
 * @author giangbinhtran
 * This is free software and can be distributed for any purpose with GNU license
 */
public class Kmeans {
	private Output out;
	private InputReader reader;
	private int num_cluster;
	private String dfunction;
	private int maxIterator = 500;
	private String filename = "";
	private int experimentType = 66; // by default train on 100% data
	private int _iterator = 0;
	private int _without = -1; // by default: dont choose any attribute as the target attribute
	
	private String targetAttribute = "";
	private String testfile = "";
	
	private Instance[] trainSet;
	private Instance[] testSet;
	private Centre [] _centre;

	
	/**
	 * Constructor
	 */
	public Kmeans () {
		out = new Output();
		reader = null;
		num_cluster = 3; //by default 3
		this._centre = new Centre[num_cluster]; 
		dfunction = "Euclidean"; // by default
	}
	/**
	 * Constructor fucntion 
	 * @param ncluster
	 * @param distanceAlg
	 * @param maxIter
	 */
	public Kmeans (int ncluster, String distanceAlg, int maxIter) {
		out = new Output();
		reader = null;
		num_cluster = ncluster;
		dfunction = distanceAlg;
		maxIterator = maxIter;
		this._centre = new Centre [ncluster];
	}
	
	public void setCluster (int ncluster){
		System.out.println("Log: Number of cluster is set " + ncluster);
		this.num_cluster =  ncluster;
		this._centre = new Centre [ncluster];
		for (int i = 0; i< ncluster; i++) {_centre[i] = new Centre();}
	}
	
	public void setDistanceAlgorithm(String al) {
		this.dfunction = al;
	}
	
	public void setMaxIterator (int it) {
		this.maxIterator = it;
	}
	
	/**
	 * Set the input for the clustering kmeans algorithm
	 * when the inpput filename is given, it will automatically read the file and store in the Reaer
	 * @param Filename
	 */
	public void setInput (String Filename) {
		this.filename =Filename;
		this.reader = new InputReader(this.filename);
	}
	
	/**
	 * Set experiment running type
	 * type  = 0: training on all data and test on all of them
	 * 0 < type <100: train on type% data and test on (100-type)% data
	 * @param type
	 */
	public void setExperimentType (int type) {
		this.experimentType = type;
	}
	/**
	 * training on all data and test on given testing data
	 * @param type = 102
	 * @param test_filename
	 */
	public void setExperimentType (int type, String test_filename) {
		this.experimentType = type;
		this.testfile = test_filename;
		if (type!=102) {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.err.println("This fucntion is to set the experiment with testing file");
				System.err.println("Type should be 102");
				e.printStackTrace();
			} 
		}
		this._without = -1; // no considering to target class in this mode
		
	}
	
	/**
	 * do experiment with an attribute as the target attribute, all data
	 * 
	 * type = 101 default
	 * @param targetAttribution
	 */
	public void setExperimentType (String targetAttribute) {
		this.experimentType = 101;
		setTargetAttribute(targetAttribute);
	}
	
	/**
	 * Set the target attribute as the guide for clustering
	 * @param target
	 */
	public void setTargetAttribute (String target) {
		this.targetAttribute = target;
//		System.out.println(this.reader.attributeNames);
		for (int i = 0; i<this.reader.numAttribute; i++) {
			if (this.reader.attributeNames.get(i).compareTo(target)==0) {
				this._without = i; 
				break;
			}
		}
	}
	/**
	 * preparing train/test data before running the clustering Kmeans algorithm
	 */
	private void prepareData () {
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
			if (this.experimentType ==0 || this.experimentType == 101) {//test all data, =101: test with target class
				testSet = new Instance[total];
				for (int i = 0; i< total; i++) {
					testSet[i] = reader.data.getInstance(i);
				}
			}
			else { // test on another test set type = 102
				InputReader testrd = new InputReader(this.testfile);
				testSet = new Instance[testrd.data.numInstance];
				for (int i = 0; i< testrd.data.numInstance; i++) {
					testSet[i] = testrd.data.getInstance(i);
				}
			}
		}
	}
	/**
	 * distance function for k-mean, using method given in this.dfunction (which is "Euclidean" or "Mahattan")
	 * @param x
	 * @param y
	 * @return distance
	 */
	private Double distance (Instance x, Instance y) {
		if (this.dfunction.compareTo("Euclidean") == 0) {
			return DistanceFunction.euclideanDistance(x, y);
		}
		else /*if (this.dfunction.compareTo("Mahattan") == 0) */ {
			return DistanceFunction.mahattanDistance(x, y);
		}
	}
	/**
	 * Distance function of 2 instances regardless target attribute (without) by DFunction given before
	 * @param x
	 * @param y
	 * @param without
	 * @return
	 */
	private Double distance (Instance x, Instance y, int without) {
		if (this.dfunction.compareTo("Euclidean") == 0) {
			return DistanceFunction.euclideanDistance(x, y, without);
		}
		else /*if (this.dfunction.compareTo("Mahattan") == 0) */ {
			return DistanceFunction.mahattanDistance(x, y, without);
		}
	}
	/**
	 * Clear centers
	 */
	private void clearElementList () {
		for (int i = 0; i<this.num_cluster; i++) {
			if (this._centre[i] == null ) {
				try {
					throw new Exception ();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("Clearing the null centre");
					e.printStackTrace();
				}
				
			}
			this._centre[i].clear();
		}
	}
	/**
	 * Select random seeds for centers
	 */
	private void selectRandomSeed() {
		boolean [] flag = new boolean [trainSet.length];
//		System.out.println("Randomly selection seeds " + trainSet.length);
		Random r = new Random(123);
		for (int i = 0; i< this.num_cluster; i++) {
			int k = 0;
			do {
				k = r.nextInt(trainSet.length);
			} while (flag[k]);
			//assign the center i for Instance k
//			System.out.println(k + trainSet[k].data.toString());
			this._centre[i] = new Centre (trainSet[k]);
			flag[k] = true;
		}
	}
	/**
	 * Finding the nearest centre for one instance
	 * @param x
	 * @return index of the nearest cluster
	 */
	private int nearestCentre(Instance x) {
		Double mindistance = Constant._MAX;
		int val = 0;
		for (int i = 0; i < num_cluster; i++) {
			Double dt; 
			// then calculate distance between 2 instances, consider if the _without attribute is set
			// if _without is set --> clustering regarding to target attribute and vice versa
			if (this._without == -1) { 
				dt = distance (_centre[i].centeroid, x);
			}
			else {
				dt = distance (_centre[i].centeroid, x, this._without);
			}
			if (dt < mindistance) {
				mindistance = dt;
				val = i;
			}
		}
		return val;
	}
	
	/** Clustering
	 * 
	 */
	private void cluster () {
		selectRandomSeed();
		System.out.println("Log: Finishing randomly selecting seeds");
		int iterator = 0;
		boolean hasMovement; // mark the change on loop
		while (iterator++ <this.maxIterator) {
//			System.out.println(" KMEANS: Cluster() " + iterator);
			clearElementList();
			hasMovement = false;
			for (int i = 0; i < trainSet.length; i++) {
				int k = nearestCentre(trainSet[i]);
				_centre[k].addElement(trainSet[i]);
			}
			
			for (int k = 0; k<num_cluster; k++) {
				Instance old_centeroid = _centre[k].centeroid.cloneInstance();
				_centre[k].update(this.reader.getAttributeType());
				if (distance(old_centeroid, _centre[k].centeroid) > 1e-3) {
					hasMovement = true;
				}
			}
			if (! hasMovement) {break;}
		}
		System.out.println("KMEANS: CLUSTER finishes trainig\n");
		this._iterator = iterator;
	}
	
	/**
	 * Testing on test data
	 */
	private void test () {
		int [] cluster = new int [testSet.length];
		long [] counts = new long [testSet.length];
		for (int i = 0; i< testSet.length; i++) {
			cluster[i] = nearestCentre(testSet[i]);
			counts[cluster[i]] ++;
		}
		
		String output = "Kmean clustering\n";
		if (this._without != -1) {
			output += "Cluster regarding to target class: " + this.targetAttribute + "\n";
			output += "-->NOTE: This case, number of cluster automatically set by NUMBER Of Class\n";
		}
		output += "Numer of Clusters: " + num_cluster + "\n";
		output += "Distance algorithm: " + this.dfunction + "\n";
		output += "Max iterator: " + this.maxIterator + "\n";
		output += "====================\n";
		output += "Training on " + trainSet.length + " instances\n";
		output += "Testing on " + testSet.length + " instances\n";
		output += "====================\n";
		
		output += "RESULT:\n";
		if (this._without ==-1) {output += "Number of iterator: " + this._iterator + "\n";}
		output += "Missing values globally replaced with mean method\n";
		output += "Cluster centroids:\n";
		output += "\t " + this.reader.attributeNames + "\n";
		for (int i = 0; i<this.num_cluster; i++) {
			output += "Cluster " + i + ":\t" + this._centre[i].toInforString(this.reader.getAttributeType()) + "\n";
			
		}
		output += "=========Training===========\n";
		for (int i =0; i<this.num_cluster; i++) {
			String tmp = String.format("%2.3f", (double) this._centre[i].elements.numInstance/ trainSet.length * 100);
			output += "Cluster " + i + ":\t" + this._centre[i].elements.numInstance + "("+ tmp + " %)\n";
		}
		output += "=========Testing===========\n";
		output += "Cluster instances:\n";
		
		for (int i =0; i<this.num_cluster; i++) {
			String tmp = String.format("%2.3f", (double) counts[i]/ testSet.length * 100);
			output += "Cluster " + i + ":\t" + counts[i]+ "("+ tmp + " %)\n";
		}
		
		if (this._without!=-1 ) {
			output += "=========Class assigning===========\n";
			output += "Target Class: " + this.targetAttribute + "\n";
			for (int i = 0; i < this.num_cluster; i++) {
				output += _centre[i].name + " ---> " + "Cluster " + i + "\n";
			}
			// compute te error rate
			int wrongcase = 0;
			for (int i = 0; i<testSet.length; i++) {
				if (_centre[cluster[i]].name.compareTo(testSet[i].getOriginalAttribute(this._without)) !=0) {
					wrongcase++;
				}
			}
			output += "Number of wrong clustered instances: " + wrongcase + " ";
			String tmp = String.format ("%2.3f", (double) wrongcase / testSet.length * 100);
			output += "(~" + tmp + "%)\n";
		}
		this.out = new Output(output);
		
	} 
	/**
	 * Clustering the data regard to the target class (target attribute)
	 * Automatically set the  number of clusters equal to number of classes
	 */
	private void clusterByTargetClass () {
		
		Vector<String> attributeType = this.reader.getAttributeType();
		
		String [] targetAttributes = attributeType.get(this._without).split(",");
		//Automatically chose the number of cluster is number of classes
//		System.out.println(targetAttributes.length);
		this.setCluster(targetAttributes.length);
		
		this.clearElementList();
		for (int i = 0; i <this.num_cluster; i++) {
			_centre[i].setName(targetAttributes[i]);
			for (int k = 0; k<trainSet.length; k++) {
				if (trainSet[k].getOriginalAttribute(this._without).compareTo(_centre[i].name) ==0) {
					//finding all Instances that belongs to this class
					_centre[i].addElement(trainSet[k]);
				}
			}
			//Update the centroid of this cluster
			_centre[i].update(this.reader.getAttributeType());
			
		}
		
	}
	
	/** 
	 * Running Kmeans
	 * reference: http://nlp.stanford.edu/IR-book/html/htmledition/k-means-1.html#fig:clustfg4
	 * 
	 */
	public void run (){
		prepareData();
		if (this.experimentType !=101) { // dont consider target class in clustering
			cluster();
			
		} else { //consider target class in clustering
			clusterByTargetClass();
		}
		test();
	}
	
	/** 
	 * Noise remove task
	 * A cluster is considered as a noise if it contains only few instances
	 * To tackle the atask of noise removal, we should set the FALSE threshold for noise cluster
	 * @param: FALSE threshold = percentage of full data which indicates threshold for noise cluster size
	 * @param: number of cluster
	 * @param: Distance Algorithm (Euclidean or Mahattan)
	 */
	public void noiseRemove (Double false_threshold, int cluster, String distanceAlgorithm) {
		//setting for all
		this.setExperimentType(0); // for taking all training data in training step
		this.setCluster(cluster);
		this.setDistanceAlgorithm(distanceAlgorithm);
		prepareData();
		if (trainSet.length != this.reader.numInstances) {
			try {
				throw new Exception ();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Kmean Noise Remove error in size of training data");
				e.printStackTrace();
			}
		}
		cluster(); //run clustering with this setting
		
		String output = "Kmean clustering\n";
		output += "Noise Removal task\n";
		output += "Distance algorithm: " + this.dfunction + "\n";
		output += "Number of cluster: " + this.num_cluster + "\n";
		output += "Max iterator: " + this.maxIterator + "\n";
		output += "====================\n";
		output += "Noise removing on " + trainSet.length + " instances\n";
		output += "False threshold for noise removeing : "+ false_threshold + "%\n";
		output += "Missing values globally replaced with mean method\n";
		output += "Cluster centroids:\n";
		output += "\t " + this.reader.attributeNames + "\n";
		for (int i = 0; i<this.num_cluster; i++) {
			output += "Cluster " + i + ":\t" + this._centre[i].toInforString(this.reader.getAttributeType()) + "\n";
			
		}
		output += "=========Clustering result===========\n";
		for (int i =0; i<this.num_cluster; i++) {
			String tmp = String.format("%2.3f", (double) this._centre[i].elements.numInstance/ trainSet.length * 100);
			output += "Cluster " + i + ":\t" + this._centre[i].elements.numInstance + "("+ tmp + " %)\n";
		}
		output += "Noise Clusters (instances belong to those clusters will be considered noise)\n";
		int countnoise = 0;
		for (int i = 0; i< this.num_cluster; i++) {
			
			if (_centre[i].elements.numInstance < false_threshold * trainSet.length / 100) {
				output += "===>\t";
				output += "Cluster number " + i + ": " + _centre[i].toInforString(this.reader.getAttributeType()) + "\n";
				output += "Instances: \n";
				output += _centre[i].elements.getInfor() + "\n";
				countnoise += _centre[i].elements.numInstance;
			}
		}
		output += countnoise + " instances are noise\n";
		this.out = new Output (output);
		
	}
	
	/**
	 * Get the output of clustering run
	 */
	public Output getOutput () {
		return this.out;
	}
	/**
	 * @return the reader
	 */
	public InputReader getReader() {
		return reader;
	}
	/**
	 * @param reader the reader to set
	 */
	public void setReader(InputReader reader) {
		this.reader = reader;
	}
	
	public int getNumCluster () {
		return this.num_cluster;
	}
	
	public String getAlg () {
		return this.dfunction;
	}

}
