/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import org.apache.commons.codec.binary.Base64;
import org.thoughtworks.signatureverification.bean.SignatureData;
import org.thoughtworks.signatureverification.dtw.DynamicTimeWarping;
import org.thoughtworks.signatureverification.regression.ER2;

public final class Verification {

    /**
     * Verifies 2 signatures
     * 
     * @param signatureDataTest testSig
     * @param folderName Folder Name
     * @return Returns Final Intutive result
     * @throws java.io.IOException IOE
     */
    public double verifySignature(final SignatureData signatureDataTest, final String folderName) throws IOException, NoSuchAlgorithmException {
        final DynamicTimeWarping dtw = new DynamicTimeWarping();
        final ER2 eRSquared = new ER2();
        final LinkedList<Double> xtCopy = signatureDataTest.getX();
        final LinkedList<Double> ytCopy = signatureDataTest.getY();
        final SignatureData signatureDataTestClone = new SignatureData(xtCopy, ytCopy, 0);
        double res1;
        double res2;
        double finalResult;
        final SignatureData sigData1 = DataIO.readData(folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE_NORM);
        final SignatureData sigData2 = DataIO.readData(folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE_NORM);
        final int size1 = dtw.performDTW(sigData1, signatureDataTest);
        sigData1.setNum(size1);
        signatureDataTest.setNum(size1);
        res1 = eRSquared.getRegressionValue(sigData1, signatureDataTest);
        final int size2 = dtw.performDTW(sigData2, signatureDataTestClone);
        sigData2.setNum(size2);
        signatureDataTestClone.setNum(size2);
        res2 = eRSquared.getRegressionValue(sigData2, signatureDataTestClone);
        finalResult = (res1 + res2) / 2.0;
        if (this.verifyDigest(folderName)) {
            return finalResult;
        } else {
            return 0.0;
            // Todo should consider Penups
        }
    }

    protected boolean verifyDigest(final String folderName) throws IOException, NoSuchAlgorithmException {
        final MessageDigestCalculator messageDigestCalculator = new MessageDigestCalculator();
        final String storedDigest1 = messageDigestCalculator.fetchDigest(folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE_NORM_DIGEST);
        final String storedDigest2 = messageDigestCalculator.fetchDigest(folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE_NORM_DIGEST);
        final String computedDigest1 = Base64.encodeBase64String(messageDigestCalculator.computeDigest(folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE_NORM));
        final String computedDigest2 = Base64.encodeBase64String(messageDigestCalculator.computeDigest(folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE_NORM));
        return storedDigest1.equals(computedDigest1) && storedDigest2.equals(computedDigest2);
    }
}