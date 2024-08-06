package com.emna.micro_service2.mapper;

import com.emna.micro_service2.dto.IndexConfigurationRequest;
import com.emna.micro_service2.dto.IndexConfigurationResponse;
import com.emna.micro_service2.model.IndexConfiguration;

public class IndexConfigurationMapper {

    public static IndexConfigurationResponse mapToDTO(IndexConfiguration indexConfiguration) {
        IndexConfigurationResponse indexConfigurationResponse = new IndexConfigurationResponse();
        indexConfigurationResponse.setId(indexConfiguration.getId());
        indexConfigurationResponse.setName(indexConfiguration.getName());
        indexConfigurationResponse.setSender(indexConfiguration.getSender());
        indexConfigurationResponse.setMessageCategory(indexConfiguration.getMessageCategory());
        indexConfigurationResponse.setCreationDate(indexConfiguration.getCreationDate());
        indexConfigurationResponse.setUpdateDate(indexConfiguration.getUpdateDate());
        return indexConfigurationResponse;
    }

    public static IndexConfiguration mapToIndexConfiguration(IndexConfigurationRequest request) {
        IndexConfiguration indexConfiguration = new IndexConfiguration();
        indexConfiguration.setName(request.getName());
        indexConfiguration.setSender(request.getSender());
        indexConfiguration.setMessageCategory(request.getMessageCategory());
        return indexConfiguration;
    }
}
