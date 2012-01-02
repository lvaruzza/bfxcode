package bfx.impl;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import bfx.Sequence;
import bfx.SequenceBuilder;

public class TestSequenceBuilderList {

	@Test
	public void test0() {
		SequenceBuilder sb = new SequenceBuilderList();
		sb.append(Sequence.make("ACGT","0 1 2 3"));
		sb.append(Sequence.make("0123","0 1 2 3"));
		Sequence out = sb.getConstQual("", (byte)0);
		System.out.println(out);
		assertEquals(Sequence.make("ACGT0123"),out);
		
		Sequence out2 = sb.getWithQual("");
		System.out.println(out2);
		assertEquals(Sequence.make("ACGT0123","0 1 2 3 0 1 2 3"),out2);

	}
}
