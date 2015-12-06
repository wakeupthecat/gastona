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
import de.elxala.Eva.*;

public class tableAccessEva extends tableAccessBase
{
   public Eva evaData = null;

   //    LOOP, EVA, evaName, [ evaUnit ], [ evaFile ]
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      if (!cmdData.checkParamSize (2, 4))
         return false;

      // data that could be required by tableAccessBase tables

      String typeTable   = cmdData.getArg(0);
      String evaName     = cmdData.getArg(1);
      String evaUnit     = cmdData.getArg(2);
      String evaFile     = cmdData.getArg(3);

      currRow = zeroRow ();

      // set data
      //
      if (evaUnit.length () > 0)
      {
         // the eva has to loaded from a specific file and eva unit
         if (evaFile.length () == 0)
         {
            cmdData.getLog().err  ("tableAccessEva", "evaFile must be specified in command SET TABLE, " + evaName + ", " + evaUnit + " ...");
            return false;
         }
         EvaUnit eu = EvaFile.loadEvaUnit (evaFile, evaUnit);
         if (eu == null)
         {
            cmdData.getLog().err  ("tableAccessEva", "evaUnit " + evaUnit + " not found in file [" + evaFile + "] or file does not exists!");
            return false;
         }
         evaData = eu.getEva (evaName);
         if (evaData == null)
         {
            cmdData.getLog().err  ("tableAccessEva", "evaData not found in evaUnit " + evaUnit + " from file [" + evaFile + "]");
            return false;
         }
      }
      else
      {
         evaData = cmdData.getListix().getVarEva (evaName);
      }

      if (! isValid ())
      {
         cmdData.getLog().err  ("tableAccessEva", "TABLE EVA could not be found!: [" + evaName + "]");
         return false;
      }

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

   public void rowIsIncremented ()
   {
   }
}
