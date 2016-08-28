/*
Copyright (C) 2015 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

/**
   @author Alejandro Xalabarder
   @date   2015.07.26

   @file   jGastona.js

   @desc
      Logic in javascript emulating as close as possible the one
      used in gastona + javaj + listix + mensaka in gastona java project

      It is in its very first development phase ...

      what is implemented for now is

         - automatic generation of html widgets based on the layout component's first character
           including binding with data model (eva unit data)

         - handling of messages : msg to widgets, msg from widgets, msg responseAjax

         - AJAX facility methods : AJAXFormatBody and AJAXSend


*/

//"use strict";

function jGastona (evaConfig, existingPlaceId)
{
   "use strict";
   var dataUnit,
       listixUnit,
       corpiny,
       layMan,
       responseAjaxUnit,   // of AJAX
       javajzWidgets       // of widgetFactory
       ;
   var minWidth = -1;
   var minHeight = -1;


   // default action
   loadJast (evaConfig);
   document.body.onresize = function () { adaptaLayout () };

   return {
      // public functions to export

      getLayoutMan       : function () { return layMan; },
      mensaka            : mensaka,
      getIdValue         : function (id) { var ele = document.getElementById(id); return (ele) ? ele.value : getDataCell (id); },
      getData            : getDataCell,
      setData            : setData,
      setVarTable_DimVal : setVarTable_DimVal,
      getCellEvaUnit     : getCellEvaUnit,
      adapta             : adaptaLayout,
      mask               : function (a, b) { if (layMan) { layMan.maskLayoutId (a, b); adaptaLayout (); } },
      unmask             : function (a)    { if (layMan) { layMan.unmaskLayoutId (a); adaptaLayout (); } },


      // part ajax ...
      getAjaxResponse  : function () { return responseAjaxUnit; },
      AJAXPost         : AJAXgenericPost,
      AJAXSendData     : AJAXSendData,        // send data as evanit, prop:value or json
      AJAXSendBody     : AJAXSendData,        // alias to be deprecated ...
      AJAXUploadFile   : AJAXUploadFile,      // upload one file (NOTE: only one file!)
      AJAXLoadRootJast : AJAXLoadRootJast,    // ask the server for a jast file to generate html for the page
      AJAXgetIdContent : AJAXgetIdContent,    // ask the server for content for the id, on resposte the content will be updated automatically
      AJAXgetIdMultipleContents: AJAXgetIdMultipleContents,

      // getDataUnit    : function () { return dataUnit; }
   };

   // due to IE compatib.
   function getWindowWidth () { return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth; };
   function getWindowHeight () { return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight; };
   if (!String.prototype.startsWith) { String.prototype.startsWith = function(seas, pos) { pos = pos || 0; return this.indexOf(seas, pos) === pos; }; }

   function adaptaLayout ()
   {
      if (!layMan) return;

      var dx = getWindowWidth ()  - 15; // 15 is an empiric number ...
      var dy = getWindowHeight () - 15;

      var lali = layMan.guiConfig["layoutWindowLimits"]; // <layoutWindowLimits> mindx, mindy, maxdx, maxy
      if (lali && lali[0])
      {
         // note: lali[0] is the first row of the variable (the only one)
      //
         var mindx = parseInt(lali[0][0]|"0");
         var mindy = parseInt(lali[0][1]|"0");
         var maxdx = parseInt(lali[0][2]|"0");
         var maxdy = parseInt(lali[0][3]|"0");
         dx = Math.max (dx, mindx);
         dy = Math.max (dy, mindy);
         if (maxdx > 0) dx = Math.min (dx, maxdx);
         if (maxdy > 0) dy = Math.min (dy, maxdy);
      }

      if (layMan)
         layMan.doLayout(dx, dy);
   }

   function loadJast (evaConfig)
   {
      javajzWidgets = {};
      responseAjaxUnit = {};
      dataUnit = {};
      listixUnit = {};
      layMan = undefined;

      if (! evaConfig ) return;

      dataUnit = evaConfig["data"] || {};
      listixUnit = evaConfig["listix"] || {};

      // ensure corpinyo (STAMM) is a root element in the document's body
      //
      if (!corpiny)
      {
         var STAMM = existingPlaceId ? existingPlaceId: "jGastonaStammHtmlElem";
         if (! document.getElementById(STAMM))
            document.body.innerHTML = "<div id='" + STAMM + "' style = 'position:relative;'></div>";
         corpiny = document.getElementById(STAMM);
      }
      if (!corpiny) alert ("ERROR no " + STAMM + " no fun!");

      // remove last main jast layout if any
      // here candidate to push instead
      while (corpiny.hasChildNodes())
      {
         corpiny.removeChild(corpiny.firstChild);
      }

      // load all components and return the manager
      //
      layMan = layoutManager (evaConfig, onAddWidget);
      adaptaLayout ();
   }

   function getCellEvaUnit (unit, eva, row, col)
   {
      return unit[eva] ? unit[eva][row||"0"][col||"0"]||"": "";
   }

   function getDataCell(name, row, col)
   {
      return getCellEvaUnit (dataUnit, name, row, col);
   }

   function setDataCell (name, value, row, col)
   {
      // create variable if needed
      if (!dataUnit[name])
         dataUnit[name] = [[""]];

      // create row if needed
      if (!dataUnit[name][row||"0"])
         dataUnit[name][row||"0"] = [ "" ];

      dataUnit[name][row||"0"][col||"0"] = value||"";
   }

   function setData (name, value)
   {
      // create on demand
      if (typeof value === "string")
      {
         setDataCell (name, value);
      }
      else if (Array.isArray (value) && value.length > 0 && Array.isArray (value[0]))
      {
         dataUnit[name] = value;
      }
      else
      {
         alert ("Error: setData \"" + name  + "\", the value is not a string nor looks like an eva variable");
      }
      var wiz = getzWidgetByName (name);
      if (wiz)
         updatezWidget (wiz);
   }

   function mensaka (msg)
   {
      // handling messages for widgets i.e. "zwidget data!" (update data of zwidget)
      // (javaj task)
      var ii = msg.indexOf (" ");
      if (ii > 0)
      {
         var wnam = msg.substr (0,ii); // widget name i.e. "bBoton"
         var wmet = msg.substr (ii+1); // method      i.e. "data!"
         if (javajzWidgets[wnam])
         {
            // widget found, so we will return
            if (javajzWidgets[wnam][wmet])
                 javajzWidgets[wnam][wmet] ();
            else console.log ("Error: widget " + wnam  + " has no method '" + wmet + "'");
            return;
         }
      }

      // look for the variable <-- message>, first in data else in listix
      // (listix task)
      // Note : here it is done in an "interpreter fashion",
      //        another approach is to generate proper functions and listeners previosly
      //
      var fbody = dataUnit["-- " + msg] || listixUnit["-- " + msg];
      if (! fbody)
      {
         // message not subscribed! ignore it
         console.log ("ignoring mensaka \"" + msg  + "\"");
         return;
      }

      executeListixFormat (fbody);
   }

   function executeListixFormat (fbody)
   {
      var strBody = ""
      for (var ii in fbody)
         strBody += fbody[ii] + "\n";

      eval (strBody);
   }

   // ============ DATA AND VARIABLES HANDLING
   //
   function setVarTable_DimVal (arrVarNames)
   {
      var vara = [[ "dimension", "value" ]];
      for (var fi in arrVarNames)
      {
         vara.push ( [ arrVarNames[fi], getDataCell (arrVarNames[fi]) ]);
      }

      dataUnit["varTable_DimVal"] = vara;
   }


   // --------- START PART widgetFactory
   //

   function setValueToElement (element, valueStr)
   {
      if (element)
      {
         if (typeof element.value === "string")
              element.value = valueStr;
         else element.innerHTML = valueStr;
      }
   }


   function onAddWidget (name)
   {
      if (! name || name.length == 0) return;

      var zwid;

      var updateSimpleLabel = function () {
         //setValueToElement (this, dataUnit[name] ? getDataCell (name) : name.substr(1));
         this.innerHTML = (dataUnit[name] ? getDataCell (name) : name.substr(1));
      }
      var updateSimpleValue = function () {
         setValueToElement (this, getDataCell (name));
      }
      var updateSimpleSrc = function () {
         this.src = getDataCell (name);
      }
      var updateResetValue = function () {
         this.value = '';
      }
      var updateImage = function () {
         // this.src = getDataCell (name);
         this["style"]["background-image"] = "url('" + getDataCell (name) + "')";
      }

      var assignValue = function () {
         dataUnit[name][0] = [ this.value||"?" ];
      };
      var assignText = function ()
                       {
                           dataUnit[name] = [ [ ] ];
                           var text = this.value||"?";
                           var rows = text.split("\n");
                           for (var rr in rows)
                              dataUnit[name][rr] = [ rows[rr] ];
                       };

      var signalName = function () {
         mensaka(name);
      }
      var hayClassOf = dataUnit["class of " + name];
      var widgetclass = hayClassOf ? hayClassOf[0][0] : name;

      switch (widgetclass.charAt (0))
      {
         case 'd':
            zwid = fabricaStandard ("div", name, { "data!": updateSimpleLabel  } );
            break;
         case 'n':
            zwid = fabricaStandard ("a", name, { href: "login", "data!": updateSimpleLabel } );
            break;
         case 'b':
            zwid = fabricaStandard ("button", name, { onclick: signalName, "data!": updateSimpleLabel } );
            break;
         case 'e':
            zwid = fabricaStandard ("input", name, { type: "text", onchange: assignValue, "data!": updateSimpleValue } );
            break;


         // NOTE: actually we don't need a special character for submit
         //       we can use a button and set its property "type" to submit (<bSendIt type> //'submit)
         // case 'u':
         //    zwid = fabricaStandard ("input", name, { type: "submit", onchange: assignValue, "data!": updateSimpleLabel } );
         //    break;

         case 'f': // file to upload
                   // Note set data to this component make no sense but to empty string for reseting it
                   // for instance, if we try anything else in Chrome we get :
                   // "Failed to set the 'value' property on 'HTMLInputElement': This input element accepts a filename, which may only be programmatically set to the empty string."
            zwid = fabricaStandard ("input", name, { type: "file", onchange: signalName, "data!": updateResetValue } );
            break;

         case 'm': // image
            // zwid = fabricaStandard ("img", name, { "data!": updateImage } );
            zwid = fabricaStandard ("div", name, { "data!": updateImage } );
            break;

         case 'p': // password
            zwid = fabricaStandard ("input", name, { type: "password", onchange: assignValue, "data!": updateSimpleValue } );
            break;
         case 'l':
            zwid = fabricaStandard ("label", name, { "data!": updateSimpleLabel } );
            break;

         case 'x':
            {
               var updata = function () {
                     var tex = "", row;
                     for (row in dataUnit[this.id])
                        tex += dataUnit[this.id][row] + "\n";
                     setValueToElement (this, tex);
                  }
               zwid = fabricaStandard ("textarea", name, { "data!": updata, onchange: assignText } );
            }
            break;


         case 't': // simple table
            {
               var updata = function () {
                     var etabla, rowele, colele, row, col, evaData = dataUnit[this.id];

                     // create new html table
                     while (this.hasChildNodes())
                     {
                        this.removeChild(this.firstChild);
                     }

                     etabla = document.createElement ("table");
                     etabla["id"] = this.id + "-table"; // e.g <div id="tMiTabla"> <table id="tMitabla-table">...

                     for (row in evaData)
                     {
                        if (row === "0" && evaData[0].length == 1 && evaData[0][0] === "")
                        {
                           // row === "0" only one column and empty ==> no headers
                           // no headers!
                        }
                        else
                        {
                           rowele = document.createElement ("tr");

                           for (col in evaData[row])
                           {
                              colele = document.createElement (row === "0" ? "th": "td");

                              // use instead ? colele.value = evaData[row][col];
                              setValueToElement (colele, evaData[row][col]);
                              rowele.appendChild (colele);
                           }
                           etabla.appendChild (rowele);
                        }
                     }
                     this.appendChild (etabla);
                  }
               zwid = fabricaSimpleTable (name, { "data!": updata });
            }
            break;

         case 'c': // combo
         case 'r': // radio group
         case 'k': // checkbox group
         case 'i': // list
            {
               var labels = [], values = [];
               // Now assume list of "value, label" for the data (with column names in the first row!)
               //
               for (var row in dataUnit[name])
               {
                  if (row !== "0")
                  {
                     values.push (dataUnit[name][row][0]||"?");
                     labels.push (dataUnit[name][row][1]||"?");
                  }
               }
               var orient = dataUnit[name + " orientation"]||"X";

               //(o) TODO/jGastona/fabrica_zWidgets why not ?  zwid = fabricaSelect (...

               if (name.charAt (0) == 'c')
                  corpiny.appendChild (fabricaSelect (name, values, labels, false));
               if (name.charAt (0) == 'i')
                  corpiny.appendChild (fabricaSelect (name, values, labels, true));
               if (name.charAt (0) == 'r')
                  corpiny.appendChild (fabricaGrupo ("radio", orient, name, values, labels));
               if (name.charAt (0) == 'k')
                  corpiny.appendChild (fabricaGrupo ("checkbox", orient, name, values, labels));
            }
            break;
      }

      if (zwid)
      {
         // collect it
         javajzWidgets[name] = zwid;
         corpiny.appendChild (zwid);

         updatezWidget (zwid);

         // experimental! all widgets need data!
         if (!dataUnit[name]) 
            dataUnit[name] = [ [ "" ] ];         
      }
   }

   function getzWidgetByName (widName)
   {
      return javajzWidgets[widName];
   }

   function updatezWidget (zwidget)
   {
      if (! zwidget) return;

      if (zwidget["data!"])
         zwidget["data!"] (); // update data
      else
         alert ("ERROR (updateWidget) zwidget /" + zwidget.id + "/ with no 'data!' message");
   }

   // converts an string into a "string", "object" or js "function"
   // var str = str2jsVar ("\"sisie");
   // var arr = str2jsVar ("[ 'sisie', 'nono', 234, [1, 2] ]");
   // var fun = str2jsVar ("alarm('jol')");
   //
   function str2jsVar (str)
   {
      var str2 = "";
      if (typeof str !== "string")
      {
         for (var ii = 0; ii < str.length; ii ++)
            str2 = (ii === 0 ? "": str2 + "\n") + str[ii];
      }
      else str2 = str;

      if (str2.match(/^\s*[\"\']/))
         return str2.substr(1);

      if (str2.match(/^\s*[\[\{]/))
         return eval ("(function () { return " + str2 + ";}) ()");

      return function () { eval (str2); }
   }

   function fabricaStandard (typestr, name, atts)
   {
      var ele = document.createElement (typestr);   // "label" "button" etc
      ele["id"] = name;
      ele.style.visibility = "hidden";
      ele.spellcheck = false; // per default FALSE !!!
      for (var aa in atts)
      {
         ele[aa] = atts[aa];
      }

      // ensure a variable in data unit if not already exists
      // for the value (NOTE: for some reason it does not work for typestr === "textarea")
      if (typestr === "input" && !dataUnit[name])
         dataUnit[name] = [[ "" ]];

      for (var dd in dataUnit)
      {
         // i.e. <eText onchange> //alarm("me change!");
         if (dd.startsWith (name + " "))
         {
            var attrib = dd.substr(name.length + 1);
            var jscode = dataUnit[dd];

            ele[attrib] = str2jsVar (jscode);
         }
      }

      // if (text)
      //   ele.appendChild (document.createTextNode(text));

      return ele;
   }

   function fabricaCombo (name, arrOp, arrLab)
   {
      return fabricaSelect (name, arrOp, arrLab, false);
   }

   function fabricaList (name, arrOp, arrLab)
   {
      return fabricaSelect (name, arrOp, arrLab, true);
   }

   function fabricaSelect (name, arrOp, arrLab, ismultiple)
   {
      var ele = document.createElement ("select");
      if (ismultiple)
         ele["multiple"] = "si";

      ele["id"] = name;
      ele.style.visibility = "hidden";
      ele["onchange"] = function () { mensaka(name) };
      for (var ite in arrOp)
      {
         var subele = document.createElement ("option");
         subele["value"] = arrOp[ite];
         subele["data!"] = function () { }; //(o) TOREVIEW_jGastona_update message in subelements, is it really needed ?
         subele.appendChild (document.createTextNode(arrLab[ite]));
         ele.appendChild (subele);
      }

      return ele;
   }

   function fabricaGrupo (tipo, orient, name, arrOp, arrLab)
   {
      var ele = document.createElement ("div");
      ele["id"] = name;
      ele.style.visibility = "hidden";
      ele["onchange"] = function () {  mensaka (name); }; // alert ("elegido = " + dataUnit[name + " selected.value"]);
      for (var ite in arrOp)
      {
         var subele = document.createElement ("input");
         subele["type"] = tipo;
         subele["name"] = name;
         subele["value"] = arrOp[ite];
         subele["label"] = arrLab[ite];
         subele["data!"] = function () { };
         subele["onchange"] = function () {
                   dataUnit[name + " selected.value"] = [[ this.value||"?" ]];
                   dataUnit[name + " selected.label"] = [[ this.label||"?" ]];
                   dataUnit[name + "_value"] = [[ this.value||"?" ]]; // to have a single variable
                   };
         if (ite !== "0" && (orient == "Y" || orient == "V"))
            ele.appendChild (document.createElement ("br"));
         ele.appendChild (subele);
         ele.appendChild (document.createTextNode(arrLab[ite]));
      }

      return ele;
  }

  function fabricaSimpleTable (name, atts)
  {
      var ele = document.createElement ("div");
      ele["id"] = name;
      ele["widgetype"] = "t"; // to be used ...
      ele.style.visibility = "hidden";

      for (var aa in atts)
      {
         ele[aa] = atts[aa];
      }

      return ele;
  }

   // --------- START PART AJAX
   //


   // ============ AJAX stuff (AJAX approach V0.11)
   //
   //    Uses Eva.js of prop-val text for the body of requests and responses
   //    EXAMPLE
   //        AJAXSendBody ("addApp", ["title", "source", "code" ], format);
   //
   //    ajax request format = "eva"
   //                  #unitAjaxRequest#
   //                     <title>  //value of title variable
   //                     <source> //a good friend
   //                     <code>   //#!/usr/bin/gastona ... (UFT-8 escaped!)
   //
   //    ajax request format "propval"
   //                title: value of title variable
   //                source: a good friend
   //                code: #!/usr/bin/gastona ... (UFT-8 escaped!)
   //
   //    ajax response (depend on the server)
   //        #unitAjaxResponse#
   //
   //          <result> ok
   //          <resultStr> //app mirelos.gast has been added successfully
   //
   //
   //    the response body is placed in the variable responseAjaxUnit accessible through getAjaxResponse ()
   //    after processing the response the message "ajaxResponse xxxx" is sent
   //    for example if the request was "login" then the message will be "ajaxResponse login"
   //

   // uses var responseAjaxUnit

   function jaxGetHttpReq ()
   {
      if (window.XMLHttpRequest)
         return new XMLHttpRequest ();
      else if (window.ActiveXObject)
         return new ActiveXObject("Microsoft.XMLHTTP");

      // hardly probable ...
      alert("Your browser does not support AJAX!");
   }

   function startsWith (s1, s2)
   {
      return s1.slice(0, s2.length) === s2;
   }

   function jaxDefaultResponseFunc (bodytxt, httresp)
   {
      var RESPUNITNAME = "unitAjaxResponse";

      if (startsWith (bodytxt, "#" + RESPUNITNAME + "#"))
           responseAjaxUnit = evaFileStr2obj (bodytxt)[RESPUNITNAME] || { };
      else responseAjaxUnit = { "ajaxRESP-rawBody": [[ bodytxt ]] };
      var hparval, np = 1;

      // add variables for "ajaxRESP-parameterX" named headers found in the response
      do
      {
         hparval = httresp.getResponseHeader ("ajaxRESP-parameter" + np);
         if (hparval)
            responseAjaxUnit ["ajaxRESP-parameter" + np] = [[ hparval ]];
         np ++;
      } while (hparval);
   }

   function AJAXFormatBody (postString, bodyVariables, format, raw)
   {
      var vv;

      function getValueVariable (va)
      {
         if (!raw &&
              dataUnit[bodyVariables[va]] &&
              dataUnit[bodyVariables[va]].length == 1 &&
              dataUnit[bodyVariables[va]][0].length == 1)
            return [[ encodeURIComponent (dataUnit[bodyVariables[va]]) ]];

         return dataUnit[bodyVariables[va]] || "";
      }

      format = format || "eva";

      if (! bodyVariables)
      {
         // all variables, if don't want this then set bodyVariables to []
         bodyVariables = [];
         for (var ii in dataUnit)
            bodyVariables.push (ii);
      }

      var sal = "";

      if (format === "propval")
      {
         // each in one line with the format
         //   prop:value
         //
         for (vv in bodyVariables)
            sal += "\n" + bodyVariables[vv] + ":" + getValueVariable (vv);
      }
      else if (format === "json")
      {
         // each in one line with the format
         //   prop:value
         //
         sal = "{";
         for (vv in bodyVariables)
            sal += "'" + bodyVariables[vv] + "' : '" + getValueVariable (vv) + "', ";
         sal += "}";
      }
      else if (format === "eva")
      {
         // to be deprecated ... ???
         // prepare the body
         //
         var evaObj = evaFileObj (
                       "#unitAjaxRequest#"    + "\n" +
                       "   <_jGastonaVersion> 1.0" + "\n"
                       );
         var bodyUnit = evaObj.obj["unitAjaxRequest"];

         for (vv in bodyVariables)
            bodyUnit[bodyVariables[vv]] = getValueVariable (vv);

         sal = evaObj.toText ();
      }
      else
      {
         alert ("ERROR: calling AJAXFormatBody with not supported format [" + format + "]");
      }

      return sal;
   }

   function ajaxGenericPreProcessResponse (httresp)
   {
      //
   }


   function AJAXgenericPost (sendStr, bodyStr, objPOSTHeader, respFunction)
   {
      var httpero = jaxGetHttpReq ();
      if (!httpero) return false;

      if (!respFunction)
         respFunction = jaxDefaultResponseFunc;

      // add callback
      httpero.onreadystatechange = function () {
         if (httpero.readyState == 4 && httpero.status == 200) {
            ajaxGenericPreProcessResponse (httpero);
            respFunction (httpero.responseText, httpero);
            mensaka ("ajaxResponse " + sendStr);
         }
      }

      httpero.open ("POST", sendStr, true);

      if (objPOSTHeader) {
         for (var indx in objPOSTHeader)
            httpero.setRequestHeader(indx, objPOSTHeader[indx]);
      }

      httpero.send (bodyStr);
   }

   function AJAXSendData (postString, bodyContent, format, raw)
   {
      var bodyText = (typeof bodyContent === "string") ? bodyContent: AJAXFormatBody (postString, bodyContent, format, raw);

      AJAXgenericPost (postString, bodyText);
   }

   function AJAXUploadFile (fileElement, postMsg, postHeaders)
   {
      var filename = fileElement.files[0];
      if (! filename || filename === "") return false;

      var formo = new FormData ();
      formo.append ("filename", filename); // we add it but actually the mico server don't read it!

      AJAXgenericPost (postMsg, formo, postHeaders );
   }

   function AJAXLoadRootJast (jastName)
   {
      AJAXgenericPost ("loadRootJast", "", { "ajaxREQ-jastName": jastName },
                        function (txt) {
                           loadJast (evaFileStr2obj (txt));
                        }
                        );
   }

   function setContentsFromBody (idname, bodystr, multiple)
   {
      var mainbody = "";
      if (multiple)
      {
         // format body = sub-header sub-body
         //
         // subhead1:val1
         // subhead2:val2
         // :---body
         // subbody

         var textArr = bodystr.split("\n");

         var hh = 0;
         while (hh < textArr.length)
         {
            var idval = /([^:]*):(.*)/.exec (textArr[hh ++]);
            if (!idval) continue; // not "id:val" nor ":" ?
            if (idval[1] === "") break; // end of "id:val"
            setData (idval[1], idval[2]);
         }
         for (var bb = hh; bb < textArr.length; bb ++)
            mainbody = mainbody + (bb != hh ? "\n": "") + textArr[bb];
      }
      else
      {
         // format body = directly the content
         mainbody = bodystr;
      }

      var ele = document.getElementById (idname);
      if (ele)
         setValueToElement (ele, mainbody);
   }

   // depending of the type of the first parameter there are two possible syntaxes:
   //
   //    //1 using implicit header names ajaxREQ-id and ajaxREQ-param
   //    jgas.AJAXgetIdContent ("myTextArea", "content.txt");
   //
   //    //2 passing more headers
   //    jgas.AJAXgetIdContent ("myTextArea", { "ajaxREQ-id": "myTextArea", "theFile": "content.txt" });
   //
   //    if multiple is true then the server will send additional id:value pairs using the format
   //          id1:value
   //          id2:value
   //          :
   //          mainid value
   //
   function AJAXgetIdContent (idname, param, multiple)
   {
      // either param is a string and we add a header "ajaxREQ-param"
      // or it is actually an objects with the headers to be send
      // in the end we add always "ajaxREQ-id"
      //
      var heads = (typeof param === "string") ? { "ajaxREQ-param": param }: (param||{});
      heads["ajaxREQ-id"] = idname;

      AJAXgenericPost ("getIdContent", "", heads,
         function (txt) {
            setContentsFromBody (idname, txt, multiple);
         }
      );
   }

   function AJAXgetIdMultipleContents (idname, param)
   {
      AJAXgetIdContent (idname, param, true);
   }
}