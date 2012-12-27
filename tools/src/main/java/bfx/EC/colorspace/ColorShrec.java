package bfx.EC.colorspace;

import java.util.ArrayList;
import java.util.BitSet;

import java.util.*;

import java.io.*;

//The basic unit of the tree. Current implementation is still rather large.
//Best way to save further space is probably to define an AbstractSuffixNode
//then extend it so that nodes with high branch factor (>1 child nodes) can
//be differentiated from nodes with a single outgoing branch. Nodes with a
//single outgoing branch constitute most of the nodes in the tree and are
//uninteresting for error correction purposes. Runs of them should be compacted
//in future versions.
class SuffixNode {

	// guess
	private static int _color2int[] = new int[256];

	static {

		for (int i = 0; i < 256; i++) {

			_color2int[i] = -1;

		}

		_color2int['0'] = 0;

		_color2int['1'] = 1;

		_color2int['2'] = 2;

		_color2int['3'] = 3;

		_color2int['.'] = 4;

	}
	// the parent of this node
	public SuffixNode parent;
	// the beautiful children
	public SuffixNode[] children;
	// the label on the incoming branch
	private byte color;
	// the level in the tree this node is - level 0 is the root
	private byte level;
	// the number of leaves in the subtree rooted at this node
	public int visits;
	// ratio has THREE (3) possible uses!
	// 1: It actually holds a ratio:
	// Let min be index such that children[i].visits is the minimum at this node
	// Let max be index such that children[i].visits is the minimum at this node
	// ratio = max/min (note if only one child, min == max != 0, so we're safe).
	// 2: The read number if this node is a terminal (ie. children == null)
	// 3: If children.length == 1 then ratio contains the base (actually ordinal
	// base: 0,1,2,3) of the only child.
	private float ratio;
	// Tracks the height of the tree; used later in buildAndProcess.
	public static int height = 0;
	// Stats about the number of nodes; used to assign _gterms later.
	public static int totalNodes = 0;
	public static int numNonTerms = 0;
	public static int numBigNodes = 0;
	// Set by the main routine: Gives the error-correction a clue which nodes
	// are erroneus
	public static double[] expectedVisitsPerNode;

	public SuffixNode(char color, int level, SuffixNode parent) {

		this.parent = parent;

		this.color = (byte) color;

		this.level = (byte) level;

		this.visits = 0;

		totalNodes++;

	}

	private int color2int(char color) {

		return _color2int[color];

	}

	public SuffixNode getChildren(char color) {

		return getChildren(color2int(color));

	}

	public SuffixNode getChildren(int index) {

		if (children == null) {

			return null;

		}

		if (children.length == 1) {

			if (((int) ratio) == index) {

				return children[0];

			}

			return null;

		}

		return children[index];

	}

	// Creates a child of this node labelled with base if necessary.
	// Updates the number of leaves in the subtree rooted at the child
	// of this node labelled with base.
	public void visitChildren(char color, int readNumber) {

		int k = color2int(color);

		if (isTerminal()) {

			// This node will soon have a child node so is no longer terminal

			children = new SuffixNode[1];

			ratio = k;

			k = 0;

			numNonTerms++;

		} else if (children.length == 1) {

			if (((int) ratio) == k) {

				k = 0;

			} else {

				numBigNodes++;

				SuffixNode tmp = children[0];

				children = new SuffixNode[5];

				children[(int) ratio] = tmp;

			}

		}

		SuffixNode node = children[k];

		if (node == null) {

			// there is currently no outgoing branch for base,

			// make a new one (with a child at the end of it)

			if (this instanceof RootNode) {

				children[k] = new AlphaSuffixNode(color, (level + 1), this);

			} else {

				children[k] = new SuffixNode(color, (level + 1), this);

			}

			node = children[k];

			if (height <= (level + 1)) {

				height++; // note the new depth

			}

		}

		// track number of leaves in the subtree rooted at the child

		// of this node which has outgoing branch labelled base

		if (this instanceof RootNode && node instanceof AlphaSuffixNode) {

			((AlphaSuffixNode) node).visit(readNumber);

		} else {

			node.visit();

		}

	}

	// visits the node during tree construction
	public void visit() {

		this.visits++;

	}

	public char getColor() {

		return (char) this.color;

	}

	public int getLevel() {

		return this.level;

	}

	public boolean isTerminal() {

		return (children == null);

	}

	public double getRatio() {

		if (children == null) {
			return ratio;
		}

		if (children.length == 1) {
			return 1;
		}

		return ratio;

	}

	// If not a terminal: return the max visits value at a child
	// divided by the min (if only one child min == max != 0)
	// Otherwise: return -1;
	public double calculateRatio() {

		if (children == null) {

			return 1;

		} else {

			if (children.length == 1) {

				return 1;

			}

			int min = 1000000;
			int max = 0;

			for (int i = 0; i < 4; i++) {

				if (children[i] != null) {

					int count = children[i].visits;

					if (count < min) {

						min = count;

					}

					if (count > max) {

						max = count;

					}

				}

			}

			ratio = max / min;

			return ratio;

		}

	}

