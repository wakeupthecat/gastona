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
import java.awt.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.javaLoad;
import de.elxala.langutil.graph.uniUtilImage;

import javaj.widgets.basics.*;
import javaj.widgets.kits.*;
import javaj.widgets.text.*;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

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
      graffiti         , in    , EvaName                    , //Eva variable containing the painting (set of svgfile, path, polygon, polyline, rect,circle, oval, ellipse, text). The resulting painting will be resized to fit on the value of the property gastona.defaultButtonPaintingSize (16)
      mnemonic         , in    , char                       , //Mnemonic for the button (see java setMnemonic)
      DIALOG           , in    , FILE | FILES | DIR | DIRS  , //The button launches a file chooser dialog of one file or multiple files or one directory or multiple directories
      DIALOG FILTER    , in    , extension                  , //@todo documentar esto
      widget           , in    , zWidget name               , //zWidget name (usually edit text) associated with the FILE or DIR dialog (only single selection). If set the value of the selection is set automatically into the widget
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

      hello zButton
      <!hello zButton2
      single file chooser
      file choosers
      button graffiti

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
      //       -->, bMy button for test data!,, "Ok, you've done!"
      //

   <single file chooser>
      //#javaj#
      //
      //   <frames> F, Example Buttons
      //
      //   <layout of F>
      //         EVA, 10, 10, 4, 4
      //
      //      ---, A              , 300     , X  , A
      //       A , lChosen file   , eField  , -  , bFChooser
      //         , lManual setting, eManual , -
      //
      //#data#
      //
      //   <bFChooser DIALOG> FILE
      //   <bFChooser widget> eField
      //   <bFChooser image> javaj\img\leaf.png
      //
      //#listix#
      //
      //   <-- bFChooser>
      //      -->, eManual data!,, @<bFChooser chosen>
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
      //          ,, @<fullPath>
      //      //
      //      //
      //
      //   <-- bDirSingle>
      //      //chosen directory "@<bDirSingle chosen>"
      //      //
      //
      //

   <button graffiti>
      //#javaj#
      //
      //   <frames> main
      //
      //
      //   <layout of main>
      //      EVA, 10, 10, 2, 2
      //
      //         ,           , X
      //         , bFile
      //       X , bHome     , bCorto
      //         , bCarabassa, -
      //
      //#data#
      //
      //   <bFile graffiti>
      //      svgfile, "", javaj/img/folder_explorer.svg
      //
      //   <bCorto graffiti>
      //      rect  , "fc:yellow", 50, 30, 50,20
      //      circle, "fc:#099281", 10, 10, 50,20
      //      circle, "fc:#099281", 10, 10, 50,20
      //      circle, "fc:#099281", 10, 10, 50,20
      //
      //   <bCarabassa graffiti>
      //      path, "sw: .75;fc:#FF9900",     "M  240  157 C   244  157  252  159  257  163  261  166  266  172  267  178  269  183  269  190  267  195  265  200  263  208  256  211  249  214  235  214  228  214  220  213  217  212  212  209  208  206  202  200  201  194  200  188  202  179  205  174  208  168  212  165  216  162  220  159  226  158  228  158  231  157  231  160  233  160  235  161  236  156 z"
      //      path, "sw: .75;fc:#136F11",     "M  235  157 C   236  156  237  153  236  151  236  149  234  147  233  146  232  145  230  145  230  146  230  147  232  150  232  152  232  153  231  154  230  156  230  156  229  157  229  158  229  158  229  159  230  160  230  160  232  161  232  161  233  161  234  160  234  160  235  159  235  159 z"
      //      path, "sc:none;fc:#E47200",     "M  256  206 C   258  200  259  195  260  190  260  185  260  181  259  178  258  174  257  172  255  170  253  168  249  166  248  164  246  163  243  162  244  163  245  164  252  168  254  172  256  176  258  180  258  186  259  191  256  201  255  206"
      //      path, "sc:none;fc:#E47200",     "M  238  164 C   238  167  242  177  242  184  243  191  242  206  242  207  243  209  244  196  244  192  245  187  245  183  244  179  243  174  241  170  239  165  239  165  237  161 z"
      //      path, "sc:none;fc:#E47200",     "M  227  211 C   228  209  225  194  225  188  225  181  227  173  227  170  228  167  229  166  228  167  227  169  224  176  223  181  222  186  222  191  222  196  223  201  227  212 z"
      //      path, "sc:none;fc:#E47200",     "M  213  205 C   211  202  208  198  208  194  207  190  208  182  210  178  211  173  215  168  216  167  216  166  214  167  213  170  211  173  208  180  207  185  206  190  207  196  208  199  209  203  212  204  214  206"
      //      path, "sc:none;fc:#E47200",     "M  263  201 C   262  203  262  204  258  206  254  208  246  210  242  211  237  212  234  212  230  212  226  212  219  211  216  210  213  209  211  206  212  206  212  207  214  211  218  212  222  214  229  214  235  214  241  214  247  213  252  212  256  210  260  208  262  206  264  203  264  200  265  197"
      //
      //   <bHome graffiti>
      //      path, "sw: .75;fc:#FFF4E9",     "M  247  101 c   1 -5  5 -23  5 -32  0 -9 -4 -16 -4 -24  1 -8  1 -19  6 -25  5 -6  15 -11  23 -12  8 -1  20  2  27  7  6  5  10  13  12  20  2  7  2  13  1  22 -2  9 -6  23 -9  29 -4  5 -8  3 -12  3 -4 -1 -9 -6 -12 -5 -3  1 -4  9 -5  13 -1  4  0  7  1  11"
      //      path, "sw: .75;fc:#AFB8C9",     "M  244  323 c   0  12  4  50  5  67  1  17  2  22  1  37 -1  15 -4  39 -6  50 -2  11 -3  14 -3  18  0  4 -2  3  4  5  6  2  23  3  32  4  9  1  19  1  25  0  6 -1  10  0  10 -4  0 -4 -6 -10 -9 -22 -3 -12 -7 -28 -9 -47 -2 -19 -4 -53 -3 -70  1 -17  6 -22  7 -29  1 -7  1 -11 -2 -14 -3 -3 -7 -5 -15 -5 -8  0 -24  5 -31  6 -7  1 -6 -8 z"
      //      path, "sw: .75;fc:#ECE0DC",     "M  249  323 c  -6  1 -12  3 -17 -6 -5 -9 -12 -25 -15 -45 -3 -20 -1 -48  0 -72  1 -24  3 -54  7 -70  4 -16  8 -23  15 -28  7 -5  18 -5  27 -4  9  1  17  3  25  12  8  9  15  20  21  41  6  21  14  59  16  83  2  24 -2  49 -5  64 -3  15 -4  24 -10  27 -6  3 -16 -5 -26 -6 -10 -1 -26  0 -32  0  0  0 -6  4 z"
      //      path, "sw: .75;fc:#FFF4E9",     "M  248  296 c   2  5  4  10  6  14  2  4  2  8  7  9  5  1  19  1  23 -2  4 -3  4 -12  3 -16 -1 -4 -9 -5 -11 -7 -2 -2 -2 -4 -2 -6"
      //      path, "sw: .75;fc:#ECE0DC",     "M  289  137 c  -3 -9 -5 -18 -12 -20 -7 -2 -23 -1 -29  7 -6  8 -7  26 -8  39 -1  13  0  28  0  40  0  12  0  19  0  30  0  11  1  26  2  36  1  10  2  21  4  26  2  5  4  2  9  1  5 -1  19  1  22 -6  3 -7 -4 -21 -3 -38  1 -17  6 -47  8 -65  2 -18  5 -31  7 -44"
      //
      //   <bHome graffiti press>
      //      path, "sw: .75;fc:#FFF4E9",     "M  304  103 c   1 -5  5 -22  5 -32  0 -9 -5 -15 -4 -24  0 -8  0 -19  5 -25  4 -6  14 -11  22 -12  9 -1  21  2  27  6  7  4  11  13  13  20  2  7  3  13  1  22 -1  8 -5  23 -8  29 -3  5 -7  3 -11  3 -4  0 -10 -6 -13 -4 -3  1 -4  9 -4  13 -1  4  0  7  1  11"
      //      path, "sw: .75;fc:#AFB8C9",     "M  299  310 c   3  12  17  47  22  64  6  17  8  22  11  36  3  14  7  39  8  50  2  12  2  15  3  18  1  4 -1  4  5  4  5  0  20 -2  28 -3  9 -2  17 -4  22 -6  5 -2  9 -2  8 -7 -2 -4 -8 -8 -14 -19 -6 -11 -14 -25 -21 -44 -7 -18 -17 -51 -21 -67 -4 -17 -1 -23 -2 -30 -1 -8 -2 -11 -6 -14 -3 -2 -7 -3 -14 -1 -6  2 -20  10 -25  13 -5  4 -7 -6 z"
      //      path, "sw: .75;fc:#FFF4E9",     "M  408  275 c   5  3  9  5  14  7  4  2  6  5  10  3  5 -2  16 -11  17 -16  2 -5 -4 -12 -7 -15 -4 -2 -10  2 -13  2 -3  0 -5 -2 -6 -4"
      //      path, "sw: .75;fc:#ECE0DC",     "M  341  125 c  -8 -5 -15 -11 -22 -8 -7  2 -18  14 -18  23  0  10  11  25  18  36  7  11  18  22  25  31  7  9  11  15  18  24  8  8  17  19  24  27  8  7  15  15  20  17  4  3  4 -1  8 -5  3 -3  15 -10  13 -18 -2 -8 -16 -14 -26 -28 -10 -14 -25 -40 -34 -56 -10 -15 -16 -27 -22 -38"
      //      path, "sw: .75;fc:#AFB8C9",     "M  279  299 c  -5  11 -17  48 -23  65 -6  16 -7  21 -14  34 -6  13 -19  34 -24  44 -6  10 -8  12 -10  16 -2  3 -3  2  1  6  5  4  17  12  25  17  7  5  15  9  20  10  5  2  8  5  10  1  1 -4 -1 -12  1 -24  2 -12  2 -25  11 -48  9 -22  35 -69  43 -90  8 -20  11 -29  6 -34 -6 -5 -32  2 -40  2 -7  1 -1 -10 z"
      //      path, "sw: .75;fc:#ECE0DC",     "M  284  299 c  -3 -1 -10  1 -14 -9 -3 -10 -7 -35 -7 -52  0 -18  3 -34  7 -54  4 -19  10 -48  16 -62  6 -14  10 -19  18 -23  7 -4  18 -3  26  0  7  4  12  1  18  21  5  20  16  73  17  101  2  28 -5  51 -8  66 -2  14 -1  17 -7  19 -7  2 -19 -4 -29 -5 -10 -2 -25 -4 -30 -5  0  0 -7  3 z"
      //      path, "sw: .75;fc:#FFF4E9",     "M  195  234 c  -2  5 -4  10 -5  14 -1  5 -4  8 -1  12  3  4  13  13  19  14  5  0  11 -6  13 -10  1 -4 -4 -10 -4 -13  0 -3  1 -4  3 -6"
      //      path, "sw: .75;fc:#ECE0DC",     "M  332  143 c   4 -9  8 -17  4 -23 -4 -6 -17 -16 -26 -14 -10  1 -23  14 -32  23 -10  9 -19  22 -27  30 -8  9 -13  14 -20  23 -7  8 -16  19 -23  28 -6  8 -12  17 -14  22 -1  5  2  4  6  7  5  2  13  13  21  10  7 -4  11 -18  23 -31  12 -12  35 -31  49 -43  14 -12  24 -20  35 -28"
      //
      //#listix#
      //
      //   <main0>
      //      PROP=, gastona.defaultGraffitiSize, 46
      //


