package com.emna.micro_service2.controller;

import com.emna.micro_service2.dto.IndexConfigurationAttributeToAddRequest;
import com.emna.micro_service2.dto.IndexConfigurationRequest;
import com.emna.micro_service2.dto.IndexConfigurationResponse;
import com.emna.micro_service2.service.IndexConfigurationService;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/config")
/*@CrossOrigin(origins = "http://localhost:4200")*/
public class IndexConfigurationController {

    @Autowired
    private IndexConfigurationService configurationService;

    @Autowired
    private ServletContext context;

    @PostMapping("/{configurationId}/add-attributes")
    public ResponseEntity<IndexConfigurationResponse> addAttributeToConfiguration(
            @PathVariable("configurationId") String configurationId,
            @RequestBody IndexConfigurationAttributeToAddRequest request) {
        request.setConfigurationId(configurationId); // Set the configurationId from the URL
        System.out.println("Received request to add attribute: " + request);

        // Debugging: Print each attribute
        if (request.getAttributes() != null) {
            for (IndexConfigurationAttributeToAddRequest.Attribute attr : request.getAttributes()) {
                System.out.println("Attribute to add: " + attr.getAttributeToAdd() + ", Type: " + attr.getAttributeToAddtype());
            }
        } else {
            System.out.println("Attributes list is null");
        }

        IndexConfigurationResponse updatedConfiguration = configurationService.addAttributesToConfiguration(request);
        System.out.println("Updated configuration: " + updatedConfiguration);

        return new ResponseEntity<>(updatedConfiguration, HttpStatus.OK);
    }


    @GetMapping("/{configurationId}/attributes")
    public ResponseEntity<IndexConfigurationAttributeToAddRequest> getAttributesByConfigurationId(
            @PathVariable String configurationId) {
        System.out.println("get attributes");
        IndexConfigurationAttributeToAddRequest attributes = configurationService.getAttributesByConfigurationId(configurationId);
        if (attributes != null && !attributes.getAttributes().isEmpty()) {
            return new ResponseEntity<>(attributes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{configurationId}/update-details")
    public ResponseEntity<IndexConfigurationResponse> updateConfigurationDetails(
            @PathVariable("configurationId") String configurationId,
            @RequestBody IndexConfigurationRequest request) {

        IndexConfigurationResponse updatedConfiguration = configurationService.updateConfigurationDetails(configurationId, request);
        return new ResponseEntity<>(updatedConfiguration, HttpStatus.OK);
    }



    @GetMapping("/details/{id}")
    public ResponseEntity<IndexConfigurationResponse> getConfigById(@PathVariable String id) {
        System.out.println(id);
        try {
            System.out.println(id);
            IndexConfigurationResponse config = configurationService.getConfigurationById(id);
            System.out.println("config : " + configurationService.getConfigurationById(id));
            return ResponseEntity.ok(config);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{configurationId}")
    public ResponseEntity<String> deleteConfiguration(@PathVariable String configurationId) {

        boolean deleted = configurationService.deleteConfiguration(configurationId);
        if (deleted) {
            return new ResponseEntity<>("Configuration deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Configuration not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all-config-details")
    public ResponseEntity<List<IndexConfigurationResponse>> getAllConfigDetails() {
        List<IndexConfigurationResponse> configurations = configurationService.getAllConfigurationsWithoutAttributes();
        return new ResponseEntity<>(configurations, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<IndexConfigurationResponse> createConfig(@RequestBody IndexConfigurationRequest request) {

        System.out.println("Request body: " + request);

        System.out.println("Received request: " + request);
        IndexConfigurationResponse createdConfig = configurationService.createIndexConfiguration(request);
        return new ResponseEntity<>(createdConfig, HttpStatus.CREATED);
    }

    @DeleteMapping("/{configurationId}/delete-attribute/{attributeToAddKey}")
    public ResponseEntity<IndexConfigurationResponse> removeAttributeFromConfiguration(
            @PathVariable("configurationId") String configurationId,
            @PathVariable("attributeToAddKey") String attributeToAddKey) {

        IndexConfigurationResponse updatedConfiguration = configurationService.removeAttributeFromConfiguration(configurationId, attributeToAddKey);

        return new ResponseEntity<>(updatedConfiguration, HttpStatus.OK);
    }
    @GetMapping("/by-message-category-and-sender")
    public ResponseEntity<IndexConfigurationResponse> getConfigurationByMessageCategoryAndSender(
            @RequestParam("messageCategory") String messageCategory,
            @RequestParam("sender") String sender) {

        Optional<IndexConfigurationResponse> configuration = configurationService.getConfigurationByMessageCategoryAndSender(messageCategory, sender);
        return configuration.map(config -> new ResponseEntity<>(config, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/attribute-exists/{indexConfigurationId}")
    public boolean doesAttributeExist(@PathVariable String indexConfigurationId) {
        return configurationService.doesAttributeExist(indexConfigurationId);
    }
}