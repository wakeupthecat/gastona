/*
package javaj.widgets
Copyright (C) 2005 Alejandro Xalabarder Aulet

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



package javaj.widgets.basics;

import de.elxala.Eva.abstractTable.*;
import de.elxala.Eva.*;
import de.elxala.zServices.*;

/*


      li'nea control

            <nomWidget visible>       "0"       // si no existe es "1"
            <nomWidget enabled>       "0"       // si no existe es "1"
            <nomWidget visibleColumns>  "ricahardo", "id"


            <nomWidget selected>   en botones, check, y radio el label "1" o "0"
                                 en combos, listas y tablas los elementos seleccionados
                                 en a'rboles los nodos seleccionados

            <nomWidget selected.nomCampo>   (facilidad para el caso ti'pico de seleccio'n simple)



   EBS class diagram
   ----------------------------------------------------------------------------------



                   baseEBS(*e)
           -----------------------------------
            ^                             ^
            |                             |
            |                             |
      widgetEBS(*w)                      tableEvaDataEBS(*e)
   ----------------------          ------------------------------
            ^                          ^                 ^
            |                          |                 |
   used by almost all            tableEvaDB(*e)   absTableWindowingEBS(*e)
   zWidgets except those                           -----------------------------
   which data is table based                             ^                 ^
                                                         |                 |
                                                         |                 |
                                                   tableROSelect(*s)   tableWidgetBaseEBS(*t)
                                                                       ---------------------
                                                                           ^
                                                                           |
                                                                       tableEBS(*t)



            from package ...
      (*e) de.elxala.Eva.abstractTable
      (*w) javaj.widgets.basics
      (*t) javaj.widgets.table
      (*s) de.elxala.db.sqlite




*/
public class widgetEBS extends baseEBS
{
   public static final String sMSG_UPDATE_DATA    = "data!";
   public static final String sMSG_UPDATE_CONTROL = "control!";
   public static final String sMSG_REVERT_DATA    = "revert!";
   public static final String sMSG_SELECT_DATA    = "select!";

   /**
   forecast message to facilitate the synchronisation of mainly zJTextFields
   Note it is not actually a forecast message (send to all controlles) but since all
   zJTextField are listening to this all they will receive it.
   */
   public static final String sMSG_FORECAST_DATA_FROM_WIDGET_TO_MODEL = "_sys revertAll";  // update data with control contents (zJTextField ...)

   public static final String sATTR_SELECTED   = "selected";
   public static final String sATTR_VISIBLE    = "visible";
   public static final String sATTR_ENABLED    = "enabled";
   public static final String sATTR_IMAGE      = "image";
   public static final String sATTR_GRAFFITI_FORMAT = "graffiti format";
   public static final String sATTR_GRAFFITI   = "graffiti";
   public static final String sATTR_GRAFFITI_PRESS = "graffiti press";
   public static final String sATTR_GRAFFITI_ANIM = "graffiti animation";
   public static final String sATTR_FILES_DROPPABLE  = "droppedFiles";
   public static final String sATTR_DIRS_DROPPABLE  = "droppedDirs";
   public static final String sATTR_DIRTY      = "dirty";
   public static final String sATTR_ALTCHAR_1  = "AltChar";
   public static final String sATTR_ALTCHAR_2  = "mnemonic";
   public static final String sATTR_BACKCOLOR  = "backColor";
   public static final String sATTR_FORECOLOR  = "foreColor";

   private static logger logStatic = new logger (null, "javaj.widgets", null);
   public logger log = logStatic; // Note: it cannot be "protected"

   public widgetEBS (String name, EvaUnit pData, EvaUnit pControl)
   {
      super (name, pData, pControl);
//      System.out.println ("ha nacido un concetual widgetEBS nano [" + name + "]!");
   }

   public String evaNameSelectedField (String fieldName)
   {
      return evaName (sATTR_SELECTED) + "." + fieldName;
   }


   //
   //    DATA
   //
   public String getText ()
   {
      return mustGetEvaData ().getValue (0, 0);
   }

   public void setText (String newText)
   {
      mustGetEvaData ().setValue (newText, 0, 0);
   }

   //
   //    CONTROL
   //

