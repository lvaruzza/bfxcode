package bfx.utils.compression;

import java.io.File;
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

	static private InputStream openInputStream0(File file) throws IOException {
		return new FileInputStream(file);
	}
	
	static public InputStream openInputStream(File file) throws IOException {
		if (file.getName().endsWith(".gz")) {
			return new GZIPInputStream(openInputStream0(file));
		} else {
			return openInputStream0(file);
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

	static private OutputStream openOutputStream0(File file) throws IOException {
		return new FileOutputStream(file);
	}
	
	static public OutputStream openOutputStream(File file) throws IOException {
		if (file.getName().endsWith(".gz")) {
			return new GZIPOutputStream(openOutputStream0(file));
		} else {
			return openOutputStream0(file);
		}
	}
	
}
