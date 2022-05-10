import Foundation
import PDFjet

///
/// Example_32.java
///
public class Example_32 {

    private var x: Float = 50.0
    private var y: Float = 50.0
    private var leading: Float = 14.0

    public init() throws {

        if let stream = OutputStream(toFileAtPath: "Example_32.pdf", append: false) {

            let pdf = PDF(stream)

            let font = Font(pdf, CoreFont.HELVETICA)
            font.setSize(10.0)

            let text = try String(contentsOfFile: "Sources/Example_02/main.swift", encoding: .utf8)
            let lines = text.split(separator: "\n")

            var page: Page?
            for line in lines {
                if page == nil {
                    y = 50.0
                    page = try newPage(pdf, font)
                }
                page!.printString(String(line))
                page!.newLine()
                y += leading
                if y > (Letter.PORTRAIT[1] - 20.0) {
                    page!.setTextEnd()
                    page = nil
                }
            }
            if page != nil {
                page!.setTextEnd()
            }

            pdf.complete()
        }
    }

    private func newPage(_ pdf: PDF, _ font: Font) throws -> Page {
        let page = Page(pdf, Letter.PORTRAIT)
        page.setTextStart()
        page.setTextFont(font)
        page.setTextLocation(x, y)
        page.setTextLeading(leading)
        return page
    }

}   // End of Example_32.swift

let time0 = Int64(Date().timeIntervalSince1970 * 1000)
_ = try Example_32()
let time1 = Int64(Date().timeIntervalSince1970 * 1000)
print("Example_32 => \(time1 - time0)")
