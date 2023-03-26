package edu.yu.cs.com1320.project.impl;

import com.sun.jdi.Value;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class TrieImpl implements edu.yu.cs.com1320.project.Trie<Value> {

    @Override
    public void put(String key, Value val) {

    }

    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        return null;
    }

    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        return null;
    }

    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        return null;
    }

    @Override
    public Set<Value> deleteAll(String key) {
        return null;
    }

    @Override
    public Value delete(String key, Value val) {
        return null;
    }
}
