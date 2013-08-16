/*
 * Copyright 2012 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.nrw.hbz.regal.api.helper;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

/**
 * provides text extraction with itext and pdfbox.
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class PdfText {
    /**
     * @param pdfFile
     *            this file will be extracted.
     * @return the plain text of the pdf
     */
    public String toString(File pdfFile) {
	PDDocument doc = null;
	try {

	    doc = PDDocument.load(pdfFile);
	    PDFTextStripper stripper = new PDFTextStripper();
	    String text = stripper.getText(doc);
	    return text;
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);
	} catch (Exception e) {
	    throw new HttpArchiveException(500, e);
	} finally {
	    if (doc != null) {
		try {
		    if (doc != null)
			doc.close();
		} catch (IOException e) {

		}
	    }
	}
    }

    /**
     * @param pdfFile
     *            this file will be extracted.
     * @return the plain text of the pdf
     */
    public String itext(File pdfFile) {

	PdfReader reader;
	try {
	    reader = new PdfReader(pdfFile.getAbsolutePath());
	    PdfReaderContentParser parser = new PdfReaderContentParser(reader);
	    StringBuffer buf = new StringBuffer();
	    TextExtractionStrategy strategy;
	    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
		strategy = parser.processContent(i,
			new SimpleTextExtractionStrategy());
		buf.append(strategy.getResultantText());
	    }

	    return buf.toString();
	} catch (IOException e) {
	    throw new HttpArchiveException(500, e);
	}

    }
}