	// aligns the subtree under this node with that of another (neighbourNode)
	// this routine is used to see if a correction of this.base to another base
	// makes sense
	public boolean alignSubtree(SuffixNode neighbourNode) {
		if (this.children == null) {
			return true;
		}
		if (neighbourNode == null) {
			return false;
		}
		for (int i = 0; i < 4; i++) {
			SuffixNode nextNode = this.getChildren(i);
			if (nextNode != null) {
				SuffixNode nextNeighbour = neighbourNode.getChildren(i);
				if (nextNeighbour != null) {
					if (nextNode.alignSubtree(nextNeighbour)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Collection<String> alignSubtree2(SuffixNode neighbourNode) {

		if (this.children == null) {
			HashSet<String> s = new HashSet<String>();
			s.add("" + (char) this.color);
			return s;

		}
		if (neighbourNode == null) {
			return null;
		}

		HashSet<String> matchingStrings = new HashSet<String>();
		for (int i = 0; i < 4; i++) {
			SuffixNode nextNode = this.getChildren(i);
			if (nextNode != null) {
				SuffixNode nextNeighbour = neighbourNode.getChildren(i);
				if (nextNeighbour != null) {
					Collection<String> c = nextNode
							.alignSubtree2(nextNeighbour);
					if (c != null) {
						// Take collection into this set
						Iterator<String> elements = c.iterator();
						while (elements.hasNext()) {
							String s = elements.next();
							matchingStrings.add((char) this.color + s);
						}
					}
				}
			}
		}
		if (matchingStrings.size() > 0) {
			return matchingStrings;
		} else {
			return null;
		}

	}

	// for statistic purposes only...
	public int countNodes() {
		SuffixNode child = null;
		int subnodes = 0;
		for (int i = 0; i < 5; i++) {
			child = this.getChildren(i);
			if (child != null) {
				subnodes += child.countNodes();
			}
		}
		return subnodes + 1;
	}
}

/*
 * AlphaSuffixNodes is used for all nodes on the level right under the
 * root-nodes. It provides an additional list of reads that pass a node which is
 * used to identify the according reads during error-correction
 */
class AlphaSuffixNode extends SuffixNode {

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

/*
 * the rootNodes are each roots of a subtree. The RootNodes children are
 * AlphaSufixNodes. RootNode.key keeps track of the key which the root has in
 * the hashtable in Subtree.
 */
class RootNode extends SuffixNode {

	String key;

	public RootNode(char color, int level, String key) {
		super(color, level, null);

		this.key = key;
	}
}

// This is the main class for the algorithm: it builds the tree and performs
// error-correction.
// builds only the subtree rooted under "prefix" and only the levels passed as a
// parameter.
class Subtree {

	// the prefix that a read has to contain before being taken into this
	// subtree
	private String prefix;
	// the levels of the tree which are actually kept track of and later to be
	// analyzed
	private int fromLevel, toLevel;
	// the entry point into all the subtrees at "fromLevel" in this subtree
	private Hashtable<String, RootNode> subSubTree;
	// The place where the correctedReads are marked.
	public static BitSet correctedReads, identifiedReads;
	// Guess...
	private char int2color[] = { '0', '1', '2', '3' };

	private int combinedColors[][] = { { 0, 1, 2, 3 }, { 1, 0, 3, 2 },
			{ 2, 3, 0, 1 }, { 3, 2, 1, 0 } };

	private String baseColor2Base[][] = { { "A", "C", "G", "T" },
			{ "C", "A", "T", "G" }, { "G", "T", "A", "C" },
			{ "T", "G", "C", "A" } };

	private static int _base2int[] = new int[256];
	private static int _color2int[] = new int[256];
	static {
		for (int i = 0; i < 256; i++) {
			_base2int[i] = -1;
			_color2int[i] = -1;
		}

		_base2int['A'] = 0;
		_base2int['C'] = 1;
		_base2int['G'] = 2;
		_base2int['T'] = 3;
		_base2int['N'] = 4;

		_color2int['0'] = 0;
		_color2int['1'] = 1;
		_color2int['2'] = 2;
		_color2int['3'] = 3;
		_color2int['.'] = 4;
	}

	private static int base2int(char base) {
		return _base2int[base];
	}

	private static int color2int(char color) {
		return _color2int[color];
	}

	// The initial reads in the same order they are handed to us at
	// construction, which is the same order in which they appear on file.
	// private byte[][] reads;
	// Counts the number of nodes we actually suspected of an error.
	// Modified by examineNode or examineNodeWithTerms, whichever is
	// being used.
	public Subtree(String prefix, int fromLevel, int toLevel, int numReads) {
		this.prefix = prefix;
		this.fromLevel = fromLevel;
		this.toLevel = toLevel;
		if (fromLevel < prefix.length()) {
			System.err
					.println("Cannot build subtree with such a short prefix!");
		}
		this.subSubTree = new Hashtable<String, RootNode>();
		// correctedReads = new BitSet(numReads);
	}

	// Correct a mismatch in a color space read
	// Return the number of corrected mismatches
	private int correctColorSpaceMismatch(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int mismatches = 0;

		for (AlphaSuffixNode reliable : reliableNodes) {
			// align any suspicious node with all the reliable ones and look for
			// matches

			Collection<String> matchingStrings = skew.alignSubtree2(reliable);
			if (matchingStrings != null) {

				// match found - identify the reads and correct them
				int readNumber;

				// change all reads that pass the skew-node
				for (int i = 0; i < skew.readIndex; i++) {
					// check that this is a color space read
					if (skew.readsPassingNode[i] / 2 < ColorShrec.basereadcount) {
						continue;
					}

					if (reliable == null) {
						continue;
					}

					synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
						// get the read
						String read = new String(
								ColorShrec.reads[skew.readsPassingNode[i]]);
						String trimmedRead;

						// If the read is a complement, trim the last color +
						// base
						if (skew.readsPassingNode[i] % 2 == 1) {
							trimmedRead = read.substring(0, read.length() - 2);
						} else {
							trimmedRead = read;
						}

						// identify actual position of the error in the read
						int errPos = trimmedRead.indexOf(prefix + key)
								+ prefix.length() + key.length();
						if (errPos >= trimmedRead.length() || errPos < 0) {
							continue;
						}

						// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

						Iterator<String> possibleMatches = matchingStrings
								.iterator();
						boolean matchFound = false;

						while (possibleMatches.hasNext()) {
							String s = possibleMatches.next();

							if (errPos + s.length() > trimmedRead.length()) {
								String endOfRead = trimmedRead
										.substring(errPos);

								if (s.startsWith(endOfRead)) {
									matchFound = true;
									break;
								}

							} else if (trimmedRead.substring(errPos,
									errPos + s.length()).equals(s)) {
								matchFound = true;
								break;
							}
						}

						// half readNumber to match with the readNumber in the
						// input-file.
						readNumber = skew.readsPassingNode[i] / 2;

						if (matchFound) {
							// correct the read
							char correctToColor = reliable.getColor();
							try {
								read = read.substring(0, errPos)
										+ correctToColor
										+ read.substring(errPos + 1);
								mismatches++;
							} catch (Exception e) {
							}

							// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

							// half readNumber to match with the readNumber in
							// the input-file.
							readNumber = skew.readsPassingNode[i] / 2;
							correctedReads.set(readNumber);
							// identifiedReads.set(readNumber);

							ColorShrec.reads[skew.readsPassingNode[i]] = read
									.getBytes();

							// correct the complement as well
							if (skew.readsPassingNode[i] % 2 == 0) {
								ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
										.getBytes());
							} else {
								ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
										.getBytes());
							}
						} else {
							// no correction found!
							identifiedReads.set(readNumber);
						}
					}
				}
				// System.out.println("possible correction!");
			} else {
				// half readNumber to match with the readNumber in the
				// input-file.
				// int readNumber = skew.readsPassingNode[i] / 2;
				// identifiedReads.set(readNumber);
			}
		}
		return mismatches;
	}

	// Correct a color space read by a deletion
	// Return the number of corrected deletions
	private int correctColorSpaceDeletion(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int deletions = 0;

		HashSet<String> matchingStrings = new HashSet<String>();
		for (SuffixNode reliable : reliableNodes) {
			SuffixNode sn = skew.getChildren(reliable.getColor());
			if (sn != null) {
				Collection<String> c = sn.alignSubtree2(reliable);
				if (c != null) {
					// Take collection into this set
					Iterator<String> elements = c.iterator();
					while (elements.hasNext()) {
						String s = elements.next();
						matchingStrings.add(skew.getColor() + s);
					}
				}
			}
		}

		if (matchingStrings.size() != 0) {
			// match found - identify the reads and correct them
			int readNumber;

			// change all reads that pass the skew-node
			for (int i = 0; i < skew.readIndex; i++) {
				// check that this is a color space read
				if (skew.readsPassingNode[i] / 2 < ColorShrec.basereadcount) {
					continue;
				}

				synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
					// get the read
					String read = new String(
							ColorShrec.reads[skew.readsPassingNode[i]]);
					String trimmedRead;

					// If the read is a complement, trim the last color + base
					if (skew.readsPassingNode[i] % 2 == 1) {
						trimmedRead = read.substring(0, read.length() - 2);
					} else {
						trimmedRead = read;
					}

					// identify actual position of the error in the read
					int errPos = trimmedRead.indexOf(prefix + key)
							+ prefix.length() + key.length();
					if (errPos >= trimmedRead.length() || errPos < 0) {
						// System.out.println("Read: " + read + "Errpos: " +
						// errPos + " Prefix: " + prefix +
						// "Key: " + root.key);
						continue;
					}

					// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

					Iterator<String> possibleMatches = matchingStrings
							.iterator();
					boolean matchFound = false;

					while (possibleMatches.hasNext()) {
						String s = possibleMatches.next();

						if (errPos + s.length() > trimmedRead.length()) {
							String endOfRead = trimmedRead.substring(errPos);

							if (s.startsWith(endOfRead)) {
								matchFound = true;
								break;
							}

						} else if (trimmedRead.substring(errPos).startsWith(s)) {
							matchFound = true;
							break;
						}
					}

					// half readNumber to match with the readNumber in the
					// input-file.
					readNumber = skew.readsPassingNode[i] / 2;

					if (matchFound) {
						// correct the read
						try {
							read = read.substring(0, errPos)
									+ read.substring(errPos + 1);
							deletions++;
						} catch (Exception e) {
						}

						// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

						// half readNumber to match with the readNumber in the
						// input-file.
						readNumber = skew.readsPassingNode[i] / 2;
						correctedReads.set(readNumber);
						// identifiedReads.set(readNumber);

						ColorShrec.reads[skew.readsPassingNode[i]] = read
								.getBytes();

						// correct the complement as well
						if (skew.readsPassingNode[i] % 2 == 0) {
							ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
									.getBytes());
						} else {
							ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
									.getBytes());
						}
					} else {
						// no correction found!
						identifiedReads.set(readNumber);
					}
				}
				// System.out.println("possible correction!");
			}
		} else {
			// half readNumber to match with the readNumber in the input-file.
			// int readNumber = skew.readsPassingNode[i] / 2;
			// identifiedReads.set(readNumber);
		}

		return deletions;
	}

