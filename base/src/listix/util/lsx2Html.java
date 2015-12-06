/*
library listix (www.listix.org)
Copyright (C) 2005 Alejandro Xalabarder Aulet

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


package listix.util;

import java.util.*;
import java.io.File;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;

public class lsx2Html
{
   public static void main (String [] aa)
   {
      if (aa.length == 0)
      {
         System.out.println ("Syntax:   lsxFileName [dataname [formatname]]");
         return;
      }

      lsx2Html obj = new lsx2Html ();

      String filename = aa[0];
      String dataname = (aa.length > 1) ? aa[1]: "data";
      String formatname = (aa.length > 2) ? aa[2]: "formats";
      String outputname = filename + ".html";

      obj.start (filename, dataname, formatname, outputname);
   }

   private TextFile fix = new TextFile ();

//   ESPAN = "<span style=\"font-family:Courier New; font-size:8pt; color:#800000\">"
   private static final String ESPAN = "<span style=\"font-family:Courier New\">";


   public void start (String filename, String dataname, String formatname, String outputname)
   {
      start (filename,
             EvaFile.loadEvaUnit (filename, dataname),
             EvaFile.loadEvaUnit (filename, formatname),
             outputname);
   }

   public void start (String filename, EvaUnit edat, EvaUnit efor, String outputname)
   {
      if (!fix.fopen (outputname, "w"))
      {
         System.out.println ("Error cannot open for write " + outputname);
         return;
      }

      printHeader (filename);

      printList ("<br><h2>data</h2><br>", edat);
      printList ("<br><h2>formats</h2><br>", efor);
      printFormats (edat);
      printFormats (efor);

      printTail ();
   }

   private void printHeader (String filename)
   {
      // cabecera
      fix.writeLine ("<html><body><title>Report of LSX format <em>" + filename + "</em> </title>");
      fix.writeLine ("");
      fix.writeLine ("<h2> Fecha : " + new Date() + " </h2>");
   }

   private void printList (String titulo, EvaUnit eu)
   {
      if (eu == null) return;
      // lista de evas
      fix.writeLine (titulo + "<br><ul>");
      for (int ii = 0; ii < eu.size (); ii ++)
      {
         String nombre = eu.getEva (ii).getName ();
         fix.writeLine ("<li><a href=\"#" + nombre + "\">" + nombre + "</a></li>");
      }
      fix.writeLine ("</ul>");
   }


   private String prepareString (String str)
   {
      Cadena ca = new Cadena (str);

      ca.replaceMe ("<", "&lt;");
      ca.replaceMe (">", "&gt;");

      int partede = 0;

      do
      {
         int indx = ca.indexOfsubstr ("@&lt;", partede); // index of "@<"
         if (indx == -1) break;

         // referencia encontrada !
         int end = ca.indexOfsubstr ("&gt;", indx);    // index of ">" after "@<"
         if (end == -1) break;

         //System.out.println ("PECADORLS! partede=" + partede + " indx=" + indx + " end=" + end + " cosa [" + ca.o_str + "]");

         // example if found @<payas> then
         // convert it into "<a href=\"#payas\">@&lt;payas&gt;</a>"
         //
         String namico = ca.o_str.substring (indx+5, end);
         String todo = ca.o_str.substring (indx, end);

         str = ca.o_str.substring (0, indx);
         str += "<a href=\"#" + namico + "\">" + todo + "</a>";
         partede = str.length ();
         str += ca.o_str.substring (end);

         ca.setStr (str);
      }
      while (true);

      return ca.o_str;
   }


   private void printFormats (EvaUnit eu)
   {
      // evas
      if (eu == null) return;
      for (int ii = 0; ii < eu.size (); ii ++)
      {
         Eva eva = eu.getEva (ii);
         String nombre = eva.getName ();

         // nombre
         fix.writeLine ("<br><br><a name=\"" + nombre + "\">" + nombre + "</a>");

         // tabla
         //fix.writeLine ("   <table border=1 cellspacing=0 cellpadding=4 bordercolor=\"#700000\">");

         int rr = 0;
         while (rr < eva.rows ())
         {
            if (eva.cols (rr) > 1)
            {
               fix.writeLine ("      <tr><td><table border=1 cellspacing=0 cellpadding=2 bordercolor=\"#700000\">");
               //fix.writeLine ("      <td>");

               do
               {
                  fix.writeLine ("         <tr>");
                  for (int cc = 0; cc < eva.cols (rr); cc ++)
                  {
                     String str = prepareString (eva.getValue (rr, cc));

                     if (str.length () == 0) str = "&Oslash;";

                     fix.writeLine ("<td>" + ESPAN + str + "</span></td>");
                  }
                  rr ++;
               }
               while (rr < eva.rows () && eva.cols (rr) > 1);

               // fix.writeLine ("      </td>");
               fix.writeLine ("      </table></td>");
            }
            else
            {
               fix.writeLine ("      <tr><td><table border=1 cellpadding=1 cellspacing=1 bordercolor=\"#700000\">");
               fix.writeLine ("      <tr><td bgcolor=\"#FFFFF1\">");
               fix.writeLine ("      <pre>" + ESPAN);
               while (rr < eva.rows () && eva.cols (rr) <= 1)
               {
                  String str = prepareString (eva.getValue (rr, 0));

                  fix.writeLine (str);
                  rr ++;
               }
               fix.writeLine ("</span></pre>");
               fix.writeLine ("      </table></td>");
            }
         }

         //fix.writeLine ("</table>");
      }
   }

   private void printTail ()
   {
      fix.writeLine ("</body></html>");
      fix.fclose ();
   }


   public boolean unit2Html (EvaUnit eu, String htmlFile)
   {

      return true;
   }
}
