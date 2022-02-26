package org.mahefa.data.structure;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.EmptyStackException;

public class DynamicArray<T> {

    private T[] array;
    private int count;
    private int size;

    public DynamicArray() {
        this.array = createInstance(1);
        this.count = 0;
        this.size = 1;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void add(T t) {
        // If the array is full double its size
        if(count == size) {
            growSize();
        }

        /**
         * Add the element at the end of the array
         * then increase the count value
         */
        array[count] = t;
        count++;
    }

    public void removeAt(int index) {
        if(count > 0) {
            for(int i = index; i < count; i++) {
                array[i] = array[i + 1];
            }

            removeLast();
        }
    }

    public void removeLast() {
        if(count > 0) {
            array[count] = null;
            count--;
        }
    }

    public T get(int index) {
        if(index > size)
            throw new IndexOutOfBoundsException("Index out of bounds");

        if(count == 0)
            throw new EmptyStackException();

        return array[index];
    }

    public void swap(int index1, int index2) {
        if(index1 > size || index2 > size)
            throw new IndexOutOfBoundsException("Index out of bounds");

        if(count >= 2) {
            T tmp = array[index1];
            array[index1] = array[index2];
            array[index2] = tmp;
        }
    }

    public void shrink() {
        T[] tmp;

        if(count > 0) {
            tmp = createInstance(count);

            for(int i = 0; i < count; i++) {
                tmp[i] = array[i];
            }

            size = count;
            array = tmp;
        }
    }

    void growSize() {
        final int nSize = size * 2;
        T[] tmp = null;

        if(count == size) {
            tmp = createInstance(nSize);

            for(int i = 0; i < count; i++) {
                tmp[i] = array[i];
            }
        }

        array = tmp;
        size = nSize;
    }

    T[] createInstance(int initialSize) {
        Class<T> typeOfT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return  (T[]) Array.newInstance(typeOfT, initialSize);
    }
}
