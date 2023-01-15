package com.develop.assignmentPart2.exporter;

import com.develop.assignmentPart2.model.User;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class pdfExporter {
    private List<User> userList;

    // Defining the constructor
    public pdfExporter(List<User> userList){
        this.userList = userList;
    }


    // Creation of Table Header
    private void writeTableHeader(PdfPTable table){
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("USERNAME", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-MAIL", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("ROLE", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("STATUS", font));
        table.addCell(cell);
    }

    // populating the rows and columns with the data from the list
    private void writeTableData(PdfPTable table){
        for(User user: userList){
            table.addCell(String.valueOf(user.getId()));
            table.addCell(String.valueOf(user.getUsername()));
            table.addCell(String.valueOf(user.getEmail()));
            table.addCell(String.valueOf(user.getRole()));
            table.addCell(String.valueOf(user.getStatus()));
        }
    }

    // Setting up a response & creating new document
    public void export(HttpServletResponse response) throws DocumentException, IOException{
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("User List", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 2.5f, 3.5f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);
        document.close();
    }

}
