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

/**
   @date   08.02.2007 20:36
   @author Alejandro Xalabarder

   Interface sufficient to make a generic Eva table adapter to JTable or JList !

   NOTE:
   We use adapters in order to not conditionate the eva tables to the names and semantics of
   JTable and JList (getRowCount etc) and keep them fully independent of swing.
*/
public interface tableSwingAdaptable
{
   public int     getRecordCount ();
   public int     getColumnCount ();
   public String  getColumnName  (int col);

   public boolean isEditable (int rowIndex, int columnIndex);
   public void    setValue   (String aValue, int row, int col);
   public String  getValue   (int row, int col);
}
