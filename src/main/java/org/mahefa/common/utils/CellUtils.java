package org.mahefa.common.utils;

import org.mahefa.common.enumerator.Direction;

import java.util.*;

public final class CellUtils {

    private static final List<Direction> directions = Arrays.asList(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT);

    public static <T> T pickRandomly(T[] array) {
        final int index = new Random().nextInt(array.length );
        return array[(index < array.length) ? index : index - 1];
    }

    public static <T> T pickRandomly(List<? extends T> list) {
        final int index = new Random().nextInt(list.size());
        return list.get((index < list.size()) ? index : index - 1);
    }

    public static Direction pickRandomly() {
        return pickRandomly(directions);
    }

    public static <E> E pickRandomly(Set<? extends E> set){
        /*
         * Generate a random number using nextInt
         * method of the Random class.
         */
        Random random = new Random();

        //this will generate a random number between 0 and HashSet.size - 1
        int randomNumber = random.nextInt(set.size());

        //get an iterator
        Iterator<? extends E> iterator = set.iterator();

        int currentIndex = 0;
        E randomElement = null;

        //iterate the HashSet
        while(iterator.hasNext()) {
            randomElement = iterator.next();

            //if current index is equal to random number
            if(currentIndex == randomNumber)
                return randomElement;

            //increase the current index
            currentIndex++;
        }

        return randomElement;
    }

    public static <T> T pickRandomly(T[][] array) {
        final int i = new Random().nextInt(array.length);
        final int j = new Random().nextInt(array[0].length);
        return array[(i < array.length) ? i : i - 1][(j < array.length) ? j : j - 1];
    }

    public static <T> T pickRandomlyOdd(T[][] array) {
        int i = new Random().nextInt(array.length - 2);
        int j = new Random().nextInt(array[0].length - 2);
        i = (i % 2 == 0) ? i / 2 * 2 + 1 : i;
        j = (j % 2 == 0) ? j / 2 * 2 + 1 : j;
        return array[(i < array.length) ? i : i - 1][(j < array.length) ? j : j - 1];
    }

    private static boolean checkBoundaries(int index, int limit) {
        return (index >= 0 && index < limit - 2);
    }
}
