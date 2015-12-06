/*
de.elxala.parse
Copyright (C) 2012 Alejandro Xalabarder Aulet

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

package de.elxala.parse.json;

import org.json.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import java.util.*;
import java.lang.*;
import de.elxala.zServices.*;
import de.elxala.langutil.*;

public class json2EVA
{
   // gastona logger
   //
   private static logger log = new logger (null, "de.elxala.parse.json.json2EVA", null);

   public static final String DEFAULT_IFNOT_STRING = "";

   public static final int TABLE_POLICY_REDUCED = 0;  // Only creates new tables when a new array is found
   public static final int TABLE_POLICY_PROLIFIC = 1; // Creates new tables also when a object type is found

   public int totalCounter = 0;
   protected Eva eSchema = null;
   public int theTablePolicy = TABLE_POLICY_REDUCED;

   public json2EVA ()
   {
   }

   public json2EVA (int tablePolicy)
   {
      theTablePolicy = tablePolicy;
   }

   public static void main (String aa [])
   {
      if (aa.length != 1)
      {
         System.out.println ("Quiero un fichero JSON tuyo!");
         return;
      }

      // Eva eva = getJsonFileAsEva (aa[0]);
      // System.out.println ("resultat:");
      // System.out.println (eva);

      EvaUnit eu = new EvaUnit ("JSON test");

      new json2EVA ().buildEvaUnitFromJSONFile (aa[0], "JSONXemari", eu);
      System.out.println (eu);
   }

   public void buildEvaUnitFromJSONFile (String jsooFileName, String baseName, EvaUnit eu)
   {
      totalCounter = 0;

      StringBuffer sb = TextFile.readFileIntoStringBuffer (jsooFileName);
      if (sb == null)
      {
         log.err ("buildEvaUnitFromJSONFile", "file " + jsooFileName + " could not be read");
         return;
      }
      buildEvaUnitFromJSONString (sb.toString (), baseName, eu);
   }

   public void buildEvaUnitFromJSONString (String jsooString, String baseName, EvaUnit eu)
   {
      JSONObject jsoo = null;
      try
      {
         jsoo = new JSONObject (jsooString);
      }
      catch (Exception e)
      {
         log.err ("buildEvaUnitFromJSONString", "exception parsing a JSON string : " + e);
      }
      if (jsoo != null)
         buildEvaUnitFromJSONobj (jsoo, baseName, eu);
   }

   /**
      Note: it does not clear the EvaUnit but only add the values and create variables if necessary
   */
   public void buildEvaUnitFromJSONobj (JSONObject jsoo, String baseName, EvaUnit eu)
   {
      totalCounter = 0;

      eSchema = eu.getSomeHowEva (baseName + "_SCHEMA");
      eSchema.clear ();
      eSchema.addLine (new EvaLine ("table, parent"));

      poblateEvaUnitFromJSONobj (jsoo, 0, baseName, "ROOT", "", eu);
   }


   // cannot be public static because it uses totalCounter
   //
   protected void poblateEvaUnitFromJSONobj (JSONObject jsoo, int parentRecord, String baseName, String subName, String campoBase, EvaUnit eu)
   {
      EvaLine eColNames = new EvaLine();
      EvaLine eColValues = new EvaLine();

      int currentRow = -1; // should never have this value!
      int currentCount = 0;

      boolean newRecord = subName.length () > 0;
      String newBaseName = Cadena.linkStrings (baseName, subName, "_");
      Eva tabla = eu.getSomeHowEva (newBaseName);
      if (tabla.rows() == 0)
      {
         // table didn't exist before, create it
         tabla.setValue ("patCount" , 0, 0);
         tabla.setValue ("patParent", 0, 1);
         currentRow = 0;

         // add line in schema
         eSchema.addLine (new EvaLine (newBaseName + ", " + baseName));
      }
      else
      {
         currentRow = tabla.rows ()-1;
         currentCount = stdlib.atoi (tabla.getValue (currentRow, 1));
      }

      if (newRecord)
      {
         currentCount = totalCounter ++;
         currentRow ++;
         tabla.setValue (""+currentCount, currentRow, 0);
         tabla.setValue (""+parentRecord, currentRow, 1);
      }


      try
      {
         // only for Java PC, Android's JSON does not have the method getNames !!
         // String [] fields = JSONObject.getNames (jsoo);
         JSONArray fieldsArr = jsoo.names();

         for (int ii = 0; ii < fieldsArr.length (); ii ++)
         {
            String fieldName = fieldsArr.getString (ii);
            JSONObject theObj = jsoo.optJSONObject(fieldName);
            JSONArray theArr = jsoo.optJSONArray(fieldName);

            String newFieldName = Cadena.linkStrings (campoBase, fieldName, "_");

            if (theObj != null)
            {
               if (theTablePolicy == TABLE_POLICY_REDUCED)
                    poblateEvaUnitFromJSONobj (theObj, currentCount, newBaseName, "", newFieldName, eu);
               else poblateEvaUnitFromJSONobj (theObj, currentCount, newBaseName, newFieldName, "", eu);
               continue;
            }
            if (theArr != null)
            {
               // an JSONArray might contain object but also be just an array of strings!
               // so if it is an array of strings we create an artificial object called value
               //   "value": stringvalue
               for (int aa = 0; aa < theArr.length (); aa ++)
               {
                  JSONObject jo = theArr.optJSONObject(aa);
                  if (jo == null)
                  {
                     jo = new JSONObject ("{ value: \"" + theArr.optString (aa, DEFAULT_IFNOT_STRING) + "\" }");
                  }

                  poblateEvaUnitFromJSONobj (jo, currentCount, newBaseName, newFieldName, "", eu);
                  //NOTE/QUESTION: if an array with a specific name contain always the same type of object then we could do simply
                  //poblateEvaUnitFromJSONobj (jo, currentCount, newBaseName, fields[ii], "", eu);
               }
               continue;
            }
            // No JSONObject, no JSONArray, then normal field
            //

            String valor = jsoo.optString (fieldName);

            // check if column exists and create it if it does not exists
            int colIndx = tabla.colOf (newFieldName);
            if (colIndx == -1)
            {
               // we add a new column for the new field
               colIndx = tabla.cols (0);
               tabla.setValue (newFieldName, 0, colIndx);
            }

            // finally set the value
            tabla.setValue (valor, currentRow, colIndx);
            //System.out.println ("field=" + fields[ii] + " value=" + valor);
         }
      }
      catch (Exception e)
      {
         log.err ("poblateEvaUnitFromJSONobj", "exception parsing JSON : " + e);
      }
   }
}

/*

#javaj#

   <frames>
       oConsole, "JSON example", 200, 300

#data#

   <JSONData>
      //{
      //miarro: [
      // {
      // menu:{"1":"sql", "2":"android", "3":"mvc"},
      // "rumbero": "afrosio",
      // identiti: framio,
      // telephones: [
      //   {
      //      loc: case,
      //      tel: "123213"
      //   },
      //   {
      //      loc: traball,
      //      tel: "43453453"
      //   }
      //   ]
      // },
      // {
      // menu:{"1":"bardo", "2":"asterix", "3":"osbeslis"},
      // "rumbero": "matansas",
      // identiti: wolframio,
      // telephones: [
      //   {
      //      loc: case,
      //      tel: "777777"
      //   },
      //   {
      //      loc: traball,
      //      tel: "1818xxx"
      //   }
      //   ]
      // },
      // ],
      // pertardo: xulo
      //}
      //


#listix#

   <main>
      JSON, EVA2VARS, JSONData
      DUMP, data
*/
