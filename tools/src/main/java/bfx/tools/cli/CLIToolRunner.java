package bfx.tools.cli;

import static java.lang.System.err;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.tools.Tool;
import bfx.utils.TextUtils;
import ch.qos.logback.classic.Level;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Run a Tool using the arguments.
 * 
 * @author varuzza
 *
 */
public class CLIToolRunner {
	private static Logger log = LoggerFactory.getLogger(CLIToolRunner.class);

	private static void parseArgs(final Tool tool,final String... args) {
		JCommander jc = new JCommander(tool);
		jc.setProgramName("bfx");
		try {
			jc.parse(args);
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
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
	
	/**
	 * Create a instance of klass, pass args and run it
	 * 
	 * @param klass - A class which extends Tool
	 * @param args - Commnad line argnuments
	 * 
	 */
	public static void run(final Class<? extends Tool> klass,final String... args) {
		try {
			log.info(String.format("Loading class '%s'",klass.getName()));
			CLIProgressMeterFactory pmf = new CLIProgressMeterFactory();
			
			Tool tool = klass.newInstance();
			tool.setProgressMeterFactory(pmf);
			
			// Parse the other args
			parseArgs(tool,args);

			tool.run();			
		} catch(Exception e) {
			if (klass == null) {
				err.println(String.format("Error: %s",e.getMessage()));				
			} else {
				err.println(String.format("Error running tool '%s': %s",
						klass.getName(),e.getMessage()));
			}
			e.printStackTrace();
		}
	}

	
	/**
	 * @param klass
	 * @param args
	 */
	public static void run(final Class<? extends Tool> klass,final String args) {
		String[] splited = args.split("\\s+");
		log.info(StringUtils.join(splited, ","));
		run(klass,splited);
	}
}
