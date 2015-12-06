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
   simple memory database based on evas


   EBS class diagram
   ----------------------------------------------------------------------------------



                   baseEBS(*e)
           -----------------------------------
            ^                             ^
            |                             |
            |                             |
      widgetEBS(*w)                      tableEvaDataEBS(*e)
   ----------------------          ------------------------------
            ^                          ^                 ^
            |                          |                 |
   used by almost all            tableEvaDB(*e)   absTableWindowingEBS(*e)
   zWidgets except those                           -----------------------------
   which data is table based                             ^                 ^
                                                         |                 |
                                                         |                 |
                                                   tableROSelect(*s)   tableWidgetBaseEBS(*t)
                                                                       ---------------------
                                                                           ^
                                                                           |
                                                                       tableEBS(*t)



            from package ...
      (*e) de.elxala.Eva.abstractTable
      (*w) javaj.widgets.basics
      (*t) javaj.widgets.table
      (*s) de.elxala.db.sqlite

*/
public class tableEvaDB extends tableEvaDataEBS
{
   public tableEvaDB (baseEBS ebs)
   {
      super (ebs);
   }

   public tableEvaDB (String databaseName)
   {
      super (new baseEBS ("protokoller", new EvaUnit (), null));
   }

   public void setScheme (EvaUnit unit)
   {
      EUContainers[DATA] = unit;
   }

   public void setScheme (String [] tableNames, String [] fieldSpec)
   {
      if (tableNames.length != fieldSpec.length)
      {
         log.err ("tableEvaDB.setScheme", "wrong sizes tableNames (" + tableNames.length + ") fieldSpec (" + fieldSpec.length + ")!");
         return;
      }
      EvaUnit eu = mustGetContainer (DATA);
      eu.clear ();
      for (int ii = 0; ii < tableNames.length; ii ++)
      {
         eu.add (new Eva (tableNames[ii], new EvaLine (fieldSpec[ii])));
      }
   }

   /**
      return the eva associated to this table or null if it does not exists
   */
   public Eva getTableAsEva (String tableName)
   {
      return getAttribute (DATA, false, tableName);
   }


   public tableEvaDataEBS getTableAsEBS (String tableName)
   {
      // note that the database itself is a tableEvaDataEBS
      // but its name correspond to the database name and not to the table name!
      // therefore we wrap the whole structure into a new tableEvaDataEBS
      // with the correct name

      return new tableEvaDataEBS (new baseEBS (tableName, getData (), null));
   }
}
