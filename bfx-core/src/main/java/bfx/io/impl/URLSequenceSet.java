package bfx.io.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import bfx.Sequence;
import bfx.SequenceSet;
import bfx.exceptions.MultipleURLsProcessingRuntimeException;
import bfx.exceptions.URLProcessingRuntimeException;
import bfx.io.SequenceFormats;
import bfx.io.SequenceReader;

public class URLSequenceSet extends SequenceSet {

	private URL url1;
	private URL url2;
	private SequenceReader reader;
	
	public URLSequenceSet(String format,URL url1) {
		this.url1 = url1;
		this.url2 = null;
		reader = SequenceFormats.getReader(url1.getPath(), format);
	}

	public URLSequenceSet(String format,URL url1,URL url2) {
		this.url1 = url1;
		this.url2 = url2;
		reader = SequenceFormats.getReader(url1.getPath(), format);
	}

	public URLSequenceSet(URL url1,URL url2) {
		this.url1 = url1;
		this.url2 = url2;
		reader = SequenceFormats.getReader(url1.getPath());
	}

	public URLSequenceSet(URL url1) {
		this.url1 = url1;
		this.url2 = null;
		reader = SequenceFormats.getReader(url1.getPath());
	}
	
	public URLSequenceSet(String url1,String url2) throws MalformedURLException {
		this(new URL(url1),new URL(url2));
	}
	
	@Override
	public Iterator<Sequence> iterator() {
		try {
			if (url2 == null)
				return reader.read(url1);
			else
				return reader.read(url1,url2);				
		} catch(IOException e) {
			if (url2==null)
				throw new URLProcessingRuntimeException(e,url1);
			else
				throw new MultipleURLsProcessingRuntimeException(e,url1,url2);
		}
	}

}
