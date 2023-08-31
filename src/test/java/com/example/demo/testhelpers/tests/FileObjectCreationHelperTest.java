
package com.example.demo.testhelpers.tests;

import com.example.demo.testhelpers.helpers.FileObjectCreationHelper;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class FileObjectCreationHelperTest {
	
	@Test
	public void supportedFilesListIsNotEmptyTest() throws IOException {
		assertFalse(FileObjectCreationHelper.loadSupportedFiles().isEmpty());
	}
	
	@Test
	public void unsupportedFilesListIsNotEmptyTest() throws IOException {
		assertFalse(FileObjectCreationHelper.loadUnsupportedFiles().isEmpty());
	}
	
	@Test
	public void unsupportedTrueExtensionFilesListIsNotEmptyTest() 
			throws IOException {
		
		assertFalse(
			FileObjectCreationHelper.loadUnsupportedTrueExtensionFiles()
				.isEmpty()
		);
	}
    
    @Test
	public void unsupportedFakeExtensionFilesListIsNotEmptyTest() 
			throws IOException {
		
		assertFalse(
			FileObjectCreationHelper.loadUnsupportedFakeExtensionFiles()
				.isEmpty()
		);
	}
}
