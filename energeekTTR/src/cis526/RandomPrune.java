/**
 * 
 */
package cis526;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @author yucong li
 * randomly prune translators
 */
public class RandomPrune {
	
	//all translator IDs
	private ArrayList<String> transltrID = new ArrayList<String>();
	
	/**
	 * read file from data at the beginning
	 */
	public RandomPrune() {
		gatherData();
	}
	
	/**
	 * get all translator IDs
	 * @return 
	 */
	public ArrayList<String> getAllTanslatorID() {
		return transltrID;
	}
	
	/**
	 * 
	 * @param remains is the number of remaining translators
	 * @return an ArrayList of random translator IDs with the size of "remain"
	 */
	public ArrayList<String> prune(int remains){
		Random r = new java.util.Random();
		ArrayList<String> prunedList = new ArrayList<String>();
		
		int counter = 0;
		while (counter < remains) {
		    int tmp = r.nextInt(transltrID.size());
		    if (!prunedList.contains(transltrID.get(tmp))) { //if the number is generated then ignore
		    	prunedList.add(transltrID.get(tmp));
		    	counter++;
		    }
		} 
		return prunedList;
	}
	
	/**
	 * read from data files and find out all the translator IDs
	 */
	private void gatherData() {
		final int sntnsNum = 1683; //1683 files in total
		int sntnsIdx = 1;
		
		while(sntnsIdx <= sntnsNum) {
			//gather data
			FormatSentFileInput oneSentData = ReadFile.sentFileInput(sntnsIdx);
			HashMap<String, String> dct = oneSentData.getTransDict();
	        
	        //traverse all the translated sentence for one Urdu sentence
	        //and find the highest score
	        Iterator<Entry<String, String>> iter = dct.entrySet().iterator();
	        while (iter.hasNext()) {
	        	Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
	        	String key = entry.getKey();
	        	if (!transltrID.contains(key)) transltrID.add(key);
	        }
	        sntnsIdx++;
		}
	}
}