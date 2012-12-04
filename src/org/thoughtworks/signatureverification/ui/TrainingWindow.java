/**
 * Project: Signature Verification
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.ui;

import org.thoughtworks.signatureverification.DataIO;
import org.thoughtworks.signatureverification.Enroll;
import org.thoughtworks.signatureverification.MessageDigestCalculator;
import static org.thoughtworks.signatureverification.SignatureVerificationConstants.*;
import org.thoughtworks.signatureverification.bean.SignatureData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class TrainingWindow extends JFrame {

    private Point curPoint;
    private Point prevPoint;
    private Boolean newClick = true;
    private String folderName = null;
    private boolean paintBool = false;
    private JButton clearButton;
    private JButton quitButton;
    private JButton acceptButton;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel usnLabel;
    private JPanel instructionPanel;
    private JPanel sigTopPanel;
    private JPanel sig1Panel;
    private JPanel informationPanel;
    private JPanel sig2Panel;
    private JScrollPane instructionPane;
    private JTextArea instructionTextArea;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usnField;
    private JButton sig1SaveButton;
    private JButton sig2SaveButton;
    private ImageIcon trainIcon;

    private LinkedList<Double> xr1 = new LinkedList<Double>();
    private LinkedList<Double> yr1 = new LinkedList<Double>();
    private LinkedList<Double> xr2 = new LinkedList<Double>();
    private LinkedList<Double> yr2 = new LinkedList<Double>();
    private boolean canCapture = false;
    private MessageDigestCalculator messageDigestCalculator;
    private Enroll enroll;

    public TrainingWindow() {
        curPoint = new Point();
        prevPoint = new Point();
        enroll = new Enroll();
        messageDigestCalculator = new MessageDigestCalculator();
        initComponents();
    }

    public void initComponents() {
        instructionPanel = new JPanel();
        instructionPane = new JScrollPane();
        instructionTextArea = new JTextArea();
        informationPanel = new JPanel();
        firstNameLabel = new JLabel();
        firstNameField = new JTextField();
        lastNameLabel = new JLabel();
        lastNameField = new JTextField();
        usnLabel = new JLabel();
        usnField = new JTextField();
        sigTopPanel = new JPanel();
        sig1Panel = new JPanel();
        sig2Panel = new JPanel();
        clearButton = new JButton();
        quitButton = new JButton();
        sig1SaveButton = new JButton();
        sig2SaveButton = new JButton();
        acceptButton = new JButton("Accept");
        trainIcon = new ImageIcon("Images\\48.gif");
        setLayout(null);
        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Training Module");
        setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
        setMaximizedBounds(new Rectangle(0, 0, 800, 590));
        setPreferredSize(new Dimension(800, 590));
        setIconImage(trainIcon.getImage());
        instructionPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        instructionPanel.setPreferredSize(new Dimension(600, 82));
        instructionPane.setAutoscrolls(false);
        instructionPane.setPreferredSize(new Dimension(580, 150));
        instructionTextArea.setColumns(20);
        instructionTextArea.setLineWrap(true);
        instructionTextArea.setRows(5);
        instructionTextArea.setText("Welcome to the training module. This module will help the software\nrecognize your signatures with a greater accuracy. Please enter\nyour personal details along with multiple copies of your signature,\nso that we can analyse the same and use it during verification.\nNote: Please sign in a sequential manner.\n\nThank you,\nAjay R, Keshav Kumar HK and Sachin Sudheendra");
        instructionTextArea.setDoubleBuffered(true);
        instructionTextArea.setPreferredSize(new Dimension(580, 150));
        instructionTextArea.setEditable(false);
        instructionTextArea.setToolTipText("General Instructions");
        instructionPane.setViewportView(instructionTextArea);
        instructionPanel.add(instructionPane);
        add(instructionPanel);
        instructionPanel.setBounds(39, 5, 620, 190);
        informationPanel.setLayout(new GridLayout(4, 2));
        informationPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        informationPanel.setPreferredSize(new Dimension(300, 115));
        firstNameLabel.setText("First Name");
        firstNameField.setToolTipText("Enter your first name");
        informationPanel.add(firstNameLabel);
        informationPanel.add(firstNameField);
        lastNameLabel.setText("Last Name");
        lastNameField.setToolTipText("Enter your last name");
        informationPanel.add(lastNameLabel);
        informationPanel.add(lastNameField);
        usnLabel.setText("USN");
        usnField.setToolTipText("Enter your USN number");
        informationPanel.add(usnLabel);
        informationPanel.add(usnField);
        informationPanel.add(new JLabel("Click to Accept"));
        acceptButton.setToolTipText("Click to accept input");
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
                clearButton.setEnabled(false);
                sig1SaveButton.setEnabled(true);
                canCapture = true;
            }

            public void checkUser() {
                File f = new File(folderName);
                if (f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "User Already Exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
            }
        });
        add(informationPanel);
        informationPanel.setBounds(390, 240, 250, 110);
        sigTopPanel.setBorder(BorderFactory.createTitledBorder("Sign Here"));
        sig1Panel.setBackground(new Color(255, 255, 255));
        sig1Panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sig1Panel.setPreferredSize(new Dimension(300, 100));
        sig1Panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point panelBound = sig1Panel.getLocationOnScreen();
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
                    xr1.add((double) curPoint.x);
                    yr1.add((double) curPoint.y);
                }
                paintBool = true;
                if (withinBounds(curPoint, sig1Panel))
                    repaint();
            }
        });
        sig1Panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point panelBound = sig1Panel.getLocationOnScreen();
                int px = panelBound.x;
                int py = panelBound.y;
                newClick = true;
                curPoint.x = px + e.getX();
                curPoint.y = py + e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                Point panelBound = sig1Panel.getLocationOnScreen();
                int px = panelBound.x;
                int py = panelBound.y;
                newClick = true;
                curPoint.x = px + e.getX();
                curPoint.y = py + e.getY();
            }
        });
        sigTopPanel.add(sig1Panel);
        sig2SaveButton.setEnabled(false);
        sig1SaveButton.setEnabled(false);
        sig1SaveButton.setText("Save Signature 1");
        sig1SaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SignatureData sigData1 = new SignatureData(xr1, yr1, 0);
                SignatureData sigData1Norm = enroll.enrollSignature(sigData1);
                try {
                    DataIO.writeData(sigData1, folderName, FIRST_TEMPLATE_SIGNATURE);
                    DataIO.writeData(sigData1Norm, folderName, FIRST_TEMPLATE_SIGNATURE_NORM);
                } catch (IOException e1) {
                }
                try {
                    byte[] dig = messageDigestCalculator.computeDigest(folderName, FIRST_TEMPLATE_SIGNATURE_NORM);
                    messageDigestCalculator.recordDigestToDisk(dig, folderName, FIRST_TEMPLATE_SIGNATURE_NORM_DIGEST);
                } catch (NoSuchAlgorithmException e1) {
                } catch (IOException e1) {
                }
                sig1SaveButton.setEnabled(false);
                sig2SaveButton.setEnabled(true);
            }
        });
        sigTopPanel.add(sig1SaveButton);
        sig2Panel.setBackground(new Color(255, 255, 255));
        sig2Panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sig2Panel.setPreferredSize(new Dimension(300, 100));
        sig2Panel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point panelBound = new Point(sig2Panel.getLocationOnScreen());
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
                    xr2.add((double) curPoint.x);
                    yr2.add((double) curPoint.y);
                }
                paintBool = true;
                if (withinBounds(curPoint, sig2Panel))
                    repaint();
            }
        });
        sig2Panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point panelBound = new Point(sig2Panel.getLocationOnScreen());
                int px = panelBound.x;
                int py = panelBound.y;
                newClick = true;
                curPoint.x = px + e.getX();
                curPoint.y = py + e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                Point panelBound = new Point(sig2Panel.getLocationOnScreen());
                int px = panelBound.x;
                int py = panelBound.y;
                newClick = true;
                curPoint.x = px + e.getX();
                curPoint.y = py + e.getY();
            }
        });
        sigTopPanel.add(sig2Panel);
        sig2SaveButton.setText("Save Signature 2");
        sig2SaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SignatureData sigData1 = new SignatureData(xr2, yr2, 0);
                SignatureData sigData1Norm = enroll.enrollSignature(sigData1);
                try {
                    DataIO.writeData(sigData1, folderName, SECOND_TEMPLATE_SIGNATURE);
                    DataIO.writeData(sigData1Norm, folderName, SECOND_TEMPLATE_SIGNATURE_NORM);
                    byte[] dig = messageDigestCalculator.computeDigest(folderName, SECOND_TEMPLATE_SIGNATURE_NORM);
                    messageDigestCalculator.recordDigestToDisk(dig, folderName, SECOND_TEMPLATE_SIGNATURE_NORM_DIGEST);
                } catch (Exception e1) {
                    e1.getMessage();
                }
                sig2SaveButton.setEnabled(false);
            }
        });
        sigTopPanel.add(sig2SaveButton);
        add(sigTopPanel);
        sigTopPanel.setBounds(40, 240, 330, 300);
        clearButton.setText("Clear");
        clearButton.setToolTipText("Clear the fields");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                firstNameField.setText("");
                lastNameField.setText("");
                usnField.setText("");
                acceptButton.setEnabled(true);
                sig1SaveButton.setEnabled(false);
                repaint();
            }
        });
        getContentPane().add(clearButton);
        clearButton.setBounds(390, 450, 117, 28);
        quitButton.setText("Quit");
        quitButton.setToolTipText("Click to quit to main window");
        quitButton.setToolTipText("This step is irreversible. Please confirm twice before pressing the quit button.");
        getContentPane().add(quitButton);
        quitButton.setBounds(520, 450, 120, 30);
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

    }

    public void paint(Graphics g) {
        if (paintBool) {
            g.drawLine(prevPoint.x - getX(), prevPoint.y - getY(), curPoint.x - getX(), curPoint.y - getY());
            paintBool = false;
        } else
            super.paint(g);
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