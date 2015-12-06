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


/**
 * Detects the typical zoom multitouch pinch and spread gesture
 *
 */
public class gestureGestor implements MouseListener, MouseMotionListener
{
   public static final int MODE_GESTURE_NONE = 0;
   public static final int MODE_GESTURE_DEFAULT = 1;
   public static final int MODE_GESTURE_OBJECTS = 2;
   public static final int MODE_GESTURE_MESSAGING = 3;


   private int currentMode = MODE_GESTURE_DEFAULT;
   private zoomTouchDetector.interested  mInterested_Zoom = null;
   private strokeDetector.interested     mInterested_Stroke = null;
   private displaceDetector.interested   mInterested_Displace = null;
   private multiTouchDetector.interested mInterested_MultiTouch = null;


//   public gestureGestor (
//                         zoomTouchDetector.interested pZoom,
//                         strokeDetector.interested    pStroke,
//                         multiTouchDetector.interested pMulti)
//   {
//      setGestureInterests (pZoom, pDisplace, pStroke, pMulti);
//      mInterested_Zoom = pZoom;
//      mInterested_Stroke = pStroke;
//      mInterested_MultiTouch = pMulti;
//   }

   public setGestureInterests (
            zoomTouchDetector.interested pZoom,
            displaceDetector.interested  pDisplace,
            strokeDetector.interested    pStroke,
            multiTouchDetector.interested pMulti)
   {
      mInterested_Zoom = new pZoom;
      mInterested_Displace = pDisplace;
      mInterested_Stroke = pStroke;
      mInterested_MultiTouch = pMulti;
   }

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

      if (mInterested_Zoom != null)
      {
      }

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
}
