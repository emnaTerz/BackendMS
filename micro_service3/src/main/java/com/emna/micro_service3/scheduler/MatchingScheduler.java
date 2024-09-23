package com.emna.micro_service3.scheduler;

import com.emna.micro_service3.MatchingProcess.MatchingProcessFactory;
import com.emna.micro_service3.model.MatchingConfiguration;
import com.emna.micro_service3.service.MatchingConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class MatchingScheduler {

    @Autowired
    private MatchingConfigurationService matchingConfigurationService;

    @Autowired
    private MatchingProcessFactory matchingProcessFactory;

    @Scheduled(fixedDelay = 60000) // Run every minute
    public void scheduleMatchingTasks() throws ScriptException, IOException {
        Date now = new Date();
        List<MatchingConfiguration> configurations = matchingConfigurationService.getAllMatchingConfigurationss();

        for (MatchingConfiguration config : configurations) {
            List<Date> scheduleList = config.getScheduleList();

            for (Date scheduleDate : scheduleList) {
                if (isExactScheduledTime(now, scheduleDate)) {
                    performMatching(config);
                }
            }
        }
    }

    private void performMatching(MatchingConfiguration config) throws ScriptException, IOException {
        System.out.println("Performing matching for configuration ID: " + config.getId());

        matchingProcessFactory.getMatchingProcess(config.getMatchingType()).process(config);

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
}
