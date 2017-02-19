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


  jsEva v0.20

  differences respect eva parser in C++ and java

   - "rest of line" (chat ' or //) is only implemented  for the first column!

         for example

            first, second, //third, etc

         should be parsed as [ "first", "second", "third, etc"]     (3 elem)
         but results in      [ "first", "second", "//third", "etc"] (4 elem)


   - double quotation is not handled! the rows are splited simply by comma (,)

         for example

            first, "second ""and"" last"

         should be parsed as [ 'first', 'second "and" last']     (2 elem)
         but results in      [ 'first', '"second ""and"" last"'] (1 elem)

   - final cells are processed with the function decodeURIComponent ()
     this allow the inclusion of any unicode character, returns (%0A) and commas (%2C)!

       this is actually an advantage regards the C++ and java parsers

  - to allow content with commas, either use "rest of the line" (//) if the row has only
     one column or use %2C

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
      var str = "", uni;
      for (uni in evafileObj)
      {
         str += "\n#" + uni + "#\n";
         str += evaUnitObj2Text (evafileObj[uni]);
      }
      return str;
   }

   function evaUnitObj2Text (obj)
   {
      var str = "", eva;
      for (eva in obj)
      {
         str += "\n   <" + eva + ">\n";
         str += evaObj2Text (obj[eva]);
      }
      return str;
   }

   function evaObj2Text (obj)
   {
      var str = "", lin;
      for (var row in obj)
      {
         str += "      ";
         for (var col in obj[row])
         {
            if (col > 0)
               str += ", ";
            if (col == obj[row].length - 1)
               str += "//";
            str += obj[row][col];
         }
         str += "\n";
      }
      return str;
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

function evaFileStr2obj (fileStr)
{
   // trim for IE8
   if (typeof String.prototype.trim !== 'function') {
      String.prototype.trim = function() {
         return this.replace(/^\s+|\s+$/g, '');
      }
   }


   return parseFileStr (fileStr);

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
      if (line.indexOf (charStart) != 0) return;
      var indx = line.indexOf (charEnd, 1);
      if (indx > 0)
         return {
            name: line.substr (1, indx-1),      // do not trim names
            rest: line.substr (indx+1).trim ()
         }
   }

   /*
      Example of use:
      out (parseEvaLine ("    one    , two,     three ... etc   "));
      // => [ "one", "two", "three ... etc" ]
   */
   function parseEvaLine (evalineStr, /*var*/ le, arr, indx)
   {
      evalineStr = evalineStr.trim ();
      le = evalineStr.length;
      if (le == 0) return;

      // implement basic rest of line escape either with ' or // but ONLY for the first column!
      if (evalineStr.indexOf("'") == 0)
      {
         return [ evalineStr.substr(1) ];
      }
      if (evalineStr.indexOf("//") == 0)
      {
         return [ evalineStr.substr(2) ];
      }

      // remove last trailing "," if last character
      //
      //       ","  => 1 element
      //       ",," => 2 element
      //       etc..
      //
      if (evalineStr.lastIndexOf (",") == le - 1)
         evalineStr = evalineStr.substring (0, le - 1);

      arr = evalineStr.split(',');
      for (indx in arr)
      {
         arr[indx] = decodeURIComponent (arr[indx].trim ());
      }

      return arr;
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
         textArr = filetext.split('\n');
      }

      for (lindx in textArr)
      {
         linStr = textArr[lindx].trim ();

         // check if start of unit
         //
         novoName = isname (linStr, "#", "#");
         if (novoName)
         {
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

         // it is a eva line or something to ignore like
         // something before first unit or first eva of the current unit
         //
         if (nameUnit && nameEva)
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
      lines = textStr.split('\n');
      for (indx in lines)
      {
         linArr = eva_parseEvaLine (lines[indx]);
         if (linArr.length == 0) continue; //ignore blank lines
         eva.push(linArr);
      }

      return eva;
   }
}
