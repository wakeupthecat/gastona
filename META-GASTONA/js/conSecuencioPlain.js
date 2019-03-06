/**
 * conSecuencioPlain - simple and plain sequence diagram
 * Copyright (c) 2016-2019, Alejandro Xalabarder
 */

function conSecuencioPlain (diagData)
{
   function getAnInt (obj, defval)
   {
      if (!obj) return defval;
      if (typeof obj === "number") return parseInt(obj);
      return parseInt (obj[0][0]);
   }


   // getIndexOfAny (["blabla", "na", "old", "one"], ["one", "two", "three"]);
   //                   0         1      2     3
   //    returns 3
   //
   function getIndexOfAny (strArr, arrPos)
   {
      for (var ele in arrPos)
      {
         var pos = strArr.indexOf (arrPos[ele]);
         if (pos != -1) return pos;
      }
      return -1;
   }

   var arr = diagData["sequenceTable"];
   var distAgents = Math.max (2, getAnInt (diagData["distanceAgents"], 30));
   var distTimeUnit = getAnInt (diagData["distanceTimeUnit"], 4);
   var maxGapTime = getAnInt (diagData["maxGapTime"], 3);
   var autoElapsed = diagData["autoElapsed"];
   if (typeof autoElapsed === "string")
      autoElapsed = (autoElapsed === "1" || autoElapsed === "true");
   autoElapsed = autoElapsed !== false;

   // expected schema of sequenceTable (columns):  time, source, target, message
   //
   var iTimeStamp = getIndexOfAny (arr[0], ["time", "tstamp", "stamp"]);
   var iAgent1    = getIndexOfAny (arr[0], ["source", "src", "tx", "ag1", "agent1"]);
   var iAgent2    = getIndexOfAny (arr[0], ["target", "tgt", "rx", "ag2", "agent2"]);
   var iDir       = getIndexOfAny (arr[0], ["dir", "arrow"]);
   var iMessage   = getIndexOfAny (arr[0], ["message", "msg" ]);
   var iExtra     = getIndexOfAny (arr[0], ["extrainfo", "extra", "info"]);

   var agentsAlias = {};
   var agents = getAgents(arr, iAgent1, iAgent2);
   var timesIda = {}; // track elapsed times (or try to)

   function hasTime ()
   {
      return iTimeStamp != -1;
   }

   function finalName (name)
   {
      return agentsAlias[name]||name;
   }

   function getAgents (mats, tx, rx)
   {
      function pushAgent (agname)
      {
         if (agname === undefined || agname.length == 0) return;

         // name and alias if any
         var namalia = agname.split("=");
         var valo = agname;

         if (namalia[1])
         {
            valo = finalName (namalia[1]);
            agentsAlias[namalia[0]] = valo;
         }
         if (agArr.indexOf (valo) == -1 && !agentsAlias[valo])
         {
            agArr.push (valo);
         }
      }

      agentsAlias = {};
      var agArr = [];
      var siz = mats.length;
      if (siz < 2) return agArr;

      if (rx === -1 || tx === -1) return agArr;

      for (var ii in mats)
      {
         if (ii === "0") continue;

         if (tx != -1 && mats[ii][tx])
            pushAgent (mats[ii][tx]);
         if (rx != -1 && mats[ii][rx])
            pushAgent (mats[ii][rx]);
      }

      // reduce columns if some alias present
      //
      for (var ii = agArr.length - 1; ii >= 0; ii --)
         if (agentsAlias[agArr[ii]])
            agArr.splice (ii, 1);

      // resolve all alias (alias of alias etc)
      // twice ensure all combinations (?)
      for (var tt = 0; tt < 2; tt ++)
         for (var ii in agentsAlias)
            agentsAlias[ii] = finalName (agentsAlias[ii]);

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

   // string.repeat(count) not in IE 11 !
   //
   function repeat (what, times)
   {
      var arr = [];
      for (var ii=0; ii < times; ii ++)
         arr.push (what);
      return arr.join ("");
   }

   var plotResult = [];
   var TIME_LEN = 13;
   var sPre  = repeat (" ", distAgents * 0.06);
   var lastTim = 0.;

   function out (str)
   {
      plotResult.push (str);
   }

   function barras (n, cha)
   {
      if (n < 0) return "";
      cha = cha || '|';
      var ba = cha;
      for (var ii = 0; ii < n; ii ++)
         ba = ba + repeat (' ', distAgents) +  cha;
      return ba;
   }

   function putFlecha (ifrom, ito, text)
   {
      var pre = "", post = "";
      if (ifrom > ito)
      pre = "<" + (text ? ("--" + text): "");
      else
         post = "" + (text ? (text + "--"): "") + ">";
      return (pre +
             repeat ('-', (distAgents + 1) * Math.abs (ifrom - ito) - 1 - pre.length - post.length) +
             post);
   }

   function putLabel (timo, indxFrom, indxTo, txt)
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

      // make array of strings if needed
      var txtarr = [];
      for (p1 = 0, ii = 0; p1 < txt.length; p1 += (distAgents - sPre.length), ii ++)
      {
         txtarr [ii] = txt.substring (p1, p1+(distAgents - sPre.length));
      }

      // time
      //
      var stim = (repeat(' ', 10) + (timo >= 0 ? timo: " ")).right (10) + repeat (' ', 3);

      // first line(s) for label
      //
      var sPos, title, b1, b2;
      for (ii = 0; ii < txtarr.length; ii ++)
      {
         sPos = repeat (' ', distAgents - sPre.length - txtarr[ii].length);
         title, b1, b2;

         if (indxFrom <= indxTo)
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
         out (repeat (' ', stim.length) + barras (b1) + title + barras (b2));
      }

      if (indxFrom != indxTo)
      {
         // arrow
         out (stim +
              barras (i0) +
              putFlecha (indxFrom, indxTo, getElapsed (timo, indxFrom, indxTo)) +
              barras (agents.length - i1 - 1));
      }
   }

   var band = Math.round (distAgents / 3);
   var sLeft = repeat (' ', TIME_LEN);
   var sLeft0 = repeat (' ', TIME_LEN + 1 - band/2);
   var sGap  = repeat (' ', distAgents - band + 1);

   var sAgeNom = "";  //  name    name   ...
   var sAgeRay = "";  //  _____  _____   ...
   var sCont = sLeft + barras (agents.length-1);       //    |      |     ...
   var sDisc = sLeft + barras (agents.length-1, '~');  //    ~      ~     ...

   for (var ag in agents)
   {
      var agentStr = agentsAlias[agents[ag]] || agents[ag];
      var half = Math.round ((band - agentStr.length) / 2);
      sAgeNom = sAgeNom + repeat (' ', half) + agentStr + repeat (' ', band-half - agentStr.length) + sGap;
      sAgeRay = sAgeRay + repeat ('_', band) + sGap;
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

      var tx = arr[aa][iMessage];

      if (tx === undefined) {
         // agents with no message
         // this might be useful as a way of fixing some order of agents
         continue;
      }

      // plot time difference
      //
      if (distTimeUnit > 0 && ti >= 0)
      {
         var ncont = lastTim ? (ti - lastTim) * distTimeUnit: 2;
         if (ncont > maxGapTime * distTimeUnit) {
            out (sDisc);
            out (sDisc);
         }
         else
            for (var ii = 0; ii < ncont; ii ++)
               out (sCont);
      }
      lastTim = ti;

      // get source and target agents (index of agents array)
      var agenIndxAg1 = agents.indexOf (finalName (arr[aa][iAgent1]));
      var agenIndxAg2 = agents.indexOf (finalName (arr[aa][iAgent2]));

      // decide which agentindex is "from" and which is "to"
      //
      if (iDir == -1 || ["RQ", "REQ", "EV", "TX", ">>"].indexOf ((arr[aa][iDir]).toUpperCase()) >= 0)
         putLabel (ti, agenIndxAg1, agenIndxAg2, tx);
      else
         putLabel (ti, agenIndxAg2, agenIndxAg1, tx);
   }
   out (sCont);
   out (sAgeRay);
   out (sAgeNom);
   out (sAgeRay);

   return plotResult.join ("\n");
}