	// Correct a color space read by an insertion
	// Return the number of corrected insertions
	private int correctColorSpaceInsertion(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int insertions = 0;

		for (AlphaSuffixNode reliable : reliableNodes) {
			// align any suspicious node with all the reliable ones and look for
			// matches

			SuffixNode insertNode = reliable.getChildren(skew.getColor());
			if (insertNode != null) {
				Collection<String> matchingStrings = insertNode
						.alignSubtree2(skew);
				if (matchingStrings != null) {
					// match found - identify the reads and correct them
					int readNumber;

					// change all reads that pass the skew-node
					for (int i = 0; i < skew.readIndex; i++) {
						// check that this is a color space read
						if (skew.readsPassingNode[i] / 2 < ColorShrec.basereadcount) {
							continue;
						}
						if (insertNode == null) {
							continue;
						}

						synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
							// get the read
							String read = new String(
									ColorShrec.reads[skew.readsPassingNode[i]]);
							String trimmedRead;

							// If the read is a complement, trim the last color
							// + base
							if (skew.readsPassingNode[i] % 2 == 1) {
								trimmedRead = read.substring(0,
										read.length() - 2);
							} else {
								trimmedRead = read;
							}

							// identify actual position of the error in the read
							int errPos = trimmedRead.indexOf(prefix + key)
									+ prefix.length() + key.length();
							if (errPos >= trimmedRead.length() || errPos < 0) {
								continue;
							}

							// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

							Iterator<String> possibleMatches = matchingStrings
									.iterator();
							boolean matchFound = false;

							while (possibleMatches.hasNext()) {
								String s = possibleMatches.next();

								if (errPos + s.length() > trimmedRead.length()) {
									String endOfRead = trimmedRead
											.substring(errPos);

									if (s.startsWith(endOfRead)) {
										matchFound = true;
										break;
									}

								} else if (trimmedRead.subSequence(errPos,
										errPos + s.length()).equals(s)) {
									matchFound = true;
									break;
								}
							}

							// half readNumber to match with the readNumber in
							// the input-file.
							readNumber = skew.readsPassingNode[i] / 2;

							if (matchFound) {

								// correct the read
								char correctToColor = reliable.getColor();
								try {
									read = read.substring(0, errPos)
											+ correctToColor
											+ read.substring(errPos);
									insertions++;
								} catch (Exception e) {
								}

								// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

								// half readNumber to match with the readNumber
								// in the input-file.
								readNumber = skew.readsPassingNode[i] / 2;
								correctedReads.set(readNumber);
								// identifiedReads.set(readNumber);

								ColorShrec.reads[skew.readsPassingNode[i]] = read
										.getBytes();

								// correct the complement as well
								if (skew.readsPassingNode[i] % 2 == 0) {
									ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
											.getBytes());
								} else {
									ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
											.getBytes());
								}
							} else {
								// no correction found!
								identifiedReads.set(readNumber);
							}
						}
					}
					// System.out.println("possible correction!");
				}
			} else {
				// half readNumber to match with the readNumber in the
				// input-file.
				// int readNumber = skew.readsPassingNode[i] / 2;
				// identifiedReads.set(readNumber);
			}
		}
		return insertions;
	}

	// Correct a base space read by a mismatch (not N's!)
	// Return the number of corrected mismatches
	private int correctBaseSpaceMismatch(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int mismatches = 0;

		// Try to correct a mismatch (2 colors -> 2 compatible colors)
		// Figure out the combined color
		for (int j = 0; j < 4; j++) {
			SuffixNode skewChild = skew.getChildren(j);
			if (skewChild == null)
				continue;

			int combColor = combinedColors[color2int(skew.getColor())][j];
			for (AlphaSuffixNode reliable : reliableNodes) {
				// align any suspicious node with all the reliable ones and look
				// for matches
				Collection<String> matchingStrings = null;
				SuffixNode relChild = null;
				for (int i = 0; i < 4; i++) {
					relChild = reliable.getChildren(i);
					if (relChild == null)
						continue;

					if (combinedColors[color2int(reliable.getColor())][i] != combColor)
						continue;

					matchingStrings = skewChild.alignSubtree2(relChild);
					break;
				}

				if (relChild != null && matchingStrings != null) {
					// match found - identify the reads and correct them
					int readNumber;

					// change all reads that pass the skew-node
					for (int i = 0; i < skew.readIndex; i++) {
						// check that this is a base space read
						if (skew.readsPassingNode[i] / 2 >= ColorShrec.basereadcount) {
							continue;
						}

						if (reliable == null) {
							continue;
						}

						synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
							// get the read
							String read = new String(
									ColorShrec.reads[skew.readsPassingNode[i]]);
							String trimmedRead;

							// If the read is a complement, trim the last color
							// + base
							if (skew.readsPassingNode[i] % 2 == 1) {
								trimmedRead = read.substring(0,
										read.length() - 2);
							} else {
								trimmedRead = read;
							}

							// identify actual position of the error in the read
							int errPos = trimmedRead.indexOf(prefix + key)
									+ prefix.length() + key.length();
							if (errPos >= trimmedRead.length() || errPos < 0) {
								continue;
							}

							// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

							Iterator<String> possibleMatches = matchingStrings
									.iterator();
							boolean matchFound = false;
							while (possibleMatches.hasNext()) {
								String s = skew.getColor()
										+ possibleMatches.next();
								if (errPos + s.length() > trimmedRead.length()) {
									String endOfRead = trimmedRead
											.substring(errPos);

									if (s.startsWith(endOfRead)) {
										matchFound = true;
										break;
									}

								} else if (trimmedRead.subSequence(errPos,
										errPos + s.length()).equals(s)) {
									matchFound = true;
									break;
								}
							}

							// half readNumber to match with the readNumber in
							// the input-file.
							readNumber = skew.readsPassingNode[i] / 2;
							String baseRead = new String(
									ColorShrec.baseReads[readNumber]);

							if (matchFound) {
								// correct the read
								String correctToColor = ""
										+ reliable.getColor()
										+ relChild.getColor();
								String correctToBase = "";

								try {
									read = read.substring(0, errPos)
											+ correctToColor
											+ read.substring(errPos + 2);

									if (skew.readsPassingNode[i] % 2 == 0) {
										correctToBase = baseColor2Base[base2int(baseRead
												.charAt(errPos - 2))][color2int(reliable
												.getColor())];
										baseRead = baseRead.substring(0,
												errPos - 1)
												+ correctToBase
												+ baseRead.substring(errPos);
									} else {
										int blen = baseRead.length();
										correctToBase = baseColor2Base[base2int(baseRead
												.charAt(blen - errPos - 1))][color2int(reliable
												.getColor())];
										baseRead = baseRead.substring(0, blen
												- errPos - 2)
												+ correctToBase
												+ baseRead.substring(blen
														- errPos - 1);
									}
									mismatches++;
								} catch (Exception e) {
								}

								// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

								// half readNumber to match with the readNumber
								// in the input-file.
								readNumber = skew.readsPassingNode[i] / 2;
								correctedReads.set(readNumber);
								// identifiedReads.set(readNumber);

								ColorShrec.reads[skew.readsPassingNode[i]] = read
										.getBytes();
								ColorShrec.baseReads[skew.readsPassingNode[i] / 2] = baseRead
										.getBytes();

								// correct the complement as well
								if (skew.readsPassingNode[i] % 2 == 0) {
									ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
											.getBytes());
								} else {
									ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
											.getBytes());
								}

								// read = new String(Shrec.reads[readNumber*2]);
								// baseRead = new
								// String(Shrec.buildColorSpaceRead(Shrec.baseReads[readNumber]));

								// if (!read.equals(baseRead)) {
								// System.out.println("Mismatch: Corrected color read and base read do not match: "
								// + skew.readsPassingNode[i]);
								// System.out.println("color: " + read +
								// "\nbase: " + baseRead);
								// if (skew.readsPassingNode[i] % 2 == 0) {
								// System.out.println("Forward strand");
								// } else {
								// System.out.println("Reverse complement");
								// }
								// }
							} else {
								// no correction found!
								identifiedReads.set(readNumber);
							}
						}
					}
					// System.out.println("possible correction!");
				} else {
					// half readNumber to match with the readNumber in the
					// input-file.
					// int readNumber = skew.readsPassingNode[i] / 2;
					// identifiedReads.set(readNumber);
				}
			}
		}

		return mismatches;
	}

	// Correct a base space read by a mismatch (only N's!)
	// Return the number of corrected mismatches
	private int correctBaseSpaceMismatchN(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int mismatches = 0;

		SuffixNode skewChild = skew.getChildren(4);
		if (skewChild != null) {
			// Combined color can be different in different reads
			for (int combColor = 0; combColor < 4; combColor++) {
				for (AlphaSuffixNode reliable : reliableNodes) {
					// align any suspicious node with all the reliable ones and
					// look for matches

					Collection<String> matchingStrings = null;
					SuffixNode relChild = null;
					for (int i = 0; i < 4; i++) {
						relChild = reliable.getChildren(i);
						if (relChild == null)
							continue;

						if (combinedColors[color2int(reliable.getColor())][i] != combColor)
							continue;

						matchingStrings = skewChild.alignSubtree2(relChild);
						break;
					}

					if (relChild != null && matchingStrings != null) {
						// match found - identify the reads and correct them
						int readNumber;

						// change all reads that pass the skew-node
						for (int i = 0; i < skew.readIndex; i++) {
							// check that this is a base space read
							if (skew.readsPassingNode[i] / 2 >= ColorShrec.basereadcount) {
								continue;
							}

							if (reliable == null) {
								continue;
							}

							synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
								// get the read
								String read = new String(
										ColorShrec.reads[skew.readsPassingNode[i]]);
								String trimmedRead;

								// If the read is a complement, trim the last
								// color + base
								if (skew.readsPassingNode[i] % 2 == 1) {
									trimmedRead = read.substring(0,
											read.length() - 2);
								} else {
									trimmedRead = read;
								}

								// identify actual position of the error in the
								// read
								int errPos = trimmedRead.indexOf(prefix + key)
										+ prefix.length() + key.length();
								if (errPos >= trimmedRead.length()
										|| errPos < 0) {
									continue;
								}

								// Check that the combined color is right
								byte[] bread = ColorShrec.baseReads[skew.readsPassingNode[i] / 2];
								int pos = errPos;
								if (skew.readsPassingNode[i] % 2 == 1) {
									// reverse complement -> translate the
									// position
									pos = bread.length - pos - 1;
								}

								if (pos < 2 || pos >= bread.length) {
									continue;
								}

								if (bread[pos - 1] != 'N') {
									continue;
								}

								if (bread[pos - 2] == 'N' || bread[pos] == 'N')
									continue;

								if (combColor == combinedColors[base2int((char) bread[pos - 2])][base2int((char) bread[pos])]) {

									// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

									Iterator<String> possibleMatches = matchingStrings
											.iterator();
									boolean matchFound = false;

									while (possibleMatches.hasNext()) {
										String s = skew.getColor()
												+ possibleMatches.next();

										if (errPos + s.length() > trimmedRead
												.length()) {
											String endOfRead = trimmedRead
													.substring(errPos);

											if (s.startsWith(endOfRead)) {
												matchFound = true;
												break;
											}
										} else if (trimmedRead.subSequence(
												errPos, errPos + s.length())
												.equals(s)) {
											matchFound = true;
											break;
										}
									}

									// half readNumber to match with the
									// readNumber in the input-file.
									readNumber = skew.readsPassingNode[i] / 2;
									String baseRead = new String(bread);

									if (matchFound) {
										// correct the read
										String correctToColor = ""
												+ reliable.getColor()
												+ relChild.getColor();
										String correctToBase = "";

										try {
											read = read.substring(0, errPos)
													+ correctToColor
													+ read.substring(errPos + 2);

											if (skew.readsPassingNode[i] % 2 == 0) {
												correctToBase = baseColor2Base[base2int(baseRead
														.charAt(errPos - 2))][color2int(reliable
														.getColor())];
												baseRead = baseRead.substring(
														0, errPos - 1)
														+ correctToBase
														+ baseRead
																.substring(errPos);
											} else {
												int blen = baseRead.length();
												correctToBase = baseColor2Base[base2int(baseRead
														.charAt(blen - errPos
																- 1))][color2int(reliable
														.getColor())];
												baseRead = baseRead.substring(
														0, blen - errPos - 2)
														+ correctToBase
														+ baseRead
																.substring(blen
																		- errPos
																		- 1);
											}
											mismatches++;
										} catch (Exception e) {
										}

										// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

										// half readNumber to match with the
										// readNumber in the input-file.
										readNumber = skew.readsPassingNode[i] / 2;
										correctedReads.set(readNumber);
										// identifiedReads.set(readNumber);

										ColorShrec.reads[skew.readsPassingNode[i]] = read
												.getBytes();
										ColorShrec.baseReads[skew.readsPassingNode[i] / 2] = baseRead
												.getBytes();

										// correct the complement as well
										if (skew.readsPassingNode[i] % 2 == 0) {
											ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
													.getBytes());
										} else {
											ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
													.getBytes());
										}

										// read = new
										// String(Shrec.reads[readNumber*2]);
										// baseRead = new
										// String(Shrec.buildColorSpaceRead(Shrec.baseReads[readNumber]));

										// if (!read.equals(baseRead)) {
										// System.out.println("Mismatch: Corrected color read and base read do not match: "
										// + skew.readsPassingNode[i]);
										// System.out.println("color: " + read +
										// "\nbase: " + baseRead);
										// if (skew.readsPassingNode[i] % 2 ==
										// 0) {
										// System.out.println("Forward strand");
										// } else {
										// System.out.println("Reverse complement");
										// }
										// }
									} else {
										// no correction found!
										identifiedReads.set(readNumber);
									}
								}
							}
						}
						// System.out.println("possible correction!");
					} else {
						// half readNumber to match with the readNumber in the
						// input-file.
						// int readNumber = skew.readsPassingNode[i] / 2;
						// identifiedReads.set(readNumber);
					}
				}
			}
		}

		return mismatches;
	}

	// Correct a base space read by a deletion (no N's!)
	// Return the number of corrected deletions
	private int correctBaseSpaceDeletion(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int deletions = 0;

		for (int j = 0; j < 4; j++) {
			SuffixNode skewChild = skew.getChildren(j);
			if (skewChild == null)
				continue;

			int combColor = combinedColors[color2int(skew.getColor())][j];

			Collection<String> matchingStrings = null;
			for (SuffixNode reliable : reliableNodes) {
				if (color2int(reliable.getColor()) != combColor)
					continue;
				matchingStrings = skewChild.alignSubtree2(reliable);
				break;
			}

			if (matchingStrings != null && matchingStrings.size() != 0) {
				// match found - identify the reads and correct them
				int readNumber;

				// change all reads that pass the skew-node
				for (int i = 0; i < skew.readIndex; i++) {
					// check that this is a base space read
					if (skew.readsPassingNode[i] / 2 >= ColorShrec.basereadcount) {
						continue;
					}

					synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
						// get the read
						String read = new String(
								ColorShrec.reads[skew.readsPassingNode[i]]);
						String trimmedRead;

						// If the read is a complement, trim the last color +
						// base
						if (skew.readsPassingNode[i] % 2 == 1) {
							trimmedRead = read.substring(0, read.length() - 2);
						} else {
							trimmedRead = read;
						}

						// identify actual position of the error in the read
						int errPos = trimmedRead.indexOf(prefix + key)
								+ prefix.length() + key.length();
						if (errPos >= trimmedRead.length() || errPos < 0) {
							// System.out.println("Read: " + read + "Errpos: " +
							// errPos + " Prefix: " + prefix +
							// "Key: " + root.key);
							continue;
						}

						// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

						Iterator<String> possibleMatches = matchingStrings
								.iterator();
						boolean matchFound = false;

						while (possibleMatches.hasNext()) {
							String s = skew.getColor() + possibleMatches.next();
							if (errPos + s.length() > trimmedRead.length()) {
								String endOfRead = trimmedRead
										.substring(errPos);
								if (s.startsWith(endOfRead)) {
									matchFound = true;
									break;
								}
							} else if (trimmedRead.substring(errPos)
									.startsWith(s)) {
								matchFound = true;
								break;
							}
						}

						// half readNumber to match with the readNumber in the
						// input-file.
						readNumber = skew.readsPassingNode[i] / 2;
						String baseRead = new String(
								ColorShrec.baseReads[readNumber]);

						if (matchFound) {
							// correct the read
							try {
								read = read.substring(0, errPos) + combColor
										+ read.substring(errPos + 2);

								if (skew.readsPassingNode[i] % 2 == 0) {
									baseRead = baseRead
											.substring(0, errPos - 1)
											+ baseRead.substring(errPos);
								} else {
									int blen = baseRead.length();
									baseRead = baseRead.substring(0, blen
											- errPos - 2)
											+ baseRead.substring(blen - errPos
													- 1);
								}
								deletions++;
							} catch (Exception e) {
							}

							// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

							// half readNumber to match with the readNumber in
							// the input-file.
							readNumber = skew.readsPassingNode[i] / 2;
							correctedReads.set(readNumber);
							// identifiedReads.set(readNumber);

							ColorShrec.reads[skew.readsPassingNode[i]] = read
									.getBytes();
							ColorShrec.baseReads[skew.readsPassingNode[i] / 2] = baseRead
									.getBytes();

							// correct the complement as well
							if (skew.readsPassingNode[i] % 2 == 0) {
								ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
										.getBytes());
							} else {
								ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
										.getBytes());
							}

							// read = new String(Shrec.reads[readNumber*2]);
							// baseRead = new
							// String(Shrec.buildColorSpaceRead(Shrec.baseReads[readNumber]));

							// if (!read.equals(baseRead)) {
							// System.out.println("Deletion: Corrected color read and base read do not match: "
							// + skew.readsPassingNode[i]);
							// System.out.println("color: " + read + "\nbase: "
							// + baseRead);

							// if (skew.readsPassingNode[i] % 2 == 0) {
							// System.out.println("Forward strand");
							// } else {
							// System.out.println("Reverse complement");
							// }
							// }
						} else {
							// no correction found!
							identifiedReads.set(readNumber);
						}
					}
				}
				// System.out.println("possible correction!");
			} else {
				// half readNumber to match with the readNumber in the
				// input-file.
				// int readNumber = skew.readsPassingNode[i] / 2;
				// identifiedReads.set(readNumber);
			}
		}

		return deletions;
	}

	// Correct a base space read by a deletion (only N's)
	// Return the number of corrected deletions
	private int correctBaseSpaceDeletionN(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int deletions = 0;

		SuffixNode skewChild = skew.getChildren(4);
		if (skewChild != null) {
			// Combined color can be different in different reads
			for (int combColor = 0; combColor < 4; combColor++) {
				Collection<String> matchingStrings = null;
				for (SuffixNode reliable : reliableNodes) {
					if (color2int(reliable.getColor()) != combColor)
						continue;
					matchingStrings = skewChild.alignSubtree2(reliable);
					break;
				}

				if (matchingStrings != null && matchingStrings.size() != 0) {
					// match found - identify the reads and correct them
					int readNumber;

					// change all reads that pass the skew-node
					for (int i = 0; i < skew.readIndex; i++) {
						// check that this is a base space read
						if (skew.readsPassingNode[i] / 2 >= ColorShrec.basereadcount) {
							continue;
						}

						synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
							// get the read
							String read = new String(
									ColorShrec.reads[skew.readsPassingNode[i]]);
							String trimmedRead;

							// If the read is a complement, trim the last color
							// + base
							if (skew.readsPassingNode[i] % 2 == 1) {
								trimmedRead = read.substring(0,
										read.length() - 2);
							} else {
								trimmedRead = read;
							}

							// identify actual position of the error in the read
							int errPos = trimmedRead.indexOf(prefix + key)
									+ prefix.length() + key.length();
							if (errPos >= trimmedRead.length() || errPos < 0) {
								// System.out.println("Read: " + read +
								// "Errpos: " + errPos + " Prefix: " + prefix +
								// "Key: " + root.key);
								continue;
							}

							// Check that the combined color is right
							byte[] bread = ColorShrec.baseReads[skew.readsPassingNode[i] / 2];
							int pos = errPos;
							if (skew.readsPassingNode[i] % 2 == 1) {
								// reverse complement -> translate the position
								pos = bread.length - pos - 1;
							}

							if (pos < 2 || pos >= bread.length) {
								continue;
							}

							if (bread[pos - 1] != 'N') {
								continue;
							}

							if (bread[pos - 2] == 'N' || bread[pos] == 'N')
								continue;

							if (combColor == combinedColors[base2int((char) bread[pos - 2])][base2int((char) bread[pos])]) {

								// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

								Iterator<String> possibleMatches = matchingStrings
										.iterator();
								boolean matchFound = false;

								while (possibleMatches.hasNext()) {
									String s = skew.getColor()
											+ possibleMatches.next();
									if (errPos + s.length() > trimmedRead
											.length()) {
										String endOfRead = trimmedRead
												.substring(errPos);
										if (s.startsWith(endOfRead)) {
											matchFound = true;
											break;
										}

									} else if (trimmedRead.substring(errPos)
											.startsWith(s)) {
										matchFound = true;
										break;
									}
								}

								// half readNumber to match with the readNumber
								// in the input-file.
								readNumber = skew.readsPassingNode[i] / 2;
								String baseRead = new String(
										ColorShrec.baseReads[readNumber]);

								if (matchFound) {
									// correct the read
									try {
										read = read.substring(0, errPos)
												+ combColor
												+ read.substring(errPos + 2);
										if (skew.readsPassingNode[i] % 2 == 0) {
											baseRead = baseRead.substring(0,
													errPos - 1)
													+ baseRead
															.substring(errPos);
										} else {
											int blen = baseRead.length();
											baseRead = baseRead.substring(0,
													blen - errPos - 2)
													+ baseRead.substring(blen
															- errPos - 1);
										}
										deletions++;
									} catch (Exception e) {
									}
									// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

									// half readNumber to match with the
									// readNumber in the input-file.
									readNumber = skew.readsPassingNode[i] / 2;
									correctedReads.set(readNumber);
									// identifiedReads.set(readNumber);

									ColorShrec.reads[skew.readsPassingNode[i]] = read
											.getBytes();
									ColorShrec.baseReads[skew.readsPassingNode[i] / 2] = baseRead
											.getBytes();

									// correct the complement as well
									if (skew.readsPassingNode[i] % 2 == 0) {
										ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
												.getBytes());
									} else {
										ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
												.getBytes());
									}

									// read = new
									// String(Shrec.reads[readNumber*2]);
									// baseRead = new
									// String(Shrec.buildColorSpaceRead(Shrec.baseReads[readNumber]));

									// if (!read.equals(baseRead)) {
									// System.out.println("Deletion: Corrected color read and base read do not match: "
									// + skew.readsPassingNode[i]);
									// System.out.println("color: " + read +
									// "\nbase: " + baseRead);
									// if (skew.readsPassingNode[i] % 2 == 0) {
									// System.out.println("Forward strand");
									// } else {
									// System.out.println("Reverse complement");
									// }
									// }
								} else {
									// no correction found!
									identifiedReads.set(readNumber);
								}
							}
						}
					}
					// System.out.println("possible correction!");
				} else {
					// half readNumber to match with the readNumber in the
					// input-file.
					// int readNumber = skew.readsPassingNode[i] / 2;
					// identifiedReads.set(readNumber);
				}
			}
		}

		return deletions;
	}

	// Correct a base space read by an insertion
	// Return the number of corrected insertions
	private int correctBaseSpaceInsertion(String key, AlphaSuffixNode skew,
			ArrayList<AlphaSuffixNode> reliableNodes) {
		int insertions = 0;

		for (AlphaSuffixNode reliable : reliableNodes) {
			// align any suspicious node with all the reliable ones and look for
			// matches
			SuffixNode insertNode = null;
			for (int i = 0; i < 4; i++) {
				if (combinedColors[color2int((char) reliable.getColor())][i] == color2int(skew
						.getColor())) {
					insertNode = reliable.getChildren(i);
					break;
				}
			}

			if (insertNode != null) {
				HashSet<String> matchingStrings = new HashSet<String>();
				for (int i = 0; i < 4; i++) {
					SuffixNode ic, sc;
					ic = insertNode.getChildren(i);
					sc = skew.getChildren(i);
					if (ic == null || sc == null)
						continue;

					Collection<String> c = ic.alignSubtree2(sc);
					if (c != null) {
						// Take collection into this set
						Iterator<String> elements = c.iterator();
						while (elements.hasNext()) {
							String s = elements.next();
							matchingStrings.add(skew.getColor() + s);
						}
					}
				}

				if (matchingStrings != null && matchingStrings.size() > 0) {
					// match found - identify the reads and correct them
					int readNumber;

					// change all reads that pass the skew-node
					for (int i = 0; i < skew.readIndex; i++) {
						// check that this is a base space read
						if (skew.readsPassingNode[i] / 2 >= ColorShrec.basereadcount) {
							continue;
						}

						if (insertNode == null) {
							continue;
						}

						synchronized (ColorShrec.sync[skew.readsPassingNode[i] / 2]) {
							// get the read
							String read = new String(
									ColorShrec.reads[skew.readsPassingNode[i]]);
							String trimmedRead;

							// If the read is a complement, trim the last color
							// + base
							if (skew.readsPassingNode[i] % 2 == 1) {
								trimmedRead = read.substring(0,
										read.length() - 2);
							} else {
								trimmedRead = read;
							}

							// identify actual position of the error in the read
							int errPos = trimmedRead.indexOf(prefix + key)
									+ prefix.length() + key.length();
							if (errPos >= trimmedRead.length() || errPos < 0) {
								continue;
							}

							// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

							Iterator<String> possibleMatches = matchingStrings
									.iterator();
							boolean matchFound = false;

							while (possibleMatches.hasNext()) {
								String s = possibleMatches.next();
								if (errPos + s.length() > trimmedRead.length()) {
									String endOfRead = trimmedRead
											.substring(errPos);

									if (s.startsWith(endOfRead)) {
										matchFound = true;
										break;
									}
								} else if (trimmedRead.subSequence(errPos,
										errPos + s.length()).equals(s)) {
									matchFound = true;
									break;
								}
							}

							// half readNumber to match with the readNumber in
							// the input-file.
							readNumber = skew.readsPassingNode[i] / 2;
							String baseRead = new String(
									ColorShrec.baseReads[readNumber]);

							if (matchFound) {
								// correct the read
								String correctToColor = ""
										+ reliable.getColor()
										+ insertNode.getColor();
								String correctToBase = "";
								try {
									read = read.substring(0, errPos)
											+ correctToColor
											+ read.substring(errPos + 1);

									if (skew.readsPassingNode[i] % 2 == 0) {
										correctToBase = baseColor2Base[base2int(baseRead
												.charAt(errPos - 2))][color2int(reliable
												.getColor())];
										baseRead = baseRead.substring(0,
												errPos - 1)
												+ correctToBase
												+ baseRead
														.substring(errPos - 1);
									} else {
										int blen = baseRead.length();
										correctToBase = baseColor2Base[base2int(baseRead
												.charAt(blen - errPos - 1))][color2int(reliable
												.getColor())];
										baseRead = baseRead.substring(0, blen
												- errPos - 1)
												+ correctToBase
												+ baseRead.substring(blen
														- errPos - 1);
									}
									insertions++;
								} catch (Exception e) {
								}

								// XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

								// half readNumber to match with the readNumber
								// in the input-file.
								readNumber = skew.readsPassingNode[i] / 2;
								correctedReads.set(readNumber);
								// identifiedReads.set(readNumber);

								ColorShrec.reads[skew.readsPassingNode[i]] = read
										.getBytes();
								ColorShrec.baseReads[skew.readsPassingNode[i] / 2] = baseRead
										.getBytes();

								// correct the complement as well
								if (skew.readsPassingNode[i] % 2 == 0) {
									ColorShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read
											.getBytes());
								} else {
									ColorShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read
											.getBytes());
								}

								// read = new String(Shrec.reads[readNumber*2]);
								// baseRead = new
								// String(Shrec.buildColorSpaceRead(Shrec.baseReads[readNumber]));

								// if (!read.equals(baseRead)) {
								// System.out.println("Insertion: Corrected color read and base read do not match: "
								// + skew.readsPassingNode[i]);
								// System.out.println("color: " + read +
								// "\nbase: " + baseRead);
								// if (skew.readsPassingNode[i] % 2 == 0) {
								// System.out.println("Forward strand");
								// } else {
								// System.out.println("Reverse complement");
								// }
								// }
							} else {
								// no correction found!
								identifiedReads.set(readNumber);
							}
						}
					}
					// System.out.println("possible correction!");
				}
			} else {
				// half readNumber to match with the readNumber in the
				// input-file.
				// int readNumber = skew.readsPassingNode[i] / 2;
				// identifiedReads.set(readNumber);
			}
		}

		return insertions;
	}

	// this is where the action happens...
	public void buildAndProcess() {
		// statistics...
		int readsFound = 0;

		int mismatches = 0;
		int insertions = 0;
		int deletions = 0;

		int mismatches2 = 0;
		int insertions2 = 0;
		int deletions2 = 0;

		// all reads are scanned if they fit to our subtree
		for (int readNumber = 0; readNumber < ColorShrec.reads.length; readNumber++) {

			// get the current read
			byte[] read = ColorShrec.reads[readNumber];

			// analyse the read (skip adapter base + 1st color)
			int start = (readNumber % 2 == 0) ? 2 : 0;
			int len = (readNumber % 2 == 0) ? ColorShrec.reads[readNumber].length
					: ColorShrec.reads[readNumber].length - 2;

			for (int i = start; i < len - fromLevel; i++) {
				int j = 0;
				while (j < prefix.length()
						&& (char) read[i + j] == prefix.charAt(j)) {
					j++;
				}
				if (j == prefix.length()) {
					// found read which has a suffix matching with my prefix.
					// System.out.println("read "+readNumber+" has matching
					// substring");
					readsFound++;

					// Extract the Address for the hashtable from read
					String address = new String();
					for (int k = j; k < fromLevel; k++) {
						address += (char) read[i + k];
					}

					// System.out.println("address: "+address);
					// Get entry from hashtable
					SuffixNode node = subSubTree.get(address);
					if (node == null) {

						// SubSubtree doesn't exist yet -> create
						node = new RootNode((char) read[i + fromLevel],
								fromLevel, address);

						// visit the new root
						node.visit();
						subSubTree.put(address, (RootNode) node);
						// System.out.println("new adress: "+address+", read
						// "+readNumber);

					} else {

						// entry already exists - visit subsubtree
						node.visit();
					}

					// now take the rest of the read and visit or create
					// children under the rootNode
					for (int k = i + fromLevel; (k < len) && k < i + toLevel; k++) {

						// note: visitChildren creates a node if necessary.
						node.visitChildren((char) read[k], readNumber);
						node = node.getChildren((char) read[k]);
					}

				}

			}
		}
		// System.out.println(readsFound+" total number of reads in subtree");
		// System.out.println("nodes created: "+SuffixNode.totalNodes
		// +" and roots "+subSubTree.size());

		// Walk the tree and correct errors
		Enumeration<RootNode> roots = subSubTree.elements();

		// System.out.println("Number of roots: "+subSubTree.size());
		RootNode root;

		while (roots.hasMoreElements()) {
			root = roots.nextElement();

			// double ratio = root.calculateRatio();

			// if (ratio > 1.5) {
			// A skew node may be following this root - examine children!

			AlphaSuffixNode node;

			// System.out.println("Examine root "+root.key);

			ArrayList<AlphaSuffixNode> skewNodes = new ArrayList<AlphaSuffixNode>(
					0);
			ArrayList<AlphaSuffixNode> reliableNodes = new ArrayList<AlphaSuffixNode>(
					0);

			// sort children into reliable or suspicious nodes
			for (int i = 0; i < 4; i++) {
				node = (AlphaSuffixNode) root.getChildren(i);
				if (node != null) {
					if (node.visits < SuffixNode.expectedVisitsPerNode[node
							.getLevel()]) {
						skewNodes.add(node);
					} else {
						reliableNodes.add(node);
					}
				}
			}

			// Add the . child to skew nodes
			node = (AlphaSuffixNode) root.getChildren(4);
			if (node != null)
				skewNodes.add(node);

			if (skewNodes.size() > 0 && reliableNodes.size() > 0) {

				// analyse children...
				for (AlphaSuffixNode skew : skewNodes) {
					// correct color space reads first

					if (skew.colorReads) {
						int mism = correctColorSpaceMismatch(root.key, skew,
								reliableNodes);
						mismatches += mism;

						if (mism == 0) {
							// mismatch could not correct the error, try a
							// deletion
							int del = correctColorSpaceDeletion(root.key, skew,
									reliableNodes);
							deletions += del;

							if (del == 0 && skew.getColor() != '.') {
								// mismatch or deletion did not do the trick,
								// try insertion
								int ins = correctColorSpaceInsertion(root.key,
										skew, reliableNodes);
								insertions += ins;
							}
						}
					}
					if (skew.baseReads) {
						// correct a base space read

						if (skew.getColor() == '.') {
							// Handle . as a special case. Allow only mismatches
							// or deletions
							// try a mismatch first
							int mism = correctBaseSpaceMismatchN(root.key,
									skew, reliableNodes);
							mismatches2 += mism;

							if (mism == 0) {
								// mismatch did not correct the read, try a
								// deletion
								int del = correctBaseSpaceDeletionN(root.key,
										skew, reliableNodes);
								deletions2 += del;
							}
						} else {
							// Try a mismatch
							int mism = correctBaseSpaceMismatch(root.key, skew,
									reliableNodes);
							mismatches2 += mism;

							if (mism == 0) {
								// mismatch could not correct the error, try a
								// deletion
								int del = correctBaseSpaceDeletion(root.key,
										skew, reliableNodes);
								deletions2 += del;

								if (del == 0) {
									// mismatch or deletion did not do the
									// trick, try insertion
									int ins = correctBaseSpaceInsertion(
											root.key, skew, reliableNodes);
									insertions2 += ins;
								}
							}
						}
					}
				}
			} else {
				// no corretions made - analyse the nodes on possible errors
				// without possibilities for correction.
				// AlphaSuffixNode node;

				for (int i = 0; i < 5; i++) {

					node = (AlphaSuffixNode) root.getChildren(i);

					if (node != null
							&& node.visits < SuffixNode.expectedVisitsPerNode[node
									.getLevel()]) {

						int readNumber;

						for (int k = 0; k < node.readIndex; k++) {

							readNumber = node.readsPassingNode[k] / 2;

							identifiedReads.set(readNumber);
						}
					}
				}
			}
		}
		// Tree analysis finished: report to the supervisor
		ColorShrec.supervisor.processFinished(mismatches, insertions,
				deletions, mismatches2, insertions2, deletions2);
	}

	public static byte[] buildComplement(byte[] seq) {

		int len = seq.length;

		byte[] tba = new byte[len];

		for (int i = 0; i < len; i++) {

			tba[i] = seq[len - i - 1];

		}

		return tba;

	}
}

