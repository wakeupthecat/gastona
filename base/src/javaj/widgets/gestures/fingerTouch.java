/*
package javaj.widgets
Copyright (C) 2011-2016 Alejandro Xalabarder Aulet

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

import de.elxala.zServices.*;

/**
   All information related with a finger touch

   two fingers semantic

   displacement / translation

        v1 - v2 ~ 0

        o--->.......o--->
        <---o.......<---o

        o.........o
        |         |
        v         v

   zoom

        v1 + v2 ~ 0
        v1 x v2 ~ 0
        v1 . p ~ - v2 . p =  |1|

            o.......o--->
        <---o.......o--->
        o--->.......<---o

   rotation

        v1 + v2 ~ 0
        v1 x v2 ~ 0
        v1 x p ~ - v2 x p =  |1|

        ^
        |
        o.........o
                  |
                  v


        o.........o
                  |
                  v
 */
public class fingerTouch
{
   private static logger log = new logger (null, "javaj.widgets.gestures.fingerTouch", null);

   public vect3f pIni = null;
   public vect3f pNow = null;
   public vect3f pFin = null;
   protected vect3f pConverted = new vect3f ();

   private vect3f prevTouchPos = null;

   private long timeDown = 0;
   private long timeLastMovement = 0;
   private long timeLastObserved = 0;
   private long timeUp = 0;
   private long nMoves = 0;
   //private float lastDeltaMilli = 0f;

   private float velX = 0f;
   private float velY = 0f;
   private float accelX = 0f;
   private float accelY = 0f;

   public float getSpeedX () { return velX; }
   public float getSpeedY () { return velY; }
   public float getSpeed ()  { return (float) Math.sqrt (velX*velX + velY*velY); }

   public float getAccelerationX () { return accelX; }
   public float getAccelerationY () { return accelY; }
   public float getAcceleration ()  { return (float) Math.sqrt (accelX*accelX + accelY*accelY); }

   private boolean isDoubleTap = false;
   private boolean isFastSecondTap = false;

   private Object attachedObject = null;


   //public void reset (long milli, boolean ss)
   public void reset (long milli)
   {
      isDoubleTap = isFastSecondTap = false;

      pIni = pNow = pFin = null;
      attachedObject = null;
      if (milli == 0)
      {
         //after down and during movement this variable has to be preserved
         //System.out.println ("CANCELING onFingerZoom!");
         prevTouchPos = null;
      }

      timeDown = timeLastMovement = timeLastObserved = milli;
      timeUp = nMoves = 0;
      velX = velY = accelX = accelY = 0f;
   }

   public long getMovesCount ()
   {
      return nMoves;
   }

   public vect3f getLastDisplacementVector ()
   {
      return  (pIni == null || pNow == null) ?
               new vect3f (0.f, 0.f):
               new vect3f (pNow.x - pIni.x, pNow.y - pIni.y);
   }

   public int getDx ()
   {
      return (pIni == null || pNow == null) ? 0: (int) pNow.x - (int) pIni.x;
   }

   public int getDy ()
   {
      return (pIni == null || pNow == null) ? 0: (int) pNow.y - (int) pIni.y;
   }

   public vect3f getLastPosition ()
   {
      return pFin != null ? pFin:
             pNow != null ? pNow:
             pIni;
   }

   public vect3f getLastPosition (float scaleX, float scaleY, float offsetX, float offsetY)
   {
      // pConverted to avoid "new vect3f"
      pConverted.x = getLastPosition ().x;
      pConverted.y = getLastPosition ().y;

      pConverted.x /= scaleX;
      pConverted.y /= scaleY;
      pConverted.x += offsetX;
      pConverted.y += offsetY;
      
      // System.out.println ("");
      // System.out.println ("scale   (" + scaleX + ", " + scaleY + ")");
      // System.out.println ("onset   (" + offsetX + ", " + offsetY + ")");
      // System.out.println ("pos raw (" + getLastPosition ().x + ", " + getLastPosition ().y + ")");
      // System.out.println ("pos con (" + pConverted.x + ", " + pConverted.y + ")");
      
      return pConverted;
   }

   public boolean isIdle ()
   {
      return pIni == null;
   }

   public boolean isPressing ()
   {
      return pIni != null && pFin == null;
   }

   public boolean isFinished ()
   {
      return pIni != null && pFin != null;
   }

   public Object getAttachedObject ()
   {
      return attachedObject;
   }

   public void setAttachedObject (Object obj)
   {
      attachedObject = obj;
   }

   public boolean canAttachedObject (Object obj)
   {
      if (attachedObject != null) return false;
      attachedObject = obj;
      return true;
   }

