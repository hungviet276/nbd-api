package com.neo.nbdapi.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ExcelUtils {
     public static ByteArrayInputStream write2File(List<Map> list, String fileIn, int sheetAt, int styleAt) {
        try (Workbook workbook = new XSSFWorkbook(new ClassPathResource(fileIn).getInputStream());
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.getSheetAt(sheetAt);
            Font font = workbook.getFontAt(styleAt);
            CellStyle headerStyle = workbook.getCellStyleAt(styleAt);
            headerStyle.setFont(font);


            Row template = sheet.getRow(styleAt);
            int cols = template.getLastCellNum();
            CellStyle[] headerStyles = new CellStyle[cols];

            String[] dataHeaders = new String[cols];
            for (int col = 0; col < cols; col++) {
                dataHeaders[col] = template.getCell(col).getStringCellValue().trim();
                headerStyles[col] = template.getCell(col).getCellStyle();
                headerStyles[col].setFont(font);
            }
            int rowNum = styleAt;
            int no = 1;
            for (Map item : list) {
                int colNum = 0;
                Row row = sheet.createRow(rowNum);
                for (String col : dataHeaders) {
                    if ("stt".equals(col)) {
                        Cell cell = row.createCell(colNum);
//                        cell.setCellStyle(headerStyle);
                        cell.setCellStyle(headerStyles[colNum]);
                        cell.setCellValue(no);
                    } else {
                        if (item.get(col) != null) {
                            Cell cell = row.createCell(colNum);
                            cell.setCellStyle(headerStyles[colNum]);
                            cell.setCellValue(item.get(col).toString());
                        }else{
                            Cell cell = row.createCell(colNum);
                            cell.setCellStyle(headerStyles[colNum]);
                        }
                    }
                    colNum++;
                }
                rowNum++;
                no++;
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
