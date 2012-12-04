package org.thoughtworks.signatureverification;

import org.junit.Test;

public class VerificationTest {
    String folderName= "sss";
    @Test
    public void shouldVerifyTwoDigests() throws Exception {
        Verification verification = new Verification();
        verification.verifyDigest(folderName);

    }

}