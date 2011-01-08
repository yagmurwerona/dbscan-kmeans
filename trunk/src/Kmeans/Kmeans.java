package Kmeans;

import java.util.*;

import core.*;

/**
 * This class is to implemetn Kmeans algorithm
 * For the convinience of distribution, I set input format same to ARFF weka input format
 * The input is read throught Reader
 * the output of experiemnt is stored in the Output 
 * @author giangbinhtran
 * This is free sortware and can be distributed for any purpose with GNU license
 */
public class Kmeans {
	private Output out;
	private InputReader reader;
	private int num_cluster;
	private String dfunction;
	private int maxIterator;
	private String filename = "";
	private int experimentType = 66; // Default train on 100% data
	
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
		maxIterator = 100; 
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
		this.num_cluster =  ncluster;
		this._centre = new Centre [ncluster];
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
		
	}
	
	/**
	 * do experiment with an attribute as the target attribute, all data
	 * 
	 * type = 101 default
	 * @param targetAttribution
	 */
	public void setExperimentType (String targetAttribution) {
		this.experimentType = 101;
		this.targetAttribute = targetAttribution;
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
			if (this.experimentType ==0 || this.experimentType == 101) {//test all data
				testSet = new Instance[total];
				for (int i = 0; i< total; i++) {
					testSet[i] = reader.data.getInstance(i);
				}
			}
			else { // test on another test set
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
			this._centre[i].clear();
		}
	}
	/**
	 * Select random seeds for centers
	 */
	private void selectRandomSeed() {
		boolean [] flag = new boolean [trainSet.length];
		System.out.println("Randomly selection seeds " + trainSet.length);
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
	 * @return
	 */
	private int nearestCentre(Instance x) {
		Double mindistance = Constant._MAX;
		int val = 0;
		for (int i = 0; i < num_cluster; i++) {
			Double dt = distance (_centre[i].centeroid, x);
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
		System.out.println("Finishing randomly selecting seeds");
		int iterator = 0;
		boolean hasMovement; // mark the change on loop
		while (iterator++ <this.maxIterator) {
			System.out.println(" KMEANS: Cluster() " + iterator);
			clearElementList();
			hasMovement = false;
			for (int i = 0; i < trainSet.length; i++) {
				int k = nearestCentre(trainSet[i]);
				_centre[k].addElement(trainSet[i]);
			}
			
			for (int k = 0; k<num_cluster; k++) {
				Instance old_centeroid = _centre[k].centeroid.cloneInstance();
				_centre[k].update();
				if (distance(old_centeroid, _centre[k].centeroid) > 1e-3) {
					hasMovement = true;
				}
			}
			if (! hasMovement) {break;}
		}
		System.out.println("KMEANS: CLUSTER finishes trainig\n");
	}
	
	/**
	 * Testing on test data
	 */
	private void test () {
		int [] cluster = new int [testSet.length];
		for (int i = 0; i< testSet.length; i++) {
			cluster[i] = nearestCentre(testSet[i]);
		}
		
		String output = "Kmean clustering\n";
		output += "Numer of Clusters: " + num_cluster + "\n";
		output += "Distance algorithm: " + this.dfunction + "\n";
		output += "Max iterator: " + this.maxIterator + "\n";
		output += "====================\n";
		output += "Training on " + this.experimentType + " % data : " + trainSet.length + " instances\n";
		output += "Testing on " + (100-this.experimentType) + " % data :" + testSet.length + " instances\n";
		output += "====================\n";
		
		for (int i = 0; i< testSet.length; i++) {
			output += testSet[i].originalData.toString() + " --> CLUSTER " + cluster[i] + "\n";
		}
		
		this.out = new Output(output);
		
	} 
	
	/** 
	 * Running Kmeans
	 * reference: http://nlp.stanford.edu/IR-book/html/htmledition/k-means-1.html#fig:clustfg4
	 * 
	 */
	public void run (){
		prepareData();
		cluster();
		test();
	}
	
	/** 
	 * Noise remove task
	 */
	public void noiseRemove () {
		
	}
	
	/**
	 * Get the output of clustering run
	 */
	public Output getOutput () {
		return this.out;
	}
}
