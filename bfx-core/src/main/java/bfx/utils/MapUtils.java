package bfx.utils;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MapUtils {
	
	/*
	 * String representation of a map
	 * 
	 */
	public static String toString(Map<?,?> map) {
		StringBuilder buff = new StringBuilder();
		buff.append('{');
		Set<? extends Entry<?,?>> es = map.entrySet();
		int count = es.size();
		for(Entry<?,?> e: es) {
			buff.append(e.getKey().toString());
			buff.append(':');
			buff.append(e.getValue().toString());
			if (count-- > 1) buff.append(", ");
		}
		buff.append('}');
		return buff.toString();
	}
	
	/*
	 * Convert a String into a map
	 * 
	 * Splits the records by recordSep and splits the key-value pair 
	 * by valueSep
	 * 
	 * Not very robust
	 * 
	 */
	public static Map<String,String> stringToMap(String repr,
												 String recordSep,
												 String valueSep,
												 String prefix,
												 String suffix) {
		Map<String,String> map = new HashMap<String,String>();
		
		String x = repr.replaceAll("^"+prefix, "")
		               .replaceAll(suffix+"$", "");
		
		String[] pairs = x.split(recordSep);
		for(String p: pairs) {
			String[] kv = p.split(valueSep);
			map.put(kv[0], kv[1]);
		}
		return map;
	}
	
	public static Map<String,String> build(String... args) {
		Map<String,String> r = new HashMap<String,String>();
		if (args.length % 2 != 0)
			throw new RuntimeException(String.format("Invalid number of arguments: %d. The number of arguments should be even.",args.length));
		for(int i=0;i<args.length;i+=2) {
			r.put(args[i], args[i+1]);
		}
		return r;
	}
}
