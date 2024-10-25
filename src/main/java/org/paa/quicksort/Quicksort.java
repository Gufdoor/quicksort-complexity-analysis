package org.paa.quicksort;

import java.util.Random;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class Quicksort {
    // Enrollment number (M)

    private static final int M = 719316;
    private static volatile boolean isLoading = true;

    public static void quickSort(int[] array, int left, int right) {
        while (left < right) {
            int pivotIndex = handleQuicksortPartition(array, left, right);

            // Recursively sort the smaller partition and iteratively sort the larger partition
            if (pivotIndex - left < right - pivotIndex) {
                quickSort(array, left, pivotIndex - 1);
                left = pivotIndex + 1;
            } else {
                quickSort(array, pivotIndex + 1, right);
                right = pivotIndex - 1;
            }
        }

        if (left < right) {
            int pivotIndex = handleQuicksortPartition(array, left, right);
            quickSort(array, left, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, right);
        }
    }

    private static int handleQuicksortPartition(int[] array, int left, int right) {
        int pivot = array[right];
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (array[j] <= pivot) {
                i++;
                // Swap
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        // Swap pivot
        int temp = array[i + 1];
        array[i + 1] = array[right];
        array[right] = temp;

        return i + 1;
    }

    public static int[] generateRandomArray(int n) {
        final int[] array = new int[n];
        final int seed = M;

        // Random number generator initialized with enrollment number as seed
        final Random random = new Random(seed);

        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt();
        }

        return array;
    }

    public static int[] generateWorstCaseArray(int n) {
        final int[] array = new int[n];
        final int seed = M;

        // Random number generator initialized with enrollment number as seed
        final Random random = new Random(seed);

        // Random number will always be equal to one of the following interval: 1-100
        final int baseNumber = random.nextInt(100) + 1;

        for (int i = 0; i < n; i++) {
            array[i] = baseNumber + i;
        }

        return array;
    }

    public static double handleQuicksort(int[] array) {
        final double milliParseDenominator = 1000000.0;
        long startTime = System.nanoTime();
        quickSort(array, 0, array.length - 1);
        long endTime = System.nanoTime();
        final double totalTime = (endTime - startTime) / milliParseDenominator;

        return totalTime;
    }

    // Poor temporary solution - to be improved
    private static void showLoadingIndicator() {
        System.out.print("\nLoading");

        // Milliseconds
        final int sleepInterval = 500;

        while (isLoading) {
            System.out.print(".");
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nLoading complete!");
    }

    public static void main(String[] args) {
        final int simulations = 1000;
        final int arrayCount = 100;
        double[] simulationArraySizes = new double[arrayCount];
        double[] averageCaseTimesMeans = new double[arrayCount];
        double[] worstCaseTimesMeans = new double[arrayCount];

        Thread loadingThread = new Thread(() -> showLoadingIndicator());
        loadingThread.start();

        for (int i = 1, j = 0; i <= simulations; i += 10, j++) {
            int n = M * i / 10;
            simulationArraySizes[j] = n;
        }

        // Run 100 simulations
        for (int i = 1; i < arrayCount; i++) {
            int n = (int) simulationArraySizes[i]; 

            // Average case (random arrays)
            double avgElapsedTime = 0;

            for (int j = 0; j < arrayCount; j++) {
                final int[] randomArray = generateRandomArray(n);
                avgElapsedTime += handleQuicksort(randomArray);
            }

            averageCaseTimesMeans[i] = avgElapsedTime / (double) arrayCount;

            // Worst case (sorted arrays)
            double worstElapsedTime = 0;

            for (int j = 0; j < arrayCount; j++) {
                final int[] worstArray = generateWorstCaseArray(n);
                worstElapsedTime += handleQuicksort(worstArray);
            }

            worstCaseTimesMeans[i] = worstElapsedTime / (double) arrayCount;
        }

        // Plot the chart with average and worst cases series
        XYChart chart = new XYChartBuilder().width(800).height(600).title("QuickSort Analysis").xAxisTitle("n").yAxisTitle("Avarage time (ms)").build();
        chart.addSeries("Average Case", simulationArraySizes, averageCaseTimesMeans);
        chart.addSeries("Worst Case", simulationArraySizes, worstCaseTimesMeans);

        new SwingWrapper<>(chart).displayChart();

        isLoading = false;

        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
