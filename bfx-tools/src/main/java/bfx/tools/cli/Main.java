package bfx.tools.cli;

import static java.lang.System.err;
import static java.lang.System.exit;

import java.util.HashMap;
import java.util.Map;

import bfx.tools.Tool;
import bfx.tools.ToolConfiguration;

public class Main {
	private static Map<String,Class<? extends Tool>> commands = new HashMap<String,Class<? extends Tool>>();;
	
	public static void addCommand(String name,Class<? extends Tool> klass) {
		commands.put(name,klass);
	}
	
	public static void run(Class<? extends Tool> klass,String... args) {
		try {
			Tool tool = klass.newInstance();
			ToolConfiguration config = new ToolConfiguration();

			// Transform args into a configuration
			//Arrays.copyOfRange(args, 1, args.length)

			tool.setConfig(config);
			tool.run();			
		} catch(Exception e) {
			err.println(String.format("Error running tool '%s': %s",args[0],e.getMessage()));
			e.printStackTrace();
		}
	}
	
	public static void main(String... args) {
		if (args.length < 1) {
			err.println("Missing the Tool Name");
			listValidCommands();
			exit(-1);
		} else {
			if (commands.containsKey(args[0])) {
				Class<? extends Tool> klass = commands.get(args[0]);
				run(klass,args);
			} else {
				err.println(String.format("Invalid tool name '%s'",args[0]));
				listValidCommands();				
				exit(-1);
			}
		}
		
	}

	private static void listValidCommands() {
		System.err.println("Valid commands:");
		for(String cmd: commands.keySet() ) {
			System.err.println(String.format("\t%s",cmd));
		}
	}
}
