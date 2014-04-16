package cis526;

import java.io.*;
import java.util.*;

import Jama.*;
import MT.constructM;


/**
 * 
 * 
 * @author Chunxiao Mu, <chunxmu@seas.upenn.edu>
 * @version 4.8.2014
 */
public class PrepIterRank extends constructM {
	TreeMap<String, Double> entireTkC = new TreeMap<String, Double>();
	public PrepIterRank(TreeMap<String, Double> tree){
		entireTkC = tree;
	}

	public  Wrapper Rank(int dataid, ArrayList<Double> refInputs) throws NumberFormatException, IOException {
		ArrayList<Double> refInput = new ArrayList<Double>(refInputs); // get the ref scores of last round
		/**
		 * read one sentance with refference and translation
		 */
		String filename = "./data/text" + dataid + ".txt";
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String ss = "";
		ArrayList<ArrayList<Double>> tfidfwordbag = new ArrayList<ArrayList<Double>>();
		ArrayList<String> sentences = new ArrayList<String>();
		int count = 0;
		while ((ss = in.readLine()) != null) {
			if (count > 0 && count < 9) {
				if (count % 2 == 0) {
					ArrayList<Double> temparray = new ArrayList<Double>();
					String[] s = ss.split(" ");
					for (int i = 0; i < s.length; i++) {
						temparray.add(Double.parseDouble(s[i]));
					}
					tfidfwordbag.add(temparray);
				}
				if (count % 2 == 1) {
					sentences.add(ss);
				}
			}
			if (count >= 9) {
				if (count % 3 == 1) {
					sentences.add(ss);
				} else if (count % 3 == 2) {
					ArrayList<Double> temparray = new ArrayList<Double>();
					String[] s = ss.split(" ");
					for (int i = 0; i < s.length; i++) {
						temparray.add(Double.parseDouble(s[i]));
					}
					tfidfwordbag.add(temparray);
				}
			}
			count++;
		}
		in.close();
		
		/**
		 * read translators and get their pageRank score of last round
		 */
		BufferedReader trers = new BufferedReader(new FileReader(filename));
		for (int i = 0; i < 9; i++){
			trers.readLine();
		}
		ArrayList<String> ids = new ArrayList<String>();
		HashMap <String, Double> para = new HashMap<String, Double>();
		ArrayList<Double> preRank = new ArrayList<Double>();

		for (int i = 0; i < 4; i++)
		{
			String trantor = trers.readLine();
			ids.add(i, trantor);
			//get the parameter with the hash key;
			preRank.add(i, entireTkC.get(trantor));
			//System.out.println(preRank.get(i));
			para.put(trantor, entireTkC.get(trantor));
			for(int j = 0; j < 2; j++){
				trers.readLine();
			}
		}
		trers.close();
		
		/**
		 * prepare the matrix
		 */
		int lengthM = 8;
		Matrix M = new Matrix(lengthM, lengthM, 0);
		for (int l = 0; l < lengthM; l++) {
			for (int j = 0; j < lengthM; j++) {
				if (l == j)
					M.set(j, l, 0.0);
				else {
					M.set(j, l,
							cosine(tfidfwordbag.get(l), tfidfwordbag.get(j)));
				}
			}
		}
		
		for (int l = 0; l < lengthM; l++) {
			Matrix tmp = M.getMatrix(0, lengthM - 1, l, l);
			if (tmp.norm1() != 0)
				tmp = tmp.times(1 / tmp.norm1());
			M.setMatrix(0, lengthM - 1, l, l, tmp);
		}

		/**
		 * reRank with last round value
		 */
		IterateRank iPr = new IterateRank();		
		Matrix result = iPr.reRank(M, 0.85, 0.001, ids, preRank, refInput);
		int maxindex = 0;
		int realDex = 0;
		double max = 0.0;
		HashMap<String, Double> results = new HashMap<String, Double>();
		
		ArrayList<Double> refScore = new ArrayList<Double>();
		for(int k = 0; k < 4; k++){
			refScore.add(result.get(k,0));
		}
		/**
		 * get the results and return
		 */
		
		for (int k = 4; k < lengthM; k++) {
			int key = k - 4;
			results.put(ids.get(key), result.get(k, 0));
			if (result.get(k, 0) > max) {
				max = result.get(k, 0);
				maxindex = k;
			}
		}
		
		Wrapper wrap = new Wrapper(results, refScore);
		return wrap;
		
		//return results;
	}
}


