
package MT;
import java.io.*;
import java.util.*;

import BLEU.computeDocBleu;
import Jama.*;

/**
 * 
 * 
 * @author Mingkun Gao, <gmingkun@seas.upenn.edu>
 * @version $LastChangedDate$
 */
public class PPartialRefPEditTERPageRank extends constructM{
	public static final boolean TER_NORMALIZED = true;
	public static final boolean TER_CASE_ON = true;
	public static final boolean TER_IGNORE_PUNCT = false;
	public static final int TER_BEAM_WIDTH = 20;
	public static final int TER_SHIFT_DIST = 50;
	private static final TERcost TER_COST = new TERcost();
	
	public void Rank(int dataid,boolean signal,ArrayList<PrintWriter> outputlist) throws NumberFormatException, IOException{
		
		TERcalc.setNormalize(TER_NORMALIZED);
		TERcalc.setCase(TER_CASE_ON);
		TERcalc.setPunct(TER_IGNORE_PUNCT);
		TERcalc.setBeamWidth(TER_BEAM_WIDTH);
		TERcalc.setShiftDist(TER_SHIFT_DIST);
		
		String filename = "F:/ACL/NLP/TFIDFDATA/text"+dataid+".txt";
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String ss = "";
		ArrayList<ArrayList<Double>> tfidfwordbag = new ArrayList<ArrayList<Double>>();
		ArrayList<String> sentences = new ArrayList<String>();
		int count = 0;
		while ((ss = in.readLine()) != null ){
			if(count >0 && count < 9){
				if(count % 2 == 0){
					ArrayList<Double> temparray = new ArrayList<Double>();
					String[] s = ss.split(" ");
					for (int i = 0; i < s.length;i++){
						temparray.add(Double.parseDouble(s[i]));
					}
					tfidfwordbag.add(temparray);
				}
				if(count % 2 == 1){
					sentences.add(ss);
				}
			}
			if(count >= 9){
				if(count % 3 == 1){
					sentences.add(ss);
				}
				else if(count % 3 == 2){
					ArrayList<Double> temparray = new ArrayList<Double>();
					String[] s = ss.split(" ");
					for (int i = 0; i < s.length;i++){
						temparray.add(Double.parseDouble(s[i]));
					}
					tfidfwordbag.add(temparray);
				}
			}
		    count++;
		}
		 in.close();
		 if(signal == true){
		 
		 int lengthM = 18;
    	 Matrix M = new Matrix(lengthM,lengthM,0);
    	 for(int l = 0; l < lengthM;l++){
    		 for(int j = 0; j < lengthM;j++){
    			 if(l==j)
    				 M.set(j, l, 0.0);
    			 else{
    				 String hyp = sentences.get(l);
    				 String ref = sentences.get(j);
    				 TERalignment ter = TERcalc.TER(hyp,ref,TER_COST);
    				 double t = ter.numEdits/ter.numWords;
    				 double temp = 2*(1 - 1/(1+Math.exp(-t)));
    				 
    				 M.set(j, l,temp);
    				}
    		 }
    			 
    	 } 
    	 for(int l = 0; l < lengthM;l++){
    		 Matrix tmp = M.getMatrix(0, lengthM-1, l, l);
    		 if(tmp.norm1() != 0)
        		 tmp = tmp.times(1/tmp.norm1());
    		 M.setMatrix(0, lengthM-1, l, l, tmp);
    	 }
    	 
    	 
    	 pagerank pr = new pagerank();
    	 Matrix result = pr.getfirstpage(M, 0.85, 0.001);
    	 
    	 /*for(int i = 0; i < result.getRowDimension();i++){
			 System.out.println(result.get(i, 0));
			 
		 }*/
    	 
    	 int maxindex = 0;
    	 double max = 0.0;
    	 for(int k = 4; k < lengthM;k++){
    		 if(result.get(k, 0) > max){
    			 max = result.get(k, 0); 
    			 maxindex = k;
    		 }
    	 }
    	 maxindex = maxindex;
		 
    	 
    	 String Trans = sentences.get(maxindex);
    	 String Ref1 = sentences.get(0);
    	 String Ref2 = sentences.get(1);
    	 String Ref3 = sentences.get(2);
    	 String Ref4 = sentences.get(3);
		 
    	
    	 
		 outputlist.get(0).println(Ref1);
		 
		 
		 outputlist.get(1).println(Ref2);
		 
		 
		 outputlist.get(2).println(Ref3);
		 
		 outputlist.get(3).println(Ref4);
		 
		 outputlist.get(4).println(Trans);
		 }
		 else{
			 
			 
			 int lengthM = 14;
	    	 Matrix M = new Matrix(lengthM,lengthM,0);
	    	 for(int l = 4; l < 4+lengthM;l++){
	    		 for(int j = 4; j <4+ lengthM;j++){
	    			 if(l==j)
	    				 M.set(j-4, l-4, 0.0);
	    			 else{
	    				 String hyp = sentences.get(l);
	    				 String ref = sentences.get(j);
	    				 TERalignment ter = TERcalc.TER(hyp,ref,TER_COST);
	    				 double t = ter.numEdits/ter.numWords;
	    				 double temp = 2*(1 - 1/(1+Math.exp(-t)));
	    				 
	    				 M.set(j-4, l-4,temp);
	    				}
	    		 }
	    			 
	    	 } 
	    	 for(int l = 0; l < lengthM;l++){
	    		 Matrix tmp = M.getMatrix(0, lengthM-1, l, l);
	    		 if(tmp.norm1() != 0)
	        		 tmp = tmp.times(1/tmp.norm1());
	    		 M.setMatrix(0, lengthM-1, l, l, tmp);
	    	 }
	    	 
	    	 
	    	 pagerank pr = new pagerank();
	    	 Matrix result = pr.getfirstpage(M, 0.85, 0.001);
	    	 
	    	 /*for(int i = 0; i < result.getRowDimension();i++){
				 System.out.println(result.get(i, 0));
				 
			 }*/
	    	 
	    	 int maxindex = 0;
	    	 double max = 0.0;
	    	 for(int k = 0; k < lengthM;k++){
	    		 if(result.get(k, 0) > max){
	    			 max = result.get(k, 0); 
	    			 maxindex = k;
	    		 }
	    	 }
	    	 maxindex = maxindex+4;
			 
	    	 
	    	 String Trans = sentences.get(maxindex);
	    	 String Ref1 = sentences.get(0);
	    	 String Ref2 = sentences.get(1);
	    	 String Ref3 = sentences.get(2);
	    	 String Ref4 = sentences.get(3);
			 
	    	
	    	 
			 outputlist.get(0).println(Ref1);
			 
			 
			 outputlist.get(1).println(Ref2);
			 
			 
			 outputlist.get(2).println(Ref3);
			 
			 outputlist.get(3).println(Ref4);
			 
			 outputlist.get(4).println(Trans);
		 }
	}
	
