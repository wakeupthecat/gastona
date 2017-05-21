/*
library de.elxala
Copyright (C) 2005, 2017 Alejandro Xalabarder Aulet

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
   sqlite read only VIEW implementing AbstractTableModel
   =====================================================

   roViewTableModel accepts a sqlite database name and a sql SELECT query
   (also sqlite PRAGMAS are allowed) as input, and implements the interface
   AbstractTableModel, to be used by a JTable or whatever other component.

   Internally the class doesn't request all the data from the select to the database
   but it queries the DB on demand of the TableModel.

   To do that roViewTableModel has a buffer of MAX_CACHE (240) records that works
   like a window of the whole view. However the total number of records of the select
   is calculated performing a SELECT COUNT(*) ...

   Thus, in a scenario where roViewTableModel is used by a JTable component, accesing
   a read only database and querying without limit a huge table, we would have only
   the cost of counting the records and getting the first 240 records. Of course if
   the table is scrolled up and down the needed queries will be performed but since
   the cache is relative small the delays are lightly apreciated.


   Main methods


      public void setSelectQuery (String databaseFile, String sqlSelect, String extraFilter)
      public void setSelectQuery (String databaseFile, String sqlSelect)
      public void setSelectQuery (String sqlSelect)
      public void setExtraFilter (String extraFilter)
      public void copyDataToEva (Eva eva, boolean withColumnNames, int limitRecords)

      public int getColumnCount ()
      public int getColumnIndex (colName)
      public String getColumnName (int col)

      public String getValue (int row, int col)
      public String getValue (String columnName, int row)

      public String escapeString (String str)
      public String unEscapeString (String str)


   Example1:

         tableROSelect tabRO = new tableROSelect ("mydb.db", "SELECT * FROM myTable WHERE id < 10000");

         for (int rr = 0; rr < tabRO.getRecordCount (); rr ++)
         {
            // accessing one specific column by name
            //
            out (tabRO.getValue ("name", rr));

            // looping all columns
            //
            for (int cc = 0; cc < tabRO.getColumnCount (); cc ++)
               out (tabRO.getColumnName (cc) + " = " + tabRO.getValue (rr, cc));
         }

   Example2:

      tableROSelect tabRO = new tableROSelect ();

      // first set extra filter because setSelectQuery performs the query!
      //
      tabRO.setExtraFilter ("WHERE price > 100 and price < 1000");
      tabRO.setSelectQuery ("mydb.db", "SELECT * FROM myTable WHERE id < 10000");

      ... access the data



      FROM C:\Users\wakeupthecat\Dropbox\GASTONASRC\base\src\de\elxala\Eva\abstractTable\absTableWindowingEBS.java
              C:\Users\wakeupthecat\Dropbox\GASTONASRC\base\src\de\elxala\Eva\abstractTable\tableEvaDataEBS.java

            // return value or "" or "?1" or "?2"
            public String getValue (int row, int col)

            // return value or "" or "?1" or "?2"
            public String getValue (String columnName, int row)

            getColumnCount
            getColumnIndex (colName);



   Implementation details:

      given a sql Select originalSelect and a extra filter expression as input

      for example

            originalSelect  = "SELECT name, product FROM products WHERE year+0 > 2000"
            extraFilter     = "WHERE name >= 'A' AND name < 'B'"

      the final select is formed as

         FINAL_SELECT = "SELECT * FROM (" + originalSelect + ") " + extraFilter;

      then the total number of rows is calculated with

         "SELECT count(*) as n FROM (" + FINAL_SELECT + ")"

      a specific window from offsetRowStart and length MAX_CACHE can be retrieved using

         "SELECT * FROM (" + FINAL_SELECT + ") LIMIT " + offsetRowStart + "," + MAX_CACHE + ";";


   EBS class diagram
   ----------------------------------------------------------------------------------


                   baseEBS(*e)
           -----------------------------------
            ^                             ^
            |                             |
            |                             |
      widgetEBS(*w)                      tableEvaDataEBS(*e)
   ----------------------          ------------------------------
            ^                          ^                 ^
            |                          |                 |
   used by almost all            tableEvaDB(*e)   absTableWindowingEBS(*e)
   zWidgets except those                           -----------------------------
   which data is table based                             ^                 ^
                                                         |                 |
                                                         |                 |
                                                   tableROSelect(*s)   tableWidgetBaseEBS(*t)
                                                                       ---------------------
                                                                           ^
                                                                           |
                                                                       tableEBS(*t)



            from package ...
      (*e) de.elxala.Eva.abstractTable
      (*w) javaj.widgets.basics
      (*t) javaj.widgets.table
      (*s) de.elxala.db.sqlite



*/

