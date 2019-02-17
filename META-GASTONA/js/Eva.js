/*
Copyright (C) 2015,2016,2017 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

/**
   @author Alejandro Xalabarder
   @date   2015.05.21

   @file   Eva.js

   @desc
      Conversion from string to eva file object plus methods to convert again into string
      the whole object, a single unit or a single eva.

      Note : EVA (Estructura Variable de Archivos) is an open and free format

   ---------- Example of use:

   // TEST EVA

   wes = "#unit primera#                " + "\n" +
         "                              " + "\n" +
         "  <info> first eva, etc, etc  " + "\n" +
         "      second, line, and more  " + "\n" +
         "                              " + "\n" +
         "  <milist> id, name           " + "\n" +
         "    5612, Goettingen          " + "\n" +
         "    1742, Plasencia           " + "\n" +
         "    09211, Ordunya            " + "\n" +
         "                              " + "\n" +
         "#ultima#                      " + "\n" +
         "   no en eva, no cuenta       " + "\n" +
         " <sano>                       " + "\n" +
         "   bla, blah                  " + "\n" +
         "   blax, 77, blah             " + "\n" +
         "" ;

   var comol = evaFileObj (wes);
   console.log (comol);
   console.log ("Gauss lived in " + comol.obj["unit primera"]["milist"][1][1]);


  jEva v0.80

      2017.04.17  parser compatible with other implementations
                  implement correctly rest of line in all columns
                  implement double quotes in all columns
                  implement comment <!
                  implement logic end of file #**...#

*/

"use strict";

function evaFileObj (obj)
{
   // parse eva file if the object is not given directly
   //
   if (typeof obj === "string")
      obj = evaFileStr2obj (obj);

   return {
      obj             : obj,
      toText          : function () { return evaFileObj2Text (obj); },
      toString        : function () { return evaFileObj2Text (obj); },
      evaFileObj2Text : evaFileObj2Text,
      evaUnitObj2Text : evaUnitObj2Text,
      evaObj2Text     : evaObj2Text
   };

   function evaFileObj2Text (evafileObj)
   {
      var str = [];
      for (var uni in evafileObj)
      {
         str.push ("#" + uni + "#");
         str.push ("");
         str.push (evaUnitObj2Text (evafileObj[uni]));
         str.push ("");
      }
      return str.join ("\n");
   }

   function evaUnitObj2Text (obj)
   {
      var str = [];
      for (var eva in obj)
      {
         str.push ("");
         str.push ("   <" + eva + ">");
         str.push ("");
         str.push (evaObj2Text (obj[eva]));
      }
      return str.join ("\n");
   }

   function evaObj2Text (obj)
   {
      var str = [], lin = "";
      for (var row in obj)
      {
         lin = "      ";
         for (var col in obj[row])
         {
            if (col > 0)
               lin += ", ";
            if (col == obj[row].length - 1)
               lin += "//";
            lin += obj[row][col];
         }

         str.push (lin);
      }
      return str.join ("\n");
   }
}

function evaFile (sam)
{
   alert ('evaFile function name is deprecated! please use evaFileStr2obj instead');
   evaFileStr2obj (sam);
}

function evaFileUTF82obj (fileStrUtf8)
{
   return evaFileStr2obj (decodeURIComponent (fileStrUtf8.replace (/\+/g, "%20")));
}

