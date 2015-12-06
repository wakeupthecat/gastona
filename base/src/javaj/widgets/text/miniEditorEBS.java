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

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import java.awt.*;      // Color, Font

/**
   @author Alejandro Xalabarder Aulet
   @date 30.01.2005 16:04

   proxy for handle easily a zMiniEdit widget EBS model and EBS control

   EBS DATA MODEL:

      <fText>          C:\temp\fichero.txt

   EBS CONTROL:

      <fText activeColor>  vivo
      <fText colors>
            "name",  "backRGB", "foreRGB"

            std   ,  240240240, 010010010
            vivo  ,  240240240, 010010010
            raro  ,  240240240, 010010010

      <fText activeFont>   escancia
      <fText fonts>
            "name"     ,   bold, "size"

            Courier New,    0,    14
            Tahoma     ,    0,    16

      <fText tabulator>  5
*/
public class miniEditorEBS extends textEBS
{
   public static final String sMSG_UNDO  = "undo";
   public static final String sMSG_REDO  = "redo";
   public static final String sMSG_INSERT_TEXT  = "insert";

   public static final String sATTR_COLORS        = "colors";
   public static final String sATTR_ACTIVE_COLOR  = "activeColor";
   public static final String sATTR_FONTS         = "fonts";
   public static final String sATTR_ACTIVE_FONT   = "activeFont";

   public static final String sATTR_TEXT_TO_INSERT    = "textToInsert";

   private static final Color DEFAULT_BACK_COLOR = new Color (240,240,240);    // dark red
   private static final Color DEFAULT_FORE_COLOR = new Color (10, 10, 10);     // light gray
   private static final Font  DEFAULT_FONT  = new Font("Courier New", Font.PLAIN, 14);

   public miniEditorEBS (String nameWidget, EvaUnit pData, EvaUnit pControl)
   {
      super (nameWidget, pData, pControl);
   }

   public void setInsertText (String [] text)
   {
      Eva evaText = getAttribute (CONTROL, true, sATTR_TEXT_TO_INSERT);

      evaText.clear ();
      for (int ii = 0; ii < text.length; ii ++)
      {
         evaText.addRow (text[ii]);
      }
   }

   public String getInsertText ()
   {
      return getAttribute (CONTROL, true, sATTR_TEXT_TO_INSERT).getAsText ();
   }

   public Eva getTableColors ()
   {
      return getAttribute (CONTROL, true, sATTR_COLORS);
   }

   public void setTableColors (Eva colorsTable)
   {
      Eva ee = getAttribute (CONTROL, true, sATTR_COLORS);
      ee.clear ();

      //copy the eva
      for (int ii = 0; ii < colorsTable.rows (); ii ++)
         ee.addLine (new EvaLine (colorsTable.getStrArray (ii)));
   }

   public Eva getTableFonts ()
   {
      return getAttribute (CONTROL, true, sATTR_FONTS);
   }

   public void setTableFonts (Eva fontsTable)
   {
      Eva ee = getAttribute (CONTROL, true, sATTR_FONTS);
      ee.clear ();

      //copy the eva
      for (int ii = 0; ii < fontsTable.rows (); ii ++)
         ee.addLine (new EvaLine (fontsTable.getStrArray (ii)));
   }

   public void setText (String text)
   {
      log.severe ("miniEditorEBS.setText", "Cannot setText in this widget! (" + evaName("") + ")");
   }

   public String getText ()
   {
      log.severe ("miniEditorEBS.getText", "Cannot getText from this widget! (" + evaName("") + ")");
      return "";
   }
}
