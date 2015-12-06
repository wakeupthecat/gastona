/*
package javaj.widgets.graphics;
Copyright (C) 2013 Alejandro Xalabarder Aulet

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
import de.elxala.math.space.curve.*;
import de.elxala.mensaka.*;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;
import java.util.*;
import de.elxala.Eva.*;
import de.elxala.Eva.abstractTable.baseEBS;

/**
   basic RelosInMotion

      - paths load manually
      - not editable
      - function touch activate pendulus if exists

*/
public class relosInMotion
                implements  ISceneInMotion,
                            //zoomTouchDetector.interested,
                            multiFingerTouchDetector.interested
{
   private static logger log = new logger (null, "javaj.widgets.graphics.relosInMotion", null);

   private static final int MAX_FINGERS = 10;

   // gestures detectors/helpers
   //
   private multiFingerTouchDetector pulpoDetector = null;

   protected baseEBS ebs = new baseEBS ("noname", new EvaUnit (), null);

   public uniPath backgroundShape = null;
   public uniPath hoursShape = null;
   public uniPath minutesShape = null;
   public uniPath secondsShape = null;

   public uniPath axisShape = null;
   public uniPath pendulusShape = null;
   public vect3f axisPendulus = null;
   public boolean pendulusActive = false;

   protected vect3f centerAxis = new vect3f ();

   protected float angleReposoHour   = 90.f; // (stdlib.atoi (timo.substring(0, 2)) % 12) * 30.f;
   protected float angleReposoMinute = 0.f;  //  stdlib.atoi (timo.substring(3, 5)) * 30.f;
   protected float angleReposoSecond = 0.f;  //  stdlib.atoi (timo.substring(6, 8)) * 30.f;

   public relosInMotion ()
   {
      pulpoDetector = new multiFingerTouchDetector (this);
   }

   public void clear ()
   {
      backgroundShape = new uniPath ();
      hoursShape = new uniPath ();
      minutesShape = new uniPath ();
      secondsShape = new uniPath ();
      axisShape = new uniPath ();
      pendulusShape = new uniPath ();

      axisPendulus =  null;
      centerAxis = new vect3f ();
   }

   public baseEBS getDataEbs ()
   {
      return ebs;
   }

   public void loadData (String name, EvaUnit euRelos)
   {
      clear ();

      ebs = new baseEBS (name, (euRelos != null ? euRelos: new EvaUnit ("data")), null);

      backgroundShape.getEdiPaths ().parseTrazosFromEva (ebs.getDataAttribute ("backGroundShape"));
      hoursShape     .getEdiPaths ().parseTrazosFromEva (ebs.getDataAttribute ("hoursShape"));
      minutesShape   .getEdiPaths ().parseTrazosFromEva (ebs.getDataAttribute ("minutesShape"));
      secondsShape   .getEdiPaths ().parseTrazosFromEva (ebs.getDataAttribute ("secondsShape"));
      axisShape      .getEdiPaths ().parseTrazosFromEva (ebs.getDataAttribute ("axisShape"));
      pendulusShape  .getEdiPaths ().parseTrazosFromEva (ebs.getDataAttribute ("pendulusShape"));
      axisPendulus    = null;

      // axis center
      //
      Eva axePos = ebs.getDataAttribute ("axisPosition");
      if (axePos != null)
      {
         centerAxis = new vect3f ((float) stdlib.atof (axePos.getValue (0,0)), (float) stdlib.atof (axePos.getValue (0,1)));
      }

      // reference postion given by time (e.g "03:00:00")
      //
      Eva timo = ebs.getDataAttribute  ("referenceTime");
      if (timo != null)
      {
         String timstr = timo.getValue (0); // hh:mm:ss
         if (timstr.length () >= 8)
         {
            angleReposoHour   = timo == null ? 90.f: (stdlib.atoi (timstr.substring(0, 2)) % 12) * 30.f;
            angleReposoMinute = timo == null ? 0.f: stdlib.atoi (timstr.substring(3, 5)) * 6.f;
            angleReposoSecond = timo == null ? 0.f: stdlib.atoi (timstr.substring(6, 8)) * 6.f;
         }
      }
      else
      {
         angleReposoHour   = 90.f;
         angleReposoMinute = 0.f;
         angleReposoSecond = 0.f;
      }

      // alternatively another way of indicate reference time
      //   <referencePostions> axisX, axisY, endHourX, endHourY, endMinuteX, ...
      //
      Eva initPos = ebs.getDataAttribute ("referencePositions");
      if (initPos != null)
      {
         vect3f dir12Oclock = new vect3f (0, -1, 0); // 0h = y=-1 because "y" axis is downwards!
         vect3f endPos = new vect3f ();
         vect3f reposo = new vect3f ();        
         
         centerAxis = new vect3f ((float) stdlib.atof (initPos.getValue (0,0)), (float) stdlib.atof (initPos.getValue (0,1)));

         if (initPos.cols (0) >= 4)
         {
            // endHour
            endPos = new vect3f ((float) stdlib.atof (initPos.getValue (0,2)), (float) stdlib.atof (initPos.getValue (0,3)));
            reposo = new vect3f (centerAxis, endPos);
            angleReposoHour = reposo.angleDegrees (dir12Oclock);
         }
         
         if (initPos.cols (0) >= 6)
         {
            // endMinute
            endPos = new vect3f ((float) stdlib.atof (initPos.getValue (0,4)), (float) stdlib.atof (initPos.getValue (0,5)));
            reposo = new vect3f (centerAxis, endPos);
            angleReposoMinute = reposo.angleDegrees (dir12Oclock);
         }
         
         if (initPos.cols (0) >= 8)
         {
            // endSecond
            endPos = new vect3f ((float) stdlib.atof (initPos.getValue (0,6)), (float) stdlib.atof (initPos.getValue (0,7)));
            reposo = new vect3f (centerAxis, endPos);
            angleReposoSecond = reposo.angleDegrees (dir12Oclock);
         }
      }
   }

   private String uniPathContentsOrEmpty (uniPath upa)
   {
      if (upa == null) return "";
      return upa.getEdiPaths ().toString ();
   }

   // save data from editables schapes to baseEBS object (text form)
   //
   public void saveData ()
   {
      ebs = new baseEBS (ebs.getName (), new EvaUnit ("data"), null);
      if (backgroundShape != null)
         backgroundShape.getEdiPaths ().dumpIntoEva (ebs.getEnsureDataAttribute ("backGroundShape"));
      if (hoursShape != null)
         hoursShape.getEdiPaths ().dumpIntoEva (ebs.getEnsureDataAttribute ("hoursShape"));
      if (minutesShape != null)
         minutesShape.getEdiPaths ().dumpIntoEva (ebs.getEnsureDataAttribute ("minutesShape"));
      if (secondsShape != null)
         secondsShape.getEdiPaths ().dumpIntoEva (ebs.getEnsureDataAttribute ("secondsShape"));
      if (axisShape != null)
         axisShape.getEdiPaths ().dumpIntoEva (ebs.getEnsureDataAttribute ("axisShape"));

      // Eva ref = ebs.getEnsureDataAttribute ("referenceTime");
      // ref.setValue ("03:00:00"); // 3 h 0 min 0 sec
   }

   private static float phaseAngles [] = new float [] { 0.f, 4.f, 0.f, -4.f };
   private int phase = 0;
   private offsetAndScale ether = new offsetAndScale ();

   public void renderUniCanvas (uniCanvas canvas, uniColor backgroundColor)
   {
      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      //clear background if any
      if (backgroundColor != null)
         canvas.fillRect (new uniRect (0, 0, tela_dx, tela_dy), backgroundColor);

      // auto scale according to background
      //
      ether.autoZoom (backgroundShape.mEdiPaths.getBounds (), tela_dx, tela_dy);
      //System.out.println ("apply offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
      canvas.scale (ether.scaleX, ether.scaleY);
      canvas.translate (- ether.offsetX, - ether.offsetY);

      String timo =  DateFormat.getStr (new Date (), "HH:mm:ss");

      // 360 / 12 = 30°
      //
      float angleHour   = (stdlib.atoi (timo.substring(0, 2)) % 12) * 30.f;
      float angleMinute = stdlib.atoi (timo.substring(3, 5)) * 6.f;
      float angleSecond = stdlib.atoi (timo.substring(6, 8)) * 6.f;

      // add the minute advance
      angleHour += angleMinute / 12.f;

      //System.out.println ("time " + timo +  " angles : hour = " + angleHour + "  minute = " + angleMinute + " second = " + angleSecond);

      if (backgroundShape != null)
         backgroundShape.paintYou (canvas);

      boolean hasAxis = centerAxis != null;
      
      angleHour -= angleReposoHour;
      if (hasAxis) canvas.rotate (angleHour, centerAxis.x, centerAxis.y);
      hoursShape.paintYou (canvas);
      if (hasAxis) canvas.rotate (-angleHour, centerAxis.x, centerAxis.y);

      angleMinute -= angleReposoMinute;
      if (hasAxis) canvas.rotate (angleMinute, centerAxis.x, centerAxis.y);
      minutesShape.paintYou (canvas);
      if (hasAxis) canvas.rotate (-angleMinute, centerAxis.x, centerAxis.y);
      
      angleSecond -= angleReposoSecond;
      if (hasAxis) canvas.rotate (angleSecond, centerAxis.x, centerAxis.y);
      secondsShape.paintYou (canvas);
      if (hasAxis) canvas.rotate (-angleSecond, centerAxis.x, centerAxis.y);
      
      if (pendulusShape != null)
      {
         if (axisPendulus == null)
         {
            uniRect bo = pendulusShape.getBounds ();
            axisPendulus = new vect3f (bo.left (), bo.centerX ());
         }
         //System.out.println ("pendulus phase = " + phase + "  angle = " + phaseAngles[phase]);
         if (pendulusActive) canvas.rotate (phaseAngles[phase], axisPendulus.x, axisPendulus.y);
         pendulusShape.paintYou (canvas);
         if (pendulusActive) canvas.rotate (-phaseAngles[phase], axisPendulus.x, axisPendulus.y);
         phase = (phase + 1) % 4;
      }

      if (axisShape != null)
         axisShape.paintYou (canvas);
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
      return pulpoDetector.onTouchEvent(uniEvent);
   }

   // ==========================================================
   // implementing multiTouchDetector.interested
   //
   public void onFingerDown (multiFingerTouchDetector detector, int fingerIndx)
   {
      processFingers ();

      if (fingerIndx < 0 || fingerIndx >= detector.mFingers.length) return;
      fingerTouch dedo = detector.mFingers[fingerIndx];
      vect3f punt = ether.scaledToReal (dedo.pIni);

      if (pendulusShape != null)
      {
         //System.out.println ("fingerIndx " + fingerIndx);
         uniRect bo = pendulusShape.getBounds ();
         //System.out.println ("dedo ini " + dedo.pIni + " xx, yy = " + punt.x + ", " + punt.y + " reco " + bo);
         if (bo.pointInside (punt.x, punt.y))
         {
            //System.out.println (" INSIDE!! suerte!");
            pendulusActive = !pendulusActive;
            return;
         }
         //else System.out.println (" OUTSIDE!! peich");
      }

      // temporary workaround: relosInMotion does not know the object name!
      Mensaka.sendPacket ("javaj notHandledTap", null, new String [] { "" + punt.x, "" + punt.y } );
   }

   public void onFingerUp (multiFingerTouchDetector detector, int fingerIndx)
   {
      processFingers ();
   }

   public void onMovement (multiFingerTouchDetector detector)
   {
      processFingers ();
   }

   public void onGestureEnd (multiFingerTouchDetector detector, boolean cancel)
   {
      processFingers ();
   }

   private void processFingers ()
   {
      int nowFingers = pulpoDetector.getActiveFingersCount ();
   }
}
