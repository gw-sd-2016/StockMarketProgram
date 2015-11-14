package main.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.TextArea;
import java.awt.SystemColor;
import javax.swing.border.MatteBorder;

public class AboutMenu extends JFrame {

    private JPanel contentPane;
    private JLabel logo;
    private JLabel companyName;
    private JLabel version;

    /**
     * Launch the application.
     */
   
   

    /**
     * Create the frame.
     */
    public AboutMenu() {
       
        setBounds(10, 10, 300, 200);
        contentPane = new JPanel();
        contentPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(246, 131, 219)));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
       
        Icon icon = new ImageIcon("icon.gif");
       
        logo = new JLabel(icon);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(logo, BorderLayout.NORTH);
       
        companyName = new JLabel("Stock Analyzer");
        companyName.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(companyName, BorderLayout.SOUTH);
       
         version = new JLabel("Version 1.0\n\n\n");
        version.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(version, BorderLayout.CENTER);
       
       
       
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2)
                - this.getWidth() / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2)
                - this.getHeight() / 2;
        this.setLocation(x, y);
        setDefaultCloseOperation(this.HIDE_ON_CLOSE);
        setVisible(true);
        setResizable(false);       
    }

}