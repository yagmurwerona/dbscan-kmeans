package core;
/** 
 * This is free software and can be distributed under GNU3 license
 * Author: Giang Binh Tran (2011)
 */
import java.io.*;
import java.util.*;

/**
 * This class is to store result of clustering algorithm
 * @author giangbinhtran
 * 
 */
public class Output {
	private String content;
	private String information;
	
	public Output () {
		content = "";
		information = "";
		
	}
	/**
	 * Initialize new instance with content
	 * @param content
	 */
	public Output (String content) {
		this.content = content;
	}

	/**
	 * Initialize new instance with content and information of parameters
	 * @param content
	 * @param infor
	 */
	public Output (String content, String infor) {
		this.content = content;
		this.information = infor;
	}
	
	public String getContent() {
		return this.content;
	}
	public String getInfor () {
		return this.information;
	}
	public void setContent (String content) {
		this.content = content;
	}
	public void setInfor (String infor) {
		this.information = infor;
	}
}
