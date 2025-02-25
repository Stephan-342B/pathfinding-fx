package org.mahefa.component.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Heap<T> {

    protected List<T> data;

    public abstract void heapify(int index);

    public void add(T t) {
        if(data == null)
            data = new ArrayList<>();

        data.add(t);

        final int size = data.size();

        if(size > 1) {
            for(int i = (size / 2) - 1; i >= 0; i--) {
                heapify(i);
            }
        }
    }

    public void remove(T t) {
        if(!isEmpty()) {
            int count = data.size();
            int i;

            for(i = 0; i < count; i++) {
                if(t.equals(data.get(i)))
                    break;
            }

            // Swap it with the last element
            Collections.swap(data, count - 1, i);

            // Remove the last element
            data.remove(data.size() - 1);

            // Heapify
            count = data.size();

            if(!isEmpty()) {
                for(int j = (count / 2) - 1; j >= 0; j--) {
                    heapify(j);
                }
            }
        }
    }

    public T get() {
        return data.get(0);
    }

    public boolean contains(T t) {
        if(!isEmpty()) {
            if(t.equals(data.get(0)))
                return true;

            for(int i = data.size() - 1; i >= 1; i--) {
                if(t.equals(data.get(i)))
                    return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }
}
