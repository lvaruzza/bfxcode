package bfx.tools.cli;

import static java.lang.System.err;
import static java.lang.System.exit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;

import bfx.tools.Tool;

import com.beust.jcommander.JCommander;

public class Main {
	private static Logger log = Logger.getLogger(Main.class);
	
	private static ServiceLoader<Tool> toolLoader = ServiceLoader.load(Tool.class);
	
	private static Map<String,Class<? extends Tool>> commands = new HashMap<String,Class<? extends Tool>>();;
	
	public static void addCommand(String name,Class<? extends Tool> klass) {
		log.debug(String.format("Registering command '%s' to class '%s'",name,klass.getName()));
		commands.put(name,klass);
	}
	
	public static void parseArgs(Object tool,String... args) {
		JCommander jc = new JCommander(tool, args);
	}
	
	public static void run(Class<? extends Tool> klass,String... args) {
		try {
			log.info(String.format("Loading class '%s'",klass.getName()));
			
			Tool tool = klass.newInstance();
			
			// Parse the other args
			parseArgs(tool,Arrays.copyOfRange(args, 1, args.length));

			tool.run();			
		} catch(Exception e) {
			err.println(String.format("Error running tool '%s': %s",args[0],e.getMessage()));
			e.printStackTrace();
		}
	}
	/*
	 * The first argument is the tool to be executed.
	 * 
	 * Create a instance of the proper class and run it.
	 * 
	 */
	public static void main(String... args) {
		System.out.println("All regiesterd tools");
		for(Tool tool: toolLoader) {
			System.out.println(tool.getClass().getName());			
		}
		
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
