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
   //(o) WelcomeGastona_source_listix_command LOOP TABLE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix command.

#gastonaDoc#

   <docType>    listix_command
   <name>       LOOP TABLE
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdLoopTable
   <importance> 8
   <desc>       //To perform loops in listix formats

   <help>
      //
      // Loops are performed using the commands LOOP TABLE and RUN LOOP. All loops do basically the
      // same: execute a format (body) for each row of a table. Each syntax of LOOP TABLE determines
      // in a different way how the table to be looped is obtained, from a varible, a SQL etc.
      // The table itself does not have to be a real table, for example "LOOP, FOR, ii, 1, 10" would
      // loop a virtual table of one column named "ii" containing the values 1, 2, 3, .. 10 in its rows.
      //
      // During the loop the format being executed (body) can access the values of the columns by its name
      // and also row information, specifically during the loop following variables can be accessed
      //
      //    @<:lsx ROWS> : number of rows in the loop
      //    @<:lsx ROW>  : number of current row
      //    @<COLUMNAME> : value of the column named COLUMNNAME in the current row
      //
      // If a column has the same name as other already existent variable, then the column hides temporally
      // the variable. As soon as the loop is finished, the variable will become again "visible".
      // This kind of "masking variables" happens as well when loop is executed within another loop, and so on.
      //
      // Body of the loop
      // -----------------------
      //
      // The body of the loop are in general text and/or commands or both mixed. It can be passed using
      // the option BODY as many times as lines has the body. For example:
      //
      //          LOOP, arguments...
      //              , BODY, //first line of body
      //              , BODY, //second line etc
      //
      // Since BODY is the default option of LOOP, it is possible to omit it
      //
      //          LOOP, arguments...
      //              ,, //first line of body
      //              ,, //second line etc
      //
      // As said before the body might contain any text combination of text and commands, actually the body
      // acts as a virtual listix format. But of course it is still possible to separate it physically.
      // For example
      //
      //          LOOP, arguments...
      //              ,, @<my body>
      //
      // or
      //          LOOP, arguments...
      //              ,, LISTIX, my body
      //
      // HEAD, TAIL and "IF EMPTY"
      // -----------------------------
      //
      // As the option BODY, HEAD, TAIL and IF EMPTY are also virtual listix formats that might be given
      //
      //
      // Looping the loop
      // -----------------------
      // Inner loops (loop within a loop) are of course possible and often necessary. For example
      //
      //    <format1>
      //       LOOP, FOR, indx, 1, 10
      //           ,, //Index @<indx>
      //           ,, LOOP, FOR, subIndx, 1, 5
      //           ,,     ,, //   Sub-Index @<subIndx>
      //
      // One special inner loop is done by the syntax COLUMNS. "LOOP, COLUMNS" is by definition a nested loop,
      // since it always acts on the current row of the "outer" loop (previous loop before LOOP COLUMNS).
      // What it really loops is a virtual table with two columns: "columnName" and "columnValue", which
      // contain as much rows as columns has the outer loop. For example if looping the table
      //
      //       <table>
      //          id1  , id2,  lastID, name
      //          011  , aaa,  AAAAAA, Product A
      //          022  , bbb,  BBBBBB, Product A++
      //
      // calling LOOP, COLUMNS on the second record will loop the table
      //
      //       "<virtual table>"
      //          columnName, columnValue
      //          id1       , 022
      //          id2       , bbb
      //          lastID    , BBBBBB
      //          name      , Product A++
      //
      // This can be very useful, for example the loop
      //
      //    LOOP, SQL,, //SELECT * FROM mytable
      //        ,, //Record #@<:lsx ROW>
      //        ,, LOOP, COLUMNS
      //        ,,     ,, //@<columnName>: @<columnValue>
      //        ,,
      //
      // prints out all records of the given query.
      //



   <aliases>
      alias
      "LOOP",
      "MR LOOP",
      "SIR LOOP",
      "BUCLE"

   <syntaxHeader>
      synIndx, importance, desc
      1      ,    8      ,  //Loops the table contained in the Eva variable 'evaName'
      2      ,    8      ,  //Loops the table result of the SQL query 'SelectQuery' from the sqlite database 'sqliteDBName'
      3      ,    3      ,  //Set a table with columns : parentPath, fileName, extension, date, size, fullPath, fullParentPath and fullSubPath of the files found in 'path' that have of the given extensions
      4      ,    3      ,  //Make a typical for
      5      ,    4      ,  //Sets a virtual table with the fields 'columnName' and 'columnValue' of the current iteration of the current loop.

   <syntaxParams>
      synIndx, name         , defVal, desc
      1      , EVA          ,       , //
      1      , evaName      ,       , //Name of Eva variable that contains the table
      1      , evaUnit      ,       , //Name of EvaUnit where the Eva variable is to be found. If specified also evaFile must be specified
      1      , evaFile      ,       , //File name where the 'evaUnit' and within it 'evaName' are to be found

      2      , SQL          ,             , //
      2      , sqliteDBName , (default db), //Database from which the query will be executed. If not given a global and temporary database will be used instead.
      2      , SelectQuery ,              , //Select query (also sqlite pragma is accepted)

      3      , FILES        ,             , //
      3      , path         ,             , //Path to be scaned recursively for file names
      3      , extension    ,             , //File name extension to be included, also comma separated extensions are possible
      3      , ...          ,             , //Further extensions

      4      , FOR          ,             , //
      4      , indexName    ,             , //Variable name for the index of the FOR (default "forIndex")
      4      , initialValue ,             , //Initial value for the loop
      4      , endValue     ,             , //Final value for the loop
      4      , increment    ,      1      , //Increment

      5      , COLUMNS      ,             , //

      6      , TEXT FILE    ,             , //
      6      , fileName     ,             , //File name to open and loop

   <options>
      synIndx, optionName  , parameters                  , defVal, desc
          x  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited
          x  , HEAD        , subcommand                  ,       , //Executed (if rows >= 1) before the body execution of the first row. On executing HEAD loop column variables are set to the first row
          x  , TAIL        , subcommand                  ,       , //Executed (if rows >= 1) after the body execution of the last row. On executing TAIL there are not loop column variables set
          x  , IF NO RECORD, subcommand                  ,       , //Executed if the table to loop result in 0 rows that is the table is empty.
          x  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. The default value is the native return RT or RT+LF
          x  , WHILE SAME  , fieldName                   ,       , //Continue the loop while the value of fieldName and its predecessors remains the same (or until any of them change)
          x  , ON DIFFERENT, fieldName                   ,       , //Perform the loop only on different values of fieldName or any of its predecessors (skip records with same value)
          x  , FILTER      , "fieldName, operator, value",       , //Skip records that do not meet the given condition for the field. If more filters are given, skip those records that do not meet any of the filters (OR join)
          3  , EXTENSIONS  , "extension, extension"      ,       , //Add extensions as is done in the arguments extension

