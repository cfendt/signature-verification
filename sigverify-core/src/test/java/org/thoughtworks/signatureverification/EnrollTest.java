package org.thoughtworks.signatureverification;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class EnrollTest {
    private SignatureData standardSignatureData;
    private SignatureData normSignatureData;

    @Before
    public void setUp() throws Exception {
        final String stdIpPath = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";
        final String normIpPath = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";
        this.standardSignatureData = DataIO.readData(stdIpPath, "sig1Test");
        this.normSignatureData = DataIO.readData(normIpPath, "sig1NormTest");
    }

    @Test
    public void shouldEnrollData() throws Exception {
        final Enroll enroll = new Enroll();
        final SignatureData normIpData = enroll.enrollSignature(this.standardSignatureData);
        Assert.assertEquals(this.normSignatureData, normIpData);
    }
}