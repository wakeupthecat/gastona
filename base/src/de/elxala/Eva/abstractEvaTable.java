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

package de.elxala.Eva;

import javax.swing.ListModel;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;

import de.elxala.langutil.*;
import de.elxala.db.sqlite.*;
import de.elxala.zServices.*;

/**

   abstractEvaTable is a "very abstract" class for three reasons

      - extends javax.swing.table.AbstractTableModel (which is not abstract by the way),
        thus a class derived from it might act as model for a javax.swing.JTable

      - implements javax.swing.ListModel, thus a class derived from it might act
        as model for a javax.swing.JList (and JComboBox ?)

      - the class has a container for data and column information as well as implements
        functionality for handle that but SHE does not know if all the data is there!
        Therefore uses two abstract methods to handle the situation

            abstract boolean loadDataOnDemand ();
            abstract void    loadRowsFromOffset (int offsetRowStart);

   abstractEvaTable holds a buffer of part of the data (or the whole data) as well as
   state variables all in an EvaUnit variable. Therefore its contents is
   exportable within a single variable, this is a way of serialization.

   Known concrete derived classes :
      de.elxala.Eva.EvaTable
      de.elxala.db.sqlite.roViewTableModel

*/
public abstract class abstractEvaTable extends AbstractTableModel implements ListModel
{
   // ===========================================================
   // DERIVED CLASSES HAS TO IMPLEMENT THESE TWO METHODS:
   //
   //    loadDataOnDemand ()
   //    loadRowsFromOffset (int offsetRowStart)


   /**
      This abstract method has to be implemented by a concrete class derived
      from this one (abstractEvaTable).

      Implementing this function as { return false; } will indicate
      that all the data of the table is handle through a single EvaUnit variable (basicModel)
      see constructor and setEvaModel method. Therefore the derived class might implement
      the method loadRowsFromOffset () with no specific code (e.g {})

      Implementing this function as { return true; } will indicate
      that the single EvaUnit variable (basicModel) is most like a buffer of
      the whole data model. In this case the derived class has to implement
      the abstract method loadRowsFromOffset (int offsetRowStart) with code
      filling the data requested.
   */
   public abstract boolean loadDataOnDemand ();

   /**
      This method is used when the function loadDataOnDemand returns true.
      The implementation of this method should load a "window" of the
      table, usually from the index offsetRowStart to the index offsetRowStart + MAX_CACHE.
      For doing that the methods initSetDataRel and setDataRel might be used.
   */
   public abstract void loadRowsFromOffset (int offsetRowStart);

   // ===========================================================



   // Example:
   //
   //   <tMyTable>
   //       id, name, blah
   //
   //       192, Asia, etc etc
   //       232, Rita, ....
   //       ....
   //
   //   <tMyTable StateInfo>
   //
   //       varName,          value
   //       MAX_CACHE,         240
   //       totalRows,       10293
   //       maxValidRelRow,     30
   //       offsetRows,        881
   //       includeRecordNo,     0
   //
   private static logger log = new logger (null, "de.elxala.Eva.abstractEvaTable", null);

   private static final String evaTABLESTATE        = "StateInfo";
   private static final String varMAX_CACHE         = "MAX_CACHE";
   private static final String varTOTAL_ROWS        = "totalRows";
   private static final String varOFFSET_ROW        = "offsetRows";
   private static final String varMAX_VALID_REL_ROW = "maxValidRelRow";
   private static final String varINCLUDE_RECORDNO  = "includeRecordNumber";

   // buffer for data, either all the data (pure eva table) or buffer-window
   protected String  evaNameBuffer = null;
   protected EvaUnit halfModel = null;
   protected Eva     evaBuffer = null; // where the data buffer is to be found
   protected Eva     evaState  = null; // where the current state to be found

   // variables : get them always from basicModel EvaUnit !!
   //
   public int MAX_CACHE  = 240;
   public int INCLUDE_RECORDNO  = 0;

   private int rvar_totalRows    = -1;
   private int rvar_ValidRelRow  = -1;
   private int rvar_offsetRows   = -1;

