package bfx.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

class StreamGobbler extends Thread {
	InputStream is;
	OutputStream os;

	StreamGobbler(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			PrintStream pr = new PrintStream(os);
			
			String line = null;
			while ((line = br.readLine()) != null) {
				pr.println(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}