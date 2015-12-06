/*
package javaj.widgets.graphics;
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

package javaj.widgets.graphics;

import de.elxala.langutil.androidSysUtil;
import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;

import android.graphics.*;
import android.content.Context;
import android.view.View;
import android.view.MotionEvent;

import de.elxala.math.space.*;
import de.elxala.zServices.*;

/**
   Implements
      - handling of fucntions
      - drawing of them
      - gesture handling
*/
public class mathFunctionView extends View
       implements  zoomTouchDetector.interested
                 , strokeDetector.interested
{
   private static logger log = new logger (null, "javaj.widgets.graphics.mathFunctionView", null);

   private mathFunctionFrame miMatha = new mathFunctionFrame ();
   private zoomTouchDetector zoomDetector = null;
   private strokeDetector trazaDetector = null;

   private int COLOR_GRID_LINES = Color.rgb (255, 126, 0);
   private boolean ONCE_ANTIALIASING = true;

   private int PAPER_BACKGROUND_COLOR = Color.BLACK;

   public mathFunctionView (Context co)
   {
      super(co);
      // movement guys (handlers)
      zoomDetector  = new zoomTouchDetector (this);
      trazaDetector = new strokeDetector (this);
   }

   private float scaleX = 1.f;
   private float scaleY = 1.f;

   public void setScale(float scalex, float scaley)
   {
      scaleX = scalex;
      scaleY = scaley;
   }

   protected void onDraw(Canvas canvas)
   {
      // NOTE: DO NOT TAKE width and height from canvas, I don't know the meaning but it is does not
      //       match with the real shown canvas width and height !!!!
      //
      int tela_dx = getWidth ();
      int tela_dy = getHeight ();

      uniCanvas recanvas = new uniCanvas (canvas, getLeft (), getTop (), getWidth (), getHeight ());

      //System.out.println ("mi propiamente width heit " +  getWidth () + ", " + getHeight ());
      //System.out.println ("el canvases    width heit " +  canvas.getWidth () + ", " + canvas.getHeight ());

      if (tela_dx <= 0 || tela_dy <= 0) return; // >>>> return

      uniPaint paint = new uniPaint();

      float stkWidth = paint.getStrokeWidth ();

      //paint background
      paint.setColor  (PAPER_BACKGROUND_COLOR);
      canvas.drawRect (new Rect (0, 0, tela_dx, tela_dy), paint);

      if (ONCE_ANTIALIASING)
      {
         paint.setAntiAlias(true);
         ONCE_ANTIALIASING = false;
      }
      paint.setColor  (COLOR_GRID_LINES);

      miMatha.set_dx_dy (tela_dx, tela_dy);
      miMatha.drawCoordenada (recanvas, paint, true);
      miMatha.drawCoordenada (recanvas, paint, false);

      //paint.setColor  (Color.WHITE);
      //paint.setAntiAlias(true);

      float x0 = miMatha.toPixelX (0.);
      float y0 = miMatha.toPixelY (0.);
      float dx = tela_dx;
      float dy = tela_dy;

      recanvas.drawLine ((int) 0,  (int) y0, (int) dx, (int) y0, paint);
      recanvas.drawLine ((int) x0,  (int) 0, (int) x0, (int) dy, paint);

      // canvas.drawLines (new float [] { 0.f, 0.f, dx, dy }, paint);

      paint.setColor  (Color.WHITE);
      float incX = (float) (miMatha.rangX() / 300.f);
      float xval = (float) miMatha.minX;
      float yval = (float) Math.sin (xval);
      int fromx = miMatha.toPixelX (xval);
      int fromy = miMatha.toPixelY (yval);
      //paint.setStrokeWidth (6*stkWidth);
      for (int ii = 0; ii < 300; ii ++)
      {
         xval += incX;
         //yval = xval == 0.f ? 1.f: (float) (Math.sin (xval) / xval);
         yval = (float) Math.sin (xval);

         int tox = miMatha.toPixelX ((double) xval);
         int toy = miMatha.toPixelY ((double) yval);

         recanvas.drawLine (fromx, fromy, tox, toy, paint);
         fromx = tox;
         fromy = toy;
      }

//      if (zoomDetector.gestureInProgress ())
//      {
//         paint.setColor  (Color.RED);
//         paint.setStyle  (Paint.Style.STROKE);
//         paint.setStrokeWidth (stkWidth);
//         canvas.drawRect (zoomDetector.rectP1, paint);
//         canvas.drawRect (zoomDetector.rectP2, paint);
//      }

//      if (trazaDetector.gestureInProgress ())
//      {
//         double rex = miMatha.toRealX ((int) trazaDetector.pos_now.x);
//         double rey = miMatha.toRealY ((int) trazaDetector.pos_now.y);
//
//         paint.setColor  (Color.RED);
//         paint.setStyle  (Paint.Style.STROKE);
//         paint.setStrokeWidth (stkWidth);
//         canvas.drawText ("x " + rex, 10, 10, paint);
//         canvas.drawText ("y " + rey, 10, 20, paint);
//      }
   }

   public boolean onTouchEvent(MotionEvent event)
   {
      uniMotion uEvent = new uniMotion (event);

      boolean t1 = zoomDetector.onTouchEvent(uEvent);
      if (zoomDetector.gestureInProgress ())
           trazaDetector.pos_ini = null;
      else t1 = trazaDetector.onTouchEvent(uEvent);
      return t1;
   }

   /// implementing zoomTouchDetector.interested
   public boolean onGestureStart (zoomTouchDetector detector)
   {
      log.dbg (2, "onGestureStart", "GESTO START, zoom in progress " + zoomDetector.gestureInProgress ());
      miMatha.setReference4Gesture ();
      detector.setRefOffsetScale (getLeft (), getTop (),
                                  (float) miMatha.minX, (float) miMatha.maxY,
                                  (float) miMatha.scaleX, (float) miMatha.scaleY,
                                  true);
      return true;
   }

   /// implementing zoomTouchDetector.interested
   public boolean onGestureContinue (zoomTouchDetector detector)
   {
      log.dbg (2, "onGestureContinue", "GESTO CONT, zoom in progress " + zoomDetector.gestureInProgress ());
      detector.calcRectangles ();

      if (zoomDetector.gestureInProgress ())
      {
         log.dbg (2, "onGestureContinue", " zoom in progress");
         //miMatha.zoomRectangular (zoomDetector.rectP1, zoomDetector.rectP2, false);

         zoomDetector.calcZoomNow (false);
         miMatha.setScaleAndOffsets (zoomDetector.nowScaleX, zoomDetector.nowScaleY, zoomDetector.nowOffsetX, zoomDetector.nowOffsetY);
         log.dbg (2, "onGestureContinue", " zoom in progress");
      }
      //invalidate ();
      postInvalidate ();
      return true;
   }

   /// implementing zoomTouchDetector.interested
   public void onGestureEnd (zoomTouchDetector detector, boolean cancel)
   {
      //log.dbg (2, "onGestureEnd", "GESTO END  p1_fin " + printPar (detector.p1_fin) + "  p2_fin " + printPar (detector.p2_fin));
      ONCE_ANTIALIASING = true;
      postInvalidate ();
   }

   /// implementing strokeDetector.interested
   public boolean onGestureStart (strokeDetector detector)
   {
      miMatha.setReference4Gesture ();
      return true;
   }

   /// implementing strokeDetector.interested
   public boolean onGestureContinue (strokeDetector detector)
   {
      //log.dbg (2, "onGestureContinue", "(Lin)GESTO CONT pos_now " + printPar (detector.pos_now));
      if (trazaDetector.gestureInProgress ())
      {
         miMatha.relativeTranslation (trazaDetector.pos_ini, trazaDetector.pos_now);
      }
      //invalidate ();
      postInvalidate ();
      return true;
   }

   /// implementing strokeDetector.interested
   public void onGestureEnd (strokeDetector detector, boolean cancel)
   {
      //log.dbg (2, "onGestureEnd", "(Lin)GESTO END  pos_fin " + printPar (detector.pos_fin));
      ONCE_ANTIALIASING = true;
      postInvalidate ();
   }

   public String printPar (vect3f vect)
   {
      if (vect != null)
         return "(" + vect.x + ", " + vect.y + ")";
      return "(null ..)";
   }

}
