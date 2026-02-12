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

/**
   (former src/javaj/widgets/table/util/utilAsiste.java)
   utility methods about filtering with AsisteCampos (method buildSQLFilterString)

   Examples:

         asiste Values :     ""           "A"            ""
         asiste Names  :     id           name          size

         result filter :   WHERE (name LIKE '%A%')

         asiste Values :    "X Y"         "<H"          ")1024"
         asiste Names  :     id           name          size

         result filter :   WHERE (id LIKE '%X%' AND id LIKE '%Y%') AND (name < 'I') AND (size + 0 >= 1024) ORDER BY name DESC, size + 0

*****NEW*********
         asiste Values :     ""           "*>"          "+>"
         asiste Names  :     id           name          size

         result filter :   SELECT name, SUM(size), COUNT(*) AS cnt GROUP BY name;

     [] tiene que ser un SELECT "limipio"
     [] quitar ; del final
     [] orden by ? sum y count ?
     [] puede ser muy "inperformante"


*/
public class sqlSearchFilter
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
         procesaAndShow (new String [] { "", "A", "" });
         procesaAndShow (new String [] { "EL LO", ">H", "(1024" });
         procesaAndShow (new String [] { "]JA", "<H", "88" });
      }
   }

   private static void procesaAndShow (String [] datos)
   {
      for (int ii = 0; ii < 3; ii ++)
      {
         System.out.print ("\"" + datos[ii] + "\"\t");
      }
      System.out.println (":" + buildSQLFilterString (new String [] { "id", "name", "size" }, datos));
   }


   /**
      About functions
            getComposedWhereCondition        (same as getComposedWhereConditionAny (OR))
            getComposedWhereConditionAny     (in case multiple columns linked with OR)
            getComposedWhereConditionAll     (in case multiple columns linked with AND)

      composes the "WHERE [ORDER BY]" expression for the given Asiste String and the given colum names

      the first string is a search words (only single words!) are separated by spaces
      "searchword" is evaluated as
            col LIKE '%searchword%"
      unless is preceded by "-" like "-searchword", in that case
            col NOT LIKE '%searchword%"

      by default all search words are linked with "AND"
      but if one is preceded by "+" its join will be "OR"

      Examples:

     function  input parameters       resulting condition
     --------- -----------------      -----------------------------------------------------------------
               { "A B -C", col }      WHERE (col LIKE '%A%' AND col LIKE '%B%' AND col NOT LIKE '%C%')

               { "A +B +C", col }     WHERE (col LIKE '%A%' OR col LIKE '%B%' OR col LIKE '%C%')

     Any|All   { "A B", col1, col2 }  WHERE (col1 LIKE '%A%' AND co1 LIKE '%B%') (AND|OR)
                                            (col2 LIKE '%A%' AND co2 LIKE '%B%')
   */
   public static String getComposedWhereCondition (String [] asisteStrAndColumns)
   {
      return getComposedWhereConditionAny (asisteStrAndColumns);
   }

   /**
      same as getComposedWhereCondition, it builds the condition between the different columns using "OR"
   */
   public static String getComposedWhereConditionAny (String [] asisteStrAndColumns)
   {
      return getComposedWhereCondition (asisteStrAndColumns, "OR");
   }

   /**
      different from getComposedWhereCondition for more columns, it builds the condition between the
      different columns using "AND"
   */
   public static String getComposedWhereConditionAll (String [] asisteStrAndColumns)
   {
      return getComposedWhereCondition (asisteStrAndColumns, "AND");
   }

   /**
      general call for getComposedWhereCondition, getComposedWhereConditionAny and getComposedWhereConditionAll
   */
   public static String getComposedWhereCondition (String [] asisteStrAndColumns, String linkOperation) // throws Exception
   {
      if (asisteStrAndColumns.length < 2)
      {
         widgetLogger.log().err ("utilAsiste::getComposedWhereCondition", "given " + asisteStrAndColumns.length + " parameters but at least 2 were expected (assiteString and a column name)");
         return ""; // produces no result!
      }

      String [] asisteString = new String [asisteStrAndColumns.length-1];
      String [] arrColumns = new String [asisteStrAndColumns.length-1];

      // all columns the same asiste condition
      for (int ii = 0; ii < asisteString.length; ii ++)
         asisteString[ii] = asisteStrAndColumns[0];

      // copy all column names
      for (int ii = 1; ii < asisteStrAndColumns.length; ii ++)
         arrColumns[ii-1] = asisteStrAndColumns[ii];

      String sola = buildSQLFilterString (arrColumns, asisteString, linkOperation);
      return sola;
   }


   /**
      basic Asiste settings

      all the Asiste Campos different from empty string will act as filter and/or influence the order
      there are some conventions depending on the value of the field :

         First
         character
         of value      meaning        SQL Filter and order
         ---------   --------------   ----------------------------------
            (        (< numeric)      WHERE .... field + 0 <= value2 ... ORDER BY ... field
            )        (> numeric)      WHERE .... field + 0 >= value2 ... ORDER BY ... field DESC

            >        (greater than)   WHERE .... field >= 'value2' ... ORDER BY ... field
            <        (less than)      WHERE .... field <  'value2ZZ' ... ORDER BY ... field DESC

            ]        (G than strict)  WHERE .... (field >= 'value2' AND field < 'value2ZZ') ... ORDER BY ... field
            [        (L than strict)  WHERE .... (field <  'value2ZZ' AND fiedl >= 'value2') ... ORDER BY ... field DESC

            '        (no interpret)   WHERE ... field LIKE '%value2%'
         otherwise                    WHERE ... a composition based on (field LIKE '%value i%')


         where
            value2   = value - first character
            value2ZZ = value2 but with the last character incremented in one

         a composition based on:
            for each subvalue (the string is splitted in subvalues "value i" separated by white space)
            if starting with
               & (default) AND
               + OR
               - NOT
   */
   public static String buildSQLFilterString (String [] asisteCampoNames, String [] asisteValues, String conditionJoin)
   {
      String strCONDI = "";
      String strORDER = "";

      char LAST_CHAR = (char) 254;

      if (asisteCampoNames.length != asisteValues.length)
      {
         widgetLogger.log().severe ("utilAsiste::buildSQLFilterString", "wrong call to buildSQLFilterString: (#columns) " + asisteCampoNames.length + " != (#values) " + asisteValues.length );
         return ""; // produces no result!
      }

      for (int ii = 0; ii < asisteCampoNames.length; ii ++)
      {
         String value = asisteValues[ii];
         if (value == null || value.length () == 0) continue;

         String campoName = asisteCampoNames [ii];

         String plus = "";
         String ordenDir = "";
         boolean ordenarPor = true;
         boolean isNumeric = false;

         char firstC = value.charAt (0);
         String val2 = value.substring (1);

         // val2Z is the value + 1 character more at the end, for example val2="SONA" val2Z="SONB"
         // it is used for strict (]) and for reverse order
         String val2ZZ = val2;
         int LE2 = val2ZZ.length ();
         if (LE2 > 0)
            val2ZZ = val2.substring(0, LE2 - 1) + (char) (val2.charAt(LE2 - 1) + 1);
         else if (firstC == '<')
            val2ZZ = "" + LAST_CHAR;

         // concat the condition if needed
         //
         if (strCONDI.length () > 0) strCONDI += " " + conditionJoin + " ";

         switch (firstC)
         {
            case ')':
               // numeric
               if (val2.length () == 0)
                  val2 = "0";
               strCONDI += "(" + campoName + " + 0 >= " + val2 + ")";
               ordenDir = "";
               isNumeric = true;
               break;

            case '(':
               // numeric
               if (val2.length () == 0)
                  val2 = "9e999";
               strCONDI += "(" + campoName + " + 0 <= " + val2 + ")";
               ordenDir = " DESC";
               isNumeric = true;
               break;

            case ']':
               // Ejemplo : "]SU"
               //           ... (field >= 'SA' and field < 'SB') ... ORDER BY field DESC
               if (LE2 > 0)
               {
                  plus = " AND " + campoName + " < '" + val2ZZ + "'";
               }
               // NO BREAK;

            case '>':
               strCONDI += "(" + campoName + " >= '" + val2 + "'" + plus + ")";
               break;

            case '[':
               // Ejemplo : "[SU"
               //           ... (field < 'SB' and field >= 'SA') ... ORDER BY field DESC
               plus = " AND " + campoName + " >= '" + val2 + "'";
               // NO BREAK;

            case '<':
               strCONDI += "(" + campoName + " < '" + val2ZZ + "'" + plus + ")";
               ordenDir = " DESC";
               break;

            case '\'':
               // thought for searching things like "<a" then give "'<a"
               //
               value = val2;
               // NO BREAK!

            default:
               // typically "camponame LIKE '%value%'
               // but it accepts also multiple values separared by white space
               // for the conditions in a way likely to search engines : nothing indicates AND and + OR
               //
               // Example:
               //          a value "DVD +CD Ctr" will be translate into the condition
               //          (campoName LIKE '%DVD%' OR campoName LIKE '%CD%' AND campoName LIKE '%Ctr%')
               //
               String [] spliti = value.split (" ");
               String cond = "";

               for (int jj = 0; jj < spliti.length; jj ++)
               {
                  String val = spliti[jj];
                  if (val.length () == 0) continue;

                  boolean negate = false;
                  String linkOpe = " AND ";

                  switch (val.charAt(0))
                  {
                     case '&':
                        val = val.substring (1);
                        break;

                     case '\'':  // one '
                        val = val.substring (1);
                        break;

                     case '+':
                        val = val.substring (1);
                        linkOpe = " OR ";
                        break;

                     case '-':
                        val = val.substring (1);
                        negate = true;
                        break;

                     default:
                        break;
                  }

                  if (cond.length () != 0)
                     cond += linkOpe;
                  cond += campoName + (negate ? " NOT": "") + " LIKE "+ "'%" + val + "%'";
               }

               strCONDI += "(" + cond + ")";
               ordenarPor = false;
               break;
         }

         if (ordenarPor)
         {
            if (strORDER.length () > 0) strORDER += ", ";
            strORDER += campoName + (isNumeric ? " + 0": "") + ordenDir;
         }
      } // END : for (int ii = 0; ii < eNames.cols(0); ii ++)

      String strFilter = "";

      if (strCONDI.length () > 0)
          strFilter += (" WHERE " + strCONDI);
      if (strORDER.length () > 0)
         strFilter += " ORDER BY " + strORDER;

      // System.out.println ("FILTER = [" + strFilter + "]");

      return strFilter;
   }

   public static String buildSQLFilterString (String [] asisteCampoNames, String [] asisteValues)
   {
      return buildSQLFilterString (asisteCampoNames, asisteValues, "AND");
   }

   /**
      detect if group

*****NEW*********
         asiste Values :     ""           "*>"          "+>"
         asiste Names  :     id           name          size

         result filter :   SELECT name, SUM(size), COUNT(*) AS cnt GROUP BY name;

     [] tiene que ser un SELECT "limipio"
     [] quitar ; del final
     [] orden by ? sum y count ?
     [] puede ser muy "inperformante"

      // PREPAR ...
      // SELECT xxxxxx FROM (strSQL) WHERE xxx ORDER BY

      return null if no group is found

   */
   public static String buildSQLGroupBY (String [] asisteCampoNames, String [] asisteValues, String strSQL)
   {
      List groupList = new Vector ();
      List totalList = new Vector ();

      for (int ii = 0; ii < asisteCampoNames.length; ii ++)
      {
         String value = asisteValues[ii];
         if (value.length () == 0) continue;

         String campoName = asisteCampoNames [ii];

         char firstC = value.charAt (0);
         String val2 = value.substring (1);

         switch (firstC)
         {
            case '*':
               groupList.add (campoName);
               // [] eval order in orderList1
               break;
            case '+':
               totalList.add (campoName);
               // [] eval order in orderList2
               break;
            default:
               break;
         }
      } // END : for (int ii = 0; ii < eNames.cols(0); ii ++)

      if (groupList.size () == 0) return null;

      String strSQLFinal = "SELECT ";

      for (int ii = 0; ii < groupList.size (); ii++)
      {
         strSQLFinal += (ii != 0 ? ", ": "") + ((String) groupList.get (ii));
      }
      for (int ii = 0; ii < totalList.size (); ii++)
      {
         strSQLFinal += ", " + "SUM (" + ((String) totalList.get (ii)) + ")";
      }
      strSQLFinal += " FROM (" + strSQL + ") GROUP BY ";
      for (int ii = 0; ii < groupList.size (); ii++)
      {
         strSQLFinal += (ii != 0 ? ", ": "") + ((String) groupList.get (ii));
      }

      //System.out.println ("LIBERO = [" + strSQLFinal + "]");

      return strSQLFinal;
   }
}
