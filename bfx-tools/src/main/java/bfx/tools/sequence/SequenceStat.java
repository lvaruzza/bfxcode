package bfx.tools.sequence;

import bfx.io.SequenceFormat;
import bfx.io.SequenceReader;
import bfx.tools.Tool;
import bfx.tools.ToolConfiguration;
import bfx.tools.cli.Main;

public class SequenceStat extends Tool {
	static {
		Main.addCommand("sequenceStat", Tool.class);
	}
	
	@Override
	public void run() throws Exception {
		ToolConfiguration config = this.getConfig();
		String input = config.get("input");
		
		SequenceReader reader = SequenceFormat.getReader(input);
		
	}
}
