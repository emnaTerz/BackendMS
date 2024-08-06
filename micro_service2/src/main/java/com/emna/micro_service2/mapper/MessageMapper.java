package com.emna.micro_service2.mapper;

import com.emna.micro_service2.dto.PendingMessageDto;
import com.emna.micro_service2.model.PendingMessage;

public class MessageMapper {

    public static PendingMessageDto toDTO(PendingMessage entity) {
        PendingMessageDto dto = new PendingMessageDto();
        dto.setSender(entity.getSender());
        dto.setName(entity.getName());
        dto.setCreationDate(entity.getCreationDate());
        dto.setMessageCategory(entity.getMessageCategory());
        dto.setStatus(entity.getStatus());
        dto.setIndexConfigurationId(entity.getIndexConfigurationId());
        return dto;
    }

    public static PendingMessage toEntity(PendingMessageDto dto) {
        PendingMessage entity = new PendingMessage();
        entity.setSender(dto.getSender());
        entity.setName(dto.getName());
        entity.setCreationDate(dto.getCreationDate());
        entity.setMessageCategory(dto.getMessageCategory());
        entity.setStatus(dto.getStatus());
        entity.setIndexConfigurationId(dto.getIndexConfigurationId());
        return entity;
    }
}
