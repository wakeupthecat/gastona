/*
library listix (www.listix.org)
Copyright (C) 2005-2019 Alejandro Xalabarder Aulet

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
   <name>       DEEP DB
   <groupInfo>  data_db
   <javaClass>  listix.cmds.cmdDeepDB
   <importance> 6
   <desc>       //Database sql facility

   <help>
      //
      // DEEP DB acts as an extension of the SQL language, specifically
      // permits to build complex queries in an easy and readable way. It is based on a concept
      // called "deep table", which is explained in detail in the article
      // http://www.codeproject.com/KB/database/thedeeptable.aspx. Basically we define
      // connections between tables and give them a name. Then we use the connection name to
      // access related data. This is not limited to a table and its direct connections but
      // also it is possible to describe connections of connections and so on (deep).
      //
      // Example:
      //
      //    suppose we have following schema
      //
      //    table tabArtists  (ID, name)
      //    table tabAlbum    (albumID, authorID, name, year, price)
      //
      //    and the tables are related by the "productID" <-> "ID" fields.
      //    This will be our connection that we will call PRODUCT
      //    and through it we will access the related fields.
      //
      //    Then the command:
      //
      //      DEEP DB, SELECT, tabAlbum
      //             , CONNECTION, AUTHOR, tabAlbum, authorID, tabArtists, ID
      //             , FIELD     , albumID
      //             , FIELD     , AUTHOR name
      //             , FIELD     , year
      //
      //    Would generate the select query:
      //
      //      SELECT
      //         tabAlbum.albumID AS albumID
      //         , AUTHOR.name AS AUTHOR_name
      //         , tabAlbum.year AS year
      //      FROM
      //         tabAlbum
      //         , tabArtists AS AUTHOR
      //      WHERE
      //         tabAlbum.authorID == AUTHOR.ID
      //
      //    The connections can be stored in the sqlite database as part of the schema by using
      //    the command "DEEP DB" in its syntax "SET CONNECTIONS". If this is already done then the
      //    command can be simplified as:
      //
      //      DEEP DB, SELECT, tabAlbum
      //             , FIELDS, albumID, "AUTHOR name", year
      //
      //    While the option FIELDS (or COLUMNS) admit more comma separated "deep columns", the
      //    option FIELD (default one) only admits one but an alias can be given as well as a GROUP BY
      //    operation, if this is done then a "GROUP BY" select is generated.
      //
      //    Note that DEEP DB does not need knowledge about the complete schema, it just generate a
      //    select query from the given connections and deep columns.
      //


   <aliases>
      alias
      DEEP
      DBDEEP

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
            1, CONNECTION     , "connName, sourceTable, sourceKey, targetTable, targetKey",, Connection given to be stored. A connection that requires more than one key columns can be expressed using more lines with the option CONNECTION
            1, LINE BREAK     , 0 / 1            , 1     , If set to 0 it returns a string with no line breaks
            1, FIELD          , deep column      ,       , //Deep column in the format "connection connection ... field", this is the default option therefore it is possible to do it without "FIELD"

            3, CONNECTION     , "connName, sourceTable, sourceKey, targetTable, targetKey",, Connection given to be stored. A connection that requires more than one key columns can be expressed using more lines with the option CONNECTION

   <examples>
      gastSample

      DeepDB example
      DeepDB twoSyntaxes
      DeepDB deeper example
      DeepDB recursive example

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

   <DeepDB recursive example>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "listix command DEEP DB recursive example", 200, 300
      //
      //#listix#
      //
      //   <main0>
      //      // generated SELECT joining one table with itself
      //      //
      //      DEEP DB, SELECT, tabPeople
      //             , CONN, father, tabPeople, fatherID, tabPeople, ID
      //             , CONN, mother, tabPeople, motherID, tabPeople, ID
      //             ,     , ID
      //             ,     , name
      //             ,     , father name
      //             ,     , mother name
      //             ,     , mother father name, yayo
      //             ,     , mother mother name, yaya


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

      boolean optTranslate  = cmd.meantConstantString (oper, new String [] { "SELECT", "GETSELECT", "DEEPSELECT" });
      boolean optLoadConn   = cmd.meantConstantString (oper, new String [] { "GETCONNECTIONS", "LOADCONNECTIONS" });
      boolean optSaveConn   = cmd.meantConstantString (oper, new String [] { "SETCONNECTIONS", "SAVECONNECTIONS" });
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
      String  dbName = cmd.getArg(1);

      sqlSolver myDB = new sqlSolver ();

      myDB.openScript ();
      myDB.writeScript (deepSqlUtil.getSQL_CreateTableConnections ());

      String [] connection = null;
      while (null != (connection = cmd.takeOptionParameters(new String [] { "CONNECTION", "CONN" }, true)))
      {
         myDB.writeScript (deepSqlUtil.getSQL_InsertConnection(connection));
      }
      myDB.closeScript ();
      myDB.runSQL ((dbName.length () > 0) ? dbName : cmd.getListix ().getDefaultDBName ());
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

      // call fieldConnector1
      //
      deepSchema boy = new deepSchema ();
      boy.getDeepSchema (theVar, evaSchema, evaConnTab, strTableName);
   }

   protected void optionDeepSQL (listixCmdStruct cmd)
   {
      //      comm____  oper______
      //      DEEP DB, DEEP SELECT  , tableName, aliasBaseTable
      //             , CONNECTION, ...
      //             , FIELD     , ...
      //

      if (!cmd.checkParamSize (2, 3)) return;

      String baseTableName = cmd.getArg(1);
      String aliasBaseTable = cmd.getArg(2);

      String  dbName  = cmd.takeOptionString(new String [] { "DATABASE", "DB", "DBNAME" }, null );
      boolean newLine = "1".equals (cmd.takeOptionString(new String [] { "LINEBREAK", "NEWLINE" }, "1" ));

      // collect connections
      //
      String [] connection = null;
      Eva evaConn = new Eva ();
      while (null != (connection = cmd.takeOptionParameters(new String [] { "CONNECTION", "CONN" }, true)))
      {
         evaConn.addLine (new EvaLine (connection));
      }
      //System.out.println ("connectias\n" + evaConn);

      // collect deep columns
      //
      String [] fields = null;
      Eva evaDeepColumns = new Eva ();

      // FIELDS: several deep columns comma separated, it is not possible to assign alias etc
      while (null != (fields = cmd.takeOptionParameters(new String [] { "FIELDS", "COLUMNS" }, true)))
      {
         for (int ii = 0; ii < fields.length; ii ++)
            evaDeepColumns.addLine (new EvaLine (fields[ii]));
      }

      // FIELD: deep column, alias, group Opertation, group Having
      while (null != (fields = cmd.takeOptionParameters(new String [] { "FIELD", "COLUMN", "" }, true)))
      {
         evaDeepColumns.addLine (new EvaLine (fields));
      }
      //System.out.println ("recollectadas\n" + evaDeepColumns);


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
      }

      // call fieldConnector1
      //
      fieldConnector1 boy = new fieldConnector1 (evaConn, false);
      boy.resolveConnections (evaDeepColumns, false, baseTableName, aliasBaseTable);

      // format the result
      String result = printSQL(boy, newLine ? "\n": " ", newLine ? "   ": "");

      // print it out on the current listix target
      cmd.getListix().printTextLsx (result);
   }

   protected String printSQL(fieldConnector1 conn, String RET, String TAB)
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