   public abstractEvaTable ()
   {
   }

   public abstractEvaTable (EvaUnit basicModel, String nameEva)
   {
      setEvaModel (basicModel, nameEva);
   }

   /**
      if loadDataOnDemand() returns false, basicModel is supposed to contain all the data
      otherwise a cache of 240 is set
   */
   public void setEvaModel (EvaUnit basicModel, String nameEva)
   {
      if (basicModel == null) return;
      if (nameEva == null)
      {
         log.severe ("setEvaModel", "nameEva cannot be null!");
         return;
      }

      halfModel = basicModel;
      evaBuffer = halfModel.getSomeHowEva (nameEva);
      evaState  = halfModel.getSomeHowEva (nameEva + " " + evaTABLESTATE);
      int recordsInHalfModel = evaBuffer.rows () - 1;

      if (evaState.rowOf (varTOTAL_ROWS) == -1)
      {
         // build state
         evaState.addLine (new EvaLine ("varName, value"));
         evaState.addLine (new EvaLine (varMAX_CACHE + ", 240"));
         evaState.addLine (new EvaLine (varTOTAL_ROWS + ", " + recordsInHalfModel ));
         evaState.addLine (new EvaLine (varOFFSET_ROW + ", 0"));
         evaState.addLine (new EvaLine (varMAX_VALID_REL_ROW + ", -1"));
         evaState.addLine (new EvaLine (varINCLUDE_RECORDNO + ", 0"));
      }

      int rCache   = evaState.rowOf (varMAX_CACHE);
      int rValid   = evaState.rowOf (varMAX_VALID_REL_ROW);
      int rInclude = evaState.rowOf (varINCLUDE_RECORDNO);

      if (! loadDataOnDemand ())
      {
         evaState.setValue ("" + recordsInHalfModel, rCache, 1);
         evaState.setValue ("" + recordsInHalfModel, rValid, 1);
      }
      else
      {
         evaState.setValue ("" + (240 + recordsInHalfModel), rCache, 1);
         evaState.setValue ("" + (-1 + recordsInHalfModel), rValid, 1);
      }

      MAX_CACHE        = stdlib.atoi (evaState.getValue(rCache, 1));
      INCLUDE_RECORDNO = stdlib.atoi (evaState.getValue(rInclude, 1));

      rvar_totalRows   = evaState.rowOf (varTOTAL_ROWS);
      rvar_ValidRelRow = evaState.rowOf (varMAX_VALID_REL_ROW);
      rvar_offsetRows  = evaState.rowOf (varOFFSET_ROW);
   }

   public EvaUnit getBaseModel ()
   {
      return halfModel;
   }

   protected boolean checkBaseModel ()
   {
      return  halfModel != null && evaBuffer != null && evaState != null;
   }

   //
   // access setget to state general variables
   //

   private int ensureStateVariable (String variableName)
   {
      if (!checkBaseModel ()) return -1;

      int rowof = evaState.rowOf (variableName);
      if (rowof != -1)
         return rowof;

      evaState.addLine (new EvaLine (new String [] { variableName }));
      return evaState.rowOf (variableName);
   }

   protected void setStateVariable (String variableName, String value)
   {
      evaState.setValue (value, ensureStateVariable(variableName), 1);
   }

   protected String getStateVariable (String variableName)
   {
      return evaState.getValue (ensureStateVariable(variableName), 1);
   }

   //
   // access setget to state variables
   //

   protected void setTotalRecords (int total)
   {
      evaState.setValue ("" + total, rvar_totalRows, 1);
   }

   // NOTE this method is for the state variable totalRows for
   //      other pruposes use getRowCount() from the TableModel interface
   protected int getTotalRecords ()
   {
      return stdlib.atoi (evaState.getValue (rvar_totalRows, 1));
   }

   protected void setValidRelRow (int RelRow)
   {
      evaState.setValue ("" + RelRow, rvar_ValidRelRow, 1);
   }

   protected int getValidRelRow ()
   {
      return stdlib.atoi (evaState.getValue (rvar_ValidRelRow, 1));
   }

