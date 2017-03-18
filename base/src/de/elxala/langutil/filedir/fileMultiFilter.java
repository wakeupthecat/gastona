/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package de.elxala.langutil.filedir;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.regex.*;  // Pattern and Matcher
import de.elxala.zServices.logger;


/**   ======== de.elxala.langutil.fileMultiFilter ==========================================
   @author Alejandro Xalabarder 11.11.2006 18:59

   File and path filter

   Implements FileFilter.accept () for be used with java classes and dialogs

*/

public class fileMultiFilter implements FileFilter /* implements accept () */
{
   public static logger logStatic = new logger (null, "de.elxala.langutil.filedir.fileMultiFilter", null);
   public logger log = logStatic;

   private static final int INCLUDE = 0;
   private static final int EXCLUDE = 1;
   private static final int N_CRITERIO = 2;

   private static final int EXTENSION = 0;
   private static final int DIRECTORY = 1;
   private static final int FILENAME = 2;
   private static final int N_PARTE = 3;

   private Vector [][] filtrum = new Vector [N_CRITERIO][N_PARTE];

   public void clear ()
   {
      filtrum = new Vector [N_CRITERIO][N_PARTE];
   }

   private void genericAdd (int criterio, int parte, String text)
   {
      if (filtrum[criterio][parte] == null)
         filtrum[criterio][parte] = new Vector ();

      if (parte == EXTENSION)
         text = "\\." + text + "$";

      filtrum[criterio][parte].add (Pattern.compile(text));
   }

   /**
      <pre>
         Adds a filter criteria through an operation and a text

         optFilter might be

            +   the extension 'textFilter' will be included (all extensions do not added with + then will not be included)
            -   the extension 'textFilter' will be excluded (all extensions do not excluded with - then will be included)
            +D  the relative directory 'textFilter' will be included (all directories do not added with +D ...)
            -D  the relative directory 'textFilter' will be excluded (all directories do not excluded with -D ...)
            +F  the file 'textFilter' will be included (all files do not added with +F ...)
            -F  the file 'textFilter' will be excluded (all files do not excluded with -F ...)

            Java Regular expresions are accepted as 'textFilter'

          Examples:

                  - , "obj"      exclude extensions obj
                  -F, ".obj$"    exclude extensions obj
                  -D, "lint"     exclude the directory "/lint/" and its subdirectories
                  +F, "Proxy",   add the files which name includes "Proxy"

      </pre>
   */
   public void addCriteria (String optFilter, String textFilter)
   {
      if (optFilter == null || optFilter.length () == 0) return; // estas de cachondeo ...
      if (textFilter == null || textFilter.length () == 0)  return; // no es valido

      int crite = (optFilter.charAt(0) == '+') ? INCLUDE: EXCLUDE;
      int parte = FILENAME;

      char prime = (optFilter.length () <= 1) ? 'E': optFilter.charAt(1);
      switch (prime)
      {
         case 'D': parte = DIRECTORY; break;
         case 'F': parte = FILENAME; break;
         case 'E': parte = EXTENSION; break;
         default: break;
      }

      genericAdd (crite, parte, textFilter);
   }

//
//   public void includeExtension (String extension)
//   {
//      genericAdd (INCLUDE, EXTENSION, extension);
//   }
//
//   public void excludeExtension (String extension)
//   {
//      genericAdd (EXCLUDE, EXTENSION, extension);
//   }
//
//
   private boolean pasaFiltro (int crit, int parte, String name)
   {
      // check if criterium is void
      if (filtrum[crit][parte] == null || filtrum[crit][parte].size () == 0) return true;

      for (int ii = 0; ii < filtrum[crit][parte].size (); ii ++)
      {
//         Pattern patterno = (Pattern) filtrum[crit][parte];
//         Matcher matcho = patterno.matcher (name);
//         boolean cumple = matcho.find ();

         boolean cumple = ((Pattern) filtrum[crit][parte].get(ii)).matcher (name).find (); // (*) see note: USE find() instead of matches()
         if (cumple)
            return (crit == INCLUDE);
      }

      // no cumple, si el criterio era exclude entonces pasa!
      return (crit == EXCLUDE);
   }

   public String toString ()
   {
      String str = "[";
      
      for (int crit = INCLUDE; crit <= EXCLUDE; crit ++)
         for (int parte = EXTENSION; parte <= FILENAME; parte ++)
            if (filtrum[crit][parte] != null)
               for (int ff = 0; ff < filtrum[crit][parte].size (); ff ++)
                  str += (crit == INCLUDE ? "+": "-") +
                         (parte == DIRECTORY ? "D": (parte == FILENAME ? "F": "E")) +
                         " \"" + ((Pattern) filtrum[crit][parte].get (ff)) + "\", ";
      str += "]";
      return str;
   }

