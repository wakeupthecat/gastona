/*
Copyright (C) 2017 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

// This library contain two funtions to pack and unpack data in and from http messages
//
//    function httPack   (parconfig, unidata)
//    function httUnpack (bodytxt, httresp)
//

// ----------------------------------------------
// function httPack (parconfig, unidata)
//
//    desc: Packs data to fit in a http request message, using for that the URI query part, headers and the body
//
//    returns : object with the elements
//                 { onelineparams: string,
//                   headers: { header: value, ...},
//                   body : string
//                 }
//
//              the object can be used to form the final ajaxPOST message
//
//    option 1 : (typeof parconfig === "string")
//
//       this is actually the simplest case
//       parconfig is interpreted as the URI query part literally and
//       no data from unidata is packed at all
//
//       Example:
//          httpPack ("myid=89281&myname=Bellguy");
//
//    option 2 : (typeof parconfig === "object")
//
//      here parconfig may contain one or more of following elements
//
//          params : string                     The value is literally the query part of the URI
//          or
//          params : { param: value, ... }      a query part of the URI will be formed as "param=encodeUTF8(value)&..."
//
//          headers : { header: value, ... }    headers will not be encoded!
//
//          body : string                       Then this will be the body
//
//          or
//          bodyVars  : [variable, ...]         Body will contain the variables specified in the array encodedUTF8 if not specified the opposite
//          bodyVarsFormat: eva|json|propval    Defines how the variables are formated in the body, default is eva
//
//          encodeUTF8 : true|false             Default and if encodeUTF8 is undefined or null is true!
//
//     NOTE: if parconfig is an object and no "body" is defined and no "bodyVars" is defined or bodyVars is undefined or null
//           then ALL variables contained in unidata will be packed in the body
//
//    Examples:
//
//       var prepost = httpack ("id=myId&name=MiNombre");
//       var prepost = httpack ({ params: "id=myId&name=MiNombre" });
//       var prepost = httpack ({ params: { id: "myId", name: "MiNombre" }});
//       var prepost = httpack ({ params: { id: "myId", name: "MiNombre" },
//                                headers : { XmyHeader1 : "valor", xETC: "nomaspues" },
//                                body : "este es \n mi body!",
//                              });
//       var prepost = httpack ({ params ....
//                                headers ....
//                                bodyVarsFormat : "eva",
//                                bodyVars : [ "myvar", "eImportantField" ],
//                                encodeUTF8: false,
//                              });
//
//       var prepost = httpack ({ params ....
//                                headers ....
//                                bodyVarsFormat : "json",
//                                bodyVars : null,
//                                encodeUTF8: false,
//                              });
//
//
//     NOTE: there is no need of specifying the format chosen for the body, the server should know it
//
function httPack (parconfig, unidata)
{
   var reto = { onelineparams : "", headers : {}, body: "" };
   var heads = {};
   var body = "";
   var oneline = [];
   var encodeUTF8 = true;

   if (typeof parconfig === "string")
        oneline = [ parconfig ]; // no encode utf8
   else
   {
      parconfig = parconfig || { };

      encodeUTF8 = parconfig["encodeUTF8"] !== false; // always default true

      //get headers directly (no encode)
      //
      reto.headers = parconfig["headers"]||{};

      //params for oneline params
      //
      var parline = parconfig["params"]||parconfig["parameters"]||{};
      if (typeof parline === "string")
         oneline = [ parline ] ;
      else
         for (var pp in parline)
            oneline.push (pp + "=" + encodeOrRaw (parline[pp]));

      body = parconfig["body"];
      if (typeof body !== "string")
         body = formatBody (parconfig["bodyVars"], parconfig["bodyVarsFormat"]||"eva");
   }

   reto.onelineparams = oneline.join ("&");
   reto.body = body;

   return reto;

   function encodeOrRaw (str)
   {
      // additionally convert ' to %27 which will be decoded correctly by decodeURIComponent
      //
      return encodeUTF8 ? encodeURIComponent ((""+str).replace (/'/g, "%27")): str+"";
   }

   function formatBody (bodyVariables, format)
   {
      function encodeEva (indx)
      {
         if (!encodeUTF8)
            return unidata[bodyVariables[indx]];

         // NOTE:
         // if encode then do a copy!
         // we don't want to change our data just send it encoded
         //
         var cop = [];
         var obj = unidata[bodyVariables[indx]];
         for (var ii in obj) {
            var lina = [];
            for (var jj in obj[ii])
               lina.push (encodeOrRaw (obj[ii][jj]));
            cop.push (lina);
         }
         return cop;
      }

      function getEvaFlat (va)
      {
         function evaVar2Flat (obj)
         {
            var str = [], lin;
            for (var row in obj)
               for (var col in obj[row])
                  str.push ((col > 0) ? ",":"" + obj[row][col]);
            return str.join ("\n");
         }

         return encodeOrRaw (evaVar2Flat (unidata[bodyVariables[va]]));
      }

      if (! bodyVariables)
      {
         // all variables, if don't want this then set bodyVariables to []
         bodyVariables = [];
         for (var ii in unidata)
            bodyVariables.push (ii);
      }

      var vv;
      var sal = "";

      if (format === "propval")
      {
         // each in one line with the format
         //   prop:value
         //
         var lans = [];
         for (vv in bodyVariables)
            lans.push (bodyVariables[vv] + ":" + getEvaFlat (vv));
         sal = lans.join("\n");
      }
      else if (format === "json")
      {
         // as JSON
         //
         var oelect = { };
         for (vv in bodyVariables)
            oelect [bodyVariables[vv]] = encodeEva (vv);
         sal = JSON.stringify(oelect);
      }
      else if (format === "eva")
      {
         // prepare the body
         //
         var evaObj = evaFileObj ("#data#\n");
         var bodyUnit = evaObj.obj["data"];

         for (vv in bodyVariables)
            bodyUnit[bodyVariables[vv]] = encodeEva (vv);

         sal = evaObj.toText ();
      }
      else
      {
         alert ("ERROR: calling formatBody with not supported format [" + format + "]");
      }

      return sal;
   }
}

// ----------------------------------------------
//    function httUnpack (bodytxt, httresp)
//
//    desc: Build a response object from the http response unpacking it
//
//    returns : object with the properties obj-params, obj-headers, obj-body
//
//          load the response body as specified in Content-type (eva or json format) into obj-body 
//          extract parameters from the header XParamsInOneLine and set them in obj-params as properties
//          set all headers and set them in obj-headers
//
//    1- Unpacking the body:
//
//       First unpacks the body according to the format contained in the header "Content-Type"
//       specifically only three formats are recognized "text/eva" and "text/json".
//
//       if format "text/eva" the body contain an eva unit #data# with the variables, for example
//
//          #data#
//
//             <mivar>     one, two, three
//             <mivar2>    Pablito clavo un clavito
//             <miTabla>
//                   id, name
//                   21, Evaristo
//                   77, Carolo
//
//       if format "text/json" the body contain a json object, for example
//
//         "obj-body" : { "mivar": [ "one", "two", "three" ],
//                       "whatsoever" : { "etc": "..." }
//                      }
//
//    2- Unpacking parameters
//
//       If the header XParamsInOneLine is found then it is interpreted as a URI query and all
//       the variables are set in the property "obj-params" of the result object always decoding them UTF8
//
//    3- Setting headers in the result object
//
//       All response headers are set in the property "obj-headers" of the result object
//
//
function httUnpack (bodytxt, httresp)
{
   var respObj = {};
   var subobj = {};

   // check Content-type for json or eva and set the object accordingly
   //
   var str = httresp.getResponseHeader ("Content-Type");
   if (str)
   {
      subobj = {}
      splitArr = str.split (";");
      if (splitArr.indexOf ("text/eva") !== -1)
      {
         // load n units (an eva file)
         subobj = evaFileStr2obj (bodytxt);
      }
      else if (splitArr.indexOf ("text/evas") !== -1)
      {
         // undocumented (experimental) support for single anonymous eva unit
         // load a unnamed unit
         subobj = evaFileStr2obj ("#anonima#\n" + bodytxt) ["anonima"];
      }
      else if (splitArr.indexOf ("text/json") !== -1)
      {
         subobj = JSON.parse(bodytxt);
      }
   }
   if (subobj.length === 0)
   {
      // don't know how to process the body, deliver it as it is
      //
      subobj["rawBody"] = bodytxt;

   }
   // >>> obj-body
   respObj["obj-body"] = subobj;
   

   // check XParamsInOneLine and set individual variables
   //
   str = httresp.getResponseHeader ("XParamsInOneLine");
   if (str)
   {
      subobj = {};
      var splitArr = str.split ("&");
      for (var vv in splitArr)
      {
         var pair = splitArr[vv].split ("=");
         if (pair.length === 2)
         {
            subobj [pair[0]] = decodeURIComponent (pair[1]);
         }
         else console.log ("ERROR on XParamsInOneLine variable [" + splitArr[vv] + "]");
      }
      respObj["obj-params"] = subobj;
   }

   // add all headers into "obj-headers", Note: getAllResponseHeaders () returns a literal string with all headers
   //
   subobj = {};
   var respArr = httresp.getAllResponseHeaders ().split ("\n");   
   for (var hh in respArr)
   {
      var hname = respArr[hh].split(":", 1);
      if (hname && hname[0])
         subobj[hname[0]] = httresp.getResponseHeader (hname[0]);
   }
   respObj["obj-headers"] = subobj;

   return respObj;
}
