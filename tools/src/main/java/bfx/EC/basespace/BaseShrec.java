package bfx.EC.basespace;

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
    private static int _base2int[] = new int[256];

    static {

        for (int i = 0; i < 256; i++) {

            _base2int[i] = -1;

        }

        _base2int['A'] = 0;

        _base2int['C'] = 1;

        _base2int['G'] = 2;

        _base2int['T'] = 3;

        _base2int['N'] = 4;

    }
    // the parent of this node
    public SuffixNode parent;
    // the beautiful children
    public SuffixNode[] children;
    // the label on the incoming branch
    private byte base;
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
    // Set by the main routine: Gives the error-correction a clue which nodes are erroneus
    public static double[] expectedVisitsPerNode;

    public SuffixNode(char base, int level, SuffixNode parent) {

        this.parent = parent;

        this.base = (byte) base;

        this.level = (byte) level;

        this.visits = 0;

        totalNodes++;

    }

    private int base2int(char base) {

        return _base2int[base];

    }

    public SuffixNode getChildren(char base) {

        return getChildren(base2int(base));

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
    public void visitChildren(char base, int readNumber) {

        int k = base2int(base);

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

                children[k] = new AlphaSuffixNode(base, (level + 1), this);

            } else {

                children[k] = new SuffixNode(base, (level + 1), this);

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

    public char getBase() {

        return (char) this.base;

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
    // this routine is used to see if a correction of this.base to another base makes sense
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
            s.add("" + (char) this.base);
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
                    Collection<String> c = nextNode.alignSubtree2(nextNeighbour);
                    if (c != null) {
                        //Take collection into this set
                        Iterator<String> elements = c.iterator();
                        while (elements.hasNext()) {
                            String s = elements.next();
                            matchingStrings.add((char) this.base + s);
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
        for (int i = 0; i < 4; i++) {
            child = this.getChildren(i);
            if (child != null) {
                subnodes += child.countNodes();
            }
        }
        return subnodes + 1;
    }
}

/* AlphaSuffixNodes is used for all nodes on the level right under the root-nodes.
 * It provides an additional list of reads that pass a node which is used to identify the
 * according reads during error-correction
 */
class AlphaSuffixNode extends SuffixNode {

    int[] readsPassingNode;
    int readIndex;

    public AlphaSuffixNode(char base, int level, SuffixNode parent) {
        super(base, level, parent);

        this.readsPassingNode = new int[(int) Math.round(expectedVisitsPerNode[level])];
        this.readIndex = 0;
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
        }
    }
}

/* the rootNodes are each roots of a subtree.
 * The RootNodes children are AlphaSufixNodes.
 * RootNode.key keeps track of the key which the root has in the hashtable in Subtree.
 */
class RootNode extends SuffixNode {

    String key;

    public RootNode(char base, int level, String key) {
        super(base, level, null);

        this.key = key;
    }
}

// This is the main class for the algorithm: it builds the tree and performs error-correction.
// builds only the subtree rooted under "prefix" and only the levels passed as a parameter.
class Subtree {

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

// this class is instantiated by the supervisor to start calculation and analysis of a subtree
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
 
    // the parameters fromLevel and toLevel indicate which parts of the tree are to be built
    public static int fromLevel = 14, toLevel = 17;
    //the number of threads to be launched in parallel - adjust to system
    private int threadsToLaunch = 8;
    private int threadsLaunched = 0;
    // statistics...
    private int aligned = 0;
    private int mismatches = 0;
    private int insertions = 0;
    private int deletions = 0;
    // Iterator to generate the prefixes for each subtree - can be exchanged with any smarter routine
    private BaseStringIterator strings;

    public Supervisor() {
    }

    public void run() {

        //main loop: each iteration stands for a whole execution of the algorithm
        for (int y = 0; y < numberOfIterations; y++) {

            // prepare fresh prefixes for the next iteration
            strings = new BaseStringIterator();
            // prepare a fresh BitSet - also for statistical purposes
            Subtree.identifiedReads = new BitSet(BaseShrec.readcount);

            //create the threads and run them
            CalculateSubtreeProcess p = new CalculateSubtreeProcess("AAAAA",
                    fromLevel, toLevel, BaseShrec.readcount);
            p.start();
            synchronized(this) {
                threadsLaunched++;
            }

            while (strings.hasNext()) {

                p = new CalculateSubtreeProcess(strings.next(), fromLevel, toLevel,
                        BaseShrec.readcount);
                p.start();
                synchronized (this) {
                    threadsLaunched++;
                    while (threadsLaunched >= threadsToLaunch) {
                        // maximum number of threads running -> suspend supervisor
                        try {
                            wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            System.out.println("Waiting for threads to finish...");

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
            System.out.println("Corrected " + aligned + " reads.");

            System.out.println("Mismatches: " + mismatches + " Deletions: " + deletions + " Insertions: " + insertions);

            System.out.println("Finished Round " + y + " - identified " + Subtree.identifiedReads.cardinality() + " reads");
        }

        // the algorithm is done by now - write the results to the output file(s)
        try {
            // for short runs (numberOfIterations low) it is practical to keep all the reads
            // in one file that are either correct or corrected - because in the last round reads
            // could be corrected but identified in the same time.
            // Even for longer runs it happens quite frequently (in low coverage regions)
            // that reads are corrected the right way but still identified as suspicous thus
            // incorrect.
            // TODO: work on a routine to identify with different values in the last round.
            // Maybe write the number of visits of suspicous nodes with it so the user or
            // external program may judge
            boolean allToOneFile = true;

            int i = 0;
            for(int j = 0; j < correctReadsFilename.length; j++) {
               PrintWriter outputCorrected = 
                    new PrintWriter(correctReadsFilename[j]);
                PrintWriter outputDiscarded = 
                    new PrintWriter(discardedReadsFilename[j]);
                String fastaComment;
                for (; i < readsInFile[j]; i++) {
                    /*
                     * fastaComment = "> read number "+i+": identified ";
                     * if(Subtree.identifiedReads.get(i)) fastaComment += "1"; else
                     * fastaComment += "0"; fastaComment += "; corrected ";
                     * if(Subtree.correctedReads.get(i)) fastaComment += "1"; else
                     * fastaComment += "0"; output.println(fastaComment);
                     * output.println(new String(Shrec.reads[i]));
                     */
                    if (j == 0) {
                        fastaComment = "> read number " + i;
                    } else {
                        fastaComment = "> read number " + (i - readsInFile[j-1]);
                    }
                    if (!allToOneFile) {
                        if (Subtree.identifiedReads.get(i)) {
                            outputDiscarded.println(fastaComment);
                            outputDiscarded.println(new String(BaseShrec.reads[i * 2]));
                        } else {
                            if (Subtree.correctedReads.get(i)) {
                                fastaComment += " (corrected)";
                            }
                            outputCorrected.println(fastaComment);
                            outputCorrected.println(new String(BaseShrec.reads[i * 2]));
                        }
                    } else {
                        if (!Subtree.identifiedReads.get(i)) {
                            if (Subtree.correctedReads.get(i)) {
                                fastaComment += " (corrected)";
                            }
                            outputCorrected.println(fastaComment);
                            outputCorrected.println(new String(BaseShrec.reads[i * 2]));
                        } else {
                            if (Subtree.correctedReads.get(i)) {
                                fastaComment += " (corrected) + (identified)";
                                outputCorrected.println(fastaComment);
                                outputCorrected.println(new String(BaseShrec.reads[i * 2]));
                            } else {
                                outputDiscarded.println(fastaComment);
                                outputDiscarded.println(new String(BaseShrec.reads[i * 2]));
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
            System.out.println("Exception caught when writing output files.");
            e.printStackTrace();
        }
    }

    public void processFinished(int aligned, int mismatches, int insertions, int deletions) {
        // System.out.println("Wake up!");
        synchronized (this) {
            this.aligned += aligned;
            this.mismatches += mismatches;
            this.insertions += insertions;
            this.deletions += deletions;
            this.threadsLaunched--;
            this.notifyAll();
        }
    }
}

/* the Shrec class contains the main routine, getting everything started, and
 * some (global :() variables.
 */
public class BaseShrec {

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

    public static void usage() throws Exception {
        System.err.println("Usage: java Shrec -n <genome length> "+
                           "[-i <number of iterations>] [-s <strictness>] "+
                           "[-f <from level>] [-t <to level>] [-c <cutoff>] " +
                           "<input basespace files>");
        System.exit(1);
    }

    // the main routine handles parsing the input file and starting the supervisor.
    public static void main(String[] args) throws Exception {
        long start, end;

        start = System.currentTimeMillis();

        int inputFilesIndex = -1;
        int genomeLength = 0;
        int cutoff = 0;

        for(int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equals("-n")) {
                    if (i == args.length-1)
                        usage();
                    genomeLength = Integer.parseInt(args[i+1]);
                    i++;
                } else if (args[i].equals("-i")) {
                    if (i == args.length-1)
                        usage();
                    Supervisor.numberOfIterations = Integer.parseInt(args[i+1]);
                    i++;
                } else if (args[i].equals("-s")) {
                    if (i == args.length-1)
                        usage();
                    strictness = Integer.parseInt(args[i+1]);
                    i++;
                } else if (args[i].equals("-f")) {
                    if (i == args.length-1)
                        usage();
                    Supervisor.fromLevel = Integer.parseInt(args[i+1]);
                    i++;
               } else if (args[i].equals("-t")) {
                    if (i == args.length-1)
                        usage();
                    Supervisor.toLevel = Integer.parseInt(args[i+1]);
                    i++;
                } else if (args[i].equals("-c")) {
                    if (i == args.length-1)
                        usage();
                    cutoff = Integer.parseInt(args[i+1]);
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
        System.out.println("Shrec will perform " + 
                           Supervisor.numberOfIterations + 
                           " iterations");
        if (cutoff == 0) {
            System.out.println("Strictness set to " + strictness);
        } else {
            System.out.println("Cutoff set to " + cutoff);
        }

        System.out.println("Building trie from level " + 
                           Supervisor.fromLevel + " to level " +
                           Supervisor.toLevel);

        if (genomeLength <= 0) {
            usage();
        }

        if (Supervisor.toLevel <= Supervisor.fromLevel) {
            usage();
        }

        if (Supervisor.numberOfIterations <= 0 ||
            Supervisor.numberOfIterations >= 15) {
            usage();
        }

        // parse the input files
        BaseShrec.readcount = 0;

        Supervisor.correctReadsFilename = 
            new String[args.length-inputFilesIndex];
        Supervisor.discardedReadsFilename = 
            new String[args.length-inputFilesIndex];
        Supervisor.readsInFile = new int[args.length-inputFilesIndex];

        ArrayList<byte[]> readsFromFile = new ArrayList<byte[]>();

        for(int i = inputFilesIndex; i < args.length; i++) {
            Supervisor.correctReadsFilename[i-inputFilesIndex] = 
                args[i] + ".corrected";
            Supervisor.discardedReadsFilename[i-inputFilesIndex] = 
                args[i] + ".discarded";
  
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

                // Trim indeterminate characters from the end
                int ii = bytes.length-1;
                while(ii >= 0 && bytes[ii] == 'N') ii--;
                if (ii != bytes.length) {
                    byte[] bytes2 = new byte[ii+1];
                    for(int j = 0; j <= ii; j++)
                        bytes2[j] = bytes[j];
                    bytes = bytes2;
                }

                readsFromFile.add(bytes);
                readsFromFile.add(Subtree.buildComplement(bytes));

                line = b.readLine(); // fasta comment

                readcount++;
            }
            Supervisor.readsInFile[i-inputFilesIndex] = readcount;
            b.close();
        }

        byte[][] reads = new byte[readsFromFile.size()][];

        readsFromFile.toArray(reads);

        readsFromFile = null; // let the gc do it's work

        System.out.println(readcount + " base space reads");

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

        BaseShrec.reads = reads;

        System.out.println("All set; starting error correction.");

        BaseShrec.supervisor = new Supervisor();
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
