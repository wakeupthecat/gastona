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

import de.elxala.math.space.*;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.graphics.Rect;

/**
 * Detects a multitouch gesture (until 5 fingers!)
 *
 */
public class multiTouchDetector
{
   public static final int MAX_POINTERS = 5;

   public vect3f [] pIni = new vect3f [MAX_POINTERS];
   public vect3f [] pNow = new vect3f [MAX_POINTERS];
   // public vect3f [MAX_POINTERS] pFin = null;

   private int highestPointerNum = 0;
   private boolean inProgress = false;

   /**
    */
   public interface interested
   {
      public boolean onGestureStart    (multiTouchDetector detector);
      public boolean onGestureContinue (multiTouchDetector detector);
      public void    onGestureEnd      (multiTouchDetector detector, boolean cancel);
   }

    /**
     */
   public class Simpleinterested implements interested
   {
      public boolean onGestureStart (multiTouchDetector detector)
      {
         return true;
      }

      public boolean onGestureContinue (multiTouchDetector detector)
      {
         return true;
      }

      public void onGestureEnd (multiTouchDetector detector, boolean cancel)
      {
      }
   }

   private Context mContext;
   private interested myInterested;

   public multiTouchDetector(Context context, interested listener)
   {
      mContext = context;
      myInterested = listener;
   }

   public int getHighestCount ()
   {
      return highestPointerNum;
   }

   public boolean onTouchEvent(MotionEvent event)
   {
      final int action = event.getAction();
      boolean handled = true;

      //android.util.Log.d ("CASCOS", "action = " + action + ", " + event.getPointerCount());

      if (! gestureInProgress ())
      {
         //android.util.Log.d ("CASCOS", "no en marcha");
         // It could start a multitouch gesture

         if (action == MotionEvent.ACTION_DOWN ||
             action == MotionEvent.ACTION_POINTER_1_DOWN ||
             action == MotionEvent.ACTION_POINTER_2_DOWN)
         {
            inProgress = true;
            highestPointerNum = Math.min (MAX_POINTERS, event.getPointerCount());
            for (int ii = 0; ii < highestPointerNum; ii ++)
            {
               pIni[ii] = new vect3f (event.getX(ii), event.getY(ii));
               pNow[ii] = null;
            }

            if (! myInterested.onGestureStart(this))
            {
               inProgress = false;
               highestPointerNum = 0;
            }
            return true;
         }
         return false;
      }

      //android.util.Log.d ("CASCOS", "SEGUIM action = " + action + ", " + event.getPointerCount());
      switch (action)
      {
         case MotionEvent.ACTION_UP:
         case MotionEvent.ACTION_POINTER_1_UP:
         case MotionEvent.ACTION_POINTER_2_UP:
            //android.util.Log.d ("CASCOS", "UPPPD");
            finalizeGesture (false);
            break;

         case MotionEvent.ACTION_CANCEL:
            finalizeGesture (true);
            break;

         case MotionEvent.ACTION_MOVE:
            //android.util.Log.d ("CASCOS", "MOVE");

            highestPointerNum = Math.max (highestPointerNum, event.getPointerCount());
            highestPointerNum = Math.min (MAX_POINTERS, highestPointerNum);
            for (int ii = 0; ii < event.getPointerCount(); ii ++)
            {
               if (ii < MAX_POINTERS)
               {
                  pNow[ii] = new vect3f (event.getX(ii), event.getY(ii));
                  if (pIni[ii] == null)
                     // reincorporacion! no estaba pero ahora sí
                     pIni[ii] = new vect3f (pNow[ii]);
               }
            }

            if (! myInterested.onGestureContinue(this))
               inProgress = false;
            break;

         default:
            return false; // not handled!
      }

      return true;
   }

   protected void finalizeGesture (boolean dueCancel)
   {
      // here still gestureInProgress and gestureFinalized gives true
      myInterested.onGestureEnd (this, dueCancel);
      inProgress = false;
   }


    /**
     */
    public boolean gestureInProgress ()
    {
      return inProgress;
    }

    public boolean gestureFinalized ()
    {
      return !inProgress;
    }
}
