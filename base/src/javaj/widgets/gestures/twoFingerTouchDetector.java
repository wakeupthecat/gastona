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
import javaj.widgets.graphics.*;

import de.elxala.zServices.*;

/**
   Detects the typical two finger plausible action like : zoom, translation, rotation
   among several strategies we provide now values for all three actions
   

*/
public class twoFingerTouchDetector // extends multiFingerTouchDetector
{
   private static logger log = new logger (null, "javaj.widgets.gestures.twoFingerTouchDetector", null);
   private multiFingerTouchDetector pulp = new multiFingerTouchDetector (null);

   public uniRect rectP1 = null;
   public uniRect rectP2 = null;
   public vect3f dispVfing1 = null;
   public vect3f dispVfing2 = null;

   /**
    */
   public interface interested
   {
      public boolean onGestureStart    (twoFingerTouchDetector detector);
      public boolean onGestureContinue (twoFingerTouchDetector detector);
      public void    onGestureEnd      (twoFingerTouchDetector detector, boolean cancel);
   }

    /**
     */
   public class Simpleinterested implements interested
   {
      public boolean onGestureStart (twoFingerTouchDetector detector)
      {
         return true;
      }

      public boolean onGestureContinue (twoFingerTouchDetector detector)
      {
         return true;
      }

      public void onGestureEnd (twoFingerTouchDetector detector, boolean cancel)
      {
      }
   }

   //private Context mContext;
   private interested myInterested;

   //public twoFingerTouchDetector(Context context, interested listener)
   public twoFingerTouchDetector(interested listener)
   {
      //mContext = context;
      myInterested = listener != null ? listener: new Simpleinterested ();
   }

   private uniRect makeRect (vect3f p1, vect3f p2)
   {
      if (p1 == null || p2 == null)
         return null;

      return
      new uniRect
                ((int) Math.min (p1.x, p2.x), // left
                (int) Math.max (p1.y, p2.y), // top
                (int) Math.max (p1.x, p2.x), // right
                (int) Math.min (p1.y, p2.y)  // bottom
                );
   }

   protected void setRectangles ()
   {
      rectP1 = rectP2 = null;
      dispVfing1 = dispVfing2 = null;

      fingerTouch dedo1 = pulp.mFingers[0];
      fingerTouch dedo2 = pulp.mFingers[1];

      if (dedo1 == null || (!dedo1.oneFingerZoomActive () && dedo2 == null)) return;

      vect3f p1_ini = dedo1.oneFingerZoomActive () ? dedo1.oneFingerZoomFirstPosition () : dedo1.pIni;
      vect3f p2_ini = dedo1.oneFingerZoomActive () ? dedo1.pIni: dedo2.pIni;
      dispVfing1 = new vect3f (p1_ini, p2_ini);

      vect3f p1_now = dedo1.oneFingerZoomActive () ? dedo1.oneFingerZoomFirstPosition () : dedo1.getLastPosition ();
      vect3f p2_now = dedo1.oneFingerZoomActive () ? dedo1.getLastPosition () : dedo2.getLastPosition ();
      dispVfing2 = new vect3f (p1_now, p2_now);
      
      rectP1 = makeRect (p1_ini, p2_ini);
      rectP2 = makeRect (p1_now, p2_now);
   }

   protected vect3f plausibleDisplacement ()
   {
      // only if "dispv1 - dispv2 ~ 0" then it is a displacement and not
      // zoom or rotation and the displazament vector is dispv1 (same as dispv2)
      // 
      return 
         (dispVfing1.distance2 (dispVfing2) < 0.01 && dispVfing1.norm2 () >= 0.01f) ?
         dispVfing1:
         null;
   }

   protected boolean plausibleZoom ()
   {
      // 2016.01.01
      // old function ... to be reviewed !
      return  (rectP1 != null && rectP2 != null) &&
              rectP2.right () != rectP2.left () &&
              rectP2.top () != rectP2.bottom () &&
              rectP1.right () != rectP1.left () &&
              rectP1.top () != rectP1.bottom ();
   }

   private boolean yAxisUp = false; // usually in 2D paints y grows downwards
   private int marcX = 0, marcY = 0;
   private float refOffX = 0.f, refOffY = 0.f, refScaleX = 1.f, refScaleY = 1.f;

//   public void setRefOffsetScale(int marcoX, int marcoY, float offsetX, float offsetY, float scaleX, float scaleY)
//   {
//      setRefOffsetScale(marcoX, marcoY, offsetX, offsetY, scaleX, scaleY, false);
//   }

