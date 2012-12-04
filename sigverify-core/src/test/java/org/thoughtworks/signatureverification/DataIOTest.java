package org.thoughtworks.signatureverification;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertNotNull;
import org.junit.Test;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class DataIOTest {
    String testInputFolder = "D:\\Projects\\signature-verification\\test\\org\\thoughtworks\\signatureverification";

    @Test
    public void shouldReadDataFromDisk() throws Exception {
        SignatureData signatureData = DataIO.readData(testInputFolder, "sig1NormTest");
        assertEquals(139, (signatureData.getX()).size());
        assertEquals(139, (signatureData.getY()).size());
    }

    @Test
    public void shouldFailWhenInvalidFileIsGivenAsInputToReadData() throws Exception {
        try {
            DataIO.readData("", "INVALID_FILE");
            fail("Should Not Come Here");
        }
        catch (Exception e) {
            assertNotNull(e);
        }

    }
}