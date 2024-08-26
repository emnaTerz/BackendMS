package com.emna.micro_service4.mapper;

import com.emna.micro_service4.dto.ReconciliationConfigurationDTO;
import com.emna.micro_service4.model.ReconciliationConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class ReconciliationConfigurationMapper {

    public static ReconciliationConfiguration mapToEntity(ReconciliationConfigurationDTO dto) {
        ReconciliationConfiguration entity = new ReconciliationConfiguration();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setMatchingConfigurationId(dto.getMatchingConfigurationId());
        entity.setCreationDate(dto.getCreationDate());
        entity.setUpdateDate(dto.getUpdateDate());
        entity.setTolerance(dto.getTolerance());
        entity.setScheduleList(dto.getScheduleList());
        System.out.println("Created ReconciliationConfiguration instance: " + entity); // Logging for debug purposes
        return entity;
    }

    public static List<ReconciliationConfiguration> mapToEntity(List<ReconciliationConfigurationDTO> dtos) {
        return dtos.stream()
                .map(ReconciliationConfigurationMapper::mapToEntity)
                .collect(Collectors.toList());
    }

    public static ReconciliationConfigurationDTO mapToDTO(ReconciliationConfiguration entity) {
        ReconciliationConfigurationDTO dto = new ReconciliationConfigurationDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setMatchingConfigurationId(entity.getMatchingConfigurationId());
        dto.setCreationDate(entity.getCreationDate());
        dto.setUpdateDate(entity.getUpdateDate());
        dto.setTolerance(entity.getTolerance());
        dto.setScheduleList(entity.getScheduleList());
        return dto;
    }

    public static List<ReconciliationConfigurationDTO> mapToDTO(List<ReconciliationConfiguration> entities) {
        return entities.stream()
                .map(ReconciliationConfigurationMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}
