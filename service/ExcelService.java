package service;

import model.Medicine;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExcelService {
    private static final String FILE_PATH = "resources/inventory.xlsx";

    // LOAD data from Excel
    public static List<Medicine> loadInventory() {
        List<Medicine> medicines = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) 
                	continue;

                String name = row.getCell(0).getStringCellValue();
                String category = row.getCell(1).getStringCellValue();
                double price = row.getCell(2).getNumericCellValue();
                int qty = (int) row.getCell(3).getNumericCellValue();

                // Safe ExpiryDate handling
                Cell expiryCell = row.getCell(4);
                LocalDate expiry;
                if (expiryCell.getCellType() == CellType.STRING) {
                    expiry = LocalDate.parse(expiryCell.getStringCellValue());
                } else {
                    expiry = expiryCell.getLocalDateTimeCellValue().toLocalDate();
                }

                medicines.add(new Medicine(name, category, price, qty, expiry));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return medicines;
    }

    // SAVE updated data back to Excel
    public static void saveInventory(List<Medicine> medicines) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventory");

            // Create header
            Row header = sheet.createRow(0);
            String[] columns = {"Name", "Category", "Price", "Quantity", "ExpiryDate"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // Populate data
            int rowIndex = 1;
            for (Medicine med : medicines) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(med.getName());
                row.createCell(1).setCellValue(med.getCategory());
                row.createCell(2).setCellValue(med.getPrice());
                row.createCell(3).setCellValue(med.getQuantity());
                row.createCell(4).setCellValue(med.getExpiryDate().toString());
            }

            // Write to file
            try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
