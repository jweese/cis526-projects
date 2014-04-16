package cis526;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import Jama.Matrix;

//import MT.PBaiscPageRank;

public class PrRankPrune {
	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(
			Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						return e1.getValue().compareTo(e2.getValue());
					}
				});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public static void prm() throws NumberFormatException, IOException {
		HashMap<String, Double> entireTks = new HashMap<String, Double>(); // all
																			// the
																			// raw
																			// sum
																			// of
																			// pagerank
																			// score
		TreeMap<String, Double> entireTksC = new TreeMap<String, Double>(); // first
																			// count
																			// the
																			// #of
																			// sentances
																			// a
																			// turker
																			// trans,
																			// then
																			// avg
																			// score
		ArrayList<ArrayList<Double>> refResults = new ArrayList<ArrayList<Double>>();

		int id;
		/**
		 * for every sentance do
		 */
		for (id = 1; id < 1683; id++) {
			HashMap<String, Double> result = new HashMap<String, Double>();
			// result of a sentance after page rank is a hash table of 4
			// translator and their score
			PageRankScore ob = new PageRankScore();
			// Test test = new Test();
			Wrapper wrap = new Wrapper();//added wrapper class
			wrap = ob.Rank(id);          //added wrapper class
			result = wrap.para;          //added wrapper class
			refResults.add(wrap.parab);  //added wrapper class
			
			// test.recordNames(id);

			/**
			 * put the result into the hashMap that has the entire list of
			 * translators
			 */
			for (String iter : result.keySet()) {
				if (entireTks.containsKey(iter)) {
					// System.out.println("contain key" + iter);
					double origin = entireTks.get(iter);
					double added = origin + result.get(iter);
					double num = entireTksC.get(iter);
					num = num + 1;
					// System.out.println(iter+ ": added" + added);
					entireTks.put(iter, added);
					entireTksC.put(iter, num);
					// System.out.println(entireTks.get(iter));
				} else {
					entireTks.put(iter, result.get(iter));
					entireTksC.put(iter, (double) 1);
				}
				// double val = result.get(iter);
				// System.out.println(iter + ":" + val);
			}

		}
		/**
		 * print out the hashMap
		 */
		int n0 = 1;
		for (String iter : entireTks.keySet()) {
			double val = entireTks.get(iter) / entireTksC.get(iter);
			entireTksC.put(iter, val);
			System.out.println(n0 + "_" + iter + ":" + val);
			n0++;
		}
		BufferedWriter out0 = new BufferedWriter(new FileWriter("./output/pageRank_ranking.txt"));
		ArrayList<Entry<String, Double>> temp0 = new ArrayList<Entry<String, Double>>();
		for (Entry<String, Double> entry : entriesSortedByValues(entireTksC)) {
			temp0.add(0, entry);
			//out.write(entry.getKey() + ":" + entry.getValue() + "\n");
		}
		for (Entry<String, Double> entry : temp0) {
			out0.write(entry.getKey() + "\n");
		}
		out0.close();
		/**
		 * loop 5 times to see whether convergent
		 */
		
		int i = 0;
		boolean loop = false;
		do {
			double [] init = new double[51];
			n0 = 0;
			for (String iter : entireTksC.keySet()) {
				init[n0] = entireTksC.get(iter) ;
			}
			
			Matrix test = new Matrix(init, 51);
			
			
			entireTks.clear(); // clean the raw sum of page rank score
			TreeMap<String, Double> entireTksC2 = new TreeMap<String, Double>(); // count
																					// the
																					// number
																					// of
																					// sentences
																					// each
																					// translator
																					// trans
			for (id = 1; id < 1683; id++) {
				HashMap<String, Double> result = new HashMap<String, Double>();

				PrepIterRank ir = new PrepIterRank(entireTksC);
				/**
				 * passing ref into the Rank
				 */
				ArrayList<Double> refInputs = new ArrayList<Double>();
				refInputs = refResults.get(id);
				
				Wrapper wrap = new Wrapper();
				wrap = ir.Rank(id, refInputs);
				result = wrap.para;
				refResults.add(i, wrap.parab);
				/**
				 * same thing stated.
				 */
				for (String iter : result.keySet()) {
					if (entireTks.containsKey(iter)) {
						// System.out.println("contain key" + iter);
						double origin = entireTks.get(iter);
						double added = origin + result.get(iter);
						double num = entireTksC2.get(iter);
						num = num + 1;
						// System.out.println(iter+ ": added" + added);
						entireTks.put(iter, added);
						entireTksC2.put(iter, num);
						// System.out.println(entireTks.get(iter));
					} else {
						entireTks.put(iter, result.get(iter));
						entireTksC2.put(iter, (double) 1);
					}
				}
			}
			for (String iter : entireTks.keySet()) {
				double val = entireTks.get(iter) / entireTksC2.get(iter);
				entireTksC.put(iter, val);
			}
			int ai = 0;
			double[] compare = new double[51];
			for (String iter : entireTks.keySet()) {
				compare[ai] = entireTks.get(iter) / entireTksC2.get(iter);
				ai++;
			}
			Matrix compare2 = new Matrix(compare, 51);
			if((test.norm2() - compare2.norm2()) > 0.001){
				loop = true;
				double[] temp = new double[51];
				for (int k = 0; k < 51; k++){
				temp[k] = compare2.get(k, 0);
				test = new Matrix(temp, 51);
				}
			}
			i++; 
			System.out.println("loop" + i);
		}while(loop);
		// print the convergent result
		int n2 = 1;
		for (String iter : entireTks.keySet()) {
			double val = entireTksC.get(iter);
			System.out.println(n2 + "_" + iter + ":" + val);
			n2++;
		}
		ArrayList<Entry<String, Double>> temp = new ArrayList<Entry<String, Double>>();
		BufferedWriter out = new BufferedWriter(new FileWriter("./output/pageRank_ranking.txt"));
		for (Entry<String, Double> entry : entriesSortedByValues(entireTksC)) {
			temp.add(0, entry);
			//out.write(entry.getKey() + ":" + entry.getValue() + "\n");
		}
		for (Entry<String, Double> entry : temp) {
			out.write(entry.getKey() + "\n");
		}
		out.close();
	}
}
