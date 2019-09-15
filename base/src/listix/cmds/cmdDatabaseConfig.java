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

   
db config, default, ""
         , alias, primera, jaja.db
         , alias, lalasa, iilkla.db
         , alias, lila1
         , attached tmp, lila2
   
#gastonaDoc#

   <docType>    listix_command
   <name>       DATABASE CONFIG
   <groupInfo>  data_db
   <javaClass>  listix.cmds.cmdDatabaseConfig
   <importance> 7
   <desc>       //Defines the default database as well as all attached databases in each db operation performed either from listix commands or javaj widgets

   <help>
      //
      // Various DB configurations for all operations with DBs. A default database name can be set as well as aliases and pragmas.
      //
      // ==== DEFAULT DATABASE
      //
      // Usually every command or operation related with database has a parameter called dbName 
      // to specify the database name, actually the physical sqlite file name to operate with. 
      // If this is omitted then a default database is used, this can be set with the command 
      // "DB CONFIG, DEFAULT, dbname". Even if no used the command DB CONFIG, DEFAULT it do exists 
      // a default database which is a temporal database created once per gastona session.
      // So a script that do not specify any dbName in its sql operations and also has not defined 
      // "DB CONFIG, DEFAULT, dbname" will work with this database. Also a call to "DB CONFIG, DEFAULT" 
      // without parameter or specifying an empty string sets this special database as default again.
      //
      //      DB CONFIG, DEFAULT, myDB.db
      //      // now all operations that not specify dbName or specify dbName as empty string will be 
      //      // performed against myDB.db
      //      DB CONFIG, DEFAULT,
      //      // now the original default database, temporal and unique per session, is restored as default
      //
      // The real database name of the temporary - once per session - database is stored in the variable
      //    @<:sys gastona.sessionTmpDB>
      // and the current default db name in 
      //    @<:sys gastona.defaultDB>
      //
      // ==== ATTACHING DATABASES AND CREATING TEMPORAL DATABASES
      //
      //  It is possible as well attaching databases for all sql operations using DB CONFIG command. This makes 
      //  the same as the command "ATTACH DATABASE" in sqlite
      //
      //    DB CONFIG, DEFAULT, myDB.db
      //             , ALIAS, second, myDB2.db
      //             , ALIAS, third , myDB3.db
      //             , ALIAS, auxiliar, @<:lsx tmp db>
      //
      //  this command will perform [ATTACH DATABASE 'myDB2.db' AS second] etc .. before each operation with sqlite.
      //  Note that it is not possible to take advantage from sqlite native temporary tables (sqlite CREATE TEMP TABLE ..), 
      //  this is because on each operation with sqlite dbs all databases are finally closed, therefore such a 
      //  temporary table would persist only during one call.
      //  Instead it is possible to create and attach temporary databases as is done in the example with the alias "auxiliar".
      //
      // ==== SETTING THE ENCODE MODEL SCHEMA
      //
      // To allow multi line text in fields as well as special characters a encoding schema is used. Per default an intern
      // simple schema is used. It can be changed to standards like "UTF-8" or "ISO-8859-1" using the command DATABASE CONFIG, ENCODING.
      //
      //
      // ==== USING PRAGMAS
      //
      //  This command will perform [PRAGMA pragmaName = value] etc .. before each operation with sqlite.
      //  For example if we want to disable journal in sqlite for a faster insert and update
      //
      //    DB CONFIG, PRAGMA, journal_mode, OFF
      //
      //

   <aliases>
      alias
      DATABASE CONFIGURE
      DATABASE CONFIG
      DB CONFIGURE
      DB CONFIG

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Sets the default database to be used in all following sql operations which do not specify dbName or specify empty string dbName
         2   ,    5      , //Defines alias and attached database to be used in all following sql operations
         3   ,    5      , //Defines a pragma to be set
         4   ,    5      , //Encode model schema to be used with the data

   <syntaxParams>
      synIndx, name           , defVal    , desc
         1   , DEFAULT        ,           , //
         1   , dbName         ,           , //Database name (a file name) or empty string in order to use the gastona session temporary database

         2   , ALIAS          ,           , //
         2   , aliasName      ,           , //Table name to be created or to add records to
         2   , dbName         ,           , //physical database name for the alias

         3   , PRAGMA         ,           , //
         3   , pragmaName     ,           , //DB pragma name
         3   , pragmaValue    ,           , //pragma value

         4   , ENCODE         ,           , //
         4   , encodeSchema   ,           , //For example UTF-8 or ISO-8859-1. If not given then the intern schema is used
         
   <options>
      synIndx, optionName            , parameters, defVal, desc
          x  , ALIAS                 , "aliasName, dbName",    , //Defines alias and attached database to be used in all following sql operations
          x  , PRAGMA                , "pragmaName, value",    , //Set a pragma for all db operations
          x  , ENCODE                , "encoding",    , //Encoding for texts. For example UTF-8 or ISO-8859-1. If not given then the intern schema is used
          x  , DEFAULT               , "dbName",    , //Database name (a file name) or empty string in order to use the gastona session temporary database

   <examples>
      gastSample

      configuring db names

   <configuring db names>
      //#javaj#
      //
      //   <frames>
      //       main, "Configuring db names", 200, 300
      //
      //   <layout of main>
      //       EVA, 10, 10, 2, 2
      //          , X
      //          , rgDBchoose
      //        X , tVista
      //        X , oSalida
      //
      //#data#
      //
      //  <rgDBchoose orientation> X
      //  <rgDBchoose>
      //       label, sql
      //       
      //       default, //SELECT * FROM content;
      //       temp 1 , //SELECT * FROM t1.content;
      //       temp 2 , //SELECT * FROM t2.content;
      //       
      //#listix#
      //
      //   <main>
      //      VAR=, tmp1, @<:lsx tmp db>
      //      VAR=, tmp2, @<:lsx tmp db>
      //
      //      DB,        , EXECUTE, //CREATE TABLE content (desc text); INSERT INTO content VALUES ("default session db");
      //      DB, @<tmp1>, EXECUTE, //CREATE TABLE content (desc text); INSERT INTO content VALUES ("temporary db 1");
      //      DB, @<tmp2>, EXECUTE, //CREATE TABLE content (desc text); INSERT INTO content VALUES ("temporary db 2");
      //
      //      DB CONFIGURE, ALIAS, t1, @<tmp1>
      //                  , ALIAS, t2, @<tmp2>
      //
      //   <-- rgDBchoose>
      //       CHECK, VAR, rgDBchoose selected.sql
      //       -->, tVista data!, sqlSelect, @<rgDBchoose selected.sql>
      //


