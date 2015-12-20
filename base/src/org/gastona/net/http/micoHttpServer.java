/*
package org.gastona.net.http
(c) Copyright 2014 Alejandro Xalabarder Aulet

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
   @company Wakeupthecat UG

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
   private static logger log = new logger (null, "org.gastona.net.http.micoHttpServer", null);

   public static int monoInstanceCounter = 0;
   public static final int DEFAULT_DEBUG_LEVEL = 5;
   public int monoInstanceNr = 0;

   protected boolean killable = false;

   private static final String ACABA_KEY = "acabamico";
   private static final String LINK_ACABA = "<a href=\"/?" + ACABA_KEY + "\">StopServer</a>";
   private static final String PAGE_ACABADO = "<html><body>Finitto!</body></html>";
   private static final String PAGE_MONOCLIENT = "<html><body>The server is busy</body></html>";

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

   protected List responseHeaders = null;


   protected String fileServerString = null;
   protected String theOnlyLivingBoyInNY = null;
   // for now variables related with verbose are static (function static used by other classes!)
   //
   protected static int verboseLevel = 12;
   protected static TextFile verboseFile = null;
   protected static String verboseFileName = "";

   public micoHttpServer (String name, int listenPort, listix logicFormats)
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

   public String getCurrentClietnIP ()
   {
      if (currentClient == null) return null;
      InetAddress iadd = ((InetSocketAddress) currentClient.getRemoteSocketAddress ()).getAddress ();
      return iadd.getHostAddress ();
   }

   public void setResponseHeader (String name, String value)
   {
      if (responseHeaders == null)
         responseHeaders = new Vector ();

      responseHeaders.add (name + ":" + value);
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
         else System.out.println (msg);
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
      //(o) TOREVIEW_micoHttp Content-Type serving a file, review : any other extension to consider ?

      // by sending application/octet-stream the browser will procede to download the file
      // which is the most desired action with a file except with css and js

      return "application/octet-stream; charset=utf-8";
   }

   protected String getContentType (httpRequestData req)
   {
      if (theListixLogic == null) return null;

      Eva cty = theListixLogic.getVarEva (getLsxFormat4Response (req) + " Content-Type");
      return (cty == null ? null: cty.getValue ());
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
      eparams.setValue (getCurrentClietnIP (), 1, col++);

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
         return defaultHTML ("with no response for [" + lsxFormat + "]!");
      }

      String MEM_RESP_FILE = ":mem httResponse mono" + monoInstanceNr; // this memory file is only used internally ...

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


   public void run ()
   {
      if (getLocalPort () < 0)
      {
         log.err ("run", "server cannot be started, it is not bound!");
         return;
      }

      out (0, "estamos en la labor ... atendiendo a port " + getLocalPort ());

      while (state == STATE_AWAKE)
      {
         try   // If any error ocurrs ... keep inside the loop !!
         {
            httpRequestData reke = new httpRequestData (getMemFileName4ReqBody ());
            InputStream inputStream = null;
            OutputStream outputStream = null;

            Socket client = theServer.accept ();

            //(o) TODO_Remove this workaround !!!! needed or IE and Chrome !! Safari and Firefox seems not to need it!
            // possible explanation pointed in
            // http://stackoverflow.com/questions/4761913/server-socket-receives-2-http-requests-when-i-send-from-chrome-and-receives-one
            // .. Apparently, Chrome opens a “speculative socket”, to be able to make the
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

            out (10, "accepted socket " + client.isConnected () + " / " + client.isInputShutdown () + " / " + client.isOutputShutdown () );
            if (theOnlyLivingBoyInNY != null)
            {
               if (theOnlyLivingBoyInNY.length () == 0)
               {
                  theOnlyLivingBoyInNY = getCurrentClietnIP ();
                  out (2, "theOnlyLBINY [" + theOnlyLivingBoyInNY + "]");
               }
               else if (!theOnlyLivingBoyInNY.equals (getCurrentClietnIP ()))
               {
                  out (2, "theOnlyLBINY [" + theOnlyLivingBoyInNY + "]");
                  out (2, "currentIP    [" + getCurrentClietnIP () + "]");
                  httpResponseData respa = new httpResponseData (outputStream, PAGE_MONOCLIENT, null, null);
                  respa.send ();
                  try { client.close (); } catch (Exception e) {};
                  continue;
               }
            }

            out (2, "hemos aceptado a un pavo! " + getCurrentClietnIP ());

            httpStreamReader reqReader = new httpStreamReader (inputStream);
            reke.processRequest (reqReader, getResposeStrID ());
            reke.dump ("nos requestan");

            // detect kill
            //
            if (killable && reke.theUriParameters.get (ACABA_KEY) != null)
            {
               httpResponseData respa = new httpResponseData (outputStream, PAGE_ACABADO, null, null);
               respa.send ();
               client.close ();
               reke.dump ("nos matan!");
               break;
            }

            httpResponseData respa = null;
            responseHeaders = null;

            // decide if serve a file or from lsx, current criteria:
            //
            //   1. try to find an existing file (see wantServeFile)
            //   2. (in preparation) try to find a resource
            //        namely a file in jar file or class path since usually a browser will not request an extern url to the server!
            //        do this only if the option (e.g. LOOKINTOJAR) is activated
            //   3. serve it from a listix format with the name (e.g. "GET /blahblah")
            //
            // 2015.08.12
            // TODO: it would be possible to give the listix format preference, so if both exists, listix format and file, then listix format wins!
            //       It sound logical but I don't know if it solves anything appart from masking specific files
            //       This might cause also "difficult to find" behaviors : why this file is not served ? etc...
            //       Finally, listix format serves per default plain text ("text/html"), if we want to override serving a file
            //       with another (binary file) we should have the mechanism to tell doing it with conten-type "application/octet-stream" (for example)
            //

            File file2serve = wantServeFile (reke.theUri);

            // if browser request a css file we have to send content-type text/css (use the function getContentTypeFromFileName)
            // if not the css styles defined in the file will not be applied !!
            // but if we want to just download it as a file is better to send Content-Type "application/octet-stream"
            //
            boolean servingAsFile = fileServerString != null && reke.theUri.startsWith ("/" + fileServerString);

            if (file2serve != null)
            {
               out ("we want to serve the file [" + file2serve + "]");
               // serving a file
               respa = new httpResponseData (outputStream,
                                             file2serve,
                                             servingAsFile ? "application/octet-stream": getContentTypeFromFileName (file2serve.getName ()),
                                             responseHeaders);
            }
            else
            {
               //!!  // either serve from listix or try a resource file (e.g. packed in jar)
               //!!  //
               //!!  String lsxFormat = getLsxFormat4Response (req);
               //!!  if (theListixLogic.getVarEva (lsxFormat) == null)
               //!!  {
               //!!     // try here
               //!!     respa = defaultHTML ("with no response for [" + lsxFormat + "]!");
               //!!  }

               out ("we want to serve from listix [" + getLsxFormat4Response (reke) + "]");
               // build response using listix
               //
               respa = new httpResponseData (outputStream, buildResponse (reke), getContentType (reke), responseHeaders);
            }

            out ("sending response");
            respa.send ();
            out ("response sent");
            client.close ();
            out ("client closed");
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

   public static void main (String [] args)
   {
      micoHttpServer ser = new micoHttpServer("romualdo", 8080, null);
      ser.start ();
      //set.yield ();
   }
}
