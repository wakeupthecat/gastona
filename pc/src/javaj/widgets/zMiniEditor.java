/*
package de.elxala.zWidgets
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

package javaj.widgets;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*; // DocumentListener etc
import java.io.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.text.*;
import javaj.widgets.basics.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (f) zMiniEditor

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zMiniEditor
   <groupInfo>  text
   <javaClass>  javaj.widgets.zMiniEditor
   <importance> 9
   <desc>       //A text area strictly associated with a file (rapid prefix 'f')

   <help>
      //
      // Text area widget like zTextArea (prefix 'x') with the limitation that the text content is not
      // hold in any data variable. Text content is loaded directly from a file, the user might change
      // it and it can be saved into a file. This is more efficient as zTextArea specially for big
      // files.
      //
      // Widget characteristics: "Common" and "Text data"
      //

   <prefix> f

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , (Eva Table)                , //Contents of items
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      wrapLines        , in    , 0 / 1                      , //Value 0 if the lines has NOT to be wraped according to text width
      tabulator        , in    , number                     , //Size for the tabulator, in monospaced fonts "should" correspond with the spaces
      fileName         , in    , file name                  , //File to be used in messages "load" and "save"
      lineToGo         , in    , number                     , //**DEPRECATED*** Line number to go on a message "gotoLine"

   <messages>

      msg, in_out, desc

      data!    , in   , //update data
      control! , in   , //update control
      clear    , in   , //clears the contents of the text
      load     , in   , //loads the contents from the file given as parameter or, if there is no parameter, in the attribute "fileName"
      save     , in   , //saves the contents to the file given as parameter or, if there is no parameter, in the attribute "fileName"
      gotoLine , in   , //set the cursor and scroll if needed to a line given as parameter (NOTE "lineToGo" attribute is DEPRECATED)
      insert   , in   , //insert the texts given in parameters
      newLine  , in   , //insert a new line and then the texts given in parameters

      undo     , in   , //undo last edit
      redo     , in   , //redo last undo



   <examples>
      gastSample

      little editor
      <!zMiniEditor autotest

   <little editor>
      //#javaj#
      //
      //    <frames> F, little Editor
      //
      //    <layout of F>
      //          EVA, 6, 6, 3, 3
      //
      //          ---,          , X
      //             , lFile    , eFile, bSearch, bReload, bSave
      //           X , fMiniEdit, -, -, -, -
      //
      //    <sysDefaultFonts>
      //       Consolas, 15, 0, TextField
      //
      //#data#
      //
      //    <bSearch DIALOG> FILE
      //
      //    <! DISABLED FOR SAFE TESTING
      //    <bSave enabled> 0
      //
      //    <bSearch image> javaj/img/linterna.png
      //    <bReload image> javaj/img/Fichero_2_2.png
      //    <bSave image>   javaj/img/floppy.png
      //
      //#listix#
      //
      //    <-- bSearch>
      //       -->, eFile data!,, @<bSearch chosen>
      //       @<load file>
      //
      //    <-- bReload>
      //       @<load file>
      //
      //    <load file>
      //       CHECK, VAR  , eFile   , @<error NoFileName>
      //       CHECK, RFILE, @<eFile>, @<error FileName does not exist>
      //       SETVAR, fMiniEdit fileName, @<eFile>
      //       MSG, fMiniEdit load
      //
      //    <-- bSave>
      //       SETVAR, fMiniEdit fileName, @<eFile>
      //       MSG, fMiniEdit save
      //
      //    <error NoFileName>
      //       //Enter a file name, then press Load
      //       //
      //
      //    <error FileName does not exist>
      //       //File name "@<eFile>" not found!
      //       //
      //

   <zMiniEditor autotest>
      //#javaj#
      //
      //    <frames> F, Hello zMiniEditor
      //
      //    <layout of F>
      //          PANEL, X
      //          fMiniEdit
      //
      //#data#
      //
      //    <sampleText>
      //       //This is the "hello zMiniEditor" gastona script
      //       //
      //
      //    <fMiniEdit> text.txt
      //    <fMiniEdit fileName> EstoEsUnFileName.txt
      //
      //#listix#
      //
      //    <main0>
      //       SETVAR, tmpFile, @<:lsx tmp>
      //       GEN, @<tmpFile>, sampleText
      //       SETVAR, fMiniEdit fileName, @<tmpFile>
      //       MSG, fMiniEdit load
      //

#**FIN_EVA#

*/


