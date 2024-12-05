package com.sam.minibank.service;

import com.sam.minibank.model.*;
import com.sam.minibank.repository.*;
import com.opencsv.CSVWriter;
import com.sam.minibank.repository.*;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Data
public class BankService {
    private final BankRepository bankRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final BankTransfersRepository bankTransfersRepository;
    private final TimeDepositRepository timeDepositRepository;
    private final ProductRepository productRepository;

    @Autowired
    private LedgerRepository ledgerRepository;  // Repository for ledger (cifTo checking)

    private static final Logger logger = Logger.getLogger(BankService.class.getName());

    @Autowired
    public BankService(BankRepository bankRepository
                     , BankTransfersRepository bankTransfersRepository
                     , TransactionHistoryRepository transactionHistoryRepository
                     , TimeDepositRepository timeDepositRepository, ProductRepository productRepository) {
        this.bankRepository = bankRepository;
        this.bankTransfersRepository = bankTransfersRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.timeDepositRepository = timeDepositRepository;
        this.productRepository = productRepository;
    }

    public List<Bank> getAllAccount() {
        return bankRepository.findAll();
    }

    public Optional<Bank> getAccByCIF(Long id) {
        return bankRepository.findById(id);
    }

    public Bank createAcc(Bank bank) {
        // Convert the provided year (from the bank object) into a Date containing only the year
        Date year = bank.getYear();  // Get the year value (assuming it's an int)

        if (year != null) {
            // Create a Calendar instance and set the year to the provided value
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(year);  // Set the date to the first day of the given year
            int yearDate = calendar.get(Calendar.YEAR); // Get the Date object containing only the year

            // Now create a new Date object with only the year
            calendar.set(yearDate, Calendar.JANUARY, 1);  // Set to January 1st of the extracted year
            Date newYearDate = calendar.getTime();  // Create a new Date with the extracted year

            bank.setYear(newYearDate);  // Set the extracted year back to the bank object
        } else {
            // If yearDate is null, you can set a default year (e.g., 2024)
            Calendar calendar = Calendar.getInstance();
            calendar.set(2024, Calendar.JANUARY, 1);  // Default to January 1st, 2024
            Date defaultYearDate = calendar.getTime();
            bank.setYear(defaultYearDate);  // Set the default year to the bank object
        }
        // Validate the product type (Deposito or Saving)
        Product product = productRepository.findByName(bank.getProduct().getProductName()).stream().findFirst().orElseThrow(() ->
                new RuntimeException("Product not found: " + bank.getProduct().getProductName()));

        // Validate if an account with the same NIK and account type already exists
        List<Bank> existingAccounts = bankRepository.findByNikAndType(bank.getNik(), bank.getType());

        if (!existingAccounts.isEmpty()) {
            // If there are accounts with the same NIK and type, throw an exception or return an error
            throw new RuntimeException("An account with the same NIK and account type already exists.");
        }

        return bankRepository.save(bank);
    }

    public void deleteAccByCIF(Long cif) {
        if (bankRepository.existsById(cif)) {
            bankRepository.deleteById(cif);
        }
        else{
            throw new RuntimeException("Account not found with CIF: " + cif);
        }
    }


    // Deposit money into an account
    public Bank deposit(Long cif, Long amount) {
        Bank account = bankRepository.findByCif(cif);
        if (account != null) {
            account.setAmount(account.getAmount() + amount);
            bankRepository.save(account);

            // Log the transaction in the BankTransfers repository
            //bankTransfersRepository.save(new BankTransfers(cif, null, "Deposit", "deposit", amount));
            logger.info("Deposit of " + amount + " to CIF " + cif + " successful. New balance: " + account.getAmount());
            return account;
        } else {
            logger.warning("Account with CIF " + cif + " not found.");
            throw new RuntimeException("Account not found.");
        }
    }

    public Bank updateAcc(Long cif, Bank bank) {
        if (bankRepository.existsById(cif)) {
            // Retrieve the existing book from the repository
            Bank existingBank = bankRepository.findById(cif).get();

            // Update the fields of the existing book with the new values
            existingBank.setProdName(bank.getProdName());
            existingBank.setName(bank.getName());
            // Add other fields if necessary
            logger.info("updateacc // Update Account Successful. New account has update: " + bank.getName() + " and " + bank.getProdName());
            // Save the updated book
            return bankRepository.save(existingBank);
        } else {
            throw new RuntimeException("Account not found with id: " + cif);
        }
    }

    public Bank withdraw(Bank bank, Long amt){
        if (bankRepository.existsById(bank.getCif())) {
            // Retrieve the existing book from the repository
            Bank existingBank = bankRepository.findById(bank.getCif()).get();

            Long amountNow = existingBank.getAmount();
            // Update the fields of the existing book with the new values
            Long amountNext  = amountNow - amt;
            existingBank.setAmount(amountNext);
            logger.info("Withdraw Successful with Amount : " + amountNext + ".");

            // Save the updated book
            return  bankRepository.save(existingBank);
        } else {
            logger.info("Withdraw Unsuccessful. Account not found.");
            throw new RuntimeException("Account not found with id: " + bank.getCif());
        }
    }

