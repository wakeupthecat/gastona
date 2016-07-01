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
   <name>       BODY WHILE SUB LOOP
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdRunLoopTable
   <importance> 7
   <desc>       //Run or continues a loop changing some conditions

   <help>
      //
      // BODY WHILE SAME applies the specified body until the given column change its value, more precisely
      // until any value of the columns until the specified one change.
      //
      // A good example where to use this construction is in a header-detail report where the select is
      // suppossed to be properly sorted. Using BODY WHILE SAME command such reports can be done using just a single
      // loop, for instance a single SQL select statement.
      //
      //  Example:
      //
      //    Having the table orders (customer, date, product, quantity, price, paid)
      //
      //    A report with header "customer", sub-header "date" and detail "products"
      //    could be done with
      //
      //    <report>
      //       LOOP, SQL,, //SELECT * FROM orders ORDER BY customer, date
      //          ,, //Customer : @<customer>
      //          ,, //
      //          ,, BODY WHILE, customer
      //          ,,           ,, //  Date : @<date>
      //          ,,           ,, //
      //          ,,           ,, BODY WHILE, date
      //          ,,           ,,           ,, //    @<product>, @<quantity> x @<price> = @<paid>
      //
      //
      // An alternative way would be doing three nested loops, for example
      //
      //    LOOP for all customers
      //        LOOP for all dates of a customer
      //           LOOP for all products in a date of a customer
      //
      // this would result into "Number of customers" x "Number of dates per customer" loops
      // and therefore such number of sql queries as well.
      //
      //

   <aliases>
      alias
      "BODY WHILE SAME",
      "BODY WHILE",
      "WHILE",
      "SUB LOOP WHILE",
      "SUB LOOP WHILE SAME",

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    8      , //Performs a sub-loop within the current loop (LOOP TABLE)

   <syntaxParams>
      synIndx, name       , defVal, desc
      1      ,  fieldName ,       , //use the provided sub-body while the value of the record from the first field until fieldName remains the same

   <options>
      synIndx, optionName  , parameters                  , defVal, desc
          1  , LINK        , text4LinkRows               ,       , //Text to be used as link between each two records in the loop. If the option is not specified then the native return is used as link string
          1  , FILTER      , "fieldName, operator, value",       , //Skip records that does not fit the given condition for the field. If more filter given, skip those record that does not fit any of the filters (OR join)
          1  , BODY        , subcommand                  ,       , //Executed on each iteration, note that this is the default option thus the word "BODY" can be omited
          1  , HEAD        , subcommand                  ,       , //Executed (if rows >= 1) before the body execution of the first row. On executing HEAD loop column variables are set to the first row
          1  , TAIL        , subcommand                  ,       , //Executed (if rows >= 1) after the body execution of the last row. On executing TAIL there are not loop column variables set
          1  , IF NO RECORD, subcommand                  ,       , //Executed if the table to loop result in 0 rows that is the table is empty.

   <examples>
      gastSample

      body while agenda sample
      orders detail sample

   <body while agenda sample>
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
      //          ,, WHILE, NAME
      //          ,,      ,, // @<KIND> : ( @<listOfPart> )
      //          ,, //
      //          ,, //-----------------------
      //
      //   <listOfPart>
      //        WHILE, KIND
      //             , LINK, ", "
      //             ,, //@<NUMBER>


   <orders detail sample>
      //#javaj#
      //
      //   <frames> oConsole, "Sample BODY WHILE SAME"
      //
      //#data#
      //
      //   <orders>
      //      customer , date       , product   , quantity, price
      //      suelen   , 2016-03-01 , ruedas    , 1430    , 6320
      //      suelen   , 2016-03-01 , tornillos ,   24    ,   53
      //      suelen   , 2016-03-01 , cables    ,  110    ,   80
      //      pocoyo   , 2016-03-01 , ruedas    , 1430    , 6320
      //      pocoyo   , 2016-03-01 , ruedas    , 1430    , 6320
      //      pocoyo   , 2016-03-01 , ruedas    , 1430    , 6320
      //      suelen   , 2016-03-25 , ruedas    , 1430    , 6320
      //      suelen   , 2016-03-25 , tornillos ,  470    , 6326
      //      comealgo , 2016-07-16 , ruedas    ,    2    , 6500
      //      comealgo , 2016-07-16 , bandejas  ,   55    ,   43
      //      comealgo , 2016-07-16 , yogures   ,  130    ,    3
      //
      //#listix#
      //
      //   <main0> LSX, report
      //
      //   <report>
      //       LOOP, VAR, orders
      //          ,, //Customer : @<customer>
      //          ,, //
      //          ,, BODY WHILE, customer
      //          ,,           ,, //  Date : @<date>
      //          ,,           ,, //
      //          ,,           ,, BODY WHILE, date
      //          ,,           ,,           ,, //    @<product>, @<quantity> x @<price>
      //

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

public class cmdBodyWhileSubLoop implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "BODY WHILE SAME",
          "BODY WHILE",
          "WHILE",
          "SUB LOOP WHILE",
          "SUB LOOP WHILE SAME",
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
         cmdData.getLog().err  ("tableAccessEva", "Too many arguments in command BODY WHILE SAME! last " + (cmdData.getArgSize()-1) + " discarded!" );
      }

      tableRunner taru = new tableRunner (cmdData);
      if (taru.hasContents ())
      {
         that.getTableCursorStack ().set_RUNTABLE (cmdData, tableCursor.TPIVOT_WHILE, cmdData.getArg(0));
         taru.doLoopTable ();
         that.getTableCursorStack ().end_RUNTABLE ();
      }

      cmdData.checkRemainingOptions ();
      return 1;
   }
}
