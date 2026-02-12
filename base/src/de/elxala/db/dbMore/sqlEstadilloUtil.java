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

/**

   https://context.reverso.net/translation/spanish-english/estadillo
   "summary report"

   also
   "cross table"

-- sqlEstadillo

      :sqlEstadillo srcTable col2sum colX colX

      mainTable = (SELECT colx AS x, coly AS y, col2sum AS N FROM srcTable)
      estadillo =
        (SELECT  y, ROUND(SUM(N)) AS sumaYs FROM mainTable GROUP BY y)
         INNER JOIN
        (SELECT  x, ROUND(SUM(N)) AS sumaXs FROM mainTable GROUP BY x)
         LEFT JOIN
        (SELECT x, y, ROUND(SUM(N)) AS suma FROM mainTable GROUP BY x, y) USING (y, x)


further use

      SELECT * FROM estadillo ORDER BY sumaYs+0 DESC,sumaXs+0 DESC ;

*/
public class sqlEstadilloUtil
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
      System.out.println (getEstadilloSQL (datos));
   }


   public static String getEstadilloSQL (String [] params)
   {
      return getEstadilloSQL (params, true);
   }

   public static String getEstadilloSQL (String [] params, boolean spaceIndent)
   {
      if (params.length < 2)
      {
         widgetLogger.log().err ("sqlEstadilloUtil::getEstadilloSQL", "given " + params.length + " parameters but at least 2 are expected (srcTable col2sum colX colX)");
         return ""; // produces no result!
      }

      String RET = spaceIndent ? "\n": "";

      // :sqlEstadillo srcTable col2sum colX colY
      //
      // mainTable = (SELECT colx AS x, coly AS y, col2sum AS N FROM srcTable)
      // estadillo =
      //   (SELECT  y, ROUND(SUM(N)) AS sumaYs FROM mainTable GROUP BY y)
      //    INNER JOIN
      //   (SELECT  x, ROUND(SUM(N)) AS sumaXs FROM mainTable GROUP BY x)
      //    LEFT JOIN
      //   (SELECT x, y, ROUND(SUM(N)) AS suma FROM mainTable GROUP BY x, y) USING (y, x)

      String table = params[0];
      String col2sum = params[1] == "*" ? "1": params[1];
      String colX  = params.length > 2 ? params[2]: "'x'";
      String colY  = params.length > 3 ? params[3]: "'y'";

      String mainTable = "(SELECT " + colX + " AS x, " + colY + " AS y, " + col2sum +  " AS N FROM " + table + ")";

      return
            "(SELECT  y, ROUND(SUM(N)) AS sumaYs FROM " + mainTable + " GROUP BY y)" + RET +
            " INNER JOIN" + RET +
            "(SELECT  x, ROUND(SUM(N)) AS sumaXs FROM " + mainTable + " GROUP BY x)" + RET +
            " LEFT JOIN" + RET +
            "(SELECT x, y, ROUND(SUM(N)) AS suma FROM " + mainTable + " GROUP BY x, y)" + RET +
            " USING (y, x)";
   }
}
