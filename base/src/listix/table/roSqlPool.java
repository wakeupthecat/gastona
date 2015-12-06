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

import java.util.List;
import java.util.Vector;
import de.elxala.db.sqlite.tableROSelect;
import de.elxala.Eva.*;
import de.elxala.zServices.logger;

/**
      Alejandro Xalabarder
      12.11.2006 15:05
      SqlthePool

      Changed on 28.02.2009 11:26

   A pool to provide roViewTableModel objects without caching contents!
   The pool is useful just to reuse temporary files needed internally by roSqlPool

*/
public class roSqlPool
{
   private static logger log = new logger (null, "listix_command", null);
   private static int nElementsActive = 0;

   // Intern static pool of tableROSelect objects
   //
   private static List /* <tableROSelect> */ roSelectStackPool = new Vector();


   public static tableROSelect getElement (String dbName, String query, String previousSql)
   {
      //System.out.println ("getElement, nElementsActive " + nElementsActive + ", roSelectStackPool.size ()" + roSelectStackPool.size ());
      tableROSelect este = null;

      if (nElementsActive < 0)
      {
         log.severe ("roSqlPool::getElement", "misprogrammed controller?: nElementsActive of " + nElementsActive + " indicates that excesive number of disposes has been performed!");
         // continue to return something
      }

      if (nElementsActive >= 0 && nElementsActive < roSelectStackPool.size ())
      {
         este = (tableROSelect) roSelectStackPool.get (nElementsActive);

         // inicializa
         EvaUnit myDataAndCtrl =  new EvaUnit ();
         este.setNameDataAndControl (null, myDataAndCtrl, myDataAndCtrl);
         if (query != null && query.length() > 0)
            este.setSelectQuery (dbName, query, previousSql);

      }
      else
      {
         // nElementsActive should not be bigger than roSelectStackPool.size ()!
         //ASSERT (nElementsActive == roSelectStackPool.size ())

         //System.out.println ("quereamos usno");
         este = new tableROSelect (dbName, query, previousSql);
         roSelectStackPool.add (este);
      }
      nElementsActive ++;

      return este;
   }

   public static void disposeElement ()
   {
      nElementsActive --;
   }
}
