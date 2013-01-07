/**
 * Project: Signature Verfication
 * 
 * @author Programmers: Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */
package org.thoughtworks.signatureverification.main.utils;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class SplashWindow extends JFrame {

    /** Class serial number */
    private static final long serialVersionUID = -1922228253846716827L;

    public SplashWindow() throws Exception {
        this.checkSettingsFile();
        this.setLNF();
    }

    private void checkSettingsFile() throws IOException {
        final File dir = new File("config");
        if (!dir.exists()) {
            System.out.println("Settings Dir created");
            dir.mkdir();
        }
        final File settings = new File("config\\settings.cfg");
        if (!settings.exists()) {
            System.out.println("Settings File created");
            final FileOutputStream settingsFile = new FileOutputStream(".\\config\\settings.cfg");
            final PrintWriter pw = new PrintWriter(settingsFile);
            pw.println(".86\n1.0");
            pw.close();
            settingsFile.close();
        }
    }

    public int start() throws Exception {
        return this.initComponents();
    }

    public int initComponents() throws Exception {
        this.splashProgressBar = new JProgressBar();
        this.loadingLabel = new JLabel();
        this.componentLabel = new JLabel();
        this.splashPanel = new JPanel();
        this.splashIcon = new ImageIcon("Images\\splash.jpg");
        this.splashLabel = new JLabel(this.splashIcon);
        this.setLayout(null);
        this.setSize(new Dimension(520, 350));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setMaximizedBounds(new Rectangle(0, 0, 520, 350));
        this.setPreferredSize(new Dimension(520, 350));
        this.setUndecorated(true);
        final Toolkit tk = this.getToolkit();
        final Dimension sSize = tk.getScreenSize();
        this.setLocation(sSize.width / 4, sSize.height / 4);
        this.splashPanel.setLayout(null);
        this.splashPanel.setBorder(BorderFactory.createTitledBorder(""));
        this.splashPanel.add(this.splashLabel);
        this.splashLabel.setBounds(0, 0, 500, 300);
        this.add(this.splashPanel);
        this.splashPanel.setBounds(10, 5, 500, 300);
        this.add(this.splashProgressBar);
        this.splashProgressBar.setBounds(10, 305, 500, 15);
        this.loadingLabel.setText("Loading.......");
        this.add(this.loadingLabel);
        this.loadingLabel.setBounds(160, 325, 70, 14);
        this.add(this.componentLabel);
        this.componentLabel.setBounds(230, 325, 150, 14);
        this.setVisible(true);
        this.validateProject();
        return 0;
    }

    public void validateProject() throws InterruptedException {
        int progress = 1;
        final Map<String, String> nameAndClass = new HashMap<String, String>();
        nameAndClass.put("Signature Data", "org.thoughtworks.signatureverification.bean.SignatureData");
        nameAndClass.put("DTW Module", "org.thoughtworks.signatureverification.dtw.DynamicTimeWarping");
        nameAndClass.put("Regression Module", "org.thoughtworks.signatureverification.regression.ER2");
        nameAndClass.put("Enrollment", "org.thoughtworks.signatureverification.Enroll");
        nameAndClass.put("Access Layer", "org.thoughtworks.signatureverification.DataIO");
        nameAndClass.put("Verification Module", "org.thoughtworks.signatureverification.Verification");
        nameAndClass.put("Authentication", "org.thoughtworks.signatureverification.MessageDigestCalculator");

        for (final String name : nameAndClass.keySet()) {
            try {
                final Class<?> aClass = Class.forName(nameAndClass.get(name));
                aClass.newInstance();
                progress += 10;
                this.splashProgressBar.setValue(progress);
                this.componentLabel.setText(name);
                Thread.sleep(100);
            } catch (final InstantiationException e) {
                this.errorWhileLoading(name);
            } catch (final IllegalAccessException e) {
                this.errorWhileLoading(name);
            } catch (final ClassNotFoundException e) {
                this.errorWhileLoading(name);
            }
        }
        this.componentLabel.setText("Graphical UI ");
        Thread.sleep(100);
        this.splashProgressBar.setValue(100);
        this.dispose();
    }

    private void errorWhileLoading(final String name) {
        JOptionPane.showMessageDialog(this, "Error Loading " + name + ". Terminating process.", "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public void setLNF() throws Exception {
        final FileInputStream ip = new FileInputStream("config\\settings.cfg");
        final Scanner sc = new Scanner(ip);
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
