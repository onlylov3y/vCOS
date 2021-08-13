package com.s3.core;

import java.io.File;
import java.util.Scanner;

public class FileReader {

	public String readFileAsString(String filePath) {
		String data = "";
		File setting = new File(filePath);
		if (setting.exists()) {
			try {
				Scanner myReader = new Scanner(setting);
				while (myReader.hasNextLine()) {
					data += myReader.nextLine();
				}
				myReader.close();
			} catch (Exception e) {
				System.err.println("Read file error: " + e.toString());
			}
		}
		return data;
	}
}
