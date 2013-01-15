package org.thoughtworks.signatureverification;

public final class SignatureVerificationConstants {
    public static final String ENV_PROPERTIES = "settings.properties";
    public static final String FIRST_TEMPLATE_SIGNATURE = "sig1";
    public static final String FIRST_TEMPLATE_SIGNATURE_NORM = "sig1Norm";
    public static final String FIRST_TEMPLATE_SIGNATURE_NORM_DIGEST = "sig1NormDigest";
    public static final String SECOND_TEMPLATE_SIGNATURE = "sig2";
    public static final String SECOND_TEMPLATE_SIGNATURE_NORM = "sig2Norm";
    public static final String SECOND_TEMPLATE_SIGNATURE_NORM_DIGEST = "sig2NormDigest";
    public static final String TEST_TEMPLATE_SIGNATURE = "sigTest";
    public static final String TEST_TEMPLATE_SIGNATURE_NORM = "sigTestNorm";

    /**
     * Constructeur
     */
    private SignatureVerificationConstants() {
        super();
    }
}
