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
import de.elxala.langutil.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.logger;


/**
*/

/**

   tableCursor is the implementation of the movement of a
   generic tableAccessBase object (EVA, SQL, FILES, FOR ...)
   with some given restrictions (WHILESAME, ONDIFFERENT, THOSETHAT)


   Methods for initializing
      void    init (...)
      void    cleanData ()

   Methods for movement
      boolean increment ()
      boolean decrement ()


   Note about restrictions in SQL "cursors":

   Note that the logic of THOSETHAT (to filter values) make less sense
   for a SQL table because it is trivial to include it in the sql query.
   Nevertheless the logic of pivoting with WHILESAME and ONDIFFERENT is
   even more interesting specially for SQL tables, making possible
   to do a loop with inner sub-loops using just one sql query statement.
   It does mean, of course, that for this kind of loops this technique is
   very performant. A tipical example of these loops is a database report.


*/
public class tableCursor
{
   private static logger log = new logger (null, "listix_command", null);

   public static final int TPIVOT_NONE   = 0;
   public static final int TPIVOT_ONCE   = 1;
   public static final int TPIVOT_WHILE  = 2;

   class pivot
   {
      int type = TPIVOT_NONE;

      // column index of the pivot for the pivot types TPIVOT_ONCE and TPIVOT_WHILE
      int columnIndx = -1;

      // stores the column values until the pivot (0.. columnIndx)
      // for the pivot types TPIVOT_ONCE and TPIVOT_WHILE
      String [] pivotData = null;

      public pivot (int pType, int pColumnIndx)
      {
         type = (pColumnIndx >= 0) ? pType: TPIVOT_NONE;;
         columnIndx = pColumnIndx;
         pivotData = (type == TPIVOT_NONE) ? new String [0]: new String [columnIndx + 1];
      }

      // how many fields involves this pivot
      int columnIndex ()
      {
         return columnIndx;
      }

      boolean equalColumnValue (int col, String value)
      {
         //System.out.println ("comparamos col " + col + " ello ["+ value + "] acerca de [" + ((pivotFields[col] != null) ? pivotFields[col]: "--null--") + "]");
         return pivotData[col] != null && pivotData[col].equals (value);
      }

      void setColumnValue (int col, String value)
      {
         pivotData[col] = value;
      }
   }

   public tableAccessBase dAcc = null; // table associated
   public pivot thePivot = null;       // pivot for this tableCursor

   public int lastIncrementedPosition = 0;
   public boolean ownData = false;
   public String linkString = TextFile.RETURN_STR;
   public Vector arrFilter = null; //new Vector (); // Vector<tableSimpleFilter>

   public tableCursor (tableAccessBase access)
   {
      log.dbg (4, "tableCursor", "constructed");

      dAcc = access;
      lastIncrementedPosition = dAcc.currentRawRow ();
      dAcc.prevRow = dAcc.currentRawRow ();   //(o)!!!

      ownData = (false == dAcc.isRunning ());
      // if ownData is true then
      //    the cursor is already running that means that
      //    this is subcursor or nested cursor, when we finish (this cursor)
      //    the data has to remain for the other cursors.
      // else
      //    this cursor will start the running, thus at the end we can
      //    clear the data
   }

   public tableAccessBase data ()
   {
      return dAcc;
   }

   public void cleanData ()
   {
      if (ownData)
         dAcc.clean ();
   }

   private boolean checkSameColumn ()
   {
      if (dAcc.EOT ()) // end of table
         return false;

      if (thePivot == null)
         return true; // it does not really matters which value is returned in this case ...

      // all precedent columns have to be the same as well !!
      boolean same = true;
      for (int cc = 0; cc <= thePivot.columnIndex(); cc ++)
      {
         String currVal = dAcc.getValue (dAcc.currRow, cc);
         if (same)
            same &= thePivot.equalColumnValue (cc, currVal);

         if (! same)
         {
            thePivot.setColumnValue (cc, currVal);
            //DO NOT call break here! we have to fill completely the pivot!
         }
      }
      log.dbg (6, "tableCursor", "WHILE condition " + same);
      return same;
   }

