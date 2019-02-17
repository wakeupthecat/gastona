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

import javax.swing.ListModel;
import javax.swing.event.*;
import de.elxala.Eva.abstractTable.*;
import javaj.widgets.table.*;
import javaj.widgets.basics.widgetLogger;

/**
      ==========================================
      Implementaciones de ListModel

      IMPORTANT NOTE!:

         To use ListModel in a JList (seting the model with setModel (...))
         it is necessary to provide the JList also with a renderer !!
         if not the result might be very strange and even sometimes it could
         be right rendered but it is not guaranteed!!

         As alternative we provide also here a method

            String [] getAllRows ()

         which can be used in a JList o JComboBox with the method setDataList ()
         this is the most rapid and comfortable way but of course find its limits
         when the data is very big (i.e. comes from a sql)

      ==========================================
*/
public class swingListModelAdapter implements ListModel
{
   private tableEvaDataEBS    tabletaReal;     // this extract really - in case of DB - the data from sql
   private tableWidgetBaseEBS tabletaVisible;  // this is needed because of visible columns

   private String [] visibleColArray = null;

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
   public swingListModelAdapter (tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible)
   {
      tabletaReal    = tableReal;
      tabletaVisible = tableVisible;

      visibleColArray = tabletaVisible.getVisibleColumns ();
   }

   /**
      Note that the JList expect a single object and we have
      as model a real table, usually getColumnCount for a list will return 1
      (one visible column) but if it were not the case, which is also plausible,
      we concatenate the values comma separated.
   */
   public Object getElementAt(int row)
   {
      // ensures that the data for row 'row' is present if not it is retrieved on demand
      // note the column index doesn't matter now
      //
      tabletaReal.getValue (row, 0);

      // Now the data of row 'row' is in EBS

      String value = "";

      for (int ii = 0; ii < visibleColArray.length; ii ++)
      {
         int realCol = tabletaReal.getColumnIndex (visibleColArray[ii]);
         value += ((ii > 0) ? ", ": "") + tabletaReal.getValue (row, realCol);
      }
      return value;
   }

   // implementation of ListModel
   public int getSize ()
   {
      //21.01.2007 16:35 DEEP BUG IN ListModel
      //       This method is the implementation of ListModel
      //       and it seems it does not accept -1 as value
      //       producing the exception java.lang.NegativeArraySizeException
      //       (Note: AbstractTableModel for JTables accepts -1 without problem)
      //
      //      java.lang.NegativeArraySizeException
      //              at javax.swing.plaf.basic.BasicListUI.updateLayoutState(BasicListUI.java:1129)
      //              at javax.swing.plaf.basic.BasicListUI.maybeUpdateLayoutState(BasicListUI.java:1098)
      //              at javax.swing.plaf.basic.BasicListUI.getPreferredSize(BasicListUI.java:281)
      //              at javax.swing.JComponent.getPreferredSize(JComponent.java:1275)
      //              ...
      //
      // so avoid returning negative values
      //
      int size = tabletaReal.getRecordCount();
      return size < 0 ? 0: size;
   }

   public void addListDataListener (ListDataListener l)
   {
      // this native mechanism is not used by the moment
   }

   public void removeListDataListener (ListDataListener l)
   {
      // this native mechanism is not used by the moment
   }


   /*
      Alternative for using ListModel using setListData ()

      ==========================================
      To get all elements in an array for lists (not abused!)
      It is the easiest way to implement a JList and JComboBox
      if not it is needed ListModel + Render
      ==========================================
   */
   private static final int MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS = 1024;

   public String [] getAllRowsUpTo1024 () // (boolean header, boolean onlyVisible)
   {
      int totSize = getSize ();
      if (totSize > MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS)
      {
         widgetLogger.log().fatal ("swingListModelAdapter::getAllRows", "small List or ComboBox exceeded, the result might not be complete!");
         totSize = MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS;
      }

      String [] allE = new String [totSize];
      for (int ii = 0; ii < totSize; ii ++)
      {
         allE[ii] = (String) getElementAt(ii);
      }

      return allE;
   }
}
