package com.emna.micro_service3.service;

import com.emna.micro_service3.Repository.AttributesToMatchRepository;
import com.emna.micro_service3.model.AttributesToMatch;
import com.emna.micro_service3.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service3.model.enums.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttributesToMatchService {

    @Autowired
    private AttributesToMatchRepository repository;

    @Transactional
    public AttributesToMatch createAttributesToMatch(String matchingConfigurationId,
                                                     Map<Integer, IndexConfigurationAttributeToAdd> sourceAttributes,
                                                     Map<Integer, IndexConfigurationAttributeToAdd> targetAttributes,
                                                     Map<Integer, Operation> sourceOperations,
                                                     Map<Integer, Operation> targetOperations) {

        System.out.println("Received matchingConfigurationId: " + matchingConfigurationId);
        System.out.println("Received sourceAttributes: " + sourceAttributes);
        System.out.println("Received targetAttributes: " + targetAttributes);
        System.out.println("Received sourceOperations: " + sourceOperations);
        System.out.println("Received targetOperations: " + targetOperations);

        Map<Integer, IndexConfigurationAttributeToAdd> autoSourceAttributes = new HashMap<>();
        Map<Integer, IndexConfigurationAttributeToAdd> autoTargetAttributes = new HashMap<>();
        Map<Integer, Operation> autoSourceOperations = new HashMap<>();
        Map<Integer, Operation> autoTargetOperations = new HashMap<>();

        int key = 1;
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : sourceAttributes.entrySet()) {
            autoSourceAttributes.put(key, entry.getValue());
            key++;
        }

        key = 1;
        for (Map.Entry<Integer, IndexConfigurationAttributeToAdd> entry : targetAttributes.entrySet()) {
            autoTargetAttributes.put(key, entry.getValue());
            key++;
        }

        key = 1;
        for (Map.Entry<Integer, Operation> entry : sourceOperations.entrySet()) {
            autoSourceOperations.put(key, entry.getValue());
            key++;
        }

        key = 1;
        for (Map.Entry<Integer, Operation> entry : targetOperations.entrySet()) {
            autoTargetOperations.put(key, entry.getValue());
            key++;
        }

        AttributesToMatch attributesToMatch = new AttributesToMatch(
                UUID.randomUUID().toString(),
                matchingConfigurationId,
                autoSourceAttributes,
                autoTargetAttributes,
                autoSourceOperations,
                autoTargetOperations
        );

        return repository.save(attributesToMatch);
    }

    public List<AttributesToMatch> getAttributesToMatchByMatchingConfigurationId(String matchingConfigurationId) {
        return repository.findByMatchingConfigurationId(matchingConfigurationId);
    }
    @Transactional
    public void deleteAttributesToMatch(String id) {
        AttributesToMatch attributesToMatch = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No AttributesToMatch found with ID: " + id));

        repository.delete(attributesToMatch);
    }


    public List<AttributesToMatch> getByMatchingConfigurationId(String matchingConfigurationId) {
        return repository.findByMatchingConfigurationId(matchingConfigurationId);
    }

    public List<String> getFormulesByMatchingConfigurationId(String matchingConfigurationId) {
        List<AttributesToMatch> attributesToMatchList = getByMatchingConfigurationId(matchingConfigurationId);
        return attributesToMatchList.stream()
                .map(AttributesToMatch::getFormule)
                .collect(Collectors.toList());
    }
}
