package bfx.tools.EC;

import java.util.ArrayList;
import java.util.BitSet;

import bfx.Sequence;
import bfx.EC.colorspace.Subtree;
import bfx.EC.colorspace.SuffixNode;
import bfx.EC.colorspace.Supervisor;
import bfx.io.SequenceSource;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;

public class ShrecTool extends Tool {

	@Parameter(names = {"--genomeLength","-g"}, description = "Input File",required=true)
	public long genomeLength;
	
	@Parameter(names = {"--input","-i"}, description = "Input File",required=true)
	public String input;

	//@Parameter(names = {"--qual","-q"}, description = "Qual file (only applicable for fasta format)")
	//public String qual;
	
	@Parameter(names = {"--inputFormat","-if"}, description = "Input Format")
	public String inputFormat;
	
	@Parameter(names = {"--output","-o"}, description = "Output File")
	public String output;

	//@Parameter(names = {"--outputQual","-oq"}, description = "Output Qual File (only appliable for fasta format)")
	//public String outputQual;
	
	@Parameter(names = {"--outputFormat","-of"}, description = "Output Format")
	public String outputFormat;

	
    // the reads from the input file
    public static byte[][] reads;
    // the Fasta Comments for each read
    // annotated files
    // public static ArrayList<String> fastaComments;
    // the number of reads in input-file.
    public static int readcount;
    // for statitical purpose: we provide our input files with information about errors,
    // so the algorithm can check its performance.
    // annotated files:
    public static BitSet erroneusReads;
    // the object controlling the algorithm
    public static Supervisor supervisor;
    //
    private static double strictness = 7.0;
	
    private int cutoff = 0;

	
	@Override
	public void run() throws Exception {
		Supervisor supervisor = new Supervisor();
		//supervisor.numberOfIterations = 3;
		//supervisor.fromLevel = ??
		
		Supervisor.readsInFile = new int[1];
		Supervisor.correctReadsFilename =  new String[]{this.output};
		Supervisor.discardedReadsFilename = new String[]{this.input + ".discarted"};
		
		SequenceSource src = SequenceSource.fromFile(inputFormat, input);
		ArrayList<byte[]> readsFromFile = new ArrayList<byte[]>();
		int readCount = 0;
		for(Sequence seq: src) {
            readsFromFile.add(seq.getSeq());
            readsFromFile.add(Subtree.buildComplement(seq.getSeq()));
            readCount++;    
		}
		
		Supervisor.readsInFile[0] = readCount;
		
        reads = new byte[readsFromFile.size()][];

        readsFromFile.toArray(reads);

        readsFromFile = null; // let the gc do it's work

        System.out.println(readCount + " base space reads");
        
        // Reads are loaded; initialize the expected visits per node.
        double[] expectedVisitsPerNode = new double[30];

        //ORIGINAL CODE FRAGMENTS
        //double hitsPerPosition = (double) reads.length / (double) genomeLength;
        //int readLength = reads[1].length;

        for (int i = 1; i < expectedVisitsPerNode.length; i++) {

            //ORIGINAL CODE FRAGMENTS
            //double a = readLength - i + 1;
            //expectedVisitsPerNode[i] = a * hitsPerPosition; // Expected Value
            //double standardDeviation = Math.sqrt((a / (double) genomeLength - Math.pow(a, 2) / Math.pow(genomeLength, 2)) * reads.length);

            //ADJUSTED MODEL
            double tmp1 = 0;
            double tmp2 = 0;
            for (int n = 0; n < reads.length; n++) {
                double a = reads[n].length - i + 1;
                tmp1 += (a / (double) genomeLength - Math.pow(a, 2) / Math.pow(genomeLength, 2));
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
                System.err.println("Expected visits on analysed level too low: " + expectedVisitsPerNode[i] + "!");
                System.err.println("try a stricter configuration (lower value for strictness)");
                System.exit(1);
            }

            // System.out.println("Expected Visits for level "+i+":
            // "+expectedVisitsPerNode[i]);

        }

        SuffixNode.expectedVisitsPerNode = expectedVisitsPerNode;

        System.out.println("Expected visits on analyzed level: " + expectedVisitsPerNode[Supervisor.fromLevel+1]);

        // Get on with building and processing the tree

        Subtree.correctedReads = new BitSet(readcount);

		

        System.out.println("All set; starting error correction.");

        supervisor.start();
        supervisor.join();

        int numCorrected = Subtree.correctedReads.cardinality();

        int numIdentified = Subtree.identifiedReads.cardinality();

        System.out.println(numCorrected + " reads corrected.");

        System.out.println(numIdentified + " reads (additionally) identified.");

        System.out.println("Error Correction Complete");

        }

	@Override
	public String getName() {
		return "shrec";
	}

	@Override
	public String getGroup() {
		return "sequence";
	}

}
