import java.security.Key;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class ChainedHashTable<K, V> implements HashTable<K, V> {
    final static int DEFAULT_INIT_CAPACITY = 4;
    final static double DEFAULT_MAX_LOAD_FACTOR = 2;
    final private HashFactory<K> hashFactory;
    final private double maxLoadFactor;
    private int capacity;
    private HashFunctor<K> hashFunc;
    private List<Element<K,V>>[] table;
    private long size;
    private int k;

    /*
     * You should add additional private fields as needed.
     */

    public ChainedHashTable(HashFactory<K> hashFactory) {
        this(hashFactory, DEFAULT_INIT_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
    }

    public ChainedHashTable(HashFactory<K> hashFactory, int k, double maxLoadFactor) {
        this.hashFactory = hashFactory;
        this.maxLoadFactor = maxLoadFactor;
        this.capacity = 1 << k;
        this.hashFunc = hashFactory.pickHash(k);
        this.table = new List[this.capacity];
        this.size = 0;
        this.k = k;
    }

    public int findIndexOfKeyInTable(K key) {
        return this.hashFunc.hash(key);
    }

    public Element<K,V> findElementWithKey(K key, int place) {
        //if the list doesn't exists - return null
        if(this.table[place] == null)
            return null;

        //go through the list of elements in the relevant place in the table - search for this element
        Iterator<Element<K, V>> iter = this.table[place].iterator();
        while (iter.hasNext()) {
            Element<K,V> current = iter.next();
            if(current.key().equals(key))
                return current;
        }

        return null;
    }

    public V search(K key) {
        int place = findIndexOfKeyInTable(key);
        Element<K,V> element = findElementWithKey(key, place);

        if(element != null)
            return element.satelliteData();

        return null;
    }

    public void insert(K key, V value) {
        //find where should insert
        int place = findIndexOfKeyInTable(key);

        //search if the element already exists - don't insert
        Boolean exists = this.search(key) != null;

        if(!exists) {
            //insert the element to the table where it belongs
            Element<K,V> element = new Element<>(key,value);
            if(this.table[place] == null) { //check if this list exists - if not - create it
                this.table[place] = new ArrayList<>();
            }
            this.table[place].add(element);
            this.size++;

            //check if the size overlaps - call rehashing
            if((double) this.size/this.capacity >= this.maxLoadFactor) {
                rehash();
            }
        }
    }

    private void rehash() {
        //create a table with the new size
        this.capacity = this.capacity*2;
        this.k=this.k+1;
        List<Element<K,V>>[] newTable = new List[this.capacity];

        //generate new hashFunction
        this.hashFunc = hashFactory.pickHash(k);

        //go through all values in current table to the new table
        for(int i = 0; i < this.table.length; i++) {
            if(this.table[i] != null) {
                Iterator<Element<K,V>> iter = this.table[i].iterator();
                while (iter.hasNext()) {
                    Element<K,V> current = iter.next();
                    //find where to insert
                    int place = findIndexOfKeyInTable(current.key());

                    //insert to new table
                    if(newTable[place] == null) {
                        newTable[place] = new ArrayList<>();
                    }

                    newTable[place].add(current);
                }
            }
        }

        //update table to the new table
        this.table = newTable;
    }

    public boolean delete(K key) {
        int place = findIndexOfKeyInTable(key);
        Element<K,V> element = findElementWithKey(key, place);
        if(element != null) {
            this.table[place].remove(element);
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
