/**
 * MarketesModul - a marketes evolution
 * Copyright (c) 2020 Alejandro Xalabarder (CongoSofwareFee License)
 */

// NEXT Marketes ...
//
/*

   Apart from primitive hashes new modules will be loaded on demand for the document

   the syntax will be

      #module:variant param, param ...
         ...
         body passed to the module
         ...
      #/

   where "module" cannot contain ":" but any other character like for example "/" for refining etc

   marketesModul first scans for all "#module" and load the needed modules which consist on js modules
   that are found in some table

   modules can be specialized to math, biology, software development etc

   some module ideas
   
      finestreta
      sequenci
      graphviz
      ploton
      ataula
      jocs
   
      benzenus



   EXAMPLE:

      #h Header

      Here there is a tables

      #table:trunkera

         abc, desc, jalala

         etc, lalalalalalal, 84373297
         ram, asdajkllk lgsdkj, 34244

      #/

      Here some code by the face

      #code:sang
         #include <stdio.h>

         int main (int nn, char ** aa)
         {
            printf ("minestrotta");
         }
      #/

      Es get los mit Diagramen!

      #sequenci:plain

         A=Usera
         B=Interfazzo
         C=Servera

         10:12 A >> B getTimeNow
         10:16 B >> C getEnvironment
         10:45 B << C respEnvironment
         10:55 B >> C subscribe("multiservices")
         11:46 B << C subscribed

      #/

      #graphviz:dot

         digraph G {
            A -> B [label="getTimeNow"]
            B -> C [label="getEnvironment"]
            C -> B [label="respEnvironment"]
            B -> C [label="subscribe('multiservices')"]
            C -> B [label="subscribed"]
         }
      #/


      #jocs:eugino66

         Jocs.carrega = function ()
         {
            ...
         }

         Jocs.dibuixa = function ()
         {
            ...
         }

      #/

*/
//   Convert a marketes text into formated html
//
//   Example to convert a text given in the text area 'myTextArea'
//
//   var str = document.getElementById('myTextArea').value;
//   var productoHtml = marketes(str);
//   document.getElementById('myDiv').innerHTML = productoHtml;
//
function marketes (filo)
{
   "use strict";
   var textHtml = "";
   var out = [];
   var openedTag = "";
   var acumRt = "";

   // trim also for IE8
   function trimStr (str) { return str.replace(/^\s+|\s+$/g, ''); }

   function str2lineArray (str) { return str.replace(/\r\n/g, "\r").replace(/\n/g, "\r").split(/\r/); }

   var fileArr = (typeof filo == "string" ? str2lineArray (filo): filo);

   for (var ii in fileArr)
   {
      var linstr = fileArr[ii];

      if (checkSpecial ("#h ", "header")) continue;
      if (checkHeader (1)) continue;
      if (checkHeader (2)) continue;
      if (checkHeader (3)) continue;
      if (checkHeader (4)) continue;

      if (! trimStr (linstr))
      {
         // empty line
         if (openedTag === "code")
         {
            // if after all empty lines we continue with code we have to accumulate returns
            acumRt += "\n";
         }
         else openTag ();
      }
      else
      {
         if (linstr.indexOf ("    ") == 0)
         {
            openTag ("code");
            out.push (textHtml + acumRt + escapeHtml (linstr.substr (4)));
            acumRt = "";
         }
         else
         {
            openTag ("p");
            out.push (textHtml + linstr);
         }
         textHtml = "";
      }
   }

   return out.join ("\n") + textHtml;
   // ........................ return


   // ===============================
   // === functions

   function escapeHtml(str)
   {
      return String(str).replace(/&/g, '&amp;')
                        .replace(/"/g, '&quot;')
                        .replace(/'/g, '&#39;')
                        .replace(/</g, '&lt;')
                        .replace(/>/g, '&gt;');
   }

   function closeCurrentTag ()
   {
      // Note: for </code> we need </code></pre>
      if (openedTag.length > 0)
         return "</" + openedTag + ">" + ((openedTag === "code") ? "</pre>\n" : "\n");
      return "";
   }

   function openCurrentTag ()
   {
      // Note: for <code> we need <pre><code>
      if (openedTag.length > 0)
         return ((openedTag === "code") ? "<pre>" : "") + "<" + openedTag + ">";
      return "";
   }

   function openTag (tagS)
   {
      if (!tagS || tagS !== openedTag)
      {
         textHtml += closeCurrentTag ();
         openedTag = tagS || "";
         textHtml += openCurrentTag ();
         acumRt = "";
      }
   }

   function checkHeader(levh)
   {
      var ca1 = "####".substr(0,levh),
          ca2 = "----".substr(0,levh);

      if (linstr.indexOf (ca1 + " ") == 0 || linstr.indexOf (ca2 + " ") == 0)
      {
         openTag ("h" + levh);
         textHtml += escapeHtml (linstr.substr (levh+1));
         openTag ();
         return true;
      }
      return false;
   }

   function checkSpecial(str, attval)
   {
      var c1 = linstr.indexOf (str);
      if (c1 == 0)
      {
         textHtml += "<" + attval + ">" + escapeHtml (linstr.substr (str.length)) + "</" + attval + ">";
         return true;
      }
      return false;
   }
}
