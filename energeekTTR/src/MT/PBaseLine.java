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
public class PBaseLine extends constructM{
	
	public void Rank(int dataid,ArrayList<PrintWriter> outputlist) throws NumberFormatException, IOException{
		
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
		 
		 
		 Random random = new Random();
		 

		 int maxindex = random.nextInt(4) + 4;
		 System.out.println(maxindex);
		
    	 
    	 double ref1 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(0));
    	 double ref2 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(1));
    	 double ref3 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(2));
    	 double ref4 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(3));
    	 
    	 String Trans = sentences.get(maxindex);
    	 String Ref1 = sentences.get(0);
    	 String Ref2 = sentences.get(1);
    	 String Ref3 = sentences.get(2);
    	 String Ref4 = sentences.get(3);
    	 
    	
    	
		 outputlist.get(0).println(Ref1);
		 
		 
		 outputlist.get(1).println(Ref2);
		
		 
		 outputlist.get(2).println(Ref3 );
		 
		 outputlist.get(3).println(Ref4);
		 
		 outputlist.get(4).println(Trans);
		 
		
	}
	
	public void TotalRank() throws IOException{
		int i = 1;
		
		ArrayList<PrintWriter> outputlist = new ArrayList<PrintWriter> (); 
		 String filename2 = "F:/ACL/NLP/Evaluate/PlainText/BaseLine/Ref1.txt";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		 String filename3 = "F:/ACL/NLP/Evaluate/PlainText/BaseLine/Ref2.txt";
		 File file3 = new File(filename3);
		 PrintWriter output3 = new PrintWriter(file3);
		 
		 String filename4 = "F:/ACL/NLP/Evaluate/PlainText/BaseLine/Ref3.txt";
		 File file4 = new File(filename4);
		 PrintWriter output4 = new PrintWriter(file4);
		 
		 String filename5 = "F:/ACL/NLP/Evaluate/PlainText/BaseLine/Ref4.txt";
		 File file5 = new File(filename5);
		 PrintWriter output5 = new PrintWriter(file5);
		 
		 String filename6 = "F:/ACL/NLP/Evaluate/PlainText/BaseLine/Trans.txt";
		 File file6 = new File(filename6);
		 PrintWriter output6 = new PrintWriter(file6);
		 
		 
		 
		 
		 outputlist.add(output2);
		 outputlist.add(output3);
		 outputlist.add(output4);
		 outputlist.add(output5);
		 outputlist.add(output6);
		
		while( i < 1684  ){  
				      Rank(i,outputlist);
		      i++;
		      
		
		      
		} 
		
		outputlist.get(0).close();
		
		outputlist.get(1).close();
		outputlist.get(2).close();
		outputlist.get(3).close();
		outputlist.get(4).close();
		
		
				
		
	}

}
