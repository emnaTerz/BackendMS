package com.emna.micro_service2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IndexConfigurationAttributeToAddRequest {

    @JsonProperty("configurationId")
    private String configurationId;

    @JsonProperty("attributes")
    private List<Attribute> attributes;

    // Default constructor
    public IndexConfigurationAttributeToAddRequest() {}

    // Getters and setters
    public String getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(String configurationId) {
        this.configurationId = configurationId;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "IndexConfigurationAttributeToAddRequest{" +
                "configurationId='" + configurationId + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    public static class Attribute {
        @JsonProperty("id")
        private String id;

        @JsonProperty("attributeToAdd")
        private String attributeToAdd;

        @JsonProperty("attributeToAddKey")
        private String attributeToAddKey;

        @JsonProperty("attributeToAddtype")
        private String attributeToAddtype;

        // Constructor with all fields
        public Attribute(String id, String attributeToAdd, String attributeToAddKey, String attributeToAddtype) {
            this.id = id;
            this.attributeToAdd = attributeToAdd;
            this.attributeToAddKey = attributeToAddKey;
            this.attributeToAddtype = attributeToAddtype;
        }

        // Default constructor
        public Attribute() {}

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAttributeToAdd() {
            return attributeToAdd;
        }

        public void setAttributeToAdd(String attributeToAdd) {
            this.attributeToAdd = attributeToAdd;
        }

        public String getAttributeToAddKey() {
            return attributeToAddKey;
        }

        public void setAttributeToAddKey(String attributeToAddKey) {
            this.attributeToAddKey = attributeToAddKey;
        }

        public String getAttributeToAddtype() {
            return attributeToAddtype;
        }

        public void setAttributeToAddtype(String attributeToAddtype) {
            this.attributeToAddtype = attributeToAddtype;
        }

        @Override
        public String toString() {
            return "Attribute{" +
                    "id='" + id + '\'' +
                    "attributeToAdd='" + attributeToAdd + '\'' +
                    ", attributeToAddKey='" + attributeToAddKey + '\'' +
                    ", attributeToAddtype='" + attributeToAddtype + '\'' +
                    '}';
        }
    }
}