import java.util.*;
import de.elxala.Eva.abstractTable.*;
import de.elxala.langutil.*;
import de.elxala.db.*;
import de.elxala.Eva.*;
import de.elxala.zServices.*;

/*
   is a TableModel for a general SELECT but read only
   the SELECT will be converted in a TEM VIEW

*/
public class tableROSelectBatch extends absTableWindowingEBS
{
//   private logger log = new logger (this, "de.elxala.db.sqlite.tableROSelect", null);

   public static final String sATTR_DB_DATABASE_NAME      = "dbName";
   public static final String sATTR_DB_SQL_SELECT_QUERY   = "sqlSelect";
   public static final String sATTR_DB_EXTRA_FILTER       = "sqlExtraFilter";

   private sqlSolver myDB = new sqlSolver ();  // database client caller

   private String CURRENT_SELECT = "";

   public tableROSelectBatch ()
   {
      super (new baseEBS ("default_tableROSelect", null, null));

      // data & control all in one
      EvaUnit myDataAndCtrl = new EvaUnit ();
      setNameDataAndControl (null, myDataAndCtrl, myDataAndCtrl);
   }

   public tableROSelectBatch (baseEBS ebs)
   {
      super (ebs);

      // if we have already data for the sql we execute it
      executeIfDataContainsSQL ();
   }

   public tableROSelectBatch (String databaseFile, String SQLSelect)
   {
      super (new baseEBS ("default_tableROSelect", null, null));

      // data & control all in one
      EvaUnit myDataAndCtrl = new EvaUnit ();
      setNameDataAndControl (null, myDataAndCtrl, myDataAndCtrl);

      if (databaseFile != null)
         setSimpleAttribute(DATA, sATTR_DB_DATABASE_NAME, databaseFile);

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

   public void setSelectQuery (String databaseFile, String sqlSelect, String extraFilter)
   {
      // first extraFilter !
      setExtraFilter (extraFilter);
      setSelectQuery (databaseFile, sqlSelect);
   }

   public void setSelectQuery (String databaseFile, String sqlSelect)
   {
      setSimpleAttribute(DATA, sATTR_DB_DATABASE_NAME, databaseFile);
      setSelectQuery (sqlSelect);
   }

   public void setSelectQuery (String sqlSelect)
   {
      if (sqlSelect != null)
      {
         setSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY, sqlSelect);
         executeQuery ();
      }
   }

   public void setExtraFilter (String extraFilter)
   {
      if (extraFilter != null)
         setSimpleAttribute(DATA, sATTR_DB_EXTRA_FILTER, extraFilter);
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
         log.severe ("tableROSelect.executeQuery", "sqlite3 could not be checked!");
         return;
      }

      /*
         NOTE: FOR sqlite ADMIT PRAGMAS as well
      */
      if (sqlStart6.equalsIgnoreCase ("PRAGMA"))
      {
         String sqlSelect = getSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY);
         List resultPragma = myDB.getSQL (dbName, sqlSelect);
         if (resultPragma.size() <= 0)
         {
            log.err ("tableROSelect.executeQuery", "WRONG PRAGMA RETURN FOR QUERY (" + sqlSelect + ")");
            return;
         }

         setColumnNames (((String)resultPragma.get(0)).split ("\\|"));  // character |

         int totalRows = 0;
         //System.err.println ("resultPragma[0] [" + resultPragma[0] + "]");

         initCache (0);
         for (int rr = 1; rr < resultPragma.size (); rr ++)
         {
            setRelativeCompacted (totalRows ++, (String)resultPragma.get(rr), 0);
            //System.err.println ("resultPragma[" + rr + "] [" + resultPragma[rr] + "]");
         }

