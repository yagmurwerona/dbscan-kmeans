package core;

/**
 * This program is free sortware and can be distributed under GNU 3 License
 * author: Giang Binh Tran (2010)
 *
 */
import java.io.*;
import java.util.*;
import core.*;
/**
 * Compute the distance bt 2 instances
 * @author giangbinhtran
 *
 */
public class DistanceFunction {
	/**
	 * Euclidean Distance of 2 instances 
	 * @param x instance
	 * @param y instance
	 * @return distance double
	 */
	public static Double euclideanDistance(Instance x, Instance y) {
		Double sqr = new Double(0);
		for (int i = 0; i<x.numAttribute; i++) {
			sqr += Math.pow(Math.abs(x.data.get(i) - y.data.get(i)), 2);
		}
		return Math.sqrt(sqr);
	}
	/**
	 * Euclidean distance of 2 instances regardless to attribute number i
	 * Instances:
	 * @param x Instance
	 * @param y Instance
	 * @param i int
	 * @return euclidean distance
	 */
	public static Double euclideanDistance(Instance x, Instance y, int without) {
		Double sqr = new Double(0);
		for (int i = 0; i<x.numAttribute; i++) {
			if (i != without) {
				sqr += Math.pow(Math.abs(x.data.get(i) - y.data.get(i)), 2);
			}
		}
		return Math.sqrt(sqr);
	}
	/**
	 * Mahattan distance of 2 instances
	 * @param x
	 * @param y
	 * @return distance
	 */
	public static Double mahattanDistance (Instance x, Instance y) {
		Double sqr = new Double(0);
		for (int i = 0; i<x.numAttribute; i++) {
			sqr += Math.abs(x.data.get(i) - y.data.get(i));
		}
		return sqr;
	}
	/**
	 * Mahattan distance regardless 1 attribute (target attrubute) i
	 * @param x
	 * @param y
	 * @param i (without attribute)
	 * @return (double) distance
	 */
	public static Double mahattanDistance (Instance x, Instance y, int without) {
		Double sqr = new Double(0);
		for (int i = 0; i<x.numAttribute; i++) {
			if (i!= without) {
				sqr += Math.abs(x.data.get(i) - y.data.get(i));
			}
		}
		return sqr;
	}
}


