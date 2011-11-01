package bfx.tools;

import java.util.Map;

public class ToolConfiguration {
	private Map<String,String> values;
	
	public String get(String name) {
		if (!values.containsKey(name))
			throw new RuntimeException(String.format("Missing configuration '%s'",name));
		return values.get(name);
	}
	
	public void set(String name,String value) {
		if (values.containsKey(name))
			throw new RuntimeException(String.format("Trying to overwrite configuration '%s'",name));
		values.put(name,value);
	}
	
}
