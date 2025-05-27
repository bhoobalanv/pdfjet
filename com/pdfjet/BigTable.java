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
    private float maxWidth = 200.0F;
    
    public BigTable(PDF var1, Font var2, Font var3, float[] var4) {
        this(var1, var2, var3);
        this.pageSize = var4;
        
    }
    
    public BigTable(PDF var1, Font var2, Font var3) {
        this.pdf = var1;
        this.f1 = var2;
        this.f2 = var3;
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
    
    public void setAutoCalcuateColumnWidths(boolean var1)
    {
        this.autoCalcuateColumnWidths = var1;
    } // autoCalcuateColumnWidths
    
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
            this.newPage( 0);
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
        
        // Automatically calculate and set page size based on column widths
        float totalWidth = 0f;
        for (Float w : widths) {
            totalWidth += w;
        }
        totalWidth += (widths.size() - 1) * this.spacing + 2 * this.x1; // Add spacing and left/right margins
        if (this.pageSize != null && this.pageSize.length == 2) {
            this.pageSize[0] = totalWidth;
        } else {
            this.pageSize = new float[] { totalWidth, 612f }; // Default height if not set
        }
        
        // Draw the buffered rows
        for (String[] row : this.bufferedRows) {
            drawRow(row, 0);
        }
        
        // Clear the buffer
        this.bufferedRows.clear();
    }
    
    public void complete() {
       try {
           flushBuffer();
       }catch (Exception e) {
           throw new RuntimeException("Failed to flush buffer", e);
       }
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
    
    private void newPage( int var2) throws Exception {
       
       
        if (this.page != null) {
            this.page.addArtifactBMC();
            float[] var3 = this.page.getPenColor();
            this.page.setPenColor(this.penColor);
            //bottom border line
            this.page.drawLine((Float)this.vertLines.get(0), this.yText - this.f1.ascent +padding, (Float)this.vertLines.get(this.headerRow.length), this.yText - this.f1.ascent +padding);
            
            for(int var4 = 0; var4 <= this.headerRow.length; ++var4) {
                //vertical lines
                this.page.drawLine((Float)this.vertLines.get(var4), this.y1, (Float)this.vertLines.get(var4), this.yText - this.f1.ascent+padding);
             }

            this.page.setPenColor(var3);
            this.page.addEMC();
        }
        
        this.page = new Page(this.pdf, this.pageSize, false);
        this.pages.add(this.page);
        this.page.setPenWidth(0.0F);
        this.yText = this.y1 + this.f1.ascent;
        this.page.addArtifactBMC();
        
        // --- Begin wrapping logic ---
        List<List<String>> wrappedCells = new ArrayList<>();
        float[] wrapMeasures = getWrappedCells(wrappedCells, this.headerRow);
        
        
        this.drawHighlight(this.page, this.highlightColor, this.f1, wrapMeasures[2],-1f);
        this.highlightRow = false;
        float[] var9 = this.page.getPenColor();
        this.page.setPenColor(this.penColor);
        //Table top border
        this.page.drawLine((Float)this.vertLines.get(0) , this.yText - this.f1.ascent, (Float)this.vertLines.get(this.headerRow.length), this.yText - this.f1.ascent );
        
        this.page.setPenColor(var9);
        this.page.addEMC();
        String var10 = this.getRowText(this.headerRow);
        this.page.addBMC("P", this.language, var10, var10);
        this.page.setTextFont(this.f1);
        this.page.setBrushColor(var2);
        

        float lineHeight = this.f1.getBodyHeight();
        
        drawWrappedCells(this.headerRow, wrappedCells, wrapMeasures[0], this.f1, lineHeight);
        // --- End wrapping logic ---
        
        this.page.addEMC();
        this.yText += this.f1.descent + this.f2.ascent + wrapMeasures[2];
    }
    
    private void drawOn(String[] row, int var2) throws Exception {
        if (row.length <= this.headerRow.length) {
            
            List<List<String>> wrappedCells = new ArrayList<>();
            float[] wrapMeasures = getWrappedCells(wrappedCells, row);
           processRows(wrappedCells, wrapMeasures, row, var2);
        }
    }
    
    private void processRows(List<List<String>> wrappedCells, float[] wrapMeasures, String[]row, int var2) throws Exception
    {
        if (this.yText + wrapMeasures[2] > this.page.height - this.bottomMargin) {
            float balaneSpace = this.page.height - this.bottomMargin - this.yText;
            float linesLeft = wrapMeasures[0];
            float rowHeight = this.f2.getBodyHeight();
            float linesCanWritten = balaneSpace / rowHeight;
            System.out.println("Lines can be written: " + linesCanWritten + ", Lines left: " + linesLeft);
            
            int round = Math.round(linesCanWritten);
            List<List<String>> trimmedWrappedCells = new ArrayList<>();
            List<List<String>> unwritten = new ArrayList<>();
            int unwrittenMaxLines = 1;
            for (int i = 0;  i < wrappedCells.size(); i++) {
                List<String> strings = wrappedCells.get(i);
                if (strings.size() > round) {
                    List<String> trimmed = strings.subList(0, round+1);
                    List<String> strings1 = strings.subList(round+1, strings.size());
                    trimmedWrappedCells.add(trimmed);
                    unwritten.add(strings1);
                    if (strings1.size() > unwrittenMaxLines) {
                        unwrittenMaxLines = strings1.size();
                    }
                }
                else {
                    trimmedWrappedCells.add(strings);
                    unwritten.add(new ArrayList<>());
                }
            }
            this.drawRowContent(trimmedWrappedCells, wrapMeasures, row, var2, round);
            if(unwrittenMaxLines > 0) {
                wrapMeasures[0] = unwrittenMaxLines;
                wrapMeasures[1] = unwrittenMaxLines * rowHeight + this.padding ;
                wrapMeasures[2] = (unwrittenMaxLines - 1) * rowHeight + padding;
                processRows(unwritten, wrapMeasures, row, var2);
            }
        }
        else
        {
            this.drawRowContent(wrappedCells, wrapMeasures, row, var2,-1);
        }
        
    }
    
    private void drawRowContent(List<List<String>> wrappedCells, float[] wrapMeasures, String[] row, int var2, int linesCanBeWritten) throws Exception
    {
        this.page.addArtifactBMC();
        
        float lineHeight = this.f2.getBodyHeight();
        if (this.highlightRow) {
            
            this.drawHighlight(this.page, this.highlightColor, this.f2, wrapMeasures[2],padding);
            this.highlightRow = false;
        } else {
            this.highlightRow = true;
        }
        float[] var3 = this.page.getPenColor();
        this.page.setPenColor(this.penColor);
        this.page.moveTo((Float)this.vertLines.get(0), this.yText - this.f2.ascent+padding);
        this.page.lineTo((Float)this.vertLines.get(this.headerRow.length), this.yText - this.f2.ascent+padding);
        this.page.strokePath();
        this.page.setPenColor(var3);
        this.page.addEMC();
        String var4 = this.getRowText(row);
        // this.page.addBMC("P", this.language, var4, var4);
        this.page.setPenWidth(0.0F);
        this.page.setTextFont(this.f2);
        this.page.setBrushColor(0);
        
        drawWrappedCells(row, wrappedCells, wrapMeasures[0], this.f2, lineHeight);
        // --- End wrapping logic ---
        
        this.page.addEMC();
        if (var2 != 0) {
            this.page.addArtifactBMC();
            float[] var10 = this.page.getPenColor();
            this.page.setPenColor(var2);
            this.page.setPenWidth(3.0F);
            float var6 = (Float) this.vertLines.get(row.length);
            this.page.drawLine((Float) this.vertLines.get(0) - 2.0F, this.yText - this.f2.ascent, (Float) this.vertLines.get(0) - 2.0F, this.yText + wrapMeasures[2] + 2.0F);
            this.page.drawLine(var6 + 2.0F, this.yText - this.f2.ascent +padding, var6 + 2.0F, this.yText + wrapMeasures[2] + 2.0F);
            this.page.setPenColor(var10);
            this.page.setPenWidth(0.0F);
            this.page.addEMC();
        }
        
        this.yText += wrapMeasures[1];
        if (this.yText + this.f2.descent > this.page.height - this.bottomMargin) {
            this.newPage( 0);
        }
    }
    
    /**
     * Draws wrapped cells on the current page
     * @param row The row data to draw
     * @param wrappedCells The list of wrapped cell content
     * @param wrapMeasures Measurements for wrapped content [maxLines, rowHeight, addedHeight]
     * @param font The font to use for drawing
     * @param lineHeight The line height for text
     */
    private void drawWrappedCells(String[] row, List<List<String>> wrappedCells, float maxLines, Font font, float lineHeight) {
        for (int line = 0; line < maxLines; ++line) {
            for (int var7 = 0; var7 < row.length; ++var7) {
                float var5 = (Float) this.vertLines.get(var7);
                float var6 = (Float) this.vertLines.get(var7 + 1);
               
                String text = line < wrappedCells.get(var7).size() ? (wrappedCells.get(var7).get(line) != null ? wrappedCells.get(var7).get(line) : "-") : null;
                if( text == null || text.isEmpty()) {
                    //this.page.endText();
                    continue;
                }
                this.page.beginText();
                if (this.align != null && (Integer) this.align.get(var7) != 0) {
                    if ((Integer) this.align.get(var7) == 1) {
                        this.page.setTextLocation(var6 - this.padding - font.stringWidth(text), this.yText + line * lineHeight+padding);
                    }
                } else {
                    this.page.setTextLocation(var5 + this.padding, this.yText + line * lineHeight+padding);
                }
                this.page.drawText(text);
                this.page.endText();
            }
        }
    }
    
    /**
     * Computes the measurements for the wrapped cells
     * @param wrappedCells The list of wrapped cell content
     * @param row The row data to wrap
     * @return An array of measurements for the wrapped content [maxLines, rowHeight, addedHeight]
     */
    private float[] getWrappedCells(List<List<String>> wrappedCells, String[] row) {
        float maxLines = 1;
        for (int i = 0; i < row.length; ++i) {
            String cell = row[i];
            float colWidth = (Float) this.vertLines.get(i + 1) - (Float) this.vertLines.get(i) - 2 * this.padding;
            List<String> wrapped = wrapText(cell, this.f2, colWidth);
            wrappedCells.add(wrapped);
            if (wrapped.size() > maxLines) maxLines = wrapped.size();
        }
        float lineHeight = this.f2.getBodyHeight();
        float rowHeight = maxLines * lineHeight + this.padding ;
        float addedHeight = (maxLines - 1) * lineHeight + padding;
        return new float[]{maxLines, rowHeight, addedHeight};
    }
    
    
    private List<String> wrapText(String text, Font font, float maxWidth) {
        List<String> lines = new ArrayList<>();
        
        // If text is empty, return empty list
        if (text == null || text.isEmpty()) {
            return lines;
        }
        
        // Check if the entire text exceeds maxWidth
        if (font.stringWidth(null, text) <= maxWidth) {
            // Text fits in one line, no need to wrap
            lines.add(text);
            return lines;
        }
        
        // Text needs wrapping - calculate approximate characters that fit per line
        float avgCharWidth = font.stringWidth(null, "m"); // Use 'm' as an average character
        int charsPerLine = Math.max(1, (int)(maxWidth / avgCharWidth));
        
        // Process text character by character to create lines
        int textLength = text.length();
        int startPos = 0;
        
        while (startPos < textLength) {
            // Initial estimate of end position
            int endPos = Math.min(startPos + charsPerLine, textLength);
            
            // If we're not at the end of text, try to break at a space
            if (endPos < textLength) {
                // Look for the last space within our estimated range
                int lastSpace = text.lastIndexOf(' ', endPos);
                
                // If we found a space in our range, break there
//                if (lastSpace > startPos) {
//                    endPos = lastSpace;
//                } else {
                    // No space found, check if the current segment fits
                    String segment = text.substring(startPos, endPos);
                    
                    // Fine-tune to ensure the segment fits
                     float segmentWidth = font.stringWidth(null, segment);
                     if( segmentWidth > maxWidth) {
                        // If the segment is too wide, reduce endPos until it fits
                        while (endPos > startPos + 1 && segmentWidth > maxWidth) {
                            endPos--;
                            segment = text.substring(startPos, endPos);
                            segmentWidth = font.stringWidth(null, segment);
                        }
                    }else {
                        
                        
                        // If the segment fits, reduce endPos until it doesn't
                             if (endPos > startPos + 1 && (segmentWidth) <= maxWidth) {
                                 float diff = maxWidth - segmentWidth;
                                 float v = segmentWidth / segment.length();
                                 endPos =Math.min( endPos + Math.round(diff / v) - 1, textLength);
                                 segment = endPos < textLength ? text.substring(startPos, endPos) : text.substring(startPos);
                                 segmentWidth = font.stringWidth(null, segment);
                             }
                             //TODO if only one spcae is present and the word is too long to hold in single, the word in break after inserting new line.
                            //we can break along with the single word itself.
                             if(segmentWidth > maxWidth || textLength>endPos+1&& Character.isLetter(text.charAt(endPos+1))) {
                                 int lastSpaceIndex = segment.lastIndexOf(" ");
                                 if (lastSpaceIndex != -1) {
                                     endPos = startPos + lastSpaceIndex;
                                 }
                             }
                    }
                    
                  
//                }
            }
            
            // Add the line
            String line = text.substring(startPos, endPos).trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
            
            // Move to next position (skip the space if we broke at one)
            startPos = (endPos < textLength && text.charAt(endPos) == ' ') ? endPos + 1 : endPos;
        }
        
        return lines;
    }
    
    // Helper method to wrap text within a given width
//    private List<String> wrapText(String text, Font font, float maxWidth) {
//        List<String> lines = new ArrayList<>();
//
//        // Calculate approximate characters that fit per line based on font metrics
//        float avgCharWidth = font.stringWidth(null, "m"); // Use 'm' as an average character
//        int charsPerLine = Math.max(1, (int)(maxWidth / avgCharWidth));
//
//        // Split text into words
//        String[] words = text.split(" ");
//        StringBuilder currentLine = new StringBuilder();
//
//        for (String word : words) {
//            // If current line is empty, try to add the word
//            if (currentLine.length() == 0) {
//                // Check if the word itself exceeds maxWidth
//                if (font.stringWidth(null, word) > maxWidth) {
//                    // Break the word into chunks that fit
//                    breakWordIntoLines(word, font, maxWidth, charsPerLine, lines);
//                } else {
//                    currentLine.append(word);
//                }
//            } else {
//                // Try adding the word to the current line
//                String testLine = currentLine + " " + word;
//                if (font.stringWidth(null, testLine) <= maxWidth) {
//                    currentLine.append(" ").append(word);
//                } else {
//                    // Current line is full, add it to lines and start a new line
//                    lines.add(currentLine.toString());
//                    currentLine = new StringBuilder();
//
//                    // Now handle the word for the new line
//                    if (font.stringWidth(null, word) > maxWidth) {
//                        // Break the word into chunks that fit
//                        breakWordIntoLines(word, font, maxWidth, charsPerLine, lines);
//                    } else {
//                        currentLine.append(word);
//                    }
//                }
//            }
//        }
//
//        // Add the last line if it's not empty
//        if (currentLine.length() > 0) {
//            lines.add(currentLine.toString());
//        }
//
//        return lines;
//    }
//
    // Helper method to break a word into multiple lines
    private void breakWordIntoLines(String word, Font font, float maxWidth, int charsPerLine, List<String> lines) {
        int start = 0;
        while (start < word.length()) {
            // Initial estimate of how many characters might fit
            int end = Math.min(start + charsPerLine, word.length());
            
            // Fine-tune to ensure the segment fits
            String part = word.substring(start, end);
            while (end > start + 1 && font.stringWidth(null, part) > maxWidth) {
                end--;
                part = word.substring(start, end);
            }
            
            lines.add(part);
            start = end;
        }
    }
    
    private void drawHighlight(Page var1, int var2, Font var3,float addedHeight,float startPadding) {
        float[] var4 = var1.getBrushColor();
        var1.setBrushColor(var2);
        var1.moveTo((Float)this.vertLines.get(0), this.yText - var3.ascent+ (startPadding!=-1f?startPadding:0));
        var1.lineTo((Float)this.vertLines.get(this.headerRow.length), this.yText - var3.ascent+ (startPadding!=-1f?startPadding:0));
        var1.lineTo((Float)this.vertLines.get(this.headerRow.length), this.yText + var3.descent+ addedHeight +padding);
        var1.lineTo((Float)this.vertLines.get(0), this.yText + var3.descent+ addedHeight +padding);
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
      if(vertLines != null) {
            return vertLines;
        }
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
            if (width > this.maxWidth) {
                width = this.maxWidth;
            }
            if (rowIndex == 0) {
                widths.add(width+padding);
            } else if (i < widths.size() && width > (Float)widths.get(i)) {
                widths.set(i, width+padding);
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
