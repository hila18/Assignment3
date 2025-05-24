import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class ProbingHashTable<K, V> implements HashTable<K, V> {
    final static int DEFAULT_INIT_CAPACITY = 4;
    final static double DEFAULT_MAX_LOAD_FACTOR = 0.75;
    final private HashFactory<K> hashFactory;
    final private double maxLoadFactor;
    private int capacity;
    private HashFunctor<K> hashFunc;
    private Element<K,V>[] table;
    private boolean[] elementWasDeleted;
    private int size;
    private int k;

    /*
     * You should add additional private fields as needed.
     */

    public ProbingHashTable(HashFactory<K> hashFactory, int k, double maxLoadFactor) {
        this.hashFactory = hashFactory;
        this.maxLoadFactor = maxLoadFactor;
        this.capacity = 1 << k;
        this.hashFunc = hashFactory.pickHash(k);
        this.table = new Element[capacity];
        this.elementWasDeleted = new boolean[capacity]; // in the beginning - everything false - nothing was deleted yet
        this.size = 0;
        this.k = k;
    }
	
	public ProbingHashTable(HashFactory<K> hashFactory) {
        this(hashFactory, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
    }

    public int findMyIndex(K key) {
        //find where I was supposed to be entered
        int place = this.hashFunc.hash(key);

        //go from place until there is an empty space -
        // null in the table / true (was deleted) in the boolean table
        for(int i = 0; i < this.table.length; i++) {
            if(this.table[place] == null)
                return -1;

            else if(this.table[place].key().equals(key) && !this.elementWasDeleted[place])
                return place;

            place = (place + 1) % this.table.length;
        }

        return -1;
    }

    public V search(K key) {
        int myIndex = findMyIndex(key);

        if(myIndex == -1) return null;

        return this.table[myIndex].satelliteData();
    }

    public void insert(K key, V value) {
        //check if exists - returns -1 if doesn't exists
        int exists = this.findMyIndex(key);

        if(exists == -1) {
            this.size++;

            if((double) this.size/this.capacity >= this.maxLoadFactor) {
                rehash();
            }

            //insert the element
            Element<K,V> newElement = new Element(key,value);

            //find where I was supposed to be entered
            int place = this.hashFunc.hash(key);
            boolean success = false;

            //find the first empty place - null or deleted - and insert me
            for(int i = 0; i < this.table.length && !success; i++) {
                if(this.table[place] == null || this.elementWasDeleted[place]) {
                    this.table[place] = newElement;
                    this.elementWasDeleted[place] = false;
                    success = true;
                }

                place = (place + 1) % this.capacity;
            }
        }
    }

    private void rehash() {
        this.capacity = this.capacity*2;
        this.k=this.k+1;
        Element<K,V>[] newTable = new Element[this.capacity];
        boolean[] newWasDeleted = new boolean[this.capacity];
        this.hashFunc = hashFactory.pickHash(this.k);

        for(int i = 0; i < this.table.length; i++) {
            if(this.table[i] != null && !this.elementWasDeleted[i])
                insertElement(this.table[i], newTable);
        }

        this.elementWasDeleted = newWasDeleted;
        this.table = newTable;
    }

    private boolean insertElement(Element<K,V> element, Element<K,V>[] currTable) {
        //find where I was supposed to be entered
        int place = this.hashFunc.hash(element.key());

        //find the first empty place - null or deleted - and insert me
        for(int i = 0; i < this.capacity; i++) {
            if(currTable[place] == null) {
                currTable[place] = element;
                return true;
            }

            place = (place + 1) % this.capacity;
        }

        return false;
    }

    public boolean delete(K key) {
        //find if exists
        int myIndex = findMyIndex(key);

        //if yes - delete (change the value of the boolean table in the relevant place)
        if(myIndex != -1) {
            this.elementWasDeleted[myIndex] = true;
            this.size--;
            return true;
        }

        return false;
    }

    public HashFunctor<K> getHashFunc() {
        return hashFunc;
    }

    public int capacity() { return capacity; }
}