   public boolean accept (boolean isDir, String parentPath, String fileName)
   {
      // check dir
      //
      if (! pasaFiltro (INCLUDE, DIRECTORY, parentPath)) return false;
      if (! pasaFiltro (EXCLUDE, DIRECTORY, parentPath)) return false;
      if (isDir) 
      {
         log.dbg (4, "accept", "accepted directory parentPath [" +  parentPath + "]");
         return true;
      }

      // check extension
      //
      //12.07.2012 make only files be affected by extension filter
      if (! pasaFiltro (INCLUDE, EXTENSION, fileName)) return false;
      if (! pasaFiltro (EXCLUDE, EXTENSION, fileName)) return false;

      // check fileName
      //
      if (! pasaFiltro (INCLUDE, FILENAME, fileName)) return false;
      if (! pasaFiltro (EXCLUDE, FILENAME, fileName)) return false;

      log.dbg (4, "accept", "accepted file parentPath [" + parentPath + "] fileName [" + fileName + "]");
      // ha pasado todas las cribas!
      return true;
   }

   public boolean accept (File elFile)
   {
      return accept (elFile.isDirectory (), elFile.getParent (), elFile.getName ());
   }

   public static void main (String [] aa)
   {
      if (aa.length < 1)
      {
         System.out.println ("parameters: baseDir [opFilter str] ...");
         System.out.println ("where opFilter can be:");
         System.out.println ("");
         System.out.println ("   +   include the extension 'str'");
         System.out.println ("   -   exclude the extension 'str'");
         System.out.println ("   +D  include the directory that contains 'str'");
         System.out.println ("   -D  exclude the directory that contains 'str'");
         System.out.println ("   +F  include the file that contains 'str'");
         System.out.println ("   -F  exclude the file that contains 'str'");
         return;
      }

      if (aa[0].equals("@"))
      {
         // simple test pattern matcher
         String patt  = (aa.length >= 2) ? aa[1]: "a";
         String texto = (aa.length >= 3) ? aa[2]: "this is a text";

         Pattern patterno = Pattern.compile (patt);
         Matcher matcho = patterno.matcher (texto);
         boolean cumple = matcho.matches ();

         System.out.println ("little Pattern-Matcher test :");
         System.out.println ("   Pattern [" + patt + "]");
         System.out.println ("   Matcher [" + texto + "]");
         System.out.println ("");
         System.out.println ("   matches ? " + ((cumple) ? "yes": "no"));
         System.out.println ("   findes  ? " + matcho.find ());

         return;
      }

      fileMultiFilter macro = new fileMultiFilter ();

      // add all criteria
      for (int cc = 1; cc < aa.length; cc += 2)
      {
         macro.addCriteria (aa[cc], aa[cc + 1]);
      }

      pathGetFiles moto = new pathGetFiles();

      moto.initScan (aa[0], new String [] {});

      int nAceto = 0;
      int nNoAceto = 0;
      List cosas = null;
      do
      {
         cosas = moto.scanN (100);
         for (int jj = 0; jj < cosas.size (); jj++)
         {
            String [] fileX = (String []) cosas.get (jj);

            boolean aceto = macro.accept (false, fileX[0], fileX[1]);

            System.out.print (aceto ? "SI": "NO");
            System.out.println (" " + fileX[0] + "/" + fileX[1]);

            if (aceto)
                 nAceto ++;
            else nNoAceto ++;
         }
      }
      while (cosas.size () > 0);

      //System.out.println (kantos + " files");
      System.out.println ((nAceto + nNoAceto) + " files scanned and " + nNoAceto + " filtered");
   }
}



/*
   (*) Note: USE find() instead of matches()

      "ClaseController.java" match "Controler" FALSE
      "ClaseController.java" find  "Controler" TRUE
      "ClaseControllerX"     find  "Controler" TRUE

   el find es mas flexible y siempre lo podemos hacer ma's ri'gido puesto que
   admitimos RE de java, por ejemplo

      "ClaseController.java" find  "Controler\." TRUE
      "ClaseControllerX"     find  "Controler\." FALSE

   si queremos hacer lo mismo con match deberi'amos anyadir nosotros ".*" + texto + ".*"
   pero entonces texto no puede ser cualquier RE sino que deberi'a ser simplemente un literal!
*/