   private boolean checkFilter ()
   {
      if (dAcc.EOT ()) // end of table
         return false;

      if (arrFilter == null) return true;  // no filter ? ok!

      String value = "";
      for (int ii = 0; ii < arrFilter.size (); ii ++)
      {
         tableSimpleFilter fil = (tableSimpleFilter) arrFilter.get (ii);
         value = dAcc.getValue (dAcc.currRow, fil.getAssociatedIndex ());
         if (! fil.passOperand1 (value))
         {
            if (log.isDebugging (6))
               log.dbg (6, "tableCursor", "FILTER " + ii + " did not pass [" + value + "]");
            return false;
         }
      }

      log.dbg (6, "tableCursor", "FILTER passed");
      return true;
   }

   protected void analyzeOptions (listixCmdStruct cmdData)
   {
      // PIVOT
      //
      String fieldWhile = cmdData.takeOptionString (new String [] { "WHILE", "WHILESAME", "SAME", "WHILEHEADER", "WHILESAMEHEADER", "SAMEHEADER" }, null);
      String fieldOnce  = cmdData.takeOptionString (new String [] { "DIFFERENT", "DIFFERENTHEADER", "ONCE", "ONCEHEADER", "ONCEPER" }, null);

      if (fieldWhile != null)
      {
         thePivot = new pivot (TPIVOT_WHILE, dAcc.colOf (fieldWhile));
         log.dbg (2, "tableCursor", "analyzeOptions, set Pivot WHILE " + fieldWhile + "");
      }
      else if (fieldOnce != null)
      {
         thePivot = new pivot (TPIVOT_ONCE, dAcc.colOf (fieldOnce));
         log.dbg (2, "tableCursor", "analyzeOptions, set Pivot ONCE " + fieldOnce + "");
      }


      // collect filters
      //
      String [] filterArg = null;
      do
      {
         filterArg = cmdData.takeOptionParameters (new String [] { "IF", "FILTER", "THOSE" }, true);
         if (filterArg == null) break;
         if (filterArg.length != 3)
         {
            log.err ("tableCursor", "analyzeOptions, wrong number of arguments " + filterArg.length + " in filter for column " + ((filterArg.length > 0) ? filterArg[0]: "?") + "");
            continue;
         }

         int iColumn = dAcc.colOf (filterArg[0]);
         String sOpe = filterArg[1];
         String sVal = filterArg[2];

         log.dbg (2, "tableCursor", "analyzeOptions, add filter " + filterArg[0] + " (col " + iColumn + ") " + sOpe + " " + sVal);

         if (arrFilter == null)
            arrFilter = new Vector (); // Vector<tableSimpleFilter>
         arrFilter.add (new tableSimpleFilter (sOpe, sVal, iColumn));
      }
      while (filterArg != null);


      // LINK STRING
      //
      linkString = TextFile.RETURN_STR; // default

      String linkStr = cmdData.takeOptionString (new String [] { "LINK", "ROWLINK" }, null);
      if (linkStr != null)
      {
         log.dbg (2, "tableCursor", "link String [" + linkStr + "]");
         linkString = linkStr;
      }
   }

   public void analyzeOptionsAndPivot (listixCmdStruct cmdData, int pivotType, String pivotField)
   {
      //   RUN TABLE, lsxFormat, [ option, value,  [ option, value ...] ]
      //
      //            WHILE SAME    field name
      //            ON DIFFERENT  field name
      //      IF,FILTER,THOSE     field name, operator, value
      //            ROW LINK      text to link rows
      //

      // look for options
      if (pivotType != TPIVOT_NONE)
      {
         thePivot = new pivot (pivotType, dAcc.colOf (pivotField));
         log.dbg (2, "tableCursor", "analyzeOptionsAndPivot, set Pivot type " + pivotType + " with value " + pivotField);
      }
      analyzeOptions (cmdData);
   }


   //
   // ===================  COMMANDS
   //
   public int set_RUNTABLE (listixCmdStruct cmdData)
   {
      return set_RUNTABLE (cmdData, TPIVOT_NONE, null);
   }

