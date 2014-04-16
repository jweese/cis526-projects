package cis526;

import java.util.ArrayList;
import java.util.HashMap;

import Jama.*; 
import MT.Statistic;
/**
 * 
 * 
 * @author Chunxiao Mu, <chunxmu@seas.upenn.edu>
 * @version $LastChangedDate$
 */
public class IterateRank extends Statistic{
	private static final double POSITIVE_INFINITY = 10000;
	HashMap<String, Double> parameter = new HashMap<String, Double>();
	ArrayList<String> ids = new ArrayList<String>(); //a list of four translator
	ArrayList<Double> preScore = new ArrayList<Double>(); //a list of the score in the previous rank result
	double sum = 0;

	public Matrix reRank(Matrix Mt, double d, double v_quadratic_error, ArrayList<String> ids, ArrayList<Double> preScore, ArrayList<Double> refInput){
		int N = Mt.getRowDimension();
		
		this.ids = ids;
		this.preScore = preScore;
		
		
		for(int i = 0; i < 4; i++){
			sum = sum + preScore.get(i) + refInput.get(i);
		}
		/**
		 * get the normalized initial value
		 */
		for(int i = 0; i < 4; i++){
			preScore.add(i, preScore.get(i)/sum); 
		}
		/**
		 * for the references, give 0 initial value
		 */
		double[] init = new double[N];
		for (int i = 0; i < 4; i++){
			init[i] = refInput.get(i)/sum;
		}
		for (int i = 4; i < N; i++){
			int index = i-4;
			init[i] = preScore.get(index);
		}
		
		double[] last_init = new double[N];
		for (int i = 0; i < N; i++){
			last_init[i] = POSITIVE_INFINITY;
		}
		
		/**
		 * use the page rank algo
		 */
		Matrix v = new Matrix(init,N);
		v = v.timesEquals((double)1/v.norm1());
		Matrix last_v = new Matrix(last_init,N);
		Matrix M = Mt;
		Matrix ones = new Matrix(N,N,1);
		Matrix M_hat = M.timesEquals(d).plus(ones.timesEquals((double)(1 - d) / N));
		Matrix Residual = v.minus(last_v);
		while(Residual.norm2() > v_quadratic_error){
			last_v = v;
			v = M_hat.times(v);
			Residual = v.minus(last_v);
		}
		return v;
		
	}
	
	public Matrix reRank(Matrix Mt, double d, double v_quadratic_error, ArrayList<String> ids, ArrayList<Double> preScore){
		int N = Mt.getRowDimension();
		
		this.ids = ids;
		this.preScore = preScore;
		
		
		for(int i = 0; i < 4; i++){
			sum = sum + preScore.get(i);
		}
		/**
		 * get the normalized initial value
		 */
		for(int i = 0; i < 4; i++){
			preScore.add(i, preScore.get(i)/sum); 
		}
		/**
		 * for the references, give 0 initial value
		 */
		double[] init = new double[N];
		for (int i = 0; i < 4; i++){
			init[i] = 0;
		}
		for (int i = 4; i < N; i++){
			int index = i-4;
			init[i] = preScore.get(index);
		}
		
		double[] last_init = new double[N];
		for (int i = 0; i < N; i++){
			last_init[i] = POSITIVE_INFINITY;
		}
		
		/**
		 * use the page rank algo
		 */
		Matrix v = new Matrix(init,N);
		v = v.timesEquals((double)1/v.norm1());
		Matrix last_v = new Matrix(last_init,N);
		Matrix M = Mt;
		Matrix ones = new Matrix(N,N,1);
		Matrix M_hat = M.timesEquals(d).plus(ones.timesEquals((double)(1 - d) / N));
		Matrix Residual = v.minus(last_v);
		while(Residual.norm2() > v_quadratic_error){
			last_v = v;
			v = M_hat.times(v);
			Residual = v.minus(last_v);
		}
		return v;
		
	}
	
	
}