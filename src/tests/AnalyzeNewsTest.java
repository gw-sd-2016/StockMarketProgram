package tests;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import main.news.AnalyzeNews;
import objects.PressRelease;

public class AnalyzeNewsTest {

	@Test
	public void returnAverageScoreTest() throws ParseException, IOException {

		ArrayList<Double> scores = new ArrayList<Double>();
		scores.add(4.0);
		scores.add(5.0);
		scores.add(3.0);

		double result = AnalyzeNews.averageScore(scores);

		assertEquals(result, 4.0, 1e-15);
	}

	@Test
	public void returnCleanedTextTest() throws ParseException, IOException {
		String result = AnalyzeNews.cleanText("@#$test&&");

		assertEquals(result, "test");
	}

	@Test
	public void wordCountTest() throws IOException {
		String test = "This is a test sentence in order to get the word count.";

		int result = AnalyzeNews.wordCount(test);

		assertEquals(result, 12);
	}

	@Test
	public void calculateAnnotationOffsetTest() {
		int numberOfTweets = 300;

		int result = AnalyzeNews.calculateAnnotationOffset(numberOfTweets);

		assertEquals(result, 270);
	}

	@Test
	public void removeStopWordsTest() throws IOException {
		String test = "this is a stop word test and it should remove it.";

		String result = AnalyzeNews.removeStopWords(test);

		assertEquals(result, "stop word test remove ");
	}

	@Test
	public void extractMessageLinksTest() throws IOException {
		ArrayList<String> expectedResult = new ArrayList<String>();

		Document doc = Jsoup.parse(FileUtils.readFileToString(new File("testdocuments//HTMLDoc.txt")));

		expectedResult.add("http://finance.yahoo.com/news/old-dominion-freight-line-announces-164500860.html");

		assertEquals(expectedResult, AnalyzeNews.extractMessageLinks(doc));
	}

	@Test
	public void predictionScoreTest() throws IOException {
		DecimalFormat df = new DecimalFormat("####0.00");

		double sentimentScore = 1.0;
		int timesSeen = 10;
		int wordCountWithoutStopWords = 200;

		double result = AnalyzeNews.predictionScore(sentimentScore, timesSeen, wordCountWithoutStopWords);

		assertEquals(Double.parseDouble(df.format(result)), 33.33, 1e-15);
	}

	@Test
	public void returnValidFileNameTest() throws IOException {
		String title = "Shareholder Rights Law Firm Johnson Weaver LLP Initiates Investigations of Chipotle Mexican Grill Inc. Navient Corporation Brixmor Property Group Inc. and The Boeing Company Encourages Investors to Contact the Firm with any questions.";
		String result = "Shareholder Rights Law Firm Johnson Weaver LLP Initiates Investigations of Chipotle Mexican Grill Inc. Navient Corporation Brixmor Property Group Inc. and The Boeing Company Encourages Investors to Contact the";

		assertEquals(AnalyzeNews.returnValidFileName(title), result);
	}

	@Test
	public void returnPressReleaseGivenTitleTest() throws IOException, ParseException {
		Date today = new Date();
		PressRelease one = new PressRelease("IP just released", "This is the content here.", today);
		PressRelease two = new PressRelease("IP just released again", "This is the content here.", today);
		PressRelease three = new PressRelease("IP just released again again", "This is the content here.", today);
		PressRelease four = new PressRelease("IP just released again again again", "This is the content here.", today);

		ArrayList<PressRelease> list = new ArrayList<PressRelease>();
		list.add(one);
		list.add(two);
		list.add(three);
		list.add(four);

		assertEquals(one, AnalyzeNews.returnPressReleaseGivenTitle("IP just released", list));
	}

	@Test
	public void returnDateGivenHeadlineTest() throws ParseException {
		File f = new File("Title one.txt");
		Map<String, ArrayList<String>> headlinesAndDates = new HashMap<String, ArrayList<String>>();
		ArrayList<String> headlines = new ArrayList<String>();

		headlines.add("Title one");
		headlines.add("Title two");
		headlines.add("Title three");
		headlines.add("Title four");
		headlines.add("Title five");

		Calendar cal = Calendar.getInstance();
		cal.set(2016, 11, 11, 0, 0, 0);

		Date theDate = cal.getTime();

		headlinesAndDates.put("2016-12-11", headlines);

		assertEquals(AnalyzeNews.returnDateGivenHeadline(f, headlinesAndDates).toString(), theDate.toString());
	}
}
