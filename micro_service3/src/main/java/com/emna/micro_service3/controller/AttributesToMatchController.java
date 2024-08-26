package com.emna.micro_service3.controller;

import com.emna.micro_service3.Repository.AttributesToMatchRepository;
import com.emna.micro_service3.dto.AttributesToMatchRequest;
import com.emna.micro_service3.dto.FormulaDTO;
import com.emna.micro_service3.model.AttributesToMatch;
import com.emna.micro_service3.service.AttributesToMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AttributesToMatchController {
    @Autowired
    private AttributesToMatchRepository repository;
    @Autowired
    private AttributesToMatchService service;
    @PostMapping("/create")
    public ResponseEntity<AttributesToMatch> createAttributesToMatch(@RequestBody AttributesToMatchRequest request) {
        AttributesToMatch created = service.createAttributesToMatch(
                request.getMatchingConfigurationId(),
                request.getSourceAttributes(),
                request.getTargetAttributes(),
                request.getSourceOperations(),
                request.getTargetOperations()
        );
        return ResponseEntity.ok(created);
    }

    @GetMapping("/by-matching-configuration/{matchingConfigurationId}")
    public ResponseEntity<?> getAttributesToMatchByMatchingConfigurationId(@PathVariable String matchingConfigurationId) {
        try {
            List<AttributesToMatch> attributesToMatchList = service.getAttributesToMatchByMatchingConfigurationId(matchingConfigurationId);
            return ResponseEntity.ok(attributesToMatchList);
        } catch (Exception e) {
            // Log the exception details here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data: " + e.getMessage());
        }
    }
    @DeleteMapping("/deleteAttributesToMatch/{id}")
    public ResponseEntity<?> deleteAttributesToMatch(@PathVariable String id) {
        try {
            service.deleteAttributesToMatch(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting attribute: " + e.getMessage());
        }
    }
    @GetMapping("/formulas/{reconciliationConfigurationId}")
    public ResponseEntity<List<FormulaDTO>> getFormulasByMatchingConfigurationId(@PathVariable String reconciliationConfigurationId) {
        List<FormulaDTO> formulas = service.getFormulasByMatchingConfigurationId(reconciliationConfigurationId);
        if (formulas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(formulas);
    }

    @DeleteMapping("/formulas/{id}")
    public ResponseEntity<Void> deleteFormulaById(@PathVariable String id) {
        if (repository.existsById(id)) {
            service.deleteFormulaById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if the ID does not exist
        }
    }


}
