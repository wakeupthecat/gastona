/*
package javaj.widgets.graphics;
Copyright (C) 2005-2020 Alejandro Xalabarder Aulet

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

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;


import de.elxala.math.space.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/*
   //(o) WelcomeGastona_source_javaj_widgets (k) z2DMathFunc

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       z2DMathFunc
   <groupInfo>  2D
   <javaClass>  javaj.widgets.z2DMathFunc
   <importance> 8
   <desc>       //2D Math functions

   <help>
      //
      // Testing for 2D Math functions
      //
      //

   <prefix>  2Dmath

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , (String)                   , //Caption of the checkbox
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      selected         , inout , 0 | 1                      , //Value 0 not checked, 1 checked

   <messages>

      msg     , in_out , desc

              ,  out   , check box has been pressed
      data!   ,  in    , update data
      control!,  in    , update control


   <examples>
      gastSample

      Demo z2DMath

   <Demo z2DMath>
      //#javaj#
      //
      //    <frames> 2DmathDemo, Demo z2DMathFunc, 600, 500
      //

#**FIN_EVA#

*/


/**
   Implements
      - handling of fucntions
      - drawing of them
      - gesture handling
*/

public class mathFunctionView extends JPanel implements MouseListener, MouseMotionListener
//       implements  zoomTouchDetector.interested
//                 , strokeDetector.interested
{
   private mathFunctionFrame miMatha = new mathFunctionFrame ();
//   private zoomTouchDetector zoomDetector = null;
//   private strokeDetector trazaDetector = null;

   private uniColor COLOR_GRID_LINES = new uniColor (255, 126, 0);
   private boolean ONCE_ANTIALIASING = true;

   public mathFunctionView ()
   {
      addMouseListener (this);
      addMouseMotionListener(this);
      miMatha = new  mathFunctionFrame ();
      // movement guys (handlers)
//      zoomDetector  = new zoomTouchDetector (co, this);
//      trazaDetector = new strokeDetector (co, this);
   }

   private float scaleX = 1.f;
   private float scaleY = 1.f;

   public void setScale(float scalex, float scaley)
   {
      scaleX = scalex;
      scaleY = scaley;
   }

   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      uniCanvas ges2 = new uniCanvas ((Graphics2D) g, getX(), getY(), getWidth (), getHeight ());
      render2D (ges2);
   }

   public void render2D (uniCanvas canvas)
   //protected void onDraw(Canvas canvas)
   {
      // NOTE: DO NOT TAKE width and height from canvas, I don't know the meaning but it is does not
      //       match with the real shown canvas width and height !!!!
      //
      int tela_dx = getWidth ();
      int tela_dy = getHeight ();

      //System.out.println ("mi propiamente width heit " +  getWidth () + ", " + getHeight ());
      //System.out.println ("el canvases    width heit " +  canvas.getWidth () + ", " + canvas.getHeight ());

      if (tela_dx <= 0 || tela_dy <= 0) return; // >>>> return

      uniPaint paint = new uniPaint();

      //float stkWidth = paint.getStrokeWidth ();

      //paint background
      paint.setColorRGB  (uniColor.PAPER_INK_COLOR);
      canvas.drawRect (new uniRect (0, 0, tela_dx, tela_dy), paint);

      if (ONCE_ANTIALIASING)
      {
         paint.setAntiAlias(true);
         ONCE_ANTIALIASING = false;
      }
      paint.setColor  (COLOR_GRID_LINES);

      miMatha.set_dx_dy (tela_dx, tela_dy);
      miMatha.drawCoordenada (canvas, paint, true);
      miMatha.drawCoordenada (canvas, paint, false);

      //paint.setColor  (Color.WHITE);
      //paint.setAntiAlias(true);

      float x0 = miMatha.toPixelX (0.);
      float y0 = miMatha.toPixelY (0.);
      float dx = tela_dx;
      float dy = tela_dy;

      canvas.drawLine (0,  (int)y0, (int)dx, (int)y0, paint);
      canvas.drawLine ((int)x0,  0, (int)x0, (int)dy, paint);

      // canvas.drawLines (new float [] { 0.f, 0.f, dx, dy }, paint);

      paint.setColorRGB  (uniColor.WHITE);
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

         canvas.drawLine (fromx, fromy, tox, toy, paint);
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

//         paint.setColor(Color.WHITE);
//         canvas.drawLines (new float [] { 0.f, 0.f, 100.f, 100.f }, paint);
   }

