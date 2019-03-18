// codiFinestreta
//
//    The component compose a svg from a text, typically source code to be highlighted, to be rendered
//    in a container thought to work with style overflow = "scroll". That is the svg is bigger than
//    the container and can be scrolled by it.
//
//    The fact that the container only shows a small window (finestra) is used by codiFinestreta
//    to actual generate on demand the needed svg. This provides a huge performance improvement
//    specially when dealing with big files, and also it saves from unnecessary process when,
//    for example, the text is not scrolled.
//
//    construction (note: no new to be used on construction!)
//       var myCLF = codiFinestreta (codeTextArray, offsetLineNr, regexpMark)
//
//       codeTextArray : array of strings containing the code or portion of code to be highlighted
//       offsetLineNr  : number of line of the first provided line of code
//       regexpMark    : regular expression to search for matches
//
//    once we have the object we can associate it to a container in order to be rendered
//
//       myCLF.attachTo (myDiv);
//       myCLF.detachFrom (myDiv);
//       myCLF.gotoLine (144);
//       //... myCLF.gotoFirst ();
//       //... myCLF.gotoNext ();
//       //... myCLF.gotoPrev ();
//       //... myCLF.gotoLast ();
//
//    also some properties can be changed
//
//       myCLF.changeTheme ("fusta");
//       //... myCLF.changeHighlightLanguage ("sql");
//       //... myCLF.changeFont ("Courier New");
//       //... myCLF.changeFontWeight ("bold");
//
//
function codiFinestreta (codeTextArr, offsetLineNr, regexpMark, startInComment)
{
   // most keywords taken from prettify
   //
   var FLOW_CONTROL_KEYWORDS = ["break,continue,do,else,for,if,return,while"];
   var C_KEYWORDS = [FLOW_CONTROL_KEYWORDS,"auto,case,char,const,default," +
       "double,enum,extern,float,goto,inline,int,long,register,restrict,short,signed," +
       "sizeof,static,struct,switch,typedef,union,unsigned,void,volatile"];
   var COMMON_KEYWORDS = [C_KEYWORDS,"catch,class,delete,false,import," +
       "new,operator,private,protected,public,this,throw,true,try,typeof"];
   var CPP_KEYWORDS = [COMMON_KEYWORDS,"alignas,alignof,align_union,asm,axiom,bool," +
       "concept,concept_map,const_cast,constexpr,decltype,delegate," +
       "dynamic_cast,explicit,export,friend,generic,late_check," +
       "mutable,namespace,noexcept,noreturn,nullptr,property,reinterpret_cast,static_assert," +
       "static_cast,template,typeid,typename,using,virtual,where"];
   var JAVA_KEYWORDS = [COMMON_KEYWORDS,
       "abstract,assert,boolean,byte,extends,finally,final,implements,import," +
       "instanceof,interface,null,native,package,strictfp,super,synchronized," +
       "throws,transient,String"];
   var CSHARP_KEYWORDS = [COMMON_KEYWORDS,
       "abstract,add,alias,as,ascending,async,await,base,bool,by,byte,checked,decimal,delegate,descending," +
       "dynamic,event,finally,fixed,foreach,from,get,global,group,implicit,in,interface," +
       "internal,into,is,join,let,lock,null,object,out,override,orderby,params," +
       "partial,readonly,ref,remove,sbyte,sealed,select,set,stackalloc,string,select,uint,ulong," +
       "unchecked,unsafe,ushort,value,var,virtual,where,yield"];
   var COFFEE_KEYWORDS = "all,and,by,catch,class,else,extends,false,finally," +
       "for,if,in,is,isnt,loop,new,no,not,null,of,off,on,or,return,super,then," +
       "throw,true,try,unless,until,when,while,yes";
   var JSCRIPT_KEYWORDS = [COMMON_KEYWORDS,
       "abstract,async,await,constructor,debugger,enum,eval,export,function," +
       "get,implements,instanceof,interface,let,null,set,undefined,var,with," +
       "yield,Infinity,NaN"];
   var PERL_KEYWORDS = "caller,delete,die,do,dump,elsif,eval,exit,foreach,for," +
       "goto,if,import,last,local,my,next,no,our,print,package,redo,require," +
       "sub,undef,unless,until,use,wantarray,while,BEGIN,END";
   var PYTHON_KEYWORDS = [FLOW_CONTROL_KEYWORDS, "and,as,assert,class,def,del," +
       "elif,except,exec,finally,from,global,import,in,is,lambda," +
       "nonlocal,not,or,pass,print,raise,try,with,yield," +
       "False,True,None"];
   var RUBY_KEYWORDS = [FLOW_CONTROL_KEYWORDS, "alias,and,begin,case,class," +
       "def,defined,elsif,end,ensure,false,in,module,next,nil,not,or,redo," +
       "rescue,retry,self,super,then,true,undef,unless,until,when,yield," +
       "BEGIN,END"];
   var SH_KEYWORDS = [FLOW_CONTROL_KEYWORDS, "case,done,elif,esac,eval,fi," +
       "function,in,local,set,then,until"];

   var ALL_KEYWORDS = [
       CPP_KEYWORDS, CSHARP_KEYWORDS, JAVA_KEYWORDS, JSCRIPT_KEYWORDS,
       PERL_KEYWORDS, PYTHON_KEYWORDS, RUBY_KEYWORDS, SH_KEYWORDS];

   var SVGNamespace = "http://www.w3.org/2000/svg";
   var DEFCOL = "#606060";

   var ARR_ALL = ALL_KEYWORDS.join(",").split(",");

   // A typical code does not have lines such long
   // and if so, highlighting it until the end would not be
   // a great help for the user.
   // Note that files like for instance minified javascript files
   // may have such lines
   // So we don't waste processing time on that
   //
   var MAX_LEN_TO_BE_HIGHTLIGHTED = 400;

   var NUMLINE_POSX_LINENR  = 50;
   var NUMLINE_POSX_LINETXT = NUMLINE_POSX_LINENR + Math.round (DIMCHA_X * 4 / 3);

   var DIMCHA_X = 123 / 14;   // will be set in ensureSvg ()
   var DIMCHA_Y = 20;         // will be set in ensureSvg ()
   var svgElem;               // svg element to containing all lines
   var renderedLines = {};    // track lines already with svg element
   var startInCommentBlock = {}; // track if line start inside a comment block
   var CURRENT_FOCUS_LINENR = 1;
   var lastClickEvent;

   var currentFont = "monospace";

   var theTheme  = { mark    : "#937979", //575757
                     normal  : "#FFFFFF",
                     key     : "#e0b370", //DCC275
                     comment : "#ea8883", //888888  8bef8b
                     literal : "#b7b3b6", //A2FCA2  ad87af
                     punkt   : "#FFFFAA",
                     num     : "#f78585", //D36363
                     linenum : "#89b1b2", //719293
                     back    : "#2d1d1d", //333333
                   };

   // we give the sizes calculated (default size!)
   // it can be calculated with a text element "el" containing one character (e.g. X)
   //    var dim = el.getBoundingClientRect() (then .width and .height)
   //
   // Consolas    : 8.79261302947998 x 18.18181800842285
   // Courier New : 9.59375 x 18
   // Monospace   : 7.140625 x 15
   //
   var fonts = {
      "consolas"   : [ "Consolas", 8.79261302947998, 18.18181800842285 ],
      "couriernew" : [ "Courier New", 9.59375, 18 ],
      "monospace"  : [ "Monospace", 7.140625, 15 ],
   };

   var themens = {
      clar  :  "e8e8e83636360f4db7d14306917777f0b4230f4db7f4555ab2a6a6",
      dunes :  "f5f3d80a1007a4a400679227d07853d5db62a82b73da3d74d3be47",
      sang  :  "2d1d1dFFFFFFe0b370b7b3b6ea8883937979FFFFAAf7858589b1b2",
      fusta :  "333333FFFFFFDCC275A2FCA2888888575757FFFFAAD36363719293",
      cafeto:  "373b41dadcdbf0c674bfe9a0eb8f63717935f2f73cb5d7d2888f97",
      dark  :  "002b36FFFFFFe0b370b7b3b6d0e1c9937979FFFFAA9ddfddd0e1c9",
      dark2 :  "002b36e9e9e9ecee9568d7cec1c1c1268fe6FFFFAAed9cc70dcac0",
   };
   
   var codeTextArray = codeTextArr;

   var globNnumLines = codeTextArray ? codeTextArray.length : 0;
   var globMaxLength = 0;
   var assocCont; // for now just one but it could be an array of them

   // compute the max length to know the total width dimension needed
   // and make a list of lines which belong to started comment block /* .. */
   //
   var nowComment = startInComment;

   loadText (codeTextArray);

   return {
      attachTo             : attachTo,
      dettachFrom          : dettachFrom,
      loadText             : loadText,
      gotoLine             : gotoLine,
      insertChar           : insertChar,
      changeFont           : changeFont,
      setFont              : changeFont,
      changeTheme          : changeTheme,
      setTheme             : changeTheme,
      getThemeColor        : getThemeColor,
      getSvgPane           : getSvgPane,
      renderFinestreta     : renderFinestreta,
      getPosLastClick      : getPosLastClick,
   };

   function loadText (textLines)
   {
      codeTextArray = textLines;
      globNnumLines = textLines ? textLines.length : 0;
      globMaxLength = 0;

      // compute the max length to know the total width dimension needed
      // and make a list of lines which belong to started comment block /* .. */
      //
      var nowComment = startInComment;

      for (var ii = 0; ii < textLines.length; ii ++)
      {
         if (textLines[ii].length > globMaxLength)
            globMaxLength = textLines[ii].length;

         // by now we will ignore (bad interpret) the bizarre cases of
         //          something no comment // blabla /* etc
         //          something no comment "string! /* haha fake!"
         //          something comment */ /* continue
         //          something comment */ i do stuff here!
         startInCommentBlock[ii] = nowComment;
         if (nowComment)
         {
            if (textLines[ii].match (/^\s*\//))
               nowComment = false;
         }
         else
         {
            if (textLines[ii].match (/^\s\/*/))
               nowComment = true;
         }
      }

      // to force render
      changeTheme ("");
   }

   function invalidateSvg ()
   {
      svgElem = null;
   }

   function ensureSvg ()
   {
      if (svgElem) return; // yes, have it!

      // create the svg element
      //
      DIMCHA_X = fonts[currentFont][1];
      DIMCHA_Y = fonts[currentFont][2];

      NUMLINE_POSX_LINENR  = Math.round (5 * DIMCHA_X); // later log (maxNLine) ...
      NUMLINE_POSX_LINETXT = NUMLINE_POSX_LINENR + Math.round (DIMCHA_X * 4 / 3);

      // creation of svg with the total dimension to contain the whole text
      //
      svgElem = document.createElementNS (SVGNamespace, "svg");

      var dx = NUMLINE_POSX_LINETXT + Math.round (DIMCHA_X * (3 + globMaxLength));
      var dy = Math.round (DIMCHA_Y * (1 + globNnumLines));

      svgElem.setAttribute ("width", dx);
      svgElem.setAttribute ("height", dy);
      svgElem.setAttribute ("style", "background-color:" + theTheme.back);
      svgElem.setAttribute ("id", "codeLightPro"); // not relevant ...
      renderedLines = {};
      associateSvgToContainer ();
   }

   function getThemeColor (what)
   {
      return theTheme[what] || DEFCOL;
   }

   function getSvgPane ()
   {
      ensureSvg ();
      return svgElem;
   }

   function attachTo (htmlElem)
   {
      if (assocCont)
         dettachFrom (assocCont);

      // reference to us (finestreta), usually the events
      // goes to the html element, having this reference can be use to
      // call funtions of the finestreta
      htmlElem.finestreta = this;

      // associated html container to the finestreta
      //
      assocCont = htmlElem;
      assocCont.style.overflow = "scroll";
      associateSvgToContainer ();
      assocCont.addEventListener ("scroll", rerenderCode);
      assocCont.addEventListener ("click", userClick);
      window.addEventListener ("resize", rerenderCode);
   }

   function associateSvgToContainer ()
   {
      if (assocCont)
      {
         assocCont.style["font-family"] = fonts[currentFont][0];
         assocCont.style["font-weight"] = "normal";
         //assocCont.style["font-size"] = "14px";
         assocCont.style["background-color"] = getThemeColor ("back");

         // fill unic
         while (assocCont.hasChildNodes())
            assocCont.removeChild(assocCont.firstChild);
         assocCont.appendChild (getSvgPane ());
      }
   }

   function dettachFrom (htmlElem)
   {
      assocCont.removeEventListener ("scroll", rerenderCode);
      assocCont.removeEventListener ("click", userClick);
      window.removeEventListener ("resize", rerenderCode);
      while (assocCont.hasChildNodes())
         assocCont.removeChild(assocCont.firstChild);
      assocCont = null;
   }

   function gotoLine (lineNr)
   {
      CURRENT_FOCUS_LINENR = lineNr;
      var nlinesVisible = assocCont.clientHeight / DIMCHA_Y;
      assocCont.scrollTop = Math.max (0, (CURRENT_FOCUS_LINENR - offsetLineNr - nlinesVisible/2) * DIMCHA_Y);
      rerenderCode ();
   }

   function insertChar (lochar, lineNr, colNr)
   {
      codeTextArray[lineNr] = codeTextArray[lineNr].substr(0, colNr) + lochar + codeTextArray[lineNr].substr(0, colNr+1);

      // invalidate all!
      renderedLines = {};
      gotoLine (lineNr)
   }

   function getPosLastClick ()
   {
      if (!assocCont || !lastClickEvent) return { row: 0, col: 0 };

      return { row: offsetLineNr + Math.round (lastClickEvent.offsetY / DIMCHA_Y),
               col: Math.round (lastClickEvent.offsetX - NUMLINE_POSX_LINETXT / DIMCHA_X)
      };
   }

   function rerenderCode ()
   {
      if (!assocCont) return;
      ensureSvg ();
      renderFinestreta (assocCont.clientWidth, assocCont.clientHeight, assocCont.scrollTop);
   }

   function userClick (ev)
   {
      lastClickEvent = ev;

      var cono = getPosLastClick ();
      console.log ("das wurde row " + cono.row + " col " + cono.col);
   }

   function changeTheme (colors)
   {
      // admit      "ccccccCCCCCCcccccc..."
      // as well as "cccccc CCCCCC cccccc ..."
      var cols = colors.length >= 48 ? colors: (themens[colors] || themens["fusta"]);
      cols = cols.replace(/\s+/g, '');

      theTheme["back"]    = "#" + cols.substr( 0, 6);
      theTheme["normal"]  = "#" + cols.substr( 6, 6);
      theTheme["key"]     = "#" + cols.substr(12, 6);
      theTheme["literal"] = "#" + cols.substr(18, 6);
      theTheme["comment"] = "#" + cols.substr(24, 6);
      theTheme["mark"]    = "#" + cols.substr(30, 6);
      theTheme["punkt"]   = "#" + cols.substr(36, 6);
      theTheme["num"]     = "#" + cols.substr(42, 6);
      theTheme["linenum"] = "#" + cols.substr(48, 6);

      invalidateSvg ();
      rerenderCode ();
   }

   function changeFont (fontname)
   {
      var fnormal = fontname.replace(/\s/g, "").toLowerCase ();
      if (fonts[fnormal])
      {
         currentFont = fnormal;
         invalidateSvg ();
         rerenderCode ();
      }
      else console.error ("codeFinestra: cannot set font to [" + fontname + "]");
   }

   function renderFinestreta (finestraWidth, finestraHeight, scrollTop)
   {
      var fromCodeIndx = Math.round (scrollTop / DIMCHA_Y); // lines above
      var toCodeIndx = Math.min (4 + fromCodeIndx + Math.round (finestraHeight / DIMCHA_Y), codeTextArray.length);

      //console.log ("renderFinestreta " + finestraWidth + ", " + finestraHeight + ", " + Math.round (scrollTop));

      for (var ii = fromCodeIndx - 2; ii < toCodeIndx; ii ++)
      {
         if (ii >=0 && !renderedLines[ii]) {
            //console.log ("reder first time " + ii);
            svgElem.appendChild (codeLightSvgLine(codeTextArray[ii], regexpMark, offsetLineNr + ii, DIMCHA_Y * (ii + 1), DIMCHA_X, DIMCHA_Y, theTheme));
            renderedLines[ii] = "y";
         }
      }
   }

   //--- regexAgents object
   //
   function regexAgents (agents, linestr)
   {
      var currLinstr = linestr;
      var currMatch = null;

      return {
         setData: function (str) { currLinstr = str; },
         getData: function () { return currLinstr; },
         addData: function (str) { currLinstr += str; },
         nextMatch : nextMatch,
         getLastMatch : function () { return currMatch; },
         // test: test,
      };

      function nextMatch ()
      {
         if (!currLinstr || currLinstr.length === 0) return null;

         var mini = currLinstr.length;
         var agent = "";
         for (var szeno in agents) {
            agents[szeno].match = agents[szeno].reg.exec (currLinstr);
            if (agents[szeno].match && agents[szeno].match.index < mini)
            {
               mini = agents[szeno].match.index;
               agent = szeno;
            }
         }
         if (! agent) return null;

         var match = agents[agent].match;
         if (match && agent)
         {
            currMatch = {  "agent": agent,
                           "prevStr": currLinstr.substr (0, match.index),
                           "matchStr": match[0]
                        };
            currLinstr = currLinstr.substr(match.index + match[0].length);
            return currMatch;
         }
      }
   }

   // render on svg one single line
   // returns the svg element for the line
   //
   function codeLightSvgLine (linetext, regMark, lineNr, svgposy, CHAX, CHAY, theme)
   {
      var svgLine;
      var svgLineTextEle;

      // var CURR_COL = 0;

      // whole line including line number
      svgLine = document.createElementNS (SVGNamespace, "g");

      // line content with all tspan for different colors
      // it will be included in svgLine.
      //
      svgLineTextEle = document.createElementNS (SVGNamespace, "text");
      svgLineTextEle.setAttribute ("x", NUMLINE_POSX_LINETXT);
      svgLineTextEle.setAttribute ("y", svgposy);
      svgLineTextEle.setAttribute ("style", "white-space: pre");

      var agentsWords = regexAgents ( {  number: { reg: /[-+]?\.[0-9]+|[-+]?[0-9]+/ },
                                         word  : { reg: /\w+/ },
                                         space : { reg: /\s+/ },
                                      }
                                    );

      return procesoLinea (linetext);  // ---------------------> > > >

      // functions of codeLightSvgLine
      //
      function entagText (tag, text)
      {
         if (!text) return;
         // var x0 = (NUMLINE_POSX + (CURR_COL + 1) * CHAX);
         // CURR_COL += text.length;

         // if only blanks no need of tag stuff, we just append it to the line text
         //
         if (!tag || text.match (/^\s+$/)) {
            // console.log ("append [" + text + "]");

            // WORK IN WINDOWS BUT NOT IN LINUX ????
            // svgLineTextEle.append (text);
            svgLineTextEle.appendChild (document.createTextNode (text));
            return;
         }

         var pato = document.createElementNS (SVGNamespace, "tspan");

         // ---- no need of specifying x since white-space = "pre" takes care of it
         // pato.setAttribute ("x", Math.round(x0));
         // pato.setAttribute ("y", svgposy);
         pato.textContent = text;
         pato.setAttribute ("fill", theme[tag]||DEFCOL);

         svgLineTextEle.appendChild (pato);
      }

      // detect marks for the background rectangles
      //
      function detectMarks (text)
      {
         var regM2 = regMark;
         var mat;

         var pato = document.createElementNS (SVGNamespace, "text");

         pato.setAttribute ("x", NUMLINE_POSX_LINENR);
         pato.setAttribute ("y", svgposy);
         pato.textContent = lineNr + "";
         pato.setAttribute ("style", "fill: " + (theme["linenum"]||DEFCOL) + "; text-anchor: end");

         // first the line number
         svgLine.appendChild (pato);

         while ((mat = regM2.exec (text)) !== null)
         {
            // This is necessary to avoid infinite loops with zero-width matches
            if (mat.index === regM2.lastIndex)
                regM2.lastIndex ++;

            var x0    = Math.round (NUMLINE_POSX_LINETXT + mat.index * CHAX);
            var width = Math.round (CHAX * mat[0].length);

            var pato = document.createElementNS (SVGNamespace, "rect");

            // + 2 pixels left and 3 pixels right

            pato.setAttribute ("x", (x0 - 2));
            pato.setAttribute ("y", Math.round (svgposy - 3 * CHAY / 4));
            //?? pato.textContent = lineNr + "";
            pato.setAttribute ("fill", (theme["mark"]||DEFCOL));
            pato.setAttribute ("width", width + 4);
            pato.setAttribute ("height", CHAY);

            // first the line number
            svgLine.appendChild (pato);
         }
      }

      function procesoLinea (text)
      {
         detectMarks (text);
         // special comment with " * " or # only detectable at the beginning of the line
         // note : in C the line
         //      *myvar = xx;
         //      is legal and very probable
         //      what is still legal but no so probable is the style
         //      * myvar = xx;
         //
         var isAllComment = text && (text.match (/^\s+#/) || text.match (/^\s+\*\s/));
         if (isAllComment)
            entagText ("comment", text);
         else {
            if (text.length > MAX_LEN_TO_BE_HIGHTLIGHTED)
            {
                processWords (text.substr(0, MAX_LEN_TO_BE_HIGHTLIGHTED));
                entagText ("normal", text.substr(MAX_LEN_TO_BE_HIGHTLIGHTED));
            }
            else processWords (text);
         }

         svgLine.appendChild (svgLineTextEle);
         return svgLine;
      }

      function processWords (text)
      {
         // compromise about comments
         //
         // all "/*" "//" and "<!" as commented line
         // this not detect closing multi line comments (*/) and may
         //
         // Note: detecting correctly multiline comments /**/ require too much effort specially if we are dealing
         //       only with snipets of code that may belong to a huge code
         //
         var regComm = /\/\/|\/\*|<!/;
         var regLit  = /[\"']/;

         // 1. detect possible comment and/or literals
         //
         var matComm = regComm.exec(text);
         var matLit = regLit.exec(text);

         if (matComm && (!matLit || matLit.index > matComm.index))
         {
            // comment wins
            return processWords (text.substr (0, matComm.index)) +
                   entagText ("comment", text.substr (matComm.index));
         }

         if (matLit)
         {
            // there is a literal and no comment before
            var simb = matLit[0].charAt(0);
            var cierraLit = matLit.index + 1;
            while (cierraLit < text.length)
            {
               while (cierraLit < text.length && text.charAt (cierraLit) !== simb)
                  cierraLit ++;
               if (cierraLit >= text.length) break;
               if (text.charAt (cierraLit-1) === '\\')
                  cierraLit ++;
               else if (cierraLit+1 < text.length && text.charAt (cierraLit+1) === simb)
                  cierraLit += 2;
               else break; // found !
            }

            return processWords (text.substr (0, matLit.index)) +
                   entagText ("literal", text.substr (matLit.index, 1 + cierraLit - matLit.index)) +
                   processWords (text.substr (cierraLit + 1));
         }

         // 2. NO COMMENT, NO STRING, PROCEED TO SEPARATE KEY WORDS
         //
         agentsWords.setData (text);
         var lama;
         var linFormated = [];

         while ((lama = agentsWords.nextMatch ()) !== null)
         {
            entagText ("punkt", lama.prevStr);
            if (lama.agent === "number")
               entagText ("num", lama.matchStr);
            else if (lama.agent === "space")
               entagText (null, lama.matchStr); // spaces have to appended to the text!
            else if (lama.agent === "word") {
               // check if it is a key word ...
               if (ARR_ALL.indexOf (lama.matchStr) !== -1)
                  entagText ("key", lama.matchStr);
               else
                  entagText ("normal", lama.matchStr);
            }
         }

         entagText ("punkt", agentsWords.getData ());
      }
   }
}
