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

import de.elxala.db.sqlite.*;
import de.elxala.langutil.*;
import de.elxala.Eva.abstractTable.*;

import javaj.widgets.*;
import javaj.widgets.table.util.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;


/**
*/
public class tableAparatoGeneral extends basicTableAparato
{
   private static final int MAX_SELECTED_ELEMENTS_FOR_TABLES = 1000;

   // data containers
   // --------------------------
   /// dummy model, needed for updating some widgets (JTable)

   /// for tables, lists etc contained within an eva variable
   protected tableEvaDataEBS  myEvaTableModel    = null;

   /// for tables, lists etc provenient from sqlite databases
   protected tableROSelect myDBViewTableModel = null;

   private boolean withVirtualCountColumn = true;
   //
   // --------------------------

   protected MessageHandle HMSG_ROW_SELECT   = new MessageHandle ();


   public tableAparatoGeneral (MensakaTarget objCtrl, tableEBS pDataAndControl)
   {
      super (objCtrl, pDataAndControl);
   }

   public tableEBS ebsTable ()
   {
      return (tableEBS) pEBS;
   }

   public boolean hasVirtualCountColumn ()
   {
      return withVirtualCountColumn;
   }

   public tableEvaDataEBS getRealTableObject ()
   {
      if (ebsTable ().isDatabaseTable ())
         return myDBViewTableModel;

      return myEvaTableModel;
   }

   //!!oo!! this method has to be renamed (checkModel (...) or something
   //
   //    it is very difficult to distinguish
   //
   //         helper.setNameDataAndControl (null, euData, null);
   //     from
   //         helper.ebsTable ().setNameDataAndControl (null, euData, null);
   //       
   //     the first one call this method which do a lot of things more than super.setNameDataAndControl
   //
   // public void setNameDataAndControl (String nameWidget, EvaUnit pData, EvaUnit pControl)
   // {
      // ebsTable ().setNameDataAndControl (nameWidget, pData, pControl);

      // // try to update data when
      // //    1) the data is specified
      // //    2) it is not but the control produces that both are specified now for the first time
      // //
      // if (pData != null || ebsTable().firstTimeHavingDataAndControl ())
      // {
         // tryUpdateData ();
      // }

      // preparControl4Selection ();
   // }

   public void setDataControlAndModel (EvaUnit pData, EvaUnit pControl, String [] args)
   {
      //ebsTable ().setNameDataAndControl (null, pData, pControl);
      ebsTable ().setDataControlAttributes (pData, pControl, args);

      // try to update data when
      //    1) the data is specified
      //    2) it is not but the control produces that both are specified now for the first time
      //
      if (pData != null || ebsTable().firstTimeHavingDataAndControl ())
      {
         tryUpdateData ();
      }

      preparControl4Selection ();
   }

   private void tryUpdateData ()
   {
      if (!ebsTable().hasAll ()) return;

      // kind of data (eva ? sqlite sql query ?)
      //
      if (ebsTable ().isDatabaseTable ())
      {
         log.dbg (2, "tableAparato::tryUpdateData",  ebsTable().getName () + " table type database");
         if (myDBViewTableModel == null)
         {
            log.dbg (2, "tableAparato::tryUpdateData",  ebsTable().getName () + " new db model");
            myDBViewTableModel = new tableROSelect (ebsTable ());
         }
         else
         {
            log.dbg (2, "tableAparato::tryUpdateData",  ebsTable().getName () + " reset extra filter and update data");

            // reset the filter since the data has been changed!
            myDBViewTableModel.setExtraFilter ("");

            //NO!   myDBViewTableModel.setNameDataAndControl (ebsTable ());
            myDBViewTableModel.updateNameDataAndControl (ebsTable ());

         }
      }
      else // Table of type Eva (not db)
      {
         log.dbg (2, "tableAparato::tryUpdateData",  ebsTable().getName () + " table type Eva");
         // table given in an Eva variable
         if (myEvaTableModel == null)
              myEvaTableModel = new tableEvaDataEBS (ebsTable ());
         else myEvaTableModel.copyEBS (ebsTable ());
      }

//      // ask for metadata for field sizes
//      updateMetaData ();

      // on every set model the selection is reseted
      preparControl4Selection ();
   }


   /**
      Set column names for the selection eva.

      Here we need both data and control thus we just try it in silent
      and if not set do not print out error messages

   */
   protected void preparControl4Selection ()
   {
      if (ebsTable().getData () == null) return;    // no error, just not ready for this ... wait until having data
      if (ebsTable().getControl () == null) return; // no error, just not ready for this ... wait until having control

      Eva eValues  = ebsTable ().getSubTableSelection (true);
      eValues.clear ();

      log.dbg (2, "tableAparato::preparControl4Selection", "");

      String [] visibleColArray = ebsTable().getVisibleColumns ();

      // fill the subtable with column names
      //
      for (int ii = 0; ii < visibleColArray.length; ii ++)
      {
         int realCol = ebsTable().getColumnIndex (visibleColArray[ii]);
         eValues.setValue (ebsTable().getColumnName (realCol), 0, ii);  // subtables works with not real indexes
      }
   }

//   protected void updateMetaData ()
//   {
//      utilMetadata.updateMetaData (ebsTable ());
//   }

