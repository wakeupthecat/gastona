/*
Copyright (C) 2015-2109 Alejandro Xalabarder Aulet
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
   };

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
         getLengthInRange  : getLengthInRange,   // (totalExtra, indexfrom, indexto) returns the length of the items in the range
         countItems        : function () { return regla.length; } // number of items of the header
      };

      function HeaderItem (headT)
      {
         var type = MAXMIN, extraPercent = 0, len = 0;

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
         };
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
         for (ii in regla) if (regla.hasOwnProperty (ii))
         {
            ele = regla[ii];
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
                                parseInt (layInfo [0][3]) || 0); // gap X
      headRows    = HeaderLine (parseInt (layInfo [0][2]) || 0,  // margin Y
                                parseInt (layInfo [0][4]) || 0); // gap Y

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