/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.dtw;

import java.util.LinkedList;
import java.util.List;

import org.thoughtworks.signatureverification.bean.SignatureData;

public final class DynamicTimeWarping {

    private double[][] dtwMatrix;
    private int[][] wrapPath;
    private int warpLength = 0;

    public int performDTW(final SignatureData signatureDataStored, final SignatureData signatureDataTest) {
        double[][] distanceMatrix;
        final List<Double> xStoredData = signatureDataStored.getX();
        final List<Double> yStoredData = signatureDataStored.getY();
        int numStoredData = xStoredData.size();
        final List<Double> xTestData = signatureDataTest.getX();
        final List<Double> yTestData = signatureDataTest.getY();
        final int numTestData = xTestData.size();
        this.dtwMatrix = null;
        distanceMatrix = new double[numStoredData][numTestData];
        this.dtwMatrix = new double[numStoredData][numTestData];
        for (int i = 0; i < numStoredData; i++) {
            for (int j = 0; j < numTestData; j++) {
                final double u = Math.sqrt(xStoredData.get(i) * xStoredData.get(i) + yStoredData.get(i) * yStoredData.get(i));
                final double v = Math.sqrt(xTestData.get(j) * xTestData.get(j) + yTestData.get(j) * yTestData.get(j));
                distanceMatrix[i][j] = (u - v) * (u - v);
            }
        }
        this.dtwMatrix[0][0] = distanceMatrix[0][0];
        for (int i = 1; i < numStoredData; i++) {
            this.dtwMatrix[i][0] = distanceMatrix[i][0] + this.dtwMatrix[i - 1][0];
        }
        for (int i = 1; i < numTestData; i++) {
            this.dtwMatrix[0][i] = distanceMatrix[0][i] + this.dtwMatrix[0][i - 1];
        }
        for (int i = 1; i < numStoredData; i++) {
            for (int j = 1; j < numTestData; j++) {
                this.dtwMatrix[i][j] = distanceMatrix[i][j] + Math.min(this.dtwMatrix[i - 1][j], Math.min(this.dtwMatrix[i - 1][j - 1], this.dtwMatrix[i][j - 1]));
            }
        }
        this.warpLength = this.computePath(numStoredData, numTestData);
        numStoredData = this.timeWarpSeries(xStoredData, yStoredData, 0);
        this.timeWarpSeries(xTestData, yTestData, 1);
        return numStoredData;
    }

    private int computePath(final int numStoredData, final int numTestData) {
        this.wrapPath = new int[4000][2];
        int numOptimalDistance = 0;
        int i = numStoredData - 1;
        int j = numTestData - 1;
        double minimumValue;
        while (i > 1 || j > 1) {
            if (i == 1) {
                j--;
            } else if (j == 1) {
                i--;
            } else {
                minimumValue = Math.min(this.dtwMatrix[i - 1][j], Math.min(this.dtwMatrix[i - 1][j - 1], this.dtwMatrix[i][j - 1]));
                if (minimumValue == this.dtwMatrix[i - 1][j]) {
                    i--;
                } else if (minimumValue == this.dtwMatrix[i][j - 1]) {
                    j--;
                } else {
                    i--;
                    j--;
                }
            }
            this.wrapPath[numOptimalDistance][0] = i;
            this.wrapPath[numOptimalDistance][1] = j;
            numOptimalDistance++;
        }
        return numOptimalDistance;
    }

    private int timeWarpSeries(final List<Double> xData, final List<Double> yData, final int indexInWarpMatrix) {
        final List<Double> xTemp = new LinkedList<Double>();
        final List<Double> yTemp = new LinkedList<Double>();
        for (int i = this.warpLength - 1; i >= 0; i--) {
            xTemp.add(xData.get(this.wrapPath[i][indexInWarpMatrix]));
            yTemp.add(yData.get(this.wrapPath[i][indexInWarpMatrix]));
        }
        xData.clear();
        xData.addAll(xTemp);
        yData.clear();
        yData.addAll(yTemp);
        return xTemp.size();
    }
}
