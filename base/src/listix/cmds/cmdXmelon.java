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
   //(o) WelcomeGastona_source_listix_command PARSONS

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       XMELON
   <groupInfo>  system_process
   <javaClass>  listix.cmds.cmdXmelon
   <importance> 4
   <desc>       //Parses a XML file and stores the data into a database

   <help>
      //
      // Parses a XML file and stores the data and the path structure in tables into a database.
      //

   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Parse 'xmlSourceFile' and set the result into the database 'dbName' creating the schema if needed
         2   ,    4      , //Enables or disables a cache used to keep loaded tables (tagID and pathID) that accelerates the parsing with multiple files

   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , FILE2DB      ,             , //
         1   , xmlSourceFile,             , //Name of text file to be parsed
         1   , targetDbName , (default db), //Database name where to put the result of parsing
         1   , tablePrefix  , xmelon      , //Optional prefix for the result tables ('prefix'_files, 'prefix'_paths and 'prefix'_data)

         2   , ENABLE CACHE ,             , //
         2   , 0 / 1        , 0           , //Set to 1 if want xmelon to use a cache for tagID and pathID tables. In loops it makes the parser to work faster

<!         3   , INIT MULTI    ,             , //
<!         3   , targetFileBase,             , //default "parsons" will generate parsons_files.txt parsons_parsons.txt

<!         4   , MULTI        ,             , //
<!         4   , sourceFile   ,             , //Name of text file to be parsed
<!         4   , recordPatternRef,          , //Eva name where the Pattern is to be found (pattern given in an Eva variable)

   <options>
      synIndx, optionName, parameters, defVal, desc
      1      , SCHEMA    , 1 / 2     ,  2    , //Xmelon database schema to apply (1 or 2 which is more efficient)

   <examples>
      gastSample
      <!parsing a CD Catalog


<kkkkkkkkk>
      //      GEN,, print schema (db), listix, META-GASTONA/utilApp/std/printSchema.lsx
      //         , PARAMS, ""


#**FIN EVA#
*/

package listix.cmds;

import java.util.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.utilEscapeStr;
import de.elxala.parse.xml.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end


/**
*/
public class cmdXmelon implements commandable
{
   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "_lib scanFiles_error"
   private static MessageHandle LIGHT_MSG_START     = null; // new MessageHandle (); // "_lib parsons_start"
   private static MessageHandle LIGHT_MSG_PROGRESS  = null; // new MessageHandle (); // "_lib parsons_progresss"
   private static MessageHandle LIGHT_MSG_END       = null; // new MessageHandle (); // "_lib parsons_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   private saXmelon theSaXmelon = null;
   private saXmelon2 theSaXmelon2 = null;
   private boolean allowCache = false;

   private int fileID = -1;

   private static boolean initOnce_msgHandles ()
   {
      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_PROGRESS  = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         //(o) TODO_parsons Unify ledMsg parsing_start etc for PARSONS and XMELON

         //NOTE! the message should be the same as in parsons, this signal should be generl
         //      for parsing activity

         // this messages are not mandatory to be suscribed, the are provided just as internal information of parser command
         Mensaka.declare (null, LIGHT_MSG_START    , "ledMsg parsing_start"      , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_PROGRESS , "ledMsg parsing_progresss"  , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_END      , "ledMsg parsing_end"        , logServer.LOG_DEBUG_0);
      }
      return true;
   }

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "XMELON",
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

      String oper        = cmd.getArg(0);
      String fileSource  = cmd.getArg(1);
      String dbName      = cmd.getArg(2);
      String tablePrefix = cmd.getArg(3);
      if (tablePrefix.length () == 0)
         tablePrefix = "xmelon";

      if (dbName.length () == 0)
         dbName = cmd.getListix ().getDefaultDBName ();

      boolean optCache   = cmd.meantConstantString (oper, new String [] { "ENABLECACHE", "CACHE" } );
      boolean optFile2DB = cmd.meantConstantString (oper, new String [] { "FILE2DB" } );

      if (!optCache && !optFile2DB)
      {
         cmd.getLog().err ("XMELON", "operation [" + oper + "] not valid, only FILE2DB or ENABLE CACHE are allowed");
         return 1;
      }

      if (optCache)
      {
         allowCache = cmd.getArg(1).equals ("1");
         cmd.getLog().dbg (2, "XMELON", "operation [" + oper + "] set it to " + allowCache);
         // reset anyway
         if (theSaXmelon != null)
         {
            theSaXmelon.clearCache ();
            theSaXmelon2.clearCache ();
         }
         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (cmd.getLog().isDebugging (2))
         cmd.getLog().dbg (2, "XMELON", "execute with : oper [" + oper + "] fileSource [" + fileSource + "] dbName [" +  dbName + "] prefix [" + tablePrefix + "]");
      if (fileSource.length () == 0)
      {
         that.log ().err ("XMELON", "No file to scan given!");
         return 1;
      }

      String schemaType = cmd.takeOptionString(new String [] {"SCHEMATYPE", "SCHEMA", "SCHEME", "SCHEMETYPE" }, "2");

      // Important to keep theSaXmelon alive to take profit of its
      // cache habilities (e.g. when parsing multiple files calling the command XMELON
      // continously)
      //
      if (theSaXmelon == null)
      {
         theSaXmelon = new saXmelon ();
         theSaXmelon2 = new saXmelon2 ();
      }

      if (!allowCache)
      {
         theSaXmelon.clearCache ();
         theSaXmelon2.clearCache ();
      }

      if (schemaType.equals ("1"))
         theSaXmelon.parseFile (fileSource, dbName, tablePrefix);
      else if (schemaType.equals ("2"))
         theSaXmelon2.parseFile (fileSource, dbName, tablePrefix, allowCache);
      else that.log ().err ("XMELON", "Wrong option SCHEMA with value [" + schemaType + "], only 1 or 2 are accepted.");


      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
