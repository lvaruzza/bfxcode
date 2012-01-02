package bfx.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import bfx.Sequence;
import bfx.SequenceBuilder;

/*
 * 
 * 
 * 
 */
/**
 * SequnceBuilder based on a list.
 * 
 * Each appended sequence will be pushed on a list
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public class SequenceBuilderList implements SequenceBuilder {
	private LinkedList<Sequence> store;
	private int size = 0;
	
	public SequenceBuilderList() {
		store = new LinkedList<Sequence>();
	}
	
	@Override
	public void append(Sequence sequence) {
		store.push(sequence);
		size += sequence.length();
	}

	@Override
	public Sequence getConstQual(String name,byte qual) {
		byte[] rs = new byte[size];
		int pos = 0;
		Iterator<Sequence> revseqs = store.descendingIterator();
		while(revseqs.hasNext()) {
			Sequence s = revseqs.next();
			for(byte b: s.getSeq())
				rs[pos++] = b;
		}
		return new SequenceConstQual(name,rs,qual);
	}

	@Override
	public Sequence getWithQual(String name) {
		byte[] rs = new byte[size];
		byte[] rq = new byte[size];
		int pos = 0;
		Iterator<Sequence> revseqs = store.descendingIterator();
		while(revseqs.hasNext()) {
			Sequence s = revseqs.next();
			int i = pos;
			for(byte b: s.getSeq())
				rs[i++] = b;
					
			int j = pos;
			pos = i;
		    for(byte b: s.getQual())
		    	rq[j++] = b;
		}
		return new SequenceQual(name,rs,rq);
	}

	@Override
	public int getPosition() {
		return size;
	}

}
