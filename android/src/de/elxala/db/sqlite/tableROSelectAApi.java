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


/*
   same functionality as tableROSelectBatch
   see documentation in base/src/de/elxala/db/sqlite/tableROSelectBatch.java

*/

import java.util.*;
import android.database.Cursor;

import de.elxala.Eva.abstractTable.*;
import de.elxala.langutil.*;
import de.elxala.db.*;
import de.elxala.Eva.*;
import de.elxala.zServices.*;

/*
   is a TableModel for a general SELECT but read only
   the SELECT will be converted in a TEM VIEW

*/
public class tableROSelectAApi extends absTableWindowingEBS
{
//   private logger log = new logger (this, "de.elxala.db.sqlite.tableROSelect", null);

   public static final String sATTR_DB_DATABASE_NAME      = "dbName";
   public static final String sATTR_DB_SQL_SELECT_QUERY   = "sqlSelect";
   public static final String sATTR_DB_EXTRA_FILTER       = "sqlExtraFilter";

   private sqlSolver myDB = new sqlSolver ();  // database client caller

   public tableROSelectAApi (baseEBS ebs)
   {
      super (ebs);

      // if we have already data for the sql we execute it
      executeIfDataContainsSQL ();
   }

   /**
      Constructor to be used without setting data and control
      It is not appropiated to be used into zWidgets!
   */
   public tableROSelectAApi (String databaseFile, String SQLSelect)
   {
      super (new baseEBS ("default_tableROSelect", null, null));

      // data & control all in one
      EvaUnit myDataAndCtrl = new EvaUnit ();
      setNameDataAndControl (null, myDataAndCtrl, myDataAndCtrl);

      if (SQLSelect != null && SQLSelect.length() > 0)
         setSelectQuery (databaseFile, SQLSelect);
   }

// no podemos hacer esto porque sino sera'n llamados antes de la construccio'n de la misma clase ...

//   public void setNameDataAndControl (baseEBS ebs)
//   {
//      super.setNameDataAndControl (ebs);
//      executeIfDataContainsSQL ();
//   }
//
//   public void setNameDataAndControl (String name, EvaUnit pData, EvaUnit pControl)
//   {
//      super.setNameDataAndControl (name, pData, pControl);
//      executeIfDataContainsSQL ();
//   }

   public void dispose ()
   {
      // myDB.dispose ();
   }

   public void updateNameDataAndControl (baseEBS ebs)
   {
      super.copyEBS (ebs);
      executeIfDataContainsSQL ();
   }

   public void updateNameDataAndControl (String name, EvaUnit pData, EvaUnit pControl)
   {
      super.setNameDataAndControl (name, pData, pControl);
      executeIfDataContainsSQL ();
   }


   public String escapeString (String str)
   {
      return myDB.escapeString (str);
   }

   public String unEscapeString (String str)
   {
      return myDB.unEscapeString (str);
   }

   public void setSelectQuery (String databaseFile, String sqlSelect)
   {
      setSimpleAttribute(DATA, sATTR_DB_DATABASE_NAME, databaseFile);
      //(o) TODO_db Tables: if sqlQuery has more one line !!!
      //
      setSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY, sqlSelect);

