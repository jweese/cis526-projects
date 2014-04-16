package BLEU;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * 
 * @author Mingkun Gao, <gmingkun@seas.upenn.edu>
 * @version $LastChangedDate$
 */

public class computeDocBleu {
	public double computeBleu(String ref1, String ref2, String ref3, String ref4, String trans,String result) throws IOException{
		BufferedReader inref1 = new BufferedReader(new FileReader(ref1));
		BufferedReader inref2 = new BufferedReader(new FileReader(ref2));
		BufferedReader inref3 = new BufferedReader(new FileReader(ref3));
		BufferedReader inref4 = new BufferedReader(new FileReader(ref4));
		BufferedReader intrans = new BufferedReader(new FileReader(trans));
		
		
		
		
		String Ref1 = "";
		String Ref2 = "";
		String Ref3 = "";
		String Ref4 = "";
		String Trans = "";
		double bleusum = 0.0;
		int count = 0;
		while ((Ref1 = inref1.readLine()) != null&&(Ref2 = inref2.readLine()) != null
		&&(Ref3 = inref3.readLine()) != null&&(Ref4 = inref4.readLine()) != null &&(Trans = intrans.readLine()) != null){
			String [] refset = new String[3];
			refset[0] = Ref1;
			refset[1] = Ref2;
			refset[2] = Ref3;
			double b1 = BLEU.computeSentenceBleu(refset, Trans);
			refset[0] = Ref1;
			refset[1] = Ref2;
			refset[2] = Ref4;
			double b2 = BLEU.computeSentenceBleu(refset, Trans);
			refset[0] = Ref1;
			refset[1] = Ref3;
			refset[2] = Ref4;
			double b3 = BLEU.computeSentenceBleu(refset, Trans);
			refset[0] = Ref2;
			refset[1] = Ref3;
			refset[2] = Ref4;
			double b4 = BLEU.computeSentenceBleu(refset, Trans);
			
			bleusum += (b1+b2+b3+b4)/4;
			count++;
			
			
		}
		inref1.close();
		inref2.close();
		inref3.close();
		inref4.close();
		intrans.close();
		//System.out.println(count);
		 
		 PrintWriter res = new PrintWriter(new File(result));
		 res.println((double)bleusum/count);
		 res.close();
		return (double)bleusum/count;
		
	}

}
