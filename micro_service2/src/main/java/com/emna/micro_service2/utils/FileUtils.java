package com.emna.micro_service2.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static void saveToFile(String directoryPath, String fileName, String content) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directoryPath + File.separator + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}