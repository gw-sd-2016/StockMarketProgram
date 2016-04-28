package popupmessages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import main.MainFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import javax.swing.JLabel;

public class ViewMovementOnGraph extends JDialog {

	private final JPanel contentPanel = new JPanel();

	public ViewMovementOnGraph(String prDate, String prMovement) {
		setBounds(100, 100, 954, 362);
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - this.getWidth() / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - this.getHeight() / 2;
		setLocation(x, y);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 1, 0, 0));
		{
			JLabel lblImage = new JLabel("");

			double annotationPosX = MainFrame.annotationPositions.get(prDate).returnPositionX();
			double annotationPosY = MainFrame.annotationPositions.get(prDate).returnPositionY();

			ImageIcon chartImage = new ImageIcon(MainFrame.returnChartImageAndResize(prMovement, annotationPosX,
					annotationPosY, MainFrame.mainChartPanel.getWidth(), MainFrame.mainChartPanel.getHeight(), prDate));

			lblImage.setIcon(chartImage);

			contentPanel.add(lblImage);
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
