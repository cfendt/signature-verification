/**
 * Project: Signature Verification
 * 
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.main.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class AboutWindow extends JFrame {

    /** Class serial number */
    private static final long serialVersionUID = -1814312783024292676L;

    private JPanel infoPanel;
    private JTextArea creditsTextArea;
    private JScrollPane creditsScrollPane1;
    private JPanel byPanel;
    private JButton closeButton;
    private ImageIcon byLogo;
    private JLabel byLogoLabel;
    private ImageIcon aboutIcon;

    public AboutWindow() {
        this.initComponents();
    }

    public void initComponents() {
        this.infoPanel = new JPanel();
        this.creditsScrollPane1 = new JScrollPane();
        this.creditsTextArea = new JTextArea();
        this.byPanel = new JPanel();
        this.closeButton = new JButton();
        this.byLogo = new ImageIcon("Images\\by.jpg");
        this.byLogoLabel = new JLabel(this.byLogo);
        this.aboutIcon = new ImageIcon("Images\\24.gif");
        this.setLayout(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(500, 500));
        this.setMaximizedBounds(new Rectangle(0, 0, 500, 490));
        this.setPreferredSize(new Dimension(500, 490));
        this.setAlwaysOnTop(true);
        this.setIconImage(this.aboutIcon.getImage());
        this.setTitle("About Us");
        final Dimension sSize = this.getToolkit().getScreenSize();
        this.setLocation(sSize.width / 4, sSize.height / 4);
        this.infoPanel.setLayout(null);
        this.infoPanel.setBorder(BorderFactory.createTitledBorder("Contact Info"));
        this.creditsTextArea.setText("Project Home: http://code.google.com/p/signature-verification\n" + "Ohloh Home: http://www.ohloh.net/projects/10480?p=Signature+Verification\n"
                + "Email: sachin.sudheendra@gmail.com\n");
        this.creditsTextArea.setEditable(false);
        this.creditsTextArea.setAutoscrolls(true);
        this.creditsTextArea.setColumns(20);
        this.creditsTextArea.setRows(5);
        this.creditsScrollPane1.setViewportView(this.creditsTextArea);
        this.infoPanel.add(this.creditsScrollPane1);
        this.creditsScrollPane1.setBounds(10, 20, 450, 110);
        this.add(this.infoPanel);
        this.infoPanel.setBounds(10, 5, 470, 145);
        this.byPanel.setLayout(null);
        this.byPanel.setBorder(BorderFactory.createTitledBorder(""));
        this.byLogoLabel.setBounds(4, 4, 465, 255);
        this.byPanel.add(this.byLogoLabel);
        this.add(this.byPanel);
        this.byPanel.setBounds(10, 160, 470, 260);
        this.closeButton.setText("Close");
        this.getContentPane().add(this.closeButton);
        this.closeButton.setBounds(180, 430, 130, 23);
        this.closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AboutWindow.this.dispose();
            }
        });
    }
}
