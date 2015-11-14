package popupmessages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import main.news.AnalyzeNews;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PressReleaseFrequency extends JDialog {
	private JTable twitterFrequencyTable;
	private static DefaultTableModel twitterFrequencyModel; // model for volume

	/**
	 * Create the dialog.
	 */
	public PressReleaseFrequency() {

		setBounds(100, 100, 538, 471);
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - this.getWidth() / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - this.getHeight() / 2;
		setLocation(x, y);
		getContentPane().setLayout(new BorderLayout());
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
		{
			JScrollPane twitterScrollPane = new JScrollPane();
			getContentPane().add(twitterScrollPane, BorderLayout.CENTER);
			{
				twitterFrequencyTable = new JTable();
				twitterFrequencyTable.setModel(
						new DefaultTableModel(new Object[][] {}, new String[] { "Word", "Number of Times Seen" }) {
							Class[] columnTypes = new Class[] { String.class, Integer.class };

							public Class getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}
						});
				twitterFrequencyTable.getColumnModel().getColumn(0).setPreferredWidth(163);
				twitterFrequencyTable.getColumnModel().getColumn(1).setPreferredWidth(191);
				twitterScrollPane.setViewportView(twitterFrequencyTable);
				twitterFrequencyModel = (DefaultTableModel) twitterFrequencyTable.getModel();
			}
		}
		wordFrequency(AnalyzeNews.allNewsList);
		setVisible(true);
	}

	public Map<String, Integer> wordFrequency(String xlo) {
		Map<String, Integer> myMap = new HashMap<String, Integer>();

		String words = xlo;
		String lowerCase = words.toLowerCase();
		String alphaOnly = lowerCase.replaceAll("\\W", " "); // Replaces all
																// special
																// characters
		String finalString = alphaOnly.replaceAll("[0-9]", " "); // Gets rid of
																	// numbers
		String[] array = finalString.split("\\s+");

		for (String name : array) {
			if (myMap.containsKey(name)) {
				int count = myMap.get(name);
				myMap.put(name, count + 1);

			} else {
				myMap.put(name, 1);
			}
		}

		printMapOnTable(myMap, twitterFrequencyModel);
		TableRowSorter sorter = new TableRowSorter(twitterFrequencyModel);
		twitterFrequencyTable.setRowSorter(sorter);
		sorter.setSortsOnUpdates(true);

		return myMap;
	}

	public void printMapOnTable(Map<String, Integer> map, DefaultTableModel model) {
		for (Map.Entry entry : map.entrySet()) {
			model.addRow(new Object[] { entry.getKey(), entry.getValue() });
		}
	}
}
