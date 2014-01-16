package de.nrw.hbz.regal.mab;

public class MabRecord {

	/*-
	5              Satzstatus
	               c = korrigierter Datensatz (corrected)
	               d = geloeschter Datensatz (deleted)
	               n = neuer Datensatz
	               p = provisorischer Datensatz
	               u = umgelenkter Datensatz
	               v = unveraenderter Datensatz*/
	public enum Satzstatus {
		KORRIGIERT, GELOESCHT, NEU, PROVISORISCH, UMGELENKT, UNVERAENDERT;
	}

	/*-
	23             Satztyp
	               h = Hauptsatz fuer Titeldaten (MAB-TITEL)
	               y = Untersatz fuer die Auffuehrung von Abteilungen
	                   (MAB-TITEL)
	               u = Untersatz fuer die Bandauffuehrung (MAB-TITEL)
	               v = Pauschalverweisungssatz oder Siehe-auch-Hinweis
	                   (MAB-TITEL)

	               p = Personennamensatz (MAB-PND)
	               t = Pauschalverweisungssatz oder Siehe-auch-Hinweis
	                   (MAB-PND)

	               k = Koerperschaftsnamensatz (MAB-GKD)
	               w = Pauschalverweisungssatz oder Siehe-auch-Hinweis
	                   (MAB-GKD)

	               r = Schlagwortkettensatz (MAB-SWD)
	               s = Schlagwortsatz (MAB-SWD)
	               x = Pauschalverweisungssatz oder Siehe-auch-Hinweis
	                   (MAB-SWD)

	               q = Notationssatz (MAB-NOTAT)
	                     Provisorischer Notationsdatensatz fuer die Angabe
	                     von Notationen

	               l = Hauptsatz fuer Lokaldaten, die fuer alle Exemplare
	                   gueltig sind (MAB-LOKAL)
	               e = Untersatz fuer Exemplardaten, die fuer ein oder
	                   mehrere Exemplare gueltig sind (MAB-LOKAL)

	               m = Adressdatensatz (MAB-ADRESS)
	                     Provisorischer Adressdatensatz fuer die Angabe
	                     von Adressdaten

	               c = Redaktionssatz
	 */
	public enum Satztyp {
		HAUPTSATZ, UNTERSATZ_ABTEILUNGEN, UNTERSATZ_BAENDE, PAUSCHALVERWEISUNGSSATZ_TITEL, PERSONENNAMENSATZ, PAUSCHALVERWEISUNGSSATZ_PND, KOERPERSCHAFTSNAMENSATZ, PAUSCHALVERWEISUNGSSATZ_GKD, SCHLAGWORTKETTENSATZ, SCHLAGWI
	}

	/*- 
	http://www.dnb.de/SharedDocs/Downloads/DE/DNB/standardisierung/mabTitelBibliographischeDaten2001.txt;jsessionid=899A9F945EADFCFECDD616AD246D45F7.prod-worker2?__blob=publicationFile
		war: http://www.dnb.de/DE/Standardisierung/Formate/MAB/mab_node.html
		war: http://www.d-nb.de/standardisierung/txt/segm000.txt

		ï»¿                                MAB2-Format
		                         Satzkennung und Segmente 0--
		                       (gueltig fuer alle MAB2-Dateien)

		                        Online-Kurzreferenz-Version
		                           Stand: November 2001




		SATZKENNUNG

		0 - 4          Satzlaenge
	 */
	String satzlaenge = null;
	/*-
		5              Satzstatus
		               c = korrigierter Datensatz (corrected)
		               d = geloeschter Datensatz (deleted)
		               n = neuer Datensatz
		               p = provisorischer Datensatz
		               u = umgelenkter Datensatz
		               v = unveraenderter Datensatz*/
	Satzstatus satzstatus;

	/*-
		6 - 9          Versionsangabe
	 */
	String versionsangabe;
	/*-
		10             Indikatorlaenge
	 */
	char indikatorlaenge;
	/*-
		11             Teilfeldkennungslaenge
	 */
	char teilfeldkennungslaenge;
	/*-
		12 - 16        Datenanfangsadresse
	 */
	String datenanfangsadresse;
	/*-
		17 - 22        nicht benutzt
	 */
	String nichtBenutzt_17_22;
	/*-
		23             Satztyp
		               h = Hauptsatz fuer Titeldaten (MAB-TITEL)
		               y = Untersatz fuer die Auffuehrung von Abteilungen
		                   (MAB-TITEL)
		               u = Untersatz fuer die Bandauffuehrung (MAB-TITEL)
		               v = Pauschalverweisungssatz oder Siehe-auch-Hinweis
		                   (MAB-TITEL)

		               p = Personennamensatz (MAB-PND)
		               t = Pauschalverweisungssatz oder Siehe-auch-Hinweis
		                   (MAB-PND)

		               k = Koerperschaftsnamensatz (MAB-GKD)
		               w = Pauschalverweisungssatz oder Siehe-auch-Hinweis
		                   (MAB-GKD)

		               r = Schlagwortkettensatz (MAB-SWD)
		               s = Schlagwortsatz (MAB-SWD)
		               x = Pauschalverweisungssatz oder Siehe-auch-Hinweis
		                   (MAB-SWD)

		               q = Notationssatz (MAB-NOTAT)
		                     Provisorischer Notationsdatensatz fuer die Angabe
		                     von Notationen

		               l = Hauptsatz fuer Lokaldaten, die fuer alle Exemplare
		                   gueltig sind (MAB-LOKAL)
		               e = Untersatz fuer Exemplardaten, die fuer ein oder
		                   mehrere Exemplare gueltig sind (MAB-LOKAL)

		               m = Adressdatensatz (MAB-ADRESS)
		                     Provisorischer Adressdatensatz fuer die Angabe
		                     von Adressdaten

		               c = Redaktionssatz
	 */
	Satztyp satztyp;
	/*-

		001-029   SEGMENT IDENTIFIKATIONSNUMMERN, DATUMS- UND VERSIONS-
		          ANGABEN
	 */
	String segId_datum_version;
	/*-
		001       IDENTIFIKATIONSNUMMER DES DATENSATZES
	 */
	String id;
	/*-
		          Indikator:
		          Blank = nicht definiert

	 */
	String indikator;
	/*-
		002       DATUM DER ERSTERFASSUNG / FREMDDATENUEBERNAHME

		          Indikator:
		          a = Datum der Ersterfassung
		          b = Datum der Fremddatenuebernahme

	 */
	String datumDerErsterfassung;
	String datumDerUebernahme;
	/*-
		003       DATUM DER LETZTEN KORREKTUR

		          Indikator:
		          Blank = nicht definiert

	 */
	String datumLetzteKorrektur;
	/*-
		004       ERSTELLUNGSDATUM DES AUSTAUSCHSATZES

		          Indikator:
		          Blank = nicht definiert
	 */
	String datumDerErstellungAustauschsatz;
	/*-

		005       TRANSAKTIONSDATUM

		          Indikator:
		          n = letzte Transaktion
		          v = vorletzte Transaktion

	 */
	String datumLetzteTransaktion;
	String datumVorletzteTransaktion;
	/*-
		006       VERSIONSNUMMER

		          Indikator:
		          n = letzte Transaktion
		          v = vorletzte Transaktion
	 */
	String versionsnummerLetzteTransaktion;
	String versionsnummerVorletzteTransaktion;
	/*-

		010       IDENTIFIKATIONSNUMMER DES DIREKT UEBERGEORDNETEN
		          DATENSATZES

		          Indikator:
		          blank = nicht definiert
	 */
	String idUeberordnung;
	/*-

		011       IDENTIFIKATIONSNUMMER DER VERKNUEPFTEN SAETZE FUER
		          PAUSCHALVERWEISUNGEN UND SIEHE-AUCH-HINWEISE

		          Indikator:
		          blank = nicht definiert
	 */
	String idPauschalverweisung;
	/*-

		012       IDENTIFIKATIONSNUMMER DES TITELDATENSATZES (MAB-LOKAL)

		          Indikator:
		          blank = nicht definiert
	 */
	String idTiteldatensatz;
	/*-

		015       IDENTIFIKATIONSNUMMER DES ZIELSATZES

		          Indikator:
		          Blank = nicht definiert
	 */
	String idZielsatz;
	/*-

		016       IDENTIFIKATIONSNUMMER DES UMGELENKTEN SATZES

		          Indikator:
		          Blank = nicht definiert

	 */
	String idUmgelenkterSatz;
	/*-
		020       IDENTIFIKATIONSNUMMER EINES GELIEFERTEN DATENSATZES

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Ueberregionale Identifikationsnummer
		          b     = Regionale Identifikationsnummer
		          c     = Lokale Identifikationsnummer

	 */
	String idGelieferterDatensatz_Ueberregional;
	String idGelieferterDatensatz_Regional;
	String idGelieferteerDatensatz_Lokal;
	/*-
		021       IDENTIFIKATIONSNUMMER DER PRIMAERFORM

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Ueberregionale Identifikationsnummer
		          b     = Regionale Identifikationsnummer
		          c     = Lokale Identifikationsnummer
	 */
	String idPrimaerform_Ueberregional;
	String idPrimaerform_Regional;
	String idPrimaerform_Lokal;
	/*-

		022       IDENTIFIKATIONSNUMMER DER SEKUNDAERFORM

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Ueberregionale Identifikationsnummer
		          b     = Regionale Identifikationsnummer
		          c     = Lokale Identifikationsnummer
	 */
	String idSekundaerform_Ueberregional;
	String idSekundaerform_Regional;
	String idSekundaerform_Lokal;
	/*-

		023       IDENTIFIKATIONSNUMMER DES ZU KORRIGIERENDEN SATZES

		          Indikator:
		          blank = nicht spezifiziert
		          a     = MAB-TITEL
		          b     = MAB-PND
		          c     = MAB-GKD
		          d     = MAB-SWD
		          e     = MAB-NOTAT
		          f     = MAB-ADRESS

		          Unterfelder:
		          $a    = Identifikationsnummer des zu korrigierenden Datensatzes

	 */

