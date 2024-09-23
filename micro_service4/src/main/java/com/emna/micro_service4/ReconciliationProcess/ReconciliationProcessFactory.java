
package com.emna.micro_service4.ReconciliationProcess;

import com.emna.micro_service4.client.MatchingResultsClient;
import com.emna.micro_service4.model.MatchingConfiguration;
import com.emna.micro_service4.model.enums.MatchingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReconciliationProcessFactory {

    @Autowired
    private OneToOneReconciliationProcess oneToOneReconciliationProcess;

    @Autowired
    private OneToManyReconciliationProcess oneToManyReconciliationProcess;

    @Autowired
    private ManyToOneReconciliationProcess manyToOneReconciliationProcess;

    @Autowired
    private ManyToManyReconciliationProcess manyToManyReconciliationProcess; // Add this line

    @Autowired
    private MatchingResultsClient matchingResultsClient;

    public ReconciliationProcess getReconciliationProcess(String matchingConfigurationId) {
        Optional<MatchingConfiguration> matchingConfigurationOpt = matchingResultsClient.getMatchingConfigurationById(matchingConfigurationId);

        if (matchingConfigurationOpt.isPresent()) {
            MatchingConfiguration matchingConfiguration = matchingConfigurationOpt.get();
            com.emna.micro_service3.model.enums.MatchingType externalMatchingType = matchingConfiguration.getMatchingType();

            MatchingType matchingType = convertToLocalMatchingType(externalMatchingType);

            switch (matchingType) {
                case one_to_one:
                    return oneToOneReconciliationProcess;
                case one_to_many:
                    return oneToManyReconciliationProcess;
                case many_to_one:
                    return manyToOneReconciliationProcess;
                case many_to_many: // Add this case for many-to-many
                    return manyToManyReconciliationProcess;
                default:
                    throw new IllegalArgumentException("Unsupported matching type: " + matchingType);
            }
        } else {
            throw new IllegalArgumentException("Matching Configuration not found for ID: " + matchingConfigurationId);
        }
    }

    private MatchingType convertToLocalMatchingType(com.emna.micro_service3.model.enums.MatchingType externalMatchingType) {
        switch (externalMatchingType) {
            case one_to_one:
                return MatchingType.one_to_one;
            case one_to_many:
                return MatchingType.one_to_many;
            case many_to_one:
                return MatchingType.many_to_one;
            case many_to_many:
                return MatchingType.many_to_many;
            default:
                throw new IllegalArgumentException("Unsupported external matching type: " + externalMatchingType);
        }
    }
}
