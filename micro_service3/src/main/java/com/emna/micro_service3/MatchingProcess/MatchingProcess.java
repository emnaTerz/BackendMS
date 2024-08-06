package com.emna.micro_service3.MatchingProcess;

import com.emna.micro_service3.model.MatchingConfiguration;

import javax.script.ScriptException;
import java.io.IOException;

public interface MatchingProcess {
    void process(MatchingConfiguration matchingConfiguration) throws ScriptException, IOException;
}
