package de.nrw.hbz.regal.api.helper;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;


public class PdfText
{
	public String toString(File pdfFile)
	{
		PDDocument doc = null;
		try
		{

			doc = PDDocument.load(pdfFile);
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(doc);
			return text;
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(500, "Didn't find  pdf file.");
		}
		catch (Exception e)
		{
			throw new HttpArchiveException(500, e.getMessage());
		}
		finally
		{
			if (doc != null)
			{
				try
				{
					if (doc != null)
						doc.close();
				}
				catch (IOException e)
				{

				}
			}
		}
	}

	public String itext(File pdfFile)
	{

		PdfReader reader;
		try
		{
			reader = new PdfReader(pdfFile.getAbsolutePath());
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			StringBuffer buf = new StringBuffer();
			TextExtractionStrategy strategy;
			for (int i = 1; i <= reader.getNumberOfPages(); i++)
			{
				strategy = parser.processContent(i,
						new SimpleTextExtractionStrategy());
				buf.append(strategy.getResultantText());
			}

			return buf.toString();
		}
		catch (IOException e)
		{
			throw new HttpArchiveException(500, "itext problem");
		}

	}
}
