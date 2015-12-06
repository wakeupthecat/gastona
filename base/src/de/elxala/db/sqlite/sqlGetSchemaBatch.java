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
public class sqlGetSchemaBatch
{
   private static logger log = new logger (null, "de.elxala.db.sqlite.sqlGetSchemaBatch", null);

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

      // for (int ii = 0; ii < vecTables.length; ii ++)
      //    System.out.println (vecTables[ii]);

      //build the script
      //
      //.separator ,
      //.headers off
      //SELECT "#data#";
      //SELECT "   <tableInfo first_table>";
      //PRAGMA table_info(first_table);
      //SELECT "   <tableInfo second_table>";
      //PRAGMA table_info(second_table);
      //...

      cliDB.openScript (false);
      cliDB.writeScript (".separator ,");
      cliDB.writeScript (".headers off");
      cliDB.writeScript ("SELECT \"#data#\" ;");
      for (int ii = 0; ii < vecTables.length; ii ++)
      {
         cliDB.writeScript ("SELECT \"   <tableInfo " + vecTables[ii] + ">\" ;");
         cliDB.writeScript ("PRAGMA table_info(" + vecTables[ii] + ") ;");
      }
      for (int ii = 0; ii < vecViews.length; ii ++)
      {
         cliDB.writeScript ("SELECT \"   <tableInfo " + vecViews[ii] + ">\" ;");
         cliDB.writeScript ("PRAGMA table_info(" + vecViews[ii] + ") ;");
      }
      cliDB.closeScript ();
      String fout = fileUtil.createTemporal ();
      cliDB.setStdOutputFile (fout);

      // execute the query ...
      cliDB.runSQL (dbName);

      //The result should be a EvaUnit, let's load it
      EvaUnit euSqlRes = EvaFile.loadEvaUnit (fout, "data");
      if (euSqlRes == null)
      {
         log.err ("DATABASE", "SCHEMA: cannot read result from file [" + fout +  "].");
         return;
      }

      // Now fill the unique result variable with all tables info's
      //
      int tableCnt = 0;
      for (int ii = 0; ii < vecTables.length; ii ++)
      {
         Eva tiTab = euSqlRes.getEva ("tableInfo " + vecTables[ii]);
         if (tiTab == null)
         {
            log.err ("DATABASE", "SCHEMA: cannot read result for table " + vecTables[ii] +  ".");
            continue;
         }
         collect_fields (evaResult, tiTab, tableCnt++, "table", vecTables[ii]);
      }
      for (int ii = 0; ii < vecViews.length; ii ++)
      {
         Eva tiTab = euSqlRes.getEva ("tableInfo " + vecViews[ii]);
         if (tiTab == null)
         {
            log.err ("DATABASE", "SCHEMA: cannot read result for view " + vecViews[ii] +  ".");
            continue;
         }
         collect_fields (evaResult, tiTab, tableCnt++, "view", vecViews[ii]);
      }
   }

   private static void collect_fields (Eva evaResult, Eva infoTable, int tableCnt, String tableType, String tableName)
   {
      for (int jj = 0; jj < infoTable.rows (); jj ++)
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
               infoTable.getValue (jj, 0) + ", " +  // cid
               infoTable.getValue (jj, 1) + ", " +  // name
               infoTable.getValue (jj, 2) + ", " +  // type
               infoTable.getValue (jj, 3) + ", " +  // notnull
               infoTable.getValue (jj, 4) + ", " +  // dflt_value
               infoTable.getValue (jj, 5)           // pk
            ));
      }
   }
}
