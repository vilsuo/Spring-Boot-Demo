
package com.example.demo.utility;

import com.example.demo.testhelpers.helpers.FileObjectCreationHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class FileUtilityTest {
	
	private static List<MockMultipartFile> trueExtensionFiles;
	private static List<MockMultipartFile> fakeExtensionFiles;
	
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		trueExtensionFiles = new ArrayList<>() {{
			addAll(FileObjectCreationHelper.loadSupportedFiles());
			addAll(FileObjectCreationHelper.loadUnsupportedTrueExtensionFiles());
		}};
		
		fakeExtensionFiles = FileObjectCreationHelper.loadUnsupportedFakeExtensionFiles();
	}
	
	@Test
	public void detectsMatchingMimeTypeFromRealExtensionTest() {
		for (final MockMultipartFile file : trueExtensionFiles) {
			final String mimeType
				= getFileMimeTypeBasedOnFileExtension(file.getName());
			
			final String detectedTrueMimeType
				= FileUtility.getRealMimeType(file);
			
			assertTrue(
				mimeType.equalsIgnoreCase(detectedTrueMimeType),
				"Expected the MIME type of file '" + file.getName() + "' to be "
				+ "' " + mimeType + "', but it was detected to be '"
				+ detectedTrueMimeType + "'"
			);
		}
	}
	
	@Test
	public void detectsRealMimeTypeFromFakeExtensionTest() {
		for (final MockMultipartFile file : fakeExtensionFiles) {
			final String mimeType
				= getFileMimeTypeBasedOnFileExtension(file.getName());
			
			final String detectedTrueMimeType
				= FileUtility.getRealMimeType(file);
			
			assertFalse(
				mimeType.equalsIgnoreCase(detectedTrueMimeType),
				"Expected the MIME type of file '" + file.getName() + "' NOT "
				+ "to be ' " + mimeType + "'"
			);
		}
	}
	
	/**
	 * CALL ONLY ON FILES WHERE THE EXTENSION MATCHES THE MIME TYPE!
	 * 
	 * @param filename
	 * @return 
	 */
	private String getFileMimeTypeBasedOnFileExtension(final String filename) {
		final String[] parts = filename.split("\\.");
		final String extension = parts[parts.length - 1];
		
		switch (extension) {
			case "gif" -> { return "image/gif"; }
			case "jpg" -> { return "image/jpeg"; }
			case "png" -> { return "image/png"; }
			case "svg" -> { return "image/svg+xml"; }
			case "txt" -> { return "text/plain"; }
			
			default -> {
				throw new IllegalArgumentException(
					"Could not detect MIME type from filename: '"
					+ filename + "'"
				);
			}
		}
	}
}
