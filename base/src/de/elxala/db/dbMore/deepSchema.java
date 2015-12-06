/*
library de.elxala
Copyright (C) 2009 Alejandro Xalabarder Aulet

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

package de.elxala.db.dbMore;

import java.util.*;
import de.elxala.Eva.*;
import de.elxala.langutil.Cadena;
import de.elxala.zServices.*;

//import de.elxala.db.dbMore;

/**
   Utility to generate the deep schema which is a list of "all" possible deep columns in a database
   from a given connection table and the standard schema of the database. Not all possible deep columns
   can be included since this can be easily unlimited if, for example, a table connects with itself or due
   to a circular connection (e.g. A connects to B, B to C, and C to A). Thus a limited recursivity is
   applied.

   connection structure (connName, sourceTable  , sourceKey  , targetTable, targetKey)
   schema structure     (id, tabType, tableName, columnCid, columnName, columnType, not_null, def_value, pk)

   The schema structure is taken from the result of the command DATABASE SCHEMA

*/
public class deepSchema
{
   private logger log = new logger (this, "de.elxala.db.dbMore", null);

   private static final int XEM_COL_TABID = 0;
   private static final int XEM_COL_TABTYPE = 1;
   private static final int XEM_COL_TABNAME = 2;
   private static final int XEM_COL_COLID = 3;
   private static final int XEM_COL_COLNAME = 4;

   private static final int SCHEMA_INDX0 = 1;
   private static final int CONNTAB_INDX0 = 1;

   private List tabPila = null;
   private Eva evaDBSchema = null;
   private Eva evaConnTab = null;
   private Eva evaDeepXem = null;

   private int limitRecursivity = 2;

   public Eva getDeepSchema (Eva schema, Eva connectionTable)
   {
      Eva deep = new Eva ("deepSchema");
      getDeepSchema (deep, schema, connectionTable);
      return deep;
   }

   private boolean initDeepSchema (Eva resultDeepSchema, Eva schema, Eva connectionTable)
   {
      if (resultDeepSchema == null) return false;
      if (schema == null) return false;
      //if (connectionTable == null) return false;

      evaDBSchema = schema;
      evaConnTab = connectionTable;
      evaDeepXem = resultDeepSchema;
      evaDeepXem.clear ();
      evaDeepXem.setValue ("deepColumn", 0, 0);
      tabPila = new Vector ();

      return true;
   }

   public void getDeepSchema (Eva resultDeepSchema, Eva schema, Eva connectionTable, String tableName)
   {
      getDeepSchema (resultDeepSchema, schema, connectionTable, tableName, 2);
   }

   public void getDeepSchema (Eva resultDeepSchema, Eva schema, Eva connectionTable, String tableName, int maxRecursive)
   {
      limitRecursivity = maxRecursive;
      if (tableName == null || tableName.length () == 0)
      {
         getDeepSchema (resultDeepSchema, schema, connectionTable);
         return;
      }

      if (!initDeepSchema (resultDeepSchema, schema, connectionTable)) return;
      addAllConnectionsAndColumns (tableName, tableName + " ");
   }

   public void getDeepSchema (Eva resultDeepSchema, Eva schema, Eva connectionTable)
   {
      if (!initDeepSchema (resultDeepSchema, schema, connectionTable)) return;

      List allTables = getAllTables ();
      for (int ii = 0; ii < allTables.size (); ii ++)
      {
         String tableName = (String) allTables.get(ii);
         addAllConnectionsAndColumns (tableName, tableName + " ");
      }
   }

   private List getAllTables ()
   {
      List myList = new Vector ();
      for (int ii = SCHEMA_INDX0; ii < evaDBSchema.rows (); ii ++)
      {
         String tableName = evaDBSchema.getValue (ii, XEM_COL_TABNAME);
         //it is suposed that schema does not contain __dbMore_connections but ...
         if (tableName.equals ("__dbMore_connections")) continue;
         if (myList.contains (tableName)) continue;

         myList.add (tableName);
      }
      return myList;
   }

