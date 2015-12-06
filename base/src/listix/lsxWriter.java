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

package listix;

import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;


/**
*/

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import java.util.*;
import java.util.regex.*;  // for Matcher, Pattern

/**
      si encuentra <main> lo ejecuta

*/
public class lsxWriter
{
   public static void main (String [] par)
   {
      if (par.length > 0)
      {
         if (par[0].equals ("-listix_version"))
         {
            System.out.println (listix.getVersion ());
            return;
         }
      }

      switch (par.length)
      {
         case 1:
            llamada1 (par);
            break;

         case 6:
            llamadaN (par);
            break;

         default:
            SyntaxN ();
      }
   }

   protected static void SyntaxN ()
   {
      System.out.println ("listix.lsxWriter version " + listix.getVersion ());
      System.out.println ("copyright (c) 2005,2006,2007,2008,2009,2010,2011 Alejandro Xalabarder Aulet");
      System.out.println ("");
      System.out.println ("Syntax I  : lsxWriter lsxFile");
      System.out.println ("Syntax II : lsxWriter dataFile dataUnit formatFile formatUnit formatName outputFile");
      System.out.println ("");
      System.out.println ("Example:    lsxWriter sample.lsx data sample.lsx formats main con");
      System.out.println ("");
      System.out.println ("Where sample.lsx is a file that contains:");
      System.out.println ("");
      System.out.println ("#data#");
      System.out.println ("");
      System.out.println ("  <variable> World");
      System.out.println ("");
      System.out.println ("#listix#");
      System.out.println ("");
      System.out.println ("  <main>");
      System.out.println ("     // Hello lsx @<variable>!");
      System.out.println ("     // (read more about Eva and Lsx in www.elxala.de)");
      System.out.println ("     //");
      System.out.println ("");
   }

   protected static boolean llamada1 (String [] par)
   {
      if (par.length != 1)
         return false;
      callComplete (par[0], "data", par[0], "listix", "main", "con");
      return true;
   }

   protected static boolean llamadaN (String [] par)
   {
      //                                                0       1         2        3           4             5
      // System.out.println ("Syntax II : lsxWriter dataFile dataUnit formatFile formatUnit formatName outputFile\n");
      if (par.length != 6)
      {
         SyntaxN ();
         return false;
      }

      callComplete (par[0], par[1], par[2], par[3], par[4], par[5]);
      return true;
   }


   public static boolean callSimple (String lsxFileName)
   {
      return callComplete (lsxFileName, "data", lsxFileName, "listix", "main", "con");
   }

   public static boolean callComplete (String dataFile, String dataUnit, String  formatFile, String  formatUnit, String  formatName, String  outputFile)
   {
      EvaUnit Udata    = EvaFile.loadEvaUnit (dataFile,   dataUnit);
      EvaUnit Uformats = EvaFile.loadEvaUnit (formatFile, formatUnit);

      if (Udata == null)
      {
         System.err.println ("Evaunit #" + dataUnit + "# for data not found in \"" + dataFile + "\"!");
         return false;
      }

      if (Uformats == null)
      {
         Uformats = new EvaUnit ("void formats");
         // System.err.println ("Evaunit #" + formatUnit + "# for formats not found in \"" + formatFile + "\"!");
         // return false;
      }

      // some variables (IDEA, it could be done in makeFile)
      (Udata.getSomeHowEva (":LSX dataFile")).setValueVar (dataFile);
      (Udata.getSomeHowEva (":LSX formatFile")).setValueVar (formatFile);

      makeFile (Udata, Uformats, formatName, outputFile, new tableCursorStack (), null, null);
      return true;
   }

   public static void makeFile (EvaUnit EUdata, EvaUnit EUformats, String formatToSolve, String outputFile, tableCursorStack tables, TextFile currentTextFile, Eva currentTargetEva)
   {
      // go
      //
      listix writer = new listix (EUformats, EUdata, tables);

      // outputFile could use eva variables! e.g. "@<objName>.cpp"
      //
      // System.err.println ("ORIGINARIAMENTE \"" + outputFile + "\"");
      outputFile = writer.solveStrAsString (outputFile);

      // decide if open a file or just continue writing in the current one
      // ("-", "+" or "*" as filename means continue with the current one)
      //
      boolean bOpenNewFile = outputFile.length () > 0 &&
                            !outputFile.equals("-") &&
                            !outputFile.equals("+") &&
                            !outputFile.equals("*");

      //bOpenNewFile = true;
      if (bOpenNewFile)
      {
         writer.log().dbg (0, "lsxWriter::makeFile(1)", "generating file \"" + outputFile + "\"");
         if (!writer.openTargetFile (outputFile))
         {
            writer.log().err ("lsxWriter::makeFile(1)", "output file \"" + outputFile + "\" could not be opened!");
            return;
         }
      }
      else
      {
         //19.03.2006 12:18   Now is allowed for currentTextFile to be null (= con)
//         if (currentTextFile == null)
//         {
//            System.err.println ("Error : outputFile \"" + outputFile + "\" could not be opened because current file is null!");
//            return;
//         }
         writer.setTargetEva (currentTargetEva);
         writer.assignTargetFile (currentTextFile);
      }

      // write it out
      //
      writer.printLsxFormat (formatToSolve);

      writer.closeTargetFile ();
   }

   public static void makeFile (listix writer, String formatToSolve, String outputFile, TextFile currentTextFile, Eva currentTargetEva)
   {
      // default, do not append!
      makeFile (writer, formatToSolve, outputFile, currentTextFile, currentTargetEva, false);
   }

   public static void makeFile (listix writer, String formatToSolve, String outputFile, TextFile currentTextFile, Eva currentTargetEva, boolean append)
   {
      // outputFile could use eva variables! e.g. "@<objName>.cpp"
      //
      // System.err.println ("ORIGINARIAMENTE \"" + outputFile + "\"");
      outputFile = writer.solveStrAsString (outputFile);

      // decide if open a file or just continue writing in the current one
      // ("-", "+" or "*" as filename means continue with the current one)
      //
      boolean bOpenNewFile = outputFile.length () > 0 &&
                            !outputFile.equals("-") &&
                            !outputFile.equals("+") &&
                            !outputFile.equals("*");

      //bOpenNewFile = true;
      if (bOpenNewFile)
      {
         writer.log().dbg (0, "lsxWriter::makeFile(2)", "generating file \"" + outputFile + "\"");
         if (!writer.openTargetFile (outputFile, append))
         {
            writer.log().err ("lsxWriter::makeFile(2)", "output file \"" + outputFile + "\" could not be opened!");
            return;
         }
      }
      else
      {
         //19.03.2006 12:18   Now is allowed for currentTextFile to be null (= con)
//         if (currentTextFile == null)
//         {
//            System.err.println ("Error : outputFile \"" + outputFile + "\" could not be opened because current file is null!");
//            return;
//         }
         writer.setTargetEva (currentTargetEva);
         writer.assignTargetFile (currentTextFile);
      }

      // write it out
      //
      writer.printLsxFormat (formatToSolve);

      writer.closeTargetFile ();
   }
}
