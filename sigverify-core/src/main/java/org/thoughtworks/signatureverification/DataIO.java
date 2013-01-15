/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.thoughtworks.signatureverification.bean.SignatureData;

public final class DataIO {

    public static SignatureData readData(final String folderName, final String fileName) throws IOException {
        final FileInputStream ip = new FileInputStream(folderName + File.separatorChar + fileName);
        try {
            return DataIO.readData(ip);
        } finally {
            ip.close();
        }
    }

    public static SignatureData readData(final InputStream input) throws IOException {
        final List<Double> xData = new LinkedList<Double>();
        final List<Double> yData = new LinkedList<Double>();
        final Scanner sc = new Scanner(input);
        sc.useLocale(Locale.US);
        try {
            while (sc.hasNext()) {
                xData.add(sc.nextDouble());
                yData.add(sc.nextDouble());
            }
        } finally {
            sc.close();
        }
        return new SignatureData(xData, yData, 0);
    }

    public static void writeData(final SignatureData signatureData, final String folderName, final String fileName) throws IOException {
        final List<Double> x = signatureData.getX();
        final List<Double> y = signatureData.getY();
        final File file = new File(folderName);
        if (!file.isDirectory()) {
            DataIO.createDir(folderName);
        }
        final FileOutputStream op = new FileOutputStream(folderName + File.separatorChar + fileName);
        final PrintWriter pw = new PrintWriter(op);
        for (int i = 0; i < x.size(); i++) {
            pw.println(x.get(i) + " " + y.get(i));
        }
        pw.close();
        op.close();
    }

    private static void createDir(final String folderName) {
        final File file = new File(folderName);
        if (!file.mkdir()) {
            throw new RuntimeException("Cannot Create Directory");
        }
    }
}
