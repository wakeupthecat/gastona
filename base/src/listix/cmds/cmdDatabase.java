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

/*
   //(o) WelcomeGastona_source_listix_command DATABASE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       DATABASE
   <groupInfo>  data_db
   <javaClass>  listix.cmds.cmdDatabase
   <importance> 7
   <desc>       //Working with intern sqlite database

   <help>
      //
      // To create and poblate database tables from Eva variables (Eva tables) or to execute
      // any sql statement sequence. The underlaying database engine of Listix is sqlite
      // (visit www.sqlite.org and try http://www.sqlite.org/lang.html for a SQL syntax online help).
      // In this engine a database is simply an ordinary file (any extension may be used) that can
      // hold multiple tables and views.
      //
      // Creating tables from Eva variables:
      // -----------------------------------
      //
      //    It is a convenient way of creating tables if we have the data in some variable. For example:
      //
      //       <myTable>
      //             id, name       , desc
      //             01, Gastona    , Aplications in few lines
      //             02, Javaj      , GUI on the fly
      //
      //       <main>
      //          DATABASE, myDB.db, CREATE TABLE, myTable
      //
      //    Adding data is also simple. Text values with multiple lines can be formed
      //    using a separate variable
      //
      //
      //          ...
      //          DATABASE, myDB.db, ADD TO TABLE, myTable, moreValues
      //
      //       <moreValues>
      //             id, name       , desc
      //             03, Listix     , @<listix desc>
      //             04, Mesnaka    , An eficient messanger
      //
      //       <listix desc>
      //             //This is the story of listix:
      //             //Once upon a time ...
      //             //etc..
      //
      // Using freely SQL statments:
      // -----------------------------------
      //
      //    We might also create tables using directly SQL statments. This may be done
      //    in a simple way as well
      //
      //          DATABASE, myDB.db, EXECUTE, //CREATE TABLE myTable (id, name, desc);
      //
      //    or specifying more details if the application requires it
      //
      //       <main>
      //          DATABASE, myDB.db, EXECUTE, @<create all>
      //
      //       <create all>
      //          //CREATE TABLE myTable2
      //          //       (id INTEGER PRIMARY KEY,
      //          //        name,
      //          //        desc);
      //          //
      //          //CREATE INDEX ind1 ON myTable2 (name);
      //
      //    In general any SQL sequence can be performed using "DATABASE,,EXECUTE". In addition
      //    writing sql statements with listix, where the use of variables, formats and all listix
      //    commands is possible, is pretty like having stored procedures. For example, we can use
      //    variables in a query like in
      //
      //       <theTable> myTable
      //       <date1> 2009-01
      //       <date2> 2009-02
      //
      //       <xyz>
      //          LOOP, SQL, myDB.db, @<query>
      //          ...
      //
      //       <query>
      //          //SELECT *
      //          //    FROM @<theTable>
      //          //    WHERE date >= '@<date1>' AND date <= '@<date2>';
      //
      //
      //       or make it more general using parameters (command LISTIX)
      //
      //       <query>
      //          LISTIX, select between dates, myTable, 2009-01, 2009-06
      //
      //       <select between dates>
      //          //SELECT *
      //          //    FROM @<p1>
      //          //    WHERE date >= '@<p2>' AND date <= '@<p3>';
      //


   <aliases>
      alias
      DB
      BASEDATOS

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    7      , //Creates the table 'tableName' from the eva variable 'evaData', if not given 'evaData' then 'tableName' will be used as Eva name
         2   ,    7      , //Add values to the table 'tableName' from the eva variable 'evaData', if not given 'evaData' then 'tableName' will be used as Eva name
         3   ,    5      , //Executes the query sqlQuery on the sqlite database 'sqliteDBName'.
         4   ,    3      , //Extracts the schema of the database (only table structure) into an Eva variable (table) with the columns (id, tabType, tableName, columnCid, columnName, columnType, not_null, def_value, pk)

   <syntaxParams>
      synIndx, name           , defVal    , desc
         1   , sqliteDBName   , default db, //Database name (a file name)
         1   , CREATE (TABLE) ,           , //
         1   , tableName      ,           , //Table name to be created or to add records to
         1   , evaData        , same as tableName, //Variable Eva containing the column names and the data to insert into 'tableName', if not given it is suposed to be the same as 'tableName'

         2   , sqliteDBName   , default db, //Database name (a file name)
         2   , ADD (TO TABLE) ,           , //
         2   , tableName    ,             , //Table name to be created or to add records to
         2   , evaData      , same as tableName, //Variable Eva containing the column names and the data to insert into 'tableName', if not given it is suposed to be the same as 'tableName'

         3   , sqliteDBName , default db, //Database name (a file name)
         3   , EXECUTE      ,           , //
         3   , sqlQuery     ,           , //SQL query to be executed

         4   , sqliteDBName , default db, //Database name (a file name)
         4   , SCHEMA       ,           , //
         4   , evaData      ,           , //Eva variable name where to put the resultant schema (table of tabtype, name, cid, )


   <options>
      synIndx, optionName            , parameters, defVal, desc
          1  , CLEAN (PREVIOUS DATA) , 1 / 0    ,    0  , If true (1) removes previous data if exists
          1  , SOLVE LSX             , 1 / 0    ,    1  , If false (0) the values found in the source Eva will be insterted without any listix resolve (variables @<..>) at all
          2  , SOLVE LSX             , 1 / 0    ,    1  , If false (0) the values found in the source Eva will be insterted without any listix resolve (variables @<..>) at all

          3  , TRANSACTION           , 1 / 0    ,    1 , If false (0) the block with be executed without sqlite transactions (see sqlite transaction). Note : this option has no effect if option FROMFILE is given
          3  , FROM FILE             , fileNameWithSQLScript, "", If specified a filename this will be used by sqlite instead of any given query. Note that the file should contain the sqlite commands BEGIN TRANSACTION; END; for a fast execution
          3  , OUTPUT TO FILE        , outputFile           , "", File to print out the output of the execute command (sqlite output)
          3  , ERRORS TO FILE        , errOutputFile        , "", File to print out the error output of the execute command (sqlite errors). Note: it is still possible that some errors are printed out into the output file instead of the error file (due to the use of sqlite of the stdout and stderr streams)

   <examples>
      gastSample

      creating a table
<!      dynamic selects

   <creating a table>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "listix command DATABASE example", 200, 300
      //
      //#data#
      //
      //    <myData>
      //       id   , name
      //
      //       010  , Gastona
      //       020  , Javaj
      //       021  , zWidgets
      //       030  , Listix
      //       031  , Commands
      //       040  , Mensaka
      //       050  , Parsons
      //       060  , XMeLon
      //
      //
      //#listix#
      //
      //   <main>
      //      //Creating the table ...
      //      DATABASE, , CREATE TABLE, myData
      //      //done.
      //      //Viewing the table ...
      //      //
      //      LOOP, SQL, , //SELECT name FROM myData ORDER BY name DESC
      //          ,, @<name>
      //      //
      //      //
      //      //done.
      //


#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import java.io.File;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;


public class cmdDatabase implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "DATABASE",
          "DB",
          "BASEDATOS",
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      //      comm____  dbName__   oper______    par1_____   par2______
      //      DATABASE,         ,  CREATETABLE,  mieva
      //      DATABASE,         ,  ADDTABLE,     mieva,      mievaAdiciono
      //      DATABASE,         ,  EXECUTE,      query
      //      DATABASE,         ,  SCHEMA,       miEva
      //

      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String dbName    = cmd.getArg(0);
      String oper      = cmd.getArg(1);

      boolean optCreate  = cmd.meantConstantString (oper, new String [] { "CREATETABLE", "CREATE", "TABLE" });
      boolean optAdd     = cmd.meantConstantString (oper, new String [] { "ADDTABLE", "ADDTOTABLE", "ADD" });
      boolean optExecute = cmd.meantConstantString (oper, new String [] { "EXECUTE" });
      boolean optShema   = cmd.meantConstantString (oper, new String [] { "SCHEMA", "SCHEME", "ESQUEMA" });

      if (optCreate || optAdd || optExecute || optShema)
      {
         // ok
      }
      else
      {
         cmd.getLog().err ("DATABASE", "DATABASE operation [" + oper + "] not recognized! (while giving [" + dbName + "] as database name)");
         return 1;
      }

      //ensure directories if database name is specified
      //
      if (dbName.length () > 0)
      {
         File fill = fileUtil.getNewFile (dbName);

         if (optShema)
         {
            if (! fill.exists ())
            {
               cmd.getLog().err ("DATABASE", "Database [" + dbName + "] not found, cannot extract schema!");
               return 1;
            }
         }
         else
         {
            //(o) TOSEE_listix_cmds DATABASE, solve in a base utility (i.e. in de.elxala.langutil.filedir) creating necessary directories (mkdirs)
            java.io.File pare = fill.getParentFile ();

            if (pare != null)
            {
               // ensure dirs
               pare.mkdirs ();
            }
         }
      }

      if (optShema)
      {
         // DATABASE, dbName, SCHEMA, evaData
         if (!cmd.checkParamSize (3, 3)) return 1;
         obtainSchema (cmd);

         cmd.checkRemainingOptions ();
         return 1;
      }

      sqlSolver cliDB = new sqlSolver ();

      // ------> EXECUTE
      if (optExecute)
      {
         // DATABASE, dbName, EXECUTE, sqlQuery
         if (!cmd.checkParamSize (2, 3)) return 1;

         //look if option FROMFILE is given
         String  customFile = cmd.takeOptionString(new String [] { "FROMFILE", "FILE" }, "" );
         String  customOutFile = cmd.takeOptionString(new String [] { "OUTPUTTOFILE", "OUT", "OUTPUT", "TOFILE" }, "" );
         String  customErrFile = cmd.takeOptionString(new String [] { "ERRORSTOFILE", "ERRTOFILE" }, "" );

         if (customOutFile.length () > 0)
         {
            // ouput to file
            //
            cmd.getLog().dbg (2, "DATABASE", "option OUTPUTTOFILE = '" + customOutFile + "'");
            cliDB.setStdOutputFile (customOutFile);
         }

         if (customErrFile.length () > 0)
         {
            // ouput to file
            //
            cmd.getLog().dbg (2, "DATABASE", "option ERRORSTOFILE = '" + customErrFile + "'");
            cliDB.setErrOutputFile (customErrFile);
         }

         if (customFile.length () > 0)
         {
            // user script!
            //
            cmd.getLog().dbg (2, "DATABASE", "option FROMFILE = '" + customFile + "'");
            boolean withTransaction = "1".equals (cmd.takeOptionString(new String [] { "TRANSACTION" }, "0" ));   // DEFAULT 0! in FROM FILE
            cliDB.setInputScriptFile (customFile, withTransaction);
         }
         else
         {
            //   DATABASE,  dbFileName,  EXECUTE, query

            boolean withTransaction = "1".equals (cmd.takeOptionString(new String [] { "TRANSACTION" }, "1" ));   // DEFAULT 1 else
            // the query (argument 2)
            String querySQL = cmd.getArg(2);

            // create the script from the query
            //
            cliDB.openScript (withTransaction);
            cliDB.writeScript (querySQL);
            cliDB.closeScript ();
         }

         cliDB.runSQL ((dbName.length () > 0) ? dbName : that.getDefaultDBName ());
         cmd.checkRemainingOptions ();
         return 1;
      }

      // ------> CREATE | ADD
      //                   0        1           2           3        4           5
      //      DATABASE,  dbname,  ADDTABLE, tableName, [evaData], [evaUnit], [evaFile]

      String tableName = cmd.getArg(2);
      String evaName   = cmd.getArg(3);
      String evaUnit   = cmd.getArg(4);
      String evaFile   = cmd.getArg(5);

      //defaults
      if (evaName.length () == 0) evaName = tableName;
      if (tableName.length () == 0) tableName = evaName;

      if (evaName.length () == 0 && tableName.length () == 0)
      {
         cmd.getLog().err ("DATABASE", "DATABASE " + oper + " wrong arguments, missing tableName or evaData");
         return 1;
      }

      // obtain eva with data
      //
      Eva eva = null;
      if (evaUnit.length () == 0)
      {
         eva = cmd.getListix().getVarEva (evaName);
      }
      else
      {
         // comes from a file
         // must provide also evaFile!
         if (evaFile.length () != 0)
         {
            EvaUnit eu = EvaFile.loadEvaUnit (evaFile, evaUnit);
            eva = eu.getEva (evaName);
            if (eva == null)
            {
               cmd.getLog().err ("DATABASE", "eva [" + evaName + "] not found in #" + evaUnit + "# of [" + evaFile + "]");
               return 1;
            }
         }
         else
         {
            cmd.getLog().err ("DATABASE", "EvaUnit [" + evaUnit + "] specified but not file name given!");
            return 1;
         }
      }

      if (eva == null)
      {
         cmd.getLog().err ("DATABASE", "operation but eva [" + evaName + "] not found!");
         return 1;
      }
      //

      cliDB.openScript ();

      // create column-name list (see CREATE TABLE and INSERT INTO syntax)
      //
      StringBuffer columNameTextList = new StringBuffer ();
      StringBuffer columNameList = new StringBuffer ();
      for (int cc = 0; cc < eva.cols (0); cc ++)
      {
         columNameList.append (((cc > 0) ? ", ":"") + eva.getValue (0, cc));
         columNameTextList.append (((cc > 0) ? ", ":"") + eva.getValue (0, cc) + " text");
      }

      if (optCreate)
      {
         // DATABASE, dbName, CREATE, tableName, evaData
         if (!cmd.checkParamSize (3, 4)) return 1;
         //
         boolean preservePrevData = "0".equals (cmd.takeOptionString(new String [] { "CLEANPREVIOUSDATA", "CLEANDATA", "CLEAN", "CLEARPREVIOUSDATA", "CLEARDATA", "CLEAR" }, "" ));

         if (! preservePrevData)
            cliDB.writeScript ("DROP TABLE IF EXISTS " + tableName +  ";");

         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tableName +  " (" + columNameTextList.toString () + ");");
         cmd.getLog().dbg (2, "DATABASE", "CREATE TABLE " + tableName +  " (" + columNameTextList.toString () + ");");
      }

      if (optCreate || optAdd)
      {
         // DATABASE, dbName, CREATE, tableName, evaData
         if (!cmd.checkParamSize (3, 4)) return 1;

         //(o) TODO_remove old compatibility
         boolean solveLsx = true;
         if ("0".equals (cmd.takeOptionString(new String [] { "RAWDATA", "NOSOLVE", "NOTSOLVE", "NOLSXSOLVE", "NOLISTIXSOLVE" }, "0" )))
            solveLsx = false;
         if ("0".equals (cmd.takeOptionString(new String [] { "SOLVE", "SOLVELSX", "SOLVELISTIX" }, "?" )))
            solveLsx = true;

         // boolean rawData = "1".equals (cmd.takeOptionString(new String [] { "RAWDATA", "NOSOLVE", "NOTSOLVE", "NOLSXSOLVE", "NOLISTIXSOLVE" }, "0" ));

         int nFields = eva.cols (0);   // nfields is equal for all rows !!

         // populate a db table with the contents of a eva
         //
         for (int rr = 1; rr < eva.rows (); rr ++)
         {
            StringBuffer valueList = new StringBuffer ();
            for (int cc = 0; cc < nFields; cc ++)
            {
               // get raw value
               String svalue = eva.getValue (rr, cc);

               // solved if needed
               if (solveLsx)
                  svalue = cmd.getListix().solveStrAsString (svalue);

               // escape returns etc
               valueList.append (((cc > 0) ? ", ":"") + "'" + cliDB.escapeString(svalue) + "'");
            }

            String sql = "INSERT INTO " + tableName +  " (" + columNameList.toString () + ") VALUES (" + valueList.toString () + ");";

            cliDB.writeScript (sql);
            cmd.getLog().dbg (2, "DATABASE", sql);
         }
      }
      cliDB.closeScript ();
      cliDB.runSQL ((dbName.length () > 0) ? dbName : that.getDefaultDBName ());

      cmd.checkRemainingOptions ();
      return 1;
   }

   protected void obtainSchema (listixCmdStruct cmd)
   {
      // Syntax command:
      //      DATABASE,  dbName,  SCHEMA,     miEva
      //
      String dbName  = cmd.getArg(0);

      if (dbName.length () == 0)
         dbName = cmd.getListix().getDefaultDBName ();

      // Eva result
      //
      String evaName = cmd.getArg(2);
      if (evaName.length () == 0)
         evaName = "DBSchema of " + dbName;

      Eva evaResult = cmd.getListix().getSomeHowVarEva (evaName);

      sqlSolver cliDB = new sqlSolver ();
      cliDB.getSchema (dbName, evaResult);
   }
}
