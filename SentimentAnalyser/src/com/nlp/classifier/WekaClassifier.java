package com.nlp.classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;



public class WekaClassifier {
	
	private static String arffFile;
	private static String algorithm;
	private static Classifier classifier = null;
	private static Instances trainData;
	private static Random rnd = new Random();

	public WekaClassifier(String arffFile, String algorithm) {
		WekaClassifier.arffFile = arffFile;
		WekaClassifier.algorithm = algorithm;
		if (algorithm.equals("Naive Bayes"))
			classifier = new NaiveBayes();
		if (algorithm.equals("Random Forest"))
			classifier = new RandomForest();
		if (algorithm.equals("Support Vector Machine"))
			classifier = new SMO();
	}

	public Instances getTrainData() {
		return trainData.stringFreeStructure();
	}

	public String trainClassifierCrossValidate() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(arffFile));
		trainData = new Instances(reader);
		reader.close();

		trainData.setClassIndex(trainData.numAttributes() - 1);
		classifier.buildClassifier(trainData);

		Evaluation eval = new Evaluation(trainData);
		eval.crossValidateModel(classifier, trainData, 10, rnd);

		StringBuilder results = new StringBuilder();
		results.append("File: " + arffFile);
		results.append(System.getProperty("line.separator"));
		results.append("Algorithm: " + algorithm);
		results.append(System.getProperty("line.separator"));
		results.append(System.getProperty("line.separator"));
		results.append(eval.toSummaryString("--- Summary ---\n", false));

		return results.toString();
	}

}
