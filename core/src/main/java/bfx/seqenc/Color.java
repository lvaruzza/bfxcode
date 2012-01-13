package bfx.seqenc;

import bfx.Sequence;



/**
 * Code for manipulating and converting color-encododed DNA sequencies.
 * 
 * 
 * The dibase to color convertion table is:
 * 
 *   A C G T N
 * A 0 1 2 3 4
 * C 1 0 3 2 4
 * G 2 3 0 1 4
 * T 3 2 1 0 4
 * N 4 4 4 4 4
 * 
 * More info about color encoding:
 * 
 * http://www3.appliedbiosystems.com/cms/groups/mcb_marketing/documents/generaldocuments/cms_058265.pdf
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class Color {
	//private static Logger log = Logger.getLogger(Color.class);
	
	// Convert byte to index in [0,4] range
	private static byte[] enc = new byte[] {
		4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 0, 4, 1, 4, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 1, 
        4, 4, 4, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4};

	// convert index to bases
	public static byte[] bases = new byte[] {'A','C','G','T','N'};
	// convert index to colors
	public static byte[] colors = new byte[] {'0','1','2','3','.'};
	
	/**
	 * 
	 * Color encoding matrix (is the same for encoding and decoding)
	 * 
	 */
	private static byte[][] mat = new byte[][] 
									{{0,1,2,3,4},
				        	 		 {1,0,3,2,4},
				        	 		 {2,3,0,1,4},
				        	 		 {3,2,1,0,4},
				        	 		 {4,4,4,4,4}};
	
	public static byte naiveColorDecodeBase(byte a,byte b) {
		return bases[mat[enc[a]][enc[b]]];
	}

	public static byte naiveColorEncodeColor(byte a,byte b) {
		return colors[mat[enc[a]][enc[b]]];
	}

	private static byte[] naiveColorDecode(byte[] seq,int start,int length) {
		byte[] r = new byte[length];
		r[0]=seq[start];
		for(int i=start+1,j=1;i<start+length;i++,j++) {
			r[j] = naiveColorDecodeBase(r[j-1],seq[i]);
		}
		return r;
	}

	private static byte[] naiveColorEncode(byte[] seq,int start,int length) {
		byte[] r = new byte[length];
		r[0]=seq[start];
		for(int i=start+1,j=1;i<start+length;i++,j++) {
			//log.debug(String.format("i=%d j=%d",i,j));
			r[j] = naiveColorEncodeColor(seq[i-1],seq[i]);
		}
		return r;
	}
	
	/**
	 * Convert a byte array in color space to base space
	 * 
	 * @param seq  byte array with color space sequence
	 * @return base space byte array
	 */
	public static byte[] colorDecode(byte[] seq) {
		return naiveColorDecode(seq,0,seq.length);
	}

	/**
	 * Convert a byte array in color space to base space
	 * 
	 * @param seq  byte array with color space sequence
	 * @param start start position
	 * @param length length of sequence to convert 
	 * @return base space byte array
	 */
	public static byte[] colorDecode(byte[] seq,int start,int length) {
		return naiveColorDecode(seq,start,length);
	}
	
	/**
	 * Convert a byte array in base space to color space
	 * 
	 * @param seq  byte array with base space sequence
	 * @return color encoded byte array
	 */
	public static byte[] colorEncode(byte[] seq) {
		return naiveColorEncode(seq,0,seq.length);
	}

	/**
	 * Convert a byte array in base space to color space
	 * 
	 * @param seq  byte array with base space sequence
	 * @param start start position
	 * @param length length of sequence to convert 
	 * @return color encoded byte array
	 */
	public static byte[] colorEncode(byte[] seq,int start,int length) {
		return naiveColorEncode(seq,start,length);
	}
	
	
	/**
	 * Convert from color to base-space
	 * 
	 * @param seq Color space sequence
	 * @return Base space sequence
	 */
	public static Sequence colorDecode(Sequence seq) {
		byte[] s = seq.getSeq();
		return seq.changeSeq(colorDecode(s));
	}
	
	/**
	 * Convert from base to color space
	 * 
	 * @param seq base space sequence
	 * @return color encoded sequence
	 */
	public static Sequence colorEncode(Sequence seq) {
		byte[] s = seq.getSeq();
		return seq.changeSeq(colorEncode(s));
	}
}	
