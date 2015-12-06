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
      // Loops are performed using the commands LOOP TABLE and RUN LOOP. All lopps do basically the
      // same: execute a format (body) for each row of a table. The table can be a real table (e.g.
      // command syntaxes EVA and SQL) or a virtual one (syntaxes FILES, FOR and COLUMNS).
      //
      // During the loop the format being executed can acces the values of the columns, specifically
      // following variables can be called
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
      // Since BODY is the default option for this command, we just can omit the word BODY
      //
      //          LOOP, arguments...
      //              ,, //first line of body
      //              ,, //second line etc
      //
      // Using the option BODY (or default) is the recomended way of specifying the body to execute.
      // If the body is complex or big, it is always possible to place it in a separate format and
      // just use a line to call it, for example
      //
      //          LOOP, arguments...
      //              ,, @<my body>
      //
      // There is still another way of specifying the body of a loop. If no BODY option is used
      // the text found after the last option until the end of the format or until another command
      // will be used as body of the loop. For example
      //
      //          LOOP, arguments...
      //              , option
      //              , ...
      //          // --------------------
      //          // | body of the loop |
      //          // --------------------
      //          =,,
      //
      // Though this form is old and has no advantages but drawbacks, for instance it is not possible
      // to include directly a command on it. Therefore, it will be probably deprecated.
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
      "START TABLE",
      "SET TABLE",
      "TABLE",
      "TABLA",

   <syntaxHeader>
      synIndx, importance, desc
      1      ,    8      ,  //Sets the table defined in the Eva variable 'evaName'
      2      ,    8      ,  //Sets the table defined in the SQL query 'SelectQuery' from the sqlite database 'sqliteDBName'
      3      ,    3      ,  //Set a table of the files found in 'path' that have of the given extensions
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
      3      , extension    ,             , //File name extension to be included
      3      , ...          ,             , //Further extensions are possible

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
          1  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          1  , WHILE SAME  , fieldName                   ,       , //continue the loop while the value of fieldName remains the same (or until the value of fieldName changes)
          1  , ON DIFFERENT, fieldName                   ,       , //make the loop only on different values of fieldName (skip records with same value)
          1  , FILTER      , "fieldName, operator, value",       , //Skip records that do not meet the given condition for the field. If more filters are given, skip those records that do not meet any of the filters (OR join)
          1  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited

          2  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
<!//(o) TODO_previousSQL dbFeature (documented it when all tests ok)
<!          2  , PREVIOUSSQL , sqlCommand                  ,       , //If specified the given sql command (or commands) will be called before the sqlQuery. Useful for example for sql commands like ATTACH, CREATE TEMP VIEW etc.
          2  , WHILE SAME  , fieldName                   ,       , //continue the loop while the value of fieldName remains the same (or until the value of fieldName changes)
          2  , ON DIFFERENT, fieldName                   ,       , //make the loop only on different values of fieldName (skip records with same value)
          2  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          2  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited

          3  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          3  , WHILE SAME  , fieldName                   ,       , //continue the loop while the value of fieldName remains the same (or until the value of fieldName changes)
          3  , ON DIFFERENT, fieldName                   ,       , //make the loop only on different values of fieldName (skip records with same value)
          3  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          3  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited
          3  , RECURSIVE   , 0 / 1                       ,  1    , //If has to recurse subdirectories (default is 1)

          4  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          4  , WHILE SAME  , fieldName                   ,       , //continue the loop while the value of fieldName remains the same (or until the value of fieldName changes)
          4  , ON DIFFERENT, fieldName                   ,       , //make the loop only on different values of fieldName (skip records with same value)
          4  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          4  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited

          5  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          5  , WHILE SAME  , fieldName                   ,       , //continue the loop while the value of fieldName remains the same (or until the value of fieldName changes)
          5  , ON DIFFERENT, fieldName                   ,       , //make the loop only on different values of fieldName (skip records with same value)
          5  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          5  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited

          6  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          6  , WHILE SAME  , fieldName                   ,       , //continue the loop while the value of fieldName remains the same (or until the value of fieldName changes)
          6  , ON DIFFERENT, fieldName                   ,       , //make the loop only on different values of fieldName (skip records with same value)
          6  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          6  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited


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

   // this method is public in order to share it with the class cmdRunLoopTable
   //
   public static int indxSkipingOptions (Eva commands, int indxComm, Eva embeddedFormat)
   {
      // go through the options of the loop ...
      //
      int indxPassOpt = indxComm + 1;
      while (indxPassOpt < commands.rows () &&
             commands.cols(indxPassOpt) > 1 &&
             commands.getValue(indxPassOpt, 0).equals("")
             )
      {
         if (commands.getValue(indxPassOpt, 1).equals("") ||
             commands.getValue(indxPassOpt, 1).equalsIgnoreCase("BODY"))
         {
            // detected body format within the options (embedded loop format), e.g.
            //
            //       LOOP, ETC
            //           ,, format
            //           ,, etc
            //
            int row = embeddedFormat.rows ();
            embeddedFormat.setValue ("", row, 0); // ensure at least an empty line
            for (int ii = 2; ii < commands.cols (indxPassOpt); ii ++)
            {
               embeddedFormat.setValue (commands.getValue(indxPassOpt, ii), row, ii - 2);
            }
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

      Eva inlineFormat = new Eva ("loop_embedded_format");
      int indxPassOpt = indxSkipingOptions(commands, indxComm, inlineFormat);

      if (inlineFormat.rows () > 0)
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
         runningTables.runLoopTable (that, inlineFormat);
         that.getTableCursorStack ().end_RUNTABLE ();
         return 1;
      }

      that.log().dbg (4, "LOOP TABLE", "inline format");


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
         passRows = runningTables.runLoopTable (that, "", commands, indxPassOpt);
         that.getTableCursorStack ().end_RUNTABLE ();
      }
      else
      {
         that.log().err ("LOOP TABLE", "No body found for the loop, LOOP not set!");
      }

      return indxPassOpt - indxComm + passRows; // the command was SET TABLE among errors
   }
}
