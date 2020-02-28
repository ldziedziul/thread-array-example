package pl.dziedziul.threadarray.example;

import org.junit.Test;

import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ThreadFunTest {
    @Test
    public void shouldCount() throws InterruptedException {
        int[] numbers = new int[40320000];
        fillWithRandomNumber(numbers);
        long start = System.nanoTime();
        int countNegativesNoThreads = countNegatives(numbers);
        long stop = System.nanoTime();
        System.out.println("No threads: " + countNegativesNoThreads + " time: " + (stop - start) / 1_000_000 + "ms");
        assertThat(countNegativesNoThreads, is(-2017649200));
        start = System.nanoTime();
        int countNegativesSingleThread = countNegatives(numbers, 1);
        stop = System.nanoTime();
        System.out.println("Single threads " + countNegativesNoThreads + " time: " + (stop - start) / 1_000_000 + "ms");
        assertThat(countNegativesSingleThread, is(-2017649200));
    }

    private static void fillWithRandomNumber(final int[] numbers) {
        Random r = new Random(10);
        for (int i = 0, numbersLength = numbers.length; i < numbersLength; i++) {
            numbers[i] = r.nextInt(10) - 5;
        }
    }

    private static int countNegatives(int[] numbers) {
        int count = 0;
        int length = numbers.length;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < 100; j++) {
                count += numbers[i];
            }
        }
        return count;
    }

    static int countNegatives(int[] numbers, int numberOfThreads) throws InterruptedException {
        int[] results = new int[numberOfThreads];
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new Counter(numbers, results, i, numberOfThreads));
            threads[i].start();
        }
        int sum = 0;
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
            sum += results[i];
        }
        return sum;
    }

    private static class Counter implements Runnable {
        private final int[] numbers;
        private final int[] results;
        private final int myNumber;
        private final int threadCount;

        private Counter(final int[] numbers, final int[] results, final int myNumber, final int threadCount) {
            this.numbers = numbers;
            this.results = results;
            this.myNumber = myNumber;
            this.threadCount = threadCount;
        }


        @Override
        public void run() {
            int rangeStart = (numbers.length / threadCount) * myNumber;
            int rangeStop = rangeStart + (numbers.length / threadCount);
            int count = 0;
            for (int i = rangeStart; i < rangeStop; i++) {
                for (int j = 0; j < 100; j++) {
                    count += numbers[i];
                }
            }
            results[myNumber] = count;
        }
    }
}
