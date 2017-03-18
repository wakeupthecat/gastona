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

/*
   //(o) WelcomeGastona_source_javaj_layout TOOLBAR

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       TOOLBAR
   <groupInfo>  special containers
   <javaClass>  javaj.widgets.kits.fabrikToolBar
   <importance> 4
   <desc>       //Creates a tool bar

   <help>
      //
      //  Create a tool bar of buttons, optionally with tool tip and image
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       TOOLBAR, 0 | 1
      //
      //       message1 , tip text 1, image
      //       ...      , ...       , ...
      //
      //  The first row starts with "TOOLBAR", then if the toolbar should be flotable (1)
      //  or fix (0). From the second to the last row all tool bar buttons are specified with
      //  the message to send, tool tip text and an image.
      //


   <examples>
      gastSample

      toolbar layout example

   <toolbar layout example>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout TOOLBAR"
      //
      //
      //   <layout of F>
      //      EVA, 10, 10, 6, 6
      //
      //        ,  X
      //        , tula
      //      X , xText
      //
      //   <layout of tula>
      //      TOOLBAR, 0
      //
      //      wantABook    , Find a book  ,  javaj/img/book.png
      //      wantAFloppy  , Find a floppy,  javaj/img/floppy.png
      //      wantToWrite  , Take notes   ,  javaj/img/write.png
      //
      //#listix#
      //
      //  <-- wantAFloppy> BOX, W, //You want ... a floppy?

#**FIN_EVA#


*/

package javaj.widgets.kits;

//  01.11.2004 13:32 pasado a zWidgets
//  04.11.2004 23:22 quitar del mensaje " toolbar " (poder compartir mensajes con menu, otro boton etc)
//

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import java.awt.event.*;
import javax.swing.*;

/**
   @author Alejandro Xalabarder Aulet
*/
public class fabrikToolBar
{
   /**
      fabrica y retorna una nueva toolbar
   */
   public static JToolBar fabrik (Eva evaTool, String preMsg)
   {
      JToolBar tool = new JToolBar ();

      setContent (tool, evaTool, preMsg);
      return tool;
   }

   /**
      llena una existente toolbar (borrando el contenido anterior !)

      Ejemplo de eva:

      <layout of tula> TOOLBAR, 0
         bLinterna, Buscar un fichero, images/Linterna.gif
         bExplorer, abrir explorador,  images/2Explorer.gif
         bEditar,   editar un poco,    images/EditarEscribir.gif
         bMsDOS,    DOS shell,         images/MSDOSshell.gif
         bMiDesto,  Otro MiDesto,      images/MiDesto.gif

   */
   public static void setContent (JToolBar obj, Eva evaTool, final String preMsg)
   {
      obj.removeAll ();

      // set if floatable or not floatable tool bar (default is floatable)
      //
      //    Examples floatable:
      //
      //       <tool1> TOOLBAR
      //                ...
      //       <tool2> TOOLBAR, 1
      //                ...
      //    Example NO floatable:
      //
      //       <tool1> TOOLBAR, 0
      //
      boolean isFloatable = (evaTool.cols(0) <= 1) || (stdlib.atoi (evaTool.getValue (0,1)) == 1);
      obj.setFloatable (isFloatable);

      // interesting content starts at second line (index 1)
      //
      for (int ii = 1; ii < evaTool.rows (); ii ++)
      {
         ImageIcon ima  = javaLoad.getSomeHowImageIcon (evaTool.getValue(ii, 2));

         // create concrete action  (see method JButton JToolBar::add (Action))
         //
         AbstractAction act = new AbstractAction (evaTool.getValue(ii, 0).substring(1), ima) {
                              public void actionPerformed(ActionEvent e) {
                                 Mensaka.sendPacket (preMsg +
                                                     ((preMsg.length () > 0) ? " ": "") +
                                                     e.getActionCommand());
                              }
                          };


         // add it and get the button object. Set the action command and the tool tip
         //  Note: in future maybe save the buttons (register) in an array
         //
         JButton bb = obj.add (act);

         bb.setActionCommand  ((String) evaTool.getValue (ii, 0));   // << action command
         bb.setToolTipText    ((String) evaTool.getValue (ii, 1));   // << tool tip
      }
   }
}
