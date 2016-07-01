/*
package javaj.widgets.graphics;
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

package javaj.widgets;

import de.elxala.langutil.androidSysUtil;
import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;

import android.graphics.*;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;

import de.elxala.math.space.*;


public class zBasicGraphic2D extends View
                             implements izWidget
                                        , zoomTouchDetector.interested
                                        , strokeDetector.interested
                                        , multiFingerTouchDetector.interested
                                        //, MensakaTarget
{
   private zoomTouchDetector zoomDetector = null;
   private strokeDetector trazaDetector = null;
   private multiFingerTouchDetector pulpoDetector = null;

   private int COLOR_GRID_LINES = Color.rgb (255, 126, 0);
   private boolean ONCE_ANTIALIASING = true;

   private int PAPER_BACKGROUND_COLOR = Color.BLACK;

   public zBasicGraphic2D (Context co, String name)
   {
      super(co);
      // movement guys (handlers)
      zoomDetector  = new zoomTouchDetector (this);
      trazaDetector = new strokeDetector (this);
      //pulpoDetector = new multiFingerTouchDetector (co, this);
      pulpoDetector = new multiFingerTouchDetector (this);
      setName (name);
   }

   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return getHeight (); }
   public int getDefaultWidth () { return getWidth (); }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String pname)
   {
      mName = pname;
   }

   private float scaleX = 1.f;
   private float scaleY = 1.f;

   private float pressure1 = 0.f;
   private float pressure2 = 0.f;

   public void setScale(float scalex, float scaley)
   {
      scaleX = scalex;
      scaleY = scaley;
   }

   protected void onDraw(Canvas canvas)
   {
      uniCanvas uCanvas = new uniCanvas (canvas, getLeft (), getTop (), getWidth (), getHeight ());
      //android.util.Log.d ("CASCOS", "ME PINTAS " + zoomDetector.gestureInProgress ());
      // NOTE: DO NOT TAKE width and height from canvas, I don't know the meaning but it is does not
      //       match with the real shown canvas width and height !!!!
      //
      int tela_dx = getWidth ();
      int tela_dy = getHeight ();

      //System.out.println ("mi propiamente width heit " +  getWidth () + ", " + getHeight ());
      //System.out.println ("el canvases    width heit " +  canvas.getWidth () + ", " + canvas.getHeight ());

      if (tela_dx <= 0 || tela_dy <= 0) return; // >>>> return

      uniPaint uPaint = new uniPaint ();

      float stkWidth = uPaint.getStrokeWidth ();

      //paint background
      uPaint.setColor  (PAPER_BACKGROUND_COLOR);
      canvas.drawRect (new Rect (0, 0, tela_dx, tela_dy), uPaint);

      if (ONCE_ANTIALIASING)
      {
         uPaint.setAntiAlias(true);
         ONCE_ANTIALIASING = false;
      }
      uPaint.setColor  (COLOR_GRID_LINES);

      //canvas.drawLine (0,  0, 100, 100, paint);

      if (zoomDetector.gestureInProgress ())
      {
         uPaint.setColor  (Color.RED);
         uPaint.setStyle  (Paint.Style.STROKE);
         uPaint.setStrokeWidth (stkWidth);
         uCanvas.drawRect (zoomDetector.rectP1, uPaint);
         uCanvas.drawRect (zoomDetector.rectP2, uPaint);
      }

      Rect charBound = new Rect ();
      uPaint.getTextBounds ("X", 0, 1, charBound);

      int incx = charBound.width ();
      int incy = charBound.height ();
      int posx = (int) (incx * .5);
      int posy = (int) (incy * 1.5);

      for (int ff = 0; ff < pulpoDetector.getMaxFingers (); ff ++)
      {
         fingerTouch fi = pulpoDetector.getFinger (ff);
         if (fi != null && fi.isPressing ())
         {
            vect3f vec0 = fi.pIni;
            vect3f vec1 = fi.pNow;
            float velAbs = fi.getSpeed ();
            float accAbs = fi.getAcceleration ();

            uPaint.setColor  (Color.YELLOW);
            uCanvas.drawText ("" + ff + ":", posx, posy, uPaint);
            if (vec1 != null && vec0 != null)
            {
               uCanvas.drawText ("" + (int) vec1.x , posx + incx*3 , posy, uPaint);
               uCanvas.drawText ("" + (int) vec1.y , posx + incx*7 , posy, uPaint);

               uCanvas.drawText ("v " + velAbs , posx + incx*12 , posy, uPaint);
               uCanvas.drawText ("a " + accAbs , posx + incx*28 , posy, uPaint);

               uPaint.setColor  (Color.RED);
               uCanvas.drawLine ((int) vec0.x, (int) vec0.y, (int) vec1.x, (int) vec1.y, uPaint);
            }
         }
         posy += incy + 4;
////         if (pulpoDetector.gestureInProgress ())
////         {
////            Rect charBound = new Rect ();
////            paint.getTextBounds ("X", 0, 1, charBound);
////
////            int incx = charBound.width ();
////            int incy = charBound.height ();
////            int posx = (int) (incx * .5);
////            int posy = (int) (incy * 1.5);
////
////            for (int ii = 0; ii < pulpoDetector.getHighestCount (); ii ++)
////            {
////               vect3f vec0 = pulpoDetector.pIni[ii];
////               vect3f vec1 = pulpoDetector.pNow[ii];
////
////               paint.setColor  (Color.YELLOW);
////               canvas.drawText ("" + ii + ":", posx, posy, paint);
////               if (vec1 != null && vec0 != null)
////               {
////                  canvas.drawText ("" + (int) vec1.x , posx + incx*3 , posy, paint);
////                  canvas.drawText ("" + (int) vec1.y , posx + incx*10, posy, paint);
////
////                  canvas.drawText ("" + pressure1 , posx + incx*25 , posy, paint);
////                  canvas.drawText ("" + pressure2 , posx + incx*25 , posy, paint);
////
////                  paint.setColor  (Color.RED);
////                  canvas.drawLine (vec0.x, vec0.y, vec1.x, vec1.y, paint);
////               }
////               posy += incy + 4;
////            }
////         }
      }
   }

   public boolean onTouchEvent(MotionEvent event)
   {
      uniMotion uEvent = new uniMotion (event);

      boolean t1 = zoomDetector.onTouchEvent(uEvent);
      if (!zoomDetector.gestureInProgress ())
         t1 = trazaDetector.onTouchEvent(uEvent);

      t1 |= pulpoDetector.onTouchEvent(uEvent);
      return t1;
   }

   /// implementing zoomTouchDetector.interested
   public boolean onGestureStart (zoomTouchDetector detector)
   {
      // TODO Set some reference to know the start rectangle
      //miMatha.setReference4Gesture ();
      //detector.setRefOffsetScale (getLeft (), getTop (), (float) miMatha.minX, (float) miMatha.maxY, (float) miMatha.scaleX, (float) miMatha.scaleY);
      return true;
   }

   /// implementing zoomTouchDetector.interested
   public boolean onGestureContinue (zoomTouchDetector detector)
   {
      //android.util.Log.d ("soom", "GESTO CONT  p1_now " + printPar (detector.p1_now) + "  p2_now " + printPar (detector.p2_now));

      if (zoomDetector.gestureInProgress ())
      {
         // now zoomDetector.rectP1, zoomDetector.rectP2 are calculated
      }
      //invalidate ();
      postInvalidate ();
      return true;
   }

   /// implementing zoomTouchDetector.interested
   public void onGestureEnd (zoomTouchDetector detector, boolean cancel)
   {
      //android.util.Log.d ("soom", "GESTO END  p1_fin " + printPar (detector.p1_fin) + "  p2_fin " + printPar (detector.p2_fin));
      ONCE_ANTIALIASING = true;
      postInvalidate ();
   }

   /// implementing strokeDetector.interested
   public boolean onGestureStart (strokeDetector detector)
   {
      // TODO Set some reference to know the start rectangle
      //miMatha.setReference4Gesture ();^
      if (zoomDetector.gestureInProgress ())
         return false;
      return true;
   }

   /// implementing strokeDetector.interested
   public boolean onGestureContinue (strokeDetector detector)
   {
      android.util.Log.d ("soom", "(Lin)GESTO CONT pos_now " + printPar (detector.pos_now));
      if (zoomDetector.gestureInProgress ())
         return false;

      if (trazaDetector.gestureInProgress ())
      {
         // TODO now use trazaDetector.pos_ini, trazaDetector.pos_now
         // miMatha.relativeTranslation (trazaDetector.pos_ini, trazaDetector.pos_now);
      }
      //invalidate ();
      postInvalidate ();
      return true;
   }

   /// implementing strokeDetector.interested
   public void onGestureEnd (strokeDetector detector, boolean cancel)
   {
      android.util.Log.d ("soom", "(Lin)GESTO END  pos_fin " + printPar (detector.pos_fin));
      ONCE_ANTIALIASING = true;
      postInvalidate ();
   }


   public void onFingerDown    (multiFingerTouchDetector detector, int fingerIndx)
   {
      android.util.Log.d ("pulps", "onFingerDown " + fingerIndx);
      postInvalidate ();
   }

   public void onFingerUp      (multiFingerTouchDetector detector, int fingerIndx)
   {
      android.util.Log.d ("pulps", "onFingerUp " + fingerIndx);
      postInvalidate ();
   }

   public void onMovement      (multiFingerTouchDetector detector)
   {
      android.util.Log.d ("pulps", "onMovement");
      postInvalidate ();
   }

   public void onGestureEnd    (multiFingerTouchDetector detector, boolean cancel)
   {
      android.util.Log.d ("pulps", "onGestureEnd");
      postInvalidate ();
   }

////   // ==========================================================
////   // implementing multiTouchDetector.interested
////   //
////   public boolean onGestureStart (multiTouchDetector detector)
////   {
////      android.util.Log.d ("soom", "GERJO START " + detector.getHighestCount () + " deditos!");
////      return true;
////   }
////
////   /// implementing strokeDetector.interested
////   public boolean onGestureContinue (multiTouchDetector detector)
////   {
////      android.util.Log.d ("soom", "GERJO SIGUE " + detector.getHighestCount () + " deditos!");
////
////      postInvalidate ();
////      return true;
////   }
////
////   /// implementing strokeDetector.interested
////   public void onGestureEnd (multiTouchDetector detector, boolean cancel)
////   {
////      android.util.Log.d ("soom", "GERJO FIN " + detector.getHighestCount () + " deditos!");
////
////      ONCE_ANTIALIASING = true;
////      postInvalidate ();
////   }

   public String printPar (vect3f vect)
   {
      if (vect != null)
         return "(" + vect.x + ", " + vect.y + ")";
      return "(null ..)";
   }

}
