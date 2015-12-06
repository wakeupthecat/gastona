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

import javax.swing.JPanel;
import java.awt.event.*;

import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.math.space.vect3f;

//import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.objects.*;


/**
   General View whos content is stored (cached) in Path-Paint pairs and texts
 */
public abstract class flexiPathView extends JPanel implements MouseListener, MouseMotionListener
{
   private static logger log = new logger (null, "javaj.widgets.graphics.flexiPathView", null);

   public Scene laEscena = new Scene ();

   private boolean ONCE_ANTIALIASING = true;
//   protected int PAPER_BACKGROUND_COLOR = Color.BLACK;
//   protected int PAPER_INK_COLOR = Color.WHITE;

   protected boolean SCREEN_AUTOFIT_X = true;
   protected boolean SCREEN_AUTOFIT_Y = true;

   // protected boolean DEFAULT_GESTURES = true;
   public static final int MODE_GESTURE_NONE = 0;
   public static final int MODE_GESTURE_DEFAULT = 1;
   public static final int MODE_GESTURE_OBJECTS = 2;
   public static final int MODE_GESTURE_MESSAGING = 3;

   protected int gestureMode = MODE_GESTURE_DEFAULT;

   public flexiPathView  ()
   {
      addMouseListener (this);
      addMouseMotionListener(this);
   }


   public void setGestureMode (int mode)
   {
      gestureMode = mode;
   }


   // for implement MODE_GESTURE_MESSAGING
   public abstract void messageGestureStart (float xScreen, float yScreen, float x, float y);
   public abstract void messageGestureContinue (float dxScreen, float dyScreen, float dx, float dy);

   public boolean isGestureMode (int mode)
   {
      return gestureMode == mode;
   }

   public void resetpos ()
   {
      scaleX = 1.f;
      scaleY = 1.f;
      offsetX = 0.f;
      offsetY = 0.f;

      render ();
   }

