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

package de.elxala.langutil.filedir;

/**   ======== de.elxala.langutil.filedir ==========================================
   Alejandro Xalabarder
*/

import java.io.*;
import java.util.List;
import java.util.Vector;
import de.elxala.zServices.*;
import de.elxala.langutil.*;

/**
   class TextFile
   @author Alejandro Xalabarder Aulet
   @date   2015

   Utilities with files and paths
*/
public class uniFileUtil
{
   public static void deleteTmpFileOnExit (File fi)
   {
      androidFileUtil.deleteTmpFileOnExit (fi);
   }
   
   public static String getTemporalDirBase ()
   {
      if (androidFileUtil.getAndroidCacheDir () != null) return androidFileUtil.getAndroidCacheDir ();
      // mensaje de error ?
      return null;
   }

   public static String resolveCurrentDirFileName (String vagueFileName)
   {
      // do not affect empty values
      if (vagueFileName == null || vagueFileName.length () == 0) return vagueFileName;

      // if it is not an absolute path and not an url (e.g. "file://..." or "http://..." etc)
      // then return the path from application dir
      if (vagueFileName.charAt(0) != '/' && !fileUtil.looksLikeUrl (vagueFileName))
      {
         if (androidFileUtil.statPersistDir == null)
         {
            fileUtil.log.severe ("resolveCurrentDirFileName", "applicationDir not set yet, the method resolveCurrentDirFileName cannot be used!");
            return vagueFileName;
         }
         return fileUtil.getApplicationDir () + "/" + vagueFileName;
      }

      return vagueFileName;
   }

}
