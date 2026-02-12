/*
library de.elxala
Copyright (C) 2009-2022 Alejandro Xalabarder Aulet

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
import de.elxala.zServices.*;

/**
   2022-06-26 22:27:37

   Utility to generate the "DEEP SELECT" SQL expression from a list of deep columns and connections

   ------ Example :

      having the database schema

            TABLE tabCountry (cCode, name)
            TABLE tabAuthor  (auID, name, birthDate, countryCode)
            TABLE tabWork    (workID, authorId, name, date, description)

      we want to connect tabWork with tabAutor using authorId and auID from each table
      and tabAuthor with tabCountry using countryCode and cCode, giving the connection
      names "author" and "country" for each.

      these connections are expressed as follows

               <myConnections>
                  author, tabWork, authorId, tabAuthor, auID
                  country, tabAuthor, countryCode, tabCountry, cCode

      then we want to generate a SQL SELECT query for the set of deep columns

               <myDeepColumns>
                  workID
                  name
                  date
                  author name
                  author country name

      the result would be

             SELECT
                tabWork.workID AS workID
                , tabWork.name AS name
                , tabWork.name AS date
                , author.name AS author_name
                , author_country.name AS author_country_name
             FROM
                tabWork
                , tabAuthor AS author
                , tabCountry AS author_country
             WHERE
                tabWork.authorId == author.auID
                 AND author.countryCode == author_country.cCode

      which in turn executed on a database could give a result like this

             workID | name            | date   | author_name     | author_country_name
             -------|-----------------|--------|-----------------|--------------------
              8402  | The man who sold| 1970   | David Bowie     | UK
              4318  | Dioptria        | 1970   | Pau Riba        | CAT
              7211  | Horses          | 1975   | Patti Smith     | US


   ------ Code for the example:

    In order to do that using deepTableConnector class and 
    assuming that we have loaded the previous data in two Eva variables evaConnections and evaDeepColumns  


      // create the object with the connections
      deepTableConnector matxaca = new deepTableConnector (evaConnections);
      
      // do the actual job of generating the select data
      matxaca.resolveConnections (evaDeepColumns);         

      // print out the SELECT
      System.out.printf (matxaca.getSQL());


   ------ Notes about the source code

      The main algorithm for generating a SQL from deep columns and connections
      is implemented in the function

         public boolean resolveConnectionspublic (...)

      it is somehow intricate. Its explained in a graphical way in the article

         https://www.codeproject.com/Articles/75601/The-Deep-Table

      in the section "Algorithm for Deep Select"

*/
public class deepTableConnector
{
   private logger log = new logger (this, "de.elxala.db.dbMore", null);

   private Eva tableConnections = null;

   public List resultListSELECT = null;
   public List resultListFROM = null;
   public List resultListWHERE = null;

   // phantom
   public String resultStringGroupBy = "";
   public String resultStringHaving = "";

   private int linkTableIndx0 = 1;
   private int deepTableIndx0 = 1;

   private boolean optNoQuoteNames = true;
   private boolean optSimpleExprIfNoDeep = false;

   /*
      <dbmeta TableConnectionTable>

         connName, sourceTable, targetTable , sourceField, targetField

         pare    , persona   , persona     , pareid  , id
         mare    , persona   , persona     , mareid  , id
         lead    , projecto  , persona     , leaderid, id
         proy    , capitulos , projecto    , pid     , pid
         capitul , dedica    , capitulos   , pid     , pid
         capitul , dedica    , capitulos   , capi    , capi
   */
   public deepTableConnector (Eva connectionTable, boolean startInRow1, boolean noQuoteNames, boolean simpleExprIfNoDeep)
   {
      init (connectionTable, startInRow1, noQuoteNames, simpleExprIfNoDeep);
   }

   public deepTableConnector (Eva connectionTable)
   {
      init (connectionTable, false, false, true);
   }

   public void init (Eva connectionTable, boolean startInRow1, boolean noQuoteNames, boolean simpleExprIfNoDeep)
   {
      linkTableIndx0 = startInRow1 ? 1: 0;
      tableConnections = connectionTable;
      optNoQuoteNames = noQuoteNames;
      optSimpleExprIfNoDeep = simpleExprIfNoDeep;

      init ();
   }

