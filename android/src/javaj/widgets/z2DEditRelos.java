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

package javaj.widgets;

import android.graphics.*;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.graphics.drawable.Drawable;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.math.space.*;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;

public class z2DEditRelos extends uniSceneInMotionView
                        implements izWidget, MensakaTarget
{
   private final int DUMP = 16;
   private final int EDIT_TRAZO = 17;
   private final int INC_TRAZO = 18;
   private final int ADD_POINTS_TRAZO = 19;
   private final int NEW_TRAZO = 20;
   private final int DEL_POINTS = 21;
   private final int REDUCE_POINTS = 22;
   private final int SET_STYLE = 23;
   private final int CLEAR = 24;

   private basicAparato helper = null;
   private Bitmap backgBitmap = null;

   private cebollaClockInMotion miCebolla = new cebollaClockInMotion ();

   public z2DEditRelos (Context co, String name)
   {
      super(co);
      assignNewScene (miCebolla);
      init (name);
      setName (name);
   }

   public void init (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));
   }

   //-i- interface iWidget
   //
   public int getDefaultHeight ()
   {
      //if (theDrawable != null) return theDrawable.getIntrinsicHeight ();
      return 100;
   }

   public int getDefaultWidth ()
   {
      //if (theDrawable != null) return theDrawable.getIntrinsicWidth ();
      return 100;
   }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String pname)
   {
      mName = pname;
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "z2DEditRelos",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            //loadAllData ();
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "z2DEditRelos",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());

            String vale = "";

            //vale = helper.ebs ().getSimpleDataAttribute ("MIN_DISTANCE_TWO_PTS");
            //if (vale != null) miCebolla.MIN_DISTANCE_TWO_PTS = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("EDIT_POINT_RECT");
            if (vale != null) miCebolla.EDIT_POINT_RECT  = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("REDUCTION_TOLERANCE");
            if (vale != null) miCebolla.REDUCTION_TOLERANCE  = stdlib.atoi (vale);

            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         default:
            return false;
      }

      return true;
   }
}
