package com.emna.micro_service2.model;

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
    @Id
    private String id;
    private String attributeToAdd;
    private String attributeToAddKey;

    private String attributeToAddtype;



    public IndexConfigurationAttributeToAdd() {
        this.id = UUID.randomUUID().toString();  // Automatically generate key when an instance is created
    }

    public IndexConfigurationAttributeToAdd(String indexConfigurationId, String attributeToAdd, String attributeToAddtype) {
        this();
        this.indexConfigurationId = indexConfigurationId;
        this.attributeToAdd = attributeToAdd;
        this.indexConfigurationId = indexConfigurationId;
        this.attributeToAddtype = attributeToAddtype;
    }


    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndexConfigurationId() { return indexConfigurationId; }
    public void setIndexConfigurationId(String indexConfigurationId) { this.indexConfigurationId = indexConfigurationId; }
    public String getAttributeToAdd() { return attributeToAdd; }
    public void setAttributeToAdd(String attributeToAdd) { this.attributeToAdd = attributeToAdd; }
    public String getAttributeToAddKey() { return attributeToAddKey; }
    public void setAttributeToAddKey(String attributeToAddKey) { this.attributeToAddKey = attributeToAddKey; }

    public String getAttributeToAddtype() {
        return attributeToAddtype;
    }

    public void setAttributeToAddtype(String attributeToAddtype) {
        this.attributeToAddtype = attributeToAddtype;
    }

    @Override
    public String toString() {
        return "IndexConfigurationAttributeToAdd{" +
                "indexConfigurationId='" + indexConfigurationId + '\'' +
                ", attributeToAdd='" + attributeToAdd + '\'' +
                ", attributeToAddKey='" + attributeToAddKey + '\'' +
                ", attributeToAddtype='" + attributeToAddtype + '\'' +
                '}';
    }
}
