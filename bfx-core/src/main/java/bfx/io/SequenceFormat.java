package bfx.io;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import bfx.utils.compression.CompressionUtils;

public class SequenceFormat {

	private static Map<String,SequenceReader> extensionTable = new HashMap<String,SequenceReader>();
			
	public static void registerExtension(String ext,SequenceReader reader) {
		extensionTable.put(ext,reader);
	}
	
	public static SequenceReader getReader(String filename) {
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		if (!extensionTable.containsKey(ext)) {
			throw new RuntimeException(String.format("Unknown file format '%s' for file '%s': Could not create an appropriate sequence reader.",ext,filename));
		}
		return extensionTable.get(ext);
	}
}
