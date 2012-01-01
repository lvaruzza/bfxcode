package bfx.utils.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.log4j.Logger;

public class CompressionUtils {
	private static Logger log = Logger.getLogger(CompressionUtils.class);
	
	// TODO: improve extension handling. Test extensions against a list of 
	// known extensions
	
	/*private static Map<String,String> extensions = new HashMap<String,String>();
	
	static {
		extensions.put(".gz","gzip");
		extensions.put(".bz2","bzip2");
	}*/
	
	static private InputStream openInputStream0(String filename) throws IOException {
		return new FileInputStream(filename);
	}
	
	static public InputStream openInputStream(String filename) throws IOException {
		if (filename.endsWith(".gz")) {
			log.debug(String.format("Openning GZIP file '%s'",filename));
			return new GZIPInputStream(openInputStream0(filename));
		} else if(filename.endsWith(".bz2")) {
			log.debug(String.format("Openning BZIP file '%s'",filename));
			return new BZip2CompressorInputStream(openInputStream0(filename));
		} else {
			log.debug(String.format("Openning file '%s'",filename));
			return openInputStream0(filename);
		}
	}

	static private InputStream openInputStream0(File file) throws IOException {
		return new FileInputStream(file);
	}
	
	static public InputStream openInputStream(File file) throws IOException {
		if (file.getName().endsWith(".gz")) {
			log.debug(String.format("Openning GZIP file '%s'",file.getName()));
			return new GZIPInputStream(openInputStream0(file));
		} else if (file.getName().endsWith(".bz2")) {
			log.debug(String.format("Openning BZIP file '%s'",file.getName()));
				return new BZip2CompressorInputStream(openInputStream0(file));
		} else {
			log.debug(String.format("Openning file '%s'",file.getName()));
			return openInputStream0(file);
		}
	}
	
	
	static private OutputStream openOutputStream0(String filename) throws IOException {
		return new FileOutputStream(filename);
	}
	
	static public OutputStream openOutputStream(String filename) throws IOException {
		if (filename.endsWith(".gz")) {
			return new GZIPOutputStream(openOutputStream0(filename));
		} else if(filename.endsWith(".bz2")) {
			return new BZip2CompressorOutputStream(openOutputStream0(filename));
		} else {
			return openOutputStream0(filename);
		}
	}

	static private OutputStream openOutputStream0(File file) throws IOException {
		return new FileOutputStream(file);
	}
	
	static public OutputStream openOutputStream(File file) throws IOException {
		if (file.getName().endsWith(".gz")) {
			return new GZIPOutputStream(openOutputStream0(file));
		} else if(file.getName().endsWith(".bz2")) {
			return new BZip2CompressorOutputStream(openOutputStream0(file));
		} else {
			return openOutputStream0(file);
		}
	}
	
	/*
	 * Remove the compression extension from a filename or return the same same.
	 * 
	 */
	static public String uncompressedFilename(String filename) {
		if (filename.endsWith(".gz")) {
			return filename.replaceAll(".gz$", "");
		} else if(filename.endsWith(".bz2")) {
			return filename.replaceAll(".bz2$", "");			
		} else {
			return filename;
		}
	}
}
