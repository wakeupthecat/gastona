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

package listix.table;

import listix.*;


import java.util.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

public class tableAccessTextFile extends tableAccessBase
{
   private listix callerRef = null;
   private TextFile fileopen = null;
   private String fileName = null;
   private int readRow = 0;


   //    SET TABLE, TEXTFILE, fileName
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      if (!cmdData.checkParamSize (2, 2))
         return false;

      callerRef = cmdData.getListix ();

      fileName = cmdData.getArg(1);

      currRow = zeroRow ();

      fileopen = new TextFile ();
      if (! fileopen.fopen (fileName, "r"))
      {
         callerRef.log().err ("LOOP TEXTFILE", "File name [" + fileName + "] could not be opened!");
         fileopen.fclose ();
         return false;
      }

      // set cursor to 1st line
      readRow = 1;
      fileopen.readLine ();

      return true;
   }

   public void clean ()
   {
   }

   public int zeroRow ()
   {
      return 1;
   }

   public boolean isValid ()
   {
      return true; // ?? sino que ??
   }

   public boolean BOT ()
   {
      return currRow == zeroRow ();
   }

   public boolean EOT ()
   {
      while (!fileopen.feof () && readRow < currRow)
      {
         readRow ++;
         fileopen.readLine ();
      }

      return fileopen.feof ();
   }

   public int columns ()
   {
      return 2;
   }

   public String colName  (int colIndex)
   {
      if (colIndex == 0) return "line";
      if (colIndex == 1) return "value";
      return "";
   }

   public int colOf (String colName)
   {
      if (colName.equals ("line"))
      {
         return 0;
      }
      if (colName.equals ("value"))
      {
         return 1;
      }

      // only column 0 is allowed
      //callerRef.log().err ("LOOP TEXTFILE", "Attemp to read column \"" + colName + "\", but only \"line\" or \"value\" are allowed!");
      return -1;
   }

   public String getName ()
   {
      return fileName;
   }

   public String getValue (int row, int col)
   {
      if (col != 0 && col != 1)
      {
         // only column 0 is allowed
         callerRef.log().err ("LOOP TEXTFILE", "Attemp to read column " + col + ", but only column 0 and 1 are possible!");
         return "";
      }

      if (row < readRow)
      {
         // read should be sequential!
         callerRef.log().err ("LOOP TEXTFILE", "Attemp to read a previous line (row = " + row + ", currRow = " + currRow + ")!");
         return "";
      }

      if (EOT ())
      {
         callerRef.log().err ("LOOP TEXTFILE", "Attemp to read after EOT!");
         return "";
      }

      return (col == 0) ? ("" + readRow): fileopen.TheLine ();
   }

   public int rawRows ()
   {
      return currRow + 1;  // false rawRows but cannot be calculated (too expensive!)
   }
}
