package org.thoughtworks.signatureverification;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class DataIOTest {

    @Test
    public void shouldReadDataFromDisk() throws Exception {
        final InputStream ip = this.getClass().getResourceAsStream("/org/thoughtworks/signatureverification/sig1NormTest");
        final SignatureData signatureData = DataIO.readData(ip);
        Assert.assertEquals(139, signatureData.getX().size());
        Assert.assertEquals(139, signatureData.getY().size());
    }

    @Test
    public void shouldFailWhenInvalidFileIsGivenAsInputToReadData() throws Exception {
        try {
            DataIO.readData("", "INVALID_FILE");
            Assert.fail("Should Not Come Here");
        } catch (final Exception e) {
            Assert.assertNotNull(e);
        }

    }
}