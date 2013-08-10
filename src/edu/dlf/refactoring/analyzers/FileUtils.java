package edu.dlf.refactoring.analyzers;

import java.io.File;
import java.io.FileInputStream;

public class FileUtils {

	public static String readAll(String path) throws Exception {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
		return new String(data, "UTF-8");
	}
}
