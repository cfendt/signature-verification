/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.regression;

import java.util.LinkedList;

import org.thoughtworks.signatureverification.bean.SignatureData;

public class ER2 {

    private double xrBarSourceData;
    private double yrBarSourceData;
    private double xtBarTestData;
    private double ytBarTestData;

    public double getRegressionValue(final SignatureData sourceData, final SignatureData testData) {
        double res;
        this.calculateMean(sourceData.getX(), sourceData.getY(), sourceData.getNum(), testData.getX(), testData.getY(), testData.getNum());
        res = this.evaluateFormulae(sourceData.getX(), sourceData.getY(), sourceData.getNum(), testData.getX(), testData.getY(), testData.getNum());
        return res;
    }

    public void calculateMean(final LinkedList<Double> xr, final LinkedList<Double> yr, final int nr, final LinkedList<Double> xt, final LinkedList<Double> yt, final int nt) {
        double sumx = 0;
        double sumy = 0;
        for (int i = 0; i < nr; i++) {
            sumx += xr.get(i);
            sumy += yr.get(i);
        }
        this.xrBarSourceData = sumx / nr;
        this.yrBarSourceData = sumy / nr;
        sumx = 0;
        sumy = 0;
        for (int i = 0; i < nt; i++) {
            sumx += xt.get(i);
            sumy += yt.get(i);
        }
        this.xtBarTestData = sumx / nt;
        this.ytBarTestData = sumy / nt;
    }

    public double evaluateFormulae(final LinkedList<Double> xr, final LinkedList<Double> yr, final int nr, final LinkedList<Double> xt, final LinkedList<Double> yt, final int nt) {
        double nume;
        nume = this.calculateNumerator(xr, yr, nr, xt, yt, nt);
        final double deno = this.calculateDenominator(xr, yr, nr, xt, yt, nt);
        return nume / deno;
    }

    public double calculateNumerator(final LinkedList<Double> xr, final LinkedList<Double> yr, final int nr, final LinkedList<Double> xt, final LinkedList<Double> yt, final int nt) {
        double insum1 = 0;
        for (int i = 0; i < nr; i++) {
            insum1 += (xr.get(i) - this.xrBarSourceData) * (xt.get(i) - this.xtBarTestData);
        }
        double insum2 = 0;
        for (int i = 0; i < nt; i++) {
            insum2 += (yr.get(i) - this.yrBarSourceData) * (yt.get(i) - this.ytBarTestData);
        }
        final double finalNumeSum = insum1 + insum2;
        return finalNumeSum * finalNumeSum;
    }

    public double calculateDenominator(final LinkedList<Double> xr, final LinkedList<Double> yr, final int nr, final LinkedList<Double> xt, final LinkedList<Double> yt, final int nt) {
        double xsum1 = 0;
        for (int i = 0; i < nr; i++) {
            xsum1 += (xr.get(i) - this.xrBarSourceData) * (xr.get(i) - this.xrBarSourceData);
        }
        double ysum1 = 0;
        for (int i = 0; i < nr; i++) {
            ysum1 += (yr.get(i) - this.yrBarSourceData) * (yr.get(i) - this.yrBarSourceData);
        }
        final double sum1 = xsum1 + ysum1;
        double xsum2 = 0;
        for (int i = 0; i < nt; i++) {
            xsum2 += (xt.get(i) - this.xtBarTestData) * (xt.get(i) - this.xtBarTestData);
        }
        double ysum2 = 0;
        for (int i = 0; i < nt; i++) {
            ysum2 += (yt.get(i) - this.ytBarTestData) * (yt.get(i) - this.ytBarTestData);
        }
        final double sum2 = xsum2 + ysum2;
        return sum1 * sum2;
    }
}
