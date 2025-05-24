import java.util.Collections; // can be useful
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HashingExperimentUtils {
    final private static int k = 16;
    final private static int NUM_EXPERIMENTS = 25; // Number of experiments for averaging
    final private static int CAPACITY = 1 << k; // 2^16 = 65536

    public static double[] measureInsertionsProbing() {
        double[] loadFactors = {0.5, 0.75, 0.875, 0.9375}; // 1/2, 3/4, 7/8, 15/16
        double[] averageTimes = new double[loadFactors.length];
        
        for (int i = 0; i < loadFactors.length; i++) {
            double totalTime = 0.0;
            
            for (int experiment = 0; experiment < NUM_EXPERIMENTS; experiment++) {
                ModularHash hashFactory = new ModularHash();
                ProbingHashTable<Integer, Integer> hashTable = 
                    new ProbingHashTable<>(hashFactory, k, loadFactors[i]);
                
                HashingUtils utils = new HashingUtils();
                int numItemsToInsert = (int) (CAPACITY * loadFactors[i]);
                Integer[] keysToInsert = utils.genUniqueIntegers(numItemsToInsert);
                
                long startTime = System.nanoTime();
                
                for (int j = 0; j < numItemsToInsert; j++) {
                    hashTable.insert(keysToInsert[j], keysToInsert[j]);
                }
                
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime) / (double) numItemsToInsert;
            }
            
            averageTimes[i] = totalTime / NUM_EXPERIMENTS;
        }
        
        return averageTimes;
    }

    public static double[] measureSearchesProbing() {
        double[] loadFactors = {0.5, 0.75, 0.875, 0.9375}; // 1/2, 3/4, 7/8, 15/16
        double[] averageTimes = new double[loadFactors.length];
        
        for (int i = 0; i < loadFactors.length; i++) {
            double totalTime = 0.0;
            
            for (int experiment = 0; experiment < NUM_EXPERIMENTS; experiment++) {
                ModularHash hashFactory = new ModularHash();
                ProbingHashTable<Integer, Integer> hashTable = 
                    new ProbingHashTable<>(hashFactory, k, 1.0); // Set high load factor to avoid rehashing
                
                HashingUtils utils = new HashingUtils();
                int numItemsToInsert = (int) (CAPACITY * loadFactors[i]);
                Integer[] keysToInsert = utils.genUniqueIntegers(numItemsToInsert);
                
                // Insert elements first
                for (int j = 0; j < numItemsToInsert; j++) {
                    hashTable.insert(keysToInsert[j], keysToInsert[j]);
                }
                
                // Prepare search keys: 50% successful, 50% unsuccessful
                int numSearches = 1000;
                Integer[] searchKeys = new Integer[numSearches];
                Random rand = new Random();
                
                for (int j = 0; j < numSearches; j++) {
                    if (j < numSearches / 2) {
                        // 50% successful searches - use existing keys
                        searchKeys[j] = keysToInsert[rand.nextInt(numItemsToInsert)];
                    } else {
                        // 50% unsuccessful searches - use random keys not in table
                        Integer randomKey;
                        do {
                            randomKey = rand.nextInt(Integer.MAX_VALUE);
                        } while (hashTable.search(randomKey) != null);
                        searchKeys[j] = randomKey;
                    }
                }
                
                long startTime = System.nanoTime();
                
                for (int j = 0; j < numSearches; j++) {
                    hashTable.search(searchKeys[j]);
                }
                
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime) / (double) numSearches;
            }
            
            averageTimes[i] = totalTime / NUM_EXPERIMENTS;
        }
        
        return averageTimes;
    }

    public static double[] measureInsertionsChaining() {
        double[] loadFactors = {0.5, 0.75, 1.0, 1.5, 2.0}; // 1/2, 3/4, 1, 3/2, 2
        double[] averageTimes = new double[loadFactors.length];
        
        for (int i = 0; i < loadFactors.length; i++) {
            double totalTime = 0.0;
            
            for (int experiment = 0; experiment < NUM_EXPERIMENTS; experiment++) {
                ModularHash hashFactory = new ModularHash();
                ChainedHashTable<Integer, Integer> hashTable = 
                    new ChainedHashTable<>(hashFactory, k, loadFactors[i]);
                
                HashingUtils utils = new HashingUtils();
                int numItemsToInsert = (int) (CAPACITY * loadFactors[i]);
                Integer[] keysToInsert = utils.genUniqueIntegers(numItemsToInsert);
                
                long startTime = System.nanoTime();
                
                for (int j = 0; j < numItemsToInsert; j++) {
                    hashTable.insert(keysToInsert[j], keysToInsert[j]);
                }
                
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime) / (double) numItemsToInsert;
            }
            
            averageTimes[i] = totalTime / NUM_EXPERIMENTS;
        }
        
        return averageTimes;
    }

    public static double[] measureSearchesChaining() {
        double[] loadFactors = {0.5, 0.75, 1.0, 1.5, 2.0}; // 1/2, 3/4, 1, 3/2, 2
        double[] averageTimes = new double[loadFactors.length];
        
        for (int i = 0; i < loadFactors.length; i++) {
            double totalTime = 0.0;
            
            for (int experiment = 0; experiment < NUM_EXPERIMENTS; experiment++) {
                ModularHash hashFactory = new ModularHash();
                ChainedHashTable<Integer, Integer> hashTable = 
                    new ChainedHashTable<>(hashFactory, k, 10.0); // Set very high load factor to avoid rehashing
                
                HashingUtils utils = new HashingUtils();
                int numItemsToInsert = (int) (CAPACITY * loadFactors[i]);
                Integer[] keysToInsert = utils.genUniqueIntegers(numItemsToInsert);
                
                // Insert elements first
                for (int j = 0; j < numItemsToInsert; j++) {
                    hashTable.insert(keysToInsert[j], keysToInsert[j]);
                }
                
                // Prepare search keys: 50% successful, 50% unsuccessful
                int numSearches = 1000;
                Integer[] searchKeys = new Integer[numSearches];
                Random rand = new Random();
                
                for (int j = 0; j < numSearches; j++) {
                    if (j < numSearches / 2) {
                        // 50% successful searches - use existing keys
                        searchKeys[j] = keysToInsert[rand.nextInt(numItemsToInsert)];
                    } else {
                        // 50% unsuccessful searches - use random keys not in table
                        Integer randomKey;
                        do {
                            randomKey = rand.nextInt(Integer.MAX_VALUE);
                        } while (hashTable.search(randomKey) != null);
                        searchKeys[j] = randomKey;
                    }
                }
                
                long startTime = System.nanoTime();
                
                for (int j = 0; j < numSearches; j++) {
                    hashTable.search(searchKeys[j]);
                }
                
                long endTime = System.nanoTime();
                totalTime += (endTime - startTime) / (double) numSearches;
            }
            
            averageTimes[i] = totalTime / NUM_EXPERIMENTS;
        }
        
        return averageTimes;
    }
}
