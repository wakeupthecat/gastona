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
import de.elxala.db.sqlite.tableROSelect;

public class tableAccessSql extends tableAccessBase
{
   public tableROSelect sqlModel = null;

   //    SET TABLE, SQL, database, sqlQuery
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      if (!cmdData.checkParamSize (3, 3))
         return false;

      // data that could be required by tableAccessBase tables

      String typeTable   = cmdData.getArg(0);
      String dbName      = cmdData.getArg(1);
      String sqlQuery    = cmdData.getArg(2);

      if (sqlQuery.length () <= 1)
      {
         cmdData.getLog().err ("tableAccessSql", "not valid query specified!");
         return false;
      }

      if (dbName.length () == 0)
         dbName = cmdData.getListix().getDefaultDBName ();

      // set data
      //
      sqlModel = roSqlPool.getElement (dbName, sqlQuery);
      //sqlModel = getROSelectObj (cmdData.getListix().getTableCursorStack().getDepth(), dbName, sqlQuery, sqlPrevious);
      currRow = zeroRow ();

      return true;
   }

   public void clean ()
   {
      roSqlPool.disposeElement ();
   }

   public int zeroRow ()
   {
      return 0;
   }

   public boolean isValid ()
   {
      return sqlModel != null;
   }

   public boolean BOT ()
   {
      return currRow == zeroRow ();
   }

   public boolean EOT ()
   {
      return (! isValid () || currRow >= rawRows () || nDataRows () < 1);
   }

   public int columns ()
   {
      return sqlModel.getColumnCount ();
   }

   public String colName  (int colIndex)
   {
      return sqlModel.getColumnName (colIndex);
   }

   public int colOf (String colName)
   {
      return sqlModel.getColumnIndex (colName);
   }

   public String getName ()
   {
      return "tableCursorSql";
   }

   public String getValue (int row, int col)
   {
      return (String) sqlModel.getValue (row, col);
   }

   public int rawRows ()
   {
      return sqlModel.getRecordCount ();
   }

   public void rowIsIncremented ()
   {
   }
}
