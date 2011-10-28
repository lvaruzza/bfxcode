package bfx.io;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class SequenceFormat {

	private static Map<String,SequenceReader> extensionTable = new HashMap<String,SequenceReader>();
			
	public static void registerExtension(String ext,SequenceReader reader) {
		extensionTable.put(ext,reader);
	}
	
	public static SequenceReader getReader(String filename) {
		String ext = FilenameUtils.getExtension(filename);
		if (!extensionTable.containsKey(ext)) {
			
		}
		return extensionTable.get(ext);
	}
}
