package bfx.io.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import utils.exception.EmptyIteratorException;
import bio.gff.GFF;
import bio.gff.GFFAlign;

public class TestComIOGFFReader {
	private static Logger log = Logger.getLogger(TestComIOGFFReader.class);
	
	private ComIOGFFReader reader;
	private Iterator<GFFAlign>  it;
	
	@Before
	public void setUp() throws IOException {
		reader = new ComIOGFFReader();
		it = reader.read("test/test1.gff");
	}
	
	@Test
	public void testReadCount() throws IOException {
		System.out.println();
		assertEquals(180,utils.IteratorUtils.count(it));
	}
	
	@Test
	public void testReadFirst() throws IOException, EmptyIteratorException {
		System.out.println();
		GFFAlign align = utils.IteratorUtils.first(it);
		log.debug(align);
	}
	private void testParseAttrs1(String line,String geneId,String transcriptId) {
		Map<String,String> attr=GFF.parseAttrs(line);
		log.debug(utils.MapUtils.toString(attr));
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
