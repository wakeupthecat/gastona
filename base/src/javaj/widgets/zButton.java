/*
package javaj.widgets
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

import java.awt.event.*;
import javax.swing.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.javaLoad;

import javaj.widgets.basics.*;
import javaj.widgets.kits.*;


/*
   //(o) WelcomeGastona_source_javaj_widgets (b) zButton

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zButton
   <groupInfo>  button
   <javaClass>  javaj.widgets.zButton
   <prefix>  b
   <importance> 10
   <desc>       //A typical button, also useful as file chooser dialog facility (rapid prefix 'b')

   <help>
      //
      // Typical button whose data is the label, an image (png, gif or jpeg) can be associated as well.
      // A special characteristic of this widget is that can launch dialogs for file(s) and directory(ies)
      // chooser. The attributes associated with this feature are 'DIALOG' and 'chosen', if 'DIALOG'
      // is given then the button acts as dialog and in the attribute 'chosen' will return the chosen
      // item(s).
      //
      // Note : If not given in #data# the button takes its label from its own name. This is only a
      //        facility for the development and serves only as default label.Do not confuse button's
      //        name with its label, among other things widget names cannot be changed.
      //
      // Widget characteristics: "Common" and "Drop files and directories".
      //

   <prefix>  b

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , text                       , //Caption text of the button
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      image            , in    , fileName                   , //File name of the image (png, gif or jpg)
      mnemonic         , in    , char                       , //Mnemonic for the button (see java setMnemonic)
      DIALOG           , in    , FILE | FILES | DIR | DIRS  , //The button launches a file chooser dialog of one file or multiple files or one directory or multiple directories
      DIALOG FILTER    , in    , extension                  , //@todo documentar esto
      chosen           , out   , string or list             , //If attribute 'DIALOG' is given, this attribute holds the chosen file(s) or directory(ies). In case of multiselection the attribute is a Eva table (fullPath).
      droppedFiles     , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped files. Note that this attribute has to exist in order to enable drag & dropping files into this Component.
      droppedDirs      , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped directories. Note that this attribute has to exist in order to enable drag & dropping files into this Component.

   <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control
                  , out , button has been pressed
      droppedFiles, out , If files drag & drop enabled this message indicates that the user has dropped files (see attribute 'droppedFiles')
      droppedDirs , out , If directories drag & drop is enabled this message indicates that the user has dropped directories (see attribute 'droppedDirs')


   <examples>
      gastSample

      <!data4Tester
      hello zButton
      <!hello zButton2
      single file chooser
      file choosers

   <data4Tester>
      //#data#
      //
      //    <bName> Caption for the button
      //

   <hello zButton>
      //#javaj#
      //
      //    <frames> bMyButton, Hello zButton
      //
      //
      //#data#
      //
      //    <bMyButton>       "Press the button"
      //    <bMyButton image> javaj/img/ok.png
      //
      //#listix#
      //
      //    <-- bMyButton>
      //       SET VAR, bMyButton, "Ok, you did it!"
      //       MSG, bMyButton data!
      //

   <hello zButton2>
      //#javaj#
      //
      //    <frames> bMy button for test, Hello zButton2
      //
      //#listix#
      //
      //    <-- bMy button for test>
      //       SET DATA!, bMy button for test,, "Ok, you've done!"
      //

   <single file chooser>
      //#javaj#
      //
      //   <frames> F, Example Buttons
      //
      //   <layout of F>
      //         EVA, 10, 10, 4, 4
      //
      //      ---, A           , 300   , X  , A
      //       A , lChosen file, eField, -  , bFChooser
      //
      //#data#
      //
      //   <bFChooser DIALOG> FILE
      //   <bFChooser image> javaj\img\leaf.png
      //
      //#listix#
      //
      //   <-- bFChooser>
      //      -->, eField,, @<bFChooser chosen>
      //



   <file choosers>
      //#javaj#
      //
      //   <frames> F, Example buttons as file choosers
      //
      //   <layout of F>
      //         EVA, 10, 10, 4, 4
      //
      //         ,        ,            ,            ,          , X
      //         , bSimple, bFileSingle, bFileMulti, bDirSingle
      //       X , oConsole, -, -, -, -
      //
      //#data#
      //
      //   <bFileSingle DIALOG> FILE
      //   <bFileMulti DIALOG> FILES
      //   <bDirSingle DIALOG> DIR
      //
      //   <bFileSingle image> javaj\img\Fichero_2_0.png
      //   <bFileMulti image>  javaj\img\Fichero_2_1.png
      //   <bDirSingle image>  javaj\img\folder.png
      //
      //#listix#
      //
      //   <-- bSimple>
      //      //simple button clicked (no dialog associated)
      //      //
      //
      //   <-- bFileSingle>
      //      //chosen file "@<bFileSingle chosen>"
      //      //
      //
      //   <-- bFileMulti>
      //      //chosen files:
      //      //
      //      LOOP, EVA, bFileMulti chosen
      //      @<fullPath>
      //      =,,
      //      //
      //      //
      //
      //   <-- bDirSingle>
      //      //chosen directory "@<bDirSingle chosen>"
      //      //
      //
      //

#**FIN_EVA#

*/

