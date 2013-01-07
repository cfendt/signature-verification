/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.main.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.thoughtworks.signatureverification.DataIO;
import org.thoughtworks.signatureverification.Enroll;
import org.thoughtworks.signatureverification.SignatureVerificationConstants;
import org.thoughtworks.signatureverification.Verification;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class VerificationWindow extends JFrame {

    /** Class serial number */
    private static final long serialVersionUID = -7060299436760778101L;

    private JButton acceptButton;
    private JButton verifyButton;
    private JLabel note1Label;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel usnLabel;
    private JLabel note2Label;
    private JPanel instructionPanel;
    private JPanel sigAreaPanel;
    private JPanel sigPanel;
    private JPanel informationPanel;
    private JScrollPane instructionScrollPane;
    private JTextArea instructionTextArea;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usnField;
    private JTextField resultPercentageField;
    private JTextField resultAnalysisField;
    private String folderName;
    private final LinkedList<Double> xt = new LinkedList<Double>();
    private final LinkedList<Double> yt = new LinkedList<Double>();
    private final Point curPoint;
    private final Point prevPoint;
    private boolean newClick = true;
    private double threshold = 0.90;
    private boolean paintBool = false;
    private ImageIcon recogIcon;

    private SignatureData signatureDataTest;
    private boolean canCapture = false;
    private Enroll enroll;

    public VerificationWindow() throws FileNotFoundException {
        this.curPoint = new Point();
        this.prevPoint = new Point();
        this.initComponents();
        this.getThreshold();
    }

    public void initComponents() {
        this.instructionPanel = new JPanel();
        this.instructionScrollPane = new JScrollPane();
        this.instructionTextArea = new JTextArea();
        this.sigAreaPanel = new JPanel();
        this.sigPanel = new JPanel();
        this.informationPanel = new JPanel();
        this.firstNameLabel = new JLabel();
        this.firstNameField = new JTextField();
        this.lastNameLabel = new JLabel();
        this.lastNameField = new JTextField();
        this.usnLabel = new JLabel();
        this.usnField = new JTextField();
        this.verifyButton = new JButton();
        this.note1Label = new JLabel();
        this.note2Label = new JLabel();
        this.acceptButton = new JButton();
        this.resultAnalysisField = new JTextField();
        this.resultPercentageField = new JTextField();
        this.recogIcon = new ImageIcon("Images\\45.gif");
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setMaximizedBounds(new Rectangle(0, 0, 700, 595));
        this.setPreferredSize(new Dimension(700, 595));
        this.setSize(new Dimension(700, 600));
        this.setIconImage(this.recogIcon.getImage());
        this.setTitle("Recognition Module");
        this.instructionPanel.setLayout(null);
        this.instructionPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        this.instructionTextArea.setColumns(20);
        this.instructionTextArea.setRows(5);
        this.instructionTextArea.setEditable(false);
        this.instructionTextArea
                .setText("Please use the below \"Signature Area\" to put your signature. \nAlso enter your personal information so that we can check if \nthe undersigned is indeed the original user.");
        this.instructionScrollPane.setViewportView(this.instructionTextArea);
        this.instructionPanel.add(this.instructionScrollPane);
        this.instructionScrollPane.setBounds(10, 20, 530, 120);
        this.add(this.instructionPanel);
        this.instructionPanel.setBounds(20, 20, 550, 150);
        this.sigAreaPanel.setLayout(null);
        this.sigAreaPanel.setBorder(BorderFactory.createTitledBorder("Signature Area"));
        this.sigPanel.setLayout(null);
        this.sigPanel.setBackground(new Color(255, 255, 255));
        this.sigPanel.setForeground(Color.BLACK);
        this.sigPanel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.sigAreaPanel.add(this.sigPanel);
        this.sigPanel.setBounds(10, 20, 270, 100);
        this.sigPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                final Point panelBound = new Point(VerificationWindow.this.sigPanel.getLocationOnScreen());
                final int px = panelBound.x;
                final int py = panelBound.y;
                if (VerificationWindow.this.newClick) {
                    VerificationWindow.this.prevPoint.x = VerificationWindow.this.curPoint.x = px + e.getX();
                    VerificationWindow.this.prevPoint.y = VerificationWindow.this.curPoint.y = py + e.getY();
                    VerificationWindow.this.newClick = false;
                } else {
                    VerificationWindow.this.prevPoint.x = VerificationWindow.this.curPoint.x;
                    VerificationWindow.this.prevPoint.y = VerificationWindow.this.curPoint.y;
                    VerificationWindow.this.curPoint.x = px + e.getX();
                    VerificationWindow.this.curPoint.y = py + e.getY();
                }
                if (VerificationWindow.this.canCapture) {
                    VerificationWindow.this.xt.add(new Double(VerificationWindow.this.curPoint.x));
                    VerificationWindow.this.yt.add(new Double(VerificationWindow.this.curPoint.y));
                }
                VerificationWindow.this.paintBool = true;
                if (VerificationWindow.this.withinBounds(VerificationWindow.this.curPoint, VerificationWindow.this.sigPanel)) {
                    VerificationWindow.this.repaint();
                }
            }
        });
        this.sigPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final Point panelBound = new Point(VerificationWindow.this.sigPanel.getLocationOnScreen());
                final int px = panelBound.x;
                final int py = panelBound.y;
                VerificationWindow.this.newClick = true;
                VerificationWindow.this.curPoint.x = px + e.getX();
                VerificationWindow.this.curPoint.y = py + e.getY();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                final Point panelBound = new Point(VerificationWindow.this.sigPanel.getLocationOnScreen());
                final int px = panelBound.x;
                final int py = panelBound.y;
                VerificationWindow.this.newClick = true;
                VerificationWindow.this.curPoint.x = px + e.getX();
                VerificationWindow.this.curPoint.y = py + e.getY();
            }
        });
        this.add(this.sigAreaPanel);
        this.sigAreaPanel.setBounds(20, 190, 290, 140);
        this.informationPanel.setLayout(new java.awt.GridLayout(4, 2));
        this.informationPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        this.informationPanel.setPreferredSize(new java.awt.Dimension(300, 115));
        this.firstNameLabel.setText("First Name");
        this.informationPanel.add(this.firstNameLabel);
        this.firstNameField.setToolTipText("Enter your first name");
        this.informationPanel.add(this.firstNameField);
        this.lastNameLabel.setText("Last Name");
        this.informationPanel.add(this.lastNameLabel);
        this.lastNameField.setToolTipText("Enter your last name");
        this.informationPanel.add(this.lastNameField);
        this.usnLabel.setText("USN");
        this.informationPanel.add(this.usnLabel);
        this.usnField.setToolTipText("Enter your USN number");
        this.informationPanel.add(this.usnField);
        this.informationPanel.add(new JLabel("Click to Accept"));
        this.acceptButton.setToolTipText("Click to Accept");
        this.acceptButton.setText("Accept");
        this.informationPanel.add(this.acceptButton);
        this.acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (VerificationWindow.this.firstNameField.getText().length() == 0 || VerificationWindow.this.lastNameField.getText().length() == 0
                        || VerificationWindow.this.usnField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "All fields are mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                VerificationWindow.this.folderName = VerificationWindow.this.firstNameField.getText() + VerificationWindow.this.lastNameField.getText() + VerificationWindow.this.usnField.getText();
                this.checkUser();
                VerificationWindow.this.acceptButton.setEnabled(false);
                VerificationWindow.this.verifyButton.setEnabled(true);
                VerificationWindow.this.canCapture = true;
            }

            public void checkUser() {
                final File f = new File(VerificationWindow.this.folderName);
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "User Database Does Not Exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    VerificationWindow.this.dispose();
                }
            }
        });
        this.add(this.informationPanel);
        this.informationPanel.setBounds(320, 190, 250, 110);
        this.verifyButton.setText("Verify");
        this.verifyButton.setToolTipText("Click to verify");
        this.verifyButton.setEnabled(false);
        this.verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                VerificationWindow.this.verifyButton.setEnabled(false);
                VerificationWindow.this.enroll = new Enroll();
                VerificationWindow.this.signatureDataTest = new SignatureData(VerificationWindow.this.xt, VerificationWindow.this.yt, 0);
                final SignatureData signatureDataTestNorm = VerificationWindow.this.enroll.enrollSignature(VerificationWindow.this.signatureDataTest);
                try {
                    DataIO.writeData(VerificationWindow.this.signatureDataTest, VerificationWindow.this.folderName, SignatureVerificationConstants.TEST_TEMPLATE_SIGNATURE);
                    DataIO.writeData(signatureDataTestNorm, VerificationWindow.this.folderName, SignatureVerificationConstants.TEST_TEMPLATE_SIGNATURE_NORM);
                } catch (final FileNotFoundException e1) {
                } catch (final IOException e1) {
                }
                final Verification verification = new Verification();
                double finalResult = 0.0;
                try {
                    finalResult = verification.verifySignature(signatureDataTestNorm, VerificationWindow.this.folderName);
                } catch (final Exception e1) {
                }
                this.displayResult(finalResult);
                // Todo should use MD5 data
            }

            private void displayResult(final double finalResult) {
                String resultAnalysis;
                VerificationWindow.this.resultPercentageField.setText(" " + finalResult * 100 + "%");
                if (finalResult >= VerificationWindow.this.threshold) {
                    resultAnalysis = "        Genuine";
                    VerificationWindow.this.resultAnalysisField.setForeground(Color.ORANGE);
                } else {
                    resultAnalysis = "    Fake / Inconsistent";
                    VerificationWindow.this.resultAnalysisField.setForeground(Color.RED);
                }
                VerificationWindow.this.resultAnalysisField.setText(resultAnalysis);
            }
        });
        this.add(this.verifyButton);
        this.verifyButton.setBounds(240, 380, 110, 40);
        this.note1Label.setText("Note: Please verify whether you have filled all the fields and put up your signature ONLY ONCE");
        this.add(this.note1Label);
        this.note1Label.setBounds(30, 340, 530, 14);
        this.note2Label.setText("on the panel, before pressing the Verify button. This step is irreversible. Take Caution.");
        this.add(this.note2Label);
        this.note2Label.setBounds(60, 360, 530, 14);
        this.add(this.resultPercentageField);
        this.resultPercentageField.setEditable(false);
        this.resultPercentageField.setFont(new Font("Arial", Font.BOLD, 20));
        this.resultPercentageField.setBounds(60, 430, 280, 60);
        this.add(this.resultAnalysisField);
        this.resultAnalysisField.setEditable(false);
        this.resultAnalysisField.setFont(new Font("Arial", Font.BOLD, 20));
        this.resultAnalysisField.setBounds(340, 430, 280, 60);
    }

    @Override
    public void paint(final Graphics g) {
        if (this.paintBool) {
            g.drawLine(this.prevPoint.x - this.getX(), this.prevPoint.y - this.getY(), this.curPoint.x - this.getX(), this.curPoint.y - this.getY());
            this.paintBool = false;
        } else {
            super.paint(g);
        }
    }

    public void getThreshold() throws FileNotFoundException {
        final FileInputStream ip = new FileInputStream("config\\settings.cfg");
        final Scanner sc = new Scanner(ip);
        this.threshold = sc.nextDouble();
    }

    public boolean withinBounds(final Point curPoint, final JPanel sigPanel) {
        final Point topLeft = sigPanel.getLocationOnScreen();
        final Point bottomRight = new Point(topLeft.x + sigPanel.getWidth(), topLeft.y + sigPanel.getHeight());
        return curPoint.x >= topLeft.x && curPoint.x <= bottomRight.x && curPoint.y >= topLeft.y && curPoint.y <= bottomRight.y;
    }
}