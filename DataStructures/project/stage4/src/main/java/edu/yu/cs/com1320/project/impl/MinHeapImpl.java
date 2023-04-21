package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.stage4.Document;

public class MinHeapImpl extends MinHeap<Document> {
    @Override
    public void reHeapify(Document element) {
        upHeap(getArrayIndex(element));
        downHeap(getArrayIndex(element));
    }

    @Override
    protected int getArrayIndex(Document element) {
        for (int i = 1; i <= elements.length; i++) {
            if(elements[i] == null) {
                return -1;
            }
            if(elements[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void doubleArraySize() {
        int previousArraySize = elements.length;
        Document[] temp = (Document[]) new Object[previousArraySize * 2];
        for (int i = 1; i <= previousArraySize; i++) {
            temp[i] = elements[i];
        }
        elements = temp;
    }
}
