package com.nlp.io;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.nlp.TurkishProcessor.Replacer;
import com.nlp.result.ResultAn;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;




public class File2Reader {
	
	String fileName;
	int startLine;
	int endLine;
	
	public File2Reader(String fileName, int startLine, int endLine) {
		super();
		this.fileName = fileName;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	
	public File2Reader(String fileName) {
		super();
		this.fileName = fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	public List<String> read() {
		List<String> dataList = null;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			dataList = new ArrayList<String>();
			String strLine;
			int line = 1;
	
			while ((strLine = br.readLine()) != null) {
				if (line >= startLine && line <= endLine) {
					
					dataList.add(strLine.replaceAll(" ", "//"));
				
				} else if (line > endLine) {
					break;
				}
				line++;
			}
			System.out.println(fileName + " Total line:" + line);

			in.close();
			fstream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataList;

	}
	
	
	public List<String> readLines() {
		List<String> dataList = null;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			dataList = new ArrayList<String>();
			String strLine;
			while ((strLine = br.readLine()) != null) {
				dataList.add(strLine);
			}

			in.close();
			fstream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataList;
	}
	
	public String readfiletoString() {
		StringBuilder dataList = new StringBuilder();
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				dataList.append(strLine+" ");
			}

			in.close();
			fstream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataList.toString();
	}

	public List<String> readLinesLimited() {
		List<String> dataList = null;
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			int counter = 0;
			dataList = new ArrayList<String>();
			String strLine;
			while ((strLine = br.readLine()) != null && counter < endLine) {
				dataList.add(strLine);
				counter++;
			}

			in.close();
			fstream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataList;
	}
	


	public static void main(String[] args) {
	File2Reader reader = new File2Reader("/Users/Ozgen/Desktop/NLP/dataset/ozgen.txt");
	
	File2Reader reader2 = new File2Reader("/Users/Ozgen/Desktop/NLP/dataset/pos.txt");
	
	File2Reader reader3 = new File2Reader("/Users/Ozgen/Desktop/NLP/dataset/neg.txt");
	
	    
		List<String> words = reader.readLines();
		List<String> pos   = reader2.readLines();
		List<String> neg   = reader3.readLines();
		
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
				if(!zemberek.kelimeDenetle(kelime)){//Kelime denetle önermiyorsa önerilmeyenlere ekle
					onerilmeyen.append(kelime+System.getProperty("line.separator"));
					veri10K.append(kelime+" ");
				}else{
					Kelime[] cozumleme = zemberek.kelimeCozumle(kelime);
//					System.out.println(cozumleme[0].toString());
				//	 [ Kok: gel, FIIL ]  Ekler: FIIL_OLUMSUZLUK_ME + FIIL_MASTAR_MEK
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
			File2Writer writer1 = new File2Writer("/Users/Ozgen/Desktop/NLP/dataset/cs2.txt");
		
		writer1.write(veri10K.toString(), false);
	
		
		
		
		
		
		
		
		
		
		
		//System.out.println(neg.get(101));
		ArrayList<Integer>countList = new ArrayList<Integer>();
		
		
		List<String> sentList = new ArrayList<String>();
		List<String> wordList = new ArrayList<String>();
		File2Reader reader4 = new File2Reader("/Users/Ozgen/Desktop/NLP/dataset/cs2.txt");
		List<String> zwords = reader4.readLines();

		for (String string : zwords) {
			string.split(" ");
			sentList.add(string);
			//System.out.println(string.trim().toLowerCase(new Locale("tr")));
			
		}
		
		//System.out.println(list.get(3));
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
			//System.out.println(count);
			countList.add(count);
			if (count>0) {
				builder.append(" olumlu");
			}else {
				
				builder.append(" olumsuz");

			}
			
			builder.append(System.getProperty("line.separator"));

			
		}
		
			
		
			File2Writer writer = new File2Writer("/Users/Ozgen/Desktop/NLP/dataset/cs3.txt");
			
			writer.write(builder.toString(), false);
			
			ResultAn an = new ResultAn();
			String a = an.resultAnayse(countList);
			System.out.println(a);
			
			

			
			
			
		}
	}


