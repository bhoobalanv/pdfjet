package examples;

import java.io.*;
import java.util.*;
import com.pdfjet.*;

/**
 * Example_43.java
 */
public class BigTableExample
{
    public BigTableExample() throws Exception {
        File file = new File("D:\\Temp\\BigTable.pdf");
        PDF pdf = new PDF(
                new BufferedOutputStream(new FileOutputStream(file)));
        pdf.setCompliance(Compliance.PDF_UA);

         Font f1 = new Font(pdf, CoreFont.HELVETICA_BOLD);
         Font f2 = new Font(pdf, CoreFont.HELVETICA);

        f1.setSize(8f);
        f2.setSize(8f);

        String fileName = "D:/Downloads/PDFjet-ForJava-Eval-v8.0.3/data/Electric_Vehicle_Population_Data.csv";


        BigTable table = new BigTable(pdf, f1, f2, Letter.LANDSCAPE);
        table.setColumnSpacing(7f);
        table.setLocation(20f, 15f);
        table.setBottomMargin(15f);


        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        boolean headerRow = true;
        String line = null;
        int lineNumber = 100;
        while ((line = reader.readLine()) != null && lineNumber-- > 0) {
            String[] fields = line.split(",");
                table.drawRow(fields, Color.black);
            
            headerRow = false;
        }
        table.complete();
        reader.close();

        List<Page> pages = table.getPages();
        for (int i = 0; i < pages.size(); i++) {
            Page page = pages.get(i);
            page.addFooter(new TextLine(f1, "Page " + (i + 1) + " of " + pages.size()));
            pdf.addPage(page);
        }

        pdf.complete();
    }

  
    
    public static void main(String[] args) throws Exception {
        long time0 = System.currentTimeMillis();
        new BigTableExample();
        long time1 = System.currentTimeMillis();
        TextUtils.printDuration("Example_43", time0, time1);
    }
}
