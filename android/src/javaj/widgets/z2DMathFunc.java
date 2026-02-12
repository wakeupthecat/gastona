/*
package javaj.widgets.graphics;
Copyright (C) 2011-2020 Alejandro Xalabarder Aulet

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

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.androidSysUtil;
import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;

import android.graphics.*;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;

import de.elxala.langutil.*;
import de.elxala.math.space.*;


public class z2DMathFunc extends mathFunctionView
                         implements izWidget, MensakaTarget
{
   private final int SCALE_X = 11;
   private final int SCALE_Y = 12;
   private basicAparato helper = null;

   public z2DMathFunc (Context co, String name)
   {
      super(co);
      setName (name);
   }

   public void init (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      Mensaka.subscribe (this, SCALE_X, map_name + " scaleX");
      Mensaka.subscribe (this, SCALE_Y, map_name + " scaleY");
   }

   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return getHeight (); }
   public int getDefaultWidth () { return getWidth (); }

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
                  "data", "z2DMathFunc",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);

            drawFunctions ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "z2DMathFunc",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());
            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         case SCALE_X:
         case SCALE_Y:
            float sX = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("scaleX"));
            float sY = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("scaleY"));
            setScale (sX, sY);
            break;


         default:
            return false;
      }

      return true;
   }

   protected void drawFunctions ()
   {
      Eva funcArr = helper.ebs ().getDataAttribute ("functions");
      if (funcArr == null) return;

      for (int ii = 0; ii < funcArr.rows (); ii ++)
      {

      }
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
