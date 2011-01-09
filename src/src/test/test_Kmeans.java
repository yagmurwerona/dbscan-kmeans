package test;
import core.*;
import Kmeans.*;
import java.io.*;
import java.util.*;

public class test_Kmeans {
	public static void main (String [] args) {
		Kmeans km = new Kmeans ();
		km.setInput("car.arff");
		km.setDistanceAlgorithm("Mahattan");
		//Testing with splitting data into training/testing data
		km.setExperimentType(10);
		
		//Testing with clustering regarding to target attribute
		km.setExperimentType("class");
//		km.setExperimentType(102,"car.arff");
		
		km.setCluster(10);
		km.run();
		System.out.println(km.getOutput().getContent());
	}
}
