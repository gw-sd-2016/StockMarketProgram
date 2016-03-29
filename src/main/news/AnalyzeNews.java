package main.news;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.apache.commons.io.FileUtils;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleInsets;
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
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import helpers.ButtonColumn;
import helpers.PieRenderer;
import main.MainFrame;
import objects.IntegerPair;
import popupmessages.CheckInternet;
import popupmessages.ReadNewsContent;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

public class AnalyzeNews extends JFrame {
	private Set<String> stopWordList = new HashSet<String>();
	private static DefaultTableModel headLineTableModel; // model for volume
	private static DefaultTableModel informationTableModel; // model for info
															// summary
	public static String tweetString;
	private Map<String, String> newsHeadlineAndContent = new HashMap<String, String>();
	private Map<String, String> tweetSentimentAndContent = new HashMap<String, String>();
	private ArrayList<String> headlinesForFiles = new ArrayList<String>();
	private ChartPanel pieChartPanel;
	private ChartPanel barChartPanel;
	private ChartPanel twitterChartPanel;
	private GridBagConstraints gbc_pieChartPanel;
	private GridBagConstraints gbc_barChartPanel;
	private GridBagConstraints gbc_twitterChartPanel;
	private JScrollPane headlineScrollPane;
	private JScrollPane informationScrollPane;
	private JScrollPane extendedWordScrollPane;
	private JTable headLineTable;
	private JTable informationTable;
	private String symbol = MainFrame.searchBox.getText();
	private String loadingLabelDirectory = MainFrame.GLOBALPATH + "images/loading-image.gif";
	private String prDate = null;
	private String prMovement = null;
	private Color highlightRed = new Color(242, 44, 67);
	private Color highlightGreen = new Color(44, 242, 153);
	private Color highlightGray = new Color(198, 204, 201);
	private JPanel twitterResultPanel;
	private JPanel searchKeyPanel;
	private JPanel borderPanel;
	private JLabel twitterNegLabel;
	private JLabel twitterPosLabel;
	private JLabel twitterNeutLabel;
	private JLabel lblAverageScore;
	private JLabel twitterLoadingLabel;
	private JLabel sectorLoadingLabel;
	private JLabel sigTermsLoadingLabel;
	private JLabel searchLoadingLabel;
	private JLabel lblSeen;
	private JLabel lblWord;
	private JTextComponent informationTextField;
	private JTextPane extendedTextPane;

