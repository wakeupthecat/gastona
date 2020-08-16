
// "use strict";

//BROWSER  @ < :infile F:/PAPERS/DEVELOPER/11043_13211_Cantabriales/goFilePlacebo.js>

// converts a csv file into a javascript object
// where the the first line is the column names
//
// the input parameter is either a file given as string array or as string containing the whole text
// including line ends.
//
// NOTE: The listix command CSV uses the same algorithm but since it is implemented in java
//       is much faster, so if possible use that listix command instead.
//
function empassaCSV (csvFileName, sal)
{
   var QUOT = '\"';
   var sepaCh = ",";
   var headColNames = [];
   var nIncidence = 0;
   var fo;

   return {
      csv2SqliteScript  : csv2SqliteScript,
      csvStrOrArr2obj   : csvStrOrArr2obj,
      csvStrUTF82obj    : csvStrUTF82obj,
   };

   function outIncidence (linenr, desc)
   {
      nIncidence ++;
      if (nIncidence == 100)
         fo.writeLine ("INSERT INTO atauCSVIncidences VALUES (" + nIncidence + ", " + linenr + ", 'too much incidences, stop storing them!');");
      if (nIncidence < 100)
         fo.writeLine ("INSERT INTO atauCSVIncidences VALUES (" + nIncidence + ", " + linenr + ", '" + desc + "');");
   }

   // trim also for IE8
   function trimStr (str) { return str.replace(/^\s+|\s+$/g, ''); }

   function str2lineArray (str) { return str.replace(/\r\n/g, "\r").replace(/\n/g, "\r").split(/\r/); }

   function escapeStr (str)
   {

      listix.getSomeHowVarEva("mytempvar").setValue (str, 0, 0);
      return listix.solveLsxFormatAsEva (":encode mytempvar").getValue (0,0);
/*
*/
      //BROWSER   return str;
   }

   function csvStrUTF82obj (fileStrUtf8)
   {
      return csvStrOrArr2obj (decodeURIComponent (fileStrUtf8.replace (/\+/g, "%20")));
   }

   function csvStrOrArr2obj (filetext)
   {
      var currObj = [];

      // get array of lines
      //
      var textArr = filetext;
      if (typeof filetext == "string")
      {
         textArr = str2lineArray (filetext);
      }

      for (var lindx = 0; lindx < textArr.length; lindx ++)
      {
         var linStr = trimStr (textArr[lindx]);

         if (linStr)
         {
            var lineArr = parseCsvLine (linStr);
            if (lineArr)
               currObj.push (lineArr);
         }
      }
      return currObj;
   }

   function parseCsvLine (str, doescape)
   {
      var eline = [];
      var FI = str.length;
      var pi = 0;

      do
      {
         var cell = "";

         // trim
         while (pi < FI && (str[pi] == ' ' || str[pi] == '\t')) pi++;
         if (pi >= FI) break;

         var envolta = str[pi] == '\"';
         var ini = envolta ? ++pi: pi;
         do
         {
             if (envolta)
             {
                if (str[pi] != '\"') pi ++;
                else
                    if (pi+1 < FI && str[pi+1] == '\"')
                    {
                      // double ""
                      // add a part including one " and continue
                      cell += (pi+1 > ini ? str.substring (ini, pi+1): "");
                      pi += 2;
                      ini = pi;
                    }
                    else break; // close "
             }
             else
             {
                 if (str[pi] == sepaCh) break;
                 pi ++;
             }
         } while (pi < FI);

         var fi2 = pi;

         // right trim if not quoted
         //
         if (! envolta)
           while (fi2 > ini && (str[fi2-1] == ' ' || str[fi2-1] == '\t')) fi2 --;

         if (fi2 > ini)
           cell += str.substring (ini, fi2);

         pi ++;

         if (envolta)
         {
            // find the next comma if any
            while (pi < FI && str[pi] != sepaCh) pi++;
            pi ++;
         }

         // add one cell
         //
         eline.push (doescape ? escapeStr (cell): cell);
      } while (pi < FI);

      // allow finishing with empty string. Example:
      //    name, tel
      //    Justine, 991
      //    Lalia,
      //
      if (headColNames.size () > 0 &&
          (eline.size () + 1) == headColNames.size () &&
          str[FI-1] == sepaCh)
         eline.push ("");

      return eline;
   }

   function processHeader (heastr)
   {
      // choose separator , ; or tab
      //
      function countCh (str, ch)
      {
         var cnt = 0;
         for(var ii = 0; ii < str.length; ii ++)
            if (str[ii] == ch) cnt ++;
         return cnt;
      }

      // a column name in a header should not contain ,;tab or similar
      var c1 = countCh (heastr, ",");
      var c2 = countCh (heastr, ";");
      var c3  = countCh (heastr, "\t");
      sepaCh = "\t";

      if (c1 >= c2 && c1 >= c3) sepaCh = ",";
      else if (c2 >= c1 && c2 >= c3) sepaCh = ";";

      var brutos = parseCsvLine (heastr, false);
      var heacnt = 1;

      // force all unique column names
      //
      headColNames = [];
      for (var ii in brutos)
      {
         var nam = trimStr(brutos[ii]);

         // it cannot be accepted an empty column name
         // also in order to filter last false header if line ends with separator
         if (nam.length == 0) break;

         nam = nam.replace (/[^a-zA-Z0-9]/g, "");
         nam = nam + ((nam.length == 0 || headColNames.indexOf (nam) != -1) ? ("_" + (heacnt++)): "");
         headColNames.push ("c_" + nam);
      }

      // fo.writeLine ("# from header [" + heastr + "]");
      fo.writeLine ("CREATE TABLE IF NOT EXISTS atauCSVCells (" + headColNames.join (", ") + ");");
      fo.writeLine ("CREATE TABLE IF NOT EXISTS atauCSVIncidences (incidenceNr, lineNr, desc);");
   }

   function csv2SqliteScript (fileName, outSqlfile)
   {
      headColNames = [];
      nIncidence = 0;
      fo = new goFile ();

      var fix = new goFile ();
      var leos;
      if (fix.fopen (fileName, "r"))
      {
         if (fo.fopen (outSqlfile, "w"))
         {
            fo.writeLine ("BEGIN;");

            leos = fix.readLine ();
            var linenr = 2;
            if (leos != null) processHeader (leos);
            // --- while ((leos = fix.readLine ()) != null && linenr < 10)
            while ((leos = fix.readLine ()) != null)
            {
               var lineValues = parseCsvLine (leos, true);
               if (lineValues.length == headColNames.length)
                  fo.writeLine ("INSERT INTO atauCSVCells VALUES ('" + lineValues.join ("', '") + "');");
               else
                  outIncidence (linenr, lineValues.length + " cells generated but " + headColNames.length + " are required");

               linenr ++;
            }
            fo.writeLine ("COMMIT;");
         }
         if (nIncidence > 0)
            fo.writeLine ("INSERT INTO atauCSVIncidences VALUES (" + nIncidence + ", " + linenr + ", 'final count of incidences " + nIncidence + "');");
         fo.fclose ();
         fix.fclose ();
      }
   }
}
