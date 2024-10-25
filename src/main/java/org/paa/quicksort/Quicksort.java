package org.paa.quicksort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class Quicksort {
    // Enrollment number (M)
    private static final int M = 719316;
    private static volatile boolean isLoading = true;

    private static void quickSort(int[] array, int left, int right) {
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

    private static int[] generateRandomArray(int n) {
        final int[] array = new int[n];
        final int seed = M;

        // Random number generator initialized with enrollment number as seed
        final Random random = new Random(seed);

        for (int i = 0; i < n; i++) {
            array[i] = random.nextInt();
        }

        return array;
    }

    private static int[] generateWorstCaseArray(int n) {
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

    private static double handleQuicksort(int[] array) {
        final double milliParseDenominator = 1000000.0;
        long startTime = System.nanoTime();
        quickSort(array, 0, array.length - 1);
        long endTime = System.nanoTime();
        final double totalTime = (endTime - startTime) / milliParseDenominator;

        return totalTime;
    }

    private static double[] handleRegression(double[] xData, double[] yData, boolean isAverageCase) {
        final SimpleRegression regression = new SimpleRegression();

        for (int i = 1; i < xData.length; i++) {
            final double xValue = xData[i];
            final double yValue = yData[i];

            if (isAverageCase) {
                // n log n for average case
                final double logN = xValue * Math.log(xValue);
                regression.addData(logN, yValue);
            } else {
                // n^2 for worst case
                final double nSquared = xValue * xValue;
                regression.addData(nSquared, yValue);
            }
        }

        return new double[]{regression.getSlope(), regression.getIntercept()};
    }

    private static double[] calculateRegressionValues(double slope, double intercept, double[] simulationArraySizes, boolean isAverageCase) {
        final double[] regressionValues = new double[simulationArraySizes.length];

        for (int i = 1; i < simulationArraySizes.length; i++) {
            final double n = simulationArraySizes[i];

            if (isAverageCase) {
                // n log n for average case
                final double logN = n * Math.log(n);
                regressionValues[i] = slope * logN + intercept;
            } else {
                // n^2 for worst case
                final double nSquared = n * n;
                regressionValues[i] = slope * nSquared + intercept;
            }
        }

        return regressionValues;
    }

    private static XYChart handleCaseChart(String title, double[] sizes, double[] times, double[] regressionValues) {
        final XYChart chart = new XYChartBuilder().width(600).height(400).title(title).xAxisTitle("n").yAxisTitle("Average time (ms)").build();
        chart.addSeries("T(n)", sizes, times);

        final XYSeries regressionSeries = chart.addSeries("Regression", sizes, regressionValues);
        regressionSeries.setLineStyle(SeriesLines.DASH_DASH).setLineWidth(1);
        regressionSeries.setMarker(SeriesMarkers.CROSS);

        return chart;
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

        // Perform regression for the average case
        final double[] avgRegressionCoeffs = handleRegression(simulationArraySizes, averageCaseTimesMeans, true);
        final double avgSlope = avgRegressionCoeffs[0];
        final double avgIntercept = avgRegressionCoeffs[1];
        final double[] averageRegressionValues = calculateRegressionValues(avgSlope, avgIntercept, simulationArraySizes, true);

        // Perform regression for the worst case
        final double[] worstRegressionCoeffs = handleRegression(simulationArraySizes, worstCaseTimesMeans, false);
        final double worstSlope = worstRegressionCoeffs[0];
        final double worstIntercept = worstRegressionCoeffs[1];
        final double[] worstRegressionValues = calculateRegressionValues(worstSlope, worstIntercept, simulationArraySizes, false);

        final List<XYChart> charts = new ArrayList<>();

        // Build the chart with average case series
        XYChart averageCasechart = handleCaseChart("Average Case", simulationArraySizes, averageCaseTimesMeans, averageRegressionValues);
        charts.add(averageCasechart);

        // Build the chart with worst cases series
        XYChart worseCasechart = handleCaseChart("Worst Case", simulationArraySizes, worstCaseTimesMeans, worstRegressionValues);
        charts.add(worseCasechart);

        new SwingWrapper<>(charts).displayChartMatrix();

        isLoading = false;

        try {
            loadingThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
