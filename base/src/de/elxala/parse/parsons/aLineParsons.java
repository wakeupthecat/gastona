/*
package de.elxala
(c) Copyright 2005-2026 Alejandro Xalabarder Aulet

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


   2024-08-29 20:11 Last change
   2008-03-20 17:51 Creation
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
   @date   2008-2024

   Class to facilitate parsing lines.
   This class is the kern of the Listix command PARSONS (used directly in listix/cmds/cmdParsons.java)
   See documentation of the listix command in welcomeGastona.
   Provides also a command line call (see main)

   2008-03-20 17:51 Creation

*/
public class aLineParsons
{
    private parsonsColumn [] currentRecord = new parsonsColumn [0];
    private parsonsColumn [] currentOptRecord = new parsonsColumn [0];

    //
    //  Note: Pattern array + offset fields are candidate to be sub-classed unfortunatelly (with the java version I do compile) we would need a separate file
    //        (sub-classing the same as sub-functioning was an important language feature removed from Pascal in the very firt C design for no reason)
    //

    private int [] offsetFields = new int [0];               // on each pattern starts a field, this is the index of this field
    private Pattern [] arrPatterns = new Pattern [0];        // array of patterns to check for each line

    private int [] offsetOptColFields = new int [0];         // on each pattern starts a field, this is the index of this field
    private Pattern [] arrOptColPatterns = new Pattern [0];  // array of patterns for optional columns

    private Pattern [] arrAntiPatterns = new Pattern [0];    // array of patterns to check for each line

    private int nextPattern = -1;           // current ordinary pattern to start with while scanning a line
    private boolean recordComplete = false;
    private boolean ready = false;

    private logger log = new logger (this, "de.elxala.parse.parsons.aLineParsons", null);

    public Eva regularColumnPatternMap = new Eva ();      // original description of the ordinary pattern-Field mapping

    public aLineParsons ()
    {
    }

    public aLineParsons (Eva thePatternMap)
    {
        setRegularPatternList (thePatternMap);
    }

    public aLineParsons (Eva thePatternMap, Eva optionalPatternMap)
    {
        setRegularPatternList (thePatternMap);
        setOptionalPatternList (optionalPatternMap);
    }

    public aLineParsons (Eva thePatternMap, Eva optionalPatternMap, Eva antiPatternMap)
    {
        setRegularPatternList (thePatternMap);
        setOptionalPatternList (optionalPatternMap);
        setAntiPatternList (antiPatternMap);
    }

    public boolean isValid ()
    {
        // some null value indicates errors in last compilation
        //
        return arrPatterns != null && arrAntiPatterns != null && arrOptColPatterns != null;
    }

    public boolean setRegularPatternList (Eva patternMap)
    {
        //NOTE: <offsetFields> is also set but we only set to null <arrPatterns> for traking purposes
        arrPatterns = compileRegularPatternList (patternMap);
        return arrPatterns != null;
    }

    public boolean setAntiPatternList (Eva patternMap)
    {
        arrAntiPatterns = compileAntiPatternList (patternMap);
        return arrAntiPatterns != null;
    }

    public boolean setOptionalPatternList (Eva patternMap)
    {
        //NOTE: <offsetOptColFields> is also set but we only set to null <arrOptColPatterns> for traking purposes
        arrOptColPatterns = compileOptionalPatternList (patternMap);
        return arrOptColPatterns != null;
    }

    protected Pattern []  compileRegularPatternList (Eva regularColumnPatternMap)
    {
        //  patternMap example
        //
        //    <patternFieldMap>
        //         recordId, date, //record: (.*) date: (.*)
        //         clientid, // client: (.*)
        //
        log.dbg (5, "init", "setRegularPatternList [" + regularColumnPatternMap + "]");


        // --- CREATE AND COMPILE REGULAR PATTERNS
        //
        offsetFields = new int [regularColumnPatternMap.rows ()];
        Pattern [] pattArr  = new Pattern [regularColumnPatternMap.rows ()];
        List stdCamps = new Vector();
        int offsetIndx = 0;

        for (int ii = 0; ii < regularColumnPatternMap.rows (); ii ++)
        {
            if (regularColumnPatternMap.cols (ii) < 2)
            {
                log.err ("init", "no pattern found at row " + ii + " (the rowNS has only one column)");
                return null;
            }

            int nchamps = regularColumnPatternMap.cols (ii) - 1;
            String thePattern = regularColumnPatternMap.getValue (ii, nchamps); // nchamps is also the pattern index

            offsetFields[ii] = offsetIndx;
            offsetIndx += nchamps;

            try
            {
                pattArr[ii] = Pattern.compile (thePattern);
            }
            catch (PatternSyntaxException e)
            {
                log.err ("init", "PatternSyntaxException compiling expresion [" + thePattern + "]." + e);
                return null;
            }
            catch (Exception e)
            {
                log.severe ("init", "exception compiling expresion [" + thePattern + "]." + e);
                return null;
            }

            // search for field names
            for (int cc = 0; cc < nchamps; cc ++)
            {
                String fname = regularColumnPatternMap.getValue (ii, cc);
                if (fname.length () == 0) continue;

                parsonsColumn fi = new parsonsColumn (fname);

                if (stdCamps.contains (fi))
                {
                    log.err ("init", "duplicated field names (" + fi.getName () + ") are not allowed!");
                    return null;
                }

                // add new field
                stdCamps.add (fi);
                log.dbg (5, "init", "add field " + fi.getName () + " at index " + (stdCamps.size ()-1));
            }
        }

        // build the array of fields and values from the list
        //
        currentRecord = new parsonsColumn [stdCamps.size ()];
        log.dbg (5, "init", "filedNames of size " + stdCamps.size () + " created");

        for (int ii = 0; ii < currentRecord.length; ii ++)
        {
            currentRecord[ii] = (parsonsColumn) stdCamps.get (ii);
            log.dbg (5, "init", "filedNames [" + ii + "] = \"" + currentRecord[ii].getName () + "\"");
        }
        nextPattern = 0;

        return pattArr;
    }