   /// getter for attribute dirty: true is contents of native widget has changed and are not reflected in zWidget's model
   public boolean getIsDirty ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, sATTR_DIRTY, "0"));
   }

   /// setter for attribute dirty: true is contents of native widget has changed and are not reflected in zWidget's model
   public void setIsDirty (boolean value)
   {
      setSimpleAttribute (CONTROL, sATTR_DIRTY, (value ? "1": "0"));
   }

   public boolean isChecked ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, sATTR_SELECTED, "0" /* default false! */));
   }

   /**
      only aplicable to checkbutton and radio buttons!
   */
   public void setChecked (boolean on)
   {
      setSimpleAttribute (CONTROL, sATTR_SELECTED, (on ? "1" : "0"));
   }

   public boolean isDroppable ()
   {
      // if retuns default value (not) then the eva does not exist (or it has "not" as value)
      boolean canDropFiles       =  ! "not".equals (getSimpleAttribute (CONTROL, sATTR_FILES_DROPPABLE, "not"));
      boolean canDropDirectories =  ! "not".equals (getSimpleAttribute (CONTROL, sATTR_DIRS_DROPPABLE, "not"));

      return canDropFiles || canDropDirectories;
   }


   public void setFilesDroppable (boolean valor)
   {
      setSimpleAttribute (CONTROL, sATTR_FILES_DROPPABLE, (valor ? "" : "not"));
   }

   public void setDirsDroppable (boolean valor)
   {
      setSimpleAttribute (CONTROL, sATTR_DIRS_DROPPABLE, (valor ? "" : "not"));
   }


   public String getImageFile ()
   {
      return getSimpleAttribute (DATA, sATTR_IMAGE, "");
   }

   public void setImageFile (String imageName)
   {
      setSimpleAttribute (DATA, sATTR_IMAGE, imageName);
   }

   public Eva getGraffiti ()
   {
      return getAttribute (DATA, sATTR_GRAFFITI);
   }

   public String getGraffitiFormat ()
   {
      return getSimpleAttribute (DATA, sATTR_GRAFFITI_FORMAT);
   }

   public boolean isGraffitiFormatTrazos ()
   {
      return "trazos".equalsIgnoreCase (getGraffitiFormat ());
   }

   public Eva getGraffitiPress ()
   {
      return getAttribute (DATA, sATTR_GRAFFITI_PRESS);
   }

   public Eva getGraffitiAnimation ()
   {
      return getAttribute (DATA, sATTR_GRAFFITI_ANIM);
   }

//   public void setPainting (Eva imageName)
//   {
//      setAttribute (DATA, sATTR_IMAGE, imageName);
//   }

   public boolean getEnabled ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, sATTR_ENABLED, "1" /* default true! */));
   }

   public void setEnabled (boolean valor)
   {
      setSimpleAttribute (CONTROL, sATTR_ENABLED, (valor ? "1" : "0"));
   }

   //(o) TODO_REVIEW visibility issue
   public boolean getVisible ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, sATTR_VISIBLE, "1" /* default true! */));
   }

   public String getBackColorAttribute ()
   {
      return getSimpleAttribute (DATA, sATTR_BACKCOLOR, null);
   }

   public String getForeColorAttribute ()
   {
      return getSimpleAttribute (DATA, sATTR_FORECOLOR, null);
   }

   public void setVisible (boolean valor)
   {
      setSimpleAttribute (CONTROL, sATTR_VISIBLE, (valor ? "1" : "0"));
   }

   public void setDataControlAttributes (EvaUnit pData, EvaUnit pControl, String [] pairAttValues)
   {
      setNameDataAndControl (null, pData, pControl);
      setArrayOfSimpleAttributes (pairAttValues);
   }


   // thought for message
   //   MSG, widgetName data!, att1, val1, att2, val2 ...
   //
   public void setArrayOfSimpleAttributes (String [] params)
   {
      if (params == null || params.length == 0) return;

      for (int pp = 0; pp+1 < params.length; pp += 2)
      {
         setSimpleAttribute (DATA, params[pp], params[pp+1]);
      }
   }


   /**
      returns the Mnemonic character (attribute AltChar) or character 0 if no
      such attibute
   */
   public char getMnemonic ()
   {
      return getAltChar ();
   }

   /**
      returns the Mnemonic character (attribute AltChar) or character 0 if no
      such attibute
   */
   public char getAltChar ()
   {
      String at = getSimpleAttribute (CONTROL, sATTR_ALTCHAR_1);
      if (at == null)
         at = getSimpleAttribute (CONTROL, sATTR_ALTCHAR_2);

      return (at == null || at.length () < 1) ? 0: at.charAt (0);
   }
}