<!//(o) TODO_previousSQL dbFeature (documented it when all tests ok)
<!          2  , PREVIOUSSQL , sqlCommand                  ,       , //If specified the given sql command (or commands) will be called before the sqlQuery. Useful for example for sql commands like ATTACH, CREATE TEMP VIEW etc.

   <examples>
      gastSample

      first loop

   <first loop>

      //#javaj#
      //
      //   <frames>  oConsolar, "first loop"
      //
      //#data#
      //
      //    <people>
      //       id, name   , desc
      //       01, Gastona, framework
      //       02, Listix , generator
      //       03, Javaj  , GUI maker
      //
      //#listix#
      //
      //   <main0>
      //      LOOP, EVA, people
      //          ,, //Hello @<name>, little big @<desc>!
      //

#**FIN_EVA#
*/


package listix.cmds;

import listix.table.*;
import listix.*;
import de.elxala.Eva.*;

public class cmdLoopTable implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "LOOP",
            "LOOP TABLE",
            "SET LOOP",
            "SET TABLE",
         };
   }
   
   private static void addSubCommand (Eva commands, int indxComm, Eva target)
   {
      int row = target.rows ();
      target.setValue ("", row, 0); // ensure at least an empty line
      for (int ii = 2; ii < commands.cols (indxComm); ii ++)
      {
         target.setValue (commands.getValue(indxComm, ii), row, ii - 2);
      }
   }

   public static int indxSkipingOptions (Eva commands, int indxComm, Eva embeddedFormat)
   {
      return indxSkipingOptions (commands, indxComm, null, embeddedFormat, null, null);
   }

   // this method is public in order to share it with the class cmdRunLoopTable and others
   //
   public static int indxSkipingOptions (Eva commands, int indxComm, Eva headerFormat, Eva bodyFormat, Eva tailFormat, Eva if0RowLsxFormat)
   {
      // go through the options of the loop ...
      //
      int indxPassOpt = indxComm + 1;
      while (indxPassOpt < commands.rows () &&
             commands.cols(indxPassOpt) > 1 &&
             commands.getValue(indxPassOpt, 0).equals("")
             )
      {
         String optName = commands.getValue(indxPassOpt, 1);

         if (optName.equals("") || optName.equalsIgnoreCase("BODY"))
         {
            // detected body format within the options (embedded loop format), e.g.
            //
            //       LOOP, ETC
            //           ,, format
            //           ,, etc
            //
            if (bodyFormat != null)
               addSubCommand (commands, indxPassOpt, bodyFormat);
         }
         else if (optName.equalsIgnoreCase("HEAD") || optName.equalsIgnoreCase("HEADER"))
         {
            if (headerFormat != null)
               addSubCommand (commands, indxPassOpt, headerFormat);
         }
         else if (optName.equalsIgnoreCase("TAIL"))
         {
            if (tailFormat != null)
               addSubCommand (commands, indxPassOpt, tailFormat);
         }
         else if (optName.equalsIgnoreCase("IF NO RECORD") || optName.equalsIgnoreCase("IF EMPTY") || optName.equalsIgnoreCase("IF 0 ROWS"))
         {
            // detected fallback body in case the loop has no rows
            //
            //       LOOP, ETC
            //           ,IF 0 ROWS, format
            //           ,IF 0 ROWS, etc
            //
            if (if0RowLsxFormat != null)
               addSubCommand (commands, indxPassOpt, if0RowLsxFormat);
         }
         indxPassOpt ++;
      }
      return indxPassOpt;
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
      listixCmdStruct cmdData = new listixCmdStruct (that, commands, indxComm);

      tableAccessBase nova = tableAccessFabrik.create (cmdData);

      if (nova == null) return 1;

      // Note: the names of this Evas are irrelevant, they are not used at all
      Eva inHeadFormat = new Eva ("loop_head_format");  
      Eva inlineFormat = new Eva ("loop_body_format");
      Eva inTailFormat = new Eva ("loop_tail_format");
      Eva inlineIf0RowFormat = new Eva ("loop_onNoRecord_format");

      int indxPassOpt = indxSkipingOptions(commands, indxComm, inHeadFormat, inlineFormat, inTailFormat, inlineIf0RowFormat);

      if (inlineFormat.rows () > 0 || 
          inHeadFormat.rows () > 0 || 
          inTailFormat.rows () > 0 || 
          inlineIf0RowFormat.rows () > 0)
      {
         // embedded loop format found, perform the loop with it
         //
         //       LOOP, ETC
         //           ,, format
         //           ,, etc
         //
         that.log().dbg (4, "LOOP TABLE", "embedded format");
         that.getTableCursorStack ().pushTableCursor (new tableCursor (nova));

         that.getTableCursorStack ().set_RUNTABLE (cmdData);
         runningTables.runLoopTable (that, inHeadFormat, inlineFormat, inTailFormat, inlineIf0RowFormat);
         that.getTableCursorStack ().end_RUNTABLE ();
         return 1;
      }

      //(o) TODO 2014.01.11 03:53 DEPRECATE OLD INLINE BODY OF LOOP TABLE!!!!
      //       uncomment the log error and remove the rest of the method except the last return
      //
      //that.log().err (4, "LOOP TABLE", "No body, header or tail found for the loop, LOOP not set!");
      
      that.log().dbg (4, "LOOP TABLE", "inline format");
        
      // 2013.07.14
      // NOTE: supporting "inline format after options" is a very old feature 
      //        not anymore used since the standard inline format is much better and clear 
      //        "inline format after options" should be deprecated !!
      //
      

      // check if exists inline format after the options, e.g.
      //
      //       LOOP, ETC
      //           , xxx
      //       //format
      //       //etc
      //       ,,
      //       continue...

      int passRows = 0;

      // if there are more lines and they are not commands then
      // the format to run is inline
      if (commands.rows () > indxPassOpt &&
          commands.cols (indxPassOpt) == 1)
      {
         // do format inline
         //
         that.getTableCursorStack ().pushTableCursor (new tableCursor (nova));

         that.getTableCursorStack ().set_RUNTABLE (cmdData);
         passRows = runningTables.runLoopTableInlineAfterOptions (that, "", commands, indxPassOpt);
         that.getTableCursorStack ().end_RUNTABLE ();
      }
      else
      {
         that.log().err ("LOOP TABLE", "No body found for the loop, LOOP not set!");
      }

      return indxPassOpt - indxComm + passRows; // the command was SET TABLE among errors
   }
}
