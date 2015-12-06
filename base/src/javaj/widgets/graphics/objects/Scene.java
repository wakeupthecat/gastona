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
package javaj.widgets.graphics.objects;


import javaj.widgets.graphics.*;

import java.util.List;
import java.util.Vector;

import de.elxala.math.space.*;
import de.elxala.zServices.*;

import javaj.widgets.gestures.*;

public class Scene
{
   private static logger log = new logger (null, "javaj.widgets.graphics.objects.Scene", null);

   public List arrEscena = new Vector ();             //vector<objectGraph>

   // pointers to current objects beeing changing
   //
   public objectGraph [] currentObjChanging = new objectGraph[multiFingerTouchDetector.MAX_POINTERS];

   public Scene ()
   {
      clearGraphic ();
   }

   public void clearGraphic ()
   {
      arrEscena = new Vector();
      ETHER_affected = false;
      currentObjChanging = new objectGraph[multiFingerTouchDetector.MAX_POINTERS];
      for (int ii = 0; ii < currentObjChanging.length; ii ++)
         currentObjChanging[ii] = null;
   }

   public void addObject (objectGraph obj)
   {
      arrEscena.add (obj);
   }
   
   public uniRect bounds ()
   {
      uniRect bounder = null;

      if (log.isDebugging (4))
         log.dbg (4, "bounds", "Escena " + arrEscena);
//System.out.println ("bounds::Escena " + arrEscena);
      if (arrEscena != null && arrEscena.size () > 0)
      {
         if (log.isDebugging (4))
            log.dbg (4, "bounds", "Escena with " + arrEscena.size () + " elements");
//System.out.println ("bounds::Escena with " + arrEscena.size () + " elements");
         for (int ss = 0; ss < arrEscena.size (); ss ++)
         {
            if (log.isDebugging (4))
               log.dbg (4, "bounds", "object #" + ss);
//System.out.println ("bounds::Escena object #" + ss);
            objectGraph ob = (objectGraph) arrEscena.get(ss);
            if (bounder == null)
               bounder = new uniRect (ob.originalBounds);
            else
               bounder.union (ob.originalBounds);
          }
      }

      return bounder != null ? bounder: new uniRect ();
   }


   public void draw(uniCanvas canvas)
   {
      //..redFrame.. uniPaint paRojo = new uniPaint ();
      //..redFrame.. paRojo.setColor (new uniColor(uniColor.RED));
      if (arrEscena != null && arrEscena.size () > 0)
      {
         log.dbg (2, "draw", "Escene of size " + arrEscena.size ());
         for (int ss = 0; ss < arrEscena.size (); ss ++)
         {
            objectGraph ob = (objectGraph) arrEscena.get(ss);
            ob.paintYou (canvas);
         }
      }
      else log.dbg (2, "draw", "Empty scene, do nothing");
   }

   //private float refOffX = 0.f;
   //private float refOffY = 0.f;
   public float refScaleX = 1.f;
   private float refScaleY = 1.f;
   
   //2013.01.27
   // concept of "Ether" to move the whole scene in the screen
   // only if any other object is affected or mask the movement of other object!
   //
   public float ETHER_pressX = 0.f;
   public float ETHER_pressY = 0.f;
   public float ETHER_desplazaX = 0.f;
   public float ETHER_desplazaY = 0.f;   
   public boolean ETHER_affected = false;

   public boolean objectsGestureStart (vect3f posIni, int indxFinger, float scaleX, float scaleY, float offsetX, float offsetY)
   {
      float normX = posIni.x / scaleX + offsetX;
      float normY = posIni.y / scaleY + offsetY;

      // detect start of gesture in any object
      for (int ii = arrEscena.size ()-1; ii >= 0; ii --)
      {
         objectGraph o = (objectGraph) arrEscena.get(ii);

         if (o.affectByPointer (normX, normY))
         {
            log.dbg (4, "onGestureStart(strokeDetector)", "object " + o.name + " start gesture");
            currentObjChanging[indxFinger] = o;

            refScaleX = scaleX;
            refScaleY = scaleY;
            //refOffX = offsetX;
            //refOffY = offsetY;
            ETHER_affected = false;
            return true;
         }
      }
      refScaleX = scaleX;
      refScaleY = scaleY;
      ETHER_pressX = normX;
      ETHER_pressY = normY;
      ETHER_desplazaX = 0.f;
      ETHER_desplazaY = 0.f;
      ETHER_affected = true;
      return false;
   }

   public boolean objectsGestureContinue (fingerTouch [] fingers)
   {
      boolean someoneMoved = false;
      float lastDesplazaX = 0.f;
      float lastDesplazaY = 0.f;
      
      for (int ii = 0; ii < fingers.length; ii ++)
      {
         if (fingers[ii].isPressing ())
         {
            if (ii > currentObjChanging.length)
            {
               log.err ("objectsGestureContinue", "finger index = " + ii + " but objChanging size = " + currentObjChanging.length);
               continue;
            }
            lastDesplazaX = fingers[ii].getDx ();
            lastDesplazaY = fingers[ii].getDy ();
            objectGraph obj = currentObjChanging[ii];
            if (obj != null)
            {
               obj.affectContinue (lastDesplazaX / refScaleX, lastDesplazaY / refScaleY);
               someoneMoved = true;
            }
         }
      }
      if (!someoneMoved && ETHER_affected)
      {
         ETHER_desplazaX = lastDesplazaX / refScaleX;
         ETHER_desplazaY = lastDesplazaY / refScaleY;
      }
      return someoneMoved;
   }

   public void objectsGestureEnd (int indxFinger)
   {
      objectGraph obj = currentObjChanging[indxFinger];
      if (obj != null)
      {
         log.dbg (4, "objectsGestureEnd(indxFinger)", "object " + obj.name + " end affected");
         obj.affectEnd ();
      }
      currentObjChanging[indxFinger] = null;
   }

   public void objectsGestureEnd ()
   {
      for (int ii = 0; ii < currentObjChanging.length; ii ++)
      {
         objectGraph obj = currentObjChanging[ii];
         if (obj != null)
         {
            log.dbg (4, "onGestureContinue(strokeDetector)", "object " + obj.name + " end affected");
            obj.affectEnd ();
         }
         currentObjChanging[ii] = null;
      }
      ETHER_affected = false;
   }
}