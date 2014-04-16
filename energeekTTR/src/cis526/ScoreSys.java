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
public class ScoreSys {

	private static final int totalTurkNum = 51; //51 turkers in total
	//key is the sentence id and value is the number of translations
	//at the beginning, all sentences has four turkers' translation, this is implemented in init()
	private static HashMap<Integer, Integer> sentenceTurkNum = new HashMap<Integer, Integer>();
	
	/**
	 * 
	 * @param rankedList: a ranked list of turkers ID, the length should be 51
	 * @return: the min number of turkders that guarantees all sentences are covered
	 */
	public static int pruneRankedTurkersList(ArrayList<String> rankedList) {
		FormatTurkerFileInput tmp = new FormatTurkerFileInput(); //read the input from file
		ArrayList<Integer> tmpSentsList= new ArrayList<Integer>();
		
		init(); //set up the initial table for sentences
		//loop from the last element
		for (int turkerRankIdx = totalTurkNum - 1; turkerRankIdx >= 0; --turkerRankIdx) {
			//get all the sentences' ID that a single person translated
			tmp = ReadFile.turkerFileInput(rankedList.get(turkerRankIdx));
			tmpSentsList = tmp.getTranslatedSentNum();

			//for each sentence ID, modify the corresponding table value
			Iterator<Integer> iter = tmpSentsList.iterator();
			while(iter.hasNext()) {
				int tmpSent = iter.next();
				sentenceTurkNum.put(tmpSent, sentenceTurkNum.get(tmpSent) - 1);
				if (sentenceTurkNum.get(tmpSent) == 0) {
					//when any value reaches zero, return the number of left turkers
					return (turkerRankIdx + 1); 
				}
			}
		}
		return 0;
	}
	
	public static ArrayList<Double> loopCalcBestScore(ArrayList<String> rankedList, boolean doPrint) {
		ArrayList<Double> outputData = new ArrayList<Double>();
		for (int i = 0; i < 51; ++i) {
			int counter = 0;
			ArrayList<String> temp = new ArrayList<String>();
			Iterator<String> iter = rankedList.iterator();
			while(iter.hasNext()) {
				temp.add(iter.next());
				++counter;
				if (counter > i) break;
			}
			double tmpScore = calcScoreForMinTurks(temp, doPrint, 1);
			outputData.add(tmpScore);
			if (doPrint) System.out.println("\n");
		}
		return outputData;
	}	
	
	/**
	 * 
	 * @return
	 */
	public static double calcScoreForMinTurks(ArrayList<String> rankedList, boolean doPrint, int mode) {
		
		//mode 1 for not prune, mode 2 for prune
		int listLen = 0;
		ArrayList<String> prunedList = new ArrayList<String>();
		if (mode == 1) {
			prunedList = rankedList; 
			listLen = prunedList.size();
		}
		if (mode == 2) {
			listLen = pruneRankedTurkersList(rankedList);
			prunedList = pruneList(listLen, rankedList);
		}
		if (mode != 1 && mode != 2) {
			System.out.println("No such mode for calcScoreForMinTurks.");
			return -1;
		}
		
		
		final int sntnsNum = 1683; //1683 files in total
		int sentsLeft = sntnsNum;
		int sntnsIdx = 1;
		double scoreSum = 0;
		double scoreThis, tmp;
		double[] tmp1 = new double[4];
		
		while(sntnsIdx <= sntnsNum) {
			//gather data
			FormatSentFileInput oneSentData = ReadFile.sentFileInput(sntnsIdx);
			String[] lst = oneSentData.getRefList();
			String[] lst1 = new String[3];
			HashMap<String, String> dct = oneSentData.getTransDict();
	 
			//initialize
	        scoreThis = 0;
	        
	        //traverse all the translated sentence for one Urdu sentence
	        //and find the highest score
	        Iterator<Entry<String, String>> iter = dct.entrySet().iterator();
	        while (iter.hasNext()) {
	        	Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
	        	String key = entry.getKey();
	        	String val = entry.getValue();
	        	//the bleu score will be calculated and added only when the turker is in the list
	        	if (prunedList.contains(key)) {
	        		lst1[0] = lst[0];
	        		lst1[1] = lst[1];
	        		lst1[2] = lst[2];
	        		tmp1[3] = BLEU.computeSentenceBleu(lst1, val); //compute bleu score
	        		
	        		lst1[2] = lst[3];
	        		tmp1[2] = BLEU.computeSentenceBleu(lst1, val); //compute bleu score
	        		
	        		lst1[1] = lst[2];
	        		tmp1[1] = BLEU.computeSentenceBleu(lst1, val); //compute bleu score
	        		
	        		lst1[0] = lst[1];
	        		tmp1[0] = BLEU.computeSentenceBleu(lst1, val); //compute bleu score
	        		
	        		tmp = (tmp1[0] + tmp1[1] + tmp1[2] + tmp1[3]) / 4;
		        	scoreThis = (tmp > scoreThis) ? tmp : scoreThis; //update scoreThis to the highest score
	        	}
	        }
	        if (scoreThis == 0) {
        		--sentsLeft;
        	}
	        scoreSum += scoreThis;
	        sntnsIdx++;
		}
		
		if (doPrint) {
			System.out.println("Sentences left: " + sentsLeft);
			System.out.println("Turker left: " + listLen);
			System.out.println("Best average BLEU score: " + scoreSum / sentsLeft);	
		}
		return scoreSum / sentsLeft;
	}
	
	/**
	 * prune list to a certain length
	 * @param len
	 * @param list
	 * @return the list
	 */
	public static ArrayList<String> pruneList(int len, ArrayList<String> list) {
		ArrayList<String> prunedList = new ArrayList<String>();
		
		int idx = 0;
		while(idx < len) {
			prunedList.add(list.get(idx));
			++idx;
		}
		return prunedList;
	}
	
	/**
	 * initialization
	 */
	private static void init() {
		for (int sentIdx = 0; sentIdx < 1683; ++sentIdx) {
			sentenceTurkNum.put(sentIdx + 1, 4);
		}
	}
}