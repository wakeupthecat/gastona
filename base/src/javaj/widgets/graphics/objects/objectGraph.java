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

import java.util.List;
import java.util.Vector;

import javaj.widgets.graphics.*;

import de.elxala.mensaka.*;
import de.elxala.zServices.*;

/**
   handles several graphic objects, lines, paths etc that can be moved, scaled and selected
   as one item

   main  methods

      // to build the set
      //
      void addElement (paintableDimensionableElement ele)

      // to know if the given coordiante affects the set
      //
      boolean affectByPointer (float posx, float posy)


 */
public class objectGraph
{
   private static logger log = new logger (null, "javaj.widgets.graphics.objects.objectGraph", null);

   public String name = "noname?";
   public List objElements = new Vector(); // public to access it from the clickCtrl object
   public uniRect originalBounds = new uniRect ();
   public uniRect worldBounds = null; // to be recalculated
   private offsetAndScale currentPosAndScale  = new offsetAndScale ();
   private offsetAndScale changingPosAndScale = new offsetAndScale ();

   public movable   movil = new movable ();
   public expandable expander = new expandable ();
   public clickable clickCtrl = new clickable ();

   public boolean maskOtherMovements = false; // if object cannot move decide if mask other movements

   public void addElement (paintableDimensionableElement ele)
   {
      uniRect eleRec = new uniRect ();
      ele.getBounds (eleRec);
      originalBounds.union (eleRec);
      //(o)extra System.out.println  ("add Element to " + name + " with dimensions " + eleRec + " novas originalBounds " + originalBounds);

      objElements.add (ele);
   }

//   public setCurrentPosAndScale (float posx, float posy, float scalex, float scaley)
//   {
//      currentPosAndScale = new
//   }

   public void setCurrentPosAndScale (offsetAndScale posAndScale)
   {
      if (posAndScale != null)
         currentPosAndScale = posAndScale;
      worldBounds = null; // to be recalculated
   }

   public uniRect getWorldBounds ()
   {
      if (worldBounds == null)
         worldBounds = new uniRect (currentPosAndScale.offsetX + currentPosAndScale.scaleX * originalBounds.left (),
                                    currentPosAndScale.offsetY + currentPosAndScale.scaleY * originalBounds.top (),
                                    currentPosAndScale.offsetX + currentPosAndScale.scaleX * originalBounds.right (),
                                    currentPosAndScale.offsetY + currentPosAndScale.scaleY * originalBounds.bottom ());
      return worldBounds;
   }


   public paintableDimensionableElement getElement (int indx)
   {
      if (indx < 0 || indx >= objElements.size ())
         return null;
      return (paintableDimensionableElement) objElements.get (indx);
   }

   public float tempTranslationX ()
   {
      return currentPosAndScale.offsetX + changingPosAndScale.offsetX;
   }

   public float tempTranslationY ()
   {
      return currentPosAndScale.offsetY + changingPosAndScale.offsetY;
   }

   public float tempScaleX ()
   {
      return currentPosAndScale.scaleX * changingPosAndScale.scaleX;
   }

   public float tempScaleY ()
   {
      return currentPosAndScale.scaleY * changingPosAndScale.scaleY;
   }

   float inclinus = 0.03f;
   boolean PLANO_INCLINADO = false;

   public boolean affectByPointer (float posx, float posy)
   {
      //(o) TOREVIEW/android/javaj/touching Old comment, does it still applies ?
      //   //!!!if suspect it is not right calculated then uncomment this. but note that redundant calculations should be avoided!
      //   //worldBounds = null;
      uniRect wbo = getWorldBounds ();

      log.dbg (4, "affectByPointer", "point " + posx + ", " + posy + " the object " + name + " bounds " + wbo );

      if (!PLANO_INCLINADO && !maskOtherMovements && !movil.canMove () && !clickCtrl.canBeClicked ())
      {
         log.dbg (4, "affectByPointer", "maskOtherMovements is false and object cannot move or be clicked, therefore not affected");
         return false;
      }

      //(o) TOREVIEW/android/javaj/touching Old comment, does it still applies ?
      //   //TODO trabajar el aspectal ... scale para optima detección (segunda vez etc)
      //   //      if (originalBounds.contains (posx - currentPosAndScale.offsetX , posy - currentPosAndScale.offsetY))
      if (wbo.contains (posx, posy))
      {
         log.dbg (4, "affectByPointer", name + " affected!");
         changingPosAndScale.set (0f, 0f, 1f, 1f);

         if (clickCtrl.canBeClicked ())
            clickCtrl.pressIt (posx, posy);

         return true;
      }
      else log.dbg (4, "affectByPointer", name + " not affected ...");
      return false;
   }

