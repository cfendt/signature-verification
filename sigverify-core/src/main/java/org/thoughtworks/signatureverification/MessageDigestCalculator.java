/**
 * Project: Signature Verfication
 * 
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

public final class MessageDigestCalculator {

    /**
     * Constructor
     */
    public MessageDigestCalculator() {
        super();
    }

    public byte[] computeDigest(final String folderName, final String fileName) throws NoSuchAlgorithmException, IOException {
        final InputStream ip = new FileInputStream(folderName + File.separatorChar + fileName);
        try {
            return this.computeDigest(ip);
        } finally {
            ip.close();
        }
    }

    public byte[] computeDigest(final InputStream input) throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest;
        DigestInputStream digestInputStream;
        messageDigest = MessageDigest.getInstance("MD5");
        digestInputStream = new DigestInputStream(input, messageDigest);
        final byte[] buffer = new byte[8000];
        while (digestInputStream.read(buffer) != -1) {
            ;
        }
        return messageDigest.digest();
    }

    public String fetchDigest(final String folderName, final String fileName) throws IOException {
        final FileInputStream ip = new FileInputStream(folderName + File.separatorChar + fileName);
        final String digest = new Scanner(ip).next();
        ip.close();
        return digest;
    }

    public void recordDigestToDisk(final byte[] dig, final String folderName, final String firstTemplateSignatureNormDigest) {
        FileOutputStream op;
        try {
            op = new FileOutputStream(folderName + File.separatorChar + firstTemplateSignatureNormDigest);
            final PrintWriter printWriter = new PrintWriter(op);
            printWriter.print(Base64.encodeBase64String(dig));
            printWriter.close();
            op.close();
        } catch (final IOException e) {

        }
    }
}