#**FIN_EVA#

*/

package listix.cmds;


import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.*;


public class cmdDatabaseConfig implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
         "DATABASE CONFIGURE",
         "DATABASE CONFIG",
         "DB CONFIGURE",
         "DB CONFIG",
         "DB CFG",
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
      // NOTE : optionOffset = 0 all options can be in the first line (syntax + arguments)
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm, 0 /* options can be syntax! */);

      String defDB = cmd.takeOptionString (new String [] { "DEFAULT", "DEFAULTDB", "DEFDB", "DEF" }, null);
      if (defDB != null)
      {
         System.setProperty(sqlUtil.sGLOB_PROPERTY_DB_DEFAULT_DATABASE_NAME, defDB);
         cmd.getLog().dbg (2, "DATABASE CONFIG", "default db name set to [" + defDB + "]");
      }
      
      String encoding = cmd.takeOptionString (new String [] { "ENCODE", "ENCODING" }, null);
      if (encoding != null)
      {
         utilEscapeStr.setEscapeModel (encoding);
         cmd.getLog().dbg (2, "DATABASE CONFIG", "encoding model set to [" + encoding + "]");
      }
      
      Eva prag = cmd.takeOptionAsEva (new String [] { "PRAGMA", "" }, "pragmas", true);
      if (prag != null)
      sqlUtil.setGlobalDefaultDBPragmas (prag);
         
      Eva ali = cmd.takeOptionAsEva (new String [] { "ALIAS", "ALIASDB" }, "aliases", true);
      if (ali != null)
      sqlUtil.setGlobalDefaultDBAliases (ali);
      
      cmd.getLog().dbg (2, "DATABASE CONFIG", "set " + (prag != null ? prag.rows () : 0)+ " pragmas and " + (ali != null ? ali.rows (): 0) + " aliases");
      
      cmd.checkRemainingOptions ();
      return 1;
   }
}

