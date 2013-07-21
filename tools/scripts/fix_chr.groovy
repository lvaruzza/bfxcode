#!/usr/bin/env groovy

def  map = [ "1" : "chr1"]

for(i in 1..22) { 
  map["${i}"] = "chr${i}";
}
map["Y"]="chrY"
map["X"]="chrX"
map["MT"]="chrM"

System.in.eachLine {  line ->
  def lst = (line.split("\t") as List)
  if (lst.size() > 0) { 
    try { 
      if (map.containsKey(lst[0])) { 
	lst[0] = map[lst[0]]
      }
    } catch(Exception e) { 
      System.err.println("lst = ${lst}")
      System.err.println(e)
    }
    println(lst.join("\t"))
  }
}