package com.emna.micro_service4.controller;

import com.emna.micro_service4.dto.ReconciliationConfigurationDTO;
import com.emna.micro_service4.model.ReconciliationConfiguration;
import com.emna.micro_service4.service.ReconciliationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/reconciliation")
public class ReconciliationConfigurationController {

    @Autowired
    private ReconciliationConfigurationService service;

    @PostMapping("/create")
    public ResponseEntity<?> createReconciliationConfiguration(@RequestBody ReconciliationConfigurationDTO dto) {
        try {
            // Set creation and update dates for a new configuration
            dto.setCreationDate(new Date());
            dto.setUpdateDate(new Date());

            ReconciliationConfiguration config = service.createReconciliationConfiguration(dto);
            return ResponseEntity.ok(config);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReconciliationConfigurationDTO> getReconciliationConfiguration(@PathVariable String id) {
        return service.getReconciliationConfiguration(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReconciliationConfiguration(@PathVariable String id, @RequestBody ReconciliationConfigurationDTO dto) {
        try {
            // Set the update date for an existing configuration
            dto.setUpdateDate(new Date());

            ReconciliationConfiguration config = service.updateReconciliationConfiguration(id, dto);
            return ResponseEntity.ok(config);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReconciliationConfiguration(@PathVariable String id) {
        boolean deleted = service.deleteReconciliationConfiguration(id);
        if (deleted) {
            return ResponseEntity.ok("Deleted successfully.");
        } else {
            // Correctly returning a response entity with 404 status and a custom JSON message
            return ResponseEntity
                    .status(404)  // This sets the status directly
                    .header("Content-Type", "application/json")
                    .body("{\"message\":\"ReconciliationConfiguration not found with ID: " + id + "\"}");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReconciliationConfigurationDTO>> getAllReconciliationConfigurations() {
        List<ReconciliationConfigurationDTO> dtos = service.getAllReconciliationConfigurations();
        return ResponseEntity.ok(dtos);
    }
}