#**FIN_EVA#

*/

/**
   zButton : javaj zWidget representing a GUI button

   //(o) TODO_zButton some improvements in zButton
   =date 2015.01.05 14:59
     TODO:
         if a DIALOG button with an associated widget and admit dropping then
         assign automatically the widget value when a file / dir is dropped on the button

         To do that zButton has to notify itself to "... droppedFiles" since this is launched directly by dndFileTransHandler::importData
         and we don't have callback from it

     TODO:
         extend MensakaTarget with a virtual method (optional ?)
         ::addedListener (String message, Object obj)
         which is called by mensaka for the owner of a message for all the subscriptors that message (even if they are subscribed before the declaration)

         gives the opportunity to the widget to know the listeners (e.g. if listeners for "droppedFiles" then activate dropping)
         the widget really don't need to store the object address since it is managed by mensaka, but can use it for instance to know
         if it is itself

            void addedListener (String message, Object obj)
            {
               if (message.equals ("myName droppingFiles")
                  enableDropping = (obj != this);
            }

     TODO:
         maybe if button of type DIALOG always enable dropping files!

     TODO:
         add some visible signal in the button to indicate that is a droppable area

*/
public class zButton extends JButton implements ActionListener, MensakaTarget
{
   private basicAparato helper = null;
   private smallTextEBS widgetAssoc = null; // maybe is better to use a basicMando or textMando or similar ...
   private dndFileTransHandler dndHandler = null;

