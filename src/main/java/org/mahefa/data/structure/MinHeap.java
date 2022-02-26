package org.mahefa.data.structure;

public class MinHeap<T> extends Heap<T> {

    @Override
    public void heapify(int index) {
        final int leftChildIndex = (2 * index) + 1;
        final int rightChildIndex = leftChildIndex + 1;
        final int value = getIndex(index, leftChildIndex, rightChildIndex, array.getCount());

        if(value != index) {
            array.swap(index, value);
            heapify(value);
        }
    }

    int getIndex(int pIndex, int leftIndex, int rightIndex, int n) {
        T parent = array.get(pIndex);
        final int pValue = 0;

        if(leftIndex < n) {
            final T leftChild = array.get(leftIndex);
            final int value = 0; // F

            if(value < pValue)
                pIndex = leftIndex;
        }

        if(rightIndex < n) {
            final T rightChild = array.get(rightIndex);
            final int value = 0; // F

            if(value < pValue)
                pIndex = leftIndex;
        }

        return pIndex;
    }
}