// converts a file formated as EVA into a javascript object
// where the the first layer of properties are units, the second evas and each eva
// is an array of string arrays
//
// the input parameter is either a file given as string array or as string containing the whole text
// including line ends.
//
function evaFileStr2obj (evaFileAsArrOrStr)
{
   // trim also for IE8
   function trimStr (str) { return str.replace(/^\s+|\s+$/g, ''); }

   function str2lineArray (str) { return str.replace(/\r\n/g, "\r").replace(/\n/g, "\r").split(/\r/); }

   return parseFileStr (evaFileAsArrOrStr);

   /*
      Example of use:
      var sal = isname ("< isThis aName?> yes it is   ", "<", ">")
      out ("name = /" + sal.name + "/");
      out ("rest = /" + sal.rest + "/");
      // name = / isThis aName?/
      // rest = /yes it is/
   */
   function isname (line, charStart, charEnd)
   {
      if (line.charAt (0) !== charStart) return;
      var indx = line.indexOf (charEnd, 1);
      if (indx > 0)
         return {
            name: line.substr (1, indx-1),      // do not trim names
            rest: trimStr (line.substr (indx+1))
         }
   }

   /*
      Example of use:
      out (parseEvaLine ("    one    , two,     three ... etc   "));
      // => [ "one", "two", "three ... etc" ]
   */
   function parseEvaLine (str)
   {
       var eline = [];
       var FIN = str.length;
       var pi = 0;

       function fin() { return pi >= str.length; }
       function getCell ()
       {
          var cell = "";

          // trim
          while (pi < str.length && (str.charAt(pi) == ' ' || str.charAt(pi) == '\t')) pi++;
          if (fin ()) return null;

          if (str.charAt(pi) == '\'')
          {
             cell = str.substring (pi+1);
             pi = FIN;
             return cell;
          }

          // si // return str.substring (pos+2);
          //
          if (str.charAt(pi) == '/' && str.charAt(pi+1) == '/')
          {
             cell = str.substring (pi+2);
             pi = FIN;
             return cell;
          }

          var envolta = str.charAt(pi) == '\"';
          var ini = envolta ? ++pi: pi;
          do
          {
              if (envolta)
              {
                 if (str.charAt(pi) != '\"') pi ++;
                 else
                     if (str.charAt(pi+1) == '\"')
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
                  if (str.charAt (pi) == ',') break;
                  pi ++;
              }
          } while (! fin ());

          var fi2 = pi;

          // right trim if not quoted
          //
          if (! envolta)
            while (fi2 > ini && (str.charAt(fi2-1) === ' ' || str.charAt(fi2-1) === '\t')) fi2 --;

          if (fi2 > ini)
            cell += str.substring (ini, fi2);

          pi ++;

          if (envolta) {
             // find the next comma if any
             while (! fin () && str.charAt(pi) != ',') pi++;
             pi ++;
          }

          return cell;
       }

       do {
            var cel = getCell ();
            if (typeof cel === "string")
               eline.push (cel);
       } while (! fin ());

       return eline;
   }

   function parseFileStr (filetext)
   {
      var nameUnit, nameEva, novoName, linStr, textArr, lineArr, lindx;
      var currFile = {}, currUnit = {}, currEva = [];

      function setCurrent (finishUnit)
      {
         if (nameEva)
            currUnit[nameEva] = currEva .length > 0 ? currEva: [[""]]; // all evas has at least one element!
         nameEva = undefined;
         currEva = [];

         if (finishUnit)
         {
            if (nameUnit)
               currFile[nameUnit] = currUnit;
            nameUnit = undefined;
            currUnit = [];
         }
      }

      // get array of lines
      //
      var textArr = filetext;
      if (typeof filetext === "string")
      {
         textArr = str2lineArray (filetext);
      }

      for (lindx in textArr)
      {
         linStr = trimStr (textArr[lindx]);

         // check if comment, then ignore line
         if (linStr.charAt (0) === '<' && linStr.charAt (1) === '!') continue;

         // check if start of unit
         //
         novoName = isname (linStr, "#", "#");
         if (novoName)
         {
            if (novoName.name && novoName.name.charAt(0) === '*' && novoName.name.charAt(1) == '*') break; // logic end of file
            setCurrent (true);
            nameUnit = novoName.name;
            // console.log ("ignoring " + novoName.rest);
            continue;
         }

         // check if start of eva
         //
         novoName = isname (linStr, "<", ">");
         if (novoName)
         {
            setCurrent (false);
            nameEva = novoName.name;
            linStr = novoName.rest;
            if (linStr.length == 0) continue;
         }

         // line is not empty
         // and we are within a unit and an eva
         //
         if (linStr && nameUnit && nameEva)
         {
            lineArr = parseEvaLine (linStr);
            if (lineArr)
               currEva.push (lineArr);
         }
         else
         {
            // console.log ("ignoring " + linStr);
         }
      }
      setCurrent (true);
      return currFile;
   }

   //
   // parseSingleEva ("  one , two, three ... etc \n 1002, theEnd");
   //
   // => [ [ "one", "two", "three ... etc" ], [ "1002", "theEnd" ] ]
   //
   function parseSingleEva (textStr)
   {
      var eva = [], lines, indx, linArr;
      lines = str2lineArray (textStr);
      for (indx in lines)
      {
         linArr = eva_parseEvaLine (lines[indx]);
         if (linArr.length == 0) continue; //ignore blank lines
         eva.push(linArr);
      }

      return eva;
   }
}
