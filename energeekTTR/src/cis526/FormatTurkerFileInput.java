/**
 * 
 */
package cis526;

import java.util.ArrayList;

/**
 * @author yucongli
 * this is a wrapper class for turker file
 * including: String, ArrayList<Integer>
 */
public class FormatTurkerFileInput {
	private String turkerID = new String();
	private ArrayList<Integer> translatedSentNum = new ArrayList<Integer>();
	
	/**
	 * constructor with no input
	 */
	public FormatTurkerFileInput(){
		this.turkerID = "";
		this.translatedSentNum = null;
	}
	
	/**
	 * constructor with the following two arguments
	 * @param turkerID
	 * @param translatedSentNum
	 */
	public FormatTurkerFileInput(String turkerID, ArrayList<Integer> translatedSentNum){
		this.turkerID = turkerID;
		this.translatedSentNum = translatedSentNum;
	}
	
	/**
	 * setter method
	 * @param turkerID
	 * @param translatedSentNum
	 */
	public void setData(String turkerID, ArrayList<Integer> translatedSentNum){
		this.turkerID = turkerID;
		this.translatedSentNum = translatedSentNum;
	}
	
	/**
	 * getter method
	 * @return: turkerID (String)
	 */
	public String getTurkerID(){
		return turkerID;
	}
	
	/**
	 * getter method
	 * @return: translated SentNum (ArrayList<Integer>)
	 */
	public ArrayList<Integer> getTranslatedSentNum(){
		return translatedSentNum;
	}
}