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
 * Detects the typical zoom multitouch pinch and spread gesture
 *
 */
public class zoomTouchDetector // extends multiFingerTouchDetector
{
   private static logger log = new logger (null, "javaj.widgets.gestures.zoomTouchDetector", null);
   private multiFingerTouchDetector pulp = new multiFingerTouchDetector (null);

   public uniRect rectP1 = new uniRect ();
   public uniRect rectP2 = new uniRect ();

   /**
    */
   public interface interested
   {
      public boolean onGestureStart    (zoomTouchDetector detector);
      public boolean onGestureContinue (zoomTouchDetector detector);
      public void    onGestureEnd      (zoomTouchDetector detector, boolean cancel);
   }

    /**
     */
   public class Simpleinterested implements interested
   {
      public boolean onGestureStart (zoomTouchDetector detector)
      {
         return true;
      }

      public boolean onGestureContinue (zoomTouchDetector detector)
      {
         return true;
      }

      public void onGestureEnd (zoomTouchDetector detector, boolean cancel)
      {
      }
   }

   //private Context mContext;
   private interested myInterested;

   //public zoomTouchDetector(Context context, interested listener)
   public zoomTouchDetector(interested listener)
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

   private void setRectangles ()
   {
      rectP1 = rectP2 = null;

      fingerTouch dedo1 = pulp.mFingers[0];
      fingerTouch dedo2 = pulp.mFingers[1];

      if (dedo1 == null || (!dedo1.oneFingerZoomActive () && dedo2 == null)) return;

      vect3f p1_ini = dedo1.oneFingerZoomActive () ? dedo1.oneFingerZoomFirstPosition () : dedo1.pIni;
      vect3f p2_ini = dedo1.oneFingerZoomActive () ? dedo1.pIni: dedo2.pIni;

      vect3f p1_now = dedo1.oneFingerZoomActive () ? dedo1.oneFingerZoomFirstPosition () : dedo1.getLastPosition ();
      vect3f p2_now = dedo1.oneFingerZoomActive () ? dedo1.getLastPosition () : dedo2.getLastPosition ();

      rectP1 = makeRect (p1_ini, p2_ini);
      rectP2 = makeRect (p1_now, p2_now);
   }

   public boolean plausibleZoom ()
   {
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

   public double nowOffsetX = 0.f;
   public double nowOffsetY = 0.f;
   public double nowScaleX = 1.f;
   public double nowScaleY = 1.f;

   // return the original point as if scale and offset weren't exist
   //
   public vect3f unScaledPoint (vect3f p)
   {
      vect3f uvec = new vect3f (p);
      uvec.x /= nowScaleX;
      uvec.y /= nowScaleY;
      uvec.x += nowOffsetX;
      uvec.y += nowOffsetY;
      return uvec;
   }

   public void calcZoomNow (boolean square)
   {
      log.dbg (2, "calcZoomNow", "marc      X, Y = " + marcX + ", " + marcY);
      log.dbg (2, "calcZoomNow", "refOff    X, Y = " + refOffX + ", " + refOffY);
      log.dbg (2, "calcZoomNow", "refScale  X, Y = " + refScaleX + ", " + refScaleY);

      // add 1 pixel to avoid raise conditions (factor 0 or infinite)
      double facZoomX = (Math.abs (rectP2.width ()) + 1.f) / (Math.abs (rectP1.width ()) + 1.f);
      double facZoomY = (Math.abs (rectP2.height ()) + 1.f) / (Math.abs (rectP1.height ()) + 1.f);

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
            if (plausibleZoom ())
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
