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

package javaj.widgets.kits;

import de.elxala.mensaka.*;

import java.io.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import de.elxala.Eva.*;

import java.io.File;
import java.util.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.logger;

/**
   //(o) todo_reviewDoc_dndFileTransferHandler review documentation

   Transfer handler (java.awt.datatransfer.TransferHandler) for Drag&Droping files

   When a drag & drop of files is performed (i.e. from windowes explorer)
   this "agent" sets the eva 'messageDnD' of the EvaUnit 'lineComm' with the files dropped
   and sends the mensaka message 'messageDnD'. Where 'messageDnD' and 'lineComm' are given
   in the constructor.

   For example:-----------------OLD-----------------------------

      abilitate a javax.swing.JComponent to be drag&drop able

      class aWidget (has to be a JComponent)
      {
         EvaUnit lineControl = new EvaUnit ();

         aWidget ()
         {
            setTransferHandler ("myName dndFiles", lineControl);
         }
      }

      Then when the drag&drop is performed, let's say with the files c:\temp\file1 and c:\temp\file2
      will generate on the given EvaUnit (lineComm) the following Eva

         <myName dndFiles>
            fullName
            c:\temp\file1
            c:\temp\file2

      and after that will send the message "myName dndFiles" through the
      mensaka communication mechanism

         Mensaka.sendPacket ("myName dndFiles", lineControl);

      Of course the widget could be suscribed to this message and add the elements,
      but it is also possible that any other controller make this task.


      ---- Control through lineControl

      Once the dndFileTransferHandler is constructed is still possible to change its behaviour

      Control the fields of the result table (per default only "fullName")

         <myName dndFiles fields>   fileName, extension

      Control if only directories or only files or both are to be dropped

         <myName dndFiles types>   D          (only directories)
         <myName dndFiles types>   F          (only files)
         <myName dndFiles types>   DF         (both)

*/
public class dndFileTransHandler extends TransferHandler
{
   public static final String sFIELD_PATHFILE  = "pathFile";
   public static final String sFIELD_FILENAME  = "fileName";
   public static final String sFIELD_EXTENSION = "extension";
   public static final String sFIELD_FULLNAME  = "fullPath";
   public static final String sFIELD_DATE      = "date";
   public static final String sFIELD_SIZE      = "size";
   public static final String [] arrALL_FIELDS =
                          new String []
                          {
                              sFIELD_PATHFILE ,
                              sFIELD_FILENAME ,
                              sFIELD_EXTENSION,
                              sFIELD_FULLNAME ,
                              sFIELD_DATE     ,
                              sFIELD_SIZE     ,
                          };

   private String    sNameOfClient = "";
   private EvaUnit   uCommLine      = null;

   private String [] aFieldsToFill  = new String [] { "fullPath" };

   private static logger log = null;

   /**
      'messageDnD' Eva name to be generated each time a drag&drop operation is performed by the user
      'lineComm'   EvaUnit where to build the eva named 'messageDnD'

      'filedsToFill' array of strings containing any combination of following names
            pathFile    only the path
            fileName    only the file name with extension
            extension   only the extension
            date        file date 'yyyy/mm/dd hh:mm'
            size        file size in bytes
            fullName    full file name
   */
   public dndFileTransHandler (EvaUnit lineComm, String nameOfClient, String [] fieldsToFill)
   {
      if (log == null)
         log = new logger (null, "javaj.widgets.kits.dndFileTransHandler", null);
      sNameOfClient  = nameOfClient;
      aFieldsToFill  = fieldsToFill;
      setCommunicationLine (lineComm);
   }

   /**
      permits to change the communication line given in the constructor
   */
   public void setCommunicationLine (EvaUnit lineComm)
   {
      uCommLine = lineComm;
      if (uCommLine == null)
      {
         log.dbg (2, "setCommunicationLine", "received null data for communication, drag & drop not yet enabled");
      }
      else
      {
         boolean okFiles = uCommLine.getEva (sNameOfClient + " droppedFiles") != null;
         boolean okDirs  = uCommLine.getEva (sNameOfClient + " droppedDirs") != null;

         log.dbg (2, "setCommunicationLine", "received data for communication, by the moment " +
                     "enable droppedFiles is " + okFiles +
                     " and enable droppedDirs is " + okDirs);
      }
   }

   /**
      permits to change the array of fields to fill given in the constructor
   */
   public void setFieldsToFill (String [] fieldsToFill)
   {
      aFieldsToFill  = fieldsToFill;
   }

   public boolean canImport(JComponent c, DataFlavor[] fla)
   {
      return hasFileFlavor (fla);
   }

   private boolean hasFileFlavor(DataFlavor[] flavors)
   {
      for (int i = 0; i < flavors.length; i++)
      {
         if (flavors[i].isFlavorJavaFileListType ()) return true;
      }
      return false;
   }

