package analyzers;

import java.io.File;
import java.io.IOException;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

import main.MainFrame;

public class SentimentAnalysis {

	File mPolarityDir;
	File otherPolarityDir;
	String symbol;
	String[] mCategories;
	DynamicLMClassifier<NGramProcessLM> mClassifier;

	public SentimentAnalysis(String s) throws ClassNotFoundException, IOException {
		mPolarityDir = new File(MainFrame.GLOBALPATH + "trainers\\sentiment.analysis");
		otherPolarityDir = new File(MainFrame.GLOBALPATH + "cache\\");
		System.out.println("\nData Directory=" + mPolarityDir);
		mCategories = mPolarityDir.list();
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
		symbol = s;
		run();
	}

	void run() throws ClassNotFoundException, IOException {
		train();
		evaluate();
	}

	void train() throws IOException {
		int numTrainingCases = 0;
		int numTrainingChars = 0;
		System.out.println("\nTraining.");
		for (int i = 0; i < mCategories.length; ++i) {
			String category = mCategories[i];
			Classification classification = new Classification(category);
			File file = new File(mPolarityDir, mCategories[i]);
			File[] trainFiles = file.listFiles();
			for (int j = 0; j < trainFiles.length; ++j) {
				File trainFile = trainFiles[j];

				++numTrainingCases;
				String review = Files.readFromFile(trainFile, "ISO-8859-1");
				numTrainingChars += review.length();
				Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
				mClassifier.handle(classified);
			}
		}

		System.out.println("  # Training Cases=" + numTrainingCases);
		System.out.println("  # Training Chars=" + numTrainingChars);
	}

	void evaluate() throws IOException {
		System.out.println("\nEvaluating.");
		int numTests = 0;
		int numCorrect = 0;
		for (int i = 0; i < mCategories.length; ++i) {
			String category = mCategories[i];
			File file = new File(otherPolarityDir, symbol);
			File[] trainFiles = file.listFiles();
			for (int j = 0; j < trainFiles.length; ++j) {
				File trainFile = trainFiles[j];

				String review = Files.readFromFile(trainFile, "ISO-8859-1");
				++numTests;
				Classification classification = mClassifier.classify(review);
				if (classification.bestCategory().equals(category))
					System.out.println(trainFile.getName() + classification.bestCategory());
				++numCorrect;
			}

		}
		System.out.println("  # Test Cases=" + numTests);
		System.out.println("  # Correct=" + numCorrect);
		System.out.println("  % Correct=" + ((double) numCorrect) / (double) numTests);
	}
}
