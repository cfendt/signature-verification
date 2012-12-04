/**
 * Project: Signature Verification
 * @author Ajay R, Keshav Kumar HK and Sachin Sudheendra
 */

package org.thoughtworks.signatureverification.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutWindow extends JFrame {

    private JPanel infoPanel;
    private JTextArea creditsTextArea;
    private JScrollPane creditsScrollPane1;
    private JPanel byPanel;
    private JButton closeButton;
    private ImageIcon byLogo;
    private JLabel byLogoLabel;
    private ImageIcon aboutIcon;

    public AboutWindow() {
        initComponents();
    }


    public void initComponents() {
        infoPanel = new JPanel();
        creditsScrollPane1 = new JScrollPane();
        creditsTextArea = new JTextArea();
        byPanel = new JPanel();
        closeButton = new JButton();
        byLogo = new ImageIcon("Images\\by.jpg");
        byLogoLabel = new JLabel(byLogo);
        aboutIcon = new ImageIcon("Images\\24.gif");
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 500));
        setMaximizedBounds(new Rectangle(0, 0, 500, 490));
        setPreferredSize(new Dimension(500, 490));
        setAlwaysOnTop(true);
        setIconImage(aboutIcon.getImage());
        setTitle("About Us");
        Dimension sSize = getToolkit().getScreenSize();
        setLocation(sSize.width / 4, sSize.height / 4);
        infoPanel.setLayout(null);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Contact Info"));
        creditsTextArea.setText("Project Home: http://code.google.com/p/signature-verification\n" +
                "Ohloh Home: http://www.ohloh.net/projects/10480?p=Signature+Verification\n" +
                "Email: sachin.sudheendra@gmail.com\n");
        creditsTextArea.setEditable(false);
        creditsTextArea.setAutoscrolls(true);
        creditsTextArea.setColumns(20);
        creditsTextArea.setRows(5);
        creditsScrollPane1.setViewportView(creditsTextArea);
        infoPanel.add(creditsScrollPane1);
        creditsScrollPane1.setBounds(10, 20, 450, 110);
        add(infoPanel);
        infoPanel.setBounds(10, 5, 470, 145);
        byPanel.setLayout(null);
        byPanel.setBorder(BorderFactory.createTitledBorder(""));
        byLogoLabel.setBounds(4, 4, 465, 255);
        byPanel.add(byLogoLabel);
        add(byPanel);
        byPanel.setBounds(10, 160, 470, 260);
        closeButton.setText("Close");
        getContentPane().add(closeButton);
        closeButton.setBounds(180, 430, 130, 23);
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
