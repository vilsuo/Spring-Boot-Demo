
package com.example.demo.testhelpers.tests;

import com.example.demo.testhelpers.helpers.FileHelper;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

public class FileHelperTest {
	
	@Test
	public void supportedFilesListIsNotEmptyTest() throws IOException {
		assertFalse(FileHelper.loadSupportedFiles().isEmpty());
	}
	
	@Test
	public void unsupportedFilesListIsNotEmptyTest() throws IOException {
		assertFalse(FileHelper.loadUnsupportedFiles().isEmpty());
	}
}