   // me'todo llamado automa'ticamente por el mecanismo drag & drop
   // cuando se intenta dropar un fichero/directorio o seleccio'n de los mismos
   //
   public boolean importData (JComponent c, Transferable t)
   {
      log.dbg (2, "importData", "received importData for " + sNameOfClient);
      if (!canImport(c, t.getTransferDataFlavors()))
      {
         log.dbg (2, "importData", "cannot import this flavor " + t.getTransferDataFlavors()[0]);
         return false;
      }

      if (uCommLine == null)
      {
         // the widget has to be right programmed. (scenarios with widget ok and this message ?)
         log.severe ("importData", "has no data communication, cannot transmit dragged files");
         return false;
      }

      if (!hasFileFlavor(t.getTransferDataFlavors()))
      {
         log.dbg (2, "importData", "another flavor different from files, do nothing");
         // "has not file flavor ? what is this!"
         return false;
      }

      Eva evaFiles = uCommLine.getEva (sNameOfClient + " droppedFiles");
      Eva evaDirs = uCommLine.getEva (sNameOfClient + " droppedDirs");
      log.dbg (2, "importData", "enabling dragndrop for " + sNameOfClient + ",  droppedFiles is " + (evaFiles != null) + " and enable droppedDirs is " + (evaDirs != null));

      if (evaFiles == null && evaDirs == null)
      {
         // the client does not allow any dnd
         log.dbg (2, "importData", "drag&drop not performed because not allowed by " + sNameOfClient + " (neither ...droppedFiles nor ...droppedDirs variables found)");
         return true;
      }

      // collect files from drag'n'drop action
      //
      java.util.List fileList = null;
      try
      {
         fileList = (java.util.List) t.getTransferData (DataFlavor.javaFileListFlavor);
      }
      catch (UnsupportedFlavorException ufe)
      {
         log.err ("importData", "unsupported data flavor! " + ufe);
         return false;
      }
      catch (IOException ieo)
      {
         log.err ("importData", "I/O exception! " + ieo);
         return false;
      }

      // prepar tables
      //
      if (evaFiles != null)
      {
         evaFiles.clear ();
         evaFiles.addLine (new EvaLine (aFieldsToFill));
      }

      if (evaDirs != null)
      {
         evaDirs.clear ();
         evaDirs.addLine (new EvaLine (aFieldsToFill));
      }

      // fill tables
      //

      for (int ii = 0; ii < fileList.size(); ii ++)
      {
         File entry = (File) fileList.get (ii);

         // debug "...dragged"
         //
         if (entry.isDirectory ())
            log.dbg (2, "importData", "dragged directory [" + entry.getName() + "]");
         else if (entry.isFile ())
            log.dbg (2, "importData", "dragged file [" + entry.getName() + "]");
         else
            log.err ("importData", "what is dragged ? [" + entry.getName() + "]");

         // add it
         //
         if (entry.isFile () && evaFiles != null)
         {
            addEntry (evaFiles, entry);
         }
         else if (entry.isDirectory () && evaDirs != null)
         {
            addEntry (evaDirs, entry);
         }
      }

      // send the signals of drag'n'drop action
      //
      if (evaFiles != null && evaFiles.rows () > 1)
      {
         Mensaka.sendPacket (evaFiles.getName (), uCommLine);
         log.dbg (2, "importData", "message and data sent, \"" + evaDirs.getName () + "\"");
      }

      if (evaDirs != null && evaDirs.rows () > 1)
      {
         Mensaka.sendPacket (evaDirs.getName (), uCommLine);
         log.dbg (2, "importData", "message and data sent, \"" + evaDirs.getName () + "\"");
      }


      return true;
   }

   private void addEntry (Eva eva, File entry)
   {
      int row = eva.rows (); // add one
      String full = entry.getAbsolutePath ();
      log.dbg (2, "entry [" + full + "] added");
      for (int cc = 0; cc < eva.cols (0); cc ++)
      {
         String coln = eva.getValue (0, cc);

         if (coln.equals (sFIELD_FULLNAME))
            eva.setValue (full, row, cc);
         else if (coln.equals (sFIELD_PATHFILE))
            eva.setValue (fileUtil.getParent (full), row, cc);
         else if (coln.equals (sFIELD_FILENAME))
            eva.setValue (fileUtil.getJustNameAndExtension (full), row, cc);
         else if (coln.equals (sFIELD_EXTENSION))
            eva.setValue (fileUtil.getExtension (full), row, cc);
         else if (coln.equals (sFIELD_DATE))
            eva.setValue (DateFormat.getStr (new Date(entry.lastModified ())), row, cc);
         else if (coln.equals (sFIELD_SIZE))
            eva.setValue ("" + entry.length (), row, cc);

      }
   }
}
