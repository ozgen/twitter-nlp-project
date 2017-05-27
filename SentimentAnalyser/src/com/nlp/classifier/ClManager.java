package com.nlp.classifier;




public class ClManager {
	
	public String classify(String arffPath, String classifierName) {
		WekaClassifier wnbc = new WekaClassifier(arffPath, classifierName);
		String results = null;

		try {
			results = wnbc.trainClassifierCrossValidate();
		} catch (Exception e) {
			results = e.toString();
		}
		return results;
	}

}
