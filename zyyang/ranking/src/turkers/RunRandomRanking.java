package turkers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Jama.Matrix;

public class RunRandomRanking {

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Param: BASE_PATH PERCENT");
			return;
		}
		
		String basePath = args[0];
		int percent = Integer.parseInt(args[1]);
		if(percent < 0) {
			percent = 0;
		} else if(percent > 100) {
			percent = 100;
		}
		File mMatrixFile = new File(basePath, "transition_graph.mtx");
		Matrix Mt = new Matrix(SparseMtxReader.readDoubleMatrix(mMatrixFile));
		
		Pagerank ranker = new Pagerank();
		Matrix result = ranker.getfirstpage(Mt, 0.08, 0.01);
		
		List<Score> scores = new ArrayList<Score>();
		int number = result.getRowDimension() * percent / 100;
		for(int i = 0; i < number; i++) {
			scores.add(new Score(i, result.get(i, 0)));
		}
		for(Score s : scores) {
			System.out.printf("%d\t%f\n", s.index, s.score);
		}
	}

}
