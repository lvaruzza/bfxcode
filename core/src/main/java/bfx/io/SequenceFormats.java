package bfx.io;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.utils.BFXMapUtils;
import bfx.utils.compression.CompressionUtils;

/**
 * List of available Sequence Formats Readers and Writers
 * 
 * Each Sequence Format Reader should register itself using the ServiceLoader facility. 
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class SequenceFormats {
	private static Logger log = LoggerFactory.getLogger(SequenceFormats.class);
	
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

	
	/**
	 * Return the appropriate SequenceReader
	 * 
	 * @param formatName Name of the format (which is just the extension for the format like fasta, fastq, etc)
	 * @return SequenceReader for formatName
	 */
	public static SequenceReader getReader(String  formatName) {
		assert(formatName!=null);
		if (!extension2reader.containsKey(formatName)) {
			log.debug("Extensions: " + BFXMapUtils.toString(extension2reader));
			throw new RuntimeException(String.format("Unknown file format '%s' : Could not create an appropriate sequence reader.",formatName));
		}
		return extension2reader.get(formatName);
	}
	
	/**
	 * Return the SequenceReader for formatName, or if formatName is null, try to find
	 * a SequenceReader based on the filenameExtension.
	 * 
	 * @param filename   Name of the file which will be read
	 * @param formatName Name of the format (which is just the extension for the format like fasta, fastq, etc)
	 * @return SequenceReader for formatName
	 */
	public static SequenceReader getReaderForFile(String filename,String formatName) {
		if (formatName == null) {
			return getReaderForFile(filename);
		}
		SequenceReader reader = getReader(formatName);
		log.info("Using SequenceReader: " + reader.getClass().getName());
		return reader;
	}
	
	/**
	 * Return the SequenceReader based on filename extension.
	 * 
	 * @param filename   Name of the file which will be read
	 * @return SequenceReader for filename.
	 */
	public static SequenceReader getReaderForFile(String filename) {
		log.debug("Getting reader for file: " + filename);
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		SequenceReader reader = getReader(ext);
		log.info("Using SequenceReader: " + reader.getClass().getName());
		return reader;
	}

	/**
	 * Return the appropriate SequenceWriter
	 * 
	 * @param formatName Name of the format (which is just the extension for the format like fasta, fastq, etc)
	 * @return SequenceWriter for formatName
	 */
	public static SequenceWriter getWriter(String formatName) {
		if (!extension2reader.containsKey(formatName)) {
			log.debug("Extensions: " + BFXMapUtils.toString(extension2writer));
			throw new RuntimeException(String.format("Unknown file format '%s': Could not create an appropriate sequence writer.",formatName));
		}
		return extension2writer.get(formatName);
	}
	
	/**
	 * Return the SequenceWriter based on filename extension.
	 * 
	 * @param filename   Name of the file which will be read
	 * @return SequenceWriter for filename.
	 */
	public static SequenceWriter getWriterForFile(String filename) {
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		return getWriterForFile(filename,ext);
	}
	
	/**
	 * Return the SequenceWriter for formatName, or if formatName is null, try to find
	 * a SequenceWriter based on the filenameExtension.
	 * 
	 * @param filename   Name of the file which will be read
	 * @param formatName Name of the format (which is just the extension for the format like fasta, fastq, etc)
	 * @return SequenceWriter for formatName
	 */
	public static SequenceWriter getWriterForFile(String filename, String formatName) {
		log.debug("Getting writer for file: " + filename);
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		SequenceWriter writer = getWriter(ext);
		log.info("Using SequenceReader: " + writer.getClass().getName());
		return writer;
	}
}
