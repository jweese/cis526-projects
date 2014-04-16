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
public class UseRef extends pagerank{
	public int getWord(String s, int startindex){
		int i = startindex;
		while(i < s.length()) {
			
			if(s.charAt(i) == ' ' || s.charAt(i) == '\t'|| s.charAt(i) == '\n')
				break;
			i++;
			//System.out.println("I am here4");
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
	public double worddivider(String s,int dataid,ArrayList<PrintWriter> outputlist,ArrayList<String> wholewords,ArrayList<Integer> wholestatic,int totalcount) throws FileNotFoundException{
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> wordlist = new ArrayList<String>();
		ArrayList<ArrayList<Integer>> wordbag = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Double>> tfidfwordbag = new ArrayList<ArrayList<Double>>();
		int length = s.length();
		String s_real = s; //////////////////////////
		s = s.toLowerCase();
		//System.out.println(s);
		//System.out.println(length);
		int counttab = 0;
	    int i  = 0;
	    /**************************** new adding ****************************/
	    int realcounttab = 0;
	    int i_real  = 0;
	    ArrayList<ArrayList<String>> real_sentences = new ArrayList<ArrayList<String>>();
	    
	    
	    while(i_real < length && realcounttab < 3){
	    	if(s_real.charAt(i_real)=='\t') {
	    		realcounttab++;
	    		//System.out.print("I am here3");
	    	}
	    	i_real++;
	    }
	    int realrear;
	    ArrayList<String> realtemp = new ArrayList<String>();
	    while(i_real < length){
	    	//System.out.println("I am here5");
	    	
	    	
	    	if(s_real.charAt(i_real)!=' ' &&s_real.charAt(i_real)!='\t' && (realcounttab < 7 || realcounttab%2!=1)){
	    	realrear = getWord(s_real,i_real);
	    	
	    	String tmpword = s_real.substring(i_real, realrear);
	    	int wlen = tmpword.length();
	    	//System.out.println(tmpword);
	    	/*if (tmpword.length() > 1 &&(tmpword.substring(tmpword.length()-2)==".\""||tmpword.substring(tmpword.length()-2)==".\'"||
	    		tmpword.substring(tmpword.length()-2)=="?\""||tmpword.substring(tmpword.length()-2)=="?\'"||tmpword.substring(tmpword.length()-2)==".)"))
	    		tmpword = tmpword.substring(0, tmpword.length()-2);
	    	if (tmpword.charAt(0)=='\"' || tmpword.charAt(0)=='\'' || tmpword.charAt(0)==',' || tmpword.charAt(0)==';'|| tmpword.charAt(0)=='(')
	    		tmpword = tmpword.substring(1,tmpword.length());
	    	if (tmpword.length() > 0&&(tmpword.charAt(tmpword.length()-1)=='\"' || tmpword.charAt(tmpword.length()-1)=='\'' ||
	    			tmpword.charAt(tmpword.length()-1)==','|| tmpword.charAt(tmpword.length()-1)==';' ||
	    			tmpword.charAt(tmpword.length()-1)=='?'||tmpword.charAt(tmpword.length()-1)=='.'||tmpword.charAt(tmpword.length()-1)==')'))
	    		tmpword = tmpword.substring(0, tmpword.length()-1);
	    	*/	
	    	i_real = realrear;
	    	
	    	//if(tmpword == "n/a") System.out.println("I find it");
	    	if(tmpword != null && tmpword.equals("N/A") == false)
	    	realtemp.add(tmpword);
	    	
	    	//System.out.println(realrear);
	    	//System.out.println(realtemp.size()+"Temp Size");
	    	/*if (wordlist.indexOf(tmpword) == -1 && tmpword.equals("n/a") == false)
	    	wordlist.add(tmpword);*/
	    	continue;
	    	}
	    	else if(s_real.charAt(i_real)!=' ' &&s_real.charAt(i_real)!='\t' && (realcounttab >= 7 && realcounttab%2==1)){
	    		realrear = getWord(s_real,i_real);
		    	//System.out.println(realrear+"I am in 6");
		    	//String tmpword = s_real.substring(i_real, realrear+1);
		    	i_real = realrear;
	    	}
	    	else if(s_real.charAt(i_real)==' ') {
	    		i_real++;
	    		//System.out.println("I am here2");
	    	}
	    	else if(s_real.charAt(i_real)=='\t' || s_real.charAt(i_real)=='\n') {
	    		//
	    		if (realtemp.size() > 0)
	    		{//System.out.println(realtemp.get(0)+"dhka");
	    		    
	    		    
	    			real_sentences.add(realtemp);
	    		
	    			realtemp = new ArrayList<String>();
	    		}
	    		//System.out.println("I am in 7"+realtemp.size());
	    		
	    		i_real++;
	    		realcounttab ++;
	    		//System.out.println("I am here1"+realcounttab);
	    	}
	    }
	    if (realtemp.size() > 0)
		{//System.out.println(realtemp.get(0)+"dhka");
		
	    
		real_sentences.add(realtemp);
			//real_sentences.add(realtemp);
		
		realtemp = new ArrayList<String>();
		}
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    /**************************** new adding ****************************/
	    //output2.println();
	    while(i < length && counttab < 3){
	    	if(s.charAt(i)=='\t') {
	    		counttab++;
	    		//System.out.print("I am here3");
	    	}
	    	//output2.print(s.charAt(i));
	    	i++;
	    }
	    int rear;
	    ArrayList<String> temp = new ArrayList<String>();
	    while(i < length){
	    	//System.out.println("I am here5");
	    	
	    	
	    	if(s.charAt(i)!=' ' &&s.charAt(i)!='\t' && (counttab < 7 || counttab%2!=1)){
	    	rear = getWord(s,i);
	    	
	    	String tmpword = s.substring(i, rear);
	    	int wlen = tmpword.length();
	    	//
	    	if (tmpword.length() > 1 &&(tmpword.substring(tmpword.length()-2).equals(".\"")==true||tmpword.substring(tmpword.length()-2).equals(".\'")==true||
	    		tmpword.substring(tmpword.length()-2).equals("?\"")==true||tmpword.substring(tmpword.length()-2).equals("?\'")==true||
	    		tmpword.substring(tmpword.length()-2).equals("..") == true||tmpword.substring(tmpword.length()-2).equals(".)")==true||tmpword.substring(tmpword.length()-2).equals("),") == true))
	    		tmpword = tmpword.substring(0, tmpword.length()-2);
	    	if (tmpword.charAt(0)=='\"' || tmpword.charAt(0)=='\'' || tmpword.charAt(0)==',' || tmpword.charAt(0)==';'|| tmpword.charAt(0)=='(')
	    		tmpword = tmpword.substring(1,tmpword.length());
	    	if (tmpword.length() > 0&&(tmpword.charAt(tmpword.length()-1)=='\"' || tmpword.charAt(tmpword.length()-1)=='\'' ||
	    			tmpword.charAt(tmpword.length()-1)==','|| tmpword.charAt(tmpword.length()-1)==';' ||
	    			tmpword.charAt(tmpword.length()-1)=='?'||tmpword.charAt(tmpword.length()-1)=='.'||tmpword.charAt(tmpword.length()-1)==')'))
	    		tmpword = tmpword.substring(0, tmpword.length()-1);
	    		
	    	i = rear;
	    	//System.out.println(tmpword);
	    	//if(tmpword == "n/a") System.out.println("I find it");
	    	if(tmpword != null && tmpword.equals("n/a") == false)
	    	temp.add(tmpword);
	    	
	    	//System.out.println(rear);
	    	//System.out.println(temp.size()+"Temp Size");
	    	if (wordlist.indexOf(tmpword) == -1 && tmpword.equals("n/a") == false)
	    	wordlist.add(tmpword);
	    	continue;
	    	}
	    	else if(s.charAt(i)!=' ' &&s.charAt(i)!='\t' && (counttab >= 7 && counttab%2==1)){
	    		rear = getWord(s,i);
		    	//System.out.println(rear+"I am in 6");
		    	//String tmpword = s.substring(i, rear+1);
		    	i = rear;
	    	}
	    	else if(s.charAt(i)==' ') {
	    		i++;
	    		//System.out.println("I am here2");
	    	}
	    	else if(s.charAt(i)=='\t' || s.charAt(i)=='\n') {
	    		//
	    		if (temp.size() > 0)
	    		{//System.out.println(temp.get(0)+"dhka");
	    		    
	    		    
	    			sentences.add(temp);
	    		
	    			temp = new ArrayList<String>();
	    		}
	    		//System.out.println("I am in 7"+temp.size());
	    		
	    		i++;
	    		counttab ++;
	    		//System.out.println("I am here1"+counttab);
	    	}
	    }
	    if (temp.size() > 0)
		{//System.out.println(temp.get(0)+"dhka");
		
	    
		sentences.add(temp);
			//sentences.add(temp);
		
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
	    	
	   /*************************Out Put**************************/
	    /*	String filename = "F:/NLP/Data/text"+dataid+".txt";
		    File file1 = new File(filename);*/
		  /*  if(file1.exists()){
		    	System.out.println("File already exits");
		    	System.exit(0);
		    }
		    */
		   /* PrintWriter output = new PrintWriter(file1);*/
		    /*for(int j = 0;j < wordlist.size();j++){
		    	String as = wordlist.get(j);
		    	
		    	output.print(as+" ");
		    	
		    }*/
		    
	    	 /*for(int j = 0;j < tfidfwordbag.size();j++){
	 	    	ArrayList<Double> as = tfidfwordbag.get(j);
	 	    	System.out.println();
	 	    	for(int k = 0; k < as.size();k++){
	 	    		System.out.print(as.get(k)+" ");
	 	    	}
	 	    }*/
	    	/* output.close();
	    	 */
	    	
	    	 int lengthM = 8;//tfidfwordbag.size()-4;
	    	 Matrix M = new Matrix(lengthM,lengthM,0);
	    	 for(int l = 0; l < lengthM;l++){
	    		 for(int j = 0; j < lengthM;j++){
	    			 if(l==j)
	    				 M.set(j, l, 0.0);
	    			 else{
	    				 M.set(j, l, cosine(tfidfwordbag.get(l),tfidfwordbag.get(j)));
	    				// M.set(j, l, cosineint(wordbag.get(l+4),wordbag.get(j+4)));
	    			 }
	    		 }
	    			 
	    	 }
	    	 for(int l = 0; l < lengthM;l++){
	    		 Matrix tmp = M.getMatrix(0, lengthM-1, l, l);
	    		/* System.out.println("TEMP");
	    		 for(int j = 0; j < lengthM;j++){
	    			 
	      			  System.out.println(tmp.get(j, 0));
	      			 
	      		     }*/ 
	    		 tmp = tmp.times(1/tmp.norm1());
	    		/* System.out.println("New TEMP");
	    		 for(int j = 0; j < lengthM;j++){
	    			 
	      			  System.out.println(tmp.get(j, 0));
	      			 
	      		     }*/ 
	    		 M.setMatrix(0, lengthM-1, l, l, tmp);
	    	 }
	    	 
	    	 
	    	/*for(int l = 0; l < lengthM;l++){
	    		 System.out.println();
	    		 for(int j = 0; j < lengthM;j++){
	    			 
	    			  System.out.print(M.get(l,j)+" ");
	    			 
	    		 }
	    			 
	    	 }*/
	    	 
	    	 pagerank pr = new pagerank();
	    	 Matrix result = pr.getfirstpage(M, 0.85, 0.001);
	    /*	 System.out.println();
	    	 System.out.println("Size of Result"+result.getColumnDimension()+" "+ result.getRowDimension());
	    	 for(int j = 0; j < lengthM;j++){
    			 
   			  System.out.println(result.get(j, 0));
   			 
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
	    	 double ref1 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(0));
	    	 //System.out.println(tfidfwordbag.get(maxindex));
	    	 //System.out.println(tfidfwordbag.get(0));
	    	 double ref2 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(1));
	    	 double ref3 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(2));
	    	 double ref4 = cosine(tfidfwordbag.get(maxindex),tfidfwordbag.get(3));
	    	 
	    	 ArrayList<String> res = sentences.get(maxindex);
	    	 ArrayList<String> sref1 = sentences.get(0);
	    	 ArrayList<String> sref2 = sentences.get(1);
	    	 ArrayList<String> sref3 = sentences.get(2);
	    	 ArrayList<String> sref4 = sentences.get(3);
	    	 
	    	 int sentencelength = res.size();
	    	 double sum = 0.0;
	    	 /*for(int l = 0; l < 4;l++){
	    		 int ccount = 0; 
	    		 for(int k = 0; k < sentencelength;k++){
	    			 String st = res.get(k);
	    			 if(l==0){
	    				 if(sref1.indexOf(st)!=-1){
	    					 ccount++;
	    				 }
	    			 }
	    			 else if(l==1){
	    				 if(sref2.indexOf(st)!=-1){
	    					 ccount++;
	    				 }
	    			 }
	    			 else if(l==2){
	    				 if(sref3.indexOf(st)!=-1){
	    					 ccount++;
	    				 }
	    			 }
	    			 else if(l==3){
	    				 if(sref4.indexOf(st)!=-1){
	    					 ccount++;
	    				 }
	    			 }
	    		 }
	    		 sum+= (double)ccount/sentencelength;
	    		}
	    	 sum = sum/4;
	    	*/
	    	 
	    	 
	    	 /*************************Print Similarity***************************/
	    	 String Trans="";
	    	 String Ref1="";
	    	 String Ref2="";
	    	 String Ref3="";
	    	 String Ref4="";
	    	 for(int l = 0; l < res.size();l++){
	    		 if (l < res.size() -1){
	    			 Trans += res.get(l);
	    			 Trans += " ";
	    		 }
	    		 if (l==res.size()-1)
	    			 Trans += res.get(l);
	    	 }
	    	 for(int l = 0; l < sref1.size();l++){
	    		 
	    		 if (l < sref1.size() -1){
	    			 Ref1 += sref1.get(l);
	    			 Ref1 += " ";
	    		 }
	    		 if (l==sref1.size()-1)
	    			 Ref1 += sref1.get(l);
	    	 }
	    	 for(int l = 0; l < sref2.size();l++){
	    		 if (l < sref2.size() -1){
	    			 Ref2 += sref2.get(l);
	    			 Ref2 += " ";
	    		 }
	    		 if (l==sref2.size()-1)
	    			 Ref2 += sref2.get(l);
	    	 }
	    	 for(int l = 0; l < sref3.size();l++){
	    		 if (l < sref3.size() -1){
	    			 Ref3 += sref3.get(l);
	    			 Ref3 += " ";
	    		 }
	    		 if (l==sref3.size()-1)
	    			 Ref3 += sref3.get(l);
	    	 }
	    		 
	    	 for(int l = 0; l < sref4.size();l++){
	    		 if (l < sref4.size() -1){
	    			 Ref4 += sref4.get(l);
	    			 Ref4 += " ";
	    		 }
	    		 if (l==sref4.size()-1)
	    			 Ref4 += sref4.get(l);
	    	 }
	    	 /**************************Print Testing Files**********************************/
	    	 String docid = "document"+dataid;
			 outputlist.get(0).println("<doc docid=\""+ docid + "\" genre=\"nw\">"); 
			 outputlist.get(0).println("<seg id=\"1\">"+Ref1 +"</seg>");
			 outputlist.get(0).println("</doc> ");
			 
			 outputlist.get(1).println("<doc docid=\""+ docid + "\" genre=\"nw\">"); 
			 outputlist.get(1).println("<seg id=\"1\">"+Ref2 +"</seg>");
			 outputlist.get(1).println("</doc> ");
			 
			 outputlist.get(2).println("<doc docid=\""+ docid + "\" genre=\"nw\">"); 
			 outputlist.get(2).println("<seg id=\"1\">"+Ref3 +"</seg>");
			 outputlist.get(2).println("</doc> ");
			 
			 outputlist.get(3).println("<doc docid=\""+ docid + "\" genre=\"nw\">"); 
			 outputlist.get(3).println("<seg id=\"1\">"+Ref4 +"</seg>");
			 outputlist.get(3).println("</doc> ");
			 
			 outputlist.get(4).println("<doc docid=\""+ docid + "\" genre=\"nw\">"); 
			 outputlist.get(4).println("<seg id=\"1\">"+Trans +"</seg>");
			 outputlist.get(4).println("</doc> ");
	    	 
	    	 
	    	return sum;
	    	
	    }
		
	
	
	public void getM(String filename,ArrayList<String> wholewords,ArrayList<Integer> wholestatic,int totalcount) throws IOException{
		File file = new File(filename);
		Scanner input =  new Scanner(file);
		input.nextLine();
		input.nextLine();
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
		ArrayList<PrintWriter> outputlist = new ArrayList<PrintWriter> (); 
		 String filename2 = "F:/NLP/Evaluate/WithRef/Ref1.xml";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		 
		 
		  
		 
		 
		 
		 
		 String filename3 = "F:/NLP/Evaluate/WithRef/Ref2.xml";
		 File file3 = new File(filename3);
		 PrintWriter output3 = new PrintWriter(file3);
		 
		 String filename4 = "F:/NLP/Evaluate/WithRef/Ref3.xml";
		 File file4 = new File(filename4);
		 PrintWriter output4 = new PrintWriter(file4);
		 
		 String filename5 = "F:/NLP/Evaluate/WithRef/Ref4.xml";
		 File file5 = new File(filename5);
		 PrintWriter output5 = new PrintWriter(file5);
		 
		 String filename6 = "F:/NLP/Evaluate/WithRef/Trans.xml";
		 File file6 = new File(filename6);
		 PrintWriter output6 = new PrintWriter(file6);
		 
		 output2.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		 output2.println("<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">"); 
		 output2.println("<mteval>");
		 output2.println("<refset setid=\"source_set\" srclang=\"Urdu\" trglang=\"English\" refid=\"reference01\">");
		 
		 output3.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		 output3.println("<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">"); 
		 output3.println("<mteval>");
		 output3.println("<refset setid=\"source_set\" srclang=\"Urdu\" trglang=\"English\" refid=\"reference02\">");
		 
		 output4.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		 output4.println("<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">"); 
		 output4.println("<mteval>");
		 output4.println("<refset setid=\"source_set\" srclang=\"Urdu\" trglang=\"English\" refid=\"reference03\">");
		 
		 output5.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		 output5.println("<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">"); 
		 output5.println("<mteval>");
		 output5.println("<refset setid=\"source_set\" srclang=\"Urdu\" trglang=\"English\" refid=\"reference04\">");
		 
		 
		 output6.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		 output6.println("<!DOCTYPE mteval SYSTEM \"ftp://jaguar.ncsl.nist.gov/mt/resources/mteval-xml-v1.3.dtd\">"); 
		 output6.println("<mteval>");
		 output6.println("<tstset setid=\"source_set\" srclang=\"Urdu\" trglang=\"English\" sysid=\"NIST\">");
		 
		 
		 outputlist.add(output2);
		 outputlist.add(output3);
		 outputlist.add(output4);
		 outputlist.add(output5);
		 outputlist.add(output6);
		BufferedReader br = new BufferedReader(new FileReader(filename));  
		String data = br.readLine();//一次读入一行，直到读入null为文件结束  
		//data = br.readLine();
		
		/*while( data!=null  && i < 1556){
			data = br.readLine(); 
				i++;
		}*/
		double totalsum = 0.0; 
		while( data!=null  ){  
			//System.out.println(data);
		     totalsum += worddivider(data,i,outputlist,wholewords,wholestatic,totalcount);
		      i++;
		      
		      data = br.readLine(); //接着读下一行
		      
		} 
		//output2.println("AVG BLEU:"+totalsum/(i-1));
		input.close();
		
		outputlist.get(0).println("</refset>");
		outputlist.get(0).println("</mteval> ");
		outputlist.get(0).close();
		
		outputlist.get(1).println("</refset>");
		outputlist.get(1).println("</mteval> ");
		outputlist.get(1).close();
		outputlist.get(2).println("</refset>");
		outputlist.get(2).println("</mteval> ");
		outputlist.get(2).close();
		outputlist.get(3).println("</refset>");
		outputlist.get(3).println("</mteval> ");
		outputlist.get(3).close();
		outputlist.get(4).println("</tstset>");
		outputlist.get(4).println("</mteval> ");
		outputlist.get(4).close();
			
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
		String filename2 = "F:/NLP/newdata.txt";
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
		
		String filename2 = "F:/NLP/Evaluate/src.xml";
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
