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
		byte [] tmp=new byte[3];
		byte[] r = new byte[repr.length/2+1];
		int k=0;
		int j=0;
		for(int i=0;i<repr.length;i++) {
			if(repr[i] == ' ' || repr[i] == '\t' || repr[i] == '\n') {
				switch(j) {
				case 0:
					continue;
				case 1:
					r[k++] = tmp[0];
					break;
				case 2:
					r[k++]=(byte)(tmp[0]*10 + tmp[1]);
					tmp[1] = (byte)0;
					break;
				case 3:
					r[k++]=(byte)(tmp[0]*100 + tmp[1]*10 + tmp[2]);
					tmp[1] = (byte)0;
					tmp[2] = (byte)0;
					break;
				default:
					throw new RuntimeException("j="+j);
				}
				//if (k!=0) System.out.println(String.format("# i=%d, k=%d, tmp='%s', r[k]=%d",i,k,Arrays.toString(tmp),r[k-1]));
				j=0;
			} else if (repr[i] >= '0' && repr[i] <= '9'){
				tmp[j++] = (byte)(repr[i]-'0');
				//System.out.println(String.format("i=%d, repr[i]='%d', tmp='%s'",i,repr[i],Arrays.toString(tmp)));
			}
		}
		switch(j) {
		case 0: break;
		case 1:
			r[k++] = tmp[0];
			break;
		case 2:
			r[k++]=(byte)(tmp[0]*10 + tmp[1]);
			tmp[1] = (byte)0;
			break;
		case 3:
			r[k++]=(byte)(tmp[0]*100 + tmp[1]*10 + tmp[2]);
			tmp[1] = (byte)0;
			tmp[2] = (byte)0;
			break;
		default:
			throw new RuntimeException("j="+j);
		}
		return Arrays.copyOfRange(r, 0, k);
	}


	public byte[] textToQual(byte[] repr, int off, int len) {
		return textToQual(new String(Arrays.copyOfRange(repr, off, off+len)));
	}

	
	@Override
	public byte[] textToQual(String qual) {
		return textToQual(qual.getBytes());
	}
}
