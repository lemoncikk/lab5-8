package org.example.model;

import java.util.Iterator;
import java.util.Stack;

public class CycledStack<T> implements Iterable<T>{
    int size;
    Stack<T> stack = new Stack<>();

    public CycledStack(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void push(T elem) {
        while(stack.size() >= size) {
            stack.pop();
        }
        stack.push(elem);
    }

    public T pop() {
        return stack.pop();
    }

    @Override
    public Iterator<T> iterator() {
        return stack.iterator();
    }
}