/**
   @author Alejandro Xalabarder Aulet
   @date 30.01.2005 16:04

   Widget zMiniEdit is a JTextArea oriented for viewing and editing files. The real model of this widget remains
   the internal Document of JTextArea for complexity reasons and we will use EBS model for setting
   different commands like "load", "save" etc (see class zMiniEditEBS).
   Note that it is not a JFrame so a final GUI has to be build using it, and also note that the text itself
   cannot be retrieved directly.
*/
public class zMiniEditor extends undoredoTextArea implements DocumentListener, MensakaTarget
{
   private miniEditorAparato helper = null;

   public zMiniEditor ()
   {
      super (24, 80);
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zMiniEditor (String map_name)
   {
      super (24, 80);
      build  (map_name);
   }

   public void setName (String map_name)
   {
      build (map_name);
   }

   private void build (String map_name)
   {
      super.setName (map_name);
      setTabSize (5);      // NO CHUTA ? O QUE PASA!

      helper = new miniEditorAparato (this, new miniEditorEBS (map_name, null, null));

      // abilitate me to listen to myself !
      //
      getDocument().addDocumentListener (this);

      //(o) TODO_javaj_widgets Drag&Drop texts: if we abilitate drag&drop then cut,copy,paste is disabled (dndFileTransHandler has to be improved)
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case miniEditorAparato.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            break;

         case miniEditorAparato.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
            if (helper.ebsText().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            updateControl ();
            break;

         case miniEditorAparato.RX_COMMNAD_LOAD:
            commandLoad (pars);
            break;

         case miniEditorAparato.RX_COMMNAD_SAVE:
            commandSave (pars);
            break;

         case miniEditorAparato.RX_COMMNAD_CLEAR:
            setText ("");
            break;

         case miniEditorAparato.RX_COMMNAD_GOTO:
            goToLine (pars);
            break;

         case miniEditorAparato.RX_COMMNAD_UNDO:
            undo ();
            break;

         case miniEditorAparato.RX_COMMNAD_REDO:
            redo ();
            break;

         case miniEditorAparato.RX_COMMNAD_INSERTTEXT:
            {
               System.out.println ("Recibo es estimulo " + pars.length);
               for (int ii = 0; ii < pars.length; ii ++)
                  setText (getText () + pars[ii]);
            }
            break;

         case miniEditorAparato.RX_COMMNAD_NEWLINE:
            {
               System.out.println ("Recibo es nestimulo " + pars.length);
               for (int ii = 0; ii < pars.length; ii ++)
                  setText (getText () + "\n" + pars[ii]);
            }
            break;

         default:
            return false;
      }

      return true;
   }


   private void updateControl ()
   {
      helper.updateControl (this);
      setTabSize (helper.ebsMiniEditor ().getTabulator ());

      setEnabled (helper.ebs ().getEnabled ());

      //(o) TODO_REVIEW visibility issue
      // avoid setVisible (false) when the component is not visible (for the first time ?)
      boolean visible = helper.ebs ().getVisible ();
      if (visible && isShowing ())
         setVisible  (visible);
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsText().hasAll ())
      {
         updateData ();
      }
   }


   private void updateData ()
   {
      if (helper.ebsText().hasAll ())
      {
         commandLoad (null);
      }
   }

