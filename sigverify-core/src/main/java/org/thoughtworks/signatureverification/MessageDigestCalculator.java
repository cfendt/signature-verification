/**
 * Project: Signature Verfication
 * 
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import sun.misc.BASE64Encoder;

public class MessageDigestCalculator {

    private final BASE64Encoder base64Encoder;

    public MessageDigestCalculator() {
        this.base64Encoder = new BASE64Encoder();
    }

    public byte[] computeDigest(final String folderName, final String fileName) throws NoSuchAlgorithmException, IOException {
        final InputStream ip = new FileInputStream(folderName + "\\" + fileName);
        MessageDigest messageDigest;
        DigestInputStream digestInputStream;
        messageDigest = MessageDigest.getInstance("MD5");
        digestInputStream = new DigestInputStream(ip, messageDigest);
        final byte[] buffer = new byte[8000];
        while (digestInputStream.read(buffer) != -1) {
            ;
        }
        ip.close();
        return messageDigest.digest();
    }

    public String fetchDigest(final String folderName, final String fileName) throws IOException {
        final FileInputStream ip = new FileInputStream(folderName + "\\" + fileName);
        final String digest = new Scanner(ip).next();
        ip.close();
        return digest;
    }

    public void recordDigestToDisk(final byte[] dig, final String folderName, final String firstTemplateSignatureNormDigest) {
        FileOutputStream op;
        try {
            op = new FileOutputStream(folderName + "\\" + firstTemplateSignatureNormDigest);
            final PrintWriter printWriter = new PrintWriter(op);
            printWriter.print(this.base64Encoder.encode(dig));
            printWriter.close();
            op.close();
        } catch (final IOException e) {

        }
    }
}
