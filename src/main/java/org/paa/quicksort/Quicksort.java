package org.paa.quicksort;

import java.util.Random;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public class Quicksort {
    // Enrollment number (M)

    // private static final int M = 719316;
    private static final int M = 15;
    private static volatile boolean isLoading = true;

    public static void quickSort(int[] array, int low, int high) {
        while (low < high) {
            int pivotIndex = handleQuicksortSepartion(array, low, high);

            // Recursively sort the smaller partition and iteratively sort the larger partition
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(array, low, pivotIndex - 1);
                low = pivotIndex + 1; // tail recursion on the larger part
            } else {
                quickSort(array, pivotIndex + 1, high);
                high = pivotIndex - 1; // tail recursion on the larger part
            }
        }
        // if (low < high) {
        //     int pivotIndex = partition(array, low, high);
        //     quickSort(array, low, pivotIndex - 1);
        //     quickSort(array, pivotIndex + 1, high);
        // }
    }

    private static int handleQuicksortSepartion(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
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
        array[i + 1] = array[high];
        array[high] = temp;

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
        int[] array = new int[n];

        for (int i = 0; i < n; i++) {
            array[i] = i + 1;
        }

        return array;
    }

    public static long measureExecutionTime(int[] array) {
        long startTime = System.currentTimeMillis();
        quickSort(array, 0, array.length - 1);
        long endTime = System.currentTimeMillis();

        return (endTime - startTime);
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
        double[] simulationArraysSizes = new double[simulations];
        double[] averageCaseTimesMeans = new double[simulations];
        double[] worstCaseTimesMeans = new double[simulations];

        Thread loadingThread = new Thread(() -> showLoadingIndicator());
        loadingThread.start();

        for (int i = 1; i <= simulations; i += 10) {
            int n = M * i / 10;
            simulationArraysSizes[i - 1] = n;

            // Average case (random arrays)
            long avgTime = 0;

            for (int j = 0; j < arrayCount; j++) {
                final int[] randomArray = generateRandomArray(n);
                avgTime += measureExecutionTime(randomArray);
            }

            // Calculate the average time for sorting 100 arrays
            averageCaseTimesMeans[i - 1] = avgTime / arrayCount;

            // Worst case (sorted arrays)
            long worstTime = 0;

            for (int j = 0; j < arrayCount; j++) {
                int[] worstArray = generateWorstCaseArray(n);
                worstTime += measureExecutionTime(worstArray);
            }

            // Calculate the average time for sorting 100 arrays
            worstCaseTimesMeans[i - 1] = worstTime / arrayCount;
        }

        // Plot the chart with average and worst cases series
        XYChart chart = new XYChartBuilder().width(800).height(600).title("QuickSort Simulations").xAxisTitle("n").yAxisTitle("Time (ms)").build();
        chart.addSeries("Average Case", simulationArraysSizes, averageCaseTimesMeans);
        chart.addSeries("Worst Case", simulationArraysSizes, worstCaseTimesMeans);

        new SwingWrapper<>(chart).displayChart();

        isLoading = false;

        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
