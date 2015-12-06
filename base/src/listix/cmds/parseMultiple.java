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

/*
*/

//package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.parse.parsons.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end

/**
*/
public class parseMultiple
{
   private String fileSource  = null;
   private String baseTarget  = null;

   private int startLine = 1;
   private int endLine   = -1;
   private int limitLines = -1;
   private int limitRecords = -1;

   private int fileID = -1;

   private TextFile fixSources = new TextFile ();
   private TextFile fixTarget = new TextFile ();

   private aLineParsons parsons = null;

   public static void main (String [] aa)
   {
      if (aa.length < 4)
      {
         System.out.println ("Syntax: sourceFilesFile targetFile field, field, ..., strPattern");
         return;
      }

      String [] fields = new String [aa.length - 3];
      for (int ii = 2; ii < aa.length - 1; ii ++)
      {
         System.out.println ("field " + (ii-2) + " " + aa[ii]);
         fields [ii-2] = aa[ii];
      }
      System.out.println ("pattern is [" + aa[aa.length - 1] + "]");

      parseMultiple coso = new parseMultiple ();
      coso.parseMultipleFiles (aa[0], aa[1], fields, aa[aa.length - 1]);
   }

   /**
   */
   public void parseMultipleFiles (String sourceFilesFile, String targetFile, String [] fields, String strPattern)
   {
      //System.out.println ("working with file" + fileSource);
      TextFile fixSources = new TextFile ();

      System.out.println ("open [" + sourceFilesFile + "] with files to parse");
      if (!fixSources.fopen (sourceFilesFile, "r"))
      {
         System.err.println ("File cannot be open! [" + sourceFilesFile + "]");
         return;
      }

      System.out.println ("open [" + targetFile + "] to write result");
      fixTarget = new TextFile ();
      if (!fixTarget.fopen (targetFile, "w"))
      {
         System.err.println ("File cannot be open for append! [" + targetFile + "]");
         return;
      }

      // construct parsons
      //
      // public void addFieldsPatternMap (String pattern, String [] fields)
      parsons = new aLineParsons();
      parsons.addFieldsPatternMap (strPattern, fields);

      fileID = -1;
      while (fixSources.readLine ())
      {
         String fileToParse = fixSources.TheLine ();
         System.out.println ("parsing [" + fileToParse + "]");
         fileID ++;
         doParseFile (fileToParse);
         //doParseFile (fileID ++, fileToParse);
      }
      fixTarget.fclose ();
      fixSources.fclose ();
   }

   private void doParseFile (String fileToParse)
   {
      if (fileToParse == null || fileToParse.length () == 0)
      {
         System.err.println ("File to parse not found! [" + fileSource + "]");
         //that.log ().err ("PARSONS", "File to scan not found! [" + fileSource + "]");
         return;
      }

      TextFile fix = new TextFile ();
      if (!fix.fopen (fileToParse, "r"))
      {
         System.err.println ("File cannot be open! [" + fileToParse + "]");
         return;
      }
      // file is open, start to parse!
      //

//      String fieldsCommas = "";
//      for (int ii = 0; ii < parsons.getFieldNames().length; ii ++)
//      {
//         fieldsCommas += ((ii > 0) ? ", ":"") + parsons.getFieldNames()[ii];
//      }

      int nLine = 1;
      int nLinesRead = 0;
      int nRecords = 0;

      String lineStr = "";
      while (fix.readLine ())
      {
         // control optional limits
         //
         if (nLine < startLine) { nLine++; continue; };
         if (endLine != -1 && nLine > endLine)   break;
         if (limitLines != -1 && nLinesRead >= limitLines) break;
         if (limitRecords != -1 && nRecords >= limitRecords) break;

         lineStr = fix.TheLine ();
         while (lineStr.length() > 0)
         {
            int indxCol = parsons.scan (lineStr);
            if (parsons.recordComplete ())
            {
               nRecords ++;
               //that.log ().dbg (5, "PARSONS", "new record at line " + nLine);
               String camphos = "";
               for (int jj = 0; jj < parsons.getFieldNames().length; jj ++)
               {
                  //that.log ().dbg (5, "PARSONS", parsons.getFieldNames()[jj] + " = [" + parsons.getCurrentValues()[jj] + "]");
                  //camphos += ((jj==0)? "": ",") + "'" + myDB.escapeString (parsons.getCurrentValues()[jj]) + "'";
                  camphos += ((jj==0)? "": ",") + parsons.getCurrentValues()[jj];
               }

               fixTarget.writeLine (fileID + ", " + nLine + ", " + camphos);
            }
            if (indxCol > 0)
            {
               lineStr = lineStr.substring (indxCol);
            }
            else lineStr = "";
         }
         nLine ++;
         nLinesRead ++;
      }
      fix.fclose ();
   }
}