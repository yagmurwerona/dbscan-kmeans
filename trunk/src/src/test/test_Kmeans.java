package test;
import core.*;
import Kmeans.*;
import java.io.*;
import java.util.*;

public class test_Kmeans {
	public static void main (String [] args) {
		Kmeans km = new Kmeans ();
		km.setInput("ADULT.arff");
		km.setDistanceAlgorithm("Mahattan");
//		//Testing with splitting data into training/testing data
//		km.setExperimentType(10);
//		
//		//Testing with clustering regarding to target attribute
		km.setExperimentType("class");
////		km.setExperimentType(102,"car.arff");
//		
		km.setCluster(2);
		km.run();
		
//		Test noise removal
//		km.noiseRemove(0.5, 10, "Euclidean");
		System.out.println(km.getOutput().getContent());
	}
}