         setTotalRecords (totalRows);

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
         iniciaSelect ();
      }
      else
      {
         log.err ("tableROSelect.executeQuery", "sqlSelect MUST START either with SELECT or a PRAGMA! (" + sqlStart6 + ")");
         return;
      }
   }


   private void iniciaSelect ()
   {
      // important call to set CURRENT_QUERY !
      if (! setCurrentQuery ())
      {
         setTotalRecords (0);
         return;
      }

      // === OPTIMIZACION LEER PRIMERO HASTA MAX_CACHE

      setTotalRecords (MAX_CACHE + 1); // asuming
      initCache (0);
      obtainRow (0); // << this will perform the query for the first rows

      int tengo = getCachedRecordCount ();
      if (tengo < MAX_CACHE)
      {
         // not necessary to get the count with SELECT COUNT
         setTotalRecords (tengo);
      }
      else
      {
         List rescount = QueryViewResult ("SELECT count(*) as n FROM " + CURRENT_SELECT + ";");
         int size = rescount.size () < 2 ? 0: stdlib.atoi ((String) rescount.get(1));
         setTotalRecords (size);
      }
   }

   /**
      set data to be processed by expandRow
   */
   protected void setRelativeCompacted (int relativeRow, String compactedRow, int offsetColumn)
   {
      //(o) TODO_elxala_db_sqlite remove this dependency on de.elxala.db
      //          also make the compact-expand policy more flexible
      //
      String arr[] = myDB.expandRow (compactedRow, offsetColumn);

      setRelativeRecord (relativeRow, arr);
   }

   protected boolean setCurrentQuery ()
   {
      CURRENT_SELECT = "";
      //from variable <... sqlSelect>
      //get the real query
      //
      Eva eRealQuery = getAttribute (DATA, false, sATTR_DB_SQL_SELECT_QUERY);
      if (eRealQuery == null)
      {
         log.err ("tableROSelect.QueryViewResult", "not found query in <" + getName() + " " + sATTR_DB_SQL_SELECT_QUERY + ">");
         return false;
      }
      String realQuery = eRealQuery.getAsText ();
      log.dbg (2, "tableROSelect.QueryViewResult", "real Query [" + realQuery + "]");

      // remove last ';' !!
      while (realQuery.length () > 0 && realQuery.endsWith (";") || realQuery.endsWith (" ") || realQuery.endsWith ("\t"))
         realQuery = realQuery.substring (0, realQuery.length ()-1);


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

      //Note 2017.02.11
      //   probably " AS _nonameX" is not needed (TODO: check it with postgresSQL etc)
      //
      //    CURRENT_SELECT = "(SELECT * FROM (" + realQuery + ") " + extraFilter + ")";

      CURRENT_SELECT = "(" + realQuery + ") AS _noname1";
      if (extraFilter.length () > 0)
         CURRENT_SELECT = "(SELECT * FROM " + CURRENT_SELECT + " " + extraFilter + ") AS _noname2";

      log.dbg (2, "tableROSelect.QueryViewResult", "current select [" + CURRENT_SELECT + "]");
      return true;
   }


   /**
      @brief executing the final select query (note: not for pragmas!) taking into account
             the possible previous sql queries, the real query (desired query) and the possible
             extra filter.
   */
   private List QueryViewResult (String finalQuery)
   {
      if (! myDB.openScript (false)) // we're sure we don't need transactions
      {
         log.severe ("tableROSelect.QueryViewResult", "db cannot be opened!");
         return new Vector ();
      }

      myDB.writeScript (finalQuery);
      myDB.closeScript ();
      return myDB.getSQL (getSomeHowDatabase ());
   }


   public void loadRowsFromOffset (int offsetRowStart)
   {
      // 29.05.2009 Facilitating clientCaller SQLlite error detection
      //       One problem of parsing the sqlite output is that a record could contain the pattern string
      //       (for example "SQL error"), this would confuse the error detection.
      //       To avoid this we request a dummy column at the beginning, note that the name of this column (rowNull) has to be
      //       forbidden like rowId etc
      // (see also de\elxala\db\sqlite\roViewTableModel.java loadRowsFromOffset)

      int cantRows = 0;
      int OFFSET_COLUMN = 1; // due to rowNull
      String theQuery = "SELECT '' AS rowNull,* FROM " + CURRENT_SELECT + " LIMIT " + offsetRowStart + "," + MAX_CACHE + ";";

      log.dbg (2, "loadRowsFromOffset", "query:[" + theQuery + "]");
      List result = QueryViewResult (theQuery);
      log.dbg (2, "loadRowsFromOffset", "query result = " + result.size() + " elements");

      if (result.size () > 0)
      {
         //header (column names)
         //
         EvaLine eliHead = new EvaLine (((String)result.get(0)).split ("\\|"));
         eliHead.removeColumn (0); // due to rowNull
         setColumnNames (eliHead.getColumnArray ());

         initCache (getRecordOffset ());
         for (int rr = 1; rr < result.size() && cantRows < MAX_CACHE; rr ++)
         {
            String str = (String)result.get(rr);
            if (str.length () == 0) continue; //(o) TOSEE_elxala_db para que era este workaround?
            setRelativeCompacted (cantRows ++, str, OFFSET_COLUMN);
         }
      }
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