   private void addAllConnectionsAndColumns (String tableName, String base)
   {
      // check recursivity of connections
      //
      int cuento = 0;
      for (int ii = 0; ii < tabPila.size (); ii ++)
         cuento += ((String) tabPila.get(ii)).equals (tableName) ? 1: 0;

      if (cuento >= limitRecursivity) return;

      //push
      tabPila.add (tableName);

      String lastConnTab = "";
      if (evaConnTab != null)
      {
         // go through all connections for this table (tableName)
         // and add all associated fields to that connection
         // NOTE: if the connection uses more than one key it occupies more entries, these has to be skipped!
         for (int ii = CONNTAB_INDX0; ii < evaConnTab.rows (); ii ++)
         {
            String src  = evaConnTab.getValue (ii, dbMore.CONN_INDX_SOURCETABLE);
            if (!src.equals (tableName)) continue; // is not a connection from this table

            String conn = evaConnTab.getValue (ii, dbMore.CONN_INDX_NAME);
            String tgt  = evaConnTab.getValue (ii, dbMore.CONN_INDX_TARGETTABLE);

            // SALTARSE TODOS LOS connName+sourceTable iguales!
            if (lastConnTab.equals (conn + src)) continue; // it is just another key field of the last connection
            lastConnTab = conn + src;

            //============ Add all fields of this connection
            addAllConnectionsAndColumns (tgt, base + conn + " ");
         }
      }

      // add all fields of the table
      int indx = findTableIndx (tableName);
      String tabId = evaDBSchema.getValue (indx, XEM_COL_TABID);
      while (tabId.equals (evaDBSchema.getValue (indx, XEM_COL_TABID)))
      {
         evaDeepXem.addLine (new EvaLine (base + evaDBSchema.getValue (indx, XEM_COL_COLNAME)));
         indx ++;
      }

      //pop
      tabPila.remove (tabPila.size ()-1);
   }

   private int findTableIndx (String tableName)
   {
      for (int ii = SCHEMA_INDX0; ii < evaDBSchema.rows(); ii ++)
         if (tableName.equals(evaDBSchema.getValue(ii, XEM_COL_TABNAME))) return ii;

      return -1;
   }

   public static void main (String [] aa)
   {
      Eva schemaDB = new Eva ("dbSchema");

      schemaDB.addLine (new EvaLine ("tabId, type   , name , coid ,  colName"));
      schemaDB.addLine (new EvaLine ("0, table  , projecto , 0    ,  pid"));
      schemaDB.addLine (new EvaLine ("0, table  , projecto , 1    ,  leaderid"));
      schemaDB.addLine (new EvaLine ("0, table  , projecto , 2    ,  nasmensProj"));
      schemaDB.addLine (new EvaLine ("0, table  , projecto , 3    ,  cosasProj"));

      schemaDB.addLine (new EvaLine ("1, table  , persona , 0    ,  id"));
      schemaDB.addLine (new EvaLine ("1, table  , persona , 1    ,  pareid"));
      schemaDB.addLine (new EvaLine ("1, table  , persona , 2    ,  mareid"));
      schemaDB.addLine (new EvaLine ("1, table  , persona , 3    ,  cosasPersonatxe"));

      schemaDB.addLine (new EvaLine ("2, table  , capitulos , 0    ,  pid"));
      schemaDB.addLine (new EvaLine ("2, table  , capitulos , 1    ,  capi"));
      schemaDB.addLine (new EvaLine ("2, table  , capitulos , 2    ,  modafasaons"));
      schemaDB.addLine (new EvaLine ("2, table  , capitulos , 3    ,  ostras"));

      schemaDB.addLine (new EvaLine ("3, table  , dedica , 0    ,  pid"));
      schemaDB.addLine (new EvaLine ("3, table  , dedica , 1    ,  deka"));
      schemaDB.addLine (new EvaLine ("3, table  , dedica , 2    ,  somastros"));

      schemaDB.addLine (new EvaLine ("4, table  , notas , 0    ,  proyid"));
      schemaDB.addLine (new EvaLine ("4, table  , notas , 1    ,  leaderIdent"));
      schemaDB.addLine (new EvaLine ("4, table  , notas , 2    ,  notases"));

      Eva connTable = new Eva ("connTable");

      connTable.addLine (new EvaLine ("linkName, table     , srcField, targetTable ,  targetField"));
      connTable.addLine (new EvaLine ("pare    , persona   , pareid  , persona     ,  id"));
      connTable.addLine (new EvaLine ("mare    , persona   , mareid  , persona     ,  id"));
      connTable.addLine (new EvaLine ("lead    , projecto  , leaderid, persona     ,  id"));
      connTable.addLine (new EvaLine ("proy    , capitulos , pid     , projecto    ,  pid"));
      connTable.addLine (new EvaLine ("capitul , dedica    , pid     , capitulos   ,  pid"));

      connTable.addLine (new EvaLine ("proylead , notas    , proyid     , projecto   ,  pid"));
      connTable.addLine (new EvaLine ("proylead , notas    , leaderIdent, projecto   ,  leaderid"));

      connTable.addLine (new EvaLine ("proy     , notas    , proyid     , projecto   ,  pid"));
      connTable.addLine (new EvaLine ("lead     , notas    , leaderIdent, persona    ,  id"));

      deepSchema o = new deepSchema ();
      Eva deepXem = o.getDeepSchema (schemaDB, connTable);

      System.out.println ("Tablas schema = " + schemaDB);
      System.out.println ("Tabla conexiones = " + connTable);
      System.out.println ("Deep schema = " + deepXem);
   }
}
