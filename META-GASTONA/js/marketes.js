/**
 * marketes - a marketes format parser
 * Copyright (c) 2015, Alejandro Xalabarder (MIT Licensed)
 */

// uses fileStr (file)
//

function marketes (filo)
{
   "use strict";
   var fileOb,
       textHtml = "",
       linstr = "",
       openedTag = "",
       isemptyLine = false,
       acumRt = ""
       ;

   // trim for IE8
   if (typeof String.prototype.trim !== 'function') {
      String.prototype.trim = function() {
         return this.replace(/^\s+|\s+$/g, '');
      }
   }

   fileOb = fileStr (filo);

   while (! fileOb.eof ())
   {
      linstr = fileOb.getLine ();

      if (checkSpecial ("#h ", "header")) continue;
      if (checkHeader (1)) continue;
      if (checkHeader (2)) continue;
      if (checkHeader (3)) continue;
      if (checkHeader (4)) continue;

      isemptyLine = (linstr.trim ().length === 0);
      if (isemptyLine)
      {
         if (openedTag === "code")
            acumRt += "\n";
         else
            openTag ();
      }
      else
      {
         if (linstr.indexOf ("    ") == 0)
         {
            openTag ("code");
            textHtml += acumRt + escapeHtml (linstr.substr (4)) + "\n";
            acumRt = "";
         }
         else
         {
            openTag ("p");
            textHtml += linstr + "\n";;
         }
      }
   }

   return textHtml;
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


/**
 * fileStr - split lines from a text file given as string handling all possible combinations of CR and LF characters
 * Copyright (c) 2015, Alejandro Xalabarder (MIT Licensed)
 */

//// use
// var textal = fileStr (strfilo);
// while (! textal.eof ())
//    out (textal.getLine ());
//
function fileStr (strcontent)
{
   var textArr = strcontent.split("\n");
   var indx = 0, restales = "", au, resp;

   return {
      eof : eof,
      getLine : getLine,
      rewind : indx = 0,
      getCountLines : function () { return textArr.length; }
   };

   function eof ()
   {
      return indx >= textArr.length && restales.length == 0;
   }

   function getLine ()
   {
      if (eof ()) return;
      if (restales.length == "")
         restales = textArr[indx ++];

      au = restales.indexOf("\r");
      if (au >= 0)
      {
         resp = restales.substr(0,au);
         restales = restales.substr(au+1);
         return resp;
      }
      resp = restales;
      restales = "";
      return resp;
   }
}
