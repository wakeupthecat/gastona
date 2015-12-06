/*
packages de.elxala
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

import de.elxala.math.space.vect3f;


/**
   @author Alejandro Xalabarder
   @date   2013
   @lastupdate   17.02.2013
*/
public class offsetAndScale
{
   public float offsetX = 0.f;
   public float offsetY = 0.f;
   public float scaleX = 1.f;
   public float scaleY = 1.f;
   
   public offsetAndScale ()
   {
   }
   
   public offsetAndScale (float oX, float oY, float sX, float sY)
   {
      set (oX, oY, sX, sY);
   }
   
   public void set (float oX, float oY, float sX, float sY)
   {
      offsetX = oX;
      offsetY = oY;
      scaleX = sX;
      scaleY = sY;
   }
   
   // adjust scale and offset to fit the image bounds in the given pixels
   // if pixels are 0 it means do not adjust in this dimension
   //
   public void autoZoom (uniRect imageBounds, int canvasPixelsX, int canvasPixelsY)
   {
      if (canvasPixelsX == 0 && canvasPixelsY == 0) return;

      // log.dbg (2, "autoZoom", "bounds are " + imageBounds.toString ());

      if (canvasPixelsX != 0 && imageBounds.width () > 0)
         scaleX =  (float) canvasPixelsX / (float) imageBounds.width ();

      if (canvasPixelsY != 0 && imageBounds.height () > 0)
         scaleY =  (float) canvasPixelsY / (float) imageBounds.height ();

      // adjust both at the same time, get the smallest one
      if (canvasPixelsX != 0 && canvasPixelsY != 0)
         if (scaleX * imageBounds.height() > canvasPixelsY)
              scaleX = scaleY;
         else scaleY = scaleX;

      // in fact translate
      offsetX = imageBounds.left ();
      offsetY = imageBounds.top ();

      //log.dbg (2, "checkAutoFit", "offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
   }

   
   public void autoZoom (uniRect imageBounds, uniRect canvasAreaToFit)
   {
      if (canvasAreaToFit.width () == 0 && canvasAreaToFit.height () == 0) return;
 
      // log.dbg (2, "autoZoom", "bounds are " + imageBounds.toString ());
 
      if (canvasAreaToFit.width() != 0 && imageBounds.width () > 0)
         scaleX =  (float) canvasAreaToFit.width() / (float) imageBounds.width ();
 
      if (canvasAreaToFit.height() != 0 && imageBounds.height () > 0)
         scaleY =  (float) canvasAreaToFit.height() / (float) imageBounds.height ();
 
      // adjust both at the same time, get the smallest one
      if (canvasAreaToFit.width () != 0 && canvasAreaToFit.height () != 0)
         if (scaleX * imageBounds.height() > canvasAreaToFit.height())
              scaleX = scaleY;
         else scaleY = scaleX;
 
         
      // in fact translate
      offsetX = canvasAreaToFit.left() / scaleX - imageBounds.left ();
      offsetY = canvasAreaToFit.top() / scaleY - imageBounds.top ();
      //System.out.println ("canvasAreaToFit.top() / scaleY - imageBounds.top () = " + canvasAreaToFit.top() + " / "  + scaleX + " - " + imageBounds.left () + " = " + offsetY);
 
      //log.dbg (2, "checkAutoFit", "offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
   }
   
   
   vect3f scaledToReal (vect3f point)
   {
      return new vect3f (point.x / scaleX + offsetX, point.y / scaleY + offsetY);
   }

   vect3f realToScaled (vect3f point)
   {
      return new vect3f ((point.x - offsetX) * scaleX, (point.y - offsetY) * scaleY);
   }

   public String toString ()
   {
      return "offset (" + offsetX + ", " + offsetY + ") scale (" + scaleX + ", " + scaleY + ")";
   }
}