// this class is instantiated by the supervisor to start calculation and
// analysis of a subtree
class CalculateSubtreeProcess extends Thread {

	Subtree subtree;

	public CalculateSubtreeProcess(String prefix, int fromLevel, int toLevel,
			int numReads) {
		this.subtree = new Subtree(prefix, fromLevel, toLevel, numReads);
	}

	public void run() {

		// run the error-correction
		subtree.buildAndProcess();

	}
}

/*
 * controls the threads which are working on different parts of the tree:
 * threadsToLaunch threads are started simultaneously each with a different
 * prefix. Waits till threads report to have finished until no prefixes are
 * left. Then starts another round (y-loop) to correct reads with more than one
 * error. TODO: find an estimation for subtree-size based on the prefix:
 * Prefixes with lots of Cs and Gs are likely to result in small subtrees.
 */
class Supervisor extends Thread {

	// number of iterations the algorithm is to be run
	public static int numberOfIterations = 3;
	// the filenames for the textual output of reads
	public static String[] correctReadsFilename;
	public static String[] discardedReadsFilename;
	public static int[] readsInFile;
	public static boolean[] isColorSpaceFile;
	// the parameters fromLevel and toLevel indicate which parts of the tree are
	// to be built
	// toLevel increased from basic Shrec because correcting indels seems to
	// require more accuracy
	public static int fromLevel = 14, toLevel = 18;
	// the number of threads to be launched in parallel - adjust to system
	private int threadsToLaunch = 8;
	private int threadsLaunched = 0;
	// statistics...
	private int mismatches = 0;
	private int insertions = 0;
	private int deletions = 0;
	private int mismatches2 = 0;
	private int insertions2 = 0;
	private int deletions2 = 0;
	// Iterator to generate the prefixes for each subtree - can be exchanged
	// with any smarter routine
	private ColorStringIterator strings;

