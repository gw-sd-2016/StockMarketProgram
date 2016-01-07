package main.news;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import analyzers.SignificantPhrases;
import analyzers.TopicIdentification;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import helpers.ButtonColumn;
import helpers.IFrequency;
import helpers.PieRenderer;
import main.MainFrame;
import popupmessages.CheckInternet;
import popupmessages.PressReleaseFrequency;
import popupmessages.ReadNewsContent;
import popupmessages.TwitterFrequency;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JLabel;

public class AnalyzeNews extends JFrame implements IFrequency {
	private Set<String> stopWordList = new HashSet<String>();
	private static DefaultTableModel headLineTableModel; // model for volume
	public static String tweetString;
	private Map<String, String> newsHeadlineAndContent = new HashMap<String, String>();
	private ArrayList<String> headlinesForFiles = new ArrayList<String>();
	private ChartPanel pieChartPanel;
	private GridBagConstraints gbc_pieChartPanel;
	private GridBagConstraints gbc_barChartPanel;
	private JTextPane twitterTextArea;
	private JScrollPane headlineScrollPane;
	private JTable headLineTable;
	private JButton prWordFrequency;
	private JButton twtrWordFrequency;
	private ChartPanel barChartPanel;

	public AnalyzeNews() throws IOException {

		setExtendedState(MainFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 213, 0 };
		gridBagLayout.rowHeights = new int[] { 197, 229, 0, 0, 293, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		headlineScrollPane = new JScrollPane();
		GridBagConstraints gbc_headlineScrollPane = new GridBagConstraints();
		gbc_headlineScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_headlineScrollPane.fill = GridBagConstraints.BOTH;
		gbc_headlineScrollPane.gridx = 1;
		gbc_headlineScrollPane.gridy = 0;
		getContentPane().add(headlineScrollPane, gbc_headlineScrollPane);

		headLineTable = new JTable();

		headLineTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Number", "Headline", "Date", "Movement", "Days After Press Release", "Content" }) {
			Class[] columnTypes = new Class[] { String.class, Integer.class, String.class, String.class, String.class,
					Integer.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, true, true, true, true, true };

			public boolean isCellEditable(int row, int column) {

				return columnEditables[column];
			}
		});
		headLineTable.getColumnModel().getColumn(2).setPreferredWidth(225);
		headLineTable.getColumnModel().getColumn(4).setPreferredWidth(224);
		headLineTable.getColumnModel().getColumn(5).setPreferredWidth(204);

		headLineTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				String fileTitle = headLineTable.getValueAt(headLineTable.getSelectedRow(), 1).toString() + ".txt";
				System.out.println(cleanText(fileTitle));

