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
   //(o) WelcomeGastona_source_listix_command DBMORE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       DBMORE
   <groupInfo>  data_db
   <javaClass>  listix.cmds.cmdDBMore
   <importance> 7
   <desc>       //Enhancing working with databases

   <help>
      //
      // This is very new! no docu yet
      //


   <aliases>
      alias
      DBMORE
      DBMAS

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Build an select sql from a list of "deep" columns using table connections
         2   ,    3      , //Load stored connections from a database (from the table __dbMore_connections)
         3   ,    3      , //Save a list of connections in a database (into the table __dbMore_connections)
         4   ,    3      , //Returns a list of all possible deep columns from databse or from a specific table

   <syntaxParams>
      synIndx, name           , defVal    , desc
         1   , DEEP TO SQL    ,
         1   , EvaDeepColList ,           , //Eva variable containing the "deep column list" (e.g. "tableBase conn1 conn2 name")
         1   , baseTable      ,           , //If given the "deep" columns are expected starting either with a connection or with the final colum name (e.g. "conn1 conn2 name")
         1   , aliasBaseTable ,           , //If given it is used as alias for the first table

         2   , LOAD CONNECTIONS ,          ,
         2   , dbName           ,          , //Database from where the connections has to be loaded
         2   , EvaConnectionList,          , //Eva name of the variable target
         2   , withHeader       ,  0       , //If 1 column names will also retrieved (in the row 0 of the eva variable)

         3   , SAVE CONNECTIONS ,          ,
         3   , dbName           ,          , //Database from where the connections has to be loaded

         4   , DEEP SCHEMA     ,
         4   , varForDeepShema , EvaName   , //Variable name for the resulting deep schema
         4   , EvaSchemaDB     ,           , //Schema of the database (e.g. obtained with DATABASE SCHEMA)
         4   , EvaConnectionList,          , //Eva name of the variable containing the connection table definitions (connName, sourceTable, sourceKey, targetTable,  targetKey)
         4   , tableName       , ""         , //If given, it limits retrieved deep columns to the table 'tableName'

   <options>
      synIndx, optionName     , parameters       , defVal, desc
            1, VARCONNECTIONS , EvaConnectionList,       , Eva name of the variable containing the the connection table definitions (connName, sourceTable, sourceKey, targetTable,  targetKey)
            1, DBNAME         , dbName           ,       , If given and EvaConnectionList is NOT given the connections will be retrieve from the table __dbMore_connections of the database
            1, LINE BREAK     , 0 / 1            , 1     , If set to 0 it returns a string with no line breaks
            1, DEEP COL HEADER, 0 / 1            , 0     , If set to 1 headers are expected in the first row of EvaDeepColList (actually the first row will be ignored/skiped)
            1, CONNECTION HEADER, 0 / 1          , 0     , If set to 1 headers are expected in the first row of EvaConnectionList (actually the first row will be ignored/skiped)
            <!1, EMPTYSQL, sqlselect         , "SELECT """" AS ""no selection""" , 

            3, CONNECTION     , "connName, sourceTable, sourceKey, targetTable, targetKey",, //Conenction given to be stored

<!!                    , 11 / 01 / 10 / 00, 11    , "01" means no titles (first row) in DeepColumnDef, "10" in linkTableDef and "00" no titles in both, default value is "11"

<!! Por ahora (30.01.2010 03:13) NO SOPORTAR ESTAS OPCIONES.....
<!!            3, DBNAME         , dbName           ,       , If given schema and connections will be retrieve from the given database, then EvaSchemaDB and EvaConnectionList dont have to be specified
<!!            3, CONNECTION HEADER, 0 / 1          , 0     , If set to 1 headers are expected in the first row of EvaConnectionList (actually the first row will be ignored/skiped)

   <examples>
      gastSample

      DBMore example

   <DBMore example>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "listix command DATABASE example", 200, 300
      //
      //#data#
      //
      //   <connections>
      //      <! linkName, sourceTable  , sourceKey  , targetTable, targetKey
      //
      //      pare    , tabPersona   , pareid     , tabPersona , id
      //      mare    , tabPersona   , mareid     , tabPersona , id
      //      perso   , tabNota      , persoId    , tabPersona , id
      //
      //
      //   <fields>
      //      <! column
      //
      //      tabNota noteId
      //      tabNota date
      //      tabNota perso name
      //      tabNota perso pare name
      //      tabNota perso mare name
      //      tabNota text
      //
      //#listix#
      //
      //   <main0>
      //      DBMORE, TO SQL, fields
      //            , VAR CONNECTIONS, connections
      //


#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.dbMore.*;
import de.elxala.db.sqlite.*;


