
package com.emna.micro_service3.controller;

import com.emna.micro_service3.dto.MatchingConfigurationRequest;
import com.emna.micro_service3.dto.MatchingConfigurationResponse;
import com.emna.micro_service3.model.MatchingConfiguration;
import com.emna.micro_service3.service.MatchingConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MatchingConfigurationController {
    @Autowired
    private MatchingConfigurationService service;

    @PostMapping("/matching-configurations")
    public ResponseEntity<?> createMatchingConfiguration(@RequestBody MatchingConfigurationRequest request) {
        try {
            System.out.println("Received MatchingConfigurationRequest: " + request);
            MatchingConfiguration created = service.createMatchingConfiguration(
                    request.getSourceId(),
                    request.getTargetId(),
                    request.getName(),
                    request.getMatchingType(),
                    request.getScheduleList()
            );
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MatchingConfigurationResponse>> getAllMatchingConfigurations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MatchingConfiguration> configurationsPage = service.getAllMatchingConfigurations(PageRequest.of(page, size));
        List<MatchingConfigurationResponse> configurations = configurationsPage.getContent().stream()
                .map(config -> new MatchingConfigurationResponse(
                        config.getId(),
                        config.getSourceId(),
                        config.getTargetId(),
                        config.getName(),
                        config.getMatchingType(),
                        config.getCreationDate(),
                        config.getUpdateDate(),
                        config.getScheduleList()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(configurations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchingConfiguration> updateMatchingConfiguration(
            @PathVariable String id,
            @RequestBody MatchingConfigurationRequest request) {
        MatchingConfiguration updated = service.updateMatchingConfiguration(
                id,
                request.getSourceId(),
                request.getTargetId(),
                request.getName(),
                request.getMatchingType(),
                request.getScheduleList()
        );
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchingConfiguration> getMatchingConfigurationById(@PathVariable String id) {
        Optional<MatchingConfiguration> matchingConfiguration = service.getMatchingConfigurationById(id);

        // Add SOP to print out the data
        matchingConfiguration.ifPresent(config -> {
            System.out.println("Matching Configuration Found: ");
            System.out.println("ID: " + config.getId());
            System.out.println("Source ID: " + config.getSourceId());
            System.out.println("Target ID: " + config.getTargetId());
            System.out.println("Name: " + config.getName());
            System.out.println("Matching Type: " + config.getMatchingType());
            System.out.println("Schedule List: " + config.getScheduleList());
            System.out.println("Creation Date: " + config.getCreationDate());
            System.out.println("Update Date: " + config.getUpdateDate());
        });

        return matchingConfiguration.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatchingConfiguration(@PathVariable String id) {
        try {
            service.deleteMatchingConfiguration(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting configuration and related attributes: " + e.getMessage());
        }
    }
}
