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

//import de.elxala.db.sqlite.*;

/**
   Utility to generate the info needed to build a SELECT sql
   based on a set of connections and optionally a set of formulas

   NOTE: The generated code does not have to be a valid SQL for a
         specific database, this class DOES NOT need to know even the
         database schema, it simple generates the lists
         listSELECT, listFROM, listWHERE from the given information

   Example:

      Having the tables

         tabPersona (id, name, pareid, mareid)
         tabNota    (noteId, persoId, date, text)

      we want to auto-connect the table 'tabPersona' using pareid and mareid
      and connect 'tabNota' with 'persona' using persoId

      <connections>
         linkName, sourceTable  , targetTable, sourceKey, targetKey

         pare    , tabPersona   , tabPersona , pareid   , id
         mare    , tabPersona   , tabPersona , mareid   , id
         perso   , tabNota      , tabPersona , persoId  , id


      now we want to generate a SQL to do following:
      get all notes, show the name of the person who write the note and the
      names of his father and mother as well


      <fields>
         column

         tabNota noteId
         tabNota date
         tabNota perso name
         tabNota perso pare name
         tabNota perso mare name
         tabNota text

   === Code using fieldConnector1

   fieldConnector1 boy = new fieldConnector1 (evaConnections);
   boy.resolveConnections (evaFields);
   System.out.printf (boy.getSQL());


   ========== NOTES

   08.12.2009 00:04

     Decided to have the following column order for better human-readability,
     but it has some "logical" drawbacks!

      <connections>
         linkName, sourceTable, sourceKey, targetTable, targetKey

         l1      , ta1        , ...
         l1      , ta1        , ...
         l2      , ta1        , ...
         l1      , ta2

     note that if extracted from a database the desired ORDER BY could be "sourceTable, linkName" !

        SELECT linkName, sourceTable, sourceKey, targetTable, targetKey
        FROM ...
        ORDER BY sourceTable, linkName;

     this does not fit with the RUN LOOP WHILE criterium!! for that better do

        SELECT sourceTable, linkName, ...
        FROM ...
        ORDER BY sourceTable, linkName;
*/
public class fieldConnector1
{
   private logger log = new logger (this, "de.elxala.db.dbMore", null);

   private Eva tableConnections = null;

   public List resultListSELECT = null;
   public List resultListFROM = null;
   public List resultListWHERE = null;

   public String resultStringGroupBy = null;
   public String resultStringHaving = null;

   private int linkTableIndx0 = 1;
   private int deepTableIndx0 = 1;

   /*
      <dbmeta TableConnectionTable>

         linkName, sourceTable, targetTable , sourceField, targetField

         pare    , persona   , persona     , pareid  , id
         mare    , persona   , persona     , mareid  , id
         lead    , projecto  , persona     , leaderid, id
         proy    , capitulos , projecto    , pid     , pid
         capitul , dedica    , capitulos   , pid     , pid
         capitul , dedica    , capitulos   , capi    , capi
   */
   public fieldConnector1 (Eva linkTable, boolean hasTitlesInRow0)
   {
      linkTableIndx0 = hasTitlesInRow0 ? 1: 0;
      tableConnections = linkTable;
      init ();
   }

   public void init ()
   {
      resultListSELECT = new Vector ();
      resultListFROM = new Vector ();
      resultListWHERE = new Vector ();
      resultStringGroupBy = "";
      resultStringHaving = "";
   }

