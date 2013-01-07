package org.thoughtworks.signatureverification;

import org.junit.Assert;
import org.junit.Test;

import sun.misc.BASE64Encoder;

public class MessageDigestCalculatorTest {

    @Test
    public void shouldComputeDigestForNormalizedSignatureData() throws Exception {
        final String folderName = "test\\org\\thoughtworks\\signatureverification";
        final BASE64Encoder base64Encoder = new BASE64Encoder();
        final MessageDigestCalculator messageDigestCalculator = new MessageDigestCalculator();
        final byte[] digest = messageDigestCalculator.computeDigest(folderName, "sig1NormTest");
        Assert.assertEquals("hzEV9SH32QzlXgG73tQeOQ==", base64Encoder.encode(digest));

    }
}