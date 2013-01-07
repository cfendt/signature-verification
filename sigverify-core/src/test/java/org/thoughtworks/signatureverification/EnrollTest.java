package org.thoughtworks.signatureverification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class EnrollTest {
    private SignatureData standardSignatureData;
    private SignatureData normSignatureData;

    @Before
    public void setUp() throws Exception {
        this.standardSignatureData = DataIO.readData(this.getClass().getResourceAsStream("/org/thoughtworks/signatureverification/sig1Test"));
        this.normSignatureData = DataIO.readData(this.getClass().getResourceAsStream("/org/thoughtworks/signatureverification/sig1NormTest"));
    }

    @Test
    public void shouldEnrollData() throws Exception {
        final Enroll enroll = new Enroll();
        final SignatureData normIpData = enroll.enrollSignature(this.standardSignatureData);
        Assert.assertEquals(this.normSignatureData, normIpData);
    }
}