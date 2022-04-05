package org.mahefa.data.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Heap<T> {

    protected List<T> list;

    public abstract void heapify(int index);

    public void insert(T t) {
        if(list == null)
            list = new ArrayList<>();

        list.add(t);

        final int size = list.size();

        if(size > 1) {
            for(int i = (size / 2) - 1; i >= 0; i--) {
                heapify(i);
            }
        }
    }

    public void remove(T t) {
        if(!isEmpty()) {
            int count = list.size();
            int i;

            for(i = 0; i < count; i++) {
                if(t.equals(list.get(i)))
                    break;
            }

            // Swap it with the last element
            Collections.swap(list, count - 1, i);

            // Remove the las element
            list.remove(list.size() - 1);

            // Heapify
            count = list.size();

            if(!isEmpty()) {
                for(int j = (count / 2) - 1; j >= 0; j--) {
                    heapify(j);
                }
            }
        }
    }

    public T get() {
        return list.get(0);
    }

    public boolean contains(T t) {
        if(!isEmpty()) {
            if(t.equals(list.get(0)))
                return true;

            for(int i = list.size() - 1; i >= 1; i--) {
                if(t.equals(list.get(i)))
                    return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
