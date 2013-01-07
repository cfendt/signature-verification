package org.thoughtworks.signatureverification;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class VerificationTest {
    String folderName = "sss";

    @Test
    public void shouldVerifyTwoDigests() throws Exception {
        final Verification verification = new Verification();
        verification.verifyDigest(this.folderName);

    }
}