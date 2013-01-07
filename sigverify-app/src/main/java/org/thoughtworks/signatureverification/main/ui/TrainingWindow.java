/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.main.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

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
import org.thoughtworks.signatureverification.MessageDigestCalculator;
import org.thoughtworks.signatureverification.SignatureVerificationConstants;
import org.thoughtworks.signatureverification.bean.SignatureData;

public class TrainingWindow extends JFrame {

    /** Class serial number */
    private static final long serialVersionUID = 2304013309153514467L;

    private final Point curPoint;
    private final Point prevPoint;
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

    private final LinkedList<Double> xr1 = new LinkedList<Double>();
    private final LinkedList<Double> yr1 = new LinkedList<Double>();
    private final LinkedList<Double> xr2 = new LinkedList<Double>();
    private final LinkedList<Double> yr2 = new LinkedList<Double>();
    private boolean canCapture = false;
    private final MessageDigestCalculator messageDigestCalculator;
    private final Enroll enroll;

    public TrainingWindow() {
        this.curPoint = new Point();
        this.prevPoint = new Point();
        this.enroll = new Enroll();
        this.messageDigestCalculator = new MessageDigestCalculator();
        this.initComponents();
    }

    public void initComponents() {
        this.instructionPanel = new JPanel();
        this.instructionPane = new JScrollPane();
        this.instructionTextArea = new JTextArea();
        this.informationPanel = new JPanel();
        this.firstNameLabel = new JLabel();
        this.firstNameField = new JTextField();
        this.lastNameLabel = new JLabel();
        this.lastNameField = new JTextField();
        this.usnLabel = new JLabel();
        this.usnField = new JTextField();
        this.sigTopPanel = new JPanel();
        this.sig1Panel = new JPanel();
        this.sig2Panel = new JPanel();
        this.clearButton = new JButton();
        this.quitButton = new JButton();
        this.sig1SaveButton = new JButton();
        this.sig2SaveButton = new JButton();
        this.acceptButton = new JButton("Accept");
        this.trainIcon = new ImageIcon("Images\\48.gif");
        this.setLayout(null);
        this.setSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("Training Module");
        this.setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
        this.setMaximizedBounds(new Rectangle(0, 0, 800, 590));
        this.setPreferredSize(new Dimension(800, 590));
        this.setIconImage(this.trainIcon.getImage());
        this.instructionPanel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        this.instructionPanel.setPreferredSize(new Dimension(600, 82));
        this.instructionPane.setAutoscrolls(false);
        this.instructionPane.setPreferredSize(new Dimension(580, 150));
        this.instructionTextArea.setColumns(20);
        this.instructionTextArea.setLineWrap(true);
        this.instructionTextArea.setRows(5);
        this.instructionTextArea
                .setText("Welcome to the training module. This module will help the software\nrecognize your signatures with a greater accuracy. Please enter\nyour personal details along with multiple copies of your signature,\nso that we can analyse the same and use it during verification.\nNote: Please sign in a sequential manner.\n\nThank you,\nAjay R, Keshav Kumar HK and Sachin Sudheendra");
        this.instructionTextArea.setDoubleBuffered(true);
        this.instructionTextArea.setPreferredSize(new Dimension(580, 150));
        this.instructionTextArea.setEditable(false);
        this.instructionTextArea.setToolTipText("General Instructions");
        this.instructionPane.setViewportView(this.instructionTextArea);
        this.instructionPanel.add(this.instructionPane);
        this.add(this.instructionPanel);
        this.instructionPanel.setBounds(39, 5, 620, 190);
        this.informationPanel.setLayout(new GridLayout(4, 2));
        this.informationPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        this.informationPanel.setPreferredSize(new Dimension(300, 115));
        this.firstNameLabel.setText("First Name");
        this.firstNameField.setToolTipText("Enter your first name");
        this.informationPanel.add(this.firstNameLabel);
        this.informationPanel.add(this.firstNameField);
        this.lastNameLabel.setText("Last Name");
        this.lastNameField.setToolTipText("Enter your last name");
        this.informationPanel.add(this.lastNameLabel);
        this.informationPanel.add(this.lastNameField);
        this.usnLabel.setText("USN");
        this.usnField.setToolTipText("Enter your USN number");
        this.informationPanel.add(this.usnLabel);
        this.informationPanel.add(this.usnField);
        this.informationPanel.add(new JLabel("Click to Accept"));
        this.acceptButton.setToolTipText("Click to accept input");
        this.informationPanel.add(this.acceptButton);
        this.acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (TrainingWindow.this.firstNameField.getText().length() == 0 || TrainingWindow.this.lastNameField.getText().length() == 0 || TrainingWindow.this.usnField.getText().length() == 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "All fields are mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                TrainingWindow.this.folderName = TrainingWindow.this.firstNameField.getText() + TrainingWindow.this.lastNameField.getText() + TrainingWindow.this.usnField.getText();
                this.checkUser();
                TrainingWindow.this.acceptButton.setEnabled(false);
                TrainingWindow.this.clearButton.setEnabled(false);
                TrainingWindow.this.sig1SaveButton.setEnabled(true);
                TrainingWindow.this.canCapture = true;
            }

            public void checkUser() {
                final File f = new File(TrainingWindow.this.folderName);
                if (f.exists()) {
                    JOptionPane.showMessageDialog(new JFrame(), "User Already Exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    TrainingWindow.this.dispose();
                }
            }
        });
        this.add(this.informationPanel);
        this.informationPanel.setBounds(390, 240, 250, 110);
        this.sigTopPanel.setBorder(BorderFactory.createTitledBorder("Sign Here"));
        this.sig1Panel.setBackground(new Color(255, 255, 255));
        this.sig1Panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.sig1Panel.setPreferredSize(new Dimension(300, 100));
        this.sig1Panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                final Point panelBound = TrainingWindow.this.sig1Panel.getLocationOnScreen();
                final int px = panelBound.x;
                final int py = panelBound.y;
                if (TrainingWindow.this.newClick) {
                    TrainingWindow.this.prevPoint.x = TrainingWindow.this.curPoint.x = px + e.getX();
                    TrainingWindow.this.prevPoint.y = TrainingWindow.this.curPoint.y = py + e.getY();
                    TrainingWindow.this.newClick = false;
                } else {
                    TrainingWindow.this.prevPoint.x = TrainingWindow.this.curPoint.x;
                    TrainingWindow.this.prevPoint.y = TrainingWindow.this.curPoint.y;
                    TrainingWindow.this.curPoint.x = px + e.getX();
                    TrainingWindow.this.curPoint.y = py + e.getY();
                }
                if (TrainingWindow.this.canCapture) {
                    TrainingWindow.this.xr1.add((double) TrainingWindow.this.curPoint.x);
                    TrainingWindow.this.yr1.add((double) TrainingWindow.this.curPoint.y);
                }
                TrainingWindow.this.paintBool = true;
                if (TrainingWindow.this.withinBounds(TrainingWindow.this.curPoint, TrainingWindow.this.sig1Panel)) {
                    TrainingWindow.this.repaint();
                }
            }
        });
        this.sig1Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final Point panelBound = TrainingWindow.this.sig1Panel.getLocationOnScreen();
                final int px = panelBound.x;
                final int py = panelBound.y;
                TrainingWindow.this.newClick = true;
                TrainingWindow.this.curPoint.x = px + e.getX();
                TrainingWindow.this.curPoint.y = py + e.getY();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                final Point panelBound = TrainingWindow.this.sig1Panel.getLocationOnScreen();
                final int px = panelBound.x;
                final int py = panelBound.y;
                TrainingWindow.this.newClick = true;
                TrainingWindow.this.curPoint.x = px + e.getX();
                TrainingWindow.this.curPoint.y = py + e.getY();
            }
        });
        this.sigTopPanel.add(this.sig1Panel);
        this.sig2SaveButton.setEnabled(false);
        this.sig1SaveButton.setEnabled(false);
        this.sig1SaveButton.setText("Save Signature 1");
        this.sig1SaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final SignatureData sigData1 = new SignatureData(TrainingWindow.this.xr1, TrainingWindow.this.yr1, 0);
                final SignatureData sigData1Norm = TrainingWindow.this.enroll.enrollSignature(sigData1);
                try {
                    DataIO.writeData(sigData1, TrainingWindow.this.folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE);
                    DataIO.writeData(sigData1Norm, TrainingWindow.this.folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE_NORM);
                } catch (final IOException e1) {
                }
                try {
                    final byte[] dig = TrainingWindow.this.messageDigestCalculator.computeDigest(TrainingWindow.this.folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE_NORM);
                    TrainingWindow.this.messageDigestCalculator.recordDigestToDisk(dig, TrainingWindow.this.folderName, SignatureVerificationConstants.FIRST_TEMPLATE_SIGNATURE_NORM_DIGEST);
                } catch (final NoSuchAlgorithmException e1) {
                } catch (final IOException e1) {
                }
                TrainingWindow.this.sig1SaveButton.setEnabled(false);
                TrainingWindow.this.sig2SaveButton.setEnabled(true);
            }
        });
        this.sigTopPanel.add(this.sig1SaveButton);
        this.sig2Panel.setBackground(new Color(255, 255, 255));
        this.sig2Panel.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.sig2Panel.setPreferredSize(new Dimension(300, 100));
        this.sig2Panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                final Point panelBound = new Point(TrainingWindow.this.sig2Panel.getLocationOnScreen());
                final int px = panelBound.x;
                final int py = panelBound.y;
                if (TrainingWindow.this.newClick) {
                    TrainingWindow.this.prevPoint.x = TrainingWindow.this.curPoint.x = px + e.getX();
                    TrainingWindow.this.prevPoint.y = TrainingWindow.this.curPoint.y = py + e.getY();
                    TrainingWindow.this.newClick = false;
                } else {
                    TrainingWindow.this.prevPoint.x = TrainingWindow.this.curPoint.x;
                    TrainingWindow.this.prevPoint.y = TrainingWindow.this.curPoint.y;
                    TrainingWindow.this.curPoint.x = px + e.getX();
                    TrainingWindow.this.curPoint.y = py + e.getY();
                }
                if (TrainingWindow.this.canCapture) {
                    TrainingWindow.this.xr2.add((double) TrainingWindow.this.curPoint.x);
                    TrainingWindow.this.yr2.add((double) TrainingWindow.this.curPoint.y);
                }
                TrainingWindow.this.paintBool = true;
                if (TrainingWindow.this.withinBounds(TrainingWindow.this.curPoint, TrainingWindow.this.sig2Panel)) {
                    TrainingWindow.this.repaint();
                }
            }
        });
        this.sig2Panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final Point panelBound = new Point(TrainingWindow.this.sig2Panel.getLocationOnScreen());
                final int px = panelBound.x;
                final int py = panelBound.y;
                TrainingWindow.this.newClick = true;
                TrainingWindow.this.curPoint.x = px + e.getX();
                TrainingWindow.this.curPoint.y = py + e.getY();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                final Point panelBound = new Point(TrainingWindow.this.sig2Panel.getLocationOnScreen());
                final int px = panelBound.x;
                final int py = panelBound.y;
                TrainingWindow.this.newClick = true;
                TrainingWindow.this.curPoint.x = px + e.getX();
                TrainingWindow.this.curPoint.y = py + e.getY();
            }
        });
        this.sigTopPanel.add(this.sig2Panel);
        this.sig2SaveButton.setText("Save Signature 2");
        this.sig2SaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final SignatureData sigData1 = new SignatureData(TrainingWindow.this.xr2, TrainingWindow.this.yr2, 0);
                final SignatureData sigData1Norm = TrainingWindow.this.enroll.enrollSignature(sigData1);
                try {
                    DataIO.writeData(sigData1, TrainingWindow.this.folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE);
                    DataIO.writeData(sigData1Norm, TrainingWindow.this.folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE_NORM);
                    final byte[] dig = TrainingWindow.this.messageDigestCalculator.computeDigest(TrainingWindow.this.folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE_NORM);
                    TrainingWindow.this.messageDigestCalculator.recordDigestToDisk(dig, TrainingWindow.this.folderName, SignatureVerificationConstants.SECOND_TEMPLATE_SIGNATURE_NORM_DIGEST);
                } catch (final Exception e1) {
                    e1.getMessage();
                }
                TrainingWindow.this.sig2SaveButton.setEnabled(false);
            }
        });
        this.sigTopPanel.add(this.sig2SaveButton);
        this.add(this.sigTopPanel);
        this.sigTopPanel.setBounds(40, 240, 330, 300);
        this.clearButton.setText("Clear");
        this.clearButton.setToolTipText("Clear the fields");
        this.clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                TrainingWindow.this.firstNameField.setText("");
                TrainingWindow.this.lastNameField.setText("");
                TrainingWindow.this.usnField.setText("");
                TrainingWindow.this.acceptButton.setEnabled(true);
                TrainingWindow.this.sig1SaveButton.setEnabled(false);
                TrainingWindow.this.repaint();
            }
        });
        this.getContentPane().add(this.clearButton);
        this.clearButton.setBounds(390, 450, 117, 28);
        this.quitButton.setText("Quit");
        this.quitButton.setToolTipText("Click to quit to main window");
        this.quitButton.setToolTipText("This step is irreversible. Please confirm twice before pressing the quit button.");
        this.getContentPane().add(this.quitButton);
        this.quitButton.setBounds(520, 450, 120, 30);
        this.quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                TrainingWindow.this.dispose();
            }
        });

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

    public boolean withinBounds(final Point curPoint, final JPanel sigPanel) {
        final Point topLeft = sigPanel.getLocationOnScreen();
        final Point bottomRight = new Point(topLeft.x + sigPanel.getWidth(), topLeft.y + sigPanel.getHeight());
        return curPoint.x >= topLeft.x && curPoint.x <= bottomRight.x && curPoint.y >= topLeft.y && curPoint.y <= bottomRight.y;
    }
}