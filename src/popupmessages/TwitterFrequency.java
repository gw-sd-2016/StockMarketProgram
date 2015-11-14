package popupmessages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import helpers.IFrequency;
import main.news.AnalyzeNews;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class TwitterFrequency extends JDialog {
	private JTable twitterFrequencyTable;
	private static DefaultTableModel twitterFrequencyModel; // model for volume

	/**
	 * Create the dialog.
	 */
	public TwitterFrequency() {

		setBounds(100, 100, 1226, 471);
		int x = (Toolkit.getDefaultToolkit().getScreenSize().width / 2) - this.getWidth() / 2;
		int y = (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - this.getHeight() / 2;
		setLocation(x, y);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 592, 1210, 0 };
		gridBagLayout.rowHeights = new int[] { 399, 33, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
		{
			JScrollPane twitterScrollPane = new JScrollPane();
			GridBagConstraints gbc_twitterScrollPane = new GridBagConstraints();
			gbc_twitterScrollPane.fill = GridBagConstraints.BOTH;
			gbc_twitterScrollPane.insets = new Insets(0, 0, 5, 5);
			gbc_twitterScrollPane.gridx = 0;
			gbc_twitterScrollPane.gridy = 0;
			getContentPane().add(twitterScrollPane, gbc_twitterScrollPane);
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
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 1;
			gbc_panel.gridy = 0;
			getContentPane().add(panel, gbc_panel);

			final CategoryDataset dataset1 = createDataset1();

			final JFreeChart chart = ChartFactory.createBarChart("Dual Axis Chart", // chart
																					// title
					"Category", // domain axis label
					"Value", // range axis label
					dataset1, // data
					PlotOrientation.VERTICAL, true, // include legend
					true, // tooltips?
					false // URL generator? Not required...
			);

			// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
			chart.setBackgroundPaint(Color.white);
			// chart.getLegend().setAnchor(Legend.SOUTH);

			// get a reference to the plot for further customisation...
			final CategoryPlot plot = chart.getCategoryPlot();
			plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
			plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			// add the chart to a panel...
			ChartPanel chartPanel = new ChartPanel(chart);
			panel.setPreferredSize(new java.awt.Dimension(500, 270));
			panel.add(chartPanel);

		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.gridwidth = 2;
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 0;
			gbc_buttonPane.gridy = 1;
			getContentPane().add(buttonPane, gbc_buttonPane);
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
		wordFrequency(AnalyzeNews.tweetString);
		setVisible(true);
	}

	
	//this is a start. need to fix
	private CategoryDataset createDataset1() {
		final String series1 = "First";
		final String series2 = "Second";
		final String series3 = "Third";

		final String category1 = "Category 1";
		final String category2 = "Category 2";
		final String category3 = "Category 3";
		final String category4 = "Category 4";
		final String category5 = "Category 5";
		final String category6 = "Category 6";
		final String category7 = "Category 7";
		final String category8 = "Category 8";

		
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		dataset.addValue(1.0, series1, category1);
		dataset.addValue(4.0, series1, category2);
		dataset.addValue(3.0, series1, category3);
		dataset.addValue(5.0, series1, category4);
		dataset.addValue(5.0, series1, category5);
		dataset.addValue(7.0, series1, category6);
		dataset.addValue(7.0, series1, category7);
		dataset.addValue(8.0, series1, category8);

		dataset.addValue(5.0, series2, category1);
		dataset.addValue(7.0, series2, category2);
		dataset.addValue(6.0, series2, category3);
		dataset.addValue(8.0, series2, category4);
		dataset.addValue(4.0, series2, category5);
		dataset.addValue(4.0, series2, category6);
		dataset.addValue(2.0, series2, category7);
		dataset.addValue(1.0, series2, category8);

		dataset.addValue(4.0, series3, category1);
		dataset.addValue(3.0, series3, category2);
		dataset.addValue(2.0, series3, category3);
		dataset.addValue(3.0, series3, category4);
		dataset.addValue(6.0, series3, category5);
		dataset.addValue(3.0, series3, category6);
		dataset.addValue(4.0, series3, category7);
		dataset.addValue(3.0, series3, category8);

		return dataset;
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
