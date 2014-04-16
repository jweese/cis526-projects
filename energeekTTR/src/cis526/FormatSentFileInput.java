/**
 * 
 */
package cis526;

import java.util.HashMap;

/**
 * @author yucongli
 * this is a wrapper class for sentence file input
 * including: String[], HashMap<String, String>
 */
public class FormatSentFileInput {
	private String[] refList = new String[4];
	//the keys are turkerID and values are the corresponding english sentence
	private HashMap<String, String> idAndTransDict = new HashMap<String, String>();
	
	/**
	 * constructor with no arguments
	 */
	public FormatSentFileInput(){
		this.refList = null;
		this.idAndTransDict = null;
	}
	
	/**
	 * constructor with the following two arguments
	 * @param refList
	 * @param transDict
	 */
	public FormatSentFileInput(String[] refList, HashMap<String, String> transDict){
		this.refList = refList;
		this.idAndTransDict = transDict;
	}
	
	/**
	 * setter method
	 * @param refList
	 * @param transDict
	 */
	public void setData(String[] refList, HashMap<String, String> transDict){
		this.refList = refList;
		this.idAndTransDict = transDict;
	}
	
	/**
	 * getter method
	 * @return
	 */
	public String[] getRefList(){
		return refList;
	}
	
	/**
	 * getter method
	 * @return
	 */
	public HashMap<String, String> getTransDict(){
		return idAndTransDict;
	}
}