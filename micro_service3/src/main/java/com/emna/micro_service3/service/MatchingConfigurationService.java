

package com.emna.micro_service3.service;

import com.emna.micro_service3.Repository.AttributesToMatchRepository;
import com.emna.micro_service3.Repository.MatchingConfigurationRepository;
import com.emna.micro_service3.Repository.MatchingResultsRepository;
import com.emna.micro_service3.model.AttributesToMatch;
import com.emna.micro_service3.model.MatchingConfiguration;
import com.emna.micro_service3.model.MatchingResult;
import com.emna.micro_service3.model.enums.MatchingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MatchingConfigurationService {

    @Autowired
    private MatchingConfigurationRepository repository;
    @Autowired
    private AttributesToMatchRepository attributesToMatchRepository;
    @Autowired
    private MatchingResultsRepository matchingResultRepository;

    @Transactional
    public MatchingConfiguration createMatchingConfiguration(String sourceId, String targetId, String name, MatchingType matchingType, List<Date> scheduleList) {
        // Check if a matching configuration already exists with the same sourceId, targetId, and matchingType
        List<MatchingConfiguration> existingConfigs = repository.findBySourceIdAndTargetIdAndMatchingType(sourceId, targetId, matchingType);

        if (!existingConfigs.isEmpty()) {
            throw new IllegalArgumentException("A matching configuration with the same sourceId, targetId, and matchingType already exists.");
        }

        MatchingConfiguration matchingConfiguration = new MatchingConfiguration(
                UUID.randomUUID().toString(),
                sourceId,
                targetId,
                name,
                matchingType,
                new Date(), // Creation date
                new Date(), // Update date
                scheduleList
        );

        return repository.save(matchingConfiguration);
    }

    @Transactional
    public MatchingConfiguration updateMatchingConfiguration(String id, String sourceId, String targetId, String name, MatchingType matchingType, List<Date> scheduleList) {
        MatchingConfiguration existingConfiguration = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No matching configuration found with ID: " + id));

        existingConfiguration.setSourceId(sourceId);
        existingConfiguration.setTargetId(targetId);
        existingConfiguration.setName(name);
        existingConfiguration.setMatchingType(matchingType);
        existingConfiguration.setUpdateDate(new Date());  // Set new update date
        existingConfiguration.setScheduleList(scheduleList);

        return repository.save(existingConfiguration);
    }

    @Transactional
    public Page<MatchingConfiguration> getAllMatchingConfigurations(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }

    @Transactional
    public List<MatchingConfiguration> getAllMatchingConfigurationss() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }


    @Transactional
    public Optional<MatchingConfiguration> getMatchingConfigurationById(String id) {
        return repository.findById(id);
    }


/*  @Transactional
  public void deleteMatchingConfiguration(String id) {
      // Retrieve the configuration to ensure it exists
      MatchingConfiguration configuration = repository.findById(id)
              .orElseThrow(() -> new IllegalArgumentException("No matching configuration found with ID: " + id));

      // Fetch all associated AttributesToMatch entries by matchingConfigurationId
      List<AttributesToMatch> attributesList = attributesToMatchRepository.findByMatchingConfigurationId(id);

      // Check if there are any attributes to delete
      if (!attributesList.isEmpty()) {
          // Delete all associated AttributesToMatch entries
          attributesToMatchRepository.deleteAll(attributesList);
          System.out.println("Deleted " + attributesList.size() + " AttributesToMatch entries associated with matchingConfigurationId: " + id);
      } else {
          System.out.println("No AttributesToMatch entries found for matchingConfigurationId: " + id);
      }

      // Delete the MatchingConfiguration
      repository.delete(configuration);
      System.out.println("Deleted MatchingConfiguration with ID: " + id);
  }*/
@Transactional
public void deleteMatchingConfiguration(String id) {
    // Retrieve the MatchingConfiguration to ensure it exists
    MatchingConfiguration configuration = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No matching configuration found with ID: " + id));

    // Fetch all associated AttributesToMatch entries by matchingConfigurationId
    List<AttributesToMatch> attributesList = attributesToMatchRepository.findByMatchingConfigurationId(id);

    // Check if there are any attributes to delete
    if (!attributesList.isEmpty()) {
        // Delete all associated AttributesToMatch entries
        attributesToMatchRepository.deleteAll(attributesList);
        System.out.println("Deleted " + attributesList.size() + " AttributesToMatch entries associated with matchingConfigurationId: " + id);
    } else {
        System.out.println("No AttributesToMatch entries found for matchingConfigurationId: " + id);
    }

    // Fetch all associated MatchingResult entries by matchingConfigurationId
    List<MatchingResult> matchingResults = matchingResultRepository.findByMatchingConfigurationId(id);

    // Check if there are any matching results to delete
    if (!matchingResults.isEmpty()) {
        // Delete all associated MatchingResult entries
        matchingResultRepository.deleteAll(matchingResults);
        System.out.println("Deleted " + matchingResults.size() + " MatchingResult entries associated with matchingConfigurationId: " + id);
    } else {
        System.out.println("No MatchingResult entries found for matchingConfigurationId: " + id);
    }

    // Finally, delete the MatchingConfiguration
    repository.delete(configuration);
    System.out.println("Deleted MatchingConfiguration with ID: " + id);
}

}
