/* *****************************************************************************
 * Read in a list of words from standard input and print out the most frequently
 * occurring word that has length greater than a given threshold.
 *
 * Useful as a test client for various symbol table implementations.
 *
 *  % java FrequencyCounter 1 < tinyTale.txt
 *  it 10
 *
 *  % java FrequencyCounter 8 < tale.txt
 *  business 122
 *
 *  % java FrequencyCounter 10 < leipzig1M.txt
 *  government 24763
 *
 ***************************************************************************** */

import edu.princeton.cs.algs4.In;


public class FrequencyCounter {

    // Do not instantiate.
    private FrequencyCounter() {
    }

    /**
     * Reads in a command-line integer and sequence of words from
     * standard input and prints out a word (whose length exceeds
     * the threshold) that occurs most frequently to standard output.
     * It also prints out the number of words whose length exceeds
     * the threshold and the number of distinct such words.
     */
    public static void main(String[] args) {
        int distinct = 0;
        int words = 0;
        int minlen = Integer.parseInt(args[0]);
        Trie<Integer> st = new Trie<>();
        // Map<String, Integer> st = new HashMap<>();

        // import the dictionary
        In file = new In("\\D:\\Algorithms I\\kdtree\\leipzig1M.txt");
        String[] dictionary = file.readAllStrings();
        file.close();
        System.out.println("Input dictionary size = " + dictionary.length);

        // build the symbol table and compute frequency counts
        System.out.println("\nBuilding the symbol table...");
        double start = System.currentTimeMillis();
        for (String word : dictionary) {
            if (word.length() < minlen) continue;
            words++;
            if (st.contains(word)) {
                st.put(word, st.get(word) + 1);
            }
            else {
                st.put(word, 1);
                distinct++;
            }
        }
        double end = System.currentTimeMillis();
        System.out.println("Time to build the symbol table: " + (end - start) / 1000 + " secs");

        // check search speed
        System.out.println("\nRunning a search...");
        int counter = 0;
        long totalCounter = 0;
        double start1 = System.currentTimeMillis();
        for (String word : dictionary) {
            if (st.contains(word)) {
                counter++;
                totalCounter += st.get(word);
            }
        }
        double end1 = System.currentTimeMillis();
        System.out.println("Search time: " + (end1 - start1) / 1000 + " secs");
        System.out.println("Total search hits = " + counter);
        System.out.println("Sum of occurrence frequencies = " + totalCounter);

        // find a key with the highest frequency count
        String max = "";
        st.put(max, 0);
        for (String word : st.keys()) {
            if (st.get(word) > st.get(max))
                max = word;
        }

        System.out.println();
        System.out.println(max + " " + st.get(max));
        System.out.println("distinct = " + distinct);
        System.out.println("Symbol table size = " + st.size());
        System.out.println("words    = " + words);
    }
}
