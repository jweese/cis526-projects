/**
 * 
 */
package edu.jhu.jacana.align;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

import approxlib.distance.EditDist;
import approxlib.distance.EditDistWordnet;
import approxlib.tree.LblTree;

import edu.jhu.jacana.dependency.DependencyTree;
import edu.jhu.jacana.reader.TextualEntailment;
import edu.jhu.jacana.util.FileManager;

/**
 * This class reads in a pair of parsed sentences (e.g., premise and hypothesis)
 * and outputs the aligned edit in RTE06MSR format, which is later evaluated against
 * the gold standard by the NatLog ranker.
 * 
 * @author Xuchen Yao
 *
 */
public class SimpleTEDAligner {
	
	public static String getMSRalignFormat (EditDist dist) {
		StringBuilder sb = new StringBuilder();
		HashMap<Integer, Integer> alignInTreeOrder = dist.getAlign2to1();
		HashMap<Integer, Integer> alignInWordOrder = new HashMap<Integer, Integer>(); 
		String sent1 = dist.getSentence1().replaceAll("\t", " ");
		int lenSent1 = sent1.split(" ").length;
		for (Integer j:alignInTreeOrder.keySet()) {
			Integer i = alignInTreeOrder.get(j);
			if (dist.id2idxInWordOrder2(j) == -1 || dist.id2idxInWordOrder1(i) == -1 ||
					dist.id2idxInWordOrder1(i) >= lenSent1) continue;
			alignInWordOrder.put(dist.id2idxInWordOrder2(j), dist.id2idxInWordOrder1(i));
		}
		sb.append(sent1);
		sb.append("\n");
		sb.append("NULL ({ / / }) ");
		String sent2 = dist.getSentence2();
		String[] splits2 = sent2.split("\\s+");
		for (int i=0; i<splits2.length; i++) {
			sb.append(splits2[i] + " ");
			if (alignInWordOrder.containsKey(i)) {
				sb.append(String.format("({ %d / / }) ", alignInWordOrder.get(i)+1));
			} else {
				sb.append("({ / / }) ");
			}
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String trainFile, editFile = null;

		if (args.length >= 1) {
			trainFile = args[0];
			editFile = args[1];
		} else {
			trainFile = "alignment-data/msr/converted/parse/RTE2_test_M.dat";
			editFile = "alignment-data/msr/baselines/ted/RTE2_test_M.align.ted.txt";
		}

		int counter = 1;

		try {
			BufferedReader in = FileManager.getReader(trainFile);
			BufferedWriter out = FileManager.getWriter(editFile);
			String line;

			while ((line = in.readLine()) != null) {

				StringBuilder sb = new StringBuilder();
				if (counter % 200 == 0)
					System.out.println(counter);
				String[] splits = line.split("\\t");
				DependencyTree[] trees = TextualEntailment.parseLine(splits);
				String aString = trees[0].toCompactString();
				LblTree aTree = LblTree.fromString(aString);
				aTree.setSentence(trees[0].getOrigSentence());
				//aTree.prettyPrint();

				String bString = trees[1].toCompactString();
				LblTree bTree = LblTree.fromString(bString);
				bTree.setSentence(trees[1].getOrigSentence());
				//bTree.prettyPrint();

				//EditDist a2b = new EditDist(true);
				// set q2a = false since the first tree (hypothesis) is longer
				EditDist a2b = new EditDistWordnet(true, false);
				a2b.treeDist(aTree, bTree);

				a2b.printHumaneEditScript();
				
				sb.append("# sentence pair " + counter);
				sb.append("\n");
				sb.append(getMSRalignFormat(a2b));
				sb.append("\n");
				out.write(sb.toString());
				
				counter += 1;
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
