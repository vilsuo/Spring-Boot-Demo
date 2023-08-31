
package com.example.demo.testhelpers.helpers;

import com.example.demo.domain.Account;
import com.example.demo.domain.FileObject;
import com.example.demo.utility.FileUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public final class FileObjectCreationHelper {
	
	private static final String SUPPORTED_FILES_PATH
		= "src/test/resources/supported";
	
	private static final String UNSUPPORTED_TRUE_EXTENSION_FILES_PATH
		= "src/test/resources/unsupported/true";
    
    private static final String UNSUPPORTED_FAKE_EXTENSION_FILES_PATH
		= "src/test/resources/unsupported/fake";
	
	public static List<MockMultipartFile> loadSupportedFiles() 
			throws IOException {
		
		return loadFiles(SUPPORTED_FILES_PATH);
	}
	
	public static List<MockMultipartFile> loadUnsupportedFiles() 
			throws IOException {
		
		return new ArrayList<>() {{
			addAll(loadUnsupportedTrueExtensionFiles());
			addAll(loadUnsupportedFakeExtensionFiles());
		}};
	}
	
	public static List<MockMultipartFile> loadUnsupportedTrueExtensionFiles() 
			throws IOException {
		
		return loadFiles(UNSUPPORTED_TRUE_EXTENSION_FILES_PATH);
	}
    
    public static List<MockMultipartFile> loadUnsupportedFakeExtensionFiles() 
			throws IOException {
		
		return loadFiles(UNSUPPORTED_FAKE_EXTENSION_FILES_PATH);
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
	
	public static void assertFileObjectIsCreatedFromAccountAndMultipartFile(
			final FileObject fileObject, final Account account, 
			final MultipartFile mpFile) {
		
		assertEquals(
			account, fileObject.getAccount(),
			"FileObject was created with " + account + ", but it "
			+ "has " + fileObject.getAccount()
		);
		
		assertEquals(
			mpFile.getName(), fileObject.getName(),
			"FileObject was created with filename '"
			+ mpFile.getName() + "', but it has filename '"
			+ fileObject.getName() + "'"
		);
		
		final String detectedMimeType
			= FileUtility.getRealMimeType(mpFile);
		
		assertEquals(
			detectedMimeType, fileObject.getMediaType(),
			"The detected media type of the created FileObject is '"
			+ detectedMimeType + "', but it has media type of '"
			+ fileObject.getMediaType() + "'"
		);
		
	}
	
	public static String fileObjectCreateInfo(
			final Account account, final MultipartFile file) {
		
		return "FileObject creation parameters with " + account + " and "
				+ getMultipartFileInfo(file);
	}
	
	private static String getMultipartFileInfo(final MultipartFile file) {
		if (file == null) { return "null MultipartFile"; }
		
		return "MultipartFile with filename: '" + file.getName()
				+ "', original filename: '" + file.getOriginalFilename()
				+ "', content type: '" + file.getContentType()
				+ "' and file size: " + file.getSize();
	}
}