////   public boolean onTouchEvent(MotionEvent event)
////   {
////      boolean t1 = zoomDetector.onTouchEvent(event);
////      if (!zoomDetector.gestureInProgress ())
////         t1 = trazaDetector.onTouchEvent(event);
////      return t1;
////   }

   /// implementing zoomTouchDetector.interested
//   public boolean onGestureStart (zoomTouchDetector detector)
//   {
//      miMatha.setReference4Gesture ();
//      detector.setRefOffsetScale (getLeft (), getTop (), (float) miMatha.minX, (float) miMatha.maxY, (float) miMatha.scaleX, (float) miMatha.scaleY);
//      detector.setRefOffsetScaleExtra ((float) miMatha.maxX, (float) miMatha.minY);
//      return true;
//   }
//
//   /// implementing zoomTouchDetector.interested
//   public boolean onGestureContinue (zoomTouchDetector detector)
//   {
//      android.util.Log.d ("soom", "GESTO CONT  p1_now " + printPar (detector.p1_now) + "  p2_now " + printPar (detector.p2_now));
//      detector.calcRectangles ();
//
//      if (zoomDetector.gestureInProgress ())
//      {
//         //miMatha.zoomRectangular (zoomDetector.rectP1, zoomDetector.rectP2, false);
//
//         zoomDetector.calcZoomNow (false);
//         miMatha.zoom (zoomDetector.nowOffsetX, zoomDetector.nowEndX,
//                       zoomDetector.nowOffsetY, zoomDetector.nowEndY);
//      }
//      //invalidate ();
//      postInvalidate ();
//      return true;
//   }

//////   /// implementing zoomTouchDetector.interested
//////   public void onGestureEnd (zoomTouchDetector detector, boolean cancel)
//////   {
//////      android.util.Log.d ("soom", "GESTO END  p1_fin " + printPar (detector.p1_fin) + "  p2_fin " + printPar (detector.p2_fin));
//////      ONCE_ANTIALIASING = true;
//////      postInvalidate ();
//////   }
//////
//////   /// implementing strokeDetector.interested
//////   public boolean onGestureStart (strokeDetector detector)
//////   {
//////      miMatha.setReference4Gesture ();
//////      return true;
//////   }
//////
//////   /// implementing strokeDetector.interested
//////   public boolean onGestureContinue (strokeDetector detector)
//////   {
//////      android.util.Log.d ("soom", "(Lin)GESTO CONT pos_now " + printPar (detector.pos_now));
//////      if (trazaDetector.gestureInProgress ())
//////      {
//////         miMatha.relativeTranslation (trazaDetector.pos_ini, trazaDetector.pos_now);
//////      }
//////      //invalidate ();
//////      postInvalidate ();
//////      return true;
//////   }
//////
//////   /// implementing strokeDetector.interested
//////   public void onGestureEnd (strokeDetector detector, boolean cancel)
//////   {
//////      android.util.Log.d ("soom", "(Lin)GESTO END  pos_fin " + printPar (detector.pos_fin));
//////      ONCE_ANTIALIASING = true;
//////      postInvalidate ();
//////   }

   public String printPar (vect3f vect)
   {
      if (vect != null)
         return "(" + vect.x + ", " + vect.y + ")";
      return "(null ..)";
   }


   private int pressedX = 0;
   private int pressedY = 0;

   private boolean leftButton = true;

   //public boolean onGestureStart (zoomTouchDetector detector)
   public void mousePressed (MouseEvent e)
   {
      pressedX = e.getX ();
      pressedY = e.getY ();

      leftButton = e.getButton() == MouseEvent.BUTTON1;

      miMatha.setReference4Gesture ();

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
//      if (!isGestureMode (sceneInMotion.MODE_GESTURE_DEFAULT)) return;

//      int inc = (int) (e.getX () - pressedX);
//      int incY = (int) (e.getY () - pressedY);
//      if (inc == 0) return;
//
//      if (leftButton)
//      {
//         // move
//         offsetX = offsetRefX + inc * scaleX;
//         offsetY = offsetRefY + incY * scaleY;
//      }
//      else
//      {
//         // zoom
//         int tela_dx = getWidth ();
//         scaleX = scaleRef * (1f + ((float) inc / ((float) tela_dx/3f)));
//         scaleY = scaleRef * (1f + ((float) inc / ((float) tela_dx/3f)));
//
//         //System.out.println ("CORCIAL! escalar " + scaleX);
//
////         offsetX = (int) detector.nowOffsetX;
////         offsetY = (int) detector.nowOffsetY;
//      }
//
      //render ();
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

}
