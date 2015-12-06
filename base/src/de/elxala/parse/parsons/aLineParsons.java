/*
package de.elxala
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package de.elxala.parse.parsons;

/**   ======== de.elxala.parse.parsons.aLineParsons ==========================================
   Alejandro Xalabarder


   20.03.2008 17:50    created
*/

import java.util.*;
import java.util.regex.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;


/**
   class aLineParsons
   @author Alejandro Xalabarder Aulet
   @date   2008

   Class to facilitate parsing lines.
   This class is the kern of the Listix command PARSONS (used directly in listix/cmds/cmdParsons.java)
   See documentation of the listix command in welcomeGastona.
   Provides also a command line call (see main)

   20.03.2008 17:51 Creation
*/
public class aLineParsons
{
   public class fieldType
   {
      public String name = null;
      public String value = null;
      public String oldValue = null;

      public boolean varOptional = false;
      public boolean keepValue = false;

      public fieldType ()
      {
      }

      public fieldType (String fieldName, boolean isOptional, boolean keepVal)
      {
         name = fieldName;
         keepValue = keepVal;
         varOptional = isOptional;
      }

   };

   private fieldType [] currentRecord;

   private int [] offsetFields = null;     // on each pattern starts a field, this is the index of this field
   private Pattern [] arrPatterns = null;  // array of patterns to check for each line

   private int nextPattern = -1;           // current pattern to start with while scanning a line
   private boolean recordComplete = false;
   private boolean ready = false;

   private logger log = new logger (this, "de.elxala.parse.parsons.aLineParsons", null);

   public Eva patternMap;                  // original description of the pattern-Field mapping

   public aLineParsons ()
   {
      patternMap = new Eva ("field pattern map");
      ready = false;
   }

   public aLineParsons (Eva thePatternMap)
   {
      patternMap = thePatternMap;
      ready = false;
   }

   public void addFieldsPatternMap (String allInOne)
   {
      patternMap.addLine (new EvaLine (allInOne));
      ready = false;
   }

   public void addFieldsPatternMap (String pattern, String [] fields)
   {
      patternMap.addLine (new EvaLine (fields));

      patternMap.addCol (pattern, patternMap.rows()-1);
      ready = false;
   }

   /**

      <patternFieldMap>

         :keep, :optional, headName, //TITLE: (.*)

         fileName   , //FileName: (.*)
         time       , //Timestamp: (.*)
         Camera     , //Camera: (.*)
         ISO        , //ISO speed: (.*)

         shutter, units, //Shutter: (.*) (.*)


   
      where 
         :keep     => the field value will kept, not reset on each new record
         :optional => the field is optional, might or might not appear
                      (policy ? if it does not appear .. NULL or empty string ?)

   */
   public boolean init ()
   {
      log.dbg (5, "init", "analyzing pattern [" + patternMap + "]");
//System.out.println ("patternMap is " + patternMap);
      // recognizing the fields
      //
      List campos = new Vector();

      offsetFields = new int [patternMap.rows ()];
      arrPatterns  = new Pattern [patternMap.rows ()];
      int offset = 0;
      boolean isOptional = false;
      boolean toBeKept = false;

      // for each row a pattern and one or more fields
      //
      for (int ii = 0; ii < patternMap.rows (); ii ++)
      {
         isOptional = false;
         toBeKept = false;
         if (patternMap.cols (ii) < 2)
         {
            log.err ("init", "no pattern found at row " + ii + " (the rowNS has only one column)");
            return false;
         }

         String thePattern = patternMap.getValue (ii, patternMap.cols (ii) - 1);

         try
         {
            arrPatterns[ii] = Pattern.compile (thePattern);
         }
         catch (PatternSyntaxException e)
         {
            log.err ("init", "PatternSyntaxException compiling expresion [" + thePattern + "]." + e);
            return false;
         }
         catch (Exception e)
         {
            log.severe ("init", "exception compiling expresion [" + thePattern + "]." + e);
            return false;
         }

         // search for field names
         for (int cc = 0; cc < patternMap.cols (ii) - 1; cc ++)
         {
            String fname = patternMap.getValue (ii, cc);

            if (fname.startsWith (":"))
            {
               if (fname.equalsIgnoreCase(":keep")) toBeKept = true;
               if (fname.equalsIgnoreCase(":opt")) isOptional = true;
               if (fname.equalsIgnoreCase(":optional")) isOptional = true;
               continue;
            }

            fieldType fi = new fieldType (fname, isOptional, toBeKept);


            //(o) TODO change this, since List::contains (object) does not detect equal objects (only for strings, int etc ?)
            //    anyway the error is detected by SQL "...duplicate column name: xxx"
            //
            if (campos.contains (fi))
            {
               log.err ("init", "duplicated field names (" + fi.name + ") are not allowed!");
               return false;
            }

            // add new field
            campos.add (fi);
            log.dbg (5, "init", "add field " + fi.name + " at index " + (campos.size ()-1));
         }

         offsetFields[ii] = offset;
         offset += campos.size ();
      }

      // build the array of fields and values from the list
      //
      currentRecord = new fieldType [campos.size ()];

      log.dbg (5, "init", "filedNames of size " + campos.size () + " created");

      for (int ii = 0; ii < currentRecord.length; ii ++)
      {
         currentRecord[ii] = (fieldType) campos.get (ii);
         log.dbg (5, "init", "filedNames [" + ii + "] = \"" + currentRecord[ii].name + "\"");
      }

      nextPattern = 0;

      log.dbg (5, "init", "init successed");
      ready = true;
      return true;
   }

