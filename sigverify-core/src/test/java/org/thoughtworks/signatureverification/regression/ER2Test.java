package org.thoughtworks.signatureverification.regression;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thoughtworks.signatureverification.DataIO;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class ER2Test {

    private SignatureData signatureDataSourceAndTest;

    @Before
    public void setUp() throws IOException {
        final String folderName = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";
        this.signatureDataSourceAndTest = DataIO.readData(folderName, "sig1NormTest");
    }

    @Test
    public void shouldReturnValidNumeratorAfterCalculation() throws Exception {
        final ER2 er2 = new ER2();
        final double result = er2.calculateNumerator(this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum(),
                this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum());
        Assert.assertEquals(7640.2080133693, result);
    }

    @Test
    public void shouldReturnValidDenominatorAfterCalculation() throws Exception {
        final ER2 er2 = new ER2();
        final double result = er2.calculateDenominator(this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum(),
                this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum());
        Assert.assertEquals(7640.2080133693, result);
    }

    @Test
    public void shouldComputeTheFormulae() throws Exception {
        final ER2 er2 = new ER2();
        final Double result = er2.evaluateFormulae(this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum(),
                this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum());
        Assert.assertEquals(1, result.intValue());
    }

    @Test
    public void shouldComputeER2OfTwoDifferentSignatures() throws Exception {
        final ER2 er2 = new ER2();
        final SignatureData signatureDataHacked = this.hackSignatureData(this.signatureDataSourceAndTest);
        final Double result = er2.getRegressionValue(this.signatureDataSourceAndTest, signatureDataHacked);
        Assert.assertEquals(0, result.intValue());
    }

    @Test
    public void shouldComputeER2OfTwoSignatures() throws Exception {
        final ER2 er2 = new ER2();
        final Double result = er2.getRegressionValue(this.signatureDataSourceAndTest, this.signatureDataSourceAndTest);
        Assert.assertEquals(1, result.intValue());
    }

    @After
    public void tearDown() throws Exception {
    }

    private SignatureData hackSignatureData(final SignatureData signatureDataSourceAndTest) {
        final SignatureData signatureDataHacked = new SignatureData();
        final LinkedList<Double> xList = signatureDataSourceAndTest.getX();
        final LinkedList<Double> yList = signatureDataSourceAndTest.getX();
        final int numList = signatureDataSourceAndTest.getNum();
        for (int i = 1; i <= 10; i++) {
            xList.remove(xList.size() - i);
            yList.remove(yList.size() - i);
            xList.add(0.0);
            yList.add(0.0);
        }
        signatureDataHacked.setX(xList);
        signatureDataHacked.setY(yList);
        signatureDataHacked.setNum(numList);
        return signatureDataHacked;
    }
}