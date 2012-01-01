package bfx.tools.cli;

import static java.lang.System.err;
import static java.lang.System.exit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bfx.ProgressCounter;
import bfx.tools.Tool;
import bfx.utils.TextUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {
	private static Logger log = Logger.getLogger(Main.class);
	
	private static ServiceLoader<Tool> toolLoader = ServiceLoader.load(Tool.class);
	
	private static Map<String,Class<? extends Tool>> commands = new HashMap<String,Class<? extends Tool>>();;
	
	public static void addCommand(String name,Class<? extends Tool> klass) {
		System.out.println(String.format("Registering command '%s' to class '%s'",name,klass.getName()));
		commands.put(name,klass);
	}
	
	public static void parseArgs(Tool tool,String... args) {
		JCommander jc = new JCommander(tool);
		jc.setProgramName("bfx");
		try {
			jc.parse(args);
			Logger root = Logger.getRootLogger();
			if (tool.verbose) {
			      root.setLevel(Level.DEBUG);
			 } else {
			      root.setLevel(Level.INFO);				 
			 }
		} catch(ParameterException e) {
			System.err.println(TextUtils.doubleLine());
			System.err.print("Command Line error: ");
			System.err.println(e.getMessage());
			System.err.println(TextUtils.line());
			jc.usage();			
			System.err.println(TextUtils.doubleLine());
			System.exit(-1);
		}
	}
	
	public static void run(Class<? extends Tool> klass,String... args) {
		try {
			log.info(String.format("Loading class '%s'",klass.getName()));
			ProgressCounter pc = new ProgressCounter();
			CLIProgressBar pb = new CLIProgressBar();
			pc.addObserver(pb);
			
			Tool tool = klass.newInstance();
			tool.setProgressCounter(pc);
			
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
