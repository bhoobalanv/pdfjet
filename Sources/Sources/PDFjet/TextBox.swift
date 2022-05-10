/**
 *  TextBox.swift
 *
Copyright 2020 Innovatics Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
import Foundation


///
/// A box containing line-wrapped text.
///
/// <p>Defaults:<br />
/// x = 0f<br />
/// y = 0f<br />
/// width = 300f<br />
/// height = 0f<br />
/// alignment = Align.LEFT<br />
/// valign = Align.TOP<br />
/// spacing = 3f<br />
/// margin = 1f<br />
/// </p>
///
/// This class was originally developed by Ronald Bourret.
/// It was completely rewritten in 2013 by Eugene Dragoev.
///
public class TextBox : Drawable {

    var font: Font?
    var text: String?

    var x: Float = 0.0
    var y: Float = 0.0

    var width: Float = 300.0
    var height: Float = 0.0
    var spacing: Float = 3.0
    var margin: Float = 1.0

    private var lineWidth: Float = 0.0
    private var background = Color.transparent
    private var pen = Color.black
    private var brush = Color.black
    private var valign = Align.LEFT
    private var fallbackFont: Font?
    private var colors: [String : UInt32]?

    // TextBox properties
    // Future use:
    // bits 0 to 15
    // Border:
    // bit 16 - top
    // bit 17 - bottom
    // bit 18 - left
    // bit 19 - right
    // Text Alignment:
    // bit 20
    // bit 21
    // Text Decoration:
    // bit 22 - underline
    // bit 23 - strikeout
    // Future use:
    // bits 24 to 31
    private var properties: UInt32 = 0x000F0001


    ///
    /// Creates a text box and sets the font.
    ///
    /// @param font the font.
    ///
    public init(_ font: Font) {
        self.font = font
    }


    ///
    /// Creates a text box and sets the font.
    ///
    /// @param text the text.
    /// @param font the font.
    ///
    public init(_ font: Font, _ text: String) {
        self.font = font
        self.text = text
    }


    ///
    /// Creates a text box and sets the font and the text.
    ///
    /// @param font the font.
    /// @param text the text.
    /// @param width the width.
    /// @param height the height.
    ///
    public init(_ font: Font, _ text: String, _ width: Float, _ height: Float) {
        self.font = font
        self.text = text
        self.width = width
        self.height = height
    }


    ///
    /// Sets the font for this text box.
    ///
    /// @param font the font.
    ///
    public func setFont(_ font: Font?) -> TextBox {
        self.font = font
        return self
    }


    ///
    /// Returns the font used by this text box.
    ///
    /// @return the font.
    ///
    public func getFont() -> Font? {
        return self.font
    }


    ///
    /// Sets the text box text.
    ///
    /// @param text the text box text.
    ///
    @discardableResult
    public func setText(_ text: String?) -> TextBox {
        self.text = text
        return self
    }


    ///
    /// Returns the text box text.
    ///
    /// @return the text box text.
    ///
    public func getText() -> String? {
        return self.text
    }


    public func setPosition(_ x: Float, _ y: Float) {
        setLocation(x, y)
    }


    ///
    /// Sets the location where this text box will be drawn on the page.
    ///
    /// @param x the x coordinate of the top left corner of the text box.
    /// @param y the y coordinate of the top left corner of the text box.
    ///
    @discardableResult
    public func setLocation(_ x: Float, _ y: Float) -> TextBox {
        self.x = x
        self.y = y
        return self
    }


    ///
    /// Sets the width of this text box.
    ///
    /// @param width the specified width.
    ///
    @discardableResult
    public func setWidth(_ width: Float) -> TextBox {
        self.width = width
        return self
    }


    ///
    /// Returns the text box width.
    ///
    /// @return the text box width.
    ///
    public func getWidth() -> Float {
        return width
    }


    ///
    /// Sets the height of this text box.
    ///
    /// @param height the specified height.
    ///
    @discardableResult
    public func setHeight(_ height: Float) -> TextBox {
        self.height = height
        return self
    }


    ///
    /// Returns the text box height.
    ///
    /// @return the text box height.
    ///
    public func getHeight() -> Float {
        return height
    }


    ///
    /// Sets the margin of this text box.
    ///
    /// @param margin the margin between the text and the box
    ///
    @discardableResult
    public func setMargin(_ margin: Float) -> TextBox {
        self.margin = margin
        return self
    }


    ///
    /// Returns the text box margin.
    ///
    /// @return the margin between the text and the box
    ///
    public func getMargin() -> Float {
        return margin
    }


    ///
    /// Sets the border line width.
    ///
    /// @param lineWidth float
    ///
    @discardableResult
    public func setLineWidth(_ lineWidth: Float) -> TextBox {
        self.lineWidth = lineWidth
        return self
    }


    ///
    /// Returns the border line width.
    ///
    /// @return float the line width.
    ///
    public func getLineWidth() -> Float {
        return lineWidth
    }


    ///
    /// Sets the spacing between lines of text.
    ///
    ///  @param spacing
    ///
    @discardableResult
    public func setSpacing(_ spacing: Float) -> TextBox {
        self.spacing = spacing
        return self
    }


    ///
    /// Returns the spacing between lines of text.
    ///
    /// @return float the spacing.
    ///
    public func getSpacing() -> Float {
        return spacing
    }


    ///
    /// Sets the background to the specified color.
    ///
    /// @param color the color specified as 0xRRGGBB integer.
    ///
    @discardableResult
    public func setBgColor(_ color: UInt32) -> TextBox {
        self.background = color
        return self
    }


    ///
    /// Sets the background to the specified color.
    ///
    /// @param color the color specified as array of integer values from 0x00 to 0xFF.
    ///
    @discardableResult
    public func setBgColor(_ color: [UInt32]) -> TextBox {
        self.background = color[0] << 16 | color[1] << 8 | color[2]
        return self
    }


    ///
    /// Returns the background color.
    ///
    /// @return int the color as 0xRRGGBB integer.
    ///
    public func getBgColor() -> UInt32 {
        return self.background
    }


    ///
    /// Sets the pen and brush colors to the specified color.
    ///
    /// @param color the color specified as 0xRRGGBB integer.
    ///
    @discardableResult
    public func setFgColor(_ color: UInt32) -> TextBox {
        self.pen = color
        self.brush = color
        return self
    }


    ///
    /// Sets the pen and brush colors to the specified color.
    ///
    /// @param color the color specified as 0xRRGGBB integer.
    ///
    @discardableResult
    public func setFgColor(_ color: [UInt32]) -> TextBox {
        self.pen = color[0] << 16 | color[1] << 8 | color[2]
        self.brush = pen
        return self
    }


    ///
    /// Sets the pen color.
    ///
    /// @param color the color specified as 0xRRGGBB integer.
    ///
    @discardableResult
    public func setPenColor(_ color: UInt32) -> TextBox {
        self.pen = color
        return self
    }


    ///
    /// Sets the pen color.
    ///
    /// @param color the color specified as an array of int values from 0x00 to 0xFF.
    ///
    @discardableResult
    public func setPenColor(_ color: [UInt32]) -> TextBox {
        self.pen = color[0] << 16 | color[1] << 8 | color[2]
        return self
    }


    ///
    /// Returns the pen color as 0xRRGGBB integer.
    ///
    /// @return int the pen color.
    ///
    public func getPenColor() -> UInt32 {
        return self.pen
    }


    ///
    /// Sets the brush color.
    ///
    /// @param color the color specified as 0xRRGGBB integer.
    ///
    @discardableResult
    public func setBrushColor(_ color: UInt32) -> TextBox {
        self.brush = color
        return self
    }


    ///
    /// Sets the brush color.
    ///
    /// @param color the color specified as an array of int values from 0x00 to 0xFF.
    ///
    @discardableResult
    public func setBrushColor(_ color: [UInt32]) -> TextBox {
        self.brush = color[0] << 16 | color[1] << 8 | color[2]
        return self
    }


    ///
    /// Returns the brush color.
    ///
    /// @return int the brush color specified as 0xRRGGBB integer.
    ///
    public func getBrushColor() -> UInt32 {
        return self.brush
    }


    ///
    /// Sets the TextBox border object.
    ///
    /// @param border the border object.
    ///
    @discardableResult
    public func setBorder(_ border: UInt32, _ visible: Bool) -> TextBox {
        if visible {
            self.properties |= border
        }
        else {
            self.properties &= (~border & 0x00FFFFFF)
        }
        return self
    }


    ///
    /// Returns the text box border.
    ///
    /// @return Bool the text border object.
    ///
    public func getBorder(_ border: UInt32) -> Bool {
        return (self.properties & border) != 0
    }


    ///
    /// Sets all borders to be invisible.
    /// This cell will have no borders when drawn on the page.
    ///
    @discardableResult
    public func setNoBorders() -> TextBox {
        self.properties &= 0x00F0FFFF
        return self
    }


    ///
    /// Sets the cell text alignment.
    ///
    /// @param alignment the alignment code.
    /// Supported values: Align.LEFT, Align.RIGHT and Align.CENTER.
    ///
    @discardableResult
    public func setTextAlignment(_ alignment: UInt32) -> TextBox {
        self.properties &= 0x00CFFFFF
        self.properties |= (alignment & 0x00300000)
        return self
    }


    ///
    /// Returns the text alignment.
    ///
    /// @return alignment the alignment code. Supported values: Align.LEFT, Align.RIGHT and Align.CENTER.
    ///
    public func getTextAlignment() -> UInt32 {
        return (self.properties & 0x00300000)
    }


    ///
    /// Sets the underline variable.
    /// If the value of the underline variable is 'true' - the text is underlined.
    ///
    /// @param underline the underline flag.
    ///
    @discardableResult
    public func setUnderline(_ underline: Bool) -> TextBox {
        if underline {
            self.properties |= 0x00400000
        }
        else {
            self.properties &= 0x00BFFFFF
        }
        return self
    }


    ///
    /// Whether the text will be underlined.
    ///
    /// @return whether the text will be underlined
    ///
    public func getUnderline() -> Bool {
        return (properties & 0x00400000) != 0
    }


    ///
    /// Sets the srikeout flag.
    /// In the flag is true - draw strikeout line through the text.
    ///
    /// @param strikeout the strikeout flag.
    ///
    @discardableResult
    public func setStrikeout(_ strikeout: Bool) -> TextBox {
        if strikeout {
            self.properties |= 0x00800000
        }
        else {
            self.properties &= 0x007FFFFF
        }
        return self
    }


    ///
    /// Returns the strikeout flag.
    ///
    /// @return Bool the strikeout flag.
    ///
    public func getStrikeout() -> Bool {
        return (properties & 0x00800000) != 0
    }


    @discardableResult
    public func setFallbackFont(_ font: Font?) -> TextBox {
        self.fallbackFont = font
        return self
    }


    public func getFallbackFont() -> Font? {
        return self.fallbackFont
    }


    ///
    /// Sets the vertical alignment of the text in this TextBox.
    ///
    /// @param alignment - valid values areAlign.TOP, Align.BOTTOM and Align.CENTER
    ///
    @discardableResult
    public func setVerticalAlignment(_ alignment: UInt32) -> TextBox {
        self.valign = alignment
        return self
    }


    public func getVerticalAlignment() -> UInt32 {
        return self.valign
    }


    @discardableResult
    public func setTextColors(_ colors: [String : UInt32]?) -> TextBox {
        self.colors = colors
        return self
    }


    public func getTextColors() -> [String : UInt32]? {
        return self.colors
    }


    ///
    /// Draws this text box on the specified page.
    ///
    /// @param page the Page where the TextBox is to be drawn.
    /// @param draw flag specifying if this component should actually be drawn on the page.
    /// @return x and y coordinates of the bottom right corner of this component.
    /// @throws Exception
    ///
    @discardableResult
    public func drawOn(_ page: Page?) -> [Float] {
        return drawTextAndBorders(page)
    }


    private func drawBackground(_ page: Page) {
        page.setBrushColor(background)
        page.fillRect(x, y, width, height)
    }


    private func drawBorders(_ page: Page) {
        page.setPenColor(pen)
        page.setPenWidth(lineWidth)

        if getBorder(Border.TOP) &&
                getBorder(Border.BOTTOM) &&
                getBorder(Border.LEFT) &&
                getBorder(Border.RIGHT) {
            page.drawRect(x, y, width, height)
        }
        else {
            if getBorder(Border.TOP) {
                page.moveTo(x, y)
                page.lineTo(x + width, y)
                page.strokePath()
            }
            if getBorder(Border.BOTTOM) {
                page.moveTo(x, y + height)
                page.lineTo(x + width, y + height)
                page.strokePath()
            }
            if getBorder(Border.LEFT) {
                page.moveTo(x, y)
                page.lineTo(x, y + height)
                page.strokePath()
            }
            if getBorder(Border.RIGHT) {
                page.moveTo(x + width, y)
                page.lineTo(x + width, y + height)
                page.strokePath()
            }
        }
    }


    private func reformat(_ line: String, toFit textAreaWidth: Float, addTo lines: inout Array<String>) {
        let scalars = Array(line.unicodeScalars)
        var buffer: String = ""
        var i = 0
        while i < scalars.count {
            buffer.append(String(scalars[i]))
            if font!.stringWidth(buffer) > textAreaWidth {
                if scalars[scalars.count - 1] == Unicode.Scalar(" ") ||
                        buffer.components(separatedBy: .whitespaces).count <= 1 {
                    lines.append(buffer)
                }
                else {
                    lines.append(String(buffer.prefix(upTo: buffer.lastIndexOf(" ")!)))
                    while scalars[i] != Unicode.Scalar(" ") {
                        i -= 1
                    }
                }
                buffer = ""
            }
            i += 1
        }
        if !buffer.isEmpty {
            lines.append(buffer)
        }
    }


    private func drawTextAndBorders(_ page: Page?) -> [Float] {
        let textAreaWidth = self.width - (font!.stringWidth("w") + 2*margin)
        let original = text!.components(separatedBy: "\n")
        var lines = [String]()
        var i = 0
        while i < original.count {
            let line = original[i]
            if (font!.stringWidth(line) < textAreaWidth) {
                lines.append(line)
            }
            else {
                reformat(line, toFit: textAreaWidth, addTo: &lines)
            }
            i += 1
        }

        let lineHeight = font!.getBodyHeight() + spacing
        var xText: Float = 0.0
        var yText = y + font!.ascent + margin
        if (Float(lines.count) * lineHeight) > self.height {
            self.height = Float(lines.count) * lineHeight
        }

        if page != nil {
            if getBgColor() != Color.transparent {
                drawBackground(page!)
            }
            page!.setPenColor(self.pen)
            page!.setBrushColor(self.brush)
            page!.setPenWidth(self.font!.underlineThickness)
        }

        if height > 0.0 {
            if valign == Align.BOTTOM {
                yText += height - Float(lines.count) * lineHeight
            }
            else if valign == Align.CENTER {
                yText += (height - Float(lines.count) * lineHeight)/2
            }

            for i in 0..<lines.count {
                if getTextAlignment() == Align.RIGHT {
                    xText = (x + width) - (font!.stringWidth(lines[i]) + margin)
                }
                else if getTextAlignment() == Align.CENTER {
                    xText = x + (width - font!.stringWidth(lines[i]))/2
                }
                else {
                    // Align.LEFT
                    xText = x + margin
                }
                if yText + font!.getBodyHeight() + spacing + font!.descent >= y + height
                        && i < (lines.count - 1) {
                    let line = lines[i]
                    let index = line.range(of: ".", options: .backwards)?.lowerBound ?? line.endIndex
                    if index != line.endIndex {
                        lines[i] = line.prefix(upTo: index) + " ..."
                    }
                    else {
                        lines[i] = line + " ..."
                    }
                }
                if yText + font!.descent < y + height {
                    if page != nil {
                        drawText(page!, font!, fallbackFont, lines[i], xText, yText, colors)
                    }
                    yText += font!.getBodyHeight() + spacing
                }
            }
        }
        else {
            for line in lines {
                if getTextAlignment() == Align.RIGHT {
                    xText = (x + width) - (font!.stringWidth(line) + margin)
                }
                else if getTextAlignment() == Align.CENTER {
                    xText = x + (width - font!.stringWidth(line))/2
                }
                else {
                    // Align.LEFT
                    xText = x + margin
                }
                if page != nil {
                    drawText(page!, font!, fallbackFont, line, xText, yText, colors)
                }
                yText += font!.getBodyHeight() + spacing
            }
            height = yText - (y + font!.ascent + margin)
        }

        if page != nil {
            drawBorders(page!)
        }

        return [x + width, y + height]
    }


    private func drawText(
            _ page: Page?,
            _ font: Font,
            _ fallbackFont: Font?,
            _ text: String,
            _ xText: Float,
            _ yText: Float,
            _ colors: [String : UInt32]?) {
        if fallbackFont == nil {
            if colors == nil {
                page!.drawString(font, text, xText, yText)
            }
            else {
                page!.drawString(font, text, xText, yText, colors!)
            }
        }
        else {
            page!.drawString(font, fallbackFont, text, xText, yText)
        }

        let lineLength = font.stringWidth(text)
        if getUnderline() {
            let yAdjust = font.underlinePosition
            page!.moveTo(xText, yText + yAdjust)
            page!.lineTo(xText + lineLength, yText + yAdjust)
            page!.strokePath()
        }
        if getStrikeout() {
            let yAdjust = font.bodyHeight/4
            page!.moveTo(xText, yText - yAdjust)
            page!.lineTo(xText + lineLength, yText - yAdjust)
            page!.strokePath()
        }
    }

}   // End of TextBox.swift