	public void TotalRank(boolean[] signals) throws IOException{
		int i = 1;
		
		ArrayList<PrintWriter> outputlist = new ArrayList<PrintWriter> (); 
		 String filename2 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref1.txt";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		 String filename3 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref2.txt";
		 File file3 = new File(filename3);
		 PrintWriter output3 = new PrintWriter(file3);
		 
		 String filename4 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref3.txt";
		 File file4 = new File(filename4);
		 PrintWriter output4 = new PrintWriter(file4);
		 
		 String filename5 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref4.txt";
		 File file5 = new File(filename5);
		 PrintWriter output5 = new PrintWriter(file5);
		 
		 String filename6 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Trans.txt";
		 File file6 = new File(filename6);
		 PrintWriter output6 = new PrintWriter(file6);
		 
		
		 
		 
		 outputlist.add(output2);
		 outputlist.add(output3);
		 outputlist.add(output4);
		 outputlist.add(output5);
		 outputlist.add(output6);
		 
		
		while( i < 1684  ){  
				      Rank(i,signals[i-1],outputlist);
		      i++;
		      
		
		      
		} 
		outputlist.get(0).close();
		
		outputlist.get(1).close();
		outputlist.get(2).close();
		outputlist.get(3).close();
		outputlist.get(4).close();
		
		
				
		
	}
	public void PartialRank() throws IOException{
		String writefile = "F:/ACL/NLP/Evaluate/PlainText/CURVE/partialpeditrank.txt";
		File file = new File(writefile);
		PrintWriter outwriter = new PrintWriter(file);
		MakePartition mp = new MakePartition(); 
		int[] parts = new int[1683];
		parts = mp.make_xval_partition(1683, 10);
		
		boolean [][] signals = new boolean[11][1683];
		for(int i = 0; i < 10;i++){
			for(int j = 0; j < 1683; j++)
				signals[i][j] = false;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1)
				signals[1][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2)
				signals[2][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3)
				signals[3][i] = true;
		}
		
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4)
				signals[4][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4||parts[i] == 5)
				signals[5][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4||parts[i] == 5||parts[i] == 6)
				signals[6][i] = true;
		}
		
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4||parts[i] == 5||parts[i] == 6||parts[i] == 7)
				signals[7][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4||parts[i] == 5||parts[i] == 6||parts[i] == 7||parts[i] == 8)
				signals[8][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4||parts[i] == 5||parts[i] == 6||parts[i] == 7||parts[i] == 8||parts[i] == 9)
				signals[9][i] = true;
		}
		for(int i = 0; i < 1683;i++){
			if(parts[i] == 1 || parts[i] == 2 || parts[i] == 3||parts[i] == 4||parts[i] == 5||parts[i] == 6||parts[i] == 7||parts[i] == 8||parts[i] == 9||parts[i] == 10)
				signals[10][i] = true;
		}
		int i = 0;
		computeDocBleu bleu = new computeDocBleu(); 
		while(i < 11){
			TotalRank(signals[i]);
			 String ref1 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref1.txt";
			 String ref2 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref2.txt";
			 String ref3 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref3.txt";
			 String ref4 = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Ref4.txt";
			 String trans = "F:/ACL/NLP/Evaluate/PlainText/10Folds/Trans.txt";
			 String res = "F:/ACL/NLP/Evaluate/PlainText/10Folds/result.txt";
			 System.out.println(10*i+" "+bleu.computeBleu(ref1, ref2, ref3, ref4, trans,res));
			 outwriter.println(10*i +" "+bleu.computeBleu(ref1, ref2, ref3, ref4, trans,res));
			 i++;
		}
		
		outwriter.close();
	}

}
