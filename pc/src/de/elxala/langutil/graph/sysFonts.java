/*
package de.elxala.langutil
(c) Copyright 2009 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_javaj_variables sysDefaultFonts

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_ variables
   <name>       sysDefaultFonts
   <groupInfo>  look
   <javaClass>  de.elxala.langutil.graph.sysDefaultFonts
   <importance> 4
   <desc>       //Define default fonts for all or specific java GUI Swing components

   <help>
      //
      //  This variable permits setting default font, size and type for all, or individually for each,
      //  java Swing components (GUI components). Since almost all javaj zWidgets are based on Swing
      //  components, this default values will affect them as well. Note: This option use the java
      //  class javax.swing.UIManager, for more information consult java documentation about UIManager
      //  and Swing Components.
      //
      //  Syntax:
      //
      //    <sysDefaultFonts>
      //       Font family,  size, style, Component, ...
      //       ...
      //
      //    More rows might be used and each one defines
      //
      //       Font family: a name identifiying the Font (i.e. "Arial")
      //       Size       : Size of the font (11 is a typical value)
      //       Type       : one of 0 (Normal) 1 (Bold) 2 (italic) 3 (bold and italic)
      //       Component  : a component group (i.e. "Button") or * if it has to be applied to all
      //                    known system components
      //
      //  List of "all knonw system components":
      //
      //    Button, CheckBox, RadioButton, ToggleButton, ComboBox, Label, Panel, TitledBorder
      //    List, Table, TableHeader, Tree, Menu, MenuBar, MenuItem, PopupMenu, Text, TextArea,
      //    TextField, TextPane, EditorPane, ToolBar, ToolTip, ColorChooser, ProgressBar, OptionPane,
      //    ScrollPane, PasswordField
      //
      //  Example:
      //
      //    <sysDefaultFonts>
      //       Arial,   10, 0, *
      //       Verdana, 11, 3, Button, ToggleButton
      //       Verdana, 11, 0, Label, ..
      //       ...
      //
      //

   <examples>
      gastSample

      default fonts example

   <default fonts example>

      //#javaj#
      //
      //   <frames>
      //      F, "Default fonts example"
      //
      //   <layout of F>
      //
      //     EVA, 10, 10, 5, 5
      //
      //        ,        ,  200
      //        , bButton, eTextField,
      //
      //    <sysDefaultFonts>
      //       Consolas, 15, 0, TextField
      //       Verdana, 30, 3, Button, ToggleButton
      //
      //#data#
      //
      //   <eTextField> //Is this Consolas?

#** FIN_EVA#
*/

package de.elxala.langutil.graph;

import java.awt.*;
import javax.swing.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

public class sysFonts
{
   private static logger log ()
   {
      return sysDefaults.log;
   }

   public static void setDefaultFonts ()
   {
      setDefaultFonts ("Arial", 12);
      setDefaultFonts ("Arial", 11, new String [] { "RadioButton.font", "CheckBox.font", "TableHeader.font", "Button.font", "Label.font" });
   }

   public static void setDefaultFonts (String fontname, int size)
   {
      setDefaultFonts (fontname, size,
                       new String [] {
                  "Button.font",
                  "ToggleButton.font",
                  "Label.font",
                  "Panel.font",
                  "TitledBorder.font",

                  "List.font",

                  "TableHeader.font",
                  "Table.font",

                  "CheckBox.font",     // OJO !! con "Checkbox.font" (sacado de un arti'culo) NO funciona !!
                  "ComboBox.font",
                  "RadioButton.font",
                  "Menu.font",
                  "MenuBar.font",
                  "MenuItem.font",
                  "PopupMenu.font",

                  "Text.font",
                  "TextArea.font",
                  "TextField.font",
                  "TextPane.font",
                  "EditorPane.font",

                  "ToolBar.font",
                  "ToolTip.font",
                  "ColorChooser.font",
                  "ProgressBar.font",
                  "Tree.font",
                  "OptionPane.font",
                  "ScrollPane.font",
                  "PasswordField.font"
                 }
                 );

      // System.out.println ("UI del checkbox = [" + UIManager.getFont ("CheckBox.font") + "]");
   }

   public static void setDefaultFonts (String fontname, int size, String [] componentNames)
   {
      Font defaultFont = new Font (fontname, Font.PLAIN, size);
      javax.swing.plaf.FontUIResource resorzo = new javax.swing.plaf.FontUIResource ( defaultFont );

      for (int ii = 0; ii < componentNames.length; ii ++)
      {
         String compFont = componentNames[ii] + (componentNames[ii].endsWith (".font") ? "": ".font");
         UIManager.put (componentNames[ii], resorzo);
      }
   }

   /**
      example
         <sysDefaultFonts>
               Arial,   10, 0, *
               Verdana, 11, 3, Button.font, ToggleButton.font
               Verdana, 11, 0, Label.font, ..
               ...
   */
   public static void setDefaultFonts (Eva eva)
   {
      for (int ii = 0; ii < eva.rows (); ii ++)
      {
         if (eva.cols (ii) < 4) continue; // print error ?

         String fname = eva.getValue (ii, 0);
         int size  = stdlib.atoi (eva.getValue (ii, 1));
         int forma = stdlib.atoi (eva.getValue (ii, 2));
         if (size < 0 || forma < 0 || forma > 3) continue; // print error ?

         Font ofont = new Font (fname, forma, size);
         javax.swing.plaf.FontUIResource resorzo = new javax.swing.plaf.FontUIResource (ofont);

         for (int cc = 3; cc < eva.cols (ii); cc ++)
         {
            if (eva.getValue (ii,cc).equals ("*"))
            {
               setDefaultFonts (fname, size);
            }
            else
            {
               String compFont = eva.getValue (ii,cc);
               if (! compFont.endsWith (".font"))
                  compFont += ".font";

               UIManager.put (compFont, resorzo);
            }
         }
      }
   }
}