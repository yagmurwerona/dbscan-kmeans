package test;
import dbscan.dbscan;

public class test_dbscan {
	public static void main (String [] args) {
		
		dbscan dbsc= new dbscan("car.arff");
		dbsc.setEps(1);
		dbsc.setExperimentType("66");
		dbsc.setMinPts(4);
		dbsc.setTarget("class");
		
		dbsc.setUsingKD("1");
		dbsc.setDistanceFunction("MANHATTAN");
		dbsc.preProcessing(); //must run this function before use dbscan
		
		//start use dbscan
		dbsc.SuggestEps(4,"eps_test.xls");
		dbsc.RunDBSCAN();
		
	}
}
