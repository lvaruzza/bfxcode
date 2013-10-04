package bfx.EC.colorspace;

/*
 * AlphaSuffixNodes is used for all nodes on the level right under the
 * root-nodes. It provides an additional list of reads that pass a node which is
 * used to identify the according reads during error-correction
 */
public class AlphaSuffixNode extends SuffixNode {

	int[] readsPassingNode;
	int readIndex;
	boolean baseReads;
	boolean colorReads;

	public AlphaSuffixNode(char color, int level, SuffixNode parent) {
		super(color, level, parent);

		this.readsPassingNode = new int[(int) Math
				.round(expectedVisitsPerNode[level])];
		this.readIndex = 0;
		this.baseReads = false;
		this.colorReads = false;
	}

	// Despite from raising the counter - in a AlphaSuffixNode the reads going
	// into this branch will be collected
	// But only for a fixed amount of expectedVisits - otherwise the whole
	// branch will be considered correct.
	public void visit(int readNumber) {
		super.visit();
		if (readIndex < readsPassingNode.length) {
			readsPassingNode[readIndex] = readNumber;
			readIndex++;
			if (readNumber / 2 < ColorShrec.basereadcount) {
				this.baseReads = true;
			} else {
				this.colorReads = true;
			}
		}
	}
}
