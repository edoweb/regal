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

import java.util.Properties;

/**
 * Provides URN-Generation
 * 
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class URN {
    String raw = null;
    String urn = "urn";
    String nbn = "nbn";
    String de = "de";
    String snid = null;
    String niss = null;

    String urnAsString = null;

    /**
     * @param snid
     *            sub name id
     * @param niss
     *            a self generated id
     */
    public URN(String snid, String niss) {
	this.snid = snid;
	this.niss = niss;
	raw = urn + ":" + nbn + ":" + de + ":" + snid + "-" + niss;
	String checksum = getUrnChecksum(raw);
	urnAsString = raw + "" + checksum;
    }

    public String toString() {
	return urnAsString;
    }

    private String getUrnChecksum(String rawUrn) {
	String checksum = "";
	int ps = 0;
	char[] urnArray = urnMap(rawUrn);
	for (int i = 1; i <= urnArray.length; i++) {
	    ps = ps + i * Integer.parseInt(String.valueOf(urnArray[i - 1]));
	}
	int q = ps
		/ Integer.parseInt(String
			.valueOf(urnArray[urnArray.length - 1]));
	checksum = String.valueOf(q);
	return String.valueOf(checksum.charAt(checksum.length() - 1));
    }

    private char[] urnMap(String urn) {
	Properties mKonkordanz = new Properties();
	mKonkordanz.setProperty("0", "1");

	mKonkordanz.setProperty("1", "2");

	mKonkordanz.setProperty("2", "3");

	mKonkordanz.setProperty("3", "4");

	mKonkordanz.setProperty("4", "5");

	mKonkordanz.setProperty("5", "6");

	mKonkordanz.setProperty("6", "7");

	mKonkordanz.setProperty("7", "8");

	mKonkordanz.setProperty("8", "9");

	mKonkordanz.setProperty("9", "41");

	mKonkordanz.setProperty("a", "18");

	mKonkordanz.setProperty("b", "14");

	mKonkordanz.setProperty("c", "19");

	mKonkordanz.setProperty("d", "15");

	mKonkordanz.setProperty("e", "16");

	mKonkordanz.setProperty("f", "21");

	mKonkordanz.setProperty("g", "22");

	mKonkordanz.setProperty("h", "23");

	mKonkordanz.setProperty("i", "24");

	mKonkordanz.setProperty("j", "25");

	mKonkordanz.setProperty("k", "42");

	mKonkordanz.setProperty("l", "26");

	mKonkordanz.setProperty("m", "27");

	mKonkordanz.setProperty("n", "13");

	mKonkordanz.setProperty("o", "28");

	mKonkordanz.setProperty("p", "29");

	mKonkordanz.setProperty("q", "31");

	mKonkordanz.setProperty("r", "12");

	mKonkordanz.setProperty("s", "32");

	mKonkordanz.setProperty("t", "33");

	mKonkordanz.setProperty("u", "11");

	mKonkordanz.setProperty("v", "34");

	mKonkordanz.setProperty("w", "35");

	mKonkordanz.setProperty("x", "36");

	mKonkordanz.setProperty("y", "37");

	mKonkordanz.setProperty("z", "38");

	mKonkordanz.setProperty("-", "39");

	mKonkordanz.setProperty(":", "17");

	char[] urnArray = urn.toLowerCase().toCharArray();

	StringBuffer strBuf = new StringBuffer();

	for (int i = 0; i < urnArray.length; i++) {

	    strBuf.append(Integer.parseInt(mKonkordanz.getProperty(String
		    .valueOf(urnArray[i]))));

	}

	urnArray = strBuf.toString().toCharArray();
	return urnArray;
    }
}
