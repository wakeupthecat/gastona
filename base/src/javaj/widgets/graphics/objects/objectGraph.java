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

import de.elxala.zServices.*;

/**
 */
public class objectGraph
{
   private static logger log = new logger (null, "javaj.widgets.graphics.objects.objectGraph", null);

   public String name = "noname?";
   protected List objElements = new Vector();
   public uniRect originalBounds = new uniRect ();
   public uniRect currentPosAndScale  = new uniRect (0f, 0f, 1f, 1f);
   public uniRect changingPosAndScale = new uniRect (0f, 0f, 1f, 1f);

   public movable   movil = new movable ();
   public expandable expander = new expandable ();

   public boolean maskOtherMovements = false; // if object cannot move decide if mask other movements

   public void addElement (paintableDimensionableElement ele)
   {
      if (objElements.size () == 0)
           ele.getBounds (originalBounds);
      else ele.unionBounds (originalBounds);

      objElements.add (ele);
   }

   public float tempTranslationX ()
   {
      return currentPosAndScale.left() + changingPosAndScale.left();
   }

   public float tempTranslationY ()
   {
      return currentPosAndScale.top() + changingPosAndScale.top();
   }

   public boolean affectByPointer (float posx, float posy)
   {
      log.dbg (4, "affectByPointer", "point " + posx + ", " + posy + " the object " + name + " bounds " + originalBounds + " currentPosAndScale " + currentPosAndScale);

      if (!maskOtherMovements && !movil.canMove ())
      {
         log.dbg (4, "affectByPointer", "maskOtherMovements is false and object cannot move, therefore not affected");
         return false;
      }

      if (originalBounds.contains (posx - currentPosAndScale.left(), posy - currentPosAndScale.top()))
      {
         log.dbg (4, "affectByPointer", "affected!");
         changingPosAndScale.set (0f, 0f, 1f, 1f);
         return true;
      }
      return false;
   }

   public boolean affectContinue (float despX, float despY)
   {
      log.dbg (4, "movingRelative", "object " + name + " affected moving incx, incy " + despX + ", " + despY);

      float relX = despX / originalBounds.width ();
      float relY = despY / originalBounds.height ();

      if (movil.move (relX, relY))
      {
         changingPosAndScale.set (movil.currentDesplacementX * originalBounds.width (), movil.currentDesplacementY * originalBounds.height (), 1f, 1f);
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
      currentPosAndScale.set (currentPosAndScale.left() + changingPosAndScale.left(),
                              currentPosAndScale.top() + changingPosAndScale.top(),
                              currentPosAndScale.right() * changingPosAndScale.right(), // expand x factor!
                              currentPosAndScale.bottom() * changingPosAndScale.bottom() // expand y factor!
                              );
      changingPosAndScale.set (0f, 0f, 1f, 1f);
   }

   public boolean expandingRelative (float despX, float despY)
   {
      log.dbg (4, "expandingRelative", "object " + name + " affected expanding incx, incy " + despX + ", " + despY);

      return expander.expand (despX / originalBounds.width (), despY / originalBounds.height ());
   }
}
