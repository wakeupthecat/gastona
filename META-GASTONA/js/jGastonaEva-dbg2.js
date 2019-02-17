/*
jGastonaEva-min.js version 0.20180409 / groupcalc
composed by Eva.js + EvaLayout.js + LayoutManager.js + httSaco.js + jGastona.js minified
is part of the open source project https://github.com/wakeupthecat/gastona

Copyright (C) 2015,2016,2017,2018 Alejandro Xalabarder Aulet
@license : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/
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

   @file   EvaLayout.js

   @desc   This class (function) is responsible for a single evalayout. Due to the composition and masking features
           it needs from a reference to the layoutManager to be capable of rendering other layouts and handling
           properly the masking

   @requires LayoutManager.js

   ---------- Example of tipical use:

   typically handled by a LayoutManager

*/
function EvaLayout (mangr, layName)
{
   // "use strict";

   var DBG_ON = 0;

   var HEADER_ADAPT      = "A";
   var HEADER_EXPAND     = "X";
   var EXPAND_HORIZONTAL = "-";
   var EXPAND_VERTICAL   = "+";

   var headColumns, headRows, isPrecalculated = false;
   var layInfo = mangr.guiLayouts[layName];
   var iniRect = { left : 0, right : 200, top : 0, bottom : 20};
   var indxPos = {ileft : 0, iright : 0, itop : 0, ibottom : 0};
   //var COMPENSATE_BROWSER = 15;

   if (!layInfo)
   {
      console.log ("ERROR: no eva found with name \"" + layName + "\"");
      return;
   }
   if (!layInfo[0][0].match (/^eva$|^evalayout$/i))
   {
      // not an error, but no evalayout found
      return;
   }

   return {
      precalculateLayout: precalculateLayout,

      // general layable info (it could be a basis prototype)
      //
      wName     : layName,    // name of the component in the layout array
      isLaidOut : false,      // flag is true if has been found in the layout array
      iniRect   : iniRect,
      indxPos   : indxPos,
      invalidate : function () { isPrecalculated = false; },

      // specific for widget (html widget)
      //
      isWidget  : false,
      doMove    : doMove,
      doShow    : doShow
   }

   // sub-sub "class" HeaderLine to hold and compute all column or row information
   //
   function HeaderLine (margin, gap)
   {
      var FIXLEN = 0,      // type when pixels width or hight are given
          MAXMIN = 1,      // adaptable, max of min sizes of all elements in the column or row has to be computed
          EXPAND = 2,      // expandable, expansion percentage has to be
          totalExtra = 0,  // for intern calculation
          fixLineSize = 0, // exported (see return)
          regla = [];        // exported (see return)

      // return object representing a HeaderLine = margin, gap .. and [ HeaderItem, HeaderItem ... ]
      //
      return {
         // exported members
         margin      : margin,      // symmetric margin in pixels of the header (left-right or top-bottom)
         gap         : gap,         // gap between header elements (columns or rows)
         fixedSize   : function () { return fixLineSize; }, // computed fixed size of the header (width if columns, height if rows)
         regla       : regla,       // array of HeaderItem representing column or row attributes

         // exported methods
         addItem           : addItem,           // add a HeaderItem (colum or row) to the header
         setLengthOfItemAt : setLengthOfItemAt, // (index, len) to inform about the minimal size of a layable object at the header index
         endItems          : endItems,          // call it once finished all input data of headerItems
         getLengthInRange  : getLengthInRange,   // (totalExtra, indexfrom, indexto) returns the length of the items ín the range
         countItems        : function () { return regla.length; } // number of items of the header
      }

      function HeaderItem (headT)
      {
         var type = MAXMIN, extraPercent = 0., len = 0;

         headT = headT + ""; // to accept numbers as well as strings

         if (headT.length == 0 || headT.indexOf (HEADER_ADAPT) == 0)
         {
            type = MAXMIN;
         }
         else if (headT.indexOf (HEADER_EXPAND) == 0)
         {
            type = EXPAND;
            // percentage will calculated afterwards
            extraPercent = (headT.length < 2) ? 1: parseInt (headT.substr (1));
         }
         else
         {
            type = FIXLEN; // explicit size
            len = parseInt (headT);
         }

         return {
            type: type,
            len: len,
            extraPercent: extraPercent
         }
      }

      function addItem (headType)
      {
         var ite = HeaderItem(headType || "");
         regla.push (ite);
         totalExtra += ite.extraPercent;
      }

      function setLengthOfItemAt (index, len)
      {
         // take into account the information only if column (or row) is of type MAXMIN
         //
         if (regla[index] && regla[index].type == MAXMIN)
            regla[index].len = Math.max(regla[index].len, len);
      }

      function endItems ()
      {
         var ii, ele;
         // compute final fixed size and the extraPercent if needed
         //
         fixLineSize = margin + margin + /*COMPENSATE_BROWSER + */ gap * (regla.length - 1);
         for (ii in regla)
         {
            ele = regla[ii]
            fixLineSize += ele.len;
            if (totalExtra > 0)
               ele.extraPercent = (ele.extraPercent / totalExtra);
         }
      }

      function getLengthInRange (totalExtra, index, toIndx)
      {
         var suma = 0;
         toIndx = toIndx || index; // per default range = index, index

         // toIndx -1 means : until the end
         for (; (index <= toIndx || toIndx == -1) && index < regla.length; index ++)
            suma += gap + regla[index].len + regla[index].extraPercent * totalExtra;
         return suma;
      }
   }

   // example of layInfo structure with real indexes
   //          0        1   2    3   4
   //  0    EvaLayout, Mx , My , Gx, Gy
   //  1    grid     , X  ,  A , X3
   //  2           A , wi1, -  , wi2
   //  3           X , +  , wi3, -
   //
   // note that row 0 does not belong to the grid

   // functions to see the grid as 0,0 based array
   //
   function columnHeader (ncol)  {  return layInfo[1][ncol + 1];  }
   function rowHeader    (nrow)  {  return layInfo[2 + nrow][0];  }
   function gridRows     ()      {  return layInfo.length - 2;           }
   function gridCols     (nrow)  {  return layInfo[2 + nrow].length - 1; }

   function getGridCell (rr, cc)
   {
      rr += 2;
      cc += 1;
      if (rr >= 0 && rr < layInfo.length &&
          cc >= 0 && cc < layInfo[rr].length)
            return layInfo[rr][cc];
   }

   function precalculateLayout ()
   {
      if (isPrecalculated) return;

      // from .. marginX, marginY, gapX, gapY
      //
      headColumns = HeaderLine (parseInt (layInfo [0][1]) || 0,  // margin X
                                parseInt (layInfo [0][3]) || 0)  // gap X
      headRows    = HeaderLine (parseInt (layInfo [0][2]) || 0,  // margin Y
                                parseInt (layInfo [0][4]) || 0)  // gap Y

      var nCols = 0;
      var nRows = gridRows ();

      for (var rr = 0; rr < nRows; rr ++)
      {
         nCols = gridCols (rr);
         headRows.addItem (rowHeader (rr));

         for (var cc = 0; cc < nCols; cc ++)
         {
            if (cc >= headColumns.countItems ()) // add just once per column!
            {
               headColumns.addItem (columnHeader (cc));
            }

            var wname = getGridCell(rr, cc);
            if (! wname ) continue;

            if (DBG_ON)
               console.log ("precalc " + wname);
            var laya = mangr.getLayableByName (wname);
            if (! laya) continue;

            // set position x1,y1
            //
            laya.indxPos.ileft = cc;
            laya.indxPos.itop  = rr;

            // set position x2 checking cells with horizontal expansion
            //
            var ava = cc;
            while (ava+1 < nCols && getGridCell(rr, ava+1) === EXPAND_HORIZONTAL) ava ++;
            if (ava+1 < nCols && getGridCell(rr, ava+1) === EXPAND_HORIZONTAL + EXPAND_HORIZONTAL) ava = -1;
            laya.indxPos.iright = ava;
            if (ava == cc)
            {
               // add contribution of the cell rr, cc to the MAXMIN computation of rows
               // only if this widget does not expand horinzontally!
               //
               headColumns.setLengthOfItemAt (cc, laya.iniRect.right - laya.iniRect.left);
            }

            // set position y2 checking cells with vertical expansion
            //
            ava = rr;
            while (ava+1 < nRows && getGridCell(ava+1, cc) === EXPAND_VERTICAL) ava ++;
            if (ava+1 < nRows && getGridCell(ava+1, cc) === EXPAND_VERTICAL + EXPAND_VERTICAL) ava = -1;
            laya.indxPos.ibottom = ava;
            if (ava == rr)
            {
               // add contribution of the cell rr, cc to the MAXMIN computation of rows
               // only if this widget does not expand horinzontally!
               //
               headRows.setLengthOfItemAt (rr, laya.iniRect.bottom - laya.iniRect.top);
            }
            if (DBG_ON)
               console.log ("precalc set " + wname + " at " + laya.indxPos.ileft + ", " + laya.indxPos.itop + ", " + laya.indxPos.iright + ", " + laya.indxPos.ibottom);

            laya.isLaidOut = true;
         }
      }

      // last calculations
      //
      headColumns.endItems ();
      headRows.endItems ();

      iniRect.top = 0;
      iniRect.bottom = headRows.fixedSize ();
      iniRect.left = 0;
      iniRect.right = headColumns.fixedSize ();

      isPrecalculated = true;
   }

   function doMove (x0, y0, totWidth, totHeight)
   {
      precalculateLayout ();

      var extraVertical   = Math.max(0, totHeight - headRows.fixedSize ());
      var extraHorizontal = Math.max(0, totWidth  - headColumns.fixedSize ());

      var posX = x0 + headColumns.margin;
      for (var cc = 0; cc < headColumns.countItems (); cc ++)
      {
         // add the size of the previous column
         if (cc > 0)
            posX += headColumns.getLengthInRange (extraHorizontal, cc-1);

         var posY = y0 + headRows.margin;
         for (var rr = 0; rr < headRows.countItems (); rr ++)
         {
            // add the size of the previous row
            if (rr > 0)
               posY += headRows.getLengthInRange (extraVertical, rr-1);

            var wname = getGridCell(rr, cc);
            if (! wname ) continue;
            var laya = mangr.getLayableByName (wname);
            if (! laya) continue;

            if (laya.isLaidOut)
            {
               var dx = -headColumns.gap; // we will add one too many
               var dy = -headRows.gap;    // we will add one too many

               dx += headColumns.getLengthInRange (extraHorizontal, laya.indxPos.ileft, laya.indxPos.iright);
               dy += headRows.getLengthInRange (extraVertical, laya.indxPos.itop, laya.indxPos.ibottom);

               if (posX < 0 || posY < 0 || dx < 0 || dy < 0)
               {
                  if (DBG_ON)
                     console.log ("esto " + wname + " no se ve na de na " + posX + ", " + posY + ", " + dx + ", " + dy);
                  continue;
               }
               if (DBG_ON)
                  console.log ("widget " + wname + " set at " + posX + ", " + posY + " dim " + dx + " x " + dy);
               laya.doMove (posX, posY, dx, dy);
               laya.doShow (true);
            }
            else
            {
               if (DBG_ON)
                  console.log ("esto " + wname + " lo oculto ");
               laya.doShow (false);
            }
         }
      }
   }

   function doShow (bShow)
   {
      precalculateLayout ();
      for (var cc = 0; cc < headColumns.countItems (); cc ++)
      {
         for (var rr = 0; rr < headRows.countItems (); rr ++)
         {
            var laya = mangr.getLayableByName (getGridCell(rr, cc));
            if (laya)
               laya.doShow (bShow);
         }
      }
   }
}
/*
Copyright (C) 2015 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

/**
   @author Alejandro Xalabarder
   @date   2015.05.21

   @file   LayoutManager.js

   @desc   Load a set of eva layouts (Eva File format with one unit called "layouts")
           Handles the visibility of the widgets keeping only visible those whose layout is being rendered
           Enables the masking mechanism

   @requires EvaLayout.js
             Eva.js only necessary if want to parse eva format (method evaFile)

   ---------- Example of tipical use:

   // load a complete set of layouts to be composed or switched
   //
   var managr = layoutManager (evaFile ("#layouts#\n <layout1> Evalayout...."));

   // select a specific layout, if the function is not called the name "main" is assumed
   managr.setLayout ("layout3");

   // place the components according to the current layout and using the sizes
   // tipically this can be called when the window size changes
   managr.doLayout (dx, dy);

   // an example of masking, it replaces the widget button1 with the one called combo4
   //
   managr.maskLayoutId ("button1", "combo4");

*/
function layoutManager (evaObj, callbackAddWidget)
{
   "use strict";
   var DBG_VERBOSE = false;
   var guiLayouts,
       guiConfig,
       changeInWidgets,
       callbAddWidget = callbackAddWidget || function (ele) { console.log ("adding widget id [" + ele + "]"); },
       maskMap = {},
       layoutStack = [],
       layoutElemBag = {},
       layoutList = [],
       currentLayoutName
       ;

   reloadAllLayouts (evaObj);

   return {
      // public functions to export
      guiConfig      : guiConfig,
      loadConfig     : function (evaObject) { evaObj = evaObject; reloadAllLayouts (evaObj); },
      maskLayoutId   : maskElement,
      unmaskLayoutId : unmaskElement,
      doShowLayout   : function (layName, dx, dy) { doShowByNameAt (layName, 0, 0, dx, dy); },
      setLayout      : function (name) { setCurrentLayoutName (name); },
      doLayout       : function (dx, dy) { doShowByNameAt (currentLayoutName, 0, 0, dx, dy); }
   };

   // function to check unique values
   //
   function checkPushable (stack, layelem, /**/ indx)
   {
      for (indx in stack)
         if (stack[indx] === layelem)
         {
            console.log ("ERROR: layout element " + layelem + " already exists, it cannot be stacked!");
            return false;
         }
      return true;
   }

   function setCurrentLayoutName (name)
   {
      if (!currentLayoutName || currentLayoutName !== name)
      {
         // hide all elements ?
         invalidateAll ();
      }

      currentLayoutName = name;
   }

   function invalidateAll ()
   {
      for (var layname in layoutElemBag)
      {
         var layo = layoutElemBag[layname];
         if (!layo.isWidget)
            layo.invalidate ();
         else
            layo.doShow (false);
      }
   }

   // constructor for a layout or widget
   //
   function createLayableWidget (wname, oRect)
   {
      // try to estimate default size (height & width) for the element
      //
      var ele = document.getElementById(wname);
      if (ele && !oRect)
      {
         var height = (ele.offsetHeight + 1);
         var width  = (ele.offsetWidth + 1);

         if (DBG_VERBOSE)
            console.log ("element " + wname + " content[" + ele.innerHTML + "] estimates " + width + " x " + height);

         if (height > 1)
            oRect = { left: 0, right: width * 1.2, top: 0, bottom: height * 1.2 };
      }

      return {
         // general layable info (it could be a basis prototype)
         //
         wName     : wname,      // name of the component in the layout array
         isLaidOut : false,      // flag is true if has been found in the layout array

         // initial position and size rectangle of the component
         //
         iniRect   : oRect || { left : 0, right : 200, top : 0, bottom : 20},

         // indxPos : place in the layout array measured in array indices
         //
         indxPos   : {ileft : 0, iright : 0, itop : 0, ibottom : 0},

         // specific for widget (html widget)
         //
         isWidget  : true,
         doMove : function (x, y, dx, dy)
         {
            var ele = document.getElementById(wname);
            if (ele)
            {
               ele.style.position = "absolute";
               ele.style.left   = Math.round(x) + "px";
               ele.style.top    = Math.round(y) + "px";
               ele.style.width  = Math.round(dx) + "px";
               ele.style.height = Math.round(dy) + "px";
            }
            else console.log ("ERROR expected html element " + wname + " not found!");
         },

         doShow : function (bShow) {
               var elm = document.getElementById(wname);
               if(elm)
               {
                  //don't work on IE ... sometimes ???
                  //elm.style.display = bShow ? "inline-block" : "none";
                  elm.style.visibility = bShow ? "visible" : "hidden";
               }
            }
      };
   }

   function getLayableByName (oname)
   {
      if (oname === "" || !cellElementIsAnId (oname)) return; // undefined

      var layable = layoutElemBag [doYouMean (oname)];
      if (! layable)
      {
         console.log ("ERROR: don't know how to find " + oname + " or " + doYouMean (oname));
         return createLayableWidget ("bDummy");
      }

      // Note : getLayableByName is called in doShowAt
      //        so is a candidate for endless recursion
      //        here we just prevent calling precalculateAll
      if (checkPushable (layoutStack, layable.wName))
      {
         if (!layable.isWidget)
            layable.precalculateLayout ();
      }

      return layable;
   }


   function maskElement (masked, masker)
   {
      // masking to undefined, "" or itself is the same as unmask "masked"!
      maskMap[masked] = (!masker || masker === "" || masked === masker) ? undefined: masker;
      changeInWidgets = true;
      invalidateAll ();
   }

   function unmaskElement (masked)
   {
      if (! maskMap[masked]) return false;

      maskMap[masked] = undefined;
      changeInWidgets = true;
      invalidateAll ();
      return true;
   }

   // basically return the proper name according to the mask state
   //
   function doYouMean (namewanted)
   {
      var masker,
          indx,
          antiRecList = [];

      if (!cellElementIsAnId (namewanted))
         return namewanted;

      do
      {
         masker = maskMap [namewanted];
         if (! masker || masker === "" || masker === namewanted)
         return namewanted; // as it not masked

         for (indx in antiRecList)
            if (antiRecList[indx] === masker)
            {
               console.log ("ERROR: masks for " + namewanted + " found circular!");
               return namewanted;
            }
         antiRecList.push (namewanted);
         namewanted = masker;
      } while (antiRecList.length < 200); // a limit of 10 would be enough as well ..

      console.log ("ERROR: masks for " + namewanted + " too deep!");
      return namewanted;
   }

   function cellElementIsAnId (ele)
   {
      if (! ele  || ele.length == 0) return false;
      return ele.match (/^[^+-]/);
   }

   function exportThisManager ()
   {
      return {
         guiLayouts: guiLayouts,
         guiConfig: guiConfig,
         getLayableByName: getLayableByName,
      };
   }

   function reloadAllLayouts (evaObj)
   {
      // either find unit #layouts# containing all layouts
      //
      guiConfig = {};

      // #layouts#
      //    is the name to be used in scripts that only use layout manager (not jGastona),
      //    all variables are suppose to be "layout of"
      //
      guiLayouts = evaObj["layouts"];
      if (! guiLayouts)
      {
         guiLayouts = {};
         // or unit #javaj# to (name compatible with gast)
         // containing layouts in variables <layout of ...>
         // and other configurations
         //
         var javajUnit = evaObj["jGuix"] || evaObj["guix"] || evaObj["javaj"]; // towards "guix" instead of javaj ...
         if (! javajUnit)
         {
            console.log ("Error: unit layouts not found!");
            return;
         }

         // collect only layouts
         for (var eva in javajUnit)
         {
            if (eva.indexOf ("layout of ") == 0)
            {
               // remove "layout of " from the name
               guiLayouts[eva.substr(10)] = javajUnit[eva];
            }
            else
            {
               // other configurations like <layoutWindowLimits> minx, miny, maxx, maxy
               //
               guiConfig[eva] = javajUnit[eva];
            }
         }
      }

      changeInWidgets = true;
      maskMap = {};
      layoutStack = [];
      //... layoutList = [];  // we have or collect only layouts...
      layoutElemBag = {};

      // now fill layoutElemBag with all layout and widget ids and its
      // "layable" data

      // find out all widgets and layouts
      //
      for (var lay in guiLayouts)
      {
         var ela = EvaLayout (exportThisManager (), lay);
         if (ela)
         {
            // +++ collect "layeables" layouts ids
            layoutElemBag[lay] = ela;
            // ... console.log ("adding layout id [" + lay + "]");
            //... layoutList.push (lay);
         }
      }
      // now that we have collected all layout names
      // go again through all layouts and find the inner elements (widgets or layout references)
      //
      //... for (lay in layoutList)
      for (var lay in guiLayouts)
      {
         var eva = guiLayouts[lay];
         for (var rr = 2; rr < eva.length; rr ++)
         {
            for (var cc = 1; cc < eva[rr].length; cc ++)
            {
               var ele = eva[rr][cc];
               if (cellElementIsAnId (ele))
               {
                  // widget id candidate .. check if it is not a layout id
                  if (! layoutElemBag[ele])
                  {
                     callbAddWidget (ele);
                     layoutElemBag[ele] = createLayableWidget (ele);  // +++ collect "layeables" widget ids
                  }
               }
            }
         }
      }
   }

   function doShowByNameAt (oname, x0, y0, dx, dy)
   {
      oname = oname || "main";
      setCurrentLayoutName (oname);
      var layo = getLayableByName (oname);
      if (layo)
      {
         // push name
         layoutStack.push (layo.wName);

         layo.doMove (x0, y0, dx, dy);

         // pop name
         layoutStack.pop ();
      }
   }
}
/*
Copyright (C) 2017 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

// This library contain two funtions to pack and unpack data in and from http messages
//
//    function httPack   (parconfig, unidata)
//    function httUnpack (bodytxt, httresp)
//

// ----------------------------------------------
// function httPack (parconfig, unidata)
//
//    desc: Packs data to fit in a http request message, using for that the URI query part, headers and the body
//
//    returns : object with the elements
//                 { onelineparams: string,
//                   headers: { header: value, ...},
//                   body : string
//                 }
//
//              the object can be used to form the final ajaxPOST message
//
//    option 1 : (typeof parconfig === "string")
//
//       this is actually the simplest case
//       parconfig is interpreted as the URI query part literally and
//       no data from unidata is packed at all
//
//       Example:
//          httpPack ("myid=89281&myname=Bellguy");
//
//    option 2 : (typeof parconfig === "object")
//
//      here parconfig may contain one or more of following elements
//
//          params : string                     The value is literally the query part of the URI
//          or
//          params : { param: value, ... }      a query part of the URI will be formed as "param=encodeUTF8(value)&..."
//
//          headers : { header: value, ... }    headers will not be encoded!
//
//          body : string                       Then this will be the body
//
//          or
//          bodyVars  : [variable, ...]         Body will contain the variables specified in the array encodedUTF8 if not specified the opposite
//          bodyVarsFormat: eva|json|propval    Defines how the variables are formated in the body, default is eva
//
//          encodeUTF8 : true|false             Default and if encodeUTF8 is undefined or null is true!
//
//     NOTE: if parconfig is an object and no "body" is defined and no "bodyVars" is defined or bodyVars is undefined or null
//           then ALL variables contained in unidata will be packed in the body
//
//    Examples:
//
//       var prepost = httpack ("id=myId&name=MiNombre");
//       var prepost = httpack ({ params: "id=myId&name=MiNombre" });
//       var prepost = httpack ({ params: { id: "myId", name: "MiNombre" }});
//       var prepost = httpack ({ params: { id: "myId", name: "MiNombre" },
//                                headers : { XmyHeader1 : "valor", xETC: "nomaspues" },
//                                body : "este es \n mi body!",
//                              });
//       var prepost = httpack ({ params ....
//                                headers ....
//                                bodyVarsFormat : "eva",
//                                bodyVars : [ "myvar", "eImportantField" ],
//                                encodeUTF8: false,
//                              });
//
//       var prepost = httpack ({ params ....
//                                headers ....
//                                bodyVarsFormat : "json",
//                                bodyVars : null,
//                                encodeUTF8: false,
//                              });
//
//
//     NOTE: there is no need of specifying the format chosen for the body, the server should know it
//
function httPack (parconfig, unidata)
{
   var reto = { onelineparams : "", headers : {}, body: "" };
   var heads = {};
   var body = "";
   var oneline = [];
   var encodeUTF8 = true;

   if (typeof parconfig === "string")
        oneline = [ parconfig ]; // no encode utf8
   else
   {
      parconfig = parconfig || { };

      encodeUTF8 = parconfig["encodeUTF8"] !== false; // always default true

      //get headers directly (no encode)
      //
      reto.headers = parconfig["headers"]||{};

      //params for oneline params
      //
      var parline = parconfig["params"]||parconfig["parameters"]||{};
      if (typeof parline === "string")
         oneline = [ parline ] ;
      else
         for (var pp in parline)
            oneline.push (pp + "=" + encodeOrRaw (parline[pp]));

      body = parconfig["body"];
      if (typeof body !== "string")
         body = formatBody (parconfig["bodyVars"], parconfig["bodyVarsFormat"]||"eva");
   }

   reto.onelineparams = oneline.join ("&");
   reto.body = body;

   return reto;

   function encodeOrRaw (str)
   {
      // additionally convert ' to %27 which will be decoded correctly by decodeURIComponent
      //
      return encodeUTF8 ? encodeURIComponent ((""+str).replace (/'/g, "%27")): str+"";
   }

   function formatBody (bodyVariables, format)
   {
      function encodeEva (indx)
      {
         if (!encodeUTF8)
            return unidata[bodyVariables[indx]];

         // NOTE:
         // if encode then do a copy!
         // we don't want to change our data just send it encoded
         //
         var cop = [];
         var obj = unidata[bodyVariables[indx]];
         for (var ii in obj) {
            var lina = [];
            for (var jj in obj[ii])
               lina.push (encodeOrRaw (obj[ii][jj]));
            cop.push (lina);
         }
         return cop;
      }

      function getEvaFlat (va)
      {
         function evaVar2Flat (obj)
         {
            var str = [], lin;
            for (var row in obj)
               for (var col in obj[row])
                  str.push ((col > 0) ? ",":"" + obj[row][col]);
            return str.join ("\n");
         }

         return encodeOrRaw (evaVar2Flat (unidata[bodyVariables[va]]));
      }

      if (! bodyVariables)
      {
         // all variables, if don't want this then set bodyVariables to []
         bodyVariables = [];
         for (var ii in unidata)
            bodyVariables.push (ii);
      }

      var vv;
      var sal = "";

      if (format === "propval")
      {
         // each in one line with the format
         //   prop:value
         //
         var lans = [];
         for (vv in bodyVariables)
            lans.push (bodyVariables[vv] + ":" + getEvaFlat (vv));
         sal = lans.join("\n");
      }
      else if (format === "json")
      {
         // as JSON
         //
         var oelect = { };
         for (vv in bodyVariables)
            oelect [bodyVariables[vv]] = encodeEva (vv);
         sal = JSON.stringify(oelect);
      }
      else if (format === "eva")
      {
         // prepare the body
         //
         var evaObj = evaFileObj ("#data#\n");
         var bodyUnit = evaObj.obj["data"];

         for (vv in bodyVariables)
            bodyUnit[bodyVariables[vv]] = encodeEva (vv);

         sal = evaObj.toText ();
      }
      else
      {
         alert ("ERROR: calling formatBody with not supported format [" + format + "]");
      }

      return sal;
   }
}

// ----------------------------------------------
//    function httUnpack (bodytxt, httresp)
//
//    desc: Build a response object from the http response unpacking it
//
//    returns : object with the properties obj-params, obj-headers, obj-body
//
//          load the response body as specified in Content-type (eva or json format) into obj-body
//          extract parameters from the header XParamsInOneLine and set them in obj-params as properties
//          set all headers and set them in obj-headers
//
//    1- Unpacking the body:
//
//       First unpacks the body according to the format contained in the header "Content-Type"
//       specifically only three formats are recognized "text/eva" and "text/json".
//
//       if format "text/eva" the body contain an eva unit #data# with the variables, for example
//
//          #data#
//
//             <mivar>     one, two, three
//             <mivar2>    Pablito clavo un clavito
//             <miTabla>
//                   id, name
//                   21, Evaristo
//                   77, Carolo
//
//       if format "text/json" the body contain a json object, for example
//
//         "obj-body" : { "mivar": [ "one", "two", "three" ],
//                       "whatsoever" : { "etc": "..." }
//                      }
//
//    2- Unpacking parameters
//
//       If the header XParamsInOneLine is found then it is interpreted as a URI query and all
//       the variables are set in the property "obj-params" of the result object always decoding them UTF8
//
//    3- Setting headers in the result object
//
//       All response headers are set in the property "obj-headers" of the result object
//
//
function httUnpack (bodytxt, httresp)
{
   var respObj = {};
   var subobj = {};

   // check Content-type for json or eva and set the object accordingly
   //
   var str = httresp.getResponseHeader ("Content-Type");
   if (str)
   {
      subobj = null;
      splitArr = str.split (";");
      if (splitArr.indexOf ("text/eva") !== -1)
      {
         // load n units (an eva file)
         subobj = evaFileStr2obj (bodytxt);
      }
      else if (splitArr.indexOf ("text/evas") !== -1)
      {
         // undocumented (experimental) support for single anonymous eva unit
         // load a unnamed unit
         subobj = evaFileStr2obj ("#anonima#\n" + bodytxt) ["anonima"];
      }
      else if (splitArr.indexOf ("text/json") !== -1)
      {
         subobj = JSON.parse(bodytxt);
      }
   }
   if (subobj == null)
   {
      // don't know how to process the body, deliver it as it is
      //
      subobj = { rawBody: bodytxt };
   }
   // >>> obj-body
   respObj["obj-body"] = subobj;


   // check XParamsInOneLine and set individual variables
   //
   str = httresp.getResponseHeader ("XParamsInOneLine");
   if (str)
   {
      subobj = {};
      var splitArr = str.split ("&");
      for (var vv in splitArr)
      {
         var pair = splitArr[vv].split ("=");
         if (pair.length === 2)
         {
            subobj [pair[0]] = decodeURIComponent (pair[1]);
         }
         else console.log ("ERROR on XParamsInOneLine variable [" + splitArr[vv] + "]");
      }
      respObj["obj-params"] = subobj;
   }

   // add all headers into "obj-headers", Note: getAllResponseHeaders () returns a literal string with all headers
   //
   subobj = {};
   var respArr = httresp.getAllResponseHeaders ().split ("\n");
   for (var hh in respArr)
   {
      var hname = respArr[hh].split(":", 1);
      if (hname && hname[0])
         subobj[hname[0]] = httresp.getResponseHeader (hname[0]);
   }
   respObj["obj-headers"] = subobj;

   return respObj;
}
/*
Copyright (C) 2015,2016,2017,2018 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

/**
   @author Alejandro Xalabarder
   @date   2015.07.26

   @file   jGastona.js

   @desc
      Logic in javascript emulating as close as possible the one
      used in gastona + javaj + listix + mensaka in gastona java project

      what is implemented is

         - automatic generation of html widgets and layout (#javaj# unit)

               'd': // div
               'n': // link (login ?)
               'b': // button
               'e': // text input
               'u': // upload file selector
               'm': // image
               'p': // password
               'h1': header
               'h2': header
               'h3': header
               'l': // label
               'x': // text area
               't': // simple table
               'c': // combo
               'r': // radio group
               'k': // checkbox group
               'i': // list


         - creation of data model and binding with widgets (#data# unit + widgets in #javaj#)

         - handling of messages : msg to widgets, msg from widgets, msg responseAjax

         - AJAX facility methods : httPack and AJAXSend


*/

