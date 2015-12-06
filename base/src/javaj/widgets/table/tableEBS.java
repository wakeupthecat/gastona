/*
package javaj.widgets
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

package javaj.widgets.table;

import de.elxala.Eva.abstractTable.*;
import de.elxala.db.sqlite.*;
import de.elxala.Eva.*;

import de.elxala.mensaka.*;


/**
   tableEBS: final class to be used by Aparato's and Mando's

   NOTE:
      Although tableROSelect is used this class is not even used here
      just the constant texts are used. We could place this texts in another class
      but for comodity we don't do it.


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
public class tableEBS extends tableWidgetBaseEBS
{
   public tableEBS (baseEBS ebs)
   {
      super (ebs);
   }

   public tableEBS (String name, EvaUnit pData, EvaUnit pControl)
   {
      super (new baseEBS (name, pData, pControl));
   }

   /**
      sets the sqlite DB connection for the table
   */
   public void setDBConnection (String sqliteDBName, String SelectSQL)
   {
      setSimpleAttribute (DATA, tableROSelect.sATTR_DB_DATABASE_NAME, sqliteDBName);
      setSimpleAttribute (DATA, tableROSelect.sATTR_DB_SQL_SELECT_QUERY, SelectSQL);
   }

   /**
      returns the sqlite DB name of the connection of the table if this
      exists, if it does not exist returns ""
   */
   public String getSqliteDatabaseName ()
   {
      return getSimpleAttribute(DATA, tableROSelect.sATTR_DB_DATABASE_NAME);
   }

   /**
      returns the sqlite select query of the connection of the table if this
      exists, if it does not exist returns ""
   */
   public String getSqliteSelectQuery ()
   {
//      Eva eQuery = getAttribute (DATA, false, tableROSelect.sATTR_DB_SQL_SELECT_QUERY);
//      System.out.println ("eQuery rows = " + eQuery.rows ());
//      System.out.println ("eQuery [" + eQuery + "]");
//      System.out.println ("si te lo colecto [" + eQuery.getAsText () + "]");
//      System.out.println ("by otro lado eQuery [" + getSimpleAttribute(DATA, tableROSelect.sATTR_DB_SQL_SELECT_QUERY) + "]");
      return getSimpleAttribute(DATA, tableROSelect.sATTR_DB_SQL_SELECT_QUERY);
// FATAL!!!
//      Eva eQuery = getAttribute (DATA, false, tableROSelect.sATTR_DB_SQL_SELECT_QUERY);
//      return (eQuery == null) ? null: eQuery.getAsText ();
   }

   /**
      returns true if it is a database table (otherwise is an Eva Table)
   */
   public boolean isDatabaseTable ()
   {
      Eva query = getAttribute(DATA, tableROSelect.sATTR_DB_SQL_SELECT_QUERY);
      return (query != null);
   }

   public void setDataControlAttributes (EvaUnit pData, EvaUnit pControl, String [] pairAttValues)
   {
      setNameDataAndControl (null, pData, pControl);
      setArrayOfSimpleAttributes (pairAttValues);
   }

   // thought for message 
   //   MSG, widgetName data!, att1, val1, att2, val2 ...
   //
   public void setArrayOfSimpleAttributes (String [] params)
   {
      if (params == null || params.length == 0) return;
      
      for (int pp = 0; pp+1 < params.length; pp += 2)
      {
         setSimpleAttribute (DATA, params[pp], params[pp+1]);
      }
   }

   /**
      returns true if the virtual column for record count has to be shown
      Note that if not specified, default value is true
   */
   public boolean hasVirtualRecordCountColumn ()
   {
      return ! "1".equals (getSimpleAttribute(DATA, "hideCountColumn"));
   }
}

