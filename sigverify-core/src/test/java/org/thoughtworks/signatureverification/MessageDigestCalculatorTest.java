package org.thoughtworks.signatureverification;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Charles
 */
public class MessageDigestCalculatorTest {

    /**
     * Test method
     * 
     * @throws IOException I/O error
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void shouldComputeDigestForNormalizedSignatureData() throws IOException, NoSuchAlgorithmException {
        final MessageDigestCalculator messageDigestCalculator = new MessageDigestCalculator();
        final byte[] digest = messageDigestCalculator.computeDigest(this.getClass().getResourceAsStream("/org/thoughtworks/signatureverification/sig1NormTest"));
        Assert.assertEquals("hzEV9SH32QzlXgG73tQeOQ==", Base64.encodeBase64String(digest));
    }
}