    protected Pattern [] compileAntiPatternList (Eva antiPatternList)
    {
        // --- CREATE AND COMPILE ANTI PATTERNS
        //
        Pattern [] pattArrr = new Pattern [antiPatternList.rows ()];
        for (int ii = 0; ii < antiPatternList.rows (); ii ++)
        {
            String thePattern = antiPatternList.getValue (ii, 0);

            try
            {
                pattArrr[ii] = Pattern.compile (thePattern);
            }
            catch (PatternSyntaxException e)
            {
                log.err ("init", "PatternSyntaxException compiling expresion [" + thePattern + "]." + e);
                return null;
            }
            catch (Exception e)
            {
                log.severe ("init", "exception compiling expresion [" + thePattern + "]." + e);
                return null;
            }
        }
        return pattArrr;
    }

    protected Pattern [] compileOptionalPatternList (Eva optionalColumnPatterMap)
    {
        // --- CREATE AND COMPILE OPTIONAL COLUMN PATTERNS
        //
        offsetOptColFields = new int [optionalColumnPatterMap.rows ()];
        Pattern [] pattArrr  = new Pattern [optionalColumnPatterMap.rows ()];
        List optCamps = new Vector();
        int offsetIndx = 0;

        for (int ii = 0; ii < optionalColumnPatterMap.rows (); ii ++)
        {
            if (optionalColumnPatterMap.cols (ii) < 2)
            {
                log.err ("init", "no pattern found at row " + ii + " (the rowNS has only one column)");
                return null;
            }

            int nchamps = optionalColumnPatterMap.cols (ii) - 1;
            String thePattern = optionalColumnPatterMap.getValue (ii, nchamps); // nchamps is also the pattern index

            offsetOptColFields[ii] = offsetIndx;
            offsetIndx += nchamps;

            try
            {
                pattArrr[ii] = Pattern.compile (thePattern);
            }
            catch (PatternSyntaxException e)
            {
                log.err ("init", "PatternSyntaxException compiling expresion [" + thePattern + "]." + e);
                return null;
            }
            catch (Exception e)
            {
                log.severe ("init", "exception compiling expresion [" + thePattern + "]." + e);
                return null;
            }

            // search for field names
            for (int cc = 0; cc < nchamps; cc ++)
            {
                String fname = optionalColumnPatterMap.getValue (ii, cc);
                if (fname.length () == 0) continue;

                parsonsColumn fi = new parsonsColumn (fname);

                if (optCamps.contains (fi))
                {
                    log.err ("init", "duplicated field names (" + fi.getName () + ") are not allowed!");
                    return null;
                }

                // add new field
                optCamps.add (fi);
                log.dbg (5, "init", "add field " + fi.getName () + " at index " + (optCamps.size ()-1));
            }
        }

        // build the array of fields and values from the list
        //
        currentOptRecord = new parsonsColumn [optCamps.size ()];
        log.dbg (5, "init", "filedNames of size " + optCamps.size () + " created");

        for (int ii = 0; ii < currentOptRecord.length; ii ++)
        {
            currentOptRecord[ii] = (parsonsColumn) optCamps.get (ii);
            log.dbg (5, "init", "filedNames [" + ii + "] = \"" + currentOptRecord[ii].getName () + "\"");
        }
        return pattArrr;
    }

   public Pattern [] getRegularPatterns ()
   {
      return arrPatterns;
   }

   public Pattern [] getOptionalPatterns ()
   {
      return arrOptColPatterns;
   }

   public Pattern [] getAntiPatterns ()
   {
      return arrAntiPatterns;
   }

