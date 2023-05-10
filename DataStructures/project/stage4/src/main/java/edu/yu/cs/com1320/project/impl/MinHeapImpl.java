package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {

    public MinHeapImpl() {
        elements = (E[]) new Comparable[10];
    }
    @Override
    public void reHeapify(E element) {
        upHeap(getArrayIndex(element));
        downHeap(getArrayIndex(element));
    }

    @Override
    protected int getArrayIndex(E element) {
        if (element == null){
            throw new NoSuchElementException("There is no Such Element in the Heap");
        }
        for (int i = 1; i < elements.length; i++) {
            if(elements[i] == null) {
                throw new NoSuchElementException("There is no Such Element in the Heap");
            }
            if(elements[i].equals(element)) {
                return i;
            }
        }
        throw new NoSuchElementException("There is no Such Element in the Heap");
    }

    @Override
    protected void doubleArraySize() {
        int previousArraySize = elements.length;
        E[] temp = (E[]) new Comparable[previousArraySize * 2];
        for (int i = 1; i < previousArraySize; i++) {
            temp[i] = elements[i];
        }
        elements = temp;
    }
}
