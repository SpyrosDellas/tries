/* *****************************************************************************
 *  Name: Spyros Dellas
 *  Date: 21/09-2020
 *  Description: Implementation of a classic Trie symbol table
 **************************************************************************** */

import java.util.ArrayList;
import java.util.List;

public class Trie<V> {

    private static final int EXTENDED_ASCII = 256;
    private static final int LOWER_CASE_OFFSET = 97;
    private static final int UPPER_CASE_OFFSET = 65;
    private static final int ENGLISH_ALPHABET = 26;

    private final int radix; // the size of the alphabet
    private final int radixOffset;

    private int size;

    private Node root;

    private static class Node {
        private Object value;
        private Node[] next;

        public Node(int radix) {
            next = new Node[radix];
        }
    }

    /**
     * Create a new Trie symbol table
     * - No duplicate or null keys are allowed
     * - No null values are allowed
     * - It is assumed the alphabet is the extended ASCII character set
     */
    public Trie() {
        this.radix = EXTENDED_ASCII;
        this.radixOffset = 0;
    }

    /**
     * Create a new Trie symbol table
     * - No duplicate or null keys are allowed
     * - No null values are allowed
     *
     * @param radix       the radix (size) of the alphabet
     * @param radixOffset the offset of the alphabet
     */
    public Trie(int radix, int radixOffset) {
        this.radix = radix;
        this.radixOffset = radixOffset;
    }

    /**
     * value paired with key (null if key is absent)
     *
     * @param key the key
     * @return returns the value associated with the key or null if key is absent
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public V get(String key) {
        if (key == null)
            throw new IllegalArgumentException("the null key is unsupported");
        Node x = get(key, root, 0);
        if (x == null)
            return null;
        return (V) x.value;
    }

    private Node get(String key, Node x, int index) {
        if (x == null) {
            return null;
        }
        if (index == key.length()) {
            return x;
        }
        return get(key, x.next[key.charAt(index) - radixOffset], index + 1);
    }

    /**
     * is there a value paired with key?
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * put key-value pair into the table (remove key if value is null)
     *
     * @param key
     * @param value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(String key, V value) {
        if (key == null)
            throw new IllegalArgumentException("the null key is unsupported");
        if (value == null)
            delete(key);
        root = put(key, value, root, 0);
    }

    private Node put(String key, V value, Node x, int index) {
        if (x == null)
            x = new Node(radix);
        if (index == key.length()) {
            if (x.value == null)
                size++;
            x.value = value;
            return x;
        }
        int nextNodeId = key.charAt(index) - radixOffset;
        x.next[nextNodeId] = put(key, value, x.next[nextNodeId], index + 1);
        return x;
    }

    /**
     * remove key (and its value)
     *
     * @param key the key of the key-value pair to remove
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void delete(String key) {
        if (key == null)
            throw new IllegalArgumentException("the null key is unsupported");
        root = delete(key, root, 0);
    }

    private Node delete(String key, Node x, int index) {
        if (x == null)
            return null;
        if (index == key.length()) {
            if (x.value != null) size--;
            x.value = null;
            for (Node y : x.next) {
                if (y != null) return x;
            }
            return null;
        }
        int nextNodeId = key.charAt(index) - radixOffset;
        x.next[nextNodeId] = delete(key, x.next[nextNodeId], index + 1);
        if (x.value != null || x.next[nextNodeId] != null)
            return x;
        for (Node y : x.next) {
            if (y != null) return x;
        }
        return null;
    }

    /**
     * is this symbol table empty?
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * the longest key in the Trie that is a prefix of s
     *
     * @param s
     * @return returns the empty string if there is no key in the Trie that is a prefix of s
     */
    public String longestPrefixOf(String s) {
        if (s == null)
            return null;
        return longestPrefixOf(s, root, 0, -1);
    }

    private String longestPrefixOf(String s, Node x, int index, int prefixLength) {
        if (x == null)
            return s.substring(0, prefixLength);
        if (x.value != null)
            prefixLength = index;
        if (index == s.length()) {
            if (prefixLength == -1)
                return null;
            else
                return s.substring(0, prefixLength);
        }
        return longestPrefixOf(s, x.next[s.charAt(index) - radixOffset], index + 1, prefixLength);
    }

