package com.emna.micro_service2.controller;

import com.emna.micro_service2.service.FileStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/config")
/*@CrossOrigin(origins = "http://localhost:4200")*/
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.storage.location}")
    private String storageLocation;

    @Autowired
    private ServletContext context;

    @PostMapping(value = "/upload-xsd", headers = "content-type=multipart/*", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JsonNode> upload(
            @RequestParam("file") MultipartFile inputFile,
            @RequestParam("indexConfigurationId") String indexConfigurationId) {

        System.out.println("Received file upload request with filename: " + inputFile.getOriginalFilename());

        if (!inputFile.isEmpty()) {
            try {
                // === Step 1: Convert the XSD file to JSON ===
                XmlMapper xmlMapper = new XmlMapper();
                JsonNode jsonNode = xmlMapper.readTree(inputFile.getInputStream());
                System.out.println("Successfully converted XSD to JSON");

                // === Step 2: Save the JSON file to disk ===
                String jsonFilename = indexConfigurationId + ".json";
                System.out.println("JSON filename: " + jsonFilename);
                File directory = new File(storageLocation);

                if (!directory.exists()) {
                    directory.mkdirs();
                    System.out.println("Directory created: " + storageLocation);
                }

                File jsonFile = new File(storageLocation + File.separator + jsonFilename);
                System.out.println("Destination file path: " + jsonFile.getAbsolutePath());

                Files.write(Paths.get(jsonFile.getAbsolutePath()), jsonNode.toString().getBytes());
                System.out.println("JSON file saved successfully");

                // === Step 3: Return the JSON content in response ===
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", "application/json");
                return new ResponseEntity<>(jsonNode, headers, HttpStatus.OK);
            } catch (Exception e) {
                System.err.println("Error processing the file: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            System.err.println("File is empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get-attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> getAttributes(@RequestParam("filename") String filename) {

        try {
            String jsonFilePath = storageLocation + File.separator + filename;
            File jsonFile = new File(jsonFilePath);

            if (!jsonFile.exists()) {
                System.err.println("File not found: " + jsonFilePath);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            System.out.println("Successfully read JSON file: " + jsonFilePath);

            // No longer including the filename in the response
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            return new ResponseEntity<>(jsonNode, headers, HttpStatus.OK);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }}