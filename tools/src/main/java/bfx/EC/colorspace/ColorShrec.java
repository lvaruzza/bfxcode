package bfx.EC.colorspace;

import java.util.ArrayList;
import java.util.BitSet;

import java.util.*;

import java.io.*;


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
