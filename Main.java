package dz1;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        int[] numbers = {1, 1, 2, 4, 5};

        double average = Arrays.stream(numbers)
                .filter(n -> n%2 == 0)
                .average()
                .getAsDouble();

        System.out.println(average);

    }
}