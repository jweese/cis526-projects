package MT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import BLEU.computeDocBleu;
import Jama.Matrix;

/**
 * 
 * 
 * @author Mingkun Gao, <gmingkun@seas.upenn.edu>
 * @version $LastChangedDate$
 */
public class GreedyDelete {
	public static final boolean TER_NORMALIZED = true;
	public static final boolean TER_CASE_ON = true;
	public static final boolean TER_IGNORE_PUNCT = false;
	public static final int TER_BEAM_WIDTH = 20;
	public static final int TER_SHIFT_DIST = 50;
	private static final TERcost TER_COST = new TERcost();
	public boolean JudgeComplete(int dataid,ArrayList<String> deletestring) throws NumberFormatException, IOException{
		
		String filename = "F:/ACL/NLP/TFIDFDATA/text"+dataid+".txt";
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String ss = "";
		ArrayList<ArrayList<Double>> tfidfwordbag = new ArrayList<ArrayList<Double>>();
		ArrayList<String> sentences = new ArrayList<String>();
		ArrayList<String> IDs = new ArrayList<String>();
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
				if(count % 3 == 0){
					IDs.add(ss);
				}
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
	    
		 int deletelength = deletestring.size();
		 int i,j, deletecount = 0;
	    for(i = 0; i < deletelength; i++){
	    	String t = deletestring.get(i);
	    	for (j = 0; j < 4; j++){
	    		if(t.equals((String)IDs.get(j)) == true){
	    			deletecount++;
	    		}
	    	}
	    }
	    
	    if (deletecount >= 4) return false;
	    else return true;
		
	}
	public boolean Scan(ArrayList<String> deletestring) throws IOException{
		int i = 1;
		while( i < 1684  ){  
			boolean b = JudgeComplete(i,deletestring);
			if (b == false) return false;
		      i++;
		}
		return true;
	}
public void Rank(int dataid,ArrayList<PrintWriter> outputlist,ArrayList<String> deletestring) throws NumberFormatException, IOException{
		
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
		ArrayList<String> IDs = new ArrayList<String>();
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
				if(count % 3 == 0){
					IDs.add(ss);
				}
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
		 
		 
		int deletelength = deletestring.size();
		int i,j, deletecount = 0;
		int [] ID = new int[14];
		for(i=0;i<14;i++)
			ID[i] = 0;
		ArrayList<String> newsentences = new ArrayList<String>();
		
	    for(i = 0; i < deletelength; i++){
	    	String t = deletestring.get(i);
	    	for (j = 0; j < 4; j++){
	    		if(t.equals((String)IDs.get(j)) == true){
	    			ID[j] = 1;
	    			deletecount++;
	    			//System.out.println("Delete" + " "+ dataid);
	    		}
	    	}
	    }
	    newsentences.add(sentences.get(0));
	    newsentences.add(sentences.get(1));
	    newsentences.add(sentences.get(2));
	    newsentences.add(sentences.get(3));
	    for(i = 0; i< 14;i++){
	    	if(ID[i] != 1){
	    		newsentences.add(sentences.get(i+4));
	    	}
	    }
		 
		 
		 int lengthM = 8 - deletecount;
    	 Matrix M = new Matrix(lengthM,lengthM,0);
    	 for(int l = 0; l < lengthM;l++){
    		 for(j = 0; j < lengthM;j++){
    			 if(l==j)
    				 M.set(j, l, 0.0);
    			 else{
    				 String hyp = newsentences.get(l);
    				 String ref = newsentences.get(j);
    				 TERalignment ter = TERcalc.TER(hyp,ref,TER_COST);
    				 double t = ter.numEdits/ter.numWords;
    				 double temp = 2*(1 - 1/(1+Math.exp(-t)));
    				 
    				 M.set(j, l,temp);
    				}
    		 }
    			 
    	 }
    	 
    	 
    	/* for(int l = 0; l < lengthM;l++){
		 for(int j = 0; j < lengthM;j++){
			 System.out.print(M.get(l, j)+" ");
		 }
		 System.out.println();
    	 }*/
    	 
    	 
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
    	
    	 
    	 String Trans = newsentences.get(maxindex);
    	 String Ref1 = newsentences.get(0);
    	 String Ref2 = newsentences.get(1);
    	 String Ref3 = newsentences.get(2);
    	 String Ref4 = newsentences.get(3);
    	 
    	
    	 
		 outputlist.get(0).println(Ref1);
		 
		 
		 outputlist.get(1).println(Ref2);
		 
		 
		 outputlist.get(2).println(Ref3);
		 
		 outputlist.get(3).println(Ref4);
		 
		 outputlist.get(4).println(Trans);
		 
		
	}
	
