package org.thoughtworks.signatureverification;

import static junit.framework.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class EnrollTest {
    private SignatureData standardSignatureData;
    private SignatureData normSignatureData;

    @Before
    public void setUp() throws Exception {
        String stdIpPath = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";
        String normIpPath = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";
        standardSignatureData = DataIO.readData(stdIpPath, "sig1Test");
        normSignatureData = DataIO.readData(normIpPath, "sig1NormTest");
    }

    @Test
    public void shouldEnrollData() throws Exception {
        Enroll enroll = new Enroll();
        SignatureData normIpData = enroll.enrollSignature(standardSignatureData);
        assertEquals(normSignatureData, normIpData);
    }

//    @Test
//    public void shouldGiveOutEnrollDataForRealTimeInputs() throws Exception {
//        TestFrame testFrame = new TestFrame();
//        SignatureData signatureData = testFrame.getSignatureData();
//        System.out.println(signatureData.getX().size());
//        Enroll enroll = new Enroll();
//        SignatureData signatureNormData = enroll.enrollSignature(signatureData);
//        System.out.println(signatureNormData.getX().size());
//        FileOutputStream fop = new FileOutputStream("D:\\Projects\\SignatureVerification\\test\\org\\thoughtworks\\sachin\\signatureverification\\b");
//        DataIO.writeData(signatureNormData, fop);
//        assertNotNull(signatureData);
//        assertTrue(signatureData.getX().size() != 0);
//    }
}