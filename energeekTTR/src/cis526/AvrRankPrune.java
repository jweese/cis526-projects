package cis526;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import MT.TERcalc;

/**
 * @author yucongli
 *
 */
public class AvrRankPrune {
	
	private HashMap<String, Double> idScoreMap = new HashMap<String, Double>();
	private TreeMap<String, Double> sortedMap = new TreeMap<String, Double>();
	
	/**
	 * get translator id and the corresponding average TER scores 
	 * and sorting at the beginning
	 */
	public AvrRankPrune() {
		computeIdAndAvrScore();
		sortingHashMap();
	}
	
	/**
	 * 
	 * @return
	 */
	public TreeMap<String, Double> getSortedMap(){
		return sortedMap;
	}
	
	/**
	 * 
	 * @return an ArrayList of best translator IDs with the size of "remain"
	 */
	public ArrayList<String> prune(int remains) {

        ArrayList<String> returnList = new ArrayList<String>();
        
        int counter = 0;
        Iterator<Entry<String, Double>> iter = sortedMap.entrySet().iterator();
        while (iter.hasNext()) {
        	Map.Entry<String, Double> entry = (Map.Entry<String, Double>) iter.next();
        	String key = entry.getKey();
        	returnList.add(key);
        	
        	counter++;
        	if (counter >= remains) break;
        }
		return returnList;
	}
	
	/**
	 * sort the global hash map
	 */
	private void sortingHashMap() {
        ValueComparator cmprtr =  new ValueComparator(idScoreMap);
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(cmprtr);
        sorted_map.putAll(idScoreMap);
        sortedMap = sorted_map;
	}
	
	/**
	 * generate a HashMap with translator IDs and corresponding average scores 
	 * from data files
	 */
	private void computeIdAndAvrScore() {
		//translatorPerformance 
		HashMap<String, Integer> transNum = new HashMap<String, Integer>();
		HashMap<String, Double> transScore = new HashMap<String, Double>();
		
		
		final int sntnsNum = 1683; //1683 files in total
		int sntnsIdx = 1;
		double tmpScore;
		
		while(sntnsIdx <= sntnsNum) {
			//gather data
			FormatSentFileInput oneSentData = ReadFile.sentFileInput(sntnsIdx);
			String[] lst = oneSentData.getRefList();
			HashMap<String, String> dct = oneSentData.getTransDict();
	        
	        //traverse all the translated sentence for one Urdu sentence
	        //and find the highest score
 	        Iterator<Entry<String, String>> iter = dct.entrySet().iterator();
	        while (iter.hasNext()) {
	        	Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
	        	String key = entry.getKey();
	        	String val = entry.getValue();
	        	tmpScore = (TERcalc.TER(lst[0], val).score() +
	        			TERcalc.TER(lst[1], val).score() +
	        			TERcalc.TER(lst[2], val).score() +
	        			TERcalc.TER(lst[3], val).score()) / 4;
	        	if (!transScore.containsKey(key)) {
	        		transScore.put(key, tmpScore);
	        		transNum.put(key, 1);
	        	} else {
	        		transScore.put(key, transScore.get(key) + tmpScore);
	        		transNum.put(key, transNum.get(key) + 1);
	        	}
	        }
	        sntnsIdx++;
		}
		
		Iterator<Entry<String, Double>> iter = transScore.entrySet().iterator();
	    while (iter.hasNext()) {
	    	Map.Entry<String, Double> entry = (Map.Entry<String, Double>) iter.next();
	    	String key = entry.getKey();
	    	Double val = entry.getValue();
	    	idScoreMap.put(key, val / transNum.get(key));
	    }
	}
	
	/**
	 * used when sorting
	 */
	class ValueComparator implements Comparator<String> {
	    Map<String, Double> base;
	    public ValueComparator(Map<String, Double> base) {
	        this.base = base;
	    }

	    //this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) <= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
}
