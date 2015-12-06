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
   //(o) WelcomeGastona_source_javaj_layout MENU

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       MENU
   <groupInfo>  special containers
   <javaClass>  javaj.widgets.kits.fabrikMenuBar
   <importance> 7
   <desc>       //Creates a menu

   <help>
      //
      //  Menu bar for the container frame
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       MENU
      //
      //       root, submenu, ..., messagToSend
      //       ...., ...    , ..., ...
      //
      //  The first row is "MENU". Next rows specify, each one, an end menu point.
      //  First the whole menu path is, comma separated, specified and at the last column
      //  the message to be send on selection of the menu point.
      //  If part of the path is left in blank it is supposed to be the same
      //  as the previous menu point.
      //
      //  For example:
      //
      //      File, Open   , menuOpen
      //          , Close  , menuClose
      //
      // will create a menu "File" with two end menues "Open" and "Close", and when
      // "Open" is chosen the message "menuOpen" is sent.

   <examples>
      gastSample

      menu layout example

   <menu layout example>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout MENU"
      //
      //   <layout of F>
      //      EVA, 0, 0, 6, 6
      //
      //        , X
      //        , myMenu
      //      X , xText
      //
      //   <layout of myMenu>
      //      MENU,
      //
      //      File, Open   , menuOpen
      //          , Close  , menuClose
      //      View, Typical, menuTypicalView
      //          , Second , menuSecondView
      //          , Other  , Big    , menuBigView
      //          ,        , Medium , menuMediumView
      //          ,        , Small  , menuSmallView
      //
      //#listix#
      //
      //    <-- menuOpen>  BOX, I, //Open this message, ok?
      //    <-- menuClose> MSG, javaj doExit
      //

#**FIN_EVA#

*/

package javaj.widgets.kits;

//  01.11.2004 13:32 pasado a zWidgets
//  04.11.2004 23:22 quitar del mensaje " menu " (poder compartir mensajes con toolbar, otro boton etc)
//

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class fabrikMenuBar
{
   /**
      fabrica y retorna una nuevo menu bar
   */
   public static JMenuBar fabrik (Eva evaTool, String preMsg)
   {
      JMenuBar menu = new JMenuBar ();

      setContent (menu, evaTool, preMsg);
      return menu;
   }

   /**
      llena una JMenuBar (borrando el contenido anterior !)

   //
   // //(o) javaj_layouts layout tipo MENU
   //
   //             <aMenu>
   //               MENU,
   //                  "archival",  Abrir, fopen
   //                            , Cerrar, fclose
   //                            ,  Salir, fexit
   //
   //                  "vistas",  "tasmanyos", Gross,     big
   //                          ,             , Klein,    small
   //                          ,             , Zu klein, toosmall
   //                          ,   "Otras cosinyas", otras
   //                          ,   "bueno adio's",   buenas
   //
   //    Notar que :
   //           un item final acaba con
   //             - un identicador para el mensaje que no debe cambiarse con idioma
   //             - un texto que es dependiente del idioma
   //           el nombre de items intermedios debera' adaptarse a cada idoma
   //
   */
   public static void setContent (JMenuBar obj, Eva evalay, String preMsg)
   {
      obj.removeAll ();

      // interesting content starts at second line (index 1)
      //
      int rou = 1;
      while (rou < evalay.rows ())
      {
         rou = addEllo (obj, evalay, preMsg, rou, 0);
      }
   }

   private static int addEllo (Container pana, Eva evalay, String preMsg, int row, int col)
   {
      if (col >= evalay.cols (row)) return row+1;

      String name = evalay.getValue (row, col);

      // mirar si es item final !
      //
      if (evalay.cols (row)-2 == col)
      {
         // es un item final
      	 String itemId = evalay.getValue (row, col+1);
         //System.out.println  ("item final name=" + name + " text=" + itemId);
         addItem (pana, evalay.getValue (row, col+1), name, preMsg);
         return row + 1;
      }

      // es un submenu
      JMenu novo = new JMenu (name);
      int rr = row;
      while (rr < evalay.rows () &&
             (name.equals (evalay.getValue (rr, col)) ||
             evalay.getValue (rr, col).length () == 0))
      {
         rr = addEllo (novo, evalay, preMsg, rr, col+1);
         //rr ++;
      }
      pana.add (novo);
      return rr;
   }

   //(o) javaj_widgets_primitives solucionar el tema de JMenuItem (zWidget ? aceptar cualquier widget en un menu ?)
   //
   private static void addItem (Container pana, String item, String literal, final String preMsg)
   {
      // colocar el widget que sea !! (por defecto deberi'a ser JMenuItem ...
//         Component compos = getComponentWidget (name);
//         pana.add (compos);

      // colocar un JMenuItem siempre por ahora...
      AbstractButton mi = new JMenuItem (literal);
      mi.setName (item);

      // on the fly listener ... (change with primitive menuItem or so)
	   mi.addActionListener (new ActionListener ()
	   {
		   public void actionPerformed(ActionEvent e)
		   {
		      JMenuItem tio = (JMenuItem) e.getSource ();
		      String jol = tio.getName ();
			   // Mensaka.sendPacket (preMsg + " menu " + jol);
			   Mensaka.sendPacket (preMsg + jol);
		   }
	   });

      pana.add (mi);
   }
}