   private String currentIconName = null;

   private float calcScaleX = 1.f;
   private float calcScaleY = 1.f;
   private float calcOffsetX = 0.f;
   private float calcOffsetY = 0.f;


   public zButton ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zButton (String map_name, String slabel)
   {
      super (slabel);
      construct (map_name);
   }

//   public java.awt.Dimension getPreferredSize ()
//   {
//      float FAX = 8.2f;
//      return new java.awt.Dimension ((int) (getText ().length () * FAX), 30);
//   }


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

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            setText (helper.decideLabel (getText ()));

            if (helper.ebs().getImageFile () != currentIconName)
            {
               currentIconName = helper.ebs().getImageFile ();
               ImageIcon ima = javaLoad.getSomeHowImageIcon (currentIconName);
               setIcon (ima);
            }

            if (helper.ebs().getGraffiti () != null)
            {
               ImageIcon icon = new ImageIcon (
                                     uniUtilImage.graffitiToBufferedImage (
                                             helper.ebs().getGraffiti (),
                                             helper.ebs().getGraffitiFormat ())
                                    );
               setIcon (icon);
               if (helper.ebs().getGraffitiPress () != null)
                  setPressedIcon (new ImageIcon (
                                      uniUtilImage.graffitiToBufferedImage (
                                             helper.ebs().getGraffitiPress (),
                                             helper.ebs().getGraffitiFormat ())
                                 ));
               else setPressedIcon (null);
            }

            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            // AltChar or mnemonic
            char c = helper.ebs().getAltChar ();
            if (c != 0)
               setMnemonic(c);

