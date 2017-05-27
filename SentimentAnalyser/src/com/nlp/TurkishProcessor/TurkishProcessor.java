package com.nlp.TurkishProcessor;

import java.util.ArrayList;
import java.util.List;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;

import com.nlp.io.File2Reader;
import com.nlp.io.File2Writer;
import com.nlp.model.WekaElement;






public class TurkishProcessor {
	
	private List<String> posAttributes;
	
	private List<String> negAttributes;
	
	private List<String> attributes;
	
	

	public ArrayList<Integer>countList = new ArrayList<Integer>();
	
	
	private void initialize(String posAttributesPath, String negAttirbutesPath, String attributesPath ) {
		
		File2Reader posAtrributesReader = new File2Reader(posAttributesPath);
		posAttributes = posAtrributesReader.readLines();
		
		File2Reader negAtrributesReader = new File2Reader(negAttirbutesPath);
		negAttributes = negAtrributesReader.readLines();
		
		File2Reader attributesReader = new File2Reader(negAttirbutesPath);
		attributes = attributesReader.readLines();
		
	}
	
	public boolean execute(String dataPath, String posAttributesPath, String negAttirbutesPath,String attributesPath,String outputWekaPath) {
		String tempFilePath = dataPath + ".tmp";
		
		initialize(posAttributesPath, negAttirbutesPath,attributesPath);
		processInputData(dataPath, tempFilePath);
		
		return generateWekaFile(tempFilePath, attributesPath, outputWekaPath);
	}
	
	public ArrayList<Integer> analayseInputData(String inputPath, String outputPath, String posAttributesPath, String negAttirbutesPath){
		
		File2Reader docReader = new File2Reader(inputPath);
		
		File2Reader posReader = new File2Reader(posAttributesPath);
		
		File2Reader negReader = new File2Reader(negAttirbutesPath);
	
		List<String> words = docReader.readLines();
		List<String> pos   = posReader.readLines();
		List<String> neg   = negReader.readLines();
		
		
		
		
		List<String> sentList = new ArrayList<String>();
		List<String> wordList = new ArrayList<String>();

		StringBuilder veri10K = new StringBuilder();
		StringBuilder onerilmeyen = new StringBuilder();
		StringBuilder notEkiAlanlar = new StringBuilder();
		List<String> sifatlarVeOlumsuzlar = new ArrayList<String>();
		Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());
		Replacer replacer = new Replacer();
		int i = 0;
		for (String dataLine : words) {
			i++;
			String[] splitted = dataLine.split("\",");
			
			String[] kelimeler= splitted[0].replaceAll("\"", " ").split(" ");
			veri10K.append("\"");
			for (String kelime : kelimeler) {
				
				kelime = replacer.replaceAll(kelime);
				
				//ZEMBEREK KULLANIMI
				if(!zemberek.kelimeDenetle(kelime)){
					onerilmeyen.append(kelime+System.getProperty("line.separator"));
					veri10K.append(kelime+" ");
				}else{
					Kelime[] cozumleme = zemberek.kelimeCozumle(kelime);

					if (cozumleme[0].toString().contains(" FIIL ")) {
						String fiil=cozumleme[0].toString().split("Kok: ")[1];
						fiil=fiil.split(", FIIL")[0];
						if (cozumleme[0].toString().contains("FIIL_OLUMSUZLUK_ME +")
								&!cozumleme[0].toString().contains("FIIL_ISTEK_E")
								&!cozumleme[0].toString().contains("FIIL_DONUSUM_ILI")) {
							veri10K.append("not_"+fiil+" ");
							if(!notEkiAlanlar.toString().contains("not_"+fiil)){
							notEkiAlanlar.append(kelime+" not_"+fiil);
							sifatlarVeOlumsuzlar.add("not_"+fiil);
							notEkiAlanlar.append(System.getProperty("line.separator"));
							}
						}else {
							veri10K.append(fiil+" ");
						}
					}else{
						String isim=cozumleme[0].toString().split("Kok: ")[1];
						isim=isim.split(", ")[0];
						veri10K.append(isim+" ");
						if (cozumleme[0].toString().contains("SIFAT") && !sifatlarVeOlumsuzlar.contains(isim)) {
							sifatlarVeOlumsuzlar.add(isim);
						}
					}
				}
				
			}
			
			veri10K.append("\",");

			veri10K.append(System.getProperty("line.separator"));
		
		}
			File2Writer writer1 = new File2Writer(outputPath+".zem");
		
		writer1.write(veri10K.toString(), false);

		File2Reader zemReader = new File2Reader(outputPath+".zem");
		
		 List<String> zemWords = zemReader.readLines();
		
		for (String string : zemWords) {
			string.split(" ");
			sentList.add(string);
			
			
		}
		
		
		StringBuilder builder = new StringBuilder();
		
		int count =0;
		for (int i1 = 0; i1 < sentList.size(); i1++) {
			count=0;
			String[]a = null;
			wordList = new ArrayList<String>();
			a =sentList.get(i1).split(" "); 
			for (int j = 0; j < a.length; j++) {
				wordList.add(a[j]);
			}
			for (int j = 0; j < wordList.size(); j++) {
				
				for (int j2 = 0; j2 < pos.size(); j2++) {
					if (wordList.get(j).equals(pos.get(j2))) {
						
						count = count+1;
						
					}
					
					for (int k = 0; k < neg.size(); k++) {
						
						if (wordList.get(j).equals(neg.get(k))) {
							count= count-1;
						}
						
					}
					
				}
			
				
				builder.append(wordList.get(j)+" ");
			}
				
			
			if (count>=0) {
				
				count=1;
				
			}else {
				count =-1;
			}
			
			countList.add(count);
			
			
			if (count>0) {
				builder.append(" olumlu");
			}else {
				
				builder.append(" olumsuz");

			}
			
			builder.append(System.getProperty("line.separator"));

			
		}
		

		
		
