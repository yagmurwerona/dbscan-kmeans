package test;
import core.*;
import java.io.*;
import java.util.*;

/**
 * Test the distance measurement
 * @author giangbinhtran
 *
 */

public class test_distance {
	public static void main (String [] args) {
		Vector <Double> x = new Vector <Double> ();
		Double [] a = {0.0,0.0,0.0,0.0};
		Double [] b = {1.0,1.0,1.0,1.0};
		x.addAll(Arrays.asList(a));
		Vector <Double> y = new Vector <Double> ();
		y.addAll (Arrays.asList(b));
		System.out.println(x);
		System.out.println(y);
		
		String [] ordata = {"a", "a","a","a"};
		
		//distance :EUclediean = 2, Mahattan distance = 4;
		Instance I1 = new Instance ();
		Instance I2 = new Instance ();
		I1.setNormalizedData(x);
		I2.setNormalizedData(y);
		System.out.println("Euclidean distance :" + DistanceFunction.euclideanDistance(I1, I2));
		System.out.println("Mahattan distance :" + DistanceFunction.mahattanDistance(I1, I2));
		
		Instance I3 = I2.cloneInstance();
		
		
		// Test center
		Centre c = new Centre(I2);
		System.out.println(c.centeroid.data);
		c.addElement(I1);
		c.addElement(I2);
		
		c.update();
		System.out.println(c.centeroid.data);
		
		boolean [] xx = new boolean [2];
		System.out.println(xx[0]);
		
	} 
}
