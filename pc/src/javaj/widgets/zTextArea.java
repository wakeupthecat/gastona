/*
package de.elxala.zWidgets
Copyright (C) 2005-2014 Alejandro Xalabarder Aulet

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

import java.awt.Dimension;

import javax.swing.JTextArea;
import javax.swing.event.*; // DocumentListener etc
import java.awt.event.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;

import javaj.widgets.basics.*;
import javaj.widgets.text.*;


/*
   //(o) WelcomeGastona_source_javaj_widgets (x) zTextArea

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zTextArea
   <groupInfo>  text
   <javaClass>  javaj.widgets.zTextArea
   <importance> 7
   <desc>       //A Text Area (rapid prefix 'x')

   <help>
      //
      // Text area for multi line texts, either accepting new lines or wrapping the text depending on
      // the attribute "wrapLines".
      //
      // Widget characteristics: "Common" and "Text data"
      //

   <prefix> x

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , text                       , //Contents of the text area
      var              , in    , Eva name                   , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      wrapLines        , in    , 0 / 1                      , //Value 0 if the lines has NOT to be wraped according to text width
      tabulator        , in    , number                     , //Size for the tabulator, in monospaced fonts "should" correspond with the spaces
      fileName         , in    , file name                  , //File to be used in messages "load" and "save"
      lineToGo         , in    , number                     , //**DEPRECATED*** Line number to go on a message "gotoLine"

   <messages>

      msg      , in_out, desc

      data!    , in   , //update data
      control! , in   , //update control
      clear    , in   , //clears the contents of the text
      load     , in   , //loads the contents from the file given in attribute "fileName"
      save     , in   , //saves the contents to the file given in attribute "fileName"
      gotoLine , in   , //set the cursor and scroll if needed to a line given as parameter (NOTE "lineToGo" attribute is DEPRECATED)
      insert   , in   , //insert the texts given in parameters
      newLine  , in   , //insert a new line and then the texts given in parameters

      undo     , in   , //undo last edit
      redo     , in   , //redo last undo


   <examples>
      gastSample

      hello zTextArea

   <hello zTextArea>
      //#javaj#
      //
      //    <frames> F, Hello zTextArea
      //
      //    <layout of F>
      //          PANEL, X
      //          xText
      //
      //#data#
      //
      //    <xText>
      //       //This is the text
      //       //of the hello zTextArea example
      //

#**FIN_EVA#

*/


/**
*/
public class zTextArea extends undoredoTextArea implements DocumentListener, MensakaTarget
{
   private textAparato helper = null;

   public zTextArea ()
   {
      super ();
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zTextArea (String map_name)
   {
      build  (map_name);
   }

   public void setName (String map_name)
   {
      build (map_name);
   }

   private void build (String map_name)
   {
      super.setName (map_name);
      setLineWrap (true);
      setWrapStyleWord (true);
      setTabSize (3);

      helper = new textAparato (this, new smallTextEBS (map_name, null, null));

      // abilitate me to listen to myself !
      //
      getDocument().addDocumentListener (this);
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
            if (helper.ebsText().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            updateControl ();
            break;

         case widgetConsts.RX_REVERT_DATA:
            if (helper.getIsDirty ())
            {
               helper.ebsText ().setText (getText ());
               changeDirty (false);
            }
            break;

         case textAparato.RX_COMMNAD_LOAD:
            commandLoad (pars);
            break;

         case textAparato.RX_COMMNAD_SAVE:
            commandSave (pars);
            break;

         case textAparato.RX_COMMNAD_UNDO:
            undo ();
            break;

         case textAparato.RX_COMMNAD_REDO:
            redo ();
            break;

         case textAparato.RX_COMMNAD_GOTO:
            goToLine (pars);
            break;

         case textAparato.RX_COMMNAD_CLEAR:
            setText ("");
            helper.ebsText ().setText ("");
            changeDirty (false);
            break;

         case miniEditorAparato.RX_COMMNAD_INSERTTEXT:
            {
               for (int ii = 0; ii < pars.length; ii ++)
                  setText (getText () + pars[ii]);
               helper.ebsText ().setText (getText ());
            }
            break;

         case miniEditorAparato.RX_COMMNAD_NEWLINE:
            {
               for (int ii = 0; ii < pars.length; ii ++)
                  setText (getText () + "\n" + pars[ii]);
               helper.ebsText ().setText (getText ());
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
      setTabSize (helper.ebsText ().getTabulator ());

      boolean wrap = helper.ebsText ().getIsWrapLines ();
      setLineWrap (wrap);
      setWrapStyleWord (wrap);

      setEnabled (helper.ebs ().getEnabled ());

      //(o) TODO_REVIEW visibility issue
      // avoid setVisible (false) when the component is not visible (for the first time ?)
      boolean visible = helper.ebs ().getVisible ();
      if (visible || isShowing ())
         setVisible  (visible);
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsText().hasAll ())
      {
         setText (helper.ebsText ().getText ());
         setTabSize (helper.ebsText ().getTabulator ());

         goToLine (null);
         changeDirty (false);
      }
   }

   private void commandLoad (String [] params)
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zTextArea.commandLoad", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_LOAD) + "] ignored");
         return;
      }

      // DETECT fileName in parameters
      if (params != null && params.length > 0)
      {
         String fName = params[0];
         if (params.length > 1)
            helper.log.warn ("zTextArea.commandLoad", "too much arguments only first \"" +  fName + "\" will be used");
         helper.ebsText ().setFileName (fName);
      }

      helper.ebsText ().setText ("");
      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0)
      {
         helper.log.err ("zTextArea.commandLoad", "widget " +  helper.ebsText().getName () + " has no attribute fileName. Message load ignored");
         return;
      }
      if (fileName.length () == 0) return;

      String [] lines = TextFile.readFile (fileName);
      if (lines == null)
      {
         // the file does not exist
         //(o) TODO_widgets_text use a new control variable "fileStatus" or "error" to indicate the error
         //helper.ebsText ().err ("reading from file " + fileName);
         return;
      }

      StringBuffer alltext = new StringBuffer ();
      for (int ii = 0; ii < lines.length; ii ++)
      {
         alltext.append (lines[ii] + "\n");
      }
      helper.ebsText ().setText (alltext.toString ());

      tryAttackWidget ();
   }

   private void commandSave (String [] params)
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zTextArea.commandSave", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_SAVE) + "] ignored (sent too early ?)");
         return;
      }

      // DETECT fileName in parameters
      if (params != null && params.length > 0)
      {
         String fName = params[0];
         if (params.length > 1)
            helper.log.warn ("zTextArea.commandSave", "too much arguments only first \"" +  fName + "\" will be used");
         helper.ebsText ().setFileName (fName);
      }

      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0)
      {
         helper.log.err ("zTextArea.commandSave", "widget " +  helper.ebsText().getName () + " has " +
                         ((fileName == null) ? "no attribute fileName": ("no valid fileName [" + fileName + "]")) +
                         ". Message save ignored");
         return;
      }

      boolean ok = TextFile.writeFile (fileName, helper.ebsText ().getText ());

      helper.log.dbg (2, "zTextArea.commandSave", "writing into the file " + fileName);
      if (! ok)
      {
         helper.log.err ("zTextArea.commandSave", "error writing into the file " + fileName);
      }
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
         helper.log.dbg (2, "zTextArea.goToLine", "lineNr == -1 (end of the file)");
         setCaretPosition (getText().length ());
      }
      else
      {
         helper.log.dbg (2, "zTextArea.goToLine", "go to the lineNr == " + lineNr);

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