public class cmdDBMore implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "DBMORE",
          "DBMAS",
       };
   }

   private static final String INVALID_DBNAME = "~";

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

      boolean optTranslate  = cmd.meantConstantString (oper, new String [] { "DEEPTOSQL", "DEEPSQL", "DEEP", "TRANSLATE", "TOSQL", "SQL" });
      boolean optLoadConn   = cmd.meantConstantString (oper, new String [] { "LOADCONNECTIONS", "LOADCONN" });
      boolean optSaveConn   = cmd.meantConstantString (oper, new String [] { "SAVECONNECTIONS", "SAVECONN" });
      boolean optDeepSchema = cmd.meantConstantString (oper, new String [] { "DEEPSCHEMA", "DEEPCOLUMNS", "PLUSSCHEMA", "EXTENDEDSCHEMA" });

      if (optTranslate)       optionDeepSQL (cmd);
      else if (optLoadConn)   optionLoadConnections (cmd);
      else if (optSaveConn)   optionSaveConnections (cmd);
      else if (optDeepSchema) optionDeepSchema (cmd);
      else
      {
         cmd.getLog().err ("DBMORE", "DBMORE la operation [" + oper + "] not recognized!");
         return 1;
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

   protected void optionLoadConnections (listixCmdStruct cmd)
   {
      //      comm____  oper______
      //      DBMORE  , LOAD CONN , dbName, EvaConnectionList, withHeader
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
            cmd.getLog().err ("DBMORE", "Too many connections found (" + myRO.getRecordCount() + "), only using the first 3000!");
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
      //      DBMORE  , SAVE CONNECTIONS, dbName
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
      //      DBMORE  , DEEP SCHEMA, varForDeepShema, EvaSchemaDB, EvaConnectionList, [tableName]
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
      //            cmd.getLog().warn ("DBMORE", "given both database [" + dbName + "] and EvaConnectionList [" + tabConnTab + "], the last one will be ignored!");
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
      //            cmd.getLog().err ("DBMORE", "No connections table found (in eva <" + tabConnTab + ">)");
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
      //      DBMORE  , DEEP SQL  , EvaDeepColList, baseTable, [aliasTableBase]
      //              , VARCONNECTIONS, EvaConnectionList
      //

      if (!cmd.checkParamSize (2, 4)) return;

      String deepColTable = cmd.getArg(1);
      String baseTableName = cmd.getArg(2);
      String aliasBaseTable = cmd.getArg(3);


      String  dbName    = cmd.takeOptionString(new String [] { "DATABASE", "DB", "DBNAME" }, null );
      String  varConnections = cmd.takeOptionString(new String [] { "VARCONNECTIONS" }, null );
      boolean titleInLink = "1".equals (cmd.takeOptionString(new String [] { "CONNECTIONHEADER", "CONNHEADER" }, "0" ));
      boolean titleInDeep = "1".equals (cmd.takeOptionString(new String [] { "DEEPCOLUMNSHEADER", "DEEPCOLHEADER", "DEEPHEADER" }, "0" ));
      boolean newLine     = "1".equals (cmd.takeOptionString(new String [] { "LINEBREAK", "NEWLINE" }, "1" ));

      String [] fields = cmd.takeOptionParameters (new String [] { "FIELD", "" }, true);

      //check deepColTable
      Eva evaDeepColTable = cmd.getListix().getVarEva (deepColTable);
      if (evaDeepColTable == null)
      {
         cmd.getLog().err ("DBMORE", "deep column list not found (source eva <" + deepColTable + "> not found)!");
         return;
      }

      Eva evaConnTab = null; //  cmd.getListix().getVarEva (tabConnTab);

      //check tableBase
      if (dbName != null)
      {
         if (varConnections != null && varConnections.length () > 0)
            cmd.getLog().warn ("DBMORE", "given both database [" + dbName + "] and VAR CONNECTIONS [" + varConnections + "], the last one will be ignored!");

         evaConnTab = new Eva ("anonima");
         loadConnections (cmd, dbName, evaConnTab, false);
         titleInLink = false; // we give the connections without headers!
      }
      else
      {
         evaConnTab = cmd.getListix().getVarEva (varConnections);
         if (evaConnTab == null)
         {
            cmd.getLog().err ("DBMORE", "No connections table found (in eva <" + varConnections + ">)");
            return;
         }
      }

      // call fieldConnector1
      //
      fieldConnector1 boy = new fieldConnector1 (evaConnTab, titleInLink);
      boy.resolveConnections (evaDeepColTable, titleInDeep, baseTableName, aliasBaseTable);

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

      //System.out.println ("YA EN DBMORE conn.resultListFROM size " + conn.resultListFROM.size () + " conn.resultListWHERE size " + conn.resultListWHERE.size ());

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

