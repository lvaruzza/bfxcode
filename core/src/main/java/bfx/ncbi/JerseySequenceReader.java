package bfx.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import bfx.io.SequenceSource;
import bfx.io.impl.StreamSequenceSource;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;

public class JerseySequenceReader implements MessageBodyReader<SequenceSource> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		System.out.println(type.getName());
		return type.getName().equals("bfx.io.SequenceSource");
	}

	private FileBackedOutputStream out = new FileBackedOutputStream(512*1024,true);

	@Override
	public SequenceSource readFrom(Class<SequenceSource> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		ByteStreams.copy(entityStream, out);
		return new StreamSequenceSource("fasta",out.asByteSource().openStream());
	}
	

}
