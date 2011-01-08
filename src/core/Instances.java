package core;

import java.util.Vector;

/**
 * This is free software and can be distributed under GNU 3 license
 * @author giangbinhtran
 *	
 */



/**
 * This class is to represent list of instances
 */
public class Instances {
	public Vector <Instance> data;
	
	public int numInstance = 0; //number of instances
	public int numAttribute = 0; // number of attributes of each instance
	public Vector <String> attributeName = new Vector<String> ();
	/**
	 * Constructors
	 */
	public Instances () {
		data = new Vector <Instance> ();
		numInstance = 0;
		numAttribute =0;
		attributeName = new Vector<String> ();
	}
	
	public Instances (Vector <Instance> dat) {
		numInstance = dat.size();
		data = new Vector <Instance> (dat);
		numAttribute = data.get(1).numAttribute;
		
	}
	/**
	 * Set the attribute Name of instances, at the same moment, set the number of attribute
	 * @param names
	 */
	public void setAttributeNames (Vector <String> names) {
		attributeName = new Vector<String> (names);
		numAttribute = names.size();
	}
	/**
	 * Add an Instance x to data list
	 * @param x
	 */
	public void add(Instance x) {
		this.data.add(x);
		this.numInstance++;
		if (this.numAttribute == 0) {this.numAttribute = x.numAttribute;}
		else if (this.numAttribute != x.numAttribute) {
			try {
				throw new Exception();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Remore an Instance x
	 * @param x
	 */
	public void remove(Instance x) {
		data.remove(x);
		numInstance --;
	}
	/** 
	 * 
	 * @return Vector of Instancs of data
	 */
	public Vector<Instance> getData () {
		return this.data;
	}
	
	/**
	 * get one instance given index
	 * @param index
	 * @return elemnent 'index' of the instance list
	 */
	public Instance getInstance (int index) {
		return data.get(index);
	}
	
	public void clear () {
		this.data.clear();
		this.numInstance = 0;
	}
	
	public void printInfor () {
		System.out.println("Information of data");
		System.out.println("Number of instances: " + this.numInstance);
		System.out.println("Number of Attribute: " + this.numAttribute);
		System.out.println("Attributes: " + this.attributeName);
		for (int i = 0; i<5; i++) {
			System.out.println(this.getInstance(i).data + " " + this.getInstance(i).originalData);
		}
	}
	
}