   public void init ()
   {
      resultListSELECT = new Vector ();
      resultListFROM = new Vector ();
      resultListWHERE = new Vector ();
   }

   private int findConnection (String table, String connection)
   {
      for (int ii = linkTableIndx0; ii < tableConnections.rows (); ii ++)
      {
         if (table.equalsIgnoreCase (tableConnections.getValue(ii, deepSqlUtil.CONN_INDX_SOURCETABLE)) &&
             connection.equalsIgnoreCase (tableConnections.getValue(ii, deepSqlUtil.CONN_INDX_NAME)))
             return ii;
      }
      //debug error connection not found
      log.err ("Connection " + connection + " from table " + table + " not found in connection array of " +  (tableConnections.rows ()-linkTableIndx0) + " elements!");

      return -1;
   }

   /**
      Resolve the connections of the given deep column list 'evaColumns'
      where the first row contain information titles (which are really not evaluated
      in this method) and all deep columns start with the base table.

      Example

         <myEva4Connections>
            deepColumn               , alias

            tabNota noteId           ,
            tabNota date             , NotaDate
            tabNota perso name       , NameOfThePersonWhoWroteTheNote
   */
   public boolean resolveConnections (Eva evaColumns)
   {
      return resolveConnections (evaColumns, true, null, null);
   }

   /**
      Resolve the connections of the given deep column list 'evaColumns', where
      the first row contain information titles if hasTitlesInRow0 is true.
   */
   public boolean resolveConnections (Eva evaColumns, boolean hasTitlesInRow0)
   {
      return resolveConnections (evaColumns, hasTitlesInRow0, null, null);
   }


   // if alias start with "mainTable_..." then remove "mainTable_"
   //
   private String shortAlias(String mainTable, String alias)
   {
      return (alias.startsWith (mainTable + "_")) ? alias.substring (mainTable.length()+1): alias;
   }

   public static String decoColumName (String columnName)
   {
      if (columnName.length () > 0 && columnName.charAt(0) == '\"')
         return columnName;

      // deco needed to accept column names like "count", "where", "my col name", "#count"
      //  in general SQL key words (count, where, as, index etc), names with spaces or special characters
      //  this makes the query a little pedantic but I see no other solution
      //
      return "\"" + columnName + "\"";
   }


   public String quoteColumName (String columnName)
   {
      if (optNoQuoteNames && columnName.indexOf (' ') == -1)
         return columnName;

      return decoColumName (columnName);
   }

   // build the simplest SELECT expression if
   //    - the option is activated
   //    - there are no deep columns
   //    - tableBase is provided
   //
   private boolean simplestExpressionGenerated (Eva evaColumns, boolean hasTitlesInRow0, String tableBase, String shortAliasForBase)
   {
      int indx0 = hasTitlesInRow0 ? 1: 0;

      if (false == optSimpleExprIfNoDeep || evaColumns.rows () == 0)
         return false;

      // if tableBase is not given it is expected to be first entry in all deep columns
      //   e.g.  "tabSales client name"
      //
      int explicitTableBaseSize = 0;
      if (tableBase == null || tableBase.length () == 0)
      {
         int indx = evaColumns.getValue (indx0, 0).indexOf (' ');
         if (indx != -1)
            tableBase = evaColumns.getValue (indx0, 0).substring (0, indx);
         else return false;
         explicitTableBaseSize = tableBase.length ();
      }

      // check all columns to see if there is at least one deep, in that case return with false
      for (int cc = indx0; cc < evaColumns.rows (); cc ++)
      {
         String deepColumn  = evaColumns.getValue (cc, 0); // e.g. "persona pare name"

         if (explicitTableBaseSize > 0)
         {
            //   e.g.  "tabSales client name"
            if (false == deepColumn.startsWith (tableBase + " ") ||
               deepColumn.indexOf (' ', explicitTableBaseSize + 1) != -1)
               return false;
         }
         else if (deepColumn.indexOf (' ') != -1)
            return false;
      }

      // all columns are simple (no deep, no need of connections)
      // simplest SQL expression can be built
      //
      String aliasStr = "";

      if (shortAliasForBase != null && shortAliasForBase.length () > 0)
           resultListFROM.add (tableBase + " AS " + shortAliasForBase);
      else resultListFROM.add (tableBase);

      for (int cc = indx0; cc < evaColumns.rows (); cc ++)
      {
         String simpleColumn = evaColumns.getValue (cc, 0); // e.g. "name"
         String columnAlias  = evaColumns.getValue (cc, 1); // e.g. "la meva mare"

         if (explicitTableBaseSize > 0)
         {
            //   e.g.  "tabSales client name"
            simpleColumn = simpleColumn.substring (explicitTableBaseSize + 1);
         }

         String colName = quoteColumName (simpleColumn) + (columnAlias.length () > 0 ? " AS " + quoteColumName (columnAlias): "");
         if (!resultListSELECT.contains (colName))
         {
            log.dbg (4, "resolveConnections", "add to list SELECT  [" + colName + "]");
            resultListSELECT.add (colName);
         }
      }
      return true;
   }

