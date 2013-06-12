package de.nrw.hbz.regal.api;

import java.io.IOException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import de.nrw.hbz.regal.api.helper.Actions;
import de.nrw.hbz.regal.api.helper.HttpArchiveException;

public class TestTextExtraction
{
	// @Test
	public void testExtraction() throws IOException
	{
		Actions actions = new Actions();

		List<String> pids = actions.findByType("monograph");

		int count = 1;
		for (String pid : pids)
		{
			if (count <= 0)
				break;
			count--;
			System.out.println(pid);
			try
			{
				String pdfBox = actions.pdfbox(actions.readNode(pid));

				String itext = actions.itext(actions.readNode(pid));

				System.out.print("pdfbox:\t");
				Counter pdfBoxCount = wordCount(pdfBox);
				System.out.println(".");
				System.out.print("itext:\t");
				Counter itextCount = wordCount(itext);
				System.out.println(".");
				System.out.println(pid + " - pdfBox: " + pdfBoxCount.hit + "/"
						+ pdfBoxCount.fail + ", itext: " + itextCount.hit + "/"
						+ itextCount.fail);
			}
			catch (HttpArchiveException e)
			{
				System.out.println(e.getMessage());

			}
			catch (Throwable e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	public Counter wordCount(String text)
	{

		BreakIterator bi = BreakIterator.getWordInstance(Locale.GERMAN);
		bi.setText(text);
		int lastIndex = bi.first();
		Counter counter = new Counter();
		while (lastIndex != BreakIterator.DONE)
		{
			int firstIndex = lastIndex;
			lastIndex = bi.next();
			try
			{
				if (lastIndex != BreakIterator.DONE
						&& Character.isLetterOrDigit(text.charAt(firstIndex)))
				{
					String word = text.substring(firstIndex, lastIndex);
					System.out.print(word);
					if (wordExists(word))
					{
						counter.hit++;
						System.out.print("(+)");
						if (counter.hit > 100)
							break;
					}
					else
					{
						counter.fail++;
						System.out.print("(-)");
						if (counter.fail > 200)
							break;
					}
				}

			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}

		return counter;
	}

	boolean wordExists(String word) throws IOException
	{

		URL url = new URL(
				"http://en.wiktionary.org/w/api.php?action=query&titles="
						+ word + "&format=json");

		String json = IOUtils.toString(url.openStream());

		return !json.contains("missing");
	}

	class Counter
	{
		public long hit = 0;
		public long fail = 0;
	}
}
