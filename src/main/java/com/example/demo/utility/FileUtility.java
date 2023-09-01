
package com.example.demo.utility;

import com.example.demo.domain.Privacy;
import java.io.IOException;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtility {
	
	public static final Privacy PLACEHOLDER_PRIVACY = Privacy.ALL;
	
	/**
	 * https://chidokun.github.io/2021/10/mime-type-and-upload-file-problem/en/#:~:text=Using%20the%20MIME%20Type%20defined%20by%20the%20User%2Dagent&text=So%20the%20MultipartFile%20class%20in,based%20on%20the%20MIME%20Type.
	 * 
	 * Determine the actual type of file uploaded by the user. Relying on a 
	 * client-defined MIME type may have some risks when users intentionally 
	 * change the file’s name and extension.
	 * 
	 * Apache Tika finds out the data format of a file based on several 
	 * criteria.
	 * 
	 * @param file 
	 * @return the actual mime type of the {@code file}
	 */
	public static String getRealMimeType(final MultipartFile file) {
        AutoDetectParser parser = new AutoDetectParser();
        Detector detector = parser.getDetector();
        try {
            Metadata metadata = new Metadata();
            TikaInputStream stream = TikaInputStream.get(file.getInputStream());
            MediaType mediaType = detector.detect(stream, metadata);
            
			return mediaType.toString();
			
        } catch (IOException e) {
            return MimeTypes.OCTET_STREAM;
        }
    }
}
