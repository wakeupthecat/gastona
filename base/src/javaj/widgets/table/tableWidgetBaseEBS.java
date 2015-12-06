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

package javaj.widgets.table;

import de.elxala.Eva.*;
import de.elxala.Eva.abstractTable.*;

import javaj.widgets.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.uniColor;



/**
   23.01.2007 21:34

   tableWidgetBaseEBS origin of the convenient cascade of base classes to separate cleanly the funtionality of tableEBS

      tableWidgetBaseEBS      <<<<<<< handles the column visibility and Asiste Columns
         ^
      tableEBS                : object to be used by Aparato's and Mando's



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



   technically:
      derived from absTableWindowingEBS (all methods getRecordCount, getColumnCount etc ...)
      implements functionality of widgetEBS
      implements column visibility  (getVisisbleColumns ...)
      implements column asiste      (getAsisteColumns ...)

   NOTE:
      We cannot derive this class from both widgetEBS and absTableWindowingEBS (java)
      to have both functionalities we have to implement here widgetEBS funcionallity (replicate all methods).
      As consequence we also have to repeat the functionality of basicMando into basicTableMando because
      basicMando needs a widgetEBS but we are not a widgetEBS! etc..
*/
public class tableWidgetBaseEBS extends absTableWindowingEBS
{
   public static final String sATTR_VISIBLE_COLUMNS = "visibleColumns";
   public static final String sATTR_ASISTE_COLUMNS  = "asisteColumns";
   public static final String sATTR_SUBTABLE_SELECTION  = "subTableSelection";
   public static final String sATTR_SELECTED_INDICES  = "selectedIndices";
   public static final String sATTR_FILES_DROPPABLE  = "droppedFiles";
   public static final String sATTR_DIRS_DROPPABLE  = "droppedDirs";

   public tableWidgetBaseEBS (baseEBS ebs)
   {
      // forward constructor
      super (ebs);
   }

   /**
      This method is forbidden, the real tables are instanciated directly in
      Aparato classes
   */
   public void loadRowsFromOffset (int offset)
   {
      log.severe ("loadRowsFromOffset", "wrong use of a tableWidgetBaseEBS! Its method loadRowsFromOffset cannot be called!");
   }

   // ================= All Columns of the table

   /**
      get an array with the names of the visible columns
      Note: if there weren't visible columns declared and still no data
      it returns null
   */
   public String [] getVisibleColumns ()
   {
      Eva visCol = getAttribute (CONTROL, sATTR_VISIBLE_COLUMNS);
      if (visCol == null || visCol.cols(0) == 0)
      {
         // per default all columns are visible!
         return getColumnNames ();
      }

      return visCol.get(0).getColumnArray ();
   }

   /**
      get an array with the names of the visible columns show names
      Note: if there weren't visible columns declared and still no data
      it returns null
   */
   public String [] getVisibleColumnShowNames ()
   {
      Eva visCol = getAttribute (CONTROL, sATTR_VISIBLE_COLUMNS);
      if (visCol == null || visCol.cols(0) == 0)
      {
         // per default all columns are visible!
         return getColumnNames ();
      }
      return (visCol.rows() > 1) ? visCol.get(1).getColumnArray () : visCol.get(0).getColumnArray ();
   }

   /**
      set the array with the names of the visible columns
   */
   public void setVisibleColumns (String [] colNames)
   {
      // get the current visible columns or create it if not already created
      Eva visCol = getAttribute (CONTROL, sATTR_VISIBLE_COLUMNS);
      if (visCol == null)
      {
         // has to be created!
         setSimpleAttribute (CONTROL, sATTR_VISIBLE_COLUMNS, "");

         // now it has to exist
         visCol = getAttribute (CONTROL, sATTR_VISIBLE_COLUMNS);
      }

      visCol.get(0).set (colNames);
   }


   // ================= Visible columns of the table
   //
   public String [] getAsisteColumns()
   {
      Eva asiCol = getAttribute (CONTROL, sATTR_ASISTE_COLUMNS);
      if (asiCol == null)
      {
         // per default all visible columns are Asiste!
         return getVisibleColumns ();
      }

      return asiCol.get(0).getColumnArray ();
   }

   public void setAsisteColumns (String [] columNames)
   {
      // get the current Asiste columns or create it if not already created
      Eva asiCol = getAttribute (CONTROL, sATTR_ASISTE_COLUMNS);
      if (asiCol == null)
      {
         // has to be created!
         setSimpleAttribute (CONTROL, sATTR_ASISTE_COLUMNS, "");

         // now it has to exist
         asiCol = getAttribute (CONTROL, sATTR_VISIBLE_COLUMNS);
      }

      asiCol.get(0).set (columNames);
   }


   /*
      returns the eva of control for the selection values
   */
   public Eva getSubTableSelection (boolean forceCreation)
   {
      return getAttribute (CONTROL, forceCreation, sATTR_SUBTABLE_SELECTION);
   }

   /*
      returns the eva of control for the Selected indices
   */
   public Eva getSelectedIndices (boolean forceCreation)
   {
      return getAttribute (CONTROL, forceCreation, sATTR_SELECTED_INDICES);
   }

   // needed FUNCTIONALITY OF widgetEBS (it has to be implemented)
   //

   /// getter for attribute dirty: true is contents of native widget has changed and are not reflected in zWidget's model
   public boolean getIsDirty ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, widgetEBS.sATTR_DIRTY, "0"));
   }

   /// setter for attribute dirty: true is contents of native widget has changed and are not reflected in zWidget's model
   public void setIsDirty (boolean value)
   {
      setSimpleAttribute (CONTROL, widgetEBS.sATTR_DIRTY, (value ? "1": "0"));
   }

   public String evaNameSelectedField (String fieldName)
   {
      return evaName (widgetEBS.sATTR_SELECTED) + "." + fieldName;
   }

   public boolean getEnabled ()
   {
      return "1".equals (getSimpleAttribute (widgetEBS.CONTROL, widgetEBS.sATTR_ENABLED, "1" /* default true! */));
   }

   public void setEnabled (boolean valor)
   {
      setSimpleAttribute (CONTROL, widgetEBS.sATTR_ENABLED, (valor ? "1" : "0"));
   }

   public boolean getVisible ()
   {
      return "1".equals (getSimpleAttribute (CONTROL, widgetEBS.sATTR_VISIBLE, "1" /* default true! */));
   }

   public void setVisible (boolean valor)
   {
      setSimpleAttribute (CONTROL, widgetEBS.sATTR_VISIBLE, (valor ? "1" : "0"));
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

   private static uniColor COLOR_FONT = new uniColor (0, 0, 0);
   private static uniColor COLOR_SEL  = new uniColor (255, 200, 0);  // Color.orange
   private static uniColor COLOR_OVER = new uniColor (255, 128, 128);

   public uniColor getSelFontColor ()
   {
      return COLOR_FONT;
   }

   public uniColor getSelNormalColor ()
   {
      return COLOR_SEL;
   }

   public uniColor getSelOverflowColor ()
   {
      return COLOR_OVER;
   }
}
