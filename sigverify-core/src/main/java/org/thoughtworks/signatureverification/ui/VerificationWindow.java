/**
 * Project: Signature Verification
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.ui;


import org.thoughtworks.signatureverification.*;
import static org.thoughtworks.signatureverification.SignatureVerificationConstants.*;
import org.thoughtworks.signatureverification.bean.SignatureData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class VerificationWindow extends JFrame {
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
    private LinkedList<Double> xt = new LinkedList<Double>();
    private LinkedList<Double> yt = new LinkedList<Double>();
    private Point curPoint;
    private Point prevPoint;
    private boolean newClick = true;
    private double threshold = 0.90;
    private boolean paintBool = false;
    private MessageDigestCalculator messageDigestCalculator;
    private ImageIcon recogIcon;

    private SignatureData signatureDataTest;
    private boolean canCapture = false;
    private Enroll enroll;

    public VerificationWindow() throws FileNotFoundException {
        curPoint = new Point();
        prevPoint = new Point();
        messageDigestCalculator = new MessageDigestCalculator();
        initComponents();
        getThreshold();
    }

    public void initComponents() {
        instructionPanel = new JPanel();
        instructionScrollPane = new JScrollPane();
        instructionTextArea = new JTextArea();
        sigAreaPanel = new JPanel();
        sigPanel = new JPanel();
        informationPanel = new JPanel();
        firstNameLabel = new JLabel();
        firstNameField = new JTextField();
        lastNameLabel = new JLabel();
        lastNameField = new JTextField();
        usnLabel = new JLabel();
        usnField = new JTextField();
        verifyButton = new JButton();
        note1Label = new JLabel();
        note2Label = new JLabel();
        acceptButton = new JButton();
        resultAnalysisField = new JTextField();
        resultPercentageField = new JTextField();
        recogIcon = new ImageIcon("Images\\45.gif");
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMaximizedBounds(new Rectangle(0, 0, 700, 595));
        setPreferredSize(new Dimension(700, 595));
        setSize(new Dimension(700, 600));
        setIconImage(recogIcon.getImage());
        setTitle("Recognition Module");
        instructionPanel.setLayout(null);
        instructionPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        instructionTextArea.setColumns(20);
        instructionTextArea.setRows(5);
        instructionTextArea.setEditable(false);
        instructionTextArea.setText("Please use the below \"Signature Area\" to put your signature. \nAlso enter your personal information so that we can check if \nthe undersigned is indeed the original user.");
        instructionScrollPane.setViewportView(instructionTextArea);
        instructionPanel.add(instructionScrollPane);
        instructionScrollPane.setBounds(10, 20, 530, 120);
        add(instructionPanel);
        instructionPanel.setBounds(20, 20, 550, 150);
        sigAreaPanel.setLayout(null);
        sigAreaPanel.setBorder(BorderFactory.createTitledBorder("Signature Area"));
        sigPanel.setLayout(null);
        sigPanel.setBackground(new Color(255, 255, 255));
        sigPanel.setForeground(Color.BLACK);
        sigPanel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sigAreaPanel.add(sigPanel);
        sigPanel.setBounds(10, 20, 270, 100);
        sigPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point panelBound = new Point(sigPanel.getLocationOnScreen());
                int px = panelBound.x;
                int py = panelBound.y;
                if (newClick) {
                    prevPoint.x = curPoint.x = px + e.getX();
                    prevPoint.y = curPoint.y = py + e.getY();
                    newClick = false;
                } else {
                    prevPoint.x = curPoint.x;
                    prevPoint.y = curPoint.y;
                    curPoint.x = px + e.getX();
                    curPoint.y = py + e.getY();
                }
                if (canCapture) {
                    xt.add(new Double(curPoint.x));
                    yt.add(new Double(curPoint.y));
                }
                paintBool = true;
                if (withinBounds(curPoint, sigPanel))
                    repaint();
            }
        });
        sigPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point panelBound = new Point(sigPanel.getLocationOnScreen());
                int px = panelBound.x;
                int py = panelBound.y;
                newClick = true;
                curPoint.x = px + e.getX();
                curPoint.y = py + e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                Point panelBound = new Point(sigPanel.getLocationOnScreen());
                int px = panelBound.x;
                int py = panelBound.y;
                newClick = true;
                curPoint.x = px + e.getX();
                curPoint.y = py + e.getY();
            }
        });
        add(sigAreaPanel);
        sigAreaPanel.setBounds(20, 190, 290, 140);
        informationPanel.setLayout(new java.awt.GridLayout(4, 2));
        informationPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        informationPanel.setPreferredSize(new java.awt.Dimension(300, 115));
        firstNameLabel.setText("First Name");
        informationPanel.add(firstNameLabel);
        firstNameField.setToolTipText("Enter your first name");
        informationPanel.add(firstNameField);
        lastNameLabel.setText("Last Name");
        informationPanel.add(lastNameLabel);
        lastNameField.setToolTipText("Enter your last name");
        informationPanel.add(lastNameField);
        usnLabel.setText("USN");
        informationPanel.add(usnLabel);
        usnField.setToolTipText("Enter your USN number");
        informationPanel.add(usnField);
        informationPanel.add(new JLabel("Click to Accept"));
        acceptButton.setToolTipText("Click to Accept");
        acceptButton.setText("Accept");
        informationPanel.add(acceptButton);
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (firstNameField.getText().length() == 0 || lastNameField.getText().length() == 0 || usnField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "All fields are mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                folderName = firstNameField.getText() + lastNameField.getText() + usnField.getText();
                checkUser();
                acceptButton.setEnabled(false);
                verifyButton.setEnabled(true);
                canCapture = true;
            }

            public void checkUser() {
                File f = new File(folderName);
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "User Database Does Not Exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
            }
        });
        add(informationPanel);
        informationPanel.setBounds(320, 190, 250, 110);
        verifyButton.setText("Verify");
        verifyButton.setToolTipText("Click to verify");
        verifyButton.setEnabled(false);
        verifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verifyButton.setEnabled(false);
                enroll = new Enroll();
                signatureDataTest = new SignatureData(xt, yt, 0);
                SignatureData signatureDataTestNorm = enroll.enrollSignature(signatureDataTest);
                try {
                    DataIO.writeData(signatureDataTest, folderName, TEST_TEMPLATE_SIGNATURE);
                    DataIO.writeData(signatureDataTestNorm, folderName, TEST_TEMPLATE_SIGNATURE_NORM);
                } catch (FileNotFoundException e1) {
                } catch (IOException e1) {
                }
                Verification verification = new Verification();
                double finalResult = 0.0;
                try {
                    finalResult = verification.verifySignature(signatureDataTestNorm, folderName);
                } catch (Exception e1) {
                }
                displayResult(finalResult);
                //Todo should use MD5 data
            }

            private void displayResult(double finalResult) {
                String resultAnalysis;
                resultPercentageField.setText(" " + (finalResult * 100) + "%");
                if (finalResult >= threshold) {
                    resultAnalysis = "        Genuine";
                    resultAnalysisField.setForeground(Color.ORANGE);
                } else {
                    resultAnalysis = "    Fake / Inconsistent";
                    resultAnalysisField.setForeground(Color.RED);
                }
                resultAnalysisField.setText(resultAnalysis);
            }
        });
        add(verifyButton);
        verifyButton.setBounds(240, 380, 110, 40);
        note1Label.setText("Note: Please verify whether you have filled all the fields and put up your signature ONLY ONCE");
        add(note1Label);
        note1Label.setBounds(30, 340, 530, 14);
        note2Label.setText("on the panel, before pressing the Verify button. This step is irreversible. Take Caution.");
        add(note2Label);
        note2Label.setBounds(60, 360, 530, 14);
        add(resultPercentageField);
        resultPercentageField.setEditable(false);
        resultPercentageField.setFont(new Font("Arial", Font.BOLD, 20));
        resultPercentageField.setBounds(60, 430, 280, 60);
        add(resultAnalysisField);
        resultAnalysisField.setEditable(false);
        resultAnalysisField.setFont(new Font("Arial", Font.BOLD, 20));
        resultAnalysisField.setBounds(340, 430, 280, 60);
    }

    public void paint(Graphics g) {
        if (paintBool) {
            g.drawLine(prevPoint.x - getX(), prevPoint.y - getY(), curPoint.x - getX(), curPoint.y - getY());
            paintBool = false;
        } else
            super.paint(g);
    }

    public void getThreshold() throws FileNotFoundException {
        FileInputStream ip = new FileInputStream("config\\settings.cfg");
        Scanner sc = new Scanner(ip);
        threshold = sc.nextDouble();
    }

    public boolean withinBounds(Point curPoint, JPanel sigPanel) {
        Point topLeft = sigPanel.getLocationOnScreen();
        Point bottomRight = new Point((topLeft.x + sigPanel.getWidth()), (topLeft.y + sigPanel.getHeight()));
        return curPoint.x >= topLeft.x
                && curPoint.x <= bottomRight.x
                && curPoint.y >= topLeft.y
                && curPoint.y <= bottomRight.y;
    }
}