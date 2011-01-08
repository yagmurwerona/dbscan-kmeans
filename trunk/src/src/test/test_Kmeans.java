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
		km.setCluster(4);
		km.run();
		System.out.println(km.getOutput().getContent());
	}
}
