package popupmessages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class CheckInternet extends JDialog {

	private final JPanel contentPanel = new JPanel();

	public CheckInternet() {

		setBounds(100, 100, 427, 145);
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - this.getWidth() / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - this.getHeight() / 2;
		setLocation(x, y);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JLabel lblYouMustEnter = new JLabel("                           Information could not be loaded   ");
			contentPanel.add(lblYouMustEnter);
		}
		{
			JLabel lblEither = new JLabel("  Either the data cannot be found or there is a connection problem");
			contentPanel.add(lblEither);
		}
		{
			JLabel lblCheckConnectionAnd = new JLabel("                              Check connection and try again");
			contentPanel.add(lblCheckConnectionAnd);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						if (arg0.getActionCommand() == "OK") {
							dispose();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		setVisible(true);
	}
}
