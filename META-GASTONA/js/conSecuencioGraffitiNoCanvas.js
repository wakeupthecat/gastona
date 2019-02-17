/**
 * conSecuencioCanvas - convert a simple sequence diagram to a "graffiti"
 * Copyright (c) 2016,2017,2018 Alejandro Xalabarder
 */

// c2d Canvas from canv.getContext('2d');
//
function conSecuencioGraffitiNoCanvas (diagData, pCharSize)
{
   var graff = [];

   pCharSize = pCharSize|| 5;

   function getAnInt (obj, defval)
   {
      if (!obj) return defval;
      if (typeof obj === "number") return parseInt(obj);
      return parseInt (obj[0][0]);
   }

   var arr = diagData["sequenceTable"];
   var dia = getAnInt (diagData["distanceAgents"], 30);
   var dit = getAnInt (diagData["distanceTimeUnit"], 4);
   var maxGapTime = getAnInt (diagData["maxGapTime"], 3);
   var autoElapsed = diagData["autoElapsed"];
   if (typeof autoElapsed === "string")
      autoElapsed = (autoElapsed === "1" || autoElapsed === "true");
   autoElapsed = autoElapsed !== false;

   // multipliers to convert from char space to pixels
   //
   dia *= 12;
   dit *= 12;
   maxGapTime *= 3;

   if (arr.length == 0) return ;

   // expected schema of sequenceTable (columns):  time, source, target, message
   //
   var iTimeStamp = arr[0].indexOf ("time");
   var iAgentTx   = arr[0].indexOf ("source");
   var iAgentRx   = arr[0].indexOf ("target");
   var iMessage   = arr[0].indexOf ("message");

   var agents = getAgents(arr, iAgentTx, iAgentRx);
   var timesIda = {}; // track elapsed times (or try to)

   function hasTime ()
   {
      return iTimeStamp != -1;
   }

   function getAgents (mats, tx, rx)
   {
      var agArr = [];
      var siz = mats.length;
      if (siz < 2) return agArr;

      if (rx === -1 || tx === -1) return agArr;

      for (var ii in mats)
      {
         if (ii === "0") continue;

         if (tx != -1 && mats[ii][tx] && agArr.indexOf (mats[ii][tx]) == -1)
            agArr.push (mats[ii][tx]);
         if (rx != -1 && mats[ii][rx] && agArr.indexOf (mats[ii][rx]) == -1)
            agArr.push (mats[ii][rx]);
      }

      return agArr;
   }

   function getElapsed (tnow, indxFrom, indxTo, /**/ telapsed)
   {
      if (!autoElapsed) return "";

      telapsed = timesIda["" + indxTo + "/" + indxFrom];
      delete timesIda["" + indxFrom + "/" + indxTo];

      if (telapsed)
      {
         telapsed = "(" + Math.round ((tnow - telapsed) * 1000)/1000 + " s)";
      }
      else timesIda["" + indxFrom + "/" + indxTo] = tnow;
      return telapsed;
   }

   function textWidth (txt)
   {
      return ("" + txt).length * pCharSize;
   }

   var PIX_CHAR = Math.round (textWidth ("X")); // suppose width == height for X
   var DIM_PTA = Math.round (PIX_CHAR * .8);
   var DIM_PTAx2 = 2 * DIM_PTA;
   var DIM_PTAx4 = 4 * DIM_PTA;

   dia = dia || (PIX_CHAR * 30);  // horizontal distance between agents
   dit = dit || (PIX_CHAR * 3);  // vertical distance between time units (i.e. seconds)


   function flecha (x0, x1, y0, L2R, text)
   {
      var xpunt = Math.round (L2R ? x1: x0);
      var xbase = Math.round (xpunt + (L2R ? - DIM_PTAx2: + DIM_PTAx2));
      var xtext = Math.round (xbase + (L2R ? - 3 * DIM_PTAx2 - textWidth (text): +3 * DIM_PTAx2));
      var xend  = Math.round (L2R ? x0: x1);

      // line from end to base   ---------------| >
      graff.push ([ "z", xend, y0, "", "pol", xbase-xend, 0]);

      // arrow    | >
      graff.push ([ "z", xbase, y0 - DIM_PTA, "", "pol", 0, 2*DIM_PTA, xpunt-xbase, - DIM_PTA, xbase-xpunt, -DIM_PTA]);

      if (text && text.length > 0)
      {
         // text background
         graff.push ([ "rect", xtext - DIM_PTA, y0 - DIM_PTAx2, "fc:#EEEEEE",
                       DIM_PTA + DIM_PTAx2 + textWidth (text),
                       DIM_PTAx2 * 2
                     ]);

         // text itself
         graff.push ([ "text", xtext, y0 + DIM_PTA, "fc:#000000", text ]);
      }
   }

   var timeLine = dia * .50;
   var x0, y0 = 2*DIM_PTAx4, x1, y1 = y0;
   var currTim = 0.;
   var dtFin = dit * 2 * arr.length;

   if (hasTime ())
   {
      currTim = arr[1][iTimeStamp];
      dtFin   = Math.min (dit * (arr[arr.length-1][iTimeStamp] - currTim), (arr.length-1) * maxGapTime * dit);
      //dtFin   = dit * (arr[arr.length-1][iTimeStamp] - currTim);
   }

   // draw all agents and their columns
   //
   for (var ag = 0, x0 = timeLine; ag < agents.length; ag ++, x0 += dia)
   {
      graff.push ([ "z", Math.round (x0)            , y0, "", "pol", 0, dtFin - y0]);
      graff.push ([ "z", Math.round (x0 - dia * 0.2), y0, "", "pol", dia*0.4, 0]);
      graff.push ([ "text", Math.round (x0 - textWidth (agents[ag]) * 0.5), y0 - PIX_CHAR, "fc:#000000", agents[ag] ]);
   }

   y1 += dit;

   var ti, a1, a2, tx, dirLR, dimFlex = DIM_PTA;
   var lapsos = [];

   for (var aa in arr)
   {
      if (aa === "0") continue;

      ti = hasTime () ? arr[aa][iTimeStamp]: parseInt (aa);
      a1 = agents.indexOf (arr[aa][iAgentTx]);
      a2 = agents.indexOf (arr[aa][iAgentRx]);
      tx = arr[aa][iMessage];

      if (tx === undefined) continue;

      dirLR = a1 < a2;

      // add time gap
      //
      if (dit > 0 && ti >= 0)
      {
         if (!hasTime () || (ti - currTim) < maxGapTime)
            y1 += Math.round (Math.max(DIM_PTAx2 + DIM_PTAx4, (ti - currTim) * dit));
         else
         {
            // draw this later to cover agent lines
            lapsos[lapsos.length] = { posy: y1, text: "... " + Math.round (ti - currTim) + " s ..." };

            y1 += Math.round (3 * DIM_PTAx2 + dit);
         }
         currTim = ti;
      }

      if (hasTime () && currTim > 0)
      {
         x0 = Math.round (timeLine - 3 * DIM_PTA - textWidth (currTim));

         graff.push ([ "text", x0, y1 + DIM_PTA, "fc:#000000", currTim ]);
      }

      x0 = Math.round (timeLine + (dirLR ? a1: a2) * dia);
      x1 = Math.round (timeLine + (dirLR ? a2: a1) * dia);

      if (dirLR)
           graff.push ([ "text", x0 + DIM_PTAx2, y1 - DIM_PTA, "fc:#000000", tx ]);
      else graff.push ([ "text", Math.round (x1 - DIM_PTAx2 - textWidth (tx)), y1 - DIM_PTA, "fc:#000000", tx ]);

      flecha (x0, x1, y1, dirLR, getElapsed (ti, a1, a2));
   }

   // draw all agent time lines
   //
   for (var ag = 0, x0 = timeLine; ag < agents.length; ag ++, x0 += dia)
   {
      graff.push ([ "z", Math.round (x0), y0, "", "pol", 0, Math.round (y1+dit-y0)]);
   }

   for (var lap in lapsos)
   {
      // text background
      graff.push ([ "rect", Math.round (-DIM_PTAx4 + timeLine), Math.round (lapsos[lap].posy + DIM_PTAx4), "fc:#EEEEEE",
                    Math.round (dia * (agents.length - 1) + 2 * DIM_PTAx4),
                    DIM_PTAx4
                  ]);
      graff.push ([ "text", Math.round (DIM_PTAx2 + timeLine),
                            Math.round (lapsos[lap].posy + DIM_PTAx4 + DIM_PTAx2 + DIM_PTA),
                            "fc:#000000", lapsos[lap].text ]);
   }

   return graff;
}
