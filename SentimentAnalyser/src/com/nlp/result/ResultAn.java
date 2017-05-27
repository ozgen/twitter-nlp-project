package com.nlp.result;

import java.util.ArrayList;



public class ResultAn {

public String resultAnayse(ArrayList<Integer>cList){
		
		int pos = 0;
		int neg = 0;
		int c = 0;
		
		for (int i = 0; i < cList.size(); i++) {
			
			
			if (cList.get(i)>0) {
				pos = pos+1;
			}else {
				neg = neg+1;
			}
			c++;
		}
		StringBuilder builder = new StringBuilder();
		
		builder.append("---Result---");
		
		builder.append(System.getProperty("line.separator"));
		
		builder.append("Total Tweets Number:"+c);
		
		builder.append(System.getProperty("line.separator"));
		
		int posPer = (pos*100/c);
		
		builder.append("Positive Sentiment = "+posPer+"%"+ "    Tweets Number = "+pos);
		
		builder.append(System.getProperty("line.separator"));
		
		int negPer = (neg*100/c);
		
		builder.append("Negative Sentiment = "+negPer+"%"+"    Tweets Number = "+pos);
		
		
		
		
		
		return builder.toString();
	} 

}
