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

import java.awt.Dimension;

import javax.swing.JTextArea;
import javax.swing.event.*; // DocumentListener etc
import java.awt.event.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;

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
      lineToGo         , in    , number                     , //Line number to go on a message "gotoLine"

   <messages>

      msg      , in_out, desc

      data!    , in   , //update data
      control! , in   , //update control
      clear    , in   , //clears the contents of the text
      insert   , in   , //insert a text
      load     , in   , //loads the contents from the file given in attribute "fileName"
      save     , in   , //saves the contents to the file given in attribute "fileName"
      gotoLine , in   , //set the cursor and scroll if needed to a line given in "lineToGo" attribute


   <examples>
      gastSample

      <!data4Tester
      hello zTextArea

   <data4Tester>
      //
      //    <xName>
      //       This is a text
      //       for testing pruposes
      //

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
public class zTextArea extends JTextArea implements DocumentListener, MensakaTarget
{
   private textAparato helper = null;

   public zTextArea ()
   {
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

   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebsText ().setNameDataAndControl (null, euData, null);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebsText ().setNameDataAndControl (null, null, euData);
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
            commandLoad ();
            break;

         case textAparato.RX_COMMNAD_SAVE:
            commandSave ();
            break;

         case textAparato.RX_COMMNAD_GOTO:
            goToLine ();
            break;

         case textAparato.RX_COMMNAD_CLEAR:
            setText ("");
            helper.ebsText ().setText ("");
            changeDirty (false);
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
      if (isShowing ())
         setVisible (helper.ebs ().getVisible ());
   }

   private void tryAttackWidget ()
   {
      ////// OJO! EFECTOS SECUNDARIOS MUY MALOS! ver version y comentario sobre 11.12.2009 00:47
      //////      //(o) TOSEE_javaj_widgets added invokeLater : see if there is any side effect of this ? (09.12.2009 21:27)
      //////      //        NOTE: invoke later was added to allow updating the tree during a click
      //////      javax.swing.SwingUtilities.invokeLater (new Runnable() { public void run() {
      //////         updateData ();
      //////      }});
      //////   }
      //////
      //////
      //////   private void updateData ()
      //////   {
      if (helper.ebsText().hasAll ())
      {
         setText (helper.ebsText ().getText ());
         setTabSize (helper.ebsText ().getTabulator ());

         goToLine ();
         changeDirty (false);
      }
   }

   private void commandLoad ()
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zTextArea.commandLoad", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_LOAD) + "] ignored");
         return;
      }

      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0)
      {
         helper.log.err ("zTextArea.commandLoad", "widget has no attribute fileName. Message load ignored");
         return;
      }

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

/*
   NOTA: este es el me'todo empleado por zMiniEditor pero quedan las siguientes cuestiones por resolver:

      - Podra' leer textos de un jar ? (es posible que si' pues read lee URLs)
      - el modelo (el texto) tiene que estar en la eva y aqui' no lo cargamos
         no'tese que zMiniEditor no puede manejar el texto en si'. Tan solo cargarlo y salvarlo

   private void commandLoad ()
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ()) return;

      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0) return;

      java.io.File fi = new java.io.File (fileName);
      if (!fi.exists ())
      {
         // the file does not exist

         return;
      }

      // try to read the file into the text area
      try { read (new FileReader (fileName), null); }
      catch (Exception e)
      {
         helper.ebsText().err ("Error while reading file " + helper.ebsText ().getFileName ());
      }
      changeDirty (false);
   }
*/

   private void commandSave ()
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zTextArea.commandSave", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_SAVE) + "] ignored");
         return;
      }

      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0)
      {
         helper.log.err ("zTextArea.commandSave", "widget has no attribute fileName. Message save ignored");
         return;
      }

      boolean ok = TextFile.writeFile (fileName, helper.ebsText ().getText ());

      helper.log.dbg (2, "zTextArea.commandSave", "writing into file " + fileName);
      if (! ok)
      {
         helper.log.err ("zTextArea.commandSave", "error writing into file " + fileName);
      }
   }

   private void goToLine ()
   {
      requestFocus();
      requestFocusInWindow();

      int lineNr = helper.ebsText ().getLineToGo ();
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
