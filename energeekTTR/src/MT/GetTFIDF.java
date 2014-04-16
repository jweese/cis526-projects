
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
public class GetTFIDF extends pagerank {
	public int getWord(String s, int startindex){
		int i = startindex;
		while(i < s.length()) {
			
			if(s.charAt(i) == ' ' || s.charAt(i) == '\t'|| s.charAt(i) == '\n')
				break;
			i++;
			
		}
		return i;
	}
	public double cosine(ArrayList<Double> a, ArrayList<Double> b){
		int lenA = a.size();
		int lenB = b.size();
		if(lenA != lenB){
			System.out.println("Error");
			return -2;
		}
		int i = 0;
		double sum = 0.0;
		double sumA = 0.0;
		double sumB = 0.0;
		for(;i < lenA;i++){
			sum += a.get(i)*b.get(i);
			sumA += a.get(i)*a.get(i);
			sumB += b.get(i)*b.get(i);
		}
		return sum/(Math.sqrt(sumA)*Math.sqrt(sumB));
	}
	
	public double cosineint(ArrayList<Integer> a, ArrayList<Integer> b){
		int lenA = a.size();
		int lenB = b.size();
		if(lenA != lenB){
			System.out.println("Error");
			return -2;
		}
		int i = 0;
		double sum = 0.0;
		double sumA = 0.0;
		double sumB = 0.0;
		for(;i < lenA;i++){
			sum += a.get(i)*b.get(i);
			sumA += a.get(i)*a.get(i);
			sumB += b.get(i)*b.get(i);
		}
		return sum/(Math.sqrt(sumA)*Math.sqrt(sumB));
	}
	public void worddivider(String s,int dataid,ArrayList<String> wholewords,ArrayList<Integer> wholestatic,int totalcount) throws FileNotFoundException{
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> wordlist = new ArrayList<String>();
		ArrayList<String> idlist = new ArrayList<String>();
		ArrayList<ArrayList<Integer>> wordbag = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Double>> tfidfwordbag = new ArrayList<ArrayList<Double>>();
		int length = s.length();
		
		
		int counttab = 0;
	    int i  = 0;
	  
	    while(i < length && counttab < 3){
	    	if(s.charAt(i)=='\t') {
	    		counttab++;
	    	}
	    	i++;
	    }
	    s = s.substring(i);
	    
	    s = s.replaceAll("[^((a-zA-Z)|\t|0-9) ]", "").toLowerCase();
	    //System.out.println(s);
	    int rear;
	    ArrayList<String> temp = new ArrayList<String>();
	    i = 0;
	    length = s.length();
	    while(i < length){
	    	if(s.charAt(i)!=' ' &&s.charAt(i)!='\t' && (counttab < 7 || counttab%2!=1)){
	    	rear = getWord(s,i);
	    	
	    	String tmpword = s.substring(i, rear);
	    	int wlen = tmpword.length();
	    	/*if (tmpword.length() > 1 &&(tmpword.substring(tmpword.length()-2).equals(".\"")==true||tmpword.substring(tmpword.length()-2).equals(".\'")==true||
	    		tmpword.substring(tmpword.length()-2).equals("?\"")==true||tmpword.substring(tmpword.length()-2).equals("?\'")==true||
	    		tmpword.substring(tmpword.length()-2).equals("..") == true||tmpword.substring(tmpword.length()-2).equals(".)")==true||tmpword.substring(tmpword.length()-2).equals("),") == true))
	    		tmpword = tmpword.substring(0, tmpword.length()-2);
	    	if (tmpword.charAt(0)=='\"' || tmpword.charAt(0)=='\'' || tmpword.charAt(0)==',' || tmpword.charAt(0)==';'|| tmpword.charAt(0)=='(')
	    		tmpword = tmpword.substring(1,tmpword.length());
	    	if (tmpword.length() > 0&&(tmpword.charAt(tmpword.length()-1)=='\"' || tmpword.charAt(tmpword.length()-1)=='\'' ||
	    			tmpword.charAt(tmpword.length()-1)==','|| tmpword.charAt(tmpword.length()-1)==';' ||
	    			tmpword.charAt(tmpword.length()-1)=='?'||tmpword.charAt(tmpword.length()-1)=='.'||tmpword.charAt(tmpword.length()-1)==')'))
	    		tmpword = tmpword.substring(0, tmpword.length()-1);
	    	*/	
	    	i = rear;
	    	if(tmpword != null && tmpword.equals("n/a") == false)
	    	temp.add(tmpword);
	    	
	    	if (wordlist.indexOf(tmpword) == -1 && tmpword.equals("n/a") == false)
	    	wordlist.add(tmpword);
	    	continue;
	    	}
	    	else if(s.charAt(i)!=' ' &&s.charAt(i)!='\t' && (counttab >= 7 && counttab%2==1)){
	    		rear = getWord(s,i);
		    	String ids = s.substring(i, rear);
		    	idlist.add(ids);
		    	i = rear;
	    	}
	    	else if(s.charAt(i)==' ') {
	    		i++;
	    	}
	    	else if(s.charAt(i)=='\t' || s.charAt(i)=='\n') {
	    		if (temp.size() > 0)
	    		{
	    			sentences.add(temp);	    		
	    			temp = new ArrayList<String>();
	    		}
	    		i++;
	    		counttab ++;
	    	}
	    }
	    if (temp.size() > 0)
		{
		sentences.add(temp);
		temp = new ArrayList<String>();
		}
	   /* System.out.println(sentences.size());
	    for(int j = 0;j < sentences.size();j++){
	    	ArrayList<String> as = sentences.get(j);
	    	System.out.println();
	    	for(int k = 0; k < as.size();k++){
	    		System.out.print(as.get(k)+" ");
	    	}
	    }
	    System.out.println();*/
	    
	    	//String as = wordlist.get(j);
	    int senlength = sentences.size();
	    int wordlistlength = wordlist.size();
	    	for(int l = 0;l < senlength;l++){
		    	ArrayList<String> sen = sentences.get(l);
		    	if(sen.get(0).equals("n/a")==false ) {
		    	ArrayList<Integer> t = new ArrayList<Integer>();
		    	for(int j = 0;j < wordlistlength;j++){
					String token = wordlist.get(j);
					int plength = sen.size();
					int k = 0;
					int wcount = 0;
					//if (sen.get(0) != "n/a"){
						for(; k < plength;k++){
		    		      if (token.equals(sen.get(k))== true)
		    		    	  wcount++;
		    			}
		    		t.add(wcount);
				//	}
		    		 
		    	}
		    	wordbag.add(t);
		    	}
	    	}
	    
	    /*************************************TFIDF******************************/
	    int num_document = wordbag.size();
	    int wordslength = wordbag.get(0).size();
	    int [] num_wordlist = new int[wordslength];
	    double [] idf_wordlist = new double[wordslength];
	    int [] termsum = new int[num_document];
	    
	    for (int l = 0; l < wordslength;l++ )
	    	num_wordlist[l] = 0;
	    for (int l = 0; l < num_document;l++ )
	    	termsum[l] = 0;
	    for(int l = 0; l < wordbag.size();l++){
	    	ArrayList<Integer> tmp = wordbag.get(l);
	    	for (int k = 0; k < wordslength;k++){
	    		termsum[l] += tmp.get(k);
	    	
	    	}
	    }
	   // System.out.println("Size:"+totalcount);
	    for(int l = 0; l < wordslength;l++){
	    	String wordtmp = wordlist.get(l);
	    	num_wordlist[l] = wholestatic.get(wholewords.indexOf(wordtmp));
	    }
	    for (int l = 0; l < wordslength;l++ )
	    	idf_wordlist[l] = Math.log((double)totalcount/num_wordlist[l]);
	    
	    for(int l = 0; l<num_document;l++){
	    	ArrayList<Double> arrtmp = new ArrayList<Double>();
	    	ArrayList<Integer> tmp = wordbag.get(l);
	    	for(int k = 0; k < wordslength;k++){
	    		double dtmp = idf_wordlist[k]*tmp.get(k)/termsum[l];
	    		arrtmp.add(dtmp);
	    		}
	        tfidfwordbag.add(arrtmp);	
	    }
	    	
	  
	    	String filename = "F:/ACL/NLP/TFIDFDATA/text"+dataid+".txt";
		    File file = new File(filename);
		  /*  if(file1.exists()){
		    	System.out.println("File already exits");
		    	System.exit(0);
		    }
		    */
		   PrintWriter output = new PrintWriter(file);
		    for(int j = 0;j < wordlist.size();j++){
		    	String tokens = wordlist.get(j);
		    	if (j < wordlist.size() -1)
		    		output.print(tokens+" ");
		    	else if (j == wordlist.size() -1)
		    		output.print(tokens);
		    }
		    output.print('\n');
		    for(int j = 0;j < 4;j++){
	 	    	ArrayList<Double> as = tfidfwordbag.get(j);
	 	    	ArrayList<String> sas = sentences.get(j);
	 	    	for(int k = 0; k < sas.size();k++){
	 	    		if (k < sas.size() -1)
	 	    			output.print(sas.get(k)+" ");
	 	    		else if (k == sas.size() -1)
	 	    			output.print(sas.get(k));
	 	    	}
	 	    	output.print('\n');
	 	    	for(int k = 0; k < as.size();k++){
	 	    		if (k < as.size() -1)
	 	    			output.print(as.get(k)+" ");
	 	    		else if (k == as.size() -1)
	 	    			output.print(as.get(k));
	 	    	}
	 	    	output.print('\n');
	 	    }
	    	 for(int j = 4;j < tfidfwordbag.size();j++){
	    		 output.println(idlist.get(j-4));
	 	    	ArrayList<Double> as = tfidfwordbag.get(j);
	 	    	ArrayList<String> sas = sentences.get(j);
	 	    	for(int k = 0; k < sas.size();k++){
	 	    		if (k < sas.size() -1)
	 	    			output.print(sas.get(k)+" ");
	 	    		else if (k == sas.size() -1)
	 	    			output.print(sas.get(k));
	 	    	}
	 	    	output.print('\n');
	 	    	
	 	    	for(int k = 0; k < as.size();k++){
	 	    		if (k < as.size() -1)
	 	    			output.print(as.get(k)+" ");
	 	    		else if (k == as.size() -1)
	 	    			output.print(as.get(k));
	 	    	}
	 	    	output.print('\n');
	 	    }
	    	 output.close();
	    	 
	    	
	    	
	    	 
	    	
	    }
		
	
	
