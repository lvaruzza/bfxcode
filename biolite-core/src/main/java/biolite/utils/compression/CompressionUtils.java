package biolite.utils.compression;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtils {
	static private InputStream openInputStream0(String filename) throws IOException {
		return new FileInputStream(filename);
	}
	
	static public InputStream openInputStream(String filename) throws IOException {
		if (filename.endsWith(".gz")) {
			return new GZIPInputStream(openInputStream0(filename));
		} else {
			return openInputStream0(filename);
		}
	}

	static private OutputStream openOutputStream0(String filename) throws IOException {
		return new FileOutputStream(filename);
	}
	
	static public OutputStream openOutputStream(String filename) throws IOException {
		if (filename.endsWith(".gz")) {
			return new GZIPOutputStream(openOutputStream0(filename));
		} else {
			return openOutputStream0(filename);
		}
	}

	
}
