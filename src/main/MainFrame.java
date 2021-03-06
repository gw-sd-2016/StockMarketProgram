package main;

import java.awt.BasicStroke;
import java.awt.Color;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
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
import helpers.CircleDrawer;
import main.menu.AboutMenu;
import main.news.AnalyzeNews;
import objects.AnnotationPosition;
import objects.GeneralData;
import objects.VolumeDate;
import popupmessages.CheckInternet;

public class MainFrame implements ActionListener, KeyListener {
	private static final Logger logger = Logger.getLogger(MainFrame.class);
	private static JFrame frame;
	private static JPanel contentPane;
	private static ArrayList<VolumeDate> volumeDataDate = new ArrayList<VolumeDate>();
	private static JTextField searchBox;
	private static GridBagConstraints gbc_mainChartPanel;
	private static Map<String, ArrayList<String>> headlinesAndDates;
	private static JFreeChart mainChart;
	public static ChartPanel mainChartPanel;
	public static Map<String, AnnotationPosition> annotationPositions = new HashMap<String, AnnotationPosition>();
	private JMenuBar menuBar;
	private JMenuItem menuApplication;
	private JMenuItem menuAppAbout;
	private JMenuItem menuAppFeedback;
	private JMenuItem menuAppExit;
	private JButton btnNews;
	private JLabel lblCompanyName;
	private JLabel lblDate;
	private JLabel lblPressReleasesToBeAn;
	private JLabel lblAvgDailyVolume;
	private JLabel lblDayRange;
	private JLabel lblExchange;
	private String symbol;

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		BasicConfigurator.configure();

		logger.info("Application starting");

		com.jtattoo.plaf.acryl.AcrylLookAndFeel.setTheme("Large-Font", "", "");
		UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");

