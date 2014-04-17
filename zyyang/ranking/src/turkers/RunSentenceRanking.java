package turkers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Jama.Matrix;

public class RunSentenceRanking {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Param: BASE_PATH");
			return;
		}
		
		String basePath = args[0];
		File mMatrixFile = new File(basePath, "transition_graph.mtx");
		Matrix Mt = new Matrix(SparseMtxReader.readDoubleMatrix(mMatrixFile));
		
		Pagerank ranker = new Pagerank();
		Matrix result = ranker.getfirstpage(Mt, 0.08, 0.01);
		
		List<Score> scores = new ArrayList<Score>();
		for(int i = 0; i < result.getRowDimension(); i++) {
			scores.add(new Score(i, result.get(i, 0)));
		}
		Collections.sort(scores);
		for(Score s : scores) {
			System.out.printf("%d\t%f\n", s.index, s.score);
		}
	}

}
