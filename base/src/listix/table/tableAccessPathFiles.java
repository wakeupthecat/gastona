/*
library listix (www.listix.org)
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

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

package listix.table;

import listix.*;


import java.util.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;

public class tableAccessPathFiles extends tableAccessBase
{
   protected boolean onlyDirectories = false;

   protected String [] columnNames = null;
   protected Eva cacheEvaData = null;
   protected EvaLine currentData = null;
   protected int cacheIndx = 0;
   protected int lastRequestedRow = 0;
   protected boolean endOfListing = true;
   protected pathGetFiles fileMotor = null;

   protected listixCmdStruct lastCmdData = null;

   private void startScanFiles (String dirPath, fileMultiFilter fil, boolean recurse)
   {
      clean ();
      columnNames = pathGetFiles.getRecordColumns ();
      cacheEvaData = new Eva (onlyDirectories ? "dirs": "files");

      fileMotor = new pathGetFiles (onlyDirectories ? pathGetFiles.FOR_DIRECTORIES: pathGetFiles.FOR_FILES);
      fileMotor.initScan (dirPath, "", recurse, fil);
      currRow =  0;
      lastRequestedRow = 0;
      loadNextFiles ();
      endOfListing = cacheEvaData.rows () == 0;
   }

   private boolean prepareRow (int row)
   {
      if (row == lastRequestedRow)
         return true;

      if (endOfListing)
      {
         lastCmdData.getLog().dbg (4, "prepareNextRow (" + row  + "), endOfListing is true");
         return false;
      }

      if (row != lastRequestedRow + 1)
      {
         lastCmdData.getLog().warn ("prepareNextRow (" + row  + ") but lastRequestedRow = " + lastRequestedRow + ", pathFiles has to be accessed sequentially!");
         return false;
      }

      lastRequestedRow = row;
      if (cacheIndx + 1 < cacheEvaData.rows ())
      {
         cacheIndx ++;
         lastCmdData.getLog().dbg (4, "prepareNextRow (" + row  + "), increase cache index to " + cacheIndx);
         return true;
      }
      return loadNextFiles ();
   }


   private boolean loadNextFiles ()
   {
      cacheIndx = 0;
      List cosas = null;
      cacheEvaData =  new Eva ("files");
      cosas = fileMotor.scanN (100);

      lastCmdData.getLog().dbg (4, "loadNextFiles, new scan result in " + cosas.size () + " new files");
      for (int jj = 0; jj < cosas.size (); jj++)
      {
         String [] record = (String []) cosas.get (jj);
         cacheEvaData.addLine (new EvaLine (record));
      }
      //lastCmdData.getLog().dbg (4, "VALUO VALUO [" +  cacheEvaData.getValue (0, 0) + "] rows " + cacheEvaData.rows ());

      endOfListing = cacheEvaData.rows () == 0;
      return !endOfListing;
   }

   private void addExtensions (fileMultiFilter filtrum, String extOrExts)
   {
      // each string will be splited by comma as well
      // so making possible to give all extensions in the firt argument
      //
      // we trim only if found comma separated values, the reason is to
      // allow in other cases including blanks, it sounds weird but it could be possible, for example
      //    EXT, " weird"
      //
      // to include the file "soso. weird"
      //
      String[] extext = extOrExts.split(",");
      for (int ee = 0; ee < extext.length; ee ++)
         filtrum.addCriteria ("+", extext.length == 1 ? extext[ee]: extext[ee].trim ());
   }

   //    LOOP, FILES, path, extension, extension
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      lastCmdData = cmdData;
      if (!cmdData.checkParamSize (2, 99999))
         return false;

      // data that could be required by tableAccessBase tables

      // String typeTable   = cmdData.getArg(0); // always FILES
      String dirPath     = cmdData.getArg(1);

      fileMultiFilter filtrum = new fileMultiFilter ();

      boolean recursive = -1 != "1yYSs".indexOf (cmdData.takeOptionString(new String [] { "RECURSIVE", "RECURSE", "REC" }, "1").substring(0,1));
      String [] extList = cmdData.takeOptionParameters(new String [] { "EXTENSIONS", "EXT" });

      // get extensions and other filters
      //

      // add extensions from arguments to filter
      //
      for (int aa = 2; aa < cmdData.getArgSize (); aa ++)
      {
         addExtensions (filtrum, cmdData.getArg(aa));
      }

      if (extList != null)
         for (int oe = 0; oe < extList.length; oe ++)
         {
            addExtensions (filtrum, extList[oe]);
         }

      // get option PATHFILTERS, opt, regexp, opt, regexp ...
      // i.e.       PATHFILTERS, -D, \.git, -D, lint
      //
      String [] optArr = cmdData.takeOptionParameters(new String [] { "FILTERS", "PATHFILTER", "PATHFILTERS" });
      if (optArr != null)
      {
         if (optArr.length == 1)
         {
            // we have a comma separated list of filters like "-D, \.git, -D, lint"
            // so we split it in the same array!
            //
            filtrum.addCriteriaInAString (optArr[0]);
         }
         else
         {
            for (int ff = 0; ff+1 < optArr.length; ff += 2)
            {
               filtrum.addCriteria (optArr[ff], optArr[ff + 1]);
            }
         }
      }

      lastCmdData.getLog().dbg (2, "setCommand dirPath [" + dirPath + "] filtrum [" + filtrum + "] recursive " + recursive);
      startScanFiles (dirPath, filtrum, recursive);
      currRow = zeroRow ();

      return true;
   }

   // Alternative setting
   //
   public boolean setCommand (listixCmdStruct cmdData, String rootFolder, String extensions, boolean recursive)
   {
      lastCmdData = cmdData;
      fileMultiFilter filtrum = new fileMultiFilter ();

      addExtensions (filtrum, extensions);

      lastCmdData.getLog().dbg (2, "setCommand rootFolder [" + rootFolder + "] filtrum [" + filtrum + "] recursive " + recursive);
      startScanFiles (rootFolder, filtrum, recursive);
      currRow = zeroRow ();
      return true;
   }

   public void rowIsIncremented ()
   {
      prepareRow (currRow);
   }

   public void clean ()
   {
      columnNames = null;
      cacheEvaData = null;
      currentData = null;
      cacheIndx = 0;
      endOfListing = true;
      fileMotor = null;
   }

   public int zeroRow ()
   {
      return 0;
   }

   public boolean isValid ()
   {
      return cacheEvaData != null;
   }

   public boolean BOT ()
   {
      return currRow == zeroRow ();
   }

   public boolean EOT ()
   {
      return (cacheEvaData == null || cacheEvaData.rows () == 0 || endOfListing);
   }

   public int columns ()
   {
      return columnNames == null ? 0: columnNames.length;
   }

   public String colName  (int colIndx)
   {
      return (columnNames == null || colIndx < 0 || colIndx >= columnNames.length) ? "": columnNames[colIndx];
   }

   public int colOf (String colName)
   {
      if (columnNames == null) return -1;
      for (int ii = 0; ii < columnNames.length; ii ++)
         if (colName.equals(columnNames[ii]))
            return ii;
      return -1;
   }

   public String getName ()
   {
      return cacheEvaData.getName ();
   }

   public String getValue (int row, int col)
   {
      prepareRow (row);
      if (!EOT ())
      {
         //lastCmdData.getLog().dbg (2, "tableAccessPathFiles getValue (" + row + ", " + col + ") cacheIndx " + cacheIndx + " rows " + cacheEvaData.rows () + " VAL00 ]" + cacheEvaData.getValue (0, 0) + "[");
         String val = cacheEvaData.getValue (cacheIndx, col);
         return val;
      }
      return "";
   }

   public int rawRows ()
   {
      if (lastCmdData != null)
         lastCmdData.getLog().warn ("rawRows for tableAccessPathFiles is not supported!");
      return cacheEvaData.rows () + 1;
   }
}
