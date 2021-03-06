package bfx.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bfx.GFF;
import bfx.exceptions.InvalidFormatException;

/**
 * Parse a GFF Line
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class GFFParser {
	//static private Logger log = Logger.getLogger(AbstractGFFReader.class);
	
	/**
	 * Parse GFF Line.
	 * 
	 * @param line Line with GFF text
	 * 
	 * @return GFF Record
	 * @throws InvalidFormatException
	 */
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
	
	/**
	 * Parse a String with key-value pairs in the foramt
	 * 
	 * key1="value1"; key2="value2"...
	 * 
	 * @param attributes Attributes String
	 * @return Map with parameter Name -> Value both as Strings
	 */
	public static Map<String, String> parseAttrs(String attributes) {
		Map<String,String> map = new HashMap<String,String>();
		//log.debug(s);
		for(String x: attributes.split(";")) {
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

	/**
	 * Unimplemented
	 * 
	 * @param line
	 * @return GFF Record
	 */
	static public GFF parseGTF(String line) {
		throw new RuntimeException("Unimplemented");
	}	
}