      executeQuery ();
   }

   public void setSelectQuery (String sqlSelect)
   {
      //(o) TODO_db Tables: if sqlQuery has more one line !!!
      //
      setSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY, sqlSelect);

      executeQuery ();
   }

   private String getSomeHowDatabase ()
   {
      // try to find  dbName in attribute <.... dbName>
      //
      String dbName = getSimpleAttribute(DATA, sATTR_DB_DATABASE_NAME);
      return (dbName == null || dbName.length () == 0) ? sqlUtil.getGlobalDefaultDB (): dbName;
   }

   private void executeIfDataContainsSQL ()
   {
      Eva evaSqlSelect = getAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY);
      if (evaSqlSelect == null)
         return;

      executeQuery ();
   }

   /*
   */
   protected void executeQuery ()
   {
      String dbName = getSomeHowDatabase ();

      //(o) TODO_sqlite Deprecate "PRAGMA" support or do it through sqlPragma attribute!
      //    This variable only pretends check if the first word is PRAGMA or SELECT
      String sqlQ = getSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY).trim();
      if (sqlQ.length () < 6)
      {
         setTotalRecords (0); // ? es ok ? => test
         log.err ("tableROSelect.executeQuery", "Query does not start with SELECT or PRAGMA!");
         return;
      }

      String sqlStart6 = sqlQ.substring(0, 6);

      if (dbName == null || dbName.length () == 0)
      {
         log.err ("tableROSelect.executeQuery", "NO database name specified at all!");
         return;
      }
      if (! myDB.checkClient())
      {
         log.err ("tableROSelect.executeQuery", "sqlite3 could not be checked!");
         return;
      }

      /*
         NOTE: FOR sqlite ADMIT PRAGMAS as well
      */
      if (sqlStart6.equalsIgnoreCase ("PRAGMA"))
      {
         initCache (0);

         String sqlSelect = getSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY);

         myDB.getSQLCursor (dbName, sqlSelect);
         if (myDB.getLastCursor () == null)
         {
            log.err ("tableROSelect.executeQuery", "WRONG PRAGMA RETURN FOR QUERY (" + sqlSelect + ")");
            return;
         }
         setColumnNames (myDB.getLastCursor ().getColumnNames ());
         setTotalRecords (myDB.getLastCursor ().getCount ());
         obtainRow (0);
         myDB.closeDB ();
         /*
            sqlite PRAGMAS

               PRAGMA database_list;
               For each open database, invoke the callback function once with information about that database. Arguments include the index and the name the database was attached with. The first row will be for the main database. The second row will be for the database used to store temporary tables.

               PRAGMA foreign_key_list(table-name);
               For each foreign key that references a column in the argument table, invoke the callback function with information about that foreign key. The callback function will be invoked once for each column in each foreign key.

               PRAGMA index_info(index-name);
               For each column that the named index references, invoke the callback function once with information about that column, including the column name, and the column number.

               PRAGMA index_list(table-name);
               For each index on the named table, invoke the callback function once with information about that index. Arguments include the index name and a flag to indicate whether or not the index must be unique.

               PRAGMA table_info(table-name);
               For each column in the named table, invoke the callback function once with information about that column, including the column name, data type, whether or not the column can be NULL, and the default value for the column.

         */
      }
      else if (sqlStart6.equalsIgnoreCase("SELECT"))
      {
         initCache (0);
         setTotalRecords (0);
         QueryViewResult ();
         if (myDB.getLastCursor () != null)
              setTotalRecords (myDB.getLastCursor ().getCount ());
         obtainRow (0);
         myDB.closeDB ();
      }
      else
      {
         log.err ("tableROSelect.executeQuery", "sqlSelect MUST START either with SELECT or a PRAGMA! (" + sqlStart6 + ")");
         return;
      }
   }

   /**
      @brief executing the final select query (note: not for pragmas!) taking into account
             the possible previous sql queries, the real query (desired query) and the possible
             extra filter.
   */
   private void QueryViewResult ()
   {
      if (! myDB.openScript (false)) // we're sure we don't need transactions
      {
         log.severe ("tableROSelect.QueryViewResult", "db cannot be opened!");
         return;
      }

      //myDB.writeScript ("DROP VIEW " + VIEW_TEMP_NAME + ";");
      String dbName   = getSomeHowDatabase ();

      //from variable <... sqlSelect>
      //get the real query
      //
      Eva eRealQuery = getAttribute (DATA, false, sATTR_DB_SQL_SELECT_QUERY);
      if (eRealQuery == null)
      {
         log.err ("tableROSelect.QueryViewResult", "not found query in <" + getName() + " " + sATTR_DB_SQL_SELECT_QUERY + ">");
         return;
      }
      String realQuery = eRealQuery.getAsText ();
      log.dbg (2, "tableROSelect.QueryViewResult", "real Query [" + realQuery + "]");

      //from variable <... sqlExtraFilter>
      //get extra filter if any
      //
      Eva eExtraFilter  = getAttribute (DATA, false, sATTR_DB_EXTRA_FILTER);
      String extraFilter = "";
      if (eExtraFilter != null)
      {
         extraFilter = eExtraFilter.getAsText ();
         log.dbg (2, "tableROSelect.QueryViewResult", "extra filter [" + extraFilter + "]");
      }
      log.dbg (2, "tableROSelect.QueryViewResult", "final query [" + realQuery + " " + extraFilter + "]");

      // write the sql batch and execute it
      //
      myDB.writeScript (realQuery + " " + extraFilter);
      myDB.closeScript ();
      myDB.getSQLCursor (dbName);
      //myDB.closeDB ();
   }


   public void setExtraFilter (String extraFilter)
   {
      setSimpleAttribute(DATA, sATTR_DB_EXTRA_FILTER, extraFilter);
   }

   public void loadRowsFromOffset (int offsetRowStart)
   {
      int cantRows = 0;

      myDB.ensureOpenDB ();
      Cursor cur = myDB.getLastCursor ();

      setColumnNames (cur.getColumnNames ());
      log.dbg (4, "loadRowsFromOffset", "offsetRowStart = " + offsetRowStart + " getCount () = " + cur.getCount ());
      if (offsetRowStart < cur.getCount ())
      {
         initCache (getRecordOffset ());
         for (int rr = offsetRowStart; rr < cur.getCount() && cantRows < MAX_CACHE; rr ++)
         {
            log.dbg (5, "loadRowsFromOffset", "setting relative record " + cantRows + " from record " + rr);
            cur.moveToPosition(rr);
            setRelativeRecord (cantRows ++, decodeRecordAsArray (cur));
         }
      }
      myDB.closeDB ();
   }

   private String [] decodeRecordAsArray (Cursor cur)
   {
      if (cur == null) return new String [] {};
      if (cur.isBeforeFirst () ||  cur.isAfterLast ())
            return new String [] {};

      String [] arr = new String [cur.getColumnCount ()];
      log.dbg (4, "recordAsArray", "filling " + arr.length + " columns");
      for (int cc = 0; cc < arr.length; cc ++)
      {
         arr[cc] = utilEscapeStr.desEscapeStr (cur.getString (cc));
         log.dbg (5, "recordAsArray", "value " + arr[cc]);

         //(o) Android Calling sqlite with api, null values has to be avoided
         //    in java gastona (no android) does not exist this problem since the null
         //    values are represented as empty strings
         if (arr[cc] == null)
            arr[cc] = "";
      }

      return arr;
   }

   public void setValue (String aValue, int row, int col)
   {
      log.severe ("tableROSelect.setValue", "Want to set a read only value! (Note that viewTableModel is just a viewer)");
      // not possible!
   }

   /**
      Copies the whole data the Eva variable 'eva', or until 'limitRecords' if this is greater than 0.
      In order to check if the limit has been reached you can compare eva.rows() with getRecordCount ()
      or just supose that this has been reached if the value is high enough.

      @param eva eva variable where the current values are going to be copied
      @param withColumnNames if true copies also the colum names, if not the first record will start at row 0
      @param limitRecords If greater than 0 (0 = no limit!) will limit the copy to this number of records
   */
   public void copyDataToEva (Eva eva, boolean withColumnNames, int limitRecords)
   {
      int limit = getRecordCount ();
      if (limitRecords > 0 && limitRecords < limit)
         limit = limitRecords;

      eva.clear ();

      if (withColumnNames)
      {
         for (int cc = 0; cc < getColumnCount(); cc ++)
            eva.setValue (getColumnName(cc), 0, cc);
      }

      int off = withColumnNames ? 1:0;

      for (int ii = 0; ii < limit; ii ++)
      {
         for (int cc = 0; cc < getColumnCount(); cc ++)
            eva.setValue(getValue (ii, cc), ii+off, cc);
      }
   }
}
