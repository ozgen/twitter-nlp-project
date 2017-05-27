package com.nlp.TurkishProcessor;

public class Replacer {
public static String replaceAll(String oldStr){
		
		int numChar = oldStr.length();
		char[] charArray = new char[numChar];
		oldStr.getChars(0, numChar, charArray, 0);
		
		int i = 0;
		
		while (i < charArray.length) {
			switch (charArray[i]) {
			case 'ı':
				charArray[i] = 'i';
			case 'İ':
				charArray[i] = 'i';
				break;
			case 'ö':
				charArray[i] = 'o';
				break;
			case 'Ö':
				charArray[i] = 'o';
				break;
			case 'ü':
				charArray[i] = 'u';
				break;
			case 'Ü':
				charArray[i] = 'u';
				break;
			case 'ç':
				charArray[i] = 'c';
				break;
			case 'Ç':
				charArray[i] = 'c';
				break;
			case 'ğ':
				charArray[i] = 'g';
				break;
			case 'Ğ':
				charArray[i] = 'g';
				break;
			case 'ş':
				charArray[i] = 's';
				break;
			case 'Ş':
				charArray[i] = 's';
				break;
			default:
				break;
			}
			i++;
		}
		String newStr = new String(charArray);
		return newStr;
		
	}

}
