
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
public class GetTERIDOrder extends constructM{
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
		
		String filename = "F:/NLP/TFIDFDATA/text"+dataid+".txt";
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
    				 M.set(j, l, temp);
    				}
    		 }
    			 
    	 }
    	 
    	 /*for(int l = 0; l < lengthM;l++){
    		 for(int j = 0; j < lengthM;j++){
    			 System.out.print(M.get(l, j)+" ");
    		 }
    		 System.out.println();
    	 }*/
    	 for(int l = 0; l < lengthM;l++){
    		 Matrix tmp = M.getMatrix(0, lengthM-1, l, l);
    		// System.out.println(tmp.norm1()+" "+l);
    		 if(tmp.norm1() != 0)
    		 tmp = tmp.times(1/tmp.norm1());
    		/* for(int i = 0; i < tmp.getRowDimension();i++){
    			 System.out.println(tmp.get(i, 0));
    			 
    		 }*/
    		 M.setMatrix(0, lengthM-1, l, l, tmp);
    	 }
    	 
    	 
    	 pagerank pr = new pagerank();
    	 Matrix result = pr.getfirstpage(M, 0.85, 0.001);
    	 //System.out.println("Rank");
    	/* for(int i = 0; i < result.getRowDimension();i++){
			 System.out.println(result.get(i, 0));
			 
		 }*/
    	 for(int k = 4; k < lengthM;k++){
    		 String id = IDs.get(k-4);
    		 int index = IsExist(id);
    		 if(index == -1){
    			 IDnode tmp = new IDnode(id,result.get(k, 0),1);
    			 nodelist.add(tmp);
    		 }
    		 else {
    		 double currentrank = nodelist.get(index).getRank();
    		 double newrank = currentrank+result.get(k, 0);
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
		for(int j = 0; j < nodelist.size();j++)
		System.out.println(nodelist.get(j).getID() + " "+ nodelist.get(j).getRank()+" "+ nodelist.get(j).getCount() +" "+nodelist.get(j).getRank()/nodelist.get(j).getCount());
    
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
		String filename = "F:/ACL/NLP/IDRANK/TERIDrank.txt";
		File file = new File(filename);
		PrintWriter output = new PrintWriter(file);
		for(int i = 0; i < nodelist.size();i++){
			output.println(nodelist.get(i).getID()+" "+nodelist.get(i).getRank()/nodelist.get(i).getCount());
		}
		output.close();
	}
}

	


