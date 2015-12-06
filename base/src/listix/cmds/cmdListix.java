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
   //(o) WelcomeGastona_source_listix_command LISTIX

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       LISTIX
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdListix
   <importance> 3
   <desc>       //Execute a listix format optionally with parameters

   <help>
      //
      // Invoques a format optionally with arguments or parameters. Parameters passed to the format
      // are accessible using the variables @<p1>, @<p2> etc. These special variables are valid only
      // during the execution of the called format, they can be checked with the command "CHECK, VAR".
      // The command LISTIX is just another syntax of the command GENERATE. The idea is make posible
      // calling a format as it were a procedure.
      //
      //    Example:
      //          ...
      //          LISTIX, doSomething, input.dat, output.txt
      //
      //       <doSomething>
      //          //Analyzing @<p1> and write the results onto @<p2> ...
      //
      //    Sometimes results convenient choosing an adecuate name for such procedures, it just
      //    made the code more clear
      //
      //          LISTIX, "Analyze(in, out)", input.dat, output.txt
      //
      //    or better avoiding commas in the name
      //
      //          LISTIX, Analyze(in out), input.dat, output.txt
      //
      //       <Analyze(in out)>
      //          //Analyzing etc...
      //

   <aliases>
      alias
      LSX
      DO FORMAT

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Call a loaded listix format with parameters or arguments

   <syntaxParams>
      synIndx, name         , defVal , desc
         1   , lsxFormat    ,        , //Listix format to "call"
         1   , param1       ,        , //parameter
         1   , ...          ,        , //

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample

      cmd listix example
      cmd listix example2

   <cmd listix example>
      //#javaj#
      //
      //   <frames> oConsole
      //
      //#listix#
      //
      //   <showThis>
      //      DUMP, listix
      //      //------------------------------------
      //      //
      //
      //   <main0>
      //      @<showThis>
      //      LISTIX, doSomething, Chicago, Dallas
      //
      //   <doSomething>
      //      //I will do something with "@<p1>" and "@<p2>"
      //      //@<viva1>
      //      LISTIX, viva1, Barcelona
      //      "and "
      //      LISTIX, viva1, @<p2>
      //
      //   <viva1>
      //      //@<p1> is a very nice city!
      //      //

   <cmd listix example2>
      //#javaj#
      //
      //   <frames> oConsole, listix command LISTIX example
      //
      //#listix#
      //
      //   <main0>
      //       LISTIX, rectangle (a b), 18  , 6
      //       LISTIX, rectangle (a b), 2.71, 0.92
      //       LISTIX, rectangle (a b), Mrs Robinson, Joe DiMaggio
      //
      //   <rectangle (a b)>
      //      "The area of a rectangle @<p1> x @<p2> is "
      //      =, p1*p2
      //      // m^2
      //      //

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.TextFile;

public class cmdListix implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "LISTIX",
            "LSX",
            "DOFORMAT",
         };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      if (cmd.getArgSize () < 1)
      {
         that.log().err ("LISTIX", "No parameters, nothing to do!!");
         return 1;
      }

      // prepar params for the format
      String [] params = new String [cmd.getArgSize () - 1];
      for (int ii = 1; ii < cmd.getArgSize(); ii ++)
      {
         params[ii-1] = cmd.getArg (ii);
      }

      // store the key stack level
      //
      int oldStackLevel = cmd.getListix ().getStackDepthZero4Parameters();
      cmd.getListix ().setStackDepthZero4Parameters (cmd.getListix ().getTableCursorStack ().getDepth());

      // push the parameters in the stack
      //
      boolean beenPushed = false;
      if (params.length > 0)
      {
         cmd.getLog ().dbg (2, "LISTIX", "params length " + params.length);
         cmd.getListix ().getTableCursorStack ().pushTableCursor(new tableCursor(new tableAccessParams (params)));
         beenPushed = true;
      }

      // call the listix format
      //
      cmd.getListix ().printLsxFormat (cmd.getArg (0));

      // restore the key stack level and pop the param table if needed
      //
      cmd.getListix ().setStackDepthZero4Parameters (oldStackLevel);

      if (beenPushed)
         cmd.getListix ().getTableCursorStack ().end_RUNTABLE ();

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
