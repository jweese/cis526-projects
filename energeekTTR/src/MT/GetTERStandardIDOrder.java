
package MT;
import java.io.*;
import java.util.*;

import Jama.*;

/**
 * 
 * 
 * @author Mingkun Gao, <gmingkun@seas.upenn.edu>
 * @version $LastChangedDate$
 */

/*class IDnode{
	String ID;
	double Rank;
	int Count;
	
	IDnode(String id, double rank, int count){
		ID = id;
		Rank = rank;
		Count = count;
	}
	
	public int getCount(){
		return Count;
	}
	public void setCount(int count){
		Count = count;
	}
	public String getID(){
		return ID;
	}
	public void setID(String id){
		ID = id;
	}
	
	public double getRank(){
		return Rank;
	}
	public void setRank(double rank){
		Rank = rank;
	}
}*/
public class GetTERStandardIDOrder extends constructM{
	public static final boolean TER_NORMALIZED = true;
	public static final boolean TER_CASE_ON = true;
	public static final boolean TER_IGNORE_PUNCT = false;
	public static final int TER_BEAM_WIDTH = 20;
	public static final int TER_SHIFT_DIST = 50;
	private static final TERcost TER_COST = new TERcost();
	ArrayList<IDnode> nodelist = new ArrayList<IDnode>();
	
	public int IsExist(String nodeid){
		int length = nodelist.size();
		int i;
		for(i = 0; i < length;i++){
			IDnode tmpnode = nodelist.get(i);
			if(tmpnode.getID().equals(nodeid) == true)
				break;
		}
		if(i < length){
			return i;
		}
		else
			return -1;
		
	}
	
	public void Rank(int dataid ) throws NumberFormatException, IOException{
		
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
		 
		 int lengthM = tfidfwordbag.size();
		 if(lengthM < 18){
    	 System.out.println(lengthM +" "+dataid);
		 }
    	 String hyp = "";
    	 String ref = "";
    	 double [] result = new double[lengthM -4];
    	 for(int i = 4; i < lengthM;i++){
    		 hyp = sentences.get(i);
    		 ref = sentences.get(0);
    		 TERalignment ter1 = TERcalc.TER(hyp,ref,TER_COST);
			 double t1 = ter1.numEdits/ter1.numWords;
			 
			 ref = sentences.get(1);
    		 TERalignment ter2 = TERcalc.TER(hyp,ref,TER_COST);
			 double t2 = ter2.numEdits/ter2.numWords;
			 
			 ref = sentences.get(2);
    		 TERalignment ter3 = TERcalc.TER(hyp,ref,TER_COST);
			 double t3 = ter3.numEdits/ter3.numWords;
			 
			 ref = sentences.get(3);
    		 TERalignment ter4 = TERcalc.TER(hyp,ref,TER_COST);
			 double t4 = ter4.numEdits/ter4.numWords;
			 
			 result[i-4] = (double)(t1+t2+t3+t4)/4;
    		 
    	 }
    	 /*for(int l = 0; l < lengthM;l++){
    		 for(int j = 0; j < lengthM;j++){
    			 System.out.print(M.get(l, j)+" ");
    		 }
    		 System.out.println();
    	 }*/
    	 
    	 for(int k = 0; k < lengthM-4;k++){
    		 String id = IDs.get(k);
    		 int index = IsExist(id);
    		 if(index == -1){
    			 IDnode tmp = new IDnode(id,result[k],1);
    			 nodelist.add(tmp);
    		 }
    		 else {
    		 double currentrank = nodelist.get(index).getRank();
    		 double newrank = currentrank+result[k];
    		 int newcount = 1 + nodelist.get(index).getCount();
    		 nodelist.get(index).setRank(newrank);
    		 nodelist.get(index).setCount(newcount);
    		 	/*if(nodelist.get(index).getID().equals("a353ocl6lm6m4o")){
    		 		System.out.println(currentrank +" "+result.get(k, 0)+" "+dataid);
    		 	}*/
    		 }
    	 }			
	}
	public void getTotalRank() throws IOException{
		int i = 1;
		while( i < 1684  ){  
		      Rank(i);
		      i++;
		}
		int transcount = 0;
		for(int j = 0; j < nodelist.size();j++){
			transcount += nodelist.get(j).getCount();
		System.out.println(nodelist.get(j).getID() + " "+ nodelist.get(j).getRank()+" "+ nodelist.get(j).getCount() +" "+nodelist.get(j).getRank()/nodelist.get(j).getCount());
		}
		System.out.println(transcount);
	}
	public void Sort(){
		int max;
		for(int i = 0; i < nodelist.size()-1;i++){
			max = i;
			for(int j = i+1;j < nodelist.size();j++){
				if(nodelist.get(j).getRank()/nodelist.get(j).getCount() > nodelist.get(max).getRank()/nodelist.get(max).getCount()){
					max = j;
				}
				
			}
			IDnode tmp = nodelist.get(i);
			nodelist.set(i, nodelist.get(max));
			nodelist.set(max, tmp);
		}
	}
	public void PrintID() throws FileNotFoundException{
		String filename = "F:/ACL/NLP/IDRANK/TERStandardIDrank.txt";
		File file = new File(filename);
		PrintWriter output = new PrintWriter(file);
		for(int i = 0; i < nodelist.size();i++){
			output.println(nodelist.get(i).getID()+" "+nodelist.get(i).getRank()/nodelist.get(i).getCount());
		}
		output.close();
	}
}

	


