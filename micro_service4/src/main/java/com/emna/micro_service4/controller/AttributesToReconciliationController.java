package com.emna.micro_service4.controller;

import com.emna.micro_service4.Repository.AttributesToReconciliationRepository;
import com.emna.micro_service4.dto.AttributesToReconciliationDTO;
import com.emna.micro_service4.dto.FormulaDTO;
import com.emna.micro_service4.model.AttributesToReconciliation;
import com.emna.micro_service4.service.AttributesToReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reconciliation")
public class AttributesToReconciliationController {
    @Autowired
    private AttributesToReconciliationRepository repository;

    @Autowired
    private AttributesToReconciliationService service;

    @PostMapping("/attributes/create")
    public ResponseEntity<?> createOrUpdateAttributes(@RequestBody AttributesToReconciliationDTO dto) {
        try {
            AttributesToReconciliation entity = service.createOrUpdateAttributesToReconciliation(dto);
            return ResponseEntity.ok(entity);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data");
        }
    }

    @GetMapping("/attributes/{id}")
    public ResponseEntity<AttributesToReconciliationDTO> getAttributesToReconciliation(@PathVariable String id) {
        return service.getAttributesToReconciliation(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/formulas/{id}")
    public ResponseEntity<Void> deleteFormulaById(@PathVariable String id) {
        if (repository.existsById(id)) {
            service.deleteFormulaById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping("/attributes/all/{reconciliationConfigurationId}")
    public ResponseEntity<List<AttributesToReconciliationDTO>> getAllByReconciliationConfigurationId(@PathVariable String reconciliationConfigurationId) {
        List<AttributesToReconciliationDTO> dtos = service.findAllByReconciliationConfigurationId(reconciliationConfigurationId);
        if (dtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/formulas/{reconciliationConfigurationId}")
    public ResponseEntity<List<FormulaDTO>> getFormulasByReconciliationConfigurationId(@PathVariable String reconciliationConfigurationId) {
        List<FormulaDTO> formulas = service.getFormulasByReconciliationConfigurationId(reconciliationConfigurationId);
        if (formulas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(formulas);
    }


    @DeleteMapping("/attributes/deleteAll/{reconciliationConfigurationId}")
    public ResponseEntity<String> deleteAllByReconciliationConfigurationId(@PathVariable String reconciliationConfigurationId) {
        boolean deleted = service.deleteAllByReconciliationConfigurationId(reconciliationConfigurationId);
        if (deleted) {
            return ResponseEntity.ok("All attributes deleted successfully for reconciliationConfigurationId: " + reconciliationConfigurationId);
        } else {
            return ResponseEntity.status(404).body("No attributes found for reconciliationConfigurationId: " + reconciliationConfigurationId);
        }
    }

}
