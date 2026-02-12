/*
package de.elxala.zWidgets
Copyright (C) 2005-2022 Alejandro Xalabarder Aulet

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

package javaj.widgets.gestures;

import javaj.widgets.basics.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import de.elxala.mensaka.*;
import de.elxala.zServices.logger;

public class keyboard
{
   public static final int WIN_KEY = 254;
   public static final int MUM_KEY = 144;
   public static final int CAPSLOCK_KEY = 20;

   public static final int BACK_KEY = 8;
   public static final int ENTER_KEY = 10;
   public static final int SHIFT_KEY = 16;
   public static final int CTRL_KEY = 17;
   public static final int ALT_KEY = 18;

   public static final int PRT_SCREEN = 154; // only release is sent!!
   public static final int ROLLEN = 145;
   public static final int PAUSE = 19;

   public static final int F0  = 111; //
   public static final int F1  = F0+1;
   public static final int F2  = F0+2;
   public static final int F3  = F0+3;
   public static final int F4  = F0+4;
   public static final int F5  = F0+5;
   public static final int F6  = F0+6;
   public static final int F7  = F0+7;
   public static final int F8  = F0+8;
   public static final int F9  = F0+9;
   public static final int F10 = F0+10;
   public static final int F11 = F0+11;
   public static final int F12 = F0+12;

   public static final int ARROW_LEFT  = 37;
   public static final int ARROW_UP    = 38;
   public static final int ARROW_RIGHT = 39;
   public static final int ARROW_DOWN  = 40;

   public static final int ESC    = 27;
   public static final int INS    = 155;
   public static final int DEL    = 127;
   public static final int POS1   = 36;
   public static final int POSEND = 35;

   public static final int PAGE_UP   = 33;
   public static final int PAGE_DOWN = 34;

   public static final int DIGIT_0  = 48;
   public static final int DIGIT_9  = 57;

   protected static KeyListener theOneListener = null;

   public static KeyListener getListener ()
   {
      if (theOneListener == null)
         theOneListener = new kybListener ();
      return theOneListener;
   }
}