	/*-
		025       UEBERREGIONALE IDENTIFIKATIONSNUMMER

		          Indikator:
		          blank = nicht spezifiziert
		          a     = DDB
		          b     = BNB
		          c     = Casalini libri
		          e     = ekz
		          f     = BNF
		          g     = ZKA
		          l     = LoC
		          o     = OCLC
		          z     = ZDB

	 */
	/*-
		026       REGIONALE IDENTIFIKATIONSNUMMER

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Bibliotheksverbund Berlin-Brandenburg
		          b     = Norddeutscher Bibliotheksverbund (bis 1996)
		          c     = Bibliotheksverbund Niedersachsen/Sachsen-Anhalt
		                  (bis 1996)
		          d     = Nordrhein-Westfaelischer Bibliotheksverbund
		          e     = Hessisches Bibliotheksinformationssystem
		          f     = Suedwestdeutscher Bibliotheksverbund
		          g     = Bibliotheksverbund Bayern
		          h     = Gemeinsamer Bibliotheksverbund der Laender Bremen,
		                  Hamburg, Mecklenburg-Vorpommern, Niedersachsen,
		                  Sachsen-Anhalt, Schleswig-Holstein, Thueringen
		                  (ab 1996)
	 */
	/*-

		027       LOKALE IDENTIFIKATIONSNUMMER

		          Indikator:
		          blank = nicht spezifiziert
		          a     = gepruefte Identifikationsnummer
		          b     = ungepruefte Identifikationsnummer
	 */
	/*-

		028       IDENTIFIKATIONSNUMMER VON NORMDATEN

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Identifikationsnummer der PND
		          b     = Identifikationsnummer der GKD
		          c     = Identifikationsnummer der SWD
	 */
	/*-

		029       SONSTIGE IDENTIFIKATIONSNUMMER DES VORLIEGENDEN
		          DATENSATZES

		          Indikator:
		          blank = nicht spezifiziert


	 */
	/*-

		030-035   SEGMENT ALLGEMEINE VERARBEITUNGSTECHNISCHE ANGABEN
	 */
	/*-
		030       CODIERTE ANGABEN ZUM DATENSATZ

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Bearbeitungsstatus
		               a = Autopsie
		               b = teilweise Autopsie
		               c = Uebernahme aus Nationalbibliographie
		               d = Uebernahme aus anderen Quellen
		               e = konvertierte Altdaten
		               f = CIP-Aufnahme
		               g = vervollstaendigte CIP-Aufnahme
		               h = ohne Autopsie
		               u = maschinelle Umsetzung einer Titelaufnahme,
		                   die nicht nach RAK erstellt ist
		               z = keine Angabe

		            1  Ansetzungsstatus (Normdateien)
		               a = ueberregional autorisierte Ansetzungsform
		               b = regional autorisierte Ansetzungsform
		               c = lokal autorisierte Ansetzungsform
		               d = nicht autorisierte Ansetzungsform
		               e = maschinell ermittelte Ansetzungsform
		               f = vorlaeufige Ansetzung
		               z = keine Angabe

		            2  Zeichenvorrat
		               1 = MAB-Zeichenvorrat
		               3 = DIN 31628, Stufe 1
		               5 = DIN 31628, Stufe 2
		               7 = DIN 31628, Stufe 3
		               z = Sonstiger Zeichenvorrat

		            3  Zeichencode
		               a = DIN 66003-DRV
		               b = DIN 66003-IRV
		               c = DIN 66003 + DIN 31624
		                   Die DIN-Normen entsprechen dem Zeichenvorrat
		                   von DIN 31628, Stufe 2.
		               d = ISO 646 (IRV) + ISO 5426
		                   Im MAB-Zeichensatz sind die Zeichen in
		                   ISO 646 (IRV) und ISO 5426 (in der vorlaeufigen
		                   deutschen Version) definiert.
		               i = Industriestandard  (=  festgelegte
		                   Zeichensatztabellen IBM-kompatibler PC's
		                   fuer MS-DOS-Anwendungen)
		               u = Unicode / ISO 10646 (UTF 8)
		               z = Sonstiger Zeichencode

		            4  Regeln fuer die Formalerschliessung
		               a = RAK-Anwendung der Deutschen Bibliothek
		               b = RAK-OEB mit alternativen Ansetzungsformen
		               c = RAK-WB
		               d = Sonstige RAK-Anwendung
		               e = DIN 1505
		               f = PI - Instruktionen fuer die alphabetischen
		                   Kataloge der preussischen Bibliotheken
		               g = RNA - Regeln fuer Nachlaesse und Autographen
		               h = Formalerschliessung nach dem Verzeichnis der
		                   Drucke des 16. Jahrhunderts (VD 16)
		               i = Formalerschliessung nach dem Verzeichnis der
		                   Drucke des 17. Jahrhunderts (VD 17)
		               k = maschinelle Umsetzung aus AACR
		               z = Sonstiges Regelwerk

		            5  Regeln fuer die Sacherschliessung
		               r = RSWK
		               s = RSWK-Alternativregeln
		               z = Sonstiges Regelwerk

		            6  Regeln fuer die Normdatenansetzung
		               g = RNA - Regeln fuer Nachlaesse und Autographen
		               h = Ansetzung nach dem Verzeichnis der Drucke des
		                   16. Jahrhunderts (VD 16)
		               i = Ansetzung nach dem Verzeichnis der Drucke des
		                   17. Jahrhunderts (VD 17)
		               k = LOC Name Authority
		               l = PND-Ansetzungsform
		               m = GKD-Ansetzungsform
		               n = SWD-Ansetzungsform
		               r = RSWK
		               s = RSWK-Alternativregeln
		               z = Sonstiges Regelwerk

		            7   Transliteration/Transkription
		                a = Transliteration
		                b = Transkription
		                z = keine Angabe

		            8   Stichwortkennung
		                a = Stichwortanfang- und Stichwortendezeichen
		                b = Stichwortanfangszeichen
		                c = eigene Stichwortfelder

		         9-10   Faecherstatistik
		                Die Faecherstatistik erfolgt nach der Deutschen
		                Bibliotheksstatistik (DBS).

		            11  Haupteintragungstyp
		                1 = Verfasserwerk
		                2 = Urheberwerk
		                3 = Sachtitelwerk

		            12  Ordnungssachtitel
		                4 = Ordnungssachtitel ist der Inhalt des
		                    Feldes 304
		                5 = Ordnungssachtitel ist der Inhalt des
		                    Feldes 310
		                7 = Ordnungssachtitel ist der Inhalt des
		                    Feldes 331

	 */
	/*-
		031       ANGABEN ZUM REDAKTIONSSATZ

		          Indikator:
		          blank = nicht definiert

		          Unterfelder:
		          $a    = Art des Redaktionssatzes
		          $b    = Stand der redaktionellen Bearbeitung
		          $c    = Weitere Angaben zum Redaktionssatz
		          $d    = Inhalt des neuen (korrigierten) Feldes
		          $e    = Grund des Redaktionssatzes

	 */
	/*-

		036-049   SEGMENT ALLGEMEINE CODIERTE ANGABEN
	 */
	/*-
		036       LAENDERCODE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = zweibuchstabiger Laendercode nach DIN EN 23166
		          b     = dreibuchstabiger Laendercode nach DIN EN 23166
		          c     = Laendercode der SWD
		          z     = sonstiger Laendercode
	 */
	/*-

		037       SPRACHENCODE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Sprachencode nach DIN 2335
		          b     = Sprachencode nach ISO 639
		          c     = Sprachencode nach Z39.53 (USMARC, UNIMARC)
		          z     = Sonstiger Sprachencode
	 */
	/*-

		038       CODE FUER HERKUNFTSSPRACHE / SPRACHE DES ORIGINALS

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Sprachencode nach DIN 2335
		          b     = Sprachencode nach ISO 639
		          c     = Sprachencode nach Z39.53 (USMARC, UNIMARC)
		          z     = Sonstiger Sprachencode
	 */
	/*-

		039       ZEITCODE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Zeitcode der Universalen Dezimal-
		                  Klassifikation (UDK-Zeitcode)
		          b     = Time Period Code der Library of Congress
		          c     = Zeitcode nach Jahreszahlen
		          z     = Sonstiger Zeitcode

	 */
	/*-
		040       NOTATION FUER NORMDATEN

		          Indikator:
		          blank = nicht spezifiziert
	 */
	/*-

		041       NOTATIONSSPEZIFISCHE CODIERUNGEN

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0   Art der Notation
		                blank = Systematik der katalogisierenden Institution
		                a     = UDC     (Universal Decimal Classification)
		                b     = DDC     (Dewey Decimal Classification)
		                c     = LC      (Library of Congress Classification)
		                d     = DNB     (Systematik der Deutschen Nationalbibliographie)
		                e     = Methode Eppelsheimer
		                g     = Regensburger Verbundklassifikation
		                h     = Gesamthochschulbibliothekssystematik (GHBS)
		                l     = RPB     (Rheinland-Pfaelzische Bibliographie)
		                m     = MSC     (Mathematics Subject Classification)
		                n     = NWBib   (Nordrhein-Westfaelische Bibliographie)
		                o     = ASB     (Allgemeine Systematik fÃ¼r Bibliotheken)
		                p     = SSD     (Systematik der Stadtbibliothek Duisburg)
		                q     = SfB     (Systematik fÃ¼r Bibliotheken)
		                r     = KAB     (Klassifikation fÃ¼r Allgemeinbibliotheken)
		                s     = Systematiken der ekz
		                t     = Systematik der TUB Muenchen
		                u     = DOPAED der UB Erlangen
		                v     = IFZ-Systematik
		                w     = Systematik der Bayerischen Bibliographie
		                z     = ZDB-Systematik

		            1   Art der Notation bei Anwendung der Methode Eppelsheimer
		                a     = Notation des systematischen Katalogs
		                b     = Notation des Laenderkatalogs
		                c     = Notation des biographischen Katalogs
		                d     = Notation des Ortskatalogs


	 */
	/*-
		050-064   SEGMENT  VEROEFFENTLICHUNGS- UND MATERIALSPEZIFISCHE
		          ANGABEN
	 */
	/*-
		050       DATENTRAEGER

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Druckschrift
		               a = nicht spezifiziert

		            1  Handschrift
		               a = nicht spezifiziert

		            2  Papierzustand
		               a = nicht spezifiziert
		               b = saeurefreies, alterungsbestaendiges Papier
		               c = kein saeurefreies, kein alterungsbestaendiges
		                   Papier
		               d = entsaeuertes Papier
		               e = Pergament
		               z = sonstiges Material

		            3  Mikroform
		               a = nicht spezifiziert
		               b = Mikroform-Master
		               c = Sekundaerform

		            4  Blindenschrifttraeger
		               a = nicht spezifiziert

		          5-6  Audiovisuelles Medium / Bildliche Darstellung

		               Tontraeger:
		               aa = CD-DA (Compact Disc Digital Audio, Single
		                    Compact Disc)
		               ab = CD-Bildplatte
		               ac = Tonband
		               ad = Compact-Cassette
		               ae = Micro-Cassette (Diktier- oder Stenocassette)
		               af = Digital Audio Tape (DAT-Cassette)
		               ag = Digital Compact Cassette (DCC-Cassette)
		               ah = Cartridge (8-Track Cartridge)
		               ai = Drahtton (Stahlband)
		               aj = Schallplatte
		               ak = Walze (Zylinder)
		               al = Klavierrolle (Mechanisches Klavier)
		               am = Filmtonspur
		               an = Tonbildreihe

		               Film, visuelle Projektion:
		               ba = Filmspulen
		               bb = Film-Cartridge
		               bc = Film-Cassette
		               bd = Anderes Filmmedium
		               be = Filmstreifen
		               bf = Filmstreifen-Cartridge
		               bg = Filmstreifen-Rolle
		               bh = Anderer Filmstreifentyp
		               bi = Diapositiv, Diaset, Stereograph
		               bj = Arbeitstransparent
		               bk = Arbeitstransparentstreifen

		               Videoaufnahme:
		               ca = Videobandcassette
		               cb = Videobandcartridge
		               cc = Videobandspulen
		               cd = Bildplatte (Videodisc)
		               ce = Anderer Videotyp

		               Bildliche Darstellung:
		               da = Foto
		               db = Kunstblatt (Originalgraphik, Nachdruck)
		               dc = Plakat

		               Sonstige Angaben:
		               uu = unbekannt
		               yy = nicht spezifiziert
		               zz = sonstige audiovisuelle Medien

		            7  Medienkombination
		               a = nicht spezifiziert

		            8  Computerdatei
		               a = nicht spezifiziert
		               b = Diskette(n)
		               c = Magnetbandkassette(n)
		               d = Optische Speicherplatte(n)
		                   (z.B. CD-ROM, CD-I, Photo-CD, WORM, DVD)
		               e = Einsteckmodul(e)
		               f = Magnetband, Magnetbaender
		               g = Computerdatei(en) im Fernzugriff
		               z = sonstige Computerdatei(en)

		            9  Spiele
		               a = nicht spezifiziert

		           10  Landkarten
		               a = nicht spezifiziert

		        11-13  Anzahl der physischen Einheiten

	 */
	/*-
		051     VEROEFFENTLICHUNGSSPEZIFISCHE ANGABEN ZU BEGRENZTEN
		        WERKEN

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Erscheinungsform
		               a = unselbstaendig erschienenes Werk
		               f = Fortsetzung
		               m = einbaendiges Werk - nicht Teil eines
		                   Gesamtwerks
		               n = mehrbaendiges begrenztes Werk - nicht Teil
		                   eines Gesamtwerks
		               s = einbaendiges Werk  u n d  Teil (mit
		                   Stuecktitel) eines Gesamtwerks
		               t = mehrbaendiges begrenztes Werk  u n d
		                   Teil (mit Stuecktitel) eines Gesamtwerks

		          1-3  Veroeffentlichungsart und Inhalt
		               a = Abstract (Referat)
		               b = Bibliographie
		               c = Katalog
		               d = Woerterbuch
		               e = Enzyklopaedie
		               f = Festschrift
		               g = Datenbank
		               h = Biographie
		               i = Registerwerk
		               j = Fortschrittsbericht
		               k = Konferenzschrift
		               l = Gesetz
		               m = Musikalia
		               n = Normschrift
		               o = Loseblattausgabe
		               p = Patentdokument
		               q = Lieferungswerk
		               r = Report
		               s = Statistik
		               t = Aufsatz
		               u = Universitaetsschrift
		               v = Sonderdruck
		               x = Schulbuch
		               z = sonstige Veroeffentlichungsart/-inhalt

		            4  Literaturtyp
		               f = Fachbuch
		               k = Kinderbuch, Jugendbuch, Schulbuch
		               l = Lehrbuch
		               p = populaerwissenschaftliche Literatur
		               s = Belletristik
		               t = Trivialliteratur
		               w = wissenschaftliche Literatur
		               z = Sonstiges

		            5  Reprint-Kennzeichen
		               r = Reprint

		            6  Kennzeichnung Amtlicher Druckschriften
		               b = Regierungsbezirksebene
		               f = nationalstaatliche Ebene
		               i = internationale Ebene (multinational)
		               k = Kreis
		               l = lokale Ebene (Stadt, Gemeinde)
		               m = mehrere amtliche Koerperschaften innerhalb
		                   eines Staates sind beteiligt
		               o = Koerperschaft des oeffentlichen Rechts
		               r = Region
		               s = Land (Provinz)
		               u = sonstige amtliche Druckschrift
	 */
	/*-

		052       VEROEFFENTLICHUNGSSPEZIFISCHE ANGABEN ZU FORTLAUFENDEN
		          SAMMELWERKEN

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Erscheinungsform
		               a = unselbstaendig erschienenes Werk
		               f = Fortsetzung
		               j = zeitschriftenartige Reihe
		               p = Zeitschrift
		               r = Schriftenreihe (Serie)
		               z = Zeitung

		          1-6  Veroeffentlichungsart und Inhalt
		               ab = Abstract (Referat)
		               aa = Amtsblatt
		               am = Amts- und Gesetzblatt
		               az = Anzeigenblatt
		               au = Aufsatz
		               bi = Bibliographie
		               kt = Bibliothekskatalog
		               da = Datenbank
		               di = Directory
		               es = Entscheidungssammlung
		               ft = Fachzeitung
		               fz = Firmenzeitschrift/-zeitung
		               fb = Fortschrittsbericht
		               ag = Gesetz(und Verordnungs-)blatt
		               ha = Haushaltsplan
		               il = Illustrierte
		               in = Index
		               ko = Konferenzschrift / Kongressbericht
		               mg = Magazin
		               me = Messeblatt
		               pa = Parlamentaria
		               rf = Referateorgan
		               re = Report-Serie
		               sc = Schul- / Universitaetsschrift
		               se = Serie
		               so = Sonderdruck
		               xj = Sonstige Periodika, juristische
		               st = Statistik
		               ub = Uebersetzungszeitschrift
		               bg = Biographie
		               ez = Enzyklopaedie
		               li = Lieferungswerk
		               lo = Loseblattausgabe
		               mu = Musikalia
		               no = Normschrift
		               pt = Patentdokument
		               rg = Registerwerk
		               uu = sonstige Veroeffentlichungsart/-inhalt

		               ao = Zeitung fuer die allgemeine Oeffentlichkeit
		               eo = Zeitung fuer eine eingeschraenkte
		                    Oeffentlichkeit
		               up = Ueberregionale Zeitung
		               rp = Regionale Zeitung
		               lp = Lokale Zeitung

		            7  Publikationsstatus
		               a = fortlaufende Publikation ohne geplanten
		                   Abschluss
		               f = Titelaenderung
		               t = eingestelltes Erscheinen
		               z = keine Angabe moeglich

		         8-10  Erscheinungsweise
		               d = taeglich
		               t = drei- bis fuenfmal woechentlich
		               c = zweimal woechentlich
		               w = woechentlich
		               e = vierzehntaegig
		               s = halbmonatlich
		               m = monatlich
		               b = alle zwei Monate
		               q = vierteljaehrlich
		               f = halbjaehrlich
		               a = jaehrlich
		               g = alle zwei Jahre
		               h = alle drei Jahre
		               z = unregelmaessig oder sonstige Erscheinungsweise

		           11  Reprint-Kennzeichen
		               r = Reprint

		           12  Kennzeichnung Amtlicher Druckschriften
		               b = Regierungsbezirksebene
		               f = nationalstaatliche Ebene
		               i = internationale Ebene (multinational)
		               k = Kreis
		               l = lokale Ebene (Stadt, Gemeinde)
		               m = mehrere amtliche Koerperschaften innerhalb
		                   eines Staates sind beteiligt
		               o = Koerperschaft des oeffentlichen Rechts
		               r = Region
		               s = Land (Provinz)
		               u = sonstige amtliche Druckschrift

		        13-14  Fruehere Erscheinungsform
		               a = unselbstaendig erschienenes Werk
		               f = Fortsetzung
		               j = zeitschriftenartige Reihe
		               p = Zeitschrift
		               r = Schriftenreihe (Serie)
		               z = Zeitung

	 */
	/*-
		053       NACHLAESSE UND AUTOGRAPHEN

		          Indikator:
		          blank = nicht definiert

		          Datenelement
		            0  Nachlassmaterialien
		               b = Korrespondenz
		               l = Lebensdokument/Sachakte
		               n = Nachlass
		               s = Sammlung
		               w = Werkmanuskript
	 */
	/*-

		057       MATERIALSPEZIFISCHE CODES FUER MIKROFORMEN

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Materialart
		               a = Mikrofilm-Lochkarte
		               b = Mikrofilm-Cartridge
		               c = Mikrofilm-Cassette
		               d = Mikrofilmspule
		               e = Mikrofiche (Mikroplanfilm)
		               f = Mikrofiche-Kassette
		               g = Mikro-opaque (Microcard usw.)
		               h = Mikrofilmstreifen
		               j = Mikrofilm-Jacket
		               u = Unbekannt
		               z = Andere

		            1  Polaritaet
		               a = Positiv
		               b = Negativ
		               d = Gemischte Polaritaet
		               u = Unbekannt

		            2  Format der Mikroform
		               a =   8 mm                         (Mikrofilm)
		               d =  16 mm                         (Mikrofilm)
		               f =  35 mm                         (Mikrofilm)
		               g =  70 mm                         (Mikrofilm)
		               h = 105 mm                         (Mikrofilm)
		               l = 76,2x127 mm (3x5 in.)
		                   (Mikrofiche oder Mikro-opaque)
		               m = 101,6x152,4 mm (4x6 in., d.h. 105x148mm)
		                   (Mikrofiche oder Mikro-opaque)
		               o = 152,4x228,6 mm (6x9 in.)
		                   (Mikrofiche oder Mikro-opaque)
		               p = 82,55x187,325 mm (3 1/4 x 7 3/8 in.)
		                   (Mikrofilm-Lochkarte)
		               u = Unbekanntes Format
		               z = Andere Formate

		            3  Verkleinerungsrate
		               a = Niedrige Verkleinerung
		               b = Standardverkleinerung (16x - 30x)
		               c = Hohe Verkleinerung (31x - 60x)
		               d = Sehr hohe Verkleinerung (61x - 90x)
		               e = Extrem hohe Verkleinerung (91x -  )
		               u = Unbekannte Verkleinerung
		               v = Verschiedene Verkleinerungen

		          4-6  Spezifische Verkleinerungsrate

		            7  Farbe
		               a = Monochrom
		               b = Farbig
		               u = Unbekannt
		               v = Variiert

		            8  Emulsion des Filmes
		               a = Silberhalogenid
		               b = Diazo
		               c = Vesikularfilm
		               u = Unbekannte Emulsion
		               v = Verschiedene Emulsionen
		               x = Nicht anwendbar
		               z = Andere Emulsion

		            9  Generation
		               a = Erste Generation (Mutterfilm, Master)
		               b = Zweite Generation: Dupliziervorlage
		                   (Printing-Master)
		               c = Gebrauchskopie
		               u = Unbekannt
		               v = Verschiedene Generationen

		           10  Traegermaterial
		               a = Sicherheitstraegermaterial: Polyester,
		                   Polyethylenerephtalat
		               b = Sicherheitstraegermaterial: Acetatmaterial
		                   (Triacetat)
		               c = Kein Sicherheitstraegermaterial (z.B.
		                   Cellulosenitrat)
		               u = Unbekanntes Traegermaterial
		               v = Verschiedene Traegermaterialien
		               x = Nicht anwendbar


	 */
	/*-

		065-069   SEGMENT NORMDATENSPEZIFISCHE ANGABEN
	 */
	/*-
		065       NORMDATENSPEZIFISCHE ANGABEN ZUR PND

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Individualisierungskennzeichen
		               a = Individualisierter Personennamensatz
		               b = Nicht-Individualisierter Personennamensatz

		            1  Geschlecht
		               m = maennlich
		               w = weiblich

		            2  Namenstyp
		               a = Pseudonym
		               b = Verlagspseudonym
		               c = Sammelpseudonym
		               d = fiktive Person (z.B. literarische Gestalt)
		               e = Familien- oder Geschlechtername
		               f = Person, stellvertretend fuer ihre Familie
		               g = Person, stellvertretend fuer eine ihr
		                   zugeordnete Einrichtung (keine Koerperschaft)

		            3  Personentyp
		               a = Person mit modernem Namen in einer
		                   europaeischen Sprache
		               b = Person mit modernem Namen in einer nicht-
		                   europaeischen Sprache
		               c = Person mit biblischem Namen
		               d = Person mit altgriechischem Namen
		               e = Person mit altroemischem Namen
		               f = Person mit sonstigem Namen des Altertums
		               g = Person mit mittelalterlichem Namen
		                   in einer europaeischen Sprache
		               h = Person mit mittelalterlichem Namen in einer
		                   nicht-europaeischen Sprache
		               i = Person mit byzantinischem Namen
		               j = Person mit Fuerstennamen
		               k = Person mit Namen eines geistlichen
		                   Wuerdentraegers
	 */
	/*-

		066       NORMDATENSPEZIFISCHE ANGABEN ZUR GKD

		          Indikator:
		          blank = nicht defininiert

		          Datenelemente:
		            0  Typ der Koerperschaft
		               blank = nicht spezifizierte Koerperschaft
		               c     = Kongress (pauschal)
		               d     = Kongress (einzeln)
		               f     = Firma
		               g     = Gebietskoerperschaft
		               k     = kirchliche Koerperschaft
		               m     = musikalische Koerperschaft
		               u     = Un-Koerperschaft

		            1  Stufe der Koerperschaft
		               blank = oberste Stufe
		               a     = nachgeordnete Stufe

	 */
	/*-
		067       NORMDATENSPEZIFISCHE ANGABEN ZUR SWD

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		            0  Schlagwortkategorie
		               p = Personenschlagwort
		               k = Koerperschaftsschlagwort (fuer Koerperschaften,
		                   die unter ihrem Individualnamen angesetzt werden)
		               c = Koerperschaftsschlagwort (fuer Koerperschaften,
		                   die unter einem Geographikum angesetzt werden)
		               g = geographisches/ethnographisches Schlagwort
		               t = Sachtitel eines Werkes
		               s = Sachschlagwort
		               f = Formschlagwort
		               z = Zeitschlagwort

		            1  Permutationskennung fuer Hauptschlagwort bzw.
		               Schlagwortansetzung
		               1 = konstanter Wert

		            2  Permutationskennung fuer Hauptschlagwort bzw.
		               Schlagwortansetzung
		               0 = Feld 801 wird nicht permutiert
		               1 = Feld 801 wird permutiert

		            3  Permutationskennung fuer Hauptschlagwort bzw.
		               Schlagwortansetzung
		               0 = Feld 802 wird nicht permutiert

		            4  Permutationskennung fuer Hauptschlagwort bzw.
		               Schlagwortansetzung
		               0 = Feld 803 wird nicht permutiert

		            5  Permutationskennung fuer Hauptschlagwort bzw.
		               Schlagwortansetzung
		               0 = Feld 804 wird nicht permutiert

		            6  Permutationskennung fuer Hauptschlagwort bzw.
		               Schlagwortansetzung
		               0 = Feld 805 wird nicht permutiert

		            7  Hinweissatz
		               a = Hinweissatz zur Benutzung in der SWD

	 */
	/*-
		068       NORMDATENSPEZIFISCHE CODIERUNGEN

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Teilbestandskennzeichen
		          b     = Autorisierungskennzeichen
		          c     = Verwendungskennzeichen
		          d     = Herkunftskennzeichen
		          e     = Nutzungskennzeichen
		          z     = sonstige Codierung



	 */
	/*-
		070-075   SEGMENT ANWENDERSPEZIFISCHE DATEN UND CODES
	 */
	/*-
		070       IDENTIFIZIERUNGSMERKMALE DER BEARBEITENDEN INSTITUTION

		          Indikator:
		          blank = Kennzeichen der katalogisierenden Institution
		          a     = Kennzeichen der liefernden Institution
		          b     = Kennzeichen der korrigierenden Institution
	 */
	/*-

		071       IDENTIFIZIERUNGSMERKMALE DER BESITZENDEN INSTITUTION

		          Indikator:
		          blank = Kennzeichen der besitzenden Institution
		                  (Bibliothekssigel)
		          a     = Bibliothekskennzeichnung der besitzenden
		                  Bibliothek (= BIK)
		          b     = Identifikationsnummer der Deutschen
		                  Bibliotheksstatistik (DBS)
		          c     = Regionales Bibliothekskennzeichen

	 */
	/*-
		072       CODIERTE ANGABEN ZUR BESITZENDEN INSTITUTION

		          Indikator:
		          blank = nicht definiert

		          Datenelemente:
		          0-2  Leihverkehrsregion
		               BAW = Baden-Wuerttemberg, Saarland und Teile von
		                     Rheinland-Pfalz
		               BAY = Bayern
		               BER = Berlin und Brandenburg
		               HAM = Hamburg, Bremen und Schleswig-Holstein
		               HES = Hessen und Teile von Rheinland-Pfalz
		               MEC = Mecklenburg-Vorpommern
		               NIE = Niedersachsen
		               NRW = Nordrhein-Westfalen und Teile von Rheinland-
		                     Pfalz
		               SAA = Sachsen-Anhalt
		               SAX = Sachsen
		               THU = Thueringen
		               WEU = Europaeisches Ausland
		               WWW = regional und national uebergreifende Bestaende

		            3  Leihverkehrsrelevanz der besitzenden Bibliothek
		               l = leihverkehrsrelevante Bibliothek
		               n = nicht leihverkehrsrelevante Bibliothek
		               u = unbekannt bzw. nicht definiert

		            4  Benutzungsbeschraenkungen / Ausleihindikator

		            5  Geschaeftsgangstatus
	 */
	/*-

		073       SONDERSAMMELGEBIETSNUMMER

		          Indikator:
		          blank = nicht definiert

	 */
	/*-
		074       SONDERSAMMELGEBIETSNOTATION

		          Indikator:
		          blank = nicht definiert

	 */
	/*-
		075       ZDB-PRIORITAETSZAHL

		          Indikator:
		          blank = nicht definiert


	 */
	/*-

		076-088   SEGMENT ANWENDERSPEZIFISCHE ANGABEN
	 */
	/*-
		076       FREI DEFINIERBARE ANWENDERSPEZIFISCHE ANGABEN,
		          KENNZEICHEN UND CODES

		...
	 */
	/*-
		079       FREI DEFINIERBARE ANWENDERSPEZIFISCHE ANGABEN,
		          KENNZEICHEN UND CODES
	 */
	/*-
		080       ZUGRIFFS- UND UPDATE-ANWEISUNGEN
	 */
	/*-
		081       FREI DEFINIERBARE ANWENDERSPEZIFISCHE ANGABEN,
		          KENNZEICHEN UND CODES

		...
	 */
	/*-
		088       FREI DEFINIERBARE ANWENDERSPEZIFISCHE ANGABEN,
		          KENNZEICHEN UND CODES



		http://www.d-nb.de/standardisierung/txt/titelmab.txt
		                               MAB2-TITEL
		                       Online-Kurzreferenz-Version
		                          Stand: November 2001



		          SATZKENNUNG


		001-088   SEGMENT 0--

		001-029   IDENTIFIKATIONSNUMMERN, DATUMS- UND VERSIONSANGABEN
		030-035   ALLGEMEINE VERARBEITUNGSTECHNISCHE ANGABEN
		036-049   ALLGEMEINE CODIERTE ANGABEN
		050-064   VEROEFFENTLICHUNGS- UND MATERIALSPEZIFISCHE ANGABEN
		070-075   ANWENDERSPEZIFISCHE DATEN UND CODES
		076-088   ANWENDERSPEZIFISCHE ANGABEN

	 */
	/*-

	 */
	/*-
		089-090   SEGMENT BANDANGABEN
	 */
	/*-
		089       BANDANGABEN IN VORLAGEFORM

		          Indikator:
		          blank = nicht definiert
	 */
	/*-

		090       BANDANGABEN IN SORTIERFORM

		          Indikator:
		          blank = nicht definiert

	 */
	/*-


		1--       SEGMENT PERSONENNAMEN

		100       NAME DER 1. PERSON IN ANSETZUNGSFORM

		          Indikator:
		          blank = Name des 1. Verfassers
		                  Haupteintragung
		          b     = Name der 1. sonstigen beteiligten Person
		                  einteilige Nebeneintragung
		          c     = Name der 1. sonstigen beteiligten Person
		                  ein- und zweiteilige Nebeneintragung
		          f     = Name der 1. gefeierten Person
		                  zweiteilige  Nebeneintragung  mit  dem
		                  Formalsachtitel 'Festschrift'
		          e     = Name des 1. Interpreten
		                  einteilige Nebeneintragung
	 */
	/*-

		101       VERWEISUNGSFORMEN ZUM NAMEN DER 1. PERSON

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Pseudonym
		          b     = wirklicher Name
		          c     = frueherer Name
		          d     = spaeterer Name
		          z     = zusaetzliche, weitere Verweisungsform

	 */
	/*-
		102       IDENTIFIKATIONSNUMMER  DES PERSONENNAMENSATZES DER
		          1. PERSON

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Ueberregionale Identifikationsnummer
		          b     = Regionale Identifikationsnummer
		          c     = Lokale Identifikationsnummer
	 */
	/*-

		103       KOERPERSCHAFT, BEI DER DIE 1. PERSON BESCHAEFTIGT IST

		          Indikator:
		          blank = nicht definiert
	 */
	/*-

		104       NAME DER 2. PERSON IN ANSETZUNGSFORM

		          Indikator:
		          a = Name des 2. Verfassers
		              zweiteilige Nebeneintragung
		          b = Name der 2. sonstigen beteiligten Person
		              einteilige Nebeneintragung
		          c = Name der 2. sonstigen beteiligten Person
		              ein- und zweiteilige Nebeneintragung
		          f = Name der 2. gefeierten Person
		              zweiteilige Nebeneintragung mit dem Formalsachtitel
		              'Festschrift'
		          e = Name des 2. Interpreten
		              einteilige Nebeneintragung
	 */
	/*-

		105       VERWEISUNGSFORMEN ZUM NAMEN DER 2. PERSON
		106       IDENTIFIKATIONSNUMMER  DES PERSONENNAMENSATZES DER
		          2. PERSON
		107       KOERPERSCHAFT, BEI DER DIE 2. PERSON BESCHAEFTIGT IST

		...

		196       NAME DER 25. PERSON IN ANSETZUNGSFORM
		197       VERWEISUNGSFORMEN ZUM NAMEN DER 25. PERSON
		198       IDENTIFIKATIONSNUMMER DES PERSONENNAMENSATZES DER
		          25. PERSON
		199       KOERPERSCHAFT, BEI DER DIE 25. PERSON BESCHAEFTIGT IST




		2--       SEGMENT KOERPERSCHAFTSNAMEN

		200       NAME DER 1. KOERPERSCHAFT IN ANSETZUNGSFORM

		          Indikator:
		          blank = Name des 1. Urhebers
		                  Haupteintragung
		          b     = Name des 1. Urhebers oder der 1. sonstigen
		                  beteiligten Koerperschaft
		                  einteilige Nebeneintragung
		          c     = Name des 1. Urhebers oder der 1. sonstigen
		                  beteiligten Koerperschaft
		                  ein- und zweiteilige Nebeneintragung
		          e     = Name des 1. Interpreten
		                  einteilige Nebeneintragung


		201       VERWEISUNGSFORMEN ZUM NAMEN DER 1. KOERPERSCHAFT

		          Indikator:
		          blank = nicht spezifiziert
		          z     = weitere, zusaetzliche Verweisungsform


		202       IDENTIFIKATIONSNUMMER DES KOERPERSCHAFTSNAMENSATZES DER
		          1. KOERPERSCHAFT

		          Indikator:
		          blank = nicht definiert
		          a     = Ueberregionale Identifikationsnummer
		          b     = Regionale Identifikationsnummer
		          c     = Lokale Identifikationsnummer


		204       Name der 2. Koerperschaft in Ansetzungsform

		          Indikator:
		          a = Name des 2. Urhebers
		              zweiteilige Nebeneintragung
		          b = Name des 2. Urhebers oder der sonstigen
		              beteiligten Koerperschaft
		              einteilige Nebeneintragung
		          c = Name des 2. Urhebers oder der sonstigen
		              beteiligten Koerperschaft
		              ein- und zweiteilige Nebeneintragung
		          e = Name des Interpreten
		              einteilige Nebeneintragung


		205       VERWEISUNGSFORMEN ZUM NAMEN DER 2. KOERPERSCHAFT
		206       IDENTIFIKATIONSNUMMER DES KOERPERSCHAFTSNAMENSATZES
		          DER 2. KOERPERSCHAFT

		...

		296       NAME DER 25. KOERPERSCHAFT IN ANSETZUNGSFORM
		297       VERWEISUNGSFORMEN ZUM NAMEN DER 25. KOERPERSCHAFT
		298       IDENTIFIKATIONSNUMMER DES KOERPERSCHAFTSNAMENSATZES
		          DER 25. KOERPERSCHAFT




		3--       SEGMENT SACHTITEL

		300       SAMMLUNGSVERMERK

		          Indikator:
		          blank = nicht definiert


		304       EINHEITSSACHTITEL

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Sachtitel
		          b     = zusaetzliche Nebeneintragung mit dem Sachtitel


		310       HAUPTSACHTITEL IN ANSETZUNGSFORM

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Sachtitel
		          b     = zusaetzliche Nebeneintragung mit dem Sachtitel


		331       HAUPTSACHTITEL IN VORLAGEFORM ODER MISCHFORM

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Sachtitel
		          b     = zusaetzliche Nebeneintragung mit dem Sachtitel


		333       ZU ERGAENZENDE URHEBER ZUM HAUPTSACHTITEL

		          Indikator:
		          blank = nicht definiert


		334       ALLGEMEINE MATERIALBENENNUNG

		          Indikator:
		          blank = nicht definiert


		335       ZUSAETZE ZUM HAUPTSACHTITEL

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Zusatz
		          b     = zusaetzliche Nebeneintragung mit dem Zusatz


		340       1. PARALLELSACHTITEL IN ANSETZUNGSFORM

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Sachtitel
		          b     = zusaetzliche Nebeneintragung mit dem Sachtitel


		341       1. PARALLELSACHTITEL IN VORLAGEFORM ODER MISCHFORM

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Sachtitel
		          b     = zusaetzliche Nebeneintragung mit dem Sachtitel


		342       ZU ERGAENZENDE URHEBER ZUM 1. PARALLELSACHTITEL

		          Indikator:
		          blank = nicht definiert


		343       ZUSAETZE ZUM 1. PARALLELSACHTITEL

		          Indikator:
		          blank = keine Nebeneintragung
		          a     = zusaetzliche Nebeneintragung unter dem Zusatz
		          b     = zusaetzliche Nebeneintragung mit dem Zusatz


		344       2. PARALLELSACHTITEL IN ANSETZUNGSFORM
		345       2. PARALLELSACHTITEL IN VORLAGEFORM ODER MISCHFORM
		346       ZU ERGAENZENDE URHEBER ZUM 2. PARALLELSACHTITEL
		347       ZUSAETZE ZUM 2. PARALLELSACHTITEL

		...

		352       4. PARALLELSACHTITEL IN ANSETZUNGSFORM
		353       4. PARALLELSACHTITEL IN VORLAGEFORM ODER MISCHFORM
		354       ZU ERGAENZENDE URHEBER ZUM 4. PARALLELSACHTITEL
		355       ZUSAETZE ZUM 4. PARALLELSACHTITEL


		359       VERFASSERANGABE

		          Indikator:
		          blank = nicht definiert


		360       UNTERREIHEN

		          Indikator:
		          blank = nicht definiert


		361       BEIGEFUEGTE WERKE

		          Indikator:
		          blank = nicht definiert


		365       ZUSAETZE ZUR GESAMTEN VORLAGE

		          Indikator:
		          blank = nicht definiert


		369       VERFASSERANGABE ZUR GESAMTEN VORLAGE

		          Indikator:
		          blank = nicht definiert


		370       WEITERE SACHTITEL

		          Indikator:
		          a = zusaetzliche Nebeneintragung unter dem Sachtitel
		          b = zusaetzliche Nebeneintragung mit dem Sachtitel
		          c = zusaetzliche Nebeneintragung unter und mit dem
		              Sachtitel


		376       NORMIERTE ZEITSCHRIFTENTITEL

		          Indikator:
		          blank = nicht definiert
		          a     = Kurztitel nach DIN 1502
		          b     = CODEN
		          c     = Key Title nach DIN 1430
		          d     = Kurztitel nach INIS




		400-437   SEGMENT VEROEFFENTLICHUNGSVERMERK, UMFANG, BEIGABEN

		400       AUSGABEBEZEICHNUNG IN NORMIERTER FORM

		          Indikator:
		          blank = nicht definiert


		403       AUSGABEBEZEICHNUNG IN VORLAGEFORM

		          Indikator:
		          blank = nicht definiert


		405       ERSCHEINUNGSVERLAUF

		          Indikator:
		          blank = nicht definiert


		406       NORMIERTER ERSCHEINUNGSVERLAUF

		          Indikator:
		          blank = nicht spezifiziert
		          a     = abgeschlossenes Erscheinen
		          b     = fortlaufendes Erscheinen

		          Unterfelder:
		          Beginngruppe:
		          $5    = Parallele Zaehlung
		          $f    = Sachliche Benennung
		          $d    = Bandzaehlung
		          $e    = Heft
		          $b    = Tag
		          $c    = Monat
		          $j    = Berichtszeit bzw. Erscheinungszeit
		          $h    = Abweichende Erscheinungszeit
		          $g    = Kommentar zur Beginngruppe

		          Endegruppe:
		          $n    = Bandzaehlung
		          $o    = Heft
		          $l    = Tag
		          $m    = Monat
		          $k    = Berichtszeit bzw. Erscheinungszeit
		          $i    = Abweichende Erscheinungszeit
		          $q    = Kommentar zur Endegruppe


		407       KARTOGRAPHISCHE MATERIALIEN: MATHEMATISCHE ANGABEN

		          Indikator:
		          blank = nicht definiert


		410       ORT(E) DES 1. VERLEGERS, DRUCKERS USW.

		          Indikator:
		          blank = Verlagsort(e)
		          a     = Druckort(e)
		          b     = Vertriebsort(e)
		          c     = Auslieferungsort(e)
		          u     = nicht spezifiziert


		411       ADRESSE DES 1. VERLEGERS, DRUCKERS USW.

		          Indikator:
		          blank = nicht definiert


		412       NAME DES 1. VERLEGERS, DRUCKERS USW.

		          Indikator:
		          blank = Verleger
		          a     = Drucker
		          b     = Vertrieb
		          c     = Auslieferer
		          u     = nicht spezifiziert

		415       ORT(E) DES 2. VERLEGERS, DRUCKERS USW.
		416       ADRESSE DES 2. VERLEGERS, DRUCKERS USW.
		417       NAME DES 2. VERLEGERS, DRUCKERS USW.


		418       ANGABEN ZU WEITEREN UND/ODER FRUEHEREN VERLEGERN, DRUCKERN ETC.

		          Indikator:
		          blank = Verleger
		          a     = Drucker
		          b     = Vertrieb
		          c     = Auslieferer
		          u     = nicht spezifiziert

		          Unterfelder:
		          $a    = Ort(e)
		          $b    = Strasse der postalischen Adresse
		          $c    = Hausnummer der postalischen Adresse
		          $d    = Postfach der postalischen Adresse
		          $e    = Postleitzahl der postalischen Adresse
		          $f    = Ort der postalischen Adresse
		          $g    = Name
		          $h    = Datierung
		          $u    = nicht spezifiziert


		420       MEHRTEILIGE, UNSELBSTAENDIG ERSCHIENENE WERKE:
		          ZUSAMMENFASSENDE UND OFFENE AUFFUEHRUNG VON TEILEN

		          Indikator:
		          blank = nicht definiert


		425       ERSCHEINUNGSJAHR(E)

		          Indikator:
		          blank = Erscheinungsjahr(e) in Vorlageform
		          a     = Erscheinungsjahr(e) in Ansetzungsform
		          b     = Erscheinungsjahr des ersten Bandes in Ansetzungsform
		          c     = Erscheinungsjahr des letzten Bandes in Ansetzungsform
		          p     = Publikationsdatum bei Tontraegern (P-Datum)


		426       DATUMSANGABEN

		          Indikator:
		          blank = Datumsangaben in Vorlageform
		          a     = Datumsangaben in normierter Form


		427       ZUSAMMENFASSENDE BESTANDSANGABE

		          Indikator:
		          blank = nicht definiert


		429       BESTANDSLUECKEN

		          Indikator:
		          blank = nicht definiert


		431       ZUSAMMENFASSENDE REGISTER

		          Indikator:
		          blank = nicht definiert


		432       ZUSAMMENFASSENDE UND OFFENE BANDAUFFUEHRUNG

		          Indikator:
		          blank = nicht definiert


		433       UMFANGSANGABE

		          Indikator:
		          blank = Umfangsangabe
		          a     = Zusammenfassung der Baende nach abgeschlossenem
		                  Erscheinen eines mehrbaendigen begrenzten Werkes
		          b     = Zaehlungsangabe bei unselbstaendig erschienenen
		                  Werken
		          c     = Anzahl und Materialbenennung physischer
		                  Einheiten


		434       ILLUSTRATIONSANGABE / TECHNISCHE ANGABEN ZU TONTRAEGERN

		          Indikator:
		          blank = Illustrationsangabe
		          a     = Technisches System
		          b     = Laufgeschwindigkeit bei Tontraegern
		          c     = Umdrehungszahl bei Schallplatten
		          d     = Aufnahme- und Wiedergabeverfahren


		435       FORMATANGABE

		          Indikator:
		          blank = Formatangabe
		          a     = Durchmesser von Tontraegern


		437       ANGABE VON BEGLEITMATERIALIEN

		          Indikator:
		          blank = nicht definiert




		451-496   SEGMENT GESAMTTITELANGABEN


		451-456   1. GESAMTTITEL

		451       1. GESAMTTITEL IN VORLAGEFORM

		          Indikator:
		          blank = 1. Gesamttitel mit Zaehlung der Stuecktitel
		          a     = 1. Gesamttitel mit Zaehlung der Stuecktitel -
		                  die Angabe der Zaehlung erfolgt auf nachfolgender
		                  Hierarchiestufe
		          b     = 1. Gesamttitel ohne Zaehlung der Stuecktitel


		452       STANDARDNUMMERN DES 1. GESAMTTITELS

		          Indikator:
		          a = ISSN formal richtig
		          b = ISSN formal falsch
		          c = ISBN formal richtig
		          d = ISBN formal falsch
		          e = ISMN formal richtig
		          f = ISMN formal falsch
		          z = sonstige Standardnummern


		453       IDENTIFIKATIONSNUMMER DES 1. GESAMTTITELS

		          Indikator:
		          blank = nicht definiert
		          m     = mehrbaendiges begrenztes Werk
		          r     = Schriftenreihe oder anderes fortlaufendes
		                  Sammelwerk


		454       1. GESAMTTITEL IN ANSETZUNGSFORM

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Verfasserwerk
		          b     = Urheberwerk
		          c     = Sachtitelwerk


		455       BANDANGABE

		          Indikator:
		          blank = nicht definiert


		456       BANDANGABE IN SORTIERFORM

		          Indikator:
		          blank = nicht definiert


		461-466   2. GESAMTTITEL

		461       2. GESAMTTITEL IN VORLAGEFORM

		          Indikator:
		          blank = 2. Gesamttitel mit Zaehlung der Stuecktitel
		          a     = 2. Gesamttitel mit Zaehlung der Stuecktitel -
		                  Angabe der Zaehlung erfolgt auf nachfolgender
		                  Hierarchiestufe
		          b     = 2. Gesamttitel ohne Zaehlung der Stuecktitel
		          c     = 2. Gesamttitel mit Zaehlung der Stuecktitel -
		                  ohne maschinelle Generierung eines Stuecktitelnachtrags
		                  (Deskriptivform)


		462       STANDARDNUMMERN DES 2. GESAMTTITELS
		463       IDENTIFIKATIONSNUMMER DES 2. GESAMTTITELS
		464       2. GESAMTTITEL IN ANSETZUNGSFORM
		465       BANDANGABE
		466       BANDANGABE IN SORTIERFORM


		471-476   3. GESAMTTITEL

		471       3. GESAMTTITEL IN VORLAGEFORM
		472       STANDARDNUMMERN DES 3. GESAMTTITELS
		473       IDENTIFIKATIONSNUMMER DES 3. GESAMTTITELS
		474       3. GESAMTTITEL IN ANSETZUNGSFORM
		475       BANDANGABE
		476       BANDANGABE IN SORTIERFORM


		481-486   4. GESAMTTITEL

		481       4. GESAMTTITEL IN VORLAGEFORM
		482       STANDARDNUMMERN DES 4. GESAMTTITELS
		483       IDENTIFIKATIONSNUMMER DES 4. GESAMTTITELS
		484       4. GESAMTTITEL IN ANSETZUNGSFORM
		485       BANDANGABE
		486       BANDANGABE IN SORTIERFORM


		491-496   5. GESAMTTITEL

		491       5. GESAMTTITEL IN VORLAGEFORM
		492       STANDARDNUMMERN DES 5. GESAMTTITELS
		493       IDENTIFIKATIONSNUMMER DES 5. GESAMTTITELS
		494       5. GESAMTTITEL IN ANSETZUNGSFORM
		495       BANDANGABE
		496       BANDANGABE IN SORTIERFORM




		501-539   SEGMENT FUSSNOTEN

		501       SAMMELFELD FUER UNAUFGEGLIEDERTE FUSSNOTEN

		          Indikator:
		          blank = nicht definiert


		502       EINHEITSSACHTITEL EINES BEIGEFUEGTEN ODER KOMMENTIERTEN
		          WERKES

		          Indikator:
		          blank = nicht definiert


		503       DEUTSCHE UEBERSETZUNG DES HAUPTSACHTITELS BZW.
		          HINWEIS AUF DIE MUSIKALISCHE FORM

		          Indikator:
		          blank = Deutsche Uebersetzung des Hauptsachtitels
		          a     = Hinweis auf die musikalische Form und/oder
		                  Besetzung


		504       ANGABE VON PARALLELTITELN

		          Indikator:
		          blank = nicht definiert


		505       ANGABE VON NEBENTITELN

		          Indikator:
		          blank = nicht definiert


		507       ANGABEN ZUM HAUPTSACHTITEL UND ZU DEN ZUSAETZEN

		          Indikator:
		          blank = nicht definiert


		508       ANGABE DER QUELLE DER AUFNAHME

		          Indikator:
		          blank = nicht definiert


		509       VERMERKE ZUR VERFASSERANGABE

		          Indikator:
		          blank = nicht definiert


		510       ANGABEN ZUR AUSGABEBEZEICHNUNG

		          Indikator:
		          blank = nicht definiert


		511       ANGABEN ZUM ERSCHEINUNGSVERMERK

		          Indikator:
		          blank = nicht definiert


		512       ANGABEN ZUM KOLLATIONSVERMERK BZW. ZUR PHYSISCHEN
		          BESCHREIBUNG

		          Indikator:
		          blank = Angaben zum Kollationsvermerk
		          a     = Angaben zur physischen Beschreibung


		513       AENDERUNGEN IM IMPRESSUM

		          Indikator:
		          blank = nicht definiert


		515       ERGAENZUNGEN ZUR GESAMTTITELANGABE

		          Indikator:
		          blank = nicht definiert


		516       ANGABEN UEBER SCHRIFT, SPRACHE UND VOLLSTAENDIGKEIT DER
		          VORLAGE UND MUSIKALISCHE NOTATION

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Angaben ueber die Sprache der Vorlage
		          b     = Angaben ueber die Schrift der Vorlage
		          c     = Angaben ueber die Vollstaendigkeit der Vorlage
		          d     = Angaben ueber die musikalische Notation


		517       ANGABEN ZUM INHALT

		          Indikator:
		          blank = Angaben zum Inhalt
		          a     = beigefuegte Werke
		          b     = enthaltene Werke
		          c     = Inhaltsverzeichnis


		518       ANGABE DER NAMEN VON INTERPRETEN BZW. WEITERE ANGABEN
		          ZUR INTERPRETATION

		          Indikator:
		          a = Angabe der Namen von Interpreten
		          b = weitere Angaben zur Interpretation


		519       HOCHSCHULSCHRIFTENVERMERK

		          Indikator:
		          blank = nicht definiert


		522       TEILUNGSVERMERK BEI FORTLAUFENDEN SAMMELWERKEN

		          Indikator:
		          blank = nicht definiert


		523       ANGABEN UEBER ERSCHEINUNGSWEISE UND ERSCHEINUNGSDAUER

		          Indikator:
		          blank = nicht definiert


		524       HINWEISE AUF UNSELBSTAENDIG ENTHALTENE WERKE

		          Indikator:
		          blank = nicht definiert


		525       HERKUNFTSANGABEN

		          Indikator:
		          blank = nicht definiert


		526       TITEL VON REZENSIERTEN WERKEN

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		527       HINWEISE AUF PARALLELE AUSGABEN

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		528       TITEL VON REZENSIONEN

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		529       TITEL VON FORTLAUFENDEN BEILAGEN

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		530       TITEL VON BEZUGSWERKEN

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		531       HINWEISE AUF FRUEHERE AUSGABEN UND BAENDE

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		532       HINWEISE AUF FRUEHERE UND SPAETERE SOWIE ZEITWEISE
		          GUELTIGE TITEL

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		533       HINWEISE AUF SPAETERE AUSGABEN UND BAENDE

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		534       TITELKONKORDANZEN

		          Indikator:
		          blank = verbale Beschreibung
		          x     = reziproke Beziehung
		          y     = nicht reziproke Beziehung
		          z     = nicht differenzierte Beziehung


		535       ANZAHL VON EXEMPLAREN

		          Indikator:
		          blank = nicht definiert


		536       VORAUSSICHTLICHER ERSCHEINUNGSTERMIN

		          Indikator:
		          blank = nicht definiert


		537       REDAKTIONELLE BEMERKUNGEN

		          Indikator:
		          blank = nicht definiert


		538       ANGABE DER VERVIELFAELTIGUNGSART

		          Indikator:
		          blank = nicht definiert




		540-589   SEGMENT STANDARDNUMMERN

		540       INTERNATIONALE STANDARDBUCHNUMMER (ISBN)

		          Indikator:
		          blank = ISBN formal nicht geprueft
		          a     = ISBN formal richtig
		          b     = ISBN formal falsch
		          z     = keine ISBN, aber Einbandart und/oder Preis


		541       INTERNATIONALE STANDARDNUMMER FUER MUSIKALIEN (ISMN)

		          Indikator:
		          blank = ISMN formal nicht geprueft
		          a     = ISMN formal richtig
		          b     = ISMN formal falsch
		          z     = keine ISMN, aber Einbandart und/oder Preis


		542       INTERNATIONALE STANDARDNUMMER FUER FORTLAUFENDE
		          SAMMELWERKE (ISSN)

		          Indikator:
		          blank = ISSN formal nicht geprueft
		          a     = ISSN formal richtig
		          b     = ISSN formal falsch
		          z     = keine ISSN, aber Einbandart und/oder Preis


		543       INTERNATIONALE STANDARDNUMMER FUER REPORTS (ISRN)

		          Indikator:
		          blank = ISRN formal nicht geprueft
		          a     = ISRN formal richtig
		          b     = ISRN formal falsch
		          z     = keine ISRN, aber Einbandart und/oder Preis


		544       LOKALE SIGNATUR

		          Indikator:
		          blank = keine Benutzungsbeschraenkung
		          a     = nicht verleihbar


		546       POSTVERTRIEBSKENNZEICHEN

		          Indikator:
		          blank = nicht definiert


		550       AMTLICHE DRUCKSCHRIFTENNUMMER

		          Indikator:
		          blank = nicht definiert


		551       VERLAGS-, PRODUKTIONS- UND BESTELLNUMMER VON MUSIKALIEN
		          UND TONTRAEGERN

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Verlags- und Firmenbestellnummer
		          b     = Druckplattennummer bei Musikalien
		          c     = Plattennummer
		          d     = Setnummer
		          e     = Produktionsnummer
		          f     = Kompaktkassettennummer


		552       DIGITAL OBJECT IDENTIFIER (DOI)

		          Indikator:
		          blank = nicht definiert


		553       ARTIKELNUMMER

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Internationale Artikelnummer (EAN)
		          b     = Universal Product Code (UPC)


		554       HOCHSCHULSCHRIFTENNUMMER

		          Indikator:
		          blank = nicht definiert


		556       REPORTNUMMER

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = Reportnummer
		          b     = Kontraktnummer
		          c     = Task-Nummer


		562       PATENTNUMMER

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = Patentschrift
		          b     = Offenlegungsschrift
		          c     = Auslegeschrift


		564       NORMNUMMER

		          Indikator:
		          blank = nicht definiert


		566       FIRMENSCHRIFTENNUMMER

		          Indikator:
		          blank = nicht definiert


		568       NATIONALBIBLIOGRAPHIENUMMER DER CIP-AUFNAHME

		          Indikator:
		          blank = nicht definiert


		570       NATIONALBIBLIOGRAPHIENUMMER DER FALSCHEN AUFNAHME

		          Indikator:
		          blank = nicht definiert


		574       NATIONALBIBLIOGRAPHIENUMMER (NBN)

		          Indikator:
		          blank = nicht definiert


		576       PFLICHTABLIEFERUNGSNUMMER

		          Indikator:
		          blank = nicht definiert


		578       FINGERPRINT

		          Indikator:
		          blank = nicht definiert


		580       SONSTIGE STANDARDNUMMERN

		          Indikator:
		          blank = nicht definiert


		581-589   NICHT BENUTZT




		590-599   SEGMENT HERKUNFT (QUELLE) UNSELBSTAENDIG ERSCHIENENER
		          WERKE

		590       HAUPTSACHTITEL UND GGF. ZU ERGAENZENDE URHEBER DER
		          QUELLE

		          Indikator:
		          blank = nicht definiert


		591       VERFASSERANGABE DER QUELLE

		          Indikator:
		          blank = nicht definiert


		592       ABTEILUNG / UNTERREIHE DER QUELLE

		          Indikator:
		          blank = nicht definiert


		593       AUSGABEBEZEICHNUNG DER QUELLE IN VORLAGEFORM

		          Indikator:
		          blank = nicht definiert


		594       ERSCHEINUNGSORT DER QUELLE

		          Indikator:
		          blank = Verlagsort(e)
		          a     = Druckort(e)
		          b     = Vertriebsort(e)
		          c     = Auslieferungsort(e)


		595       ERSCHEINUNGSJAHR DER QUELLE

		          Indikator:
		          blank = nicht definiert


		596       BANDZAEHLUNG, JAHRESZAEHLUNG, HEFTZAEHLUNG, UMFANGS-
		          UND ILLUSTRATIONSANGABE DER QUELLE

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = zusaetzlich mit Band- und/oder Heftbezeichnung
		          b     = zusaetzlich mit Tagesdatum bei Zeitungen
		          c     = zusaetzlich mit Legislaturperiode bei
		                  Parlamentsveroeffentlichungen


		597       GESAMTTITEL DER QUELLE

		          Indikator:
		          blank = nicht definiert


		598       FUSSNOTE DER QUELLE

		          Indikator:
		          blank = nicht definiert


		599       STANDARDNUMMERN DER QUELLE

		          Indikator:
		          blank = Identifikationsnummer der selbstaendigen Schrift
		          a     = ISSN formal richtig
		          b     = ISSN formal falsch
		          c     = ISBN formal richtig
		          d     = ISBN formal falsch
		          e     = ISMN formal richtig
		          f     = ISMN formal falsch
		          g     = ISRN formal richtig
		          h     = ISRN formal falsch
		          s     = Identifikationsnummer der ZDB




		600-603   SEGMENT PAUSCHALVERWEISUNGEN UND SIEHE-AUCH-HINWEISE

		600       1. TEIL DER VERWEISUNG

		          Indikator:
		          a = allgemein
		          b = Personennamen (nicht differenziert)
		          c = persoenliche Namen
		          d = Familiennamen
		          e = Koerperschaftsnamen
		          f = Sachtitel


		601       BEMERKUNGEN ZUM 1. TEIL DER VERWEISUNG

		          Indikator:
		          blank = nicht definiert


		602       2. TEIL DER VERWEISUNG

		          Indikator:
		          a = Pauschalverweisung
		          b = Siehe-auch-Hinweis


		603       BEMERKUNGEN ZUM 2. TEIL DER VERWEISUNG

		          Indikator:
		          blank = nicht definiert




		610-650   SEGMENT AUSGABEVERMERK SEKUNDAERFORMEN

		610       FUSSNOTE ZUR SEKUNDAERAUSGABE

		          Indikator:
		          blank = einleitende Wendung
		          a     = Angaben zur Sekundaerform


		611       ORT(E) DES 1. VERLEGERS, HERSTELLERS USW.

		          Indikator:
		          blank = Verlagsort(e)
		          a     = Herstellungsort(e)
		          b     = Vertriebsort(e)
		          c     = Auslieferungsort(e)
		          u     = nicht spezifiziert


		612       ADRESSE DES 1. VERLEGERS, HERSTELLERS USW.

		          Indikator:
		          blank = Verlegeradresse(n)
		          a     = Herstelleradresse(n)
		          b     = Vertriebsadresse(n)
		          c     = Auslieferungsadresse(n)
		          u     = nicht spezifiziert


		613       NAME DES 1. VERLEGERS, HERSTELLERS USW.

		          Indikator:
		          blank = Verleger
		          a     = Hersteller
		          b     = Vertrieb
		          c     = Auslieferer
		          u     = nicht spezifiziert


		614       ORT(E) DES 2. VERLEGERS, HERSTELLERS USW.
		615       ADRESSE DES 2. VERLEGERS, HERSTELLERS USW.
		616       NAME DES 2. VERLEGERS, HERSTELLERS USW.


		617       1. URHEBER DER VERFILMUNG

		          Indikator:
		          blank = Name des 1. Verfilmungsurhebers
		          a     = Adresse des 1. Verfilmungsurhebers


		618       2. URHEBER DER VERFILMUNG

		          Indikator:
		          blank = Name des 2. Verfilmungsurhebers
		          a     = Adresse des 2. Verfilmungsurhebers


		619       ERSCHEINUNGSJAHR(E) DER SEKUNDAERFORM

		          Indikator:
		          blank = Erscheinungsjahr(e) in Vorlageform
		          a     = Erscheinungsjahr(e) in Ansetzungsform
		          b     = Erscheinungsjahr des ersten Bandes in Ansetzungsform
		          c     = Erscheinungsjahr des letzten Bandes in Ansetzungsform


		620       HINWEISE ZUR VERFILMUNG

		          Indikator:
		          blank = Verfilmungsdatum
		          a     = Datum der geplanten Verfilmung
		          b     = Kennzeichen der Institution, die eine Verfilmung plant


		621-626   1. GESAMTTITEL DER SEKUNDAERFORM

		621       1. GESAMTTITEL DER SEKUNDAERFORM IN VORLAGEFORM

		          Indikator:
		          blank = 1. Gesamttitel mit Zaehlung der Stuecktitel
		          a     = 1. Gesamttitel mit Zaehlung der Stuecktitel -
		                  die Angabe der Zaehlung erfolgt auf
		                  nachfolgender Hierarchiestufe
		          b     = 1. Gesamttitel ohne Zaehlung der Stuecktitel


		622       STANDARDNUMMERN DES 1. GESAMTTITELS DER SEKUNDAERFORM

		          Indikator:
		          a = ISSN formal richtig
		          b = ISSN formal falsch
		          c = ISBN formal richtig
		          d = ISBN formal falsch
		          e = ISMN formal richtig
		          f = ISMN formal falsch
		          z = sonstige Standardnummern


		623       IDENTIFIKATIONSNUMMER DES 1. GESAMTTITELS DER
		          SEKUNDAERFORM

		          Indikator:
		          blank = nicht definiert
		          m     = mehrbaendiges begrenztes Werk
		          r     = Schriftenreihe oder anderes fortlaufendes
		                  Sammelwerk


		624       1. GESAMTTITEL DER SEKUNDAERFORM IN ANSETZUNGSFORM

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Verfasserwerk
		          b     = Urheberwerk
		          c     = Sachtitelwerk


		625       BANDANGABE

		          Indikator:
		          blank = nicht definiert


		626       BANDANGABE IN SORTIERFORM

		          Indikator:
		          blank = nicht definiert


		627-632   2. GESAMTTITEL DER SEKUNDAERFORM

		627       2. GESAMTTITEL DER SEKUNDAERFORM IN VORLAGEFORM

		          Indikator:
		          blank = 2. Gesamttitel mit Zaehlung der Stuecktitel
		          a     = 2. Gesamttitel mit Zaehlung der Stuecktitel -
		                  Angabe der Zaehlung erfolgt auf nachfolgender
		                  Hierarchiestufe
		          b     = 2. Gesamttitel ohne Zaehlung der Stuecktitel
		          c     = 2. Gesamttitel mit Zaehlung der Stuecktitel -
		                  ohne maschinelle Generierung eines Stuecktitelnachtrags
		                  (Deskriptivform)


		628       STANDARDNUMMERN DES 2. GESAMTTITELS DER SEKUNDAERFORM
		629       IDENTIFIKATIONSNUMMER DES 2. GESAMTTITELS DER
		          SEKUNDAERFORM
		630       2. GESAMTTITEL DER SEKUNDAERFORM IN ANSETZUNGSFORM
		631       BANDANGABE
		632       BANDANGABE IN SORTIERFORM


		633       ABWEICHENDER TITEL DER SEKUNDAERFORM

		          Indikator:
		          blank = nicht definiert


		634       INTERNATIONALE STANDARDBUCHNUMMER (ISBN) DER
		          SEKUNDAERFORM

		          Indikator:
		          blank = ISBN formal nicht geprueft
		          a     = ISBN formal richtig
		          b     = ISBN formal falsch
		          z     = keine ISBN, aber Preis


		635       INTERNATIONALE STANDARDNUMMER FUER FORTLAUFENDE
		          SAMMELWERKE (ISSN) DER SEKUNDAERFORM

		          Indikator:
		          blank = ISSN formal nicht geprueft
		          a     = ISSN formal richtig
		          b     = ISSN formal falsch
		          z     = keine ISSN, aber Preis


		636       SONSTIGE STANDARDNUMMER DER SEKUNDAERFORM

		          Indikator:
		          blank = nicht definiert


		637       UMFANGSANGABE UND PHYSISCHE BESCHREIBUNG DER
		          SEKUNDAERFORM

		          Indikator:
		          blank = nicht definiert


		638       ANGABE VON BEGLEITMATERIALIEN

		          Indikator:
		          blank = nicht definiert


		640       AUSGABEBEZEICHNUNG DER SEKUNDAERFORM IN VORLAGEFORM

		          Indikator:
		          blank = nicht definiert


		642       VERFILMTE BAENDE

		          Indikator:
		          blank = nicht definiert


		644       SIGNATUR DER SEKUNDAERFORM

		          Indikator:
		          blank = keine Benutzungsbeschraenkung
		          a     = nicht verleihbar


		645       ERGAENZENDE ANGABEN ZUR SEKUNDAERFORM

		          Indikator:
		          blank = nicht definiert


		646       BESITZNACHWEIS FUER DIE VERFILMUNGSVORLAGE

		          Indikator:
		          blank = Name der besitzenden Institution
		          a     = Adresse der besitzenden Institution
		          b     = Signatur der Verfilmungsvorlage


		647       BESITZNACHWEIS FUER DEN SEKUNDAERFORM-MASTER

		          Indikator:
		          blank = Name der besitzenden Institution
		          a     = Adresse der besitzenden Institution
		          b     = Signatur des Sekundaerform-Master




		651-659   SEGMENT AUSGABEVERMERK COMPUTERDATEIEN

		651       FUSSNOTE ZUR COMPUTERDATEI

		          Indikator:
		          blank = nicht definiert

		          Unterfelder:
		          $a    = einleitende Wendung
		          $b    = Angaben zur Computerdatei

		652       SPEZIFISCHE MATERIALBENENNUNG UND DATEITYP

		          Indikator:
		          blank = nicht spezifiziert
		          a     = RAK-NBM
		          b     = ISBD-CF

		          Unterfelder:
		          $a    = Spezifische Materialbenennung
		          $b    = Dateityp


		653       PHYSISCHE BESCHREIBUNG DER COMPUTERDATEI AUF DATENTRAEGER

		          Indikator:
		          blank = nicht definiert

		          Unterfelder:
		          $a    = Anzahl und Materialbenennung physischer Einheiten
		          $b    = Dateiumfang
		          $c    = Sonstige physische und technische Angaben
		          $d    = Physische Groesse des Datentraegers
		          $e    = Begleitmaterial


		654       SYSTEMVORAUSSETZUNGEN FUER DIE COMPUTERDATEI

		          Indikator:
		          blank = nicht definiert

		          Unterfelder:
		          $a   = Prozessor
		          $b   = Hardware-Konfiguration
		          $c   = Software-Anforderungen
		          $d   = Sonstige Anforderungen


		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART
		          FUER EINE COMPUTERDATEI IM FERNZUGRIFF

		          Indikator:

		          Zugriffsmethode:
		          blank = nicht spezifiziert
		          a     = E-Mail
		          b     = FTP (File Transfer)
		          c     = Remote Login (Telnet)
		          d     = Dial-up (konventioneller Telefonanschluss)
		          e     = HTTP
		          h     = In Unterfeld $2 spezifizierte Zugriffsmethode

		          Unterfelder:
		          $a    = Name des Host
		          $b    = IP-Zugriffsnummer
		          $c    = Art der Komprimierung
		          $d    = Zugriffspfad fuer eine Datei
		          $f    = Elektronischer Name der Datei im Verzeichnis des Host
		          $g    = URN (Uniform Resource Name)
		          $h    = Durchfuehrende Stelle einer Anfrage
		          $i    = Anweisung fuer die Ausfuehrung einer Anfrage
		          $j    = Datenuebertragungsrate (Bits pro Sekunde)
		          $k    = Passwort
		          $l    = Logon/Login-Angabe
		          $m    = Kontaktperson
		          $n    = Ort des Host
		          $o    = Betriebssystem des Host
		          $p    = Port
		          $q    = Elektronischer Dateiformattyp
		          $r    = Einstellungen fuer die Dateiuebertragung
		          $s    = Groesse der Datei
		          $t    = Unterstuetzte Terminal-Emulationen
		          $u    = URL (Uniform Resource Locator)
		          $v    = Oeffnungszeiten des Host fuer die gewaehlte Zugangsart
		          $w    = Identifikationsnummer des verknuepften Datensatzes
		          $x    = Interne Bemerkungen
		          $z    = Allgemeine Bemerkungen
		          $2    = Zugriffsmethode
		          $3    = Bezugswerk
		          $A    = Beziehung


		659       ERGAENZENDE BEMERKUNGEN ZUR COMPUTERDATEI

		          Indikator:
		          blank = nicht definiert

		          Unterfelder:
		          $a    = Ergaenzende Bemerkungen
		          $b    = Bestandsschutzmassnahmen



		661-669   SEGMENT AUSGABEVERMERK NACHLAESSE UND AUTOGRAPHEN

		661       ANGABEN ZUM TEXT DER UNTERLAGE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Incipit der Unterlage
		          b     = Einheitsincipit
		          c     = Ausreifung / Entstehungsstufe
		          d     = Literarische Gattung


		662       ANGABEN ZUM AEUSSEREN DER UNTERLAGE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Beschreibstoff
		          b     = Einband
		          c     = Wasserzeichen
		          d     = Erhaltungszustand
		          e     = Restaurierungsmassnahmen


		663       BEZUGSWERKE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Editionshinweise
		          b     = Literaturhinweise
		          c     = Sonstige Bezugswerke


		664       PROVENIENZ

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Herkunft
		          b     = Erwerbung
		          c     = Verlust


		669       REDAKTIONELLE BEMERKUNGEN ZUR UNTERLAGE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Benutzungsbeschraenkung
		          b     = Sperrvermerk



		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		670       SACHTITEL IN ABWEICHENDER ORTHOGRAPHIE

		          Indikator:
		          blank = nicht definiert


		672       AUTORENNAME IN NORMIERTER FORM

		          Indikator:
		          blank = nicht definiert


		673       ORT IN NORMIERTER FORM

		          Indikator:
		          blank = Veranstaltungsort
		          a     = Erscheinungsort
		          b     = Verbreitungsort
		          c     = Hochschulort


		674       VERANSTALTUNGSJAHR / ERSCHEINUNGSJAHR DES ORIGINALS

		          Indikator:
		          blank = Veranstaltungsjahr
		          a     = Erscheinungsjahr des Originals


		675       STICHWOERTER IN ABWEICHENDER ORTHOGRAPHIE

		          Indikator:
		          blank = nicht definiert
		          a     = Sachbegriff
		          b     = geographischer Begriff
		          c     = Personenname
		          d     = Koerperschaftsname




		7--   SEGMENT SACHERSCHLIESSUNG

		700       NOTATION EINES KLASSIFIKATIONSSYSTEMS

		          Indikator:

		          blank = Systematik der katalogisierenden Institution
		          a     = UDC     (Universal Decimal Classification)
		          b     = DDC     (Dewey Decimal Classification)
		          c     = LC      (Library of Congress Classification)
		          d     = DNB     (Systematik der Deutschen Nationalbibliographie)
		          e     = Methode Eppelsheimer
		          g     = Regensburger Verbundklassifikation
		          h     = Gesamthochschulbibliothekssystematik (GHBS)
		          l     = RPB     (Rheinland-Pfaelzische Bibliographie)
		          m     = MSC     (Mathematics Subject Classification)
		          n     = NWBib   (Nordrhein-Westfaelische Bibliographie)
		          o     = ASB     (Allgemeine Systematik für Bibliotheken)
		          p     = SSD     (Systematik der Stadtbibliothek Duisburg)
		          q     = SfB     (Systematik für Bibliotheken)
		          r     = KAB     (Klassifikation für Allgemeinbibliotheken)
		          s     = Systematiken der ekz
		          t     = Systematik der TUB Muenchen
		          u     = DOPAED der UB Erlangen
		          v     = IFZ-Systematik
		          w     = Systematik der Bayerischen Bibliographie
		          z     = ZDB-Systematik


		710       SCHLAGWOERTER UND SCHLAGWORTKETTEN

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = Sachschlagwort
		          b     = geographisch-ethnographisches Schlagwort
		          c     = Personenschlagwort
		          d     = Koerperschaftsschlagwort
		          f     = Formschlagwort
		          z     = Zeitschlagwort


		711       SCHLAGWOERTER UND SCHLAGWORTKETTEN NACH ANDERERN REGELWERKEN

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = Sachschlagwort
		          b     = geographisch-ethnographisches Schlagwort
		          c     = Personenschlagwort
		          d     = Koerperschaftsschlagwort
		          f     = Formschlagwort
		          z     = Zeitschlagwort

		          Unterfelder:
		          $a    = Schlagwort oder Schlagwortkette
		          $v    = Verweisungsformen
		          $x    = Regelwerk
		          $y    = Herkunft


		720       STICHWOERTER

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = Sachbegriff
		          b     = geographischer Begriff
		          c     = Personenname
		          d     = Koerperschaftsname


		730       PRECIS

		          Indikator:
		          blank = nicht definiert


		740       SUBJECT HEADINGS

		          Indikator:
		          p = Personal Name used as Subject
		          c = Corporate Body Name used as Subject
		          f = Family Name used as Subject
		          n = Name and Title used as Subject
		          t = Title used as Subject
		          s = Topical Name used as Subject
		          g = Geographical Name used as Subject
		          u = Uncontrolled Subject


		750       1. INHALTLICHE ZUSAMMENFASSUNG

		          Indikator:
		          blank = nicht aufgegliedert
		          a     = Auszug
		          b     = Zusammenfassung
		          c     = Annotation (Untertitel)
		          d     = Kurzreferat
		          e     = Sammelreferat
		          f     = Rezension
		          g     = Sammelrezension
		          h     = Literaturbericht
		          i     = Autorenkurzreferat
		          j     = Fremdkurzreferat
		          k     = maschinell erstelltes Kurzreferat
		          l     = vorlaeufiges Kurzreferat (ekz-aktuell)
		          m     = Regest


		751       VERFASSER DER 1. INHALTLICHEN ZUSAMMENFASSUNG

		          Indikator:
		          blank = nicht definiert


		752       SPRACHEN DER 1. INHALTLICHEN ZUSAMMENFASSUNG

		          Indikator:
		          blank = nicht definiert


		753       2. INHALTLICHE ZUSAMMENFASSUNG
		754       VERFASSER DER 2. INHALTLICHEN ZUSAMMENFASSUNG
		755       SPRACHEN DER 2. INHALTLICHEN ZUSAMMENFASSUNG


		756       3. INHALTLICHE ZUSAMMENFASSUNG
		757       VERFASSER DER 3. INHALTLICHEN ZUSAMMENFASSUNG
		758       SPRACHEN DER 3. INHALTLICHEN ZUSAMMENFASSUNG




		8--   Segment Nichtstandardmaessige Nebeneintragungen (NE)

		800       PERSON DER 1. NE

		          Indikator:
		          blank = nicht definiert


		801       VERWEISUNGSFORM ZUR PERSON DER 1. NE

		          Indikator:
		          blank = nicht definiert


		802       KOERPERSCHAFT DER 1. NE

		          Indikator:
		          blank = nicht definiert


		803       VERWEISUNGSFORM ZUR KOERPERSCHAFT DER 1. NE

		          Indikator:
		          blank = nicht definiert


		804       EINHEITSSACHTITEL DER 1. NE

		          Indikator:
		          blank = nicht definiert


		805       SACHTITEL DER 1. NE

		          Indikator:
		          a = Ansetzungsform
		          b = Mischform


		806       PERSON DER 2. NE
		807       VERWEISUNGSFORM ZUR PERSON DER 2. NE
		808       KOERPERSCHAFT DER 2. NE
		809       VERWEISUNGSFORM ZUR KOERPERSCHAFT DER 2. NE
		810       EINHEITSSACHTITEL DER 2. NE
		811       SACHTITEL DER 2. NE

		...

		824       PERSON DER 5. NE
		825       VERWEISUNGSFORM ZUR PERSON DER 5. NE
		826       KOERPERSCHAFT DER 5. NE
		827       VERWEISUNGSFORM ZUR KOERPERSCHAFT DER 5. NE
		828       EINHEITSSACHTITEL DER 5. NE
		829       SACHTITEL DER 5. NE




		9--       SEGMENT RSWK-SCHLAGWORTKETTEN

		900       IDENTIFIKATIONSNUMMER DER 1. SCHLAGWORTKETTE

		          Indikator:
		          blank = nicht definiert


		902       KETTENGLIED DER 1. SCHLAGWORTKETTE

		          Indikator:
		          p     = Personenschlagwort
		          g     = geographisch-ethnographisches Schlagwort
		          s     = Sachschlagwort
		          k     = Koerperschaftsschlagwort: Ansetzung unter dem
		                  Individualnamen
		          c     = Koerperschaftsschlagwort: Ansetzung unter dem
		                  Ortssitz
		          z     = Zeitschlagwort
		          f     = Formschlagwort
		          t     = Werktitel als Schlagwort
		          blank = Unterschlagwort einer Ansetzungskette


		903       PERMUTATIONSMUSTER DER 1. SCHLAGWORTKETTE

		          Indikator:
		          blank = nicht definiert


		904       ERLAEUTERUNGEN ZUR 1. SCHLAGWORTKETTE

		          Indikator:
		          blank = nicht definiert
		          a     = Herkunft / Nutzung


		905       IDENTIFIKATIONSNUMMER DER 2. SCHLAGWORTKETTE
		907       KETTENGLIED DER 2. SCHLAGWORTKETTE
		908       PERMUTATIONSMUSTER DER 2. SCHLAGWORTKETTE
		909       ERLAEUTERUNGEN ZUR 2. SCHLAGWORTKETTE

		...

		945       IDENTIFIKATIONSNUMMER DER 10. SCHLAGWORTKETTE
		947       KETTENGLIED DER 10. SCHLAGWORTKETTE
		948       PERMUTATIONSMUSTER DER 10. SCHLAGWORTKETTE
		949       Erlaeuterungen zur 10. Schlagwortkette



		http://www.d-nb.de/standardisierung/txt/erw-mab.txt

		                                  MAB2
		                       Online-Kurzreferenz-Version
		        Uebersicht der Formaterweiterungen und Formataenderungen

		                           Stand: Juni 2006


		                       ============================
		                       Satzkennung und Segmente 0--
		                       ============================


		026       REGIONALE IDENTIFIKATIONSNUMMER

		          Indikator:
		          i     = Oesterreichischer Bibliothekenverbund


		040       NOTATION FUER NORMDATEN

		          Indikator:
		          blank = nicht spezifiziert
		              a = Systematiknummer Der Deutschen Bibliothek
		              b = DDC (Dewey Decimal Classification)

		          Wiederholungsfaktor = 3 / fakultativ / in den Satztypen p, k, s


		041       NOTATIONSSPEZIFISCHE CODIERUNGEN

		          Datenelemente:
		            0   Art der Notation
		                f     = BK      (Basisklassifikation)
		                j     = NDC     (Nippon Decimal Classification)
		                k     = NDLC    (National Diet Library Classification)


		051       VEROEFFENTLICHUNGSSPEZIFISCHE ANGABEN ZU BEGRENZTEN WERKEN

		          Datenelemente:
		            0  Erscheinungsform
		               h = Finite Integrating Resource

		          1-3  Veroeffentlichungsart und Inhalt
		               w = Website
		               y = Dissertation

		            7  Angaben der Freiwilligen Selbstkontrolle der Filmwirtschaft (FSK)
		               0 = Freigegeben ohne Altersbeschraenkung
		               1 = Freigegeben ab 6 Jahren
		               2 = Freigegeben ab 12 Jahren
		               3 = Freigegeben ab 16 Jahren
		               4 = Nicht freigegeben unter 18 Jahren


		052       VEROEFFENTLICHUNGSSPEZIFISCHE ANGABEN ZU FORTLAUFENDEN SAMMELWERKEN

		          Datenelemente:
		            0  Erscheinungsform
		               i = Continuing Integrating Resource

		          1-6  Veroeffentlichungsart und Inhalt
		               ws = Website

		         8-10  Erscheinungsweise
		               k = laufend


		053       NACHLAESSE UND AUTOGRAPHEN

		          Datenelemente:
		            0  Nachlassmaterialien
		               m = Musikquelle

		            1  Authentizitaetsgrad
		               a = Autograph
		               b = Fragliches Autograph
		               c = Teilautograph
		               d = Abschrift
		               e = Druckausgabe (mit handschriftlichen Ergaenzungen und dgl.)


		058       MATERIALSPEZIFISCHE CODES FUER ELEKTRONISCHE RESSOURCEN

		          Datenelemente:
		            0  Materialart
		               c = Elektronische Ressource

		            1  Spezifische Materialbenennung
		               a = Magnetbandkartusche
		               b = Einsteckmodul
		               c = optische Diskette
		               f = Magnetbandkassette
		               h = Magnetbandspule
		               j = Diskette
		               m = magneto-optischer Datentraeger (wiederbeschreibbar)
		               o = optischer Datentraeger (nur lesbar, z. B. CD-ROM, DVD)
		               r = Online-Ressource
		               u = nicht spezifiziert
		               z = andere

		            2  nicht besetzt

		            3  Farbe
		               a = einfarbig
		               b = schwarzweiss
		               c = mehrfarbig
		               g = Graustufen
		               m = gemischt (mehr als eine Farbart)
		               n = nicht anzuwenden
		               u = unbekannt
		               z = andere

		            4  Abmessungen
		               a = 3 1/2 Zoll
		               e = 12 Zoll
		               g = 4 3/4 Zoll oder 12 cm
		               i = 1 1/8 x 2 3/8 Zoll
		               j = 3 7/8 x 2 1/2 Zoll
		               n = nicht anzuwenden
		               o = 5 1/4 Zoll
		               u = unbekannt
		               v = 8 Zoll
		               z = andere

		            5  Ton
		               Blank = ohne Ton
		               a = mit Ton
		               u = unbekannt

		          6-8  Bit-Tiefe der Bilddatei
		               001 - 999 = exakte Bit-Tiefe (z. B. 001)
		               mmm = gemischt (mehr als eine Bilddatei)
		               nnn = nicht anzuwenden
		               --- = unbekannt

		            9  Anzahl der Dateiformate
		               a = ein Dateiformat
		               m = mehrere Dateiformate
		               u = unbekannt

		           10  Messskalen zur Qualitaetssicherung
		               a = nicht vorhanden
		               n = nicht anzuwenden
		               p = vorhanden
		               u = unbekannt

		           11  Vorgaengermedium/Quelle
		               a = Datei wurde vom Original aufgenommen
		               b = Datei wurde von einer Mikroform aufgenommen
		               c = Datei wurde von einer Computer-Datei aufgenommen
		               d = Datei wurde von einer anderen Vorlage als Mikroform aufgenommen
		               m = gemischte Vorlagen
		               n = nicht anzuwenden
		               u = unbekannt

		           12  Grad der Komprimierung
		               a = nicht komprimiert
		               b = verlustfrei komprimiert
		               d = mit Verlust komprimiert
		               m = gemischt (mehr als eine Komprimierungsart)
		               u = unbekannt

		           13  Qualitaet der Konversion
		               a = Zugang fuer Nutzer
		               n = nicht anzuwenden
		               p = Bestandserhaltung
		               r = Ersatz des Originals
		               u = unbekannt


		071       IDENTIFIZIERUNGSMERKMALE DER BESITZENDEN INSTITUTION

		          Indikator:
		              d = Internationales Bibliothekssigel (ISIL)


		072       CODIERTE ANGABEN ZUR BESITZENDEN INSTITUTION

		          Datenelemente:
		            6  Fernleihindikator
		               a = Fernleihe (Nur Ausleihe)
		               k = Fernleihe (Nur Kopie)
		               l = Fernleihe (Kopie und Ausleihe)
		               n = Keine Fernleihe


		073       SONDERSAMMELGEBIETSNUMMER

		          Indikator:
		          blank = nicht definiert

		          Wiederholungsfaktor = 5 / fakultativ / in den Satztypen h, y, u, l, e



		                               =========
		                               MAB-TITEL
		                               =========


		304       EINHEITSSACHTITEL

		          nicht wiederholbar / fakultativ / in den Satztypen h, u


		305       IDENTIFIKATIONSNUMMER DES EINHEITSSACHTITELSATZES

		          Indikator:
		          blank = nicht spezifiziert
		              a = Ueberregionale Identifikationsnummer
		              b = Regionale Identifikationsnummer
		              c = Lokale Identifikationsnummer

		          nicht wiederholbar / fakultativ / in den Satztypen h, u


		370       WEITERE SACHTITEL

		          Wiederholungsfaktor = 50 / fakultativ / in den Satztypen h, u


		542       INTERNATIONALE STANDARDNUMMER FUER FORTLAUFENDE
		          SAMMELWERKE (ISSN)

		          Wiederholungsfaktor = 50 / fakultativ / in den Satztypen h, u


		544       LOKALE SIGNATUR

		          Indikator:
		          b     = Fruehere Signatur


		545       WEITERE INTERNATIONALE STANDARDNUMMER FUER FORTLAUFENDE
		          SAMMELWERKE (ISSN)

		          wiederholbar / fakultativ / in den Satztypen h, u

		          Indikator:
		          blank = ISSN formal nicht geprueft
		          a     = ISSN formal richtig
		          b     = ISSN formal falsch

		          Unterfelder:
		          $a    = Autorisierte ISSN                    (NW)
		          $b    = ISSN der Ausgabe auf Datentraeger    (NW)
		          $c    = ISSN der Internetausgabe             (NW)
		          $d    = ISSN der Druckausgabe                (NW)


		550       AMTLICHE DRUCKSCHRIFTENNUMMER

		          Indikator:
		          blank = nicht definiert
		          Wiederholungsfaktor = 50


		551       VERLAGS-, PRODUKTIONS- UND BESTELLNUMMER

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Verlags- und Firmenbestellnummer
		          b     = Druckplattennummer bei Musikalien
		          c     = Plattennummer
		          d     = Setnummer
		          e     = Produktionsnummer
		          f     = Kompaktkassettennummer


		552       PERSISTENT IDENTIFIERS (PI)

		          Indikator:
		          blank = nicht spezifiziert
		              a = Digital Object Identifier (DOI)
		              b = Uniform Resource Name (URN)
		              c = Handle


		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART FUER EINE COMPUTERDATEI IM
		          FERNZUGRIFF

		          Unterfelder:
		          $g    = URN (Uniform Resource Name)
		                  GESTRICHEN; SIEHE FELD 552, INDIKATOR b
		          $y    = Link-Text


		671       ANDERSSCHRIFTLICHE DARSTELLUNG

		          Indikator:
		          blank = nicht definiert

		          wiederholbar / fakultativ / in allen Satztypen

		          Unterfelder:
		          siehe die Bestimmungen fuer die Unterfelder des verknuepften Feldes,
		          falls vorhanden

		          Fester Vorspann:
		          0-2  Feldnummer des verknuepften Feldes
		            3  Indikator des verknuepften Feldes
		          4-5  Nummer des Feldes bei Feldwiederholung (Occurence)
		          6-9  Code fuer die verwendete Schrift, nach ISO 15924
		           10  Code fuer die Orientierung des Feldes ("l" oder "r")
		        11-14  Code fuer die Schrift des verknuepften Feldes, nach ISO 15924
		           15  Code fuer die Orientierung des verknuepften Feldes ("l" oder "r")

		          Inhalt des verknuepften Feldes in einer anderen Schrift


		680-689   SEGMENT ANGABEN ZU MUSIKMATERIALIEN

		680       WERKVERZEICHNIS

		          Indikator:
		          blank = nicht definiert

		          wiederholbar / fakultativ / in den Satztypen h, u

		          Unterfelder:
		          $a    = Angabe des Werkverzeichnisses
		          $b    = Fundstelle (Werkverzeichnis-Nummer)


		681       ANGABEN ZUM MUSIKWERK

		          Indikator:
		          blank = nicht definiert

		          wiederholbar / fakultativ / in den Satztypen h, u

		          Unterfelder:
		          $a    = Opus-Zahl
		          $b    = Tonart des Werkes
		          $c    = Pauschale Besetzungsangabe


		682       ANGABEN ZUM MUSIKINCIPIT

		          Indikator:
		          blank = nicht definiert

		          wiederholbar / fakultativ / in den Satztypen h, u

		          Unterfelder:
		          $a    = Incipitnummer
		          $b    = Besetzung des Incipit
		          $c    = Rollenangabe zum Incipit
		          $d    = Satztitel und Tempoangabe im Incipit
		          $e    = Besetzung Satz
		          $f    = Text im Incipit
		          $g    = Lateinische Texte
		          $h    = Taktangabe
		          $i    = Zaehltakt
		          $j    = Schluessel
		          $k    = Tonart des Incipits
		          $l    = Angabe des Musikincipits in codierter Form
		                 (Plaine and Easy-Code)
		          $m    = Kommentar zum Incipit


		683       ANGABEN ZUR BESETZUNG

		          Indikator:
		          blank = nicht definiert

		          nicht wiederholbar / fakultativ / in den Satztypen h, u

		          Unterfelder:
		          $a    = Solostimmen
		          $b    = Chorstimmen
		          $c    = Soloinstrumente
		          $d    = Orchesterinstrumente
		          $e    = Kommentar zur Besetzung
		          $f    = Gesamtanzahl Besetzung


		700       NOTATION EINES KLASSIFIKATIONSSYSTEMS

		          Indikator:
		          f     = BK      (Basisklassifikation)
		          j     = NDC     (Nippon Decimal Classification)
		          k     = NDLC    (National Diet Library Classification)


		705       DDC (DEWEY DECIMAL CLASSIFICATION) ANALYTISCH

		          Indikator:
		          blank = nicht spezifiziert
		              a = Standardausgabe
		              b = Kurzausgabe

		          wiederholbar / fakultativ / in den Satztypen h, y, u, l, e

		          Unterfelder:
		          $a    = Vollstaendige Notation
		          $b    = Exemplarnummer (Item number)
		          $c    = Grundnotation
		          $d    = Notationen anderer Haupttafeln
		          $e    = Angabe der zugrunde liegenden DDC-Ausgabe
		          $f    = Notation aus Hilfstafel 1
		          $g    = Notation aus Hilfstafel 2
		          $h    = Notation aus Hilfstafel 3A
		          $i    = Notation aus Hilfstafel 3B
		          $j    = Notation aus Hilfstafel 3C
		          $k    = Notation aus Hilfstafel 4
		          $l    = Notation aus Hilfstafel 5
		          $m    = Notation aus Hilfstafel 6
		          $t    = Notation aus einer Anhaengetafel
		          $A    = Quelle der vergebenen Notation
		                  0 = Notation von der Library of Congress vergeben
		                  1 = Notation von Der Deutschen Bibliothek vergeben
		                  4 = Notation von anderer Institution vergeben



		                               =======
		                               MAB-PND
		                               =======


		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART FUER EINE COMPUTERDATEI IM
		          FERNZUGRIFF
		          siehe MAB-TITEL


		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		671       ANDERSSCHRIFTLICHE DARSTELLUNG
		          siehe MAB-TITEL


		814       DATEN ZUR PERSON

		          Wiederholungsfaktor = 50 / fakultativ

		          Indikator:
		          n     = Ungefaehre Zeitangaben


		815       DATEN ZUR PERSON IN NORMIERTER FORM

		          Wiederholungsfaktor = 50 / fakultativ

		          Indikator:
		          i     = Beruf und/oder Funktion
		                  UMBENANNT IN
		          i     = Weite, individualisierende Berufsbezeichnung
		          o     = Spezifische Berufs- bzw. Funktionsbezeichnung aus der SWD

		          Feldstruktur:

		          Das Feld wird von einer 20 Zeichen umfassenden festen Zeichenfolge
		          eingeleitet, wenn die Identifikationsnummer eines verknuepften
		          Normdatensatzes angegeben wird. Das Feld wird vom Fuellzeichen "|"
		          eingeleitet, wenn keine Verknuepfungsnummer uebermittelt wird. Bei
		          Verwendung des Indikators "v" fuer "Bemerkungen" beginnt das Feld
		          immer mit einem Fuellzeichen "|".

		          Die Verknuepfung bei Verwendung der Indikatoren "c", "d", "e", "i"
		          und "o" erfolgt immer hin zur SWD (also mittels einer SWD-Nummer);
		          bei Verwendung des Indikators "m" erfolgt die Verknuepfung immer
		          hin zur PND (also mittels einer PND-Nummer).


		820       ANSETZUNGSFORM NACH EINEM WEITEREN REGELWERK

		          Indikator:
		          n = Ansetzungsform nach der Polnischen Nationalbibliographie



		                               =======
		                               MAB-GKD
		                               =======


		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART FUER EINE COMPUTERDATEI IM
		          FERNZUGRIFF
		          siehe MAB-TITEL


		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		671       ANDERSSCHRIFTLICHE DARSTELLUNG
		          siehe MAB-TITEL


		802       OFFIZIELLER NAME DER KOERPERSCHAFT

		          Indikator:
		          blank = Offizieller Name der Koerperschaft
		              a = Bemerkungen zum offiziellen Namen der Koerperschaft

		          wiederholbar / fakultativ


		897       WEITERE VERWEISUNGSFORMEN

		          Indikator:
		          blank = nicht definiert

		          wiederholbar / fakultativ



		                               =======
		                               MAB-SWD
		                               =======


		605       NICHT-DESKRIPTOR

		          Indikator:
		          p     = Personenschlagwort bzw. Schlagwortansetzung mit
		                  Personenschlagwort als Hauptschlagwort
		          k     = Koerperschaftsschlagwort bzw. Schlagwortansetzung mit
		                  Koerperschaftsschlagwort als Hauptschlagwort
		                  (fuer Koerperschaften, die unter ihrem Individualnamen
		                  angesetzt werden)
		          c     = Koerperschaftsschlagwort bzw. Schlagwortansetzung mit
		                  Koerperschaftsschlagwort als Hauptschlagwort
		                 (fuer Koerperschaften, die unter einem Geographikum angesetzt
		                  werden)
		          g     = geographisches/ethnographisches Schlagwort, Schlagwort fuer
		                  Sprachbezeichnungen bzw. Schlagwortansetzung mit Geographikum
		                  als Hauptschlagwort
		          t     = Sachtitel eines Werkes
		          s     = Sachschlagwort
		          f     = Formschlagwort
		          z     = Zeitschlagwort
		          blank = nicht spezifiziert

		          nicht wiederholbar / fakultativ


		606       ZU VERKNUEPFENDE DESKRIPTOREN

		          Indikator:
		          p     = Personenschlagwort bzw. Schlagwortansetzung mit
		                  Personenschlagwort als Hauptschlagwort
		          k     = Koerperschaftsschlagwort bzw. Schlagwortansetzung mit
		                  Koerperschaftsschlagwort als Hauptschlagwort
		                  (fuer Koerperschaften, die unter ihrem Individualnamen
		                  angesetzt werden)
		          c     = Koerperschaftsschlagwort bzw. Schlagwortansetzung mit
		                  Koerperschaftsschlagwort als Hauptschlagwort
		                 (fuer Koerperschaften, die unter einem Geographikum angesetzt
		                  werden)
		          g     = geographisches/ethnographisches Schlagwort, Schlagwort fuer
		                  Sprachbezeichnungen bzw. Schlagwortansetzung mit Geographikum
		                  als Hauptschlagwort
		          t     = Sachtitel eines Werkes
		          s     = Sachschlagwort
		          f     = Formschlagwort
		          z     = Zeitschlagwort
		          blank = nicht spezifiziert

		          wiederholbar / fakultativ


		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART FUER EINE COMPUTERDATEI IM
		          FERNZUGRIFF
		          siehe MAB-TITEL


		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		671       ANDERSSCHRIFTLICHE DARSTELLUNG
		          siehe MAB-TITEL



		                               =========
		                               MAB-NOTAT
		                               =========


		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		671       ANDERSSCHRIFTLICHE DARSTELLUNG
		          siehe MAB-TITEL



		                               =========
		                               MAB-LOKAL
		                               =========


		130       BIBLIOGRAPHISCHE ANGABEN

		          Wiederholungsfaktor = 50 / fakultativ / in den Satztypen l, e


		220       1. SIGNATUR

		          Indikator:
		          blank = nicht definiert

		          nicht wiederholbar / fakultativ / im Satztyp l

		          Unterfelder:
		          $a    = Magazin-/Grundsignatur
		          $c    = Kommentar, insbesondere zu dem Aufbewahrungs-/
		                  Verfuegbarkeitszeitraum
		          $d    = Ausleihindikator
		          $f    = aktueller Standort
		          $g    = Signatur am aktuellen Standort
		          $l    = Fernleihindikator

		221       2. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		222       3. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		223       4. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		224       5. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		225       6. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		226       7. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		227       8. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		228       9. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220

		229       10. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 220


		230       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 1. SIGNATUR

		          Indikator:
		          blank = nicht spezifiziert
		              a = abgeschlossener Bestand
		              b = laufender Bestand
		              c = abgeschlossener Bestand mit Aufbewahrungs-/
		                  Verfuegbarkeitszeitraum, Moving wall
		              d = laufender Bestand mit Moving wall

		          nicht wiederholbar / fakultativ / im Satztyp l

		          Unterfelder:

		          Beginngruppe
		          $d    = Bandzaehlung
		          $e    = Heft
		          $b    = Tag
		          $c    = Monat
		          $j    = Berichtsjahr bzw. Erscheinungsjahr
		          $h    = Abweichendes Erscheinungsjahr

		          Endegruppe
		          $n    = Bandzaehlung
		          $o    = Heft
		          $l    = Tag
		          $m    = Monat
		          $k    = Berichtsjahr bzw. Erscheinungsjahr
		          $i    = Abweichendes Erscheinungsjahr

		          Aufbewahrungs-/ Verfuegbarkeitszeitraum, Moving wall
		          $7    = Aufbewahrungs-/ Verfuegbarkeitszeitraum,
		                  Moving wall

		          Inhalt von $7:
		          +nY   = nur die n neuesten Jahrgaenge sind zugaenglich
		          -nY   = die n neuesten Jahrgaenge sind nicht zugaenglich
		          +nV   = nur die n neuesten Baende sind zugaenglich
		          -nV   = die n neuesten Baende sind nicht zugaenglich
		          +nM   = nur die Ausgaben der letzten n Monate sind zugaenglich
		          -nM   = die Ausgaben der letzten n Monate sind nicht zugaenglich
		          +nD   = nur die n neuesten Tage sind zugaenglich
		          -nD   = die n neuesten Tage sind nicht zugaenglich
		          +nI   = nur die Ausgaben der letzten n Hefte sind zugaenglich
		          -nI   = die Ausgaben der letzten n Hefte sind nicht zugaenglich

		231       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 2. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		232       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 3. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		233       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 4. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		234       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 5. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		235       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 6. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		236       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 7. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		237       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 8. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		238       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 9. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230

		239       AUFBEWAHRUNGS-/ VERFUEGBARKEITSZEITRAUM, MOVING WALL
		          ZUR 10. SIGNATUR
		          Indikator, Vorkommen und Unterfelder wie Feld 230


		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART FUER EINE COMPUTERDATEI IM
		          FERNZUGRIFF
		          siehe MAB-TITEL


		671       ANDERSSCHRIFTLICHE DARSTELLUNG
		          siehe MAB-TITEL


		705       DDC (Dewey Decimal Classification) analytisch
		          siehe MAB-TITEL



		                               ==========
		                               MAB-ADRESS
		                               ==========


		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		671       ANDERSSCHRIFTLICHE DARSTELLUNG
		          siehe MAB-TITEL


		805       LIEFER- UND MELDERKENNUNGEN DER ZDB

		          Unterfelder:
		          $b    = Lieferkategorie Verbundsysteme (Replikationskennung)
		                  KEINE FESTLEGUNGEN MEHR ZUM INHALT DES UNTERFELDES
		          $c    = Liefersystem
		                  KEINE FESTLEGUNGEN MEHR ZUM INHALT DES UNTERFELDES


		806       ZUSAETZLICHE SIGELANGABEN

		          Unterfelder:
		          $f    = Zustaendiges Verbundsystem




		http://www.d-nb.de/standardisierung/txt/lokalmab.txt

		                               MAB2-LOKAL
		                       Online-Kurzreferenz-Version
		                          Stand: November 2001



		          SATZKENNUNG


		001-088   SEGMENT 0--

		001-029   IDENTIFIKATIONSNUMMERN, DATUMS- UND VERSIONSANGABEN
		030-035   ALLGEMEINE VERARBEITUNGSTECHNISCHE ANGABEN
		036-049   ALLGEMEINE CODIERTE ANGABEN
		050-064   VEROEFFENTLICHUNGS- UND MATERIALSPEZIFISCHE ANGABEN
		065-069   NORMDATENSPEZIFISCHE ANGABEN
		070-075   ANWENDERSPEZIFISCHE DATEN UND CODES
		076-088   ANWENDERSPEZIFISCHE ANGABEN



		090-091   SEGMENT ANGABEN ZUR BANDAUFFUEHRUNG

		090       Sortierhilfe

		          Indikator:
		          blank = nicht definiert


		091       Einzelbandauffuehrung

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Heftangabe
		          b     = Bandangabe
		          c     = Lueckenangabe
		          d     = Beilagen
		          e     = Register



		1--       SEGMENT TITEL- UND EXEMPLARBEZOGENE LOKALDATEN

		100       Signatur

		          Indikator:
		          blank = nicht spezifiziert
		          s     = Sortierform


		105       STANDORT

		          Indikator:
		          blank = nicht definiert


		107       ZUSAETZLICHE SIGNATUR

		          Indikator:
		          blank = nicht spezifiziert
		          s     = Sortierform


		110       ANZAHL DER EXEMPLARE

		          Indikator:
		          blank = nicht definiert


		115       AKZESSIONSNUMMER

		          Indikator:
		          blank = nicht definiert


		120       BUCHUNGSNUMMER

		          Indikator:
		          blank = nicht definiert


		123       EIGENTUEMER

		          Indikator:
		          blank = nicht definiert


		125       BEMERKUNGEN ZU DEN TITEL- UND EXEMPLARBEZOGENEN
		          LOKALDATEN

		          Indikator:
		          blank = nicht spezifiziert
		          a     = benutzerrelevante Bemerkungen
		          b     = verwaltungsinterne Bemerkungen


		130       BIBLIOGRAPHISCHE ANGABEN ZUM VORLIEGENDEN EXEMPLAR

		          Indikator:
		          blank = nicht definiert


		132       ALTE DRUCKE

		          Indikator:
		          blank = nicht spezifiziert
		          a     = Alte Signaturen
		          b     = Exemplarspezifische bibliographische Zitate
		          e     = Exemplarhinweise
		          k     = Kaufvermerke
		          m     = Marginalien
		          p     = Provenienz

		135       BUCHBINDERISCHE ZAEHLUNG ZUR BIBLIOGRAPHISCHEN
		          BANDZAEHLUNG

		          Indikator:
		          blank = nicht definiert



		2--       SEGMENT ZUSAMMENFASSENDE BESTANDSANGABEN

		200       ZUSAMMENFASSENDE BESTANDSANGABEN

		          Indikator:
		          blank = nicht definiert

		          Unterfelder:
		          $a = Einleitender Text
		          $b = Zusammenfassende Bestandsangabe
		          $c = Lueckenangabe (allgemein)
		          $d = Lueckenangabe (Desideratenverzeichnisse)
		          $e = Kommentar
		          $f = Magazin- / Grundsignatur
		          $g = (Sonder-) Standort
		          $h = (Sonder-) Standortsignatur
		          $k = Kommentar zur Grundsignatur
		          $n = SUBITO-Lieferbedingungen

		          $0 = Sortierhilfe


		210       NORMIERTE BESTANDSANGABEN

		          Indikator:
		          blank = nicht spezifiziert
		          a     = abgeschlossener Bestand
		          b     = laufender Bestand

		          Unterfelder:
		          Beginngruppe:
		          $5    = Parallele Zaehlung
		          $f    = Sachliche Benennung
		          $d    = Bandzaehlung
		          $e    = Heft
		          $b    = Tag
		          $c    = Monat
		          $j    = Berichtszeit bzw. Erscheinungszeit
		          $h    = Abweichende Erscheinungszeit
		          $g    = Kommentar zur Beginngruppe

		          Endegruppe:
		          $n    = Bandzaehlung
		          $o    = Heft
		          $l    = Tag
		          $m    = Monat
		          $k    = Berichtszeit bzw. Erscheinungszeit
		          $i    = Abweichende Erscheinungszeit
		          $q    = Kommentar zur Endegruppe

		          Schlussgruppe
		          $1    = Aufbewahrungszeitraum
		          $4    = Kommentar zu den Bestandsangaben


		Alle nachfolgenden Felder entsprechen MAB-TITEL:


		610-650   SEGMENT AUSGABEVERMERK SEKUNDAERFORMEN

		610       FUSSNOTE ZUR SEKUNDAERAUSGABE

		611       ORT(E) DES 1. VERLEGERS, HERSTELLERS USW.
		612       ADRESSE DES 1. VERLEGERS, HERSTELLERS USW.
		613       NAME DES 1. VERLEGERS, HERSTELLERS USW.

		614       ORT(E) DES 2. VERLEGERS, HERSTELLERS USW.
		615       ADRESSE DES 2. VERLEGERS, HERSTELLERS USW.
		616       NAME DES 2. VERLEGERS, HERSTELLERS USW.

		617       1. URHEBER DER VERFILMUNG
		618       2. URHEBER DER VERFILMUNG

		619       ERSCHEINUNGSJAHR(E) DER SEKUNDAERFORM
		620       HINWEISE ZUR VERFILMUNG


		621-626   1. GESAMTTITEL DER SEKUNDAERFORM

		621       1. GESAMTTITEL DER SEKUNDAERFORM IN VORLAGEFORM
		622       STANDARDNUMMERN DES 1. GESAMTTITELS DER SEKUNDAERFORM
		623       IDENTIFIKATIONSNUMMER DES 1. GESAMTTITELS
		          DER SEKUNDAERFORM
		624       1. GESAMTTITEL DER SEKUNDAERFORM IN ANSETZUNGSFORM
		625       BANDANGABE
		626       BANDANGABE IN SORTIERFORM


		627-632   2. GESAMTTITEL DER SEKUNDAERFORM

		627       2. GESAMTTITEL DER SEKUNDAERFORM IN VORLAGEFORM
		628       STANDARDNUMMERN DES 2. GESAMTTITELS DER SEKUNDAERFORM
		629       IDENTIFIKATIONSNUMMER DES 2. GESAMTTITELS
		          DER SEKUNDAERFORM
		630       2. GESAMTTITEL DER SEKUNDAERFORM IN ANSETZUNGSFORM
		631       BANDANGABE
		632       BANDANGABE IN SORTIERFORM

		633       ABWEICHENDER TITEL DER SEKUNDAERFORM
		634       INTERNATIONALE STANDARDBUCHNUMMER (ISBN) DER
		          SEKUNDAERFORM
		635       INTERNATIONALE STANDARDNUMMER FUER FORTLAUFENDE
		          SAMMELWERKE (ISSN) DER SEKUNDAERFORM
		636       SONSTIGE STANDARDNUMMER DER SEKUNDAERFORM
		637       UMFANGSANGABE UND PHYSISCHE BESCHREIBUNG DER
		          SEKUNDAERFORM
		638       ANGABE VON BEGLEITMATERIALIEN
		640       AUSGABEBEZEICHNUNG DER SEKUNDAERFORM IN VORLAGEFORM
		644       SIGNATUR DER SEKUNDAERFORM
		645       ERGAENZENDE ANGABEN ZUR SEKUNDAERFORM

		646       BESITZNACHWEIS FUER DIE VERFILMUNGSVORLAGE
		647       BESITZNACHWEIS FUER DEN SEKUNDAERFORM-MASTER



		651-659   SEGMENT AUSGABEVERMERK COMPUTERDATEIEN

		651       FUSSNOTE ZUR COMPUTERDATEI
		652       SPEZIFISCHE MATERIALBENENNUNG UND DATEITYP
		653       PHYSISCHE BESCHREIBUNG DER COMPUTERDATEI AUF DATENTRAEGER
		654       SYSTEMVORAUSSETZUNGEN FUER DIE COMPUTERDATEI
		655       ELEKTRONISCHE ADRESSE UND ZUGRIFFSART
		          FUER EINE COMPUTERDATEI IM FERNZUGRIFF
		659       ERGAENZENDE BEMERKUNGEN ZUR COMPUTERDATEI



		670-675   SEGMENT ZUSAETZLICHE SUCHKRITERIEN

		670       SACHTITEL IN ABWEICHENDER ORTHOGRAPHIE
		672       AUTORENNAME IN NORMIERTER FORM
		673       ORT IN NORMIERTER FORM
		674       VERANSTALTUNGSJAHR / ERSCHEINUNGSJAHR DES ORIGINALS
		675       STICHWOERTER IN ABWEICHENDER ORTHOGRAPHIE



		7--       SEGMENT SACHERSCHLIESSUNG

		700       NOTATION EINES KLASSIFIKATIONSSYSTEMS
		710       SCHLAGWOERTER UND SCHLAGWORTKETTEN
		720       STICHWOERTER
		730       PRECIS
		740       SUBJECT HEADINGS

		750       1. INHALTLICHE ZUSAMMENFASSUNG
		751       VERFASSER DER 1. INHALTLICHEN ZUSAMMENFASSUNG
		752       SPRACHEN DER 1. INHALTLICHEN ZUSAMMENFASSUNG

		753       2. INHALTLICHE ZUSAMMENFASSUNG
		754       VERFASSER DER 2. INHALTLICHEN ZUSAMMENFASSUNG
		755       SPRACHEN DER 2. INHALTLICHEN ZUSAMMENFASSUNG

		756       3. INHALTLICHE ZUSAMMENFASSUNG
		757       VERFASSER DER 3. INHALTLICHEN ZUSAMMENFASSUNG
		758       SPRACHEN DER 3. INHALTLICHEN ZUSAMMENFASSUNG



		9--       SEGMENT RSWK-SCHLAGWORTKETTEN

		900       IDENTIFIKATIONSNUMMER DER 1. SCHLAGWORTKETTE
		902       KETTENGLIED DER 1. SCHLAGWORTKETTE
		903       PERMUTATIONSMUSTER DER 1. SCHLAGWORTKETTE
		904       ERLAEUTERUNGEN ZUR 1. SCHLAGWORTKETTE

		905       IDENTIFIKATIONSNUMMER DER 2. SCHLAGWORTKETTE
		907       KETTENGLIED DER 2. SCHLAGWORTKETTE
		908       PERMUTATIONSMUSTER DER 2. SCHLAGWORTKETTE
		909       ERLAEUTERUNGEN ZUR 2. SCHLAGWORTKETTE

		...

		945       IDENTIFIKATIONSNUMMER DER 10. SCHLAGWORTKETTE
		947       KETTENGLIED DER 10. SCHLAGWORTKETTE
		948       PERMUTATIONSMUSTER DER 10. SCHLAGWORTKETTE
		949       ERLAEUTERUNGEN ZUR 10. SCHLAGWORTKETTE

	 **/
}
