package com.emna.micro_service2.mapper;

import com.emna.micro_service2.dto.IndexConfigurationAttributeToAddRequest;
import com.emna.micro_service2.model.IndexConfigurationAttributeToAdd;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IndexConfigurationAttributeMapper {

    public static IndexConfigurationAttributeToAdd mapToEntity(IndexConfigurationAttributeToAddRequest.Attribute attr, String configurationId) {
        IndexConfigurationAttributeToAdd attribute = new IndexConfigurationAttributeToAdd();
        attribute.setIndexConfigurationId(configurationId);
        attribute.setAttributeToAdd(attr.getAttributeToAdd());
        attribute.setAttributeToAddKey(attr.getAttributeToAddKey() != null ? attr.getAttributeToAddKey() : UUID.randomUUID().toString());
        attribute.setAttributeToAddtype(attr.getAttributeToAddtype());
        System.out.println("Created IndexConfigurationAttributeToAdd instance: " + attribute); // Logging for debug
        return attribute;
    }

    public static List<IndexConfigurationAttributeToAdd> mapToEntity(IndexConfigurationAttributeToAddRequest request) {
        return request.getAttributes().stream()
                .map(attr -> mapToEntity(attr, request.getConfigurationId()))
                .collect(Collectors.toList());
    }

    public static IndexConfigurationAttributeToAddRequest.Attribute mapToDTO(IndexConfigurationAttributeToAdd attribute) {
        IndexConfigurationAttributeToAddRequest.Attribute dto = new IndexConfigurationAttributeToAddRequest.Attribute();
        dto.setId(attribute.getId()); // Set the ID field
        dto.setAttributeToAdd(attribute.getAttributeToAdd());
        dto.setAttributeToAddKey(attribute.getAttributeToAddKey());
        dto.setAttributeToAddtype(attribute.getAttributeToAddtype());
        return dto;
    }

    public static IndexConfigurationAttributeToAddRequest mapToDTO(List<IndexConfigurationAttributeToAdd> attributes) {
        IndexConfigurationAttributeToAddRequest request = new IndexConfigurationAttributeToAddRequest();
        if (!attributes.isEmpty()) {
            request.setConfigurationId(attributes.get(0).getIndexConfigurationId());
        }
        List<IndexConfigurationAttributeToAddRequest.Attribute> attrList = attributes.stream()
                .map(IndexConfigurationAttributeMapper::mapToDTO)
                .collect(Collectors.toList());
        request.setAttributes(attrList);
        return request;
    }
}
