/*
library de.elxala
Copyright (C) 2022 Alejandro Xalabarder Aulet

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
   <name>       sqlDynDimTable
   <groupInfo>  sql
   <javaClass>  de.elxala.db.dbMore.sqlDynDimTableUtil
   <importance> 2
   <desc>       //Generate a SQL select that computes an histogram of some column value of a table

   <help>
      // From the records of a source table obtain a different table where the new column names and values
      // are obtained from the data of the source table
      //
      // The syntax is (aliases : sqlDynDimTable, sqlInnerTable, sqlSubTable)
      //
      //       @<:sqlDynDimTable srcTable colRecKey colDimName colDimValue dimName1 dimName2 ...>
      //
      // where
      //        srcTable    table or view where the data comes from
      //        colRecKey   column that will be used as unique key for the new derived table records
      //        colDimName  column which values contain the new column name or dimension of the derived table
      //        colDimValue column which values contain the data of the dimension
      //        dimName1    dimension or new column name of the derived table to get
      //        ..
      //        dimNameN    dimension or new column name of the derived table to get
      //
      // All column names are expected to be a single not reserved word (not names like count, order, index, etc ...)
      // This can be always accomplished by creating a temporary view naming all columns as expected and use it as "srcTable" if necessary.
      //
      // the returned SQL will be
      //
      //      SELECT
      //         colRecKey, COUNT(*) AS cnt,
      //         MAX(CASE WHEN colDimName = 'dimName1' THEN colDimValue) AS dimName1,
      //         ...
      //         MAX(CASE WHEN colDimName = 'dimNameN' THEN colDimValue) AS dimNameN
      //       FROM srcTable
      //       GROUP BY colRecKey
      //
      // --- Uses
      //
      // There are several use cases for this "dynamic dimensions table" construction.
      // Two of them are shown in the examples: a NoSQL database and a log report reformatted report
      //


   <examples>
      gastSample
      NoSQL sample
      log reformatted report

   <NoSQL sample>
     //
     //   This example shows how to create a NoSQL database based on two single tables
     //   for all posible "dynamic" tables.
     //
     //#gastona#
     //
     //   <fusion>
     //      META-GASTONA/utilApp/std/printLoop.gasti
     //
     //#data#
     //
     //   <trecords>
     //     rec, recdate  , dynTable
     //     87, 2016-01-11, people
     //     88, 2016-01-11, people
     //     89, 2016-01-22, events
     //     90, 2016-01-22, people
     //     91, 2016-01-22, people
     //
     //   <tvalues>
     //      rec, dim, value
     //      87 , name, Victor Jara
     //      87 , year, 1973
     //      88 , name, Patrice Lumumba
     //      88 , year, 1961
     //      88 , location, Katanga
     //      89 , name, DR Congo independence from Belgium
     //      89 , year, 1960
     //      90 , name, Dag Hammarskjoeld
     //      90 , year, 1961
     //      91 , name, J.F. Kennedy
     //      91 , year, 1963
     //      91 , location, Dallas
     //
     //#javaj#
     //
     //  <frames> oOutput, LogReformatted
     //
     //#listix#
     //
     //   <main0>
     //      DB,, CREATE TABLE, trecords
     //      DB,, CREATE TABLE, tvalues
     //      DB,, EXECUTE, //CREATE VIEW dataPeople AS SELECT * FROM tvalues WHERE rec IN (SELECT rec FROM trecords WHERE dynTable = 'people');
     //      DB,, EXECUTE, //CREATE VIEW dataEvents AS SELECT * FROM tvalues WHERE rec IN (SELECT rec FROM trecords WHERE dynTable = 'events');
     //      // ---- NoSQL table records
     //      //
     //      LSX, printLoopSQL_quickFormat, //SELECT * FROM trecords
     //      //
     //      // ---- NoSQL table values
     //      //
     //      LSX, printLoopSQL_quickFormat, //SELECT * FROM tvalues
     //      //
     //      // ---- Dynamic Dimension table "people" generated SQL
     //      //
     //      @<SQL_PEOPLE>
     //      //
     //      // ---- Dynamic Dimension table "people"
     //      //
     //      LSX, printLoopSQL_quickFormat, @<SQL_PEOPLE>
     //      //
     //      // ---- Dynamic Dimension table "events"
     //      //
     //      LSX, printLoopSQL_quickFormat, @<:sqlDynDimTable dataEvents rec dim value name year>
     //
     //   <SQL_PEOPLE>
     //      @<:sqlDynDimTable-indent dataPeople rec dim value name year location>

   <log reformatted report>
     //
     //   This example shows how to represent a log report in a different way based on the data
     //
     //#gastona#
     //
     //   <fusion>
     //      META-GASTONA/utilApp/std/printLoop.gasti
     //
     //#data#
     //
     //   <runlog>
     //      time, agent    , message
     //      102 , gui      , //request position
     //      105 , mediator , //request engine status
     //      112 , engine   , //ok, alive
     //      118 , mediator , //request engine date time
     //      119 , engine   , //you tell me
     //      122 , gui      , //request position!!
     //      126 , mediator , //response "in the car"
     //      133 , gui      , //request route to B
     //      144 , mediator , //response "no way!"
     //
     //#javaj#
     //
     //  <frames> oOutput, LogReformatted
     //
     //#listix#
     //
     //   <main0>
     //      DB,, CREATE TABLE, runlog
     //      // ---- Runlog (source data)
     //      //
     //      LSX, printLoopSQL_quickFormat, //SELECT * FROM runlog
     //      //
     //      // ---- Dynamic Dimension SQL
     //      //
     //      //@<SQL_REPORT2>
     //      //
     //      // ---- runLog report2
     //      //
     //      LSX, printLoopSQL_quickFormat, @<SQL_REPORT2>
     //
     //   <SQL_REPORT2>
     //      @<:sqlDynDimTable-indent runlog time agent message gui mediator engine>
     //

#**FIN_EVA#

*/
public class sqlDynDimTableUtil
{
   public static void main (String [] aa)
   {
      System.out.println ("Examples of resulting SQL for Dynamic Dimension table");
      System.out.println ("");

      if (aa.length >= 1)
      {
         procesaAndShow (aa);
      }
      else
      {
         procesaAndShow (new String [] { "srcTable", "colRecKey", "colDimName", "colDimValue", "col1", "col2" });
      }
   }

