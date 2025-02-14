

package com.emna.micro_service2.service;

import com.emna.micro_service2.dto.IndexConfigurationAttributeToAddRequest;
import com.emna.micro_service2.dto.IndexConfigurationRequest;
import com.emna.micro_service2.dto.IndexConfigurationResponse;
import com.emna.micro_service2.mapper.IndexConfigurationAttributeMapper;
import com.emna.micro_service2.mapper.IndexConfigurationMapper;
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
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexConfigurationService {

    @Autowired
    private IndexConfigurationRepository indexConfigurationRepository;

    @Autowired
    private PendingMessageRepository pendingMessageRepository;

    @Autowired
    private ValueOfAttributeRepository valueOfAttributeRepository;

    @Autowired
    private IndexConfigurationAttributeToAddRepository attributeToAddRepository;

    @Value("${file.storage.location}")
    private String storageLocation;
    @Value("${json.storage.directory}")
    private String storageDirectory;

    @Autowired
    public IndexConfigurationService(IndexConfigurationRepository indexConfigurationRepository) {
        this.indexConfigurationRepository = indexConfigurationRepository;
    }
    private JsonNode fetchJsonNodeFromFile(String jsonFilePath) {
        System.out.println("Fetching JSON file from path: " + jsonFilePath);
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(jsonFilePath));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonData);
            System.out.println("Fetched JSON data: " + rootNode.toString());
            return rootNode;
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file from path: " + jsonFilePath, e);
        }}
    @Transactional
    public IndexConfigurationResponse addAttributesToConfiguration(IndexConfigurationAttributeToAddRequest request) {
        try {
            System.out.println("Received request: " + request);

            if (StringUtils.isEmpty(request.getConfigurationId())) {
                throw new IllegalArgumentException("Configuration ID is missing in the request");
            }

            IndexConfiguration indexConfiguration = indexConfigurationRepository.findById(request.getConfigurationId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid configuration ID"));
            System.out.println("Original configuration: " + indexConfiguration);

            // Process each attribute in the request
            for (IndexConfigurationAttributeToAddRequest.Attribute attribute : request.getAttributes()) {
                System.out.println("Mapping attribute type: " + attribute.getAttributeToAddtype());
                String originalPath = attribute.getAttributeToAdd();
                String attributeType = attribute.getAttributeToAddtype();
                attribute.setAttributeToAddKey(originalPath);

                JsonNode rootNode = fetchJsonNodeFromFile(storageLocation + File.separator + request.getConfigurationId() + ".json");
                if (rootNode != null) {
                    String jsonPath = extractJsonPathFromRequest(originalPath, rootNode);
                    jsonPath = cleanPath(jsonPath);
                    attribute.setAttributeToAdd(jsonPath);
                    attribute.setAttributeToAddtype(attributeType);
                    System.out.println("Processed attribute: " + attribute);
                } else {
                    System.out.println("Root JSON node is null for configuration ID: " + request.getConfigurationId());
                }
            }

            List<IndexConfigurationAttributeToAdd> attributes = IndexConfigurationAttributeMapper.mapToEntity(request);
            System.out.println("Attributes to save: " + attributes); // Print attributes to save
            attributeToAddRepository.saveAll(attributes);

            // Find all pending messages related to the given IndexConfiguration ID
            List<PendingMessage> pendingMessages = pendingMessageRepository.findByIndexConfigurationId(request.getConfigurationId());
            Map<String, JsonNode> messageJsonNodes = new HashMap<>();

            ObjectMapper objectMapper = new ObjectMapper();
            for (PendingMessage pendingMessage : pendingMessages) {
                // Construct the file path for the JSON file associated with each pending message
                String messageJsonFilePath = storageDirectory + File.separator + pendingMessage.getId() + ".json";
                File jsonFile = new File(messageJsonFilePath);
                if (jsonFile.exists()) {
                    // Parse the JSON file
                    JsonNode jsonNode = objectMapper.readTree(jsonFile);
                    if (jsonNode != null) {
                        // Clean the JSON node
                        JsonNode cleanedJsonNode = cleanJsonNode(jsonNode);

                        // Store the cleaned JSON node in the map
                        messageJsonNodes.put(pendingMessage.getId(), cleanedJsonNode);
                        System.out.println("Loaded and cleaned JSON file for message ID: " + pendingMessage.getId());
                    } else {
                        System.out.println("No content in JSON file for message ID: " + pendingMessage.getId());
                    }
                } else {
                    System.out.println("JSON file not found for message ID: " + pendingMessage.getId());
                }
            }

            // Process each attribute in the request for each pending message
            List<ValueOfAttribute> valueOfAttributes = new ArrayList<>();
            for (IndexConfigurationAttributeToAddRequest.Attribute attribute : request.getAttributes()) {
                System.out.println("Processing attribute with path: " + attribute.getAttributeToAdd());
                String attributePath = attribute.getAttributeToAdd();

                // Convert JSON Path to JSON Pointer expression
                String jsonPointer = convertToJsonPointer(attributePath);

                // Extract the value for the attribute from each JSON file
                for (Map.Entry<String, JsonNode> entry : messageJsonNodes.entrySet()) {
                    String pendingMessageId = entry.getKey();
                    JsonNode jsonNode = entry.getValue();
                    boolean valueFound = false;
                    try {
                        System.out.println("Attempting to extract value for path: " + jsonPointer + " from message ID: " + pendingMessageId);
                        JsonNode valueNode = jsonNode.at(jsonPointer);
                        System.out.println("Value Node: " + valueNode); // Log the node for debugging
                        if (!valueNode.isMissingNode()) {
                            String attributeValue = valueNode.asText();
                            ValueOfAttribute valueOfAttribute = new ValueOfAttribute(pendingMessageId, attribute.getAttributeToAdd(), attribute.getAttributeToAddKey(), attributeValue);
                            valueOfAttributes.add(valueOfAttribute);
                            System.out.println("Extracted valueOfAttribute for PendingMessage ID " + pendingMessageId + ": " + valueOfAttribute);
                            valueFound = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to extract value for path '" + jsonPointer + "' from message ID: " + pendingMessageId + ": " + e.getMessage());
                    }
                    if (!valueFound) {
                        System.out.println("No value found for the path: " + jsonPointer + " in message ID: " + pendingMessageId);
                    }
                }
            }

            System.out.println("Preparing to save ValueOfAttributes: " + valueOfAttributes.size());
            valueOfAttributes.forEach(attr -> System.out.println(attr.toString()));


            // Save the new attributes to the repository
            for (ValueOfAttribute value : valueOfAttributes) {
                try {
                    valueOfAttributeRepository.save(value);
                } catch (Exception e) {
                    System.out.println("Failed to save ValueOfAttribute: " + value + " Error: " + e.getMessage());
                }
            }


            // Update the index configuration's update date
            indexConfiguration.setUpdateDate(new Date());
            IndexConfiguration savedConfiguration = indexConfigurationRepository.save(indexConfiguration);
            System.out.println("Saved configuration: " + savedConfiguration);

            return IndexConfigurationMapper.mapToDTO(savedConfiguration);
        } catch (Exception e) {
            System.out.println("Error in addAttributesToConfiguration: " + e.getMessage());
            throw new RuntimeException("Error processing attributes to configuration: " + e.getMessage(), e);
        }
    }


    // Method to clean the JSON node if necessary
    private JsonNode cleanJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getValue().isNull() || (field.getValue().isArray() && field.getValue().isEmpty(null))) {
                    fields.remove();
                } else {
                    cleanJsonNode(field.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                cleanJsonNode(arrayItem);
            }
        }
        return node;
    }

    // Convert JSON Path to JSON Pointer expression
    private String convertToJsonPointer(String jsonPath) {
        return "/" + jsonPath.replace(".", "/");
    }
    private String extractJsonPathFromRequest(String attributePath, JsonNode rootNode) {
        System.out.println("Extracting JSON path from attribute path: " + attributePath);
        String[] pathSegments = attributePath.split("\\.");

           // Initialize the jsonPathBuilder with "Body."
       StringBuilder jsonPathBuilder = new StringBuilder("Body.");
        //StringBuilder jsonPathBuilder = new StringBuilder();

        JsonNode currentNode = rootNode;
        for (int i = 0; i < pathSegments.length - 1; i++) {
            String segment = pathSegments[i];
            System.out.println("Processing segment: " + segment);

            // Check if this is the last segment
            if (i == pathSegments.length - 1) {
                jsonPathBuilder.append(segment);
                System.out.println("Final segment reached: " + segment);
                break;
            }

            if (segment.matches("\\d+")) {
                int index = Integer.parseInt(segment);
                currentNode = currentNode.get(index);
            } else {
                currentNode = currentNode.path(segment);
            }

            if (currentNode == null || currentNode.isMissingNode()) {
                throw new IllegalArgumentException("Invalid path segment in the request: " + segment);
            }

            jsonPathBuilder.append(currentNode.path("name").asText()).append(".");
            System.out.println("Current JSON path: " + jsonPathBuilder.toString());
        }

        String finalJsonPath = jsonPathBuilder.toString();
        System.out.println("Final JSON path: " + finalJsonPath);
        return finalJsonPath;
    }
    private String cleanPath(String path) {
        return path.replaceAll("\\.+", ".").replaceAll("^\\.|\\.$", "");
    }

    @Transactional
    public IndexConfigurationResponse updateConfigurationDetails(String configurationId, IndexConfigurationRequest request) {
        Optional<IndexConfiguration> configOptional = indexConfigurationRepository.findById(configurationId);
        if (configOptional.isPresent()) {
            IndexConfiguration config = configOptional.get();
            config.setName(request.getName());
            config.setSender(request.getSender());
            config.setMessageCategory(request.getMessageCategory());
            config.setUpdateDate(new Date());
            IndexConfiguration updatedConfig = indexConfigurationRepository.save(config);
            return IndexConfigurationMapper.mapToDTO(updatedConfig);
        } else {
            throw new RuntimeException("Configuration not found");
        }
    }

    @Transactional
    public IndexConfigurationAttributeToAddRequest getAttributesByConfigurationId(String configurationId) {
        System.out.println(configurationId);
        List<IndexConfigurationAttributeToAdd> attributes = attributeToAddRepository.findByIndexConfigurationId(configurationId);
        System.out.println("Attributes: " + attributes);
        if (attributes != null && !attributes.isEmpty()) {
            return IndexConfigurationAttributeMapper.mapToDTO(attributes);
        } else {
            return new IndexConfigurationAttributeToAddRequest();
        }
    }


    @Transactional
    public IndexConfigurationResponse getConfigurationById(String id) {
        System.out.println(id);
        Optional<IndexConfiguration> config = indexConfigurationRepository.findById(id);
        if (config.isPresent()) {
            return IndexConfigurationMapper.mapToDTO(config.get());
        } else {
            throw new IllegalArgumentException("Configuration not found for ID: " + id);
        }
    }

    @Transactional
    public boolean deleteConfiguration(String configurationId) {
        // Fetch the IndexConfiguration entity using the provided configuration ID
        Optional<IndexConfiguration> configurationOptional = indexConfigurationRepository.findById(configurationId);

        if (!configurationOptional.isPresent()) {
            // If the configuration does not exist, return false
            return false;
        }

        // Fetch all attributes associated with the configuration
        List<IndexConfigurationAttributeToAdd> attributes = attributeToAddRepository.findByIndexConfigurationId(configurationId);

        // Check if attributes exist and delete them
        if (!attributes.isEmpty()) {
            attributeToAddRepository.deleteAll(attributes);
            System.out.println("Deleted attributes for configuration ID: " + configurationId);
        }

        // Fetch all pending messages associated with the configuration
        List<PendingMessage> pendingMessages = pendingMessageRepository.findByIndexConfigurationId(configurationId);

        if (!pendingMessages.isEmpty()) {
            // Fetch and delete all ValueOfAttribute associated with the pending messages
            List<String> pendingMessageIds = pendingMessages.stream().map(PendingMessage::getId).collect(Collectors.toList());
            List<ValueOfAttribute> valueOfAttributes = valueOfAttributeRepository.findByPendingMessageIdIn(pendingMessageIds);

            if (!valueOfAttributes.isEmpty()) {
                valueOfAttributeRepository.deleteAll(valueOfAttributes);
                System.out.println("Deleted value attributes for pending messages associated with configuration ID: " + configurationId);
            }

            // Delete all pending messages
            pendingMessageRepository.deleteAll(pendingMessages);
            System.out.println("Deleted pending messages for configuration ID: " + configurationId);
        }

        // Now, delete the IndexConfiguration itself
        indexConfigurationRepository.delete(configurationOptional.get());
        System.out.println("Deleted configuration with ID: " + configurationId);

        return true;
    }


    @Transactional
    public IndexConfigurationResponse createIndexConfiguration(IndexConfigurationRequest request) {
        System.out.println("Service received: " + request);

        IndexConfiguration indexConfiguration = IndexConfigurationMapper.mapToIndexConfiguration(request);
        indexConfiguration.setCreationDate(new Date());
        indexConfiguration.setUpdateDate(new Date());

        IndexConfiguration createdIndexConfiguration = indexConfigurationRepository.save(indexConfiguration);

        IndexConfigurationResponse response = IndexConfigurationMapper.mapToDTO(createdIndexConfiguration);
        System.out.println("Service response: " + response);
        return response;
    }

    public List<IndexConfigurationResponse> getAllConfigurationsWithoutAttributes() {
        return indexConfigurationRepository.findAll().stream()
                .map(IndexConfigurationMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public IndexConfigurationResponse removeAttributeFromConfiguration(String configurationId, String attributeToAddKey) {
        IndexConfiguration indexConfiguration = indexConfigurationRepository.findById(configurationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid configuration ID"));

        attributeToAddRepository.deleteByAttributeToAddKey(attributeToAddKey);

        indexConfiguration.setUpdateDate(new Date());
        IndexConfiguration savedConfiguration = indexConfigurationRepository.save(indexConfiguration);

        List<PendingMessage> pendingMessages = pendingMessageRepository.findByIndexConfigurationId(configurationId);
        for (PendingMessage pendingMessage : pendingMessages) {
            valueOfAttributeRepository.deleteByAttributeKey(attributeToAddKey);

            System.out.println("Deleted attribute from PendingMessage: " + pendingMessage.getId());
        }

        return IndexConfigurationMapper.mapToDTO(savedConfiguration);
    }

    @Transactional
    public Optional<IndexConfigurationResponse> getConfigurationByMessageCategoryAndSender(String messageCategory, String sender) {
        Optional<IndexConfiguration> configuration = indexConfigurationRepository.findBySenderAndMessageCategory(sender, messageCategory);
        return configuration.map(IndexConfigurationMapper::mapToDTO);
    }
    public boolean doesAttributeExist(String indexConfigurationId) {
        return attributeToAddRepository.existsByIndexConfigurationId(indexConfigurationId);
    }

}