	public void getTFIDF(String filename,ArrayList<String> wholewords,ArrayList<Integer> wholestatic,int totalcount) throws IOException{
		int i = 1;
		/*while(i < 1000000){
			System.out.println("hello");
			i++;
		}
		while(input.hasNext()){
			//if (i %100 == 0) System.out.println();
			System.out.println(input.next());
			i++;
		}*/
	
		 

		BufferedReader br = new BufferedReader(new FileReader(filename));  
		String data = br.readLine();//一次读入一行，直到读入null为文件结束  
		//data = br.readLine();
		
		/*while( data!=null  && i < 18){
			data = br.readLine(); 
				i++;
		}*/

		while( data!=null  ){  
			//System.out.println(data);
		     worddivider(data,i,wholewords,wholestatic,totalcount);
		      i++;
		      
		      data = br.readLine(); //接着读下一行
		      
		} 
		//output2.println("AVG BLEU:"+totalsum/(i-1));
		
		
		
		
		
		
		
		
		
	}
	public int[] getSubStringIndex(String s){
		int counttab = 0;
		
		
		int [] res = new int[2];
		for (int i = 0; i < s.length();i ++){
			if (s.charAt(i) == '\t'){
				counttab++;
			}
			if (counttab == 2){
				res[0] = i;
				break;
			}
			
			
		}
		counttab = 0;
		for (int i = 0; i < s.length();i ++){
			if (s.charAt(i) == '\t'){
				counttab++;
			}
			if (counttab == 3){
				res[1] = i;
				break;
			}
			
			
		}
		
		return res;
		
	}
	public void dataclean(String filename) throws IOException{
		
		int i = 1;
		
		String filename2 = "F:/ACL/NLP/newdata.txt";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		 
		 FileInputStream fis = new FileInputStream(new File(filename));
		 BufferedReader br = new BufferedReader(new InputStreamReader(fis , "UTF-8"));
		String data = br.readLine();//一次读入一行，直到读入null为文件结束  
		data = br.readLine();
		
		System.out.println(data);
		while( data!=null  ){
			
		  if (data.indexOf("N/A") == -1 && data.indexOf('&') == -1)
				output2.println(data);
		      i++;
		      
		      data = br.readLine(); //接着读下一行
		      
		}  
		output2.close();
		
		
	}
public int counttab(String line){
		int i = 0;
		int count = 0;
		while(i < line.length()){
			if (line.charAt(i) =='\t')
				count ++;
			i++;
		}
		return count;
	}
public void setSRC(String filename) throws IOException{
		
		int i = 1;
		
	/* String filename2 = "F:/NLP/newdata.txt";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		BufferedReader br = new BufferedReader(new FileReader(filename));  
		String data = br.readLine();//一次读入一行，直到读入null为文件结束  
		data = br.readLine();
		
		
		while( data!=null  ){
			
		  if (data.indexOf("N/A") == -1)
				output2.println(data);
		      i++;
		      
		      data = br.readLine(); //接着读下一行
		      
		}  
		output2.close();*/
		
		String filename2 = "F:/ACL/NLP/Evaluate/src.xml";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		 
		 output2.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		 output2.println("<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">"); 
		 output2.println("<mteval>");
		 output2.println("<srcset setid=\"source_set\" srclang=\"Urdu\"> ");

		 
		 FileInputStream fis = new FileInputStream(new File(filename));
		 BufferedReader br = new BufferedReader(new InputStreamReader(fis , "UTF-8"));
		String data = br.readLine();//一次读入一行，直到读入null为文件结束  
		//data = br.readLine();
		
		System.out.println(data);
		while( data!=null){
			
		 int [] indices = getSubStringIndex(data);
		
		 String s = data.substring(indices[0], indices[1]);
//		 System.out.println(s);
//		 System.out.println(indices[0]+" " +indices[1]);
		 String docid = "document"+i;
		 output2.println("<doc docid=\""+ docid + "\" genre=\"nw\">"); 
		 output2.println("<seg id=\"1\">"+s +"</seg>");
		 output2.println("</doc> ");
		 i++;     
		 data = br.readLine(); //接着读下一行
		      
		}  
		output2.println("</srcset>");
		output2.println("</mteval> ");

		output2.close();
		
		
	}
	
	

}