   private int findConnection (List connArray)
   {
      if (connArray.size () < 2) return -1;
      String tab = (String) connArray.get(0);
      String con = (String) connArray.get(1);
      for (int ii = linkTableIndx0; ii < tableConnections.rows (); ii ++)
      {
         if (tab.equalsIgnoreCase (tableConnections.getValue(ii, dbMore.CONN_INDX_SOURCETABLE)) &&
             con.equalsIgnoreCase (tableConnections.getValue(ii, dbMore.CONN_INDX_NAME)))
             return ii;
      }
      //debug error connection not found
      log.err ("Connection " + con + " from table " + tab + " not found in connection array of " +  (tableConnections.rows ()-linkTableIndx0) + " elements!");

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

   /*
      Solve a list of columns (deep columns) using for that the given connection
      array in the constructor. The result of the method is reflected in the
      public variables:

         List resultListSELECT
         List resultListFROM
         List resultListWHERE
         String resultStringGroupBy
         String resultStringHaving

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
      deepTableIndx0 = hasTitlesInRow0 ? 1: 0;

      boolean bOK = true;
      boolean bGrouping = false;
      boolean bUsingFirstAlias = (shortAliasForBase != null && shortAliasForBase.length () > 0);

      // if tableBase is given then the deep columns start with a connection or a final column
      // if not then they start with the base table (all of them with the same!)
      String addBase = (tableBase == null || tableBase.length()==0) ? "": tableBase + " ";

      init ();
      // for all fields
      for (int cc = deepTableIndx0; cc < evaColumns.rows (); cc ++)
      {
         if (log.isDebugging (4))
            log.dbg (4, "resolveConnections", "new deep column [" + addBase + evaColumns.getValue (cc, 0) + "]");
         List currConnArray = Cadena.simpleToList(addBase + evaColumns.getValue (cc, 0), " "); // e.g. "persona pare name"
         String columnAlias = evaColumns.getValue (cc, 1); // e.g. "elmeupare"
         String groupFunc   = evaColumns.getValue (cc, 2); // e.g. "MAX"
         String groupHaving = evaColumns.getValue (cc, 3); // e.g. "> 10"

         if (groupFunc.length () > 0)
            bGrouping = true;

         if (currConnArray.size () == 0) continue;

         // get absolute first table (independently from 'tablaBase')
         String firstTable = (String) currConnArray.get(0);
         String currResiduo = bUsingFirstAlias ? shortAliasForBase: firstTable;
         //xxx String currResiduo = bUsingFirstAlias ? shortAliasForBase: "";
         if (resultListFROM.size () == 0)
         {
            // first table for "FROM"
            //
            String str = bUsingFirstAlias ? (firstTable + " AS " + shortAliasForBase): firstTable;
            resultListFROM.add (str);
            log.dbg (4, "resolveConnections", "add to list FROM [" + str + "]");
         }

         //System.out.println ("FIRST TABLA [" + firstTa + "] currResiduo [" + currResiduo + "] resultListFROM size " + resultListFROM.size () + " resultListWHERE size " + resultListWHERE.size ());

         // loop for solving all connections in this field
         //
         while (currConnArray.size () > 2)
         {
            String previaTable = currResiduo;

            if (currResiduo.length () > 0)
                 currResiduo +=  "_" + (String) currConnArray.get(1);
            else currResiduo = (String) currConnArray.get(1);

            if (log.isDebugging (4))
               log.dbg (4, "resolveConnections", "algo step. previaTable [" + previaTable + "] currResiduo [" + currResiduo + "]");

            int indx = findConnection (currConnArray);
            if (indx >= 0)
            {
               // found the connection at indx
               // table, linkName, targetTable , srcField, targetField

               String targetTable = tableConnections.getValue (indx, dbMore.CONN_INDX_TARGETTABLE); // targetTable
               log.dbg (4, "resolveConnections", "connection found, targetTable [" + targetTable + "]");

               String elemeFrom = targetTable + " AS " + shortAlias (firstTable, currResiduo);
               if (!resultListFROM.contains (elemeFrom))
               {
                  //connection still not there, build condition
                  // Loop for MULTIPLE CONDITION (a.id == b.id AND a.name == a.name)
                  String condition = "";
                  do
                  {
                     if (condition.length () > 0) condition = condition + " AND ";
                     condition += shortAlias(firstTable, previaTable) + "." + tableConnections.getValue (indx, dbMore.CONN_INDX_SOURCEKEY) + // srcField
                                 " == " +
                                 shortAlias(firstTable, currResiduo) + "." + tableConnections.getValue (indx, dbMore.CONN_INDX_TARGETKEY); // targetField
                     indx ++;
                  } while (tableConnections.rows () > indx &&
                           tableConnections.getValue (indx, dbMore.CONN_INDX_SOURCETABLE).equals (tableConnections.getValue (indx-1, dbMore.CONN_INDX_SOURCETABLE)) &&
                           tableConnections.getValue (indx, dbMore.CONN_INDX_NAME).equals (tableConnections.getValue (indx-1, dbMore.CONN_INDX_NAME))
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
               currConnArray.remove (0);
               currConnArray.set (0, targetTable);
            }
            else
            {
               //error already logged in ::findConnection
               //log.err ("Connection on " + currResiduo + " not found!");
               bOK = false;
               break;
            }
         }

         //System.out.println (" resultListFROM size " + resultListFROM.size () + " resultListWHERE size " + resultListWHERE.size ());

         if (currConnArray.size () != 2)
         {
            log.err ("at index " + cc + " currConnArray.size () is " + currConnArray.size () + " but it should be 2!" + (currConnArray.size () == 0 ? "": "[" + (String) currConnArray.get(0) + "]"));
            bOK = false;
            continue;
         }

         // last element is the column name
         // e.g. "tab1_con1_con2.field AS tab1_con1_con2_field"

         String tab = (currResiduo != null && currResiduo.length () > 0) ? currResiduo: (String) currConnArray.get(0);
         String colName = (String) currConnArray.get(1); // e.g. "nombre"
         String nameQuali = tab + "." + colName;         // e.g. "persona_pare.nombre"

         // control alias
         //
         if (columnAlias.length () == 0)
            columnAlias = tab + "_" + colName;

         // control grouping (NO MATTER IF bGrouping is false)
         //
         if (groupFunc.length () == 0)
         {
            // group candidate
            if (resultStringGroupBy.length () > 0) resultStringGroupBy += ", ";
            resultStringGroupBy += shortAlias (firstTable, columnAlias);
         }
         else
         {
            nameQuali = groupFunc + "(" + nameQuali + ")";
            if (groupHaving.length () > 0)
            {
               if (resultStringHaving.length () > 0) resultStringHaving += " AND ";
               resultStringHaving += columnAlias + " " + groupHaving;
            }
         }

         // form final column name and register it
         //
         colName = shortAlias (firstTable, nameQuali) + " AS " + shortAlias (firstTable, columnAlias);
         if (!resultListSELECT.contains (colName))
         {
            log.dbg (4, "resolveConnections", "add to list SELECT  [" + colName + "]");
            resultListSELECT.add (colName);
         }
      }

      if (! bGrouping)
      {
         resultStringGroupBy = "";
         resultStringHaving = "";
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

      if (resultStringGroupBy.length () > 0)
         System.out.println ("GROUP BY " + resultStringGroupBy);

      if (resultStringHaving.length () > 0)
         System.out.println ("HAVING " + resultStringHaving);
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

      filedTable.addLine (new EvaLine ("columnExpr  , aliasName, groupFunc, havingExp"));

      filedTable.addLine (new EvaLine ("dedica pid, PIDO"));
      filedTable.addLine (new EvaLine ("dedica date, DOTA"));
      filedTable.addLine (new EvaLine ("dedica time, TIMO"));
      filedTable.addLine (new EvaLine ("dedica notes"));
      filedTable.addLine (new EvaLine ("dedica capitul desc"));

      filedTable.addLine (new EvaLine ("dedica capitul effort, AUF, SUM"));
      filedTable.addLine (new EvaLine ("dedica capitul proy nombre"));
      filedTable.addLine (new EvaLine ("dedica capitul proy leaderid, LONGO, LENGTH"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead nombre, SUMARIO, COUNT"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead pare nombre, AGUELETENS, MAX, < 10"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead mare nombre"));
      filedTable.addLine (new EvaLine ("dedica capitul proy lead pare pare nombre"));


      fieldConnector1 o = new fieldConnector1 (connTable, true);
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