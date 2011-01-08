/**
 * This is free software and can be distributed under GNU 3 license
 * @Author: Giang Binh Tran (2010)
 *  
 */

package core;

import java.util.Vector;

/**
 * This class is a representing of one instance
 * Format: numeric vector
 * @author giangbinhtran
 *
 */

public class Instance {
	public Vector <Double> data; // store the vector of attributes of double ( - already normalized)
	public Vector <String> originalData; // original (without conversion from nominal data to Double)
	public int numAttribute = 0; // numer of Attributes
	
	public Instance () {
		data = new Vector <Double> ();
		originalData = new Vector <String> ();
		numAttribute =0;
	}
	
	public Instance (Vector<Double> dat, Vector <String> ordat) {
		numAttribute = dat.size();
		data = new Vector <Double> (dat);
		originalData = new Vector <String> (ordat);
	}
	
	public void update(Vector <Double> dat) {
		this.data.clear();
		this.data = (Vector <Double>) dat.clone();
		
	}
	
	/** seting the data vector for the instance
	 * 
	 * @param dat
	 */
	public void setNormalizedData (Vector<Double> dat) {
		this.data = new Vector<Double> (dat);
		numAttribute = dat.size();
	}
	
	/**
	 * Setting the original data vector for the instance
	 * 
	 * @param ordat
	 */
	public void setOriginalData (Vector <String> ordat) {
		this.originalData = new Vector <String> (ordat);
	}
	
	/**
	 * Get attribut value number i
	 * @param i
	 * @return double: attribute value  
	 */
	public Double getAttribute(int i) {
		return data.get(i);
	}
	
	/**
	 * Get attribute orginnal value 
	 * @param i
	 * @return (String) original attribute value
	 */
	public String getOriginalAttribute (int i) {
		return originalData.get(i);
	}
	
	/**
	 * Clone a nother instance
	 */
	public Instance cloneInstance () {
		return new Instance(this.data, this.originalData);
		
	}
	
	
}
