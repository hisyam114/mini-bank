package com.sam.minibank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {

    @Autowired
    private TimeDepositService timeDepositService;

    // This method will run every day at midnight (00:00)
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTimeDepositStatusesScheduled() {
        timeDepositService.updateTimeDepositStatuses();
    }
}