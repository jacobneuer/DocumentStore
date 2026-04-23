package edu.yu.cs.com1320.project.impl;

public class HashTableImpl<Key,Value> implements edu.yu.cs.com1320.project.HashTable<Key, Value> {
    //constructor for NHT
    public HashTableImpl() {
        this.table = new Object[5];
        for (int i = 0; i < 5; i++) {
            table[i] = new LinkedEntries();
        }
    }
    private class Entry<Key, Value> {

        Key key;
        Value value;

        Entry(Key k, Value v) {
            if (k == null) {
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
        }
    }
    private Object[] table;
    private int hashFunction(Key key) {
        if (key != null) {
            return (key.hashCode() & 0x7fffffff) % this.table.length;
        }
        throw new IllegalArgumentException("Can't Hash a Null Key");
    }

    @Override
    public Value get(Key k) {
        int index = this.hashFunction(k);
        LinkedEntries list = (LinkedEntries) this.table[index];
        LinkedEntries.Node current = list.head;
        if (current == null) {
            return null;
        }
        while (current != null){
            if (current.entry.key.equals(k)){
                Value rtrn = (Value) current.entry.value;
                return rtrn;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public Value put(Key k, Value v) {
        int index = this.hashFunction(k);
        Entry<Key, Value> putEntry = new Entry<>(k, v);
        LinkedEntries list = (LinkedEntries) this.table[index];
        LinkedEntries.Node current = list.head;
        if (v == null) {
            return deleteEntry(k);
        }
        if (current == null) {
            list.addNode(putEntry);
            return null;
        }
        return finishPut(k, v);
    }

    @Override
    public boolean containsKey(Key key) {
        if (key == null) {
            throw new NullPointerException("The Key You're Trying to Find is Null");
        }
        int index = this.hashFunction(key);
        LinkedEntries list = (LinkedEntries) this.table[index];
        LinkedEntries.Node current = list.head;
        if (current == null) {
            return false;
        }
        else {
            return finishFindingKey(key);
        }
    }

    private boolean finishFindingKey(Key key) {
        int index = this.hashFunction(key);
        LinkedEntries list = (LinkedEntries) this.table[index];
        LinkedEntries.Node current = list.head;
        Key currentKey = (Key) current.entry.key;
        if (currentKey.equals(key)) {
            return true;
        }
        while (current != null){
            if (current.next != null) {
                Key loopKey = (Key) current.next.entry.key;
                if(loopKey.equals(key)) {
                    return true;
                }
            }
            current = current.next;
        }
        return false;
    }

    private Value deleteEntry(Key k){
        int index = this.hashFunction(k);
        LinkedEntries list = (LinkedEntries) this.table[index];
        LinkedEntries.Node current = list.head;
        if (current == null) {
            return null;
        }
        Key currentKey = (Key) current.entry.key;
        if (currentKey.equals(k)) {
            Value old = (Value) current.entry.value;
            list.head = list.head.next;
            return old;
        }
        else {
            while (current != null){
                if (current.next != null) {
                    Key loopKey = (Key) current.next.entry.key;
                    if(loopKey.equals(k)) {
                        Value old = (Value) current.next.entry.value;
                        current.next = current.next.next;
                        return old;
                    }
                }
                current = current.next;
            }
        }
        return null;
    }

    private Value finishPut(Key k, Value v){
        int index = this.hashFunction(k);
        Entry<Key, Value> putEntry = new Entry<>(k, v);
        LinkedEntries list = (LinkedEntries) this.table[index];
        LinkedEntries.Node current = list.head;
        Key currentKey = (Key) current.entry.key;
        if (currentKey.equals(k)) {
            Value old = (Value) current.entry.value;
            list.head = list.head.next;
            list.addNode(putEntry);
            return old;
        }
        while (current != null){
            if (current.next != null) {
                Key loopKey = (Key) current.next.entry.key;
                if(loopKey.equals(k)) {
                    Value old = (Value) current.next.entry.value;
                    current.next = current.next.next;
                    list.addNode(putEntry);
                    return old;
                }
            }
            current = current.next;
        }
        list.addNode(putEntry);
        return null;
    }

    private class LinkedEntries {
        Node head;
        class Node {
            Entry entry;
            Node next;
            Node(Entry e) {
                entry = e;
                next = null;
            }
        }
        public void addNode (Entry entry){
            Node newNode = new Node(entry);
            if (this.head == null){
                this.head = newNode;
            }
            else {
                newNode.next = head;
                head = newNode;
            }
        }
    }
}
