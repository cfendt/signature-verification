/**
 * Project: Signature Verification
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

//This source code is release under the GNU General Public License v3

import org.thoughtworks.signatureverification.Main;

public class StartApp {
    public static void main(String[] args) {
        Main myMain = new Main();
        try {
            myMain.startApp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
