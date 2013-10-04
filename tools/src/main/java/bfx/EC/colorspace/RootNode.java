package bfx.EC.colorspace;

/*
 * the rootNodes are each roots of a subtree. The RootNodes children are
 * AlphaSufixNodes. RootNode.key keeps track of the key which the root has in
 * the hashtable in Subtree.
 */
public class RootNode extends SuffixNode {

	String key;

	public RootNode(char color, int level, String key) {
		super(color, level, null);

		this.key = key;
	}
}
