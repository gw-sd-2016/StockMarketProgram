package main.menu;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import main.MainFrame;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class AboutMenu extends JFrame {

	private JPanel contentPane;
	private JLabel version;
	private JLabel logo;
	private JLabel lblDellolio;

	public AboutMenu() throws IOException {
		setIconImage(Toolkit.getDefaultToolkit().getImage("images/taskbarlogo.png"));

		setBounds(10, 10, 300, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(246, 131, 219)));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 292, 0 };
		gbl_contentPane.rowHeights = new int[] { 130, 20, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		BufferedImage logoImage = ImageIO.read(new File(MainFrame.GLOBALPATH + "images/logo.png"));
		logo = new JLabel("");
		logo.setIcon(
				new ImageIcon(new ImageIcon(logoImage).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
		GridBagConstraints gbc_logo = new GridBagConstraints();
		gbc_logo.insets = new Insets(0, 0, 5, 0);
		gbc_logo.gridx = 0;
		gbc_logo.gridy = 0;
		contentPane.add(logo, gbc_logo);

		version = new JLabel("Version 1.0");
		version.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_version = new GridBagConstraints();
		gbc_version.insets = new Insets(0, 0, 5, 0);
		gbc_version.fill = GridBagConstraints.BOTH;
		gbc_version.gridx = 0;
		gbc_version.gridy = 1;
		contentPane.add(version, gbc_version);

		lblDellolio = new JLabel("Dellolio");
		GridBagConstraints gbc_lblDellolio = new GridBagConstraints();
		gbc_lblDellolio.gridx = 0;
		gbc_lblDellolio.gridy = 2;
		contentPane.add(lblDellolio, gbc_lblDellolio);

		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - this.getWidth() / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - this.getHeight() / 2;
		this.setLocation(x, y);
		setDefaultCloseOperation(this.HIDE_ON_CLOSE);
		setVisible(true);
		setResizable(false);
	}
}
