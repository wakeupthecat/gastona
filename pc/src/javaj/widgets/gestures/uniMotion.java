/*
package javaj.widgets
Copyright (C) 2011 Alejandro Xalabarder Aulet

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

import java.awt.event.MouseEvent;


public class uniMotion
{
   public static final int ACTION_CANCEL = 0;
   public static final int FIRST_POINTER_DOWN = 1;
   public static final int LAST_POINTER_UP = 2;
   public static final int POINTER_DOWN = 3;
   public static final int POINTER_UP = 4;
   public static final int POINTER_MOVE = 5;

   private MouseEvent theEvent;
   private int theAction = ACTION_CANCEL;

   public uniMotion (MouseEvent event, int action)
   {
      theEvent = event;
      theAction = action;
   }

   public int getAction ()
   {
      return theAction;
   }

   public int getPointerCount ()
   {
      return 1;
   }

   public int getPointerId (int indx)
   {
      return 0;
   }

   public final long getEventTime ()
   {
      return theEvent.getWhen();
   }

   public float getX (int indx)
   {
      return theEvent.getX ();
   }

   public float getY (int indx)
   {
      return theEvent.getY ();
   }

   public int getActionFingerIndx ()
   {
      return 0;
   }
}
