package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private Node head;
    public StackImpl() {
        this.head = new Node(null);
    }
    private class Node {
        T data;
        Node next;
        Node(T e) {
            data = e;
            next = null;
        }
    }
    @Override
    public void push(T element) {
        Node newNode = new Node(element);
        newNode.next = this.head.next;
        this.head.next = newNode;
    }

    @Override
    public T pop() {
        if (this.head.next == null){
            return null;
        }
        T returning = this.head.next.data;
        this.head.next = this.head.next.next;
        return returning;
    }

    @Override
    public T peek() {
        if (this.head.next == null) {
            return null;
        }
        return this.head.next.data;
    }

    @Override
    public int size() {
        if (this.head.next == null){
            return 0;
        }
        int elements = 1;
        Node current = this.head.next;
        while(current.next != null){
            elements = elements + 1;
            current = current.next;
        }
        return elements;
    }
}
