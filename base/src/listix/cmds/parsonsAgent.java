/*
library listix (www.listix.org)
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

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

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.parse.parsons.*;
import de.elxala.db.sqlite.sqlSolver;

/*
*/
public class parsonsAgent
{
   static final int DB_TABLE = 0;
   static final int EVA_TABLE = 1;
   static final int SINGLE_EVA_VALUE = 2;

   public aLineParsons parsons = null;
   public String tableName = "";
   public int agentType = DB_TABLE;

   private String cteNamesCS = "";     // comma separated column names for constants p.e. "dateParse, user"
   private String cteValuesCS = "";    // comma separated values for constants p.e. "'2012/01/01', '@<user>'"

   private int firstLineNr = -1;
   private int lastRecFirstLine = -1;
   private int lastRecLastLine = -1;
   private String parsingRemainingLine = "";

   public parsonsAgent (String tabname)
   {
   }

   public parsonsAgent (String tabname, int pAgentType)
   {
      tableName = tabname;
      agentType = pAgentType;
   }

   boolean isTypeDB_TABLE () { return agentType == DB_TABLE; }
   boolean isTypeEVA_TABLE () { return agentType == EVA_TABLE; }
   boolean isTypeSINGLE_EVA_VALUE () { return agentType == SINGLE_EVA_VALUE; }


   public parsonsAgent (String [] tabParameters, int pAgentType)
   {
      agentType = pAgentType;

      if (tabParameters.length < 1) return;
      tableName = tabParameters[0];

      if (isTypeDB_TABLE ())
      {
//System.out.println ("IS DBTABLE!!");
         // Exaple with contants
         // TABLE, tableName, CTE, "cte1, second, etc", //'772', '@<myVar>', 'fin'
         //          0         1      2                  3
         if (tabParameters.length < 4) return;  // not recognized
         cteNamesCS = tabParameters[2];
         cteValuesCS = tabParameters[3];
      }
      else if (isTypeEVA_TABLE ())
      {
//System.out.println ("IS EVATABLE!!");
         // Exaple with contants
         // VARTABLE, varName
         return;
      }
      else if (isTypeSINGLE_EVA_VALUE ())
      {
         // Exaple with contants
         // VAR, var1, var2, ..., //pattern

//System.out.println ("IS SINGLE_VAR!!");
         Eva eva = new Eva ("");

         eva.addLine (new EvaLine (tabParameters));
//         for (int ii = 1; ii < tabParameters.length; ii ++)
//            evaWithPattern.setValue (tabParameters[ii], 0, ii-1);

         parsons = new aLineParsons (eva);
      }
   }

   /**
       get the first line and last line of the last matched record as a string
       p.e. "144, 147"
   */
   public String getFirstAndLastLines ()
   {
      return lastRecFirstLine + ", " + lastRecLastLine;
   }

   public int getFirstLine ()
   {
      return lastRecFirstLine;
   }

   public int getLastLine ()
   {
      return lastRecLastLine;
   }

   public int getColumnCount ()
   {
      return  parsons.getCurrentRecord().length +
              parsons.getCurrentOptionalColumnsRecord().length;
   }

   public parsonsColumn getColumn (int indx)
   {
      int ran1 = parsons.getCurrentRecord ().length;
      int ran2 = parsons.getCurrentOptionalColumnsRecord ().length;

      if (indx < 0 || indx >= ran1 + ran2)
         return new parsonsColumn ("");

      return indx < ran1 ?
              parsons.getCurrentRecord()[indx] :
              parsons.getCurrentOptionalColumnsRecord()[indx-ran1] ;
   }


   /// get comma separated string with column names of the record
   public String getColumnNamesCS ()
   {
      String ret = cteNamesCS;

      for (int ii = 0; ii < getColumnCount (); ii ++)
      {
         parsonsColumn col = getColumn (ii);
         if (col.isSpecial ()) continue;

         ret += (ret.length () > 0 ? ", ": "") + col.getName ();
      }

      return ret;
   }