            // review associated widget, only valid for FILE and DIR (single selection)
            // widgetAssoc to null if no assocWidget, else ensure the widget object
            //
            String assocWidget = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "widget");
            widgetAssoc = (assocWidget == null || assocWidget.length () == 0) ? null:
                          (widgetAssoc == null) ? new smallTextEBS (assocWidget, helper.ebs().getData (), helper.ebs().getControl ()): widgetAssoc;

            if (dndHandler == null)
            {
               if (helper.ebs ().isDroppable ())
               {
                  // made it "dropable capable"

                  // drag & drop ability
                  //
                  /**
                      Make the zWidget to drag'n'drop of files or directories capable
                      Note that the zWidget is not subscribed to the drag'n'drop message itself ("%name% droppedFiles" or ..droppedDirs")
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
            String init = (widgetAssoc != null) ? widgetAssoc.getText (): null;
            if (!fileDialog.selectFile (assocFilter, init, files, false)) return;  // nothing selected
            if (widgetAssoc != null)
            {
               // set value and emulate widget change signal!
               widgetAssoc.setText (files.getValue (0, 0));
               Mensaka.sendPacket (widgetAssoc.getName () + " data!", widgetAssoc.getData ());
               Mensaka.sendPacket (widgetAssoc.getName (), widgetAssoc.getData ());
            }
         }
         else if (assocDialog.equalsIgnoreCase ("FILES"))
         {
            if ( ! fileDialog.selectFile (assocFilter, null, files, true)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("DIR"))
         {
            String init = (widgetAssoc != null) ? widgetAssoc.getText (): null;
            if ( ! fileDialog.selectDir (init, files, false)) return;  // nothing selected
            if (widgetAssoc != null)
            {
               // set value and emulate widget change signal!
               widgetAssoc.setText (files.getValue (0, 0));
               Mensaka.sendPacket (widgetAssoc.getName () + " data!", widgetAssoc.getData ());
               Mensaka.sendPacket (widgetAssoc.getName (), widgetAssoc.getData ());
            }
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
