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

public class Scene
{
   private static logger log = new logger (null, "javaj.widgets.graphics.objects.Scene", null);

   public List laEscena = new Vector ();             //vector<objectGraph>

   // pointers to current objects beeing changing
   //
   public objectGraph [] currentObjChanging = new objectGraph[5];

   private objectGraph currentObject = null;

   public void clearGraphic ()
   {
      laEscena = new Vector();
      currentObject = null;
   }

   public void addObject (String name)
   {
      addObject (name, "1111", null);
   }

   public void addObject (String name, String basicMov, uniRect posScale)
   {
      log.dbg (2, "addObject", name);
      currentObject = new objectGraph ();
      currentObject.name = name;
      currentObject.movil.setBasicMovement (basicMov);
      laEscena.add (currentObject);
   }

   public void addObject (String name, movable movil, expandable expander)
   {
      log.dbg (2, "addObject", name + " movil " + movil.toString ());
      currentObject = new objectGraph ();
      currentObject.name = name;
      currentObject.movil = movil;
      currentObject.expander = expander;
      laEscena.add (currentObject);
   }

   public void addElement (paintableDimensionableElement ele)
   {
      if (currentObject == null)
      {
         log.err ("addElement", "adding an element with no object (no addObject before!)"    + this);
         return;
      }
      currentObject.addElement (ele);
   }

   public uniRect bounds ()
   {
      uniRect bounder = null;

      if (laEscena != null && laEscena.size () > 0)
      {
         for (int ss = 0; ss < laEscena.size (); ss ++)
         {
            objectGraph ob = (objectGraph) laEscena.get(ss);
            for (int ee = 0; ee < ob.objElements.size (); ee ++)
            {
               if (bounder == null)
                    bounder = new uniRect (ob.originalBounds);
               else bounder.union (ob.originalBounds);
            }
          }
      }

      return bounder != null ? bounder: new uniRect ();
   }


   public void draw(uniCanvas canvas)
   {
      if (laEscena != null && laEscena.size () > 0)
      {
         log.dbg (2, "draw", "Escene of size " + laEscena.size ());
         for (int ss = 0; ss < laEscena.size (); ss ++)
         {
            objectGraph ob = (objectGraph) laEscena.get(ss);
            float desX = ob.tempTranslationX ();
            float desY = ob.tempTranslationY ();

            log.dbg (2, "draw", "obj [" + ob.name + "] temp translation " + desX + ", " + desY);
            canvas.translate (desX, desY);
            // theCanvas.drawRect (ob.originalBounds, paRojo);
            for (int ee = 0; ee < ob.objElements.size (); ee ++)
            {
               paintableDimensionableElement grao = (paintableDimensionableElement) ob.objElements.get (ee);
               grao.paintYou (canvas);
            }
            canvas.translate (-desX, -desY);
         }
      }
      else log.dbg (2, "draw", "Empty scene, do nothing");
   }

   //private float refOffX = 0.f;
   //private float refOffY = 0.f;
   private float refScaleX = 1.f;
   private float refScaleY = 1.f;

   public boolean objectsGestureStart (vect3f [] arr_posIni, float scaleX, float scaleY, float offsetX, float offsetY)
   {
      if (arr_posIni.length == 0) return false;

      float normX = arr_posIni[0].x / scaleX - offsetX;
      float normY = arr_posIni[0].y / scaleY - offsetY;

      // detect start of gesture in any object
      for (int ii = laEscena.size ()-1; ii >= 0; ii --)
      {
         objectGraph o = (objectGraph) laEscena.get(ii);

         if (o.affectByPointer (normX, normY))
         {
            log.dbg (4, "onGestureStart(strokeDetector)", "object " + o.name + " start gesture");
            currentObjChanging[0] = o;
            currentObjChanging[1] = null;

            refScaleX = scaleX;
            refScaleY = scaleY;
            //refOffX = offsetX;
            //refOffY = offsetY;
            return true;
         }
      }
      return false;
   }

   public void objectsGestureContinue (vect3f [] arr_posIni, vect3f [] arr_posNow)
   {
      objectGraph obj = currentObjChanging[0];
      if (obj != null && arr_posIni.length > 0 && arr_posNow.length > 0)
      {
         float desplazaX = (arr_posNow[0].x - arr_posIni[0].x) / refScaleX;
         float desplazaY = (arr_posNow[0].y - arr_posIni[0].y) / refScaleY;

         obj.affectContinue (desplazaX, desplazaY);
      }
   }

   public void objectsGestureEnd ()
   {
      objectGraph obj = currentObjChanging[0];
      if (obj != null)
      {
         log.dbg (4, "onGestureContinue(strokeDetector)", "object " + obj.name + " end affected");
         obj.affectEnd ();
      }
      currentObjChanging[0] = null;
   }

}