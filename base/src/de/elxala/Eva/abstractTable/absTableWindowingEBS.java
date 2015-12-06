/*
library de.elxala
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

package de.elxala.Eva.abstractTable;

import javax.swing.ListModel;
import javax.swing.event.*;

import de.elxala.langutil.*;
import de.elxala.Eva.*;

/**
   For handling cached tables, loading the parts on demand
   the loaded part is called "window", note that it has nothing to do
   with Windows, frames etc.


   handles following EBS

      <name db totalRecordCount>
      <name db cachedRecordCount>
      <name db recordOffset>

   It is an abstract class because of the method

      public abstract void loadRowsFromOffset (int offsetRowStart);


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
public abstract class absTableWindowingEBS extends tableEvaDataEBS implements tableSwingAdaptable
{
   public static final String sATTR_DB_TOTAL_RECORDS  = "db totalRecordCount";
   public static final String sATTR_DB_CACHED_RECORDS = "db cachedRecordCount";
   public static final String sATTR_DB_RECORD_OFFSET  = "db recordOffset";


   // variables : get them always from basicModel EvaUnit !!
   //
   public int MAX_CACHE  = 240;

   public absTableWindowingEBS (baseEBS ebs)
   {
      super (ebs); //setNameDataAndControl (ebs.getName (), ebs.getData (), ebs.getControl ());
   }


   /**
      This method is used when the function loadDataOnDemand returns true.
      The implementation of this method should load a "window" of the
      table, usually from the index offsetRowStart to the index offsetRowStart + MAX_CACHE.
      For doing that the methods initSetDataRel and setDataRel might be used.
   */
   public abstract void loadRowsFromOffset (int offsetRowStart);

   /**
      This method has to be used by a real windowing table on each
      update of its data
   */
   protected void setTotalRecords (int total)
   {
      setSimpleAttribute (CONTROL, sATTR_DB_TOTAL_RECORDS, "" + total);
   }

   /**
      Returns the total number of records of the table
      not only the records cached into the window.
   */
   public int getTotalRecords ()
   {
      // NOTE:
      // this class might be used as abstract class for real windowing tables (like tableROSelect)
      // as well as for simple EvaTables (pure tableEvaDataEBS). The last ones do not have
      // an attribute for the total number of records while the first ones must use the method
      // setTotalRecords (int total) each time the data change.

      String str = getSimpleAttribute (CONTROL, sATTR_DB_TOTAL_RECORDS);
      if (str != null)
         // it is a real windowing table
         return stdlib.atoi (str);
      else
         // it is a pure tableEvaDataEBS and don't have the attribute sATTR_DB_TOTAL_RECORDS
         return super.getRecordCount ();

      // return stdlib.atoi (getSimpleAttribute (CONTROL, sATTR_DB_TOTAL_RECORDS));
   }

   /**
      Returns the total number of records of the table
      not only the records cached into the window.
      Note: the function is exactly the same as getTotalRecords ()
            it is needed with this name for two reasons:
              - comply with the interface tableSwingAdaptable and
              - override the method with the same name of the base class
                which meaning is totally different!
   */
   public int getRecordCount ()
   {
      return getTotalRecords ();
   }

   /**
      Returns the number of records cached of the table
      according with the base class (only for private use)
   */
   private int getRealCachedRecordCount ()
   {
      return super.getRecordCount ();
   }


   public boolean isEditable (int row, int col)
   {
      return false;
   }

   /**
      if loadDataOnDemand() returns false, basicModel is supposed to contain all the data
      otherwise a cache of 240 is set
   */
   public void setNameDataAndControl (String name, EvaUnit pData, EvaUnit pControl)
   {
      super.setNameDataAndControl (name, pData, pControl);

      if (hasAll () && (pData != null || firstTimeHavingDataAndControl ()))
      {
         // reset the rel ?
         setCachedRecordCount (getRealCachedRecordCount ());
      }
   }

   //
   // access setget to state variables
   //

   protected void setCachedRecordCount (int RelRow)
   {
      setSimpleAttribute (CONTROL, sATTR_DB_CACHED_RECORDS, "" + RelRow);
   }

   /**
      this method is only interesting for windowing tables
   */
   protected int getCachedRecordCount ()
   {
      return stdlib.atoi (getSimpleAttribute (CONTROL, sATTR_DB_CACHED_RECORDS));
   }

   /**
      this method is only interesting for windowing tables
   */
   protected void setRecordOffset (int offset)
   {
      setSimpleAttribute (CONTROL, sATTR_DB_RECORD_OFFSET, "" + offset);
   }

   /**
      this method is only interesting for windowing tables
   */
   protected int getRecordOffset ()
   {
      return stdlib.atoi (getSimpleAttribute (CONTROL, sATTR_DB_RECORD_OFFSET));
   }

   //
   // ----
   //

   /**
      this method is only interesting for windowing tables
   */
   protected void initCache (int offset)
   {
      setRecordOffset  (offset);
      setCachedRecordCount (0);
   }

   /**
      set data directly
   */
   protected void setRelativeRecord (int relativeRow, String [] arr)
   {
      Eva dad = mustGetEvaData ();
      if (dad == null) return;

      if (relativeRow > MAX_CACHE)
      {
         log.severe ("absTableWindowingEBS", "bad use of ::setRelativeRecord : tried to override MAX_CACHE!");
         return;
      }

      if (dad.rows () <= relativeRow + 1)
         dad.setValue ("", relativeRow + 1, 0);

      dad.get (1 + relativeRow).set (arr);
      setCachedRecordCount (1 + relativeRow);
   }

   public void obtainRow (int demandedRow)
   {
//      System.out.print (" A VER QUE PASA CON TU DEMANDA " + demandedRow + " OSET " + getRecordOffset () + " cacheto " + getCachedRecordCount () + " ... ");
      if (demandedRow < 0 || demandedRow >= getTotalRecords ())
      {
         // can happen ...
         // err ("::expandRow " + demandedRow + " while total Rows are " + getTotalRecords ());
//         System.out.println (" NO SE QUE PASA PERO VA A SER QUE NO!");
         return;
      }

      // a ver si la tengo ...
      int toca = demandedRow - getRecordOffset ();
      if (toca >= 0 && toca < getCachedRecordCount ())
      {
//         System.out.println (" LATENGO!!");
         return; // la tengo!
      }

      // NOTE : could be more efficient for the up 1 down 1 scroll but for now it is ok!

      if (toca > 0)
      {
         setRecordOffset (demandedRow);
      }
      else
      {
         // to facilitate the fine sroll up
         setRecordOffset (Math.max (0, demandedRow - MAX_CACHE / 2));
      }

//      System.out.println (" TE LA CONSIGO SETANDO UN OSET DE " + getRecordOffset ());
      loadRowsFromOffset (getRecordOffset ());
   }


   public String getValue (int row, int col)
   {
      if (row < 0 || col < 0) return "?1"; // absurd!
      if (row > getTotalRecords ()) return "?2";

      obtainRow (row);

      return mustGetEvaData ().getValue (1 + row - getRecordOffset (), col);
   }

   /**
      returns the value of column '' at position row 'row' (0..N-1 records)
      Note: if the row/col par does not exists the returned value is an empty string ""
            instead of null. To check if the position is valid use getColumns and getTotalRecords
   */
   public String getValue (String columnName, int row)
   {
      int col = getColumnIndex (columnName);

      if (row < 0 || col < 0) return "?1"; // absurd!
      if (row > getTotalRecords ()) return "?2";

      obtainRow (row);

      return mustGetEvaData ().getValue (1 + row - getRecordOffset (), col);
   }
}