   public String getValuesCS (listix that)
   {
      return getValuesCS (that, false);
   }

   public String getValuesCS (listix that, boolean trim)
   {
      String ret = that.solveStrAsStringFast (cteValuesCS);

      for (int ii = 0; ii < getColumnCount (); ii ++)
      {
         parsonsColumn col = getColumn (ii);
         if (col.isSpecial ()) continue;

         String v0 = col.getValue ();
         if (v0 == null) v0 = "";
         ret += (ret.length () > 0 ? ", '": "'") + sqlSolver.escapeString(trim ? v0.trim (): v0) + "'";
      }

      return ret;
   }

   public boolean checkAllValues ()
   {
      parsonsColumn [] mainColumns = parsons.getCurrentRecord ();
      for (int ii = 0; ii < mainColumns.length; ii ++)
      {
         if (mainColumns [ii].isRegular () && mainColumns [ii].getValue () == null)
            return false;
      }
      return true;
   }

   public String firstUnfilledColumn ()
   {
      parsonsColumn [] mainColumns = parsons.getCurrentRecord ();
      for (int ii = 0; ii < mainColumns.length; ii ++)
      {
         if (mainColumns [ii].isRegular () && mainColumns [ii].getValue () == null)
            return "(" + ii + ") " + mainColumns [ii].getName ();;
      }
      return null;
   }

   public boolean hasPatterns ()
   {
      return parsons.hasPatterns ();
   }

   public boolean hasRecordCompleted ()
   {
      return parsons.recordComplete ();
   }

   public boolean needDataToCompleteRecord ()
   {
      // NOTE: although completedOrEmpty () == !needDataToCompleteRecord ()
      //       keep them separate for better legibility
      return hasPatterns () && !parsons.recordComplete ();
   }

   public boolean completedOrEmpty ()
   {
      // NOTE: although completedOrEmpty () == !needDataToCompleteRecord ()
      //       keep them separate for better legibility
      return !hasPatterns () || parsons.recordComplete ();
   }

   public boolean hasRemainingLine ()
   {
      return parsingRemainingLine.length () > 0;
   }

   public String getRemainingLine ()
   {
      return parsingRemainingLine;
   }

   public void consumeRemainingLine ()
   {
      parsingRemainingLine = "";
   }

   public void consumeRecord ()
   {
      parsons.recordConsumed ();
   }

   /**
      parses a line to find matches
      if the record is completed then "boolean hasRecordCompleted ()"
      returns true and it could be that a remaining line exists

      the algorithm to parse with an agent might be something like

         while (readLine (line))
         {
            lineNr ++;
            do
            {
               agent.parseLine (line, lineNr);
               if (agent.hasRecordCompleted ())
               {
                  writeRecord ();
                  line = agent.getRemainingLine ();
               }
            } while (agent.hasRemainingLine ());
         }

      to write the record, following methods the result can be used

         String getFirstAndLastLines ()    e.g. "177, 179"
         String getColumnNamesCS ()       e.g. "id,name,tel"
         String getValuesCS ()            e.g. "'3321', 'Ramon', '771-99910001'

      Note that if hasRecordCompleted is false hasRemainingLine is also false.

   */
   public void parseLine (String lineStr, int lineNr)
   {
      parsingRemainingLine = lineStr;
      while (parsingRemainingLine.length() > 0)
      {
         int indxCol = parsons.scan (parsingRemainingLine);

         //detect first line of record
         //
         if (firstLineNr == -1 && indxCol > 0)
            firstLineNr = lineNr;

         if (indxCol > 0)
              parsingRemainingLine = parsingRemainingLine.substring (indxCol);
         else parsingRemainingLine = "";
         if (parsons.recordComplete ())
         {
            lastRecFirstLine = firstLineNr;
            lastRecLastLine  = lineNr;

            // reset first line
            firstLineNr = -1;
            break;
         }
      }
   }
}