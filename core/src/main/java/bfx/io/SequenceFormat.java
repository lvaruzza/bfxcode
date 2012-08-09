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
abstract public class SequenceFormat {
	private static Logger log = LoggerFactory.getLogger(SequenceFormat.class);
	
	private static Map<String,SequenceFormat> extension2format = new HashMap<String,SequenceFormat>();
			
	private static ServiceLoader<SequenceFormat> sequenceFormats = ServiceLoader.load(SequenceFormat.class);
	
	static {
		for(SequenceFormat fmt: sequenceFormats) {
			log.debug("Registering sequence format: " + fmt.getClass().getName());
			for(String ext: fmt.getPreferredExtesionsList()) {
				extension2format.put(ext,fmt);
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
		if (!extension2format.containsKey(formatName)) {
			throw new RuntimeException(String.format("Unknown file format '%s' : Could not create an appropriate sequence reader.",formatName));
		}
		SequenceFormat fmt = extension2format.get(formatName);
		if (fmt.getReader() == null) {
			throw new RuntimeException(String.format("Format %s does not have a reader", fmt.getName()));
		}  else {
			return fmt.getReader();
		}
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
		if (!extension2format.containsKey(formatName)) {
			throw new RuntimeException(String.format("Unknown file format '%s': Could not create an appropriate sequence writer.",formatName));
		}
		SequenceFormat fmt = extension2format.get(formatName);
		if (fmt.getWriter() == null) {
			throw new RuntimeException(String.format("Format %s does not have a writer", fmt.getName()));
		}  else {
			return fmt.getWriter();
		}
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

	public static SequenceFormat getFormat(String extension) {
		if (!extension2format.containsKey(extension)) {
			throw new RuntimeException(String.format("Unknown sequence format for extension '%s'",extension));
		}
		return extension2format.get(extension);
	}
	
	public static SequenceFormat getFormatForFile(String filename) {
		String ext = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename));
		return getFormat(ext);
	}
	
	
	abstract public SequenceReader getReader();
	abstract public SequenceWriter getWriter();
	abstract public String getName();
	
	/**
	 * Return a list of life extensions associated with this SequenceReader.
	 *   
	 * @return List of file extensions.
	 */
	abstract public String[] getPreferredExtesionsList();
	abstract public String getPreferredExtesion();

	
	
}
