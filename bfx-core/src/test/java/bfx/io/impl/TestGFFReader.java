package bfx.io.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import bfx.GFF;
import bfx.exceptions.EmptyIteratorException;
import bfx.impl.GFFParser;
import bfx.utils.IteratorUtils;
import bfx.utils.MapUtils;

public class TestGFFReader {
	private static Logger log = Logger.getLogger(TestGFFReader.class);
	
	private LineBasedGFFReader reader;
	private Iterator<GFF>  it;
	
	@Before
	public void setUp() throws IOException {
		reader = new LineBasedGFFReader();
		it = reader.read("data/gff/test1.gff");
	}
	
	@Test
	public void testReadCount() throws IOException {
		System.out.println();
		assertEquals(180,IteratorUtils.count(it));
	}
	
	@Test
	public void testReadFirst() throws IOException, EmptyIteratorException, bfx.utils.EmptyIteratorException {
		System.out.println();
		GFF align = IteratorUtils.first(it);
		log.debug(align);
	}
	private void testParseAttrs1(String line,String geneId,String transcriptId) {
		Map<String,String> attr=GFFParser.parseAttrs(line);
		log.debug(MapUtils.toString(attr));
		assertTrue(attr.containsKey("gene_id"));
		assertEquals(geneId,attr.get("gene_id"));
		assertTrue(attr.containsKey("transcript_id"));
		assertEquals(transcriptId,attr.get("transcript_id"));
	}
	
	@Test
	public void testParseAttrs() {
		testParseAttrs1("gene_id \"PLEKHN1\"; transcript_id \"NM_032129\";","PLEKHN1","NM_032129");
		testParseAttrs1("gene_id \"gene 1\"; transcript_id \"NM_032129\";","gene 1","NM_032129");
		testParseAttrs1("gene_id; transcript_id \"NM_032129\";","","NM_032129");
	}
	
}
