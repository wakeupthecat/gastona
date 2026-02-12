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
   //(o) WelcomeGastona_source_listix_command RUN LOOP

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       PARTIAL LOOP
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdRunLoopTable
   <importance> 7
   <desc>       //Run or continues a loop changing some conditions

   <help>
      //
      //PARTIAL LOOP or SUB LOOP continues a given loop changing the loop conditions for it.
      //
      //Note that is not really a nested loop actually it does not start any loop but do either:
      //
      //   - continue the current one until one column change its value (option WHILE SAME)
      //or
      //   - skips rows while a column has the same value (option ON DIFFERENT)
      //
      //It is mainly thought for header-detail reports, where the table is supposed to contain sorted
      //header-detail information. Using PARTIAL LOOP command such reports can be done using just a single
      //loop, for instance if we build the following table about sales of products (e.g. using SQL)
      //
      //       customer, date, product, quantity, price, paid
      //
      //we could make a report with header "customer", sub-header "date" and detail "products"
      //using a single loop. For example:
      //
      //    <main loop>
      //       LOOP, SQL,, //SELECT * FROM orders
      //          ,, //Customer : @<customer>
      //          ,, //
      //          ,, PARTIAL LOOP, @<formatForSameDate>
      //          ,,             , WHILE, customer
      //
      //    <formatForSameDate>
      //       //  Date : @<date>
      //       //
      //       PARTIAL LOOP,,
      //                   , WHILE, date
      //                   ,, //    @<product>, @<quantity> x @<price> = @<paid>
      //
      //
      //An alternative way would be doing three nested loops, for example
      //
      //    LOOP for all customers
      //        LOOP for all dates of a customer
      //           LOOP for all products in a date of a customer
      //
      //this would result into "Number of customers" x "Number of dates per customer" loops
      //and therefore such number of sql queries as well (1: note of readability).
      //
      //The options WHILE HEADER and DIFFERENT HEADER have a single column name as parameter
      //but the meaning actually is "either the column or one of the previous columns keep/change its
      //values", therefore it is important that the table is sorted by the desired header and sub-headers.
      //
      //The first argument of RUN LOOP is a format name (eva variable name) for the loop body, it can be left in blank
      //if wanted to inline the body but still two columns are needed for the command. For example
      //
      //       PARTIAL LOOP,,
      //             ,, //inline format
      //             ,, //etc..
      //
      //--- (1) Note of readability
      //
      //Nevertheless, it has to be say that nested loops, although at the cost of performance, is still more intuitive
      //and readable than using PARTIAL LOOP. To improve the the partial loop idea it could help having two separate commands,
      //for instance: "SKIP ROWS WITH SAME" and "LOOP BODY WHILE" instead on only one command with two syntaxes.
      //

   <aliases>
         alias
         "PARTIAL LOOP",
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
          x  , WHILE SAME  , fieldName                   ,       , //continue the sub-loop while the value of fieldName remains the same (or until the value of fieldName changes)
          x  , ON DIFFERENT, fieldName                   ,       , //make the sub-loop only on different values of fieldName (skip records with same value)
          x  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          x  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          x  , IF 0 ROWS   , subcommand                  ,       , //Executed if the table to loop result in 0 rows (actually no loop at all)


   <examples>
      gastSample

      run loop agenda sample

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
      //      //I have the telephone numbers of @<show people>
      //      //
      //      //Hier is my book:
      //      //
      //      @<show agenda>
      //
      //   <show people>
      //      LOOP, EVA, telephones
      //          , DIFFERENT HEADER, NAME
      //          , LINK, ", "
      //          ,, @<NAME>
      //
      //   <show agenda>
      //      LOOP, EVA, telephones
      //          ,, //Telephones of @<NAME> :
      //          ,, //
      //          ,, //@<listOfAll>
      //          ,, //
      //          ,, //-----------------------
      //
      //   <listOfAll>
      //      PARTIAL LOOP,,
      //                  , WHILE, NAME
      //                  ,, // @<KIND> : ( @<listOfPart> )
      //
      //   <listOfPart>
      //      PARTIAL LOOP,,
      //                  , WHILE, KIND
      //                  , LINK, ", "
      //                  ,, //@<NUMBER>


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import listix.table.*;
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
          "PARTIAL LOOP",
          "PARCIAL LOOP",

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

      tableRunner taru = new tableRunner (cmdData);

      // support the original syntax of RUN LOOP, where the first parameter is the format name
      //
      //    RUN TABLE, lsxFormat (eva)
      //
      taru.setBodyFormatName (cmdData.getArg(0));

      if (taru.hasContents ())
      {
         that.getTableCursorStack ().set_RUNTABLE (cmdData);
         taru.doLoopTable ();
         that.getTableCursorStack ().end_RUNTABLE ();
      }

      cmdData.checkRemainingOptions ();
      return 1;
   }
}
