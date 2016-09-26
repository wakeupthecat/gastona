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
      // Invoques a listix format optionally with arguments or parameters. Parameters passed to the format
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

      1      , PARAMS        ,  "p1, [p2, ...]"            ,                 , //Set parameters for listix generation (accesibles through @<p1> @<p2> etc)
      1      , LOAD FORMATS  ,  "file, evaUnit"            , "    , listix"  , //Specifies file and unit for the listix formats to be used in the generation
      1      , LOAD DATA     ,  "file, evaUnit"            , "    , data"    , //Specifies file and unit for the data to be used in the generation
      1      , PUSH VARIABLES,  "file, evaUnit"            , "    , data"    , //Specifies file and unit for the variables (only one string) will be "pushed", that is, added on top of the existing data variables.
      1      , SET VAR DATA  ,  "EvaName, value"           ,                 , //Affects the data to be used, either the current one or an external unit (option LOAD DATA), setting the variable 'EvaName' with the value 'value'
      1      , PASS VAR DATA ,  "EvaName"                  ,                 , //If using external data (option LOAD DATA), this option might be used to share a variable of the current data with the extern one whithout need to copy it (e.g. SET VAR DATA)
      1      , TARGET EVA    ,  "EvaName"                  ,                 , //Specifies the variable name where the listix generation will place the result
      1      , NEW LINE      ,  "(NATIVE) / RT / LF / RTLF", NATIVE          , //Will generate the file using the new line character specified : NATIVE (default) is native to the system, RT return (ascci 13), LF line feed (ascci 10), RFLF return and line feed (13 + 10)
      1      , APPEND        ,  "0 / 1"                    , 0               , //If 1 then the generation will be append at the end of the file

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

   <pushVars example>
      //#javaj#
      //
      //   <frames> oSal
      //   
      //#data#
      //
      //   <sampleFile>
      //      //#data#
      //      //
      //      // <year>     1829
      //      // <name>     Abel's irreducibility theorem
      //      // <refid>    888-8888-8888
      //
      //#listix#
      //
      //    <main>
      //      GEN, :mem dataFile, sampleFile
      //      LSX, processData
      //         , PUSH, :mem dataFile
      //
      //   <processData>
      //      //name is @<name>
      //      //
      //      //all are:
      //      //
      //      //
      //      LOOP, COLUMNS,
      //          ,, //@<columnName> : "@<columnValue>"
      //      //
      //      //possible insert:
      //      //
      //      //INSERT INTO myTable (@<fields>) VALUES (@<values>) ;
      //
      //   <fields>
      //      LOOP, COLUMNS,
      //          , LINK, ","
      //          ,, //@<columnName>
      //
      //   <values>
      //      LOOP, COLUMNS,
      //          , LINK, ","
      //          ,, //'@<:encode-utf8 columnValue>'
      //

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

      parameters4LsxGenerate PAOP = new parameters4LsxGenerate ();

      PAOP.genMainFormat = cmd.getArg (0);

      // prepar params for the format
      PAOP.genParameters = new String [cmd.getArgSize () - 1];
      for (int ii = 1; ii < cmd.getArgSize(); ii ++)
      {
         PAOP.genParameters[ii-1] = cmd.getArg (ii);
      }

      if (!PAOP.evalOptions (cmd))
      {
         cmd.getLog ().severe ("LISTIX", "Cannot eval options!");
         return 1;
      }

      PAOP.executeListix (cmd);
      return 1;
   }
}
