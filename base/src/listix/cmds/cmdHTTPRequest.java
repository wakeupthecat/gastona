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
         1   , IPaddress      ,           , //IP address of the http server
         1   , port           , 80        , //Port of the server (default value is 80)

   <options>
      synIndx, optionName  , parameters, defVal, desc
         1   , HEADER      , text      ,  0    , //Request method (e.g. "GET / HTTP/1.1") and headers. NOTE! If body is given using BODY option the header "Content-Length" is calculated and added to the headers automatically
         1   , BODY        , text      ,  0    , //Place in this lines the body of the http request
         1   , CHARSET     , charSetName, UTF-8, //Charset for the receiving data. Examples ISO-8859-1, UTF-8, US-ASCII. Note: don't know if useful but if desired to use the default charset for the JVM (see java.nio.charset.Charset documentation) then set the value "-" or "none"

   <examples>
      gastSample

      Http simple
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
      //      HTTP, www.google.com
      //          ,, //GET /webhp?sourceid=chrome-instant&ion=1&espv=2&ie=UTF-8#q=wakeupthecat HTTP/1.1

   <Http SOAP>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "Http SOAP", 200, 300
      //
      //#listix#
      //
      //   <main>
      //      HTTP, www.webservicex.net
      //          ,     , //POST /globalweather.asmx HTTP/1.1
      //          ,     , //Content-Type: text/xml;charset=UTF-8
      //          ,     , //SOAPAction: "http://www.webserviceX.NET/GetWeather"
      //          ,     , //Host: www.webservicex.net
      //          , body, //<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:web="http://www.webserviceX.NET">
      //          , body, //   <soap:Header/>
      //          , body, //   <soap:Body>
      //          , body, //      <web:GetWeather>
      //          , body, //         <web:CityName>Stuttgart</web:CityName>
      //          , body, //         <web:CountryName>Germany</web:CountryName>
      //          , body, //      </web:GetWeather>
      //          , body, //   </soap:Body>
      //          , body, //</soap:Envelope>



#**FIN_EVA#

*/

package listix.cmds;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.*;
import java.util.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.langutil.*;


//(o) TODO_listix_command_HTTPRequest review this command! (when I first programmed it I have no idea about HTTP!)


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
      //      HTTPREQUEST, IP address     , port
      //                 , HEADER, text
      //                 , HEADER, text
      //                 , BODY, text
      //                 , BODY, text

      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String HTTP_IPadd  = cmd.getArg(0);
      String HTTPortPar  = cmd.getArg(1);
      int HTTP_port = 80;  // 80 is the standard HTTP port
      if (HTTPortPar != null && HTTPortPar.length () > 0)
      {
         HTTP_port = stdlib.atoi (HTTPortPar);
      }

      // DEFAULT charSet is UTF-8, if desired the JVM default charset then CHARSET has to be "-", "none" or "JVMdefault"
      String readerCharSet = cmd.takeOptionString(new String [] { "CHARSET", "RESPONSE CHARSET" }, "UTF8" );
      boolean specifiedCharSet = !readerCharSet.equals ("-") &&
                                 !readerCharSet.equalsIgnoreCase ("none") &&
                                 !readerCharSet.equalsIgnoreCase ("JVMdefault");
     // HTTP request ...
     {
         //HttpURLConnection connection = null;
         Socket sock = null;
         BufferedReader isreader = null;
         OutputStream oswriter = null;

         try {
            sock = new Socket(HTTP_IPadd, HTTP_port);

            //get the output stream writer and write the output to the server
            // first the header
            //
            int count = 0;
            oswriter = sock.getOutputStream();
            do
            {
               String [] HTTPRequestHeader = cmd.takeOptionParameters(new String [] { "REQUESTHEADER", "REQUEST", "REQ", "HEADER", "HEAD", "" } );
               if (HTTPRequestHeader == null) break;
               if (HTTPRequestHeader.length == 1)
               {
                  oswriter.write ((HTTPRequestHeader[0] + CRLF).getBytes());
               }
               else
                  cmd.getLog().err ("HTTPREQUEST", "option REQUEST HEADER (count " + count + ") has length " + HTTPRequestHeader.length + " but has to have just one parameter!");
               count ++;
            } while (true);

            // now the body
            //         , BODY, //blah blah ...
            //         , BODY, //etc etc  ...
            //
            //
            List bodyList = new Vector ();
            count = 0;
            int contentLength = 0;
            do
            {
               String [] HTTPRequestBody = cmd.takeOptionParameters(new String [] { "REQUESTBODY", "BODY" } );
               if (HTTPRequestBody == null) break;
               if (HTTPRequestBody.length == 1)
               {
                  bodyList.add (HTTPRequestBody[0]);
                  contentLength += (2 + HTTPRequestBody[0].length ()); // we will add return line feed (2 bytes) after each line!
               }
               else
                  cmd.getLog().err ("HTTPREQUEST", "option BODY (count " + count + ") has length " + HTTPRequestBody.length + " but it has to have just one parameter!");
               count ++;
            } while (true);

            if (contentLength > 0)
            {
               oswriter.write (("Content-Length: " + contentLength + CRLF).getBytes());
               oswriter.write (CRLF.getBytes()); // empty line separates request header from body
               for (int ii = 0; ii < bodyList.size (); ii ++)
               {
                  oswriter.write (((String) bodyList.get(ii) + CRLF).getBytes());
               }
            }
            else
               oswriter.write (CRLF.getBytes()); // empty line ends header since body is missing!
            oswriter.flush();
            sock.shutdownOutput();

            // that.printTextLsx ("Socket oswriter closed");

            //read the result from the server
            if (specifiedCharSet)
            {
               cmd.getLog().dbg (2, "HTTPREQUEST", "reading HTTP response using charSet \"" + readerCharSet + "\"");
               isreader  = new BufferedReader(new InputStreamReader (sock.getInputStream(), readerCharSet));
            }
            else
               isreader  = new BufferedReader(new InputStreamReader (sock.getInputStream())); // use JVM default charset (which is ? not clear, see CharSet doc)

            String line;
            while ((line = (String) isreader.readLine()) != null)
            {
               cmd.getListix ().printTextLsx (line);
               cmd.getListix ().newLineOnTarget ();
            }
         }
         catch (Exception e)
         {
            cmd.getLog().severe ("HTTPREQUEST", "Http socket exception " + e);
         }
         //catch (MalformedURLException ProtocolException IOException
         finally
         {
            try{
               if (isreader != null) isreader.close();
               if (oswriter != null) oswriter.close();
               if (sock != null) sock.close();
            }
            catch(IOException ioException){
               cmd.getLog().severe ("HTTPREQUEST", "Http closing socket exception " + ioException);
            }
         }
     }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
