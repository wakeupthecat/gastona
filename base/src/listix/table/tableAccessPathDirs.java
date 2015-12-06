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
import de.elxala.Eva.*;

public class tableAccessPathDirs extends tableAccessBase
{
   public Eva evaData = null;

   private void getDirectories (String dirPath, boolean recurse)
   {
      evaData = new Eva ("dirs");
      evaData.addLine (new EvaLine (pathGetFiles.getRecordColumns ()));

      pathGetFiles moto = new pathGetFiles(pathGetFiles.FOR_DIRECTORIES);
      moto.initScan (dirPath, new String[0], recurse);

      List cosas = null;
      do
      {
         cosas = moto.scanN (100);
         for (int jj = 0; jj < cosas.size (); jj++)
         {
            String [] record = (String []) cosas.get (jj);
            evaData.addLine (new EvaLine (record));
         }
      }
      while (cosas.size () > 0);
   }

   //    LOOP, DIRS, path, recursive
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      if (!cmdData.checkParamSize (2, 99999))
         return false;

      // data that could be required by tableAccessBase tables

      String typeTable   = cmdData.getArg(0);
      String dirPath     = cmdData.getArg(1);
      boolean recursive  = "1".equals (cmdData.getArg(2));

      // set data
      getDirectories (dirPath, recursive);

      currRow = zeroRow ();

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
      return evaData != null;
   }

   public boolean BOT ()
   {
      return currRow == zeroRow ();
   }

   public boolean EOT ()
   {
      return (evaData == null || currRow >= evaData.rows () || evaData.rows () < 2);
   }

   public int columns ()
   {
      return evaData.cols (0);
   }

   public String colName  (int colIndex)
   {
      return evaData.getValue (0, colIndex);
   }

   public int colOf (String colName)
   {
      return evaData.colOf (colName);
   }

   public String getName ()
   {
      return evaData.getName ();
   }

   public String getValue (int row, int col)
   {
      return evaData.getValue (row, col);
   }

   public int rawRows ()
   {
      return evaData.rows ();
   }
}
