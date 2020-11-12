/*
Copyright (C) 2015-2020 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

function trassos2D ()
{
   var SVGNamespace = "http://www.w3.org/2000/svg";
   var DEFAULT_GRAPH_DX = 300;
   var DEFAULT_GRAPH_DY = 200;
   var graffitiPila = [];  // to avoid recurrent graffitis
   var TT_UNDEF     = -1;
   var TT_DEFSTYLE  = 0;
   var TT_IMAGE     = 1;
   var TT_TEXT      = 2;
   var TT_TRASS     = 3;
   var TT_RECT      = 4;
   var TT_CIRCLE    = 5;
   var TT_ELLIPSE   = 6;
   var TT_GRAFFITI  = 7;

   return {
      renderCanvasGraffitis : renderCanvasGraffitis,
      renderSvgGraffitis    : renderSvgGraffitis,
      renderClassGraffiti   : renderClassGraffiti,

      drawGraffiti2canvas   : drawGraffiti2canvas,
      trassShapeNoSyncCanvas: trassShapeNoSyncCanvas,

      drawGraffiti2svg      : drawGraffiti2svg,
      trassShape2svg        : trassShape2svg,

      autoCasteljau         : autoCasteljau,
      convertPathArrayJ2C   : convertPathArrayJ2C,

      getSVGById            : getSVGById,
      getCanvasById         : getCanvasById,
      clearCanvas           : clearCanvas,
      clearSvg              : clearSvg,
      clearGraffitiElement  : clearGraffitiElement,
   };

   function getTrassType (str)
   {
      if (str === 'z') return TT_TRASS;
      if (str === 'defstyle' || str === 'def') return TT_DEFSTYLE;
      if (str === 'rect' || str === 'rec') return TT_RECT;
      if (str === 'image' || str === 'img') return TT_IMAGE;
      if (str === 'circle' || str === 'cir') return TT_CIRCLE;
      if (str === 'ellipse' || str === 'ell') return TT_ELLIPSE;
      if (str === 'text' || str === 'txt') return TT_TEXT;
      if (str === 'graffiti' || str === 'gra' || str === 'graf') return TT_GRAFFITI;

      return TT_UNDEF;
   }

   ////////////////////
   // parse2DStyle
   ////////////////////

   function parse2DStyle (stylestr)
   {
      var reto = {};
      if (!stylestr || typeof stylestr !== "string") return reto;
      var atts = stylestr.split (';');
      if (!atts || atts.length === 0) return reto;


      var aliases = {
            op: "opacity",
            sc: "stroke",
            sw: "stroke-width",
            so: "stroke-opacity",
            sd: "stroke-dasharray",
            fc: "fill",
            fr: "fill-rule",
            fo: "fill-opacity",
            font: "font-family",
            ff: "font-family",
            fof: "font-family",
            fs: "font-size",
            fos: "font-size",
            ft: "font-type",
            fot: "font-type",
      };

      function toHex (strDeci)
      {
         var twochar = Number(strDeci).toString(16);
         return (twochar.length == 1 ? "0": "") + twochar;
      }

      for (var ii = 0; ii < atts.length; ii ++)
      {
         var atval = atts[ii].split (':');
         if (atval.length !== 2) continue;

         // name
         var name = atval[0].trim ();
         name = aliases[name] || name;

         // value

         var value = atval[1].trim ();

         // convert "+rrrgggbbb" (decimal) to "#RRGGBB" (hex) ?
         // e.g  "+255128040" to "#FF8028"
         if (value.length >= 10 && value[0] == '+' && (name == 'fill' || name == 'stroke'))
            value = "#" + toHex (value.substr(1, 3))
                        + toHex (value.substr(4, 3))
                        + toHex (value.substr(7, 3))
                        + (value.length >= 13 ? toHex (value.substr(10, 3)): "");

         reto [name] = value;
      }

      return reto;
   }

   ////////////////////
   // autoCasteljau
   ////////////////////


   // convert an array 'jau' (autocasteljau) into a c curve (cubic)
   //    example
   //    out (ta.convertPathArrayJ2C (40, 10, false, [1, 2, 7, -2]));
   //  outpts
   //    39.75,9.5,39.78275799460319,12,41,12,42.21724200539681,12,46.25,10.5,48,10
   //
   function convertPathArrayJ2C (px, py, closePath, arrJ)
   {
      var au = autoCasteljau (px, py, closePath, arrJ);
      au.computePoints ();
      return au.getArrayCasteljau ().slice(2); // slice (2) to remove position 0, 0 (why is it included ?)
   }

   function autoCasteljau (x0, y0, close, arrPtos)
   {
      // from a starting point x0, y0 and and an array of relative displacements [ xr1, yr1, xr2, yr2, ... etc ]
      // a Casteljau or Bezier curve passing by all points is returned, being the control points
      // automatically calculated.

      var arrCasteljau = [];
      var arreglo = -1.; // either automatic (-1) or an adjust value between 0 and 1
      var MISTAD = 0.5;

      return {
         x0: x0,
         y0: y0,
         arrPtos: arrPtos,
         computePoints: computeAutoCasteljau,
         getArrayCasteljau: function () { return arrCasteljau; } ,

         //move: function (indx, dx, dy) { }
         //insert:
         //remove:
         //
      };

      function computeAutoCasteljau ()
      {
         if (!arrPtos || arrPtos.length < 2) return [];

         // ctrl      c0  c1   c2  c3    c4  c5
         // ptos    0        1        2          3
         //
         var NP = 3 * (2+arrPtos.length) - 4; // final length of point plus control points
         var ctrl;
         var posa = 0;
         arrCasteljau = [];

         var prev = new vec3 ();
         var p1 = new vec3 (x0, y0);
         var p2 = new vec3 ();
         var p3 = new vec3 ();

         for (var ii = 0; ii < arrPtos.length - 3; ii += 2)
         {
            if (ii > 0)
               p1.fromArray (arrPtos, ii/2 -1 , 2);
            p2.fromArray (arrPtos, ii/2 + 0, 2);
            p3.fromArray (arrPtos, ii/2 + 1, 2);

            // all points are relative to the previous
            p1.plus (prev);
            p2.plus (p1);
            p3.plus (p2);
            prev.set (p1.x, p1.y, p1.z); // new previous

            ctrl = getControlPoints (p1, p2, p3);

            // we set the point and the two control points of the next point!
            //
            // ctrl       ?1  s2    s4
            // ptos    s0        ?3
            //
            p1.setIntoArray      (arrCasteljau, posa+0, 2);
            ctrl[0].setIntoArray (arrCasteljau, posa+2, 2);
            ctrl[1].setIntoArray (arrCasteljau, posa+4, 2);
            posa += 3;
         }

         // calculate extrem control points
         //
         if (close)
         {
            // we need two more control points, between last and first point
            // plus repeat the first point at the end! (3*2 = 6)
            NP += 6;

            // ctrl                   c0    c1   ?0    ?1  ?2     ?3
            // ptos      ...    p1       p2         p3         0
            // new p1           x-->                           p1 (new)

            // reuse p2 and p3 and change p1
            p1 = new vec3 (x0, y0);
            ctrl = getControlPoints (p2, p3, p1);  // note the order!  2-3-1

            p2.setIntoArray      (arrCasteljau, posa+0, 2);  // p2
            ctrl[0].setIntoArray (arrCasteljau, posa+2, 2);  // ?0
            p3.setIntoArray      (arrCasteljau, posa+3, 2);  // p3   last real point
            ctrl[1].setIntoArray (arrCasteljau, posa+4, 2);  // ?1

            // ctrl     c0    c1   ?0    ?1  ?2    ?3    c    c
            // ptos       --         p3         p1         p2

            // reuse p3, p1 and change p2
            p2.fromArray (arrPtos, 0, 2); // point at index 1
            p2.plus (p1); // add the point 0 since is relative to it!

            ctrl = getControlPoints (p3, p1, p2);  // note the order!  3-1-2

            ctrl[0].setIntoArray (arrCasteljau, posa+5, 2); // very last control point   (left from point at index 0)
            p1.setIntoArray      (arrCasteljau, posa+6, 2); // repeat the first point at the end
            ctrl[1].setIntoArray (arrCasteljau,     1 , 2); // very first control point  (right from point at index 0)
         }
         else
         {
            // ctrl      ?   c1   c2 c3   c4 ?
            // ptos    0        1       2      3
            //                  p1      p2     p3

            //--- very last control point
            //
            // extend "p4" = p3 + (p3-p2)
            p1 = p3.clone ();
            p1.plus (vec3FromTo (p2, p3));

            ctrl = getControlPoints (p2, p3, p1);  // note the order!  2-3-1
            p2.setIntoArray      (arrCasteljau, posa+0, 2);  // p2
            ctrl[0].setIntoArray (arrCasteljau, posa+2, 2);  // ?0
            p3.setIntoArray      (arrCasteljau, posa+3, 2);  // p3   last real point

            //--- very first control point
            //
            // get p1, p2 and extend "p-1" = p1 + (p1-p2)
            //
            p1.set (x0, y0);         // point at index 0
            p2.fromArray (arrPtos, 0, 2); // point at index 1
            p2.plus (p1); // add the point 0 since is relative to it!
            p3 = p1.clone ();
            p3.plus (vec3FromTo (p2, p1));
            ctrl = getControlPoints (p3, p1, p2);
            ctrl[0].setIntoArray (arrCasteljau,  1 , 2); // very first control point  (right from point at index 0)
         }
         if (NP !== arrCasteljau.length)
            console.log ("Error: Error calculating final array NP " + NP + " !== " + (arrCasteljau.length));

         return arrCasteljau;
      }

      // given 3 points p1, p2, p3 it returns the two cotrol points of the middle point p2 !
      //
      //          c1
      //           ...p2      c2
      //        ...     ......
      //    p1..              ..........
      //                               ....p3
      //
      // ctrl       ?  s1    s2    ?
      // ptos    p1       p2          p3
      //
      function getControlPoints (p1, p2, p3)
      {
         var cp0 = new vec3 ();
         var cp1 = new vec3 ();

         var v1 = vec3FromTo (p1, p2);
         var v2 = vec3FromTo (p3, p2);

         var n1 = v1.norm ();
         var n2 = v2.norm ();
         var fator = (n1 + n2) * (n1 + n2);

         var dir = vec3FromTo (v2, v1);
         if (fator > 0)
            dir.div (fator);

         // faco1 makes the curve very curly in edges
         var faco1 = n1 > n2 ? n1 * n1: n2 * n2;

         // faco2 makes the curve quite flat
         var faco2 = n1 * n2;

         // atomatic arreglo : the more difference in distance the more faco2, if not more faco1
         var prop = n2 > 0 ? n1 / n2: 1;
         if (arreglo < 0.) arreglo = (prop > 1. ? 1./prop: prop);

         dir.mult (MISTAD * (arreglo * faco1 + (1 - arreglo) * faco2));

         cp0.set (p2);
         cp1.set (p2);

         cp0.minus (dir);
         cp1.plus  (dir);

         return [ cp0, cp1 ];
      }
   }; // ======= end of autoCasteljau

   ///////////////////////////////////
   // trasses - bounding box and autofit (autoscale)
   ///////////////////////////////////

   function boundingBoxAndAutoScale (trasses, width, height, squareratio, marginPercent)
   {
      marginPercent = marginPercent || 0;

      // auto scale, offset and center image
      //
      var bounds = calcBoundingBox (trasses);

      // NOTE ABOUT width and height for scaling
      //
      // canvasElem.width and height from canvas itself (what we want)
      // canvasElem.clientWidth and clientHeight from graffiti class elements (final aspect)

      var scalex = width * (1 -2 * marginPercent / 100) / bounds.dx;
      var scaley = height * (1 -2 * marginPercent / 100) / bounds.dy;
      var extrax = 0;
      var extray = 0;
      if (squareratio)
      {
         if (scalex < scaley) {
            extray = (0.5 * (scaley-scalex) * bounds.dy) / scalex;
            scaley = scalex;
         }
         else {
            extrax = (0.5 * (scalex-scaley) * bounds.dx) / scaley;
            scalex = scaley;
         }
      }

      return {
         scalex: scalex,
         scaley: scaley,
         offsetx: extrax - bounds.x + (marginPercent / 100) * bounds.dx,
         offsety: extray - bounds.y + (marginPercent / 100) * bounds.dy,
      };
      // center ? this don't work ...
      //c2d.translate (canvasElem.clientWidth / (2*scalex) - bounds.x0 + bounds.dx/2,
      //               canvasElem.clientHeight / (2*scaley) - bounds.y0 + bounds.dy/2;
   }

   function calcBoundingBox (trasses)
   {
      var DEF_SIZE_IMG = 100;
      var DEF_SIZE_CHAR = 25;
      var xx, yy, ii;
      var x0 = null, y0, x1, y1;

      function computePair (x, y) {
         if (x0 == null)
         {
            x0 = x;
            y0 = y;
            x1 = x+1;
            y1 = y+1;
            return;
         }
         if (x < x0) x0 = x;
         if (x > x1) x1 = x+1;   // avoid dx == 0
         if (y < y0) y0 = y;
         if (y > y1) y1 = y+1;   // avoid dy == 0
      }

      function computeTrassa (trass) {
         xx = +(trass[1]);
         yy = +(trass[2]);
         computePair (xx, yy);
         for (ii = 5; ii+1 < trass.length; ii += 2)
         {
            xx += +(trass[ii]);
            yy += +(trass[ii+1]);
            computePair (xx, yy);
         }
      }

      function computeSquare (x, y, dx, dy) {
         computePair (x, y);
         computePair (x+dx, y+dy);
      }

      function computeRect (trass) {
         computeSquare (+(trass[1]), +(trass[2]), +(trass[4]), +(trass[5]));
      }

      function computeCircle (trass) {
         computeSquare (+(trass[1])-(trass[4]), +(trass[2])-(trass[4]), +(trass[4])*2, +(trass[4])*2);
      }

      function computeEllipse (trass) {
         computeSquare (+(trass[1])-(trass[4]), +(trass[2])-(trass[5]), +(trass[4])*2, +(trass[5])*2);
      }

      function computeText (trass) {
         computeSquare (+(trass[1]), +(trass[2]), trass[4].length * DEF_SIZE_CHAR, DEF_SIZE_CHAR);
      }

      for (var tt in trasses) {
         switch (getTrassType (trasses[tt][0]))
         {
            case TT_TRASS:   computeTrassa  (trasses[tt]); break;
            case TT_RECT:    computeRect    (trasses[tt]); break;
            case TT_CIRCLE:  computeCircle  (trasses[tt]); break;
            case TT_ELLIPSE: computeEllipse (trasses[tt]); break;
            case TT_TEXT:    computeText    (trasses[tt]); break;
            case TT_IMAGE:
               // ----------- don't know how to calculate it now ...
               // computeSquare (+(trasses[1]), +(trasses[2]), DEF_SIZE_IMG, DEF_SIZE_IMG);
               break;
            case TT_GRAFFITI:
               // ----------- don't know how to calculate it now ...
               // computeSquare (+(trasses[1]), +(trasses[2]), DEF_SIZE_IMG, DEF_SIZE_IMG);
               break;
            default: break;
         }
      }

      return { x: x0, y: y0, dx: (x1-x0), dy: (y1-y0) };
   }


   ///////////////////////////////////
   // trassos - repetition
   ///////////////////////////////////


   // repetition using parametric positions
   //
   function bucleParRep (trass, estils, dibuixafunc)
   {
      var xx = +(trass[1]);
      var yy = +(trass[2]);

      dibuixafunc (xx, yy);

      // example style with parametric repetition
      //    "repeatPosN=2;funcPosX=0;funcPosY=Math.sin(t)"
      var nPar = estils["repeatPosN"];

      if (nPar > 0)
      {
         var paramX = estils["funcPosX"]||0;
         var paramY = estils["funcPosY"]||0;

         for (var tt = 1; tt <= nPar; tt ++)
         {
            // either t or n can be used as iterator variable
            xx = eval ("var t = n = " + tt + "; " + paramX);
            yy = eval ("var t = n = " + tt + "; " + paramY);

            dibuixafunc (xx, yy);
         }
      }
   }


   ///////////////////////////////////
   // trasses - graffiti 2 canvas
   ///////////////////////////////////

   function clearCanvas (canvas)
   {
      if (canvas.getContext)
         canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height);
   }


   // keep this method not using canvasSync for compatibility with the function
   // editablePaths::buildJavaScriptCode with optimized = true (using trassos2D
   //
   function trassShapeNoSyncCanvas (c2d, form, px, py, pathStyle, closep, arrp)
   {
      var relative = true;
      var xx = +(px), yy = +(py);

      c2d.beginPath();
      c2d.moveTo(px, py);

      if (form === "jau") {
         var curv = autoCasteljau (+(px), +(py), closep, arrp);
         curv.computePoints ();
         var cc = curv.getArrayCasteljau ();

         for (var ii = 2; ii+5 < cc.length; ii += 6)
            c2d.bezierCurveTo (cc[ii], cc[ii+1], cc[ii+2], cc[ii+3], cc[ii+4], cc[ii+5]);
      }
      else {
         for (var ii = 0; ii < arrp.length; ii += 2)
         {
            var plusx = relative ? xx : 0;
            var plusy = relative ? yy : 0;

            if (form === "pol") {
               xx = +(arrp[ii+0]) + plusx;
               yy = +(arrp[ii+1]) + plusy;

               c2d.lineTo (xx, yy);
            }
            else if (form == "qua") {
               xx = +(arrp[ii+2]) + plusx;
               yy = +(arrp[ii+3]) + plusy;
               c2d.quadraticCurveTo (+(arrp[ii]) + plusx, +(arrp[ii+1]) + plusy, xx, yy);
               ii += 2;
            }
            else if (form == "cub" || form == "bez") {
               xx = +(arrp[ii+4]) + plusx;
               yy = +(arrp[ii+5]) + plusy;
               c2d.bezierCurveTo (+(arrp[ii]) + plusx, +(arrp[ii+1]) + plusy, +(arrp[ii+2]) + plusx, +(arrp[ii+3]) + plusy, xx, yy);
               ii += 4;
            }
            else
            {
               console.log ("ERROR: unknow form " + form + " calling trassShapeNoSyncCanvas!");
               break; // error!
            }
         }
         if (closep)
            c2d.closePath();
      }
   }


   function drawGraffiti2canvas (atrass, canv, props)
   {
      var canvSync = canvasSync (canv.getContext("2d"));

      // sample atrass:
      //
      //    "defstyle", "red", "sc:#AA1010"
      //    "z", 10, 10, "red"       , "pol", 40, 0, 10, -50, -66, 30
      //    "z", 10, 10, "fc:#990122", "jau", 40, 0, 10, -50, -66, 30
      //    "z", 10, 10, "fc:#990122", "jau", 40, 0, 10, -50, -66, 30

      function makestyle (a) { return a; }

      var styleAliases = {};

      var applyprop = props || { autofit: true, squareratio: true };
      if (applyprop.autofit)
      {
         applyprop = boundingBoxAndAutoScale (atrass, canv.width, canv.height, applyprop.squareratio);
      }
      var hasScaleAndOffsets = "scalex" in applyprop;

      // declareImage has to be called out of instructions included within addRender
      //
      for (var rr in atrass)
      {
         var lotrass = atrass[rr];
         if (lotrass && lotrass.length > 4 && lotrass[0] === "img")
            canvSync.declareImage (lotrass[4]);
      }

      canvSync.addRender (function () {
         var thas = this;
         var cctx = this.ctx;

         // inner function 1
         function applyCanvasStyle (estils)
         {
            var oldWidth;
            var oldLineDash;

            if ("fill" in estils)
            {
               if (estils["fill"] !== "none")
               {
                  cctx.fillStyle = estils["fill"];
                  cctx.fill ();
               }
            }
            if ("stroke-width" in estils)
            {
               oldWidth = cctx.lineWidth;
               cctx.lineWidth = estils["stroke-width"];
            }
            if ("stroke-dasharray" in estils)
            {
               cctx.setLineDash (eval ("[" + estils["stroke-dasharray"] + "]"));
            }
            cctx.strokeStyle = ("stroke" in estils) ? estils["stroke"]: "#000000";
            cctx.stroke ();

            if (oldWidth !== undefined)
               cctx.lineWidth = oldWidth;
            if (oldLineDash !== undefined)
               cctx.setLineDash ([]);
         }

         // inner function 2
         function trassShape2canvas (form, px, py, closep, arrp)
         {
            var relative = true;
            var xx = +(px), yy = +(py);

            cctx.beginPath();
            cctx.moveTo(px, py);

            if (form === "jau") {
               var curv = autoCasteljau (+(px), +(py), closep, arrp);
               curv.computePoints ();
               var cc = curv.getArrayCasteljau ();

               for (var ii = 2; ii+5 < cc.length; ii += 6)
                  cctx.bezierCurveTo (cc[ii], cc[ii+1], cc[ii+2], cc[ii+3], cc[ii+4], cc[ii+5]);
            }
            else {
               for (var ii = 0; ii < arrp.length; ii += 2)
               {
                  var plusx = relative ? xx : 0;
                  var plusy = relative ? yy : 0;

                  if (form === "pol") {
                     xx = +(arrp[ii+0]) + plusx;
                     yy = +(arrp[ii+1]) + plusy;

                     cctx.lineTo (xx, yy);
                  }
                  else if (form == "qua") {
                     xx = +(arrp[ii+2]) + plusx;
                     yy = +(arrp[ii+3]) + plusy;
                     cctx.quadraticCurveTo (+(arrp[ii]) + plusx, +(arrp[ii+1]) + plusy, xx, yy);
                     ii += 2;
                  }
                  else if (form == "cub" || form == "bez") {
                     xx = +(arrp[ii+4]) + plusx;
                     yy = +(arrp[ii+5]) + plusy;
                     cctx.bezierCurveTo (+(arrp[ii]) + plusx, +(arrp[ii+1]) + plusy, +(arrp[ii+2]) + plusx, +(arrp[ii+3]) + plusy, xx, yy);
                     ii += 4;
                  }
                  else
                  {
                     console.log ("ERROR: unknow form " + form + " calling trassShape2canvas!");
                     break; // error!
                  }
               }
               if (closep)
                  cctx.closePath();
            }
         }
         //
         // end of inner functions

         var escalax;
         var escalay;

         if (hasScaleAndOffsets)
         {
            escalax = applyprop.scalex;
            escalay = applyprop.scaley;
            cctx.save ();
            cctx.lineWidth = 1.0 / applyprop.scalex; // compensate the scale with stroke
            cctx.scale (escalax, escalay);
            cctx.translate (applyprop.offsetx, applyprop.offsety);
         }

         // ---------------------
         // set all paths (canvas)
         //
         for (var rr in atrass)
         {
            var lotrass = atrass[rr];
            if (!lotrass || lotrass.length < 3) continue;

            // parse all styles either from aliases or directly
            var styleArr = parse2DStyle (styleAliases [lotrass[3]]|| lotrass[3]);

            switch (getTrassType (lotrass[0]))
            {
               case TT_DEFSTYLE:
                  styleAliases [lotrass[1]] = makestyle (lotrass[2]);
                  break;

               case TT_IMAGE:
               // e.g.   [ "img" ,238, 121, "scale=1.;opacity=1.",  "wakeupthecat.png" ],
                  bucleParRep (lotrass, styleArr,
                               function (xp, yp)
                               {
                                  thas.renderImage (lotrass[4], xp, yp);
                               });
                  break;

               case TT_TEXT:
                  if ("font-family" in styleArr)
               {
                  // style ctx.font = "12px Arial"
                  //
                     if ("font-size" in styleArr)
                          cctx.font = styleArr["font-size"] + "px " + styleArr["font-family"];
                     else cctx.font = styleArr["font-family"];
               }

                  bucleParRep (lotrass, styleArr,
                       function (xp, yp)
               {
                           if ("fill" in styleArr)
                           {
                              cctx.fillStyle = styleArr["fill"];
                              cctx.fillText (lotrass[4], xp, yp);
               }
                           if ("stroke" in styleArr)
               {
                              cctx.strokeStyle = styleArr["stroke"];
                              cctx.strokeText (lotrass[4], xp, yp);
               }
                       });
                  break;

               case TT_TRASS:
                  if (lotrass.length >= 6)
                  {
               // 0   1    2     3      4  5...
               // z ,238, 121, "pel", jau, 84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81
               // trassShape2canvas (c2d, form, px, py, style, closep, arrp)
                     bucleParRep (lotrass, styleArr,
                          function (xp, yp)
                          {
               trassShape2canvas (
                           lotrass[4].substring (0, 3),              // type ("pol" "jau" etc)
                                          xp, yp,
                           lotrass[4].length > 3 && lotrass[4].substring(3) === 'z', // is closed ?
                           lotrass.slice (5));
                              applyCanvasStyle (styleArr);
                          });
            }
                  break;

               case TT_RECT:
                  {
                     bucleParRep (lotrass, styleArr,
                          function (xp, yp)
                          {
                              cctx.beginPath();
                              cctx.rect (xp, yp, +(lotrass[4]), +(lotrass[5]));
                              applyCanvasStyle (styleArr);
                          });
                  }
                  break;

               case TT_CIRCLE:
                  bucleParRep (lotrass, styleArr,
                       function (xp, yp)
                       {
                          cctx.beginPath();
                          cctx.ellipse (xp, yp, lotrass[4], lotrass[4], 0, 2 * Math.PI, 0);
                          applyCanvasStyle (styleArr);
                       });
                  break;

               case TT_ELLIPSE:
               // void ctx.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise)
                  bucleParRep (lotrass, styleArr,
                       function (xp, yp)
                       {
                          cctx.beginPath();
                          cctx.ellipse (xp, yp, lotrass[4], lotrass[5], 0, 2 * Math.PI, 0);
                          applyCanvasStyle (styleArr);
                       });
                  break;

               case TT_GRAFFITI:
                  // console.log ("Error: element graffiti not implemented in canvas!");
                  
                  var graffitiName = lotrass[3];
                  var enPila = graffitiPila.indexOf (graffitiName) > -1;

                  // "graf", 50, 50, Caballar, 100
                  var graf = restData ? restData[graffitiName]: null;
                  if (graf && !enPila)
                  {
                     bucleParRep (lotrass, styleArr,
                                  function (xp, yp)
                                  {
                                     var gelo = document.createElementNS (SVGNamespace, "svg");
                                     gelo.setAttribute("width",  lotrass[4] +  "px");
                                     gelo.setAttribute("height", lotrass[5]  +  "px");
                                     gelo.setAttribute("x", xp +  "px");
                                     gelo.setAttribute("y", yp +  "px");

                                     // enter recursive call
                                     graffitiPila.push (graffitiName);
                                     drawGraffiti2canvas (graf, gelo, null, restData);
                                     graffitiPila.slice (-1, 1); // pop

                                     gaga.appendChild (gelo);
                                  });
                  }
                  break;

               default:
                  break;
            }
         }

         if (hasScaleAndOffsets)
            cctx.restore ();
      });

      canvSync.renderAll ();
   }

   ///////////////////////////////////
   // atrass - graffiti 2 svg
   ///////////////////////////////////

   function trassText2svg (svgEle, px, py, estils, textContent)
   {
      var pato = document.createElementNS (SVGNamespace, "text");

      pato.setAttribute ("x", +(px));
      pato.setAttribute ("y", +(py));
      pato.textContent = textContent;

      for (var ee in estils)
         pato.setAttribute (ee, estils[ee]);
      if (!("stroke" in estils))
         pato.setAttribute ("stroke", "#000000");

      svgEle.appendChild (pato);
   }

   function trassImage2svg (svgEle, px, py, estils, imageSource)
   {
      //<svg>
      //
      //   <image x="20" y="20" width="256" height="256" opacity="0.5" href="tiles/14/7813/5985.png" />
      //   <image x="236" y="20" width="256" height="256" href="tiles/13/3906/2992.png" />
      //
      //</svg>
      var pato = document.createElementNS (SVGNamespace, "image");
      pato.setAttribute ("x", +(px));
      pato.setAttribute ("y", +(py));
      pato.setAttribute ("href", imageSource);

      for (var ee in estils)
         pato.setAttribute (ee, estils[ee]);

      svgEle.appendChild (pato);
   }

   function createSvgElement (svgtype, estils)
   {
      var pato = document.createElementNS (SVGNamespace, svgtype);

      for (var ee in estils)
         pato.setAttribute (ee, estils[ee]);
      if (!("stroke" in estils))
         pato.setAttribute ("stroke", "#000000");

     return pato;
   }

   function trassShape2svg (svgEle, forma, px, py, estilArr, closep, arrp)
   {
      //<svg height="210" width="400">
      //   <path d="M150 0 L75 200 L225 200 Z" />
      //</svg>

     // <path d="M 100 350 q 150 -300 300 0" stroke="blue"  stroke-width="5" fill="none" />
      var pato = createSvgElement ("path", estilArr);

      var dstr = [ "M " + px + " " + py + " " ];

      if (forma === "jau") {
         var curv = autoCasteljau (+(px), +(py), closep, arrp);
         curv.computePoints ();
         var cc = curv.getArrayCasteljau ();

         dstr.push (" C ");
         for (var ii = 2; ii+5 < cc.length; ii += 6)
            dstr.push (cc[ii] + " " + cc[ii+1] + " " + cc[ii+2] + " " + cc[ii+3] + " " + cc[ii+4] + " " + cc[ii+5] + " ");
      }
      else {
         if (forma === "pol")
            dstr.push (" l ");
         else if (forma == "qua")
            dstr.push (" q ");
         else if (forma == "cub" || forma == "bez")
            dstr.push (" c ");

         for (var ii = 0; ii < arrp.length; /**/)
         {
            if (forma === "pol") {
               dstr.push (arrp[ii] + " " + arrp[ii+1] + " ");
               ii += 2;
            }
            else if (forma == "qua") {
               dstr.push (arrp[ii] + " " + arrp[ii+1] + " " + arrp[ii+2] + " " + arrp[ii+3] + " ");
               ii += 4;
            }
            else if (forma == "cub" || forma == "bez") {
               dstr.push (arrp[ii] + " " + arrp[ii+1] + " " + arrp[ii+2] + " " + arrp[ii+3] + " " + arrp[ii+4] + " " + arrp[ii+5] + " ");
               ii += 6;
            }
            else
            {
               console.log ("ERROR: unknow form " + forma + " calling trassShape!");
               break; // error!
            }
         }
         if (closep)
            dstr.push (" Z");
      }

      pato.setAttribute ("d", dstr.join (""));
      svgEle.appendChild (pato);
   }

   function drawGraffiti2svg (atrass, svgElem, props, restData)
   {
      function makestyle (a) { return a; }

      var styleAliases = {};

      // ---------------------
      // add parent element "g" basically for auto-fit transformations
      //
      var gaga = document.createElementNS (SVGNamespace, "g");

      var applyprop = props || { autofit: true, squareratio: true };
      if (applyprop.autofit)
      {
         // example transform:
         //      <g transform="translate(1, 1) scale(2, 2)  rotate(45)"><path>...</path></g>
         var wi = parseInt (svgElem.style.width || DEFAULT_GRAPH_DX);
         var hi = parseInt (svgElem.style.height || DEFAULT_GRAPH_DY);

         // special SVG issue with SVGLength ... change it if you know something about this stupid thing
         //
         if (svgElem.width && svgElem.width.baseVal) wi = parseInt (svgElem.width.baseVal.value);
         if (svgElem.height && svgElem.height.baseVal) hi = parseInt (svgElem.height.baseVal.value);

         applyprop = boundingBoxAndAutoScale (atrass, wi, hi, applyprop.squareratio);
      }

      // apply scales and offsets
      //
      if ("scalex" in applyprop)
      {
         gaga.setAttribute ("stroke-width", "" + (1.0 / applyprop.scalex));
         gaga.setAttribute ("transform",
                            " scale     (" + applyprop.scalex + ", " + applyprop.scaley + ")" +
                            " translate (" + applyprop.offsetx + ", " + applyprop.offsety + ")");
      }
      svgElem.appendChild (gaga);

      // ---------------------
      // set all paths (svg)
      //
      for (var rr in atrass)
      {
         var lotrass = atrass[rr];
         if (!lotrass || lotrass.length < 3) continue;

         // parse all styles either from aliases or directly
         var styleArr = parse2DStyle (styleAliases [lotrass[3]]|| lotrass[3]);

         switch (getTrassType (lotrass[0]))
            {
            case TT_DEFSTYLE:
               styleAliases [lotrass[1]] = makestyle (lotrass[2]);
               break;

            case TT_IMAGE:
            //    [ "img" ,238, 121, "scale=1.;opacity=1.",  "logas.png" ],
               bucleParRep (lotrass, styleArr,
                            function (xp, yp) {
                               trassImage2svg (gaga, xp, yp, styleArr, lotrass[4]);
                            });
               break;

            case TT_TEXT:
            //    [ "text" ,238, 121, "",  "pericollosso" ],
               bucleParRep (lotrass, styleArr,
                            function (xp, yp) {
                              trassText2svg (gaga, xp, yp, styleArr, lotrass[4]);
                            });
               break;

            case TT_TRASS:
               if (lotrass.length >= 6)
               {
                  // 0   1    2     3      4  5...
                  // z ,238, 121, "pel", jau, 84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81
                  // trassShape2svg (gaga, form, px, py, styleStr, closep, arrp)
                  bucleParRep (lotrass, styleArr,
                               function (xp, yp)
                               {
                  trassShape2svg (gaga,
                              lotrass[4].substring (0, 3),
                                                xp,
                                                yp,
                                                styleArr,
                              lotrass[4].length > 3 && lotrass[4].substring(3) == 'z',
                              lotrass.slice (5));
                               });
         }
               break;

            case TT_RECT:
            //    [ "rect" ,238, 121, "",  dx, dy, rx, ry ],

               bucleParRep (lotrass, styleArr,
                            function (xp, yp)
                            {
                               let pato = createSvgElement ("rect", styleArr);
                               pato.setAttribute ("x", xp);
                               pato.setAttribute ("y", yp);
            pato.setAttribute ("width",  lotrass[4]);
            pato.setAttribute ("height", lotrass[5]);
            pato.setAttribute ("rx", lotrass[6]||0);
            pato.setAttribute ("ry", lotrass[7]||0);
            gaga.appendChild (pato);
                            });
               break;

            case TT_CIRCLE:
            //    [ "circle" ,238, 121, "",  dx, dy, rx, ry ],
            // <rect x="50" y="20" rx="20" ry="20" width="150" height="150"
               bucleParRep (lotrass, styleArr,
                            function (xp, yp)
                            {
                               var pato = createSvgElement ("circle", styleArr);
                               pato.setAttribute ("cx", xp);
                               pato.setAttribute ("cy", yp);
            pato.setAttribute ("r", lotrass[4]);
            gaga.appendChild (pato);
                            }
                           );
               break;

            case TT_ELLIPSE:
            //    [ "circle" ,238, 121, "",  rx, ry ],
               bucleParRep (lotrass, styleArr,
                            function (xp, yp)
                            {
                               var pato = createSvgElement ("ellipse", styleArr);
                               pato.setAttribute ("cx", xp);
                               pato.setAttribute ("cy", yp);
            pato.setAttribute ("rx", lotrass[4]);
            pato.setAttribute ("ry", lotrass[5]);
            gaga.appendChild (pato);
                            });
               break;

            case TT_GRAFFITI:
               var graffitiName = lotrass[3];
               var enPila = graffitiPila.indexOf (graffitiName) > -1;

               // "graf", 50, 50, Caballar, 100
               var graf = restData ? restData[graffitiName]: null;
               if (graf && !enPila)
               {
                  bucleParRep (lotrass, styleArr,
                               function (xp, yp)
                               {
                  var gelo = document.createElementNS (SVGNamespace, "svg");
                  gelo.setAttribute("width",  lotrass[4] +  "px");
                  gelo.setAttribute("height", lotrass[5]  +  "px");
                                  gelo.setAttribute("x", xp +  "px");
                                  gelo.setAttribute("y", yp +  "px");

                  // enter recursive call
                  graffitiPila.push (graffitiName);
                  drawGraffiti2svg (graf, gelo, null, restData);
                  graffitiPila.slice (-1, 1); // pop

                  gaga.appendChild (gelo);
                               });
         }
               break;

            default:
               break;
         }
      }
   }

   // go through all elements (divs) of class "graffiti", gets its ids
   // and if the data <"id" graffiti> is found then
   // set an svg or a canvas if svg is not supported and draw the graffiti
   //
   function renderClassGraffiti (uData, scalesAndOffsets, dirty)
   {
      var supportSVG = !!window.SVGSVGElement;
      // supportSVG = false;

      // render all canvas graffitis
      //
      //NOT! var arr = document.getElementsByClassName ("graffiti");
      var arr = [].slice.call(document.getElementsByClassName('graffiti'), 0);
      for (var indx in arr) {
         var grafo = arr[indx].id + " graffiti";
         if (!uData [grafo]) continue;

         var styW = parseInt(arr[indx].style.width||DEFAULT_GRAPH_DX);
         var styH = parseInt(arr[indx].style.height||DEFAULT_GRAPH_DY);

         // create new html element svg or canvas
         // NOTE!!! create svg with special NS method!!
         //
         var gele = (supportSVG) ? document.createElementNS (SVGNamespace, "svg"):
                               document.createElement ("canvas");

         gele.setAttribute("width", styW +  "px");
         gele.setAttribute("height", styH +  "px");

         if (supportSVG)
         {
            if (!dirty) clearSvg (gele);
            drawGraffiti2svg (uData [grafo], gele, scalesAndOffsets, uData);
         }
         else
         {
            if (!dirty) clearCanvas (gele);
            drawGraffiti2canvas (uData [grafo], gele, scalesAndOffsets);
         }

         // remove previous if any and add the new one
         while (arr[indx].hasChildNodes())
            arr[indx].removeChild(arr[indx].firstChild);

         arr[indx].appendChild (gele);
      }
   }

   function getCanvasById (canvasId)
   {
      var arr = [].slice.call(document.getElementsByTagName('canvas'), 0);
      for (var indx in arr)
         if (arr[indx].id === canvasId)
            return arr[indx];
      return null;
   }

   function renderCanvasGraffitis (uData, scalesAndOffsets)
   {
      // render all canvas graffitis
      //
      var arr = [].slice.call(document.getElementsByTagName('canvas'), 0);
      for (var indx in arr) {
         var grafo = arr[indx].id + " graffiti";
         if (uData [grafo])
            drawGraffiti2canvas (uData [grafo], arr[indx], scalesAndOffsets);
      }
   }

   function getSVGById (svgId)
   {
      var arr = [].slice.call(document.getElementsByTagNameNS (SVGNamespace, "svg"));
      for (var indx in arr)
         if (arr[indx].id === svgId)
            return arr[indx];

      return null;
   }

   function renderSvgGraffitis (uData, scalesAndOffsets)
   {
      // render all svg graffitis
      //
      var arr = [].slice.call(document.getElementsByTagNameNS (SVGNamespace, "svg"));
      for (var indx in arr)
      {
         clearSvg (arr[indx]);
         var grafo = arr[indx].id + " graffiti";
         if (uData [grafo])
            drawGraffiti2svg (uData [grafo], arr[indx], scalesAndOffsets, uData);
      }
   }

   function clearSvg (svgelem)
   {
      while (svgelem.lastChild)
          svgelem.removeChild(svgelem.lastChild);
      }

   function clearGraffitiElement (gelem)
   {
      if (gelem == null) return;
      // both methods are safe for canvas and svg element
      clearCanvas (gelem);
      clearSvg (svgelem);
   }
}
