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

package javaj.widgets.panels;

import java.awt.Dimension;

import de.elxala.langutil.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;

/**
   //(o) javaj_widgets_EBS frameEBS
*/
public class frameEBS extends widgetEBS
{
   public static final String sATTR_TITLE = "title";
   public static final String sATTR_MAXIMIZED = "maximized"; //(o) TODO_widgets zFrame maximized to be implemented in zFrame

   public static final String sMSG_SHOW  = "show";
   public static final String sMSG_HIDE  = "hide";
   public static final String sMSG_TOGGLE_VISIBLE = "toggleVisible";
   public static final String sMSG_TELL_POSANDSIZE = "tellPosAndSize";

   public static final String sATTR_POSX  = "posX";
   public static final String sATTR_POSY  = "posY";
   public static final String sATTR_SIZEX  = "sizeX";
   public static final String sATTR_SIZEY  = "sizeY";

   // screen size to check position values!
   private static Dimension screeSize =  java.awt.Toolkit.getDefaultToolkit().getScreenSize();

   public frameEBS (String nameWidget, EvaUnit pData, EvaUnit pControl)
   {
      super (nameWidget, pData, pControl);
   }

   public boolean isMaximized ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, sATTR_MAXIMIZED, "0" /* default false! */));
   }

   public void setMaximized (boolean on)
   {
      setSimpleAttribute (CONTROL, sATTR_MAXIMIZED, (on ? "1" : "0"));
   }

   public boolean getPosX (int [] posX)
   {
      Eva pos = getAttribute (CONTROL, sATTR_POSX);
      if (pos == null)
         return false;

      posX[0] = stdlib.atoi (pos.getValue ());
      return true;
   }

   public boolean getPosY (int [] posY)
   {
      Eva pos = getAttribute (CONTROL, sATTR_POSY);
      if (pos == null)
         return false;

      posY[0] = stdlib.atoi (pos.getValue ());
      return true;
   }

   public boolean getSizeX (int [] sizeX)
   {
      Eva size = getAttribute (CONTROL, sATTR_SIZEX);
      if (size == null)
         return false;

      sizeX[0] = stdlib.atoi (size.getValue ());
      return true;
   }

   public boolean getSizeY (int [] sizeY)
   {
      Eva size = getAttribute (CONTROL, sATTR_SIZEY);
      if (size == null)
         return false;

      sizeY[0] = stdlib.atoi (size.getValue ());
      return true;
   }

   public void setPos (int x, int y)
   {
      setSimpleAttribute (CONTROL, sATTR_POSX, "" + x);
      setSimpleAttribute (CONTROL, sATTR_POSY, "" + y);
   }

   public void setSize (int dx, int dy)
   {
      setSimpleAttribute (CONTROL, sATTR_SIZEX, "" + dx);
      setSimpleAttribute (CONTROL, sATTR_SIZEY, "" + dy);
   }
}