   public void render ()
   {
      log.dbg (2, "redraw", "  offset " + offsetX + ", " + offsetY);
      log.dbg (2, "redraw", "  scale  " + scaleX + ", " + scaleY);
      invalidate ();
      //postInvalidate ();
   }

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
      setOffset(offX, offY, true);
   }

   public void setOffset(float offX, float offY, boolean render)
   {
      offsetX = offX;
      offsetY = offY;
      log.dbg (2, "setOffset", " set offset to " + offsetX + ", " + offsetY);
      if (render) render ();
   }

   public void setScale(float scalex, float scaley)
   {
      setScale (scalex, scaley, true);
   }

   public void setScale(float scalex, float scaley, boolean render)
   {
      scaleX = scalex;
      scaleY = scaley;
      log.dbg (2, "setScale", " set scate to " + scaleX + ", " + scaleY);
      if (render) render ();
   }

   public void checkAutoFit ()
   {
      if (!SCREEN_AUTOFIT_X && !SCREEN_AUTOFIT_Y) return;

      int tela_dx = getWidth ();
      int tela_dy = getHeight ();

      uniRect bounder = laEscena.bounds ();
      log.dbg (2, "onDraw", "bounds are " + bounder.toString ());

      if (SCREEN_AUTOFIT_X && bounder.width () > 0)
         scaleX =  (float) tela_dx / (float) bounder.width ();

      if (SCREEN_AUTOFIT_Y && bounder.height () > 0)
         scaleY =  (float) tela_dy / (float) bounder.height ();

      if (SCREEN_AUTOFIT_X && SCREEN_AUTOFIT_Y)
      {
         if (scaleX < scaleY)
              scaleX = scaleY;
         else scaleY = scaleX;
      }

      offsetX = - bounder.left();
      offsetY = - bounder.top();

      log.dbg (2, "onDraw", "offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);

      SCREEN_AUTOFIT_X = false;
      SCREEN_AUTOFIT_Y = false;
   }

   private uniPath currPaint = new uniPath ();

   public void render2D (uniCanvas canvas)
   {
      checkAutoFit ();

      canvas.scale (scaleX, scaleY);
      canvas.translate (offsetX, offsetY);

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

////   public boolean onTouchEvent(MotionEvent event)
////   {
////      if (isGestureMode (MODE_GESTURE_NONE)) return false;
////
////      boolean tocao = false;
////      if (isGestureMode (MODE_GESTURE_DEFAULT))
////      {
////         //default gesture expects zoom or pan (displacement) events
////         //
////         tocao = zoomDetector.onTouchEvent(event);
////         //if (!zoomDetector.gestureInProgress ())
////         //   tocao = trazaDetector.onTouchEvent(event);
////      }
////      if (isGestureMode (MODE_GESTURE_OBJECTS) || isGestureMode (MODE_GESTURE_MESSAGING))
////      {
////         tocao = pulpoDetector.onTouchEvent(event);
////         //if (!pulpoDetector.gestureInProgress ())
////         //   tocao = trazaDetector.onTouchEvent(event);
////      }
////
////      if (!tocao)
////         tocao = trazaDetector.onTouchEvent(event);
////
////      return tocao;
////   }
////

   private int pressedX = 0;
   private int pressedY = 0;

   private float scaleRef = 1f;
   private float offsetRefX = 0f;
   private float offsetRefY = 0f;

   private boolean leftButton = true;

   //public boolean onGestureStart (zoomTouchDetector detector)
   public void mousePressed (MouseEvent e)
   {
      pressedX = e.getX ();
      pressedY = e.getY ();

      leftButton = e.getButton() == MouseEvent.BUTTON1;

      scaleRef = scaleX;
      offsetRefX = offsetX;
      offsetRefY = offsetY;

      // System.out.println ("PRESON " + e.getX () + " leftButton " + leftButton);

      //log.dbg (4, "onGestureStart(zoomTouchDetector)", "GESTO START p1_ini " + detector.p1_ini.x + ", " + detector.p1_ini.y + "  p2_ini " + detector.p2_ini.x + ", " + detector.p2_ini.y);
//      if (isGestureMode (MODE_GESTURE_DEFAULT))
//      {
//         log.dbg (4, "onGestureStart(zoomTouchDetector)", "default gestures activated, no object gestures!");
//         detector.setRefOffsetScale (pressedX, pressedY, offsetX, offsetY, scaleX, scaleY);
//         return true;
//      }
//      return false;
   }

   /// implementing zoomTouchDetector.interested
   public void mouseDragged(MouseEvent e)
//   public boolean onGestureContinue (zoomTouchDetector detector)
   {
      //System.out.println ("DRAGON " + e.getX () + " default " + isGestureMode (MODE_GESTURE_DEFAULT));
//      log.dbg (4, "onGestureContinue(zoomTouchDetector)", "GESTO CONT p1_now " + detector.p1_now.x + ", " + detector.p1_now.y + "  p2_now " + detector.p2_now.x + ", " + detector.p2_now.y);
//      detector.calcRectangles ();
//
      if (!isGestureMode (MODE_GESTURE_DEFAULT)) return;

      int inc = (int) (e.getX () - pressedX);
      int incY = (int) (e.getY () - pressedY);
      if (inc == 0) return;

      if (leftButton)
      {
         // move
         offsetX = offsetRefX + inc * scaleX;
         offsetY = offsetRefY + incY * scaleY;
      }
      else
      {
         // zoom
         int tela_dx = getWidth ();
         scaleX = scaleRef * (1f + ((float) inc / ((float) tela_dx/3f)));
         scaleY = scaleRef * (1f + ((float) inc / ((float) tela_dx/3f)));

         //System.out.println ("CORCIAL! escalar " + scaleX);

//         offsetX = (int) detector.nowOffsetX;
//         offsetY = (int) detector.nowOffsetY;
      }

      render ();
      invalidate ();
      paintImmediately (0, 0, 3000, 3000);
   }

   public void mouseReleased(MouseEvent e)
   {
   }

   public void mouseExited(MouseEvent e)
   {
   }

   public void mouseEntered(MouseEvent e)
   {
   }

   public void mouseClicked(MouseEvent e)
   {
   }

   public void mouseMoved(MouseEvent e)
   {
   }

   /// implementing zoomTouchDetector.interested
//   public void onGestureEnd (zoomTouchDetector detector, boolean cancel)
//   {
//      log.dbg (4, "onGestureContinue(zoomTouchDetector)", "GESTO END  p1_fin " + detector.p1_fin.x + ", " + detector.p1_fin.y + "  p2_fin " + detector.p2_fin.x + ", " + detector.p2_fin.y);
//      ONCE_ANTIALIASING = true;
//      postInvalidate ();
//   }

   /// implementing strokeDetector.interested
//   public boolean onGestureStart (strokeDetector detector)
//   {
//      log.dbg (4, "onGestureStart(strokeDetector)", "GESTO CONT pos_ini " + detector.pos_ini.x + ", " + detector.pos_ini.y);
//
//      switch (gestureMode)
//      {
//         case MODE_GESTURE_DEFAULT:
//            log.dbg (4, "onGestureStart(strokeDetector)", "default gestures activated, no object gestures!");
//            detector.setRefOffsetScale (offsetX, offsetY, scaleX, scaleY);
//            return true;
//
//         case MODE_GESTURE_OBJECTS:
//            return laEscena.objectsGestureStart (new vect3f [] { detector.pos_ini }, scaleX, scaleY, offsetX, offsetY);
//
//         case MODE_GESTURE_MESSAGING:
//
//            float normX = detector.pos_ini.x / scaleX - offsetX;
//            float normY = detector.pos_ini.y / scaleY - offsetY;
//            log.dbg (4, "onGestureStart(strokeDetector)", "  normalize pos_ini " + normX + ", " + normY);
//
//            messageGestureStart (detector.pos_ini.x, detector.pos_ini.y, normX, normY);
//            return true;
//
//         default:
//            break;
//      }
//      return false;
//   }

   /// implementing strokeDetector.interested
//   public boolean onGestureContinue (strokeDetector detector)
//   {
//      log.dbg (4, "onGestureContinue(strokeDetector)", "GESTO CONT pos_now " + detector.pos_now.x + ", " + detector.pos_now.y);
//      if (trazaDetector.gestureInProgress ())
//      {
//         detector.calcTranslationNow ();
//
//         switch (gestureMode)
//         {
//            case MODE_GESTURE_DEFAULT:
//               setOffset ((int) detector.nowOffsetX, (int) detector.nowOffsetY);
//               break;
//
//            case MODE_GESTURE_OBJECTS:
//               laEscena.objectsGestureContinue (new vect3f [] { detector.pos_ini }, new vect3f [] { detector.pos_now });
//               break;
//
//            case MODE_GESTURE_MESSAGING:
//               messageGestureContinue (detector.pos_now.x - detector.pos_ini.x,
//                                       detector.pos_now.y - detector.pos_ini.y,
//                                       detector.calcVectorX (),
//                                       detector.calcVectorY ());
//               break;
//
//            default:
//               break;
//         }
//      }
//      //invalidate ();
//      postInvalidate ();
//      return true;
//   }

   /// implementing strokeDetector.interested
//   public void onGestureEnd (strokeDetector detector, boolean cancel)
//   {
//      log.dbg (4, "onGestureContinue(strokeDetector)", "GESTO END  pos_fin " + detector.pos_fin.x + ", " + detector.pos_fin.y);
//      ONCE_ANTIALIASING = true;
//
//      laEscena.objectsGestureEnd ();
//      postInvalidate ();
//   }

   // ==========================================================
   // implementing multiTouchDetector.interested
   //
//   public boolean onGestureStart (multiTouchDetector detector)
//   {
//      if (isGestureMode (MODE_GESTURE_OBJECTS))
//      {
//         laEscena.objectsGestureStart (detector.pIni, scaleX, scaleY, offsetX, offsetY);
//      }
//      if (isGestureMode (MODE_GESTURE_MESSAGING) && detector.pIni.length > 0)
//      {
//         float normX = detector.pIni[0].x / scaleX - offsetX;
//         float normY = detector.pIni[0].y / scaleY - offsetY;
//         log.dbg (4, "onGestureStart(strokeDetector)", "  normalize pos_ini " + normX + ", " + normY);
//
//         messageGestureStart (detector.pIni[0].x, detector.pIni[0].y, normX, normY);
//      }
//
//      return true;
//   }

//   public boolean onGestureContinue (multiTouchDetector detector)
//   {
//      if (isGestureMode (MODE_GESTURE_OBJECTS))
//      {
//         laEscena.objectsGestureContinue (detector.pIni, detector.pNow);
//      }
//      if (isGestureMode (MODE_GESTURE_MESSAGING) && detector.pIni.length > 0 && detector.pNow.length > 0)
//      {
//         float desplazaX = (detector.pNow[0].x - detector.pIni[0].x) / scaleX;
//         float desplazaY = (detector.pNow[0].y - detector.pIni[0].y) / scaleY;
//
//         messageGestureContinue (detector.pNow[0].x - detector.pIni[0].x,
//                                 detector.pNow[0].y - detector.pIni[0].y,
//                                 desplazaX,
//                                 desplazaY);
//      }
//
//      postInvalidate ();
//      return true;
//   }

//   public void onGestureEnd (multiTouchDetector detector, boolean cancel)
//   {
//      laEscena.objectsGestureEnd ();
//      ONCE_ANTIALIASING = true;
//      postInvalidate ();
//   }
}
