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

package listix.cmds;

import java.util.*;
import listix.listix;
import listix.listixCmdStruct;
import listix.lsxWriter;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.TextFile;

/**
   cmdListix and cmdGenerate use the same set of parameters
   and the same options. This class handle them

   NOTE:
      07.01.2012
      My first approach was to extend the class, for example
         class cmdListix extends parameters4LsxGenerate ...

      It does NOT WORK!! reason: cmdListix and in general any
      listix command can be called recursivelly, they must not have
      member variables!


      a test where the problem appeared is

      #javaj#

         <frames>
             oConsole, "Test of command LSX"

      #listix#

         <main>
            //The expected result is "Correcto y no FALSO!"
            //
            LSX, FINAL, @<LLAMADA2>

         <LLAMADA2>
            LSX, LLAMADA3

         <LLAMADA3> FALSO!

         <FINAL> Correcto y no @<p1>


      #**#


*/
public class parameters4LsxGenerate
{
   public String genFileToGenerate = "";
   public String genMainFormat  = "";

   public String genFormatsUnitName = "";
   public String genFormatsFileName = "";

   public String genDataUnitName  = "";
   public String genDataFileName  = "";

   public String genNewLineString = null;

   public String [] genParameters = null;

   public Eva genTargetVarEva = null;

   public EvaUnit genFormatsEvaUnit = null;
   public EvaUnit genDataEvaUnit = null;

   public boolean genAppendToFile = false;

   //protected listixCmdStruct genCmd;
   public boolean StrictOptionsErrors = false;

