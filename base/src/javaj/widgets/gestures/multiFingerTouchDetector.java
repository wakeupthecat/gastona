/*
package javaj.widgets
Copyright (C) 2011-2022 Alejandro Xalabarder Aulet

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

//import android.content.Context;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.graphics.Rect;

import de.elxala.zServices.*;

/**
 * Detects a multitouch gesture (until 5 fingers!)
 *
 */
public class multiFingerTouchDetector
{
   private static logger log = new logger (null, "javaj.widgets.gestures.multiFingerTouchDetector", null);

   public static final int MAX_POINTERS = 10;
   public static final int INVALID = -1;

   public fingerTouch [] mFingers = new fingerTouch [MAX_POINTERS];

   private int highestPointerIndx = INVALID;
   private int activePointerCount = INVALID;
   private boolean inProgress = false;

   /**
    */
   public interface interested
   {
      public void onFingerDown    (multiFingerTouchDetector detector, int fingerIndx);
      public void onFingerUp      (multiFingerTouchDetector detector, int fingerIndx);
      public void onMovement      (multiFingerTouchDetector detector);
      public void onGestureEnd    (multiFingerTouchDetector detector, boolean cancel);
   }

    /**
     */
   public class Simpleinterested implements interested
   {
      public void onFingerDown (multiFingerTouchDetector detector, int fingerIndx)
      {
      }

      public void onFingerUp (multiFingerTouchDetector detector, int fingerIndx)
      {
      }

      public void onMovement (multiFingerTouchDetector detector)
      {
      }

      public void onGestureEnd (multiFingerTouchDetector detector, boolean cancel)
      {
      }
   }

   //private Context mContext;
   private interested myInterested;

//   public multiFingerTouchDetector(Context context, interested listener)
//   {
//      mContext = context;
//      myInterested = listener;
//      reset ();
//   }
   public multiFingerTouchDetector(interested listener)
   {
      //mContext = context;
      myInterested = listener != null ? listener: new Simpleinterested ();
      reset ();
   }

   public void reset ()
   {
      highestPointerIndx = INVALID;
      activePointerCount = INVALID;
      inProgress = false;
      for (int ff = 0; ff < mFingers.length; ff++)
         mFingers[ff] = new fingerTouch ();
   }


   public fingerTouch getFinger (int indx)
   {
      if (indx < 0 || indx >= mFingers.length) return null;
      return mFingers[indx];
   }

   public int getMaxFingers ()
   {
      return mFingers.length;
   }

   public int getHighestFingerIndex ()
   {
      if (highestPointerIndx != INVALID)
         return highestPointerIndx; // cached value

      for (int ii = mFingers.length-1; ii >= 0; ii --)
         if (mFingers[ii].isPressing ())
         {
            highestPointerIndx = ii;
            return ii;
         }
      highestPointerIndx = INVALID;
      return highestPointerIndx;
   }

   public int getActiveFingersCount ()
   {
      if (activePointerCount != INVALID)
         return activePointerCount; // cached value

      activePointerCount = 0;
      for (int ii = 0; ii < mFingers.length; ii ++)
         activePointerCount += mFingers[ii].isPressing () ? 1: 0;
      return activePointerCount;
   }

   public boolean onTouchEvent(uniMotion event)
   {
      boolean consumed  = true;
      int action         = event.getAction();
      //int actionFingerId = -1;
      //int actionFingerIndx4Id = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
      int actionFingerIndx = -1;

      highestPointerIndx = INVALID;
      activePointerCount = INVALID;


      // invalidate actionFingerId since it has no meaning if no action_pointer occurs
      // if (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP)
      //   actionFingerId = event.getPointerId (actionFingerIndx4Id);

      int npointers = event.getPointerCount();

      if (log.isDebugging (2))
         log.dbg (2, "onTouchEvent", "action = " + action + " npointers = " + npointers);

      switch (action)
      {
         case uniMotion.ACTION_CANCEL:
            for (int ii = 0; ii < mFingers.length; ii ++)
               mFingers[ii].reset (0);
            break;

         case uniMotion.POINTER_DOWN:
         case uniMotion.POINTER_UP:
            actionFingerIndx = event.getActionFingerIndx ();
            if (log.isDebugging (2))
               log.dbg (2, "onTouchEvent", "actionFingerIndx = " + actionFingerIndx);
            // NO BREAK!

         case uniMotion.FIRST_POINTER_DOWN:
         case uniMotion.LAST_POINTER_UP:
         case uniMotion.POINTER_MOVE:
            for (int ii = 0; ii < event.getPointerCount(); ii ++)
            {
               int indx = event.getPointerId (ii);

               // in case Pointer_down or Pointer_up => transmit only it to the affected pointer
               // and for the rest just a movement
               int reAction = action;
               if (actionFingerIndx != -1 && indx != actionFingerIndx)
               {
                  // in this case for this finger is just a movement
                  reAction = uniMotion.POINTER_MOVE;
               }
               if (indx < MAX_POINTERS)
               {
                  inProgress = true;
                  if (log.isDebugging (2))
                     log.dbg (2, "onTouchEvent", "fingerIndx " + indx + " setActionAndPos (" + reAction + " x,y " + event.getX(ii) + ", " + event.getY(ii) + " timeMs " + event.getEventTime() + ")");
                  mFingers[indx].setActionAndPos (reAction, event.getX(ii), event.getY(ii), event.getEventTime());
               }
               else
               {
                  log.warn ("onTouchEvent", "cannot take into account finger #" + indx + ", (is it really a finger ?)");
               }
            }
            break;

         default:
            break;
      }

      switch (action)
      {
         case uniMotion.ACTION_CANCEL:
            finalizeGesture (true);
            break;

         case uniMotion.FIRST_POINTER_DOWN:
            myInterested.onFingerDown (this, event.getPointerId (0));
            break;

         case uniMotion.LAST_POINTER_UP:
            myInterested.onFingerUp (this, event.getPointerId (0));
            break;

         case uniMotion.POINTER_DOWN:
            myInterested.onFingerDown (this, event.getPointerId (actionFingerIndx));
            break;

         case uniMotion.POINTER_UP:
            myInterested.onFingerUp (this, event.getPointerId (actionFingerIndx));
            break;

         case uniMotion.POINTER_MOVE:
            myInterested.onMovement (this);
            break;

         default:
            consumed = false;
            break;
      }

      return consumed;
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
