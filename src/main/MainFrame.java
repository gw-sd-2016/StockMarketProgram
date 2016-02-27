package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import helpers.AnnotationPosition;
import helpers.CircleDrawer;
import helpers.IStringHelper;
import main.menu.AboutMenu;
import main.news.AnalyzeNews;
import popupmessages.CheckInternet;

public class MainFrame extends JFrame implements ActionListener, KeyListener, IStringHelper {
	public static String GLOBALPATH = "C:\\git\\StockMarketProgram\\";
	private static final Logger logger = Logger.getLogger(MainFrame.class);
	private static JPanel contentPane;
	private JMenuBar menuBar;
	private JMenuItem menuApplication;
	private JMenuItem menuAppAbout;
	private JMenuItem menuAppFeedback;
	private JMenuItem menuAppMasterList;
	private JMenuItem menuAppExit;
	public static ArrayList<Double> volumeDataNumber = new ArrayList<Double>();
	public static ArrayList<String> volumeDataDate = new ArrayList<String>();
	public static JLabel lblDate;
	public static JLabel labelCompany;
	private JLabel lblTime;
	public static JTextField searchBox;
	private Date date;
	private DateFormat timeFormat;
	private DateFormat dateFormat;
	public static ChartPanel mainChartPanel;
	public static GridBagConstraints gbc_chartPanel;
	public static Map<String, String> newsHeadLines = new HashMap<String, String>();
	public static Map<String, ArrayList<String>> headlinesAndDates;
	public static Map<String, AnnotationPosition> annotationPositions = new HashMap<String, AnnotationPosition>();
	public static JLabel lblCompanyName;
	public JLabel lblPressReleasesToBeAn;
	public JLabel lblAvgDailyVolume;
	public JLabel lblDayRange;
	public JLabel lblExchange;
	public static JFreeChart mainChart;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		BasicConfigurator.configure();

		logger.info("Application starting");

		UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");

