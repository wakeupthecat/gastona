/*
library de.elxala
Copyright (C) 2023 Alejandro Xalabarder Aulet

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
import de.elxala.db.utilEscapeStr;
import javaj.widgets.basics.widgetLogger;

/*
   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#


   <docType>    listix_primitives
   <name>       varTable2sql
   <groupInfo>  sql
   <javaClass>  de.elxala.db.dbMore.varTable2sql
   <importance> 2
   <desc>       //Generate a SQL select from a table contained in an eva variable


   <help>
      //   This primitive generates a SQL select from an eva variable that contains a table with or without column names
      //
      //   Aliases:
      //       varTable2sql
      //       evaTable2sql
      //       eva2sql
      //       var2sql
      //       sqlEva2table
      //       sqlVar2table
      //
      //   Options or modifiers:
      //       -indent  it returns the sql in more lines and indented
      //
      //   Syntax:
      //
      //       @<:sqlFromEva[-indent] evaVarName [col1 col2 ... colN]>
      //
      //   If columns (col1..colN) are specified then the variable contains no column names in its first row
      //   that means all rows are data, otherwise the first row contains the column names
      //
      //   Example:
      //
      //    if we have the eva table
      //
      //       <myTable>
      //          id, name, year
      //          01, Lumumba, 1961
      //          02, Victor Jara, 1972
      //
      //    then the primitive
      //
      //       @<:varTable2sql-indent myTable>
      //
      //    generates the SQL select
      //
      //            SELECT  '01' AS 'id', 'Lumumba' AS 'name', '1961' AS 'year',
      //              UNION ALL
      //            SELECT  '02' AS 'id', 'Victor Jara' AS 'name', '1972' AS 'year',
      //
      // Note:
      //
      //    We can achieve similar result through following listix format
      //
      //         <var2sql>
      //            LOOP, VAR, @<p1>
      //                , LINK, " UNION "
      //                ,, // SELECT
      //                ,, LOOP, COLUMNS
      //                ,,     , LINK, ","
      //                ,,     ,,  // '@<:escape columnValue>' AS '@<columnName>'
      //   calling
      //
      //         LSX, var2sql, myTable


   <examples>
      gastSample
      sqlFromEva sample
      sortList sample

   <sqlFromEva sample>
      //#javaj#
      //
      //   <frames> oosqlFromEva Example
      //
      //#data#
      //
      //   <myTable>
      //      id, name, year
      //      01, Patrice Lumumba, 1961
      //      02, Victor Jara, 1973
      //      03, Shireen Abu Akleh, 2022
      //
      //#listix#
      //
      //   <main0>
      //      //---- using primitive
      //      //
      //      //@<:varTable2sql-indent myTable>
      //      //
      //      //---- using listix format
      //      //
      //      LSX, var2sql, myTable
      //
      //   <var2sql>
      //      LOOP, VAR, @<p1>
      //          , LINK, " UNION "
      //          ,, // SELECT
      //          ,, LOOP, COLUMNS
      //          ,,     , LINK, ","
      //          ,,     ,,  // '@<:escape columnValue>' AS '@<columnName>'

   <sortList sample>
      //#javaj#
      //
      //   <frames> oosqlFromEva sort list Example
      //
      //#data#
      //
      //   <myList>
      //      Patrice
      //      Victor
      //      Shireen
      //
      //#listix#
      //
      //   <main0>
      //      //---- sorted list
      //      //
      //      LOOP, SQL,, SELECT * FROM (@<:varTable2sql myList name>) ORDER BY name
      //          ,, @<name>

#**FIN_EVA#

*/
public class varTable2sql
{
   // public static void main (String [] aa)
   // {
   //    System.out.println ("Examples of resulting SQL filter strings for ");
   //    System.out.println ("the given asiste values of the fields 'id', 'name' and 'size'");
   //    System.out.println ("");
   //
   //    if (aa.length >= 3)
   //    {
   //       procesaAndShow (aa);
   //    }
   //    else
   //    {
   //       procesaAndShow (new String [] { "prev", "tabula", "tstamp", "date", "etc" });
   //       procesaAndShow (new String [] { "delta", "tabula", "tstamp" });
   //    }
   // }
   //
   // private static void procesaAndShow (String [] datos)
   // {
   //    String [] arr2 = new String[datos.length-1];
   //
   //    for (int ii = 0; ii < datos.length; ii ++)
   //    {
   //       System.out.print ("\"" + datos[ii] + "\"\t");
   //       if (ii > 0)
   //          arr2[ii] = datos[ii-1];
   //    }
   //
   //    if (datos[0].equalsIgnoreCase ("prev"))
   //    {
   //       System.out.println (getTablePrevSQL (arr2));
   //    }
   //    if (datos[0].equalsIgnoreCase ("delta"))
   //    {
   //       System.out.println (getTableDeltaSQL (arr2));
   //    }
   //    else
   //       System.out.println ("invalid first parameter '" + datos[0] + "', it has to be prev or delta");
   // }

   public static String varTable2sql (Eva evaTableData, String [] columnNames, boolean bIndent)
   {
      int rowData0 = 0;
      if (columnNames == null || columnNames.length == 0)
      {
         // get the column names from row == 0
         columnNames = evaTableData.getStrArray (0);
         rowData0 = 1;
      }

      if (evaTableData.rows () < rowData0 + 1)
      {
         widgetLogger.log().err ("sqlTableDeltaUtil::varTable2sql", "given no data");
         return ""; // produces no result!
      }

      String SPC_OR_RET = bIndent ? "\n": " ";
      StringBuffer strb = new StringBuffer ("");

      for (int rr = rowData0; rr < evaTableData.rows (); rr ++)
      {
         strb.append ("SELECT ");
         for (int cc = 0; cc < columnNames.length; cc ++)
         {
            strb.append ("'" + utilEscapeStr.escapeStr (evaTableData.getValue (rr, cc)) + "' AS '" +
                         columnNames [cc] +
                         "'" + (1 + cc < columnNames.length ? ",": "")
                        );
         }
         if (rr + 1 < evaTableData.rows ())
            strb.append (SPC_OR_RET + "UNION" + SPC_OR_RET);
      }
      return strb.toString ();
   }
}
