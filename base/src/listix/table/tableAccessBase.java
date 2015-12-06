/*
library listix (www.listix.org)
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

package listix.table;

import listix.*;
import de.elxala.zServices.logger;

/**
   abstract class that represents the basic access to a generic listix table

   the following methods has to be implemented by the class that
   implement the real table: isValid, BOT, EOT, colOf, getName, getValue, rawRows, zeroRow

      void    setCommand (listixCmdStruct cmdData)
      boolean isValid ()
      boolean BOT     ()
      boolean EOT     ()
      int     colOf   (String colName)  returns the


*/
public abstract class tableAccessBase
{
   public int currRow = zeroRow ();
   public int prevRow = zeroRow ();
   // public int lastIncrementedPosition = 0;


   public abstract boolean isValid ();    /// if the table is valid (it is initialized etc)
   public abstract boolean BOT ();        /// returns true if Begin Of Table
   public abstract boolean EOT ();        /// returns true if End Of Table
   public abstract int     rawRows ();    /// number of total rows (data + header)
   public abstract int     zeroRow ();    /// index of first data row
   public abstract void rowIsIncremented (); // a new row is requested; oportunity to retrieve data if some cache mechanism is used

   /**
      sets the table data from the command 'wholeCommand', the parameter 'caller' could be
      needed to complement the information needed by the SET TABLE command
      for instance some table types could need caller.getDefaultDBName () or getGlobalData () etc
   */
//   public abstract boolean setCommand_SETTABLE (String [] wholeCommand, listix caller);

   /**
      sets the command data for the listix table
   */
   public abstract boolean setCommand (listixCmdStruct cmdData);

   public abstract int    columns  ();                   /// returns the number of columns
   public abstract String colName  (int colIndex);       /// returns the column name of the 'columnIndex' (0..cols()-1)
   public abstract int    colOf    (String colName);     /// returns the column index (0..maxcol-1) of the column named 'colName'
   public abstract String getName  ();                   /// returns the name of the table
   public abstract String getValue (int row, int col);   /// returns the value of the row (data row) with index 'row' and column index 'col'

   /**
       called when the table is not anymore needed (unreferenced)
       to be implemented by tableCursor classes that need cleanup
   */
   public abstract void clean ();

   /// returns the current raw-row (not data row!)
   public int currentRawRow ()
   {
      return currRow;
   }

   /// returns the current data-row
   public int currentDataRow ()
   {
      return currRow - zeroRow ();
   }

   /// returns the total number of data rows
   public int nDataRows ()
   {
      return rawRows () - zeroRow ();
   }

   /// increments a row, jumping if needed the rows that have to be filtered
   public boolean incrementRow ()
   {
      currRow ++;
      rowIsIncremented ();
      return ! EOT ();
   }
   
   /// decrements a row, jumping if needed the rows that have to be filtered
   public boolean decrementRow ()
   {
      currRow --;
      return currRow >= zeroRow ();
   }

   /// sets the current row to the begining of the table
   public void rewind ()
   {
      currRow = zeroRow ();
   }

   // to know if a run table owns the table or not
   //
   private boolean running = false;

   /// returns if the running has started on this table
   public boolean isRunning ()
   {
      return running;
   }

   public void setRunning ()
   {
      running = true;
   }
}