   /*
      Solve a list of columns (deep columns) using for that the given connection
      array in the constructor. The result of the method is reflected in the
      public variables:

         List resultListSELECT
         List resultListFROM
         List resultListWHERE

      with which it is possible to build a SQL (e.g. method printSQL ())

      Input parameters:

      @param Eva evaColumns

      This is basically a list of "deep columns", the structure of a deep column is

         baseTable connection1 [ connection2 ... ] columnName

      this is given in a single string and the elements are separated by one space.
      In order the resultant SQL to make sense all the columns has to have the same
      baseTable at its first element. Optionally we can give a baseTable different
      from null or empty string (second parameter), in this case the structure expected
      for the deep columns is

         connection1 [ connection2 ... ] columnName


      Example of evaColumns (not giving a baseTable in the second parameter)

      <evaColumns>
         name

         dedica pid
         dedica capi
         dedica date
         dedica time
         dedica notes
         dedica capitul desc
         dedica capitul effort
         dedica capitul proy nombre
         dedica capitul proy leaderid
         dedica capitul proy lead nombre
         dedica capitul proy lead pare nombre
         dedica capitul proy lead mare nombre
         dedica capitul proy lead pare pare nombre
   */
   public boolean resolveConnections (Eva evaColumns, boolean hasTitlesInRow0, String tableBase, String shortAliasForBase)
   {
      init ();

      if (simplestExpressionGenerated (evaColumns, hasTitlesInRow0, tableBase, shortAliasForBase))
         return true ;

      // the algorithm implemented here is explained graphically in
      // https://www.codeproject.com/Articles/75601/The-Deep-Table
      // in section "Algorithm for Deep Select"
      //
      deepTableIndx0 = hasTitlesInRow0 ? 1: 0;
      boolean bOK = true;
      boolean bUsingFirstAlias = (shortAliasForBase != null && shortAliasForBase.length () > 0);

      // if tableBase is given then the deep columns start with a connection or a final column
      // if not then they start with the base table (all of them with the same!)
      String addBase = (tableBase == null || tableBase.length()==0) ? "": tableBase + " ";

      // for all fields
      for (int cc = deepTableIndx0; cc < evaColumns.rows (); cc ++)
      {
         String deepColumn  = evaColumns.getValue (cc, 0); // e.g. "persona pare name"
         String columnAlias = evaColumns.getValue (cc, 1); // e.g. "elmeupare"

         String [] currConnArray = (new String(addBase + deepColumn)).split (" "); // e.g. "persona pare name"
         int shiftConn = 0;
         while ((1+shiftConn) < currConnArray.length && currConnArray[1+shiftConn].length() == 0)
            shiftConn ++; // skip empty connections

         if (log.isDebugging (4))
            log.dbg (4, "resolveConnections", "new deep column [" + addBase + deepColumn + "] alias [" + columnAlias + "]");

         if (currConnArray.length-shiftConn == 0) continue;

         // get absolute first table (independently from 'tablaBase')
         String firstTable = currConnArray [0];
         String currResidu = bUsingFirstAlias ? shortAliasForBase: firstTable;
         if (resultListFROM.size () == 0)
         {
            // first table for "FROM"
            //
            String str = bUsingFirstAlias ? (firstTable + " AS " + shortAliasForBase): firstTable;
            resultListFROM.add (str);
            log.dbg (4, "resolveConnections", "add to list FROM [" + str + "]");
         }

         //System.out.println ("FIRST TABLA [" + firstTa + "] currResidu [" + currResidu + "] resultListFROM size " + resultListFROM.size () + " resultListWHERE size " + resultListWHERE.size ());

         // loop for solving all connections in this field
         //
         while (currConnArray.length - shiftConn > 2)
         {
            String previaTable = currResidu;
            currResidu = ((currResidu.length () > 0) ? currResidu + "_": "") + currConnArray[1 + shiftConn];

            if (log.isDebugging (4))
               log.dbg (4, "resolveConnections", "algo step. previaTable [" + previaTable + "] currResidu [" + currResidu + "]");

            int indx = findConnection (currConnArray[0], currConnArray[1 + shiftConn]);
            if (indx >= 0)
            {
               // found the connection at indx
               // table, linkName, targetTable , srcField, targetField

               String targetTable = tableConnections.getValue (indx, deepSqlUtil.CONN_INDX_TARGETTABLE); // targetTable
               log.dbg (4, "resolveConnections", "connection found, targetTable [" + targetTable + "]");

               String elemeFrom = targetTable + " AS " + quoteColumName (shortAlias (firstTable, currResidu));
               if (!resultListFROM.contains (elemeFrom))
               {
                  //connection still not there, build condition
                  // Loop for MULTIPLE CONDITION (a.id == b.id AND a.name == a.name)
                  String condition = "";
                  do
                  {
                     if (condition.length () > 0) condition = condition + " AND ";
                     condition += shortAlias(firstTable, previaTable) + "." + quoteColumName (tableConnections.getValue (indx, deepSqlUtil.CONN_INDX_SOURCEKEY)) + // srcField
                                 " == " +
                                 shortAlias(firstTable, currResidu) + "." + quoteColumName (tableConnections.getValue (indx, deepSqlUtil.CONN_INDX_TARGETKEY)); // targetField
                     indx ++;
                  } while (tableConnections.rows () > indx &&
                           tableConnections.getValue (indx, deepSqlUtil.CONN_INDX_SOURCETABLE).equals (tableConnections.getValue (indx-1, deepSqlUtil.CONN_INDX_SOURCETABLE)) &&
                           tableConnections.getValue (indx, deepSqlUtil.CONN_INDX_NAME).equals (tableConnections.getValue (indx-1, deepSqlUtil.CONN_INDX_NAME))
                           );

                  // add table to FROM and link condition to WHERE
                  resultListFROM.add (elemeFrom);
                  resultListWHERE.add (condition);
                  log.dbg (4, "resolveConnections", "add to list FROM  [" + elemeFrom + "]");
                  log.dbg (4, "resolveConnections", "add to list WHERE [" + condition + "]");
               }

               // shift connection
               //    "ta1 con1 con2 field" ==> "taXY con2 field"
               //
               // instead of copying arrays whe just increment shiftConn
               //
               //                   shiftConn = 1 -----v
               //
               //    "ta1 con1 con2 field" ==> "taXY ---- con2 field"
               //
               shiftConn ++;
               while (currConnArray.length-shiftConn > 0 && currConnArray[1 + shiftConn].length() == 0) shiftConn ++; // skip empty connections
               currConnArray[0] = targetTable;
            }
            else
            {
               //error already logged in ::findConnection
               //log.err ("Connection on " + currResidu + " not found!");
               bOK = false;
               break;
            }
         }

         //System.out.println (" resultListFROM size " + resultListFROM.size () + " resultListWHERE size " + resultListWHERE.size ());

         if (currConnArray.length - shiftConn != 2)
         {
            log.err ("at index " + cc + " currConnArray.size () is " + (currConnArray.length - shiftConn) + " but it should be 2!" + (currConnArray.length == 0 ? "": "[" + currConnArray[0] + "]"));
            bOK = false;
            continue;
         }

         // last element is the column name
         // e.g. "tab1_con1_con2.field AS tab1_con1_con2_field"

         String tab = (currResidu != null && currResidu.length () > 0) ? currResidu: currConnArray[0];
         String colName = currConnArray[1 + shiftConn]; // e.g. "nombre"
         String nameQuali = tab + "." + quoteColumName (colName);         // e.g. persona_pare."nombre"

         //System.out.println ("GARGOS [" + nameQuali + "]");

         // control alias
         //
         if (columnAlias.length () == 0)
            columnAlias = tab + "_" + colName;

         // form final column name and register it
         //
         colName = shortAlias (firstTable, nameQuali) + " AS " + quoteColumName (shortAlias (firstTable, columnAlias));
         if (!resultListSELECT.contains (colName))
         {
            log.dbg (4, "resolveConnections", "add to list SELECT  [" + colName + "]");
            resultListSELECT.add (colName);
         }
      }

      return bOK;
   }

