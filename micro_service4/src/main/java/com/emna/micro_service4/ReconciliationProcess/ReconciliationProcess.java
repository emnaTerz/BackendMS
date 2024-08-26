package com.emna.micro_service4.ReconciliationProcess;

import com.emna.micro_service4.model.ReconciliationConfiguration;

import javax.script.ScriptException;
import java.io.IOException;

public interface ReconciliationProcess {
    void process(ReconciliationConfiguration reconciliationConfiguration) throws ScriptException, IOException;
}
