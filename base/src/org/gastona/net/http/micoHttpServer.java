/*
package org.gastona.net.http
(c) Copyright 2014-2018 Alejandro Xalabarder Aulet

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

package org.gastona.net.http;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.DateFormat;
import de.elxala.mensaka.*;
import de.elxala.zServices.logger;
import de.elxala.db.utilEscapeStr;

/**
   @author  Alejandro Xalabarder
   @date    2014.11.27 23:51

   @brief small http server using listix language


      - It doesn't keep the connection alive (new connection for each request = memoryless)
      - Suport multiple clients, but respose requests sequentially (clients has to wait for other clients)
      - if lauched with port 0 the port is automatically assigned
      - if lauched with port -1 the port is automatically assigned and the server is killable by using
        the key parameter "acabamico" on a request of any method and uri. This mode is thought as mono-client
        where the unique client can finish the server.


*/
public class micoHttpServer extends Thread
{
   //:SCENE HTTP_Request | net/micohttp | Describes how MicoHttp process the http requests from clients

   private static logger log = new logger (null, "org.gastona.net.http.micoHttpServer", null);

   public static int monoInstanceCounter = 0;
   public static final int DEFAULT_DEBUG_LEVEL = 5;

   // not static! this number is different for each instance
   public int monoInstanceNr = 0;

   // About socket timeout when uploading a file:
   //    It is needed if the client is a slow computer or the connection is poor.
   //    A test in a slow computer (my mother's PC) shown a big maximum of 27 seconds between socket reads!
   //    this computer succeded the upload of a 45 MB file with the given timeout of 40 seconds.
   public static final int DEFAULT_SOCKET_TIMEOUT_WHEN_UPLOADING = 40000;
   public static final int DEFAULT_MAXSIZE_BYTES_UPLOADING = 10*1024*1024; // 10MB
   public int uploadSocketTimeout = DEFAULT_SOCKET_TIMEOUT_WHEN_UPLOADING;

   //(o) TODO: implement uploadMaximumSizeBytes
   //          the result if succeed or not has to be stored in some variable,
   //          for example _fileUploaded = "0", _fileUploadReason = "oK|maxSize|socketTO|socketErr"
   public int uploadMaximumSizeBytes = DEFAULT_MAXSIZE_BYTES_UPLOADING;

   protected boolean killable = false;

   private static final String ACABA_KEY = "acabamico";
   private static final String LINK_ACABA = "<a href=\"/?" + ACABA_KEY + "\">StopServer</a>";
   private static final String PAGE_ACABADO = "<html><body>Finitto!</body></html>";
   private static final String PAGE_MONOCLIENT = "<html><body>Check if the server has been started in MONO mode</body></html>";

   public static final int STATE_AWAKE = 10;
   public static final int STATE_SLEEP = 20;
   public static final int STATE_ZOMBIE = 30;
   public static final int STATE_CLOSED = 40;

   protected int responseID = 10000;

   protected int state = STATE_ZOMBIE;

   protected String myName = null;
   protected ServerSocket theServer = null;
   protected listix theListixLogic = null;
   protected Socket currentClient = null;

   protected TreeMap responseHeaders = new TreeMap ();

   protected String uploadFilesSubDir = "filesUpload";
   protected String fileServerString = null;
   protected String theOnlyLivingBoyInNY = null;
   // for now variables related with verbose are static (function static used by other classes!)
   //
   protected static int verboseLevel = 12;
   protected static TextFile verboseFile = null;
   protected static String verboseFileName = "";
   protected static int millisToClose = -1;

   public micoHttpServer (String name, int listenPort, listix logicFormats, boolean ignoreBinExcep)
   {
      monoInstanceNr = monoInstanceCounter ++;
      myName = name;
      theListixLogic = logicFormats;
      killable = listenPort == -1;
      try
      {
         theServer = new ServerSocket ();
         theServer.bind(new java.net.InetSocketAddress(listenPort < 0 ? 0: listenPort));
         state = STATE_AWAKE;
      }
      catch (java.net.BindException e)
      {
         if (!ignoreBinExcep)
              log.err ("micoHttpServer", "exception creating server socket " + e);
         else log.dbg (4, "micoHttpServer", "java.net.BindException ignored");
         state = STATE_ZOMBIE;
         theServer = null;
      }
      catch (Exception e)
      {
         log.err ("micoHttpServer", "exception creating server socket " + e);
         state = STATE_ZOMBIE;
         theServer = null;
      }
   }

