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

import java.util.*;
import listix.*;
import de.elxala.Eva.*;
import de.elxala.zServices.logger;

/**
   holds the table cursor stack used by listix

   Each time a command SET TABLE or RUN TABLE is performed a new table cursor is
   created and pushed in this stack. This is a LIFO stack, the last table cursor becomes
   the current one. The meaning of this LIFO behaviour is used in the method 'findValueColumn'.
   A column will be search first in the last table cursor added, if not found the will be
   seach in the previous table cursor and so on until the first table cursor of the stack.

*/
public class tableCursorStack
{
   private static logger log = new logger (null, "listix_command", null);

   private Vector pilaTablas = new Vector ();   /// stack of cursor tables  (Vector<tableCursor>)
   private tableCursor currentTable = null;     /// current table cursor

   /**
      returns the depth of the table stack, that is how many
      tables are nested
   */
   public int getDepth ()
   {
      return pilaTablas.size ();
   }

   public Vector getTableStack ()
   {
      return pilaTablas;
   }


   //use with care!
   public Vector /* <tableCursor> */ getInternVector ()
   {
      return pilaTablas;
   }

   /**
      shows the table stack
   */
   //private void xou ()
   //{
   //   System.out.println ("\nSTACK =================");
   //   for (int pp = 0; pp < pilaTablas.size (); pp ++)
   //   {
   //      tableCursor tabla = (tableCursor) pilaTablas.get (pp);
   //      System.out.println ("[" + pp + "] " + tabla.dAcc.getName () +  " own = " + tabla.ownData + " lastPos = " + tabla.lastIncPosition + " running = " + tabla.dAcc.isRunning ());
   //   }
   //   System.out.println ("\n==================STACK");
   //}

   /**
      pushes a new table into the the table stack
   */
   public void pushTableCursor (tableCursor tabla)
   {
      pilaTablas.add (tabla);
      currentTable = tabla;
      // xou ();
   }

   public boolean checkCurrentTable ()
   {
      if (currentTable != null) return true;
      log.err ("getCurrentRawRow", "there is no currentTable (null)");
      return false;
   }

   /**
      pops a table from the the table stack
   */
   public void popTableCursor ()
   {
      if (!checkCurrentTable ()) return;
      currentTable.cleanData ();
      pilaTablas.remove (pilaTablas.size () -1);
      currentTable = (pilaTablas.size () > 0) ? (tableCursor) pilaTablas.get (pilaTablas.size () - 1): null;
      // xou ();
   }

   /**
      gets the current row in its raw format
   */
   public int getCurrentRawRow ()
   {
      if (!checkCurrentTable ()) return 0;
      return currentTable.data ().currentRawRow ();
   }

   /**
      gets the current data row 0..nDataRows()
   */
   public int getCurrentDataRow ()
   {
      if (!checkCurrentTable ()) return 0;
      return currentTable.data ().currentDataRow ();
   }

   /**
      gets the number of data rows
   */
   public int getCurrentDataRows ()
   {
      if (!checkCurrentTable ()) return 0;
      return currentTable.data ().nDataRows ();
   }

   /**
      gets the link string which is the string used
   */
   public String getLinkString ()
   {
      if (!checkCurrentTable ()) return "";
      return currentTable.linkString;
   }

   /**
      Looks if exists a table in the stack of tables that has the column
      named 'columnName', if this table is found than the value of that column
      at the current row position is returned, otherwise returns null.
   */
   public String findValueColumn (String columnName, boolean oldRow)
   {
      if (pilaTablas == null || pilaTablas.size () == 0) return null;

      int colIndx = -1;
      int posLook = pilaTablas.size () - 1;
      tableCursor tablaLook = null;

      while (posLook >= 0)
      {
         tablaLook = (tableCursor) pilaTablas.get (posLook);
         int rowLook = (oldRow) ? tablaLook.data ().prevRow: tablaLook.data ().currRow;

         colIndx = tablaLook.data ().colOf (columnName);
         //System.out.println ("columnName es " + columnName + " corresponde a un index " + colIndx);

         boolean found = (colIndx != -1);
         if (! oldRow && tablaLook.data ().EOT ())
            found = false;

         if (found)
         {
            return tablaLook.data ().getValue (rowLook, colIndx);
         }
         posLook --;
      }

      // not found !
      return null;
   }

   /**
      the current table has reach the End Of Table (EOT)
   */
   public boolean isCurrentTableEOT ()
   {
      if (!checkCurrentTable ()) return true; //(o) TOSEE changed to true on 10.12.2009 19:07
      return currentTable.data ().EOT ();
   }

   //
   // ===================  COMMANDS
   //

   /**
      finalizes the current table (pops it from the stack)
   */
   public void end_RUNTABLE ()
   {
      if (!checkCurrentTable ()) return;
      popTableCursor ();
   }

   /**
      sets the command RUN TABLE into the current table
      that produces a new push into the table stack
   */
   public int set_RUNTABLE (listixCmdStruct cmdData)
   {
      if (!checkCurrentTable ()) return 1;
      if (currentTable.data ().isRunning ())
      {
         tableCursor cur = new tableCursor (currentTable.data ());
         pushTableCursor (cur);
      }
      else
      {
         currentTable.data ().setRunning ();
      }
      return currentTable.set_RUNTABLE (cmdData);
   }

   /**
      increments a row in the current table cursor
   */
   public boolean increment_RUNTABLE ()
   {
      if (!checkCurrentTable ()) return false;
      return currentTable.increment_RUNTABLE ();
   }

   /**
      decrements a row in the current table cursor
   */
   public boolean decrement_RUNTABLE ()
   {
      if (!checkCurrentTable ()) return false;
      return currentTable.decrement_RUNTABLE ();
   }
}
