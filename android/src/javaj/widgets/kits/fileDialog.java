/*
package de.elxala.langutil
(c) Copyright 2005-2026 Alejandro Xalabarder Aulet

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

package javaj.widgets.kits;

import de.elxala.langutil.filedir.*;
import java.io.*;
//import javax.swing.*;
//import java.awt.Dimension;

import de.elxala.langutil.*;
import de.elxala.Eva.*;

/**
*/
public class fileDialog
{
   public static final int MODUS_FILES = 1;
   public static final int MODUS_DIR = 2;

   public static boolean selectFile (String Extension, String iniDir, Eva fileEvaTable, boolean multiselection)
   {
      return selectFileOrDir (Extension, iniDir, fileEvaTable, MODUS_FILES, multiselection);
   }

   public static boolean selectDir (String iniDir, Eva fileEvaTable, boolean multiselection)
   {
      return selectFileOrDir (null, iniDir, fileEvaTable, MODUS_DIR, multiselection);
   }

   private static boolean selectFileOrDir (String Extension, String thisDir, Eva filesSelected, int modus, boolean multiEnabled)
   {
//      // Assign automatically a start directory if 'thisDir' is not specified (is null)
//      // for single selection we use the last dir
//      //
//      if (thisDir == null)
//      {
//         if (!multiEnabled && filesSelected.rows () > 0)
//         {
//            thisDir = filesSelected.getValue (0,0);
//         }
//         else thisDir = ".";
//      }
//
//      // fix: JFileChooser peta si lo creamos con un directorio relativo a . ! (p.e. "./langConfig")
//      //         java.lang.NullPointerException
//      //                 at javax.swing.filechooser.FileSystemView.getParentDirectory(FileSystemView.java:433)
//      //                 at javax.swing.JFileChooser.setCurrentDirectory(JFileChooser.java:537)
//      //                 at javax.swing.JFileChooser.<init>(JFileChooser.java:333)
//      //                 at javax.swing.JFileChooser.<init>(JFileChooser.java:288)
//      //
//      String dirName = "";
//      File fil = new File2 (thisDir);
//      if (! fil.exists ())
//         fil = new File2 (".");
//      try { dirName = fil.getCanonicalPath (); } catch (Exception e) {}
//      //
//      // fin de fix
//
//      JFileChooser chooser = new JFileChooser (dirName);
//
//      Dimension esa = chooser.getPreferredSize ();
//      chooser.setPreferredSize (new Dimension ((int) esa.getWidth (), (int) esa.getHeight () + 120));
//
//      if (Extension != null && Extension.length () > 0)
//      {
//         Filtrus filter = new Filtrus (Extension);
//         chooser.setFileFilter (filter);
//      }
//
//      chooser.setFileSelectionMode(modus);
//      chooser.setMultiSelectionEnabled (multiEnabled);
//
//      int option = chooser.showOpenDialog (null);
//      if (option == JFileChooser.APPROVE_OPTION)
//      {
//         filesSelected.clear ();
//         if (multiEnabled)
//         {
//            // Multi selection
//            //
//            //(o) elxala_langutil fileDialog
//            //  filesSelected.addLine (new EvaLine ("pathFile, fileName, extension, date, size"));
//            filesSelected.addRow ("fullPath"); // the same name as in the listix command FILESYS
//            File[] sels = chooser.getSelectedFiles();
//            if (sels != null)
//            {
//               for (int ss = 0; ss < sels.length; ss ++)
//                  addOneFile (filesSelected, sels[ss]);
//
//               return true;
//            }
//         }
//         else
//         {
//            // Single selection
//            //
//            File sel = chooser.getSelectedFile ();
//            if (sel != null)
//            {
//               addOneFile (filesSelected, sel);
//               return true;
//            }
//         }
//      }
      return false;
   }

   private static void addOneFile (Eva fileEvaTable, File theFile)
   {
      fileEvaTable.addRow (theFile.getPath ());

      //(o) elxala_langutil fileDialog

      /*
      String name = theFile.getPath ();
      String extension = fileUtil.getExtension (name);
      String nameSolo = theFile.getName ();

      String dirSolo  = "";

      int iniDir = 0;
      int finDir = name.length () - nameSolo.length ();

      // add one for the separator ?
      if (iniDir < 0 || finDir-iniDir < 0 || finDir > name.length() || iniDir >= name.length())
      {
         System.err.println ("strange path [" + name + "] cut (" + iniDir + ", " + finDir + ") not possible!");
      }
      else
      {
         dirSolo  = name.substring (iniDir, finDir);
      }


      // match one extension => add to files
      if (fileUtil.endsWithSeparator (dirSolo))
         dirSolo = dirSolo.substring(0, dirSolo.length()-1);

      String dateStr  = DateFormat.getStr (new Date(aFile.lastModified ()));

      fileEvaTable.addLine (new EvaLine (
                              new String []
                              {
                                 dirSolo,
                                 nameSolo,
                                 extension,
                                 dateStr,
                                 "" + aFile.length ()
                              }));
      */
   }
}
