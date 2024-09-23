package com.emna.micro_service2.service;

import com.emna.micro_service2.dto.PendingMessageDto;
import com.emna.micro_service2.exception.ValueExtractionException;
import com.emna.micro_service2.mapper.MessageMapper;
import com.emna.micro_service2.model.IndexConfiguration;
import com.emna.micro_service2.model.IndexConfigurationAttributeToAdd;
import com.emna.micro_service2.model.PendingMessage;
import com.emna.micro_service2.model.ValueOfAttribute;
import com.emna.micro_service2.repository.IndexConfigurationAttributeToAddRepository;
import com.emna.micro_service2.repository.IndexConfigurationRepository;
import com.emna.micro_service2.repository.PendingMessageRepository;
import com.emna.micro_service2.repository.ValueOfAttributeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class MessageService {

    @Value("${json.storage.directory}")
    private String storageDirectory;

    @Autowired
    private IndexConfigurationRepository indexConfigurationRepository;
    @Autowired
    private IndexConfigurationAttributeToAddRepository indexConfigurationAttributeToAddRepository;

    @Autowired
    private PendingMessageRepository pendingMessageRepository;

    @Autowired
    private ValueOfAttributeRepository valueOfAttributeRepository;

  public void storeFile(String xmlContent) throws IOException {
      System.out.println("Starting to process the XML content.");

      String cleanedXmlContent = cleanXmlContent(xmlContent);
      System.out.println("Cleaned XML Content: " + cleanedXmlContent);

      if (isValidXml(cleanedXmlContent)) {
          System.out.println("XML content detected.");

          XmlMapper xmlMapper = new XmlMapper();
          try {
              JsonNode jsonNode = xmlMapper.readTree(cleanedXmlContent);
              JsonNode cleanedJsonNode = cleanJson(jsonNode);

              ObjectMapper jsonMapper = new ObjectMapper();
              String jsonContent = jsonMapper.writeValueAsString(cleanedJsonNode);
              System.out.println("Cleaned JSON Content: " + jsonContent);

              String sender = extractSender(cleanedJsonNode);
              String messageCategory = extractMessageIdentifier(cleanedJsonNode);
              System.out.println("sender: " + sender);
              System.out.println("messageCategory: " + messageCategory);

              Optional<IndexConfiguration> optionalIndexConfiguration = findConfiguration(sender, messageCategory);
              if (optionalIndexConfiguration.isEmpty()) {
                  System.out.println("No index config found for the provided sender and message category.");
                  return;
              }

              IndexConfiguration indexConfiguration = optionalIndexConfiguration.get();
              String commonId = UUID.randomUUID().toString();
              String fileName = commonId + ".json";
              String filePath = storageDirectory + File.separator + fileName;
              System.out.println("Generated file name: " + fileName);
              System.out.println("File path: " + filePath);

              // Find PendingMessages by IndexConfigurationId
              List<PendingMessage> existingPendingMessages = pendingMessageRepository.findByIndexConfigurationId(indexConfiguration.getId());

              List<IndexConfigurationAttributeToAdd> attributesToAdd = indexConfigurationAttributeToAddRepository.findByIndexConfigurationId(indexConfiguration.getId());
              System.out.println("attributesToAdd: " + attributesToAdd);

              List<ValueOfAttribute> valueOfAttributes = new ArrayList<>();
              for (IndexConfigurationAttributeToAdd attributeToAdd : attributesToAdd) {
                  if (attributeToAdd.getAttributeToAdd() == null) {
                      System.out.println("Attribute to add is null for attributeToAddKey: " + attributeToAdd.getAttributeToAddKey());
                      continue;
                  }
                  String cleanedPath = attributeToAdd.getAttributeToAdd().replaceAll("^[^:]*:", "");
                  try {
                      System.out.println("Attempting to extract value for path: " + cleanedPath);
                      String attributeValue = extractValueFromJson(cleanedJsonNode, cleanedPath);
                      ValueOfAttribute valueOfAttribute = new ValueOfAttribute(commonId, attributeToAdd.getAttributeToAdd(), attributeToAdd.getAttributeToAddKey(), attributeValue);
                      valueOfAttributes.add(valueOfAttribute);
                      System.out.println("valueOfAttribute: " + valueOfAttribute);
                  } catch (ValueExtractionException e) {
                      System.out.println("Failed to extract value for path '" + cleanedPath + "': " + e.getMessage());
                  }
              }

              System.out.println("Saving all ValueOfAttribute entities: " + valueOfAttributes);
              valueOfAttributeRepository.saveAll(valueOfAttributes);

              PendingMessage pendingMessage = new PendingMessage(
                      sender,
                      indexConfiguration.getName(),
                      new Date(),
                      messageCategory,
                      "unused"
              );
              pendingMessage.setId(commonId);
              pendingMessage.setIndexConfigurationId(indexConfiguration.getId());
              pendingMessageRepository.save(pendingMessage);
              System.out.println("pendingMessage: " + pendingMessage);

              File file = new File(filePath);
              try (FileOutputStream fos = new FileOutputStream(file)) {
                  fos.write(jsonContent.getBytes());
                  System.out.println("Message saved to file: " + fileName);
              } catch (IOException e) {
                  System.out.println("Failed to write the file: " + e.getMessage());
                  throw e;
              }
          } catch (IOException e) {
              System.out.println("Failed to parse XML content: " + e.getMessage());
              throw e;
          }
      } else {
          System.out.println("Received content is not valid XML.");
      }
  }

    private String cleanXmlContent(String xmlContent) {
        return xmlContent.replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replaceAll("^\"|\"$", "");
    }

    private boolean isValidXml(String xmlContent) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.readTree(xmlContent);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private JsonNode cleanJson(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getValue().isNull() || (field.getValue().isArray() && field.getValue().isEmpty(null))) {
                    fields.remove();
                } else {
                    cleanJson(field.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                cleanJson(arrayItem);
            }
        }
        return node;
    }

    private String extractSender(JsonNode jsonNode) {
        try {
            JsonNode senderNode = jsonNode.path("Header").path("Message").path("Sender").path("FullName").path("X1");
            return senderNode.isMissingNode() ? null : senderNode.asText();
        } catch (Exception e) {
            System.out.println("Failed to extract Sender from JSON: " + e.getMessage());
            return null;
        }
    }

    private String extractMessageIdentifier(JsonNode jsonNode) {
        try {
            JsonNode messageIdentifierNode = jsonNode.path("Header").path("Message").path("MessageIdentifier");
            return messageIdentifierNode.isMissingNode() ? null : messageIdentifierNode.asText();
        } catch (Exception e) {
            System.out.println("Failed to extract MessageIdentifier from JSON: " + e.getMessage());
            return null;
        }
    }

    public Optional<IndexConfiguration> findConfiguration(String sender, String messageCategory) {
        return indexConfigurationRepository.findBySenderAndMessageCategory(sender, messageCategory);
    }

    private String extractValueFromJson(JsonNode jsonNode, String jsonPath) throws ValueExtractionException {
        try {
            String[] keys = jsonPath.split("\\.");
            JsonNode currentNode = jsonNode;

            for (String key : keys) {
                key = key.replaceAll("^[^:]*:", "");
                System.out.println("Processing key: " + key);
                if (currentNode.has(key)) {
                    currentNode = currentNode.get(key);
                    System.out.println("Current node for key '" + key + "': " + currentNode.toString());
                } else {
                    System.out.println("Key '" + key + "' not found in the current node.");
                    throw new ValueExtractionException("Key '" + key + "' not found in the current node.");
                }
            }

            if (currentNode.isArray()) {
                StringBuilder arrayValues = new StringBuilder();
                for (JsonNode arrayItem : currentNode) {
                    arrayValues.append(arrayItem.asText()).append(", ");
                }
                return arrayValues.length() > 0 ? arrayValues.substring(0, arrayValues.length() - 2) : null;
            }

            return currentNode.isMissingNode() ? null : currentNode.asText();
        } catch (Exception e) {
            throw new ValueExtractionException("Failed to extract value from JSON for path: " + jsonPath, e);
        }
    }

    public PendingMessageDto getMessageById(String id) {
        PendingMessage message = pendingMessageRepository.findById(id).orElse(null);
        if (message != null) {
            return MessageMapper.toDTO(message);
        }
        return null;
    }
    public List<ValueOfAttribute> getAttributesByPendingMessageId(String pendingMessageId) {
        System.out.println(valueOfAttributeRepository.findByPendingMessageId(pendingMessageId));
        return valueOfAttributeRepository.findByPendingMessageId(pendingMessageId);
    }
    public List<PendingMessage> getAllMessages() {
        return pendingMessageRepository.findAll();
    }

    public List<PendingMessage> getMessagesByConfigId(String configId) {
        return pendingMessageRepository.findByIndexConfigurationId(configId);
    }

    public void updateStatusById(String id, String status) {
        Optional<PendingMessage> optionalMessage = pendingMessageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            PendingMessage message = optionalMessage.get();
            message.setStatus(status);
            pendingMessageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Message with ID " + id + " not found.");
        }
    }
}