   public void anotateUserShortLenCampo (int fieldIndx, int colWidth)
   {
      String [] visibleColArray = ebsTable().getVisibleColumns ();
      utilMetadata.anotateUserShortLenCampo (visibleColArray[fieldIndx], colWidth);
   }

   public int getShortLenCampo (int fieldIndx)
   {
      String [] visibleColArray = ebsTable().getVisibleColumns ();

      if (fieldIndx < 0 || fieldIndx >= visibleColArray.length) return 0;

      tableEvaDataEBS ta = getRealTableObject();
      if (ta == null) return 0;
      int realCol = ta.getColumnIndex (visibleColArray[fieldIndx]);

      return utilMetadata.getShortLenCampo (ebsTable().mustGetEvaData (), realCol, ebsTable().getColumnName (realCol));
   }

   public boolean getIsNumeric (int fieldIndx)
   {
      String [] visibleColArray = ebsTable().getVisibleColumns ();

      if (fieldIndx < 0 || fieldIndx >= visibleColArray.length) return false;

      tableEvaDataEBS ta = getRealTableObject();
      if (ta == null) return false;
      int realCol = ta.getColumnIndex (visibleColArray[fieldIndx]);

      return utilMetadata.getIsNumeric (ebsTable().getColumnName (realCol));
   }


   /**
      Arguments as given in widget message doSelect

      by index (0 based)

      MSG, iListo select!, #, 77
      MSG, iListo select!, #, 77, 55, 23, 12

      by index (1 based)

      MSG, iListo select!, #1, 77

      or by columName

      MSG, iListo select!, name, "pedrolo"
      MSG, iListo select!, name, "pedrolo", "princeso"
   */
   public void doSelect (String [] doSelectArg)
   {
      int [] indxarr = new int [0];

      if (doSelectArg == null || doSelectArg.length == 0)
      {
         // this is like clear all selections
      }
      else if (doSelectArg.length < 2)
      {
         // nothing
         log.err ("tableAparato::doSelect",  "need more arguments");
      }
      else if (doSelectArg[0].length () > 0 && doSelectArg[0].charAt (0) == '#')
      {
         int plusIndx = doSelectArg[0].startsWith ("#1") ? -1: 0;
         log.dbg (2, "tableAparato::doSelect", "select by index " + (-1 * plusIndx) + " based");

         indxarr = new int [doSelectArg.length - 1];
         for (int ii = 0; ii < indxarr.length; ii ++)
            indxarr[ii] = stdlib.atoi(doSelectArg[ii+1]) + plusIndx;
      }
      else
      {
         log.dbg (2, "tableAparato::doSelect", "select by value by column [" + doSelectArg[0] + "]");
         int colIndx = ebsTable ().getColumnIndex (doSelectArg[0]);
         if (colIndx >= 0)
         {
            // NOTE: in selection by column name we have to perform a search to find
            //       the records, nevertheless we are not going to search within the whole table
            //       (i.e. if it is a database table) but just in the cache table
            //       if not the selection could take to much long.
            //       Note that we already have a restriction in the number of record selected
            //       our selection is thought as user "reasonable" selection
            int nRowsInCache = ebsTable ().getRecordCount (); // count of cache

            indxarr = new int [doSelectArg.length - 1];
            for (int ss = 0; ss < indxarr.length; ss ++)
            {
               indxarr[ss] = -1;
               // only will be selected the first one found
               for (int ii = 0; ii < nRowsInCache; ii ++)
               {
                  //System.out.println ("maro [" + ebsTable ().getValue (ii, colIndx) + "] can [" + doSelectArg[ss+1] + "]");
                  if (ebsTable ().getValue (ii, colIndx).equals (doSelectArg[ss+1]))
                     indxarr[ss] = ii;
               }
            }
         }
      }

      log.dbg (2, "tableAparato::doSelect", indxarr.length + " record(s) selected");
      storeAllSelectedIndices (indxarr);
   }

   public boolean isIndexSelected (int indx)
   {
      Eva eIndices  = ebsTable ().getSelectedIndices (false);
      if (eIndices == null || eIndices.rows () == 0)
         return false;

      for (int ii = 1; ii < eIndices.rows (); ii++)
      {
         if (indx == stdlib.atoi(eIndices.getValue(ii, 0)))
            return true;
      }
      return false;
   }

   /**
   */
   public int [] getSelectedIndices ()
   {
      Eva eIndices  = ebsTable ().getSelectedIndices (false);
      if (eIndices == null || eIndices.rows () == 0)
      {
         //err ?
         return new int[0];
      }

      int [] indxs = new int[eIndices.rows () - 1];

      for (int ii = 1; ii < eIndices.rows (); ii++)
      {
         indxs[ii-1] = de.elxala.langutil.stdlib.atoi(eIndices.getValue(ii, 0));
      }
      return indxs;
   }

