package bfx.sequencing;

import java.util.Iterator;

import bfx.Sequence;
import bfx.io.SequenceSource;
import bfx.process.ProgressMeter;

public class PairMatcher {
	public interface Callback {
		public void pair(Sequence left,Sequence right) throws Exception;
		public void singlet(Sequence singlet) throws Exception;
	}

	private Platform platform;
	private ProgressMeter pm;
	private SequenceSource leftSrc;
	private SequenceSource rightSrc;
	
	public PairMatcher(Platform platform) {
		this.platform = platform;
	}
	
	public void setProgressMeter(ProgressMeter pm) {
		this.pm = pm;
	}

	public void setLeft(SequenceSource left) {
		this.leftSrc = left;
	}
	
	public void setRight(SequenceSource right) {
		this.rightSrc = right;
	}

	public void match(Callback callback) throws Exception {
		Iterator<Sequence> li = leftSrc.iterator();
		Iterator<Sequence> ri = rightSrc.iterator();
		Sequence left = li.hasNext() ? li.next() : null;
		Sequence right = ri.hasNext() ? ri.next() : null;
		while(left != null && right != null) {
			int cmp = platform.compare(left.getId(),right.getId());
			if (cmp == 0) {
				callback.pair(left, right);
				pm.incr(2);
				left = li.hasNext() ? li.next() : null;
				right = ri.hasNext() ? ri.next() : null;
			} else {
				if (cmp < 0) {
					callback.singlet(left);
					left = li.hasNext() ? li.next() : null;					
				} else {
					callback.singlet(right);
					right = ri.hasNext() ? ri.next() : null;
				}
				pm.incr(1);
			}
		}
		
		// at least one of the files ended
		// proccess the remaining seqs
		if (left != null) {
			callback.singlet(left);
			while(li.hasNext()) {
				callback.singlet(li.next());
				pm.incr(1);
			}
		}
		else if (right != null) {
			callback.singlet(right);
			while(ri.hasNext()) {
				callback.singlet(ri.next());
				pm.incr(1);
			}
		}
		
	}
	
}
