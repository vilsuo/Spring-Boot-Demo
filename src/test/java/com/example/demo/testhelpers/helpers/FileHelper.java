
package com.example.demo.testhelpers.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;

public final class FileHelper {
	
	private static final String SUPPORTED_FILES_PATH
		= "src/test/resources/supported";
	
	private static final String UNSUPPORTED_FILES_PATH
		= "src/test/resources/unsupported";
	
	public static List<MockMultipartFile> loadSupportedFiles() 
			throws IOException {
		
		return loadFiles(SUPPORTED_FILES_PATH);
	}
	
	public static List<MockMultipartFile> loadUnsupportedFiles() 
			throws IOException {
		
		return loadFiles(UNSUPPORTED_FILES_PATH);
	}
	
	private static List<MockMultipartFile> loadFiles(
			final String location) throws IOException {
		
		return Files.walk(Paths.get(location))
			.filter(Files::isRegularFile)
			.map(path -> {
				try {
					return convertFile(path.toFile());
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}).toList();
	}
	
	private static MockMultipartFile convertFile(final File file) 
			throws FileNotFoundException, IOException {
		
		return new MockMultipartFile(
			file.getName(),
			IOUtils.toByteArray(new FileInputStream(file))
		);
	}
}