		try {
			MainFrame main = new MainFrame();
			main.createAndShowGUI();
		} catch (Exception e) {
			new CheckInternet();
		}
	};

	public void createAndShowGUI() throws IOException {
		frame = new JFrame("Investor PAL");
		frame.setBounds(100, 100, 1147, 399);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);

		frame.setLocation(x, y);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/taskbarlogo.png"));

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		menuApplication = new JMenu("Investor PAL");
		menuApplication.setFont(new Font("Copperplate Gothic Light", Font.PLAIN, 14));

		menuAppAbout = new JMenuItem("About");
		menuAppAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new AboutMenu();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
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
		menuApplication.add(menuAppFeedback);
		menuApplication.add(menuAppExit);
		menuBar.add(menuApplication);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 430, 210, 278, 383, 195, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 2, 266, 266, 239, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };

		contentPane.setLayout(gbl_contentPane);

		btnNews = new JButton("News");
		GridBagConstraints gbc_btnNews = new GridBagConstraints();
		gbc_btnNews.anchor = GridBagConstraints.WEST;
		gbc_btnNews.insets = new Insets(0, 0, 5, 5);
		gbc_btnNews.gridx = 0;
		gbc_btnNews.gridy = 0;
		contentPane.add(btnNews, gbc_btnNews);

		btnNews.setEnabled(false);
		btnNews.addActionListener(this);

		lblDate = new JLabel("");
		lblDate.setFont((new Font("Copperplate Gothic Light", Font.PLAIN, 14)));
		GridBagConstraints gbc_lblDate = new GridBagConstraints();
		gbc_lblDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblDate.gridx = 3;
		gbc_lblDate.gridy = 0;

		contentPane.add(lblDate, gbc_lblDate);

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

		gbc_mainChartPanel = new GridBagConstraints();
		gbc_mainChartPanel.gridwidth = 4;
		gbc_mainChartPanel.gridheight = 3;
		gbc_mainChartPanel.insets = new Insets(0, 0, 5, 5);
		gbc_mainChartPanel.fill = GridBagConstraints.BOTH;
		gbc_mainChartPanel.gridx = 0;
		gbc_mainChartPanel.gridy = 2;

		frame.setVisible(true);
	}

	class TimeKeeper implements Runnable {
		@Override
		public void run() {
			while (true) {
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat("MMMM dd, YYYY  -  h:mm:ss a");

				lblDate.setText(String.format("%" + 10 + "s", dateFormat.format(cal.getTime())));
			}
		}
	}

	private static IntervalXYDataset createDataset() {
		TimeSeries localTimeSeries = new TimeSeries("Volume");

		for (int i = 0; i < volumeDataDate.size(); i++) {
			int day = volumeDataDate.get(i).getDay();
			int year = volumeDataDate.get(i).getYear();
			int month = volumeDataDate.get(i).getMonth();
			double volume = volumeDataDate.get(i).getVolume();

			localTimeSeries.add(new Day(day, month, year), volume);
		}

		return new TimeSeriesCollection(localTimeSeries);
	}

	private JFreeChart createChart(XYDataset priceData) throws IOException, ParseException {
		logger.info("Creating date volume chart");
		priceData = createDataset();

		String title = "";

		mainChart = ChartFactory.createTimeSeriesChart(title, "Date", "Volume", priceData, true, true, false);
		mainChart.setBackgroundPaint(new Color(255, 255, 255, 0));
		mainChart.setPadding(new RectangleInsets(10, 5, 5, 5));

		XYPlot plot = (XYPlot) mainChart.getPlot();

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setLowerMargin(0);

		DecimalFormat format = new DecimalFormat("###,###");
		rangeAxis.setNumberFormatOverride(format);

		XYItemRenderer renderer1 = plot.getRenderer();
		renderer1.setBaseToolTipGenerator(
				new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
						new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));

		retrieveNews();

		for (String date : headlinesAndDates.keySet()) {
			ArrayList<String> headline = headlinesAndDates.get(date);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Calendar volumeDate = Calendar.getInstance();

			try {
				Date dates = formatter.parse(date);
				volumeDate = Calendar.getInstance();
				volumeDate.setTime(dates);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < volumeDataDate.size(); i++) {
				if (volumeDataDate.get(i).getCalendar().equals(volumeDate)) {
					String formattedDate = changeDateFormat(date);
					double volume = volumeDataDate.get(i).getVolume();
					long time = volumeDataDate.get(i).getCalendar().getTimeInMillis();

					final XYPointerAnnotation headLineAnnotation = new XYPointerAnnotation(formattedDate, time, volume,
							3);

					XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
					r.setSeriesShape(0, ShapeUtilities.createDiamond(1));
					r.setSeriesShapesVisible(0, true);

					String listString = "";

					for (String s : headline) {
						listString += "- " + s + "<br>";
					}

					int dismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
					dismissDelay = Integer.MAX_VALUE;

					ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);

					headLineAnnotation.setToolTipText("<html>" + listString + "</html>");
					headLineAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
					plot.addAnnotation(headLineAnnotation);

					annotationPositions.put(date,
							new AnnotationPosition(headLineAnnotation.getX(), headLineAnnotation.getY()));

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
		final XYPointerAnnotation movementAnnotation = new XYPointerAnnotation(movement, x, y, 3);

		XYPlot plot = (XYPlot) mainChart.getPlot();
		plot.clearAnnotations();

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
		plot.addAnnotation(movementAnnotation);
		plot.addAnnotation(point);

		BufferedImage mainChartImage = mainChart.createBufferedImage(panelWidth, panelHeight);

		addAnnotationsBackAndRemoveMarkers(plot, point, movementAnnotation);

		return mainChartImage;
	}

	public class VolumeHistory {
		public XYDataset dataset;
		public XYDataset priceData;
		public JFreeChart chart;
		private Crosshair xCrosshair;
		private Crosshair yCrosshair;

		public VolumeHistory() throws IOException, ParseException {
			symbol = MainFrame.searchBox.getText();
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

			contentPane.add(mainChartPanel, gbc_mainChartPanel);
		}

		public void getData() throws IOException {
			volumeDataDate.clear();

			Calendar today = Calendar.getInstance();

			int todayDay = today.get(Calendar.DAY_OF_WEEK);
			int todayMonth = today.get(Calendar.MONTH) + 1;
			int todayYear = today.get(Calendar.YEAR);

			final URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + symbol + "&a=" + todayMonth + "&b="
					+ todayDay + "&c=" + (todayYear - 1) + "&d=" + todayMonth + "&e=" + todayDay + "&f=" + todayYear
					+ "&g=d&ignore=.csv");
			final Reader reader = new InputStreamReader(new BOMInputStream(url.openStream()), "UTF-8");
			final CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());

			try {
				for (final CSVRecord record : parser) {
					String date = record.get("Date");
					String volume = record.get("Volume");
					String open = record.get("Open");
					String close = record.get("Close");
					String high = record.get("High");
					String low = record.get("Low");

					volumeDataDate.add(new VolumeDate(returnCalendarWithFormat(date), Double.parseDouble(volume),
							Double.parseDouble(open), Double.parseDouble(close), Double.parseDouble(high),
							Double.parseDouble(low)));
				}
			} finally {
				parser.close();
				reader.close();
			}

			logger.info("Finished fetching historical data");
		}
	}

	private static void addAnnotationsBackAndRemoveMarkers(XYPlot plot, XYAnnotation point,
			XYPointerAnnotation movementAnnotation) {
		XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
		r.setSeriesShape(0, ShapeUtilities.createDiamond(1));
		r.setSeriesShapesVisible(0, true);

		int dismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
		dismissDelay = Integer.MAX_VALUE;

		ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);

		for (String annotationDate : annotationPositions.keySet()) {
			XYPointerAnnotation headLineAnnotation;
			String headlineList = "";

			try {
				headLineAnnotation = new XYPointerAnnotation(changeDateFormat(annotationDate),
						annotationPositions.get(annotationDate).returnPositionX(),
						annotationPositions.get(annotationDate).returnPositionY(), 3);

				for (String name : headlinesAndDates.get(annotationDate)) {
					headlineList += "- " + name + "<br>";
				}

				headLineAnnotation.setToolTipText("<html>" + headlineList + "</html>");
				headLineAnnotation.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
				plot.addAnnotation(headLineAnnotation);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		plot.clearDomainMarkers();
		plot.clearRangeMarkers();
		plot.removeAnnotation(point);
		plot.removeAnnotation(movementAnnotation);
	}

	public Calendar returnCalendarWithFormat(String date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date dates = formatter.parse(date);
			Calendar volumeDate = Calendar.getInstance();
			volumeDate.setTime(dates);

			return volumeDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void retrieveNews() throws IOException, ParseException {
		Document doc = Jsoup
				.connect("http://finance.yahoo.com/q/p?s=" + MainFrame.searchBox.getText() + "+Press+Releases").get();

		getHeadlinesAndDates(doc);
	}

	private void getGeneralData() throws IOException {
		URL generalData = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + searchBox.getText() + "&f=mnxa2");

		final Reader reader = new InputStreamReader(new BOMInputStream(generalData.openStream()), "UTF-8");
		final CSVParser parser = new CSVParser(reader,
				CSVFormat.EXCEL.withHeader("Range", "CompanyName", "Exchange", "AvgVolume"));

		try {

			for (final CSVRecord record : parser) {
				String range = record.get("Range");
				String companyName = record.get("CompanyName");
				String exchange = record.get("Exchange");
				String avgVolume = record.get("AvgVolume");

				GeneralData generalDataObject = new GeneralData(range, companyName, exchange, avgVolume);

				lblDayRange.setText(String.format("Day Range: %s", generalDataObject.getRange()));
				lblCompanyName.setText(String.format("Name: %s", generalDataObject.getCompanyName()));
				lblExchange.setText(String.format("Exchange: %s", generalDataObject.getExchange()));
				lblAvgDailyVolume.setText(String.format("Avg. Daily Volume: %s", generalDataObject.getAvgVolume()));
				lblPressReleasesToBeAn.setText(String.format("PRs Analyzed: %s", headlinesAndDates.size()));
			}
		} finally {
			parser.close();
			reader.close();
		}
	}

	public Map<String, ArrayList<String>> getHeadlinesAndDates(Document doc) throws ParseException {

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

	public static String changeDateFormat(String dateString) throws ParseException {
		SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = oldFormat.parse(dateString);
		SimpleDateFormat newFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
		newFormat.format(date);

		return newFormat.format(date).toString();
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

				btnNews.setEnabled(true);
			} catch (IOException e1) {

				new CheckInternet();
			} catch (ParseException e) {

				new CheckInternet();
			}
		} else if (arg0.getActionCommand() == "News") {
			try {
				AnalyzeNews newsObject = new AnalyzeNews(symbol, lblCompanyName.getText(), volumeDataDate,
						headlinesAndDates);
				newsObject.createAndShowGUI();

				btnNews.setEnabled(false);
			} catch (IOException | ParseException e) {

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

				btnNews.setEnabled(true);

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
}
