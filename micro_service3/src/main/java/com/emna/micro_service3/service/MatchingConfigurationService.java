/*package com.emna.micro_service3.service;

import com.emna.micro_service3.Repository.AttributesToMatchRepository;
import com.emna.micro_service3.Repository.MatchingConfigurationRepository;
import com.emna.micro_service3.model.AttributesToMatch;
import com.emna.micro_service3.model.MatchingConfiguration;
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

@Service
public class MatchingConfigurationService {

    @Autowired
    private MatchingConfigurationRepository repository;
    @Autowired
    private AttributesToMatchRepository attributesToMatchRepository;

    @Transactional
    public MatchingConfiguration createMatchingConfiguration(String sourceId, String targetId, String name, MatchingType matchingType, List<Date> scheduleList) {
        // Check if a matching configuration already exists
        List<MatchingConfiguration> existingConfigs = repository.findBySourceIdAndTargetId(sourceId, targetId);

        if (!existingConfigs.isEmpty()) {
            throw new IllegalArgumentException("A matching configuration with the same sourceId and targetId already exists.");
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
    public Optional<MatchingConfiguration> getMatchingConfigurationById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public void deleteMatchingConfiguration(String id) {
        // Retrieve the configuration to ensure it exists
        MatchingConfiguration configuration = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No matching configuration found with ID: " + id));

        // Delete all associated AttributesToMatch entries

        List<AttributesToMatch> attributesList = attributesToMatchRepository.findByMatchingConfigurationId(id);
        attributesToMatchRepository.deleteAll(attributesList);

        // Delete the MatchingConfiguration
        repository.delete(configuration);
    }

}
*/

package com.emna.micro_service3.service;

import com.emna.micro_service3.Repository.AttributesToMatchRepository;
import com.emna.micro_service3.Repository.MatchingConfigurationRepository;
import com.emna.micro_service3.model.AttributesToMatch;
import com.emna.micro_service3.model.MatchingConfiguration;
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

@Service
public class MatchingConfigurationService {

    @Autowired
    private MatchingConfigurationRepository repository;
    @Autowired
    private AttributesToMatchRepository attributesToMatchRepository;

    @Transactional
    public MatchingConfiguration createMatchingConfiguration(String sourceId, String targetId, String name, MatchingType matchingType, List<Date> scheduleList) {
        // Check if a matching configuration already exists
        List<MatchingConfiguration> existingConfigs = repository.findBySourceIdAndTargetId(sourceId, targetId);

        if (!existingConfigs.isEmpty()) {
            throw new IllegalArgumentException("A matching configuration with the same sourceId and targetId already exists.");
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
    public Optional<MatchingConfiguration> getMatchingConfigurationById(String id) {
        return repository.findById(id);
    }


  @Transactional
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
  }

}
