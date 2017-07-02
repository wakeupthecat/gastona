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


import java.util.*;
import de.elxala.langutil.*;
import de.elxala.Eva.*;


/**
   Implements the listix command "LOOP, FOR ..."

      LOOP, FOR, indexName, initialValue, endValue, [ increment (1) ]

   Example1: Simply enumerate the number of mounths in a year

      <enumerateMonths>
         LOOP, FOR,, 1, 12
            ,, // month @<forIndex>

   Example2: Print out the tables of multiplication from 1 to 9

      <multTables>
         // TABLAS DE MULTIPLICAR:
         //
         LOOP, FOR, multiploA, 1, 9
            ,, @<tablaDel>

      <tablaDel>
         //
         // tabla del @<multiploA>:
         //
         LOOP, FOR, multiploB, 1, 9
             ,, //    @<multiploA> x @<multiploB> = @<opera>

      <opera>
         =, multiploA * multiploB


*/
public class tableAccessFor extends tableAccessBase
{
   public int initialValue = 0;
   public int endValue = 0;
   public int increment = 1;
   public int totalRows = 0;
   public String indexName = "forIndex";

   //    LOOP, FOR, indexName, initialValue, endValue, [ increment (1) ]
   //
   public boolean setCommand (listixCmdStruct cmdData)
   {
      if (!cmdData.checkParamSize (4, 5))
         return false;

      if (cmdData.getArg(1).length() > 0)
         indexName = cmdData.getArg(1);

      initialValue = stdlib.atoi (cmdData.getArg(2));
      endValue     = stdlib.atoi (cmdData.getArg(3));
      increment    = stdlib.atoi (cmdData.getArg(4));

      if (increment == 0) increment = 1;

      totalRows = 1 /* column header */ + (endValue - initialValue) / increment;

      currRow = zeroRow ();

      if (! isValid ())
      {
         cmdData.getLog ().err ("LOOP FOR", "command is invalid!");
         return false;
      }

      return true;
   }

   public void clean ()
   {
   }

   public int zeroRow ()
   {
      return 1;
   }

   public boolean isValid ()
   {
      return true;
   }

   public boolean BOT ()
   {
      return currRow == zeroRow ();
   }

   public boolean EOT ()
   {
      //System.out.println ("EOTs ? con " + currRow + " >= " + rawRows () + " me dice que " + (currRow >= rawRows () ? "SIN": "NOLS"));
      return currRow >= rawRows ();
   }

   public int columns ()
   {
      return 1;
   }

   public String colName (int colIndex)
   {
      if (colIndex == 0)
         return indexName;
      return "";
   }

   public int colOf (String colName)
   {
      return colName.equals (indexName) ? 0: -1;
   }

   public String getName ()
   {
      return "tableCursorFor";
   }

   public String getValue (int row, int col)
   {
      if (col != 0)
      {
         //that.log().err ("LOOP FOR", "wrong column " + col + " in method getValue, LOOP FOR has only one column (col = 0)!");
         return "";
      }

      if (row == 0)
           return indexName;
      else return "" + (initialValue + (row-1) * increment);
   }

   public int rawRows ()
   {
      return totalRows + 1 /* for the header */;
   }

   public void rowIsIncremented ()
   {
   }
}
