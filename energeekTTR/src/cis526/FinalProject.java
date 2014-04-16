/**
 * 
 */
package cis526;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import BLEU.BLEU;

/**
 * @author yucongli
 *
 */
public class FinalProject {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FinalProject evaluation = new FinalProject();
		evaluation.run();
	}

	/**
	 * actual main method
	 */
	public void run(){
		long startT = System.currentTimeMillis(); //timer start
		
		//when only have raw data, please run the following method
		//to generate ID and score files in ./output
		//then move the file folder "translatorPerformance" to ./
//		writeTranslatorPerformance();
		
		//average method write
//		AvrRankPrune pruneAvr = new AvrRankPrune();
//		ArrayList<String> tmpAvr = pruneAvr.prune(51);
//		WriteFile.writeFile("averageTER_ranking.txt", tmpAvr);
		
		//random method write
//		RandomPrune pruneRand = new RandomPrune();
//		ArrayList<String> tmpRand = pruneRand.prune(51);
//		WriteFile.writeFile("random_ranking.txt", tmpRand);
		
		//page rank method write
//		try{
//			PrRankPrune.prm();
//		} catch (Exception e) {
//            e.printStackTrace();
//        }
		
		//read turker ranking list from file and prune turker list
//		ArrayList<String> tmp = ReadFile.rankedTurkerListInput("./output/pageRank_ranking.txt");		
//		ScoreSys.calcScoreForMinTurks(tmp, true, 2);
	
		// read turker ranking list from file and get turkers left/average bleu score plot data
//		ArrayList<String> tmp = ReadFile.rankedTurkerListInput("./output/manually_picked_from_pageRank.txt");	
//		ScoreSys.loopCalcBestScore(tmp, true);

		System.out.println("Time Elapse (s): " + (System.currentTimeMillis() - startT) / 1000.0); //timer end
	}
	
	/**
	 * write the data to files
	 */
	private void writeTranslatorPerformance() {
		HashMap<String, String> transP = extractTranslatorPerformance();
		
		Iterator<Entry<String, String>> iter = transP.entrySet().iterator();
        while (iter.hasNext()) {
        	Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
        	Object key = entry.getKey();
        	Object val = entry.getValue();
        	
        	WriteFile.writeFile("/translatorPerformance/" + key + ".txt", 
        			(String)key + (String)val);
        }
	}
	
	/**
	 * extract turkers performance from all the raw data
	 * @return
	 */
	private HashMap<String, String> extractTranslatorPerformance() {
		//translatorPerformance 
		HashMap<String, String> transP = new HashMap<String, String>();
		
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
	        	Object key = entry.getKey();
	        	Object val = entry.getValue();
	        	tmpScore = BLEU.computeSentenceBleu(lst, (String)val);
	        	if (!transP.containsKey(key)) {
	        		transP.put((String)key, "\n" + sntnsIdx + "\n" + tmpScore);
	        	} else {
	        		transP.put((String)key, transP.get((String)key) + "\n" + sntnsIdx + "\n" + tmpScore);
	        	}
	        }
	        sntnsIdx++;
		}
		return transP;
	}
}