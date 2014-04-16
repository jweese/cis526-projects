package turkers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

public class TransEditRanker {


	private Map<Pair, Integer> mPairCounts = null;
	private Map<Pair, Integer> mPairIndex = null;
	private Map<String, List<Integer>> mTurkerIndex = null;
	private Map<String, List<Integer>> mEditorIndex = null;
	private Matrix N = null;
	private Matrix W_ba = null;
	private Matrix W_hat = null;
	private Map<String, IDnode> mTurkerMaps = new HashMap<String, IDnode>();
	private double mLambda = 0;
	
	public TransEditRanker() {
	}

	public TransEditRanker(String colabDir) {
		initColaborData(colabDir);
	}
	
	public void setCollaborateMatrix(Matrix n) {
		N = n;
		normalizeN();
	}

	public void setW_ba(Matrix w_ba) {
		W_ba = w_ba;
	}

	public void setW_hat(Matrix w_hat) {
		W_hat = w_hat.transpose();
	}

	public List<Score> rankTransition(Matrix M) {
		List<Score> list = new ArrayList<Score>();
		TwoLayerRank pr = new TwoLayerRank();
		Matrix result = pr.getfirstpage(M, N, W_ba, W_hat, mLambda, 0.01);
		for(int i = 0; i < result.getRowDimension(); i++) {
			list.add(new Score(i, result.get(i, 0)));
		}
		return list;
	}

	public void setLambda(double lambda) {
		this.mLambda = lambda;
	}

