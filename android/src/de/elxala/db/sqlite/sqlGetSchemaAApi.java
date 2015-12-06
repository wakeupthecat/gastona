/*
library de.elxala
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

package de.elxala.db.sqlite;

import java.io.File;

import java.util.Vector;
import java.util.List;

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

import de.elxala.db.*;

/*
   31.12.2011 13:44

   class to get the schema of the database using the batch technique (not aplicable to android)

   Technical note:
      getSchema could be a simply method of sqlSolverBatch, but since this class is
      big enough I prefer to write this class apart.

*/
public class sqlGetSchemaAApi
{
   private static logger log = new logger (null, "de.elxala.db.sqlite.sqlGetSchemaAApi", null);

   public static void getSchema (String dbName, Eva evaResult)
   {
      evaResult.clear ();
      evaResult.addLine (new EvaLine ("id, tabType, tableName, columnCid, columnName, columnType, not_null, def_value, pk"));
      // columns : id, tabType, tableName, columnCid, columnName, columnType, not_null, def_value, pk


      //Get the table names
      //
      sqlSolver cliDB = new sqlSolver ();
      String [] vecTables = cliDB.getTables (dbName);
      String [] vecViews = cliDB.getViews (dbName);

      log.dbg (2, "getSchema", "building schema of " + vecTables.length + " table(s) and " + vecViews.length + " view(s) in DB [" + dbName +  "].");

      int tableCnt = 0;
      for (int ii = 0; ii < vecTables.length; ii ++)
      {
         tableROSelect tRo = new tableROSelect (dbName, "PRAGMA table_info("+ vecTables[ii] + ") ;");
         collect_fields (evaResult, tRo, tableCnt++, "table", vecTables[ii]);
      }
      for (int ii = 0; ii < vecViews.length; ii ++)
      {
         tableROSelect tRo = new tableROSelect (dbName, "PRAGMA table_info("+ vecViews[ii] + ") ;");
         collect_fields (evaResult, tRo, tableCnt++, "view", vecTables[ii]);
      }
   }

   private static void collect_fields (Eva evaResult, tableROSelect tableFields, int tableCnt, String tableType, String tableName)
   {
      for (int jj = 0; jj < tableFields.getRecordCount (); jj ++)
      {
         //Column structure to fill:
         // id, tabType, tableName, columnCid, columnName, columnType, not_null, def_value, pk

         // structure from table_info PRAGMA:
         // cid,name,type,notnull,dflt_value,pk
         evaResult.addLine (
            new EvaLine (
               tableCnt + ", " +
               tableType + ", " +
               tableName + ", " +
               tableFields.getValue ("cid", jj) + ", " +
               tableFields.getValue ("name", jj) + ", " +
               tableFields.getValue ("type", jj) + ", " +
               tableFields.getValue ("notnull", jj) + ", " +
               tableFields.getValue ("dflt_value", jj) + ", " +
               tableFields.getValue ("pk", jj)
            ));
      }
   }
  
}
