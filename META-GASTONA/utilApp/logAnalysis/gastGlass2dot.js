/*
Copyright (C) 2017-2021 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

//
// From an object containing units generates a dot (graphwiz)
// digraph using clusters for the units and shape=Mrecord for
// the eva variables. Make links between references @<var> and
// the variable and also for LSX calls
//
// @units         object (eva file) containing eva units to plot
// @unames2plot   array of unit names to plot, if not specified only "listix" will be plotted
// @fulltext      true to show all eva values and texts (default is false)
//
function gastGlass2dot (units, unames2plot, fulltext)
{
   var ploto = [];
   var evas = [];
   var linkCollect = [];

   var FULLTEXT = fulltext||false;
   var SLOTEVA = "enam";

   function processEva (evaContent, evaName)
   {
      var tableopen = false;
      var textal = [];
      var recLabel = []; // shape record from dot
      var cellslot = 0;
      var texslot = 0;
      var cmdslot = 0;
      var isfirstcmdline = true;

      recLabel.push ("<" + SLOTEVA + ">&lt;" + evaName + "&gt;\\l");
      for (var rr in evaContent)
      {
         var rowe = evaContent[rr];
         if (rowe.length === 1)
         {
            var texto = embelleseText (rowe[0]);
            if (typeof texto === "string")
               textal.push (texto + "\\l");
         }
         else {
            flushTextal ();
            var lina = [];
            isfirstcmdline = rowe[0].length > 0;
            for (var cc in rowe)
            {
               // just detect if it is a call to listix or generate commands
               //    LSX, evaname, ...
               //    GEN, ...., evaname
               //
               var isEvaName = rowe[cc-1] && ["lsx", "listix"].indexOf (rowe[cc-1].toLowerCase ()) >= 0 ||
                               (rowe[cc-2] && ["gen", "generate"].indexOf (rowe[cc-2].toLowerCase ()) >= 0);
               var item = embelleseCmd (rowe[cc], isEvaName);
               if (typeof item === "string")
                  lina.push (item);
            }
            if (lina.length > 0)
               recLabel.push ("{" + lina.join ("|") + "}");
         }
      }
      flushTextal ();
      return recLabel;

      // --------- inner functions
      function getReferencedEvas (str)
      {
         var las = [];
         var matxa;
         var rege = /@<([^\>]*)>/g;
         while ((matxa = rege.exec(str)) !== null && evas.indexOf (matxa[1]) >= 0)
            las.push (matxa[1]);

         return las;
      }

      function embellese (str)
      {
         return str.replace (/\"/g, "&quot;")
                   .replace (/</g, "&lt;")
                   .replace (/>/g, "&gt;")
                   .replace (/{/g, "&#123;")
                   .replace (/\|/g, "&#124;")
                   .replace (/}/g, "&#125;")
                   .replace(/\\/g, "\\\\");
      }

      function addLinkToEva (fromEva, fromSlot, toEva)
      {
         // add link only if target is found!
         if (evas.indexOf (toEva) >= 0)
            linkCollect.push (normalstr(fromEva) + ":" + fromSlot + " -> " + normalstr(toEva) + ":" + SLOTEVA);
      }

      function embelleseText (str)
      {
         var nlin = 1 + textal.length;

         // searching for references @<...> and add them to links
         //
         var refas = getReferencedEvas (str);
         for (var rr in refas)
            addLinkToEva (evaName, ("text" + texslot), refas[rr]);

         if (!FULLTEXT)
         {
            // not relevant line
            if (nlin > 1 && refas.length == 0) return; // undefined
            var refStr = refas.length > 0 ? ("@<" + refas.join (">..<") + ">..") : "";

            if (nlin === 1)
                str = str.substr (0, 10) + ".." + refStr;
            else if (refStr.length > 0)
                str = ".." + refStr;
            else return; // undefined
         }
         return embellese (str);
      }

      function embelleseCmd (str, contentIsEvaName)
      {
         var col = 1 + Math.round(cc);  // cc from outer loop

         var strslot = ("slot" + cellslot ++);
         var strCmdslot = (isfirstcmdline && col === 1) ? ("cmd" + cmdslot ++): "";

         if (strCmdslot.length > 0)
            strslot = strCmdslot;

         var hasslot = strCmdslot.length > 0; // we still don't know about cell if contain refs or not ...

         if (!FULLTEXT && !isfirstcmdline)
            strslot = "cmd" + (cmdslot -1); // slot of current (previous row) command

         if (contentIsEvaName)
         {
            addLinkToEva (evaName, strslot, str);
            hasslot = true;
         }

         // searching for references @<...> and add them to links
         //
         var refas = getReferencedEvas (str);
         hasslot |= refas.length > 0;
         for (var rr in refas)
            addLinkToEva (evaName, strslot, refas[rr]);

         if (!FULLTEXT && !isfirstcmdline) return; // undefined

         if (col >= 3 && !FULLTEXT && !hasslot)
            return col === 3 ? "...": undefined;

         var refStr = (!FULLTEXT && refas.length > 0) ? ("@<" + refas.join (">..<") + ">..") : str;

         // main:logics -> logics:ename;
         return (hasslot ? ("<" + strslot + ">"): "") + embellese (col >= 3 ? refStr: str);
      }

      function flushTextal ()
      {
         if (textal.length === 0) return;
         var slote = "<" + ("text" + texslot++) + ">";
         recLabel.push (slote + textal.join ("\\n"));
         textal = [];
      }
   }

   function normalstr (nam)
   {
      return nam.replace(/[^a-z|A-Z]/g, "_");
   }

   function processUnit (uniname)
   {
      if (!units[uniname]) return;

      // ploto.push ("   subgraph cluster_" + normalstr(uniname) + " {");
      for (var evaname in units[uniname])
      {
         var reco = processEva (units[uniname][evaname], evaname);
         ploto.push ("      " + normalstr(evaname) + " [label=\"" + reco.join ("|") + "\"]");
      }
      // ploto.push ("   }");
   }

   ploto.push ("digraph structs {");
   ploto.push ("   rankdir=LR;");
   ploto.push ("   node [shape=Mrecord,fontname=Courier,fontsize=8];");

   unames2plot = unames2plot|| ["listix", "data"];

   // collect all eva names from all units
   //
   for (var uu in units)
      if (unames2plot.indexOf (uu) >= 0)
         for (var ee in units[uu])
            evas.push (ee);

   for (var ii in unames2plot)
      processUnit(unames2plot[ii]);

   for (var ii in linkCollect)
      ploto.push ("   " + linkCollect[ii]);

   ploto.push ("}");
   return ploto.join ("\n");
}