   protected void setOffsetRows (int offset)
   {
      evaState.setValue ("" + offset, rvar_offsetRows, 1);
   }

   protected int getOffsetRows ()
   {
      return stdlib.atoi (evaState.getValue (rvar_offsetRows, 1));
   }

   //
   // ----
   //


   protected void setHeader (EvaLine header)
   {
      if (!checkBaseModel ()) return;

      if (evaBuffer.rows () == 0)
           evaBuffer.addLine (header);
      else
         evaBuffer.get(0).set (header);
   }

   protected void setHeaderAndData (Eva headerAndData)
   {
      if (!checkBaseModel ()) return;

      evaBuffer = headerAndData;
   }

   protected void initSetDataRel (int offset)
   {
      setOffsetRows  (offset);
      setValidRelRow (-1);
   }

   protected void setDataRel (int posRela, String compactedRow)
   {
      setDataRel (posRela, compactedRow, 0);
   }

   /**
      set data to be processed by expandRow

      // 29.05.2009 New parameter offsetColumn to facilitate clientCaller SQLlite error detection

   */
   protected void setDataRel (int posRela, String compactedRow, int offsetColumn)
   {
      if (!checkBaseModel ()) return;

      //(o) todo_EvaTable remove this dependency on de.elxala.db
      //          also make the compact-expand policy more flexible
      //
      String arr[] = de.elxala.db.utilEscapeStr.expandRow (compactedRow, offsetColumn);

      if (posRela > MAX_CACHE)
      {
         log.severe ("setDataRel", "bad use of ::setData : try to override MAX_CACHE");
         return;
      }

      if (evaBuffer.rows () <= posRela+1)
         evaBuffer.setValue ("", posRela+1, 0);

      evaBuffer.get (1+posRela).set (arr);
      setValidRelRow (posRela);
   }

   /**
      set data directly
   */
   protected void setDataRel (int posRela, String [] arr)
   {
      if (!checkBaseModel ()) return;

      if (posRela > MAX_CACHE)
      {
         log.severe ("setDataRel", "bad use of ::setData : try to override MAX_CACHE");
         return;
      }

      if (evaBuffer.rows () <= posRela+1)
         evaBuffer.setValue ("", posRela+1, 0);

      evaBuffer.get (1+posRela).set (arr);
      setValidRelRow (posRela);
   }

   public void obtainRow (int demandedRow)
   {
      if (demandedRow < 0 || demandedRow >= getTotalRecords ())
      {
         // can happen ...
         // err ("::expandRow " + demandedRow + " while total Rows are " + getTotalRecords ());
         return;
      }

      // a ver si la tengo ...
      int toca = demandedRow - getOffsetRows ();
      if (toca >= 0 && toca <= getValidRelRow ())
      {
         return; // la tengo!
      }

      // NOTE : could be more efficient for the up 1 down 1 scroll but for now it is ok!

      if (toca > 0)
      {
         setOffsetRows (demandedRow);
      }
      else
      {
         // to facilitate the fine sroll up
         setOffsetRows (Math.max (0, demandedRow - MAX_CACHE / 2));
      }

      loadRowsFromOffset (getOffsetRows ());
   }


   /**
      gets the index of the column name 'colName' where the columns are all
      visible and not visible 0..nColumns
   */
   public int getAllColumnIndex (String colName)
   {
      for (int ii = 0; ii < evaBuffer.cols (0); ii ++)
      {
         if (evaBuffer.getValue (0, ii).equalsIgnoreCase (colName))
            return ii;
      }

      return -1;
   }

   /*
      ==========================================
      Implementaciones de AbstractTableModel
      ==========================================
   */

   // implementation of AbstractTableModel
   public int getRowCount ()
   {
      //21.01.2007 16:35 avoiding return negative values although it seems that AbstractDataModel
      //                 has no problem with it ListModel does
      //                 (see getSize() implementation of ListModel in this class)
      //
      int size = getTotalRecords ();
      return size < 0 ? 0: size;
   }

