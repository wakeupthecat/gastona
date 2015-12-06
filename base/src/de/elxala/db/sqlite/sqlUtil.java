/*
library de.elxala
Copyright (C) 2010 Alejandro Xalabarder Aulet

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

package de.elxala.db.sqlite;

import java.io.File;

import java.util.Vector;
import java.util.List;
import java.util.regex.*;  // Pattern Matcher etc for sqlite error detection


import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.streams.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import de.elxala.Eva.*;
import de.elxala.db.*;

/*
   20.02.2010 14:39

   SQL util

*/
public class sqlUtil
{
   public static final String sGLOB_PROPERTY_DB_DEFAULT_DATABASE_NAME = "gastona.defaultDB";
   public static final String sGLOB_PROPERTY_DB_DEFAULT_ATTACHED_DBS  = "gastona.defaultDBaliasAttach";


   public static String getGlobalDefaultDB ()
   {
      return System.getProperty(sGLOB_PROPERTY_DB_DEFAULT_DATABASE_NAME, "");
   }

   public static void setGlobalDefaultDB (String defaultDBName)
   {
      System.setProperty(sGLOB_PROPERTY_DB_DEFAULT_DATABASE_NAME, defaultDBName);
   }


   /*
      for example:

         gastona.defaultDBaliasAttach = "data/midb.db, auxi, cache/auxdb.db, custom, cache/customdb.db"

      returns

        <attachedDBs>
             alias , dbName
             auxi  , cache/auxdb.db
             custom, cache/customdb.db

   */
   public static Eva getGlobalDefaultDBaliasAttach ()
   {
      String attachString = System.getProperty(sGLOB_PROPERTY_DB_DEFAULT_ATTACHED_DBS, "");
      if (attachString.equals ("")) return null;

      String [] arrDbName = Cadena.simpleToArray (attachString, ",");

      Eva eva = new Eva("attachedDBs");
      eva.addLine (new EvaLine (new String [] { "alias", "dbName" } ));
      for (int ii = 0; ii+1 < arrDbName.length; ii += 2)
      {
         eva.addLine (new EvaLine (new String [] { arrDbName[ii].trim (), arrDbName[ii+1].trim () } ));
      }
      return eva;
   }

   public static String getGlobalDefaultDBaliasAttachQuery ()
   {
      Eva att = getGlobalDefaultDBaliasAttach ();
      if (att == null) return "";

      String sal = "";
      for (int ii = 1; ii < att.rows (); ii ++)
         sal += "ATTACH DATABASE \"" + att.getValue (ii, 1) + "\" AS " + att.getValue (ii, 0) + ";\n";

      return sal;
   }

   public static String getGlobalDefaultDBaliasDetachQuery ()
   {
      Eva att = getGlobalDefaultDBaliasAttach ();
      if (att == null) return "";

      String sal = "";
      for (int ii = 1; ii < att.rows (); ii ++)
         sal += "DETACH DATABASE " + att.getValue (ii, 0) + ";\n";

      return sal;
   }


   /**
      get the first result 'selectQuery' converting it into a long
      if the query does not success return -1
      (do not use this method for id's that can be negative!)
   */
   public static long getID (String dbName, String selectQuery)
   {
      tableROSelect taRO = new tableROSelect (dbName, selectQuery);
      long ID = taRO.getRecordCount () == 0 ? -1: stdlib.atol (taRO.getValue (0, 0));
      taRO.dispose ();

      return ID;
   }

   /**
      get the next id for the field 'idName' of the table 'tableName' of the database 'dbName'
      by adding 1 to the highest current value, if there were no record in this table the value
      'defaultValue' will be returned
   */
   public static long getNextID (String dbName, String tableName, String idName, long defaultValue)
   {
      tableROSelect taRO = new tableROSelect (dbName, "SELECT " + idName + " FROM " + tableName + " ORDER BY " + idName + " DESC LIMIT 1");
      long lastID = (taRO.getRecordCount () == 0) ? defaultValue: (1 + stdlib.atol (taRO.getValue (idName, 0)));
      taRO.dispose ();

      return lastID;
   }
}