		try {
			MainFrame main = new MainFrame();
			main.setTitle("Stock Analyzer");
		} catch (Exception e) {
			new CheckInternet();
		}
	};

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public MainFrame() throws IOException {
		setBounds(100, 100, 1147, 399);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);

		menuBar = new JMenuBar();
		// Set this instance as the application's menu bar
		setJMenuBar(menuBar);
		menuApplication = new JMenu("Stock Analyzer");
		menuApplication.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 14));
		menuAppMasterList = new JMenuItem("Market List");
		menuAppMasterList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

			}

		});

		menuAppAbout = new JMenuItem("About");
		menuAppAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutMenu();
			}

		});

		menuAppFeedback = new JMenuItem("Send Feedback");
		menuAppFeedback.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		menuAppExit = new JMenuItem("Exit");
		menuAppExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menuApplication.add(menuAppAbout);
		menuApplication.add(menuAppMasterList);
		menuApplication.add(menuAppFeedback);
		menuApplication.add(menuAppExit);
		menuBar.add(menuApplication);

		lblTime = new JLabel("Time");
		menuBar.add(lblTime);
		lblTime.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 14));

		lblDate = new JLabel("Date");
		menuBar.add(lblDate);
		lblDate.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 14));

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 430, 210, 278, 383, 195, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 2, 266, 266, 239, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JButton btnNews = new JButton("News");
		GridBagConstraints gbc_btnNews = new GridBagConstraints();
		gbc_btnNews.anchor = GridBagConstraints.WEST;
		gbc_btnNews.insets = new Insets(0, 0, 5, 5);
		gbc_btnNews.gridx = 0;
		gbc_btnNews.gridy = 0;
		contentPane.add(btnNews, gbc_btnNews);
		btnNews.addActionListener(this);

		JPanel dashboardPanel = new JPanel();
		GridBagConstraints gbc_dashboardPanel = new GridBagConstraints();
		gbc_dashboardPanel.gridheight = 5;
		gbc_dashboardPanel.fill = GridBagConstraints.BOTH;
		gbc_dashboardPanel.gridx = 4;
		gbc_dashboardPanel.gridy = 0;
		contentPane.add(dashboardPanel, gbc_dashboardPanel);
		GridBagLayout gbl_dashboardPanel = new GridBagLayout();
		gbl_dashboardPanel.columnWidths = new int[] { 296, 0 };
		gbl_dashboardPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 31, 0, 21, 0, 31, 0, 0, 0, 0, 0, 56, 22, 56, 0 };
		gbl_dashboardPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_dashboardPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		dashboardPanel.setLayout(gbl_dashboardPanel);

		labelCompany = new JLabel("");
		GridBagConstraints gbc_labelCompany = new GridBagConstraints();
		gbc_labelCompany.insets = new Insets(0, 0, 5, 0);
		gbc_labelCompany.gridx = 0;
		gbc_labelCompany.gridy = 1;
		dashboardPanel.add(labelCompany, gbc_labelCompany);

		searchBox = new JTextField();
		GridBagConstraints gbc_searchBox = new GridBagConstraints();
		gbc_searchBox.insets = new Insets(0, 0, 5, 0);
		gbc_searchBox.fill = GridBagConstraints.BOTH;
		gbc_searchBox.gridx = 0;
		gbc_searchBox.gridy = 2;
		dashboardPanel.add(searchBox, gbc_searchBox);
		searchBox.setColumns(10);
		searchBox.addKeyListener(this);

		JButton go = new JButton("GO");
		GridBagConstraints gbc_go = new GridBagConstraints();
		gbc_go.anchor = GridBagConstraints.EAST;
		gbc_go.insets = new Insets(0, 0, 5, 0);
		gbc_go.gridx = 0;
		gbc_go.gridy = 3;
		dashboardPanel.add(go, gbc_go);

		lblCompanyName = new JLabel("Name:");
		GridBagConstraints gbc_lblCompanyName = new GridBagConstraints();
		gbc_lblCompanyName.anchor = GridBagConstraints.WEST;
		gbc_lblCompanyName.insets = new Insets(0, 0, 5, 0);
		gbc_lblCompanyName.gridx = 0;
		gbc_lblCompanyName.gridy = 6;
		dashboardPanel.add(lblCompanyName, gbc_lblCompanyName);

		lblPressReleasesToBeAn = new JLabel("PRs Analyzed:");
		GridBagConstraints gbc_lblPressReleasesToBeAn = new GridBagConstraints();
		gbc_lblPressReleasesToBeAn.anchor = GridBagConstraints.WEST;
		gbc_lblPressReleasesToBeAn.insets = new Insets(0, 0, 5, 0);
		gbc_lblPressReleasesToBeAn.gridx = 0;
		gbc_lblPressReleasesToBeAn.gridy = 7;
		dashboardPanel.add(lblPressReleasesToBeAn, gbc_lblPressReleasesToBeAn);

		lblAvgDailyVolume = new JLabel("Avg. Daily Volume:");
		GridBagConstraints gbc_lblAvgDailyVolume = new GridBagConstraints();
		gbc_lblAvgDailyVolume.anchor = GridBagConstraints.WEST;
		gbc_lblAvgDailyVolume.insets = new Insets(0, 0, 5, 0);
		gbc_lblAvgDailyVolume.gridx = 0;
		gbc_lblAvgDailyVolume.gridy = 8;
		dashboardPanel.add(lblAvgDailyVolume, gbc_lblAvgDailyVolume);

		lblDayRange = new JLabel("Day Range:");
		GridBagConstraints gbc_lblDayRange = new GridBagConstraints();
		gbc_lblDayRange.anchor = GridBagConstraints.WEST;
		gbc_lblDayRange.insets = new Insets(0, 0, 5, 0);
		gbc_lblDayRange.gridx = 0;
		gbc_lblDayRange.gridy = 9;
		dashboardPanel.add(lblDayRange, gbc_lblDayRange);

		lblExchange = new JLabel("Exchange:");
		GridBagConstraints gbc_lblExchange = new GridBagConstraints();
		gbc_lblExchange.anchor = GridBagConstraints.WEST;
		gbc_lblExchange.insets = new Insets(0, 0, 5, 0);
		gbc_lblExchange.gridx = 0;
		gbc_lblExchange.gridy = 10;
		dashboardPanel.add(lblExchange, gbc_lblExchange);
		go.addActionListener(this);

		Thread time = new Thread(new TimeKeeper());
		time.start();

		gbc_chartPanel = new GridBagConstraints();
		gbc_chartPanel.gridwidth = 4;
		gbc_chartPanel.gridheight = 3;
		gbc_chartPanel.insets = new Insets(0, 0, 5, 5);
		gbc_chartPanel.fill = GridBagConstraints.BOTH;
		gbc_chartPanel.gridx = 0;
		gbc_chartPanel.gridy = 2;

		setVisible(true);
	}

	// keeps time and formats nicely
	class TimeKeeper implements Runnable {

		@Override
		public void run() {
			while (true) {
				dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				timeFormat = new SimpleDateFormat("HH:mm:ss");
				date = new Date();
				lblDate.setText(dateFormat.format(date));

				lblTime.setText(
						"                                                                                                     "
								+ timeFormat.format(date) + "     |     ");

			}
		}
	}

	private static IntervalXYDataset createDataset() {
		TimeSeries localTimeSeries = new TimeSeries("Volume");

		for (int i = 0; i < volumeDataDate.size(); i++) {
			localTimeSeries.add(new Day(returnDay(volumeDataDate.get(i)), returnMonth(volumeDataDate.get(i)),
					returnYear(volumeDataDate.get(i))), volumeDataNumber.get(i));
		}

		return new TimeSeriesCollection(localTimeSeries);
	}

	private static int returnYear(String data) {

		String[] array = data.split("-");

		int year = Integer.parseInt(array[0]);
		return year;
	}

	private static int returnDay(String data) {

		String[] array = data.split("-");

		int day = Integer.parseInt(array[2]);
		return day;
	}

	private static int returnMonth(String data) {
		String[] array = data.split("-");

		int month = Integer.parseInt(array[1]);
		return month;
	}

	private JFreeChart createChart(XYDataset priceData) throws IOException, ParseException {
		logger.info("Creating date volume chart");
		priceData = createDataset();

		String title = "";
		mainChart = ChartFactory.createTimeSeriesChart(title, "Date", "Volume", priceData, true, true, false);
		mainChart.setBackgroundPaint(new Color(255, 255, 255, 0));
		mainChart.setPadding(new RectangleInsets(10, 5, 5, 5));

		XYPlot plot = (XYPlot) mainChart.getPlot();
		NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis();
		rangeAxis1.setLowerMargin(0);
		DecimalFormat format = new DecimalFormat("###,###");
		rangeAxis1.setNumberFormatOverride(format);

		XYItemRenderer renderer1 = plot.getRenderer();
		renderer1.setBaseToolTipGenerator(
				new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
						new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));

		retrieveNews();

		for (String date : headlinesAndDates.keySet()) {
			ArrayList<String> headline = headlinesAndDates.get(date);

			for (int i = 0; i < volumeDataDate.size(); i++) {
				if (volumeDataDate.get(i).equals(date)) {
					final Hour h = new Hour(2,
							new Day(returnHeadLineDay(date), returnHeadLineMonth(date), returnHeadLineYear(date)));
					final Minute m = new Minute(10, h);
					double firstMillisecond = m.getFirstMillisecond();
					final XYPointerAnnotation headLineAnnotation = new XYPointerAnnotation(volumeDataDate.get(i),
							firstMillisecond, volumeDataNumber.get(i), 3);

					XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
					r.setSeriesShape(0, ShapeUtilities.createDiamond(1));
					r.setSeriesShapesVisible(0, true);

					String listString = "";

					for (String s : headline) {
						listString += "- " + s + "<br>";
					}

					int dismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
					// Keep the tool tip showing
					dismissDelay = Integer.MAX_VALUE;
					ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);

					// use html tags to break tooltiptext into separate
					// lines
					headLineAnnotation.setToolTipText("<html>" + listString + "</html>");
					headLineAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
					plot.addAnnotation(headLineAnnotation);

					annotationPositions.put(headLineAnnotation.getText(),
							new AnnotationPosition(headLineAnnotation.getX(), headLineAnnotation.getY()));

					System.out.println(headLineAnnotation.getText() + " - " + headLineAnnotation.getX() + " and "
							+ headLineAnnotation.getY());

				}
			}
		}

		logger.info("Finished creating date volume chart");

		return mainChart;
	}

	public static BufferedImage returnChartImageAndResize(String movement, double x, double y, int panelWidth,
			int panelHeight, String prDate) {
		final CircleDrawer cd = new CircleDrawer(Color.BLUE, new BasicStroke(1.0f));
		final XYAnnotation point = new XYDrawableAnnotation(x, y, 15, 15, cd);

		XYPlot plot = (XYPlot) mainChart.getPlot();

		final XYPointerAnnotation headLineAnnotation = new XYPointerAnnotation(movement, x, y, 3);
		plot.clearAnnotations();
		plot.clearDomainMarkers();
		plot.clearRangeMarkers();
		plot.addAnnotation(point);

		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;

		try {
			date = oldFormat.parse(prDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar startDate = Calendar.getInstance();
		startDate.setTime(date);

		Calendar endDate = Calendar.getInstance();
		endDate.setTime(date);
		endDate.add(Calendar.DATE, 4);

		Marker target = new IntervalMarker((double) startDate.getTimeInMillis(), (double) endDate.getTimeInMillis());
		target.setPaint(Color.YELLOW);
		plot.addDomainMarker(target, org.jfree.ui.Layer.BACKGROUND);
		plot.addAnnotation(headLineAnnotation);

		BufferedImage mainChartImage = mainChart.createBufferedImage(panelWidth, panelHeight);

		return mainChartImage;
	}

	public class VolumeHistory {
		public XYDataset dataset;
		public XYDataset priceData;
		public JFreeChart chart;
		private Crosshair xCrosshair;
		private Crosshair yCrosshair;
		private String symbol = MainFrame.searchBox.getText();

		public VolumeHistory() throws IOException, ParseException {
			getData();
			dataset = createDataset();
			chart = createChart(priceData);
			priceData = createDataset();
			mainChartPanel = new ChartPanel(chart);

			CrosshairOverlay crosshairOverlay = new CrosshairOverlay();

			mainChartPanel.addChartMouseListener(new ChartMouseListener() {

				@Override
				public void chartMouseClicked(ChartMouseEvent arg0) {

				}

				@Override
				public void chartMouseMoved(ChartMouseEvent event) {
					Rectangle2D dataArea = mainChartPanel.getScreenDataArea();
					JFreeChart chart = event.getChart();
					XYPlot plot = (XYPlot) chart.getPlot();
					ValueAxis xAxis = plot.getDomainAxis();
					double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, RectangleEdge.BOTTOM);
					double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
					xCrosshair.setValue(x);
					yCrosshair.setValue(y);

				}
			});

			xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
			xCrosshair.setLabelVisible(false);
			yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
			yCrosshair.setLabelVisible(true);
			crosshairOverlay.addDomainCrosshair(xCrosshair);
			crosshairOverlay.addRangeCrosshair(yCrosshair);
			mainChartPanel.addOverlay(crosshairOverlay);

			contentPane.add(mainChartPanel, gbc_chartPanel);
		}

		public void getData() throws IOException {
			volumeDataDate.clear();
			volumeDataNumber.clear();

			long totalVolume = 0;
			long currentVolumeValue = 0;

			String date = MainFrame.lblDate.getText();
			/*
			 * datesplitter[0] = month datesplitter[1] = day datesplitter[2] =
			 * year must convert from string to integer, subtract the year then
			 * back to string
			 */
			String datesplitter[] = date.split("/");
			int yearBefore = Integer.parseInt(datesplitter[2]);
			yearBefore = yearBefore - 1;
			int monthBefore = Integer.parseInt(datesplitter[0]);
			monthBefore = monthBefore - 1;

			logger.info("Starting to fetch historical data");
			URL historical = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + symbol + "&a="
					+ String.valueOf(monthBefore) + "&b=" + dateFix(datesplitter[1]) + "&c="
					+ String.valueOf(yearBefore) + "&d=" + datesplitter[0] + "&e=" + dateFix(datesplitter[1]) + "&f="
					+ datesplitter[2] + "&g=d&ignore=.csv");

			BufferedReader hisdata = new BufferedReader(new InputStreamReader(historical.openStream()));
			String inputLine2;
			String vol;

			URL currentVolume = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + symbol + "&f=v");
			BufferedReader curVol = new BufferedReader(new InputStreamReader(currentVolume.openStream()));

			while ((vol = curVol.readLine()) != null) {
				currentVolumeValue = Integer.parseInt(vol);
			}

			while ((inputLine2 = hisdata.readLine()) != null) {
				String splitter[] = inputLine2.split(",");
				if (splitter[5].contains("Volume")) {

				} else {
					// splitter[0] is the date
					volumeDataDate.add(splitter[0]);
					volumeDataNumber.add(Double.parseDouble(splitter[5]));
				}
			}

			logger.info("Finished fetching historical data");
		}

		// removes the beginning 0 in order to request csv for historical date
		// properly
		private String dateFix(String fixable) {
			if (fixable.charAt(0) == '0') {

				return fixable.replace("0", "");
			}

			return fixable;
		}
	}

	private void retrieveNews() throws IOException, ParseException {
		Document doc = Jsoup
				.connect("http://finance.yahoo.com/q/p?s=" + MainFrame.searchBox.getText() + "+Press+Releases").get();

		getHeadlinesAndDates(doc);

	}

	private void getGeneralData() throws IOException {
		URL generalData = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + searchBox.getText() + "&f=mnxa2");
		BufferedReader readData = new BufferedReader(new InputStreamReader(generalData.openStream()));

		String inputLine[] = readData.readLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

		lblPressReleasesToBeAn.setText("PRs Analyzed: " + headlinesAndDates.size());
		lblDayRange.setText("Day Range: " + removeQuotes(inputLine[0]));
		lblCompanyName.setText("Name: " + removeQuotes(inputLine[1]));
		lblExchange.setText("Exchange: " + removeQuotes(inputLine[2]));

		double dailyAmount = Double.parseDouble(removeQuotes(inputLine[3]));
		DecimalFormat format = new DecimalFormat("#,###");

		lblAvgDailyVolume.setText("Avg. Daily Volume: " + removeQuotes(format.format(dailyAmount)));

	}

	private Map<String, ArrayList<String>> getHeadlinesAndDates(Document doc) throws ParseException {

		ArrayList<String> messageTitles = null;
		Elements newsDates = doc.select("div.mod.yfi_quote_headline.withsky > h3"); // 28

		headlinesAndDates = new LinkedHashMap<String, ArrayList<String>>();

		for (int i = 0; i < newsDates.size(); i++) {
			Element e = newsDates.get(i);
			Element nextSib = e.nextElementSibling();
			Elements divs = nextSib.select("a");
			messageTitles = new ArrayList<String>();

			for (int j = 0; j < divs.size(); j++) {
				Element d = divs.get(j);
				messageTitles.add(d.text());
			}

			SimpleDateFormat oldFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
			Date date = oldFormat.parse(e.text());
			SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

			headlinesAndDates.put(newFormat.format(date), messageTitles);
		}

		return headlinesAndDates;
	}

	public static String[] splitDates(String s) {
		int index = 0;
		for (int i = 0; i < 3; i++)
			index = s.indexOf(",", index + 1);

		return new String[] { s.substring(0, index), s.substring(index + 1) };
	}

	public static int returnHeadLineYear(String xlo) throws ParseException {
		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = oldFormat.parse(xlo);
		SimpleDateFormat newFormat = new SimpleDateFormat("yyyy");

		return Integer.parseInt(newFormat.format(date));
	}

	public static int returnHeadLineDay(String xlo) throws ParseException {
		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = oldFormat.parse(xlo);
		SimpleDateFormat newFormat = new SimpleDateFormat("d");

		return Integer.parseInt(newFormat.format(date));
	}

	public static int returnHeadLineMonth(String xlo) throws ParseException {
		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = oldFormat.parse(xlo);
		SimpleDateFormat newFormat = new SimpleDateFormat("MM");

		return Integer.parseInt(newFormat.format(date));
	}

	// for the breakout board list - changing the colors
	private static class MyListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (value.toString().contains("(" + MainFrame.searchBox.getText().toUpperCase() + ")")) {
				c.setBackground(Color.GRAY.brighter());
				c.setForeground(Color.MAGENTA);

			} else {

			}

			return c;
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand() == "GO") {
			try {
				if (contentPane.getComponentCount() != 0) {
					if (mainChartPanel != null) {
						contentPane.remove(mainChartPanel);
					}
				}

				new VolumeHistory();

				getGeneralData();

			} catch (IOException e1) {

				new CheckInternet();
			} catch (ParseException e) {

				new CheckInternet();
			}
		} else if (arg0.getActionCommand() == "News") {
			try {

				new AnalyzeNews();
			} catch (IOException e) {

				new CheckInternet();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				if (contentPane.getComponentCount() != 0) {
					if (mainChartPanel != null) {
						contentPane.remove(mainChartPanel);
					}
				}

				new VolumeHistory();
				getGeneralData();

			} catch (IOException e1) {

				new CheckInternet();
			} catch (ParseException e) {

				new CheckInternet();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {

	}

	@Override
	public String removeQuotes(String fixable) {
		return fixable.replaceAll("\"", "");
	}

	@Override
	public String removeDash(String fixable) {
		String fix[] = fixable.split(" ");
		return fix[0] + "         " + fix[2];
	}

	@Override
	public String removeSignAndPercent(String fixable) {
		if (fixable.contains("-")) {

			return fixable.replace("-", "").replace("%", "");

		} else if (fixable.contains("+")) {

			return fixable.replace("+", "").replace("%", "");

		}

		return null;
	}

	public static String extractBbName(String x) {
		String newX = x.trim();
		newX = newX.replaceAll("\"", "");

		String[] splitter = newX.split("title=");
		String[] splitter2 = splitter[1].split("href");

		return splitter2[0].trim();

	}
}