   public fieldType [] getCurrentRecord()
   {
      if (!ready)
         if (!init ())
            return new fieldType[0];
      return currentRecord;
   }

   private void newRecord()
   {
      for (int ii = 0; ii < currentRecord.length; ii ++)
      {
         currentRecord[ii].oldValue = currentRecord[ii].value;
         if (! currentRecord[ii].keepValue)
            currentRecord[ii].value = null;
      }
   }

   public boolean recordComplete ()
   {
      return recordComplete;
   }

   /**
      returns 0 if no match found in the line
      if a match is found it returns the column index of the given lineStr
      were the scan should continue (if more matches per line are allowed)
   */
   public int scan(String lineStr)
   {
      if (!ready)
         if (!init ())
            return 0;

      //      <patternFieldMap>
      //         start,          //^METADATA:$
      //         area,           //^AREA=(.*)
      //         c1, c2, c3, c4, //^1-(.*) TABO 2-(.*) TABO 3-(.*) TABO (.*)

      int startPattern = nextPattern;
      int miroIndx = nextPattern;

      boolean changeRecord = false;
      int returnedColumnIndex = 0;

      recordComplete = false;

      log.dbg (7, "scan", "line [" + lineStr + "]");
      do
      {
         log.dbg (7, "scan", "check pattern Nr " + miroIndx);
         if (miroIndx >= arrPatterns.length)
         {
            if (startPattern == 0) break;
            miroIndx = 0;
            changeRecord = true;
         }

         Matcher matcher = null;
         try
         {
            matcher = arrPatterns[miroIndx].matcher(lineStr);
         }
         catch (Exception e)
         {
            log.err ("scan", "Exception calling matcher\n" + e);
            return 0;
         }

         miroIndx ++;

         if (matcher != null && matcher.find())
         {
            log.dbg (5, "scan", "matcher found at index [" + (miroIndx - 1) + "]");

            // pattern found! retrieve the fields of this pattern and end the loop
            nextPattern = miroIndx;
            if (changeRecord)
            {
               newRecord();
               log.dbg (5, "scan", "new record");
            }

            if (matcher.groupCount() == 0)
            {
               //(o) elxala_parsons Storing the whole line
               // Usually parsons store just fields that are matched in groups, in order to store
               // the whole line instead just define one field and place no group in the
               // parttern string. Here is this implemented

               // Condition for storing the whole line:
               // it matches (matcher.find()) but no group is found => it means that the patter contain no groups!
               // therefore the line must be the result (value of first field)

               log.dbg (5, "scan", "no groups founds, set whole line at index " + (miroIndx - 1));

               if ((miroIndx-1) >= 0 && (miroIndx-1) < currentRecord.length)
               {
                  currentRecord [miroIndx-1].value = lineStr;
               }
               else
               {
                  log.err ("scan", "currentRecord.lenght = " + currentRecord.length + " and cannot set result of matched line with no groups [" + lineStr + "]");
               }

               returnedColumnIndex = lineStr.length ();
            }
            else
            {
               machado (matcher, miroIndx - 1);

               //returnedColumnIndex = matcher.end (matcher.groupCount());
               returnedColumnIndex = matcher.end ();

               if (log.isDebugging (5))
                  log.dbg (5, "scan", "rest of line [" + lineStr.substring (returnedColumnIndex) + "]");
            }
            recordComplete = nextPattern >= arrPatterns.length;

            log.dbg (5, "scan", "return returnedColumnIndex = " + returnedColumnIndex);
            return returnedColumnIndex;
         }
      } while (miroIndx != startPattern);

      log.dbg (7, "scan", "return 0");
      return 0;
   }

