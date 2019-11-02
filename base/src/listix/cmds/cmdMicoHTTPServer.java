/*
library listix (www.listix.org)
Copyright (C) 2014-2109 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/


/*
   //(o) WelcomeGastona_source_listix_command MicoHttpServer

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.


#gastonaDoc#

   <docType>    listix_command
   <name>       MicoHttpServer
   <groupInfo>  internet
   <javaClass>  listix.cmds.cmdMicoHTTPServer
   <importance> 3
   <desc>       //Mini http server using listix language


   <help>
      //
      // Micohttp is a http server that uses listix language to generate the http responses.
      //
      // The server can be started listening to a specific available TCP/IP port.
      // Once started it can accept http requests and respond with contents.
      //
      //## Responding to Http requests
      //
      // Given a general GET request, for instance
      //
      //        GET /myresource?parameters
      //
      // If the file "myresurce" exists as a physical file starting from the current directory where micoHttp is running,
      // then the file is served as response for the request.
      //
      // If the file does not exist then it looks for a listix format or variable with the name "GET /myresource" (in the example)
      // and if the format exists it is executed and the output sent as response for the request.
      //
      // The parameters of the request are passed as if they where variables to the format
      //
      // For example the format
      //
      //       <GET /hola>
      //           //Hola @<nombre>!
      //
      // Will respond with the text
      //
      //        "Hola pavo!"
      //
      // if we do the request
      //
      //        GET /hola?nombre=pavo
      //
      // The rest of methods like POST, PUT, etc are served using formats as done by GET when the file does not exists.
      //
      // For example a POST receiver delegating the task to another listix format
      //
      //       <POST /saveThat>
      //          LSX, saveContent
      //          //ok, done
      //
      //## Variables accesibles while processing a request
      //
      // On each request, if this is going to be processed by a listix format, following
      // information is accessible via these variables
      //
      //        _myMicoName       Name of the mico server that receives the request (name is given in START syntax)
      //        _requestIP        IP of the client that do the request
      //        _bodyMemFileName  Name of the memory file containing the body of the request if any
      //                          (this name can be obtained also as ":mem @<_myMicoName> httpRequestBODY")
      //
      //        _uploadedFilesCount  Number of files to be uploaded in case of upload
      //        _uploadedFileX       File name of file X, where X is a number between 0 and _uploadedFilesCount - 1
      //
      //## Serving files
      //
      // The described way of serving files with the method GET is the default one and it limits
      // the "visibility" to the current directory and subdirectories plus following restrictions:
      //
      // <ul>
      //    <li> Files or paths starting with "hide_", "hidden_" or "nopublic"
      //    <li> Files and folders under the directory "hide" or "hidden"
      // </ul>
      //
      // But micoHttp can also be abilitated to serve any file in the operative system, so a simple
      // file server can be built easily with it. This behaviour can be achieved using the option "FILE SERVER STR"
      // in the syntax START.
      //

   <aliases>
      alias
      MICOHTTPSERVER
      MICOHTTP
      MICO

      MONOHTTPSERVER
      MONOHTTP
      MONO

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Start a mico http server
         2   ,    3      , //Close a mico http server
         3   ,    3      , //Get the port number the server is listening to
         4   ,    3      , //Set a header for the current response of the given server
         5   ,    3      , //Set response parameters for the given server
         6   ,    3      , //Start a mico http server and close it after a timeout with no request (default 6 s)
         7   ,    3      , //Set the maximum size for a file to be uploaded
         8   ,    3      , //Set the maximum timeout when uploading
<! ---- option planed but not implemented
<!       switching dynamically base directory has few applications, also in any of them
<!       the problem with browser cached files cannot be avoided! (start server in dir1 and serve image.png then
<!       switch to dir2 which has a different image.png, since the browser has image.png cached it will not request the second one!)
<!       the use as option of START could make more sense but this can be achieved by changing the current directory directly
<!
<!         9   ,    3      , //Set path of directory or directories where the files can be served from

   <syntaxParams>
      synIndx, name         , defVal    , desc
         1   , START        ,           ,
         1   , serverName   ,           , //Name of the server
         1   , Port         , 0         , //Port number to serve, 0 for having it automatically assigned and -1 (default) for mono-client and launch-browse mode

         2   , STOP         ,           ,
         2   , serverName   ,           , //Name of the server to stop

         3   , GET PORT     ,           ,
         3   , serverName   ,           , //Name of the server to get the port number

         4   , SET RESPONSE HEADER,           ,
         4   , serverName         ,           , //Name of the server
         4   , headerName         ,           , //Header name
         4   , headerValue        ,           , //Header value
         5   , ...                ,           , //further header name-value pairs

         5   , SET RESPONSE VARIABLE,       ,
         5   , serverName         ,           , //Name of the server
         5   , variableName       ,           , //variable name
         5   , variableValue      ,           , //variable value
         5   , ...                ,           , //further variable name-value pairs

         6   , ONE GET      ,           ,
         6   , millisec     , 0         , //Millisecond to wait atfer having served the first request and before closing the server
         6   , serverName   ,           , //Optional name of the server
         6   , Port         , 0         , //Port number to serve, 0 for having it automatically assigned and -1 (default) for mono-client and launch-browse mode

         7   , SET UPLOAD MAX SIZE,          ,
         7   , serverName         ,          , //Server name
         7   , maxSizeInBytes     , 10000000 , //Max size in bytes for a file to upload (default less that 10 MB)

         8   , SET UPLOAD SOCKET TIMEOUT,    ,
         8   , serverName         ,          , //Server name
         8   , maxSizeInBytes     , 40000    , //Max socket timeout in milliseconds, time between partial reads, default is 40 s

<! ---- option planed but not implemented
<!
<!         9   , SET BASE DIR,    ,
<!         9   , directoryPath    ,            , //Path to be set as base directory for serving files. Empty string is current directory also relative paths to it are allowed
<!         9   , additionalDir    ,            , //Path for additional directory
<!         9   , ...              ,            , //Further additional directory

   <options>
      synIndx, optionName  , parameters, defVal, desc

      1      , FILE SERVER STR, string, ":SF:/" , //Tells micoHttp to serve any file of the file system when request a "GET /FSStr/fullPath" where FSStr is the string given in the option as parameter, otherwise micoHttp only serve files from its current directory
      1      , MONOCLIENT     , 0 / 1, 0, //Instructs this server to accept ONLY ONE CLIENT, namely the first one, the client might close the server using the request "GET /acabamico"
      1      , VERBOSE        , level, 12, //Set the verbosity of the server towards standard output
      1      , VERBOSE FILE   , filename, serverName/log, //Set the log file to output the trace generated by the server, the file will be always open for append
      1      , ZIP FILES      , "zipfile, folder",, //Provide a folder within a zip or jar file which entries can be served as files

      6      , VERBOSE        , level, 12, //Set the verbosity of the server towards standard output
      6      , VERBOSE FILE   , filename, serverName/log, //Set the log file to output the trace generated by the server, the file will be always open for append
      6      , ZIP FILES      , "zipfile, folder",, //Provide a folder within a zip or jar file which entries can be served as files

   <examples>
      gastSample

      exerciseDateTime
      micoHttpExample1
      micoAjaxWithForm
      whoareyou
      simple file server
      JS Chrono
      micoServerShowAJAX

   <exerciseDateTime>
      //#javaj#
      //
      //   <frames> main, Sqlite date time demo
      //
      //   <layout of main>
      //      EVA, 10, 10, 5, 5
      //         , X 
      //       X , tExpressions
      //         , lDesc,
      //         , layInteract
      //      200, oSal
      //
      //   <layout of layInteract>
      //      EVA, 4, 4, 4, 4
      //         ,            , X    ,   , 250
      //         , lEdit expression, eExpr, b=,  eValue
      //
      //   <sysDefaultFonts>
      //      Consolas, 12, 0, *
      //
      //#data#
      //
      //   <tExpressions visibleColumns> expression, value
      //
      //#listix#
      //
      //   <main0>
      //      -->, tExpressions data!, sqlSelect, //@<SQL_EXPR_TABLE>
      //      @<help>
      //
      //   <-- tExpressions>
      //      CHECK, VAR, tExpressions selected.desc, -->, lDesc data!,, ""
      //      -->, lDesc data!,, //@<tExpressions selected.desc>
      //      -->, eExpr data!,, //@<tExpressions selected.expression>
      //      -->, eValue data!,, //@<tExpressions selected.value>
      //
      //   <-- eExpr>  LSX, executeExpr
      //   <-- b=>     LSX, executeExpr
      //
      //   <executeExpr>
      //      LOOP, SQL,, //SELECT (@<eExpr>) AS val
      //          ,, -->, eValue data!,, @<val>
      //
      //   <help>
      //      // Add this code to the script in order to get the output in the browser (try it)
      //      //
      //      //    <main>
      //      //      MICOHTTP, ONCE
      //      //
      //      //    <GET />
      //      //        //<html>
      //      //        // <style> td {font-family: monospace; font-size: 12px} </style>
      //      //        //    <table>
      //      //        LOOP, SQL,, //@<@><SQL_EXPR_TABLE>
      //      //            ,, //<tr>
      //      //            ,, //   <td>@<@><expression> <td>@<@><value> <td>@<@><desc>
      //      //            ,, //</tr>
      //      //        //    </table> <br>
      //      //        //<a href="https://sqlite.org/lang_datefunc.html">see also sqlite documentation</a>
      //      //        //</html>
      //
      //   <SQL_EXPR_TABLE>
      //      LOOP, VAR, sqlexpressions
      //          , LINK, " UNION "
      //          ,, //SELECT "@<expr>" AS expression, (@<expr>) AS value, "@<:encode desc>" AS desc
      //
      //   <sqlexpressions>
      //      expr, desc
      //
      //      "date('now')", //Compute the current date
      //      "date('now','start of month','+1 month','-1 day')", //Compute the last day of the current month
      //      "date('now','start of year','+9 months','weekday 2')", //Compute the date of the first Tuesday in October for the current year
      //      "datetime(1092941466, 'unixepoch')", //Compute the date and time given a unix timestamp 1092941466
      //      "datetime(1092941466, 'unixepoch', 'localtime')", //Compute the date and time given a unix timestamp 1092941466, and compensate for your local timezone.
      //      "datetime(1570000000, 'unixepoch', 'localtime')", //A particular "rounded" day
      //      "datetime(1570000000, 'unixepoch')", //A particular rounded day in Dungarvan
      //      "julianday('now') - julianday('1776-07-04')", //Compute the number of days since the signing of the US Declaration of Independence.
      //      "(julianday('now') - 2440587.5)*86400.0", //Compute the time since the unix epoch in seconds (like strftime('%s','now') except includes fractional part)
      //      "strftime('%s','now')", //Compute the current unix timestamp
      //      "strftime('%s','now') - strftime('%s','2004-01-01 02:34:56')", //Compute the number of seconds since a particular moment in 2004
      //      "strftime('%s','2020-01-01')", //Compute the current unix timestamp

   <micoHttpExample1>
      //#javaj#
      //
      //    <frames> oConso
      //
      //#listix#
      //
      //    <main>
      //       micohttp, start, monoMico
      //
      //   <GET />
      //     //<html>
      //     //<body>
      //     //   <form action='formulario1'>
      //     //      Your name : <input type='text' name='yourName' value='Gastonchen'><br>
      //     //      Yout hobby: <input type='text' name='yourHobby' value='fencing'><br>
      //     //      <input type='submit' value='Submit'>
      //     //   </form>
      //     //   <p> Click the 'Submit' button .</p>
      //     //</body>
      //     //</html>
      //
      //   <GET /formulario1>
      //     //<html>
      //     //<body>
      //     //   Hello @<yourName>! nice to meet you<br>
      //     "   Hi you "
      //     IN CASE, @<yourHobby>
      //            , soccer, Maradona
      //            , tennis, McEnroe
      //            , baseball, DiMaggio
      //            , formula1, Fittipaldi
      //            , chess, Bobby Fischer
      //            , ELSE, Champion
      //     //!<br>
      //     //   Da ya wan a repeat the funny test ?<br><br>
      //     //   <a href="/">Oh yeah! Let's check out it again!</a><br>
      //     //   <a href="/?acabamico">Not really...</a><br>
      //     //</body>
      //     //</html>

   <micoAjaxWithForm>
      //#javaj#
      //
      //    <frames> oConso
      //
      //#listix#
      //
      //    <main>
      //       NUM=, canto, 0
      //       micohttp, start, monoMico
      //
      //   <GET />
      //     //@<pajax>
      //
      //   <POST /habla.ajax>
      //     //At @<canto> you send me :
      //     NUM=, canto, canto + 1
      //     LOOP, TEXT FILE, @<_bodyMemFileName>
      //         ,, @<value>
      //
      //   <pajax>
      //      //<html>
      //      //<body>
      //      //
      //      //<script type="text/javascript">
      //      //   function ajaxFunction()
      //      //   {
      //      //      var htepo = (window.XMLHttpRequest) ? new XMLHttpRequest(): (window.ActiveXObject) ? new ActiveXObject("Microsoft.XMLHTTP"): null;
      //      //      if (htepo == null)
      //      //      {
      //      //         alert("Your browser does not support XMLHTTP!");
      //      //      }
      //      //      htepo.onreadystatechange = function () {
      //      //            if (htepo.readyState === 4) {
      //      //               if (htepo.status === 200) {
      //      //                  document.miForm.responsivo.innerHTML = htepo.responseText;
      //      //               }
      //      //               else {
      //      //                  var amsosorry = "Sorry! the server does not respond\n save your data somewhere and try it again later";
      //      //                  document.getElementById ("statusLabel").innerHTML = (amsosorry);
      //      //                  alert (amsosorry);
      //      //               }
      //      //            }
      //      //         }
      //      //
      //      //      htepo.open("POST","habla.ajax",true);
      //      //      htepo.send("tellme " + encodeURI (document.miForm.username.value) + " fin.");
      //      //   }
      //      //</script>
      //      //
      //      //   <form name="miForm">
      //      //       Write something : <input type="text" name="username" onkeyup="ajaxFunction();" /><br>
      //      //       Server response<br><br>
      //      //       <textarea name="responsivo"></textarea>
      //      //   </form>
      //      //   <br><br>
      //      //   <label id="statusLabel"></label>
      //      //
      //      //</body>
      //      //</html>
      //      //
      //
      //

   <whoareyou>
      //#javaj#
      //
      //   <frames> oCanso
      //
      //#listix#
      //
      //   <PORTOS> 1616
      //
      //   <main0>
      //      //CONNECT TO ME TO KNOW WHO ARE YOU
      //      // http://@<:lsx host name>:@<PORTOS>
      //      // http://@<:lsx host ip>:@<PORTOS>
      //      //
      //      mico, start, whou, @<PORTOS>
      //          , VERBOSE, 2
      //
      //
      //   <GET />
      //      //<h1> YOUR IP IS @<_requestIP></h1><br>
      //      //

   <simple file server>
      //#javaj#
      //
      //    <frames> oConso
      //
      //#listix#
      //
      //   <main>
      //      VAR=, eCurrentDir, @<:sys user.dir>
      //
      //      MICOHTTP, START, fileCiervo
      //              , FILE SERVER STR, @<FS>
      //              , VERBOSE LEVEL, 2
      //
      //   <FS> //:SFILE:
      //
      //   <GET />
      //      LSX, MAIN_PAGE
      //
      //   <GET /setdir>
      //      VAR=, eCurrentDir, @<dir>
      //      LSX, MAIN_PAGE
      //
      //   <GET /parent>
      //      VAR=, eCurrentDir, @<prevDir>
      //      LSX, MAIN_PAGE
      //
      //   <GET /thisIsTheEndMyFriend>
      //      MICOHTTP, STOP, fileCiervo
      //      MSG, javaj doExit
      //
      //   <MAIN_PAGE>
      //     //<html>
      //     // <style>
      //     //    * {
      //     //       font-family: Consolas, Tahoma, sans-serif;
      //     //       margin-left: 0px;
      //     //       margin-right: 0px;
      //     //    }
      //     // </style>
      //     //<body>
      //     //   Current Path : <a href="/setdir?dir=@<eCurrentDir>">@<eCurrentDir></a><br>
      //     //
      //     // <table>
      //     //    <tr><th> Subdir name</tr>
      //     //    <tr> <td> &lt;DIR&gt; <td> <a href="/parent"> .. (parent folder)</a></tr>
      //     //
      //     LOOP, DIRS, @<eCurrentDir>
      //         , RECURSIVE, 0
      //         , BODY     , //<tr> <td> &lt;DIR&gt; <td>subdir <a href="/setdir?dir=@<fullPath>"> @<fileName></a></tr>
      //
      //     //
      //     // </table>
      //     // <table>
      //     //    <tr><th> Size <th><th> Date time <th> File name</tr>
      //     //
      //     LOOP, FILES, @<eCurrentDir>
      //         , RECURSIVE, 0
      //         , BODY     , //<tr> <td align="right"> @<TAMA> <td> @<date> <td> <a href="@<FS>/@<:linux-path fullPath>"> @<fileName></a></tr>
      //     // </table>
      //     //
      //     //   <br><br><a href="/thisIsTheEndMyFriend">FINISH FILE SERVER</a><br>
      //     //</body>
      //     //</html>
      //
      //   <prevDir>
      //      STRCONV, PATH-SOLVE, @<eCurrentDir>/..
      //
      //   <TAMA> JS, @<sizeJS>
      //
      //   <sizeJS>
      //      //function bytes2Str(num)
      //      //{
      //      //   var units = ["bytes", "Kb", "MB", "GB", "TB"];
      //      //   if (num == 0) return "0 " + units[0];
      //      //   var ndig = Math.min (units.length-1, Math.floor (1 + Math.log10((num < 0 ? -num: num)/1024)/3));
      //      //   return Math.round (num * Math.pow(1024, - ndig)) + " " + units [ndig];
      //      //}
      //      //
      //      //bytes2Str (@<size>);


   <JS Chrono>
      //#listix#
      //
      //   <main>
      //      MICOHTTP, ONCE
      //
      //   <GET />
      //      //<html>
      //      //
      //      //<style>
      //      //   * {
      //      //      font-family: Ubuntu, Consolas;
      //      //      font-size: 22;
      //      //   }
      //      //
      //      //   #cronoLabel {
      //      //      font-size: 82;
      //      //      color: green
      //      //   }
      //      //
      //      //</style>
      //      //
      //      //
      //      //<div id="cronoLabel" style=>0:00</div>
      //      //
      //      //    <input type="button" value="Compte enrere 3s" onClick="compteEnrere()" />
      //      //    <input type="button" value="START" onClick="chronoStart()" />
      //      //    <input type="button" value="STOP" onClick="chronoStop()" />
      //      //    <input type="button" value="Reset" onClick="chronoReset()" />
      //      //
      //      //<script>
      //      //
      //      //var cronoLabel;
      //      //var start = 0;
      //      //var lastDiff = 0;
      //      //var timerID = 0;
      //      //
      //      //function setCronoLabel (text, negative)
      //      //{
      //      //   if (! cronoLabel)
      //      //      cronoLabel = document.getElementById("cronoLabel");
      //      //
      //      //   cronoLabel.style.color = (negative ? "red": "green");
      //      //   cronoLabel.innerHTML = (negative ? "-": "") + (text ? text: "0:00");
      //      //}
      //      //
      //      //function refrescaCrono ()
      //      //{
      //      //   var end = new Date();
      //      //   var neg = end < start;
      //      //   var diffa = new Date(neg ? start - end : end - start);
      //      //   lastDiff = new Date(end - start);
      //      //
      //      //   var milli = diffa.getMilliseconds();
      //      //   var sec = diffa.getSeconds();
      //      //   var min = diffa.getMinutes();
      //      //   var hor = diffa.getHours()-1;
      //      //
      //      //   setCronoLabel ((hor > 0 ? (hor + ":"): "") +
      //      //                  (hor + min > 0 ? (min + ":"): "") +
      //      //                  sec + ":" +
      //      //                  (milli < 100. ? "0": "") + Math.floor (milli / 10.),
      //      //                  neg);
      //      //
      //      //   clearTimeout (timerID);
      //      //   timerID = setTimeout("refrescaCrono()", 20);
      //      //}
      //      //
      //      //function compteEnrere ()
      //      //{
      //      //   start = new Date();
      //      //   start.setTime (start.getTime () + 3000); // give 3 seconds countdown
      //      //   refrescaCrono ();
      //      //}
      //      //
      //      //function chronoStart()
      //      //{
      //      //   start = new Date();
      //      //   refrescaCrono ();
      //      //}
      //      //
      //      //// function chronoContinue()
      //      //// {
      //      ////    start = new Date(new Date() - lastDiff);
      //      ////    refrescaCrono();
      //      //// }
      //      //
      //      //function chronoReset()
      //      //{
      //      //   setCronoLabel ();
      //      //   start = new Date();
      //      //}
      //      //
      //      //function chronoStop()
      //      //{
      //      //   clearTimeout (timerID);
      //      //}
      //      //
      //      //</script>
      //      //
      //      //</html>

   <micoServerShowAJAX>
      //#javaj#
      //
      //    <frames> main, ServerShow, 400, 700
      //
      //    <layout of main>
      //      EVA, 6, 6, 3, 3
      //
      //         , X
      //       X , xRequest
      //     150 , oConso
      //         , bClear
      //
      //   <sysDefaultFonts>
      //      Consolas, 14, 0, TextArea.font
      //
      //#listix#
      //
      //    <main>
      //       micohttp, start, monoMico
      //
      //    <-- bClear>
      //      -->, oConso clear
      //
      //    <GET />
      //      //<html><body>
      //      //   <!-- nothing! -->
      //      //<script>
      //      //
      //      // @<:infile META-GASTONA/js/jGastonaEva-min.js>
      //      //
      //      //   var jgas = new jGastona (evaFileUTF82obj ("@<:encode-utf8 MAIN_JAST>"));
      //      //   jgas.run ();
      //      //
      //      //</script></body></html>
      //
      //   <MAIN_JAST>
      //      //#javaj#
      //      //
      //      //   <layout of main>
      //      //        EVA, 15, 15, 8, 8
      //      //
      //      //            ,                , X
      //      //            , lTitle, -
      //      //            , iRequestSamples, lRequestDesc
      //      //          X ,    +           , xRequestCode
      //      //            ,    +           , bRunCode
      //      //            , lWidget with id "eCamp", eCamp
      //      //
      //      //#data#
      //      //
      //      //   <lTitle> //MICO Server AJAX demo, choose an AJAX request, change values and see Server reaction
      //      //
      //      //   <eCamp>
      //      //
      //      //   <iRequestSamples>
      //      //      label, desc, code
      //      //
      //      //      plain request,  "Just the simplest request",       //jgas.AJAXSend ("myRequest", "");
      //      //      Request with variables,  "Send some variables",    //jgas.AJAXSend ("myRequest", "myvar=1021&other=etc");
      //      //      Send all data,  "Send all data in body (default)", //jgas.AJAXSend ("myRequest");
      //      //      Send some data, "Request choosing data to send",   //jgas.AJAXSend ("myRequest", { bodyVars : ["iRequestSamples", "eCamp"] });
      //      //      Complex request, "Possible to send body, header and variables", //jgas.AJAXSend ("myRequest",?{ body: "Here a Body!!\nend of body",?  params: { "mivar1" : "22", "name" : "marley" },?  headers: { "myDetails" : "refining the question"}?});
      //      //      Data for widget, "Request data of a specific widget by its id", //jgas.AJAXgetDataForId ("eCamp", "");
      //      //      Change more widgets, "Special multi data request", //jgas.AJAXgetDataForId ("eCamp", "multi=1", true);
      //      //      Load data,           "Load data from eva format", //jgas.AJAXLoadData ("dataDemo");
      //      //      change GUI, "loads another JAST application and GUI", //jgas.AJAXLoadRootJast ("SECOND_JGAST");
      //      //
      //      //#listix#
      //      //
      //      //  <-- iRequestSamples>
      //      //     //var strcode = getDataCell("iRequestSamples selected.code");
      //      //     //setData ("xRequestCode", strcode.replace(/?/g, "\n    "));
      //      //     //setData ("lRequestDesc", getDataCell("iRequestSamples selected.desc"));
      //      //     //mensaka ("bRunCode");
      //      //
      //      //  <-- bRunCode>
      //      //     //eval (getDataAsTextArray("xRequestCode").join("\n"));
      //      //
      //
      //   <SECOND_JGAST>
      //      //#javaj#
      //      //
      //      //   <layout of main>
      //      //        EVA, 15, 15, 8, 8
      //      //
      //      //            ,
      //      //            , lUps! another GUI ?
      //      //            , bRestore first one
      //      //#listix#
      //      //
      //      //  <-- bRestore first one>
      //      //     //jgas.AJAXLoadRootJast ("MAIN_JAST");
      //      //
      //
      //   <POST /loadRootJast>
      //      CHECK, !=, @<jastName>, "", LSX, MAIN_JAST
      //      LSX, @<jastName>
      //
      //   <POST /myRequest>
      //     -->, xRequest data!,, @<SHOW_REQUEST>
      //
      //     MICO, SET RESPONSE VARIABLE, monoMico, ala, uno, ola, dos, jala, "tres y cuatro etc"
      //     //This is the response body
      //     //for the request
      //
      //   <POST /getDataForId>
      //      -->, xRequest data!,, @<SHOW_REQUEST>
      //
      //     CHECK, VAR, desiredValue
      //          ,, @<desiredValue>
      //          , ELSE, //A value for the id
      //
      //   <POST /loadData>
      //      //#data#
      //      //
      //      //   <iRequestSamples>
      //      //       label, desc, code
      //      //       restore data, "restore the initial data", //jgas.AJAXLoadRootJast ("MAIN_JAST");
      //
      //  <SHOW_REQUEST>
      //     //LAST REQUEST AT @<:lsx date2>
      //     //
      //     //VISIBLE PARAMETERS:
      //     //
      //     LOOP, COLUMNS,
      //         ,, //   @<columnName>: @<columnValue>
      //     //
      //     //
      //     //REQUEST BODY:
      //     //
      //     LOOP, TEXT FILE, @<_bodyMemFileName>
      //         ,, @<value>
      //     //
      //     //--- END BODY
      //
      //#**#


#**FIN_EVA#
*/

