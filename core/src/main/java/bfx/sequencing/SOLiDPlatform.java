package bfx.sequencing;

import java.util.Comparator;
import java.util.Scanner;

public class SOLiDPlatform extends Platform {

	@Override
	public String[] getNames() {
		return new String[] {"SOLiD"};
	}

	@Override
	public String getFragmentName(String seqname) {
		int pos = seqname.lastIndexOf('_');
		return seqname.substring(0, pos);
	}

	@Override
	public int compare(String name1, String name2) {
		Scanner s1 = new Scanner(name1).useDelimiter("_");
		Scanner s2 = new Scanner(name2).useDelimiter("_");
		int a1 = s1.nextInt();
		int a2 = s2.nextInt();
		if (a1 == a2) {
			int b1 = s1.nextInt();
			int b2 = s2.nextInt();
			if (b1 == b2) {
				int c1 = s1.nextInt();
				int c2 = s2.nextInt();				
				if (c1 == c2) return 0;
				else return c1 < c2 ? -1 : 1;				

			} else {
				return b1 < b2 ? -1 : 1;				
			}
		} else {
			return a1 < a2 ? -1 : 1;
		}
	}
	
}
