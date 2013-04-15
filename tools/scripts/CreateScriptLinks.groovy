package bfx.support
import java.util.ServiceLoader

import bfx.tools.Tool


class CreateScriptLinks {
	
	static main(args) {
		def loader = ServiceLoader.load(Tool.class);
		def script = (args.length == 0) ? "./bfx" : args[0]
		for(tool in loader) {
			println("ln -sf ${script} ${tool.getName()}")
		}
	}

}
