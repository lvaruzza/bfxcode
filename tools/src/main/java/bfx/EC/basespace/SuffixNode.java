package bfx.EC.basespace;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

//The basic unit of the tree. Current implementation is still rather large.
//Best way to save further space is probably to define an AbstractSuffixNode
//then extend it so that nodes with high branch factor (>1 child nodes) can
//be differentiated from nodes with a single outgoing branch. Nodes with a
//single outgoing branch constitute most of the nodes in the tree and are
//uninteresting for error correction purposes. Runs of them should be compacted
//in future versions.
public class SuffixNode {

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
