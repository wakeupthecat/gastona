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

   public String genPushDataUnitName  = "";
   public String genPushDataFileName  = "";

   public String genNewLineString = null;

   public String [] genParameters = null;

   public Eva genTargetVarEva = null;

   public EvaUnit genFormatsEvaUnit = null;
   public EvaUnit genDataEvaUnit = null;
   public EvaUnit genPushDataEvaUnit = null;

   public boolean genAppendToFile = false;

   //protected listixCmdStruct genCmd;
   public boolean StrictOptionsErrors = false;
   
   //(o) NOTES/cmdListix/Complicated code
   // actually here we need a method that returns both if error occurs (boolean) and evaUnit if loaded and if not null
   // we do some trick to get the information anyway
   //
   //      EvaUnit novaEU = new EvaUnit("novaEU");
   //      novaEU.lis_Evas = null; // trick to detect if it has been assigned or not
   //      ...
   //
   // see uses of errorLoadingEvaUnit
   // change it if find a better way...
   //
   private boolean errorLoadingEvaUnit (listixCmdStruct cmd, EvaUnit eu, String fileName, String defaultFileName, String unitName, String defaultUnitName)
   {
      if (unitName.length () == 0 && fileName.length () == 0)
         return false; // NO ERROR
      if (unitName.length () == 0) unitName = defaultUnitName;
      if (fileName.length () == 0) fileName = defaultFileName;
      if (fileName.length () == 0)
      {
         // we have - by the moment - no default listix file
         cmd.getLog().err (cmd.commandName, "parameter fileData or fileFormats has to be specified!");
         if (StrictOptionsErrors) return true; // ERROR
      }

      EvaUnit euLoad = EvaFile.loadEvaUnit (fileName, unitName);

      if (euLoad == null)
      {
         cmd.getLog().err (cmd.commandName, "failed loading unit #" + unitName + "# from \"" + fileName + "\"");
         if (StrictOptionsErrors) return true; // ERROR
      }
      else
      {
         eu.setAsReferenceOf (euLoad);
         cmd.getLog().dbg (2, cmd.commandName, "loaded unit #" + unitName + "# from \"" + fileName + "\"");
      }
      return false;  // NO ERROR!
   }

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
            cmd.getLog ().err (cmd.commandName, "option NEWLINE [" + optNL + "] not valid!");
            if (StrictOptionsErrors) return false;
         }

         if (genNewLineString != null)
            cmd.getLog ().dbg (2, cmd.commandName, "option NEWLINE [" + optNL + "] set");
      }

      // Option LOAD FORMATS
      //
      String [] loadFormats = cmd.takeOptionParameters("LOADFORMATS");
      if (loadFormats != null)
      {
         if (loadFormats.length < 1)
         {
            cmd.getLog().err (cmd.commandName, "option LOAD FORMATS requires at least one parameter!");
            if (StrictOptionsErrors) return false;
         }
         else
         {
            genFormatsFileName = loadFormats[0];
            genFormatsUnitName = loadFormats.length > 1 ? loadFormats[1]: "";
         }
         cmd.getLog().dbg (2, cmd.commandName, "option LOAD FORMATS file:\"" + genFormatsFileName + "\"  unit:\"" + genFormatsUnitName + "\"");
      }

      // load evaUnit for LOAD DATA
      //
      // default formats unit = the current one
      //
      EvaUnit novaEU = new EvaUnit("novaEU");
      novaEU.lis_Evas = null; // trick to detect if it has been assigned or not
      if (errorLoadingEvaUnit (cmd,
                               novaEU,
                               genFormatsFileName, "",
                               genFormatsUnitName, "listix"))
      {
         return false;
      }
      genFormatsEvaUnit = (novaEU.lis_Evas != null) ? novaEU: cmd.getListix().getGlobalFormats ();
      

      // Option LOAD DATA
      //
      {
         String [] loadData = cmd.takeOptionParameters("LOADDATA");
         if (loadData != null)
         {
            if (loadData.length < 1)
            {
               cmd.getLog().err (cmd.commandName, "option LOAD DATA requires at least one parameter!");
               if (StrictOptionsErrors) return false;
            }
            else
            {
               genDataFileName = loadData[0];
               genDataUnitName = loadData.length > 1 ? loadData[1]: "";
            }
            cmd.getLog().dbg (2, cmd.commandName, "option LOAD DATA file:\"" + genDataFileName + "\"  unit:\"" + genDataUnitName + "\"");
         }
      }

      // Option PUSH VARIABLES
      //
      {
         String [] pushData = cmd.takeOptionParameters(new String [] { "PUSH", "PUSHVARS", "PUSHVARIABLES" });
         if (pushData != null)
         {
            if (pushData.length < 1)
            {
               cmd.getLog().err (cmd.commandName, "option PUSH VARIABLES requires at least one parameter!");
               if (StrictOptionsErrors) return false;
            }
            else
            {
               genPushDataFileName = pushData[0];
               genPushDataUnitName = pushData.length > 1 ? pushData[1]: "";
            }
            cmd.getLog().dbg (2, cmd.commandName, "option PUSH VARIABLES file:\"" + genPushDataFileName + "\"  unit:\"" + genPushDataUnitName + "\"");
         }
      }

      // Option PARAMS
      //
      String [] pars = null;
      List lisParams = new Vector ();
      while (null !=  (pars = cmd.takeOptionParameters(new String [] {"PARAMS", "PARAMETERS", "ARGS", "ARGUMENTS"}, true)))
      {
         if (pars.length < 1)
         {
            cmd.getLog().err (cmd.commandName, "option PARAMS requires at least one parameter!");
            if (StrictOptionsErrors) return false;
         }
         for (int ii = 0; ii < pars.length; ii ++)
            lisParams.add (pars[ii]);

         cmd.getLog().dbg (2, cmd.commandName, "option PARAMS, " + pars.length + " parameters given");
      }
      if (lisParams.size () > 0)
      {
         // overide the parametres if given before (in command LISTIX are given as arguments)
         genParameters = new String [lisParams.size ()];
         for (int ii = 0; ii < genParameters.length; ii ++)
            genParameters[ii] = (String) lisParams.get(ii);
      }

      // load evaUnit for LOAD DATA
      //
      // default data unit = the current one
      //
      novaEU = new EvaUnit ();
      novaEU.lis_Evas = null; // trick to detect if it has been assigned or not
      if (errorLoadingEvaUnit (cmd,
                               novaEU,
                               genDataFileName, genFormatsFileName,
                               genDataUnitName, "data"))
      {
         return false;
      }
      genDataEvaUnit = (novaEU.lis_Evas != null) ? novaEU: cmd.getListix().getGlobalData ();
      

      // load evaUnit for PUSH VARIABLES
      //
      novaEU = new EvaUnit ();
      novaEU.lis_Evas = null; // trick to detect if it has been assigned or not
      if (errorLoadingEvaUnit (cmd,
                               novaEU,
                               genPushDataFileName, genFormatsFileName,
                               genPushDataUnitName, "data"))
      {
         return false;
      }
      genPushDataEvaUnit = (novaEU.lis_Evas != null) ? novaEU: null;

      // SET VAR DATA options
      //    chance to set variables to current data
      //    this is a kind of parameter passing to the generator implemented in this GEN call
      String [] setVarOpt = null;
      while (null != (setVarOpt = cmd.takeOptionParameters(new String [] { "SETVARDATA", "SETVAR", "VAR=" })))
      {
         if (setVarOpt.length != 2)
         {
            cmd.getLog().err (cmd.commandName, "option SET VAR DATA requires 2 parameters!");
            if (StrictOptionsErrors) return false;
         }
         else
         {
            String evaname = cmd.getListix().solveStrAsString (setVarOpt[0]);
            String value   = cmd.getListix().solveStrAsString (setVarOpt[1]);
            Eva targ = genDataEvaUnit.getSomeHowEva(evaname);
            targ.setValueVar (value);
            cmd.getLog().dbg (2, cmd.commandName, "set var data <" + evaname + "> with value \"" + value + "\"");
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
            cmd.getLog().err (cmd.commandName, "option PASS VAR DATA set but no external data used! (option LOAD DATA not set)");
            if (StrictOptionsErrors) return false;
            break;
         }

         if (setVarOpt.length != 1)
         {
            cmd.getLog().err (cmd.commandName, "option PASS VAR DATA requires 1 parameter!");
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
               cmd.getLog().err (cmd.commandName, "cannot PASS VAR DATA, variable <" + evaname + "> not found!");
               if (StrictOptionsErrors) return false;
            }
            else
            {
               // get variable from external data
               Eva targ = genDataEvaUnit.getSomeHowEva(evaname);

               //reference directly the contents!
               targ.lis_EvaLin = evaFromCurrent.lis_EvaLin;
               cmd.getLog().dbg (2, cmd.commandName, "share data variable <" + evaname + ">");
            }
         }
      }

      // targetFile
      String targetFileName = cmd.takeOptionString(new String [] { "TARGETFILE", "TOFILE" }, null);
      if (targetFileName != null && targetFileName.length () > 0)
      {
         genFileToGenerate = targetFileName;
         cmd.getLog().dbg (2, cmd.commandName, "option TARGET FILE [" + targetFileName + "]");
      }

      // targetEva
      String targetEvaName = cmd.takeOptionString(new String [] { "TARGETVAR", "TARGETEVA", "TOVAR", "TOEVA" }, null);
      if (targetEvaName != null && targetEvaName.length () > 0)
      {
         genTargetVarEva = genDataEvaUnit.getSomeHowEva(targetEvaName);
         cmd.getLog().dbg (2, cmd.commandName, "option TARGET VAR [" + targetEvaName + "]");
      }

      int remainOpt = cmd.checkRemainingOptions ();
      return (StrictOptionsErrors && remainOpt != 0) ? false: true;
   }

   public void executeListix (listixCmdStruct cmd)
   {
      tableCursorStack tabstack = cmd.getListix().getTableCursorStack ();
      
      boolean withPushData = genPushDataEvaUnit != null && genPushDataEvaUnit.size () > 0;

      if (withPushData)
      {
         int col = 0;
         Eva eparams = new Eva ("push-data-eva"); // name is not relevant

         for (int ii = 0; ii < genPushDataEvaUnit.size (); ii ++)
         {
            Eva eva = genPushDataEvaUnit.getEva (ii);
            eparams.setValue (eva.getName (), 0, col);
            eparams.setValue (eva.getValue (), 1, col++);
         }
         tableAccessEva tablePars = new tableAccessEva (); // it is a tableAccessBase class
         tablePars.evaData = eparams;
         tabstack.pushTableCursor (new tableCursor (tablePars));
      }


      // call the listix format
      //
      listix novoLsx = new listix (genFormatsEvaUnit,
                                   genDataEvaUnit,
                                   tabstack,
                                   genParameters);

      novoLsx.setNewLineString (genNewLineString);

      lsxWriter.makeFile (novoLsx,
                          genMainFormat,
                          genFileToGenerate,
                          cmd.getListix().getGlobalFile (),
                          cmd.getListix().getTargetEva (),
                          genAppendToFile);
      novoLsx.destroy ();

      if (withPushData)
      {
         tabstack.popTableCursor ();
      }
   }
}
