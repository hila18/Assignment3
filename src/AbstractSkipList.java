import java.util.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractSkipList {
    final protected SkipListNode head;
    final protected SkipListNode tail;
    private int size;

    public AbstractSkipList() {
        head = new SkipListNode(Integer.MIN_VALUE);
        tail = new SkipListNode(Integer.MAX_VALUE);
        this.size = 0;
        increaseHeight();
    }

    public void increaseHeight() {
        head.addLevel(tail, null, size+1);
        tail.addLevel(null, head, 0);
    }
	
    abstract void decreaseHeight();

    abstract SkipListNode find(int key);

    abstract int generateHeight();

    public SkipListNode search(int key) {
        SkipListNode curr = find(key);

        return curr.key() == key ? curr : null;
    }

    public SkipListNode insert(int key) {
        int nodeHeight = generateHeight();
        this.size++;

        while (nodeHeight > head.height()) {
            increaseHeight();
        }

        SkipListNode prevNode = find(key);
        if (prevNode.key() == key) {
            this.size--;
            return null;
        }

        SkipListNode newNode = new SkipListNode(key);

        for (int level = 0; level <= nodeHeight && prevNode != null; ++level) {
            SkipListNode nextNode = prevNode.getNext(level);

            int currPrevSpan = prevNode.getSpan(level);

            int newPrevSpan = this.findSpan(prevNode, level-1, key);
            int mySpan = currPrevSpan - newPrevSpan + 1;

            newNode.addLevel(nextNode, prevNode, mySpan);
            prevNode.setNext(level, newNode);
            nextNode.setPrev(level, newNode);
            prevNode.setSpan(level, newPrevSpan);

            while (prevNode != null && prevNode.height() == level) {
                prevNode = prevNode.getPrev(level);
            }
        }

        SkipListNode previous = newNode.getPrev(nodeHeight);
        while (previous != null && previous.height() == nodeHeight) {
            previous = previous.getPrev(nodeHeight);
        }
        updateSpanNextLevels(previous, nodeHeight+1, 1);

        return newNode;
    }

    //find the update for the previous span to update
    //key - the value to search the span between node until him
    private int findSpan(SkipListNode node, int level , int key) {
        if(level < 0)
            return 1;

        int span = 0;
        SkipListNode temp = node;
        while(temp.key() < key) {
            span += temp.getSpan(level);
            temp = temp.getNext(level);
        }

        return span;
    }

    //if added a new node - for all levels that the key didn't enter to - need to add 1 to the span of the previous value
    //if deleted a new node - for all levels that the node is not inside - need to decrease 1 to the span of the previous value
    private void updateSpanNextLevels(SkipListNode prev, int startLevel, int addOrDecrease) {
        SkipListNode toUpdate = prev;
        for(int level = startLevel; level <= head.height(); level++) {
            if(toUpdate!=null) {
                toUpdate.setSpan(level, toUpdate.getSpan(level)+addOrDecrease);
            }

            while (toUpdate != null && toUpdate.height() == level) {
                toUpdate = toUpdate.getPrev(level);
            }
        }
    }

    public boolean delete(SkipListNode skipListNode) {
        for (int level = 0; level <= skipListNode.height(); ++level) {
            SkipListNode prev = skipListNode.getPrev(level);
            SkipListNode next = skipListNode.getNext(level);

            int prevSpan = prev.getSpan(level);
            int mySpan = skipListNode.getSpan(level);

            prev.setNext(level, next);
            prev.setSpan(level, mySpan+prevSpan-1);
            next.setPrev(level, prev);
        }
		
        while (head.height() >= 0 && head.getNext(head.height()) == tail) {
        	decreaseHeight();
        }

        int height = skipListNode.height;
        SkipListNode previous = skipListNode.getPrev(height);
        while (previous != null && previous.height() == height) {
            previous = previous.getPrev(height);
        }
        updateSpanNextLevels(previous, height+1, -1);

        this.size--;
        return true;
    }

    public SkipListNode predecessor(SkipListNode skipListNode) {
        return skipListNode.getPrev(0);
    }

    public SkipListNode successor(SkipListNode skipListNode) {
        return skipListNode.getNext(0);
    }

    public SkipListNode minimum() {
        if (head.getNext(0) == tail) {
            throw new NoSuchElementException("Empty Linked-List");
        }

        return head.getNext(0);
    }

    public SkipListNode maximum() {
        if (tail.getPrev(0) == head) {
            throw new NoSuchElementException("Empty Linked-List");
        }

        return tail.getPrev(0);
    }

    private void levelToString(StringBuilder s, int level) {
        s.append("H    ");
        SkipListNode curr = head.getNext(0);

        while (curr != tail) {
            if (curr.height >= level) {
                s.append(curr.key());
                s.append("    ");
            }
            else {
            	s.append("    ");
            	for (int i = 0; i < curr.key().toString().length(); i = i + 1)
            		s.append(" ");
            }

            curr = curr.getNext(0);
        }

        s.append("T\n");
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int level = head.height(); level >= 0; --level) {
            levelToString(str, level);
        }

        return str.toString();
    }

    public static class SkipListNode extends Element<Integer, Object> {
        final private List<SkipListNode> next;
        final private List<SkipListNode> prev;
        final private List<Integer> span;

        private int height;

        public SkipListNode(int key) {
        	super(key);
            next = new ArrayList<>();
            prev = new ArrayList<>();
            this.span = new ArrayList<>();
            this.height = -1;
            
        }

        public SkipListNode getPrev(int level) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            return prev.get(level);
        }

        public SkipListNode getNext(int level) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            return next.get(level);
        }

        public void setNext(int level, SkipListNode next) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            this.next.set(level, next);
        }

        public void setPrev(int level, SkipListNode prev) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            this.prev.set(level, prev);
        }

        public Integer getSpan(int level) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            return span.get(level);
        }

        public void setSpan(int level, Integer space) {
            if (level > height) {
                throw new IllegalStateException("Fetching height higher than current node height");
            }

            this.span.set(level, space);
        }


        public void addLevel(SkipListNode next, SkipListNode prev, int span) {
            height++;
            this.next.add(next);
            this.prev.add(prev);
            this.span.add(span);
        }
		
		public void removeLevel() {           
            this.next.remove(height);
            this.prev.remove(height);
            this.span.remove(height);
            height--;
        }

        public int height() { return height; }
    }
}
