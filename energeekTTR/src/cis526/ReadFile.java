/**
 * 
 */
package cis526;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author yucongli
 *
 */
public class ReadFile {

	/**
	 * return the translated sentences index of turker ("turkNum")
	 * @param turkerNum
	 */
	public static FormatTurkerFileInput turkerFileInput(String fileNameStr) {
		ArrayList<Integer> translatedSentNum = new ArrayList<Integer>();
		String turkerID = "";
		FormatTurkerFileInput returnVal = new FormatTurkerFileInput(turkerID, translatedSentNum);
		
		try{
			File file = new File("./translatorPerformance/" + fileNameStr + ".txt");
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader dis = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(dis);
			
			int lineNum = 1;
			String tmpLine = "";
			while ((tmpLine = reader.readLine()) != null) {
				tmpLine.trim();
				if (lineNum == 1)
					turkerID = tmpLine;
				if (lineNum % 2 == 0) { //all of even number lines contains sentence index
					translatedSentNum.add(Integer.valueOf(tmpLine));
				}
				lineNum++;
			}
			dis.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		returnVal.setData(turkerID, translatedSentNum);
		return returnVal;
	}
	
	/**
	 * 
	 * @param fileName
	 */
	public static FormatSentFileInput sentFileInput(int idx){
		String fileName = "./data/text" + idx + ".txt";
		//store four reference sentences
		ArrayList<String> refList = new ArrayList<String>();
		//key is translator ID and value is translated sentence
		HashMap<String, String> transDict = new HashMap<String, String>();
		int lineNum = 0;
		//tmpStr1 will store translator ID, and tmpStr2 will store the corresponding sentence
		String tmpStr1 = "";
		String tmpStr2 = "";
		
		try{
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader dis = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(dis);
			
			while (lineNum < 20) {
				switch (lineNum) {
				//these lines are reference sentences
				case 1: case 3: case 5: case 7:
					refList.add(reader.readLine());
					break;
				//these lines are translator IDs
				case 9: case 12: case 15: case 18:
					tmpStr1 = reader.readLine();
					break;
				//these lines are translated sentences
				case 10: case 13: case 16: case 19:
					tmpStr2 = reader.readLine();
					transDict.put(tmpStr1, tmpStr2);
					break;
				default:
					reader.readLine();
					break;
				}
				lineNum++;
			}
			dis.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		
		//transform from ArrayList to String[] for bleu score computing use
        int size = refList.size();  
        String[] array = (String[]) refList.toArray(new String[size]); 
        //return multiple collections
		FormatSentFileInput returnVal = new FormatSentFileInput(array, transDict);
		return returnVal;
	}
	
	/**
	 * 
	 * @return
	 */
	public static ArrayList<String> rankedTurkerListInput(String fileName) {
		ArrayList<String> rankedTurkerList = new ArrayList<String>();
		try{
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader dis = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(dis);
			
			String tmpLine = "";
			while ((tmpLine = reader.readLine()) != null) {
				rankedTurkerList.add(tmpLine.trim());
			}
			dis.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return rankedTurkerList;
	}
}