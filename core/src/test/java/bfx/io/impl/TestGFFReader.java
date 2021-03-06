package bfx.io.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

import bfx.GFF;
import bfx.exceptions.EmptyIteratorException;
import bfx.impl.GFFParser;
import bfx.io.GFFWriter;
import bfx.utils.BFXIteratorUtils;
import bfx.utils.BFXMapUtils;

public class TestGFFReader {
	private static Logger log = LoggerFactory.getLogger(TestGFFReader.class);
	
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
		assertEquals(180,BFXIteratorUtils.count(it));
	}
	
	@Test
	public void testReadFirst() throws IOException, EmptyIteratorException, EmptyIteratorException {
		System.out.println();
		GFF align = BFXIteratorUtils.first(it);
		log.debug(align.toString());
	}
	
	private void testParseAttrs1(String line,String geneId,String transcriptId) {
		Map<String,String> attr=GFFParser.parseAttrs(line);
		log.debug(BFXMapUtils.toString(attr));
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
	
	@Test
	public void testReadWrite() throws IOException {
		GFFWriter gw = new OSGFFWriter();
		
		gw.write(System.out,it);
	}
}
