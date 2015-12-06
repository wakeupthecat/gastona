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

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.MotionEvent;

import javax.swing.JPanel;
import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.math.space.vect3f;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;


/**
   Native view able to handle native motion events (mouse)

 */
public class uniSceneInMotionView extends View
{
   private static logger log = new logger (null, "javaj.widgets.graphics.sceneInMotionView", null);

   public ISceneInMotion laEscena2 = null;
   public Bitmap backgroudBitmap = null;

   public uniSceneInMotionView (Context context)
   {
      super(context);
   }

   public void assignNewScene (ISceneInMotion theScene)
   {
      laEscena2 = theScene;
   }

   public void assignBackgroundBitmap (Bitmap bgrBitmap)
   {
      backgroudBitmap = bgrBitmap;
   }

   public void render ()
   {
      postInvalidate ();
      //paintImmediately (new Rectangle (0, 0, 3000, 3000));
   }

   public boolean onTouchEvent(MotionEvent event)
   {
      if (laEscena2 == null) return false;
      //log.dbg (4, "onTouchEvent", "MODE_GESTURE = " + gestureMode + " event action " + event.getAction() + " pointer count " + event.getPointerCount());

      uniMotion uEvent = new uniMotion (event);
      laEscena2.onUniMotion (uEvent);
      render ();
      return true;
   }

   public void onDraw(Canvas canvas)
   {
      if (laEscena2 == null) return;
      uniCanvas ges2 = new uniCanvas (canvas, getLeft(), getTop(), getWidth (), getHeight ());
      
      uniColor backCol = null;
      if (backgroudBitmap != null)
          canvas.drawBitmap (backgroudBitmap, null, new Rect(getLeft(), getTop(), getRight (), getBottom ()), null);
      else backCol = new uniColor(uniColor.LGRAY);
      
      // NOTE: scale and translation IS DONE IN Scene.render2D !!
      ////      ges2.getG ().translate (getOffsetX(), getOffsetY());
      ////      ges2.getG ().scale (getScaleX(), getScaleY());

      laEscena2.renderUniCanvas (ges2, backCol);
   }
}