   /**
      // implementation of AbstractTableModel

      Returns the number of visible columns including the first one
      that holds the record number if it were activated
   */
   public int getColumnCount ()
   {
      if (!checkBaseModel ()) return 0;

      String [] visi = private_visibleColumnNames;

      return (visi.length == 0) ? 0: (INCLUDE_RECORDNO + visi.length);
   }

   // implementation of AbstractTableModel
   public Class getColumnClass (int columnIndex)
   {
      String yo = "";

      return yo.getClass ();
   }

   // implementation of AbstractTableModel
   public boolean isCellEditable (int rowIndex, int columnIndex)
   {
      return true;
   }

   // implementation of AbstractTableModel
   public void setValueAt (Object aValue, int row, int col)
   {
      setAllValueAt ((String) aValue, row, realCol(col));
   }

   /**
      returns the value at position rowIndex, columnIndex

      NOTE: This method implements TableModel thus the rowIndex is in range 0..(totalRecords-1)
            which is different from our RecordNumber (1..totalRecords)
   */
   // implementation of AbstractTableModel
   public Object getValueAt (int rowIndex, int columnIndex)
   {
      return getAllValueAt (rowIndex, realCol(columnIndex));
   }

   // implementation of AbstractTableModel
   public String getColumnName (int columnIndex)
   {
      return getAllColumnName (realCol(columnIndex));
   }



   /**
      Returns the number of visible columns including the not visble
      ones and the first one that holds the record number if it were activated
   */
   public int getAllColumnCount ()
   {
      if (!checkBaseModel ()) return 0;

      return (evaBuffer.rows () == 0) ? 0: (INCLUDE_RECORDNO + evaBuffer.rows ());
   }


   /**
      Sets the value 'aValue' into the row 'row' and column 'col' where the columns
      follow the index of all columns and not only the visible ones
   */
   public void setAllValueAt (String aValue, int row, int col)
   {
      if (!checkBaseModel ()) return;

      obtainRow (row);
      evaBuffer.setValue (aValue, 1 + row - getOffsetRows (), col);
   }

   /**
      returns the value at position row 'rowIndex' and column 'AllColIndex'
      where the columns follow the index of all columns and not only the visible ones
   */
   public Object getAllValueAt (int rowIndex, int AllColIndex)
   {
      if (INCLUDE_RECORDNO == 1 && AllColIndex == 0)
         return "" + (1 + rowIndex);   // our record number is 1..totalRecords

      AllColIndex -= INCLUDE_RECORDNO;
      return getRealAt (rowIndex, AllColIndex);
   }

   /**
      returns the column name of the column 'AllColIndex'
      where the columns follow the index of all columns and not only the visible ones
   */
   public String getAllColumnName (int AllColIndex)
   {
      if (INCLUDE_RECORDNO == 1 && AllColIndex == 0)
         return "#(" + getRowCount () +")";

      AllColIndex -= INCLUDE_RECORDNO;

      return evaBuffer.getValue (0, AllColIndex);
   }

   private Object getRealAt (int row, int col)
   {
      if (row < 0 || col < 0) return "?1"; // absurd!
      if (row > getTotalRecords ()) return "?2";

      obtainRow (row);

      return evaBuffer.getValue (1 + row - getOffsetRows (), col);
   }


   private String [] private_visibleColumnNames = new String [] { };
   private int [] private_recol = new int [0];

   /*
      sets the active (or main) column which is the column to reflect
      in Lists, Combos etc where only one column is shown
   */
   public void setVisibleColumns (String [] columnNames)
   {
      //(o) TODO_Eva abstractEvaTable has to transform column visibles to real columns

//System.out.println ("que ben que tens a dada!!!");


//System.out.println ("me setan las columnas visibles al " + columnNames[0] + " etc");
//System.out.println ("anda por cierto mira evaBuffer");
//System.out.println (evaBuffer);
      private_visibleColumnNames = columnNames;
      private_recol = new int [columnNames.length];

      for (int ii = 0; ii < columnNames.length; ii ++)
      {
         private_recol[ii] = getAllColumnIndex (columnNames[ii]);
//System.out.println (columnNames[ii] + " remap to "+ private_recol[ii]);
      }
   }

