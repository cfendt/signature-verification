/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.main.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class MainWindow extends JFrame {

    /** Serial number of the class */
    private static final long serialVersionUID = 661331453151094674L;

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
        this.setLNF();
        this.initComponents();
    }

    public void setLNF() throws ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException, FileNotFoundException {
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

    public void initComponents() {
        this.startTrainingLabel = new JLabel();
        this.trainingGoButton = new JButton();
        this.startRecogLabel = new JLabel();
        this.recogGoButton = new JButton();
        this.menuBar = new JMenuBar();
        this.fileMenu = new JMenu();
        this.settingsMenuItem = new JMenuItem();
        this.quitMenuItem = new JMenuItem();
        this.helpMenu = new JMenu();
        this.aboutMenuItem = new JMenuItem();
        this.docMenuItem = new JMenuItem();
        this.eulaMenuItem = new JMenuItem();
        this.titleImage = new ImageIcon("Images\\Title.jpg");
        this.titleImageLabel = new JLabel(this.titleImage);
        this.mainIcon = new ImageIcon("Images\\1.gif");
        this.setLayout(null);
        this.add(this.titleImageLabel);
        this.titleImageLabel.setBounds(0, 0, 400, 200);
        this.setMaximizedBounds(new Rectangle(0, 0, 400, 400));
        this.setPreferredSize(new Dimension(400, 400));
        this.setBounds(new Rectangle(0, 0, 400, 200));
        this.setSize(new Dimension(400, 400));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(400, 400));
        this.setTitle("Signature Verification");
        this.setIconImage(this.mainIcon.getImage());
        final Toolkit tk = this.getToolkit();
        final Dimension sSize = tk.getScreenSize();
        this.setLocation(sSize.width / 4, sSize.height / 4);
        this.startTrainingLabel.setText("Click here to start the Training Window");
        this.getContentPane().add(this.startTrainingLabel);
        this.startTrainingLabel.setBounds(20, 225, 306, 15);
        this.trainingGoButton.setText("Go");
        this.trainingGoButton.setToolTipText("Start Training Module");
        this.trainingGoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                MainWindow.this.trainingGoButtonActionPerformed();
            }
        });
        this.getContentPane().add(this.trainingGoButton);
        this.trainingGoButton.setBounds(300, 220, 50, 23);
        this.startRecogLabel.setText("Click here to start the Verification Window");
        this.getContentPane().add(this.startRecogLabel);
        this.startRecogLabel.setBounds(20, 265, 300, 15);
        this.recogGoButton.setText("Go");
        this.recogGoButton.setToolTipText("Start Verification Module");
        this.recogGoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                MainWindow.this.verificationGoButtonActionPerformed();
            }
        });
        this.getContentPane().add(this.recogGoButton);
        this.recogGoButton.setBounds(300, 260, 50, 23);
        this.fileMenu.setText("File");
        this.settingsMenuItem.setText("Settings");
        this.settingsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SettingsWindow().setVisible(true);
                    }
                });
            }
        });
        this.quitMenuItem.setText("Quit");
        this.quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                MainWindow.this.quitMenuItemActionPerformed();
            }
        });
        this.fileMenu.add(this.settingsMenuItem);
        this.fileMenu.add(this.quitMenuItem);
        this.menuBar.add(this.fileMenu);
        this.helpMenu.setText("Help");
        this.docMenuItem.setText("Documentation");
        this.docMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg) {
                final Runtime r = Runtime.getRuntime();
                final File f = new File("doc\\index.html");
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could Not Invoke Documentation.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    final String path = f.getCanonicalPath();
                    final String cmd[] = { "%System%/../../Program Files/Internet Explorer/iexplore.exe", path };
                    r.exec(cmd);
                } catch (final IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could Not Invoke Documentation.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.eulaMenuItem.setText("Eula");
        this.eulaMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final Runtime r = Runtime.getRuntime();
                final File f = new File("eula.html");
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could Not Invoke EULA.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    final String path = f.getCanonicalPath();
                    final String cmd[] = { "%System%/../../Program Files/Internet Explorer/iexplore.exe", path };
                    r.exec(cmd);
                } catch (final IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Error displaying EULA.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.aboutMenuItem.setText("About Us");
        this.aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainWindow.this.aboutMenuItemActionPerformed();
                    }
                });
            }
        });
        this.helpMenu.add(this.docMenuItem);
        this.helpMenu.add(this.eulaMenuItem);
        this.helpMenu.add(this.aboutMenuItem);
        this.menuBar.add(this.helpMenu);
        this.setJMenuBar(this.menuBar);
        this.pack();
    }

    public void verificationGoButtonActionPerformed() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new VerificationWindow().setVisible(true);
                } catch (final FileNotFoundException e) {
                }
            }
        });
    }

    public void trainingGoButtonActionPerformed() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrainingWindow().setVisible(true);
            }
        });
    }

    public void aboutMenuItemActionPerformed() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AboutWindow().setVisible(true);
            }
        });
    }

    public void quitMenuItemActionPerformed() {
        System.exit(0);
    }

}
