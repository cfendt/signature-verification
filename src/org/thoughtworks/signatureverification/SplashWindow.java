/**
 * Project: Signature Verfication
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */
package org.thoughtworks.signatureverification;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Scanner;

@SuppressWarnings("serial")
public class SplashWindow extends JFrame {

    public SplashWindow() throws Exception {
        checkSettingsFile();
        setLNF();
    }

    private void checkSettingsFile() throws IOException {
        File dir = new File("config");
        if (!dir.exists()) {
            System.out.println("Settings Dir created");
            dir.mkdir();
        }
        File settings = new File("config\\settings.cfg");
        if (!settings.exists()) {
            System.out.println("Settings File created");
            FileOutputStream settingsFile = new FileOutputStream(".\\config\\settings.cfg");
            PrintWriter pw = new PrintWriter(settingsFile);
            pw.println(".86\n1.0");
            pw.close();
            settingsFile.close();
        }
    }

    public int start() throws Exception {
        return initComponents();
    }

    public int initComponents() throws Exception {
        splashProgressBar = new JProgressBar();
        loadingLabel = new JLabel();
        componentLabel = new JLabel();
        splashPanel = new JPanel();
        splashIcon = new ImageIcon("Images\\splash.jpg");
        splashLabel = new JLabel(splashIcon);
        setLayout(null);
        setSize(new Dimension(520, 350));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMaximizedBounds(new Rectangle(0, 0, 520, 350));
        setPreferredSize(new Dimension(520, 350));
        setUndecorated(true);
        Toolkit tk = getToolkit();
        Dimension sSize = tk.getScreenSize();
        setLocation(sSize.width / 4, sSize.height / 4);
        splashPanel.setLayout(null);
        splashPanel.setBorder(BorderFactory.createTitledBorder(""));
        splashPanel.add(splashLabel);
        splashLabel.setBounds(0, 0, 500, 300);
        add(splashPanel);
        splashPanel.setBounds(10, 5, 500, 300);
        add(splashProgressBar);
        splashProgressBar.setBounds(10, 305, 500, 15);
        loadingLabel.setText("Loading.......");
        add(loadingLabel);
        loadingLabel.setBounds(160, 325, 70, 14);
        add(componentLabel);
        componentLabel.setBounds(230, 325, 150, 14);
        setVisible(true);
        validateProject();
        return 0;
    }

    public void validateProject() throws InterruptedException {
        int progress = 1;
        HashMap<String, String> nameAndClass = new HashMap<String, String>() {
            {
                put("Signature Data", "org.thoughtworks.signatureverification.bean.SignatureData");
                put("DTW Module", "org.thoughtworks.signatureverification.dtw.DynamicTimeWarping");
                put("Regression Module", "org.thoughtworks.signatureverification.regression.ER2");
                put("Enrollment", "org.thoughtworks.signatureverification.Enroll");
                put("Access Layer", "org.thoughtworks.signatureverification.DataIO");
                put("Verification Module", "org.thoughtworks.signatureverification.Verification");
                put("Authentication", "org.thoughtworks.signatureverification.MessageDigestCalculator");
            }
        };
        for (String name : nameAndClass.keySet()) {
            try {
                Class aClass = Class.forName(nameAndClass.get(name));
                aClass.newInstance();
                progress += 10;
                splashProgressBar.setValue(progress);
                componentLabel.setText(name);
                Thread.sleep(100);
            } catch (InstantiationException e) {
                errorWhileLoading(name);
            } catch (IllegalAccessException e) {
                errorWhileLoading(name);
            } catch (ClassNotFoundException e) {
                errorWhileLoading(name);
            }
        }
        componentLabel.setText("Graphical UI ");
        Thread.sleep(100);
        splashProgressBar.setValue(100);
        dispose();
    }

    private void errorWhileLoading(String name) {
        JOptionPane.showMessageDialog(this, "Error Loading " + name + ". Terminating process.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public void setLNF() throws Exception {
        FileInputStream ip = new FileInputStream("config\\settings.cfg");
        Scanner sc = new Scanner(ip);
        double lnf;
        sc.nextDouble();
        lnf = sc.nextDouble();
        switch ((int) lnf) {
            case 2: {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                break;
            }
            default: {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        }
    }

    private JLabel componentLabel;
    private JPanel splashPanel;
    private JLabel loadingLabel;
    private JLabel splashLabel;
    private JProgressBar splashProgressBar;
    private ImageIcon splashIcon;
}
