/**
 * Project: Signature Verification
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class MainWindow extends JFrame {

    private JMenuBar menuBar;
    private JMenuItem aboutMenuItem;
    private JMenuItem docMenuItem;
    private JMenuItem eulaMenuItem;
    private JMenuItem settingsMenuItem;
    private JMenuItem quitMenuItem;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JButton trainingGoButton;
    private JButton recogGoButton;
    private JLabel startRecogLabel;
    private JLabel startTrainingLabel;
    private ImageIcon mainIcon;
    private ImageIcon titleImage;
    private JLabel titleImageLabel;

    public MainWindow() throws Exception {
        setLNF();
        initComponents();
    }

    public void setLNF() throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException, FileNotFoundException {
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

    public void initComponents() {
        startTrainingLabel = new JLabel();
        trainingGoButton = new JButton();
        startRecogLabel = new JLabel();
        recogGoButton = new JButton();
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        settingsMenuItem = new JMenuItem();
        quitMenuItem = new JMenuItem();
        helpMenu = new JMenu();
        aboutMenuItem = new JMenuItem();
        docMenuItem = new JMenuItem();
        eulaMenuItem = new JMenuItem();
        titleImage = new ImageIcon("Images\\Title.jpg");
        titleImageLabel = new JLabel(titleImage);
        mainIcon = new ImageIcon("Images\\1.gif");
        setLayout(null);
        add(titleImageLabel);
        titleImageLabel.setBounds(0, 0, 400, 200);
        setMaximizedBounds(new Rectangle(0, 0, 400, 400));
        setPreferredSize(new Dimension(400, 400));
        setBounds(new Rectangle(0, 0, 400, 200));
        setSize(new Dimension(400, 400));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(400, 400));
        setTitle("Signature Verification");
        setIconImage(mainIcon.getImage());
        Toolkit tk = getToolkit();
        Dimension sSize = tk.getScreenSize();
        setLocation(sSize.width / 4, sSize.height / 4);
        startTrainingLabel.setText("Click here to start the Training Window");
        getContentPane().add(startTrainingLabel);
        startTrainingLabel.setBounds(20, 225, 306, 15);
        trainingGoButton.setText("Go");
        trainingGoButton.setToolTipText("Start Training Module");
        trainingGoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                trainingGoButtonActionPerformed();
            }
        });
        getContentPane().add(trainingGoButton);
        trainingGoButton.setBounds(300, 220, 50, 23);
        startRecogLabel.setText("Click here to start the Verification Window");
        getContentPane().add(startRecogLabel);
        startRecogLabel.setBounds(20, 265, 300, 15);
        recogGoButton.setText("Go");
        recogGoButton.setToolTipText("Start Verification Module");
        recogGoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                verificationGoButtonActionPerformed();
            }
        });
        getContentPane().add(recogGoButton);
        recogGoButton.setBounds(300, 260, 50, 23);
        fileMenu.setText("File");
        settingsMenuItem.setText("Settings");
        settingsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        new SettingsWindow().setVisible(true);
                    }
                });
            }
        });
        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quitMenuItemActionPerformed();
            }
        });
        fileMenu.add(settingsMenuItem);
        fileMenu.add(quitMenuItem);
        menuBar.add(fileMenu);
        helpMenu.setText("Help");
        docMenuItem.setText("Documentation");
        docMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg) {
                Runtime r = Runtime.getRuntime();
                File f = new File("doc\\index.html");
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could Not Invoke Documentation.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String path = f.getCanonicalPath();
                    String cmd[] = {"%System%/../../Program Files/Internet Explorer/iexplore.exe", path};
                    r.exec(cmd);
                }
                catch (IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could Not Invoke Documentation.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        eulaMenuItem.setText("Eula");
        eulaMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                Runtime r = Runtime.getRuntime();
                File f = new File("eula.html");
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could Not Invoke EULA.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String path = f.getCanonicalPath();
                    String cmd[] = {"%System%/../../Program Files/Internet Explorer/iexplore.exe", path};
                    r.exec(cmd);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Error displaying EULA.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        aboutMenuItem.setText("About Us");
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        aboutMenuItemActionPerformed();
                    }
                });
            }
        });
        helpMenu.add(docMenuItem);
        helpMenu.add(eulaMenuItem);
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        pack();
    }

    public void verificationGoButtonActionPerformed() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new VerificationWindow().setVisible(true);
                } catch (FileNotFoundException e) {
                }
            }
        });
    }

    public void trainingGoButtonActionPerformed() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TrainingWindow().setVisible(true);
            }
        });
    }


    public void aboutMenuItemActionPerformed() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AboutWindow().setVisible(true);
            }
        });
    }

    public void quitMenuItemActionPerformed() {
        System.exit(0);
    }

}
