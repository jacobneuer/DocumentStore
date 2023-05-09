package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;
import java.io.*;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements edu.yu.cs.com1320.project.BTree<Key, Value> {

    private PersistenceManager<Key, Value> persistenceManager;
    //max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private static final int MAX = 6;
    private BTreeImpl.Node root; //root of the B-tree
    private BTreeImpl.Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree

    //B-tree node data type
    private static final class Node {
        private int entryCount; // number of entries
        private BTreeImpl.Entry[] entries = new BTreeImpl.Entry[BTreeImpl.MAX]; // the array of children
        private BTreeImpl.Node next;
        private BTreeImpl.Node previous;
        // create a node with k entries
        private Node(int k) {
            this.entryCount = k;
        }
        private void setNext(BTreeImpl.Node next) {
            this.next = next;
        }
        private BTreeImpl.Node getNext() {
            return this.next;
        }
        private void setPrevious(BTreeImpl.Node previous) {
            this.previous = previous;
        }
        private BTreeImpl.Node getPrevious() {
            return this.previous;
        }
        private BTreeImpl.Entry[] getEntries() {
            return Arrays.copyOf(this.entries, this.entryCount);
        }
    }

    //internal nodes: only use key and child
    //external nodes: only use key and value
    public static class Entry {
        private Comparable key;
        private Object val;
        private BTreeImpl.Node child;

        public Entry(Comparable key, Object val, BTreeImpl.Node child) {
            this.key = key;
            this.val = val;
            this.child = child;
        }
        public Object getValue() {
            return this.val;
        }
        public Comparable getKey() {
            return this.key;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTreeImpl() {
        this.root = new BTreeImpl.Node(0);
        this.leftMostExternalNode = this.root;
    }

    @Override
    public Value get(Key k) {
        if (k == null) {
            return null;
        }
        BTreeImpl.Entry entry = this.get(this.root, k, this.height);
        if (entry != null) {
            if (entry.val instanceof File) {
                Value v;
                try {
                    v = this.persistenceManager.deserialize(k);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                entry.val = v;
            }
            return (Value) entry.val;
        }
        return null;
    }

    private BTreeImpl.Entry get(BTreeImpl.Node currentNode, Key key, int height) {
        BTreeImpl.Entry[] entries = currentNode.entries;

        //current node is a leaf node (i.e. height == 0)
        if (height == 0) {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if(isEqual(key, entries[j].key)) {
                    //found desired key. Return its value
                    return entries[j];
                }
            }
            //didn't find the key
            return null;
        }

        //current node is internal (height > 0)
        else {
            for (int j = 0; j < currentNode.entryCount; j++) {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be in the subtree below the current entry),
                //then recurse into the current entry’s child
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key)) {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
            //didn't find the key
            return null;
        }
    }

    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean isEqual(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }

    @Override
    public Value put(Key k, Value v) {
        if (k == null) {
            throw new IllegalArgumentException("Argument key to put() is null");
        }
        //if the key already exists in the b-tree, simply replace the value
        BTreeImpl.Entry alreadyThere = this.get(this.root, k, this.height);
        if(alreadyThere != null) {
            Value oldValue = (Value) alreadyThere.val;
            alreadyThere.val = v;
            return oldValue;
        }
        BTreeImpl.Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if (newNode == null) {
            return null;
        }

        //split the root:
        //Create a new node to be the root.
        //Set the old root to be new root's first entry.
        //Set the node returned from the call to put to be new root's second entry
        BTreeImpl.Node newRoot = new BTreeImpl.Node(2);
        newRoot.entries[0] = new BTreeImpl.Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new BTreeImpl.Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        //a split at the root always increases the tree height by 1
        this.height++;
        return null;
    }

    private BTreeImpl.Node put(BTreeImpl.Node currentNode, Key k, Value v, int height) {
        int j;
        BTreeImpl.Entry newEntry = new BTreeImpl.Entry(k, v, null);

        //leaf node
        if (height == 0) {
            //find index in currentNode’s entry[] to insert new entry
            //we look for key < entry.key since we want to leave j
            //pointing to the slot to insert the new entry, hence we want to find
            //the first entry in the current node that key is LESS THAN
            for (j = 0; j < currentNode.entryCount; j++) {
                if (less(k, currentNode.entries[j].key)) {
                    break;
                }
            }
        }

        // internal node
        else {
            //find index in node entry array to insert the new entry
            for (j = 0; j < currentNode.entryCount; j++) {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be added to the subtree below the current entry),
                //then do a recursive call to put on the current entry’s child
                if ((j + 1 == currentNode.entryCount) || less(k, currentNode.entries[j + 1].key)) {
                    //increment j (j++) after the call so that a new entry created by a split
                    //will be inserted in the next slot
                    BTreeImpl.Node newNode = this.put(currentNode.entries[j++].child, k, v, height - 1);
                    if (newNode == null) {
                        return null;
                    }
                    //if the call to put returned a node, it means I need to add a new entry to
                    //the current node
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        //shift entries over one place to make room for new entry
        for (int i = currentNode.entryCount; i > j; i--) {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        //add new entry
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX) {
            //no structural changes needed in the tree
            //so just return null
            return null;
        }
        else {
            //will have to create new entry in the parent due
            //to the split, so return the new node, which is
            //the node for which the new entry will be created
            return this.split(currentNode, height);
        }
    }

    private BTreeImpl.Node split(BTreeImpl.Node currentNode, int height) {
        BTreeImpl.Node newNode = new BTreeImpl.Node(BTreeImpl.MAX / 2);
        //by changing currentNode.entryCount, we will treat any value
        //at index higher than the new currentNode.entryCount as if
        //it doesn't exist
        currentNode.entryCount = BTreeImpl.MAX / 2;
        //copy top half of h into t
        for (int j = 0; j < BTreeImpl.MAX / 2; j++) {
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
        }
        //external node
        if (height == 0) {
            newNode.setNext(currentNode.getNext());
            newNode.setPrevious(currentNode);
            currentNode.setNext(newNode);
        }
        return newNode;
    }

    @Override
    public void moveToDisk(Key k) throws Exception {
        Value v = get(k);
        this.persistenceManager.serialize(k, v);

        String addedFileString = k.toString() + ".json";
        if (k.toString().startsWith("http://")) {
            addedFileString = addedFileString.substring(7);
        }
        // Create the path of directories to create
        // If there are a series of directories to create, get the
        // file name by only taking from the last / and on
        int lastIndex = addedFileString.lastIndexOf('/');
        String directoryPathString = System.getProperty("user.dir");
        if (lastIndex != -1) {
            directoryPathString = System.getProperty("user.dir") + "/" + addedFileString.substring(0, lastIndex);
            addedFileString = addedFileString.substring(lastIndex + 1);
        }

        File addedFile = new File(directoryPathString, addedFileString);
        BTreeImpl.Entry uriEntry = this.get(this.root, k, this.height);
        if(uriEntry != null) {
            uriEntry.val = addedFile;
        }
    }

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.persistenceManager = pm;
    }
}
