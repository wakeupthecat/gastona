/*
package javaj.widgets.graphics;
Copyright (C) 2011-2015 Alejandro Xalabarder Aulet

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

public class z2DCebolla extends uniSceneInMotionView
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
   private final int FLUSH = 25;
   private final int FLUSH_TRAZO = 26;
   private final int FLUSH_JS = 27;
   private final int SET_REDUCE_ALGORITHM = 28;

   private basicAparato helper = null;
   private Bitmap backgBitmap = null;

   private cebollaInMotion miCebolla = new cebollaInMotion ();

   public z2DCebolla (Context co, String name)
   {
      super(co);
      assignNewScene (miCebolla);
      init (name);
      setName (name);
   }

   public void init (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      Mensaka.subscribe (this, FLUSH, map_name + " flush");
      Mensaka.subscribe (this, FLUSH_JS, map_name + " flushTrazosJs");
      Mensaka.subscribe (this, FLUSH_TRAZO, map_name + " flushTrazos");
      Mensaka.subscribe (this, DUMP, map_name + " dump");
      Mensaka.subscribe (this, INC_TRAZO, map_name + " incCurrentTrazo");
      Mensaka.subscribe (this, EDIT_TRAZO, map_name + " editTrazo");
      Mensaka.subscribe (this, ADD_POINTS_TRAZO, map_name + " addPointsTrazo");
      Mensaka.subscribe (this, NEW_TRAZO, map_name + " newTrazo");
      Mensaka.subscribe (this, DEL_POINTS, map_name + " modeDelPoints");
      Mensaka.subscribe (this, REDUCE_POINTS, map_name + " reducePoints");
      Mensaka.subscribe (this, SET_STYLE, map_name + " setStyle");
      Mensaka.subscribe (this, CLEAR, map_name + " clear");
      Mensaka.subscribe (this, SET_REDUCE_ALGORITHM, map_name + " setReduceAlgorithm");
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

   private int getPar1Int (String [] pars, int defaultValue)
   {
      return (pars == null || pars.length == 0) ? defaultValue: stdlib.atoi (pars[0]);
   }

   private String getPar1Str (String [] pars, String defaultValue)
   {
      return (pars == null || pars.length == 0) ? defaultValue: pars[0];
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

            String vale = "";

            vale = helper.ebs ().getSimpleDataAttribute ("MIN_DISTANCE_TWO_PTS");
            if (vale != null) miCebolla.MIN_DISTANCE_TWO_PTS = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("EDIT_POINT_RECT");
            if (vale != null) miCebolla.EDIT_POINT_RECT  = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("REDUCTION_TOLERANCE");
            if (vale != null) miCebolla.REDUCTION_TOLERANCE  = stdlib.atoi (vale);

            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         case EDIT_TRAZO:
            miCebolla.setCurrentTrazoAndMode (getPar1Int (pars, 0), cebollaInMotion.MODO_MODELA);
            render ();
            break;

         case INC_TRAZO:
            miCebolla.incrementCurrentTrazo (getPar1Int (pars, 1));
            miCebolla.setMode (cebollaInMotion.MODO_MODELA);
            render ();
            break;

         case ADD_POINTS_TRAZO:
            miCebolla.setCurrentTrazoAndMode (getPar1Int (pars, 0), cebollaInMotion.MODO_TRAZA);
            render ();
            break;

         case NEW_TRAZO:
            miCebolla.setCurrentTrazoAndMode (-1, cebollaInMotion.MODO_TRAZA);
            render ();
            break;

         case DEL_POINTS:
            miCebolla.setMode (cebollaInMotion.MODO_ELIMINA_PTOS);
            render ();
            break;

         case REDUCE_POINTS:
            miCebolla.createReducedCurrent (getPar1Int (pars, 0));
            render ();
            break;

         case SET_REDUCE_ALGORITHM:
            //?? miCebolla.setReduceAlgorithm (getPar1Str (pars, ""));
            //?? render ();
            break;

         case SET_STYLE:
            miCebolla.setCurrentStyle (getPar1Str (pars, ""));
            render ();
            break;

         case CLEAR:
            miCebolla.clear ();
            render ();
            break;

         case FLUSH_TRAZO:
            flushTrazo (false);
            break;

         case FLUSH_JS:
            flushJavaScriptCode (false);
            break;

         case FLUSH:
            flushTrazo (false);
            flushJavaScriptCode (false);
            break;

         case DUMP:
            flushTrazo (true);
            flushJavaScriptCode (true);
            break;

         default:
            return false;
      }

      return true;
   }

   public void flushTrazo (boolean printout)
   {
      Eva edata = helper.ebs ().getAttribute (helper.ebs ().DATA, true, "trazos");
      edata.clear ();
      miCebolla.thePath.getEdiPaths ().dumpIntoEva  (edata);
      if (printout)
         System.out.println (edata);
   }

   public void flushJavaScriptCode (boolean printout)
   {
      //save the data into the atribute "trazos", NOTE: all in the first row! but if print onto a file will be ok
      //
      Eva edata = helper.ebs ().getAttribute (helper.ebs ().DATA, true, "trazosJS");
      String str = miCebolla.thePath.getEdiPaths ().toJavaScriptCode ();
      edata.setValueVar (str);
      if (printout)
         System.out.println (edata);
   }

   public void loadAllData ()
   {
      // load background image if any
      String backgImgFile = helper.ebs ().getSimpleDataAttribute ("image");
      if (backgImgFile != null && backgImgFile.length () > 0)
           backgBitmap = BitmapFactory.decodeFile(backgImgFile);
      else backgBitmap = null;
      assignBackgroundBitmap (backgBitmap);

      // load paths
      miCebolla.thePath = new uniPath ();
      graphicObjectLoader oba = new graphicObjectLoader ();
      oba.loadUniPathFromEvaTrazos (miCebolla.thePath, helper.ebs ().getDataAttribute ("trazos"));
   }
}

/*
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#data#


   <table_widgets>
   <!   prefix, javaName       , groupInfo,  importance, helpEvaName, desc, helpText
            2 , z2DMathFunc    , graphics ,    3       , help 2,  //A basic graphisc 2D

   <help 2>

      //A basic graphisc 2D


   <table_widgets_attributes>
    <! prefix  , name,         in_out  , possibleValues                      , desc

      2        ,visible          , in  , 0 | 1                               , //Value 0 to make the widget not visible
      2        ,enabled          , in  , 0 | 1                               , //Value 0 to disable the widget



   <table_widgets_messages>

   <! prefix  , msg, in_out, desc
      2       ,          , out   , button has been pressed
      2       , data!       , in    , update data
      2       , control!       , in    , update control


   <table_widgets_examples>
     <! prefix    , sampleEvaName       , desc, sampleText
         2        , 2 zBasicGraphic2D   , //Data for widget tester
         2        , hello zButton       , //A basic use of a zBasicGraphic2D

   <2 testData>
      //
      //

   <hello zBasicGraphic2D>
      //#listix#
      //
      //#data#
      //
      //#javaj#
      //
      //    <frames> F, Hello zBasicGraphic2D
      //
      //    <layout of F>
      //          PANEL, X
      //          2Panel2D
      //

#**FIN_EVA#

*/
