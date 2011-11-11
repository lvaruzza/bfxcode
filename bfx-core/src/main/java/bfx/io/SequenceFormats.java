package bfx.io;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import bfx.utils.MapUtils;
import bfx.utils.compression.CompressionUtils;

public class SequenceFormats {
	private static Logger log = Logger.getLogger(SequenceFormats.class);
	
	private static Map<String,SequenceReader> extensionTable = new HashMap<String,SequenceReader>();
			
	public static void registerExtension(String ext,SequenceReader reader) {
		extensionTable.put(ext,reader);
	}

	private static ServiceLoader<SequenceReader> sequenceReaderLoader = ServiceLoader.load(SequenceReader.class);
	
	static {
		for(SequenceReader reader: sequenceReaderLoader) {
			log.debug("Registering sequence reader: " + reader.getClass().getName());
			for(String ext: reader.getPreferedExtensions()) {
				registerExtension(ext,reader);
			}
		}		
	}
	
	public static SequenceReader getReader(String filename) {
		log.debug("Getting reader for file: " + filename);
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		if (!extensionTable.containsKey(ext)) {
			log.debug("Extensions: " + MapUtils.toString(extensionTable));
			throw new RuntimeException(String.format("Unknown file format '%s' for file '%s': Could not create an appropriate sequence reader.",ext,filename));
		}
		return extensionTable.get(ext);
	}
}
