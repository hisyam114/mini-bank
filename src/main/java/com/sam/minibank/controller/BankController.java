package com.sam.minibank.controller;

import com.sam.minibank.DTO.DateRangeRequest;
import com.sam.minibank.DTO.TimeDepositRequest;
import com.sam.minibank.DTO.TransactionTypeRequest;
import com.sam.minibank.model.Bank;
import com.sam.minibank.model.BankTransfers;
import com.sam.minibank.model.LogDB;
import com.sam.minibank.model.TimeDeposit;
import com.sam.minibank.service.BankService;
import com.sam.minibank.service.LogService;
import com.sam.minibank.service.TimeDepositService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bank")
@Data
@Controller
public class BankController {
    @Autowired
    private BankService bankService;

    @Autowired
    private final LogService logService;

    @Autowired
    private TimeDepositService timeDepositService;

    @GetMapping
    public List<Bank> getAllAccount() {
        return bankService.getAllAccount();
    }

    @GetMapping("/{CIF}")
    public ResponseEntity<Bank> getAccByCIF(@PathVariable Long cif) {
        Optional<Bank> bank = bankService.getAccByCIF(cif);
        return bank.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/createAcc")
    public Bank createAcc(@RequestBody Bank bank) {

        return bankService.createAcc(bank);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody Bank bank) {
        try {
            Bank account = bankService.deposit(bank.getCif(), bank.getAmount());
            logService.logInfo(bank.getCif(), "Deposit initiated. Amount: " + bank.getAmount(), "Deposit");

            return ResponseEntity.ok("Deposit successful. New balance: " + account.getAmount());
        } catch (RuntimeException e) {
            logService.logError(bank.getCif(), "Deposit failed. Error: " + e.getMessage(), "Deposit");
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{CIF}")
    public ResponseEntity<Void> deleteAcc(@PathVariable Long cif) {
        bankService.deleteAccByCIF(cif);
        return ResponseEntity.noContent().build();
    }
    // Find bank acc by name
    @GetMapping("/account/{name}")
    public List<Bank> getAccName(@PathVariable String name) {
        return bankService.getAccName(name);
    }

    // Find bank product name (partial match)
    @GetMapping("/product/{prodName}")
    public List<Bank> getProductByName(@PathVariable String prodName) {
        return bankService.getProductByName(prodName);
    }

    @PostMapping("/update/{CIF}")
    public ResponseEntity<String> updateAcc(@PathVariable Long cif, @RequestBody Bank bank) {
        try {
            Bank updatedBank = bankService.updateAcc(cif, bank);
            // Return a success message with the updated bank info
            return ResponseEntity.ok("Account updated successfully. Updated Account: CIF no: " + updatedBank.getCif() + " and Name is"+ updatedBank.getName());
        } catch (RuntimeException e) {
            // If bank is not found, return a 404 error with the appropriate message
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/balanceCheck")
    public Long balanceCheck(@RequestBody Bank bank) {
        return bankService.checkAccAmount(bank);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawal(@RequestBody Bank bank) {
        try {
            Bank cashOut = bankService.withdraw(bank, bank.getAmount());
            Long amountNow = bankService.checkAccAmount(bank);
            logService.logInfo(bank.getCif(), "Withdrawal initiated. Amount: " + bank.getAmount(), "Withdraw");
            // Return a success message with the updated bank info
            return ResponseEntity.ok("Withdraw successfully. Your Amount now is: " + amountNow);
        } catch (RuntimeException e) {
            // If bank is not found, return a 404 error with the appropriate message
            logService.logError(bank.getCif(), "Withdrawal failed. Error: " + e.getMessage(), "Withdraw");
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    /**
     * Transfer funds from one account to another.
     * @param bankTransfers - Transfer details including sender, receiver, and amount.
     * @return ResponseEntity with status and message.
     **/
    @PostMapping("/transfers")
    public ResponseEntity<String> transfers(@RequestBody BankTransfers bankTransfers) {
        try {
            Long trfAmt = bankTransfers.getAmount();
            // Call the service to process the transfer
            Bank updatedBank = bankService.transfers(bankTransfers, trfAmt);
            logService.logInfo(bankTransfers.getCif(), "Transfer initiated to CIF: " + bankTransfers.getCifTo() + ", Amount: " + bankTransfers.getAmount(), "Transfer");

            // Return a success message with the updated sender account information
            Long updatedAmount = updatedBank.getAmount();
            return ResponseEntity.ok("Transfer successful. Sender's updated balance: " + updatedAmount);
        } catch (RuntimeException e) {
            // If any exception occurs, return a failure message with error details
            logService.logError(bankTransfers.getCif(), "Transfer failed to CIF: " + bankTransfers.getCifTo() + ". Error: " + e.getMessage(), "Transfer");

            return ResponseEntity.status(400).body("Transfer failed: " + e.getMessage());
        }
    }

    @PostMapping("/transactionHistory")
    public ResponseEntity<List<LogDB>> getTransactionHistory(@RequestBody Long cifNo) {
        List<LogDB> transactions = logService.getTransactionHistory(cifNo);
        return ResponseEntity.ok(transactions);
    }

    // Endpoint to fetch transaction history by transaction type (e.g., Deposit, Withdrawal)
    @PostMapping("/transactionHistoryByType")
    public ResponseEntity<List<LogDB>> getTransactionHistoryByType(@RequestBody TransactionTypeRequest request) {
        try {
            List<LogDB> transactionHistory = logService.getTransactionHistoryByType(request.getTrxType());
            if (transactionHistory.isEmpty()) {
                return ResponseEntity.notFound().build(); // No logs for the given type
            }
            return ResponseEntity.ok(transactionHistory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // If an error occurs, return a bad request
        }
    }

    // Endpoint to fetch transaction history for a given CIF number within a date range
    @PostMapping("/transactionHistoryByDateRange")
    public ResponseEntity<List<LogDB>> getTransactionHistoryByDateRange(@RequestBody DateRangeRequest dateRangeRequest) {
        try {
            List<LogDB> transactionHistory = logService.getTransactionHistoryByDateRange(
                    dateRangeRequest.getCifNo(),
                    dateRangeRequest.getStartDate(),
                    dateRangeRequest.getEndDate()
            );
            if (transactionHistory.isEmpty()) {
                return ResponseEntity.notFound().build(); // No transactions for the given CIF in the date range
            }
            return ResponseEntity.ok(transactionHistory); // Return the transaction history
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // If an error occurs, return a bad request
        }
    }

    // Create a Time Deposit
    @PostMapping("/time-deposit/create")
    public ResponseEntity<TimeDeposit> createTimeDeposit(@RequestBody TimeDepositRequest request) {
        try {
            TimeDeposit timeDeposit = timeDepositService.createTimeDeposit(
                    request.getCif(),
                    request.getAmount(),
                    request.getInterestRate(),
                    request.getDepositStartDate(),
                    request.getDepositEndDate()
            );
            return ResponseEntity.ok(timeDeposit);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Get Time Deposit by CIF (using POST with request body)
    @PostMapping("/time-deposit")
    public ResponseEntity<List<TimeDeposit>> getTimeDepositsByCIF(@RequestBody Map<String, Long> request) {
        Long cif = request.get("cif");

        if (cif == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<TimeDeposit> timeDeposits = bankService.getTimeDepositsByCIF(cif);

        if (timeDeposits.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(timeDeposits);
    }

    // Get Maturity Amount for Time Deposit
    @GetMapping("/time-deposit/maturity/{id}")
    public ResponseEntity<Long> getMaturityAmount(@PathVariable Long id) {
        try {
            Long maturityAmount = timeDepositService.calculateMaturityAmount(id);
            return ResponseEntity.ok(maturityAmount);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check if Time Deposits are Matured based on CIF
    @PostMapping("/time-deposit/is-matured-by-cif")
    public ResponseEntity<Map<String, Object>> checkIfMaturedByCif(@RequestBody Map<String, Long> request) {
        Long cif = request.get("cif");  // Extract the CIF from the request body

        if (cif == null) {
            return ResponseEntity.badRequest().body(null);  // Return bad request if CIF is not provided
        }

        // Call the service method to check maturity status for time deposits by CIF
        Map<String, Object> response = timeDepositService.checkMaturityStatusByCif(cif);

        if (response == null) {
            return ResponseEntity.notFound().build();  // Return 404 if no time deposits found for CIF
        }

        return ResponseEntity.ok(response);  // Return the map with CIF and status
    }

    // Manually update all time deposit statuses
    @PostMapping("/time-deposit/update-status")
    public ResponseEntity<String> updateAllTimeDepositStatuses() {
        timeDepositService.updateTimeDepositStatuses();
        return ResponseEntity.ok("Time Deposit statuses updated successfully.");
    }

    // Endpoint to get all time deposits for a specific CIF
    @PostMapping("/time-deposit/all-by-cif")
    public ResponseEntity<List<TimeDeposit>> getAllTimeDepositsByCif(@RequestBody Map<String, Long> request) {
        Long cif = request.get("cif");

        if (cif == null) {
            return ResponseEntity.badRequest().body(null); // Return 400 if CIF is not provided
        }

        List<TimeDeposit> timeDeposits = timeDepositService.getTimeDepositsByCif(cif);

        if (timeDeposits.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if no time deposits are found for the given CIF
        }

        return ResponseEntity.ok(timeDeposits); // Return the list of time deposits
    }

//    @GetMapping("/export/csv")
//    public ResponseEntity<byte[]> exportBooksToCSV() {
//        try {
//            byte[] data = bankService.exportBooksToCSV();
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"books.csv\"")
//                    .contentType(MediaType.parseMediaType("text/csv"))
//                    .body(data);
//        } catch (IOException e) {
//            // Handle the error (e.g., return 500 Internal Server Error)
//            return ResponseEntity.status(500).body("Error exporting CSV".getBytes());
//        }
//    }
//    @GetMapping("/export/excel")
//    public ResponseEntity<byte[]> exportBooksToExcel() {
//        try {
//            byte[] data = bankService.exportBooksToExcel();
//
//            // Return the Excel file as a response with appropriate headers for download
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"books.xlsx\"")
//                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
//                    .body(data);
//        } catch (IOException e) {
//            // Handle any exceptions (e.g., IO errors during file generation)
//            return ResponseEntity.status(500).body("Error exporting Excel".getBytes());
//        }
//    }


}
