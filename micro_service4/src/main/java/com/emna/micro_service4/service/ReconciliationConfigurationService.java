
package com.emna.micro_service4.service;

import com.emna.micro_service4.Repository.AttributesToReconciliationRepository;
import com.emna.micro_service4.Repository.ReconciliationConfigurationRepository;
import com.emna.micro_service4.Repository.ReconciliationResultsRepository;
import com.emna.micro_service4.dto.ReconciliationConfigurationDTO;
import com.emna.micro_service4.mapper.ReconciliationConfigurationMapper;
import com.emna.micro_service4.model.AttributesToReconciliation;
import com.emna.micro_service4.model.ReconciliationConfiguration;
import com.emna.micro_service4.model.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReconciliationConfigurationService {
    @Autowired
    private ReconciliationResultsRepository reconciliationResultsRepository;
    @Autowired
    private AttributesToReconciliationRepository attributesRepository;
    @Autowired
    private ReconciliationConfigurationRepository repository;

    public ReconciliationConfiguration createReconciliationConfiguration(ReconciliationConfigurationDTO dto) {
        dto.setCreationDate(new Date());

        // Check if a configuration with the same matchingConfigurationId already exists
        Optional<ReconciliationConfiguration> existingConfig = repository.findByMatchingConfigurationId(dto.getMatchingConfigurationId());
        if (existingConfig.isPresent()) {
            // Handle the case where configuration exists, e.g., throw an exception or return null
            throw new IllegalStateException("Configuration with matchingConfigurationId '" + dto.getMatchingConfigurationId() + "' already exists.");
        }

        ReconciliationConfiguration config = ReconciliationConfigurationMapper.mapToEntity(dto);
        return repository.save(config);
    }

    public Optional<ReconciliationConfigurationDTO> getReconciliationConfiguration(String id) {
        return repository.findById(id).map(ReconciliationConfigurationMapper::mapToDTO);
    }

    public ReconciliationConfiguration updateReconciliationConfiguration(String id, ReconciliationConfigurationDTO dto) {
        return repository.findById(id)
                .map(existingConfig -> {
                    ReconciliationConfiguration updatedConfig = ReconciliationConfigurationMapper.mapToEntity(dto);
                    updatedConfig.setId(existingConfig.getId()); // Preserve the existing ID
                    return repository.save(updatedConfig);
                }).orElse(null);
    }

  /*  public boolean deleteReconciliationConfiguration(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true; // Return true indicating the deletion was successful
        }
        return false; // Return false if the configuration was not found
    }*/



    public boolean deleteReconciliationConfiguration(String id) {
        if (repository.existsById(id)) {
            // Delete associated AttributesToReconciliation
            List<AttributesToReconciliation> associatedAttributes = attributesRepository.findByReconciliationConfigurationId(id);
            if (!associatedAttributes.isEmpty()) {
                attributesRepository.deleteAll(associatedAttributes);
            }

            // Delete associated ReconciliationResults
            List<ReconciliationResult> associatedResults = reconciliationResultsRepository.findByReconciliationConfigurationId(id);
            if (!associatedResults.isEmpty()) {
                reconciliationResultsRepository.deleteAll(associatedResults);
            }

            // Delete the ReconciliationConfiguration itself
            repository.deleteById(id);
            return true; // Return true indicating the deletion was successful
        }
        return false; // Return false if the configuration was not found
    }


    public List<ReconciliationConfigurationDTO> getAllReconciliationConfigurations() {
        Iterable<ReconciliationConfiguration> configurations = repository.findAll();
        return StreamSupport.stream(configurations.spliterator(), false)
                .map(ReconciliationConfigurationMapper::mapToDTO)
                .collect(Collectors.toList());
    }
    public List<ReconciliationResult> getReconciliationResultsBySourceMessageId(String sourceMessageId) {
        // Fetch all reconciliation results
        List<ReconciliationResult> allResults = reconciliationResultsRepository.findAll();

        // Filter the results based on sourceMessageId
        List<ReconciliationResult> filteredResults = allResults.stream()
                .filter(result -> result.getSourceMessages().containsKey(sourceMessageId))
                .collect(Collectors.toList());

        System.out.println("Filtered ReconciliationResults for sourceMessageId: " + sourceMessageId);
        System.out.println("Number of ReconciliationResults found: " + filteredResults.size());

        return filteredResults;
    }

    public List<ReconciliationResult> getReconciliationResultsByTargetMessageId(String targetMessageId) {
        // Fetch all reconciliation results
        List<ReconciliationResult> allResults = reconciliationResultsRepository.findAll();

        System.out.println("Total ReconciliationResults retrieved: " + allResults.size());
        System.out.println("Target Message ID to filter: " + targetMessageId);

        // Log each target message ID in all results
        for (ReconciliationResult result : allResults) {
            System.out.println("Processing ReconciliationResult with ID: " + result.getId());
            result.getTargetMessages().keySet().forEach(targetId -> {
                String cleanedTargetId = targetId.replace("targetMessageId", "").trim();
                System.out.println("Original Target ID: " + targetId);
                System.out.println("Cleaned Target ID: " + cleanedTargetId);
            });
        }

        // Filter the results based on the cleaned targetMessageId
        List<ReconciliationResult> filteredResults = allResults.stream()
                .filter(result -> result.getTargetMessages().keySet().stream()
                        .anyMatch(key -> key.replace("targetMessageId", "").trim().equals(targetMessageId)))
                .collect(Collectors.toList());

        System.out.println("Filtered ReconciliationResults for targetMessageId: " + targetMessageId);
        System.out.println("Number of ReconciliationResults found: " + filteredResults.size());

        return filteredResults;
    }


    public boolean doesPairExist(String reconciliationConfigurationId, String sourceMessageId, String targetMessageId) {
        // Retrieve all results for the given reconciliationConfigurationId
        List<ReconciliationResult> results = reconciliationResultsRepository.findByReconciliationConfigurationId(reconciliationConfigurationId);

        // Check if any result contains the specified sourceMessageId and targetMessageId
        for (ReconciliationResult result : results) {
            // Extract the source ID from the map (assuming the key is the actual sourceMessageId)
            String sourceId = result.getSourceMessages().keySet().stream()
                    .findFirst().orElse(null);

            // Extract the target ID from the map (assuming the key is the actual targetMessageId)
            String targetId = result.getTargetMessages().keySet().stream()
                    .findFirst().orElse(null);

            if (sourceMessageId.equals(sourceId) && targetMessageId.equals(targetId)) {
                return true; // Pair exists
            }
        }
        return false; // Pair does not exist
    }


    public boolean doesGroupExist(String reconciliationConfigurationId, String sourceMessageId, Set<String> targetMessageIds) {
        List<ReconciliationResult> results = reconciliationResultsRepository.findByReconciliationConfigurationId(reconciliationConfigurationId);

        for (ReconciliationResult result : results) {
            // Extract the actual sourceMessageId (assuming the key contains the real ID)
            String existingSourceId = result.getSourceMessages().keySet().stream()
                    .findFirst().orElse(null);
            // Extract and clean up the targetMessageIds
            Set<String> existingTargetIds = result.getTargetMessages().keySet().stream()
                    .map(target -> target.replace("Target Message ", "").trim())
                    .collect(Collectors.toSet());

            // Clean up the provided sourceMessageId
            String cleanedSourceMessageId = sourceMessageId.replace("sourceMessageId", "").trim();

            if (cleanedSourceMessageId.equals(existingSourceId) && targetMessageIds.equals(existingTargetIds)) {
                return true; // Group already reconciled
            }
        }
        return false; // Group not reconciled
    }


    public boolean doesGroupExist(String reconciliationConfigurationId, Set<String> sourceMessageIds, String targetMessageId) {
        List<ReconciliationResult> results = reconciliationResultsRepository.findByReconciliationConfigurationId(reconciliationConfigurationId);

        for (ReconciliationResult result : results) {
            String existingTargetId = result.getTargetMessages().get("targetMessageId");
            Set<String> existingSourceIds = new HashSet<>(result.getSourceMessages().values());

            if (targetMessageId.equals(existingTargetId) && sourceMessageIds.equals(existingSourceIds)) {
                return true; // Group already reconciled
            }
        }
        return false; // Group not reconciled
    }

    public boolean doesGroupExist(String reconciliationConfigurationId, Set<String> sourceMessageIds, Set<String> targetMessageIds) {
        List<ReconciliationResult> results = reconciliationResultsRepository.findByReconciliationConfigurationId(reconciliationConfigurationId);

        for (ReconciliationResult result : results) {
            // Extract the source and target message IDs from the existing result
            Set<String> existingSourceIds = result.getSourceMessages().keySet();
            Set<String> existingTargetIds = result.getTargetMessages().keySet();

            // Clean the provided targetMessageIds and sourceMessageIds (remove any prefixes like "Source Message " or "targetMessageId")
            Set<String> cleanedSourceIds = sourceMessageIds.stream()
                    .map(id -> id.replace("Source Message ", "").trim())
                    .collect(Collectors.toSet());

            Set<String> cleanedTargetIds = targetMessageIds.stream()
                    .map(id -> id.replace("targetMessageId", "").trim())
                    .collect(Collectors.toSet());

            // Compare cleaned source and target IDs with those stored in the result
            if (cleanedSourceIds.equals(existingSourceIds) && cleanedTargetIds.equals(existingTargetIds)) {
                return true; // Group already reconciled
            }
        }
        return false; // Group not reconciled
    }

    public List<ReconciliationResult> getReconciliationResultsByMessageId(String messageId) {
        return reconciliationResultsRepository.findAll().stream()
                .filter(result ->
                        result.getSourceMessages().containsKey(messageId) ||
                                result.getTargetMessages().containsKey(messageId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLastReconciliationDate(String id, Date date) {
        Optional<ReconciliationConfiguration> optionalConfig = repository.findById(id);
        if (optionalConfig.isPresent()) {
            ReconciliationConfiguration config = optionalConfig.get();
            config.setLastReconciliationDate(date);
            repository.save(config);
            System.out.println("Updated last reconciliation date for config ID: " + id + " to " + date);
        } else {
            System.err.println("Configuration not found for ID: " + id);
        }
    }

}