   public boolean affectContinue (float despX, float despY)
   {
      log.dbg (4, "movingRelative", "object " + name + " affected moving incx, incy " + despX + ", " + despY);

      //(o) TOREVIEW/android/javaj/touching Old comment, does it still applies ?
      //   //!!!if suspect it is not right calculated then uncomment this. but note that redundant calculations should be avoided!
      //   //worldBounds = null;
      uniRect wbo = getWorldBounds ();

      float relX = despX / wbo.width ();
      float relY = despY / wbo.height ();

      if (PLANO_INCLINADO)
      {
         changingPosAndScale.set (despX, despY, 1f + despY * inclinus, 1f + despY * inclinus);
         return true;
      }
      if (clickCtrl.isPressed)
      {
         if (!wbo.contains (clickCtrl.Xpress + despX, clickCtrl.Ypress + despY))
         {
            clickCtrl.releaseIt ();
            return false;
         }
      }
      else if (movil.move (relX, relY))
      {
         changingPosAndScale.set (movil.currentDesplacementX * wbo.width (), movil.currentDesplacementY * wbo.height (), 1f, 1f);
         return true;
      }
      else if (expander.expand (relX, relY))
      {
         changingPosAndScale.set (0f, 0f, 1f, 1f);
         return true;
      }
      return false;
   }

   public void affectEnd ()
   {
      if (clickCtrl.isPressed)
      {
         log.dbg (2, "affectEnd", "graphic object [" + name + "] send a message \"graphicPress \"" + name + "\"");
         Mensaka.sendPacket ("graphicPress " + name, null);
         clickCtrl.releaseIt ();
      }

      setCurrentPosAndScale (new offsetAndScale (
                                currentPosAndScale.offsetX + changingPosAndScale.offsetX,
                                currentPosAndScale.offsetY + changingPosAndScale.offsetY,
                                currentPosAndScale.scaleX * changingPosAndScale.scaleX,
                                currentPosAndScale.scaleY * changingPosAndScale.scaleY
                              ));
      changingPosAndScale = new offsetAndScale ();
   }

   public boolean expandingRelative (float despX, float despY)
   {
      log.dbg (4, "expandingRelative", "object " + name + " affected expanding incx, incy " + despX + ", " + despY);

      return expander.expand (despX / originalBounds.width (), despY / originalBounds.height ());
   }

   public void paintYou (uniCanvas canvas)
   {
      float desX = tempTranslationX ();
      float desY = tempTranslationY ();
      log.dbg (2, "paintYou", "obj [" + name + "] temp translation " + desX + ", " + desY);

      float scaX = tempScaleX ();
      float scaY = tempScaleY ();
      log.dbg (2, "paintYou", "obj [" + name + "] temp scale " + scaX + ", " + scaY);

      canvas.translate (desX, desY);
      canvas.scale (scaX, scaY);

      List elements2Show = clickCtrl.isPressed ? clickCtrl.getPressObj ().objElements: objElements;

      //..redFrame.. canvas.drawRect (ob.originalBounds, paRojo);
      for (int ee = 0; ee < elements2Show.size (); ee ++)
      {
         paintableDimensionableElement grao = (paintableDimensionableElement) elements2Show.get (ee);
         grao.paintYou (canvas);
      }

      if (log.isDebugging (9))
      {
         //(o) object graphic frame for debug proposes
         canvas.drawRect (originalBounds, new uniPaint());
      }

      canvas.scale (1f/scaX, 1f/scaY);
      canvas.translate (-desX, -desY);
   }
}
