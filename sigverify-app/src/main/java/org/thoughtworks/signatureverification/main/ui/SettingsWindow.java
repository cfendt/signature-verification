/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.main.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class SettingsWindow extends JFrame {

    /** Class serial number */
    private static final long serialVersionUID = 4132624048953737305L;

    private JButton commitButton;
    private JButton masterPasswordButton;
    private JPasswordField masterPasswordField;
    private JLabel masterPasswordLabel;
    private JLabel lNFLabel;
    private JRadioButton lNFJavaRadioButton;
    private JRadioButton lNFWinRadioButton;
    private ButtonGroup lNFButtonGroup;
    private JPanel settingsPanel;
    private JLabel thresholdIndicatorLabel;
    private JLabel thresholdLabel;
    private JSlider thresholdSlider;
    private JLabel deleteLabel;
    private JButton deleteButton;
    private JButton quitButton;
    private ImageIcon setIcon;

    private JFrame delFrame;
    private JButton deleteConfirmButton;
    private JTextField fnameField;
    private JLabel fnameLabel;
    private JPanel infoPanel;
    private JTextField lnameField;
    private JLabel lnameLabel;
    private JTextField usnField;
    private JLabel usnLabel;
    private double threshold = 0.90;
    private double lNF = 1.0;

    /**
     * Constructor
     */
    public SettingsWindow() {
        super();
        this.initComponents();
    }

    public void initComponents() {
        this.masterPasswordLabel = new JLabel();
        this.masterPasswordButton = new JButton();
        this.masterPasswordField = new JPasswordField();
        this.settingsPanel = new JPanel();
        this.thresholdIndicatorLabel = new JLabel();
        this.thresholdLabel = new JLabel();
        this.thresholdSlider = new JSlider();
        this.lNFLabel = new JLabel();
        this.lNFJavaRadioButton = new JRadioButton("Java");
        this.lNFWinRadioButton = new JRadioButton("Windows");
        this.lNFButtonGroup = new ButtonGroup();
        this.deleteLabel = new JLabel();
        this.deleteButton = new JButton();
        this.commitButton = new JButton();
        this.quitButton = new JButton();
        this.setIcon = new ImageIcon("Images\\173.gif");
        this.setLayout(null);
        this.setMaximizedBounds(new Rectangle(0, 0, 400, 400));
        this.setPreferredSize(new Dimension(400, 400));
        this.setBounds(new Rectangle(0, 0, 400, 200));
        this.setSize(new Dimension(400, 400));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(400, 400));
        this.setIconImage(this.setIcon.getImage());
        this.setTitle("Settings");
        final Toolkit tk = this.getToolkit();
        final Dimension sSize = tk.getScreenSize();
        this.setLocation(sSize.width / 4, sSize.height / 4);
        this.masterPasswordLabel.setText("Provide Master Password to modify the settings below");
        this.add(this.masterPasswordLabel);
        this.masterPasswordLabel.setBounds(20, 30, 320, 14);
        this.masterPasswordButton.setText("Sign In");
        this.add(this.masterPasswordButton);
        this.masterPasswordButton.setBounds(230, 60, 90, 25);
        this.masterPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                final String pass = new String(SettingsWindow.this.masterPasswordField.getPassword());
                if (pass.equals("root")) {
                    SettingsWindow.this.displaySettingsPanel(true);
                    SettingsWindow.this.masterPasswordField.setEnabled(false);
                    SettingsWindow.this.masterPasswordButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "Invalid Password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.add(this.masterPasswordField);
        this.masterPasswordField.setBounds(60, 60, 150, 23);
        this.settingsPanel.setLayout(null);
        this.settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        this.thresholdIndicatorLabel.setBorder(BorderFactory.createTitledBorder(""));
        this.thresholdIndicatorLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent evt) {
                SettingsWindow.this.thresholdIndicatorLabelComponentShown();
            }
        });
        this.settingsPanel.add(this.thresholdIndicatorLabel);
        this.thresholdIndicatorLabel.setBounds(270, 40, 40, 30);
        this.thresholdLabel.setText("Threshold Value for Verification");
        this.settingsPanel.add(this.thresholdLabel);
        this.thresholdLabel.setBounds(20, 20, 250, 14);
        this.thresholdSlider.setMinimum(80);
        this.thresholdSlider.setMinorTickSpacing(2);
        this.thresholdSlider.setPaintLabels(true);
        this.thresholdSlider.setPaintTicks(true);
        this.thresholdSlider.setSnapToTicks(true);
        this.thresholdSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent evt) {
                SettingsWindow.this.thresholdSliderStateChanged();
            }
        });
        this.settingsPanel.add(this.thresholdSlider);
        this.thresholdSlider.setBounds(20, 40, 220, 30);
        this.lNFLabel.setText("Select the Default Look and Feel");
        this.settingsPanel.add(this.lNFLabel);
        this.lNFLabel.setBounds(20, 80, 200, 14);
        this.lNFButtonGroup.add(this.lNFJavaRadioButton);
        this.lNFButtonGroup.add(this.lNFWinRadioButton);
        this.settingsPanel.add(this.lNFJavaRadioButton);
        this.lNFJavaRadioButton.setBounds(20, 100, 80, 33);
        this.settingsPanel.add(this.lNFWinRadioButton);
        this.lNFWinRadioButton.setBounds(100, 100, 100, 33);
        this.deleteLabel.setText("Delete User's Database");
        this.settingsPanel.add(this.deleteLabel);
        this.deleteLabel.setBounds(20, 135, 150, 40);
        this.deleteButton.setText("Delete");
        this.settingsPanel.add(this.deleteButton);
        this.deleteButton.setBounds(200, 147, 80, 23);
        this.deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                SettingsWindow.this.deleteButtonActionPerformed();
            }
        });
        this.commitButton.setText("Commit");
        this.settingsPanel.add(this.commitButton);
        this.commitButton.setBounds(140, 185, 80, 23);
        this.commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                FileOutputStream op = null;
                PrintWriter pw = null;
                try {
                    op = new FileOutputStream("config\\settings.cfg");
                    pw = new PrintWriter(op);
                } catch (final Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Error loading settings. Aborting.", "Error", JOptionPane.ERROR_MESSAGE);
                    SettingsWindow.this.dispose();
                }
                SettingsWindow.this.threshold = SettingsWindow.this.thresholdSlider.getValue();
                if (SettingsWindow.this.lNFJavaRadioButton.isSelected()) {
                    SettingsWindow.this.lNF = 1.0;
                } else {
                    SettingsWindow.this.lNF = 2.0;
                }
                assert pw != null;
                pw.print(SettingsWindow.this.threshold / 100 + "\n" + SettingsWindow.this.lNF);
                try {
                    pw.close();
                    op.close();
                } catch (final Exception e) {
                }
                SettingsWindow.this.displaySettingsPanel(false);
            }
        });
        this.add(this.settingsPanel);
        this.quitButton.setText("Quit");
        this.add(this.quitButton);
        this.quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                SettingsWindow.this.dispose();
            }
        });
        this.quitButton.setBounds(160, 330, 80, 30);
        this.settingsPanel.setBounds(20, 110, 360, 220);
        this.performBackend();
    }

    public void thresholdSliderStateChanged() {
        this.thresholdIndicatorLabel.setText(this.thresholdSlider.getValue() + "");
    }

    public void thresholdIndicatorLabelComponentShown() {
        this.thresholdIndicatorLabel.setText(this.thresholdSlider.getValue() + "");
    }

    public void deleteButtonActionPerformed() {
        this.setVisible(false);
        this.delFrame = new JFrame();
        this.infoPanel = new JPanel();
        this.fnameLabel = new JLabel();
        this.lnameLabel = new JLabel();
        this.usnLabel = new JLabel();
        this.fnameField = new JTextField();
        this.lnameField = new JTextField();
        this.usnField = new JTextField();
        this.deleteConfirmButton = new JButton();
        this.delFrame.setLayout(null);
        this.delFrame.setAlwaysOnTop(true);
        this.delFrame.setMaximizedBounds(new Rectangle(0, 0, 290, 200));
        this.delFrame.setPreferredSize(new Dimension(290, 200));
        this.delFrame.setBounds(new Rectangle(0, 0, 290, 200));
        this.delFrame.setSize(new Dimension(290, 200));
        this.delFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.delFrame.setResizable(true);
        this.delFrame.setMinimumSize(new Dimension(290, 200));
        this.delFrame.setTitle("Delete User");
        this.infoPanel.setLayout(null);
        this.infoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));
        this.fnameLabel.setText("First Name");
        this.infoPanel.add(this.fnameLabel);
        this.fnameLabel.setBounds(20, 20, 70, 14);
        this.lnameLabel.setText("Last Name");
        this.infoPanel.add(this.lnameLabel);
        this.lnameLabel.setBounds(20, 50, 70, 14);
        this.usnLabel.setText("USN");
        this.infoPanel.add(this.usnLabel);
        this.usnLabel.setBounds(20, 80, 50, 14);
        this.infoPanel.add(this.fnameField);
        this.fnameField.setBounds(100, 20, 130, 19);
        this.infoPanel.add(this.lnameField);
        this.lnameField.setBounds(100, 50, 130, 19);
        this.infoPanel.add(this.usnField);
        this.usnField.setBounds(100, 80, 130, 19);
        this.deleteConfirmButton.setText("Delete");
        this.deleteConfirmButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                if (SettingsWindow.this.fnameField.getText().length() == 0 || SettingsWindow.this.lnameField.getText().length() == 0 || SettingsWindow.this.usnField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "All fields are mandatory.\nPlease contact Developers for further help.", "Error", JOptionPane.ERROR_MESSAGE);
                    SettingsWindow.this.delFrame.dispose();
                    SettingsWindow.this.setVisible(true);
                    return;
                }
                final int option = JOptionPane.showConfirmDialog(new JFrame(), "Do you really want to delete the user?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.NO_OPTION) {
                    SettingsWindow.this.delFrame.dispose();
                    SettingsWindow.this.setVisible(true);
                    return;
                }
                if (option == JOptionPane.YES_OPTION) {
                    final String folderName = SettingsWindow.this.fnameField.getText() + SettingsWindow.this.lnameField.getText() + SettingsWindow.this.usnField.getText();
                    SettingsWindow.this.deleteUser(folderName);
                }
                SettingsWindow.this.setVisible(true);
            }
        });
        this.infoPanel.add(this.deleteConfirmButton);
        this.deleteConfirmButton.setBounds(90, 110, 73, 23);
        this.delFrame.add(this.infoPanel);
        this.infoPanel.setBounds(10, 0, 260, 150);
        this.delFrame.setVisible(true);
    }

    /**
     * @param folderName The user database to be deleted
     */
    public void deleteUser(String folderName) {
        folderName = folderName.toLowerCase();
        if (folderName.equalsIgnoreCase("classes") || folderName.equalsIgnoreCase("config") || folderName.equalsIgnoreCase("media") || folderName.equalsIgnoreCase("images")
                || folderName.equalsIgnoreCase("doc")) {
            JOptionPane.showMessageDialog(new JFrame(), "Cannot Delete.", "Error", JOptionPane.ERROR_MESSAGE);
            this.delFrame.dispose();
            return;
        }
        try {
            final File f1 = new File(folderName);
            if (!f1.exists() || !f1.isDirectory()) {
                JOptionPane.showMessageDialog(new JFrame(), "User database does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                this.delFrame.dispose();
            } else {
                final File[] files = f1.listFiles();
                for (final File file : files) {
                    file.delete();
                }
                f1.delete();
                this.delFrame.dispose();
            }
        } catch (final Exception e) {
        }
    }

    /**
     * @param value Display the Settings Panel based on the argument
     */
    public void displaySettingsPanel(final boolean value) {
        this.thresholdSlider.setEnabled(value);
        this.lNFJavaRadioButton.setEnabled(value);
        this.lNFWinRadioButton.setEnabled(value);
        this.commitButton.setEnabled(value);
        this.deleteButton.setEnabled(value);
    }

    public void performBackend() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(final WindowEvent arg0) {
                FileInputStream ip = null;
                Scanner sc = null;
                try {
                    ip = new FileInputStream("config\\settings.cfg");
                    sc = new Scanner(ip);
                } catch (final Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Cannot load settings.", "Error", JOptionPane.ERROR_MESSAGE);
                    SettingsWindow.this.dispose();
                }
                assert sc != null;
                SettingsWindow.this.threshold = sc.nextDouble();
                SettingsWindow.this.thresholdSlider.setValue((int) (SettingsWindow.this.threshold * 100));
                SettingsWindow.this.thresholdIndicatorLabel.setText((int) (SettingsWindow.this.threshold * 100) + "");
                SettingsWindow.this.lNF = sc.nextDouble();
                if (SettingsWindow.this.lNF == 1.0) {
                    SettingsWindow.this.lNFJavaRadioButton.setSelected(true);
                } else {
                    SettingsWindow.this.lNFWinRadioButton.setSelected(true);
                }
                try {
                    sc.close();
                    assert ip != null;
                    ip.close();
                } catch (final Exception e) {
                }
                SettingsWindow.this.displaySettingsPanel(false);
            }
        });
    }

}
