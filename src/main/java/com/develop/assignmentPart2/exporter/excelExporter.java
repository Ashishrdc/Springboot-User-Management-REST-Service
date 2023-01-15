package com.develop.assignmentPart2.exporter;

import com.develop.assignmentPart2.model.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class excelExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<User> userList;

    // Constructor and passing the list to the class
    public excelExporter(List<User> userList){
        this.userList = userList;
        workbook = new XSSFWorkbook();
    }

    // Writing the header
    private void writeHeaderLine(){
        // Creating sheet
        sheet = workbook.createSheet("Users");

        // Creating row
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        // Creating cell //column and formatting
        // Calling the createCell method of this class
        createCell(row, 0, "ID", style);
        createCell(row, 1, "USERNAME", style);
        createCell(row, 2, "EMAIL", style);
        createCell(row, 3, "ROLE", style);
        createCell(row, 4, "STATUS", style);
    }

    // createCell

    private void createCell(Row row, int columnCount, Object value, CellStyle style){
        sheet.autoSizeColumn(columnCount); //Creates columns and adjusts according to the length of the string

        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    // Writing DATA
    private void writeDataLines(){
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);

        for(User user: userList){
            Row row = sheet.createRow(rowCount++);
            int columnCount=0;

          //createCell(row number, column number, "USER DATA", formatting)
            createCell(row, columnCount++, user.getId(), style);
            createCell(row, columnCount++, user.getUsername(), style);
            createCell(row, columnCount++, user.getEmail(), style);
            createCell(row, columnCount++, user.getRole(), style);
            createCell(row, columnCount, user.getStatus(), style);
        }
    }
    public void export(HttpServletResponse response) throws IOException{
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
