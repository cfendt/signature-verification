package org.thoughtworks.signatureverification.main;

/**
 * Project: Signature Verification
 * 
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

// This source code is release under the GNU General Public License v3

import org.thoughtworks.signatureverification.main.utils.Main;

public final class StartApp {
    public static void main(final String[] args) {
        final Main myMain = new Main();
        try {
            myMain.startApp();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