   public String getMemFileName4ReqBody ()
   {
      return ":mem " + myName + " " + "httpRequestBODY";
   }

   public static void setVerboseFile (String fileName)
   {
      if (fileName == null) return;
      if (fileName.length () == 0)
         fileName = "server/logServer.txt"; //  fileName = getServerStrId () + "/logServer.txt";

      verboseFileName = fileName;
      fileUtil.ensureDirsForFile (verboseFileName);
      verboseFile = new TextFile ();
   }

   public static void setVerboseLevel (int level)
   {
      verboseLevel = level;
   }

   public static void setCloseAfterMs (int surviveMs)
   {
      millisToClose = surviveMs;
   }

   public static boolean isDebugging (int verboseLev)
   {
      return verboseLevel >= verboseLev;
   }

   public static boolean isDebugging ()
   {
      return verboseLevel >= DEFAULT_DEBUG_LEVEL;
   }

   public void setFileServerString (String str)
   {
      if (str != null && str.length () == 0)
           fileServerString = ":SF:/";
      else fileServerString = str;  // accept null here, meaning that NO file server functionality is desired
   }

   public void setMonoClient (boolean unique)
   {
      theOnlyLivingBoyInNY = unique ? "": null;

      // don't change killable since automatically server port set it to true
      if (unique)
         killable = false;
   }

   // unique id for this server
   //
   public String getServerStrId ()
   {
      return myName + "_" + monoInstanceNr + "_" + getLocalPort ();
   }

   // unique id for the current response of this server
   //
   public String getResposeStrID ()
   {
      return getServerStrId () + "_" + responseID ++;
   }

   public void assignLogic (listix listixObj)
   {
      theListixLogic = listixObj;
   }

   public int getLocalPort ()
   {
      return theServer != null ? theServer.getLocalPort (): -1;
   }

   public String getCurrentClientIP ()
   {
      if (currentClient == null) return null;
      InetAddress iadd = ((InetSocketAddress) currentClient.getRemoteSocketAddress ()).getAddress ();

      String addIP = iadd.getHostAddress ();
      // avoid the error
      // micoHttp: 1510782719940: theOnlyLBINY [0:0:0:0:0:0:0:1]
      // micoHttp: 1510782719941: currentIP    [127.0.0.1]

      return addIP.equals ("0:0:0:0:0:0:0:1") ? "127.0.0.1": addIP;
   }

   public void setResponseHeader (String name, String value)
   {
      responseHeaders.put (name, name + " : " + value);
   }

   protected String defaultHTML (String text)
   {
      return "<html><body>Mono http server " + myName + " " + text + "<br>" + (killable ? LINK_ACABA: "") + "</body></html>";
   }

   public static void out (String msg)
   {
      out(DEFAULT_DEBUG_LEVEL, msg);
   }

   public static void out (int level, String msg)
   {
      if (level <= verboseLevel)
      {
         if (verboseFile != null && verboseFile.fopen (verboseFileName, "a"))
         {
            verboseFile.writeLine (System.currentTimeMillis () + ": " + msg);
            verboseFile.fclose ();
         }
         else System.out.println ("micoHttp: " + System.currentTimeMillis () + ": " + msg);
      }
      log.dbg (level, msg);
   }


   protected String getLsxFormat4Response (httpRequestData req)
   {
      return req.theMethod + " " + req.theUri;
   }

   protected String getContentTypeFromFileName (String fileName)
   {
      String ext = fileUtil.getExtension (fileName);
      if (ext.equalsIgnoreCase ("css")) return "text/css; charset=utf-8";
      if (ext.equalsIgnoreCase ("js")) return "application/javascript; charset=utf-8";
      if (ext.equalsIgnoreCase ("json")) return "application/json; charset=utf-8";
      if (ext.equalsIgnoreCase ("html")) return "text/html; charset=utf-8";
      if (ext.equalsIgnoreCase ("htm")) return "text/html; charset=utf-8";
      if (ext.equalsIgnoreCase ("xml")) return "text/xml; charset=utf-8";
      //(o) TOREVIEW_micoHttp Content-Type serving a file, review : any other extension to consider ?

      // by sending application/octet-stream the browser will procede to download the file
      // which is the most desired action with a file except with css and js

      return "application/octet-stream; charset=utf-8";
   }

