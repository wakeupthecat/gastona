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
   //(o) WelcomeGastona_source_listix_command GENERATE

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    listix_command
   <name>       GENERATE
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdGenerate
   <importance> 5
   <desc>       //Generates a text from a listix format

   <help>
      //
      //To generate a text file from a listix format. Actually text generation is a constant
      //activity of listix even when used for other proposes like setting variables etc. Indeed
      //listix was born as a command line "universal text generator", which means a text generator
      //not specialized (e.g. in html etc) and enough general to permit the generation of any
      //required text file. The mechanism of generation is partially explained in "Introduction to
      //Listix" in this documentation and there is also a PDF which documents the very first version
      //of Listix (lsx) that might help to understand the current one. It is also translated to
      //spanish
      //
      //   http://prdownloads.sourceforge.net/evaformat/lsx_Eva_Generator.pdf?download
      //   http://prdownloads.sourceforge.net/evaformat/lsx%2BEva_Generator_Spanish.pdf?download
      //
      //The most common call to GENERATE can be performed using few arguments, for example
      //
      //       GENERATE, myTextFile.txt, body to Generate
      //
      //where "body to Generate" is supposed to be a format whith the contents to generate. The
      //option PARAMETERS could be interesting if we want the format to be a kind of template.
      //
      //       GENERATE, emailText.txt, emailSalutation
      //               , PARAMS, Joseph, joseph@example.examplecom
      //
      //       <emailSalutation>
      //          // Hello @<p1>,
      //          // Can you confirm that your address is @<p2> ?
      //          // Cheers, me
      //


   <aliases>
      alias
      GENERATE FILE
      GEN
      GEN FILE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Generates the file 'file2Gen' from the listix format 'mainFormat'

   <syntaxParams>
      synIndx, name         , defVal , desc
         1   , file2Gen     , con    , //Name of the file to be created and generated or any of +, - or * to continue with the current file
         1   , mainFormat   , main   , //Listix format containing what to generate
         1   , unitFormats  , listix , //EvaUnit where the listix formats are to be found
         1   , fileFormats  ,        , //File name where the EvaUnit 'unitFormats' is to be found
         1   , unitData     , data   , //EvaUnit where the listix unit data is to be found
         1   , fileData     ,        , //File name where the EvaUnit 'unitData' is to be found



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
      Example GEN 1
      Example GEN 2
      Code generation demo

   <Example GEN 1>
      //#javaj#
      //
      //   <frames>
      //       Fmain, "listix command GENERATE example"
      //
      //   <layout of Fmain>
      //       EVA, 10, 10, 5, 5
      //
      //     ---,  X
      //        , bGenerate
      //      X , oConsole
      //
      //#data#
      //
      //    <html page>
      //       //<html><body>
      //       //    Hello, this has been generated by the listix commnad GENERATE
      //       //</body></html>
      //
      //#listix#
      //
      //    <-- bGenerate>
      //       GENERATE, con, html page
      //

   <Example GEN 2>
      //#javaj#
      //
      //   <frames>
      //       Fmain, "listix command GENERATE example 2"
      //
      //   <layout of Fmain>
      //       EVA, 10, 10, 5, 5
      //
      //     ---,  X
      //        , bGenerate
      //      X , oConsole
      //
      //#data#
      //
      //    <html page>
      //       //<html><body>
      //       //    Hello @<p1>, this has been generated by the listix commnad GENERATE
      //       //</body></html>
      //
      //#listix#
      //
      //    <-- bGenerate>
      //       GENERATE, con, html page
      //               , PARAMS, @<:sys user.name>
      //


   <Code generation demo>
      //#javaj#
      //
      //   <frames> fmain, Code generation sample
      //
      //   <layout of fmain>
      //      EVA, 10, 10, 7, 7
      //
      //         ,             ,   X
      //         , lClass name , eClassName
      //         , lData
      //       X , xDataVar    , -
      //         , botons      , -
      //         , lGenerated code, -
      //       X , xResult     , -
      //       X , +
      //
      //   <layout of botons>
      //      EVA, 3, 3, 4, 4
      //         ,      ,      ,                , 100
      //         , bJava, bHTML, kLaunch Browser, eBrowser
      //
      //   <sysDefaultFonts>  Consolas, 12, 0, TextField, TextArea
      //
      //
      //#data#
      //
      //   <eBrowser> firefox
      //
      //   <bJava>   Java
      //   <bHTML>   HTML
      //
      //   <bGenerate image> javaj/img/write.png
      //
      //   <eClassName> MyClass
      //
      //   <xDataVar>
      //      // varName     , varType
      //      //
      //      // Identifier  , int
      //      // Name        , String
      //      // Factors     , double []
      //
      //   <Template Java>
      //      ////
      //      ////    class : @<eClassName>
      //      ////    author: @<:sys user.name>
      //      ////    date  : @<:lsx date>
      //      ////    NOTE  : Automatically generated
      //      ////
      //      //public class @<eClassName>
      //      //{
      //      //
      //      //    // member variables
      //      //    //
      //      //
      //      LOOP, EVA, myTable
      //          ,, //    private @<varType> m@<varName>;
      //      //
      //      //
      //      //    // setters and getters
      //      //    //
      //      //
      //      LOOP, EVA, myTable
      //          ,, //    public @<varType> get@<varName> ()
      //          ,, //    {
      //          ,, //       return m@<varName>;
      //          ,, //    }
      //          ,, //
      //          ,, //    public void set@<varName> (@<varType> value)
      //          ,, //    {
      //          ,, //       m@<varName> = value;
      //          ,, //    }
      //          ,, //
      //      //}
      //      //
      //
      //   <Template Html>
      //      //<html>
      //      //   <body>
      //      //   <h2> class Name : <b> @<eClassName> </b></h2>
      //      //   <h3> Author     : <b> @<:sys user.name> </b></h3>
      //      //   <h3> Date       : <b> @<:lsx date> </b></h3>
      //      //
      //      //   <table border=1>
      //      //       <tr>
      //      //          <th>Name</th>
      //      //          <th>Type</th>
      //      //       </tr>
      //      //
      //      LOOP, EVA, myTable
      //          ,, //       <tr>
      //          ,, //          <td>@<varName></td>
      //          ,, //          <td>@<varType></td>
      //          ,, //       </tr>
      //      //
      //      //   </table>
      //      //   </body>
      //      //</html>
      //      //
      //
      //#listix#
      //
      //   <main0>
      //      SETVAR, tmpGen, @<:lsx tmp html>
      //      SETVAR, xResult fileName, @<tmpGen>
      //
      //   <main>
      //      CHECK, LINUX, -->, eBrowser control!, visible, 0
      //
      //   <-- bJava>
      //      LISTIX, doGenerate, Template Java
      //
      //   <-- bHTML>
      //      LISTIX, doGenerate, Template Html
      //      @<lauchBrowser>
      //
      //   <lauchBrowser>
      //      CHECK, VAR, kLaunch Browser selected
      //      CHECK, =, @<kLaunch Browser selected>, 1
      //
      //      BROWSER, @<tmpGen>
      //
      //   <doGenerate>
      //      CHECK, VAR, p1
      //      STRCONV, TEXT-EVA, xDataVar, myTable
      //      GEN, @<tmpGen>, @<p1>
      //      MSG, xResult load

