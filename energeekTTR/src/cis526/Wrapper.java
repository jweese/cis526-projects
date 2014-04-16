package cis526;

import java.util.HashMap;
import java.util.ArrayList;

public class Wrapper {
	HashMap<String, Double> para = new HashMap<String, Double>();
	ArrayList<Double> parab = new ArrayList<Double>();
	
	public Wrapper(HashMap<String, Double> para, ArrayList<Double> parab){
		this.para = para;
		this.parab = parab;
	}
	public Wrapper(){}
	
}
