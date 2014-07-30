package bio.blast.model.scala

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Iteration")
class ResultScala {
	 var iterationNum:Int = _
	 var queryId:String = _	 
	 var queryDef:String = _
	 var queryLen:Int = _
	 
	 @XmlElement(name = "Iteration_iter-num") 
	 def getIterationNum = iterationNum
	 def setIterationNum(x:Int) { iterationNum = x}
	 
	 @XmlElement(name = "Iteration_query-ID") 
	 def getQueryId = queryId
	 def setQueryId(x:String) { queryId = x}
	 
	 @XmlElement(name = "Iteration_query-def") 
	 def getQueryDef = queryDef
	 def setQueryDef(x:String) { queryDef = x}
	 
	 @XmlElement(name = "Iteration_query-len") 
	 def getQueryLen = queryLen
	 def setQueryLen(x:Int) {queryLen = x}
	 
	 override def toString:String = "Blast Result [name=" + queryId + "]"
}