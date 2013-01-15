/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import java.util.LinkedList;
import java.util.List;

import org.thoughtworks.signatureverification.bean.SignatureData;

public final class Enroll {
    public SignatureData enrollSignature(final SignatureData signatureData) {
        final List<Double> yList = signatureData.getY();
        final List<Double> xList = signatureData.getX();
        double minx, maxx;
        double miny, maxy;
        minx = this.min(xList);
        maxx = this.max(xList);
        miny = this.min(yList);
        maxy = this.max(yList);
        final List<Double> xTemp = new LinkedList<Double>();
        final List<Double> yTemp = new LinkedList<Double>();
        for (int i = 0; i < signatureData.getX().size(); i++) {
            xTemp.add((xList.get(i) - minx) / (maxx - minx));
            yTemp.add((yList.get(i) - miny) / (maxy - miny));
        }
        return new SignatureData(xTemp, yTemp, 0);
    }

    private double min(final List<Double> data) {
        double minTemp = Double.MAX_VALUE;
        for (final Double aDouble : data) {
            if (aDouble < minTemp) {
                minTemp = aDouble;
            }
        }
        return minTemp;
    }

    private double max(final List<Double> data) {
        double maxTemp = Double.MIN_VALUE;
        for (final Double aDouble : data) {
            if (aDouble > maxTemp) {
                maxTemp = aDouble;
            }
        }
        return maxTemp;
    }
}
