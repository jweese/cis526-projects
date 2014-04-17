package turkers;


public class Pair {

	public final String Trans;
	public final String Edit;
	
	public Pair(String trans, String edit) {
		Trans = trans;
		Edit = edit;
	}
	
	@Override
	public boolean equals(Object obj) {
		Pair p = (Pair) obj;
		return Trans.equals(p.Trans) && Edit.equals(p.Edit);
	}
	
	@Override
	public int hashCode() {
		return (Trans + Edit).hashCode();
	}
}
