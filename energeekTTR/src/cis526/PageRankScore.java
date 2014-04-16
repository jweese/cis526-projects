package cis526;

import java.io.*;
import java.util.*;

import Jama.*;
import MT.constructM;
import MT.pagerank;


/**
 * 
 * 
 * @author Chunxiao Mu, <chunxmu@seas.upenn.edu>
 * @version 4.8.2014
 */
public class PageRankScore extends constructM {
	TreeMap<String, Double> entireTks = new TreeMap<String, Double>();
	/**
	 * added
	 */
	ArrayList<Double> refResult = new ArrayList<Double>(); //save the pagerank result of four references
//	public PageRankScore(TreeMap<String, Double> hash){
//		entireTks = hash;
//	}
//	
//	public PageRankScore(){
//		
//	}

	public  Wrapper Rank(int dataid) throws NumberFormatException, IOException {
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
		 * read four translators
		 */
		BufferedReader trers = new BufferedReader(new FileReader(filename));
		for (int i = 0; i < 9; i++){
			trers.readLine();
		}
		ArrayList<String> ids = new ArrayList<String>();
		for (int i = 0; i < 4; i++)
		{
			ids.add(i, trers.readLine());
			for(int j = 0; j < 2; j++){
				trers.readLine();
			}
		}
		trers.close();
		
		/**
		 * edit the matrix
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
		 * rank
		 */
		pagerank pr = new pagerank();		 
		Matrix result = pr.getfirstpage(M, 0.85, 0.001);
		int maxindex = 0;
		int realDex = 0;
		double max = 0.0;
		HashMap<String, Double> results = new HashMap<String, Double>();
		
		
		/**
		 * get the result of reference sentences
		 */
		for (int n = 0; n < 4; n++){
			refResult.add(result.get(n, 0));
		}
		
		/**
		 * get all the results of each sentence with its translator
		 */
		
		for (int k = 4; k < lengthM; k++) {
			int key = k - 4;
			results.put(ids.get(key), result.get(k, 0));
			if (result.get(k, 0) > max) {
				max = result.get(k, 0);
				maxindex = k;
			}
		}		
		Wrapper wrap = new Wrapper(results, refResult);
		//return results;
		return wrap;
	}
}
