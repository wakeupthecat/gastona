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

import java.util.*;
import java.io.*;
import java.net.*;
import de.elxala.langutil.filedir.*;

public class httpResponseData
{
   private byte [] respBody = null;
   private File respFile = null;
   private OutputStream outStream = null;
   private String mContentTypeStr = "";
   private List myHeaderLines = null;
   private static final String CRLF = "\r\n";

   /**
      Build a http response
         @outputStream   output stream where the response has to sent
         @respTxt        response as string without UTF-8 encoding
         @contentTypeStr Content type, it can be

               empty string = no content type specified
               null         = "text/html; charset=utf-8"
               other        = the one specified
   */
   public httpResponseData (OutputStream outputStream, String respTxt, String contentTypeStr, List headerLines)
   {
      try
      {
         outStream = outputStream;
         respBody = respTxt.getBytes("UTF-8");
         mContentTypeStr = (contentTypeStr != null) ? contentTypeStr: "text/html; charset=utf-8";
         myHeaderLines = headerLines;
      }
      catch (Exception e)
      {
      }
   }

   public httpResponseData (OutputStream outputStream, File file2serve, String contentTypeStr, List headerLines)
   {
      try
      {
         outStream = outputStream;
         respFile = file2serve;
         mContentTypeStr = (contentTypeStr != null) ? contentTypeStr: "application/octet-stream";
         myHeaderLines = headerLines;
      }
      catch (Exception e)
      {
      }
   }


   private byte [] crlfAndBytes (String content)
   {
      return (content + CRLF).getBytes ();
   }
   
   private void writeResponseHeaders ()
   {
      if (myHeaderLines == null) return;
      try
      {
         for (int ii = 0; ii < myHeaderLines.size (); ii ++)
            outStream.write (crlfAndBytes ((String) myHeaderLines.get (ii)));
      }
      catch (Exception e)
      {
      }
   }   

   public void send ()
   {
      //HTTP/1.1 200 OK
      //Content-Type: text/html; charset=utf-8
      //Content-Length: length
      try
      {
         outStream.write (crlfAndBytes ("HTTP/1.1 200 OK"));

         //test for AJAX (XMLHttpRequest) response
         //
         //outStream.write (crlfAndBytes ("Vary: Accept-Encoding, Origin"));
         //outStream.write (crlfAndBytes ("Access-Control-Allow-Origin: *"));

         if (mContentTypeStr.length () > 0)
         {
            outStream.write (crlfAndBytes ("Content-Type: " +  mContentTypeStr));
         }
         
         if (respBody != null)
         {
            outStream.write (crlfAndBytes ("Content-Length: " + respBody.length));
            writeResponseHeaders ();
            outStream.write (crlfAndBytes ("")); // BODY separation !!!
            outStream.write (respBody);

            if (isDebugging ()) // avoid to string convertion if it not necessary
            {
               out ("--- RESPONSE BODY");
               out (new String (respBody));
               out ("---");
            }
         }
         if (respFile != null)
         {
            outStream.write (crlfAndBytes ("Content-Length: " + respFile.length ()));
            writeResponseHeaders ();
            outStream.write (crlfAndBytes ("")); // BODY separation !!!
            TextFile fi = new TextFile ();
            if (fi.fopen (respFile.getPath (), "rb"))
            {
               byte [] arr = new byte[1024];
               int leo = 0;
               
               do
               {
                  leo = fi.readBytes (arr);
                  if (leo == -1) break;
                  outStream.write (arr, 0, leo);
               } while (!fi.feof ());
            }
         }
         
         outStream.close ();
      }
      catch (Exception e)
      {
      }
   }
   
   public boolean isDebugging ()
   {
      return micoHttpServer.isDebugging ();
   }
   
   public boolean isDebugging (int verboseLev)
   {
      return micoHttpServer.isDebugging (verboseLev);
   }

   private void out (String sa)
   {
      micoHttpServer.out (sa);
   }

   private void out (int level, String sa)
   {
      micoHttpServer.out (level, sa);
   }
}


/*

de http://www.opencalais.com/HTTPexamples

REST via HTTP POST

The following is a sample REST API call via HTTP POST request and response. The placeholders 
shown need to be replaced with actual values. Please note that the content sent using this method 
needs to be escaped. Please also note that all of the arguments sent using this method must be URL-encoded. 

-----http request

      POST /enlighten/rest HTTP/1.1
      Host: api.opencalais.com
      Content-Type: application/x-www-form-urlencoded
      Content-Length: length

      licenseID=string&content=string&/paramsXML=string



-----http response

      HTTP/1.1 200 OK
      Content-Type: text/xml;charset=utf-8
      Content-Length: length

      string

*/
