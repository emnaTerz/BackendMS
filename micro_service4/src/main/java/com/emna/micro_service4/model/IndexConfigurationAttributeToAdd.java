package com.emna.micro_service4.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "index_configuration_attribute")
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexConfigurationAttributeToAdd {

    @JsonBackReference
    private String indexConfigurationId;
    private String attributeToAdd;
    @Id
    private String attributeToAddKey;


    public IndexConfigurationAttributeToAdd() {
        this.attributeToAddKey = UUID.randomUUID().toString();  // Automatically generate key when an instance is created
    }

    public IndexConfigurationAttributeToAdd(String indexConfigurationId, String attributeToAdd) {
        this();
        this.indexConfigurationId = indexConfigurationId;
        this.attributeToAdd = attributeToAdd;
        this.indexConfigurationId = indexConfigurationId;
    }


    // Getters and Setters
    public String getIndexConfigurationId() { return indexConfigurationId; }
    public void setIndexConfigurationId(String indexConfigurationId) { this.indexConfigurationId = indexConfigurationId; }
    public String getAttributeToAdd() { return attributeToAdd; }
    public void setAttributeToAdd(String attributeToAdd) { this.attributeToAdd = attributeToAdd; }
    public String getAttributeToAddKey() { return attributeToAddKey; }
    public void setAttributeToAddKey(String attributeToAddKey) { this.attributeToAddKey = attributeToAddKey; }

    @Override
    public String toString() {
        return "IndexConfigurationAttributeToAdd{" +
                "attributeToAdd='" + attributeToAdd + '\'' +
                ", attributeToAddKey='" + attributeToAddKey + '\'' +
                '}';
    }
}
