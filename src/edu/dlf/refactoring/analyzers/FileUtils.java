package edu.dlf.refactoring.analyzers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;
import fj.data.List;

public class FileUtils {

	private static final Random randomGenerator = new Random();
	private static final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public static final String desktop = "/home/xige/Desktop/";
	
	public static final F<String, String> readAllFunc = new F<String, String>() {
		@Override
		public String f(String path) {
			try {
				File file = new File(path);
				FileInputStream fis;
				fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();
				return new String(data, "UTF-8");
			}catch (Exception e) {
				logger.fatal(e);
				return "";
	}}}; 
	
	public static final F<String, List<String>> getSubDirectories = 
		new F<String, List<String>>() {
		@Override
		public List<String> f(String directory) {
			List<String> results = List.nil();
			DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
		        @Override
		        public boolean accept(Path file) throws IOException {
		            return (Files.isDirectory(file));
	        }};
		    Path dir = FileSystems.getDefault().getPath(directory);
		    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir,
		            filter)) {
		        for (Path path : stream) {
		        	results = results.snoc(path.toFile().getAbsolutePath());
		        }
		    } catch (IOException e){
		    	logger.fatal("Get sub-directory:" + e);
		    }
			return results;
	}};
	
	public static final F<Integer, Integer> generateRandomInteger = 
		new F<Integer, Integer>() {
		@Override
		public Integer f(Integer max) {
			return randomGenerator.nextInt(max);
	}};
}
