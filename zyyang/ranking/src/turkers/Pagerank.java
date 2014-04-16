package turkers;
import Jama.Matrix;
/**
 * 
 * 
 * @author Mingkun Gao, <gmingkun@seas.upenn.edu>
 * @version $LastChangedDate$
 */
public class Pagerank {
	private static final double POSITIVE_INFINITY = 10000;

	public Matrix getfirstpage(Matrix Mt, double d, double v_quadratic_error){
		int N = Mt.getRowDimension();
		double[] init = new double[N];
		for (int i = 0; i < N; i++){
			init[i] = (double)1.0/N;
		}
		double[] last_init = new double[N];
		for (int i = 0; i < N; i++){
			last_init[i] = POSITIVE_INFINITY;
		}
		
		
		Matrix v = new Matrix(init,N);
		v = v.timesEquals((double)1/v.norm1());
		Matrix last_v = new Matrix(last_init,N);
		Matrix M = Mt;
//		Matrix ones = new Matrix(N,N,1);
//		Matrix M_hat = M.timesEquals(d).plus(ones.timesEquals((double)(1 - d) / N));
		// To avoid memory overflow
		Matrix M_hat = M.timesEquals(d);
		double walk = (1 - d) / N;
		for(int i = 0; i < M_hat.getRowDimension(); i++) {
			for(int j = 0; j < M_hat.getColumnDimension(); j++) {
				double value = M_hat.get(i, j);
				M_hat.set(i, j, value + walk);
			}
		}
		
		Matrix Residual = v.minus(last_v);
		while(Residual.norm2() > v_quadratic_error){
			last_v = v;
			v = M_hat.times(v);
			Residual = v.minus(last_v);
		}
		return v;
		
	}
	
	
}
