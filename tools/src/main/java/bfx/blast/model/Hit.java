package bfx.blast.model;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;



/*
 *        <Hit_num>1</Hit_num>
 *        <Hit_id>gi|386326719|ref|NC_017573.1|</Hit_id>
 *        <Hit_def>Serratia sp. AS13 chromosome, complete genome</Hit_def>
 *        <Hit_accession>NC_017573</Hit_accession>
 *        <Hit_len>5442549</Hit_len>
 *        <Hit_hsps>
 */

@XmlRootElement(name = "Hit")
public class Hit {
	private int hitNum;
	private String id;
	private String def;
	private String accession;
	private int len;
	private Collection<HSP> hsps;
	
	@XmlElement(name = "Hit_num")
	public int getHitNum() {
		return hitNum;
	}
	public void setHitNum(int hitNum) {
		this.hitNum = hitNum;
	}
	
	
	@XmlElement(name = "Hit_id")
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "Hit_def")
	public String getDef() {
		return def;
	}
	public void setDef(String def) {
		this.def = def;
	}

	@XmlElement(name = "Hit_accession")
	public String getAccession() {
		return accession;
	}
	public void setAccession(String accession) {
		this.accession = accession;
	}

	@XmlElement(name = "Hit_len")
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	
	
	@XmlElementWrapper(name = "Hit_hsps")
	@XmlElement(name = "Hsp")
	public Collection<HSP> getHsps() {
		return hsps;
	}
	public void setHsps(Collection<HSP> hsps) {
		this.hsps = hsps;
	}
	
	@Override
	public String toString() {
		return "Hit [num=" + hitNum + ", id=" + id + ", def=" + def
				+ ", accession=" + accession + ", len=" + len + "]";
	}
	
	
}
