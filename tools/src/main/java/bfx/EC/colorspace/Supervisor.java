package bfx.EC.colorspace;

import java.io.PrintWriter;
import java.util.BitSet;

/*
 * controls the threads which are working on different parts of the tree:
 * threadsToLaunch threads are started simultaneously each with a different
 * prefix. Waits till threads report to have finished until no prefixes are
 * left. Then starts another round (y-loop) to correct reads with more than one
 * error. TODO: find an estimation for subtree-size based on the prefix:
 * Prefixes with lots of Cs and Gs are likely to result in small subtrees.
 */
public class Supervisor extends Thread {

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
