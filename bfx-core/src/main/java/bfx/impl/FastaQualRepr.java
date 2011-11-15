package bfx.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;


/*
 * Encode and decode quality values in the Qual format (used in fasta/qual pair)
 * 
 */
public class FastaQualRepr extends AbstractQualRepr {

	@Override
	public byte[] qualToTextBytes(byte[] qual,int off,int len) {
		ByteArrayOutputStream buff = new ByteArrayOutputStream(qual.length*3);
		PrintWriter pr = new PrintWriter(buff);
		for(int i=0;i<len;i++) {
			pr.print(qual[off+i]);
			//System.out.println(qual[off+i]);
			if (i!=len-1)pr.print(' ');
		}
		pr.close();
		byte[] out = buff.toByteArray();
		//System.out.println(new String(out));
		return out;
	}
	
	@Override
	public byte[] textToQual(byte[] repr) {
		return textToQual(new String(repr));
	}


	public byte[] textToQual(byte[] repr, int off, int len) {
		return textToQual(new String(Arrays.copyOfRange(repr, off, off+len)));
	}

	
	@Override
	public byte[] textToQual(String qual) {
		String[] vals=qual.trim().split("\\s+");
		byte[] out = new byte[vals.length];
		
		for(int i=0;i<vals.length;i++) {
			out[i] = Byte.parseByte(vals[i]);
 		}
		
		return out;
	}
}
