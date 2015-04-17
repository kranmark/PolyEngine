package kran.poly.util;

import java.io.*;

/**
 * Created by Mark on 2015-04-15.
 */
public class ResourceUtils {
	
	private ResourceUtils() {
	}
	
	public static String loadAsString(String resource) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceAsStream(resource)));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer + '\n');
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	public static InputStream getResourceAsStream(String resource) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
	}
	
}
