package org.thoughtworks.signatureverification.regression;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thoughtworks.signatureverification.DataIO;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class ER2Test {

    /** Précision pour la comparaison */
    private static final double PRECISION = 0.000001;

    private SignatureData signatureDataSourceAndTest;

    @Before
    public void setUp() throws IOException {
        this.signatureDataSourceAndTest = DataIO.readData(this.getClass().getResourceAsStream("/org/thoughtworks/signatureverification/sig1NormTest"));
    }

    @Test
    public void shouldReturnValidNumeratorAfterCalculation() throws Exception {
        final ER2 er2 = new ER2();
        final double result = er2.calculateNumerator(this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum(),
                this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum());
        Assert.assertEquals(7640.2080133693, result, ER2Test.PRECISION);
    }

    @Test
    public void shouldReturnValidDenominatorAfterCalculation() throws Exception {
        final ER2 er2 = new ER2();
        final double result = er2.calculateDenominator(this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum(),
                this.signatureDataSourceAndTest.getX(), this.signatureDataSourceAndTest.getY(), this.signatureDataSourceAndTest.getNum());
        Assert.assertEquals(7640.2080133693, result, ER2Test.PRECISION);
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
        final List<Double> xList = signatureDataSourceAndTest.getX();
        final List<Double> yList = signatureDataSourceAndTest.getX();
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