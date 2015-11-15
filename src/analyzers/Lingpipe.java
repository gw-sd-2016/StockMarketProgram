package analyzers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import com.aliasi.lm.TokenizedLM;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Files;
import com.aliasi.util.ScoredObject;

public class Lingpipe {
	private static int NGRAM = 3;
	private static int MIN_COUNT = 5;
	private static int NGRAM_REPORTING_LENGTH = 2;
	private static int MAX_COUNT = 100;
	private static File BACKGROUND_DIR;
	private static File FOREGROUND_DIR;
	public static final Map<Double, String> training = new HashMap<Double, String>();
	public static final Map<Double, String> newterms = new HashMap<Double, String>();

	public Lingpipe(File f) throws IOException {

		BACKGROUND_DIR = f;
		FOREGROUND_DIR = f;

		TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
		TokenizedLM backgroundModel = buildModel(tokenizerFactory, NGRAM, BACKGROUND_DIR);

		backgroundModel.sequenceCounter().prune(3);

		SortedSet<ScoredObject<String[]>> coll = backgroundModel.collocationSet(NGRAM_REPORTING_LENGTH, MIN_COUNT,
				MAX_COUNT);

		reportForCollocations(coll);

		TokenizedLM foregroundModel = buildModel(tokenizerFactory, NGRAM, FOREGROUND_DIR);
		foregroundModel.sequenceCounter().prune(3);

		SortedSet<ScoredObject<String[]>> newTerms = foregroundModel.newTermSet(NGRAM_REPORTING_LENGTH, MIN_COUNT,
				MAX_COUNT, backgroundModel);

		reportForNewTerms(newTerms);

		System.out.println("\nDone.");
	}

	private static TokenizedLM buildModel(TokenizerFactory tokenizerFactory, int ngram, File directory)
			throws IOException {

		String[] trainingFiles = directory.list();
		TokenizedLM model = new TokenizedLM(tokenizerFactory, ngram);

		for (int j = 0; j < trainingFiles.length; ++j) {
			String text = Files.readFromFile(new File(directory, trainingFiles[j]), "ISO-8859-1");
			model.handle(text);
		}
		
		return model;
	}

	private static void reportForCollocations(SortedSet<ScoredObject<String[]>> nGrams) {
		for (ScoredObject<String[]> nGram : nGrams) {
			double score = nGram.score();
			String[] toks = nGram.getObject();
			report_filterColl(score, toks);
		}
	}

	private static void reportForNewTerms(SortedSet<ScoredObject<String[]>> nGrams) {
		for (ScoredObject<String[]> nGram : nGrams) {
			double score = nGram.score();
			String[] toks = nGram.getObject();
			report_filterNew(score, toks);
		}
	}

	private static void report_filterColl(double score, String[] toks) {
		String accum = "";
		for (int j = 0; j < toks.length; ++j) {
			if (nonCapWord(toks[j]))
				return;
			accum += " " + toks[j];
		}

		training.put(score, accum);

	}

	private static void report_filterNew(double score, String[] toks) {
		String accum = "";
		for (int j = 0; j < toks.length; ++j) {
			if (nonCapWord(toks[j]))
				return;
			accum += " " + toks[j];
		}

		newterms.put(score, accum);

	}

	private static boolean nonCapWord(String tok) {
		if (!Character.isUpperCase(tok.charAt(0)))
			return true;
		for (int i = 1; i < tok.length(); ++i)
			if (!Character.isLowerCase(tok.charAt(i)))
				return true;
		return false;
	}

}
