package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

}
