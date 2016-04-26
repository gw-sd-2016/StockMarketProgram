package tests;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import main.MainFrame;

public class MainFrameTest {
	private MainFrame main;

	@Before
	public void init() throws IOException, ParseException {
		main = new MainFrame();
	}

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void returnCalendarWithFormatTest() throws ParseException, IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date dates = formatter.parse("1994-08-15");
		Calendar volumeDate = Calendar.getInstance();
		volumeDate.setTime(dates);

		Calendar result = main.returnCalendarWithFormat("1994-08-15");

		assertEquals(result, volumeDate);
	}

	@Test
	public void getHeadLinesAndDatesTest() throws ParseException, IOException {
		Map<String, ArrayList<String>> expectedResult = new LinkedHashMap<String, ArrayList<String>>();
		ArrayList<String> titles = new ArrayList<String>();

		Document doc = Jsoup.parse(FileUtils.readFileToString(new File("testdocuments//HTMLDoc.txt")));

		titles.add(
				"Old Dominion Freight Line Announces LTL Tons Per Day and Revenue Per Hundredweight for January and February 2016");

		expectedResult.put("2016-03-02", titles);

		assertEquals(expectedResult, main.getHeadlinesAndDates(doc));
	}

	@Test
	public void changeDateFormatTest() {

	}

	@Test
	public void readFileTest() throws IOException {
		final File tempFile = tempFolder.newFile("temporaryFile.txt");

		FileUtils.writeStringToFile(tempFile, "this is a test");

		final String s = FileUtils.readFileToString(tempFile);

		assertEquals("this is a test", s);
	}
}
