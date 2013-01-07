/**
 * Project: Signature Verification
 * 
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.main.utils;

import org.thoughtworks.signatureverification.main.ui.MainWindow;

public class Main {

    public void startApp() throws Exception {
        if (new SplashWindow().start() == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        new MainWindow().setVisible(true);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Startup module of the software. Creates a thread to invoke MainFrame
     * 
     * @param args Future use - in debugging from the command line
     * @throws Exception Just leave it alone!
     */
    public static void main(final String[] args) throws Exception {
        if (new SplashWindow().start() == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        new MainWindow().setVisible(true);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
