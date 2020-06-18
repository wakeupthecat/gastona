/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005-2020  Alejandro Xalabarder Aulet

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
      // Therefore, temp dir criteria:
      //
      //
      //    after that ALWAYS getCanonical Path and set java.io.tmpdir to the
      //    new path
      //
      // * In windows we use "\gastona" instead of "\tmp" due to the issues with
      //   antiviruses willing to check executables in temp directories even if they has been
      //   scanned once!
      //

      if (tempDirBase != null) return tempDirBase;

      File tmpDir = null;

      String explicitTmpDir = System.getProperty(org.gastona.gastonaCtes.PROP_TMP_DIR, null);
      if (explicitTmpDir != null)
      {
         //    1) explicit given temp dir (property gastona.tmp.dir)
         tmpDir = new File2 (explicitTmpDir);
      }
      else
      {
         //    2) if relative ./tmp folder exists then IT IS THE BASE TEMP DIR
         //    3) if absolute /tmp (or \gastona in windows *) folder exists then IT IS THE BASE TEMP DIR !!!
         //    4) take it from java.io.tmpdir property
         tmpDir = new File2 ("tmp");
         if (!tmpDir.exists () || !tmpDir.isDirectory ())
            tmpDir = utilSys.isSysUnix ? new File2 ("/tmp"): new File2 ("/gastona");
         if (!tmpDir.exists () || !tmpDir.isDirectory ())
            tmpDir = new File2 (System.getProperty("java.io.tmpdir", "."));
      }

      try { tempDirBase = tmpDir.getCanonicalPath (); } catch (Exception e) {}

      // check accents and rare characters in temp dir name
      //
      // temporal directories with accents (e.g. spanish ...Configuración Local...)
      // DOES NOT work with sqlite !!!
      //
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
