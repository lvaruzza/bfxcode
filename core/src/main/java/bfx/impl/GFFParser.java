package bfx.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bfx.GFF;

public class GFFParser {
	//static private Logger log = Logger.getLogger(AbstractGFFReader.class);
	
	static public GFF parseGFF3(String line) throws InvalidFormatException {
		Map<String,String> attrs;
		char strand;
		long start;
		long end;
		byte phase;
		double score;
		
		String[] fields = line.split("\t");

		// Trim all fields
		for(int i=0;i<fields.length;i++)
			fields[i] = fields[i].trim();
		

		// Parse attrs
		
		if (fields.length == 8) {
			attrs = new HashMap<String,String>();
		} else if (fields.length == 9) {
			attrs = parseAttrs(fields[8]);
		} else {
			throw new InvalidFormatException("Not enought coluns in GFF line. Founded %d but needs at least 8.\n" + 
														   " GFF line: '%s'",fields.length,line);
		}
		
		// Parse Strand
		strand = fields[6].charAt(0);
		if ("?.+-".indexOf(strand) == -1) {
			throw new InvalidFormatException("Strand should be one of this simbols +-?. instead of %c ",strand);
		}
		
		// Parse Start
		try {
			start = Long.parseLong(fields[3]);
		} catch(NumberFormatException e) {
			throw new InvalidFormatException("Invaid number format in start field: '%s' is not a number",fields[3]);
		}

		// Parse End
		try {
			end = Long.parseLong(fields[4]);
		} catch(NumberFormatException e) {
			throw new InvalidFormatException("Invaid number format in end field: '%s' is not a number",fields[4]);
		}

		// Parse Score
		try {
			score = Double.parseDouble(fields[5]);
		} catch(NumberFormatException e) {
			throw new InvalidFormatException("Invaid number format in score field: '%s' is not a number",fields[5]);
		}
		
		// Parse phase
		try {
			if (fields[7].charAt(0) == '.') {
				phase = 0;
			} else 
				phase = Byte.parseByte(fields[7]);
			if (phase < 0 || phase > 2) {
				throw new InvalidFormatException("Invaid phase field value: Founded '%s', should be 0,1 or 2",fields[7]);				
			}
		} catch(NumberFormatException e) {
			throw new InvalidFormatException("Invaid number format in phase field: '%s' is not a number",fields[7]);
		}
		
		return new GFF(fields[0], 
				fields[1], fields[2], start, end, score,strand, phase, attrs);
	}

	private static Pattern quoted = Pattern.compile("^\"(.*?)\"$");
	
	public static Map<String, String> parseAttrs(String s) {
		Map<String,String> map = new HashMap<String,String>();
		//log.debug(s);
		for(String x: s.split(";")) {
			x = x.trim();
			int pos = x.indexOf(' ');
			String key;
			String value;
			
			if (pos == -1) { 
				key = x;
				value = "";
			} else {
				key = x.substring(0, pos);
				value=x.substring(pos+1,x.length());
			}
			Matcher m = quoted.matcher(value);
			
			if (m.matches()) {
				value = m.group(1);
			}
			
			//log.debug(String.format("k='%s' v='%s'",key,value));
			map.put(key, value);
		}
		return map;
	}

	static public GFF parseGTF(String line) {
		throw new RuntimeException("Unimplemented");
	}	
}