   protected void calcDynamic (vect3f oldPos, float x, float y, long timeMilli)
   {
      if (oldPos != null)
      {
         float oldVelx = velX;
         float oldVely = velY;
         float lastDeltaMilli = (float)  (timeMilli - timeLastObserved);
         velX = (x - oldPos.x) / lastDeltaMilli;
         velY = (y - oldPos.y) / lastDeltaMilli;
         accelX = (velX - oldVelx) / lastDeltaMilli;
         accelY = (velY - oldVely) / lastDeltaMilli;
      }

      if (velX != 0f || velY != 0f)
         timeLastMovement = timeMilli;

      timeLastObserved = timeMilli;
   }

   public int MILLI_THRESHOLD_SHORT_TOUCH = 300;      // not yet adjusted (03.12.2011 23:12)
   public int MILLI_THRESHOLD_DOUBLE_TOUCH = 300;     // not yet adjusted (03.12.2011 23:12)
   public int PIXEL_THRESHOLD_SECOND_TOUCH = 20;      // not yet adjusted (03.12.2011 23:12)

   /*
      Call this function before calling setActionAndPos with the touch
      The function returns true if the new touch at the time milliNow
      has been made just after another short one.
   */
   private boolean havePreviousShortTouch (long milliNow)
   {
      return ((timeUp - timeDown) <= MILLI_THRESHOLD_SHORT_TOUCH) &&
             ((milliNow - timeUp) <= MILLI_THRESHOLD_DOUBLE_TOUCH);
   }

   /*
      Call this function before calling setActionAndPos with the touch
      The function returns true if the new touch given by newPos has been
      made at the same position as the last one, independently of when the last touch
      was made.
   */
   private boolean secondTouchAtSamePosition (vect3f newPos)
   {
      return vect3f.inRadius (newPos, pFin, PIXEL_THRESHOLD_SECOND_TOUCH);
   }

   public boolean doubleTap ()
   {
      return isDoubleTap;
   }

   public boolean oneFingerZoomJustInitiated ()
   {
      return isFastSecondTap && !isDoubleTap;
   }

   public boolean oneFingerZoomActive ()
   {
      return prevTouchPos != null;
   }

   // might only be valid if oneFingerZoomInitiated returns true
   public vect3f oneFingerZoomFirstPosition ()
   {
      return prevTouchPos;
   }

   private void setInitialPos (float x, float y, long millitime)
   {
      vect3f pos = new vect3f (x,y);
      //System.out.print ("pos x,y " + x + ", " + y);

      // double tap and "one finger zoom" detection
      isFastSecondTap = havePreviousShortTouch (millitime);
      //System.out.print ("down " + timeDown + " up " + timeUp + " now " + millitime + " is fast 2 tap " + isFastSecondTap);
      isDoubleTap = isFastSecondTap && secondTouchAtSamePosition (pos);
      //System.out.println (" isDoubleTap " + isDoubleTap);
      prevTouchPos = oneFingerZoomJustInitiated () ? pFin: null;
      //System.out.println ("oneFingerZoomJustInitiated " + oneFingerZoomJustInitiated () + " previous touch is " + prevTouchPos);
      //System.out.println ("oneFingerZoomActive () " + oneFingerZoomActive ());

      // procede with the current touch
      reset (millitime);
      pIni = pos;
      attachedObject = null;
   }

   public void setActionAndPos (int action, float x, float y, long millitime)
   {
      isFastSecondTap = isDoubleTap = false;
      switch (action)
      {
         case uniMotion.FIRST_POINTER_DOWN:
         case uniMotion.POINTER_DOWN:
            setInitialPos (x, y, millitime);
            break;

         case uniMotion.POINTER_MOVE:
            nMoves ++;
            if (pIni == null)
            {
               //log.warn ("setActionAndPos", "CHAPUX en puntero!");
               setInitialPos (x, y, millitime);
            }

            if (pNow == null)
            {
               calcDynamic (pIni, x, y, millitime);
               pNow = new vect3f(x, y);
            }
            else
            {
               calcDynamic (pNow, x, y, millitime);
               pNow.set (x, y, 0f);
            }
            pFin = null;
            break;

         case uniMotion.LAST_POINTER_UP:
         case uniMotion.POINTER_UP:
            calcDynamic (pNow, x, y, millitime);
            pFin = new vect3f(x, y);
            timeUp = timeLastMovement;
            break;

         case uniMotion.ACTION_CANCEL:
            reset (0);
            break;

         default:
            break;
      }
   }
}
