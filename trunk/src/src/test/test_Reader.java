package test;
import java.io.*;
import java.util.*;
import core.*;

import core.InputReader;
public class test_Reader {
	public static void main (String [] args) {
		InputReader r = new InputReader ("ADULT.arff");
		System.out.println(r.numAttribute);
		System.out.println(r.attributeNames);
		
		Instances data = r.getData();
		data.printInfor();
	}
}
