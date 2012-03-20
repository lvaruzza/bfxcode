package bfx.io.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import bfx.GFF;
import bfx.io.GFFReader;
import bfx.io.GFFSource;

public class FileGFFSource extends GFFSource {
	private File file;
	private GFFReader reader;
	
	public FileGFFSource(File file) {
		this.file = file;
		reader = new LineBasedGFFReader();
	}
	
	@Override
	public Iterator<GFF> iterator() {
		try {
			return reader.read(file);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

}