#**FIN_EVA#

*/

package listix.cmds;

import listix.listix;
import listix.listixCmdStruct;
import listix.lsxWriter;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.TextFile;

public class cmdGenerate implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "GENERATE FILE",
            "GENERATE",
            "GEN FILE",
            "GEN",
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


      parameters4LsxGenerate PAOP = new parameters4LsxGenerate ();

      //GENERATE, file2gen, format, listix, file

      PAOP.genFileToGenerate = cmd.getArg(0);
      PAOP.genMainFormat     = cmd.getArg(1);

      PAOP.genFormatsUnitName = cmd.getArg(2); // DEFAULT #formats#
      PAOP.genFormatsFileName = cmd.getArg(3);

      PAOP.genDataUnitName    = cmd.getArg(4);
      PAOP.genDataFileName    = cmd.getArg(5);

      //Minimal check of mainFormat
      //  Note: here we cannot check if it is a valid format, it could be a valid one after loading
      //        a new unit etc..
      if (PAOP.genMainFormat.equals (""))
      {
         cmd.getLog ().err ("GENERATE", "mainFormat cannot be an empty string! (nothing has been generated)");
         return 1;
      }

      if (!PAOP.evalOptions (cmd))
         return 1;

      PAOP.executeListix (cmd);
      return 1;
   }
}
