/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.Eva.util;

/**
   Clase EvaFileToHtml
   08.10.2004 00:28 por Alejandro Xalabarder
   09.02.2009 01:53

*/
import java.util.*;
import java.io.File;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;

public class EvaFileToHtml
{
   /**
      MAIN procedure!
   */
   public static void main (String [] aa)
   {
      if (aa.length < 2)
      {
         //aa = new String [] { "A:\java\src\gastona\app\arces\arces.gast" };
         System.out.println ("Syntax: fileNameEva htmlFileName [stylesString]");
         return;
      }

      String htmFileName = aa[1];

      String stylesString = "body { font-family: Tahoma; } td {align: left} table {}";
      if (aa.length > 2)
         stylesString = aa[2];

      EvaFileToHtml obj = new EvaFileToHtml ();
      obj.file2Html (aa[0], htmFileName, stylesString);
   }

   public boolean file2Html (String evaFileName, String htmlFileName, String styles)
   {
      File fileEva = new File (evaFileName);
      if (! fileEva.isFile ())
      {
         System.err.println ("no file given [" +  evaFileName + "]");
         return false;
      }

      evaFileName = fileEva.getAbsoluteFile() + "";
      if (htmlFileName.length () == 0)
         htmlFileName = evaFileName + ".html";

      // abrir fichero html
      TextFile fix = new TextFile ();
      if ( ! fix.fopen (htmlFileName, "w"))
      {
         System.err.println ("Problems opening " + htmlFileName + " for write!");
         return false;
      }

      // cabecera
      fix.writeLine ("<html>");
      if (styles.length () > 0)
      {
         fix.writeLine ("   <style>");
         fix.writeLine ("      " + styles);
         fix.writeLine ("   </style>");
      }

      String dateStr  = DateFormat.getStr (new Date(fileEva.lastModified ()));

      fix.writeLine ("<body><title>Report of EvaUnit <em>" + fileEva.getName() + "</em> </title>");
      fix.writeLine ("");
      fix.writeLine ("    <table class=\"fileData\">");
      fix.writeLine ("        <tr><th class=\"fileData\">File name<td>" + evaFileName + "</tr>");
      fix.writeLine ("        <tr><th class=\"fileData\">File date<td>" + dateStr + "</tr>");
      fix.writeLine ("        <tr><th class=\"fileData\">File size<td>" + fileEva.length () + "</tr>");
      fix.writeLine ("        <tr><th class=\"fileData\">Date of report<td>" + new Date () + "</tr>");
      fix.writeLine ("    </table>");
      fix.writeLine ("");

      EvaFile ef = new EvaFile ();
      EvaUnit eu = new EvaUnit ();

      // list of units
      //
      String [] catalogo = ef.getCatalog(evaFileName);
      fix.writeLine (unitCatalog2HtmlList(catalogo));

      // loop units
      //
      for (int ii = 0; ii < catalogo.length; ii ++)
      {
         fix.writeLine ("    <h2><a name=\"UNIT_" + catalogo[ii] + "\">" + "Unit #" + catalogo[ii] + "#</a></h2>");
         eu = ef.load(evaFileName, catalogo[ii]);

         fix.writeLine ("    <ul class=\"evalist\">");
         // list of units
         for (int ee = 0; ee < eu.size (); ee ++)
         {
            fix.writeLine ("      <li class=\"evalist\"><a href=\"#EVA_" + eu.getEva (ee).getName () + "\">" + eu.getEva (ee).getName () + "</a></li>");
         }
         fix.writeLine ("    </ul>");
         for (int ee = 0; ee < eu.size (); ee ++)
         {
            fix.writeLine (eva2HtmlList (eu.getEva (ee)));
         }
      }

      fix.writeLine ("</body></html>");
      fix.fclose ();
      return true;
   }


   public static String eva2HtmlList (Eva eva)
   {
      String sal = "     <h3><a name=\"EVA_" + eva.getName () + "\">&lt;" + eva.getName () + "&gt;</a></h3>\n";

      sal += "<table border=\"1\" class=\"eva\">\n";

      // tabla
      for (int rr = 0; rr < eva.rows (); rr ++)
      {
         sal += "<tr><th>" + rr + "\n";
         for (int cc = 0; cc < eva.cols (rr); cc ++)
         {
            //(o) celta_todo EvaUtil aqui' usar un StringToHtml o asi'
            Cadena ca = new Cadena (eva.getValue (rr, cc));
            ca.replaceMe ("<", "&lt;");
            ca.replaceMe (">", "&gt;");
            // acabar bien con </td> ! para que se pueda seleccionar el texto con los espacios
            sal += "<td>" + ca.o_str + "</td>\n";
         }
      }
      sal += "</table>\n";
      return sal;
   }


   public static String unitCatalog2HtmlList (String [] catalogo)
   {
      String sal = "   <h2>List of units</h2><br>\n";
      sal += "    <ul class=\"units\">";
      // list of units
      for (int ii = 0; ii < catalogo.length; ii ++)
      {
         sal += "      <li class=\"unit\"><a href=\"#UNIT_" + catalogo[ii] + "\">" + catalogo[ii] + "</a></li>";
      }
      sal += "    </ul>";
      return sal;
   }
}
