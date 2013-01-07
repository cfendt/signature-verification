package org.thoughtworks.signatureverification;

import org.junit.Assert;
import org.junit.Test;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class DataIOTest {
    String testInputFolder = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";

    @Test
    public void shouldReadDataFromDisk() throws Exception {
        final SignatureData signatureData = DataIO.readData(this.testInputFolder, "sig1NormTest");
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