   private void commandLoad (String [] params)
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ()) return;

      if (params != null && params.length > 0)
      {
         String fName = params[0];
         if (params.length > 1)
            helper.log.warn ("zMiniEditor.commandLoad", "too much arguments only first \"" +  fName + "\" will be used");
         helper.ebsMiniEditor ().setFileName (fName);
      }

      // has fileName ?
      setText ("");
      String fileName = helper.ebsMiniEditor ().getFileName ();
      if (fileName == null)
      {
         helper.log.err ("zMiniEditor.commandSave", "widget " +  helper.ebsMiniEditor().getName() + " has no attribute fileName. Message load ignored");
         return;
      }
      if (fileName.length () == 0) return;

      File fi = fileUtil.getNewFile (fileName);
      if (!fi.exists ())
      {
         // the file does not exist
         helper.log.warn ("zMiniEditor.commandLoad", "file \"" +  fileName + "\" does not exist!");
         return;
      }

      resetUndoRedo();  // from the base class undoredoTextArea

      // try to read the file into the text area
      try { read (new FileReader (fileName), null); }
      catch (Exception e)
      {
         helper.log.err ("zMiniEditor.commandLoad", e + " while reading file " + fileName);
      }

      goToLine (null);
      changeDirty (false);
   }

   private void goToLine (String [] lineArr)
   {
      int lineNr = -1;
      if (lineArr != null)
         lineNr = lineArr.length > 0 ? stdlib.atoi (lineArr[0]): -1;
      else
         lineNr = helper.ebsText ().hasLineToGo () ? helper.ebsText ().getLineToGo (): -1;

      requestFocus();
      requestFocusInWindow();

      if (lineNr == -1)
      {
         // want to go to end of file
         helper.log.dbg (2, "zMiniEditor::goToLine", "lineNr == -1 (end of the file)");
         setCaretPosition (getText().length ());
      }
      else
      {
         helper.log.dbg (2, "zMiniEditor::goToLine", "go to the lineNr == " + lineNr);

         // GO TO LINE lineNr
         //
         int llevoLines = 1;
         int posCaret = 0;
         int maxCaret = 0;
         while (llevoLines < lineNr)
         {
            //(o) TOSEE_javaj_widgets TEXT CARET Why it does not work with char 13 ?????? (I would say, it did time ago)
            maxCaret = posCaret;
            posCaret = 1 + getText().indexOf(10, posCaret);
            if (posCaret == 0) break;
            llevoLines ++;
         }

         //helper.log.warn ("zMiniEditor.goToLine", "pos careto es " + posCaret + " llevo lines se resume en " + llevoLines);

         if (posCaret >= 0)
         {
            if (maxCaret > posCaret)
               setCaretPosition (getText().length ());
            else
               setCaretPosition (posCaret);
         }
      }
   }

   private void commandSave (String [] params)
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zMiniEditor.commandSave", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_SAVE) + "] ignored");
         return;
      }

      // DETECT fileName in parameters
      if (params != null && params.length > 0)
      {
         String fName = params[0];
         if (params.length > 1)
            helper.log.warn ("zMiniEditor.commandSave", "too much arguments only first \"" +  fName + "\" will be used");
         helper.ebsMiniEditor ().setFileName (fName);
      }

      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0)
      {
         helper.log.err ("zMiniEditor.commandSave", "widget " +  helper.ebsText().getName() + " has " +
                         ((fileName == null) ? "no attribute fileName": ("no valid fileName [" + fileName + "]")) +
                         ". Message save ignored");
         return;
      }

      try
      {
         write (new FileWriter (fileName));
      }
      catch (Exception e)
      {
         helper.log.err ("zMiniEditor.commandLoad", e + " while writing file " + helper.ebsMiniEditor ().getFileName ());
      }
      changeDirty (false);
   }

   // =============== implementing DocumentListener
   //

   public void changedUpdate(DocumentEvent e)
   {
      changeDirty (true);
   }

   public void insertUpdate(DocumentEvent e)
   {
      changeDirty (true);
   }

   public void removeUpdate(DocumentEvent e)
   {
      changeDirty (true);
   }

   private void changeDirty (boolean dirty)
   {
      if (helper.setIsDirty (dirty))
      {
         setBackground (helper.ebsText ().getBackgroundColor ());
      }
   }
}