   public void setRefOffsetScale(int marcoX, int marcoY, float offsetX, float offsetY, float scaleX, float scaleY, boolean yGrowsUp)
   {
      log.dbg (2, "setRefOffsetScale", "ostima     scale X, Y = " + scaleX + ", " + scaleY);
      yAxisUp = yGrowsUp;
      marcX = marcoX;
      marcY = marcoY;
      refOffX = offsetX;
      refOffY = offsetY;
      refScaleX = scaleX;
      refScaleY = scaleY;
   }

   public float nowOffsetX = 0.f;
   public float nowOffsetY = 0.f;
   public float nowScaleX = 1.f;
   public float nowScaleY = 1.f;

   public void calcZoomNow (boolean square)
   {
      log.dbg (2, "calcZoomNow", "marc      X, Y = " + marcX + ", " + marcY);
      log.dbg (2, "calcZoomNow", "refOff    X, Y = " + refOffX + ", " + refOffY);
      log.dbg (2, "calcZoomNow", "refScale  X, Y = " + refScaleX + ", " + refScaleY);

      // add 1 pixel to avoid raise conditions (factor 0 or infinite)
      float facZoomX = (Math.abs (rectP2.width ()) + 1.f) / (Math.abs (rectP1.width ()) + 1.f);
      float facZoomY = (Math.abs (rectP2.height ()) + 1.f) / (Math.abs (rectP1.height ()) + 1.f);

      //System.out.println ("facZoom X (" + facZoomX + " Y " + facZoomY );

      // keep factors between 0.05 and 20
      facZoomX = Math.min (Math.max (facZoomX, 0.15f), 6.f);
      facZoomY = Math.min (Math.max (facZoomY, 0.15f), 6.f);

      log.dbg (2, "calcZoomNow", "facZoom X, Y " + facZoomX + ", " + facZoomY);

      if (square)
      {
         facZoomX = Math.max (facZoomX, facZoomY);
         facZoomY = facZoomX;
      }

      // center in pixels of rectangle ref
      int cx = (int) rectP1.centerX () - marcX;
      int cy = (int) rectP1.centerY () - marcY;

      log.dbg (2, "calcZoomNow", "calcZoomNow   cx cy " + cx + ", " + cy);

      nowOffsetX = refOffX + cx * ( 1.f - 1.f / facZoomX) / refScaleX;
      if (yAxisUp)
           nowOffsetY = refOffY - cy * ( 1.f - 1.f / facZoomY) / refScaleY;
      else nowOffsetY = refOffY + cy * ( 1.f - 1.f / facZoomY) / refScaleY;

      nowScaleX = refScaleX * facZoomX;
      nowScaleY = refScaleY * facZoomY;

      log.dbg (2, "calcZoomNow", "calcZoomNow   nowOffsetX, Y " + nowOffsetX + ", " + nowOffsetY);
      log.dbg (2, "calcZoomNow", "calcZoomNow   nowScaleX , Y " + nowScaleX + ", " + nowScaleY);
   }


   public boolean onTouchEvent(uniMotion event)
   {
      int prevDedos = pulp.getActiveFingersCount ();

      pulp.onTouchEvent(event);

      int nowDedos = pulp.getActiveFingersCount ();

      log.dbg (2, "onTouchEvent", "prev-dedos " + prevDedos + " post dedos " + nowDedos + " / " + " oneFingerZoomActive " + pulp.mFingers[0].oneFingerZoomActive ());

      if (prevDedos == 0)
      {
         if (nowDedos > 0)
            myInterested.onGestureStart(this);
         else
            return false; // nothing has happened (0 0)
      }
      else  // prevDedos > 0
      {
         if (nowDedos == 2 || (nowDedos == 1 && pulp.mFingers[0].oneFingerZoomActive ()))
         {
            setRectangles ();
            myInterested.onGestureContinue(this);
         }

         if (nowDedos == 0)
            finalizeGesture (false);
      }

      return true;
   }

   protected void finalizeGesture (boolean dueCancel)
   {
      pulp.finalizeGesture (dueCancel);
      // here still gestureInProgress and gestureFinalized gives true
      myInterested.onGestureEnd (this, dueCancel);
   }


   public boolean gestureInProgress ()
   {
      int nowDedos = pulp.getActiveFingersCount ();
      return (nowDedos == 2 || (nowDedos == 1 && pulp.mFingers[0].oneFingerZoomActive ()));
   }

    public boolean gestureFinalized ()
    {
      return pulp.getActiveFingersCount () == 0;
    }
}