   public void printSQL()
   {
      System.out.println ("SELECT");
      for (int ii = 0; ii < resultListSELECT.size (); ii ++)
         System.out.println ("   " + (ii==0 ? "":", ") + (String) resultListSELECT.get(ii));

      System.out.println ("FROM");
      for (int ii = 0; ii < resultListFROM.size (); ii ++)
         System.out.println ("   " + (ii==0 ? "":", ") + (String) resultListFROM.get(ii));

      if (resultListWHERE.size () > 0)
      {
         System.out.println ("WHERE");
         for (int ii = 0; ii < resultListWHERE.size (); ii ++)
            System.out.println ("   " + (ii==0 ? "":" AND ") + (String) resultListWHERE.get(ii));
      }
   }


   public static void main (String [] aa)
   {
      Eva connTable = new Eva ("connTable");

      connTable.addLine (new EvaLine ("linkName, table     , srcField, targetTable ,  targetField"));
      connTable.addLine (new EvaLine ("pare    , persona   , pareid  , persona     ,  id"));
      connTable.addLine (new EvaLine ("mare    , persona   , mareid  , persona     ,  id"));
      connTable.addLine (new EvaLine ("lead    , projecto  , leaderid, persona     ,  id"));
      connTable.addLine (new EvaLine ("proy    , capitulos , pid     , projecto    ,  pid"));
      connTable.addLine (new EvaLine ("capitul , dedica    , pid     , capitulos   ,  pid"));
      connTable.addLine (new EvaLine ("capitul , dedica    , capi    , capitulos   ,  capi"));

      Eva filedTable = new Eva ("filedTable");

      filedTable.addLine (new EvaLine ("columnExpr  , aliasName"));

      filedTable.addLine (new EvaLine ("dedica pid, PIDO"));
      filedTable.addLine (new EvaLine ("dedica date, DOTA"));
      filedTable.addLine (new EvaLine ("dedica time, TIMO"));
      filedTable.addLine (new EvaLine ("dedica notes"));
      filedTable.addLine (new EvaLine ("dedica capitul desc"));

      filedTable.addLine (new EvaLine ("dedica capitul effort, AUF"));
      filedTable.addLine (new EvaLine ("dedica capitul proy nombre"));
      filedTable.addLine (new EvaLine ("dedica capitul proy leaderid, LONGO"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead nombre, SUMARIO"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead pare nombre, AGUELETENS"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead mare nombre"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead pare pare nombre"));


      deepTableConnector o = new deepTableConnector (connTable, true, true, true);
      o.resolveConnections (filedTable);
      o.printSQL ();

      o.resolveConnections (filedTable, true, null, "ded");
      o.printSQL ();
   }
}
/*

03.12.2009 00:38

   Sí que funciona!

----- Conexiones

      <connTable>
         table     , linkName, targetTable , srcField, targetField
         persona   , pare    , persona     , pareid  , id
         persona   , mare    , persona     , mareid  , id
         projecto  , lead    , persona     , leaderid, id
         capitulos , proy    , projecto    , pid     , pid
         dedica    , capitul , capitulos   , pid     , pid
         dedica    , capitul , capitulos   , capi    , capi


----- Pre-Query

      dedica pid
      dedica capi
      dedica date
      dedica time
      dedica notes
      dedica capitul desc
      dedica capitul effort
      dedica capitul proy nombre
      dedica capitul proy leaderid
      dedica capitul proy lead nombre
      dedica capitul proy lead pare nombre
      dedica capitul proy lead mare nombre
      dedica capitul proy lead pare pare nombre

----- Query resultado

SELECT
   dedica.pid AS dedica_pid
   , dedica.capi AS dedica_capi
   , dedica.date AS dedica_date
   , dedica.time AS dedica_time
   , dedica.notes AS dedica_notes
   , dedica_capitul.desc AS dedica_capitul_desc
   , persona.id AS persona_id
   , persona.pareid AS persona_pareid
   , persona.mareid AS persona_mareid
   , persona_pare.nombre AS persona_pare_nombre
   , persona_mare.nombre AS persona_mare_nombre
   , dedica_capitul.effort AS dedica_capitul_effort
   , dedica_capitul_proy.nombre AS dedica_capitul_proy_nombre
   , dedica_capitul_proy.leaderid AS dedica_capitul_proy_leaderid
   , dedica_capitul_proy_lead.nombre AS dedica_capitul_proy_lead_nombre
   , dedica_capitul_proy_lead_pare.nombre AS dedica_capitul_proy_lead_pare_nombre
   , dedica_capitul_proy_lead_mare.nombre AS dedica_capitul_proy_lead_mare_nombre
   , dedica_capitul_proy_lead_pare_pare.nombre AS dedica_capitul_proy_lead_pare_pare_nombre
FROM
   dedica
   , capitulos AS dedica_capitul
   , persona
   , persona AS persona_pare
   , persona AS persona_mare
   , projecto AS dedica_capitul_proy
   , persona AS dedica_capitul_proy_lead
   , persona AS dedica_capitul_proy_lead_pare
   , persona AS dedica_capitul_proy_lead_mare
   , persona AS dedica_capitul_proy_lead_pare_pare
WHERE
   dedica.pid == dedica_capitul.pid AND dedica.capi == dedica_capitul.capi
    AND persona.pareid == persona_pare.id
    AND persona.mareid == persona_mare.id
    AND dedica_capitul.pid == dedica_capitul_proy.pid
    AND dedica_capitul_proy.leaderid == dedica_capitul_proy_lead.id
    AND dedica_capitul_proy_lead.pareid == dedica_capitul_proy_lead_pare.id
    AND dedica_capitul_proy_lead.mareid == dedica_capitul_proy_lead_mare.id
    AND dedica_capitul_proy_lead_pare.pareid == dedica_capitul_proy_lead_pare_pare.id
done!



29.11.2009 01:30

   ALGORITMO DE CONEXION DE TABLAS

   pasar de

      tabla conn1 conn2 conn3 campo
      ....

  a

      SELECT
         tabla_conn1_conn2_conn3.campo AS tabla_conn1_conn2_conn3_campo,
         ...

      FROM
         tabla,
         seconda AS tabla_conn1_conn2_conn3

      WHERE
         tabla.xepsilon = tabla_conn1.jaja,
         tabla_conn1_conn2_conn3.ersilon = tabla_conn1_conn2.rrara


  necesita la tabla de conexiones pero no el schema pues construye simplemente el SQL como
  string, si como SQL funciona o no ya es otra cosa!

      class campito

         String[] ristra;
         String   residuo;

         while (ristra.length > 2)
         {
            int eralon = ristra.length

            residuo += "_" + ristra[0] + "_" + ristra[1];

            ristra = deduceSubtabla (ristra)
            if (eralon == ristra.length) break;

            // para el FROM y EL WHERE
            declaraSubTabla (ristra[0], aliazas, connectionCond)
         }

         anyadeCampito (ristra[0] + "." + ristra[1] + " AS " + residuo);

*/