	public void rank(String inputDir) {
		File dir = new File(inputDir);
		File[] files = dir.listFiles();
		try {
			for (File file : files) {
				rankSentence(file);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public List<IDnode> getTranslatorRankings() {
		List<IDnode> list = new ArrayList<IDnode>(mTurkerMaps.values());
		return list;
	}

	private void normalizeN() {
		for(int i = 0; i < N.getColumnDimension(); i++) {
			double sum = 0;
			for(int j = 0; j < N.getRowDimension(); j++) {
				sum += N.get(i, j);
			}
			if(sum != 0) {
				for(int j = 0; j < N.getRowDimension(); j++) {
					N.set(i, j, N.get(i, j) / sum);
				}
			}
		}
	}

	private void rankSentence(File file) throws NumberFormatException,
			IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line;
		ArrayList<ArrayList<Double>> tfidfwordbag = new ArrayList<ArrayList<Double>>();
		ArrayList<String> sentences = new ArrayList<String>();
		ArrayList<String> idList = new ArrayList<String>();
		int count = 0;
		while ((line = in.readLine()) != null) {
			if (count > 0 && count < 9) {
				if (count % 2 == 0) {
					ArrayList<Double> temparray = new ArrayList<Double>();
					String[] s = line.split(" ");
					for (int i = 0; i < s.length; i++) {
						temparray.add(Double.parseDouble(s[i]));
					}
					tfidfwordbag.add(temparray);
				}
				if (count % 2 == 1) {
					sentences.add(line);
				}
			}
			if (count >= 9) {
				if (count % 3 == 0) {
					idList.add(line);
				}
				if (count % 3 == 1) {
					sentences.add(line);
				} else if (count % 3 == 2) {
					ArrayList<Double> temparray = new ArrayList<Double>();
					String[] s = line.split(" ");
					for (int i = 0; i < s.length; i++) {
						temparray.add(Double.parseDouble(s[i]));
					}
					tfidfwordbag.add(temparray);
				}
			}
			count++;
		}
		in.close();

		int lengthM = tfidfwordbag.size() - 4;
		Matrix M = new Matrix(lengthM, lengthM, 0);
		for (int l = 0; l < lengthM; l++) {
			for (int j = 0; j < lengthM; j++) {
				if (l == j)
					M.set(j, l, 0.0);
				else {
					M.set(j, l,
							cosine(tfidfwordbag.get(l + 4),
									tfidfwordbag.get(j + 4)));
				}
			}

		}

		final int Nsize = mPairCounts.size();
		double[][] A = new double[lengthM][Nsize];
		for (int i = 0; i < lengthM; i++) {
			for (int j = 0; j < Nsize; j++)
				A[i][j] = 0.0;
		}
		for (int i = 0; i < idList.size(); i++) {
			String id = idList.get(i);
			List<Integer> indicies;
			if(i < 4) { // Translator
				indicies = mTurkerIndex.get(id);
			} else { // Editor
				indicies = mEditorIndex.get(id);
			}
			if(indicies != null) {
				for(Integer index : indicies) {
					A[i][index] = 1;
				}
			}
		}

		double[] Csums = new double[Nsize];
		for (int i = 0; i < Nsize; i++)
			Csums[i] = 0.0;
		for (int i = 0; i < Nsize; i++) {
			for (int j = 0; j < lengthM; j++) {
				Csums[i] += A[j][i];
			}
		}

		double[] Rsums = new double[lengthM];
		for (int i = 0; i < lengthM; i++)
			Rsums[i] = 0.0;
		for (int i = 0; i < lengthM; i++) {
			for (int j = 0; j < Nsize; j++) {
				Rsums[i] += A[i][j];
			}
		}

		Matrix W_ba = new Matrix(lengthM, Nsize, 0);
		Matrix W_hat = new Matrix(lengthM, Nsize, 0);

		for (int i = 0; i < lengthM; i++) {
			for (int j = 0; j < Nsize; j++) {
				if (Rsums[i] != 0)
					W_ba.set(i, j, A[i][j] / Rsums[i]);
			}

		}
		for (int i = 0; i < lengthM; i++) {
			for (int j = 0; j < Nsize; j++) {
				if (Csums[j] != 0)
					W_hat.set(i, j, A[i][j] / Csums[j]);
			}

		}

		for (int l = 0; l < lengthM; l++) {
			Matrix tmp = M.getMatrix(0, lengthM - 1, l, l);
			if (tmp.norm1() != 0)
				tmp = tmp.times(1 / tmp.norm1());
			M.setMatrix(0, lengthM - 1, l, l, tmp);
		}

		TwoLayerRank pr = new TwoLayerRank();
		Matrix result = pr.getfirstpage(M, N, W_ba, W_hat, mLambda, 0.01);

		for (int i = 0; i < 4; i++) {
			String transId = idList.get(i);
			for (int j = 0; j < 3; j++) {
				int ind = i * 3 + j + 4;
				if (ind >= idList.size()) {
					break;
				}
				String editId = idList.get(ind);
				Pair pair = new Pair(transId, editId);
				Integer index = mPairIndex.get(pair);
				if (index != null) {
					double rank = result.get(index, 0);
					IDnode node = mTurkerMaps.get(transId);
					if (node == null) {
						node = new IDnode(transId, rank, 1);
						mTurkerMaps.put(transId, node);
					} else {
						node.Rank += rank;
						node.Count += 1;
					}
				}
			}
		}
	}

	private void initColaborData(String colabDir) {
		File allcolabor = new File(colabDir, "allcolabor.txt");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(allcolabor));
			String line;
			mPairCounts = new HashMap<Pair, Integer>();
			mPairIndex = new HashMap<Pair, Integer>();
			mTurkerIndex = new HashMap<String, List<Integer>>();
			mEditorIndex = new HashMap<String, List<Integer>>();
			int index = 0;
			while ((line = in.readLine()) != null) {
				String[] s = line.split(" ");
				String tId = s[0];
				String eId = s[1];
				Pair p = new Pair(tId, eId);
				mPairCounts.put(p, (int) Double.parseDouble(s[2]));
				mPairIndex.put(p, index);
				List<Integer> tIdList = mTurkerIndex.get(tId);
				if(tIdList == null) {
					tIdList = new ArrayList<Integer>();
					mTurkerIndex.put(tId, tIdList);
				}
				tIdList.add(index);
				List<Integer> eIdList = mEditorIndex.get(eId);
				if(eIdList == null) {
					eIdList = new ArrayList<Integer>();
					mEditorIndex.put(eId, eIdList);
				}
				eIdList.add(index);
				
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}

		File colaborMatrix = new File(colabDir, "nallclabormatrix.txt");
		try {
			in = new BufferedReader(new FileReader(colaborMatrix));
			N = new Matrix(mPairCounts.size(), mPairCounts.size(), 0);
			String line;
			int row = 0;
			while ((line = in.readLine()) != null) {
				String[] s = line.split(" ");
				for (int col = 0; col < s.length; col++) {
					N.set(row, col, Double.parseDouble(s[col]));
				}
				row++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

	private double cosine(List<Double> a, List<Double> b) {
		int lenA = a.size();
		int lenB = b.size();
		if (lenA != lenB) {
			System.out.println("Error");
			return -2;
		}
		int i = 0;
		double sum = 0.0;
		double sumA = 0.0;
		double sumB = 0.0;
		for (; i < lenA; i++) {
			sum += a.get(i) * b.get(i);
			sumA += a.get(i) * a.get(i);
			sumB += b.get(i) * b.get(i);
		}
		return sum / (Math.sqrt(sumA) * Math.sqrt(sumB));
	}
}