    /**
     * all the keys in the table
     *
     * @return
     */
    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    /**
     * all the keys having s as a prefix
     *
     * @param s
     * @return
     */
    public Iterable<String> keysWithPrefix(String s) {
        List<String> keys = new ArrayList<>();
        if (s == null)
            return keys;
        Node x = get(s, root, 0);
        collect(x, keys, new StringBuilder(s));
        return keys;
    }

    private void collect(Node x, List<String> keys, StringBuilder prefix) {
        if (x == null)
            return;
        if (x.value != null)
            keys.add(prefix.toString());
        for (int i = 0; i < radix; i++) {
            Node next = x.next[i];
            if (next == null)
                continue;
            char c = (char) (radixOffset + i);
            collect(next, keys, prefix.append(c));
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * all the keys that match s (where . matches any character)
     *
     * @param s a String where . is treated as a wildcard that matched any character
     * @return
     */
    public Iterable<String> keysThatMatch(String s) {
        List<String> keys = new ArrayList<>();
        if (s == null)
            return keys;
        collectKeysThatMatch(s, root, keys, new StringBuilder());
        return keys;
    }

    private void collectKeysThatMatch(String s, Node x, List<String> keys, StringBuilder prefix) {
        if (x == null)
            return;
        int index = prefix.length();
        if (index == s.length()) {
            if (x.value != null)
                keys.add(prefix.toString());
            return;
        }
        if (s.charAt(index) == '.') {
            for (int i = 0; i < radix; i++) {
                Node next = x.next[i];
                if (next == null)
                    continue;
                char c = (char) (radixOffset + i);
                collectKeysThatMatch(s, next, keys, prefix.append(c));
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
        else {
            char c = s.charAt(index);
            Node next = x.next[c - radixOffset];
            if (next != null) {
                collectKeysThatMatch(s, next, keys, prefix.append(c));
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }

    /**
     * number of key-value pairs
     */
    public int size() {
        return this.size;
    }

    // Unit tests
    public static void main(String[] args) {
        Trie<Integer> lexicon = new Trie<>(ENGLISH_ALPHABET, LOWER_CASE_OFFSET);

        System.out.println("\nPut 7 key-value pairs...");
        lexicon.put("", 0);
        lexicon.put("she", 3);
        lexicon.put("shells", 6);
        lexicon.put("shore", 5);
        lexicon.put("shores", 6);
        lexicon.put("sells", 5);
        lexicon.put("sea", 3);
        System.out.println("Trie size = " + lexicon.size());

        System.out.println("\nLongest prefix of 'shear': " + lexicon.longestPrefixOf("shear"));
        System.out.println(
                "\nLongest prefix of 'shoreszzz': " + lexicon.longestPrefixOf("shoreszzz"));
        System.out.println("\nLongest prefix of '': " + lexicon.longestPrefixOf(""));

        System.out.println("\nAll keys:");
        for (String word : lexicon.keys()) {
            System.out.println(word + ", " + lexicon.get(word));
        }

        System.out.println("\nKeys with prefix 'she':");
        for (String word : lexicon.keysWithPrefix("she")) {
            System.out.println(word + ", " + lexicon.get(word));
        }

        System.out.println("\nKeys that match 'sh...s':");
        for (String word : lexicon.keysThatMatch("sh...s")) {
            System.out.println(word + ", " + lexicon.get(word));
        }

        System.out.println("\nKeys that match 's..':");
        for (String word : lexicon.keysThatMatch("s..")) {
            System.out.println(word + ", " + lexicon.get(word));
        }

        System.out.println("\nDelete all 7 key-value pairs...");
        lexicon.delete("");
        lexicon.delete("she");
        lexicon.delete("shells");
        lexicon.delete("shore");
        lexicon.delete("shores");
        lexicon.delete("sells");
        lexicon.delete("sea");
        System.out.println("Trie size = " + lexicon.size());
    }

}
