package core;

import java.util.Arrays;
import java.util.Vector;

/**
 * Centre of flatting cluster
 * contain: Cordinations (x, y) 
 * and name
 * @author giangbinhtran
 *
 */
public class Centre {
	public Instance centeroid;
	public Instances elements;
	public String name ="not assigned";
	
	/** 
	 * Constructor
	 */
	public Centre () {
		centeroid = new Instance ();
		elements = new Instances ();
	}
	public Centre (Vector <Double> dat) {
		this.centeroid = new Instance ();
		this.centeroid.setNormalizedData(dat);
		elements = new Instances ();
	}
	
	public Centre (Vector <Double> dat, String name) {
		this.centeroid = new Instance ();
		this.centeroid.setNormalizedData(dat);
		this.name = name;
		elements = new Instances ();
	}
	
	public Centre (Instance x) {
		this.centeroid = x.cloneInstance();
		elements = new Instances ();
	}
	/**
	 * Update the centeroid of the Center
	 */
	public void update(Vector<String> typeValues) {
//		System.out.println ("log at Centre.java: number of instances " + elements.numInstance);
		if (elements.numInstance >0 && centeroid.numAttribute == 0) {
			centeroid.numAttribute = elements.numAttribute;
		}
		Double [] sum = new Double [centeroid.numAttribute]; 
		for (int k = 0; k < centeroid.numAttribute; k++) {sum[k] = 0.0;}
		
		for (int i = 0; i<elements.numInstance; i++) {
			for (int k = 0; k < centeroid.numAttribute; k++ ) {
				sum[k] += elements.getInstance(i).getAttribute(k);
			}
		}
		
		// get the medium
		for (int k =0; k< centeroid.numAttribute; k++ ) {
			if (typeValues.get(k).compareTo(Constant._NUMERIC) == 0) {
				sum [k] /= elements.numInstance;
			}
			else {
				sum [k] = 1.0 * Math.round(sum[k]/elements.numInstance);
			}
		}
		
		centeroid.update(new Vector<Double> (Arrays.asList(sum)));
		
		
	}
	public void setName (String name) {
		this.name  = name;

	}
	
	public void addElement (Instance x) {
		this.elements.add(x);
//		System.out.println("log: Centre.java :" + x.data);
	}
	
	/**
	 * clear the instance list (current) of centre
	 */
	public void clear() {
		elements.clear();
	}
	
	/**
	 * Get information of the cluster centroids in String format
	 */
	public String toInforString(Vector<String> typeValues) {
		String res = "";
		for (int i = 0; i < centeroid.numAttribute; i++) {
			if (typeValues.get(i).compareTo(Constant._NUMERIC) ==0) {
				String s = String.format("%.3f", centeroid.getAttribute(i));
				res += s + "\t";
			}
			else {
				String [] temp = typeValues.get(i).split(",");
//				System.out.println(typeValues.get(i) + "\t" + (int) Math.round(centeroid.getAttribute(i)));
				res += temp[(int) Math.round(centeroid.getAttribute(i)) - 1]+ "\t";
			}
		}
		return res;
	}
	
}
