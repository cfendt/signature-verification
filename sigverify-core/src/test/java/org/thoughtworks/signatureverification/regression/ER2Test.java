package org.thoughtworks.signatureverification.regression;

import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.thoughtworks.signatureverification.DataIO;
import org.thoughtworks.signatureverification.bean.SignatureData;

import java.io.IOException;
import java.util.LinkedList;

public class ER2Test {

    private SignatureData signatureDataSourceAndTest;

    @Before
    public void setUp() throws IOException {
        String folderName = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";
        signatureDataSourceAndTest = DataIO.readData(folderName, "sig1NormTest");
    }

    @Test
    public void shouldReturnValidNumeratorAfterCalculation() throws Exception {
        ER2 er2 = new ER2();
        double result = er2.calculateNumerator(signatureDataSourceAndTest.getX(), signatureDataSourceAndTest.getY(), signatureDataSourceAndTest.getNum(), signatureDataSourceAndTest.getX(), signatureDataSourceAndTest.getY(), signatureDataSourceAndTest.getNum());
        assertEquals(7640.2080133693, result);
    }

    @Test
    public void shouldReturnValidDenominatorAfterCalculation() throws Exception {
        ER2 er2 = new ER2();
        double result = er2.calculateDenominator(signatureDataSourceAndTest.getX(), signatureDataSourceAndTest.getY(), signatureDataSourceAndTest.getNum(), signatureDataSourceAndTest.getX(), signatureDataSourceAndTest.getY(), signatureDataSourceAndTest.getNum());
        assertEquals(7640.2080133693, result);
    }

    @Test
    public void shouldComputeTheFormulae() throws Exception {
        ER2 er2 = new ER2();
        Double result = er2.evaluateFormulae(signatureDataSourceAndTest.getX(), signatureDataSourceAndTest.getY(), signatureDataSourceAndTest.getNum(), signatureDataSourceAndTest.getX(), signatureDataSourceAndTest.getY(), signatureDataSourceAndTest.getNum());
        assertEquals(1, result.intValue());
    }

    @Test
    public void shouldComputeER2OfTwoDifferentSignatures() throws Exception {
        ER2 er2 = new ER2();
        SignatureData signatureDataHacked = hackSignatureData(signatureDataSourceAndTest);
        Double result = er2.getRegressionValue(signatureDataSourceAndTest, signatureDataHacked);
        assertEquals(0, result.intValue());
    }

    @Test
    public void shouldComputeER2OfTwoSignatures() throws Exception {
        ER2 er2 = new ER2();
        Double result = er2.getRegressionValue(signatureDataSourceAndTest, signatureDataSourceAndTest);
        assertEquals(1, result.intValue());
    }

    @After
    public void tearDown() throws Exception {
    }

    private SignatureData hackSignatureData(SignatureData signatureDataSourceAndTest) {
        SignatureData signatureDataHacked = new SignatureData();
        LinkedList<Double> xList = signatureDataSourceAndTest.getX();
        LinkedList<Double> yList = signatureDataSourceAndTest.getX();
        int numList = signatureDataSourceAndTest.getNum();
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