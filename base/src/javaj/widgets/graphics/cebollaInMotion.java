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
import de.elxala.math.space.curve.*;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;
import java.util.*;

/**

*/
public class cebollaInMotion
                implements  ISceneInMotion,
                            //zoomTouchDetector.interested,
                            multiFingerTouchDetector.interested
{
   protected static logger log = new logger (null, "javaj.widgets.graphics.cebollaInMotion", null);

   protected static final int MAX_FINGERS = 10;

   protected uniColor COLOR_GRID_LINES = new uniColor (255, 126, 0);
   protected uniColor EDIT_POINT_COLOR = new uniColor (0, 0, 0);
   protected uniColor DELETE_POINT_COLOR = new uniColor (255, 0, 0);

   public static int REDUCTION_TOLERANCE = 3;
   public static int MIN_DISTANCE_TWO_PTS = 1;
   public static int EDIT_POINT_RECT = 7;

   //public static final int MODO_NOSE = 0;
   public static final int MODO_TRAZA = 1;   // refe
   public static final int MODO_MODELA = 5;
   public static final int MODO_ELIMINA_PTOS = 6;

   public static final int TRAZA_EDITA = 2;
   public static final int TRAZA_REDUCE_EDITA = 3;
   public static final int TRAZA_REDUCE_TRAZA = 4;

   protected int modoActual = MODO_TRAZA;
   protected int trazaBehavior = TRAZA_REDUCE_TRAZA;
   protected int currentEditTrazo = -1;
   protected String defaultStyle = "";

   protected final int FINGER_ATTACH_WHOLE_TRAZO = -2;


   // gestures detectors/helpers
   //
   protected multiFingerTouchDetector pulpoDetector = null;

   public uniPath thePath = new uniPath ();

   public cebollaInMotion ()
   {
      pulpoDetector = new multiFingerTouchDetector (this);
   }

   protected offsetAndScale etherPortView = new offsetAndScale ();

   public void clear ()
   {
      thePath = new uniPath ();
      modoActual = MODO_TRAZA;
      currentEditTrazo = -1;
   }

   public void setTrazaBehavior (int kind)
   {
      trazaBehavior = kind;
   }

   public void setCurrentStyle (String strstyle)
   {
      if (modoActual != MODO_MODELA || currentEditTrazo == -1)
      {
         defaultStyle = strstyle;
         System.out.println ("STYLE 1set default style " + strstyle );
         return;
      }

      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      if (et != null)
      {
         System.out.println ("STYLE 2set style " + strstyle + " to current " + currentEditTrazo);
         et.style = strstyle;
         thePath.getEdiPaths ().setContentChanged ();
      }
      else
      {
         defaultStyle = strstyle;
         System.out.println ("STYLE 3set default style " + strstyle );
      }
   }

   public void setCurrentTrazoAndMode (int indx, int modo)
   {
      currentEditTrazo = indx;
      modoActual = modo;
   }

   public void setMode (int modo)
   {
      modoActual = modo;
   }

   public boolean incrementCurrentTrazo (int inc)
   {
      int maxIndx = thePath.getEdiPaths ().getTrazosSize ()-1;
      if (maxIndx < 0) return false;

      int nindx = currentEditTrazo + inc;
      if (nindx >= 0 && nindx <= maxIndx)
      {
         currentEditTrazo = nindx;
      }
      else currentEditTrazo = (inc > 0) ? maxIndx: 0;
      return true;
   }

   public boolean VER_ARREGLOS = false;

   public void renderUniCanvas (uniCanvas canvas, uniColor backgroundColor)
   {
      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      //clear background if any
      if (backgroundColor != null)
         canvas.fillRect (new uniRect (0, 0, tela_dx, tela_dy), backgroundColor);

      //System.out.println ("apply offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
      canvas.scale (etherPortView.scaleX, etherPortView.scaleY);
      canvas.translate (- etherPortView.offsetX, - etherPortView.offsetY);

         thePath.paintYou (canvas);

      
      //------
      //------
      //------
      if (! showEditPoints ()) return;

      if (VER_ARREGLOS)
      {
         polyAutoCasteljauPPT.arreglo = 0.0f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+200200255"));

         polyAutoCasteljauPPT.arreglo = .25f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+150150255"));

         polyAutoCasteljauPPT.arreglo = 0.5f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+100100255"));

         polyAutoCasteljauPPT.arreglo = 0.75f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+050050255"));

         polyAutoCasteljauPPT.arreglo = 0.999f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+000000255"));

         polyAutoCasteljauPPT.arreglo = -1.f;
      }
         styleObject editCurrent = styleGlobalContainer.getStyleObjectByName ("editCurrentTrazo");
         canvas.drawTrazoPath (thePath, currentEditTrazo, editCurrent);

         // dibujar puntos de current path
         //
         ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
         if (et != null)
         {
            uniPaint pai = new uniPaint ();
            //uniUtil.printLater ("punteando trazo " + currentEditTrazo);
            // // float xx = et.posX;
            // // float yy = et.posY;
            pai.setColor  (modoActual == MODO_ELIMINA_PTOS ? DELETE_POINT_COLOR: EDIT_POINT_COLOR);
            // // canvas.drawRect (new uniRect (true, xx, yy, EDIT_POINT_RECT, EDIT_POINT_RECT), pai);
            for (int pp = 0; pp < et.getPairsCount (); pp ++)
            {
               //System.out.println ("  rectangulito a " + xx + ", " + yy);
               canvas.drawRect (new uniRect (true, et.getPointAbsX(pp), et.getPointAbsY(pp), EDIT_POINT_RECT, EDIT_POINT_RECT), pai);
            }
         }
      }

   protected boolean showEditPoints ()
   {
      return currentEditTrazo != -1 && (modoActual == MODO_MODELA || modoActual == MODO_ELIMINA_PTOS);
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


   // ==========================================================
   // funciones que actuan sobre los paths editables
   //
   //       private void processFingers ()
   //       private void processDrawingShapes ()
   //       private void modelaCurrent ()
   //
   protected float lastDrawnX = 0;
   protected float lastDrawnY = 0;


   protected void processFingers ()
   {
      int nowFingers = pulpoDetector.getActiveFingersCount ();

      switch (modoActual)
      {
         case MODO_TRAZA:
            //System.out.println ("TRAZA champion!");
            processDrawingShapes ();
            break;
         case MODO_ELIMINA_PTOS:
            //System.out.println ("ELIMINAPTOS champion!");
         case MODO_MODELA:
            //System.out.println ("MODELLA champion!");
            modelaCurrent ();
            break;
         default: break;
       }
   }

   protected void processDrawingShapes ()
   {
      // only finger 0 is taken into account!
      //
      //System.out.println ("processEditingChains");
      fingerTouch fing = pulpoDetector.getFinger (0);
      if (fing == null) return;

      if (fing.isPressing ())
      {
         vect3f vec = fing.pNow != null ? fing.pNow: fing.pIni;
         if (currentEditTrazo != -1)
         {
            if (Math.abs(vec.x - lastDrawnX) > MIN_DISTANCE_TWO_PTS || 
                Math.abs(vec.y - lastDrawnY) > MIN_DISTANCE_TWO_PTS)
            {
               lastDrawnX = vec.x;
               lastDrawnY = vec.y;
               //System.out.println ("==TRAZO  anadimos " + vec.x + ", " + vec.y);
               thePath.getEdiPaths ().autoCasteljauPoint (vec.x, vec.y);
               //thePath.getEdiPaths ().lineTo (vec.x, vec.y);
            }
         }
         else
         {
            // esto crea un nuevo path en "thePath.getEdiPaths ()"
            currentEditTrazo = thePath.getEdiPaths ().startTrazoAt (vec.x, vec.y);
            lastDrawnX = vec.x;
            lastDrawnY = vec.y;
            //System.out.println ("==TRAZO " + currentEditTrazo + " punto move= " +  vec.x + ", " + vec.y);
         }
      }
      if (fing.isFinished ())
      {
         switch (trazaBehavior)
         {
            case TRAZA_EDITA:                 // traza y edita ... (primer modo implementado)
               modoActual = MODO_MODELA;
               break;
            case TRAZA_REDUCE_TRAZA:    // traza, reduce y inicia otra traza
               reduceCurrent ();
               setCurrentTrazoAndMode (-1, cebollaInMotion.MODO_TRAZA);
               break;
            case TRAZA_REDUCE_EDITA:    // traza, reduce y edita
               reduceCurrent ();
               modoActual = MODO_MODELA;
               break;
            default:
               break;
         }
         //System.out.println ("==TRAZO " + currentEditTrazo + " finished, MODO_MODELA chaSys.size () = " + thePath.getEdiPaths ().getTrazo (currentEditTrazo).getPairsCount ());
         //System.out.println ("==TRAZO " + currentEditTrazo + " dump");
         //System.out.println (thePath.getEdiPaths ().toString (currentEditTrazo));
      }
   }

   protected void modelaCurrent ()
   {
      int nFing = pulpoDetector.getActiveFingersCount ();
      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      int nPtos = 0;
      if (et != null)
         nPtos = et.getPairsCount ();

      if (nFing == 0 || nPtos == 0) return;
      if (nFing == 1 && pulpoDetector.getFinger (0).doubleTap ())
      {
         currentEditTrazo = -1;
         modoActual = MODO_TRAZA;
         //System.out.println ("==TRAZO  -1 MODO_TRAZA");
         return;
      }

      //System.out.println ("==TRAZO " + currentEditTrazo + " modela " + nPtos + " con " + nFing + " dedos");

      float [] fingerInfluenza = new float [nPtos];
      vect3f [] novaPos = new vect3f [nPtos];

      List newPoints = new Vector ();
      boolean modeDelete = modoActual == MODO_ELIMINA_PTOS;

      //System.out.println ("==ANTES DE MODELA " + currentEditTrazo + " dump");
      //System.out.println (thePath.getEdiPaths ().toString (currentEditTrazo));

      for (int ff = 0; ff < nFing; ff ++)
      {
         // process FINGER ff
         //
         fingerTouch fing = pulpoDetector.getFinger (ff);
         vect3f attachedPoint = (vect3f) fing.getAttachedObject ();
         if (attachedPoint == null || modeDelete)
         {
            // Finger not attached to a specific point yet
            // try to find closest point to the finger
            //
            uniRect fingSquare = new uniRect (true, fing.getLastPosition ().x, fing.getLastPosition ().y, 4 * EDIT_POINT_RECT, 4 * EDIT_POINT_RECT);

            for (int pp = 0; pp < et.getPairsCount (); pp ++)
            {
               vect3f pto = et.getPairAbsoluteAt (pp);
               pto.z = pp; // we store in z the point index!
               if (fingSquare.contains (pto.x, pto.y))
               {
                  if (modeDelete)
                  {
                     //System.out.println ("  dedo " + ff + " deletes the point with index "  + pp);
                     et.removePair (pp);
                     thePath.getEdiPaths ().setContentChanged ();
                  }
                  else
                  {
                     // attach the point to the finger for this and further movements
                     fing.setAttachedObject (pto);
                  }
                  break;
               }
            }
            if (modeDelete) break;
            attachedPoint = (vect3f) fing.getAttachedObject ();
            if (attachedPoint == null)
            {
               // still not attached ? then move the whole trazo, let's reserve the index -1 to the very first point
               fing.setAttachedObject (new vect3f (et.getPosX(), et.getPosY(), FINGER_ATTACH_WHOLE_TRAZO));
            }
         }
         if (modeDelete) continue;

         attachedPoint = (vect3f) fing.getAttachedObject ();
         if (attachedPoint == null) break; // should never happen...
         if ((int) attachedPoint.z == FINGER_ATTACH_WHOLE_TRAZO)
         {
            // FINGER ff is not attached, move the whole trazo
            //
            //System.out.println ("  dedo " + ff + " moves the whole trazo in dx, dy "  + fing.getDx () + ", " + fing.getDy ());
            //et.removePair(indx);
            et.setPosX (attachedPoint.x + (float) fing.getDx ());
            et.setPosY (attachedPoint.y + (float) fing.getDy ());
         }
         else if (attachedPoint.z >= 0)
         {
            // FINGER ff is attached to some point of the trazo, let's move the point to follow the finger
            //
            int indx = (int) attachedPoint.z;
            //System.out.println ("  dedo " + ff + " change punto " + indx + " in dx, dy "  + fing.getDx () + ", " + fing.getDy ());
            //et.removePair(indx);
            et.changePairAbs(indx, attachedPoint.x + (float) fing.getDx (), attachedPoint.y + (float) fing.getDy ());
         }
         // else ?

         thePath.getEdiPaths ().setContentChanged ();
     }

     //System.out.println ("==DESPUES DE MODELA " + currentEditTrazo + " dump");
     //System.out.println (thePath.getEdiPaths ().toString (currentEditTrazo));
   }

   public void reduceCurrent ()
   {
      ediTrazo current = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      if (current == null) return;

      ediTrazo redu = reduceCurrent (REDUCTION_TOLERANCE);
      if (redu == null) return;

      System.out.println ("STYLE reduceCurrent, me pongo el estilo " + redu.style);
      redu.style = defaultStyle;
      current.set (redu);
   }

   // return an ediTrazo with the reduced current drawing form or null if no current form
   //
   public ediTrazo reduceCurrent (float tolerance)
   {
      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      int nPtos = (et != null) ? et.getPairsCount (): 0;
      if (nPtos == 0) return null;
      
         pointReduction predo = new pointReduction (tolerance);

//uniUtil.printLater ("createReducedCurrent x y " + et.posX + ", " + et.posY + " + ptos " + nPtos);
         for (int ii = 0; ii < nPtos; ii ++)
            predo.addPoint (et.getPointX(ii), et.getPointY(ii));

      List redu = predo.reducePoints ();
      ediTrazo editredo = new ediTrazo (et.getPosX(), et.getPosY(), et.trazoForm);
      //uniUtil.printLater ("reducidos a " + redu.size ());

      if (et.getPointX(0) == 0.f && et.getPointY(0) == 0.f)
      {
      }
      else
      {
         System.err.println ("ERROR! unexpected first value on reduction reduceCurrent");
      }

      for (int ii = 1; ii < redu.size (); ii ++)
      {
         vect3f repo = (vect3f) redu.get(ii);
         editredo.addPairRel (repo.x, repo.y);
      }
      return editredo;
   }

   // reducedCurrent (float tolerance)
   public void createReducedCurrent (float tolerance)
   {
      ediTrazo redu = reduceCurrent (tolerance);
      if (redu == null) return;
      System.out.println ("STYLE reduceCurrent tolerance, me pongo el estilo " + redu.style);
      redu.style = defaultStyle;
      thePath.getEdiPaths ().arrEdiTrazos.add (redu);
      thePath.getEdiPaths ().setContentChanged ();
   }
}

//
// NOTE: the system of points used is an hybrid between relative to first point (in memory) and
//       relative to previous point (store in string)
//
//
// see bildID 11763 note
// 2013.05.30 15:47
//(o) DOC_Trazo filosofía de puntos relativos en trazo
//

/**
OLD MODELA

      for (int ff = 0; ff < nFing; ff ++)
      {
         // process FINGER ff
         //
         fingerTouch fing = pulpoDetector.getFinger (ff);
         vect3f [] attachedPoints = (vect3f []) fing.getAttachedObject ();
         if (attachedPoints == null)
         {
            // Finger not attached to a specific point yet
            // try to find closest point to the finger
            //
            attachedPoints = new vect3f [2];
            attachedPoints[0] = new vect3f(0.f, 0.f, -1.f);  // point affected x, y and z = index!
            // this finger is new, it hasn't an attached point, look for it
            //
            float xx = et.posX;
            float yy = et.posY;

            float mindist = 9999.f;
            float newposXmin = 0.f;
            float newposYmin = 0.f;

            for (int pp = 0; pp+1 < et.points.size (); pp += 2)
            {
               xx = et.posX + ((float []) et.points.get(pp))[0];
               yy = et.posY + ((float []) et.points.get(pp+1))[0];
               vect3f pto = new vect3f (xx, yy);

               float dinst = pto.distance (fing.getLastPosition ());
               if (dinst < mindist)
               {
                  mindist = dinst;
                  attachedPoints[0].set (xx, yy, pp/2); // in z we store the point index !!
               }
            }
            if (attachedPoints[0].z > 0.f)
            {
               int z = (int) attachedPoints[0].z;
               if (z > 0 && z+z+1 < et.points.size ())
               {
                   float v0x = ((float []) et.points.get(z+z-2))[0];
                   float v0y = ((float []) et.points.get(z+z-1))[0];
                   float v1x = ((float []) et.points.get(z+z))[0];
                   float v1y = ((float []) et.points.get(z+z+1))[0];
                   float prodVec = v0x * v1y - v0y * v1x;
                   System.out.println ("el attached point index " + attachedPoints[0].z + " tiene un producto vectorial de " + prodVec);
               }
               fing.setAttachedObject (attachedPoints);
            }
         }
         if (attachedPoints != null &&  attachedPoints[0].z  > 0.f)
         {
            // FINGER ff is attached to some point of the trazo, let's move the point to follow the finger
            //
            int indx = (int) attachedPoints[0].z;
            System.out.println ("  dedo " + ff + " change punto " + indx + " in dx, dy "  + fing.getDx () + ", " + fing.getDy ());
            //et.removePair(indx);
            et.changePairAbs(indx, attachedPoints[0].x + (float) fing.getDx (), attachedPoints[0].y + (float) fing.getDy ());
            thePath.getEdiPaths ().setContentChanged ();
         }
     }
*/