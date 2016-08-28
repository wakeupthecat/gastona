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
import java.awt.event.*;

import javaj.widgets.*;

import javax.swing.JTextField;
import javax.swing.event.*; // DocumentListener etc

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;
import javaj.widgets.text.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (e) zEditField

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zEditField
   <groupInfo>  text
   <javaClass>  javaj.widgets.zEditField
   <prefix> e
   <importance> 10
   <desc>       //An edit field (rapid prefix 'e')

   <help>

      //
      // A typical edit field. The only envent triggered in this widget is the empty message
      // which is sent when pressing enter on the widget.
      //
      // Widget characteristics: "Common"
      //


   <attributes>
      name           , in_out, possibleValues             , desc

                     , in    , text                       , //Data for the edit field, that is the text to be displayed/edited
      visible        , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled        , in    , 0 / 1                      , //Value 0 to disable the widget
      var            , in    , Eva name                   , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)
      dirty          , out   , 0 | 1                      , //For internal use, indicates if the data should be synchronised with the native widget

<!09.08.2009 16:49 Note : it uses the textAparato EBS but fileName etc is not implemented in zEditField
<!      wrapLines      , in    , 0 | 1                      , //Tells the widget if lines should be wraped to the width of the widget
<!      tabulator      , in    , numeric                    , //Tabulator size
<!      fileName       , in    , name of file               , //If given then the value of the text is get from the given file

   <messages>

      msg      , in_out, desc

               , out  , The user has pressed enter in the text field
      data!    , in   , update data into the widget
      control! , in   , update control

      revert   , in   , Use internally, reverts the contents of the native widget into the eva variable asigned for that (zWidget name or dataInto if specified)

<!09.08.2009 16:49 Note : it uses the textAparato EBS but fileName etc is not implemented in zEditField
<!      load  , in   , loads the content from the specified file of the attribute 'fileName'
<!      save  , in   , saves the content into the specified file of the attribute 'fileName'
<!      clear , in   , clears its contents

   <examples>
      gastSample

      hello zEditField

   <hello zEditField>
      //#javaj#
      //
      //    <frames> F, Hello zEditField
      //
      //    <layout of F>
      //          EVA,
      //         , X
      //         , eEditField
      //
      //#data#
      //
      //    <eEditField> Initial value
      //
      //#listix#
      //
      //    <-- eEditField>
      //       //the text "@<eEditField>" has been entered
      //       //
      //


#**FIN_EVA#

*/


/**
   zEditField : zWidget representing a GUI EditField

   @see zWidgets
   @see javaHelper.gast

*/
public class zEditField extends JTextField implements ActionListener, DocumentListener, MensakaTarget
{
   private textAparato helper = null;

   private int largoChar = 10;
   private boolean isFixSize = false;

   public zEditField ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

//   public zEditField (String map_name)
//   {
//      build (map_name, 5, false);
//   }

   public zEditField (String map_name, int sizeCharacters, boolean fixSize)
   {
      super (sizeCharacters + 3);
      build (map_name, sizeCharacters, fixSize);
   }

   public void setName (String map_name)
   {
      build (map_name, 5, false);
   }

   private void build (String map_name, int sizeCharacters, boolean fixSize)
   {
      super.setName (map_name);
      isFixSize = fixSize;
      largoChar = sizeCharacters;

      helper = new textAparato (this, new textEBS (map_name, null, null));

      // abilitate me to listen to myself !
      //
      addActionListener (this);
      getDocument().addDocumentListener (this);
   }

   private int lenfactor (int sizeChar)
   {
      // float fac = (sizeChar > 10) ? 7.1f: 8.f;
      float fac = 8.f;

      return (int) ((sizeChar+3) * fac);
   }

   public Dimension getMaximumSize()
   {
      if (isFixSize)
         return getPreferredSize ();

      return super.getMaximumSize();
   }

   public Dimension getMinimumSize()
   {
      if (isFixSize)
         return getPreferredSize ();
      return super.getMinimumSize();
   }

   public Dimension getPreferredSize()
   {
      if (isFixSize)
         return new Dimension (lenfactor (largoChar), 22);
      return super.getPreferredSize();
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebsText ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebsText ().setDataControlAttributes (null, euData, pars);
            if (helper.ebsText().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            helper.updateControl (this);

            setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebs ().getVisible ();
            if (visible || isShowing ())
               setVisible  (visible);
            break;

         case widgetConsts.RX_REVERT_DATA:
            if (helper.ebsText().hasData ())
            {
               helper.ebsText ().setText (getText ());
               changeDirty (false);
            }
            break;

         default:
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsText().hasAll ())
      {
         setText (helper.ebsText ().getText ());
         changeDirty (false);
      }
   }

   public void actionPerformed(ActionEvent ev)
   {
      helper.signalAction ();
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