   //(o) TOREVIEW_micoHttp review use/need of synchronized
   // NOTE: synchronized is aparently needed if two instances of micoHttpServer
   //       are sharing the same Listix object, or maybe because lsxWriter.makeFile is not synchronized
   //       or maybe due to TextFile (:mem)
   //
   protected synchronized String buildResponse (httpRequestData req)
   {
      if (theListixLogic == null)
      {
         return defaultHTML ("with no logic associated!");
      }
      out ("response start");

      // do exists format for the response ? (e.g. <GET /myreq>)
      //
      String lsxFormat = getLsxFormat4Response (req);

      // translate speculative requests i.e. from Chrome and/or IE
      //
      if ((lsxFormat.trim ()).length () == 0)
      {
         out ("speculative request solved!");
         lsxFormat = "GET /";
      }

      if (theListixLogic.getVarEva (lsxFormat) == null)
      {
         return req.isMethodGET () ? defaultHTML ("with no response for [" + lsxFormat + "]!"): "";
      }

      //:SEQ HTTP_Request | micoHTTP -> 7:PrepareParametersRequest -> buildResponseMethod | Prepare variables with information of the request (e.g. _requestIP etc) for the listix format that will process it
      //
      //
      //    extra variables: _myMicoName, _bodyMemFileName, _requestIP, _uploadedFilesCount and _uploadedFile
      //    all uri parameters, if any, as variables
      //    all headers as variables
      //

      // basically we make a listix LOOP where the table is a table of 1 row
      // containing all the parameters and its values and the listix format is given
      // by the uri of the request
      //

      // Build the tableCursor from the request parameters
      //
      int col = 0;
      Eva eparams = new Eva ("http-intern-params"); // name is not relevant

      eparams.setValue ("_myMicoName", 0, col);
      eparams.setValue (myName, 1, col++);

      eparams.setValue ("_bodyMemFileName", 0, col);
      eparams.setValue (getMemFileName4ReqBody (), 1, col++);

      eparams.setValue ("_requestIP", 0, col);
      eparams.setValue (getCurrentClientIP (), 1, col++);

      eparams.setValue ("_uploadedFilesCount", 0, col);
      eparams.setValue ("" + req.getUploadedFileCount (), 1, col ++);

      for (int ii = 0; ii < req.getUploadedFileCount (); ii ++)
      {
         eparams.setValue ("_uploadedFile" + ii, 0, col);
         eparams.setValue (req.getUploadedFilePath (ii), 1, col++);
      }

      Iterator ite = req.theUriParameters.keySet().iterator();
      while (ite.hasNext ())
      {
         String key   = (String) ite.next();
         String value = (String) req.theUriParameters.get(key);
         eparams.setValue (key  , 0, col);
         eparams.setValue (value, 1, col++);
      }

      ite = req.theHeader.keySet().iterator();
      while (ite.hasNext ())
      {
         String key   = (String) ite.next();
         String value = (String) req.theHeader.get(key);
         eparams.setValue (key  , 0, col);
         eparams.setValue (value, 1, col++);
      }

      tableAccessEva tablePars = new tableAccessEva (); // it is a tableAccessBase class
      tablePars.evaData = eparams;

      tableCursorStack tabstack = new tableCursorStack ();
      tabstack.pushTableCursor (new tableCursor (tablePars));

      //:SEQ HTTP_Request | buildResponseMethod -> 8:call_listix_for_response -> micoHTTP | Finally call the listix format to generate the response
      //
      String MEM_RESP_FILE = ":mem httResponse mono" + monoInstanceNr; // this memory file is used only internally ...

      theListixLogic.setNewLineString ("\r\n"); // required for HTTP!
      lsxWriter.makeFile (theListixLogic.getGlobalData (),
                          theListixLogic.getGlobalFormats (),
                          lsxFormat,
                          MEM_RESP_FILE,
                          tabstack,
                          null,
                          null);


      StringBuffer respBuff = TextFile.readFileIntoStringBuffer (MEM_RESP_FILE);
      if (respBuff == null)
      {
         return defaultHTML ("error reading intern response (" + MEM_RESP_FILE + ") !");
      }

      out ("response built");
      return respBuff.toString ();
   }

