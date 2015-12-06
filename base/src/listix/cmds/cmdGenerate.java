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
      // To generate a text file from a listix format. Actually text generation is a constant
      // activity of listix even when used for other proposes like setting variables etc. Indeed
      // listix was born as a command line "universal text generator", which means a text generator
      // not specialized (e.g. in html etc) and enough general to permit the generation of any
      // required text file. The mechanism of generation is partially explained in "Introduction to
      // Listix" in this documentation and there is also a PDF which documents the very first version
      // of Listix (lsx) that might help to understand the current one. It is also translated to
      // spanish
      //
      //   http://prdownloads.sourceforge.net/evaformat/lsx_Eva_Generator.pdf?download
      //   http://prdownloads.sourceforge.net/evaformat/lsx%2BEva_Generator_Spanish.pdf?download
      //
      // The most common call to GENERATE can be performed using few arguments, for example
      //
      //       GENERATE, myTextFile.txt, body to Generate
      //
      // where "body to Generate" is supposed to be a format whith the contents to generate. The
      // option PARAMETERS could be interesting if we want the format to be a kind of template.
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
      1      , SET VAR DATA  ,  "EvaName, value"           ,                 , //Affects the data to be used, either the current one or an external unit (option LOAD DATA), setting the variable 'EvaName' with the value 'value'
      1      , PASS VAR DATA ,  "EvaName"                  ,                 , //If using external data (option LOAD DATA), this option might be used to share a variable of the current data with the extern one whithout need to copy it (e.g. SET VAR DATA)
      1      , *TARGET IS    ,  "FILE / EVA"               , FILE            , //Specifies if the target ('file2Gen') is a file (default) or an Eva variable which name will be the value of 'file2Gen'
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
      //   <sysDefaultFonts>  Courier, 12, 0, TextField, TextArea
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
      //      //    private @<varType> m@<varName>;
      //      =,,
      //      //
      //      //
      //      //    // setters and getters
      //      //    //
      //      //
      //      LOOP, EVA, myTable
      //      //    public @<varType> get@<varName> ()
      //      //    {
      //      //       return m@<varName>;
      //      //    }
      //      //
      //      //    public void set@<varName> (@<varType> value)
      //      //    {
      //      //       m@<varName> = value;
      //      //    }
      //      //
      //      =,,
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
      //      //       <tr>
      //      //          <td>@<varName></td>
      //      //          <td>@<varType></td>
      //      //       </tr>
      //      =,,
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
      //      CHECK, LINUX, -->, eBrowser, visible, 0
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
      //      CHECK, LINUX, LAUNCH, //CMD /C start "" "@<tmpGen>"
      //      LAUNCH, //@<eBrowser> file://@<tmpGen>
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

      String file2Gen    = cmd.getArg(0);
      String mainFormat  = cmd.getArg(1);
      String unitFormats = cmd.getArg(2); // DEFAULT #formats#
      String fileFormats = cmd.getArg(3);

      String unitData    = cmd.getArg(4);
      String fileData    = cmd.getArg(5);

      //Minimal check of mainFormat
      //  Note: here we cannot check if it is a valid format, it could be a valid one after loading
      //        a new unit etc..
      if (mainFormat.equals (""))
      {
         cmd.getLog ().err ("GENERATE", "mainFormat cannot be an empty string! (nothing has been generated)");
         return 1;
      }

      // append option
      boolean append = cmd.takeOptionString("APPEND").equals ("1");


      // new line option
      String newLoptString = null;
      String optNL = cmd.takeOptionString("NEWLINE");
      if (optNL.length() > 0)
      {
         if (optNL.equals ("NATIVE"))
            newLoptString = TextFile.NEWLINE_NATIVE;
         else if (optNL.equals ("RTLF"))
            newLoptString = TextFile.NEWLINE_RTLF;
         else if (optNL.equals ("LF"))
            newLoptString = TextFile.NEWLINE_LF;
         else
            cmd.getLog ().err ("GENERATE", "option NEWLINE [" + optNL + "] not valid!");

         if (newLoptString != null)
            cmd.getLog ().dbg (2, "GENERATE", "option NEWLINE [" + optNL + "] set");
      }
      //else System.out.println ("NO tenemos NL!");

      // buscar formatos
      //
      EvaUnit UFormats = cmd.getListix().getGlobalFormats ();

      // Option LOAD FORMATS
      //
      String [] loadFormats = cmd.takeOptionParameters("LOADFORMATS");
      if (loadFormats != null)
      {
         if (loadFormats.length < 1)
         {
            cmd.getLog().err ("GENERATE", "option LOAD FORMATS requires at least one parameter! (at eva [" + commandEva.getName () + "])");
         }
         else
         {
            fileFormats = loadFormats[0];
            unitFormats = loadFormats.length > 1 ? loadFormats[1]: "";
         }
         cmd.getLog().dbg (2, "GENERATE", "option LOAD FORMATS file:\"" + fileFormats + "\"  unit:\"" + unitFormats + "\"");
      }

      if (unitFormats.length () != 0 || fileFormats.length () != 0)
      {
         // default formats name
         //(o) listix_TODO Clear if default format is "formats" or "listix"
         //                Nota: si lo cambio por listix se ha de buscar todos los GEN que asuman que es formats!
         //
         if (unitFormats.length () == 0) unitFormats = "listix";
         if (fileFormats.length () == 0)
         {
            // we have - by the moment - no default listix file
            cmd.getLog().err ("GENERATE", "the parameter fileFormats should be specified!");
         }
         UFormats = EvaFile.loadEvaUnit (fileFormats, unitFormats);
         cmd.getLog().dbg (2, "GENERATE", "loaded unit formats #" + unitFormats + "# from \"" + fileFormats + "\"");
      }

      // buscar data
      //
      EvaUnit UData = cmd.getListix().getGlobalData ();

      // Option LOAD DATA
      //
      String [] loadData = cmd.takeOptionParameters("LOADDATA");
      if (loadData != null)
      {
         if (loadData.length < 1)
         {
            cmd.getLog().err ("GENERATE", "option LOAD DATA requires at least one parameter! (at eva [" + commandEva.getName () + "])");
         }
         else
         {
            fileData = loadData[0];
            unitData = loadData.length > 1 ? loadData[1]: "";
         }
         cmd.getLog().dbg (2, "GENERATE", "option LOAD FORMATS file:\"" + fileData + "\"  unit:\"" + unitData + "\"");
      }

      // Option PARAMS
      //
      String [] params = cmd.takeOptionParameters(new String [] {"PARAMS", "PARAMETERS", "ARGS", "ARGUMENTS"}, true);
      if (params != null)
      {
         if (params.length < 1)
         {
            cmd.getLog().err ("GENERATE", "option PARAMS requires at least one parameter! (at eva [" + commandEva.getName () + "])");
         }
         cmd.getLog().dbg (2, "GENERATE", "option PARAMS, " + params.length + " parameters given");
         //System.out.println ("efetiviwonder !!!!! " + params.length);
      }

      if (unitData.length () != 0 || fileData.length () != 0)
      {
         if (unitData.length () == 0) unitData = "data";

         // if data file not specified try with the same as formats
         if (fileData.length () == 0)
            fileData = fileFormats;

         if (fileData.length () == 0)
         {
            // we have - by the moment - no default listix file
            cmd.getLog().err ("GENERATE", "the parameter fileData should be specified!");
         }
         UData = EvaFile.loadEvaUnit (fileData, unitData);

         if (UData == null)
         {
            cmd.getLog().err ("GENERATE", "failed loading unit data #" + unitData + "# from \"" + fileData + "\"");
            //(o) TOSEE_listix_cmds GENERATE, Loading data fails, continue or not continue ?
            // failed but continue ... ?
         }
         else
         {
            cmd.getLog().dbg (2, "GENERATE", "loaded unit data #" + unitData + "# from \"" + fileData + "\"");
         }
      }

      // SET VAR DATA options
      //    chance to set variables to current data
      //    this is a kind of parameter passing to the generator implemented in this GEN call
      String [] setVarOpt = null;
      while (null != (setVarOpt = cmd.takeOptionParameters("SETVARDATA")))
      {
         if (setVarOpt.length != 2)
            cmd.getLog().err ("GENERATE", "option SET VAR DATA requires 2 parameters!");
         else
         {
            String evaname = cmd.getListix().solveStrAsString (setVarOpt[0]);
            String value   = cmd.getListix().solveStrAsString (setVarOpt[1]);
            Eva targ = UData.getSomeHowEva(evaname);
            targ.clear ();
            targ.setValue (value);
            cmd.getLog().dbg (2, "GENERATE", "set var data <" + evaname + "> with value \"" + value + "\"");
         }
      }

      // PASS VAR DATA option
      //    do not create the variable but reference it directly from the current data
      //    this is a kind of parameter passing to the generator implemented in this GEN call
      while (null != (setVarOpt = cmd.takeOptionParameters("PASSVARDATA")))
      {
         // this option only make sense if LOAD DATA has been set
         if (UData == cmd.getListix().getGlobalData ())
         {
            cmd.getLog().err ("GENERATE", "option PASS VAR DATA set but no external data used! (option LOAD DATA not set)");
            break;
         }

         if (setVarOpt.length != 1)
            cmd.getLog().err ("GENERATE", "option PASS VAR DATA requires 1 parameter!");
         else
         {
            // get the variable name to pass
            String evaname = cmd.getListix().solveStrAsString (setVarOpt[0]);

            // get variable from current data
            Eva evaFromCurrent = cmd.getListix().getGlobalData ().getEva (evaname);
            if (evaFromCurrent == null)
            {
               cmd.getLog().err ("GENERATE", "cannot PASS VAR DATA, variable <" + evaname + "> not found!");
            }
            else
            {
               // get variable from external data
               Eva targ = UData.getSomeHowEva(evaname);

               //reference directly the contents!
               targ.lis_EvaLin = evaFromCurrent.lis_EvaLin;
               cmd.getLog().dbg (2, "GENERATE", "share data variable <" + evaname + ">");
            }
         }
      }

      //    30.06.2008 22:09
      //(o) listix_arquitectura Revisar si realmente es conveniente crear otra instancia de listix aqui
      //                      en lugar de usar directamente la instancia "that" (salvando previamente NewLineString ...)
      //
      listix novo = new listix (UFormats, UData, cmd.getListix().getTableCursorStack (), params);

      novo.setNewLineString (newLoptString);

      lsxWriter.makeFile (novo, mainFormat, file2Gen, cmd.getListix().getGlobalFile (), cmd.getListix().getTargetEva (), append);
      novo.destroy ();

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
