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
   //(o) WelcomeGastona_source_listix_command RUN LOOP

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       RUN LOOP
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdRunLoopTable
   <importance> 7
   <desc>       //Run or continues a loop changing some conditions

   <help>
      //
      // RUN LOOP continues a given loop changing the loop conditions for it. Note that is not really
      // a nested loop (only continues, actually it does not start any loop). It is mainly thought for
      // header-detail reports, where the table is suppossed to contain sorted header-detail information.
      // It is a way to do such reports using just a single loop, for instance
      // if we build the following table about sales of products (e.g. using SQL)
      //
      //       customer, date, product, quantity, price, paid
      //
      // we could make a report with header "customer", sub-header "date" and detail "products"
      // using a single loop. In this case this can also be achived using three nested loops, but
      // this would result into "Number of customers" x "Number of dates per customer" loops!
      //
      // The RUN LOOP commands for this example would be
      //
      //    <format for report>
      //       //Customer : @<customer>
      //       //
      //       RUN LOOP, format for header date,
      //               , WHILE HEADER, customer
      //
      //    <format for header date>
      //       //Date : @<date>
      //       //
      //       RUN LOOP, format for products,
      //               , WHILE HEADER, date
      //
      // The options WHILE HEADER and DIFFERENT HEADER become a single column name as parameter
      // but the meaning actually is "either the column or one of the previous columns keep/change its
      // values", therefore it is important that the table is sorted by the desired header and sub-headers.
      //
      // The first argument of RUN LOOP is a format name (eva variable name), it can be left in blank
      // if wanted to inline it, but note that still two columns are needed. For example
      //
      //       RUN LOOP,,
      //       //inline format
      //       //etc..
      //       =,,
      //
      //


   <aliases>
         alias
         "DO LOOP",
         "RUN TABLE",
         "RUN",
         "RUN LOOP",
         "SUB LOOP",

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    8      , //Performs a sub-loop within the current loop (LOOP TABLE)

   <syntaxParams>
      synIndx, name          , defVal, desc
      1      ,  listixFormat ,       , //Listix format to be printed out during the current sub-loop

   <options>
      synIndx, optionName  , parameters                  , defVal, desc
          1  , WHILE SAME  , fieldName                   ,       , //continue the sub-loop while the value of fieldName remains the same (or until the value of fieldName changes)
          1  , ON DIFFERENT, fieldName                   ,       , //make the sub-loop only on different values of fieldName (skip records with same value)
          1  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          1  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)


   <examples>
      gastSample

      simple run loop
      run loop agenda sample

   <simple run loop>
      //#javaj#
      //
      //   <frames> oConsole, "Simple RUN LOOP"
      //
      //#data#
      //
      //   <items>
      //      NAME
      //      one
      //      two
      //      three
      //
      //#listix#
      //
      //   <main0>
      //      //The items are :
      //      LOOP, EVA, items
      //      @<run it>
      //
      //   <run it>
      //      RUN LOOP, one item,
      //              , LINK, ", "
      //
      //   <one item> //[@<NAME>]

   <run loop agenda sample>
      //#javaj#
      //
      //   <frames> oConsole, "Sample RUN LOOP"
      //
      //#data#
      //
      //   <telephones>
      //      NAME  , KIND   , NUMBER
      //      peter , private, 111
      //      peter , private, 222
      //      peter , private, 333
      //      peter , work   , 444
      //      paul  , work   , 555
      //      mary  , private, 666
      //      mary  , private, 777
      //      mary  , work   , 888
      //      mary  , work   , 999
      //
      //#listix#
      //
      //   <main0>
      //      //I have the telephone numbers of @<people>
      //      //
      //      //Hier is my book:
      //      //
      //      @<show agenda>
      //
      //   <people>
      //      LOOP, EVA, telephones
      //          , DIFFERENT HEADER, NAME
      //          , LINK, ", "
      //      @<NAME>
      //
      //   <show agenda>
      //      LOOP, EVA, telephones
      //      //Telephones of @<NAME> :
      //      //
      //      //@<listOfAll>
      //      //
      //      //-----------------------
      //
      //   <listOfAll>
      //      RUN LOOP,,
      //              , WHILE HEADER, NAME
      //      // @<KIND> : ( @<listOfPart> )
      //
      //   <listOfPart>
      //      RUN LOOP,,
      //              , WHILE HEADER, KIND
      //              , LINK, ", "
      //      //@<NUMBER>


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdRunLoopTable implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "RUN LOOP",
          "SUB LOOP",

          "DO LOOP",
          "RUN TABLE",
          "RUN",
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
      listixCmdStruct cmdData = new listixCmdStruct (that, commands, indxComm);

      if (cmdData.getArgSize() > 1)
      {
         cmdData.getLog().err  ("tableAccessEva", "Too many arguments in command LOOP, EVA! last " + (cmdData.getArgSize()-1) + " discarded!" );
      }


      Eva inlineFormat = new Eva ("loop_embedded_format");
      int indxPassOpt = cmdLoopTable.indxSkipingOptions(commands, indxComm, inlineFormat);

      if (inlineFormat.rows () > 0)
      {
         // embedded loop format found, perform the loop with it
         //
         //       LOOP, ETC
         //           ,, format
         //           ,, etc
         //
         that.log().dbg (4, "RUN LOOP", "embedded format");
         that.getTableCursorStack ().set_RUNTABLE (cmdData);
         runningTables.runLoopTable (that, inlineFormat);
         that.getTableCursorStack ().end_RUNTABLE ();
         return 1;
      }

      //
      // do format inline
      //

      //
      // RUN TABLE,
      //
      that.getTableCursorStack ().set_RUNTABLE (cmdData); // comm, commands, indxComm);

      //    RUN TABLE, lsxFormat (eva)
      //
      String lsxFormatName = cmdData.getArg(0);
      if (!lsxFormatName.equals (""))
      {
         that.log().dbg (4, "RUN LOOP", "explicit format [" + lsxFormatName + "]");
      }
      else
      {
         if (commands.rows () > indxPassOpt && commands.cols (indxPassOpt) == 1)
            that.log().dbg (4, "RUN LOOP", "inline format");
         else
            that.log().err ("RUN LOOP", "No body found for the loop, LOOP not performed!");
      }

      int passRows = runningTables.runLoopTable (that, lsxFormatName, commands, indxPassOpt);

      that.getTableCursorStack ().end_RUNTABLE ();

      return indxPassOpt - indxComm + passRows; // the command was SET TABLE among errors
   }
}