   // decide if serve a file or something else
   //
   public File wantServeFile (String uri)
   {
      if (uri == null || uri.length () < 2) return null;

      // First check if it contains the special fileServerString
      // with it micoHttp is allowed to serve any file in the reachable
      // file system (including different drive units in windows)
      //
      String tryFileName = fileServerString != null ?
                                  uri.startsWith ("/" + fileServerString) ?
                                         uri.substring (1 + fileServerString.length ()): null
                                  : null;

                                  // Not the case, then check is the directory is forbidden
      if (tryFileName == null)
      {
         //(o) MICO/special/directories/nopublic all directories that are not exposed to file serving, at least by default
         //
         // standard serve file accepting ONLY index.html and subdirs html, img, js, css, data, files and gast
         //
         if (uri.startsWith ("/hide_") ||
             uri.startsWith ("/hide/") ||
             uri.startsWith ("/hidden_") ||
             uri.startsWith ("/hidden/") ||
             uri.startsWith ("/nopublic"))
         {
             // do not even try these names as filenames to serve
             // note the difference made between the special strings 'hide' and 'nopublic'
             // where "/hidelberg" is acceptable while "/nopublicBerg" is not
             out (0, "Attempt to open a not public item! [" + uri + "]");
         }
         else
         {
            tryFileName = uri.substring (1);
         }
      }
      if (tryFileName == null) return null;

      File tryF = fileUtil.getNewFile (tryFileName);

      out (6, "check file [" + tryF.getPath () + "] exist " + tryF.exists () + " isDir " + tryF.isDirectory ());

      return (tryF != null && tryF.exists () && !tryF.isDirectory ()) ? tryF: null;
   }

   private Timer closeTimer = null;

   private void scheduleEnd ()
   {
      if (millisToClose == -1) return;

      // if a timer is running then stop it and make a new one
      //
      if (closeTimer != null) closeTimer.cancel();

      TimerTask tasca = new TimerTask ()
      {
         public void run ()
         {
            out ("closing server ");
            close ();

            // we have to finish the timer, if not it hangs in the timer loop
            if (closeTimer != null) // it has to
            {
               closeTimer.cancel();
               // need this ?
               //closeTimer.purge();
            }
         }
      };

      closeTimer = new Timer ();
      closeTimer.schedule (tasca, millisToClose);
   }