   public boolean hasPatterns ()
   {
      return arrPatterns != null && arrPatterns.length > 0;
   }

   public boolean hasAntiPatterns ()
   {
      return arrAntiPatterns != null && arrAntiPatterns.length > 0;
   }

   public boolean ignoreLine (String lineStr)
   {
      for (int pp = 0; pp < arrAntiPatterns.length; pp ++)
      {
         Matcher matcher = null;
         try
         {
            matcher = arrAntiPatterns[pp].matcher(lineStr);
         }
         catch (Exception e)
         {
            log.err ("ignoreLine", "Exception calling matcher\n" + e);
            return false; // conservative
         }
         if (matcher != null && matcher.find())
         {
            log.dbg (5, "ignoreLine", "ignoring line [" + lineStr + "]");
            return true;
         }
      }
      return false;
   }

   public parsonsColumn [] getCurrentRecord ()
   {
      return currentRecord;
   }

   public parsonsColumn [] getCurrentOptionalColumnsRecord ()
   {
      return currentOptRecord;
   }

   private void newRecord()
   {
      resetRecord ();
   }

   /** reset the current parsed record, all values collected until now will be discarded
   */
   public void resetRecord ()
   {
      recordComplete = false;
      nextPattern = 0;
      for (int ii = 0; ii < currentRecord.length; ii ++)
      {
         currentRecord[ii].setValue (null);
      }
      for (int ii = 0; ii < currentOptRecord.length; ii ++)
      {
         currentOptRecord[ii].setValue ("");
      }
   }

   public boolean recordComplete ()
   {
      return recordComplete;
   }

   public void recordConsumed ()
   {
      newRecord();
   }


   /**
      returns 0 if no match found in the line
      if a match is found it returns the column index of the given lineStr
      were the scan should continue (if more matches per line are allowed)
   */
   public int scan(String lineStr)
   {
      //--- BAD IDEA --- implement instead mark pattern which match things without capturing anything
      //   // we clear the record mainly to avoid trailing optional values
      //   //
      //   // since we are based on regular columns to close the record (last column matched => new record)
      //   // it is not possible to handle optional columns either before the first regular column or
      //   // after the last one. Also the fact that optionals do not consume anything of the line
      //   // makes it difficult to handle the issue.
      //   //
      //   if (nextPattern == 0)
      //      resetRecord ();

      scanForOptionalColumns (lineStr);
      return scanForOrderedColumns (lineStr);
   }

   protected void scanForOptionalColumns (String lineStr)
   {
      String INFO = "scanForOptionalColumns";

      if (arrOptColPatterns.length == 0)
      {
         log.dbg (7, INFO, "line [" + lineStr + "] check with no optional column patterns");
         return;
      }

      for (int oo = 0; oo < arrOptColPatterns.length; oo ++)
      {
         int optIndx = offsetOptColFields[oo];
         log.dbg (7, INFO, "line [" + lineStr + "] check optional column patterns [" + oo + "]");

         Matcher matcher = null;
         try
         {
            matcher = arrOptColPatterns[oo].matcher(lineStr);
         }
         catch (Exception e)
         {
            log.err (INFO, "Exception calling matcher\n" + e);
            return;
         }
         if (matcher == null)
         {
            log.err (INFO, "Problems calling matcher\n");
            return;
         }

         if (matcher.find())
         {
            log.dbg (5, INFO, "matcher found optcol index [" + oo + "]");

            if (matcher.groupCount() == 0)
            {
               log.dbg (5, INFO, "no groups found, set whole line at index " + oo);
               currentOptRecord [optIndx].setValue (lineStr);
               log.dbg (9, INFO, "currentOptRecord [" + optIndx + "] = \"" + currentOptRecord[optIndx].getName ()+ "\" now is (" + currentOptRecord[optIndx].getValue () + ")");
            }
            else
            {
               for (int ii = 1; ii <= matcher.groupCount(); ii++)
               {
                  if (optIndx + ii - 1 >= currentOptRecord.length)
                  {
                     log.err (INFO, "too few fields (" + currentRecord.length + ") respect pattern (" +  (optIndx +  matcher.groupCount()) + ")");
                  }
                  else
                  {
                     log.dbg (7, INFO, " index = " + optIndx + " item (" + matcher.start(ii) + " to " + matcher.end(ii) + ")" );
                     currentOptRecord[optIndx + ii - 1].setValue (matcher.group(ii));
                     log.dbg (9, INFO, "currentOptRecord [" + (optIndx + ii -1) + "] = \"" + currentOptRecord[(optIndx + ii -1)].getName () + "\" now is (" + currentOptRecord[(optIndx + ii -1)].getValue () + ")");
                  }
               }
            }
         }
      }
   }