package listix.cmds;

import java.util.*;
import java.net.*;
import java.io.*;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.db.utilEscapeStr;
import org.gastona.net.http.*;


public class cmdMicoHTTPServer implements commandable
{
   protected static TreeMap micoServers = new TreeMap ();

   private final int DEFAULT_MS_ONEUSE = 6000;

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "MICOHTTPSERVER",
          "MICOHTTP",
          "MICOSERVER",
          "MICO",
          "MONOHTTP",
          "MONOHTTPSERVER",
          "MONOSERVER",
          "MONO",
      };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      String oper = cmd.getArg(0);

      boolean optStart      = cmd.meantConstantString (oper, new String [] { "START", "OPEN", "BEGIN" });
      boolean optOneGet     = cmd.meantConstantString (oper, new String [] { "ONEGET", "ONEUSE", "ONCE", "BYE" });
      boolean optGetPort    = cmd.meantConstantString (oper, new String [] { "GETPORT", "PORT" });
      boolean optGetReqIP   = cmd.meantConstantString (oper, new String [] { "CURRENT_REQUEST_IP", "REQUEST_IP", "REQIP" });
      boolean optClose      = cmd.meantConstantString (oper, new String [] { "CLOSE", "FIN", "BASTA", "STOP", "END" });
      boolean optSetRespHead  = cmd.meantConstantString (oper, new String [] { "SETRESPONSEHEADER", "SETHEADER", "RESP_HEADER=" });
      boolean optSetRespVar   = cmd.meantConstantString (oper, new String [] { "SETRESPONSEVARIABLE", "SETVARIABLE", "RESP_VAR=" });

      boolean optSetUploadMaxSize   = cmd.meantConstantString (oper, new String [] { "SETUPLOADMAXSIZE", "UPLOADMAXSIZE", "SETUPLOADSIZE", "UPLOADSIZE" });
      boolean optSetUploadMaxSockTO = cmd.meantConstantString (oper, new String [] { "SETUPLOADSOCKETTIMEOUT", "UPLOADSOCKETTIMEOUT", "UPLOADTIMEOUT" });

      if (optStart || optOneGet)
      {
         // MICOHTTP, START, Mico1, Port
         // MICOHTTP, ONE GET, 1000, Mico1, Port
         //
         String serverName = cmd.getArg (optOneGet ? 2: 1);
         int argport = (optOneGet ? 3: 2);
         int portNr = cmd.getArgSize() > argport ? stdlib.atoi (cmd.getArg (argport)): -1; // -1 (default) for mono-client launch-browser mode
         int surviveMs = optOneGet ? (cmd.getArg (1).length () > 0 ? stdlib.atoi (cmd.getArg (1)): DEFAULT_MS_ONEUSE): -1;

         // OPTIONS
         // FILE SERVER STR, string, ":SF:/" , //Tells micoHttp to serve any file of the file system when request a "GET /FSStr/fullPath" where FSStr is the string given in the option as parameter, otherwise micoHttp only serve files from subdirs js, html, img and files
         String sFileServerAllow = cmd.takeOptionString(new String [] { "FILESERVERSTR" }, null);

         // MONOCLIENT     , 0 / 1, 0, //Instructs this server to accept ONLY ONE CLIENT, namely the first one, the client might close the server using "GET /acabamico"
         boolean isMonoClient = "1".equals (cmd.takeOptionString(new String [] { "MONOCLIENT", "MONO", "ONECLIENT"  }, "0"));

         // VERBOSE        , level, 12, //Set the verbosity of the server towards standard output
         int iVerboseLevel = stdlib.atoi (cmd.takeOptionString(new String [] { "VERBOSE", "VERBOSELEVEL", "DEBUG", "DEBUGLEVEL"  }, "12"));

         // VERBOSE FILE   , filename, serverName/log, //Set the log file to output the trace generated by the server, the file will be always open for append
         String sVerboseFile = cmd.takeOptionString(new String [] { "VERBOSEFILE" }, null );

         // VERBOSE FILE   , filename, serverName/log, //Set the log file to output the trace generated by the server, the file will be always open for append
         boolean ignorebindexcep = "1".equals (cmd.takeOptionString(new String [] { "IGNOREBINDEXCEPTION" }, "0"));

         // ZIP FILES , zip/jar file, folder
         String [] zipFilesOpt = cmd.takeOptionParameters(new String [] { "ZIPFILES", "JARFILES", "ZIP", "JAR" });

         cmd.getLog().dbg (0, "MICO!", "starting " + serverName);
         micoHttpServer mose = new micoHttpServer (serverName, portNr, that, ignorebindexcep);
         mose.setFileServerString (sFileServerAllow);
         mose.setMonoClient (isMonoClient);
         mose.setVerboseFile (sVerboseFile);
         mose.setVerboseLevel (iVerboseLevel);
         mose.setCloseAfterMs (surviveMs);
         if (zipFilesOpt != null && zipFilesOpt.length > 0)
         {
            String jarFileName = zipFilesOpt[0];
            String dirInJar    = zipFilesOpt.length > 1 ? zipFilesOpt[1]: "";
            if (jarFileName.length () == 0 || jarFileName.equals("*") || jarFileName.equals("-"))
            {
               // get first jar in class path
               // property java.class.path uses the same separator as path separator of the OS
               //
               String allPaths = System.getProperty ("java.class.path", "");
               String [] cpaths = null;
               try { cpaths = allPaths.split (System.getProperty ("path.separator", ";")); }
               catch (Exception e) {}

               if (cpaths == null)
                  cpaths = new String [] { allPaths };

               for (int cc = 0; cc < cpaths.length; cc++)
                  if (cpaths[cc].length () > 4 && cpaths[cc].substring (cpaths[cc].length () - 4).equalsIgnoreCase (".jar"))
                  {
                     jarFileName = cpaths[cc];
                     break;
                  }
            }
            mose.setZipFilesToServe (jarFileName, dirInJar);
            cmd.getLog().dbg (2, "MicoHttpServer", "enable serving files from directory [" + dirInJar + "] in " + jarFileName);
         }

         mose.start ();
         micoServers.put (serverName, mose);

         cmd.getLog().dbg (4, "MicoHttpServer", "start " + serverName + " port " + mose.getLocalPort ());
         if (portNr == -1)
         {
            // auto launch mode
            String url = "http://localhost:" + mose.getLocalPort () + "/";
            cmd.getLog().dbg (4, "MicoHttpServer", "launch url in browser [" + url + "]");
            utilSys.launchBrowser (url);
         }
      }
      else if (optClose)
      {
         // MICOHTTP, CLOSE, Mico1
         //
         closeReceiver (cmd, cmd.getArg (1));
         cmd.getLog().dbg (4, "MicoHttpServer", "close " + cmd.getArg (1));
      }
      else if (optGetReqIP)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
            that.printTextLsx ((serv == null) ? "0": serv.getCurrentClientIP () + "");
      }
      else if (optGetPort)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
            that.printTextLsx ((serv == null) ? "0": serv.getLocalPort () + "");
      }
      else if (optSetRespHead)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
            for (int ii = 2; ii < cmd.getArgSize (); ii += 2)
               serv.setResponseHeader (cmd.getArg (ii), cmd.getArg (ii + 1));
      }
      else if (optSetRespVar)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
         {
            StringBuffer oneL = new StringBuffer ();
            for (int ii = 2; ii < cmd.getArgSize (); ii += 2)
               oneL.append ((ii > 2 ? "&": "") +
                            cmd.getArg (ii) + "=" +
                            utilEscapeStr.escapeStr (cmd.getArg (ii+1), "utf-8").replaceAll ("\\+", "%20"));
            serv.setResponseHeader ("XParamsInOneLine", oneL.toString ());
         }
      }
      else if (optSetUploadMaxSize)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
         {
            serv.uploadMaximumSizeBytes = (cmd.getArg (2).length () > 0) ? stdlib.atoi (cmd.getArg (2)): micoHttpServer.DEFAULT_MAXSIZE_BYTES_UPLOADING;
            cmd.getLog().dbg (4, "MicoHttpServer", "Server " + cmd.getArg (1) + ", maximum upload size for a file set to " + serv.uploadMaximumSizeBytes);
         }
         cmd.getLog().warn ("MicoHttpServer", "Setting maximum upload size for a file is NOT IMPLEMENTED!");
      }
      else if (optSetUploadMaxSockTO)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
         {
            serv.uploadSocketTimeout = (cmd.getArg (2).length () > 0) ? stdlib.atoi (cmd.getArg (2)): micoHttpServer.DEFAULT_SOCKET_TIMEOUT_WHEN_UPLOADING;
            cmd.getLog().dbg (4, "MicoHttpServer", "Server " + cmd.getArg (1) + ", maximum upload socket timeout set to " + serv.uploadSocketTimeout);
         }
      }
      else
      {
         cmd.getLog().err ("MicoHttpServer", "operation not recognized [" + oper + "]!");
         return 1;
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

   protected micoHttpServer getServerByName (listixCmdStruct cmd, String name)
   {
      // only if there is only one server name can be empty string representing the default value
      //
      if (name.length () == 0 && micoServers.size () == 1)
         return (micoHttpServer) micoServers.get (micoServers.firstKey ());

      micoHttpServer serv = (micoHttpServer) micoServers.get (name);
      if (serv == null)
         cmd.getLog().err ("MicoHttpServer", "server [" + name + "] not found!");

      return serv;
   }

   private void closeReceiver (listixCmdStruct cmd, String name)
   {
      micoHttpServer ol = getServerByName (cmd, name);
      if (ol != null)
      {
         cmd.getLog().dbg (4, "MicoHttpServer", "stoping http agent " + name);
         cmd.getLog().dbg (0, "MICO!", "stoping http agent " + name);
         ol.close ();
         micoServers.remove (name);
      }
   }

   public static void shutDownAllMicoServers ()
   {
      for (Iterator it = micoServers.entrySet().iterator(); it.hasNext();)
      {
         Map.Entry pairs = (Map.Entry) it.next();
         //cmd.getLog().dbg (0, "MICO!", "shutDownAllMicoServer " + (micoHttpServer) pairs.getKey ());
         micoHttpServer se = (micoHttpServer) pairs.getValue ();
         se.close ();
      }
      micoServers.clear ();
   }
}
