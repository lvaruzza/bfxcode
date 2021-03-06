package bfx.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.Sequence;
import bfx.io.SequenceReader;
import bfx.utils.compression.CompressionUtils;

/**
 * SequenceReader for fastaFile
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class FastaSequenceReader extends SequenceReader {
	private static Logger log = LoggerFactory.getLogger(FastaSequenceReader.class);
	
	private byte defaultQuality = 0;
	
	public FastaSequenceReader() {};
	public FastaSequenceReader(byte defaultQuality) {
			this.defaultQuality = defaultQuality;
	};
	
	@Override
	public Iterator<Sequence> read(InputStream fastaInput) throws IOException {
		log.info("progressCounter = " + pc);
		return new LineBasedFastaIterator(fastaInput,defaultQuality,pc);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader) throws IOException {
		log.info("progressCounter = " + pc);
		return new LineBasedFastaIterator(fastaReader,defaultQuality,pc);
	}

	@Override
	public Iterator<Sequence> read(File file1,File file2) throws IOException{
		if(file1.equals(file2)) {
			throw new IOException(String.format("Sequence file '%s', should be different from qual file '%s'",file1,file2));
		}
		// TODO: Test the file names 
		return super.read(file1,file2);
	}
	
	@Override
	public Iterator<Sequence> read(String filename1,String filename2) throws IOException{
		if(filename1.equals(filename2)) {
			throw new IOException(String.format("Sequence file '%s', should be different from qual file '%s'",filename1,filename2));
		}
			
		//String ext1 = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename1));
		String ext2 = FilenameUtils.getExtension(CompressionUtils.uncompressedFilename(filename1));
		if (!ext2.equals("qual"))
			log.warn(String.format("Suspicious extension in qual file: '%s'. Fasta qual file normally has '.qual' extension.",ext2));
		return super.read(filename1,filename2);
	}
	
	@Override
	public Iterator<Sequence> read(InputStream fastaInput, InputStream qualInput)
			throws IOException {
		if (qualInput != null)
			return new LineBasedFastaQualIterator(fastaInput,qualInput,pc);
		else
			return read(fastaInput);
	}

	@Override
	public Iterator<Sequence> read(Reader fastaReader, Reader qualReader)
			throws IOException {
		if (qualReader != null)
			return new LineBasedFastaQualIterator(fastaReader,qualReader,pc);
		else
			return read(fastaReader);
	}
		
	@Override
	public String getFormatName() {
		return "fasta";
	}	
}
