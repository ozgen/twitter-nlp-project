package com.nlp.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class File2Writer {

	String fileName;

	public File2Writer(String fileName) {
		super();
		this.fileName = fileName;
	}

	public void write(String line) {
		try {
			FileWriter fstream = new FileWriter(fileName, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.append(line);
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void write(String line, boolean append) {
		try {
			FileWriter fstream = new FileWriter(fileName, append);
			BufferedWriter out = new BufferedWriter(fstream);
			out.append(line);
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public boolean deleteFile(String path) {
		File f = new File(path);
		
		boolean success = f.delete();
		
		return success;
		
	}
}
