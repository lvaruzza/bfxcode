package bfx.io;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import bfx.utils.BFXMapUtils;
import bfx.utils.compression.CompressionUtils;

public class SequenceFormats {
	private static Logger log = Logger.getLogger(SequenceFormats.class);
	
	private static Map<String,SequenceReader> extension2reader = new HashMap<String,SequenceReader>();
	private static Map<String,SequenceWriter> extension2writer = new HashMap<String,SequenceWriter>();
			
	private static ServiceLoader<SequenceReader> sequenceReaderLoader = ServiceLoader.load(SequenceReader.class);
	private static ServiceLoader<SequenceWriter> sequenceWriterLoader = ServiceLoader.load(SequenceWriter.class);
	
	static {
		for(SequenceReader reader: sequenceReaderLoader) {
			log.debug("Registering sequence reader: " + reader.getClass().getName());
			for(String ext: reader.getPreferedExtensions()) {
				extension2reader.put(ext,reader);
			}
		}
		
		for(SequenceWriter writer: sequenceWriterLoader) {
			log.debug("Registering sequence writer: " + writer.getClass().getName());
			for(String ext: writer.getPreferedExtensions()) {
				extension2writer.put(ext,writer);
			}
		}		
	}

	public static SequenceReader getReader(String filename,String formatName) {
		if (formatName == null) {
			return getReader(filename);
		}
		SequenceReader reader = extension2reader.get(formatName);
		log.info("Using SequenceReader: " + reader.getClass().getName());
		return reader;
	}
	
	public static SequenceReader getReader(String filename) {
		log.debug("Getting reader for file: " + filename);
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		if (!extension2reader.containsKey(ext)) {
			log.debug("Extensions: " + BFXMapUtils.toString(extension2reader));
			throw new RuntimeException(String.format("Unknown file format '%s' for file '%s': Could not create an appropriate sequence reader.",ext,filename));
		}
		SequenceReader reader = extension2reader.get(ext);
		log.info("Using SequenceReader: " + reader.getClass().getName());
		return reader;
	}

	public static SequenceWriter getWriter(String filename) {
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		return getWriter(filename,ext);
	}
	
	public static SequenceWriter getWriter(String filename, String format) {
		log.debug("Getting writer for file: " + filename);
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		if (!extension2reader.containsKey(ext)) {
			log.debug("Extensions: " + BFXMapUtils.toString(extension2writer));
			throw new RuntimeException(String.format("Unknown file format '%s' for file '%s': Could not create an appropriate sequence writer.",ext,filename));
		}
		SequenceWriter writer = extension2writer.get(ext);
		log.info("Using SequenceReader: " + writer.getClass().getName());
		return writer;
	}
}
