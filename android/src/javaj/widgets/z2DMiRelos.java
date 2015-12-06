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
import java.util.*;

public class z2DMiRelos extends uniSceneInMotionView
                        implements izWidget, MensakaTarget
{
   private final int RUN = 16;
   private final int STOP = 17;

   private basicAparato helper = null;
   private Bitmap backgBitmap = null;

   private relosInMotion miRelos = new relosInMotion ();

   private Timer tictac = null;
   private miTimer theTicTac = new miTimer ();
   
   public z2DMiRelos (Context co, String name)
   {
      super(co);
      assignNewScene (miRelos);
	  init (name);
      setName (name);
   }

   public void init (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      Mensaka.subscribe (this, RUN, map_name + " run");      
      Mensaka.subscribe (this, STOP, map_name + " stop");
   }

   class miTimer
   {
      public int periodMilli = 500;

      public void start ()
      {
         TimerTask tasca = new TimerTask ()
         {
            public void run ()
            {
               render ();
            }
         };

         tictac = new Timer ();
         tictac.scheduleAtFixedRate (tasca, 0, periodMilli);
      }

      public void stop ()
      {
         if (tictac != null)
            tictac.cancel ();
      }
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
                  "data", "z2DCebolla",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            loadAllData ();
            theTicTac.start ();
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "z2DCebolla",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());

            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         case RUN:
            theTicTac.start ();
            break;
            
         case STOP:
            theTicTac.stop ();
            break;


         default:
            return false;
      }

      return true;
   }

   public void loadAllData ()
   {
      // load background image if any
      String backgImgFile = helper.ebs ().getSimpleDataAttribute ("image");
      if (backgImgFile != null && backgImgFile.length () > 0)
           backgBitmap = BitmapFactory.decodeFile(backgImgFile);
      else backgBitmap = null;
      assignBackgroundBitmap (backgBitmap);

      miRelos.loadData (helper.ebs ().getName (), helper.ebs ().getData ());
   }
}

