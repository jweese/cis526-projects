package turkers;


public class Score implements Comparable<Score> {
	public int index;
	public double score;
	
	public Score(int index, double score) {
		this.index = index;
		this.score = score;
	}

	@Override
	public int compareTo(Score o) {
		if(score > o.score) {
			return -1;
		} else if(score == o.score) {
			return 0;
		} else {
			return 1;
		}
	}
}