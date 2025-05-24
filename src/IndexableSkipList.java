public class IndexableSkipList extends AbstractSkipList {
    final protected double p;	// p is the probability for "success" in the geometric process generating the height of each node.
    public IndexableSkipList(double probability) {
        super();
        this.p = probability;
    }
	
	@Override
    public void decreaseHeight() {
        this.head.removeLevel();
        this.tail.removeLevel();
    }

    @Override
    public SkipListNode find(int key) {
        SkipListNode currentNode = head;

        for(int level = head.height(); level >= 0; level--) {
            while(!currentNode.getNext(level).equals(tail) && currentNode.getNext(level).key() <= key) {
                currentNode = currentNode.getNext(level);
            }

            if(currentNode.key() == key) {
                return currentNode;
            }
        }

        return currentNode;
    }

    @Override
    public int generateHeight() {
        int height = 0;
        //tail - if got lower than p - increase height
        while(Math.random() < this.p)
            height++;

        return height;
    }

    public int rank(int key) {
        int toRun = find(key).key();
        int myRank = -1;
        int level = this.head.height();
        SkipListNode current = this.head;

        while (current.key() < toRun) {
            while (current.getNext(level).key() > key) {
                level--;
            }

            myRank += current.getSpan(level);
            current = current.getNext(level);
        }

        return myRank;
    }

    public int select(int index) {
        index += 1; //calculate head as one
        int level = this.head.height();
        SkipListNode current = this.head;

        while(index > 0) {
            //check if the next node is too far - if yes go level below
            while(index - current.getSpan(level) < 0) {
                level--;
            }

            //update index - decrease the span of the current value
            index = index - current.getSpan(level);
            current = current.getNext(level);
        }

        return current.key();
    }

}
