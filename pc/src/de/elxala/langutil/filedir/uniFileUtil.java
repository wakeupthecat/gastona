/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005-2015  Alejandro Xalabarder Aulet

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

package de.elxala.langutil.filedir;

import java.io.*;
import de.elxala.zServices.*;
import de.elxala.langutil.*;

/**
   class TextFile
   @author Alejandro Xalabarder Aulet

   used by base/.../fileUtil

   each product (e.g. pc / android) has to implement its methods


*/
public class uniFileUtil
{
   public static void deleteTmpFileOnExit (File fi)
   {
      fi.deleteOnExit ();
   }

   public static String resolveCurrentDirFileName (String vagueFileName)
   {
      // this method is thought for Android Apps getFilePathFromVagueFilename
      return vagueFileName;
   }

   private static String tempDirBase = null;
   public static String getTemporalDirInitial ()
   {
      // temporal directories with accents (e.g. spanish ...Configuración Local...)
      // DOES NOT work with sqlite !!!
      // Therefore, temp dir criteria:
      //
      //    1) if tmp exists then IT IS THE BASE TEMP DIR
      //    2) if \tmp exists then IT IS THE BASE TEMP DIR !!!
      //    3) take it from java.io.tmpdir property
      //
      //    after that ALWAYS getCanonical Path and set java.io.tmpdir to the
      //    new path
      //

      if (tempDirBase != null) return tempDirBase;
      File fi = new File ("tmp");
      if (!fi.exists () || !fi.isDirectory ())
         fi = new File ("/tmp");
      if (!fi.exists () || !fi.isDirectory ())
         fi = new File (System.getProperty("java.io.tmpdir", "."));

      try { tempDirBase = fi.getCanonicalPath (); } catch (Exception e) {}

      boolean avis = false;
      for (int ii = 0; ii < tempDirBase.length (); ii++)
         if (tempDirBase.charAt (ii) > '~')
         {
            avis = true;
            break;
         }

      if (avis)
      {
         String mess = "Temporary directory path contain inconvenient characters\n [" + tempDirBase + "]\n applications like sqlite may not work properly.\n Creating a root or sub-directory named tmp might avoid such problems.";
         fileUtil.log.err ("getTemporalDirInitial", mess);

         javax.swing.JOptionPane.showMessageDialog (
               null,
               mess,
               "Error",
               javax.swing.JOptionPane.ERROR_MESSAGE);
      }

      return tempDirBase;
   }

   public static String getTemporalDirApp ()
   {
      return System.getProperty("java.io.tmpdir", ".");
   }
}
