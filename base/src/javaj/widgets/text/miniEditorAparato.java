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

package javaj.widgets.text;

import de.elxala.mensaka.*;


/**
*/
public class miniEditorAparato extends textAparato
{
   public static final int RX_COMMNAD_UNDO  = RX_MIN_CUSTOM_MAP - 4;
   public static final int RX_COMMNAD_REDO  = RX_MIN_CUSTOM_MAP - 5;
   public static final int RX_COMMNAD_INSERTTEXT = RX_MIN_CUSTOM_MAP - 6;

   public miniEditorAparato (MensakaTarget objWidget, miniEditorEBS pDataAndControl)
   {
      super (objWidget, pDataAndControl);

      Mensaka.suscribe (objWidget, RX_COMMNAD_UNDO,  ebsMiniEditor().evaName (ebsMiniEditor().sMSG_UNDO));
      Mensaka.suscribe (objWidget, RX_COMMNAD_REDO,  ebsMiniEditor().evaName (ebsMiniEditor().sMSG_REDO));
      Mensaka.suscribe (objWidget, RX_COMMNAD_INSERTTEXT,  ebsMiniEditor().evaName (ebsMiniEditor().sMSG_INSERT_TEXT));
   }

   public miniEditorEBS ebsMiniEditor ()
   {
      return (miniEditorEBS) pEBS;
   }
}


////
////      <command>  comando    (where comando : load, save, clear, undo, redo)
////      <filename> file name
//
//   /*
//      <color>
//            # of color
//            name, name, ...
//            back R, back G, back B, fore R, fore G, fore B
//            back R, back G, back B, fore R, fore G, fore B
//            ..
//   */
//   public boolean getColors (Color [] back_fore)
//   {
//      Eva colors = getEUControl ().getEva ("color");
//      int indxColor = stdlib.atoi (colors.getValue ());
//
//      if (indxColor > colors.rows () - 2)
//      {
//         // default colors
//         back_fore [0] = DEFAULT_BACK_COLOR;
//         back_fore [1] = DEFAULT_FORE_COLOR;
//         return false;
//      }
//
//      int nCol = stdlib.atoi (colors.getValue ());
//
//      back_fore [0] = new Color (
//                       stdlib.atoi (colors.getValue (2+nCol, 0)),
//                       stdlib.atoi (colors.getValue (2+nCol, 1)),
//                       stdlib.atoi (colors.getValue (2+nCol, 2)));
//      back_fore [1] = new Color (
//                       stdlib.atoi (colors.getValue (2+nCol, 3)),
//                       stdlib.atoi (colors.getValue (2+nCol, 4)),
//                       stdlib.atoi (colors.getValue (2+nCol, 5)));
//
//      return true;
//   }
//
//   public int countColors ()
//   {
//      int ncolors = getEUControl ().getEva ("color").rows () - 2;
//      return (ncolors > 0) ? ncolors: 0;
//   }
//
//
//   public Font getFont ()
//   {
//      Eva fontN = getEUControl ().getEva ("fontName");
//      if (fontN != null)
//      {
//         int    fontIndx = stdlib.atoi (fontN.getValue ());
//         String fontName = fontN.getValue (1, fontIndx);
//         int    fontSize = getFontSize ();
//
//         if (fontName.length () > 0)
//            return new Font (fontName, Font.PLAIN, (fontSize == 0) ? 10: fontSize);
//      }
//      return DEFAULT_FONT;
//   }
//
//   /*
//      <fontName>
//            # of font name
//            name, name, name
//   */
//   public int getFontSize ()
//   {
//      int size = stdlib.atoi (getEUControl ().getValue ("fontSize"));
//      return (size == 0) ? 10: size;
//   }
//
//   public int getTabSize ()
//   {
//      int size = stdlib.atoi (getEUControl ().getValue ("tabLength"));
//      return (size == 0) ? 3: size;
//   }
//