			File2Writer writer = new File2Writer(outputPath);
			
			writer.write(builder.toString(), false);
			
			
			
			return countList;
		
		
		
			
		
		
		
	}
	
	
	public void processInputData(String inputPath, String outputPath) {
		processWithZemberek(inputPath, outputPath);
	}
	
	public void processWithZemberek(String inputPath, String outputPath ){
		File2Reader docReader = new File2Reader(inputPath);
		List<String> dataList = docReader.readLines();
		
		List<String> sifatlarVeOlumsuzlar = new ArrayList<String>();
		
		StringBuilder veri10K = new StringBuilder();
		StringBuilder onerilmeyen = new StringBuilder();
		StringBuilder notEkiAlanlar = new StringBuilder();
		Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());
		int i=1;
		for (String dataLine : dataList) {
			i++;
			String[] splitted = dataLine.split("\",");
			
			String[] kelimeler= splitted[0].replaceAll("\"", " ").split(" ");
			veri10K.append("\"");
			for (String kelime : kelimeler) {
				//ZEMBEREK KULLANIMI
				if(!zemberek.kelimeDenetle(kelime)){//Kelime denetle önermiyorsa önerilmeyenlere ekle
					onerilmeyen.append(kelime+System.getProperty("line.separator"));
					veri10K.append(kelime+" ");
				}else{
					Kelime[] cozumleme = zemberek.kelimeCozumle(kelime);
//					System.out.println(cozumleme[0].toString());
					/* [ Kok: gel, FIIL ]  Ekler: FIIL_OLUMSUZLUK_ME + FIIL_MASTAR_MEK*/
					if (cozumleme[0].toString().contains(" FIIL ")) {
						String fiil=cozumleme[0].toString().split("Kok: ")[1];
						fiil=fiil.split(", FIIL")[0];
						if (cozumleme[0].toString().contains("FIIL_OLUMSUZLUK_ME +")
								&!cozumleme[0].toString().contains("FIIL_ISTEK_E")
								&!cozumleme[0].toString().contains("FIIL_DONUSUM_ILI")) {
							veri10K.append("not_"+fiil+" ");
							if(!notEkiAlanlar.toString().contains("not_"+fiil)){
							notEkiAlanlar.append(kelime+" not_"+fiil);
							sifatlarVeOlumsuzlar.add("not_"+fiil);
							notEkiAlanlar.append(System.getProperty("line.separator"));
							}
						}else {
							veri10K.append(fiil+" ");
						}
					}else{
						String isim=cozumleme[0].toString().split("Kok: ")[1];
						isim=isim.split(", ")[0];
						veri10K.append(isim+" ");
						if (cozumleme[0].toString().contains("SIFAT") && !sifatlarVeOlumsuzlar.contains(isim)) {
							sifatlarVeOlumsuzlar.add(isim);
						}
					}
				}
			}
			veri10K.append("\",");
			veri10K.append(splitted[1]);
			veri10K.append(System.getProperty("line.separator"));
		}
		File2Writer writer = new File2Writer(outputPath);
		
		writer.write(veri10K.toString(), false);
	}
	
	public boolean generateWekaFile(String dataPath, String attributesPath, String outputWekaPath) {
		File2Reader docReader = new File2Reader(dataPath);

		List<String> docs = docReader.readLines();
		List<WekaElement> elements = new ArrayList<WekaElement>();

		for (String doc : docs) {
			String[] splitted = doc.split("\",");
			if (splitted.length == 2) {
				WekaElement we = new WekaElement();

				we.setContent(splitted[0].substring(1));
				we.setTag(splitted[1]);
				elements.add(we);
			} else {
				System.err.println("Not splitted");
			}
		}
		
		File2Writer writer = new File2Writer(outputWekaPath);

		String arffContent = createArff(elements);
		writer.write(arffContent, false);
		
		return true;
	}

	private String createArff(List<WekaElement> docs) {
		StringBuilder sb = new StringBuilder();

		sb.append("@relation \"10K\"");
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));

		for (String attr : attributes) {
			sb.append("@attribute " + attr.replaceAll(" ", "_") + "		{0, 1}");
			sb.append(System.getProperty("line.separator"));
		}

		sb.append("@attribute \"class\" {olumlu,olumsuz,notr}");
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));

		sb.append("@data");
		sb.append(System.getProperty("line.separator"));
		int i=1;
		for (WekaElement doc : docs) {
			if(i%100 == 0){
			System.out.println(i);
			}
			String line = createVectorArray(doc.getContent(), doc.getTag());
			sb.append(line);
			sb.append(System.getProperty("line.separator"));
			i++;
		}

		return sb.toString();
	}
	
	private String createVectorArray(String doc, String tag) {
		String line = "";
		for (String attr : attributes) {
			if(attr.length()>5){
				if (Replacer.replaceAll(doc.toLowerCase()).contains(Replacer.replaceAll(attr.toLowerCase() ))) {
					line += 1;
				} else {
					line += 0;
				}
				line += ",";
			}else{
				if (Replacer.replaceAll(doc.toLowerCase()).contains(Replacer.replaceAll(attr.toLowerCase()))) {
					line += 1;
				} else {
					line += 0;
				}
				line += ",";
			}

		}
		line += tag;

		return line;
	}
	
	public static void main (String args[])
	{
		
		
		
	}
	

}
