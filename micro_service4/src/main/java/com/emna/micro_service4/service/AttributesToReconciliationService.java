package com.emna.micro_service4.service;

import com.emna.micro_service4.Repository.AttributesToReconciliationRepository;
import com.emna.micro_service4.dto.AttributesToReconciliationDTO;
import com.emna.micro_service4.dto.FormulaDTO;
import com.emna.micro_service4.mapper.AttributesToReconciliationMapper;
import com.emna.micro_service4.model.AttributesToReconciliation;
import com.emna.micro_service4.model.enums.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributesToReconciliationService {

    @Autowired
    private AttributesToReconciliationRepository repository;


    public AttributesToReconciliation createOrUpdateAttributesToReconciliation(AttributesToReconciliationDTO dto) {
        // Convert string operations to enum
        dto.getSourceOperations().replaceAll((k, v) -> String.valueOf(Operation.fromString(v)));
        dto.getTargetOperations().replaceAll((k, v) -> String.valueOf(Operation.fromString(v)));

        // Validate operations
        if (dto.getSourceOperations().containsValue(null) || dto.getTargetOperations().containsValue(null)) {
            throw new IllegalArgumentException("Invalid operation detected");
        }        // Generate UUID if not provided
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId(UUID.randomUUID().toString());
        }

        // Check for an existing entity that matches all attributes
        if (entityAlreadyExists(dto)) {
            throw new IllegalStateException("An identical entity already exists.");
        }

        AttributesToReconciliation entity = AttributesToReconciliationMapper.mapToEntity(dto);
        return repository.save(entity);
    }

    private boolean entityAlreadyExists(AttributesToReconciliationDTO dto) {
        List<AttributesToReconciliation> existingEntities = repository.findByReconciliationConfigurationId(dto.getReconciliationConfigurationId());
        return existingEntities.stream().anyMatch(e -> e.equalsAttributesAndOperations(dto));
    }

    public Optional<AttributesToReconciliationDTO> getAttributesToReconciliation(String id) {
        return repository.findById(id)
                .map(AttributesToReconciliationMapper::mapToDTO);
    }
    public List<FormulaDTO> getFormulasByReconciliationConfigurationId(String reconciliationConfigurationId) {
        List<AttributesToReconciliation> attributesList = repository.findByReconciliationConfigurationId(reconciliationConfigurationId);
        return attributesList.stream()
                .map(AttributesToReconciliation::getFormulaWithId) // This method now returns a FormulaDTO
                .collect(Collectors.toList());
    }
    public void deleteFormulaById(String id) {
        repository.deleteById(id);
    }



    public boolean deleteAttributesToReconciliation(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
    public List<AttributesToReconciliationDTO> findAllByReconciliationConfigurationId(String reconciliationConfigurationId) {
        return repository.findByReconciliationConfigurationId(reconciliationConfigurationId)
                .stream()
                .map(AttributesToReconciliationMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public boolean deleteAllByReconciliationConfigurationId(String reconciliationConfigurationId) {
        List<AttributesToReconciliation> entries = repository.findByReconciliationConfigurationId(reconciliationConfigurationId);
        if (!entries.isEmpty()) {
            repository.deleteAll(entries);
            return true;
        }
        return false;
    }

}