	public AnalyzeNews() throws IOException {

		setIconImage(Toolkit.getDefaultToolkit().getImage("images/taskbarlogo.png"));

		setExtendedState(MainFrame.MAXIMIZED_BOTH);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 509, 0 };
		gridBagLayout.rowHeights = new int[] { 197, 0, 229, 0, 293, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		headlineScrollPane = new JScrollPane();
		GridBagConstraints gbc_headlineScrollPane = new GridBagConstraints();
		gbc_headlineScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_headlineScrollPane.fill = GridBagConstraints.BOTH;
		gbc_headlineScrollPane.gridx = 1;
		gbc_headlineScrollPane.gridy = 0;
		getContentPane().add(headlineScrollPane, gbc_headlineScrollPane);

		headLineTable = new JTable() {
			// Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();

				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);

				try {
					tip = getValueAt(rowIndex, colIndex).toString();
				} catch (RuntimeException e1) {
				}

				return tip;
			}
		};

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
		gbc_barChartPanel.gridy = 2;

		informationTextField = new JTextField();

		PromptSupport.setPrompt("Enter a Word or Phrase to Search", informationTextField);

		informationTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {

					numberOfTimesSeenPressRelease();
					numberOfTimesSeenTwitter();
				}
			}
		});

		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		getContentPane().add(informationTextField, gbc_textField);

		sigTermsLoadingLabel = new JLabel("");
		GridBagConstraints gbc_sigTermsLoadingLabel = new GridBagConstraints();
		gbc_sigTermsLoadingLabel.insets = new Insets(0, 0, 5, 5);
		gbc_sigTermsLoadingLabel.gridx = 1;
		gbc_sigTermsLoadingLabel.gridy = 2;
		getContentPane().add(sigTermsLoadingLabel, gbc_sigTermsLoadingLabel);

		informationScrollPane = new JScrollPane();
		informationScrollPane.setEnabled(false);
		GridBagConstraints gbc_extendedWordScrollPane = new GridBagConstraints();
		gbc_extendedWordScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_extendedWordScrollPane.fill = GridBagConstraints.BOTH;
		gbc_extendedWordScrollPane.gridx = 2;
		gbc_extendedWordScrollPane.gridy = 2;
		getContentPane().add(informationScrollPane, gbc_extendedWordScrollPane);

		informationTable = new JTable();
		informationTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				JTable table = (JTable) arg0.getSource();
				Point p = arg0.getPoint();
				int row = table.rowAtPoint(p);

				if (arg0.getClickCount() == 2) {
					Thread information = new Thread(new informationTable(row));
					information.start();
				}
			}
		});

		informationTable.setEnabled(false);
		informationTable.setModel(new DefaultTableModel(new Object[][] {},
				new String[] { "Type", "Word", "Title", "Number of Times Seen", "Movement" }) {
			Class[] columnTypes = new Class[] { String.class, String.class, String.class, Integer.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}

			boolean[] columnEditables = new boolean[] { false, true, true, true, true, true };

			public boolean isCellEditable(int row, int column) {

				return columnEditables[column];
			}
		});

		informationScrollPane.setViewportView(informationTable);
		informationTableModel = (DefaultTableModel) informationTable.getModel();

		twitterChartPanel = new ChartPanel((JFreeChart) null);

		gbc_twitterChartPanel = new GridBagConstraints();
		gbc_twitterChartPanel.insets = new Insets(0, 0, 5, 5);
		gbc_twitterChartPanel.fill = GridBagConstraints.BOTH;
		gbc_twitterChartPanel.gridx = 1;
		gbc_twitterChartPanel.gridy = 4;

		twitterLoadingLabel = new JLabel("");
		GridBagConstraints gbc_twitterLoadingLabel = new GridBagConstraints();
		gbc_twitterLoadingLabel.insets = new Insets(0, 0, 5, 5);
		gbc_twitterLoadingLabel.gridx = 1;
		gbc_twitterLoadingLabel.gridy = 4;
		getContentPane().add(twitterLoadingLabel, gbc_twitterLoadingLabel);

		sectorLoadingLabel = new JLabel("");
		GridBagConstraints gbc_sectorLoadingLabel = new GridBagConstraints();
		gbc_sectorLoadingLabel.insets = new Insets(0, 0, 5, 0);
		gbc_sectorLoadingLabel.gridx = 2;
		gbc_sectorLoadingLabel.gridy = 0;
		getContentPane().add(sectorLoadingLabel, gbc_sectorLoadingLabel);

		extendedWordScrollPane = new JScrollPane();
		GridBagConstraints gbc_extendedWordScrollPane1 = new GridBagConstraints();
		gbc_extendedWordScrollPane1.insets = new Insets(0, 0, 5, 0);
		gbc_extendedWordScrollPane1.fill = GridBagConstraints.BOTH;
		gbc_extendedWordScrollPane1.gridx = 2;
		gbc_extendedWordScrollPane1.gridy = 4;
		getContentPane().add(extendedWordScrollPane, gbc_extendedWordScrollPane1);

		class ImageToolTipUI extends MetalToolTipUI {
			double annotationPosX = MainFrame.annotationPositions.get(prDate).returnPositionX();
			double annotationPosY = MainFrame.annotationPositions.get(prDate).returnPositionY();

			ImageIcon newImage = new ImageIcon(MainFrame.returnChartImageAndResize(prMovement, annotationPosX,
					annotationPosY, MainFrame.mainChartPanel.getWidth(), MainFrame.mainChartPanel.getHeight(), prDate));

			public void paint(Graphics g, JComponent c) {

				FontMetrics metrics = c.getFontMetrics(g.getFont());
				g.setColor(c.getForeground());
				g.drawString(((JToolTip) c).getTipText(), 1, 1);
				g.drawImage(newImage.getImage(), 1, metrics.getHeight(), c);
			}

			public Dimension getPreferredSize(JComponent c) {
				FontMetrics metrics = c.getFontMetrics(c.getFont());
				String tipText = ((JToolTip) c).getTipText();

				if (tipText == null) {
					tipText = "";
				}

				Image image = newImage.getImage();

				int width = SwingUtilities.computeStringWidth(metrics, tipText);
				int height = metrics.getHeight() + image.getHeight(c);

				if (width < image.getWidth(c)) {
					width = image.getWidth(c);
				}

				return new Dimension(width, height);
			}
		}

		class ImageToolTip extends JToolTip {
			public ImageToolTip() {
				setUI(new ImageToolTipUI());
			}
		}

		extendedTextPane = new JTextPane() {
			public JToolTip createToolTip() {
				return new ImageToolTip();
			}
		};

		searchLoadingLabel = new JLabel("");

		extendedWordScrollPane.setViewportView(extendedTextPane);

		twitterResultPanel = new JPanel();
		GridBagConstraints gbc_twitterResultPanel = new GridBagConstraints();
		gbc_twitterResultPanel.insets = new Insets(0, 0, 0, 5);
		gbc_twitterResultPanel.fill = GridBagConstraints.VERTICAL;
		gbc_twitterResultPanel.gridx = 1;
		gbc_twitterResultPanel.gridy = 5;
		getContentPane().add(twitterResultPanel, gbc_twitterResultPanel);
		twitterResultPanel.setLayout(new GridLayout(1, 0, 0, 0));

		twitterPosLabel = new JLabel("Positive: ");
		twitterResultPanel.add(twitterPosLabel);

		twitterNegLabel = new JLabel("Negative: ");
		twitterResultPanel.add(twitterNegLabel);

		twitterNeutLabel = new JLabel("Neutral: ");
		twitterResultPanel.add(twitterNeutLabel);

		searchKeyPanel = new JPanel();
		GridBagConstraints gbc_searchKeyPanel = new GridBagConstraints();
		gbc_searchKeyPanel.fill = GridBagConstraints.VERTICAL;
		gbc_searchKeyPanel.gridx = 2;
		gbc_searchKeyPanel.gridy = 5;
		getContentPane().add(searchKeyPanel, gbc_searchKeyPanel);

		// images are 12x11 for consistency
		BufferedImage redImage = ImageIO.read(new File(MainFrame.GLOBALPATH + "images/highlightRed.png"));
		BufferedImage greenImage = ImageIO.read(new File(MainFrame.GLOBALPATH + "images/highlightGreen.png"));
		BufferedImage grayImage = ImageIO.read(new File(MainFrame.GLOBALPATH + "images/highlightGray.png"));

		GridBagLayout gbl_searchKeyPanel = new GridBagLayout();
		gbl_searchKeyPanel.columnWidths = new int[] { 67, 86, 59, 0 };
		gbl_searchKeyPanel.rowHeights = new int[] { 20, 0, 0 };
		gbl_searchKeyPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_searchKeyPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		searchKeyPanel.setLayout(gbl_searchKeyPanel);

		JLabel positiveLabel = new JLabel("Negative");
		positiveLabel.setIcon(
				new ImageIcon(new ImageIcon(redImage).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));

		GridBagConstraints gbc_positiveLabel = new GridBagConstraints();
		gbc_positiveLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_positiveLabel.insets = new Insets(0, 0, 5, 5);
		gbc_positiveLabel.gridx = 0;
		gbc_positiveLabel.gridy = 0;
		searchKeyPanel.add(positiveLabel, gbc_positiveLabel);

		JLabel negativeLabel = new JLabel("Positive");
		negativeLabel.setIcon(
				new ImageIcon(new ImageIcon(greenImage).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));

		GridBagConstraints gbc_negativeLabel = new GridBagConstraints();
		gbc_negativeLabel.anchor = GridBagConstraints.NORTH;
		gbc_negativeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_negativeLabel.gridx = 1;
		gbc_negativeLabel.gridy = 0;
		searchKeyPanel.add(negativeLabel, gbc_negativeLabel);

		JLabel neutralLabel = new JLabel("Neutral");
		neutralLabel.setIcon(
				new ImageIcon(new ImageIcon(grayImage).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT)));

		GridBagConstraints gbc_neutralLabel = new GridBagConstraints();
		gbc_neutralLabel.insets = new Insets(0, 0, 5, 0);
		gbc_neutralLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_neutralLabel.gridx = 2;
		gbc_neutralLabel.gridy = 0;

		searchKeyPanel.add(neutralLabel, gbc_neutralLabel);

		twitterLoadingLabel.setIcon(new ImageIcon(new ImageIcon(loadingLabelDirectory).getImage()));

		sectorLoadingLabel.setIcon(new ImageIcon(new ImageIcon(loadingLabelDirectory).getImage()));

		sigTermsLoadingLabel.setIcon(new ImageIcon(new ImageIcon(loadingLabelDirectory).getImage()));

		searchLoadingLabel.setIcon(new ImageIcon(new ImageIcon(loadingLabelDirectory).getImage()));

		searchLoadingLabel.setVisible(false);

		borderPanel = new JPanel();
		borderPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Statistics",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_borderPanel = new GridBagConstraints();
		gbc_borderPanel.fill = GridBagConstraints.VERTICAL;
		gbc_borderPanel.gridwidth = 3;
		gbc_borderPanel.insets = new Insets(0, 0, 0, 5);
		gbc_borderPanel.gridx = 0;
		gbc_borderPanel.gridy = 1;
		searchKeyPanel.add(borderPanel, gbc_borderPanel);
		GridBagLayout gbl_borderPanel = new GridBagLayout();
		gbl_borderPanel.columnWidths = new int[] { 74, 86, 0 };
		gbl_borderPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_borderPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_borderPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		borderPanel.setLayout(gbl_borderPanel);

		lblWord = new JLabel("Word: -");
		GridBagConstraints gbc_lblWord = new GridBagConstraints();
		gbc_lblWord.anchor = GridBagConstraints.WEST;
		gbc_lblWord.insets = new Insets(0, 0, 5, 5);
		gbc_lblWord.gridx = 0;
		gbc_lblWord.gridy = 0;
		borderPanel.add(lblWord, gbc_lblWord);

		lblSeen = new JLabel("Seen: -");
		GridBagConstraints gbc_lblSeen = new GridBagConstraints();
		gbc_lblSeen.anchor = GridBagConstraints.WEST;
		gbc_lblSeen.insets = new Insets(0, 0, 5, 5);
		gbc_lblSeen.gridx = 0;
		gbc_lblSeen.gridy = 1;
		borderPanel.add(lblSeen, gbc_lblSeen);

		lblAverageScore = new JLabel("Average Score: -");
		GridBagConstraints gbc_lblAverageScore = new GridBagConstraints();
		gbc_lblAverageScore.gridwidth = 3;
		gbc_lblAverageScore.gridx = 0;
		gbc_lblAverageScore.gridy = 2;
		borderPanel.add(lblAverageScore, gbc_lblAverageScore);

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

		public void run() {

			Document doc = null;

			try {
				doc = Jsoup.connect("http://finance.yahoo.com/q/p?s=" + symbol + "+Press+Releases").get();

			} catch (IOException e) {

				new CheckInternet();
			}

			int numberOfPRs = 0;
			boolean cacheISNeeded = cacheNeeded(symbol);

			// this will get the days volume of press release and the next 3
			// days (if not what is available)

			for (String date : MainFrame.headlinesAndDates.keySet()) {
				double totalVolume = 0;
				ArrayList<String> headline = MainFrame.headlinesAndDates.get(date);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Calendar volumeDate = Calendar.getInstance();

				try {
					Date dates = formatter.parse(date);
					volumeDate = Calendar.getInstance();
					volumeDate.setTime(dates);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < MainFrame.volumeDataDate.size(); i++) {
					int nextThreeDates = i;
					int nextthreeDatesLimit = i + 4;
					int howManyDaysAfter = 0;

					if (MainFrame.volumeDataDate.get(i).getCalendar().equals(volumeDate)) {
						while (nextThreeDates < nextthreeDatesLimit) {

							if (nextThreeDates < MainFrame.volumeDataDate.size()) {
								totalVolume += (double) MainFrame.volumeDataDate.get(nextThreeDates).getVolume();
								howManyDaysAfter++;
								nextThreeDates++;

							} else {
								break;
							}
						}

						if (totalVolume / howManyDaysAfter < MainFrame.volumeDataDate.get(i).getVolume()) {
							ButtonColumn buttonColumn = new ButtonColumn(headLineTable, pressButtonAction, 5);

							for (String ss : headline) {
								headLineTableModel.addRow(new Object[] { ++numberOfPRs, cleanText(ss),
										MainFrame.volumeDataDate.get(i).getDateAsString(), "Down", howManyDaysAfter,
										"Read" });
							}
						} else {
							for (String ss : headline) {
								ButtonColumn buttonColumn = new ButtonColumn(headLineTable, pressButtonAction, 5);

								headLineTableModel.addRow(new Object[] { ++numberOfPRs, cleanText(ss),
										MainFrame.volumeDataDate.get(i).getDateAsString(), "Up", howManyDaysAfter,
										"Read" });
							}
						}
					}
				}
			}

			boolean fileExists = fileNameExistsInDirectory(symbol);

			int numberOfFiles = 0;

			for (String s : extractMessageLinks(doc)) {
				if (cacheISNeeded || !fileExists) {
					try {

						URL url = new URL(s);
						String text = ArticleExtractor.INSTANCE.getText(url);
						String cleanText = text.substring(text.indexOf("Done") + 4).trim().replaceAll(" +", " ");
						writeToCacheFile(cleanText, symbol, headlinesForFiles.get(numberOfFiles++));
					} catch (IOException e) {
						new CheckInternet();
					} catch (BoilerpipeProcessingException e) {
						new CheckInternet();
					}
				}

				Collections.reverse(MainFrame.volumeDataDate);
			}

			numberOfFiles = 0;

			try {
				new TopicIdentification(symbol);
			} catch (ClassNotFoundException e) {
				new CheckInternet();
			} catch (IOException e) {
				new CheckInternet();
			}

			try {
				new SignificantPhrases(new File(MainFrame.GLOBALPATH + "cache\\" + symbol));
			} catch (IOException e) {
				e.printStackTrace();
			}

			String fileTitle = headLineTable.getValueAt(1, 1).toString() + ".txt";

			try {
				addPieChart(fileTitle);
			} catch (Exception e) {
				new CheckInternet();
			}

			addBarChart();
		}
	};

	class informationTable implements Runnable {
		int row;

		public informationTable(int r) {
			row = r;

		}

		public void run() {
			extendedTextPane.setToolTipText("");
			extendedTextPane.setEnabled(false);
			extendedWordScrollPane.setRowHeaderView(searchLoadingLabel);
			extendedTextPane.setText("");
			searchLoadingLabel.setVisible(true);

			String type = informationTable.getModel().getValueAt(row, 0).toString();

			Highlighter.HighlightPainter paintGreen = new HighlightPainter(highlightGreen);
			Highlighter.HighlightPainter paintRed = new HighlightPainter(highlightRed);
			Highlighter.HighlightPainter paintGrey = new HighlightPainter(highlightGray);

			if (type.equals("Tweet")) {
				String tweetContent = informationTable.getModel().getValueAt(row, 2).toString();
				String tweetSentiment = informationTable.getModel().getValueAt(row, 4).toString();

				appendToPane(extendedTextPane, tweetContent, Color.BLACK);

				Highlighter highlightWord = extendedTextPane.getHighlighter();

				if (tweetSentiment.equals("Positive")) {
					int start = 0;
					int end = tweetContent.length();

					try {
						highlightWord.addHighlight(start, end, paintGreen);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}

				} else if (tweetSentiment.equals("Negative")) {
					int start = 0;
					int end = tweetContent.length();

					try {
						highlightWord.addHighlight(start, end, paintRed);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				} else if (tweetSentiment.equals("Neutral")) {
					int start = 0;
					int end = tweetContent.length();

					try {
						highlightWord.addHighlight(start, end, paintGrey);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}

				} else {
					int start = 0;
					int end = tweetContent.length();

					try {
						highlightWord.addHighlight(start, end, paintGrey);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}

				extendedTextPane.setEnabled(true);
				searchLoadingLabel.setVisible(false);
				extendedWordScrollPane.setRowHeaderView(extendedTextPane);

				lblAverageScore.setText("Average Score: -");
			} else if (type.equals("Press Release")) {
				ArrayList<Double> scores = new ArrayList<Double>();
				String wordToSearch = informationTable.getModel().getValueAt(row, 1).toString();
				String prTitle = informationTable.getModel().getValueAt(row, 2).toString();
				String prContent = newsHeadlineAndContent.get(prTitle).trim().replaceAll(" +", " ");
				String prMvmt = informationTable.getModel().getValueAt(row, 4).toString();
				String sentenceList = "";
				int timesSeen = (int) informationTable.getModel().getValueAt(row, 3);
				int prContentWordCount = wordCount(prContent);

				System.out.println(prContentWordCount);

				prMovement = informationTable.getModel().getValueAt(row, 4).toString();

				for (String date : MainFrame.headlinesAndDates.keySet()) {
					ArrayList<String> headline = MainFrame.headlinesAndDates.get(date);

					for (String ss : headline) {
						if ((cleanText(ss) + ".txt").equals(cleanText(prTitle))) {
							prDate = date;
						}
					}
				}

				appendToPane(extendedTextPane, prContent + ".", Color.BLACK);

				Highlighter highlightWord = extendedTextPane.getHighlighter();

				Map<String, IntegerPair> sentencesToReview = returnSentencesFromPressRelease(prContent + ".",
						wordToSearch);

				// put sentences back together for the coreMap to sort
				// it

				for (String sentence : sentencesToReview.keySet()) {
					sentenceList += sentence + " ";
				}

				Properties props = new Properties();
				props.setProperty("annotators", "tokenize, ssplit, pos,lemma, parse, sentiment");

				StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				Annotation annotation = pipeline.process(sentenceList);
				List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

				for (CoreMap sentence : sentences) {
					String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
					String sentenceToSearch = sentence.toString();

					Tree annotatedTree = sentence.get(SentimentAnnotatedTree.class);
					double score = RNNCoreAnnotations.getPredictedClass(annotatedTree);
					scores.add(score);

					if (sentiment.equals("Negative")) {
						int start = sentencesToReview.get(sentenceToSearch).returnStart();
						int end = sentencesToReview.get(sentenceToSearch).returnEnd();

						try {
							highlightWord.addHighlight(start, end, paintRed);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else if (sentiment.equals("Very negative")) {
						int start = sentencesToReview.get(sentenceToSearch).returnStart();
						int end = sentencesToReview.get(sentenceToSearch).returnEnd();

						try {
							highlightWord.addHighlight(start, end, paintRed);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else if (sentiment.equals("Positive")) {
						int start = sentencesToReview.get(sentenceToSearch).returnStart();
						int end = sentencesToReview.get(sentenceToSearch).returnEnd();

						try {
							highlightWord.addHighlight(start, end, paintGreen);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else if (sentiment.equals("Very positive")) {
						int start = sentencesToReview.get(sentenceToSearch).returnStart();
						int end = sentencesToReview.get(sentenceToSearch).returnEnd();

						try {
							highlightWord.addHighlight(start, end, paintGreen);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					} else if (sentiment.equals("Neutral")) {
						int start = sentencesToReview.get(sentenceToSearch).returnStart();
						int end = sentencesToReview.get(sentenceToSearch).returnEnd();

						try {
							highlightWord.addHighlight(start, end, paintGrey);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				}

				// SimpleSummariser summariser = new SimpleSummariser();
				// String summary = summariser.summarise(prContent, 3); // 3
				// // sentence
				// // summary
				// String cleanSummary = summary.substring(summary.indexOf("--")
				// + 3).trim().replaceAll(" +", " ");
				// appendToPane(extendedTextPane,
				// "\n\n##########################\n\n", Color.RED);
				// // appendToPane(extendedTextPane, cleanSummary, Color.BLACK);
				// if (summary.contains("--")) {
				// appendToPane(extendedTextPane, cleanSummary, Color.BLACK);
				// } else {
				// appendToPane(extendedTextPane, summary, Color.BLACK);
				// }

				DecimalFormat df = new DecimalFormat("####0.00");

				lblAverageScore
						.setText("Average Score: " + df.format((averageScore(scores) / 3.0) * 100) + "% - " + prMvmt);
				lblWord.setText("Word: " + wordToSearch);
				lblSeen.setText("Seen: " + String.valueOf(timesSeen) + " / " + prContentWordCount);

				extendedTextPane.setCaretPosition(0);
				extendedTextPane.setEnabled(true);
				searchLoadingLabel.setVisible(false);
				extendedWordScrollPane.setRowHeaderView(extendedTextPane);
			}

			extendedWordScrollPane.setViewportView(extendedTextPane);
		}
	}

	public static double averageScore(ArrayList<Double> scores) {
		double sum = 0.0;

		for (double score : scores) {
			sum += score;
		}

		return sum / scores.size();
	}

	public static int wordCount(String content) {
		String[] wordArray = content.split("\\s+");

		return wordArray.length;
	}

	// cleans text since files can't have certain characters
	public static String cleanText(String fix) {
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

		sigTermsLoadingLabel.setVisible(false);

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
		final JFreeChart chart = createPieChart(dataset);

		pieChartPanel = new ChartPanel(chart);

		getContentPane().add(pieChartPanel, gbc_pieChartPanel);
		getContentPane().revalidate();
		getContentPane().repaint();

		sectorLoadingLabel.setVisible(false);
	}

	private void pullDataFromDirectory() throws IOException {
		File folder = new File(MainFrame.GLOBALPATH + "cache\\" + symbol);
		File[] listOfFiles = folder.listFiles();
		if (!cacheNeeded(symbol)) {
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

		for (int row = 0; row < headLineTableModel.getRowCount(); row++) {
			File theFile = new File(
					theDirectory + File.separator + headLineTableModel.getValueAt(row, 1).toString() + ".txt");

			if (!theFile.exists()) {
				File fileDirectory = new File(MainFrame.GLOBALPATH + "cache\\" + symbol);

				for (File file : fileDirectory.listFiles())
					file.delete();

				return false;
			}
		}

		return true;
	}

	private Map<String, IntegerPair> returnSentencesFromPressRelease(String content, String word) {
		Map<String, IntegerPair> result = new HashMap<String, IntegerPair>();
		List<IntegerPair> positions = new ArrayList<IntegerPair>();
		BreakIterator border = BreakIterator.getSentenceInstance(Locale.US);

		border.setText(content);

		int start = border.first();

		for (int end = border.next(); end != BreakIterator.DONE; start = end, end = border.next()) {
			if (content.toLowerCase().substring(start, end).contains(word)) {
				if (start != 0) {
					positions.add(new IntegerPair(start, end));
				}
			}
		}

		Reader reader = new StringReader(content);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
			String sentenceString = Sentence.listToString(sentence);

			sentenceList.add(sentenceString.toString());
		}

		int sentenceCounter = 0; // to iterate the integerpair arraylist

		for (String sentence : sentenceList) {
			if (sentence.toLowerCase().contains(word)) {
				if (sentenceCounter < positions.size()) {
					int startPos = positions.get(sentenceCounter).returnStart();
					int endPos = positions.get(sentenceCounter).returnEnd();
					result.put(sentence, new IntegerPair(startPos, endPos));
				}

				sentenceCounter++;
			}
		}

		return result;
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
			RetrieveTwitter twitter = new RetrieveTwitter(symbol);

			String text = twitter.retrieveTweets();
			String singleResult = "";
			String fullResult = "";

			Properties props = new Properties();
			props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			Annotation annotation = pipeline.process(text);
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

			for (CoreMap sentence : sentences) {
				String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

				singleResult = sentiment + "\t" + sentence + '\n';
				fullResult += sentiment + "\t" + sentence + '\n';

				tweetSentimentAndContent.put(sentence.toString(), sentiment);
			}

			wordFrequency(fullResult, twitterPosLabel);

			final XYDataset dataset = createDatasetTwitter(tweetSentimentAndContent);
			JFreeChart chart = null;
			try {
				chart = createChartTwitter(dataset, twitter.tweetTimeAndContent);
				chart.setBackgroundPaint(new Color(255, 255, 255, 0));
				chart.setPadding(new RectangleInsets(10, 5, 5, 5));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			twitterChartPanel = new ChartPanel(chart);
			twitterLoadingLabel.setVisible(false);

			getContentPane().add(twitterChartPanel, gbc_twitterChartPanel);
			getContentPane().revalidate();
			getContentPane().repaint();

			numberOfTimesSeenTwitter();
		}
	};

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

	private XYDataset createDatasetTwitter(Map<String, String> tweetSentiment) {
		// sentence - sentement

		DefaultXYDataset result = new DefaultXYDataset();

		XYSeries series1 = new XYSeries("Words");

		int numberTweet = 0;

		for (String sentence : tweetSentiment.keySet()) {
			if (tweetSentiment.get(sentence).equals("Positive")) {
				series1.add(1, numberTweet);
			} else if (tweetSentiment.get(sentence).equals("Negative")) {
				series1.add(-1, numberTweet);
			} else if (tweetSentiment.get(sentence).equals("Neutral")) {
				series1.add(0, numberTweet);
			}

			numberTweet++;
		}

		result.addSeries(getTitle(), series1.toArray());

		return result;

	}

	private JFreeChart createChartTwitter(final XYDataset dataset, Map<Date, String> tweetDateAndContent)
			throws ParseException {

		String fromDate = "";
		String toDate = "";

		int x = 0;
		for (Date d : tweetDateAndContent.keySet()) {
			if (x == 0) {
				fromDate = d.toString();
			} else if (x == tweetDateAndContent.size() - 1) {
				toDate = d.toString();

			}

			x++;
		}

		final JFreeChart chart = ChartFactory.createXYLineChart(fromDate + " - " + toDate, // chart
																							// title
				"Sentiment", // x axis label
				"Y", // y axis label
				dataset, // data
				PlotOrientation.HORIZONTAL, true, // include legend
				true, // tooltips
				false // urls
		);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setForegroundAlpha(0.5f);

		SymbolAxis rangeAxis = new SymbolAxis("Time", listOfDates(tweetDateAndContent));

		rangeAxis.setTickUnit(new NumberTickUnit(1));
		rangeAxis.setRange(0, listOfDates(tweetDateAndContent).length);
		plot.setRangeAxis(rangeAxis);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);

		return chart;

	}

	public String returnTime(String fix) {
		String splitter[] = fix.split(" ");
		return splitter[3];
	}

	private String[] listOfDates(Map<Date, String> tweetDateAndContent) throws ParseException {
		ArrayList<String> result = new ArrayList<String>();

		for (Date d : tweetDateAndContent.keySet()) {
			SimpleDateFormat oldFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
			Date date = oldFormat.parse(d.toString());
			SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
			result.add(newFormat.format(date));
		}

		String[] stockArr = new String[result.size()];
		stockArr = result.toArray(stockArr);

		return stockArr;

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
		int negative = map.get("negative") != null ? map.get("negative") : 0;
		int positive = map.get("positive") != null ? map.get("positive") : 0;
		int neutral = map.get("neutral") != null ? map.get("neutral") : 0;

		twitterNegLabel.setText("Negative: " + negative + " ");
		twitterPosLabel.setText("Positive: " + positive + " ");
		twitterNeutLabel.setText("Neutral: " + neutral + " ");
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

		boolean cacheISNeeded = cacheNeeded(symbol);

		for (int i = 0; i < TopicIdentification.CATEGORIES.length; i++) {
			result.setValue(TopicIdentification.CATEGORIES[i],
					Math.abs(new Double(Math.abs(TopicIdentification.rankings.get(title)[i]))) / 1000);
		}

		return result;
	}

	private JFreeChart createPieChart(final PieDataset dataset) {

		final JFreeChart chart = ChartFactory.createPieChart3D(null, dataset, true, true, false);
		chart.setBackgroundPaint(new Color(255, 255, 255, 0));
		chart.setPadding(new RectangleInsets(10, 5, 5, 5));

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

		return dataset;

	}

	private JFreeChart createBarChart(final CategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createBarChart(MainFrame.lblCompanyName.getText().replace("Name: ", ""),
				"Category", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);

		chart.setBackgroundPaint(Color.white);
		chart.setBackgroundPaint(new Color(255, 255, 255, 0));
		chart.setPadding(new RectangleInsets(10, 5, 5, 5));

		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		final BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);

		final GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green, 0.0f, 0.0f, Color.lightGray);
		final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red, 0.0f, 0.0f, Color.lightGray);
		renderer.setSeriesPaint(1, gp1);
		renderer.setSeriesPaint(2, gp2);

		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		return chart;
	}

	// return if a word was seen based on frequency map
	private int wordExists(Map<String, String> content, String headline, String word) {
		int numberOfTimesSeen = 0;
		Map<String, Integer> result = new HashMap<String, Integer>();
		result = wordFrequency(newsHeadlineAndContent.get(headline).toLowerCase());

		try {
			numberOfTimesSeen = result.get(word);
		} catch (Exception e) {
		}

		if (numberOfTimesSeen > 0) {
			return result.get(word);
		}

		return 0;
	}

	private void numberOfTimesSeenPressRelease() {
		String[] search = informationTextField.getText().split(" ");
		informationTableModel.setRowCount(0);

		for (String headline : newsHeadlineAndContent.keySet()) {
			for (String word : search) {
				int numberOfTimesSeen = wordExists(newsHeadlineAndContent, headline, word);

				if (numberOfTimesSeen > 0) {
					informationTableModel.addRow(new Object[] { "Press Release", word, headline, numberOfTimesSeen,
							headLineTable.getModel().getValueAt(returnRowNumber(headline), 3) });
				}
			}
		}
	}

	private void numberOfTimesSeenTwitter() {
		String[] search = informationTextField.getText().split(" ");
		Map<String, Integer> result = new HashMap<String, Integer>();

		// number of times seen for tweets
		for (String tweet : tweetSentimentAndContent.keySet()) {
			for (String word : search) {
				result = wordFrequency(tweet);
				if (result.get(word) != null) {
					informationTableModel.addRow(new Object[] { "Tweet", word, tweet, result.get(word),
							tweetSentimentAndContent.get(tweet) });

				}
			}
		}
	}

	// returns the row that the headline is in
	private int returnRowNumber(String find) {
		for (int row = 0; row < headLineTable.getModel().getRowCount(); row++) {

			if (find.contains(headLineTable.getModel().getValueAt(row, 1).toString())) {

				return row;
			}
		}

		return 0;
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

		return myMap;
	}

	class HighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		public HighlightPainter(Color arg0) {
			super(arg0);
		}
	}
}