				try {
					addPieChart(fileTitle);
				} catch (Exception e) {
					System.out.println("e");
				}

			}
		});

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		headLineTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
		headlineScrollPane.setViewportView(headLineTable);
		headLineTableModel = (DefaultTableModel) headLineTable.getModel();

		barChartPanel = new ChartPanel((JFreeChart) null);
		gbc_barChartPanel = new GridBagConstraints();
		gbc_barChartPanel.insets = new Insets(0, 0, 5, 5);
		gbc_barChartPanel.fill = GridBagConstraints.BOTH;
		gbc_barChartPanel.gridx = 1;
		gbc_barChartPanel.gridy = 1;

		prWordFrequency = new JButton("Press Release Word Frequency");
		GridBagConstraints gbc_prWordFrequency = new GridBagConstraints();
		gbc_prWordFrequency.anchor = GridBagConstraints.WEST;
		gbc_prWordFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_prWordFrequency.gridx = 1;
		gbc_prWordFrequency.gridy = 2;

		getContentPane().add(prWordFrequency, gbc_prWordFrequency);

		prWordFrequency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PressReleaseFrequency();
			}
		});

		twtrWordFrequency = new JButton("Twitter Word Frequency");
		GridBagConstraints gbc_twtrWordFrequency = new GridBagConstraints();
		gbc_twtrWordFrequency.anchor = GridBagConstraints.WEST;
		gbc_twtrWordFrequency.insets = new Insets(0, 0, 5, 5);
		gbc_twtrWordFrequency.gridx = 1;
		gbc_twtrWordFrequency.gridy = 3;
		getContentPane().add(twtrWordFrequency, gbc_twtrWordFrequency);

		twtrWordFrequency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new TwitterFrequency();
			}
		});

		JScrollPane twitterScrollPane = new JScrollPane();
		GridBagConstraints gbc_twitterScrollPane = new GridBagConstraints();
		gbc_twitterScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_twitterScrollPane.fill = GridBagConstraints.BOTH;
		gbc_twitterScrollPane.gridx = 1;
		gbc_twitterScrollPane.gridy = 4;
		getContentPane().add(twitterScrollPane, gbc_twitterScrollPane);

		twitterTextArea = new JTextPane();
		twitterScrollPane.setViewportView(twitterTextArea);

		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.VERTICAL;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 5;
		getContentPane().add(panel, gbc_panel);
		panel.setLayout(new GridLayout(1, 0, 0, 0));

		twitterPosLabel = new JLabel("Positive:");
		panel.add(twitterPosLabel);

		twitterNegLabel = new JLabel("Negative: ");
		panel.add(twitterNegLabel);

		twitterNeutLabel = new JLabel("Neutral:");
		panel.add(twitterNeutLabel);

		gbc_pieChartPanel = new GridBagConstraints();
		gbc_pieChartPanel.insets = new Insets(0, 0, 5, 0);
		gbc_pieChartPanel.fill = GridBagConstraints.BOTH;
		gbc_pieChartPanel.gridx = 2;
		gbc_pieChartPanel.gridy = 0;

		setVisible(true);

		for (String line : Files.readAllLines(Paths.get(MainFrame.GLOBALPATH + "stop-words.txt"))) {
			for (String part : line.split("\n")) {
				stopWordList.add(part);
			}
		}

		for (String date : MainFrame.headlinesAndDates.keySet()) {
			ArrayList<String> headline = MainFrame.headlinesAndDates.get(date);

			for (String ss : headline) {
				headlinesForFiles.add(ss);
			}
		}

		try {
			pullDataFromDirectory();
		} catch (IOException e1) {
			new CheckInternet();
		}

		new Thread(retrieveNews).start();
		new Thread(retrieveTwitter).start();

	}

	Action pressButtonAction = new AbstractAction() {
		public void actionPerformed(ActionEvent e) {
			int modelRow = Integer.valueOf(e.getActionCommand());
			String valueToLookup = headLineTable.getModel().getValueAt(modelRow, 1).toString();

			ReadNewsContent dialog = new ReadNewsContent(newsHeadlineAndContent.get(valueToLookup + ".txt").trim());

		}
	};

	Runnable retrieveNews = new Runnable() {
		// fast way to remove stop words from long text.
		public String removeStopWordsAndAppend(String remove) {
			StringBuffer clean = new StringBuffer();
			int index = 0;

			while (index < remove.length()) {
				int nextIndex = remove.indexOf(" ", index);

				if (nextIndex == -1) {
					nextIndex = remove.length() - 1;
				}

				String word = remove.substring(index, nextIndex);

				if (!stopWordList.contains(word.toLowerCase())) {
					clean.append(word);
					if (nextIndex < remove.length()) {
						// this adds the word delimiter, e.g. the following
						// space
						clean.append(remove.substring(nextIndex, nextIndex + 1));
					}
				}

				index = nextIndex + 1;
			}

			return clean.toString();
		}

		public String changeDateFormat(String dateString) throws ParseException {
			SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = oldFormat.parse(dateString);
			SimpleDateFormat newFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
			newFormat.format(date);
			return newFormat.format(date).toString();
		}

		public void run() {
			Document doc = null;

			try {
				doc = Jsoup
						.connect("http://finance.yahoo.com/q/p?s=" + MainFrame.searchBox.getText() + "+Press+Releases")
						.get();

			} catch (IOException e) {

				new CheckInternet();
			}

			int numberOfPRs = 0;
			boolean cacheISNeeded = cacheNeeded(MainFrame.searchBox.getText());

			// this will get the days volume of press release and the next 3
			// days (if not what is available)
			for (String date : MainFrame.headlinesAndDates.keySet()) {
				double totalVolume = 0;
				ArrayList<String> headline = MainFrame.headlinesAndDates.get(date);

				for (int i = 0; i < MainFrame.volumeDataDate.size(); i++) {
					int nextThreeDates = i;
					int nextthreeDatesLimit = i + 4;
					int howManyDaysAfter = 0;

					if (MainFrame.volumeDataDate.get(i).equals(date)) {

						while (nextThreeDates < nextthreeDatesLimit) {

							if (nextThreeDates < MainFrame.volumeDataDate.size()) {
								totalVolume += (double) MainFrame.volumeDataNumber.get(nextThreeDates);
								howManyDaysAfter++;
								nextThreeDates++;

							} else {
								break;
							}
						}

						if (totalVolume / howManyDaysAfter < MainFrame.volumeDataNumber.get(i)) {
							ButtonColumn buttonColumn = new ButtonColumn(headLineTable, pressButtonAction, 5);

							for (String ss : headline) {
								try {

									headLineTableModel.addRow(new Object[] { ++numberOfPRs, cleanText(ss),
											changeDateFormat(MainFrame.volumeDataDate.get(i)), "Down", howManyDaysAfter,
											"Read" });

								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						} else {
							for (String ss : headline) {
								ButtonColumn buttonColumn = new ButtonColumn(headLineTable, pressButtonAction, 5);

								try {
									headLineTableModel.addRow(new Object[] { ++numberOfPRs, cleanText(ss),
											changeDateFormat(MainFrame.volumeDataDate.get(i)), "Up", howManyDaysAfter,
											"Read" });
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			boolean fileExists = fileNameExistsInDirectory(MainFrame.searchBox.getText());

			int numberOfFiles = 0;

			for (String s : extractMessageLinks(doc)) {
				if (cacheISNeeded || !fileExists) {
					try {

						URL url = new URL(s);
						String text = ArticleExtractor.INSTANCE.getText(url);
						writeToCacheFile(text, MainFrame.searchBox.getText(), headlinesForFiles.get(numberOfFiles++));
					} catch (IOException e) {
						new CheckInternet();
					} catch (BoilerpipeProcessingException e) {
						new CheckInternet();
					}
				}

				Collections.reverse(MainFrame.volumeDataDate);
				Collections.reverse(MainFrame.volumeDataNumber);
			}

			numberOfFiles = 0;

			try {
				new TopicIdentification(MainFrame.searchBox.getText());
			} catch (ClassNotFoundException e) {
				new CheckInternet();
			} catch (IOException e) {
				new CheckInternet();
			}

			try {
				new SignificantPhrases(new File(MainFrame.GLOBALPATH + "cache\\" + MainFrame.searchBox.getText()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			addBarChart();
		}
	};

	// cleans text since files can't have certain characters
	private String cleanText(String fix) {
		String result = fix;
		result = result.replaceAll("[^a-zA-Z0-9.-]", " ").trim().replaceAll(" +", " ");

		return result;
	}

	private void addBarChart() {
		if (getContentPane().getComponentCount() != 0) {
			if (barChartPanel != null) {
				getContentPane().remove(barChartPanel);
			}
		}

		final CategoryDataset dataset = createDatasetForBarChart();
		final JFreeChart chart = createBarChart(dataset);

		barChartPanel = new ChartPanel(chart);

		getContentPane().add(barChartPanel, gbc_barChartPanel);
		getContentPane().revalidate();
		getContentPane().repaint();
	}

	private void addPieChart(String title) {

		if (getContentPane().getComponentCount() != 0) {
			if (pieChartPanel != null) {
				getContentPane().remove(pieChartPanel);
			}
		}

		final PieDataset dataset = createDataSetForPieChart(title);
		final JFreeChart chart = createPieChart(dataset, title);

		pieChartPanel = new ChartPanel(chart);

		getContentPane().add(pieChartPanel, gbc_pieChartPanel);
		getContentPane().revalidate();
		getContentPane().repaint();
	}

	private void pullDataFromDirectory() throws IOException {

		File folder = new File(MainFrame.GLOBALPATH + "cache\\" + MainFrame.searchBox.getText());

		File[] listOfFiles = folder.listFiles();
		if (!cacheNeeded(MainFrame.searchBox.getText())) {
			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				if (file.isFile() && file.getName().endsWith(".txt")) {
					String content = FileUtils.readFileToString(file);
					newsHeadlineAndContent.put(file.getName(), content);
				}
			}
		}
	}

	private boolean fileNameExistsInDirectory(String symbol) {
		String directory = MainFrame.GLOBALPATH + "cache\\" + symbol;
		File theDirectory = new File(directory);

		System.out.println("d is " + theDirectory);

		for (int row = 0; row < headLineTableModel.getRowCount(); row++) {
			File theFile = new File(
					theDirectory + File.separator + headLineTableModel.getValueAt(row, 1).toString() + ".txt");
			System.out.println("file is : " + theFile);

			if (!theFile.exists()) {
				File fileDirectory = new File(MainFrame.GLOBALPATH + "cache\\" + symbol);
				for (File file : fileDirectory.listFiles())
					file.delete();

				return false;
			}
		}

		return true;
	}

	private boolean cacheNeeded(String symbol) {
		String directory = MainFrame.GLOBALPATH + "cache\\" + symbol;
		File theDirectory = new File(directory);

		if (theDirectory.exists()) {
			int fileCount = new File(directory).listFiles().length;
			int headlineSize = headlinesForFiles.size();
			if (fileCount != headlineSize) {
				File fileDirectory = new File(MainFrame.GLOBALPATH + "cache\\" + symbol);
				for (File file : fileDirectory.listFiles())
					file.delete();

				return true;
			} else {
				return false;
			}
		} else {
			createCacheFolder(symbol);
			return true;
		}

	}

	private void createCacheFolder(String symbol) {
		File dir = new File(MainFrame.GLOBALPATH + "cache\\" + symbol);

		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private void writeToCacheFile(String write, String symbol, String title) throws IOException {
		if (title.length() > 144)
			title = title.substring(0, 140);

		String filePath = (MainFrame.GLOBALPATH + File.separator + "cache" + File.separator + symbol + File.separator
				+ cleanText(title.replaceAll("\"", "")) + ".txt");

		File f = new File(filePath);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();

			FileWriter writer = new FileWriter(filePath);
			writer.write(write);
			writer.close();
		} else {

			try {
				Files.write(Paths.get(filePath), (write + System.lineSeparator() + System.lineSeparator()).getBytes(),
						StandardOpenOption.APPEND);
			} catch (IOException e) {
			}
		}
	}

	Runnable retrieveTwitter = new Runnable() {
		public void run() {
			RetrieveTwitter twitter = new RetrieveTwitter(MainFrame.searchBox.getText());

			// appendToPane(twitterTextArea, twitter.retrieveTweets(),
			// Color.BLACK);
			// ArrayList<String> tweets = new ArrayList<String>();

			// String[] myList = twitter.retrieveTweets().split("\n");

			String text = twitter.retrieveTweets();
			String result = "";

			appendToPane(twitterTextArea, "Loading . . .", Color.BLACK);

			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			Annotation annotation = pipeline.process(text);
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

			for (CoreMap sentence : sentences) {
				String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
				System.out.println(sentiment + "\t" + sentence);
				result += sentiment + "\t" + sentence + '\n';
			}

			twitterTextArea.setText("");

			wordFrequency(result, twitterPosLabel);

			appendToPane(twitterTextArea, result, Color.BLACK);

		}
	};
	private JPanel panel;
	private JLabel twitterNegLabel;
	private JLabel twitterPosLabel;
	private JLabel twitterNeutLabel;

	private ArrayList<String> extractMessageLinks(Document doc) {
		ArrayList<String> messageLinks = new ArrayList<String>();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			if (link.toString().contains("finance.yahoo.com/news/")) {
				messageLinks.add(link.attr("abs:href"));
			}
		}

		return messageLinks;
	}

	public Map<String, Integer> wordFrequency(String xlo, JLabel printArea) {
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

		printMap(myMap, printArea);
		return myMap;
	}

	// FIX ARGUMENTS
	public void printMap(Map<String, Integer> map, JLabel printArea) {
		twitterNegLabel.setText("Negative: " + map.get("negative"));
		twitterPosLabel.setText("Positive: " + map.get("positive"));
		twitterNeutLabel.setText("Neutral: " + map.get("neutral"));
	}

	private void appendToPane(JTextPane tp, String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	private PieDataset createDataSetForPieChart(String title) {

		final DefaultPieDataset result = new DefaultPieDataset();

		for (int i = 0; i < TopicIdentification.CATEGORIES.length; i++) {
			result.setValue(TopicIdentification.CATEGORIES[i],
					Math.abs(new Double(Math.abs(TopicIdentification.rankings.get(title)[i]))) / 1000);

		}

		return result;
	}

	private JFreeChart createPieChart(final PieDataset dataset, String title) {

		final JFreeChart chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);

		final PiePlot3D plot = (PiePlot3D) chart.getPlot();

		PieRenderer renderer = new PieRenderer();
		renderer.setColor(plot, dataset);

		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);
		plot.setNoDataMessage("No data to display");

		return chart;
	}

	private CategoryDataset createDatasetForBarChart() {
		final String series1 = "Significant Terms";
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (Entry<Double, String> entry : SignificantPhrases.training.entrySet()) {
			dataset.addValue(entry.getKey(), series1, entry.getValue());
		}
		// for (Entry<Double, String> entry : Lingpipe.newterms.entrySet()) {
		// System.out.println(entry.getKey() + "/" + entry.getValue());
		// dataset.addValue(entry.getKey()*1000, series2, entry.getValue());
		// }

		// dataset.addValue(1.0, series1, category1);
		// dataset.addValue(4.0, series1, category2);
		// dataset.addValue(3.0, series1, category3);
		// dataset.addValue(5.0, series1, category4);
		// dataset.addValue(5.0, series1, category5);
		//
		// dataset.addValue(5.0, series2, category1);
		// dataset.addValue(7.0, series2, category2);
		// dataset.addValue(6.0, series2, category3);
		// dataset.addValue(8.0, series2, category4);
		// dataset.addValue(4.0, series2, category5);
		//
		// dataset.addValue(4.0, series3, category1);
		// dataset.addValue(3.0, series3, category2);
		// dataset.addValue(2.0, series3, category3);
		// dataset.addValue(3.0, series3, category4);
		// dataset.addValue(6.0, series3, category5);

		return dataset;

	}

	private JFreeChart createBarChart(final CategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createBarChart(MainFrame.lblCompanyName.getText().replace("Name: ", ""),
				"Category", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);

		final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue, 0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray);
		renderer.setSeriesPaint(0, gp0);
		renderer.setSeriesPaint(1, gp1);
		renderer.setSeriesPaint(2, gp2);

		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		return chart;
	}

	@Override
	public Map<String, Integer> wordFrequency(String xlo, JTextPane printArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printMap(Map<String, Integer> map, JTextPane printArea) {
		// TODO Auto-generated method stub

	}
}
