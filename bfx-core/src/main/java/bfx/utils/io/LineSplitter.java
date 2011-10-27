package bfx.utils.io;

import com.google.common.base.Function;

public class LineSplitter implements Function<String, String[]> {
	private String sep;
	
	public LineSplitter() {
		this("\t");
	}
	
	LineSplitter(String sep) {
		this.sep = sep;
	}
	
	public String[] apply(String line) {
		return line.split(sep);
	}
}
