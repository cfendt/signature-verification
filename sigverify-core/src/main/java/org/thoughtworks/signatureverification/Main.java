/**
 * Project: Signature Verification
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification;

import org.thoughtworks.signatureverification.ui.MainWindow;

public class Main {

    public void startApp() throws Exception {
        if ((new SplashWindow().start()) == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new MainWindow().setVisible(true);
                    } catch (Exception e) {
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
    public static void main(String[] args) throws Exception {
        if ((new SplashWindow().start()) == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        new MainWindow().setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
