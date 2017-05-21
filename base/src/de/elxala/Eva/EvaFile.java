/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005  Alejandro Xalabarder Aulet
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.Eva;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import java.util.*;
import java.util.regex.*;
import java.io.File;
import de.elxala.zServices.*;

/**
   class EvaFile
   @author Alejandro Xalabarder Aulet
   @date   2001
   Provides methods to save and retrieve EvaUnit's from text files
   <pre>
   Example:
         EvaUnit euni = EvaFile.loadEvaUnit ("myFile.eva", "myUnit");
         // ... use euni for reading the Eva variables
         // ...
         // ... or change the contents of euni and
         EvaFile.saveEvaUnit ("myFile.eva", euni);
   </pre>
   02.02.2005 12:00 EVA Text File Specification v1.0
*/
public class EvaFile
{
   public String fileName = "";

   public List allUnits = new Vector ();

   private String pars_name = "";
   private String pars_aftername = "";
   private Eva pars_currentEva = null;
   private EvaUnit pars_currentUnit = null;

   private static logger logStatic = null;
   private static logger log ()
   {
      if (logStatic == null)
         logStatic = new logger (null, "de.elxala.Eva.EvaFile", null);
      return logStatic;
   }


   public EvaFile () {}

   public EvaFile (String fileName)
   {
      loadFile (fileName, null);
   }

   public EvaFile (String fileName, String [] units2load)
   {
      loadFile (fileName, units2load);
   }

   /**
      .... compatibility with old EvaFile fashion ...
      Static method to load a EvaUnit named 'UnitName' from the file 'FileName'

      @return the EvaUnit if found and null otherwise
   */
   public static EvaUnit loadEvaUnit (String FileName, String UnitName)
   {
      return (new EvaFile (FileName)).getUnit (UnitName);
   }


   public EvaUnit getUnit (String unitname)
   {
      for (int uu = 0; uu < allUnits.size (); uu ++)
         if (((EvaUnit) allUnits.get (uu)).getName ().equals (unitname))
            return (EvaUnit) allUnits.get (uu);
      return null;
   }

   public EvaUnit getSomehowUnit (String unitname)
   {
      EvaUnit eu = getUnit (unitname);
      if (eu == null)
      {
         eu = new EvaUnit (unitname);
         allUnits.add (eu);
      }
      return eu;
   }

   protected void setCurrentEva (String name)
   {
      if (pars_currentUnit != null && pars_currentEva != null)
         pars_currentUnit.add (pars_currentEva);

      pars_currentEva = (name != null) ? new Eva (name): null;
   }

   protected void setCurrentUnit (String name)
   {
      if (pars_currentUnit != null)
      {
         setCurrentEva (null);
         allUnits.add (pars_currentUnit);
      }
      pars_currentUnit = (name != null) ? new EvaUnit (name): null;
   }

   /*
      Example of use:
      var sal = isname ("< isThis aName?> yes it is   ", "<", ">")
      out ("name = /" + sal.name + "/");
      out ("rest = /" + sal.rest + "/");
      // name = / isThis aName?/
      // rest = /yes it is/
   */
   protected boolean isname (String line, char startch, char endch)
   {
      if (line.charAt (0) != startch) return false;
      int indx = line.indexOf (endch, 1);
      if (indx == -1) return false;

      pars_name = line.substring (1, indx);      // do not trim names
      pars_aftername = line.substring (indx+1).trim ();
      return true;
   }

   protected boolean nameRequired (String name, String [] requestedOnes)
   {
      if (requestedOnes == null) return true; // all

      for (int rr = 0; rr < requestedOnes.length; rr ++)
         if (requestedOnes[rr].equals (name)) return true;
      return false;
   }

