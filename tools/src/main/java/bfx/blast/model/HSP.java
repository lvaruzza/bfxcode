package bfx.blast.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/* <Hsp_num>1</Hsp_num>
 * <Hsp_bit-score>122.999</Hsp_bit-score>
 * <Hsp_score>66</Hsp_score>
 * <Hsp_evalue>6.8738e-23</Hsp_evalue>
 * <Hsp_query-from>3680</Hsp_query-from>
 * <Hsp_query-to>3850</Hsp_query-to>
 * <Hsp_hit-from>3293218</Hsp_hit-from>
 * <Hsp_hit-to>3293048</Hsp_hit-to>
 * <Hsp_query-frame>1</Hsp_query-frame>
 * <Hsp_hit-frame>-1</Hsp_hit-frame>
 * <Hsp_identity>140</Hsp_identity>
 * <Hsp_positive>140</Hsp_positive>
 * <Hsp_gaps>8</Hsp_gaps>
 * <Hsp_align-len>175</Hsp_align-len>
 *    <Hsp_qseq>ATATGGTGATGGGCCCATTCCGCCTGCCCGCCTTCGCTGAGATCGAAAGGC--AGCTCGCCGGTCAGCAACTCGT-AAAGCACAATGCCAAGGCTATAGAGATCGCTGCGGCTGTCGACCC-TGTCCGGCGTACGGGAGGTGTGCTCCGGCGACATATACGCCAGCGTACCGCCA</Hsp_qseq>
 *    <Hsp_hseq>ATATGGTGATGCGTCCACTCCTCCAGGCCACCTTCGCTGAGGTCGAAGGGCAAAGAT--CCGGTCAGCAGTTCATAAAAG-ACGATGCCAAGGCTATAGAGATCGCTGCGGCTGTCCACCGATCTTTG-CGTGCGCGAAGTGTGTTCGGGCGACATATAGGCGAGCGTGCCGCCA</Hsp_hseq>
 * <Hsp_midline>||||||||||| | ||| ||| || | || ||||||||||| ||||| |||  || |  ||||||||||  || | |||| || |||||||||||||||||||||||||||||||| |||  | |  | ||| || || ||||| || ||||||||||| || ||||| ||||||</Hsp_midline>
*/
//@XmlRootElement(name = "Hsp")
public class HSP {
	private int hspNum;
	private double bitScore;
	private int score;
	private int queryFrom;
	private int queryTo;
	private int hitFrom;
	private int hitTo;
	private byte queryFrame;
	private byte hitFrame;
	private int identity;
	private int positive;
	private int gaps;
	private int alignLen;
	private String qseq;
	private String hseq;
	private String midline;
	
	
	@XmlElement(name = "Hsp_num")
	public int getHspNum() {
		return hspNum;
	}
	public void setHspNum(int hspNum) {
		this.hspNum = hspNum;
	}
	
	
	@XmlElement(name = "Hsp_bit-score")
	public double getBitScore() {
		return bitScore;
	}
	public void setBitScore(double bitScore) {
		this.bitScore = bitScore;
	}

	@XmlElement(name = "Hsp_score")
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

	@XmlElement(name = "Hsp_query-from")
	public int getQueryFrom() {
		return queryFrom;
	}
	
	public void setQueryFrom(int queryFrom) {
		this.queryFrom = queryFrom;
	}
	
	@XmlElement(name = "Hsp_query-to")
	public int getQueryTo() {
		return queryTo;
	}

	public void setQueryTo(int queryTo) {
		this.queryTo = queryTo;
	}

	@XmlElement(name = "Hsp_query-from")
	public int getHitFrom() {
		return hitFrom;
	}
	
	public void setHitFrom(int hitFrom) {
		this.hitFrom = hitFrom;
	}
	
	@XmlElement(name = "Hsp_hit-to")
	public int getHitTo() {
		return hitTo;
	}
	public void setHitTo(int hitTo) {
		this.hitTo = hitTo;
	}
	
	@XmlElement(name = "Hsp_query-frame")
	public byte getQueryFrame() {
		return queryFrame;
	}
	public void setQueryFrame(byte queryFrame) {
		this.queryFrame = queryFrame;
	}
	
	@XmlElement(name = "Hsp_hit-frame")
	public byte getHitFrame() {
		return hitFrame;
	}
	public void setHitFrame(byte hitFrame) {
		this.hitFrame = hitFrame;
	}
	
	@XmlElement(name = "Hsp_identity")
	public int getIdentity() {
		return identity;
	}
	public void setIdentity(int identity) {
		this.identity = identity;
	}
	
	@XmlElement(name = "Hsp_positive")
	public int getPositive() {
		return positive;
	}
	public void setPositive(int positive) {
		this.positive = positive;
	}
	
	@XmlElement(name = "Hsp_gaps")
	public int getGaps() {
		return gaps;
	}
	public void setGaps(int gaps) {
		this.gaps = gaps;
	}
	
	@XmlElement(name = "Hsp_align-len")
	public int getAlignLen() {
		return alignLen;
	}
	public void setAlignLen(int alignLen) {
		this.alignLen = alignLen;
	}
	
	@XmlElement(name = "Hsp_qseq")
	public String getQseq() {
		return qseq;
	}
	public void setQseq(String qseq) {
		this.qseq = qseq;
	}
	
	@XmlElement(name = "Hsp_hseq")
	public String getHseq() {
		return hseq;
	}
	public void setHseq(String hseq) {
		this.hseq = hseq;
	}
	
	@XmlElement(name = "Hsp_midline")
	public String getMidline() {
		return midline;
	}
	public void setMidline(String midline) {
		this.midline = midline;
	}
	@Override
	public String toString() {
		return "HSP [hspNum=" + hspNum + ", bitScore=" + bitScore + ", score="
				+ score + ", queryFrom=" + queryFrom + ", queryTo=" + queryTo
				+ ", hitFrom=" + hitFrom + ", hitTo=" + hitTo + ", queryFrame="
				+ queryFrame + ", hitFrame=" + hitFrame + ", identity="
				+ identity + ", positive=" + positive + ", gaps=" + gaps
				+ ", alignLen=" + alignLen + "]";
	}
	
}
