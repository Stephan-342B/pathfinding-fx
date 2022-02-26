package org.mahefa.data.structure;

public abstract class Heap<T> {

    protected DynamicArray<T> array;

    public abstract void heapify(int index);

    public void insert(T t) {
        if(array == null)
            array = new DynamicArray<>();

        array.add(t);

        final int size = array.getCount();

        if(size > 1) {
            for(int i = (size / 2) - 1; i >= 0; i--) {
                heapify(i);
            }
        }
    }

    public void remove(T t) {
        if(!isEmpty()) {
            int count = array.getCount();
            int i;

            for(i = 0; i < count; i++) {
                if(t.equals(array.get(i)))
                    break;
            }

            // Swap it with the last element
            array.swap(count - 1, i);

            // Remove the las element
            array.removeLast();

            // Heapify
            count = array.getCount();

            if(!isEmpty()) {
                for(int j = (count / 2) - 1; j >= 0; j--) {
                    heapify(j);
                }
            }
        }
    }

    public T get() {
        return array.get(0);
    }

    public boolean contains(T t) {
        if(!isEmpty()) {
            if(t.equals(array.get(0)))
                return true;

            for(int i = array.getCount() - 1; i >= 1; i--) {
                if(t.equals(array.get(0)))
                    return true;
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return array.isEmpty();
    }
}