   public int set_RUNTABLE (listixCmdStruct cmdData, int pivotType, String pivotField)
   {
      // options has to be consumed anyway due to remainOptions!
      //
      analyzeOptionsAndPivot (cmdData, pivotType, pivotField);

      if (dAcc.EOT ())
      {
         //
         //log.err ("set_RUNTABLE", "Trying RUN TABLE but there is no table or it is ended!");
         log.dbg (6, "tableCursor", "set_RUNTABLE could not start, there is no table or it is empty");
         return 1;   // the command was RUN TABLE!
      }

      while (! checkFilter () && !dAcc.EOT ())
      {
         log.dbg (6, "tableCursor", "incremented due to filter");
         dAcc.incrementRow ();
         lastIncrementedPosition = dAcc.currRow;
      }

      // call checkSameColumn to initialize the reference values
      checkSameColumn ();

      return 1;
   }

   //(o)!!!
   public boolean decrement_RUNTABLE ()
   {
      if (thePivot == null)
      {
         log.err ("tableCursor::decrement_RUNTABLE", "tableCursor uninitialized (no set_RUNTABLE called)!");
         return false;
      }

      if (dAcc.currRow > dAcc.zeroRow ())
      {
         dAcc.currRow --;
         return true;
      }
      return false;
   }

/*//(o)!!!
   public boolean decrement_RUNTABLE ()
   {
      // check break condition (while same field, once per field ...)
      if (thePivot.type == TPIVOT_ONCE)
      {
         Oerr ("decrement_RUNTABLE in a ONCE RUN loop is not allowed!");
      }
      else
      {
         dAcc.decrementRow ();
         lastPosition = dAcc.currRow; // para que se incremente en el proximo increment_RUNTABLE
      }
      return true;
   }

*/
   /**
   */
   public boolean increment_RUNTABLE ()
   {
      log.dbg (4, "tableCursor", "prev0 " + dAcc.prevRow + ", curr0 " + dAcc.currRow + " lastIncPosition " + lastIncrementedPosition);
      dAcc.prevRow = dAcc.currRow;
      if (lastIncrementedPosition >= dAcc.currRow)
      {
         dAcc.incrementRow ();
         while (! checkFilter () && !dAcc.EOT ())
         {
            log.dbg (6, "tableCursor", "incremented due to filter");
            dAcc.incrementRow ();
         }
         lastIncrementedPosition = dAcc.currRow;
         log.dbg (4, "tableCursor", "lastIncPosition set to " + lastIncrementedPosition);
      }
      else
      {
         log.dbg (4, "increment_RUNTABLE", "not incremented because lastIncPosition =  " + lastIncrementedPosition + " and  dAcc.currRow " + dAcc.currRow);
         lastIncrementedPosition = dAcc.currRow;
      }
      log.dbg (4, "tableCursor", "prev1 " + dAcc.prevRow + ", new1 " + dAcc.currRow + " EOT " + dAcc.EOT ());

      //
      // independently from incrementing here the row or not
      // we have to check if new position complies with pivots etc...
      //

      // if the table is at the end then RUN is completed
      if (dAcc.EOT ())
         return false;

      // check break condition (while same field, once per field ...)
      if (thePivot != null)
      {
         switch (thePivot.type)
         {
            case TPIVOT_ONCE:
               while (checkSameColumn ())
               {
                  log.dbg (4, "tableCursor", "sameColumn, skip " + dAcc.currRow + " EOT " + dAcc.EOT ());
                  dAcc.incrementRow ();
                  lastIncrementedPosition = dAcc.currRow;
               }
               break;

            case TPIVOT_WHILE:
               if (! checkSameColumn ()) return false;
               log.dbg (4, "tableCursor", "sameColumn, continue");
               break;

            default:
               break;
         }
      }
      log.dbg (4, "tableCursor", "prev2 " + dAcc.prevRow + ", new2 " + dAcc.currRow + " EOT " + dAcc.EOT ());
      return ! dAcc.EOT ();
   }
}