   protected int scanForOrderedColumns (String lineStr)
   {
      if (arrPatterns.length == 0)
      {
         log.dbg (7, "scan", "line [" + lineStr + "] check with no patterns, return " + lineStr.length ());
         return lineStr.length (); // nothing to
      }

      if (nextPattern >= arrPatterns.length)
      {
         newRecord();
         log.dbg (5, "scan", "new record");
      }
      //      <patternFieldMap>
      //         start,          //^METADATA:$
      //         area,           //^AREA=(.*)
      //         c1, c2, c3, c4, //^1-(.*) TABO 2-(.*) TABO 3-(.*) TABO (.*)

      int miroIndx = nextPattern;
      int returnedColumnIndex = 0;
      recordComplete = false;

      log.dbg (7, "scan", "line [" + lineStr + "] check pattern Nr " + miroIndx);

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
      if (matcher == null)
      {
         log.err ("scan", "Problems calling matcher\n");
         return 0;
      }

      //NOTE: it has to used "find" and not "match" since the pattern can match at index biger than 0
      if (matcher.find())
      {
         log.dbg (5, "scan", "matcher found at index [" + miroIndx + "]");

         // pattern found! retrieve the fields of this pattern and end the loop
         nextPattern = miroIndx + 1;
         if (matcher.groupCount() == 0)
         {
            //(o) elxala_parsons Storing the whole line
            // Usually parsons store just fields that are matched in groups, in order to store
            // the whole line instead just define one field and place no group in the
            // parttern string. Here is this implemented

            // Condition for storing the whole line:
            // it matches (matcher.find()) but no group is found => it means that the patter contain no groups!
            // therefore the line must be the result (value of first field)

            log.dbg (5, "scan", "no groups found, set whole line at index " + miroIndx);

            if (miroIndx >= 0 && miroIndx < currentRecord.length)
            {
               currentRecord [miroIndx].setValue (lineStr);
            }
            else
            {
               log.err ("scan", "currentRecord.lenght = " + currentRecord.length + " and cannot set result of matched line with no groups [" + lineStr + "]");
            }

            returnedColumnIndex = lineStr.length ();
         }
         else
         {
            machado (matcher, miroIndx);

            //returnedColumnIndex = matcher.end (matcher.groupCount());
            returnedColumnIndex = matcher.end ();

            if (log.isDebugging (5))
               log.dbg (5, "scan", "rest of line [" + lineStr.substring (returnedColumnIndex) + "]");
         }
         recordComplete = nextPattern >= arrPatterns.length;

         log.dbg (5, "scan", "return returnedColumnIndex = " + returnedColumnIndex);
         return returnedColumnIndex;
      }

      log.dbg (7, "scan", "return 0");
      return 0;
   }

   private void machado (Matcher matcher, int row)
   {
      int offset = offsetFields[row];

      log.dbg (7, "machado", "matcher.groupCount() = " + matcher.groupCount() + " start " +  matcher.start() +  " end " + matcher.end());

      for (int ii = 1; ii <= matcher.groupCount(); ii++)
      {
         if (offset + ii - 1 >= currentRecord.length)
         {
            log.err ("machado", "too few fields (" + currentRecord.length + ") respect pattern (" +  (offset +  matcher.groupCount()) + ")");
         }
         else
         {
            int sta = matcher.start(ii);
            int end = matcher.end(ii);
            String grapo = matcher.group(ii);
            log.dbg (7, "machado", " index = " + ii + " row = " + row + " offset = " + offset +
                        " item (" + matcher.start(ii) + " to " + matcher.end(ii) + ")" );
            currentRecord[offset + ii - 1].setValue (matcher.group(ii));

            // NOTE: See documentation of Matcher.start (int)
            //       return value : The index of the first character captured by the group, or -1 if the match was successful but "The index of the first character captured by the group, or -1 if the match was successful but the group itself did not match anything
            //       ? "the group itself did not match anything" ?
            //       parece que no matcha!
            //if (matcher.start(ii) == -1)
            //{
            //   log.err ("machado", "pattern matches but group with no containt");
            //}

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

      Eva fpOptMap = eu.getEva ("optionalColPatterMap");
      if (fpOptMap == null)
      {
         System.out.println ("ERROR: Eva <optionalColPatterMap> not found in unit #scanTest# of " + aa[0]);
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

      aLineParsons sr = new aLineParsons (fpMap, fpOptMap);

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
            System.out.println ("   " + sr.currentRecord[jj].getName () + " = [" + sr.currentRecord[jj].getValue () + "]");
         }
         for (int jj = 0; jj < sr.currentOptRecord.length; jj ++)
         {
            System.out.println ("   OPT: " + sr.currentOptRecord[jj].getName () + " = [" + sr.currentOptRecord[jj].getValue () + "]");
         }
      }
      return limitRecords == 0 || Nrecords < limitRecords;
   }
}