   /**
   */
   public boolean evalOptions (listixCmdStruct cmd)
   {
      // append option
      genAppendToFile = cmd.takeOptionString("APPEND").equals ("1");

      // new line option
      genNewLineString = null;
      String optNL = cmd.takeOptionString("NEWLINE");
      if (optNL.length() > 0)
      {
         if (optNL.equals ("NATIVE"))       genNewLineString = TextFile.NEWLINE_NATIVE;
         else if (optNL.equals ("RTLF"))    genNewLineString = TextFile.NEWLINE_RTLF;
         else if (optNL.equals ("LF"))      genNewLineString = TextFile.NEWLINE_LF10;
         else
         {
            cmd.getLog ().err (cmd.cmdName, "option NEWLINE [" + optNL + "] not valid!");
            if (StrictOptionsErrors) return false;
         }

         if (genNewLineString != null)
            cmd.getLog ().dbg (2, cmd.cmdName, "option NEWLINE [" + optNL + "] set");
      }

      // default formats unit = the current one
      //
      genFormatsEvaUnit = cmd.getListix().getGlobalFormats ();

      // Option LOAD FORMATS
      //
      String [] loadFormats = cmd.takeOptionParameters("LOADFORMATS");
      if (loadFormats != null)
      {
         if (loadFormats.length < 1)
         {
            cmd.getLog().err (cmd.cmdName, "option LOAD FORMATS requires at least one parameter!");
            if (StrictOptionsErrors) return false;
         }
         else
         {
            genFormatsFileName = loadFormats[0];
            genFormatsUnitName = loadFormats.length > 1 ? loadFormats[1]: "";
         }
         cmd.getLog().dbg (2, cmd.cmdName, "option LOAD FORMATS file:\"" + genFormatsFileName + "\"  unit:\"" + genFormatsUnitName + "\"");
      }

      if (genFormatsUnitName.length () != 0 || genFormatsFileName.length () != 0)
      {
         // default formats name
         //(o) listix_TODO Clear if default format is "formats" or "listix"
         //                Nota: si lo cambio por listix se ha de buscar todos los GEN que asuman que es formats!
         //
         if (genFormatsUnitName.length () == 0) genFormatsUnitName = "listix";
         if (genFormatsFileName.length () == 0)
         {
            // we have - by the moment - no default listix file
            cmd.getLog().err (cmd.cmdName, "the parameter fileFormats should be specified!");
            if (StrictOptionsErrors) return false;
         }
         genFormatsEvaUnit = EvaFile.loadEvaUnit (genFormatsFileName, genFormatsUnitName);
         cmd.getLog().dbg (2, cmd.cmdName, "loaded unit formats #" + genFormatsUnitName + "# from \"" + genFormatsFileName + "\"");
      }

      // default data unit = the current one
      //
      genDataEvaUnit = cmd.getListix().getGlobalData ();

      // Option LOAD DATA
      //
      String [] loadData = cmd.takeOptionParameters("LOADDATA");
      if (loadData != null)
      {
         if (loadData.length < 1)
         {
            cmd.getLog().err (cmd.cmdName, "option LOAD DATA requires at least one parameter!");
            if (StrictOptionsErrors) return false;
         }
         else
         {
            genDataFileName = loadData[0];
            genDataUnitName = loadData.length > 1 ? loadData[1]: "";
         }
         cmd.getLog().dbg (2, cmd.cmdName, "option LOAD DATA file:\"" + genDataFileName + "\"  unit:\"" + genDataUnitName + "\"");
      }

      // Option PARAMS
      //
      String [] pars = null;
      List lisParams = new Vector ();
      while (null !=  (pars = cmd.takeOptionParameters(new String [] {"PARAMS", "PARAMETERS", "ARGS", "ARGUMENTS"}, true)))
      {
         if (pars.length < 1)
         {
            cmd.getLog().err (cmd.cmdName, "option PARAMS requires at least one parameter!");
            if (StrictOptionsErrors) return false;
         }
         for (int ii = 0; ii < pars.length; ii ++)
            lisParams.add (pars[ii]);            

         cmd.getLog().dbg (2, cmd.cmdName, "option PARAMS, " + pars.length + " parameters given");
      }
      if (lisParams.size () > 0)
      {
         // overide the parametres if given before (in command LISTIX are given as arguments)
         genParameters = new String [lisParams.size ()];
         for (int ii = 0; ii < genParameters.length; ii ++)
            genParameters[ii] = (String) lisParams.get(ii);
      }

      if (genDataUnitName.length () != 0 || genDataFileName.length () != 0)
      {
         if (genDataUnitName.length () == 0) genDataUnitName = "data";

         // if data file not specified try with the same as formats
         if (genDataFileName.length () == 0)
            genDataFileName = genFormatsFileName;

         if (genDataFileName.length () == 0)
         {
            // we have - by the moment - no default listix file
            cmd.getLog().err (cmd.cmdName, "the parameter fileData should be specified!");
            if (StrictOptionsErrors) return false;
         }
         genDataEvaUnit = EvaFile.loadEvaUnit (genDataFileName, genDataUnitName);

         if (genDataEvaUnit == null)
         {
            cmd.getLog().err (cmd.cmdName, "failed loading unit data #" + genDataUnitName + "# from \"" + genDataFileName + "\"");
            if (StrictOptionsErrors) return false;
            //(o) TOSEE_listix_cmds GENERATE, Loading data fails, continue or not continue ?
            // failed but continue ... ?
         }
         else
         {
            cmd.getLog().dbg (2, cmd.cmdName, "loaded unit data #" + genDataUnitName + "# from \"" + genDataFileName + "\"");
         }
      }

      // SET VAR DATA options
      //    chance to set variables to current data
      //    this is a kind of parameter passing to the generator implemented in this GEN call
      String [] setVarOpt = null;
      while (null != (setVarOpt = cmd.takeOptionParameters(new String [] { "SETVARDATA", "SETVAR", "VAR=" })))
      {
         if (setVarOpt.length != 2)
         {
            cmd.getLog().err (cmd.cmdName, "option SET VAR DATA requires 2 parameters!");
            if (StrictOptionsErrors) return false;
         }
         else
         {
            String evaname = cmd.getListix().solveStrAsString (setVarOpt[0]);
            String value   = cmd.getListix().solveStrAsString (setVarOpt[1]);
            Eva targ = genDataEvaUnit.getSomeHowEva(evaname);
            targ.setValueVar (value);
            cmd.getLog().dbg (2, cmd.cmdName, "set var data <" + evaname + "> with value \"" + value + "\"");
         }
      }

      // PASS VAR DATA option
      //    do not create the variable but reference it directly from the current data
      //    this is a kind of parameter passing to the generator implemented in this GEN call
      while (null != (setVarOpt = cmd.takeOptionParameters(new String [] { "PASSVARDATA", "PASSVAR", "VAR*"} )))
      {
         // this option only make sense if LOAD DATA has been set
         if (genDataEvaUnit == cmd.getListix().getGlobalData ())
         {
            cmd.getLog().err (cmd.cmdName, "option PASS VAR DATA set but no external data used! (option LOAD DATA not set)");
            if (StrictOptionsErrors) return false;
            break;
         }

         if (setVarOpt.length != 1)
         {
            cmd.getLog().err (cmd.cmdName, "option PASS VAR DATA requires 1 parameter!");
            if (StrictOptionsErrors) return false;
         }
         else
         {
            // get the variable name to pass
            String evaname = cmd.getListix().solveStrAsString (setVarOpt[0]);

            // get variable from current data
            Eva evaFromCurrent = cmd.getListix().getGlobalData ().getEva (evaname);
            if (evaFromCurrent == null)
            {
               cmd.getLog().err (cmd.cmdName, "cannot PASS VAR DATA, variable <" + evaname + "> not found!");
               if (StrictOptionsErrors) return false;
            }
            else
            {
               // get variable from external data
               Eva targ = genDataEvaUnit.getSomeHowEva(evaname);

               //reference directly the contents!
               targ.lis_EvaLin = evaFromCurrent.lis_EvaLin;
               cmd.getLog().dbg (2, cmd.cmdName, "share data variable <" + evaname + ">");
            }
         }
      }

      // targetFile
      String targetFileName = cmd.takeOptionString(new String [] { "TARGETFILE", "TOFILE" }, null);
      if (targetFileName != null && targetFileName.length () > 0)
      {
         genFileToGenerate = targetFileName;
         cmd.getLog().dbg (2, cmd.cmdName, "option TARGET FILE [" + targetFileName + "]");
      }

      // targetEva
      String targetEvaName = cmd.takeOptionString(new String [] { "TARGETVAR", "TARGETEVA", "TOVAR", "TOEVA" }, null);
      if (targetEvaName != null && targetEvaName.length () > 0)
      {
         genTargetVarEva = genDataEvaUnit.getSomeHowEva(targetEvaName);
         cmd.getLog().dbg (2, cmd.cmdName, "option TARGET VAR [" + targetEvaName + "]");
      }

      int remainOpt = cmd.checkRemainingOptions (true);
      return (StrictOptionsErrors && remainOpt != 0) ? false: true;
   }
}
