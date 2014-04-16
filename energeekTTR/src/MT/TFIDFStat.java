
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
public class TFIDFStat {



	public ArrayList<String> wholewords = new ArrayList<String>();
	public ArrayList<Integer> wholestatic = new ArrayList<Integer>();
	public int totalcount = 0;
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
	
	public void worddivider(String s,int dataid,PrintWriter output2,ArrayList<String> wholewordlist,ArrayList<Integer> wholestatic) throws FileNotFoundException{
		ArrayList<ArrayList<String>> sentences = new ArrayList<ArrayList<String>>();
		
		ArrayList<String> wordlist = new ArrayList<String>();
		int length = s.length();
		String s_real = s; //////////////////////////
		s = s.toLowerCase();
		
		int counttab = 0;
	    int i  = 0;
	    /**************************** new adding ****************************/
	    int realcounttab = 0;
	    int i_real  = 0;
	   // ArrayList<ArrayList<String>> real_sentences = new ArrayList<ArrayList<String>>();
	    
	    
	   
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
	    s = s.substring(i);
	    
	    s = s.replaceAll("[^((a-zA-Z)|\t|0-9) ]", "").toLowerCase();
	    ArrayList<String> temp = new ArrayList<String>();
	    i = 0;
	    length = s.length();
	    while(i < length){
	    	//System.out.println("I am here5");
	    	
	    	
	    	if(s.charAt(i)!=' ' &&s.charAt(i)!='\t' && (counttab < 7 || counttab%2!=1)){
	    	rear = getWord(s,i);
	    	
	    	String tmpword = s.substring(i, rear);
	    	int wlen = tmpword.length();
	    	//
	    	/*if (tmpword.length() > 1 &&(tmpword.substring(tmpword.length()-2).equals(".\"")==true||tmpword.substring(tmpword.length()-2).equals(".\'")==true||
	    		tmpword.substring(tmpword.length()-2).equals("?\"")==true||tmpword.substring(tmpword.length()-2).equals("?\'")==true||tmpword.substring(tmpword.length()-2).equals("..") == true
	    		||tmpword.substring(tmpword.length()-2).equals(".)")==true||tmpword.substring(tmpword.length()-2).equals("),")==true))
	    		tmpword = tmpword.substring(0, tmpword.length()-2);
	    	if (tmpword.charAt(0)=='\"' || tmpword.charAt(0)=='\'' || tmpword.charAt(0)==',' || tmpword.charAt(0)==';'|| tmpword.charAt(0)=='(')
	    		tmpword = tmpword.substring(1,tmpword.length());
	    	if (tmpword.length() > 0&&(tmpword.charAt(tmpword.length()-1)=='\"' || tmpword.charAt(tmpword.length()-1)=='\'' ||
	    			tmpword.charAt(tmpword.length()-1)==','|| tmpword.charAt(tmpword.length()-1)==';' ||
	    			tmpword.charAt(tmpword.length()-1)=='?'||tmpword.charAt(tmpword.length()-1)=='.'||tmpword.charAt(tmpword.length()-1)==')'))
	    		tmpword = tmpword.substring(0, tmpword.length()-1);
	    	*/	
	    	i = rear;
	    	//System.out.println(tmpword);
	    	//if(tmpword == "n/a") System.out.println("I find it");
	    	
	    	
	    	//System.out.println(rear);
	    	//System.out.println(temp.size()+"Temp Size");
	    	if (wholewordlist.indexOf(tmpword) == -1 && tmpword.equals("n/a") == false){
	    		
	    	wholewordlist.add(tmpword);
	    	wholestatic.add(1);
	    	}
	    	else if(wholewordlist.indexOf(tmpword) != -1&&temp.indexOf(tmpword)==-1 && tmpword.equals("n/a") == false){
	    		int position = wholewordlist.indexOf(tmpword);
	    		wholestatic.set(position, wholestatic.get(position)+1);
	    	}
	    	
	    	if(tmpword != null && tmpword.equals("n/a") == false)
	    	temp.add(tmpword);
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
	    			totalcount++;
	    		
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
		totalcount++;
		
		temp = new ArrayList<String>();
		}
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
	   
	    /*************************************TFIDF******************************/
	   
	    	
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
	    	
	    	
	    	 
	    	/*for(int l = 0; l < lengthM;l++){
	    		 System.out.println();
	    		 for(int j = 0; j < lengthM;j++){
	    			 
	    			  System.out.print(M.get(l,j)+" ");
	    			 
	    		 }
	    			 
	    	 }*/
	    	 
	    
	    	 
	  
		
	
	
	public void getM(String filename) throws IOException{
		File file = new File(filename);
		Scanner input =  new Scanner(file);
		input.nextLine();
		input.nextLine();
		int i = 1;
		
		 String filename2 = "F:/ACL/NLP/Result/TFIDFSTAT.txt";
		 File file2 = new File(filename2);
		 PrintWriter output2 = new PrintWriter(file2);
		BufferedReader br = new BufferedReader(new FileReader(filename));  
		String data = br.readLine();//一次读入一行，直到读入null为文件结束  
		
		while( data!=null  ){  
			//System.out.println(data);
		      worddivider(data,i,output2,wholewords,wholestatic);
		      i++;
		     // totalcount++;
		      
		      data = br.readLine(); //接着读下一行
		      
		}  
		System.out.println(wholewords.size());
		System.out.println(wholestatic.size());
		System.out.println(totalcount);
		for(int j = 0; j < wholewords.size();j++){
			output2.println(wholewords.get(j)+" "+wholestatic.get(j));
		}
		input.close();
		output2.close();
		
		
		
	}
	

}

