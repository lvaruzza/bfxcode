package bfx.tools.cli;

import static java.lang.System.err;
import static java.lang.System.exit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.tools.Tool;

/**
 * Entry point for executing the Tools
 * 
 * This class will load the list of available tools (searching the class paht using the ServiceLoader)
 * and them call CLIToolRunner to run the command.
 * 
 * @author varuzza
 *
 */
public class Main {
	private static Logger log = LoggerFactory.getLogger(Main.class);
	
	private static ServiceLoader<Tool> toolLoader = ServiceLoader.load(Tool.class);
	
	private static Map<String,Class<? extends Tool>> commands = new HashMap<String,Class<? extends Tool>>();;
	
	private static void addCommand(String name,Class<? extends Tool> klass) {
		log.debug(String.format("Registering command '%s' to class '%s'",name,klass.getName()));
		commands.put(name,klass);
	}
	
	/**
	 * 
	 * The first argument is the tool to be executed.
	 * 
	 * Create a instance of the proper class and run it.
	 * 
	 */
	public static void main(String... args) {
		for(Tool tool: toolLoader) {
			addCommand(tool.getName(),tool.getClass());
		}
		
		if (args.length < 1) {
			err.println("Missing the Tool Name");
			listValidCommands();
			exit(-1);
		} else {
			if (commands.containsKey(args[0])) {
				Class<? extends Tool> klass = commands.get(args[0]);				
				CLIToolRunner.run(klass,Arrays.copyOfRange(args, 1, args.length));
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
