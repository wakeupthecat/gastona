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

import android.view.MotionEvent;

public class uniMotion // extends MotionEvent
{
   public static final int ACTION_CANCEL = MotionEvent.ACTION_CANCEL;
   public static final int FIRST_POINTER_DOWN = MotionEvent.ACTION_DOWN;
   public static final int LAST_POINTER_UP = MotionEvent.ACTION_UP;
   public static final int POINTER_DOWN = MotionEvent.ACTION_POINTER_DOWN;
   public static final int POINTER_UP = MotionEvent.ACTION_POINTER_UP;
   public static final int POINTER_MOVE = MotionEvent.ACTION_MOVE;

   private MotionEvent theEvent;

   public uniMotion (MotionEvent event)
   {
         theEvent = event;
   }

   public int getAction ()
   {
      return theEvent.getAction() & MotionEvent.ACTION_MASK;
   }

   public int getPointerCount ()
   {
      return theEvent.getPointerCount();
   }

   public int getPointerId (int indx)
   {
      return theEvent.getPointerId (indx);
   }

   public final long getEventTime ()
   {
      return theEvent.getEventTime ();
   }

   public float getX (int indx)
   {
      return theEvent.getX (indx);
   }

   public float getY (int indx)
   {
      return theEvent.getY (indx);
   }

   // only in case

   public int getActionFingerIndx ()
   {
      if (getAction () == POINTER_DOWN || getAction () == POINTER_UP)
      {
         // only in these two cases we calculate the finger index "trigger" of the event
         // other events e.g. movement has no finger index "trigger"
         return (theEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
         //if (log.isDebugging (2))
         //   log.dbg (2, "onTouchEvent", "actionFingerIndx = " + actionFingerIndx);
      }
      return -1;
   }
}
