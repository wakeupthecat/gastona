/*
library listix (www.listix.org)
Copyright (C) 2014 Alejandro Xalabarder Aulet

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
         4   , headerName         ,           , //Header name, for example ajaxRESP-param1
         4   , headerValue        ,           , //Header value

         5   , SET RESPONSE PARAMETERS,       ,
         5   , serverName         ,           , //Name of the server
         5   , parameter1         ,           , //value of first parameter (implicit header name ajaxRESP-parameter1)
         5   , ...                ,           , //value of further parameters (implicit header name ajaxRESP-parameterX)


   <options>
      synIndx, optionName  , parameters, defVal, desc

      1      , FILE SERVER STR, string, ":SF:/" , //Tells micoHttp to serve any file of the file system when request a "GET /FSStr/fullPath" where FSStr is the string given in the option as parameter, otherwise micoHttp only serve files from its current directory
      1      , MONOCLIENT     , 0 / 1, 0, //Instructs this server to accept ONLY ONE CLIENT, namely the first one, the client might close the server using the request "GET /acabamico"
      1      , VERBOSE        , level, 12, //Set the verbosity of the server towards standard output
      1      , VERBOSE FILE   , filename, serverName/log, //Set the log file to output the trace generated by the server, the file will be always open for append

   <examples>
      gastSample

      micoHttpExample1
      micoAjaxWithForm
      javascriptExecutor
      marketesPublisher
      whoareyou
      
      
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

   <javascriptExecutor>
      //#javaj#
      //
      //   <frames> main, Javascript Executor
      //
      //   <layout of main>
      //      EVA, 3, 3, 2, 2
      //
      //         , X
      //         , lEnter your javascript code
      //       X , xCodeArea
      //         , bEjecute
      //         , lSalida
      //       X , xBodyResponse
      //      <! X , oSalar
      //
      //   <sysDefaultFonts>
      //      Consolas, 14, 0, TextArea.font
      //
      //#data#
      //
      //   <xCodeArea>
      //      //
      //      //function encript (t1, t2)
      //      //{
      //      //   return t2 + t1;
      //      //}
      //      //
      //      //out ("output of this function is ... " + encript ("TheCat", "WakeUp"));
      //      //
      //
      //#listix#
      //
      //   <-- bEjecute>
      //      VAR=, STARTSTAMP, @<:lsx CLOCK>
      //      MSG, lSalida data!,, Opening browser ...
      //      micohttp, start, Servako
      //
      //   <elapsed>
      //      =, (@<:lsx CLOCK> - STARTSTAMP) / 1000
      //
      //   <GET />
      //      VAR=, USER CODE, LSX, xCodeArea
      //      @<:solve-infile META-GASTONA/js/executorJS.lsx.js>
      //
      //   <POST /JSresponse>
      //      micohttp, stop, Servako
      //      MSG, lSalida data!,, //done, it took @<elapsed> seconds
      //      -->, xBodyResponse load, @<_bodyMemFileName>

   <marketesPublisher>
      //#javaj#
      //
      //   <frames> main, Marketes editor
      //
      //   <layout of main>
      //      EVA, 4, 4, 3, 3
      //
      //          , X
      //          , lArticle in marketes format
      //        X , xArticle
      //          , bPublish
      //
      //   <sysDefaultFonts>
      //      Consolas, 14, 0, TextArea.font
      //
      //#data#
      //
      //   <theTitle> ARTICLE'S TITLE
      //
      //   <xArticle>
      //      //#h Article header
      //      //-- First chapter
      //      //
      //      //Here you can start the writing text
      //      //
      //      //New paragraphs are also possible. Now some code
      //      //
      //      //      function () {
      //      //          return null;
      //      //      }
      //      //
      //      //Finally in paragraphs you can use <b><i>html tags</i></b>.
      //
      //
      //#listix#
      //
      //   <main0>
      //      mico, start, esso
      //      VAR=, serverPort, @<GET SERVER PORT>
      //
      //   <GET SERVER PORT> mico, GET PORT, esso
      //
      //   <-- bPublish>
      //      BROWSER, http://localhost:@<serverPort>
      //
      //   <GET />
      //      LSX, template generaDoc
      //
      //   <template generaDoc>
      //      //<!DOCTYPE html>
      //      //<html> <head>
      //      //      <title>@<theTitle></title>
      //      //   <style type="text/css">
      //      //
      //      @<:infile META-GASTONA/js/marketes.css>
      //      //
      //      //   </style></head>
      //      //<body>
      //      //
      //      //   <div id="sitio">  </div>
      //      //
      //      //   <script>
      //      //
      //      @<:infile META-GASTONA/js/marketes.js>
      //      //
      //      //   var miDoc = "@<:encode-utf8 xArticle>".replace (/\+/g, "%20");
      //      //
      //      //   document.getElementById('sitio').innerHTML = marketes (decodeURIComponent (miDoc));
      //      //
      //      //</script>
      //      //
      //      //</body>
      //      //</html>
      //
      //

   <whoareyou>
      //#javaj#
      //
      //   <frames> oCanso
      //   
      //#listix#
      //
      //	  <PORTOS> 1616
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


#**FIN_EVA#
*/

package listix.cmds;

import java.util.*;
import java.net.*;
import java.io.*;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import org.gastona.net.http.*;


public class cmdMicoHTTPServer implements commandable
{
   protected static Map micoServers = new TreeMap ();

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
      boolean optGetPort    = cmd.meantConstantString (oper, new String [] { "GETPORT", "PORT" });
      boolean optGetReqIP   = cmd.meantConstantString (oper, new String [] { "CURRENT_REQUEST_IP", "REQUEST_IP", "REQIP" });
      boolean optClose      = cmd.meantConstantString (oper, new String [] { "CLOSE", "FIN", "BASTA", "STOP", "END" });
      boolean optSetRespHead  = cmd.meantConstantString (oper, new String [] { "SETRESPONSEHEADER", "SETHEADER" });
      boolean optSetRespParam = cmd.meantConstantString (oper, new String [] { "SETRESPONSEPARAMETERS", "SETRESPONSEPARAMS", "SETPARAMETERS", "SETPARAMS" });

      if (optStart)
      {
         // MICOHTTP, START, Mico1, Port
         //
         String serverName = cmd.getArg (1);
         int portNr = cmd.getArgSize() > 2 ? stdlib.atoi (cmd.getArg (2)): -1; // -1 (default) for mono-client launch-browser mode

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

         cmd.getLog().dbg (0, "MICO!", "starting " + serverName);
         micoHttpServer mose = new micoHttpServer (serverName, portNr, that, ignorebindexcep);
         mose.setFileServerString (sFileServerAllow);
         mose.setMonoClient (isMonoClient);
         mose.setVerboseFile (sVerboseFile);
         mose.setVerboseLevel (iVerboseLevel);

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
      }
      else if (optGetReqIP)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
            that.printTextLsx ((serv == null) ? "0": serv.getCurrentClietnIP () + "");
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
            serv.setResponseHeader (cmd.getArg (2), cmd.getArg (3));
      }
      else if (optSetRespParam)
      {
         micoHttpServer serv = getServerByName (cmd, cmd.getArg (1));
         if (serv != null)
            for (int ii = 2; ii < cmd.getArgSize (); ii ++)
               serv.setResponseHeader ("ajaxRESP-parameter" + (ii-1), cmd.getArg (ii));
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
