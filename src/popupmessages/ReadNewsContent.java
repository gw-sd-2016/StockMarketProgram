package popupmessages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ReadNewsContent extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextArea contentArea;

	public ReadNewsContent(String content) {

		setBounds(100, 100, 709, 379);
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - this.getWidth() / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - this.getHeight() / 2;
		setLocation(x, y);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JScrollPane contentScrollPane = new JScrollPane();
			contentPanel.add(contentScrollPane);
			{
				contentArea = new JTextArea();
				contentScrollPane.setViewportView(contentArea);
			}
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

		contentArea.append(content);
		contentArea.setCaretPosition(0);

		setVisible(true);
	}
}