/**
   zButton : javaj zWidget representing a GUI button
*/
public class zButton extends JButton implements ActionListener, MensakaTarget
{
   private basicAparato helper = null;
   private dndFileTransHandler dndHandler = null;

   private String currentIconName = null;

   public zButton ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zButton (String map_name, String slabel)
   {
      super (slabel);
      construct (map_name);
   }

   public void setName (String map_name)
   {
      construct (map_name);
   }

   private void construct (String map_name)
   {
      super.setName (map_name);
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      addActionListener (this); // yo mismo
   }

   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setNameDataAndControl (null, euData, null);

            setText (helper.decideLabel (getText ()));

            if (helper.ebs().getImageFile () != currentIconName)
            {
               currentIconName = helper.ebs().getImageFile ();
               ImageIcon ima = javaLoad.getSomeHowImageIcon (currentIconName);
               setIcon (ima);
            }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setNameDataAndControl (null, null, euData);

            setEnabled (helper.ebs ().getEnabled ());
            if (isShowing ())
            {
               //NOTE: 2011.09.11 The chek of isShowing is needed
               //      For instance, following example would fail if not
               //
               // #javaj#
               //    <frames> bMyButton, Hello zButton
               //
               //      Problem was introduced on 2010 Dec 31 (bildID 11206)
               //      and detected and fixed today!
               //
               setVisible (helper.ebs ().getVisible ());
            }

            // AltChar or mnemonic
            char c = helper.ebs().getAltChar ();
            if (c != 0)
               setMnemonic(c);

            if (dndHandler == null)
            {
               if (helper.ebs ().isDroppable ())
               {
                  // made it "dropable capable"

                  // drag & drop ability
                  //
                  /**
                      Make the zWidget to drag'n'drop of files or directories capable
                      Note that the zWidget is not suscribed to the drag'n'drop message itself ("%name% droppedFiles" or ..droppedDirs")
                      therefore it will not take any action on this event. It is a task of a controller to
                      examine, accept, process and insert into the widget the files if desired and convenient

                      Note that at this point the control for the zWidget (helper.ebs().getControl ())
                      is null and we have to update it into the handler when it chanhges
                  */
                  dndHandler = new dndFileTransHandler (
                                 helper.ebs().getControl (),
                                 helper.ebs().evaName (""),
                                 dndFileTransHandler.arrALL_FIELDS
                                 );
                  setTransferHandler (dndHandler);
               }
            }
            else
            {
               // every time the control changes set it to the drag&drop handler
               dndHandler.setCommunicationLine (helper.ebs().getControl ());
            }
            break;

         default:
            return false;
      }

      return true;
   }

   public void actionPerformed(ActionEvent ev)
   {
      //(o) javaj_widgets Experimental Launch standard dialogs on button action

      String assocDialog = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "DIALOG");
      String assocFilter = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "DIALOG FILTER");
      if (assocDialog != null)
      {
         //Eva files = helper.ebs().getAttribute (helper.ebs().CONTROL, true, "chosen." + assocDialog);
         Eva files = helper.ebs().getAttribute (helper.ebs().CONTROL, true, "chosen");

         if (assocDialog.equalsIgnoreCase ("FILE"))
         {
            if ( ! fileDialog.selectFile (assocFilter, null, files, false)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("FILES"))
         {
            if ( ! fileDialog.selectFile (assocFilter, null, files, true)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("DIR"))
         {
            if ( ! fileDialog.selectDir (null, files, false)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("DIRS"))
         {
            if ( ! fileDialog.selectDir (null, files, true)) return;  // nothing selected
         }
         widgetLogger.log().dbg (9, "zButton", "selected files or directories : " + files);
      }

      // action of a normal button
      helper.signalAction ();
   }
}
