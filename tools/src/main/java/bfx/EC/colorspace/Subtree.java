package bfx.EC.colorspace;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

//This is the main class for the algorithm: it builds the tree and performs
//error-correction.
//builds only the subtree rooted under "prefix" and only the levels passed as a
//parameter.
public class Subtree {

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

//this class is instantiated by the supervisor to start calculation and
//analysis of a subtree
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