   private void machado (Matcher matcher, int row)
   {
      int offset = offsetFields[row];

      log.dbg (7, "machado", "matcher.groupCount() = " + matcher.groupCount());

      for (int ii = 1; ii <= matcher.groupCount(); ii++)
      {
         if (offset + ii - 1 >= currentRecord.length)
         {
            log.err ("machado", "too few fields (" + currentRecord.length + ") respect pattern (" +  (offset +  matcher.groupCount()) + ")");
         }
         else
         {
            log.dbg (7, "machado", " index = " + ii + " row = " + row + " offset = " + offset +
                        " item (" + matcher.start(ii) + " to " + matcher.end(ii) + ")" +
                        " group (" + matcher.start() + " to " + matcher.end() + ")");
            currentRecord[offset + ii - 1].value = matcher.group(ii);
         }
      }
   }

   public static void main (String [] aa)
   {
      if (aa.length < 1)
      {
         System.out.println ("Syntax: evaFilename [-FfileToScan] [-T] [-Lxx]");
         return;
      }

      for (int oo = 0; oo < aa.length; oo ++)
      {
         if (!conTrace && aa[oo].equalsIgnoreCase ("-T")) conTrace = true;
         if (fileToScan == null && aa[oo].startsWith ("-F")) fileToScan = aa[oo].substring (2);
         if (limitRecords == 0 && aa[oo].startsWith ("-L")) limitRecords = stdlib.atoi (aa[oo].substring (2));
      }


      EvaUnit eu = EvaFile.loadEvaUnit (aa[0], "scanTest");
      if (eu == null)
      {
         System.out.println ("ERROR: EvaUnit #scanTest# not found in " + aa[0] + " (or file not found)!");
         return;
      }

      Eva fpMap = eu.getEva ("fieldPatterMap");
      if (fpMap == null)
      {
         System.out.println ("ERROR: Eva <fieldPatterMap> not found in unit #scanTest# of " + aa[0]);
         return;
      }

      TextFile fix = null;
      Eva eData = null;

      if (fileToScan != null)
      {
         System.out.println ("working with file" + fileToScan);
         fix = new TextFile ();
         if (!fix.fopen (fileToScan, "r"))
         {
            System.out.println ("ERROR: File to scan not found! [" + fileToScan + "]");
            return;
         }
      }
      else
      {
         System.out.println ("working with sample data");
         eData = eu.getEva ("testData");
         if (eData == null)
         {
            System.out.println ("ERROR: Eva <testData> not found in unit #scanTest# of " + aa[0]);
            return;
         }
      }

      // ... ok, we have all!
      //

      aLineParsons sr = new aLineParsons(fpMap);
      if (conTrace)
      {
         System.out.println ("NOTE: At the moment trace is only possible with gastona sessionLog directory!");
      }

      if (eData != null)
      {
         for (int ii = 0; ii < eData.rows (); ii ++)
         {
            String lin = eData.getValue (ii, 0);
            sr.scan (lin);
            System.out.println ("scan " + ii + " dones");
            if (!fala (sr)) break;
         }
      }
      else
      {
         while (fix.readLine ())
         {
            sr.scan (fix.TheLine ());
            if (!fala (sr)) break;
         }
         fix.fclose ();
      }
   }

   private static boolean conTrace   = false;
   private static String  fileToScan = null;
   private static int     limitRecords = 0;
   private static int     Nrecords = 0;

   private static boolean fala (aLineParsons sr)
   {
      if (sr.recordComplete ())
      {
         Nrecords ++;
         System.out.println ("new record!");
         for (int jj = 0; jj < sr.currentRecord.length; jj ++)
         {
            System.out.println ("   " + sr.currentRecord[jj].name + " = [" + sr.currentRecord[jj].value + "]");
         }
      }
      return limitRecords == 0 || Nrecords < limitRecords;
   }
}