/*
library de.elxala
Copyright (C) 2022-2026 Alejandro Xalabarder Aulet

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

import de.elxala.langutil.*;
import java.util.*;
import javaj.widgets.basics.widgetLogger;

/*
   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#


   <docType>    listix_primitives
   <name>       sqlHistogram
   <groupInfo>  sql
   <javaClass>  de.elxala.db.dbMore.sqlHistogram
   <importance> 2
   <desc>       //Generate a SQL select that computes an histogram of some column value of a table

   <help>
      // This primitive generates a SQL select from a table that provides the histogram values
      // for a specified numeric column.
      //
      // The syntax is
      //
      //       @<:sqlHistogram srcTable colName interval>
      // or
      //       @<:sqlHistogram srcTable colName interval condition>
      //
      // where
      //        srcTable    table or view where the data comes from
      //        colName     column name of type numeric to be computed
      //        interval    real number defining the interval of each bar of the histogram
      //        condition   optional condition for the main select source table (e.g. to form "SELECT <colName> FROM <srcTable> WHERE <condition>")
      //
      // the returned SQL will be (replacing <> parameters)
      //
      //      SELECT
      //         barIndx,
      //         (barIndx - 0.5) * (<interval>) AS fromValue,
      //         (barIndx + 0.5) * (<interval>) AS toValue,
      //         barCount
      //      FROM (
      //         SELECT barIndx, count (*) AS barCount
      //         FROM (SELECT ROUND ((<colName>) / (<interval>), 0) AS barIndx FROM <srcTable> [WHERE <condition>])
      //         GROUP BY barIndx
      //         ORDER BY barIndx ASC
      //      )

   <examples>
      gastSample
      gastona samples histogram

   <gastona samples histogram>
     //
     //   This example prints out the histogram of all samples length from the column "sampleBody" in the table "tGastExamples"
     //   from the internal database of the WelcomeGastona script.
     //
     //   for that
     //         1) it creates welcomeGastona database into a temporary database (unnamed)
     //         2) it creates a convenient view to compute each sample length
     //         3) executes and print out the result of the generated sql using @<:sqlHistogram...>
     //
     //#gastona#
     //
     //   <fusion>
     //      META-GASTONA/utilApp/std/printLoop.gasti
     //
     //#javaj#
     //
     //  <frames> oOutput, SQL Histogram primitive sample
     //
     //#listix#
     //
     //   <main0>
     //      DB,, EXECUTE, @<:infile META-GASTONA/WelcomeGastona/gastonaDocScript.sql>
     //      DB,, EXECUTE, //CREATE VIEW sampleLengths AS SELECT LENGTH(sampleBody) AS sampleLen FROM tGastExamples ;
     //
     //      //SQL for the histogram:
     //      //
     //      //@<SQL_HISTO>
     //      //
     //      //Result:
     //      //
     //      LSX, printLoopSQL_quickFormat, @<SQL_HISTO>
     //
     //   <SQL_HISTO>
     //      //@<:sqlHistogram-indent sampleLengths sampleLen 200>

#**FIN_EVA#

*/
public class sqlHistogramUtil
{
   public static void main (String [] aa)
   {
      System.out.println ("Examples of resulting SQL for histogram");
      System.out.println ("");

      if (aa.length >= 1)
      {
         procesaAndShow (aa);
      }
      else
      {
         procesaAndShow (new String [] { "srcTable", "personas", "colX", "colY" });
      }
   }

   private static void procesaAndShow (String [] datos)
   {
      for (int ii = 0; ii < datos.length; ii ++)
      {
         System.out.print ("\"" + datos[ii] + "\"\t");
      }
      System.out.println (getHistogramSQL (datos));
   }


   public static String getHistogramSQL (String [] params)
   {
      return getHistogramSQL (params, true);
   }

   public static String getHistogramSQL (String [] params, boolean spaceIndent)
   {
      if (params.length < 2)
      {
         widgetLogger.log().err ("sqlHistogramUtil::getHistogramSQL", "given " + params.length + " parameters but at least 2 are expected (srcTable coly interval)");
         return ""; // produces no result!
      }

      String RET = spaceIndent ? "\n": "";
      String SPC = spaceIndent ? "   ": " ";

      //  @<:sqlHistogram srcTable colName interval [condi]>
      //
      //      SELECT
      //         barIndx,
      //         (barIndx - 0.5) * (interval) AS fromValue,
      //         (barIndx + 0.5) * (interval) AS toValue,
      //         barCount
      //      FROM (
      //         SELECT barIndx, count (*) AS barCount
      //         FROM (SELECT CAST (ROUND ((colName) / (interval), 0) AS integer) AS barIndx FROM srcTable WHERE condi)
      //         GROUP BY barIndx
      //         ORDER BY barIndx ASC
      //      )
      //
      // Note: enclosing colName and interval in parenthesis allow writting expressions like
      //       @<:sqlHistogram srcTable col1/col2 100-8>
      //

      String table = params[0];
      String colY  = params[1];
      float interval = params.length > 2 ? (float) stdlib.atof (params[2]): 1.f;

      // treat the rest as condition for the main select (optional)
      //
      String sqlCondi = "";
      for (int ii = 3; ii < params.length; ii ++)
         sqlCondi += " " + params[ii];

      return "SELECT" + RET +
             SPC + "barIndx," + RET +
             SPC + "(barIndx - 0.5) * (" + interval + ") AS fromValue," + RET +
             SPC + "(barIndx + 0.5) * (" + interval + ") AS toValue," + RET +
             SPC + "barCount" + RET +
             SPC + "FROM (" + RET +
             SPC + "SELECT barIndx, COUNT (*) AS barCount " + RET +
             SPC + "FROM (" + RET +
             SPC + SPC + "SELECT CAST (ROUND ((" + colY + ") / (" + interval + ") , 0) AS integer) AS barIndx" + RET +
             SPC + SPC + "   FROM " + table + RET +
             SPC + SPC + "   WHERE " + (sqlCondi.length () == 0 ? "(1)": sqlCondi) + ")" + RET +
             SPC + "GROUP BY barIndx" + RET +
             SPC + "ORDER BY barIndx ASC" + RET +
             SPC + ")";
   }
}
