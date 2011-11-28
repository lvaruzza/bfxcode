package bfx.impl;

import java.util.LinkedList;
import java.util.List;

import bfx.Sequence;
import bfx.SequenceBuilder;

public class SequenceBuilderListImpl implements SequenceBuilder {
	private List<Sequence> store;
	private int size = 0;
	
	public SequenceBuilderListImpl() {
		store = new LinkedList<Sequence>();
	}
	
	@Override
	public void append(Sequence sequence) {
		store.add(sequence);
		size += sequence.length();
	}

	@Override
	public Sequence getConstQual(String name,byte qual) {
		byte[] rs = new byte[size];
		int pos = 0;
		for(Sequence s: store) {
			for(byte b: s.getSeq())
				rs[pos++] = b;
		}
		return new SequenceConstQualImpl(name,rs,qual);
	}

	@Override
	public Sequence getWithQual(String name) {
		byte[] rs = new byte[size];
		byte[] rq = new byte[size];
		int pos = 0;
		for(Sequence s: store) {
			int i = pos;
			for(byte b: s.getSeq())
				rs[i++] = b;
					
			int j = pos;
			pos = i;
		    for(byte b: s.getQual())
		    	rq[j++] = b;
		}
		return new SequenceQualImpl(name,rs,rq);
	}

}
