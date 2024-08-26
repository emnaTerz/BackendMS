package com.emna.micro_service3.MatchingProcess;

import com.emna.micro_service3.model.enums.MatchingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchingProcessFactory {

    @Autowired
    private OneToOneMatchingProcess oneToOneMatchingProcess;
    @Autowired
    private OneToManyMatchingProcess oneToManyMatchingProcess;
    @Autowired
    private  ManyToOneMatchingProcess manyToOneMatchingProcess;
    @Autowired
    private MatchingProcess manyToManyMatchingProcess;
    public MatchingProcess getMatchingProcess(MatchingType matchingType) {
        switch (matchingType) {
            case one_to_one:
                return oneToOneMatchingProcess;
            case one_to_many:
                return oneToManyMatchingProcess;
            case many_to_one:

                return manyToOneMatchingProcess;
            case many_to_many:

                return manyToManyMatchingProcess;
            default:
                throw new IllegalArgumentException("Unsupported matching type: " + matchingType);
        }
    }
}
