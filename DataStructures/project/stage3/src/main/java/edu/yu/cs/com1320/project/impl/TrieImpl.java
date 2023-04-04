package edu.yu.cs.com1320.project.impl;

import com.sun.jdi.Value;

import java.util.*;

public class TrieImpl<Value> implements edu.yu.cs.com1320.project.Trie<Value> {

    private final int alphabetSize = 128;
    private Node root;

    public TrieImpl() {
        this.root = new Node();
    }
    private class Node<V> {
        private ArrayList<Value> val = new ArrayList<>();
        private Node<V>[] links = new Node[alphabetSize];
    }

    @Override
    public void put(String key, Value val) {
        //delete the value from this key
        if (key == null) {
            throw new IllegalArgumentException("Can't Put With no Value");
        }
        if (val == null) {
            return;
        }
        else {
            //calling put(Node x, String key, Value val, int depth)
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node put(Node x, String key, Value val, int depth) {
        //create a new node
        if (x == null) {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (depth == key.length()) {
            x.val.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(depth);
        x.links[c] = put(x.links[c], key, val, depth + 1);
        return x;
    }

    private Node getNode(Node x, String key, int depth) {
        if (x == null) {
            return null;
        }
        if (depth == key.length()) {
            return x;
        }
        char c = key.charAt(depth);
        return getNode(x.links[c], key, depth+1);
    }

    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        if (key == null || comparator == null) {
            throw new IllegalArgumentException("Can't Get With Null Key or Comparator");
        }
        //calling get(Node x, String key, int depth)
        List<Value> x = this.getAllSorted(this.root, key, 0);
        if (x.isEmpty()) {
            return new ArrayList<Value>();
        }
        x.sort(comparator);
        return x;
    }
    private List<Value> getAllSorted(Node x, String key, int depth) {
        //link from parent was null - return null, indicating a miss
        if (x == null) {
            return new ArrayList<Value>();
        }
        //if we've reached the last node in the key, return the node.
        //depth is initially 0 since the root isn’t part of key length.
        if (depth == key.length()) {
            return x.val;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(depth);
        return this.getAllSorted(x.links[c], key, depth + 1);
    }
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if (prefix == null || comparator == null) {
            throw new IllegalArgumentException("Can't Get With Null Key or Comparator");
        }
        List<Value> list = new LinkedList<>();
        collectAllWithPrefixSorted(getNode(this.root, prefix, 0), prefix, list);
        list.sort(comparator);
        return list;
    }
    private void collectAllWithPrefixSorted(Node x, String prefix, List<Value> q) {
        if (x == null) {
            return;
        }
        if (x.val != null) {
            q.addAll(x.val);
        }
        for (char c = 0; c < alphabetSize; c++) {
            collectAllWithPrefixSorted(x.links[c], prefix + c, q);
        }
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Can't Get With Null Key or Comparator");
        }
        Set<Value> deletedValues = new HashSet<>();
        deleteAllWithPrefixSorted(getNode(this.root, prefix, 0), prefix, deletedValues);
        return deletedValues;
    }

    private Node deleteAllWithPrefixSorted(Node x, String prefix, Set<Value> set) {
        if (x == null) {
            return x;
        }
        if (x.val != null) {
            set.addAll(x.val);
            x.val = null;
        }
        for (char c = 0; c < alphabetSize; c++) {
            x.links[c] = deleteAllWithPrefixSorted(x.links[c], prefix + c, set);
        }
        if (x.val != null) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < this.alphabetSize; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    @Override
    public Set<Value> deleteAll(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Can't Delete with a Null Input");
        }
        Set<Value> deletedValues = new HashSet<>();
        this.root = deleteAll(this.root, key, 0, deletedValues);
        return deletedValues;
    }

    private Node deleteAll(Node x, String key, int depth, Set<Value> values) {
        if (x == null) {
            return null;
        }
        //we're at the node to delete - add the value to the set and set the val to null
        if (depth == key.length()) {
            values.addAll(x.val);
            x.val = null;
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(depth);
            x.links[c] = this.deleteAll(x.links[c], key, depth + 1, values);
        }
        //this node has a val – do nothing, return the node
        if (x.val != null) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < this.alphabetSize; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    @Override
    public Value delete(String key, Value val) {
        if (key == null || val == null) {
            throw new IllegalArgumentException("Can't Delete with a Null Input");
        }
        Set<Value> deletedValues = new HashSet<>();
        this.root = delete(this.root, key, 0, deletedValues, val);
        if (deletedValues.isEmpty()) {
            return null;
        }
        Iterator<Value> iterator = deletedValues.iterator();
        return iterator.next();
    }
    private Node delete(Node x, String key, int depth, Set<Value> values, Value val) {
        if (x == null) {
            return null;
        }
        //we're at the node to delete - add the value to the set and remove the val from the node
        if (depth == key.length()) {
            if (x.val.contains(val)) {
                values.add(val);
                x.val.remove(val);
            }
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(depth);
            x.links[c] = this.delete(x.links[c], key, depth + 1, values, val);
        }
        //this node has a val – do nothing, return the node
        if (x.val != null) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < this.alphabetSize; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
}
