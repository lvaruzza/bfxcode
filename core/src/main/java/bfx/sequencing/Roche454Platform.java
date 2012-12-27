package bfx.sequencing;

public class Roche454Platform extends Platform {

	@Override
	public String[] getNames() {
		return new String[] {"roche","454"};
	}

	@Override
	public String getFragmentName(String seqname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compare(String id, String id2) {
		// TODO Auto-generated method stub
		return 0;
	}

}
