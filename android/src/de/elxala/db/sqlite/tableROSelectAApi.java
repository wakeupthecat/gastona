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


// NOTA 29.06.2008 13:53: quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c

/*
   history
   =====================================================

   04.02.2007 18:36  version using de.elxala.Eva.abstractTable.absTableWindowEBS;
   28.02.2006 22:00  version ro (read only)
   05.06.2004 16:33  Crear EvaTableModel (basada en de.elxala.app.IProject.StrArrayTableModel)
   28.10.2002 20:53
                     a partir de un String [] inducimos un String [][]
                     teniendo en cuenta que los strings (de String[]) son elementos separados por
                     tabulador (\t) y que todos tienen las mismas columnas


   sqlite read only VIEW implementing AbstractTableModel
   =====================================================

   roViewTableModel accepts a sqlite database name and a sql SELECT query
   (also sqlite PRAGMAS are allowed) as input, and implements the interface
   AbstractTableModel, to be used by a JTable or whatever, with the result view
   (not meaning here sql view). Internally the class doesn't queries all the data
   from the select to the database but it queries the DB on demand of the TableModel.

   To do that roViewTableModel has a buffer of MAX_CACHE (240) records that works
   like a window of the whole view. Nevertheless there is a information needed
   from the beginning and it is the total number of records of the view, that is
   the most time costing operation and is performed by a SELECT COUNT(*) ...

   Thus, in a scenario where roViewTableModel is used by a JTable component, accesing
   a read only database and querying without limit a huge table, we would have only
   the cost of counting the records and getting the first 240 records. Of course if
   the table is scrolled up and down the needed queries will be performed but since
   the cache is relative small the delays are lightly apreciated.

   Implementation Problem:

      According to the above scenario, it would be desirable to procede as follows:

      suposing the query is "SELECT XYZ"
      and we are allowed to create a view called myView

      DROP VIEW myView;
      CREATE VIEW myView AS SELECT XYZ;

      --> now roViewTableModel query and stores the total number of records of the select
      SELECT COUNT(*) FROM myView;

      --> now roViewTableModel query and stores the first cache
      SELECT * FROM myView LIMIT 0, 240;


      to use a view is good for two reasons:

         p1) sqlite caappTEMPdbn optimize next acceses to the view in the case SELECT XYZ is a complex
             query
         p2) we dont have to modify the original query (SELECT XYZ) because we have assigned
             to it a name. Note that without view is not possible to simply add a
             LIMIT to the original query
                  "SELECT XYZ " + " LIMIT 0,240"
             because XYZ could contain also a LIMIT
             so we would have to parse the original query and handle all cases.


      so to use a view we can:

      1) use appTEMPdb which is the temporary database created automatically with each instance
         of clientCaller and attached to the main database automatically.
         THIS UNFORTUNATLY DOESN'T WORK BECAUSE SQL VIEWS CANNOT REFERENCE ATTACHED DATABASES

      2) use CREATE TEMP VIEW in the same database
         this is possible but only solves the problem p2.
         since clientCall starts ON EACH QUERY a sqlite client the temporary view
         would remain exactly one call! (too temporal to be used in next calls!)
         Due to this problem clientCaller offers the alternative solution of appTEMPdb.
         Nevertheless this technique is what we will use here for two reasons:

            - the code needed to solve p2 would be very tricky, ugly and not very robust
            - if in future versions of sqlite java interface we could use properly TEMP views and tables
              then the code wouldn't have to be changed very much


      3) use CREATE VIEW in the same database

         it would work but have serious disadvantages

            p3) The technique is intrusive with the target database (it writes on it when it shouldn't)
            p4) Not possible (or very difficult) to guarantee a unique view name in a concurrent scenario
            p5) Cannot be done with read only databases (CD-ROM etc)


   Castellano
   ----------------------------------------------------------------------------
   problematica views

      - PARA viewTableModel NOS INTERESARi'A USAR SQL VIEW POR DOS RAZONES
            p1- nos ahorramos el tener que controlar si en el SQL hay LIMIT o no
                y gestionarlo en consecuencia (parsearlo en todas sus posibilidades OFFSET etc)
            p2- seguramente sqlite puede optimizar el acceso en el caso de un sql complejo

      - NO PODEMOS CREAR LA VIEW EN NUESTRA appTEMPDB
          sqlite : una base de datos atachada no puede referenciar otras tablas con view

      - USAR VIEW EN LA MISMA BASE DE DATOS

          es la solucio'n actual pero con los problemas:
               p3- Tocamos la base de datos del colega
               p3- NO garantizamos nombre u'nico de la VIEW si la bd esta' mas concurrida
               p3- si la bd es read-only pech!

      - USAR CREATE TEMP VIEW

           soluciona los puntos anteriores pero notar que lo tenemos que hacer
           siempre puesto que con cada acceso de los nuestros el cliente se cierra y
           con e'l las tablas temporales!!!
           con lo cual so'lo solucionari'amos el problema p1




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
   public static final String sATTR_DB_PREVIOUS_TO_SELECT = "sqlPrevious";

   private sqlSolver myDB = new sqlSolver ();  // database client caller

   public tableROSelectAApi (baseEBS ebs)
   {
      super (ebs);

      // if we have already data for the sql we execute it
      executeIfDataContainsSQL ();
   }

   public tableROSelectAApi (String databaseFile, String SQLSelect)
   {
      super (new baseEBS ("default_tableROSelect", null, null));

      // data & control all in one
      EvaUnit myDataAndCtrl = new EvaUnit ();
      setNameDataAndControl (null, myDataAndCtrl, myDataAndCtrl);

      if (SQLSelect != null && SQLSelect.length() > 0)
         setSelectQuery (databaseFile, SQLSelect, "");
   }

   /**
      Constructor to be used without setting data and control
      It is not appropiated to be used into zWidgets!
   */
   public tableROSelectAApi (String databaseFile, String SQLSelect, String previousSQL)
   {
      super (new baseEBS ("default_tableROSelect", null, null));

      // data & control all in one
      EvaUnit myDataAndCtrl = new EvaUnit ();
      setNameDataAndControl (null, myDataAndCtrl, myDataAndCtrl);

      if (SQLSelect != null && SQLSelect.length() > 0)
         setSelectQuery (databaseFile, SQLSelect, previousSQL);
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
      setSelectQuery (databaseFile, sqlSelect, "");
   }

   public void setSelectQuery (String databaseFile, String sqlSelect, String previousSql)
   {
      setSimpleAttribute(DATA, sATTR_DB_DATABASE_NAME, databaseFile);
      //(o) TODO_db Tables: if sqlQuery has more one line !!!
      //
      setSimpleAttribute(DATA, sATTR_DB_SQL_SELECT_QUERY, sqlSelect);

      //setSimpleAttribute(DATA, sATTR_DB_PREVIOUS_TO_SELECT, previousSql);
      if (previousSql != null && previousSql.length () > 0)
         log.err ("tableROSelect.setSelectQuery", "attribute " + sATTR_DB_PREVIOUS_TO_SELECT + " is deprecated! [" + previousSql + "]");

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
      if (dbName == null || dbName.length () == 0)
      {
         dbName = sqlUtil.getGlobalDefaultDB ();
         if (dbName == null || dbName.length () == 0)
         {
            log.err ("tableROSelect.getSomeHowDatabase", "NO valid database name found!");
            return null;
         }
      }

      return dbName;
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
   public void executeQuery ()
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
         //iniciaSelect_NoOptimizado ();
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

      @param finalQuery usually one of these two:
               "SELECT count(*) FROM " + VIEW_TEMP_NAME + ";"
               "SELECT * FROM " + VIEW_TEMP_NAME + " LIMIT " + offsetRowStart + "," + MAX_CACHE + ";"

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

      //from variable <... sqlPrevious>
      //get previous to query if any (for instance "attach database ..." "create temp view..." etc)
      //

      Eva ePreviousQuery = getAttribute (DATA, false, sATTR_DB_PREVIOUS_TO_SELECT);
      if (ePreviousQuery != null && ePreviousQuery.rows () > 0)
         log.err ("tableROSelect.QueryViewResult", "attribute " + sATTR_DB_PREVIOUS_TO_SELECT + " is deprecated! [" + ePreviousQuery + "]");

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
