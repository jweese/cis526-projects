package turkers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SparseMtxReader {

	public static double[][] readDoubleMatrix(File file) {
		double[][] array = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			boolean first = true;
			while((line = reader.readLine()) != null) {
				if(line.startsWith("%")) {
					continue;
				}
				if(first) {
					first = false;
					String[] data = line.split(" ");
					int rowSize = Integer.parseInt(data[0]);
					int colSize = Integer.parseInt(data[1]);
					array = new double[rowSize][colSize];
				} else {
					String[] data = line.split(" ");
					int row = Integer.parseInt(data[0]) - 1;
					int col = Integer.parseInt(data[1]) - 1;
					double value = Double.parseDouble(data[2]);
					array[row][col] = value;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return array;
	}
}
