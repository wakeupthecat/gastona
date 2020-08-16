/*
package de.elxala
(c) Copyright 2020 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package de.elxala.parse.csv;

import java.io.*;
import java.util.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.utilEscapeStr;

public class CsvParserV70
{
   public char VAR_SEPA_CH = ',';
   public char VAR_QUOTE = '"';
   public boolean autodetectSeparator = true;
   public List headColNames = null;

   public CsvParserV70 ()
   {
   }

   public CsvParserV70 (boolean detectSeparator, char separator, char quote)
   {
      configure (detectSeparator, separator, quote);
   }

   public void configure (boolean detectSeparator, char separator, char quote)
   {
      VAR_SEPA_CH = separator;
      VAR_QUOTE = quote;
      autodetectSeparator = detectSeparator;
   }

   public static void main(String[] aa) throws Exception
   {
      if (aa.length < 1)
      {
         System.out.println("use : CsvParserV70 csvFile [sqlOutputFile]");
         return ;
      }

      long start = System.currentTimeMillis ();

      String recTabName = "csv2Table";
      String incTabName = "csv2TableIncidences";

      CsvParserV70 coso = new CsvParserV70 ();
      int lineCnt = 0;
      int incidCnt = 0;
      String csvFile = aa[0];

      String line;

      PrintStream out = aa.length >= 2 ? new PrintStream(aa[1]): System.out;

      TextFile fitx = new TextFile ();

      if (fitx.fopen (csvFile, "r"))
      {
         out.print ("BEGIN;");
         if (fitx.readLine ())
         {
            coso.processHeader (fitx.TheLine ());
            out.print ("CREATE TABLE IF NOT EXISTS " + recTabName + " (");
            for (int ii = 0; ii < coso.headColNames.size (); ii ++)
               out.print ((ii != 0 ? ", ": "") + coso.headColNames.get (ii));
            out.println (");");
            out.println ("CREATE TABLE IF NOT EXISTS " + incTabName + " (incidenceNr, lineNr, desc, text);");
         }

         while (fitx.readLine ())
         {
            lineCnt ++;
            List columns = coso.parseCsvLine(fitx.TheLine ());

            if (columns.size () != coso.headColNames.size ())
            {
               incidCnt ++;
               out.println ("INSERT INTO " + incTabName + " VALUES (" + (incidCnt++) + ", " + lineCnt + ", '" +
                                   columns.size () + " columns found but " +
                                   coso.headColNames.size () + " are required', '" + utilEscapeStr.escapeStr (fitx.TheLine ()) + "');");
               if (incidCnt >= 100)
               {
                  out.println ("INSERT INTO " + incTabName + " VALUES (" + (incidCnt++) + ", " + lineCnt + ", " +
                                     "'too many incidences parse aborted', '');");
                  break;
               }
            }
            else
            {
               out.print ("INSERT INTO " + recTabName + " VALUES (");
               for (int ii = 0; ii < columns.size (); ii ++)
                  out.print((ii == 0 ? "": ", ") + "'" + utilEscapeStr.escapeStr ((String) columns.get(ii)) + "'");
               out.println (");");
            }

         }
         out.print ("COMMIT;");
         fitx.fclose ();
         out.close();
      }

      long took = System.currentTimeMillis () - start;

      System.out.println ("It took " + took/1000. + " seconds for " + lineCnt + " records ");
      System.out.println ((0.+took)/lineCnt + " ms per record");
   }

   public List parseCsvLine (String str)
   {
      List eline = new Vector ();
      int pi = 0;
      // char[] str = cvsLine.toCharArray();
      int FI = str.length ();

      do
      {
         String cell = "";

         // trim
         while (pi < FI && (str.charAt (pi) == ' ' || str.charAt (pi) == '\t')) pi++;
         if (pi >= FI) break;

         boolean envolta = str.charAt (pi) == VAR_QUOTE;
         int ini = envolta ? ++pi: pi;
         do
         {
             if (envolta)
             {
                if (str.charAt (pi) != VAR_QUOTE) pi ++;
                else
                    if (pi+1 < FI && str.charAt (pi+1) == VAR_QUOTE)
                    {
                      // double ""
                      // add a part including one " and continue
                      cell += (pi+1 > ini ? str.substring (ini, pi+1): "");
                      pi += 2;
                      ini = pi;
                    }
                    else break; // close "
             }
             else
             {
                 if (str.charAt (pi) == VAR_SEPA_CH) break;
                 pi ++;
             }
         } while (pi < FI);

         int fi2 = pi;

         // right trim if not quoted
         //
         if (! envolta)
           while (fi2 > ini && (str.charAt (fi2-1) == ' ' || str.charAt (fi2-1) == '\t')) fi2 --;

         if (fi2 > ini)
           cell += str.substring (ini, fi2);

         pi ++;

         if (envolta)
         {
            // find the next comma if any
            while (pi < FI && str.charAt (pi) != VAR_SEPA_CH) pi++;
            pi ++;
         }

         // add one cell
         //
         eline.add (cell);
      } while (pi < FI);

      // allow finishing with empty string. Example:
      //    name, tel
      //    Justine, 991
      //    Lalia,
      //
      if (headColNames != null && eline.size () + 1 == headColNames.size () && str.charAt (FI-1) == VAR_SEPA_CH)
         eline.add ("");

      return eline;
   }

   public int countCh (String str, char ch)
   {
      int cnt = 0;
      for(int ii = 0; ii < str.length (); ii ++)
         if (str.charAt (ii) == ch) cnt ++;
      return cnt;
   }

   public void processHeader (String heastr)
   {
      // choose separator , ; or tab
      //

      if (autodetectSeparator)
      {
         // a column name in a header should not contain ,;tab or similar but actually it happens in some csv!
         //
         int c1 = countCh (heastr, ',');
         int c2 = countCh (heastr, ';');
         int c3  = countCh (heastr, '\t');
         VAR_SEPA_CH = '\t';

         if (c1 >= c2 && c1 >= c3)
               VAR_SEPA_CH = ',';
         else if (c2 >= c1 && c2 >= c3)
               VAR_SEPA_CH = ';';
      }

      List brutos = parseCsvLine (heastr);
      int heacnt = 1;

      // force all unique column names
      //
      headColNames = new Vector ();
      for (int ii = 0; ii < brutos.size (); ii ++)
      {
         String nam = (String) brutos.get (ii);
         nam = nam.trim ();

         // it cannot be accepted an empty column name
         // also in order to filter last false header if line ends with separator
         if (nam.length () == 0) break;

         nam = "c" + naming.toVariableName (nam).replaceAll ("_", "");

         // some tricky colum name could be repeated if not using while loop
         while (headColNames.indexOf (nam) != -1)
            nam = nam + ("cc" + (heacnt++));

         headColNames.add (nam);
      }
   }
}
