package biojava.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;


/*
 * Encode and decode quality values in the Qual format (used in fasta/qual pair)
 * 
 */
public class QualRepr extends AbstractQualRepr {

	@Override
	public byte[] qualToBytes(byte[] qual,int off,int len) {
		ByteArrayOutputStream buff = new ByteArrayOutputStream(qual.length*3);
		PrintWriter out = new PrintWriter(buff);
		for(int i=0;i<len;i++) {
			out.print(qual[off+i]);
			if (i!=len-1)out.print(' ');
		}
		return buff.toByteArray();
	}
	
	@Override
	public byte[] bytesToQual(byte[] repr) {
		return stringToQual(new String(repr));
	}


	public byte[] bytesToQual(byte[] repr, int off, int len) {
		return stringToQual(new String(Arrays.copyOfRange(repr, off, off+len)));
	}

	
	@Override
	public byte[] stringToQual(String qual) {
		String[] vals=qual.trim().split("\\s+");
		byte[] out = new byte[vals.length];
		
		for(int i=0;i<vals.length;i++) {
			out[i] = Byte.parseByte(vals[i]);
 		}
		
		return out;
	}
}
