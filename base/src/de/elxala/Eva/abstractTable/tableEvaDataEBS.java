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

import de.elxala.Eva.*;

/**


   NOTE: This class make no use of mustGetControl only mustGetEvaData
         therefore a evaTable based on it will make only use of one eva as expected
*/
public class tableEvaDataEBS extends baseEBS implements tableSwingAdaptable
{
   public tableEvaDataEBS (baseEBS ebs)
   {
      super (ebs);
   }

   /**
      number of records cached in data
   */
   public int getRecordCount ()
   {
      //int rowsParece = mustGetEvaData ().rows () - 1;
      //System.out.println (getName () + " getRecordCount ~ " + rowsParece);

      return Math.max (0, mustGetEvaData ().rows () - 1 /* for the header */);
   }

   /**
      absolute number of records (the same as getRecordCount for EVA)
   */
   public int getTotalRecords ()
   {
      return getRecordCount ();
   }

   public void setColumnNames (String [] columNames)
   {
      Eva dad = mustGetEvaData ();
      EvaLine lin0 = dad.get(0);

      if (lin0 != null)
           lin0.set (columNames);
      else dad.addLine (new EvaLine (columNames));
   }

   public String [] getColumnNames ()
   {
      Eva dad = mustGetEvaData ();
      EvaLine lin0 = dad.get(0);

      if (lin0 == null)
         return new String [0];

      return lin0.getColumnArray (); // EvaLine::getColumnArray () (that returns a String [])
   }

   /**
      returns the number of columns
   */
   public int getColumnCount ()
   {
      return getColumnNames ().length;
   }

   /**
      returns the column name of the column 'col'
   */
   public String getColumnName (int col)
   {
      return mustGetEvaData ().getValue (0, col);
   }

   /**
      returns the column index of the given column name
   */
   public int getColumnIndex (String columnName)
   {
      return mustGetEvaData ().colOf (columnName);
   }

   /**
      Sets the value 'aValue' into the row 'row' (0..N-1 records) and column 'col'
   */
   public void setValue (String aValue, int row, int col)
   {
      mustGetEvaData ().setValue (aValue, 1 + row, col);
   }

   /**
      Sets the value 'aValue' into the row 'row' (0..N-1 records) and column with name 'columnName'
   */
   public void setValue (String aValue, int row, String columnName)
   {
      mustGetEvaData ().setValue (aValue, 1 + row, getColumnIndex (columnName));
   }

   /**
      returns the value at position row 'row' (0..N-1 records) and column 'col'
      Note: if the row/col par does not exists the returned value is an empty string ""
            instead of null. To check if the position is valid use getColumns and getTotalRecords
   */
   public String getValue (int row, int col)
   {
      //String elo = mustGetEvaData ().getValue (1 + row, col);
      //System.out.println (getName () + " getValue (" + row + ", " + col + ") [" + elo + "]");

      return mustGetEvaData ().getValue (1 + row, col);
   }

//   /**
//      returns the value of column '' at position 0
//      Note: if the column does not exists the returned value is an empty string ""
//            instead of null. To check if the position is valid use getColumns and getTotalRecords
//   */
//   public String getValue (String columnName)
//   {
//      return getValue (columnName, 0);
//   }

   /**
      returns the value of column '' at position row 'row' (0..N-1 records)
      Note: if the row/col par does not exists the returned value is an empty string ""
            instead of null. To check if the position is valid use getColumns and getTotalRecords
   */
   public String getValue (String columnName, int row)
   {
      return getValue (row, getColumnIndex (columnName));
   }

   public boolean isEditable (int row, int col)
   {
      return true;
   }

   // implementation only for EVA tables !!

   public int findFirstRecord (String columnName, String value)
   {
      Eva evad = mustGetEvaData ();

      int col = evad.colOf (columnName);
      if (col == -1) return -1;

      // we would need rowOf with offset for rows = 1 but this method does not exist
      //
      for (int rr = 1; rr < evad.rows (); rr ++)
      {
         if (value.equals (evad.getValue (rr, col)))
            return rr;
      }

      return -1;
   }

   /**
      adds a new record and returns the row where it has been placed
   */
   public int newRecord (String [] values)
   {
      Eva evad = mustGetEvaData ();

      evad.addLine (new EvaLine (values));
      return evad.rows ();
   }

   /**
   */
   public void removeRecords ()
   {
      Eva evad = mustGetEvaData ();

      if (evad.rows () == 1) return;

      EvaLine cabe = evad.get (0);
      evad.clear ();
      evad.addLine (cabe);
   }

   /**
   */
   public boolean removeRecord (int row)
   {
      Eva evad = mustGetEvaData ();

      if (row < 1 || row > evad.rows ()) return false;

      evad.removeLine (row - 1);
      return true;
   }
}
