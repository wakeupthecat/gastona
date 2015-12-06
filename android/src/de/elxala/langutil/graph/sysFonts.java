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

package de.elxala.langutil.graph;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

public class sysFonts
{
   private static logger log ()
   {
      return sysDefaults.log;
   }

   public static int getStandardTextSizeInScaledPixels ()
   {
      return 16;
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
//(o) Android_TODO default fonts
//      Font defaultFont = new Font (fontname, Font.PLAIN, size);
//      javax.swing.plaf.FontUIResource resorzo = new javax.swing.plaf.FontUIResource ( defaultFont );
//
//      for (int ii = 0; ii < componentNames.length; ii ++)
//      {
//         String compFont = componentNames[ii] + (componentNames[ii].endsWith (".font") ? "": ".font");
//         UIManager.put (componentNames[ii], resorzo);
//      }
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
//(o) Android_TODO default fonts
//      for (int ii = 0; ii < eva.rows (); ii ++)
//      {
//         if (eva.cols (ii) < 4) continue; // print error ?
//
//         String fname = eva.getValue (ii, 0);
//         int size  = stdlib.atoi (eva.getValue (ii, 1));
//         int forma = stdlib.atoi (eva.getValue (ii, 2));
//         if (size < 0 || forma < 0 || forma > 3) continue; // print error ?
//
//         Font ofont = new Font (fname, forma, size);
//         javax.swing.plaf.FontUIResource resorzo = new javax.swing.plaf.FontUIResource (ofont);
//
//         for (int cc = 3; cc < eva.cols (ii); cc ++)
//         {
//            if (eva.getValue (ii,cc).equals ("*"))
//            {
//               setDefaultFonts (fname, size);
//            }
//            else
//            {
//               String compFont = eva.getValue (ii,cc);
//               if (! compFont.endsWith (".font"))
//                  compFont += ".font";
//
//               UIManager.put (compFont, resorzo);
//            }
//         }
//      }
   }
}