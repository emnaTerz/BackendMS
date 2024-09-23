package com.emna.micro_service4.scheduler;

import com.emna.micro_service4.dto.ReconciliationConfigurationDTO;
import com.emna.micro_service4.mapper.ReconciliationConfigurationMapper;
import com.emna.micro_service4.model.ReconciliationConfiguration;
import com.emna.micro_service4.ReconciliationProcess.ReconciliationProcess;
import com.emna.micro_service4.ReconciliationProcess.ReconciliationProcessFactory;
import com.emna.micro_service4.service.ReconciliationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ReconciliationScheduler {

    @Autowired
    private ReconciliationConfigurationService reconciliationConfigurationService;

    @Autowired
    private ReconciliationProcessFactory reconciliationProcessFactory;

    @Scheduled(fixedDelay = 60000) // Run every minute
    public void scheduleReconciliationTasks() throws ScriptException, IOException {
        Date now = new Date();
        List<ReconciliationConfigurationDTO> configurations = reconciliationConfigurationService.getAllReconciliationConfigurations();

        for (ReconciliationConfigurationDTO dto : configurations) {
            ReconciliationConfiguration config = ReconciliationConfigurationMapper.mapToEntity(dto);
            List<Date> scheduleList = config.getScheduleList();

            for (Date scheduleDate : scheduleList) {
                if (isExactScheduledTime(now, scheduleDate)) {
                    performReconciliation(config);
                    updateLastReconciliationDate(config.getId(), now);
                }
            }
        }
    }

    private void performReconciliation(ReconciliationConfiguration config) throws ScriptException, IOException {
        Date now = new Date();
        if (isDateDue(now, config.getScheduleList(), config.getLastReconciliationDate())) {
            // Perform the reconciliation process
            ReconciliationProcess process = reconciliationProcessFactory.getReconciliationProcess(config.getMatchingConfigurationId());
            process.process(config); // Replace with correct method if `process` is not the right method
        }
    }

    private boolean isExactScheduledTime(Date now, Date scheduledDate) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        nowCal.set(Calendar.MILLISECOND, 0); // Ignore milliseconds

        Calendar scheduledCal = Calendar.getInstance();
        scheduledCal.setTime(scheduledDate);
        scheduledCal.set(Calendar.MILLISECOND, 0); // Ignore milliseconds

        return nowCal.get(Calendar.YEAR) == scheduledCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.MONTH) == scheduledCal.get(Calendar.MONTH) &&
                nowCal.get(Calendar.DAY_OF_MONTH) == scheduledCal.get(Calendar.DAY_OF_MONTH) &&
                nowCal.get(Calendar.HOUR_OF_DAY) == scheduledCal.get(Calendar.HOUR_OF_DAY) &&
                nowCal.get(Calendar.MINUTE) == scheduledCal.get(Calendar.MINUTE);
    }

    private boolean isDateDue(Date now, List<Date> scheduleList, Date lastReconciliationDate) {
        for (Date scheduledDate : scheduleList) {
            if (isExactScheduledTime(now, scheduledDate) &&
                    (lastReconciliationDate == null || lastReconciliationDate.before(scheduledDate))) {
                return true;
            }
        }
        return false;
    }

    private void updateLastReconciliationDate(String configId, Date now) {
        // Call the service method to update the last reconciliation date
        reconciliationConfigurationService.updateLastReconciliationDate(configId, now);
    }
}
