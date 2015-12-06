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

/**
   @class tableColumns
   @author Alejandro Xalabarder
   @brief Loop for column names and values of the current LOOP (of any type EVA, SQL, FOR etc)
          The columns for this loop are "columnName" and "columnValue"

          For example if the last loop is a LOOP EVA with following contents

          <myEva>
               id, name    , telephone
               12, Karsten , 778888822
               43, Gemma   , 118818888
               32, Patricia, 222222222
               54, Fabian  , 771717717

          and we call LOOP, COLUMNS at the ROW 2 of the LOOP EVA
          then it will be equivalent to looping the table

            columnName, columnValue

            id         , 32
            name       , Patricia
            telephone  , 222222222

         and if during this LOOP COLUMNS we perform another LOOP COLUMNS let's say at ROW 0 then
         we will have following table

            columnName, columnValue

            columnName,  name
            columnValue, Patricia

         etc

*/
public class tableColumns extends tableAccessBase
{
   public tableAccessBase refLoopTable = null; // reference to the last current loop

   //    LOOP, COLUMNS,
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      if (!cmdData.checkParamSize (1, 1))
         return false;

      currRow = zeroRow ();

      // get the reference to the last loop in the stack of listix
      //
      refLoopTable = null;
      tableCursorStack lsxStack = cmdData.getListix ().getTableCursorStack ();
      if (lsxStack == null)
      {
         cmdData.getLog().severe  ("tableColumns", "got a listix tableCursorStack null");
         return false;
      }
      int currentIndx = lsxStack.getDepth () - 1;
      if (currentIndx < 0)
      {
         cmdData.getLog().err  ("tableColumns", "LOOP COLUMNS called but there is any current LOOP");
         return false;
      }

      refLoopTable = (tableAccessBase) ((tableCursor) lsxStack.getInternVector().get (currentIndx)).data ();
      cmdData.getLog().dbg (2, "COLUMNS", "loop columns of [" + refLoopTable.getName () + "]");
      return true;
   }

   public void clean ()
   {
   }

   public int zeroRow ()
   {
      return 0;
   }

   public boolean isValid ()
   {
      return refLoopTable != null;
   }

   public boolean BOT ()
   {
      return currRow == zeroRow ();
   }

   public boolean EOT ()
   {
      return (refLoopTable == null || currRow >= rawRows () || rawRows () < 0);
   }

   public int columns ()
   {
      return 2;
   }

   public String colName  (int colIndex)
   {
      if (colIndex == 0) return "columnName";
      if (colIndex == 1) return "columnValue";
      return "";
   }

   public int colOf (String colName)
   {
      if ("columnName".equals (colName)) return 0;
      if ("columnValue".equals (colName)) return 1;
      return -1;
   }

   public String getName ()
   {
      return "ColumnLoopOf" + (refLoopTable != null ? refLoopTable.getName (): "null");
   }

   public String getValue (int row, int col)
   {
      //   Example:
      //   columnName, columnValue
      //
      //   id, 12
      //   person, Marika
      //   telef, 2321321

      int hisColumn = currentRawRow ();               // column to inspect
      int hisRow    = refLoopTable.currentRawRow();   // current row of referenced loop

      if (col == 0)
         return refLoopTable.colName (hisColumn);

      if (col == 1)
         return refLoopTable.getValue (hisRow, hisColumn);

      return "";
   }

   public int rawRows ()
   {
      // our row number is the column number of the previous loop or sub-loop
      //System.err.println ("tus propias ros son " + refLoopTable.columns ());
      return refLoopTable.columns ();
   }
}
