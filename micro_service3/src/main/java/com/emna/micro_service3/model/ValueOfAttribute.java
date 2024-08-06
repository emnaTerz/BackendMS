package com.emna.micro_service3.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "valueofaatributetoadd")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueOfAttribute {

    @Id
    private String id;
    private String pendingMessageId;
    private String attribute;

    private String attributeKey;

    private String valueOfAttribute;


    public ValueOfAttribute(String pendingMessageId, String attribute, String attributeKey, String valueOfAttribute) {
        this.pendingMessageId = pendingMessageId;
        this.attribute = attribute;
        this.attributeKey = attributeKey;
        this.valueOfAttribute = valueOfAttribute;
    }

    public ValueOfAttribute() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPendingMessageId() {
        return pendingMessageId;
    }

    public void setPendingMessageId(String pendingMessageId) {
        this.pendingMessageId = pendingMessageId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getValueOfAttribute() {
        return valueOfAttribute;
    }

    public void setValueOfAttribute(String valueOfAttribute) {
        this.valueOfAttribute = valueOfAttribute;
    }

    @Override
    public String toString() {
        return "ValueOfAttribute{" +
                "attribute='" + attribute + '\'' +
                ", attributeKey='" + attributeKey + '\'' +
                ", valueOfAttribute='" + valueOfAttribute + '\'' +
                '}';
    }
}

