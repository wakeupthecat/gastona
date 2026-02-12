/*
library de.elxala
Copyright (C) 2005-2022 Alejandro Xalabarder Aulet

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
import javaj.widgets.basics.widgetLogger;

/*
   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#


   <docType>    listix_primitives
   <name>       sqlTableDelta
   <groupInfo>  sql
   <javaClass>  de.elxala.db.dbMore.sqlTableDeltaUtil
   <importance> 2
   <desc>       //Generate a SQL select for a table and their prev and diff values

   <help>
      //   This primitive generates a SQL select from a table that provide, except for the first record,
      //   the columns specified plus either their previous values (_prev) or delta values (_delta).
      //
      //   There are two possible sqlTableDelta listix primitives
      //
      //   1- Select the column and their "_prev" values
      //
      //       @<:sqlTablePrev tablename col1 ... colN>
      //
      //       that, using the option -indent, generates the SQL select
      //
      //            SELECT
      //               a.col1 AS col1_prev,
      //               b.col1 AS col1,
      //               ...
      //               a.colN AS colN_prev,
      //               b.colN AS colN,
      //            FROM tablename AS a, tablename AS b
      //            WHERE a.rowid+1 = b.rowid
      //
      //   2- Select the delta or diff columns
      //
      //       @<:sqlTableDelta|sqlTableDiff tablename col1 ... colN>
      //
      //       that, using the option -indent, generates the SQL select
      //
      //            SELECT
      //               col1 - col1_prev AS col1_delta,
      //               ...
      //               colN - colN_prev AS colN_delta
      //            FROM (@<:sqlTablePrev tablename col1 ... colN>)
      //
      //   --- Example Delta
      //
      //   If we have the table "tab" which content is
      //
      //               t, dist
      //              10,  400
      //              12,  450
      //              20,  552
      //
      //    executing the sql "@<:sqlTablePrev tab t dist>" will produce
      //
      //               t,  t_prev, dist, dist_prev
      //              12,  10    , 450 , 400
      //              20,  12    , 552 , 450
      //
      //    and "SELECT *, dist_diff/t_diff AS speed FROM (@<:sqlTableDelta t1 t dist>)"
      //
      //          t_diff, dist_diff, speed
      //               2,     50   , 25
      //               8,    102   , 12
      //
      //   --- Requisite for the generated SQL to work as expected
      //
      //   Note that the first parameter in both primitives is a table name, it will not work with a view
      //   unless it would contain a column called "rowid" that increases in one on each record.
      //   A solution that will work for any view is to create a table from it and then use that table instead.
      //
      //       CREATE TABLE tmp3diff AS SELECT ...my select or view ;
      //
      //       @<:sqlTableDelta tmp3diff col1 col2>
      //
      //   In case of tables note that also records where their previous rowid has been deleted
      //   will not appear in the results as well.
      //

   <examples>
      gastSample
      table delta values
      SQL trip delta sample

   <table delta values>
      //#javaj#
      //
      //   <frames> oExample
      //
      //#data#
      //
      //    <triplog>
      //       t, dist
      //      10,  400
      //      12,  450
      //      20,  552
      //
      //#listix#
      //
      //   <main0>
      //      DB,, CREATE TABLE, triplog
      //      LSX, showSQL, Source table, //SELECT * FROM triplog
      //      LSX, showSQL, Prev SQL,     //@<:sqlTablePrev-indent triplog t dist>
      //      LSX, showSQL, Delta SQL,    //@<:sqlTableDelta-indent triplog t dist>
      //      LSX, showSQL, Speed Calculation, //SELECT *, dist_delta/t_delta AS speed FROM (@<:sqlTableDelta-indent triplog t dist>)
      //
      //   <showSQL>
      //      //
      //      //
      //      //=================== @<p1>
      //      //
      //      //------- SQL:
      //      //@<p2>
      //      //
      //      //------- Resulting records:
      //      //
      //      LOOP, SQL,, @<p2>
      //          , HEAD, LOOP, COLUMNS,
      //          , HEAD,     , LINK, ", "
      //          , HEAD,     ,, @<columnName>
      //          , HEAD, ""
      //          , HEAD, ""
      //          ,     , LOOP, COLUMNS,
      //          ,     ,     , LINK, ", "
      //          ,     ,     ,, @<columnValue>

   <SQL trip delta sample>
      //#gastona#
      //
      //   <fusion> META-GASTONA/utilApp/std/printLoop.gasti
      //
      //#javaj#
      //
      //  <frames> oSortida, Trip report sample
      //
      //#data#
      //
      //   <triplog>
      //      location    , distKm, timeHour
      //      Stuttgart   ,      0,  0.00
      //      Mulhouse    ,    253,  2.45
      //      Lyon        ,    634,  5.70
      //      Valence     ,    736,  6.65
      //      Montpellier ,    939,  9.03
      //      Girona      ,   1186, 11.30
      //      Barcelona   ,   1289, 12.70
      //
      //#listix#
      //
      //   <main0>
      //      DB,, CREATE TABLE, triplog
      //      // ---- Trip log (source data)
      //      //
      //      LSX, printLoopSQL_quickFormat, //SELECT * FROM triplog
      //      //
      //      // ---- Trip delta SQL
      //      //
      //      //@<SQL_DELTA>
      //      //
      //      // ---- Trip report
      //      //
      //      LSX, printLoopSQL_quickFormat, @<SQL_TRIP_REPORT>
      //
      //   <SQL_DELTA>
      //      //@<:sqlTableDelta-indent triplog location distKm timeHour>
      //
      //   <SQL_TRIP_REPORT>
      //      //SELECT *,
      //      //    ROUND (distKm_delta / timeHour_delta, 1) AS agvSpeed_Kmh
      //      // FROM (@<SQL_DELTA>)
      //

#**FIN_EVA#

*/
public class sqlTableDeltaUtil
{
   public static void main (String [] aa)
   {
      System.out.println ("Examples of resulting SQL filter strings for ");
      System.out.println ("the given asiste values of the fields 'id', 'name' and 'size'");
      System.out.println ("");

      if (aa.length >= 3)
      {
         procesaAndShow (aa);
      }
      else
      {
         procesaAndShow (new String [] { "prev", "tabula", "tstamp", "date", "etc" });
         procesaAndShow (new String [] { "delta", "tabula", "tstamp" });
      }
   }