	public void TotalRank(ArrayList<String> deletestring) throws IOException{
		int i = 1;
		
		ArrayList<PrintWriter> outputlist = new ArrayList<PrintWriter> (); 
		 String filename2 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref1.txt";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		 String filename3 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref2.txt";
		 File file3 = new File(filename3);
		 PrintWriter output3 = new PrintWriter(file3);
		 
		 String filename4 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref3.txt";
		 File file4 = new File(filename4);
		 PrintWriter output4 = new PrintWriter(file4);
		 
		 String filename5 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref4.txt";
		 File file5 = new File(filename5);
		 PrintWriter output5 = new PrintWriter(file5);
		 
		 String filename6 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Trans.txt";
		 File file6 = new File(filename6);
		 PrintWriter output6 = new PrintWriter(file6);
		 
		
		 
		 
		 outputlist.add(output2);
		 outputlist.add(output3);
		 outputlist.add(output4);
		 outputlist.add(output5);
		 outputlist.add(output6);
		
		while( i < 1684  ){  
				      Rank(i,outputlist,deletestring);
		      i++;
		      
		
		      
		} 
		outputlist.get(0).close();
		
		outputlist.get(1).close();
		outputlist.get(2).close();
		outputlist.get(3).close();
		outputlist.get(4).close();
}

	
	public void Curve() throws IOException{
		String filename = "F:/ACL/NLP/IDRANK/TERStandardIDrank.txt";
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String ss = "";
		boolean bscan; 
		ArrayList<String> deletestring = new ArrayList<String>();
		deletestring.add("NULL");
		
		String writefile = "F:/ACL/NLP/Evaluate/PlainText/CURVE/ontotalrank.txt";
		 File file = new File(writefile);
		 PrintWriter outwriter = new PrintWriter(file);
		
		 TotalRank(deletestring);
			computeDocBleu bleu = new computeDocBleu(); 
			String ref1 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref1.txt";
			String ref2 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref2.txt";
			String ref3 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref3.txt";
			String ref4 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref4.txt";
			String trans = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Trans.txt";
			String res = "F:/ACL/NLP/Evaluate/PlainText/TEMP/result.txt";
			System.out.println((deletestring.size()-1)+" "+bleu.computeBleu(ref1, ref2, ref3, ref4, trans,res));
			outwriter.println((deletestring.size()-1)+" "+bleu.computeBleu(ref1, ref2, ref3, ref4, trans,res));
		
		while ((ss = in.readLine()) != null ){
			String[] s = ss.split(" ");
			//System.out.println(s[0]);
			deletestring.add(s[0]);
			bscan = Scan(deletestring);
			if(bscan == false) break;
			else{
				TotalRank(deletestring);
				//computeDocBleu bleu = new computeDocBleu(); 
				 ref1 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref1.txt";
				 ref2 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref2.txt";
				 ref3 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref3.txt";
				 ref4 = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Ref4.txt";
				 trans = "F:/ACL/NLP/Evaluate/PlainText/TEMP/Trans.txt";
				 res = "F:/ACL/NLP/Evaluate/PlainText/TEMP/result.txt";
				System.out.println((deletestring.size()-1)+" "+bleu.computeBleu(ref1, ref2, ref3, ref4, trans,res));
				outwriter.println((deletestring.size()-1)+" "+bleu.computeBleu(ref1, ref2, ref3, ref4, trans,res));
			}
		}
		outwriter.close();
		
		
		
		
	}
	
}
