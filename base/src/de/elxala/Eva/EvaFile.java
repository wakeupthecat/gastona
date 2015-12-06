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
   private static final int esFIN_FICHERO_HARD = -3;
   private static final int esFIN_FICHERO_SOFT = -2;
   private static final int esFIN_FICHERO = -1;

   private static final int esUnidad = 0;
   private static final int esEva = 1;
   private static final int esComentario = 2;
   private static final int esDatos = 3;

   private static final char MARCA_Unit = '#';
   private static final char MARCA_Eva1 = '<';
   private static final char MARCA_Eva2 = '>';

   private static final String INITIAL_MARCA_FIN_EVA = "#**";
   private static final String MI_MARCA_FIN_EVA = "#**FIN EVA**#";


   private static logger logStatic = null;
   private static logger log ()
   {
      if (logStatic == null)
         logStatic = new logger (null, "de.elxala.Eva.EvaFile", null);
      return logStatic;
   }

   /**
      Static method to load a EvaUnit named 'UnitName' from the file 'FileName'

      @return the EvaUnit if found and null otherwise
   */
   public static EvaUnit loadEvaUnit (String FileName, String UnitName)
   {
      return (new EvaFile ()).load (FileName, UnitName);
   }

   /**
      Static method to save the EvaUnit 'ueva' in the file 'FileName'.<br>
         - If the file does not exit it will be created (if possible, i.e. no subdirectories will be created)<br>
         - If the EvaUnit does not exist in the file it will be added<br>
         - If the EvaUnit alredy exists in the file it will be updated<br>

      @return true if the operation success
   */
   public static boolean saveEvaUnit (String FileName, EvaUnit ueva)
   {
      if (ueva == null) return false;  // nothing to do

      File filN = new File (FileName);

      return (new EvaFile ()).save (filN.getPath (), ueva);
   }


   /**
      Static method to load all the names of all EvaUnit's from the file 'FileName'.

      @return a String array with the names or null if the file does not exist
   */
   static public String [] loadEvaUnitCatalog (String FileName)
   {
      return (new EvaFile ()).getCatalog (FileName);
   }

   /**
      Static method to know if the Eva unit 'EvaUnitName' is found in the file 'FileName'.
   */
   static public boolean existsEvaUnitInFile (String FileName, String EvaUnitName)
   {
      return (new EvaFile ()).existsEvaUnit (FileName, EvaUnitName);
   }


   private Cadena cNombre;
   private Cadena cLinFiltrada;
   private Cadena cLinBruta;
   private Cadena cLinSobrante;
   private TextFile m_file;

   /**
      Read a eva file and writes all the EvaUnit names in a String array.

      @param FileName The file name of the EVA file where the EvaUnits lies.
      @return A String array with all the EvaUnit names in the file. If the
              file doesn't exists then returns null.
   */
   public String [] getCatalog (String FileName)
   {
      List vecto = new Vector ();

      if (!OpenFile (FileName))
         return null;

      while (SiguienteUniEva())
         vecto.add (cNombre.o_str);

      // make the string array  (How to use the method toArray() ??)
      //
      String [] reto = new String[vecto.size ()];
      for (int ii = 0; ii < vecto.size (); ii ++)
         reto[ii] = (String) vecto.get(ii);

      CloseFile ();
      return reto;
      //return (String []) vecto.toArray ();
   }

   /**
      Retorna true si encuentra la unidad 'UnitName' en el fichero 'FileName'
      @param FileName The file name of the EVA file where the unit is to be found.
      @param UnitName The name of the unit of evas (without ## symbols)
      @return true if the unit is found
   */
   public boolean existsEvaUnit (String FileName, String UnitName)
   {
      if (false == OpenFile (FileName))
         return false;

      boolean result = BuscaUniEva (UnitName);
      CloseFile ();
      return result;
   }


   /**
      Carga una unidad de eva y la retorna si la encuentra, en caso contrario
      retorna null.
      @param FileName The file name of the EVA file where the unit is to be found.
      @param UnitName The name of the unit of evas (without ## symbols)
      @return An object of EvaUnit class which containts all the data of the
              unit
   */
   public EvaUnit load (String FileName, String UnitName)
   {
      if (false == OpenFile (FileName))
         return null;

      if (false == BuscaUniEva (UnitName))
      {
         CloseFile ();
         return null;
      }

      EvaUnit esaUni = new EvaUnit (UnitName);
      Eva esaEva = null;
      EvaLine esaLin = null;
      int que;

      if (SiguienteEva())
      {
         esaEva = new Eva (cNombre.o_str);
         do
         {
            que = LeeLineaNeta (true);

            switch (que)
            {
               case esEva:
                  esaUni.add (esaEva);
                  esaEva = new Eva (cNombre.o_str);
                  break;

               case esDatos:
                  esaLin = new EvaLine ();
                  esaLin.set (cLinFiltrada);
                  esaEva.addLine (esaLin);
                  break;

               default:
                  break;
            }
         }
         while (que >= esEva);
         esaUni.add (esaEva);
      }
      CloseFile ();
      return esaUni;
   }


   public static void textLines2Eva (String [] textLines, Eva theEva)
   {
      // Instanciate a new object since this method needs own member variables !
      EvaFile efi = new EvaFile ();

      efi.utilTextLines2Eva (textLines, theEva);
   }

   /**
      Parses the Eva-like lines of text given in 'textLines', then builds an unamed Eva
      with it which is returned
   */
   private void utilTextLines2Eva (String [] textLines, Eva theEva)
   {
      EvaLine theLin = null;

      for (int ii = 0; ii < textLines.length; ii ++)
      {
         Cadena cadFiltrada = new Cadena (textLines[ii]);
         cadFiltrada.trimMe ();

         int que = QueEs (cadFiltrada);

         if (que == esDatos)
         {
            theLin = new EvaLine ();
            theLin.set (cadFiltrada);
            theEva.addLine (theLin);
         }
         if (que < esEva) break;
      }
   }

   /**
      this function is needed because simply calling

         theFile.writeLine (ueva.toString ());

      could cause a java.lang.OutOfMemoryError if the evaunit is very large

   */
   private void writeEvaUnitOnFile (TextFile openedFile, EvaUnit eunit)
   {
      // System.out.println ("#" + eunit.getName () + "#");
      openedFile.writeLine ("#" + eunit.getName () + "#");

      for (int ee = 0; ee < eunit.size (); ee ++)
      {
         Eva eva = eunit.getEva(ee);
         openedFile.writeLine ("");
         openedFile.writeLine ("   <" + eva.getName () + ">");

         for (int rr = 0; rr < eva.rows(); rr ++)
         {
            EvaLine elin = eva.get(rr);

            // check if the line contain a return!
            // NOTE: We don't make this check EvaLine in oder to not penalize so much
            //       the performance of Eva structure in general
            if (!checkNotContainsReturn (elin.getColumnArray ()))
            {
               log().err ("writeEvaUnitOnFile", "Found a return in eva <" + eva.Nombre + "> row " + rr + " while saving EvaUnit in [" + openedFile.getFileName () + "]");
               //continue with the rest!
            }

            // System.out.println ("" + elin);
            openedFile.writeLine ("      " + elin);
//            for (int cc = 0; cc < elin.cols (); cc ++)
//            {
//               // if (cc != 0) System.out.print (", ");
//               if (cc != 0) openedFile.writeString (", ");
//               // System.out.print (elin.get (cc));
//               openedFile.writeString (elin.get (cc));
//            }
            // System.out.print ("");
//            openedFile.writeLine ("");
         }
      }
   }

   private Pattern pattReturn = null;

   private boolean checkNotContainsReturn (String [] arrStr)
   {
      if (pattReturn == null) try
      {
         pattReturn = Pattern.compile ("[\r\n]");
      }
      catch (Exception e)
      {
         // it should not happen!
         log().severe ("checkNotContainsReturn", "exception compiling expresion for checking return (strange!)" + e);
         return false;
      }

      Matcher matcher = null;
      for (int ii = 0; ii < arrStr.length; ii ++)
      {
         try
         {
            matcher = pattReturn.matcher(arrStr[ii]);
         }
         catch (Exception e)
         {
            log().err ("checkNotContainsReturn", "Exception calling matcher\n" + e);
            return false;
         }
         if (matcher != null && matcher.find())
         {
            // let the caller write the error message
            //System.out.println ("THERE IS A RETURN!!!! [" + arrStr[ii] + "] start " + matcher.start() + " end " + matcher.end ());
            return false;
         }
      }

      return true;
   }

   /**
      Salva una unidad de Eva en el fichero indicado. Si no existe el fichero lo creara'
      (si el directorio existe). En caso de que el fichero exista respetara' el contenido
      de otras unidades pero todos, el contenido y los comentarios de la unidad en cuestio'n
      sera'n sobreescritos.
      @param FileName File name of the eva file where we want to save the unit of evas. If
                        the file don't exists then it will be created, but in that case the
                      directory of the file must exist, that is, this function will not try
                        to create the directory if it does not exist. In the case that the file already
                      exists, the function will copy all the others units as they are written in
                      the file and will substitute or add the given unit of evas.
      @param ueva Unit of Evas to save
      @param bDeleteFromFile If this parameter it's true then the unit will be erased from the file
                      (even the reference to its name).
      @return If the unit has been successfully saved on the file then returns true, otherwise
              returns false.
   */
    private boolean Save (String FileName, EvaUnit ueva, boolean bDeleteFromFile)
    {
      File elFix = new File (FileName);

      if ( ! elFix.exists ())
      {
         //
         // Write a new file with the unit
         //
         TextFile ese = new TextFile();
         if (!ese.fopen (FileName, "w"))
            return false;

         writeEvaUnitOnFile (ese, ueva);

         ese.writeLine ("");
         ese.writeLine (MI_MARCA_FIN_EVA);
         ese.fclose ();
         return true;   // fertig
      }

      //
      // Update existing file
      //

      // Renombrar el original (terminandolo en .TmpBack)
      //
      File bakFix = new File (FileName + ".TmpBack");
      if (bakFix.exists ())
         bakFix.delete ();
      elFix.renameTo (bakFix);   // elFix mantiene el nombre original !!

      // si no se ha podido renombrar ...
      if (!bakFix.exists ()) return false;

      // Abrimos el hadle de escritura
      //
      TextFile hWrite = new TextFile ();
      boolean bOp = hWrite.fopen (FileName, "w");
      if (false == bOp)
      {
         System.err.println ("ERROR: EvaFile::Save, the file [" + FileName + "] couln't be opened for write!");
         return false;
      }

      // Abrimos el hadle de lectura
      //
      TextFile hRead = new TextFile ();
      hRead.fopen (bakFix.getPath (), "r");

      boolean finSoft = false;
      boolean escritoEsta = false;
      Cadena ss;
      String sRecon = MARCA_Unit + ueva.getName() + MARCA_Unit;

        while ( ! finSoft && ! hRead.feof ())
        {
         if (!hRead.readLine ()) break;

         ss = new Cadena (hRead.TheLine());
         ss.trimMe ();

         if (ss.o_str.length () == 0 || ss.o_str.charAt(0) != MARCA_Unit)
         {
            hWrite.writeLine (hRead.TheLine());
         }
         else
         {
            if (ss.InitialMatch(sRecon))
            {
               // escribir la nueva unidad
               //
               if (!bDeleteFromFile)
                  writeEvaUnitOnFile (hWrite, ueva);
               escritoEsta = true;

               // saltarnos la unidad original
               while (hRead.readLine ())
               {
                  ss = new Cadena (hRead.TheLine());
                  if (ss.o_str.length () != 0 && ss.o_str.charAt(0) == MARCA_Unit)
                     break;
               }
            }

            finSoft = ss.InitialMatch (INITIAL_MARCA_FIN_EVA); // start with #**
            finSoft &= (ss.replaceMe ("#", "#") > 1);          // and have at least 2 #

            if (false == finSoft && false == hRead.feof ())
            {
               hWrite.writeLine (hRead.TheLine());
            }
         }
      }

      if ( ! escritoEsta && ! bDeleteFromFile)
      {
         writeEvaUnitOnFile (hWrite, ueva);
         escritoEsta = true;
      }
      hWrite.writeLine (MI_MARCA_FIN_EVA);

      // escribir resto despue's de fin lo'gico de fichero eva
      //
      while (!hRead.feof ())
      {
         if (!hRead.readLine ()) break;
         hWrite.writeLine (hRead.TheLine());
      }
      hWrite.fclose ();
      hRead.fclose ();

      bakFix.delete ();
      return escritoEsta;
   }

   /**
      Saves the unit 'ueva' to the text file 'fileName'

      returns true if the operation termiates successfully
   */
   public boolean save (String fileName, EvaUnit ueva)
   {
      return Save (fileName, ueva, false);
   }

   /**
      Removes from text file 'fileName' the given unit 'ueva'

      returns true if the operation termiates successfully
   */
   public boolean deleteFromFile (String FileName, EvaUnit ueva)
   {
      return Save (FileName, ueva, true);
   }


   // ========== direct file functions
   //
    private boolean OpenFile (String FileName)
    {
      cNombre = new Cadena ();
      cLinFiltrada = new Cadena ();
      cLinBruta = new Cadena ();
      cLinSobrante = new Cadena ();

      m_file = new TextFile ();
      return (m_file.fopen (FileName, "r"));
   }

   private boolean ReadLine ()
   {
      if (cLinSobrante.o_str.length () > 0)
         cLinBruta = cLinSobrante;
      else
      {
         if (!m_file.readLine ())   return false;      //  >>>>
         cLinBruta = new Cadena (m_file.TheLine());
      }

      cLinFiltrada = new Cadena (cLinBruta.o_str);
      cLinFiltrada.trimMe ();
      cLinSobrante.clear ();

      return true;
   }

   private void CloseFile ()
   {
      m_file.fclose ();
   }
   // ==========


   /* Lee el fichero hasta posicionarse en la unidad escogida
   */
    private  boolean BuscaUniEva (String nom)
    {
        while (SiguienteUniEva())
             if (nom.equalsIgnoreCase (cNombre.o_str))
                  return true;
        return false;
   }

   /* Determina la naturaleza de la li'nea (unidad, eva, dato, comentario ...)
   */
   private int QueEs (Cadena cadLineFilt)
   {
      Cadena sLin = cadLineFilt;

      // nothing ?
      if (sLin.o_str.length () == 0)
      {
         return esComentario;
      }

      char letra = sLin.o_str.charAt (0);
      if (letra != MARCA_Unit && letra != MARCA_Eva1)
      {
         // no #Unit# no <Eva> ? then has to be data
         return esDatos; // >>>>
      }

      // starts with # or <

      // check if is a comment ("#" or "<" or "#!xxx" or "<!xxxx")
      if (sLin.o_str.length () == 1 || sLin.o_str.charAt (1) == '!')
         return esComentario;          // >>>>

      //
      // check if ended (2nd # or >)
      //
      int woEnd;
      if (letra == MARCA_Unit)
         woEnd = sLin.o_str.indexOf (MARCA_Unit, 1);
      else
         woEnd = sLin.o_str.indexOf (MARCA_Eva2, 1);

      //  "#Unit" or "<Eva" are not correctly ended ?
      if (woEnd == -1)
         return esComentario;          // >>>>

      // soft end of file ?
      //
      if (sLin.InitialMatch (INITIAL_MARCA_FIN_EVA))
         return esFIN_FICHERO_SOFT;    // >>>>

      //      // is it a Comment line ??
      //      //        "# " or "< " (followed by a blank)
      //      if ((sLin.o_str.length () <= 2) || (sLin.o_str.charAt (1) == ' '))
      //         return esComentario;          // >>>>

      // #Unit# or <Eva> : take the name and delete this from sLin ?
      cNombre = new Cadena (sLin.substrBE (1, woEnd-1));
      cLinSobrante = new Cadena (sLin.substrBE (woEnd+1, sLin.o_str.length ()));

      if (letra != MARCA_Unit)
      {
         return esEva; // >>>>
      }

      return esUnidad; // >>>>
   }

   /*
      Lee una linea del fichero eva y la filtra
   */
    private int LeeLineaNeta(boolean AhorraComentario)
    {
      int ques;

      do
      {
         if (!ReadLine ())
            return esFIN_FICHERO_HARD;         //  >>>>
         ques = QueEs (cLinFiltrada);
      }
      while (AhorraComentario && (ques == esComentario));

      return ques;
   }

   /**
      Avanza hasta la siguiente EvaUnit o Eva
   */
   private int NextUoE ()
   {
      int quees;
      do
      {
         quees = LeeLineaNeta (true);
         if (quees == esUnidad || quees == esEva)
            return quees;
       }
       while (quees > esFIN_FICHERO);
       return quees;
   }

   /**
       Buscar en un fichero EVA la siguiente eva de la unidad en curso
       Retorna: TRUE si la encuentra y FALSE si fin de fichero o FinEva
   */
   private boolean SiguienteEva()
   {
      return (NextUoE () == esEva);
   }

   /**
       Buscar en un fichero EVA la siguiente unidad
       Retorna: TRUE si la encuentra y FALSE si fin de fichero o FinEva
   */
   private  boolean SiguienteUniEva()
   {
      int ques;

      do
      {
         ques = NextUoE ();
         if (ques == esUnidad) return true;
      }
      while (ques == esEva);
      return false;
   }
}
