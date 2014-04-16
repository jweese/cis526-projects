package turkers;

public class IDnode implements Comparable<IDnode> {
	public final String ID;
	public double Rank;
	public int Count;

	IDnode(String id, double rank, int count) {
		ID = id;
		Rank = rank;
		Count = count;
	}

	@Override
	public int compareTo(IDnode o) {
		double score = Rank / Count;
		double score2 = o.Rank / o.Count;
		if (score > score2) {
			return -1;
		} else if (score < score2) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return String.format("ID: %s, Rank: %f, Count: %d, Score: %f", ID,
				Rank, Count, Rank / Count);
	}
}