    public List<Bank> getAccName(String name) {
        return bankRepository.findByName(name);
    }

    public List<Bank> getProductByName(String prodName) {
        return bankRepository.findByProdName(prodName);
    }

    public Long checkAccAmount(Bank bank){
        if (bankRepository.existsById(bank.getCif())) {
            // Retrieve the existing book from the repository
            Bank existingBank = bankRepository.findById(bank.getCif()).get();
            logger.info("Check account with CIF no: " + bank.getCif() + " successfull ");
            // Save the updated book
            return existingBank.getAmount();
        } else {
            throw new RuntimeException("Account not found with id: " + bank.getCif());
        }
    }

    public Bank transfers(BankTransfers bankTransfer, Long trfAmt) {
        // Check if sender account exists
        if (bankRepository.existsById(bankTransfer.getCif())) {
            // Retrieve sender's bank account information
            Bank existingBank = bankRepository.findById(bankTransfer.getCif()).get();
            Long amountNow = existingBank.getAmount();  // Current sender balance

            // Check if sender has enough balance
            if (amountNow >= trfAmt) {
                // Check if recipient exists in the ledger
                if (ledgerRepository.existsByCif(bankTransfer.getCifTo())) {
                    // Deduct the transfer amount from sender's account
                    Long amountNext = amountNow - trfAmt;
                    existingBank.setAmount(amountNext);
                    bankRepository.save(existingBank);  // Save the updated sender account

                    // Retrieve recipient's account and add the transfer amount
                    Bank recipientBank = bankRepository.findById(bankTransfer.getCifTo()).get();
                    Long recipientAmount = recipientBank.getAmount();
                    recipientBank.setAmount(recipientAmount + trfAmt);
                    bankRepository.save(recipientBank);  // Save the updated recipient account

                    // Log success
                    logger.info("Transfer to CIF: "+ bankTransfer.getCifTo()  +" with Amount: "+ trfAmt + " has been successfully processed.");
                } else {
                    // Log failure if recipient does not exist in the ledger
                    logger.info("Recipient with CIF: " + bankTransfer.getCifTo() + " not found in the ledger.");
                    throw new RuntimeException("Recipient not found in the ledger.");
                }
            } else {
                // Log failure if sender doesn't have sufficient funds
                logger.info("Transfer to CIF: " + bankTransfer.getCifTo() + " failed. Error: Insufficient funds. Available: " + amountNow + ", Required: " + trfAmt);
                throw new RuntimeException("Insufficient funds for the transfer.");
            }

            return existingBank;  // Return the sender's updated bank account
        } else {
            // Log failure if sender account is not found
            logger.info("Sender account with CIF: " + bankTransfer.getCif() + " not found.");
            throw new RuntimeException("Sender account not found.");
        }
    }

    // Fetch transaction history for a given CIF
    public List<TransactionHistory> getTransactionHistory(Long cif) {
        return transactionHistoryRepository.findByCif(cif);
    }

    // Method to retrieve Time Deposits by CIF
    public List<TimeDeposit> getTimeDepositsByCIF(Long cif) {
        return timeDepositRepository.findByCif(cif); // Query TimeDeposit by CIF
    }

    // Calculate interest for a 'Deposito' product
    public Double calculateInterest(Bank bank) {
        if (bank.getProduct().getType().equals("deposit")) {
            double principalAmount = bank.getAmount();
            double annualInterestRate = bank.getProduct().getInterestRate();
            double interest = principalAmount * (annualInterestRate / 12);  // Monthly interest

            return interest;
        }
        return 0.0;  // If it's not a 'deposit', return 0 interest.
    }

    public byte[] exportBooksToCSV() throws IOException {
        // Fetch the list of books
        List<Bank> banks = bankRepository.findAll();

        // Set up CSVWriter with ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(byteArrayOutputStream));

        // Write CSV Header
        csvWriter.writeNext(new String[]{"CIF", "ProdName", "Name", "Amount", "Year"});

        // Write book data rows
        for (Bank bank : banks) {
            csvWriter.writeNext(new String[]{
                    bank.getCif().toString(),
                    bank.getProdName(),
                    bank.getName(),
                    bank.getAmount().toString(),
                    String.valueOf(bank.getYear())
            });
        }

        // Close the writer
        csvWriter.close();

        // Return the CSV data as a byte array
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] exportBooksToExcel() throws IOException {
        List<Bank> banks = bankRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Title");
        headerRow.createCell(2).setCellValue("Author");
        headerRow.createCell(3).setCellValue("Publisher");
        headerRow.createCell(4).setCellValue("Year");

        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Add book data rows
        int rowNum = 1;
        for (Bank bank : banks) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bank.getCif());
            row.createCell(1).setCellValue(bank.getProdName());
            row.createCell(2).setCellValue(bank.getName());
        }

        // Create a bold font style for the header row
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        // Set background color for header (light gray)
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        // Apply style to each cell in the header row
        for (Cell cell : headerRow) {
            cell.setCellStyle(style);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        return byteArrayOutputStream.toByteArray();
    }
}
