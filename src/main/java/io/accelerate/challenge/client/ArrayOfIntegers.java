package io.accelerate.challenge.client;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayOfIntegers extends ArrayList<Integer> {
    public static ArrayOfIntegers of(Integer ... values) {
        ArrayOfIntegers arrayOfIntegers = new ArrayOfIntegers();
        arrayOfIntegers.addAll(Arrays.asList(values));
        return arrayOfIntegers;
    }
}
