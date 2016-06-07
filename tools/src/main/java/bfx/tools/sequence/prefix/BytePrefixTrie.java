package bfx.tools.sequence.prefix;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BytePrefixTrie implements BytePrefix {
	private static final Logger log = LoggerFactory.getLogger(BytePrefixTrie.class);
	final private List<TrieNode> nodes = new LinkedList<TrieNode>();	
	
	private static class TrieNodeIterator implements Iterator<Entry<byte[],Long>> {
		
		private Iterator<TrieNode> nodes;
		
		public TrieNodeIterator(List<TrieNode> ns) {
			nodes=ns.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return nodes.hasNext();
		}

		
		@Override
		public Entry<byte[], Long> next() {
			TrieNode node = nodes.next();
			Entry<byte[],Long> r = new SimpleImmutableEntry<byte[],Long>(node.seq(),node.count);
			return r;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	public static class TrieNode {
		public byte b;
		public long count;
		public int depth;
		public TrieNode right;
		public TrieNode child;
		public TrieNode parent;
		
		public static TrieNode createNode(TrieNode parent,TrieNode left,byte x) {
			TrieNode n =  new TrieNode(x);
			if (left!=null) left.right = n;
			if (parent != null) {
				if (parent.child==null) parent.child=n;
				n.parent = parent;
				n.depth=parent.depth+1;
			}
			//log.info(String.format("created node %s",new String(n.seq())));
			return n;
		}
		
		private TrieNode(byte b) {
			this.b=b;
			this.count=0;
			this.depth=0;
			this.parent=null;
			this.child=null;
			this.right=null;
		}
		//public String toString() {
		//	return String.format("[%s %c:%d]",this,b,count);
		//}
		public void increment() {
			this.count++;
		}
		
		public byte[] seq() {
			byte[] buff=new byte[depth+1];
			TrieNode n=this;
			for(int i=depth;i>=0;i--) {
				buff[i]=n.b;
				n = n.parent;
			}
			return buff;
		}
	}
	
	private TrieNode root = null;
	
	@Override
	public void add(byte[] x) {
		//log.info(String.format("add: '%s'",new String(x)));
		TrieNode cur = root;
		TrieNode left = null;
		TrieNode parent = null;
		
		
		int i=0;
		do {
			if (cur==null) {
				cur=TrieNode.createNode(parent, left, x[i]);
				//log.info(String.format("left.right=%s",left==null || left.right==null ? "null" : new String(left.right.seq())));
				nodes.add(cur); 
				if (root==null) root=cur;
			}
			//log.info(String.format("cur=%s(%c) x[%d]=%c",new String(cur.seq()),cur.b,i,x[i]));
			if (cur.b != x[i]) {
				//log.info(String.format("cur.right=%s",cur.right==null ? "null" : new String(cur.right.seq())));
				left=cur;
				cur=cur.right;
			} else {
				cur.increment();
				//log.info(String.format("increment %s: %d",new String(cur.seq()),cur.count));
				left=null;
				parent = cur;
				cur=cur.child;
				i++;
			}
		} while(i<x.length);
	}
	
	@Override
	public void add(byte[] x, int start, int end) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Entry<byte[], Long>> iterator() {
		//log.info("Iterator Created");
		return new TrieNodeIterator(nodes);
	}

}
