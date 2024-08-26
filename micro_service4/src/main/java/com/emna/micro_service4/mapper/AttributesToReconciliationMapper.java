package com.emna.micro_service4.mapper;

import com.emna.micro_service4.dto.AttributesToReconciliationDTO;
import com.emna.micro_service4.model.AttributesToReconciliation;
import com.emna.micro_service4.model.enums.Operation;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class AttributesToReconciliationMapper {

    public static AttributesToReconciliation mapToEntity(AttributesToReconciliationDTO dto) {
        return new AttributesToReconciliation(
                dto.getId(),
                dto.getReconciliationConfigurationId(),
                dto.getSourceAttributes(),
                dto.getTargetAttributes(),
                convertStringOperationsToEnum(dto.getSourceOperations()),
                convertStringOperationsToEnum(dto.getTargetOperations()),
                dto.getSourceValues() != null ? dto.getSourceValues() : Collections.emptyMap(),
                dto.getTargetValues() != null ? dto.getTargetValues() : Collections.emptyMap()
        );
    }

    public static AttributesToReconciliationDTO mapToDTO(AttributesToReconciliation entity) {
        return new AttributesToReconciliationDTO(
                entity.getId(),
                entity.getReconciliationConfigurationId(),
                entity.getSourceAttributes(),
                entity.getTargetAttributes(),
                convertEnumOperationsToString(entity.getSourceOperations()),
                convertEnumOperationsToString(entity.getTargetOperations()),
                entity.getSourceValues() != null ? entity.getSourceValues() : Collections.emptyMap(),
                entity.getTargetValues() != null ? entity.getTargetValues() : Collections.emptyMap()
        );
    }

    private static Map<Integer, Operation> convertStringOperationsToEnum(Map<Integer, String> operations) {
        if (operations == null) {
            return Collections.emptyMap();
        }
        return operations.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Operation.valueOf(entry.getValue().toLowerCase()) // Convert to lower case
                ));
    }

    private static Map<Integer, String> convertEnumOperationsToString(Map<Integer, Operation> operations) {
        if (operations == null) {
            return Collections.emptyMap();
        }
        return operations.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().name().toLowerCase() // Convert to lower case
                ));
    }
}
