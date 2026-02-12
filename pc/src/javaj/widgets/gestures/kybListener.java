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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import de.elxala.mensaka.*;
// import de.elxala.zServices.logger;

public class kybListener implements KeyListener
{
   // protected MessageHandle MSGH_keyType    = new MessageHandle ("javaj.gestures.keyboardListener", "javaj keytype");
   protected static MessageHandle MSGH_keyPress   = new MessageHandle ("javaj.gestures.keyboardListener", "javaj keypress");
   protected static MessageHandle MSGH_keyRelease = new MessageHandle ("javaj.gestures.keyboardListener", "javaj keyrelease");
   
   protected static boolean avoidReenter = false;

   public static void mskKeyEvent (boolean isPress, KeyEvent e)
   {
      Mensaka.sendPacket (isPress ? MSGH_keyPress: MSGH_keyRelease,
                          null,
                          new String [] { "" + e.getKeyCode(), KeyEvent.getKeyText(e.getKeyCode()) }
                         );
   }

   @Override
   public void keyTyped (KeyEvent e)
   {
      // ?? no keyCode ?
   }

   @Override
   public void keyPressed(KeyEvent e)
   {
      //if (avoidReenter) return;
      //avoidReenter = true;
      mskKeyEvent (true, e);
      //avoidReenter = false;
   }

   @Override
   public void keyReleased(KeyEvent e)
   {
      //if (avoidReenter) return;
      //avoidReenter = true;
      mskKeyEvent (false, e);
      //avoidReenter = false;
   }
}
