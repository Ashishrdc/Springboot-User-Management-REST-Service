package com.develop.assignmentPart2.exporter;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    // Creating a saveFile method to upload the image to a particular directory
    public static void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException{
        Path uploadPath = Paths.get(uploadDir);
        // setting up the upload path
        // checking if the specified directory exists
        // if not then create a new directory

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        // saving the file at the specified path
        // and replace if same file exists
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ioe){
            throw new IOException("Couldn't save image file " + fileName, ioe);
        }
    }
}
