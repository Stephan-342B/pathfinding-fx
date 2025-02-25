package org.mahefa.component.collection;

import java.util.Collections;

public class MinHeap<T extends Comparable<T>> extends Heap<T> {

    @Override
    public void heapify(int index) {
        final int leftChildIndex = (2 * index) + 1;
        final int rightChildIndex = leftChildIndex + 1;
        final int value = getIndex(index, leftChildIndex, rightChildIndex, data.size());

        if(value != index) {
            Collections.swap(data, index, value);
            heapify(value);
        }
    }

    int getIndex(int pIndex, int leftIndex, int rightIndex, int n) {
        T parent = data.get(pIndex);
        int smallestIndex = pIndex;

        if(leftIndex < n) {
            final T leftChild = data.get(leftIndex);

            if(leftChild.compareTo(parent) < 0)
                smallestIndex = leftIndex;
        }

        if(rightIndex < n) {
            final T rightChild = data.get(rightIndex);

            if(rightChild.compareTo(data.get(smallestIndex)) < 0)
                smallestIndex = rightIndex;
        }

        return smallestIndex;
    }
}