   private static void procesaAndShow (String [] datos)
   {
      String [] arr2 = new String[datos.length-1];

      for (int ii = 0; ii < datos.length; ii ++)
      {
         System.out.print ("\"" + datos[ii] + "\"\t");
         if (ii > 0)
            arr2[ii] = datos[ii-1];
      }

      if (datos[0].equalsIgnoreCase ("prev"))
      {
         System.out.println (getTablePrevSQL (arr2));
      }
      if (datos[0].equalsIgnoreCase ("delta"))
      {
         System.out.println (getTableDeltaSQL (arr2));
      }
      else
         System.out.println ("invalid first parameter '" + datos[0] + "', it has to be prev or delta");
   }


   public static String getTableDeltaSQL (String [] tableAndColumns)
   {
      return getTableDeltaSQL (tableAndColumns, true);
   }

   public static String getTableDeltaSQL (String [] tableAndColumns, boolean spaceIndent)
   {
      return getTableDeltaSQL (tableAndColumns, spaceIndent, false);
   }

   public static String getTableDeltaSQL (String [] tableAndColumns, boolean spaceIndent, boolean only_deltas)
   {
      if (tableAndColumns.length < 2)
      {
         widgetLogger.log().err ("sqlTableDeltaUtil::getTableDeltaSQL", "given " + tableAndColumns.length + " parameters but at least 2 were expected (tablename and a column name)");
         return ""; // produces no result!
      }

      String RET = spaceIndent ? "\n": "";

      // @<:sqlTableDiff tabula tstamp>
      //
      //    SELECT
      //        tstamp - tstamp_prev AS tstamp_delta
      //    FROM (@<:sqlTabDeltaPrev tabula tstamp>)

      StringBuffer strb = new StringBuffer ("SELECT" +  (only_deltas ? "": " *,"));

      String tableName = tableAndColumns[0];

      for (int ii = 1; ii < tableAndColumns.length; ii ++)
      {
         strb.append (ii > 1 ? ",": "");
         strb.append (spaceIndent ? RET + "   ": " ");
         strb.append (tableAndColumns[ii] + " - " + tableAndColumns[ii] + "_prev AS " + tableAndColumns[ii] + "_delta");
      }
      strb.append (RET + " FROM (" + getTablePrevSQL (tableAndColumns, spaceIndent) + ")");
      return strb.toString ();
   }

   public static String getTablePrevSQL (String [] tableAndColumns)
   {
      return getTablePrevSQL (tableAndColumns, true);
   }

   public static String getTablePrevSQL (String [] tableAndColumns, boolean spaceIndent)
   {
      if (tableAndColumns.length < 2)
      {
         widgetLogger.log().err ("sqlTableDeltaUtil::getTablePrevSQL", "given " + tableAndColumns.length + " parameters but at least 2 were expected (tablename and a column name)");
         return ""; // produces no result!
      }

      String RET = spaceIndent ? "\n": "";

      // @<:sqlTabDeltaPrev tabula tstamp date etc>
      //
      //    SELECT
      //       a.tstamp AS tstamp_prev,
      //       b.tstamp AS tstamp,
      //       a.date AS date_prev,
      //       b.date AS date,
      //       a.etc AS etc_prev,
      //       b.etc AS etc
      //    FROM tabula AS a, tabula AS b
      //    WHERE a.rowid+1 = b.rowid

      StringBuffer strb = new StringBuffer ("SELECT");

      String tableName = tableAndColumns[0];

      for (int ii = 1; ii < tableAndColumns.length; ii ++)
      {
         strb.append (ii > 1 ? ",": "");
         strb.append (spaceIndent ? RET + "   ": " ");
         strb.append ("a." + tableAndColumns[ii] + " AS " + tableAndColumns[ii] + "_prev,");
         strb.append (spaceIndent ? RET + "   ": " ");
         strb.append ("b." + tableAndColumns[ii] + " AS " + tableAndColumns[ii]);
      }
      strb.append (RET);
      strb.append (" FROM " + tableName + " AS a, " + tableName + " AS b" + RET);
      strb.append (" WHERE a.rowid+1 = b.rowid");
      return strb.toString ();
   }
}
