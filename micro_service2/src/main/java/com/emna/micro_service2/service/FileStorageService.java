package com.emna.micro_service2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {


    @Value("${file.storage.location}")
    private String storageLocation;

    public String storeJsonFile(String jsonContent, String originalFileName) throws IOException {
        String jsonFileName = originalFileName.replace(".xsd", ".json");
        Path jsonFilePath = Paths.get(storageLocation, jsonFileName);
        Files.createDirectories(jsonFilePath.getParent());
        Files.write(jsonFilePath, jsonContent.getBytes());
        return jsonFilePath.toString();
    }


}
