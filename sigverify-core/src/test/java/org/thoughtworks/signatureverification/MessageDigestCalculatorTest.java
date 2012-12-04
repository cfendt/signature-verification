package org.thoughtworks.signatureverification;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;

public class MessageDigestCalculatorTest {

    @Test
    public void shouldComputeDigestForNormalizedSignatureData() throws Exception {
        String folderName = "test\\org\\thoughtworks\\signatureverification";
        BASE64Encoder base64Encoder = new BASE64Encoder();
        MessageDigestCalculator messageDigestCalculator = new MessageDigestCalculator();
        byte[] digest = messageDigestCalculator.computeDigest(folderName, "sig1NormTest");
        assertEquals("hzEV9SH32QzlXgG73tQeOQ==", base64Encoder.encode(digest));

    }
}