package com.pdfjet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BigTable {
    private final PDF pdf;
    private Page page;
    private float[] pageSize;
    private final Font f1;
    private final Font f2;
    private float x1;
    private float y1;
    private float yText;
    private List<Page> pages;
    private List<Integer> align;
    private List<Float> vertLines;
    private String[] headerRow;
    private float bottomMargin = 15.0F;
    private float spacing;
    private float padding = 2.0F;
    private String language = "en-US";
    private boolean highlightRow = true;
    private int highlightColor = 15790320;
    private int penColor = 11579568;
    private boolean autoCalcuateColumnWidths = true;
    private int autoCalculateBufferSize = 10;
    private List<String[]> bufferedRows = new ArrayList();
    
    public BigTable(PDF var1, Font var2, Font var3, float[] var4) {
        this.pdf = var1;
        this.f1 = var2;
        this.f2 = var3;
        this.pageSize = var4;
        this.pages = new ArrayList();
    }
    
    public void setLocation(float var1, float var2) {
        this.x1 = var1;
        this.y1 = var2;
    }
    
    public void setTextAlignment(int var1, int var2) {
        this.align.set(var1, var2);
    }
    
    public void setColumnSpacing(float var1) {
        this.spacing = var1;
    }
    
    public void setBottomMargin(float var1) {
        this.bottomMargin = var1;
    }
    
    public void setLanguage(String var1) {
        this.language = var1;
    }
    
    public List<Page> getPages() {
        return this.pages;
    }
    
    public void setColumnWidths(List<Float> var1) {
        this.vertLines = new ArrayList();
        this.vertLines.add(this.x1);
        float var2 = this.x1;
        
        for(Float var4 : var1) {
            var2 += var4 + this.spacing;
            this.vertLines.add(var2);
        }
        
    }
    
    public void drawRow(String[] var1, int var2) throws Exception {
        if (this.autoCalcuateColumnWidths && this.vertLines == null) {
            // Buffer rows until we reach the specified buffer size
            this.bufferedRows.add(var1);
            
            // Once we have enough rows, calculate column widths
            if (this.bufferedRows.size() >= this.autoCalculateBufferSize) {
                calculateColumnWidthsFromBuffer();
            }
            return;
        }
        
        if (this.headerRow == null) {
            this.headerRow = var1;
            this.newPage(var1, 0);
        } else {
            this.drawOn(var1, var2);
        }
    }
    
    private void calculateColumnWidthsFromBuffer() throws Exception {
        if (this.bufferedRows.isEmpty()) {
            return;
        }
        
        ArrayList<Float> widths = new ArrayList<Float>();
        this.align = new ArrayList<Integer>();
        
        // Process each row in the buffer
        for (int rowIndex = 0; rowIndex < this.bufferedRows.size(); rowIndex++) {
            String[] rowData = this.bufferedRows.get(rowIndex);
            processRowData(rowData, widths, rowIndex);
        }
        
        // Set the calculated column widths
        setColumnWidths(widths);
        
        // Draw the buffered rows
        for (String[] row : this.bufferedRows) {
            if (this.headerRow == null) {
                this.headerRow = row;
                this.newPage(row, 0);
            } else {
                this.drawOn(row, 0);
            }
        }
        
        // Clear the buffer
        this.bufferedRows.clear();
    }
    
    public void complete() {
        this.page.addArtifactBMC();
        float[] var1 = this.page.getPenColor();
        this.page.setPenColor(this.penColor);
        this.page.drawLine((Float)this.vertLines.get(0), this.yText - this.f2.ascent, (Float)this.vertLines.get(this.headerRow.length), this.yText - this.f2.ascent);
        
        for(int var2 = 0; var2 <= this.headerRow.length; ++var2) {
            this.page.drawLine((Float)this.vertLines.get(var2), this.y1, (Float)this.vertLines.get(var2), this.yText - this.f1.ascent);
        }
        
        this.page.setPenColor(var1);
        this.page.addEMC();
    }
    
    private void newPage(String[] var1, int var2) throws Exception {
        if (this.page != null) {
            this.page.addArtifactBMC();
            float[] var3 = this.page.getPenColor();
            this.page.setPenColor(this.penColor);
            this.page.drawLine((Float)this.vertLines.get(0), this.yText - this.f1.ascent, (Float)this.vertLines.get(this.headerRow.length), this.yText - this.f1.ascent);
            
            for(int var4 = 0; var4 <= this.headerRow.length; ++var4) {
                this.page.drawLine((Float)this.vertLines.get(var4), this.y1, (Float)this.vertLines.get(var4), this.yText - this.f1.ascent);
            }
            
            this.page.setPenColor(var3);
            this.page.addEMC();
        }
        
        this.page = new Page(this.pdf, this.pageSize, false);
        this.pages.add(this.page);
        this.page.setPenWidth(0.0F);
        this.yText = this.y1 + this.f1.ascent;
        this.page.addArtifactBMC();
        this.drawHighlight(this.page, this.highlightColor, this.f1);
        this.highlightRow = false;
        float[] var9 = this.page.getPenColor();
        this.page.setPenColor(this.penColor);
        this.page.drawLine((Float)this.vertLines.get(0), this.yText - this.f1.ascent, (Float)this.vertLines.get(this.headerRow.length), this.yText - this.f1.ascent);
        this.page.setPenColor(var9);
        this.page.addEMC();
        String var10 = this.getRowText(this.headerRow);
        this.page.addBMC("P", this.language, var10, var10);
        this.page.setTextFont(this.f1);
        this.page.setBrushColor(var2);
        float var5 = 0.0F;
        float var6 = 0.0F;
        
        for(int var7 = 0; var7 < this.headerRow.length; ++var7) {
            String var8 = this.headerRow[var7];
            var5 = (Float)this.vertLines.get(var7);
            var6 = (Float)this.vertLines.get(var7 + 1);
            this.page.beginText();
            if (this.align != null && (Integer)this.align.get(var7) != 0) {
                if ((Integer)this.align.get(var7) == 1) {
                    this.page.setTextLocation(var6 - this.padding - this.f1.stringWidth(var8), this.yText);
                }
            } else {
                this.page.setTextLocation(var5 + this.padding, this.yText);
            }
            
            this.page.drawText(var8);
            this.page.endText();
        }
        
        this.page.addEMC();
        this.yText += this.f1.descent + this.f2.ascent;
    }
    
    private void drawOn(String[] var1, int var2) throws Exception {
        if (var1.length <= this.headerRow.length) {
            this.page.addArtifactBMC();
            if (this.highlightRow) {
                this.drawHighlight(this.page, this.highlightColor, this.f2);
                this.highlightRow = false;
            } else {
                this.highlightRow = true;
            }
            
            float[] var3 = this.page.getPenColor();
            this.page.setPenColor(this.penColor);
            this.page.moveTo((Float)this.vertLines.get(0), this.yText - this.f2.ascent);
            this.page.lineTo((Float)this.vertLines.get(this.headerRow.length), this.yText - this.f2.ascent);
            this.page.strokePath();
            this.page.setPenColor(var3);
            this.page.addEMC();
            String var4 = this.getRowText(var1);
            this.page.addBMC("P", this.language, var4, var4);
            this.page.setPenWidth(0.0F);
            this.page.setTextFont(this.f2);
            this.page.setBrushColor(0);
            float var5 = 0.0F;
            float var6 = 0.0F;
            
            for(int var7 = 0; var7 < var1.length; ++var7) {
                String var8 = var1[var7];
                var5 = (Float)this.vertLines.get(var7);
                var6 = (Float)this.vertLines.get(var7 + 1);
                this.page.beginText();
                if (this.align != null && (Integer)this.align.get(var7) != 0) {
                    if ((Integer)this.align.get(var7) == 1) {
                        this.page.setTextLocation(var6 - this.padding - this.f2.stringWidth(var8), this.yText);
                    }
                } else {
                    this.page.setTextLocation(var5 + this.padding, this.yText);
                }
                
                this.page.drawText(var8);
                this.page.endText();
            }
            
            this.page.addEMC();
            if (var2 != 0) {
                this.page.addArtifactBMC();
                float[] var10 = this.page.getPenColor();
                this.page.setPenColor(var2);
                this.page.setPenWidth(3.0F);
                this.page.drawLine((Float)this.vertLines.get(0) - 2.0F, this.yText - this.f2.ascent, (Float)this.vertLines.get(0) - 2.0F, this.yText + this.f2.descent);
                this.page.drawLine(var6 + 2.0F, this.yText - this.f2.ascent, var6 + 2.0F, this.yText + this.f2.descent);
                this.page.setPenColor(var10);
                this.page.setPenWidth(0.0F);
                this.page.addEMC();
            }
            
            this.yText += this.f2.descent + this.f2.ascent;
            if (this.yText + this.f2.descent > this.page.height - this.bottomMargin) {
                this.newPage(var1, 0);
            }
            
        }
    }
    
    private void drawHighlight(Page var1, int var2, Font var3) {
        float[] var4 = var1.getBrushColor();
        var1.setBrushColor(var2);
        var1.moveTo((Float)this.vertLines.get(0), this.yText - var3.ascent);
        var1.lineTo((Float)this.vertLines.get(this.headerRow.length), this.yText - var3.ascent);
        var1.lineTo((Float)this.vertLines.get(this.headerRow.length), this.yText + var3.descent);
        var1.lineTo((Float)this.vertLines.get(0), this.yText + var3.descent);
        var1.fillPath();
        var1.setBrushColor(var4);
    }
    
    private String getRowText(String[] var1) {
        StringBuilder var2 = new StringBuilder();
        
        for(String var6 : var1) {
            var2.append(var6);
            var2.append(" ");
        }
        
        return var2.toString();
    }
    
    public List<Float> getColumnWidths(String var1) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(var1));
        ArrayList<Float> widths = new ArrayList<Float>();
        this.align = new ArrayList<Integer>();
        int rowCount = 0;
        
        String line;
        for(Object unused = null; (line = reader.readLine()) != null; ++rowCount) {
            String[] rowData = line.split(",");
            processRowData(rowData, widths, rowCount);
        }
        
        reader.close();
        return widths;
    }
    
    private void processRowData(String[] rowData, List<Float> widths, int rowIndex) {
        for(int i = 0; i < rowData.length; ++i) {
            String cell = rowData[i];
            float width = this.f1.stringWidth((Font)null, cell);
            if (rowIndex == 0) {
                widths.add(width);
            } else if (i < widths.size() && width > (Float)widths.get(i)) {
                widths.set(i, width);
            }
        }
        
        if (rowIndex == 1) {
            for(String cell : rowData) {
                this.align.add(this.getAlignment(cell));
            }
        }
    }
    
    private int getAlignment(String var1) {
        StringBuilder var2 = new StringBuilder();
        if (var1.startsWith("(") && var1.endsWith(")")) {
            var1 = var1.substring(1, var1.length() - 1);
        }
        
        for(int var3 = 0; var3 < var1.length(); ++var3) {
            char var4 = var1.charAt(var3);
            if (var4 != '.' && var4 != ',' && var4 != '\'') {
                var2.append(var4);
            }
        }
        
        try {
            Double.parseDouble(var2.toString());
            return 1;
        } catch (NumberFormatException var5) {
            return 0;
        }
    }
    
    public void flushBuffer() throws Exception {
        if (this.autoCalcuateColumnWidths && this.vertLines == null && !this.bufferedRows.isEmpty()) {
            calculateColumnWidthsFromBuffer();
        }
    }
}
