package MT;
import Jama.*; 
/**
 * 
 * 
 * @author Mingkun Gao, <gmingkun@seas.upenn.edu>
 * @version $LastChangedDate$
 */
public class TwoLayerRank extends Statistic{
	private static final double POSITIVE_INFINITY = 10000;

	public Matrix getfirstpage(Matrix Mt, Matrix Nt, Matrix W_ba,Matrix W_hat, double d, double v_quadratic_error){
		int Nh = Mt.getRowDimension();
		double[] init = new double[Nh];
		for (int i = 0; i < Nh; i++){
			init[i] = (double)1.0/Nh;
		}
		int Nr = Nt.getRowDimension();
		double[] Tinit = new double[Nr];
		for (int i = 0; i < Nr; i++){
			Tinit[i] = (double)1.0/Nr;
		}
		
		double[] last_init = new double[Nh];
		for (int i = 0; i < Nh; i++){
			last_init[i] = POSITIVE_INFINITY;
		}
		
		
		Matrix v = new Matrix(init,Nh);
		Matrix t = new Matrix(Tinit,Nr);
		v = v.timesEquals((double)1/v.norm1());
		t = t.timesEquals((double)1/t.norm1());
		Matrix last_v = new Matrix(last_init,Nh);
		//Matrix M = Mt;
		//Matrix ones = new Matrix(N,N,1);
		//Matrix M_hat = M.timesEquals(d).plus(ones.timesEquals((double)(1 - d) / N));
		//Matrix N = Nt;
		//Matrix W_BA = W_ba;
		//Matrix W_BA = W_ba;
		Matrix Residual = v.minus(last_v);
		while(Residual.norm2() > v_quadratic_error){
			last_v = v;
			v = Mt.times(v).times(1-d).plus(W_hat.times(t).times(d));
			v = v.times((double)1/v.norm1());
			t = Nt.times(t).times(1-d).plus(W_ba.transpose().times(v).times(d));
			t = t.times((double)1/t.norm1());
			Residual = v.minus(last_v);
		}
		return v;
		
	}
	
	
}