   //Example
   //    evaFile ef = new evaFile ();
   //
   public boolean loadFile (String filename, String [] units2Load)
   {
      allUnits = new Vector ();

      fileName = filename;
      TextFile fitx = new TextFile ();
      if (!fitx.fopen (fileName, "r"))
         return false;

      boolean readAllUnits = units2Load == null;
      String linStr = "";

      while (fitx.readLine ())
      {
         linStr = fitx.TheLine().trim ();
         if (linStr.length () == 0) continue; // ignore empty lines

         // check for comment    <! etc ...
         if (linStr.length () > 1 && linStr.charAt(0) == '<' && linStr.charAt(1) == '!') continue;

         // check if start of unit
         //
         if (isname (linStr, '#', '#'))
         {
            if (pars_name.length () > 1 && pars_name.charAt(0) == '*' && pars_name.charAt(1) == '*') break; // logic end of file

            // load only if "all" or found in units2Load, otherwise we close the current unit
            // and wait for the next unit
            if (nameRequired (pars_name, units2Load))
                 setCurrentUnit (pars_name);
            else setCurrentUnit (null);
            continue;
         }
         if (pars_currentUnit == null) continue;

         // check if start of eva
         //
         if (isname (linStr, '<', '>'))
         {
            setCurrentEva (pars_name);
            linStr = pars_aftername;
            if (linStr.length () == 0) continue;
         }

         // line is not empty
         // and we have a current Eva active
         //
         if (linStr.length () > 0 && pars_currentEva != null)
         {
            pars_currentEva.add (new EvaLine (linStr));
         }
         else
         {
            // console.log ("ignoring " + linStr);
         }
      }
      setCurrentUnit (null);
      return true;
   }

   public boolean saveFile (String filename)
   {
      return saveFile (filename, null);
   }

   public boolean saveFile (String filename, String [] units2Save)
   {
      TextFile fitxer = new TextFile();
      if (!fitxer.fopen (filename, "w"))
      {
         System.err.println ("ERROR: EvaFile::Save, the file [" + filename + "] couln't be opened for write!");
         return false;
      }

      for (int uu = 0; uu < allUnits.size (); uu ++)
         if (nameRequired (((EvaUnit) allUnits.get (uu)).getName (), units2Save))
            writeEvaUnitOnFile (fitxer, (EvaUnit) allUnits.get(uu));

      fitxer.writeLine ("");
      fitxer.writeLine ("#**FIN#");
      fitxer.fclose ();
      return true;
   }


   /**
      Parses the Eva-like lines of text given in 'textLines', then builds an unamed Eva
      with it which is returned
   */
   public static Eva text2Eva (String [] textLines, Eva theEva)
   {
      if (theEva == null)
         theEva = new Eva ("");

      String linStr = "";

      for (int ii = 0; ii < textLines.length; ii ++)
      {
         linStr = textLines[ii].trim ();
         if (linStr.length () == 0) continue; // ignore empty lines
         theEva.add (new EvaLine (linStr));
      }

      return theEva;
   }

   /**
      this function is needed because simply calling
         theFile.writeLine (ueva.toString ());
      could cause a java.lang.OutOfMemoryError if the evaunit is very large
   */
   protected void writeEvaUnitOnFile (TextFile openedFile, EvaUnit eunit)
   {
      openedFile.writeLine ("#" + eunit.getName () + "#");

      for (int ee = 0; ee < eunit.size (); ee ++)
      {
         Eva eva = eunit.getEva(ee);
         openedFile.writeLine ("");
         openedFile.writeLine ("   <" + eva.getName () + ">");

         for (int rr = 0; rr < eva.rows(); rr ++)
         {
            EvaLine elin = eva.get(rr);

            //// check if the line contain a return!
            //// NOTE: We don't make this check EvaLine in oder to not penalize so much
            ////       the performance of Eva structure in general
            //if (!checkNotContainsReturn (elin.getColumnArray ()))
            //{
            //   log().err ("writeEvaUnitOnFile", "Found a return in eva <" + eva.Nombre + "> row " + rr + " while saving EvaUnit in [" + openedFile.getFileName () + "]");
            //   //continue with the rest!
            //}

            openedFile.writeLine ("      " + elin);
         }
      }
   }
}
