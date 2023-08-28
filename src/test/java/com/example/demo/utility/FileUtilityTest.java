
package com.example.demo.utility;

import com.example.demo.testhelpers.helpers.FileHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/*
TODO
- create packages for fake- supported & unsupported files
*/
public class FileUtilityTest {
	
	private static List<MockMultipartFile> supportedFiles
		= new ArrayList<>();
	
	@BeforeAll
	public static void initFiles() throws IOException, FileNotFoundException {
		supportedFiles = FileHelper.loadSupportedFiles();
	}
	
	@Test
	public void detectsMatchingMimeTypeFromRealExtensionTest() {
		for (final MockMultipartFile file : supportedFiles) {
			final String mimeType = getMimeBasedOnFileExtension(file.getName());
			
			assertTrue(
				mimeType.equalsIgnoreCase(FileUtility.getRealMimeType(file))
			);
		}
	}
	
	/*
	@Test
	public void detectsRealMimeTypeFromFakeExtensionTest() {
		for (final MockMultipartFile file : supportedFiles) {
			final String mimeType = getMimeBasedOnFilename(file.getName());
		}
	}
	*/
	
	/**
	 * CALL ONLY ON FILES WHERE THE EXTENSION MATCHES THE MIME TYPE!
	 * 
	 * @param filename
	 * @return 
	 */
	private String getMimeBasedOnFileExtension(final String filename) {
		final String[] parts = filename.split("\\.");
		final String extension = parts[parts.length - 1];
		
		switch (extension) {
			case "gif" -> { return "image/gif"; }
			case "jpg" -> { return "image/jpeg"; }
			case "png" -> { return "image/png"; }
			
			default -> {
				throw new IllegalArgumentException(
					"Could not detect MIME type from filename: '"
					+ filename + "'"
				);
			}
		}
	}
}
