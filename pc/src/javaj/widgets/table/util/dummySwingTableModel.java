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

import javax.swing.table.AbstractTableModel;

/**
   implementation of AbstractTableModel
   required by swing JTable
*/
public class dummySwingTableModel extends AbstractTableModel
{
   public int getRowCount ()
   {
      return 0;
   }

   public int getColumnCount ()
   {
      return 0;
   }

   public Class getColumnClass (int columnIndex)
   {
      return "".getClass ();
   }

   public boolean isCellEditable (int row, int col)
   {
      return false;
   }

   public void setValueAt (Object aValue, int row, int col)
   {
   }

   public Object getValueAt (int row, int col)
   {
      return "";
   }

   public String getColumnName (int col)
   {
      return "";
   }
}
