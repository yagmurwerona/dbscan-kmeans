package core;
/**
 * This is free software and can be distributed under GNU 3 License
 * Author: Giang Binh Tran (2010)
 */
import core.Instances;
import core.Instance;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

public class InputReader {
	public Instances data = new Instances();
	private String filename;
	public int numAttribute = 0;
	public int numInstances = 0;
	public Vector<String> attributeNames = new Vector<String> ();
	private Hashtable <String, Double> hashValue = new Hashtable <String, Double> ();
	private ArrayList <Integer> flag = new ArrayList <Integer> (); // 1 if the relative attribute is NUMERIC, 0 vice versa
	private Constant C = new Constant();
	private boolean missingVal [];
	
	public InputReader () {
		this.filename = "";
		this.data = new Instances();
	}
	
	public InputReader (String fn) {
		this.filename = fn;		
		try {
			this.read(fn);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/** 
	 * Read the header of input file ARFF
	 * @param filename
	 * @throws IOException 
	 */
	public void headerRead (String filename ) throws IOException {
		Scanner scanner = new Scanner (new FileInputStream(filename));
		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				line = line.replaceAll(",\\s+", ",");
				// ignore the empty line
				if (line.compareTo("") != 0) {
					String[] splitLine = line.split("\\s+");
					if (splitLine[0].compareTo(C._DATA) ==0) {break;}
					if (splitLine[0].compareTo(C._ATTRIBUTE) == 0) {
						if (splitLine.length != 3) {
							System.err.println("Wrong format in ATTRIBUTE");
							System.err.println(line);
							throw new IOException ();
						}
						else {
							this.attributeNames.add(splitLine[1]);
							
							if (splitLine[2].compareTo(C._NUMERIC) == 0) {
								// This is the numeric type data
								flag.add(1);
							}
							else {// this is the nominal type data
								//Convert nomial value to double value - using order
								flag.add(0);
								splitLine[2] = splitLine[2].replace("{", "");
								splitLine[2] = splitLine[2].replace("}", "");
								String values[] = splitLine[2].split(",");
								for (int i = 0; i<values.length; i++) {
//									
									hashValue.put (values[i], new Double (i+1));
								}
							}
						}
					}
				}
			}
		} 
		finally {
			scanner.close();
		}
		this.numAttribute = this.attributeNames.size();
		
		
	}
	/**
	 * Read the instances descriptions of the file
	 * @param filename
	 * @throws IOException 
	 */
	public void contentRead (String filename) throws IOException {
		Scanner scanner = new Scanner (new FileInputStream(filename));
		//initialize for missing value flag
		missingVal = new boolean[this.numAttribute];
		try {
			
			boolean reachData = false;
			while (scanner.hasNextLine()) {
				
				while (!reachData) {
					String line = scanner.nextLine();
					if (line.compareTo("")!=0) {
						String spl[] = line.split("\\s+");
						if (spl[0].compareTo(C._DATA) ==0) {reachData = true;}
					}
					if (!scanner.hasNextLine()) {
						System.err.println("No data found!!!");
						throw new IOException();
					}
				} 
				
				// read data 
				
				String line = scanner.nextLine();
				line = line.replaceAll(",\\s+", ",");
				if (line.compareTo("") !=0) {
					this.numInstances ++;
					
					String values [] = line.split(",");
					
					Vector <String> orgData = new Vector<String> ();
					for (int i = 0; i< values.length; i++) {orgData.add(values[i]);}
					
					
					if (values.length != this.numAttribute) {
						System.err.println ("Line " + this.numInstances + " is in wrong format");
						this.numInstances --; // donot count 
					}
					else {
						Vector <Double> temp = new Vector<Double> ();
						for (int i = 0; i< values.length; i++ ) {
							if (flag.get(i).compareTo(1)==0) { // numeric
								temp.add(Double.parseDouble(values[i]));
							} else {
								//handle the missing value: NOT HERE
								if (values[i].compareTo("?") ==0) {//missing value
									temp.add(C._MAX); // marked as the missing value
									missingVal[i] = true;
								}
								else {
									temp.add(hashValue.get(values[i]));
								}
							}
						}
						Instance insx = new Instance (temp, orgData);
						this.data.add(insx);
						
					}
				}
			}
		} finally {scanner.close();}
		
	}
	
	/**
	 * Read an ARFF file
	 * @param filename
	 * @throws IOException 
	 */
	public void read(String filename) throws IOException {
		this.headerRead (filename);
		this.contentRead (filename);
		this.data = missingHandle();
		this.data.setAttributeNames(this.attributeNames);
		
	}
	/**
	 * Handle missing value by everage assigning method
	 */
	public Instances missingHandle () {
		Double [] Sum = new Double [this.numAttribute];
		Double [] assgned = new Double [this.numAttribute];
		int [] count = new int [this.numAttribute];
		for (int i = 0; i< this.numAttribute; i++) {
			Sum[i] = 0.0;
			assgned[i] = 0.0;
		}
		
		for (Instance x : this.data.getData()) {
//			System.out.println(x.data);
			for (int i =0; i< this.numAttribute; i++) {
			
				if (x.getAttribute(i).compareTo(C._MAX) !=0) {
					Sum[i] += x.getAttribute(i);
					count[i] ++;
				}
			}
		}
		
		for (int i = 0; i< this.numAttribute; i++ ) {assgned[i] = Sum[i]/count[i];}
		
		Instances handledData = new Instances();
		for (Instance x: this.data.getData()) {
			Vector <Double> dat = new Vector<Double> ();
			for (int j = 0; j<this.numAttribute; j++) {
				if (x.getAttribute(j).compareTo(C._MAX) == 0) {
					dat.add(assgned[j]);
				}
				else {dat.add(x.getAttribute(j));}
			}
			handledData.add(new Instance(dat, x.originalData));
		}
		// re-assign the data
		return handledData;
	}
	
	public Instances getData () {
		return this.data;
	}
}
