/**
 *  EmbeddedFile.swift
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


/**
 *  Used to embed file objects.
 *  The file objects must added to the PDF before drawing on the first page.
 *
 */
public class EmbeddedFile {

    var objNumber: Int = -1
    var fileName: String?


    public init(
            _ pdf: PDF,
            _ fileName: String,
            _ stream: InputStream,
            _ compress: Bool) throws {
        self.fileName = fileName

        var baos = [UInt8]()
        var buf = [UInt8](repeating: 0, count: 4096)
        stream.open()
        while stream.hasBytesAvailable {
            let count = stream.read(&buf, maxLength: buf.count)
            if count > 0 {
                baos.append(contentsOf: buf[0..<count])
            }
        }
        stream.close()

        if compress {
            buf = baos
            baos = [UInt8]()
            _ = LZWEncode(&baos, &buf)
        }

        pdf.newobj()
        pdf.append("<<\n")
        pdf.append("/Type /EmbeddedFile\n")
        if compress {
            // pdf.append("/Filter /FlateDecode\n")
            pdf.append("/Filter /LZWDecode\n")
        }
        pdf.append("/Length ")
        pdf.append(baos.count)
        pdf.append("\n")
        pdf.append(">>\n")
        pdf.append("stream\n")
        pdf.append(baos)
        pdf.append("\nendstream\n")
        pdf.endobj()

        pdf.newobj()
        pdf.append("<<\n")
        pdf.append("/Type /Filespec\n")
        pdf.append("/F (")
        pdf.append(fileName)
        pdf.append(")\n")
        pdf.append("/EF <</F ")
        pdf.append(pdf.getObjNumber() - 1)
        pdf.append(" 0 R>>\n")
        pdf.append(">>\n")
        pdf.endobj()

        self.objNumber = pdf.getObjNumber()
    }


    public func getFileName() -> String {
        return self.fileName!
    }

}   // End of EmbeddedFile.swift
