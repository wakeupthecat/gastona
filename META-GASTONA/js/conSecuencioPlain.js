/**
 * conSecuencioPlain - simple and plain sequence diagram
 * Copyright (c) 2016, Alejandro Xalabarder
 */

function conSecuencioPlain (diagData)
{
   var arr = diagData["sequenceTable"];
   var dia = diagData["distanceAgents"];
   var dit = diagData["distanceTimeUnit"];
   var maxGapTime = diagData["maxGapTime"];
   var autoElapsed = diagData["autoElapsed"];
   if (typeof dia === "string") dia = parseInt (dia);
   if (typeof dit === "string") dit = parseInt (dit);
   if (typeof maxGapTime === "string") maxGapTime = parseInt (maxGapTime);

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

         if (tx != -1 && agArr.indexOf (mats[ii][tx]) == -1)
            agArr.push (mats[ii][tx]);
         if (rx != -1 && agArr.indexOf (mats[ii][rx]) == -1)
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

   // ----- TEXT

   // DEFAULT VALUES
   //
   dia = dia || 30;  // horizontal distance between agents
   dit = dit ||  4;  // vertical distance between time units (i.e. seconds)
   maxGapTime = maxGapTime || 3;
   autoElapsed = autoElapsed || true;

   //GENERAL UTILITIES
   // trim for IE8
   if (typeof String.prototype.trim !== 'function') {
      String.prototype.trim = function() {
         return this.replace(/^\s+|\s+$/g, '');
      }
   }

   // for all
   //
   if (typeof String.prototype.trimTail !== 'function') {
      String.prototype.trimTail = function() {
         return this.replace(/\s+$/g, '');
      }
   }

   if (typeof String.prototype.left !== 'function') {
      String.prototype.left = function (len) {
         return this.substring (0, len);
      }
   }
   if (typeof String.prototype.right !== 'function') {
      String.prototype.right = function (len) {
         return this.substring (this.length - len);
      }
   }

   // char.repeat (n) but checking to avoid javascript error
   //
   function rechar (cha, times)
   {
      if (times <= 0) return '';
      return cha.repeat (times);
   }

   var plotResult = "";
   var TIME_LEN = 13;
   var sPre  = ' '.repeat (dia * 0.06);
   var lastTim = 0.;

   function out (str)
   {
      plotResult += str + "\n";
   }

   function barras (n, cha)
   {
      cha = cha || '|';
      var ba = cha;
      for (var ii = 0; ii < n; ii ++)
         ba = ba + rechar (' ', dia) +  cha;
      return ba;
   }

   function flecha (ifrom, ito, text)
   {
      var pre = "", post = "";
      if (ifrom > ito)
      pre = "<" + (text ? ("--" + text): "");
      else
         post = "" + (text ? (text + "--"): "") + ">";
      return (pre +
             rechar ('-', (dia + 1) * Math.abs (ifrom - ito) - 1 - pre.length - post.length) +
             post);
   }

   function label (timo, indxFrom, indxTo, txt)
   {
      var p1, ii;

      if (indxFrom == -1)
      {
          if (indxTo == -1) return;
          indxFrom = indxTo;
      }
      else if (indxTo == -1)
           indxTo = indxFrom;

      var i0 = Math.min (indxFrom, indxTo);
      var i1 = Math.max (indxFrom, indxTo);

      // split text into a text array if larger than distance between agents
      //
      if (dia < 2) dia = 2;   // some minimum

      // make array of strings if needed
      var txtarr = [];
      for (p1 = 0, ii = 0; p1 < txt.length; p1 += (dia-sPre.length), ii ++)
      {
         txtarr [ii] = txt.substring (p1, p1+(dia-sPre.length));
      }

      // time
      //
      var stim = (rechar(' ', 10) + (timo >= 0 ? timo: " ")).right (10) + rechar (' ', 3);

      // first line(s) for label
      //
      var sPos, title, b1, b2;
      for (ii = 0; ii < txtarr.length; ii ++)
      {
         sPos = rechar (' ', dia - sPre.length - txtarr[ii].length);
         title, b1, b2;

         if (indxFrom < indxTo)
         {
            title = sPre + txtarr[ii] + sPos;
            b1 = i0;
            b2 = agents.length - i0 - 2;
         }
         else
         {
            title = sPos + txtarr[ii] + sPre;
            b1 = i1 - 1;
            b2 = agents.length - i1 - 1;
         }

         // label
         out (rechar (' ', stim.length) + barras (b1) + title + barras (b2));
      }

      // arrow
      out (stim +
           barras (i0) +
           flecha (indxFrom, indxTo, getElapsed (timo, indxFrom, indxTo)) +
           barras (agents.length - i1 - 1));
   }

   var band = Math.round (dia / 3);
   var sLeft = rechar (' ', TIME_LEN);
   var sLeft0 = rechar (' ', TIME_LEN + 1 - band/2);
   var sGap  = rechar (' ', dia - band + 1);

   var sAgeNom = "";  //  name    name   ...
   var sAgeRay = "";  //  _____  _____   ...
   var sCont = sLeft + barras (agents.length-1);       //    |      |     ...
   var sDisc = sLeft + barras (agents.length-1, '~');  //    ~      ~     ...

   for (var ag in agents)
   {
      var half = Math.round ((band - agents[ag].length) / 2);
      sAgeNom = sAgeNom + rechar (' ', half) + agents[ag] + rechar (' ', band-half-agents[ag].length) + sGap;
      sAgeRay = sAgeRay + rechar ('_', band) + sGap;
   }
   sAgeNom = sLeft0 + sAgeNom.trimTail ();
   sAgeRay = sLeft0 + sAgeRay.trimTail ();

   out (sAgeRay);
   out (sAgeNom);
   out (sAgeRay);

   lastTim = null;
   for (var aa in arr)
   {
      if (aa === "0") continue;

      var ti = hasTime () ? arr[aa][iTimeStamp]: parseInt (aa);
      var a1 = agents.indexOf (arr[aa][iAgentTx]);
      var a2 = agents.indexOf (arr[aa][iAgentRx]);
      var tx = arr[aa][iMessage];

      // plot time difference
      //
      if (dit > 0 && ti >= 0)
      {
         var ncont = lastTim ? (ti - lastTim) * dit: 2;
         if (ncont > maxGapTime * dit) {
            out (sDisc);
            out (sDisc);
         }
         else
            for (var ii = 0; ii < ncont; ii ++)
               out (sCont);
      }
      lastTim = ti;

      label (ti, a1, a2, tx);
   }
   out (sCont);
   out (sAgeRay);
   out (sAgeNom);
   out (sAgeRay);

   return plotResult;
}
