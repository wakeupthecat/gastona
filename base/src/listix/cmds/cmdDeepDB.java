/*
library listix (www.listix.org)
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command DEEP DB

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       DEEP DB
   <groupInfo>  data_db
   <javaClass>  listix.cmds.cmdDeepDB
   <importance> 6
   <desc>       //Database sql facility

   <help>
      //
      // DEEP DB command extremely facilitates joining tables in a database. Not all kind
      // of joins but probably the most useful ones. For that is used the concept of "Deep Table"
      // that is introduced in the article https://www.codeproject.com/Articles/75601/The-Deep-Table.
      //
      // Two things are the key of this deep table approach: connections between tables and connected column or deep column.
      //
      // --- Connections
      //
      // A connection has a name and defines how two tables are connected. The concept is similar to the so called
      // FOREING KEY with the difference that foreing keys are used as constraints while connection as help
      // for select queries, which is actually a feature not a pure restriction.
      //
      // Example:
      //
      // suppose we have these two tables
      //
      //        table tabAuthor (auID, name, birthDate, countryCode)
      //        table tabWork   (workID, authorId, name, date, description)
      //
      // we can define the connection
      //
      //       author:  tabWork (authorId) <-> tabAuthor (auID)
      //
      // --- Deep column
      //
      // Given a table that has defined some connection ConnName then a deep column of this table
      // is expressed as
      //       ConnName columnConnectedTable
      //
      // For example in the previous example, we can express following connected columns
      //
      //       author name
      //       author birthDate
      //       author countryCode
      //
      // but if tabAuthor had a connection, for example with a table tabCoutry (cCode, name) called country
      // we make a deeper column like
      //
      //       author country name
      //
      // --- Using DEEP DB to generate a Deep SQL
      //
      // With the sample tables
      //
      //        TABLE tabCountry (cCode, name)
      //        TABLE tabAuthor  (auID, name, birthDate, countryCode)
      //        TABLE tabWork    (workID, authorId, name, date, description)
      //
      // following DEEP DB command
      //
      //      DEEP DB, SELECT, tabWork
      //             , CONNECTION, author, tabWork, authorId, tabAuthor, auID
      //             , CONNECTION, country, tabAuthor, countryCode, tabCountry, cCode
      //             ,           , workID
      //             ,           , name
      //             ,           , date
      //             ,           , authorId
      //             ,           , author name
      //             ,           , author country name
      //
      // (default option is "DEEP COLUMN" therefore may be set empty)
      //
      // would generate the select query
      //
      //       SELECT
      //          tabWork.workID AS workID
      //          , tabWork.name AS name
      //          , tabWork.name AS date
      //          , tabWork.authorId AS authorId
      //          , author.name AS author_name
      //          , author_country.name AS author_country_name
      //       FROM
      //          tabWork
      //          , tabAuthor AS author
      //          , tabCountry AS author_country
      //       WHERE
      //          tabWork.authorId == author.auID
      //           AND author.countryCode == author_country.cCode
      //
      // a sample output from that SELECT could be something like
      //
      //          workID | name            | date   | authorId | author_name     | author_country_name
      //          -------|-----------------|--------|----------|-----------------|--------------------
      //           8402  | The man who sold| 1970   | 1023     | David Bowie     | UK
      //           4318  | Dioptria        | 1970   | 1200     | Pau Riba        | CAT
      //           7211  | Horses          | 1975   | 1691     | Patti Smith     | US
      //
      // --- Other functions
      //
      // In the example we provide the connections directly but they can be persist in the database as well
      // For the example we could set the connections as follows
      //
      //       DEEP DB, SET CONNECTIONS, dbname.db
      //              , CONNECTION, author, tabWork, authorId, tabAuthor, auID
      //              , CONNECTION, country, tabAuthor, countryCode, tabCountry, cCode
      //
      // then we could write the previous DEEP DB SELECT as follows
      //
      //      DEEP DB, SELECT, tabWork
      //             , DBNAME, dbname.db
      //             , COLUMNS, workID, name, date, date, authorId, author name, author country name
      //

   <aliases>
      alias
      DEEP
      DBDEEP
      DBMORE2

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Build a select sql from a list of "deep" columns using table connections
         2   ,    3      , //Load stored connections from a database (from the table __dbMore_connections)
         3   ,    3      , //Save a list of connections in a database (into the table __dbMore_connections)
         4   ,    3      , //Returns a list of all possible deep columns from databse or from a specific table

   <syntaxParams>
      synIndx, name           , defVal    , desc
         1   , GET SELECT     ,           ,
         1   , baseTable      ,           , //Table base of the deep select
         1   , aliasBaseTable ,           , //If given it is used as alias for the first table

         2   , GET CONNECTIONS  ,          ,
         2   , dbName           ,          , //Database from where the connections has to be loaded from the table __dbMore_connections
         2   , EvaConnectionList,          , //Eva name of the variable target
         2   , withHeader       ,  0       , //If 1 column names will also retrieved (in the row 0 of the eva variable)

         3   , SET CONNECTIONS  ,          ,
         3   , dbName           ,          , //Database where the connections has to be stored in the table __dbMore_connections

         4   , DEEP SCHEMA     ,
         4   , varForDeepShema , EvaName   , //Variable name for the resulting deep schema
         4   , EvaSchemaDB     ,           , //Schema of the database (e.g. obtained with DATABASE SCHEMA)
         4   , EvaConnectionList,          , //Eva name of the variable containing the connection table definitions (connName, sourceTable, sourceKey, targetTable,  targetKey)
         4   , tableName       , ""         , //If given, it limits retrieved deep columns to the table 'tableName'

   <options>
      synIndx, optionName     , parameters       , defVal, desc
            1, DBNAME         , dbName           ,       , If given and EvaConnectionList is NOT given the connections will be retrieve from the table __dbMore_connections of the database
            1, VARCONNECTIONS , EvaConnectionList,       , Eva name of the variable containing the the connection table definitions (connName, sourceTable, sourceKey, targetTable,  targetKey)
            1, CONNECTION     , "connName, sourceTable, sourceKey, targetTable, targetKey",, Connection given to be stored. A connection that requires more than one key columns can be expressed using more lines with the option CONNECTION
            1, LINE BREAK     , 0 / 1            , 1     , If set to 0 it returns a string with no line breaks
            1, VAR DEEP COLUMNS, EvaDeepColumns  ,       , Eva name of the variable containing the the deep columns in the format : "connection connection ... field", [alias]
            1, DEEP COLUMN    , "deep column, alias",    , //Deep column in the format "connection connection ... field" and optionally an alias, this is the default option so "DEEP COLUMN" can be omitted
            1, DEEP COLUMN LIST, "deep column, deep column, ...",  , //Comma separated deep column list in format "connection connection ... field", no alias can be given in this option
            1, DEEP COL HEADER, 0 / 1            , 0     , If set to 1 headers are expected in the first row of EvaDeepColList (actually the first row will be ignored/skiped)
            1, CONNECTION HEADER, 0 / 1          , 0     , If set to 1 headers are expected in the first row of EvaConnectionList (actually the first row will be ignored/skiped)

            1, NO QUOTATION, 0 / 1, 0  , //Set it to 1 to not quote column names. Note that then names like "count", "order" etc will not be allow as column names
            1, SIMPLEST, 0 / 1, 0  ,     //Set it to 1 to get a simpler SELECT query, actually it only can be simplified if there are no deep columns (no connection are used)

   <examples>
      gastSample

      DeepDB example
      DeepDB twoSyntaxes
      DeepDB deeper example
      DeepDB recursive demo

   <DeepDB example>
      //#javaj#
      //
      //   <frames> main, Example of DEEP DB for joining two tables
      //
      //   <layout of main>
      //       EVA, 4, 4, 2, 2
      //
      //          , X
      //        X , sResult
      //        X , oConsole
      //
      //#data#
      //
      //   <tabArtists>
      //       id, name
      //       10, Rod Stewart
      //       14, Tom Waits
      //       39, Jaume Sisa
      //
      //    <tabSongs>
      //       artId , title
      //       14    , Downtown train
      //       39    , Tres cavalls
      //       14    , In the Neighbouhood
      //       10    , Da ya think I'm sexy
      //       10    , Downtown train
      //
      //#listix#
      //
      //   <main>
      //      DB,, CREATE TABLE, tabArtists
      //      DB,, CREATE TABLE, tabSongs
      //      -->, sResult data!, sqlSelect, @<deepSQL>
      //      //SQL generated by DEEP DB:
      //      //
      //      //@<sResult sqlSelect>
      //
      //   <deepSQL>
      //      DEEP DB, SELECT, tabSongs
      //             , CONN, artist, tabSongs, artId, tabArtists, id
      //             ,     , artId
      //             ,     , artist name
      //             ,     , title
      //

   <DeepDB twoSyntaxes>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "listix command DEEP DB example", 200, 300
      //
      //#listix#
      //
      //   <main0>
      //      // generated SELECT using the standard syntax
      //      //
      //      DEEP DB, SELECT, tabOrder
      //             , CONN, product, tabOrder, prodID, tabProd, ID
      //             ,     , prodID
      //             ,     , product name
      //             ,     , product price, catalogPrice
      //             ,     , quantity
      //      //
      //      //
      //      // generated SELECT using the compact form
      //      //
      //      DEEP DB, SELECT, tabOrder
      //             , CONN  , product, tabOrder, prodID, tabProd, ID
      //             , FIELDS, prodID, product name, product price, quantity

   <DeepDB deeper example>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "listix command DEEP DB - deeper - example", 200, 300
      //
      //#listix#
      //
      //   <main0>
      //      // generated SELECT containg two joins
      //      //
      //      DEEP DB, SELECT, tabOrder
      //             , CONN, product, tabOrder, prodID, tabProd, ID
      //             , CONN, country, tabProd , countryID, tabCountry, IDAlfa3
      //             ,     , prodID
      //             ,     , product name
      //             ,     , product price
      //             ,     , product country name
      //             ,     , quantity

   <DeepDB recursive demo>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "listix command DEEP DB recursive demo"
      //
      //#data#
      //
      //   <tabPeople>
      //     ID, fatherID, motherID, name
      //      0,        0,        0, -
      //      1,        0,        0, Pampa
      //      3,        0,        0, Recaredo
      //      4,        0,        0, Sivilia
      //      5,        0,        0, Guanche
      //      6,        3,        1, Eva
      //      7,        5,        4, Rulo
      //      8,        7,        6, Gastona
      //
      //#listix#
      //
      //  <main0>
      //     DB,, CREATE TABLE, tabPeople
      //     // ============  Original table:
      //     //
      //     //
      //     LOOP, SQL,, //SELECT * FROM tabPeople
      //         ,HEAD, @<OUT_COLUMNAMES>
      //         ,, @<OUT_COLUMVALUES>
      //     //
      //     //
      //     // ============  Deep SQL SELECT:
      //     //
      //     //@<DEEP_SQL>
      //     //
      //     //
      //     // ============  Deep records:
      //     //
      //     //
      //     LOOP, SQL,, //SELECT * FROM (@<DEEP_SQL>) WHERE fatherID != '00' OR motherID != '00'
      //         ,HEAD, @<OUT_COLUMNAMES>
      //         ,, @<OUT_COLUMVALUES>
      //
      //   <OUT_COLUMNAMES>
      //      LOOP, COLUMNS
      //          , LINK, ", "
      //          ,, @<columnName>
      //      //
      //      //
      //
      //   <OUT_COLUMVALUES>
      //      LOOP, COLUMNS
      //          , LINK, ", "
      //          ,, @<columnValue>
      //
      //   <DEEP_SQL>
      //      DEEP DB, SELECT, tabPeople
      //             , CONN, father, tabPeople, fatherID, tabPeople, ID
      //             , CONN, mother, tabPeople, motherID, tabPeople, ID
      //             ,     , ID
      //             ,     , fatherID
      //             ,     , motherID
      //             ,     , name
      //             ,     , father name
      //             ,     , mother name
      //             ,     , mother father name, yayo
      //             ,     , mother mother name, yaya
      //             ,     , father father name, avi
      //             ,     , father mother name, avia
      //
      //#**#


#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.dbMore.*;
import de.elxala.db.sqlite.*;


public class cmdDeepDB implements commandable
{


   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "DEEPDB",
          "DBDEEP",
          "DEEP",
          "DEEPSQL",
          "DBMORE2",
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
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper         = cmd.getArg(0);

      boolean optTranslate  = cmd.meantConstantString (oper, new String [] { "SELECT", "GETSELECT", "DEEPSELECT", "DEEPSQL", "TOSQL" });
      boolean optLoadConn   = cmd.meantConstantString (oper, new String [] { "GETCONNECTIONS", "GETCONN", "LOADCONNECTIONS", "LOADCONN" });
      boolean optSaveConn   = cmd.meantConstantString (oper, new String [] { "SETCONNECTIONS", "SETCONN", "SAVECONNECTIONS", "SAVECONN" });
      boolean optDeepSchema = cmd.meantConstantString (oper, new String [] { "DEEPSCHEMA", "DEEPCOLUMNS", "PLUSSCHEMA", "EXTENDEDSCHEMA" });

      if (optTranslate)       optionDeepSQL (cmd);
      else if (optLoadConn)   optionLoadConnections (cmd);
      else if (optSaveConn)   optionSaveConnections (cmd);
      else if (optDeepSchema) optionDeepSchema (cmd);
      else
      {
         cmd.getLog().err ("DEEPDB", "DEEPDB operation [" + oper + "] not recognized!");
         return 1;
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

   protected void optionLoadConnections (listixCmdStruct cmd)
   {
      //      comm____  oper______
      //      DEEPDB  , LOAD CONN , dbName, EvaConnectionList, withHeader
      //
      String  dbName     = cmd.getArg(1);
      String  tabConnTab = cmd.getArg(2);
      boolean withHeader = cmd.getArg(3).equals("1");

      Eva theVar = cmd.getListix().getSomeHowVarEva (tabConnTab);
      loadConnections (cmd, dbName, theVar, withHeader);
   }

   protected void loadConnections (listixCmdStruct cmd, String dbName, Eva evavar, boolean addHeader)
   {
      //(o) DBMore_Notes the connection table name and its column names are here hardcoded

      sqlSolver db = new sqlSolver ();
      String [] connTab = db.getTables (dbName, "name == '" + deepSqlUtil.CONNECTION_TABLE_NAME + "'");
      if (connTab.length > 0)
      {
         // obtain evaconnectionlist from database
         // NOTE: the "order by" selecting connections can be another one but important is that both sourceTable and connName
         //       are included
         //
         tableROSelect myRO = new tableROSelect (dbName, "SELECT * FROM " + deepSqlUtil.CONNECTION_TABLE_NAME + " ORDER BY sourceTable, connName;");
         myRO.copyDataToEva(evavar, addHeader, 3000); // no limit?

         if (evavar.rows () >= 3000)
            cmd.getLog().err ("DEEPDB", "Too many connections found (" + myRO.getRecordCount() + "), only using the first 3000!");
      }
      else
      {
         if (addHeader)
            evavar.addLine (new EvaLine ("connName, sourceTable, sourceKey, targetTable, targetKey"));
      }
   }

   protected void optionSaveConnections (listixCmdStruct cmd)
   {
      //      comm____  oper______
      //      DEEPDB  , SAVE CONNECTIONS, dbName
      //              , CONNECTION, connName, sourceTable, sourceKey, targetTable, targetKey
      //              , CONNECTION, connName, sourceTable, sourceKey, targetTable, targetKey
      //
      String  dbName = cmd.getListix ().resolveDBName (cmd.getArg(1));

      sqlSolver myDB = new sqlSolver ();

      myDB.openScript ();
      myDB.writeScript (deepSqlUtil.getSQL_CreateTableConnections ());

      String [] connection = null;
      while (null != (connection = cmd.takeOptionParameters(new String [] { "CONNECTION", "CONN" }, true)))
      {
         myDB.writeScript (deepSqlUtil.getSQL_InsertConnection(connection));
      }
      myDB.closeScript ();
      myDB.runSQL (dbName);
   }

   protected void optionDeepSchema (listixCmdStruct cmd)
   {
      //      comm____  oper______
      //      DEEPDB  , DEEP SCHEMA, varForDeepShema, EvaSchemaDB, EvaConnectionList, [tableName]
      //

      if (!cmd.checkParamSize (4, 5)) return;

      String strVarForResult = cmd.getArg(1);
      String strEvaSchema    = cmd.getArg(2);
      String strTabConnTab   = cmd.getArg(3);
      String strTableName    = cmd.getArg(4);

      String  dbName    = cmd.takeOptionString(new String [] { "DATABASE", "DB", "DBNAME" }, null );
      boolean titleInLink = "1".equals (cmd.takeOptionString(new String [] { "CONNECTIONHEADER", "CONNHEADER" }, "0" ));

      Eva evaSchema  = strEvaSchema.length () == 0 ? null: cmd.getListix().getVarEva (strEvaSchema);
      Eva evaConnTab = strTabConnTab.length () == 0 ? null: cmd.getListix().getVarEva (strTabConnTab);
      Eva theVar = cmd.getListix().getSomeHowVarEva (strVarForResult);

      //      //check tableBase
      //      if (dbName != null)
      //      {
      //         if (tabConnTab.length () > 0)
      //            cmd.getLog().warn ("DEEPDB", "given both database [" + dbName + "] and EvaConnectionList [" + tabConnTab + "], the last one will be ignored!");
      //
      //         evaConnTab = new Eva ("anonima");
      //         loadConnections (cmd, dbName, evaConnTab, false);
      //         titleInLink = false; // we give the connections without headers!
      //
      //         ... obtain also schema ....
      //      }
      //      else
      //      {
      //         evaConnTab = cmd.getListix().getVarEva (tabConnTab);
      //         if (evaConnTab == null)
      //         {
      //            cmd.getLog().err ("DEEPDB", "No connections table found (in eva <" + tabConnTab + ">)");
      //            return;
      //         }
      //      }

      // call deepSchema
      //
      deepSchema boy = new deepSchema ();
      boy.getDeepSchema (theVar, evaSchema, evaConnTab, strTableName);
   }

   protected void optionDeepSQL (listixCmdStruct cmd)
   {
      //      comm____  oper______
      //      DEEP DB, DEEP SELECT  , tableName, aliasBaseTable
      //             , DBNAME       , dbName
      //             , VAR CONNECTIONS, EvaConnectionList
      //             , CONNECTION HEADER, 1
      //             , CONNECTION, ...
      //             , VAR DEEP COLUMNS, EvaConnectionList
      //             , DEEP COLUMN     , ...
      //             , DEEP COLUMN LIST, ...
      //             , DEEP COLUMN HEADER, 1
      //             , LINE BREAK, 1
      //             , NO QUOTATION, 1
      //             , SIMPLEST, 1

      if (!cmd.checkParamSize (1, 3)) return;

      String baseTableName = cmd.getArg(1);
      String aliasBaseTable = cmd.getArg(2);

      String  dbName  = cmd.takeOptionString(new String [] { "DATABASE", "DB", "DBNAME" }, null );
      String  varConnections = cmd.takeOptionString(new String [] { "VARCONNECTIONS", "VARCONN" }, null );
      String  varColumns = cmd.takeOptionString(new String [] { "VARCOLUMNS", "VARDEEPCOLUMNS" }, null );
      boolean titleInLink = "1".equals (cmd.takeOptionString(new String [] { "CONNECTIONSHEADER", "CONNSHEADER", "CONNHEADER" }, "0" ));
      boolean titleInDeep = "1".equals (cmd.takeOptionString(new String [] { "DEEPCOLUMNSHEADER", "DEEPCOLHEADER", "COLUMNSHEADER", "COLSHEADER", "COLHEADER" }, "0" ));
      boolean newLine = "1".equals (cmd.takeOptionString(new String [] { "LINEBREAK", "NEWLINE" }, "1" ));
      boolean noquotes = "1".equals (cmd.takeOptionString(new String [] { "NOQUOTATION", "NOQUOTES" }, "0" ));
      boolean simplest = "1".equals (cmd.takeOptionString(new String [] { "SIMPLEST", "SIMPLE", "SIMPLER" }, "0" ));

      // collect connections
      //
      String [] connection = null;
      Eva evaConn = new Eva ();
      if (varConnections != null && varConnections.length () > 0)
      {
         evaConn = cmd.getListix().getVarEva (varConnections);
         if (evaConn == null)
         {
            cmd.getLog().err ("DEEPDB", "var Connections [" + varConnections + "] not found!");
            return;
         }
      }

      while (null != (connection = cmd.takeOptionParameters(new String [] { "CONNECTION", "CONN" }, true)))
      {
         evaConn.addLine (new EvaLine (connection));
      }
      //System.out.println ("connectias\n" + evaConn);

      // collect deep columns
      //
      String [] fields = null;
      Eva evaDeepColumns = new Eva ();
      if (varColumns != null && varColumns.length () > 0)
      {
         evaDeepColumns = cmd.getListix().getVarEva (varColumns);
         if (evaDeepColumns == null)
         {
            cmd.getLog().err ("DEEPDB", "var Columns [" + varColumns + "] not found!");
            return;
         }
      }

      // FIELDS: several deep columns comma separated, it is not possible to assign alias etc
      // example:
      //          DEEP DB, DEEP SQL
      //                 , FIELDS, people name, product name
      //
      while (null != (fields = cmd.takeOptionParameters(new String [] { "FIELDS", "FIELD LIST", "COLUMNS", "DEEP COLUMNS", "DEEP COLUMN LIST" }, true)))
      {
         for (int ii = 0; ii < fields.length; ii ++)
            evaDeepColumns.addLine (new EvaLine (fields[ii]));
      }

      // FIELD: deep column, alias, group Opertation, group Having
      // example:
      //          DEEP DB, DEEP SQL
      //                 , FIELD, people name, personName
      //                 , FIELD, product name
      //
      while (null != (fields = cmd.takeOptionParameters(new String [] { "FIELD", "COLUMN", "DEEP COLUMN", "" }, true)))
      {
         evaDeepColumns.addLine (new EvaLine (fields));
      }

      Eva evaConnTab = null; //  cmd.getListix().getVarEva (tabConnTab);

      if (dbName == null && evaConn.rows () == 0)
         dbName = ""; // default db

      //check tableBase
      if (dbName != null)
      {
         if (evaConn.rows () > 0)
            cmd.getLog().warn ("DEEPDB", "database [" + dbName + "] is given , discard given connections");

         evaConn = new Eva ("anonima");
         loadConnections (cmd, dbName, evaConn, false);
         titleInLink = false; // we give the connections without headers!
      }

      // call deepTableConnector
      //
      deepTableConnector ohboy = new deepTableConnector (evaConn, titleInLink, noquotes, simplest);
      ohboy.resolveConnections (evaDeepColumns, titleInDeep, baseTableName, aliasBaseTable);

      // format the result
      String result = printSQL(ohboy, newLine ? "\n": " ", newLine ? "   ": "");

      // print it out on the current listix target
      cmd.getListix().printTextLsx (result);
   }

   protected String printSQL (deepTableConnector conn, String RET, String TAB)
   {
      String result = "SELECT " + RET;
      for (int ii = 0; ii < conn.resultListSELECT.size (); ii ++)
         result += (TAB + (ii==0 ? "":", ") + (String) conn.resultListSELECT.get(ii)) + RET;

      result += "FROM " + RET;
      for (int ii = 0; ii < conn.resultListFROM.size (); ii ++)
         result += (TAB + (ii==0 ? "":", ") + (String) conn.resultListFROM.get(ii)) + RET;

      //System.out.println ("YA EN DEEPDB conn.resultListFROM size " + conn.resultListFROM.size () + " conn.resultListWHERE size " + conn.resultListWHERE.size ());

      if (conn.resultListWHERE.size () > 0)
      {
         result += "WHERE " + RET;
         for (int ii = 0; ii < conn.resultListWHERE.size (); ii ++)
            result += (TAB + (ii==0 ? "":" AND ") + (String) conn.resultListWHERE.get(ii)) + RET;
      }

      if (conn.resultStringGroupBy.length () > 0)
         result += "GROUP BY " + conn.resultStringGroupBy + RET;

      if (conn.resultStringHaving.length () > 0)
         result += "HAVING " + conn.resultStringHaving + RET;

      return result;
   }
}

