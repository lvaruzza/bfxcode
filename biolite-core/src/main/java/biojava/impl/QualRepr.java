package biojava.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;


/*
 * Encode and decode quality values in the Qual format (used in fasta/qual pair)
 * 
 */
public class QualRepr extends AbstractQualRepr {

	@Override
	public byte[] qualToBytes(byte[] qual) {
		ByteArrayOutputStream buff = new ByteArrayOutputStream(qual.length*3);
		PrintWriter out = new PrintWriter(buff);
		int i = qual.length;
		for(byte b: qual) {
			out.print(b);
			if (i-- > 0)out.print(' ');
		}
		return buff.toByteArray();
	}

	@Override
	public byte[] bytesToQual(byte[] repr) {
		return stringToQual(new String(repr));
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
