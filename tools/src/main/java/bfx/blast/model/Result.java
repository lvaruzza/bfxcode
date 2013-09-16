package bfx.blast.model;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/*
 * <Iteration_iter-num>1</Iteration_iter-num>
 * <Iteration_query-ID>Query_1</Iteration_query-ID>
 * <Iteration_query-def>pd10_c1</Iteration_query-def>
 * <Iteration_query-len>4545</Iteration_query-len>
 * <Iteration_hits>	
*/
@XmlRootElement(name = "Iteration")
public class Result {

	private int iterationNum;
	private String queryId;
	private String queryDef;
	private int queryLen;
	
	
	private Collection<Hit> hits;
	
	@XmlElement(name = "Iteration_iter-num")
	public int getIterationNum() {
		return iterationNum;
	}
	
	public void setIterationNum(int iterationNum) {
		this.iterationNum = iterationNum;
	}
	
	@XmlElement(name = "Iteration_query-ID")
	public String getQueryId() {
		return queryId;
	}
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
	@XmlElement(name = "Iteration_query-def")
	public String getQueryDef() {
		return queryDef;
	}
	public void setQueryDef(String queryDef) {
		this.queryDef = queryDef;
	}
	
	@XmlElement(name = "Iteration_query-len")
	public int getQueryLen() {
		return queryLen;
	}
	public void setQueryLen(int queryLen) {
		this.queryLen = queryLen;
	}

	
	@XmlElementWrapper(name = "Iteration_hits")
	@XmlElement(name = "Hit")
	public Collection<Hit> getHits() {
		return hits;
	}

	public void setHits(Collection<Hit> hits) {
		this.hits = hits;
	}

	@Override
	public String toString() {
		return "Blast Result [iterationNum=" + iterationNum + ", queryId="
				+ queryId + ", queryDef=" + queryDef + ", queryLen=" + queryLen
				+ "]";
	}
	
  
}