   private static void procesaAndShow (String [] datos)
   {
      for (int ii = 0; ii < datos.length; ii ++)
      {
         System.out.print ("\"" + datos[ii] + "\"\t");
      }
      System.out.println (getDynDimTableSQL (datos));
   }


   public static String getDynDimTableSQL (String [] params)
   {
      return getDynDimTableSQL (params, true);
   }

   public static String getDynDimTableSQL (String [] params, boolean spaceIndent)
   {
      // parameters : srcTable colRecKey colDimName colDimValue dimName1 ...
      if (params.length < 5)
      {
         widgetLogger.log().err ("sqlDynDimTableUtil::getInnerTableSQL", "given " + params.length + " parameters but at least 5 were expected (srcTable colRecKey colDimName colDimValue dimName1)");
         return ""; // produces no result!
      }

      String RET = spaceIndent ? "\n": "";

      // @<:sqlInnerTable srcTable colRecKey colDimName colDimValue col1 col2 col3>
      //
      //   SELECT
      //          colRecKey,
      //          COUNT(*) AS cnt,
      //          MAX(CASE WHEN colDimName = 'col1' THEN colDimValue END) AS col1,
      //          MAX(CASE WHEN colDimName = 'col2' THEN colDimValue END) AS col2,
      //          MAX(CASE WHEN colDimName = 'col3' THEN colDimValue END) AS col3
      //   FROM srcTable
      //   GROUP BY colRecKey

      String table = params[0];
      String joiner = params[1];
      String colName = params[2];
      String colValue = params[3];

      StringBuffer strb = new StringBuffer ("SELECT" + (spaceIndent ? RET + "   ": " ") + joiner + ", COUNT(*) AS cnt");

      for (int ii = 4; ii < params.length; ii ++)
      {
         strb.append (",");
         strb.append (spaceIndent ? RET + "   ": " ");
         strb.append ("MAX(CASE WHEN " + colName + " = '" + params[ii] + "' THEN " + colValue + " END) AS " + params[ii]);
      }
      strb.append (RET + " FROM " + table);
      strb.append (RET + " GROUP BY " + joiner);
      return strb.toString ();
   }
}
