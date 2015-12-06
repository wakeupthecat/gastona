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

import javaj.widgets.graphics.uniRect;

/**
 * Detects the typical zoom multitouch pinch and spread gesture
 *
 */
public class zoomTouchDetectorBase
{
   public vect3f p1_now = null;
   public vect3f p2_now = null;

   public vect3f p1_fin = null;
   public vect3f p2_fin = null;

   public vect3f p1_ini = null;
   public vect3f p2_ini = null;

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

   private interested myInterested;

   public zoomTouchDetectorBase(interested listener)
   {
      myInterested = listener;
   }

   private uniRect makeRect (vect3f p1, vect3f p2)
   {
      return
      new Rect ((int) Math.min (p1.x, p2.x), // left
                (int) Math.max (p1.y, p2.y), // top
                (int) Math.max (p1.x, p2.x), // right
                (int) Math.min (p1.y, p2.y)  // bottom
                );

   }

   public void calcRectangles ()
   {
      vect3f v1comp = (p1_fin != null) ? p1_fin: (p1_now != null) ? p1_now: p1_ini;
      vect3f v2comp = (p2_fin != null) ? p2_fin: (p2_now != null) ? p2_now: p2_ini;

      rectP1 = makeRect (p1_ini, p2_ini);
      rectP2 = makeRect (v1comp, v2comp);
   }

   public boolean plausibleZoom ()
   {
      return  rectP2.right() != rectP2.left() &&
              rectP2.top() != rectP2.bottom() &&
              rectP1.right() != rectP1.left() &&
              rectP1.top() != rectP1.bottom();
   }

   private int marcX = 0, marcY = 0;
   private float refOffX = 0.f, refOffY = 0.f, refScaleX = 1.f, refScaleY = 1.f;
   private float refEndX = 0.f, refEndY = 0.f;

   public void setRefOffsetScale(int marcoX, int marcoY, float offsetX, float offsetY, float scaleX, float scaleY)
   {
      marcX = marcoX;
      marcY = marcoY;
      refOffX = offsetX;
      refOffY = offsetY;
      refScaleX = scaleX;
      refScaleY = scaleY;
   }

   public void setRefOffsetScaleExtra(float endX, float endY)
   {
      refEndX = endX;
      refEndY = endY;
   }

   public float nowOffsetX = 0.f;
   public float nowOffsetY = 0.f;
   public float nowEndX = 0.f;
   public float nowEndY = 0.f;

   public float nowScaleX = 1.f;
   public float nowScaleY = 1.f;

   public void calcZoomNow (boolean square)
   {
      //!android.util.Log.d ("soom", "calcZoomNow .. marcX     X, Y " + marcX + ", " + marcY);
      //!android.util.Log.d ("soom", "calcZoomNow .. refOffX   X, Y " + refOffX + ", " + refOffY);
      //!android.util.Log.d ("soom", "calcZoomNow .. refScaleX X, Y " + refScaleX + ", " + refScaleY);

      // add 1 pixel to avoid raise conditions (factor 0 or infinite)
      float facZoomX = (Math.abs (rectP2.right() - rectP2.left()) + 1.f) / (Math.abs (rectP1.right() - rectP1.left()) + 1.f);
      float facZoomY = (Math.abs (rectP2.top() - rectP2.bottom()) + 1.f) / (Math.abs (rectP1.top() - rectP1.bottom()) + 1.f);

      // keep factors between 0.05 and 20
      facZoomX = Math.min (Math.max (facZoomX, 0.15f), 6.f);
      facZoomY = Math.min (Math.max (facZoomY, 0.15f), 6.f);

      //!android.util.Log.d ("soom", "calcZoomNow .. facZoom X, Y " + facZoomX + ", " + facZoomY);

      if (square)
      {
         facZoomX = Math.max (facZoomX, facZoomY);
         facZoomY = facZoomX;
      }

      // center in pixels of rectangle ref
      int cx = (int) ((rectP1.left() + rectP1.right()) / 2.f) - marcX;
      int cy = (int) ((rectP1.top() + rectP1.bottom()) / 2.f) - marcY;

      //!android.util.Log.d ("soom", "calcZoomNow   cx cy " + cx + ", " + cy);

      nowOffsetX = refOffX + cx * ( 1.f / facZoomX - 1.f) / refScaleX;
      nowOffsetY = refOffY + cy * ( 1.f / facZoomY - 1.f) / refScaleY;

      nowScaleX = refScaleX * facZoomX;
      nowScaleY = refScaleY * facZoomY;

      nowEndX = nowOffsetX + (refEndX - refOffX) * facZoomX;
      nowEndY = nowOffsetY + (refEndY - refOffY) * facZoomY;

      //!android.util.Log.d ("soom", "calcZoomNow   nowOffsetX, Y " + nowOffsetX + ", " + nowOffsetY);
      //!android.util.Log.d ("soom", "calcZoomNow   nowScaleX , Y " + nowScaleX + ", " + nowScaleY);
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
         if (event.getPointerCount() > 1 &&
             (action == MotionEvent.ACTION_POINTER_1_DOWN ||
              action == MotionEvent.ACTION_POINTER_2_DOWN))
         {
            p1_ini = new vect3f (event.getX(0), event.getY(0));
            p2_ini = new vect3f (event.getX(1), event.getY(1));
            p1_fin = null;
            p2_fin = null;
            p1_now = null;
            p2_now = null;

            //android.util.Log.d ("CASCOS", "p1_ini " + p1_ini.x + ", " + p1_ini.y);
            //android.util.Log.d ("CASCOS", "p2_ini " + p2_ini.x + ", " + p2_ini.y);
            if (! myInterested.onGestureStart(this))
            {
               //android.util.Log.d ("CASCOS", "RETIROOOO");
               p1_ini = p2_ini = null;
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
            p1_fin = p1_now;
            p2_fin = p2_now;
            if (event.getPointerCount() > 0)
               p1_fin = new vect3f (event.getX(0), event.getY(0));
            if (event.getPointerCount() > 1)
               p2_fin = new vect3f (event.getX(1), event.getY(1));

            finalizeGesture (false);
            break;

         case MotionEvent.ACTION_CANCEL:
            finalizeGesture (true);
            p1_ini = null;
            break;

         case MotionEvent.ACTION_MOVE:
            //android.util.Log.d ("CASCOS", "MOVE");
            if (event.getPointerCount() > 0)
               p1_now = new vect3f (event.getX(0), event.getY(0));
            if (event.getPointerCount() > 1)
               p2_now = new vect3f (event.getX(1), event.getY(1));

            calcRectangles ();
            if (plausibleZoom ())
               if (! myInterested.onGestureContinue(this))
                  p1_ini = null;
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

      // reset gestureInProgress and gestureFinalized
      p1_ini = null;
      p2_ini = null;
      p1_now = null;
      p2_now = null;
      p1_fin = null;
      p2_fin = null;
   }


    /**
     */
    public boolean gestureInProgress ()
    {
      return p1_ini != null && p1_fin == null;
    }

    public boolean gestureFinalized ()
    {
      return p1_ini != null && p1_fin != null;
    }
}
