package bfx.EC.basespace;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;


// This is the main class for the algorithm: it builds the tree and performs error-correction.
// builds only the subtree rooted under "prefix" and only the levels passed as a parameter.
public class Subtree {

    // the prefix that a read has to contain before being taken into this subtree
    private String prefix;
    // the levels of the tree which are actually kept track of and later to be analyzed
    private int fromLevel, toLevel;
    // the entry point into all the subtrees at "fromLevel" in this subtree
    private Hashtable<String, RootNode> subSubTree;
    // The place where the correctedReads are marked.
    public static BitSet correctedReads, identifiedReads;
    // Guess...
    private char int2base[] = {'A', 'C', 'G', 'T'};

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
            System.err.println("Cannot build subtree with such a short prefix!");
        }
        this.subSubTree = new Hashtable<String, RootNode>();
        // correctedReads = new BitSet(numReads);
    }

    // this is where the action happens...
    public void buildAndProcess() {
        //statistics...
        int aligned = 0;
        int readsFound = 0;

        int mismatches = 0;
        int insertions = 0;
        int deletions = 0;


        // all reads are scanned if they fit to our subtree
        for (int readNumber = 0; readNumber < BaseShrec.reads.length; readNumber++) {

            //get the current read
            byte[] read = BaseShrec.reads[readNumber];

            //analyse the read
            for (int i = 0; i < BaseShrec.reads[readNumber].length - fromLevel; i++) {
                int j = 0;
                while (j < prefix.length() && (char) read[i + j] == prefix.charAt(j)) {
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

                    // now take the rest of the read and visit or create children under the rootNode
                    for (int k = i + fromLevel; (k < read.length) && k < i + toLevel; k++) {

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

//             double ratio = root.calculateRatio();

//             if (ratio > 1.5) {

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
                    if (node.visits < SuffixNode.expectedVisitsPerNode[node.getLevel()]) {
                        skewNodes.add(node);
                    } else {
                        reliableNodes.add(node);
                    }
                }
            }

            node = (AlphaSuffixNode) root.getChildren(4);
            if (node != null)
                skewNodes.add(node);

            if (skewNodes.size() > 0 && reliableNodes.size() > 0) {
                // analyse children...
                for (AlphaSuffixNode skew : skewNodes) {
                    // Only do one kind of correction (mismatch, insertion, deletions) at a time
                    boolean corrected = false;
                    for (AlphaSuffixNode reliable : reliableNodes) {

                        // align any suspicious node with all the reliable ones and look for matches

                        Collection<String> matchingStrings = skew.alignSubtree2(reliable);
                        if (matchingStrings != null) {

                            // match found - identify the reads and correct them

                            // statistics...
                            for (int k = 0; k < skew.readIndex; k++) {
                                if (!correctedReads.get(skew.readsPassingNode[k] / 2)) {
                                    aligned++;
                                }
                            }

                            int readNumber;

                            // change all reads that pass the skew-node
                            for (int i = 0; i < skew.readIndex; i++) {




                                if (reliable == null) {
                                    continue;
                                }

                                // get the read
                                String read = new String(
                                        BaseShrec.reads[skew.readsPassingNode[i]]);

                                // identify actual position of the error in the read
                                int errPos = read.indexOf(prefix + root.key) + prefix.length() + root.key.length();
                                if (errPos >= read.length() || errPos < 0) {
                                    /*
                                     * System.out.println("Read: " + read + "
                                     * Errpos: " + errPos + " Prefix: " + prefix + "
                                     * Key: " + root.key);
                                     */
                                    continue;
                                }

                                //XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

                                Iterator<String> possibleMatches = matchingStrings.iterator();
                                boolean matchFound = false;

                                while (possibleMatches.hasNext()) {

                                    String s = possibleMatches.next();

                                    if (errPos + s.length() > read.length()) {

                                        String endOfRead = read.substring(errPos);

                                        if (s.startsWith(endOfRead)) {
                                            matchFound = true;
                                            break;
                                        }

                                    } else if (read.substring(errPos, errPos + s.length()).equals(s)) {
                                        matchFound = true;
                                        break;

                                    }

                                }

                                // half readNumber to match with the readNumber in the input-file.
                                readNumber = skew.readsPassingNode[i] / 2;

                                if (matchFound) {

                                    // correct the read
                                    char correctToBase = reliable.getBase();
                                    try {
                                        read = read.substring(0, errPos) + correctToBase + read.substring(errPos + 1);
                                        mismatches++;
                                    } catch (Exception e) {
                                    }

                                    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXX


                                    // half readNumber to match with the readNumber in the input-file.
                                    readNumber = skew.readsPassingNode[i] / 2;
                                    correctedReads.set(readNumber);
                                    // identifiedReads.set(readNumber);



                                    BaseShrec.reads[skew.readsPassingNode[i]] = read.getBytes();

                                    // correct the complement as well

                                    if (skew.readsPassingNode[i] % 2 == 0) {
                                        BaseShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read.getBytes());
                                    } else {
                                        BaseShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read.getBytes());

                                    }
                                } else {

                                    // no correction found!

                                    identifiedReads.set(readNumber);
                                }

                            }
                            corrected = true;
                            break;
                            // System.out.println("possible correction!");
                        } else {
                            // half readNumber to match with the readNumber in the input-file.
                            //int readNumber = skew.readsPassingNode[i] / 2;
                            //identifiedReads.set(readNumber);
                        }
                    }

                    if (!corrected) {
                        // mismatch could not correct the error, try a deletion

                        HashSet<String> matchingStrings = new HashSet<String>();
                        for (SuffixNode reliable : reliableNodes) {
                            SuffixNode sn = skew.getChildren(reliable.getBase());
                            if (sn != null) {
                                Collection<String> c = sn.alignSubtree2(reliable);
                                if (c != null) {
                                    //Take collection into this set
                                    Iterator<String> elements = c.iterator();
                                    while (elements.hasNext()) {
                                        String s = elements.next();
                                        matchingStrings.add(s);
                                    }
                                }
                            }
                        }

                        
                        if (matchingStrings.size() != 0) {
                            // match found - identify the reads and correct them

                            // statistics...
                            for (int k = 0; k < skew.readIndex; k++) {
                                if (!correctedReads.get(skew.readsPassingNode[k] / 2)) {
                                    aligned++;
                                }
                            }

                            int readNumber;

                            // change all reads that pass the skew-node
                            for (int i = 0; i < skew.readIndex; i++) {

                                // get the read
                                String read = new String(
                                        BaseShrec.reads[skew.readsPassingNode[i]]);

                                // identify actual position of the error in the read
                                int errPos = read.indexOf(prefix + root.key) + prefix.length() + root.key.length();
                                if (errPos >= read.length() || errPos < 0) {
                                    
                                    // System.out.println("Read: " + read + "Errpos: " + errPos + " Prefix: " + prefix + 
                                    //                    "Key: " + root.key);
                                    
                                    continue;
                                }

                                //XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

                                Iterator<String> possibleMatches = matchingStrings.iterator();
                                boolean matchFound = false;

                                while (possibleMatches.hasNext()) {

                                    String s = possibleMatches.next();

                                    if (errPos+1 + s.length() > read.length()) {

                                        String endOfRead = read.substring(errPos+1);


                                        if (s.startsWith(endOfRead)) {
                                            matchFound = true;
                                            break;
                                        }

                                    } else if (read.substring(errPos+1).startsWith(s)) {
                                        matchFound = true;
                                        break;

                                    }

                                }

                                // half readNumber to match with the readNumber in the input-file.
                                readNumber = skew.readsPassingNode[i] / 2;

                                if (matchFound) {

                                    // correct the read
                                    try {
                                        read = read.substring(0, errPos) + read.substring(errPos + 1);
                                        deletions++;
                                    } catch (Exception e) {
                                    }

                                    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXX


                                    // half readNumber to match with the readNumber in the input-file.
                                    readNumber = skew.readsPassingNode[i] / 2;
                                    correctedReads.set(readNumber);
                                    // identifiedReads.set(readNumber);



                                    BaseShrec.reads[skew.readsPassingNode[i]] = read.getBytes();

                                    // correct the complement as well

                                    if (skew.readsPassingNode[i] % 2 == 0) {
                                        BaseShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read.getBytes());
                                    } else {
                                        BaseShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read.getBytes());

                                    }
                                } else {

                                    // no correction found!

                                    identifiedReads.set(readNumber);
                                }

                            }
                            corrected = true;
                            break;
                            // System.out.println("possible correction!");
                        } else {
                            // half readNumber to match with the readNumber in the input-file.
                            //int readNumber = skew.readsPassingNode[i] / 2;
                            //identifiedReads.set(readNumber);
                        }

                    }

                    if (!corrected && skew.getBase() != 'N') {

                        // mismatch or deletion did not do the trick, try insertion
                        for (AlphaSuffixNode reliable : reliableNodes) {
                        
                            // align any suspicious node with all the reliable ones and look for matches

                            SuffixNode insertNode = reliable.getChildren(skew.getBase());
                            if (insertNode != null) {
                                
                                Collection<String> matchingStrings = insertNode.alignSubtree2(skew);
                                if (matchingStrings != null) {
                                    // match found - identify the reads and correct them

                                    // statistics...
                                    for (int k = 0; k < skew.readIndex; k++) {
                                        if (!correctedReads.get(skew.readsPassingNode[k] / 2)) {
                                            aligned++;
                                        }
                                    }

                                    int readNumber;

                                    // change all reads that pass the skew-node
                                    for (int i = 0; i < skew.readIndex; i++) {


                                        if (insertNode == null) {
                                            continue;
                                        }

                                        // get the read
                                        String read = new String(
                                                                 BaseShrec.reads[skew.readsPassingNode[i]]);

                                        // identify actual position of the error in the read
                                        int errPos = read.indexOf(prefix + root.key) + prefix.length() + root.key.length();
                                        if (errPos >= read.length() || errPos < 0) {
                                            /*
                                             * System.out.println("Read: " + read + "
                                             * Errpos: " + errPos + " Prefix: " + prefix + "
                                             * Key: " + root.key);
                                             */
                                            continue;
                                        }

                                        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXX

                                        Iterator<String> possibleMatches = matchingStrings.iterator();
                                        boolean matchFound = false;

                                        while (possibleMatches.hasNext()) {
                                            
                                            String s = possibleMatches.next();

                                            if (errPos + s.length() > read.length()) {

                                                String endOfRead = read.substring(errPos);

                                                if (s.startsWith(endOfRead)) {
                                                    matchFound = true;
                                                    break;
                                                }

                                            } else if (read.subSequence(errPos, errPos + s.length()).equals(s)) {
                                                matchFound = true;
                                                break;
                                                
                                            }
                                            
                                        }

                                        // half readNumber to match with the readNumber in the input-file.
                                        readNumber = skew.readsPassingNode[i] / 2;

                                        if (matchFound) {

                                            // correct the read
                                            char correctToBase = reliable.getBase();
                                            try {
                                                read = read.substring(0, errPos) + correctToBase + read.substring(errPos);
                                                insertions++;
                                            } catch (Exception e) {
                                            }

                                            //XXXXXXXXXXXXXXXXXXXXXXXXXXXXX


                                            // half readNumber to match with the readNumber in the input-file.
                                            readNumber = skew.readsPassingNode[i] / 2;
                                            correctedReads.set(readNumber);
                                            // identifiedReads.set(readNumber);


                                            BaseShrec.reads[skew.readsPassingNode[i]] = read.getBytes();

                                            // correct the complement as well

                                            if (skew.readsPassingNode[i] % 2 == 0) {
                                                BaseShrec.reads[skew.readsPassingNode[i] + 1] = buildComplement(read.getBytes());
                                            } else {
                                                BaseShrec.reads[skew.readsPassingNode[i] - 1] = buildComplement(read.getBytes());
                                                
                                            }
                                        } else {
                                            
                                            // no correction found!

                                            identifiedReads.set(readNumber);
                                        }
                                        
                                    }
                                    corrected = true;
                                    break;
                                    // System.out.println("possible correction!");
                                }
                            } else {
                                // half readNumber to match with the readNumber in the input-file.
                                //int readNumber = skew.readsPassingNode[i] / 2;
                                //identifiedReads.set(readNumber);
                            }
                        }
                    }

                }
                // no corretions made - analyse the nodes on possible errors without possibilities for correction.
            } else {
//                 AlphaSuffixNode node;

                for (int i = 0; i < 5; i++) {

                    node = (AlphaSuffixNode) root.getChildren(i);

                    if (node != null && node.visits < SuffixNode.expectedVisitsPerNode[node.getLevel()]) {

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
        BaseShrec.supervisor.processFinished(aligned, mismatches, insertions, deletions);
    }

    public static byte[] buildComplement(byte[] seq) {

        int len = seq.length;

        byte[] tba = new byte[len];

        for (int i = 0; i < len; i++) {

            switch (seq[len - i - 1]) {

                case 'A':
                    tba[i] = 'T';
                    break;

                case 'C':
                    tba[i] = 'G';
                    break;

                case 'G':
                    tba[i] = 'C';
                    break;

                case 'T':
                    tba[i] = 'A';
                    break;

                case 'N':
                    tba[i] = 'N';
                    break;

                default:
                    System.out.println("Invalid Character" + (new String(seq)));

                    System.exit(1);

            }

        }

        return tba;

    }
}


//this class is instantiated by the supervisor to start calculation and analysis of a subtree
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