   private int realCol (int colVisible)
   {
      //(o) TODO_Eva abstractEvaTable has to transform column visibles to real columns
      //return private_recol[colVisible];
      return colVisible;
   }



   /*
      ==========================================
      To get all elements in an array for lists (not abused!)
      It is the easiest way to implement a JList and JComboBox
      if not it is needed ListModel + Render
      ==========================================
   */
   private static final int MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS = 1024;

   public String [] getAllElements ()
   {
      int totSize = getSize ();
      if (totSize > MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS)
      {
         log.err ("getAllElements", "small List or ComboBox abused, the result might not be complete!");
         totSize = MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS;
      }

      String [] visi = private_visibleColumnNames;
      String [] allE = new String [totSize];

      for (int ii = 0; ii < totSize; ii ++)
      {
         allE[ii] = (String) getElementAt(ii);
      }

      return allE;
   }


   /*
      ==========================================
      Implementaciones de ListModel

      IMPORTANT NOTE!:
         To use ListModel in a JList (seting the model with setModel (...))
         it is necessary to provide the JList also with a renderer !!
         if not the result might be very strange and even somtimes it could
         be right rendered but it is not guaranteed!!
      ==========================================
   */

   // implementation of ListModel
   public Object getElementAt(int rowindex)
   {
      String [] visi = private_visibleColumnNames;
      String value = "";

      for (int ii = 0; ii < visi.length; ii ++)
      {
         int colindx = getAllColumnIndex (visi[ii]);
         value += ((ii > 0) ? ", ": "") + getAllValueAt (rowindex, colindx);
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
      //
      //      java.lang.NegativeArraySizeException
      //              at javax.swing.plaf.basic.BasicListUI.updateLayoutState(BasicListUI.java:1129)
      //              at javax.swing.plaf.basic.BasicListUI.maybeUpdateLayoutState(BasicListUI.java:1098)
      //              at javax.swing.plaf.basic.BasicListUI.getPreferredSize(BasicListUI.java:281)
      //              at javax.swing.JComponent.getPreferredSize(JComponent.java:1275)
      //              ...
      //
      // so avoid returning negative values
      // NOTE: This class also implements AbstractDataModel and its method
      //       getRowCount () has no problem with that (anyway we've changed it there too)
      //
      int size = getTotalRecords();
      return size < 0 ? 0: size;
   }

   // implementation of ListModel
   public void addListDataListener (ListDataListener l)
   {
   }

   // implementation of ListModel
   public void removeListDataListener (ListDataListener l)
   {
   }


   // EXTENSIONES-----------------------------------------------------------------
   //

   public Object getAbsoluteValueAt (int rowIndex, int columnIndex)
   {
      if (rowIndex >= 0)
      {
         return (String) getValueAt (rowIndex, columnIndex);
      }
      return "";
   }

   public String getCampo (int row, String nombre)
   {
      int indx = findColumn (nombre);

      if (indx >= 0)
      {
         return (String) getValueAt (row, indx);
      }
      log.err ("getCampo", "field with name " + nombre + " not found!");
      return "";
   }


   /*
      ==========================================
      Other methods
      ==========================================
   */

   /**
      build an anonymus (with no name) EvaUnit where each field is a Eva
      with name the name of the filed and value the value of the filed.

      e.g.: Supouse we have

         id     Name    Date
         ----    ------- ----------
         6625   the one 2003-02-03
         1923   second  2003-03-28

      the result EvaUnit of getRegister (2) will be

         ##
         <id> 1923
         <Name> second
         <Date> 2003-03-28

   */
//   EvaUnit getRegister (int row)
//   {
//      EvaUnit una = new EvaUnit ();
//
//      if (evaData != null)
//      {
//         for (int ii = 0; ii < evaData.cols (0); ii ++)
//         {
//            una.add (new Eva (evaData.getValue (0, ii), evaData.getValue (row, ii)));
//         }
//      }
//      return una;
//   }
   //-----------------------------------------------------------------
}
