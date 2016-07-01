/*
library listix (www.listix.org)
Copyright (C) 2012 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command HTTPREQUEST

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       HTTPREQUEST
   <groupInfo>  internet
   <javaClass>  listix.cmds.cmdHTTPRequest
   <importance> 5
   <desc>       //Makes an HTTP request

   <help>
      //
      // Makes an HTTP request
      //


   <aliases>
      alias
      HTTPREQUEST
      HTTPREQ
      HTTP

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Makes an HTTP request

   <syntaxParams>
      synIndx, name           , defVal    , desc
         1   , url            ,           , //Url including protocol and parameters
         1   , method         , GET       , //one of GET, PUT, etc

   <options>
      synIndx, optionName  , parameters           , defVal, desc
         1   , HEADER      , "text | prop, value" ,       , //Headers of the request method (e.g. GET). "Content-Length" is calculated if necessary (body is given)
         1   , BODY        , text                 ,       , //Place in this lines the body of the http request
         1   , CHARSET     , charSetName          , UTF-8 , //Charset for the receiving data. Examples ISO-8859-1, UTF-8, US-ASCII. Note: don't know if useful but if desired to use the default charset for the JVM (see java.nio.charset.Charset documentation) then set the value "-" or "none"

   <examples>
      gastSample

      Http simple
      Http couchDB
      Http SOAP

   <Http simple>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "Http simple", 200, 300
      //
      //#listix#
      //
      //   <main>
      //      HTTP, http://www.google.com/webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=wakeupthecat

   <Http couchDB>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "Http simple", 200, 300
      //
      //#listix#
      //
      //   <main>
      //      HTTP, http://127.0.0.1:5984/_all_dbs

   <Http SOAP>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "Http SOAP", 200, 300
      //
      //#listix#
      //
      //   <main>
      //      HTTP, http://www.webservicex.net/globalweather.asmx, POST
      //          , HEADER, //Content-Type: text/xml;charset=UTF-8
      //          , HEADER, //SOAPAction: "http://www.webserviceX.NET/GetWeather"
      //          , HEADER, //Host: www.webservicex.net
      //          ,       , //<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:web="http://www.webserviceX.NET">
      //          ,       , //   <soap:Header/>
      //          ,       , //   <soap:Body>
      //          ,       , //      <web:GetWeather>
      //          ,       , //         <web:CityName>Stuttgart</web:CityName>
      //          ,       , //         <web:CountryName>Germany</web:CountryName>
      //          ,       , //      </web:GetWeather>
      //          ,       , //   </soap:Body>
      //          ,       , //</soap:Envelope>

#**FIN_EVA#

*/

package listix.cmds;


import java.io.*;
import java.net.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.langutil.*;


/// Explanation with samples about HttpURLConnection
/// http://stackoverflow.com/questions/2793150/using-java-net-urlconnection-to-fire-and-handle-http-requests/2793153#2793153


public class cmdHTTPRequest implements commandable
{
   private static final String CRLF = "\r\n";

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "HTTPREQUEST",
          "HTTP",
          "URLREQUEST",
          "URL",
       };
   }


   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      // ::execute - some helpful comment for all the possible syntaxes
      //
      //      comm____   , par1_____      , par2______
      //      HTTPREQUEST, Url complete   , method [GET]
      //                 , HEADER, text
      //                 , HEADER, text
      //                 , BODY  , text
      //                 , BODY  , text
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String url    = cmd.getArg (0);
      String method = cmd.getArg (1);
      if (method.length () == 0)
         method = "GET";

      // DEFAULT charSet is UTF-8, if desired the JVM default charset then CHARSET has to be "-", "none" or "JVMdefault"
      String readerCharSet = cmd.takeOptionString(new String [] { "CHARSET", "RESPONSE CHARSET" }, "UTF8" );
      boolean specifiedCharSet = !readerCharSet.equals ("-") &&
                                 !readerCharSet.equalsIgnoreCase ("none") &&
                                 !readerCharSet.equalsIgnoreCase ("JVMdefault");

      BufferedReader isreader = null;
      HttpURLConnection con = null;

      try
      {
         URL urlObj = new URL(url);
         if (urlObj == null)
         {
            cmd.getLog().err ("HTTPREQUEST", "wrong url [" + url + "]");
            return 1;
         }
         con = (HttpURLConnection) urlObj.openConnection();

         con.setRequestMethod(method);

         int count = 0;
         do
         {
            String [] header = cmd.takeOptionParameters(new String [] { "HEADER", "HEAD" } );
            if (header == null) break;
            if (header.length == 1)
            {
               int indx = header[0].indexOf (":");
               con.setRequestProperty (header[0].substring (0, indx), header[0].substring (indx+1));
            }
            else if (header.length == 2)
            {
               con.setRequestProperty (header[0], header[1]);
            }
            else
               cmd.getLog().err ("HTTPREQUEST", "option REQUEST HEADER (count " + count + ") has length " + header.length + " has to have 1 or 2 parameters!");
            count ++;
         } while (true);


         // send body if any
         count = 0;
         DataOutputStream bodyWr = null;
         do
         {
            String [] body = cmd.takeOptionParameters(new String [] { "REQUESTBODY", "BODY", "" } );
            if (body == null) break;
            if (body.length == 1)
            {
               if (bodyWr == null)
               {
                  con.setDoOutput(true);
                  if (method == "GET")
                     cmd.getLog().err ("HTTPREQUEST", "try to send a http GET with body. This unfortunatelly does not work with java class HttpURLConnection, a POST will be send!");

                  con.setRequestMethod(method);
                  bodyWr = new DataOutputStream(con.getOutputStream());
               }
               bodyWr.writeBytes (body[0] + CRLF);
            }
            else
               cmd.getLog().err ("HTTPREQUEST", "option BODY (count " + count + ") has length " + body.length + " but it has to have just one parameter!");
            count ++;
         } while (true);

         if (bodyWr != null)
         {
            bodyWr.flush ();
            bodyWr.close ();
         }

         int responseCode = con.getResponseCode();
         cmd.getLog().dbg (2, "HTTPREQUEST", "response code " + responseCode + " to sent [" + method + "] request to URL : " + url);

         //read the result from the server
         if (specifiedCharSet)
         {
            cmd.getLog().dbg (2, "HTTPREQUEST", "reading HTTP response using charSet \"" + readerCharSet + "\"");
            isreader  = new BufferedReader(new InputStreamReader (con.getInputStream(), readerCharSet));
         }
         else
            isreader  = new BufferedReader(new InputStreamReader (con.getInputStream())); // use JVM default charset (which is ? not clear, see CharSet doc)

         String line;
         while ((line = (String) isreader.readLine()) != null)
         {
            cmd.getListix ().printTextLsx (line);
            cmd.getListix ().newLineOnTarget ();
         }
         isreader.close ();
      }
      catch (Exception e)
      {
         cmd.getLog().severe ("HTTPREQUEST", "Http socket exception " + e);
      }
         //catch (MalformedURLException ProtocolException IOException
      finally
      {
         try {
            if (isreader != null) isreader.close();
            // if (con != null) con.disconnect ();
         }
         catch (IOException ioException) {
            cmd.getLog().severe ("HTTPREQUEST", "Http closing connection exception " + ioException);
         }
      }

      cmd.checkRemainingOptions ();
      return 1;
   }
}
