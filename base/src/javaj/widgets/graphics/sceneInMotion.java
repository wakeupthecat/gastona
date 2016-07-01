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

import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.math.space.vect3f;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;


/**
   General View whose content is stored (cached) in Path-Paint pairs and texts
 */
public class sceneInMotion
                implements  ISceneInMotion,
                            twoFingerTouchDetector.interested,
                            multiFingerTouchDetector.interested
{
   private static logger log = new logger (null, "javaj.widgets.graphics.sceneInMotion", null);


   // gestures detectors/helpers
   //
   private twoFingerTouchDetector zoomDetector = null;
   private multiFingerTouchDetector pulpoDetector = null;

   public Scene laEscena = new Scene ();

   private boolean ONCE_ANTIALIASING = true;

   protected boolean SCREEN_AUTOFIT_X = true;
   protected boolean SCREEN_AUTOFIT_Y = true;

   public sceneInMotion ()
   {
      zoomDetector  = new twoFingerTouchDetector (this);
      pulpoDetector = new multiFingerTouchDetector (this);
   }

   // protected boolean DEFAULT_GESTURES = true;

   public static final int ALLOW_GESTURE_ZOOM_MULTITOUCH = 1;
   public static final int ALLOW_GESTURE_ZOOM_SINGLE_X = 2;
   public static final int ALLOW_GESTURE_ZOOM_SINGLE_Y = 4;
   public static final int ALLOW_GESTURE_TRANSLATION_X = 8;
   public static final int ALLOW_GESTURE_TRANSLATION_Y = 16;
   public static final int ALLOW_GESTURE_SEND_MESSAGE = 32;

   public static final int ALLOW_GESTURE_BOTH_TRANSLATIONS = ALLOW_GESTURE_TRANSLATION_X | ALLOW_GESTURE_TRANSLATION_Y;

   public static final int MODE_GESTURE_NONE = 0;
   public static final int MODE_GESTURE_DEFAULT = ALLOW_GESTURE_ZOOM_MULTITOUCH | ALLOW_GESTURE_BOTH_TRANSLATIONS;
   public static final int MODE_GESTURE_OBJECTS = ALLOW_GESTURE_BOTH_TRANSLATIONS;
   public static final int MODE_GESTURE_ZOOM_Y_TRANS_X = ALLOW_GESTURE_ZOOM_SINGLE_Y | ALLOW_GESTURE_TRANSLATION_X;
   public static final int MODE_GESTURE_MESSAGING = ALLOW_GESTURE_ZOOM_MULTITOUCH | ALLOW_GESTURE_BOTH_TRANSLATIONS | ALLOW_GESTURE_SEND_MESSAGE;

   protected int gestureMode = MODE_GESTURE_DEFAULT;

   public void setGestureMode (int mode)
   {
      gestureMode = mode;
   }


   public boolean isGestureMode (int mode)
   {
      return gestureMode == mode;
   }

   public boolean allowGestureZoom ()
   {
      return (gestureMode & ALLOW_GESTURE_ZOOM_MULTITOUCH) != 0;
   }

   public boolean allowGestureTranslation ()
   {
      return (gestureMode & ALLOW_GESTURE_BOTH_TRANSLATIONS) != 0;
   }

   public boolean allowGestureMessages ()
   {
      return (gestureMode & ALLOW_GESTURE_SEND_MESSAGE) != 0;
   }

////   public void resetpos ()
////   {
////      scaleX = 1.f;
////      scaleY = 1.f;
////      offsetX = 0.f;
////      offsetY = 0.f;
////
////      render ();
////   }

////   public void render ()
////   {
////      log.dbg (2, "redraw", "  offset " + offsetX + ", " + offsetY);
////      log.dbg (2, "redraw", "  scale  " + scaleX + ", " + scaleY);
////      //invalidate ();
////      uniInvalidate ();
////   }

   private boolean verbose = false;
   private float scaleX = 1.f;
   private float scaleY = 1.f;
   private float offsetX = 0.f;
   private float offsetY = 0.f;

   public float getScaleX () { return scaleX; };
   public float getScaleY () { return scaleY; };
   public float getOffsetX () { return offsetX; };
   public float getOffsetY () { return offsetY; };

   public void setOffset(float offX, float offY)
   {
      offsetX = offX;
      offsetY = offY;
      log.dbg (2, "setOffset", " set offset to " + offsetX + ", " + offsetY);
   }

   public void setScale(float scalex, float scaley)
   {
      scaleX = scalex;
      scaleY = scaley;
      log.dbg (2, "setScale", " set scate to " + scaleX + ", " + scaleY);
   }

   public void setAutoFit (boolean onX, boolean onY)
   {
      SCREEN_AUTOFIT_X = onX;
      SCREEN_AUTOFIT_Y = onY;
   }

   public void checkAutoFit (uniCanvas canvas)
   {
      if (!SCREEN_AUTOFIT_X && !SCREEN_AUTOFIT_Y) return;

      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      uniRect bounder = laEscena.bounds ();
      log.dbg (2, "checkAutoFit", "bounds are " + bounder.toString ());

      if (SCREEN_AUTOFIT_X && bounder.width () > 0)
         scaleX =  (float) tela_dx / (float) bounder.width ();

      if (SCREEN_AUTOFIT_Y && bounder.height () > 0)
         scaleY =  (float) tela_dy / (float) bounder.height ();

      if (SCREEN_AUTOFIT_X && SCREEN_AUTOFIT_Y)
      {
         // si al aplicar la escala X la y se va de madre...
         if (scaleX * bounder.height() > tela_dy)
         {
            scaleX = scaleY;
         }
         else
         {
            scaleY = scaleX;
         }
      }

      // in reality translate
      offsetX = bounder.left ();
      offsetY = bounder.top ();

      log.dbg (2, "checkAutoFit", "offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);

      SCREEN_AUTOFIT_X = false;
      SCREEN_AUTOFIT_Y = false;
   }

   private uniPaint currPaint = new uniPaint ();

   public void renderUniCanvas (uniCanvas canvas, uniColor backgroundColor)
   {
      checkAutoFit (canvas);

      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      //clear background if any
      if (backgroundColor != null)
         canvas.fillRect (new uniRect (0, 0, tela_dx, tela_dy), backgroundColor);

      currPaint.setAntiAlias(true);

      //android.util.Log.d ("onDraw", "aplico escala " + scaleX + ", " + scaleY);

      //System.out.println ("apply offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
      log.dbg (2, "onDraw", "apply offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
      canvas.scale (scaleX, scaleY);

      if (laEscena.ETHER_affected)
      {
         //System.out.println ("ether movement offset " + laEscena.ETHER_desplazaY + ", " + laEscena.ETHER_desplazaY);
         log.dbg (2, "onDraw", "ether movement offset " + laEscena.ETHER_desplazaY + ", " + laEscena.ETHER_desplazaY);
      }
      canvas.translate (-offsetX + (laEscena.ETHER_affected ? laEscena.ETHER_desplazaX: 0.f),
                        -offsetY + (laEscena.ETHER_affected ? laEscena.ETHER_desplazaY: 0.f));

      //laEscena.draw (new uniCanvas (canvas));
      laEscena.draw (canvas);
   }

   // ================================================================
   // Attachability management
   //
   protected void onWindowVisibilityChanged (int visibility)
   {
      log.dbg (4, "onWindowVisibilityChanged", "" + visibility);
   }

   protected void onDetachedFromWindow ()
   {
      log.dbg (4, "onDetachedFromWindow", "detached");
   }


   // ================================================================
   // Movement management
   //

//!   public boolean onTouchEvent(MotionEvent event)
   public boolean onUniMotion (uniMotion uniEvent)
   {
      log.dbg (4, "onTouchEvent", "MODE_GESTURE = " + gestureMode + " event action " + uniEvent.getAction() + " pointer count " + uniEvent.getPointerCount());

      if (isGestureMode (MODE_GESTURE_NONE)) return false;

//!      uniMotion uniEvent = new uniMotion (event);

//      boolean tocao = false;
//      if (isGestureMode (MODE_GESTURE_DEFAULT))
//      {
//         //default gesture expects zoom or pan (displacement) events
//         //
//         tocao = zoomDetector.onTouchEvent(uniEvent) | pulpoDetector.onTouchEvent(uniEvent);
//      }
//      if (!tocao)
//         tocao = pulpoDetector.onTouchEvent(uniEvent);
//
//      if (zoomDetector.gestureInProgress ())
//      {
//         pulpoDetector.reset ();
//      }


      return (allowGestureZoom () ? zoomDetector.onTouchEvent(uniEvent):false) |
             (allowGestureTranslation () ? pulpoDetector.onTouchEvent(uniEvent): false);
   }


   /// implementing twoFingerTouchDetector.interested
   public boolean onGestureStart (twoFingerTouchDetector detector)
   {
      //log.dbg (4, "onGestureStart(twoFingerTouchDetector)", "GESTO START p1_ini " + detector.p1_ini.x + ", " + detector.p1_ini.y + "  p2_ini " + detector.p2_ini.x + ", " + detector.p2_ini.y);
      if (isGestureMode (MODE_GESTURE_DEFAULT))
      {
         log.dbg (4, "onGestureStart(twoFingerTouchDetector)", "default gestures activated, no object gestures!");
         detector.setRefOffsetScale (0, 0, offsetX, offsetY, scaleX, scaleY, false);
         return true;
      }
      return false;
   }

   /// implementing twoFingerTouchDetector.interested
   public boolean onGestureContinue (twoFingerTouchDetector detector)
   {
      //log.dbg (4, "onGestureContinue(twoFingerTouchDetector)", "GESTO CONT p1_now " + detector.p1_now.x + ", " + detector.p1_now.y + "  p2_now " + detector.p2_now.x + ", " + detector.p2_now.y);

      if (isGestureMode (MODE_GESTURE_DEFAULT) && detector.gestureInProgress ())
      {
         detector.calcZoomNow (true);
         offsetX = (int) detector.nowOffsetX;
         offsetY = (int) detector.nowOffsetY;
         scaleX = detector.nowScaleX;
         scaleY = detector.nowScaleY;
      }
      return true;
   }

   /// implementing twoFingerTouchDetector.interested
   public void onGestureEnd (twoFingerTouchDetector detector, boolean cancel)
   {
      //log.dbg (4, "onGestureContinue(twoFingerTouchDetector)", "GESTO END  p1_fin " + detector.p1_fin.x + ", " + detector.p1_fin.y + "  p2_fin " + detector.p2_fin.x + ", " + detector.p2_fin.y);
      ONCE_ANTIALIASING = true;
   }

   // ==========================================================
   // implementing multiTouchDetector.interested
   //
   public void onFingerDown (multiFingerTouchDetector detector, int fingerIndx)
   {
      if (zoomDetector.gestureInProgress ()) return;

      if (fingerIndx < 0 || fingerIndx >= detector.mFingers.length)
      {
         log.err ("onFingerDown", "fingerIndx " + fingerIndx + " while detector fingers = " + detector.mFingers.length);
         return;
      }
      fingerTouch dedo = detector.mFingers[fingerIndx];

      if (isGestureMode (MODE_GESTURE_OBJECTS) ||
          (fingerIndx == 0 && isGestureMode (MODE_GESTURE_DEFAULT)) // implement total translation only with finger 0
         )
      {
         laEscena.objectsGestureStart (dedo.pIni, fingerIndx, scaleX, scaleY, offsetX, offsetY);
      }
   }

   public void onFingerUp (multiFingerTouchDetector detector, int fingerIndx)
   {
      if (fingerIndx < 0 || fingerIndx >= detector.mFingers.length)
      {
         log.err ("onFingerUp", "fingerIndx " + fingerIndx + " while detector fingers = " + detector.mFingers.length);
         return;
      }
      laEscena.objectsGestureEnd (fingerIndx);
      //System.out.println ("onFingerUp !!! " + laEscena.ETHER_affected + " : " + laEscena.ETHER_desplazaX + ", " + laEscena.ETHER_desplazaY);
      if (laEscena.ETHER_affected)
      {
         log.dbg (4, "onFingerUp apply Ether movement (refScala " + laEscena.refScaleX + ") " + laEscena.ETHER_affected + " : " + laEscena.ETHER_desplazaX + ", " + laEscena.ETHER_desplazaY);
         offsetX -= laEscena.ETHER_desplazaX;
         offsetY -= laEscena.ETHER_desplazaY;
         laEscena.ETHER_desplazaX = 0;
         laEscena.ETHER_desplazaY = 0;
         laEscena.ETHER_affected = false;
      }
   }

   public void onMovement (multiFingerTouchDetector detector)
   {
      if (zoomDetector.gestureInProgress ()) return;

      if (isGestureMode (MODE_GESTURE_OBJECTS) || isGestureMode (MODE_GESTURE_DEFAULT))
      {
         laEscena.objectsGestureContinue (detector.mFingers);
      }
   }

   public void onGestureEnd (multiFingerTouchDetector detector, boolean cancel)
   {
      // OJO! parece que este me'todo NUNCA se llama!! TODO: investigarlo
      laEscena.objectsGestureEnd ();
      ONCE_ANTIALIASING = true;
   }
}