//"use strict";

function jGastona (evaConfig, existingPlaceId)
{
   "use strict";
   var dataUnit,
       listixUnit,
       corpiny,
       isStammLayout = false, // only if it will occupy the whole window area
       layMan,
       javajzWidgets       // of widgetFactory
       ;
   var minWidth = -1;
   var minHeight = -1;

   var AJAX_RESPONSE_MESSAGE = "ajaxResponse";       // mensaka's message for a post (e.g. to be handle with < -- ajaxResponse myPost>)

   // 2018.03.17
   //  a jGastona object has to be started explicitly, for instance
   //
   //    var jast = jGastona (...);
   //    jast.start (); // or jast.run ();
   //
   // this gives more flexibility, for instance it allows more objects to be loaded and
   // be prepared for a future use. Also very importantly it allows to reference the
   // object (e.g. jast) from a external script that may be called by the very first listix "main" entry.
   // If the object where started automatically, as it was done before, the code of "main"
   // would be called before the variable "jast" exists causing an error if any extern function tries to use
   // it at that time.
   //
   var started = false;
   function start ()
   {
      started = true;
      loadJast (evaConfig, existingPlaceId);
      window.addEventListener("resize", adaptaLayout);
   }

   setTimeout (function () {
         if (! started) {
            start ();
            console.error ("ERROR! jGastona: jast has been started by timeout!.\nThis \"compatibility feature\" will be deprecated since now a jGastona object has to be started explicitly (e.g. jgast.start ();)");
         }
      },
      100
      );

   return {
      // public functions to export

      start              : start,
      run                : start,      // alias of start
      getLayoutMan       : function () { return layMan; },
      mensaka            : mensaka,
      getIdValue         : getIdValue,
      getData            : getData,
      getDataAsTextArray : getDataAsTextArray,
      getDataCell        : getDataCell,
      setData            : setData,
      setDataCell        : setDataCell,
      setVarTable_DimVal : setVarTable_DimVal,
      getCellEvaUnit     : getCellEvaUnit,
      adapta             : adaptaLayout,
      mask               : mask,
      unmask             : unmask,
      htmlElem           : htmlElem,
      canUploadFile      : canUploadFile,

      // part ajax ...
      //
      AJAXAnyMethodRaw : AJAXAnyMethodRaw,
      AJAXPostRaw      : AJAXPostRaw,
      AJAXSend         : AJAXSend,            // send data as eva, json or prop:value
      AJAXUploadFile   : AJAXUploadFile,      // upload one file (NOTE: only one file!)
      AJAXLoadRootJast : AJAXLoadRootJast,    // ask the server for a jast file to be loaded
      AJAXgetDataForId : AJAXgetDataForId,    // ask the server for content for the id, on resposte the content will be updated automatically

      // getDataUnit    : function () { return dataUnit; }
   };

   function str2lineArray (str) { return str.replace(/\r\n/g, "\r").replace(/\n/g, "\r").split(/\r/); }

   function getWindowWidth ()
   {
      return isStammLayout ?
             // due to IE compatib.
             (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) - 15:  // 15 is an empiric number ...
             corpiny.offsetWidth;
   }

   function getWindowHeight ()
   {
      return isStammLayout ?
             // due to IE compatib.
             (window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) - 15: // 15 is an empiric number ...
             corpiny.offsetHeight;
   }

   function getIdValue (id)
   {
      var ele = document.getElementById(id);
      return (ele) ? ele.value : getDataCell (id);
   }

   // shorcuts
   function mask (a, b)
   {
      if (layMan) {
         layMan.maskLayoutId (a, b);
         adaptaLayout ();
      }
   }

   function unmask (a)
   {
      if (layMan && layMan.unmaskLayoutId (a)) {
         adaptaLayout ();
         return true;
      }
      return false;
   }

   function isEvaValue (eva)
   {
      // is an array >0 of arrays
      return Array.isArray (eva) && eva.length > 0 && Array.isArray (eva[0]);
   }

   function isEvaSingleValue (eva)
   {
      // contains only one row and one column
      return isEvaValue (eva) && eva.length == 1 && eva[0].length == 1;
   }


   function isEvaEmpty (eva)
   {
      // contains only one row and one column and its value is ""
      return isEvaSingleValue (eva) && eva[0][0] === "";
   }

   function strStartsWith (s1, s2, pos)
   {
      pos = pos || 0;
      return s1.indexOf(s2, pos) === pos;
      //return s1.slice(0, s2.length) === s2;

      // thisdoes not work for IE8, 9
      //
      //if (!String.prototype.startsWith) { String.prototype.startsWith = function(seas, pos) { pos = pos || 0; return this.indexOf(seas, pos) === pos; }; }
   }

   function htmlElem (elmeOrId)
   {
      if (typeof elmeOrId === "string")
         return document.getElementById (elmeOrId);
      return elmeOrId;
   }

   function adaptaLayout ()
   {
      if (!layMan) return;
      var dx = getWindowWidth ();
      var dy = getWindowHeight ();

      var lali = layMan.guiConfig["layoutWindowLimits"]; // <layoutWindowLimits> mindx, mindy, maxdx, maxy
      if (lali && lali[0])
      {
         // note: lali[0] is the first row of the variable (the only one)
         //
         var mindx = parseInt(lali[0][0]|"0");
         var mindy = parseInt(lali[0][1]|"0");
         var maxdx = parseInt(lali[0][2]|"0");
         var maxdy = parseInt(lali[0][3]|"0");
         dx = Math.max (dx, mindx);
         dy = Math.max (dy, mindy);
         if (maxdx > 0) dx = Math.min (dx, maxdx);
         if (maxdy > 0) dy = Math.min (dy, maxdy);
      }

      if (layMan)
         layMan.doLayout(dx, dy);
   }

   function loadJast (evaConfig, placeId)
   {
      javajzWidgets = {};
      dataUnit = {};
      listixUnit = {};
      layMan = undefined;

      if (! evaConfig ) return;

      dataUnit = evaConfig["data"] || {};
      listixUnit = evaConfig["jListix"] || evaConfig["jlistix"] || evaConfig["listix"] || {};

      // ensure corpinyo (STAMM) is a root element in the document's body
      //
      if (!corpiny)
      {
         var baseId = placeId||"jGastonaStammHtmlElem";
         if (!document.getElementById(baseId)) {
            document.body.innerHTML = "<div id='" + baseId + "' style = 'position:relative;'></div>";
            isStammLayout = true;
         }
         corpiny = document.getElementById(baseId);
      }
      if (!corpiny) {
         console.error ("no baseId can be found!");
         return;
      }

      // remove last main jast layout if any
      // here candidate to push instead
      while (corpiny.hasChildNodes())
      {
         corpiny.removeChild(corpiny.firstChild);
      }

      // load all components and return the manager
      //
      layMan = layoutManager (evaConfig, onAddWidget);
      adaptaLayout ();

      //do the first task : "main" if exists
      var fmain = listixUnit["main"];
      if (fmain)
         executeListixFormat (fmain);
   }

   function getCellEvaUnit (unit, eva, row, col)
   {
      return unit[eva] ? unit[eva][row||"0"][col||"0"]||"": "";
   }

   function getData (name)
   {
      return !dataUnit[name] ? undefined: dataUnit[name];
   }

   function getDataAsTextArray (name)
   {
      var eva = getData (name);

      var sal = [];
      for (var ii in eva)
         sal.push (eva[ii][0]);
      return sal;
   }

   function getDataCell(name, row, col)
   {
      return getCellEvaUnit (dataUnit, name, row, col);
   }

   function setDataCell (name, value, row, col)
   {
      row = row||"0";
      col = col||"0";
      // create variable if needed
      if (!dataUnit[name])
         dataUnit[name] = [[""]];

      // create row if needed
      if (!dataUnit[name][row])
         dataUnit[name][row] = [ "" ];

      dataUnit[name][row][col] = value||"";
   }

   // set the value into the data variable "name"
   // being value either a string or an eva variable
   // after that send the message data! to the widget "name" if exists
   //
   function setData (name, value)
   {
      // create on demand
      if (typeof value === "string")
      {
         dataUnit[name] = [[""]];
         setDataCell (name, value);
      }
      else if (isEvaValue (value))
      {
         dataUnit[name] = value;
      }
      else
      {
         alert ("Error: setData \"" + name  + "\", the value is not a string nor looks like an eva variable");
      }

      //2017.11.05 more general approach, even if no widget associated we send the message "name data!"
      //           if there is a widget associated then two things will happen, widget update plus message
      //..
      //.. deliverMsgToWidget (getzWidgetByName (name));
      mensaka (name + " data!");
   }

   function mensaka (msg)
   {
      // handling messages for widgets i.e. "zwidget data!" (update data of zwidget)
      // (javaj task)
      var ii = msg.indexOf (" ");
      if (ii > 0)
      {
         var wnam = msg.substr (0,ii); // widget name i.e. "bBoton"
         var wmet = msg.substr (ii+1); // method      i.e. "data!"

         deliverMsgToWidget (getWidgetByName (wnam), wmet);
         //2017.11.05 more general approach
         //           continue since maybe the user (listix) is notified to the widget message as well
         // // if (deliverMsgToWidget (getWidgetByName (wnam), wmet))
         // //     return;
      }

      // look for the variable <-- message>, first in data else in listix
      // (listix task)
      // Note : here it is done in an "interpreter fashion",
      //        another approach is to generate proper functions and listeners previosly
      //
      var fbody = dataUnit["-- " + msg] || listixUnit["-- " + msg];
      if (! fbody)
      {
         // message not subscribed! ignore it
         // console.log ("ignoring mensaka \"" + msg  + "\"");
         return;
      }

      executeListixFormat (fbody);
   }

   function executeListixFormat (fbody)
   {
      // by now a listix or jlistix format is just an array of text containing javascript code
      // so we join it all and call eval
      //
      eval (fbody.join ("\n"));
   }

   // ============ DATA AND VARIABLES HANDLING
   //
   function setVarTable_DimVal (arrVarNames)
   {
      var vara = [[ "dimension", "value" ]];
      for (var fi in arrVarNames)
      {
         vara.push ( [ arrVarNames[fi], getDataCell (arrVarNames[fi]) ]);
      }

      dataUnit["varTable_DimVal"] = vara;
   }


   // --------- START PART widgetFactory
   //

   function setValueToElement (element, valueStr)
   {
      if (element)
      {
         if (typeof element.value === "string")
              element.value = valueStr;
         else element.innerHTML = valueStr;
      }
   }

   function onAddWidget (name)
   {
      if (! name || name.length == 0) return;

      var zwid;

      //  var2Text ("eThisIsAText")); ==> "This Is A Text"
      //  var2Text ("eThis_is_a_text")); ==> "This is a text"
      function var2Text (name)
      {
         if (name.length <= 2) return "";
         var sal = name[1];

         for (var ii = 2; ii < name.length; ii ++)
         {
            if (name[ii] == '_')
              sal += " ";
            else {
               if (name[ii] < 'Z' && sal[sal.length-1] != ' ')
                  sal += " ";
               sal += name[ii];
            }
         }
         return sal;
      }

      var updateSimpleLabel = function () {
         this.innerHTML = (dataUnit[name] ? getDataCell (name) : var2Text (name));
      }
      var updateSimpleLabel2 = function () {
         this.innerHTML = (dataUnit[name] ? getDataCell (name) : var2Text (name.substr(1)));
      }
      var updateSimpleValue = function () {
         setValueToElement (this, getDataCell (name));
      }
      var updateSimpleSrc = function () {
         this.src = getDataCell (name);
      }
      var updateResetValue = function () {
         this.value = '';
      }
      var updateImage = function () {
         // this.src = getDataCell (name);
         //console.log ("setting background image for " + name + " [" + getDataCell (name) + "]");
         this["style"]["background-image"] = "url('" + getDataCell (name) + "')";
      }

      var signalName = function () {
         mensaka(name);
      }
      var assignValue = function () {
         dataUnit[name][0] = [ this.value||"?" ];
         signalName ();
      };
      var assignText = function ()
                       {
                           dataUnit[name] = [ [ ] ];
                           var text = this.value||"?";
                           var rows = str2lineArray (text);
                           for (var rr in rows)
                              dataUnit[name][rr] = [ rows[rr] ];

                           signalName ();
                       };

      var hayClassOf = dataUnit["class of " + name];
      var widgetclass = hayClassOf ? hayClassOf[0][0] : name;

      switch (widgetclass.charAt (0))
      {
         case 'd':
            zwid = fabricaStandard ("div", name, { "data!": updateSimpleLabel  } );
            break;
         case 'n':
            zwid = fabricaStandard ("a", name, { href: "login", "data!": updateSimpleLabel } );
            break;
         case 'b':
            zwid = fabricaStandard ("button", name, { onclick: signalName, "data!": updateSimpleLabel } );
            break;
         case 'e':
            zwid = fabricaStandard ("input", name, { type: "text", onchange: assignValue, placeholder: var2Text(name), "data!": updateSimpleValue } );
            break;

         // --- Note about widget class for submit button
         //    this could be handled with an extra widget class, implementing it as
         //    zwid = fabricaStandard ("input", name, { type: "submit", ...
         //    but this is not necessary since we can use a button ('b') and set its property "type" to submit
         //    for example
         //          <bSendIt type> //submit


         // --- Note about upload (choose file) widget class
         // As stated by the Chrome error message if we try to set a value different than empty string to this element
         // "Failed to set the 'value' property on 'HTMLInputElement': This input element accepts a filename, which may only be programmatically set to the empty string."
         //
         case 'u':
            zwid = fabricaStandard ("input", name, { type: "file", onchange: signalName, "data!": updateResetValue } );
            break;

         case 'm': // image
            // zwid = fabricaStandard ("img", name, { "data!": updateImage } );
            zwid = fabricaStandard ("div", name, { onclick: signalName, "data!": updateImage } );
            zwid.style["background-position"] = "center center";
            zwid.style["background-repeat"] = "no-repeat";
            zwid.style["background-size"] = "contain";
            break;

         case 'p': // password
            zwid = fabricaStandard ("input", name, { type: "password", placeholder: "password", onchange: assignValue, "data!": updateSimpleValue } );
            break;

         //(o) TOCHECK: some strange thing happen when two h's are put beside in EVALAYOUT
         case 'h':
            if (widgetclass.length >= 2)
               zwid = fabricaStandard ("h" + widgetclass.charAt (1), name, { "data!": updateSimpleLabel2 } );
            break;

         case 'l':
            zwid = fabricaStandard ("label", name, { "data!": updateSimpleLabel } );
            break;

         case 'x':
            {
               var updata = function () {
                     var tex = "", row;
                     if (!isEvaEmpty (dataUnit[this.id]))
                        for (row in dataUnit[this.id])
                           tex += dataUnit[this.id][row] + "\n";
                     setValueToElement (this, tex);
                  }
               zwid = fabricaStandard ("textarea", name, { placeholder: var2Text(name), "data!": updata, onchange: assignText } );
            }
            break;


         case 't': // simple table
            {
               var updata = function () {
                     var etabla, rowele, colele, row, col, evaData = dataUnit[this.id];

                     // create new html table
                     while (this.hasChildNodes())
                     {
                        this.removeChild(this.firstChild);
                     }

                     etabla = document.createElement ("table");
                     etabla["id"] = this.id + "-table"; // e.g <div id="tMiTabla"> <table id="tMitabla-table">...

                     for (row in evaData)
                     {
                        if (isEvaEmpty (evaData))
                        {
                           // row === "0" only one column and empty ==> no headers
                           // no headers!
                        }
                        else
                        {
                           rowele = document.createElement ("tr");

                           for (col in evaData[row])
                           {
                              colele = document.createElement (row === "0" ? "th": "td");

                              // use instead ? colele.value = evaData[row][col];
                              setValueToElement (colele, evaData[row][col]);
                              rowele.appendChild (colele);
                           }
                           etabla.appendChild (rowele);
                        }
                     }
                     this.appendChild (etabla);
                  }
               zwid = fabricaSimpleTable (name, { "data!": updata });
            }
            break;

         case 'c': // combo
         case 'r': // radio group
         case 'k': // checkbox group
         case 'i': // list
            {
               var labels = [], values = [];
               var indxLabel = 0, indxValue = 0;

               // Now assume list of "value, label" for the data (with column names in the first row!)
               //
               for (var row in dataUnit[name])
               {
                  if (row === "0")
                  {
                     indxLabel = dataUnit[name][row].indexOf ("label");
                     if (indxLabel == -1) indxLabel = 0;
                     indxValue = dataUnit[name][row].indexOf ("value");
                     if (indxValue == -1) indxValue = 0;
                  }
                  else
                  {
                     values.push (dataUnit[name][row][indxValue]||"?");
                     labels.push (dataUnit[name][row][indxLabel]||"?");
                  }
               }
               var orient = dataUnit[name + " orientation"]||"X";

               //(o) TODO/jGastona/fabrica_zWidgets why not ?  zwid = fabricaSelectList (...

               if (name.charAt (0) == 'c')
                  corpiny.appendChild (fabricaSelectList (name, values, labels, false));
               if (name.charAt (0) == 'i')
                  corpiny.appendChild (fabricaSelectList (name, values, labels, true));
               if (name.charAt (0) == 'r')
                  corpiny.appendChild (fabricaGrupo ("radio", orient, name, values, labels));
               if (name.charAt (0) == 'k')
                  corpiny.appendChild (fabricaGrupo ("checkbox", orient, name, values, labels));
            }
            break;
      }

      if (zwid)
      {
         // collect it
         javajzWidgets[name] = zwid;
         corpiny.appendChild (zwid);

         deliverMsgToWidget (zwid, "data!");

         // experimental! all widgets need data!
         if (!dataUnit[name])
            dataUnit[name] = [ [ "" ] ];
      }
   }

   function getWidgetByName (widName)
   {
      return javajzWidgets[widName];
   }

   //alias
   function getzWidgetByName (nam) { return getWidgetByName (nam); }


   function deliverMsgToWidget (zwidget, msg)
   {
      if (! zwidget) return false;

      if (zwidget[msg]) {
         zwidget[msg] (); // update data
         return true;
      }
      alert ("ERROR (updateWidget) zwidget /" + zwidget.id + "/ with no 'data!' message");
      return false;
   }


   function updatezWidget (zwidget)
   {
      //2017.11.05
      mensaka (zwidget + " data!");

      //  if (! zwidget) return;
      //
      //  if (zwidget["data!"])
      //     zwidget["data!"] (); // update data
      //  else
      //     alert ("ERROR (updateWidget) zwidget /" + zwidget.id + "/ with no 'data!' message");
   }

   // converts a string into a "string", "object" or js "function"
   // var str = str2jsVar ("\"sisie");
   // var arr = str2jsVar ("[ 'sisie', 'nono', 234, [1, 2] ]");
   //
   function str2Var (str)
   {
      var str2 = "";
      if (typeof str !== "string")
      {
         // array of strigs = text ?
         //
         str2 = str.join ("\n");
      }
      else str2 = str;

      // old style string (to be deprecated!)
      //
      if (str2.match(/^\s*[\"\']/))
         return str2.substr(1);

      // array [] or object {}
      //
      if (str2.match(/^\s*[\[\{]/))
         return eval ("(function () { return " + str2 + ";}) ()");

      // simply as string
      return str2;
   }

   function fabricaStandard (typestr, name, atts)
   {
      var ele = document.createElement (typestr);   // "label" "button" etc
      ele["id"] = name;
      ele.style.visibility = "hidden";
      ele.spellcheck = false; // per default FALSE !!!
      for (var aa in atts)
      {
         ele[aa] = atts[aa];
      }

      // ensure a variable in data unit if not already exists
      // for the value (NOTE: for some reason it does not work for typestr === "textarea")
      if (typestr === "input" && !dataUnit[name])
         dataUnit[name] = [[ "" ]];

      // Interpret attributes of element as follows
      //
      //                           action to do
      //    <elem onXXXX>  val     elem.onXXXX = val (function)
      //    <elem class>   val     elem.className = val
      //    <elem +class>  val     elem.className += (" " + val)
      //    <elem other>   val     elem.other = val
      //

      for (var dd in dataUnit)
      {
         // i.e. <eText class> //'btn
         // i.e. <eText onchange> //alarm("me change!");
         if (strStartsWith (dd, name + " "))
         {
            var attrib = dd.substr(name.length + 1);
            var value = dataUnit[dd];

            if (strStartsWith (attrib, "on")) {
               // notification to some event (e.g. "onkeyup", "onwheel" etc)
               //
               ele.addEventListener (attrib.substr(2),
                     function () {
                        // we must ensure that data of widget is reflected in dataUnit
                        // before doing anything with it for example "onkeyup" may occur before an
                        // onchange which sets automatically the data to the dataUnit
                        if (attrib !== "onchange" && ele["onchange"])
                              ele.onchange ();
                        eval (str2Var (value));
                     });
            }
            else if (attrib == "class") {
               ele.className = value;
            }
            else if (attrib == "+class") {
               ele.className += (" " + value);
            }

            try {
               // This try catch is only due to IE11, for example if value is "date"
               // it would cause "invalid argument" error and NOTHING will be shown
               // this may happen in more places, should we do a try catch erverywhere !?
               //
               ele[attrib] = str2Var (value);
            } catch (err) {}
         }
      }

      // if (text)
      //   ele.appendChild (document.createTextNode(text));

      return ele;
   }

   //== selectAllColumnsFromTable
   // <mytable>
   //        value, name          , phone
   //        01   , my first name , 88888
   //        02   , second        , 77777
   //
   // selectAllColumnsFromTable (unit, "mytable", "01");
   // produces setting the variables
   //
   //    <mytable selected.id> 01
   //    <mytable selected.name> my first name
   //    <mytable selected.phone> 88888
   //
   function selectAllColumnsFromTable (unit, name, strvalue)
   {
      if (!unit || !unit[name] || !unit[name][0]) return false;
      var colnames = unit[name][0];

      var indxVal = colnames.indexOf ("value");
      if (indxVal === -1) indxVal = 0;

      // clean all <name selected.column> variables
      for (var col in colnames)
         delete unit[name + " selected." + colnames[col]];

      //search the row with the value (unique id) at position 0
      for (var rosel = 1; rosel < unit[name].length; rosel ++)
      {
         if (unit[name][rosel][indxVal] === strvalue)
         {
            for (var col in colnames)
               unit[name + " selected." + colnames[col]] = [[ unit[name][rosel][col]||"?" ]];

            return true;
         }
      }
      return false;
   }

   // set all the variables for all column names <name seleted.columnname>
   // the single variable <name_value> and send the mensaka message "name"
   // or remove the variables if no value is selected (deselecting last item in a list)
   //
   function whenChangeTableSelection (name, value)
   {
      if (selectAllColumnsFromTable (dataUnit, name, value||"?"))
           dataUnit[name + "_value"] = [[ value||"?" ]]; // to have a single variable
      else delete dataUnit[name + "_value"];
      mensaka(name)
   }

   // for lists and combos
   //
   function fabricaSelectList (name, arrOp, arrLab, ismultiple)
   {
      var ele = document.createElement ("select");
      if (ismultiple)
         ele["multiple"] = "si";

      ele["id"] = name;
      ele.style.visibility = "hidden";
      ele.addEventListener ("change", function () { whenChangeTableSelection (name, this.value); });
      for (var ite in arrOp)
      {
         var subele = document.createElement ("option");
         subele["value"] = arrOp[ite];
         subele["data!"] = function () { }; //(o) TOREVIEW_jGastona_update message in subelements, is it really needed ?

         subele.appendChild (document.createTextNode(arrLab[ite]));
         ele.appendChild (subele);
      }

      return ele;
   }

   // for checkbox and radio groups
   //
   function fabricaGrupo (tipo, orient, name, arrOp, arrLab)
   {
      var ele = document.createElement ("div");
      ele["id"] = name;
      ele.style.visibility = "hidden";
      
      // *** Own width calculation
      // we have to estimate width, for some reason if not specified 
      // width (offsetWidth) per default is the whole width whereas the height is
      // correctly calculated from the content.
      var widthEstim = 0;

      //cannot do this here like in fabricaSelect, but to be done on each element
      //ele.addEventListener ("change", function () { whenChangeTableSelection (name, this.value); });
      for (var ite in arrOp)
      {
         var subele = document.createElement ("input");
         subele["type"] = tipo;
         subele["name"] = name;
         subele["value"] = arrOp[ite];
         subele["label"] = arrLab[ite];

         // a more accurate measure has to take into account the final font
         // which might be not know right now (?!)
         var estimW = 12 * subele["label"].length; // mean 12px per char
         
         subele["data!"] = function () { };
         subele.addEventListener ("change", function () { whenChangeTableSelection (name, this.value); });
         if (ite !== "0" && (orient == "Y" || orient == "V")) {
            ele.appendChild (document.createElement ("br"));
            widthEstim = Math.max (widthEstim, estimW);
         }
         else {
            widthEstim += estimW;
         }
         ele.appendChild (subele);
         ele.appendChild (document.createTextNode(arrLab[ite]));
      }
      ele.style.width = widthEstim + "px";

      return ele;
  }

  function fabricaSimpleTable (name, atts)
  {
      var ele = document.createElement ("div");
      ele["id"] = name;
      ele["widgetype"] = "t"; // to be used ...
      ele.style.visibility = "hidden";

      for (var aa in atts)
      {
         ele[aa] = atts[aa];
      }

      return ele;
   }

   // helper function to check is a file can be uploaded, for example
   //
   //   <-- bUploadFoto>
   //      //if (luix.canUploadFile ("uPhoto", 5)) {
   //      //   if (luix.AJAXUploadFile ("uPhoto", "uploadFoto")) {
   //      //      feedback ("uploading file ...");
   //      //   }
   //      //}
   //      //

   function canUploadFile (widname, maxSizeMB, alertEmpty, alertTooBig)
   {
      var ele = htmlElem (widname);
      var fileEle = ele.files[0]; // we only upload 1 file!

      var sizeLimitMB = maxSizeMB;

      if (! fileEle || fileEle === "") {
         if (alertEmpty !== "")
            alert (alertEmpty ? alertEmpty: "Please first choose a file!");
         return false;
      }
      if (sizeLimitMB && sizeLimitMB > 0 && fileEle.size > sizeLimitMB * 1024 * 1024) {
         if (alertTooBig !== "")
            alert (alertTooBig ? alertTooBig: ("File is too big to be uploaded, limit is " + maxSizeMB + " MB"));
         return false;
      }
      return true;
   }

   // --------- START PART AJAX
   //

   // ============ AJAX stuff (AJAX approach V0.90)
   //
   // ------------- packing and unpacking HTTP messages using paramCfg, respFuncOrObj
   //
   //    The method used for all ajax requests is POST so the places to put any information
   //    remains urlstring, querystring (for request), headers and the body itself
   //
   //    in the request:
   //
   //          POST urlstring?querystring HTTP/1.1
   //          Xheader1: valheader1
   //          Xheader2: valheader2
   //
   //          body (raw/html, eva, json, prop-val)
   //
   //    in the response:
   //
   //          HTTP/1.1 200 OK
   //          Xheader1: valheader1
   //          Xheader2: valheader2
   //          Content-Type: text/eva
   //          XParamsInOneLine: querystring
   //
   //          body (raw/html, eva, json, prop-val)
   //
   //    We can put all the information by ourselves using javascript and the data structure
   //    provided by jGastona and consume the response simply reading the http response object directly
   //    or let all be packed and unpacked more conveniently.
   //
   //    --------- Packing data in requests (paramCfg)
   //
   //    We can pack data using the parameter "paramCfg" in two methods AJAXSend and AJAXgetDataForId
   //
   //    The simplest way to use paramCfg is given it as string, then it acts as query string directly
   //
   //          AJAXSend ("myPost?id=1771");
   //          AJAXSend ("myPost", "id=1771");
   //          AJAXgetDataForId ("myId", "extra=yes")
   //
   //    by given it as object we can specify following properties:
   //
   //          property        type      example                  meaning
   //
   //          body            string    "my body"                if given, it is the body to send
   //          params          string    "id=myId&name=MiNombre"  querystring to send
   //          params          object    { id="myId", .. }        the querystring will be formed using encodingUTF8
   //          headers         object    { Xhead1: "hh", ... }    Headers will be send except
   //          bodyVars        array     [ "eTitle", "eDesc" ]    variables to be packed in the body, if not specified or empty
   //                                                             and no body specified then they are all variables!
   //          bodyVarsFormat  string    "eva"                    Format to use for the body: eva is default, json or propval
   //          encodeUTF8      boolean   false                    For using encoding UTF8 in params (object) and bodyVars,
   //                                                             if not explicity set to false it is always true
   //
   //
   //          Example of calls packing variables (data from JAST script and generated by jGastona)
   //
   //             AJAXSend ("myPost");                         // all variables will be packed
   //             AJAXSend ("myPost", "");                     // NO variable at all
   //             AJAXSend ("myPost", { body:"" });            // NO variable at all
   //             AJAXSend ("myPost", { bodyVars: ["one"] });  // only variable "one" will be packed
   //
   //
   //          Example of body packed as "eva"
   //
   //             #data#
   //
   //               <title>  //value of title variable
   //               <source> //a good friend
   //               <code>   //#!/usr/bin/gastona ... (UTF-8 escaped!)
   //
   //          Example of body packed as "json"
   //
   //               { "title": "value of title variable", "source": "a good friend", "code": "..." }
   //
   //          Example of body packed as "propval"
   //
   //               title: value of title variable
   //               source: a good friend
   //               code: #!/usr/bin/gastona ... (UTF-8 escaped!)
   //
   //
   //    --------- Unpacking data from responses (respFuncOrObj)
   //
   //    In following functions we can specify a parameter respFuncOrObj
   //
   //        function AJAXPostRaw    (sendStr, bodyStr, objPOSTHeader, respFuncOrObj)
   //        function AJAXSend       (postString, paramCfg, respFuncOrObj)
   //        function AJAXUploadFile (fileElement, postMsg, postHeaders, respFuncOrObj)
   //
   //    if this parameter is a function then it might take two parameter: body and response object
   //
   //    For example to consume the response by ourselves, we can pass a function like
   //
   //                function processResponse (body, respObj)
   //                {
   //                    processBody (body);
   //                    processHeaders (respObj.getAllResponseHeaders ());
   //                }
   //
   //    If we pass an object, this will be filled with properties result of unpacking
   //    the response as specified below
   //
   //    Two headers has to be set by the server for the proper unpacking "Content-Type" and "XParamInOneLine"
   //
   //    --- unpacking the body
   //
   //    Unpacking body when "Content-Type" contains "text/eva"
   //          The body will be interpreted as an anonymous eva unit and each eva variable will be
   //          converted in a property of type array of array of strings.
   //          Example body:
   //
   //             #data#
   //               <title>   //my title
   //               <list>    "Lunes", "Martes", "Miercoles"
   //               <table>   id  , name
   //                         1811, Evariste
   //                         1777, Carolo
   //          Unpacked object:
   //             {
   //                data:
   //                { title: [["my title"]],
   //                  list: [["Lunes", "Martes", "Miercoles"]],
   //                  table: [["id", "name"], ["1811", "Evariste"], ["1777", "Carolo"]]
   //                }
   //             }
   //
   //    Unpacking body when "Content-Type" contains "text/json"
   //          The body will be interpreted as an JSON object and will be unpacked using JSON.parse(bodytxt)
   //
   //    If no Conten-Type or any of the two above are found a new property "rawBody" containing
   //    the whole body as string will be created
   //
   //    --- unpacking XParamInOneLine
   //
   //    If the server sets the header XParamInOneLine then it will be interpreted as a query string type of variables
   //    that is
   //            variable=value&variable2=value2...
   //
   //    each variable-value will be set as a property-value in the response object
   //
   //    --- unpacking all headers
   //
   //    All headers will be set in the response object having the property name "header:"headerName
   //          Example headers:
   //               XMyheader : "etc"
   //               Content-Type: text/eva
   //               ...
   //
   //          { "header:XMyheader" : "etc", "header:Content-Type": "text/eva", .. }
   //
   //    --------- How to initialize and use the response object
   //
   //    While passing an object as response, it is important to notice that the properties of the unpacking
   //    process are going to be added but the object is not going to be cleared previosly, so it has to be
   //    done before the request in order not to take into account possible old response properties!
   //
   //          myResp2AHA = {},
   //          AJAXSend ("AHA", "", myResp2AHA);
   //
   //    Passing respFuncOrObj as functions, this code accomplish the same
   //
   //          myResp2AHA = {},
   //          AJAXSend ("AHA", "",
   //                      function (body, resObj) {
   //                          Object.assign (myResp2AHA, httUnpack (resObj.responseText, resObj));
   //                      });
   //
   //

   function jaxGetHttpReq ()
   {
      if (window.XMLHttpRequest)
         return new XMLHttpRequest ();
      else if (window.ActiveXObject)
         return new ActiveXObject("Microsoft.XMLHTTP");

      alert("Your browser does not support AJAX!");
   }

   function ajaxGenericPreProcessResponse (httresp)
   {
      //
   }

   //  (parameters: response body, response object)
   //
   //
   //  Example


   //  Send a general request using given method, url, body and headers
   //
   //     Example:
   //          AJAXAnyMethodRaw ("OPENSESAME", "myPost/etc?par=nothing", "this is my body", { "XHeader-A": "167", XHeader2: "Maria" });
   //
   //  If a response from the server has to be handled, the fourth parameter respFuncOrObj
   //  can be used (see respFuncOrObj responses)
   //
   function AJAXAnyMethodRaw (method, sendStr, bodyStr, objHeader, respFuncOrObj)
   {
      var httpero = jaxGetHttpReq ();
      if (!httpero) return false;

      // get the Method url minus parameters or query part, e.g. "POST blah" from "POST blah?name=Salma"
      var postTitle = sendStr.substring(0, sendStr.indexOf('?'));
      if (postTitle.length == 0)
         postTitle = sendStr;

      // add callback
      httpero.onreadystatechange = function () {
         if (httpero.readyState == 4 && httpero.status == 200) {
            ajaxGenericPreProcessResponse (httpero);

            if (typeof respFuncOrObj === "function") {
               // just call the function
               respFuncOrObj (httpero.responseText, httpero);
            }
            else if (typeof respFuncOrObj == "object") {
               // merge the unpack object of the response into the given object
               Object.assign (respFuncOrObj, httUnpack (httpero.responseText, httpero));
            }
            mensaka (AJAX_RESPONSE_MESSAGE + " " + postTitle);
         }
      }

      httpero.open (method, sendStr, true);

      if (objHeader) {
         for (var indx in objHeader)
            httpero.setRequestHeader(indx, objHeader[indx]);
      }

      httpero.send (bodyStr||"");
   }

   //  Send a general POST using given url, body and headers
   //
   //     Example:
   //          AJAXPostRaw ("myPost/et?par=nothing", "this is my body", { XHeader-A: 167, XHeader2: "Maria" });
   //
   //  If a response from the server has to be handled, the fourth parameter respFuncOrObj
   //  can be used (see respFuncOrObj responses)
   //
   function AJAXPostRaw (sendStr, bodyStr, objPOSTHeader, respFuncOrObj)
   {
      return AJAXAnyMethodRaw ("POST", sendStr, bodyStr, objPOSTHeader, respFuncOrObj);
   }

   //  Send a POST using given url, paramCfg and respFuncOrObj (see packing and unpacking HTTP messages with paramCfg and respFuncOrObj)
   //
   //     Example:
   //          AJAXPostRaw ("myPost/et?par=nothing", "this is my body", { XHeader-A: 167, XHeader2: "Maria" });
   //
   //  If a response from the server has to be handled, the fourth parameter respFuncOrObj
   //  can be used (see respFuncOrObj responses)
   //
   function AJAXSend (postString, paramCfg, respFuncOrObj)
   {
      var poso = httPack (paramCfg, dataUnit);
      AJAXPostRaw (postString + "?" + poso.onelineparams,
                poso.body,
                poso.headers,
                respFuncOrObj);
   }

   function AJAXUploadFile (fileElement, postMsg, postHeaders, respFuncOrObj)
   {
      if (!fileElement) return false;
      var fileEle = htmlElem (fileElement);

      var file1 = fileEle.files[0];
      if (! file1 || file1 === "") return false;

      var formo = new FormData ();
      formo.append ("filename", file1); // we add it but actually the mico server don't read it!

      AJAXPostRaw (postMsg + "?fileName=" + file1, formo, postHeaders, respFuncOrObj);
      return true;
   }

   function AJAXLoadRootJast (jastName, placeId)
   {
      AJAXPostRaw ("loadRootJast?jastName=" + jastName,
                   "",     // body
                   null,   // headers
                           // callback
                   function (txt) {
                      loadJast (evaFileStr2obj (txt), placeId);
                   }
                  );
   }

   // depending on the type of the second parameter there are two possible syntaxes:
   //
   //    //1 using the one line parameters
   //    AJAXgetDataForId ("myTextArea", "source=content.txt&fromLine=166&toLine=200");
   //
   //    //2 passing more headers
   //    AJAXgetDataForId ("myTextArea", { "ajaxREQ-id": "myTextArea", "theFile": "content.txt" });
   //
   //    if multiple is true then the server will send additional id:value pairs using the format
   //          id1:value
   //          id2:value
   //          :
   //          mainid value
   //
   //    if onlyhtml is true, only the html element will be updated
   //    and the data will not be set in "data" unit. This is convenient for not
   //    duplication in case of big contents. This flag only affects the main id,
   //    multiple ids will be updated using setData always.
   //
   function AJAXgetDataForId (idname, paramCfg, multiple, onlyhtml)
   {
      var poso = httPack (paramCfg, dataUnit);
      AJAXPostRaw ("getDataForId?" + "id=" + idname + "&" + poso.onelineparams,
                    poso.body,
                    poso.headers,
                    function (txt) {
                       setContentsFromBody (idname, txt, multiple, onlyhtml);
                       // AJAXAnyMethodRaw already sends the message "ajaxResponse getDataForId"
                       // here we trigger the extra message "ajaxResponse getDataForId myId"
                       // to allow reacting to the setting of a particular id
                       //
                       mensaka (AJAX_RESPONSE_MESSAGE + " getDataForId " + idname);
                    }
                );
   }

   function setContentsFromBody (idname, bodystr, multiple, onlyhtml)
   {
      var mainbody = multiple ? "": bodystr;
      if (multiple)
      {
         // format body = sub-header sub-body
         //
         // subhead1:val1
         // subhead2:val2
         // :---body
         // subbody

         var textArr = str2lineArray (bodystr);

         var hh = 0;
         while (hh < textArr.length)
         {
            var strlin = textArr[hh ++];
            if (!strlin) break;

            var idval = /([^:]*):(.*)/.exec (strlin);
            // console.log ("multi set " + idval[1] + " [" + idval[2] + "]");
            setData (idval[1], idval[2]);
         }
         mainbody = textArr.slice(hh).join ("\n");
      }

      if (onlyhtml)
      {
         var ele = document.getElementById (idname);
         if (ele)
            setValueToElement (ele, mainbody);
      }
      else setData (idname, mainbody);
   }
}