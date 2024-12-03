package com.sam.minibank.service;

import com.sam.minibank.model.LogDB;
import com.sam.minibank.repository.LogDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogService {

    private final LogDBRepository logDBRepository;

    @Autowired
    public LogService(LogDBRepository logDBRepository) {
        this.logDBRepository = logDBRepository;
    }

    // Method to log an event
    public void log(Long cifNo, String logDesc, String trxType) {
        LogDB logDB = new LogDB(cifNo, logDesc, trxType);  // Create a new log entry
        logDBRepository.save(logDB);  // Save to database
    }

    // Optional: Methods for logging different levels or types of messages
    public void logInfo(Long cifNo, String message, String trxType) {
        log(cifNo, "INFO: " + message, trxType);
    }

    public void logError(Long cifNo, String message, String trxType) {
        log(cifNo, "ERROR: " + message, trxType);
    }

    public void logDebug(Long cifNo, String message, String trxType) {
        log(cifNo, "DEBUG: " + message, trxType);
    }

    // Fetch transaction history for a given CIF number
    public List<LogDB> getTransactionHistory(Long cifNo) {
        // Retrieve all logs for the given CIF number
        return logDBRepository.findByCifNo(cifNo);
    }

    // Fetch transaction history by transaction type
    public List<LogDB> getTransactionHistoryByType(String trxType) {
        return logDBRepository.findByTrxType(trxType);
    }

    // Fetch transaction history by date range
    public List<LogDB> getTransactionHistoryByDateRange(Long cifNo, Date startDate, Date endDate) {
        return logDBRepository.findByCifNoAndLogDateBetween(cifNo, startDate, endDate);
    }

    // Optionally, fetch all logs (useful for admin or auditing purposes)
    public List<LogDB> getAllLogs() {
        return logDBRepository.findAll();
    }
}

