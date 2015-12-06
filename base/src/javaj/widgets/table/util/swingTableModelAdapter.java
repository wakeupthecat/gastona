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

package javaj.widgets.table.util;

import javaj.widgets.table.*;
import de.elxala.Eva.abstractTable.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.JButton;

/**
   implementation of AbstractTableModel
   required by swing JTable
*/
public class swingTableModelAdapter extends AbstractTableModel
{
   //(o) TODO_javaj Experimental!! columnas numéricas solucionado en AbstractTableModel (bastante malo)
   public static boolean INTENTA_COLUMNAS_NUMERICAS_ATONT = false;

   private tableEvaDataEBS    tabletaReal = null;     // this extract really - in case of DB - the data from sql
   private tableWidgetBaseEBS tabletaVisible = null;  // this is needed because of visible columns

   private String [] visibleColArray = null;
   private String [] visibleColNameShowArray = null;
   private boolean hasVirtualCounterColumn = true;
   private static JButton grayColumn = null;
   private static Float floto = new Float(0f);

   private boolean [] isNumeric = null;   // needed for alignement (java TableColumn does not have any method!!! all goes under getColumnClass


   public swingTableModelAdapter (tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible)
   {
      construct (tableReal, tableVisible, true);
   }

   /**
      The model needs a real object capable of extracting records on demand (in case table is DB)
      but also it has to present the data acording with visibility of the columns. The real table
      is implemented with tableROSelect (which is a tableEvaDataEBS) but this class
      knows NOTHING about visible columns since this is a feature only interesting for a GUI component
      and it is implemented in javaj.widgets.table package. Therefore we need both tables. Of course
      not two independent tables but both has to share the same EBS!

      This is how it works: when information is needed about recordCount or values of registers
      the real table is used, and this updates the EBS at this point the virtual table is capable
      to acces the updated data through EBS.

   */
   public swingTableModelAdapter (tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible, boolean withVirtualCounterColumn)
   {
      construct (tableReal, tableVisible, withVirtualCounterColumn);
   }

   private void construct (tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible, boolean withVirtualCounterColumn)
   {
      if (grayColumn == null)
      {
         grayColumn = new JButton ("O");
         //grayColumn.setEnabled (false);
      }

      tabletaReal    = tableReal;
      tabletaVisible = tableVisible;

      visibleColArray = tableVisible.getVisibleColumns ();
      visibleColNameShowArray = tableVisible.getVisibleColumnShowNames ();
      hasVirtualCounterColumn = withVirtualCounterColumn;

      // make an array of isNumeric where the indices are the ones for the java model (not the real ones!)
//      isNumeric = new boolean [getColumnCount()];
//      int voy = 0;
//      if (hasVirtualCounterColumn)
//         isNumeric [voy++] = true;
//
//      for (int ii = 0; ii < visibleColArray.length; ii ++)
//      {
//         int indx = tabletaReal.getColumnIndex (visibleColArray[ii]);
//         tabletaReal.getShortLenCampo (indx);
//         isNumeric[voy ++] = tabletaReal.getIsNumeric (indx);
//      }
   }

   public int getRowCount ()
   {
      if (tabletaReal == null) return 0;
      return tabletaReal.getRecordCount ();
   }

   public int getColumnCount ()
   {
      if (visibleColArray == null) return 0;
      return visibleColArray.length + (hasVirtualCounterColumn ? 1:0);
   }

   public Class getColumnClass (int columnIndex)
   {

      if (INTENTA_COLUMNAS_NUMERICAS_ATONT)
      {
         if (hasVirtualCounterColumn && columnIndex == 0)
            return floto.getClass();

         // HAY QUE HACER UN RENDERER PARA LAS COLUMNAS
         // NO HAY MANERA DE ALINEAR LOS NUMERICOS POR LAS BUENAS !!!
         // SI LE DIGO AQUI QUE ES NUMERICO; PETA DE MALA MANERA SI COMO VALOR RECIBE UN STRING (#$#$!!)
         // SI LE DIGO QUE ES FLOTANTE ME ESCRIBE 1.0 LO CUAL EN GENERAL NO ME VA A INTERESAR (???!!!)
         //
         boolean isNum = /*false; */ utilMetadata.getIsNumeric(getColumnName (columnIndex));
   //
   //      System.out.println ("GETCOLUMNCLASS [" + getColumnName (columnIndex) + "] isNumeric " +  isNum + " CONSECUO");
   //
         return isNum ? floto.getClass() : "".getClass ();
      }

      return "".getClass ();
   }

   public boolean isCellEditable (int row, int col)
   {
      if (hasVirtualCounterColumn && col == 0) return false;

      return tabletaVisible.isEditable (row, col);
   }

   public void setValueAt (Object aValue, int row, int col)
   {
      if (tabletaReal == null) return;
      if (visibleColArray == null) return;
      if (hasVirtualCounterColumn && col == 0) return;
      if (hasVirtualCounterColumn) col --;

      if (col < 0 || col >= visibleColArray.length) return; // error ?

      int realCol = tabletaReal.getColumnIndex (visibleColArray[col]);
      if (realCol >= 0)
         tabletaReal.setValue ((String) aValue, row, realCol);
   }

   public Object getValueAt (int row, int col)
   {
      if (hasVirtualCounterColumn)
      {
         if (col == 0)
         {
            // the user want to count from 1 .. N
            int userRowNr = (row + 1);

            if (INTENTA_COLUMNAS_NUMERICAS_ATONT)
               return new Float (userRowNr);
            return "" + userRowNr;
         }
         col --;
      }

      if (col < 0 || visibleColArray == null || col >= visibleColArray.length) return ""; // error ?

      // ensures that the data for row 'row' is present if not it is retrieved on demand
      // note the column index doesn't matter now
      //
      if (tabletaReal == null) return "";
      tabletaReal.getValue (row, 0);

      // Now the data of row 'row' is in EBS

      // reindex the column index
      col = tabletaVisible.getColumnIndex (visibleColArray[col]);
      if (col >= 0)
      {
         String val = tabletaVisible.getValue (row, col);

         if (INTENTA_COLUMNAS_NUMERICAS_ATONT)
         {
            boolean isNum = /*false; */ utilMetadata.getIsNumeric(visibleColArray[col]);
            if (! isNum)
               return val;

            return new Float (de.elxala.langutil.stdlib.atof (val));
         }
         return val;
      }

      /*
         NOTE:
         This would also currently work:

               // reindex the column index
               col = tabletaReal.getColumnIndex (visibleColArray[col]);
               if (col >= 0)
                  return tabletaReal.getValue (row, col);

         and without the first tabletaReal.getValue (row, 0);
         but it is preferible to separate the roles clearly
         for possible future changes in tabletaVisible functionality
      */

      return "";
   }

   public String getColumnName (int col)
   {
      if (hasVirtualCounterColumn && col == 0)
         return "#(" + (tabletaReal != null ? tabletaReal.getRecordCount (): 0) + ")";

      if (hasVirtualCounterColumn) col --;

      if (visibleColNameShowArray == null) return "?";
      return visibleColNameShowArray[col];
   }
}