   public void run ()
   {
      if (getLocalPort () < 0)
      {
         log.err ("run", "server cannot be started, it is not bound!");
         return;
      }

      out (0, "running ... listening port " + getLocalPort ());

      while (state == STATE_AWAKE)
      {
         try   // If any error ocurrs ... keep inside the loop !!
         {
            httpRequestData reke = new httpRequestData (getMemFileName4ReqBody ());
            if (reke == null) continue;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            //:SEQ HTTP_Request | clientHTTP -> 1:requestHTTP -> micoHTTP | Server accepts an http request from a client
            Socket client = theServer.accept ();
            // if any previous close timer was set, cancel it since now we start serving again
            if (closeTimer != null) closeTimer.cancel();

            //(o) TODO_Remove this workaround !!!! needed or IE and Chrome !! Safari and Firefox seems not to need it!
            // possible explanation pointed in
            // http://stackoverflow.com/questions/4761913/server-socket-receives-2-http-requests-when-i-send-from-chrome-and-receives-one
            // .. Apparently, Chrome opens a speculative socket, to be able to make the
            //    request immediately in case a new request needs to be made.
            //
            // Other http servers might not have a problem with this speculative socket
            // because the common approach is to open a worker thread for each request
            // since micoHttp cannot do this the empty request blocks unnecessarely the server!
            //
            client.setSoTimeout(1000);

            currentClient = client;
            inputStream = client.getInputStream ();
            outputStream = client.getOutputStream ();

            out (10, "accepted socket " + client.isConnected () + " / " + client.isInputShutdown () + " / " + client.isOutputShutdown ());
            if (theOnlyLivingBoyInNY != null)
            {
               // MONO CLIENT requests
               if (theOnlyLivingBoyInNY.length () == 0)
               {
                  // very first request => get IP of MONO CLIENT and process the request
                  theOnlyLivingBoyInNY = getCurrentClientIP ();
                  out (2, "theOnlyLBINY [" + theOnlyLivingBoyInNY + "]");
               }
               else if (theOnlyLivingBoyInNY.equals (getCurrentClientIP ()))
               {
                  // request from the "only client" ... process the request
               }
               else
               {
                  //:SEQ HTTP_Request | micoHTTP -> 2:caseMonoClient_response -> clientHTTP | We are in mode MONO client and the request does not come from the "ONLY client", therefore empty response
                  // not very first request and not the "only client" so
                  // send reject response and not process the request further
                  out (2, "theOnlyLBINY [" + theOnlyLivingBoyInNY + "]");
                  out (2, "currentIP    [" + getCurrentClientIP () + "]");
                  httpResponseData respa = new httpResponseData (outputStream, reke.isMethodGET () ? PAGE_MONOCLIENT: "", null, null);
                  respa.send ();
                  try { client.close (); } catch (Exception e) {};
                  currentClient = null;
                  continue;
               }
            }

            out (2, "request accepted from " + getCurrentClientIP ());

            httpStreamReader reqReader = new httpStreamReader (inputStream);
            reke.processRequest (reqReader, getResposeStrID (), client, uploadSocketTimeout);
            reke.dump ("on request");

            // detect kill
            //
            if (killable && reke.theUriParameters.get (ACABA_KEY) != null)
            {
               //:SEQ HTTP_Request | micoHTTP -> 3:caseKillConnecion_response -> clientHTTP | Key request to kill the HTTP session (the server actually)
               httpResponseData respa = new httpResponseData (outputStream, PAGE_ACABADO, null, null);
               respa.send ();
               try { client.close (); } catch (Exception e) {};
               currentClient = null;
               reke.dump ("on finish");
               break;
            }

            httpResponseData respa = null;
            responseHeaders = new TreeMap ();

            if (reke.isMethodGET ())
            {
               // CHECK IF SERVING A FILE

               // decide if serve a file or from lsx, current criteria:
               //
               //   1. try to find an existing file (see wantServeFile)
               //   2. if the option ZIP FILES (or JAR FILES) is given in the MICO START command
               //      then it will be checked if the file is found in the specified zip or jar file and folder inside it
               //
               // 2015.08.12
               // TODO: it would be possible to give the listix format preference, so if both exists, listix format and file, then listix format wins!
               //       It sound logical but I don't know if it solves anything appart from masking specific files
               //       This might cause also "difficult to find" behaviors : why this file is not served ? etc...
               //       Finally, listix format serves per default plain text ("text/html"), if we want to override serving a file
               //       with another (binary file) we should have the mechanism to tell doing it with conten-type "application/octet-stream" (for example)
               //

               // file names containing spaces are send in UTF-8!
               //
               String fileNameDecode = utilEscapeStr.desEscapeStr (reke.theUri, "utf8");

               File file2serve = wantServeFile (fileNameDecode);
               if (file2serve != null)
               {
                  // if browser request a css file we have to send content-type text/css (use the function getContentTypeFromFileName)
                  // if not the css styles defined in the file will not be applied !!
                  // but if we want to just download it as a file is better to send Content-Type "application/octet-stream"
                  //
                  boolean servingAsFile = fileServerString != null && fileNameDecode.startsWith ("/" + fileServerString);

                  //(o) MICO/special/directories/nocache resources from this directory shouldn't be cached
                  //
                  if (fileNameDecode.startsWith ("/nocache/"))
                  {
                     responseHeaders.put ("Cache-Control", "Cache-Control : no-cache, no-store, must-revalidate");
                     responseHeaders.put ("Pragma", "Pragma : no-cache");
                     responseHeaders.put ("Expires", "Expires : 0");
                  }

                  out ("want to serve the file [" + file2serve + "]");

                  //:SEQ HTTP_Request | micoHTTP -> 4:caseServeAFile_response -> clientHTTP | Found that it is a GET request and the url corresponds with a given file so serve the file
                  // serving a file
                  respa = new httpResponseData (outputStream,
                                                file2serve,
                                                servingAsFile ? "application/octet-stream": getContentTypeFromFileName (file2serve.getName ()),
                                                responseHeaders);
               }
               else
               {
                  // still try if file contained in declared zip

                  ZipEntry zie = null;
                  String nameInZip = "";
                  if (myZipped2Serve != null && fileNameDecode.length () > 1)
                  {
                     // remove "/" from fileNameDecode if no inital path given in zipLimited2Folder
                     nameInZip = zipLimited2Folder.length () == 0 ? fileNameDecode.substring (1): (zipLimited2Folder + fileNameDecode);
                     out ("search in zip [" + nameInZip + "]");
                     zie = myZipped2Serve.getEntry (nameInZip);
                  }
                  if (zie != null && !zie.isDirectory ())
                  {
                     //:SEQ HTTP_Request | micoHTTP -> 5:caseFileFromAZip_response -> clientHTTP | Found that it is a GET request and the url corresponds to a zipped file from the configured zip archives
                     out ("want to serve from zip [" + fileNameDecode + "]");
                     // respa = new httpResponseData (outputStream, myZipped2Serve.getInputStream (zie), zie.getSize (), getContentType (reke), responseHeaders);
                     respa = new httpResponseData (outputStream,
                                                   myZipped2Serve.getInputStream (zie),
                                                   zie.getSize (),
                                                   getContentTypeFromFileName (nameInZip),
                                                   responseHeaders);
                  }
               }
            }

            if (respa == null)
            {
               //Either a "GET /" method but with no file to serve
               //or any other method like POST, PUT etc
               //So it will be served from the listix format called as the request, for example the request
               //      "GET /myreq?name='aaa'"
               //will be served from the listix format "GET myreq", like
               //      <GET /myreq>
               //         // hello @<name>!
               //

               //:SEQ HTTP_Request | micoHTTP -> 6:ListixBuiltResponse -> buildResponseMethod | The http response will be built dynamically through a listix format
               out ("want to serve from listix [" + getLsxFormat4Response (reke) + "]");
               // build response using listix
               //
               respa = new httpResponseData (outputStream, buildResponse (reke), null, responseHeaders);
            }

            //:SEQ HTTP_Request | micoHTTP -> 9:sendResponseToClient -> clientHTTP | Send the http response
            out ("sending response");
            respa.send ();
            out ("response sent");
            try { client.close (); out ("client closed"); } catch (Exception e) { out ("cannot close client " + e); };
            currentClient = null;

            scheduleEnd (); // refresh server timeout if any (syntax ONE GET)
         }
         catch (java.net.SocketException se)
         {
            if (state != STATE_CLOSED) // avoid "java.net.SocketException: Socket closed" if it is wanted ..
            {
               log.severe ("run", "socket exception in http server " + se);
            }
         }
         catch (Exception e)
         {
            log.severe ("run", "exception in http server " + e);
            //!!! no zombie but continue with next client!
            // state = STATE_ZOMBIE;
         }
      }
      close ();
      //try { this.join(); } catch (Exception e) {};
   }

   public void close ()
   {
      state = STATE_CLOSED;
      if (theServer != null)
      {
         try { theServer.close (); } catch (Exception e) {}
      }

      //(o) NOTE 2015.11.22 consider
      //   this.interrupt ();
   }

   // SERVING FILES FROM ZIP FEATURE
   //
   private ZipFile myZipped2Serve = null;
   private String zipLimited2Folder = "";

   public void setZipFilesToServe (String zipfile, String folder)
   {
      try {
         myZipped2Serve = new ZipFile(zipfile);
         zipLimited2Folder = folder;
      }
      catch (Exception se)
      {
         log.err ("setZipFilesToServe", "cannot open Zip file " + myZipped2Serve + " " + se);
         myZipped2Serve = null;
      };
   }
}
