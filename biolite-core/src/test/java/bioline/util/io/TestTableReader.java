package bioline.util.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import biolite.utils.io.TableReader;
import static org.junit.Assert.*;

public class TestTableReader {
	
	
	@Test
	public void testTableReader() throws IOException {
		TableReader reader = new TableReader();
		
		Iterator<String[]> it = reader.read("data/test/ncbi_small.check.txt");
		int i=0;
		
		while(it.hasNext()) {
			String[] x = it.next();
			System.out.println(Arrays.toString(x));
			assertEquals(3,x.length);
			i++;
		}
		assertEquals(9,i);
	}
}
