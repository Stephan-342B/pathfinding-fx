package org.mahefa.data.structure;

import org.mahefa.data.ComparableNode;

import java.util.Collections;

public class MinHeap<T extends ComparableNode> extends Heap<T> {

    @Override
    public void heapify(int index) {
        final int leftChildIndex = (2 * index) + 1;
        final int rightChildIndex = leftChildIndex + 1;
        final int value = getIndex(index, leftChildIndex, rightChildIndex, list.size());

        if(value != index) {
            Collections.swap(list, index, value);
            heapify(value);
        }
    }

    int getIndex(int pIndex, int leftIndex, int rightIndex, int n) {
        T parent = list.get(pIndex);
        final int pValue = 0;

        if(leftIndex < n) {
            final T leftChild = list.get(leftIndex);

            if(leftChild.getValue() < pValue)
                pIndex = leftIndex;
        }

        if(rightIndex < n) {
            final T rightChild = list.get(rightIndex);

            if(rightChild.getValue() < pValue)
                pIndex = leftIndex;
        }

        return pIndex;
    }
}
