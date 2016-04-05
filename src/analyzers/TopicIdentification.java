package analyzers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.classify.JointClassifier;
import com.aliasi.classify.JointClassifierEvaluator;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;

import main.MainFrame;

public class TopicIdentification {
	private static File TRAINING_DIR = new File("trainers/topic.identification");
	private static File TESTING_DIR = new File("cache");
	public static String[] CATEGORIES = { "financials", "technology", "utilities", "services", "industrial.goods",
			"health.care", "consumer.goods" };
	private static String[] symbol = new String[1];
	public static double[] PROBABILITIES;
	public static String BESTCATEGORY;
	public static final Map<String, double[]> rankings = new HashMap<String, double[]>();
	private static int NGRAM_SIZE = 6;

	public TopicIdentification(String s) throws ClassNotFoundException, IOException {
		symbol[0] = s;
		identifyTopic();
	};

	public static void identifyTopic() throws IOException, ClassNotFoundException {
		DynamicLMClassifier<NGramProcessLM> classifier = DynamicLMClassifier.createNGramProcess(CATEGORIES, NGRAM_SIZE);

		for (int i = 0; i < CATEGORIES.length; ++i) {
			File classDir = new File(TRAINING_DIR, CATEGORIES[i]);
			if (!classDir.isDirectory()) {
				String msg = "Doesn't work";
				throw new IllegalArgumentException(msg);
			}

			String[] trainingFiles = classDir.list();
			for (int j = 0; j < trainingFiles.length; ++j) {
				File file = new File(classDir, trainingFiles[j]);
				String text = Files.readFromFile(file, "ISO-8859-1");
				Classification classification = new Classification(CATEGORIES[i]);
				Classified<CharSequence> classified = new Classified<CharSequence>(text, classification);
				classifier.handle(classified);
			}
		}

		@SuppressWarnings("unchecked") // we created object so know it's safe
		JointClassifier<CharSequence> compiledClassifier = (JointClassifier<CharSequence>) AbstractExternalizable
				.compile(classifier);

		boolean storeCategories = true;
		JointClassifierEvaluator<CharSequence> evaluator = new JointClassifierEvaluator<CharSequence>(
				compiledClassifier, symbol, storeCategories);
		JointClassification jc = null;
		File classDir = new File(TESTING_DIR, symbol[0]);
		String[] testingFiles = classDir.list();
		ArrayList<Double> jointLogProbabilities = new ArrayList<Double>();
		for (int j = 0; j < testingFiles.length; ++j) {
			String text = Files.readFromFile(new File(classDir, testingFiles[j]), "ISO-8859-1");
			Classification classification = new Classification(symbol[0]);
			Classified<CharSequence> classified = new Classified<CharSequence>(text, classification);
			jc = compiledClassifier.classify(text);
			BESTCATEGORY = jc.bestCategory();
			jointLogProbabilities.add(jc.jointLog2Probability(j));

			PROBABILITIES = new double[jc.size()];
			for (int i = 0; i < jc.size(); i++) {
				PROBABILITIES[i] = jc.jointLog2Probability(i);
			}

			rankings.put(testingFiles[j], PROBABILITIES);
		}
	}
}