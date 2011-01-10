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
	private String relation; //Name of data
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
	
	public String getInfor () {
//		System.out.println("Information of data");
//		System.out.println("Number of instances: " + this.numInstance);
//		System.out.println("Number of Attribute: " + this.numAttribute);
//		System.out.println("Attributes: " + this.attributeName);
		String outp = "";
		for (int i = 0; i<this.numInstance; i++) {
			outp += i + "\t" + this.getInstance(i).originalData + "\n";
		}
		return outp;
	}

	/**
	 * @return the numInstance
	 */
	public int getNumInstance() {
		return numInstance;
	}

	/**
	 * @param numInstance the numInstance to set
	 */
	public void setNumInstance(int numInstance) {
		this.numInstance = numInstance;
	}

	/**
	 * @return the numAttribute
	 */
	public int getNumAttribute() {
		return numAttribute;
	}

	/**
	 * @param numAttribute the numAttribute to set
	 */
	public void setNumAttribute(int numAttribute) {
		this.numAttribute = numAttribute;
	}

	/**
	 * @return the relation
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * @return the attributeName
	 */
	public Vector<String> getAttributeName() {
		return attributeName;
	}

	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(Vector<String> attributeName) {
		this.attributeName = attributeName;
	}
	
}
