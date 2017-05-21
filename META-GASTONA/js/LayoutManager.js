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
      maskMap[masked] = undefined;
      changeInWidgets = true;
      invalidateAll ();
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
      // now that we have collect all layout names
      // go again through all layouts and find the inner elements (widgets or layout references)
      //
      var eva, rr, maxrr, cc, maxcc, ele;
      //... for (lay in layoutList)
      for (lay in guiLayouts)
      {
         eva = guiLayouts[lay];
         maxrr = eva.length;
         for (var rr = 2; rr < maxrr; rr ++)
         {
            maxcc = eva[rr].length;
            for (var cc = 1; cc < maxcc; cc ++)
            {
               ele = eva[rr][cc];
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