	public Supervisor() {
	}

	public void run() {

		// main loop: each iteration stands for a whole execution of the
		// algorithm
		for (int y = 0; y < numberOfIterations; y++) {

			// prepare fresh prefixes for the next iteration
			strings = new ColorStringIterator();

			// prepare a fresh BitSet - also for statistical purposes
			Subtree.identifiedReads = new BitSet(ColorShrec.readcount);

			// create the threads and run them
			CalculateSubtreeProcess p = new CalculateSubtreeProcess("00000",
					fromLevel, toLevel, ColorShrec.readcount);
			p.start();
			synchronized (this) {
				threadsLaunched++;
			}
			while (strings.hasNext()) {

				p = new CalculateSubtreeProcess(strings.next(), fromLevel,
						toLevel, ColorShrec.readcount);
				p.start();
				synchronized (this) {
					threadsLaunched++;
					while (threadsLaunched >= threadsToLaunch) {
						// maximum number of threads running -> suspend
						// supervisor
						try {
							wait();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}

			// Main part of the loop is done: all prefixes are handled
			// Wait for all threads still running to finish
			synchronized (this) {
				while (threadsLaunched > 0) {
					try {
						wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// statistics...
			System.out.println("Mismatches(color): " + mismatches
					+ " Deletions(color): " + deletions
					+ " Insertions(color): " + insertions);
			System.out.println("Mismatches(base): " + mismatches2
					+ " Deletions(base): " + deletions2 + " Insertions(base): "
					+ insertions2);

			System.out.println("Finished Round " + y + " - identified "
					+ Subtree.identifiedReads.cardinality() + " reads");

		}

		// the algorithm is done by now - write the results to the output
		// file(s)
		try {
			// for short runs (numberOfIterations low) it is practical to keep
			// all the reads
			// in one file that are either correct or corrected - because in the
			// last round reads
			// could be corrected but identified in the same time.
			// Even for longer runs it happens quite frequently (in low coverage
			// regions)
			// that reads are corrected the right way but still identified as
			// suspicous thus
			// incorrect.
			// TODO: work on a routine to identify with different values in the
			// last round.
			// Maybe write the number of visits of suspicous nodes with it so
			// the user or
			// external program may judge
			boolean allToOneFile = true;

			int i = 0;
			for (int j = 0; j < correctReadsFilename.length; j++) {
				PrintWriter outputCorrected = new PrintWriter(
						correctReadsFilename[j]);
				PrintWriter outputDiscarded = new PrintWriter(
						discardedReadsFilename[j]);
				String fastaComment;
				for (; i < readsInFile[j]; i++) {
					/*
					 * fastaComment = "> read number "+i+": identified ";
					 * if(Subtree.identifiedReads.get(i)) fastaComment += "1";
					 * else fastaComment += "0"; fastaComment += "; corrected ";
					 * if(Subtree.correctedReads.get(i)) fastaComment += "1";
					 * else fastaComment += "0"; output.println(fastaComment);
					 * output.println(new String(Shrec.reads[i]));
					 */
					if (j == 0) {
						fastaComment = "> read number " + i;
					} else {
						fastaComment = "> read number "
								+ (i - readsInFile[j - 1]);
					}
					byte[] bytes;
					if (isColorSpaceFile[j]) {
						bytes = ColorShrec.reads[i * 2];
					} else {
						bytes = ColorShrec.baseReads[i];
					}

					if (!allToOneFile) {
						if (Subtree.identifiedReads.get(i)) {
							outputDiscarded.println(fastaComment);
							outputDiscarded.println(new String(bytes));
						} else {
							if (Subtree.correctedReads.get(i)) {
								fastaComment += " (corrected)";
							}
							outputCorrected.println(fastaComment);
							outputCorrected.println(new String(bytes));
						}
					} else {
						if (!Subtree.identifiedReads.get(i)) {
							if (Subtree.correctedReads.get(i)) {
								fastaComment += " (corrected)";
							}
							outputCorrected.println(fastaComment);
							outputCorrected.println(new String(bytes));
						} else {
							if (Subtree.correctedReads.get(i)) {
								fastaComment += " (corrected) + (identified)";
								outputCorrected.println(fastaComment);
								outputCorrected.println(new String(bytes));
							} else {
								outputDiscarded.println(fastaComment);
								outputDiscarded.println(new String(bytes));
							}
						}
					}
				}
				outputCorrected.flush();
				outputCorrected.close();
				outputDiscarded.flush();
				outputDiscarded.close();
			}
		} catch (Exception e) {
			System.out.println("Excpetion caught when writing output files.");
			e.printStackTrace();
		}
	}

	public void processFinished(int mismatches, int insertions, int deletions,
			int mismatches2, int insertions2, int deletions2) {
		// System.out.println("Wake up!");
		synchronized (this) {
			this.mismatches += mismatches;
			this.insertions += insertions;
			this.deletions += deletions;
			this.mismatches2 += mismatches2;
			this.insertions2 += insertions2;
			this.deletions2 += deletions2;
			this.threadsLaunched--;
			this.notifyAll();
		}
	}
}

// An object used for synchronizing access to the reads
class Sync {

};

/*
 * the Shrec class contains the main routine, getting everything started, and
 * some (global :() variables.
 */
public class ColorShrec {

	// the reads from the input file
	public static byte[][] reads;
	public static byte[][] baseReads;
	public static Sync[] sync;
	// the Fasta Comments for each read
	// annotated files
	// public static ArrayList<String> fastaComments;
	// the number of reads in input-file.
	public static int readcount;
	public static int basereadcount;
	// for statitical purpose: we provide our input files with information about
	// errors,
	// so the algorithm can check its performance.
	// annotated files:
	public static BitSet erroneusReads;
	// the object controlling the algorithm
	public static Supervisor supervisor;
	//
	private static double strictness = 7.0;

	private static byte base2color[][] = { { '0', '1', '2', '3', '.' },
			{ '1', '0', '3', '2', '.' }, { '2', '3', '0', '1', '.' },
			{ '3', '2', '1', '0', '.' }, { '.', '.', '.', '.', '.' } };
	private static byte color2base[][] = { { 'A', 'C', 'G', 'T', 'N' },
			{ 'C', 'A', 'T', 'G', 'N' }, { 'G', 'T', 'A', 'C', 'N' },
			{ 'T', 'G', 'C', 'A', 'N' }, { 'N', 'N', 'N', 'N', 'N' } };

	public static byte[] buildColorSpaceRead(byte[] seq) {

		if (seq[1] == '0' || seq[1] == '1' || seq[1] == '2' || seq[1] == '3'
				|| seq[1] == '.') {
			return seq;
		}

		int len = seq.length;

		byte[] tba = new byte[len + 1];
		byte b1, b2;

		tba[0] = 'A';
		b1 = 0;

		for (int i = 0; i < len; i++) {
			switch (seq[i]) {
			case 'A':
				b2 = 0;
				break;
			case 'C':
				b2 = 1;
				break;
			case 'G':
				b2 = 2;
				break;
			case 'T':
				b2 = 3;
				break;
			case 'N':
				b2 = 4;
				break;
			default:
				b2 = 4;
			}

			tba[i + 1] = base2color[b1][b2];
			b1 = b2;
		}

		return tba;

	}

	public static byte[] buildBaseSpaceRead(byte[] seq) {

		if (seq[1] == 'A' || seq[1] == 'C' || seq[1] == 'G' || seq[1] == 'T'
				|| seq[1] == 'N') {
			return seq;
		}

		int len = seq.length;

		byte[] tba = new byte[len - 1];
		byte b1, b2;

		switch (seq[0]) {
		case 'A':
			b1 = 0;
			break;
		case 'C':
			b1 = 1;
			break;
		case 'G':
			b1 = 2;
			break;
		case 'T':
			b1 = 3;
			break;
		case 'N':
			b1 = 4;
			break;
		default:
			b1 = 4;
		}

		for (int i = 1; i < len; i++) {
			switch (seq[i]) {
			case '0':
				b2 = 0;
				break;
			case '1':
				b2 = 1;
				break;
			case '2':
				b2 = 2;
				break;
			case '3':
				b2 = 3;
				break;
			case '.':
				b2 = 4;
				break;
			default:
				b2 = 4;
			}

			tba[i - 1] = color2base[b1][b2];
			switch (tba[i - 1]) {
			case 'A':
				b1 = 0;
				break;
			case 'C':
				b1 = 1;
				break;
			case 'G':
				b1 = 2;
				break;
			case 'T':
				b1 = 3;
				break;
			case 'N':
				b1 = 4;
				break;
			default:
				b1 = 4;
			}
		}

		return tba;

	}

	public static void usage() throws Exception {
		System.err.println("Usage: java Shrec -n <genome length> "
				+ "[-i <number of iterations>] [-s <strictness>] "
				+ "[-f <from level>] [-t <to level>] [-c <cutoff>] "
				+ "<input basespace files> <input colorspace files>");
		System.exit(1);
	}

	// the main routine handles parsing the input file and starting the
	// supervisor.
	public static void main(String[] args) throws Exception {
		long start, end;

		start = System.currentTimeMillis();

		int inputFilesIndex = -1;
		int genomeLength = 0;
		int cutoff = 0;

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-n")) {
					if (i == args.length - 1)
						usage();
					genomeLength = Integer.parseInt(args[i + 1]);
					i++;
				} else if (args[i].equals("-i")) {
					if (i == args.length - 1)
						usage();
					Supervisor.numberOfIterations = Integer
							.parseInt(args[i + 1]);
					i++;
				} else if (args[i].equals("-s")) {
					if (i == args.length - 1)
						usage();
					strictness = Integer.parseInt(args[i + 1]);
					i++;
				} else if (args[i].equals("-f")) {
					if (i == args.length - 1)
						usage();
					Supervisor.fromLevel = Integer.parseInt(args[i + 1]);
					i++;
				} else if (args[i].equals("-t")) {
					if (i == args.length - 1)
						usage();
					Supervisor.toLevel = Integer.parseInt(args[i + 1]);
					i++;
				} else if (args[i].equals("-c")) {
					if (i == args.length - 1)
						usage();
					cutoff = Integer.parseInt(args[i + 1]);
					i++;
				} else {
					usage();
				}
			} else {
				inputFilesIndex = i;
				break;
			}
		}

		System.out.println("Genome length set to " + genomeLength);
		System.out.println("Shrec will perform "
				+ Supervisor.numberOfIterations + " iterations");
		if (cutoff == 0) {
			System.out.println("Strictness set to " + strictness);
		} else {
			System.out.println("Cutoff set to " + cutoff);
		}

		System.out.println("Building trie from level " + Supervisor.fromLevel
				+ " to level " + Supervisor.toLevel);

		if (genomeLength <= 0) {
			usage();
		}

		if (Supervisor.toLevel <= Supervisor.fromLevel) {
			usage();
		}

		if (Supervisor.numberOfIterations <= 0
				|| Supervisor.numberOfIterations >= 15) {
			usage();
		}

		// parse the input files
		ColorShrec.readcount = 0;

		Supervisor.correctReadsFilename = new String[args.length
				- inputFilesIndex];
		Supervisor.discardedReadsFilename = new String[args.length
				- inputFilesIndex];
		Supervisor.readsInFile = new int[args.length - inputFilesIndex];
		Supervisor.isColorSpaceFile = new boolean[args.length - inputFilesIndex];

		ArrayList<byte[]> readsFromFile = new ArrayList<byte[]>();
		ArrayList<byte[]> basespaceReads = new ArrayList<byte[]>();

		boolean colorspace = false;
		basereadcount = 0;

		for (int i = inputFilesIndex; i < args.length; i++) {
			Supervisor.correctReadsFilename[i - inputFilesIndex] = args[i]
					+ ".corrected";
			Supervisor.discardedReadsFilename[i - inputFilesIndex] = args[i]
					+ ".discarded";

			// start parsing the input file
			System.out.println("Reading reads from file " + args[i]);
			FileReader f = new FileReader(args[i]);

			BufferedReader b = new BufferedReader(f);

			// Load the reads from file, making note of those with errors

			String line = b.readLine(); // fasta comment
			while (line != null) {

				line = b.readLine();

				byte[] bytes = line.getBytes();
				byte c = bytes[1];
				if (c == 'A' || c == 'C' || c == 'G' || c == 'T' || c == 'N') {
					if (colorspace) {
						usage();
					}
				} else if (c == '0' || c == '1' || c == '2' || c == '3'
						|| c == '.') {
					if (!colorspace) {
						colorspace = true;
						basereadcount = readcount;
					}
				} else {
					System.out.println("Invalid file format");
					usage();
				}

				// Trim indeterminate characters from the end
				if (colorspace) {
					int ii = bytes.length - 1;
					while (ii >= 0 && bytes[ii] == '.')
						ii--;
					if (ii != bytes.length - 1) {
						byte[] bytes2 = new byte[ii + 1];
						for (int j = 0; j <= ii; j++)
							bytes2[j] = bytes[j];
						bytes = bytes2;
					}
				} else {
					int ii = bytes.length - 1;
					while (ii >= 0 && bytes[ii] == 'N')
						ii--;
					if (ii != bytes.length - 1) {
						byte[] bytes2 = new byte[ii + 1];
						for (int j = 0; j <= ii; j++)
							bytes2[j] = bytes[j];
						bytes = bytes2;
					}
				}

				if (!colorspace) {
					basespaceReads.add(bytes);
					bytes = buildColorSpaceRead(bytes);
				}

				readsFromFile.add(bytes);
				readsFromFile.add(Subtree.buildComplement(bytes));

				line = b.readLine(); // fasta comment

				readcount++;
			}
			Supervisor.readsInFile[i - inputFilesIndex] = readcount;
			Supervisor.isColorSpaceFile[i - inputFilesIndex] = colorspace;
			b.close();
		}

		if (!colorspace) {
			basereadcount = readcount;
		}

		byte[][] reads = new byte[readsFromFile.size()][];

		readsFromFile.toArray(reads);

		readsFromFile = null; // let the gc do it's work

		byte[][] baseReads = new byte[basespaceReads.size()][];
		basespaceReads.toArray(baseReads);
		basespaceReads = null;

		System.out.println(basereadcount + " base space reads and "
				+ (readcount - basereadcount) + " color space reads.");

		// Reads are loaded, initialize the synchronization objects
		sync = new Sync[reads.length / 2];
		for (int i = 0; i < sync.length; i++) {
			sync[i] = new Sync();
		}

		// Reads are loaded; initialize the expected visits per node.
		double[] expectedVisitsPerNode = new double[30];

		// ORIGINAL CODE FRAGMENTS
		// double hitsPerPosition = (double) reads.length / (double)
		// genomeLength;
		// int readLength = reads[1].length;

		for (int i = 1; i < expectedVisitsPerNode.length; i++) {

			// ORIGINAL CODE FRAGMENTS
			// double a = readLength - i + 1;
			// expectedVisitsPerNode[i] = a * hitsPerPosition; // Expected Value
			// double standardDeviation = Math.sqrt((a / (double) genomeLength -
			// Math.pow(a, 2) / Math.pow(genomeLength, 2)) * reads.length);

			// ADJUSTED MODEL
			double tmp1 = 0;
			double tmp2 = 0;
			for (int n = 0; n < reads.length; n++) {
				double a = reads[n].length - i + 1;
				tmp1 += (a / (double) genomeLength - Math.pow(a, 2)
						/ Math.pow(genomeLength, 2));
				tmp2 += (a / (double) genomeLength);
			}
			double standardDeviation = Math.sqrt(tmp1);
			expectedVisitsPerNode[i] = tmp2;

			if (cutoff == 0) {
				expectedVisitsPerNode[i] -= strictness * standardDeviation; // Correction
			} else {
				expectedVisitsPerNode[i] = cutoff;
			}

			if (i == Supervisor.fromLevel + 1 && expectedVisitsPerNode[i] < 1) {
				System.err
						.println("Expected visits on analysed level too low: "
								+ expectedVisitsPerNode[i] + "!");
				System.err
						.println("try a stricter configuration (lower value for strictness)");
				System.exit(1);
			}

			// System.out.println("Expected Visits for level "+i+":
			// "+expectedVisitsPerNode[i]);

		}

		SuffixNode.expectedVisitsPerNode = expectedVisitsPerNode;

		System.out.println("Expected visits on analyzed level: "
				+ expectedVisitsPerNode[Supervisor.fromLevel + 1]);

		// Get on with building and processing the tree

		Subtree.correctedReads = new BitSet(readcount);
		Subtree.identifiedReads = new BitSet(readcount);

		ColorShrec.reads = reads;
		ColorShrec.baseReads = baseReads;

		System.out.println("All set; starting error correction.");

		ColorShrec.supervisor = new Supervisor();
		supervisor.start();
		supervisor.join();

		int numCorrected = Subtree.correctedReads.cardinality();

		int numIdentified = Subtree.identifiedReads.cardinality();

		System.out.println(numCorrected + " reads corrected.");

		System.out.println(numIdentified + " reads (additionally) identified.");

		System.out.println("Error Correction Complete");

		end = System.currentTimeMillis();
		double seconds = ((double) (end - start)) / 1000;

		System.out.println("Calculation took " + seconds + " seconds...");
	}
}