   /**
      indices to be selected, indices with value -1 are not considered
   */
   public boolean storeAllSelectedIndices (int [] indices)
   {
      // CLEAN <... subTableSelection>
      //
      Eva eSubTable = ebsTable ().getSubTableSelection (false);

      if (/* eRange == null || */ eSubTable == null)
      {
         log.err ("tableAparato.storeSelectedIndices", "cannot make selection because Control has not been prepared!");
         return false;
      }
      eSubTable.clear ();

      // set the header
      //
      String [] header = ebsTable ().getColumnNames ();
      for (int hh = 0; hh < header.length; hh ++)
      {
         eSubTable.setValue (header[hh], 0, hh);
      }


      // CLEAN <... selected.COLUMN>
      //
      //(o) javaj_widgets_selection Clean all <... selected.COLUMNNAME> variables
      //             This might be "expensive" (too much create and remove)
      //             but is the way of keeping a simple semantic for recognizing if there is
      //             any selection (check, VAR, widName selected.field)
      for (int cc = 0; cc < header.length; cc ++)
      {
         String nameEva = ebsTable().evaNameSelectedField (header [cc]);
         ebsTable ().getControl ().remove (nameEva);
      }

      //ensure <... selectedIndices>
      Eva eIndices  = ebsTable ().getSelectedIndices (true);
      eIndices.clear ();
      eIndices.setValue ("index", 0, 0);

      if (indices == null || indices.length == 0)
      {
         return true;
      }

      //fill subtable List
      // Note : indices migh contain value -1 that has to be discarded!
      //
      int rrow = 0;
//      int firstRow = 0;
      for (int row = 0; row < indices.length && row < MAX_SELECTED_ELEMENTS_FOR_TABLES; row ++)
      {
         int indx = indices[row];
         if (indx == -1) continue;

         rrow ++;
//...         if (stdlib.atoi (header[hh]) >= 0)
//...            eSubTable.addLine (new EvaLine (header[hh]));


         // add index
         eIndices.setValue ("" + indx, rrow, 0);

         // add subtable entry
         for (int col = 0; col < eSubTable.cols (0); col ++)
         {
            //26.04.2009 13:01 FIX Error "wrong use of a tableWidgetBaseEBS! Its method loadRowsFromOffset cannot be called!"
            //
            //  Using ebsTable() instead of getRealTableObject () works fine in most cases
            //  But only the getRealTableObject can properly retrieve new records (method loadRowsFromOffset)
            //  Rarely a selection would need a record that is not already in cache, but this happens while scrolling
            //  a table having a record selected (scrolling with page up / down)
            //
            //String value = ebsTable().getValue (indices[row], col);
            tableEvaDataEBS ta = getRealTableObject();
            if (ta != null)
            {
               String value = ta.getValue (indx, col);

               // set the value into the subTable
               //
               eSubTable.setValue (value, rrow, col);
            }
         }
      }

      // just for the first record set the values into eva selected ...
      // for example
      //       <widgetName selected.columnName> value
      //
      // Note this is a small redundance but it facilitates the typical use
      // of selection (one record).
      //
      for (int cc = 0; cc < header.length; cc ++)
      {
         int indx0 = stdlib.atoi (eIndices.getValue (1, 0));
         //System.out.println ("indx0 " + indx0);
         String nameEva = ebsTable().evaNameSelectedField (header [cc]);
         Eva selEva = ebsTable ().getControl ().getSomeHowEva (nameEva);

         //24.03.2012 14:06 FIX Error "wrong use of a tableWidgetBaseEBS! Its method loadRowsFromOffset cannot be called!"
         //                 See the first fix (26.04.2009) in the previous "for"
         //                 The same error appeared now in this line (luckly it was good documented!!)
         //-old- String value = ebsTable().getValue (indices[0], cc);
         //-old- selEva.setValue (value, 0, 0);
         tableEvaDataEBS ta = getRealTableObject();
         if (ta != null)
         {
            String value = ta.getValue (indx0, cc);
            //System.out.println ("value de indx0 cc " + cc + " = " + value);
            selEva.setValue (value, 0, 0);
         }
      }

      return (eSubTable.rows ()-1) <= MAX_SELECTED_ELEMENTS_FOR_TABLES;
   }

   // update the column "selected" accoding to the current selection
   //
   public void updateSelectedInTable ()
   {
      tableEvaDataEBS tab = getRealTableObject ();
      if (tab == null) return;

      // create column "select" if not done
      int indxSel = tab.getColumnIndex ("selected");
      if (indxSel == -1)
         indxSel = tab.getColumnCount ();
      
      int tama = tab.getTotalRecords ();
      for (int ii = 0; ii < tama; ii ++)
      {
         tab.setValue (isIndexSelected (ii) ? "1": "0", ii, indxSel);
      }
   }
}

