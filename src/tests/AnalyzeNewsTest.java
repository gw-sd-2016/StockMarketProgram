package tests;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import main.news.AnalyzeNews;

public class AnalyzeNewsTest {

	@Test
	public void returnAverageListTest() throws ParseException, IOException {
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
}
