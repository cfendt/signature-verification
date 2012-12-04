/**
 * Project: Signature Verification
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import static org.thoughtworks.signatureverification.SignatureVerificationConstants.*;
import org.thoughtworks.signatureverification.bean.SignatureData;
import org.thoughtworks.signatureverification.dtw.DynamicTimeWarping;
import org.thoughtworks.signatureverification.regression.ER2;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class Verification {

    /**
     * Verifies 2 signatures
     *
     * @param signatureDataTest testSig
     * @param folderName        Folder Name
     * @return Returns Final Intutive result
     * @throws java.io.IOException IOE
     */
    public double verifySignature(SignatureData signatureDataTest, String folderName) throws IOException, NoSuchAlgorithmException {
        DynamicTimeWarping dtw = new DynamicTimeWarping();
        ER2 eRSquared = new ER2();
        LinkedList<Double> xtCopy = signatureDataTest.getX();
        LinkedList<Double> ytCopy = signatureDataTest.getY();
        SignatureData signatureDataTestClone = new SignatureData(xtCopy, ytCopy, 0);
        double res1;
        double res2;
        double finalResult;
        final SignatureData sigData1 = DataIO.readData(folderName, FIRST_TEMPLATE_SIGNATURE_NORM);
        final SignatureData sigData2 = DataIO.readData(folderName, SECOND_TEMPLATE_SIGNATURE_NORM);
        final int size1 = dtw.performDTW(sigData1, signatureDataTest);
        sigData1.setNum(size1);
        signatureDataTest.setNum(size1);
        res1 = eRSquared.getRegressionValue(sigData1, signatureDataTest);
        final int size2 = dtw.performDTW(sigData2, signatureDataTestClone);
        sigData2.setNum(size2);
        signatureDataTestClone.setNum(size2);
        res2 = eRSquared.getRegressionValue(sigData2, signatureDataTestClone);
        finalResult = (res1 + res2) / 2.0;
        if (verifyDigest(folderName))
            return finalResult;
        else
            return 0.0;
        //Todo should consider Penups
    }

    protected boolean verifyDigest(String folderName) throws IOException, NoSuchAlgorithmException {
        MessageDigestCalculator messageDigestCalculator = new MessageDigestCalculator();
        String storedDigest1 = messageDigestCalculator.fetchDigest(folderName, FIRST_TEMPLATE_SIGNATURE_NORM_DIGEST);
        String storedDigest2 = messageDigestCalculator.fetchDigest(folderName, SECOND_TEMPLATE_SIGNATURE_NORM_DIGEST);
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String computedDigest1 = base64Encoder.encode(messageDigestCalculator.computeDigest(folderName, FIRST_TEMPLATE_SIGNATURE_NORM));
        String computedDigest2 = base64Encoder.encode(messageDigestCalculator.computeDigest(folderName, SECOND_TEMPLATE_SIGNATURE_NORM));
        return storedDigest1.equals(computedDigest1) && storedDigest2.equals(computedDigest2);
    }
}