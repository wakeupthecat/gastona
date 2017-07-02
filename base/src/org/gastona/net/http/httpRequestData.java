/*
package org.gastona.net.http
(c) Copyright 2014,2015 Alejandro Xalabarder Aulet

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
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.logger;
import de.elxala.db.utilEscapeStr;


public class httpRequestData
{
   protected String memFileName4RequestBody = "";
   public String theMethod = "";
   public String theUri = "";
   public String theHttpVersion = "";
   public Map theUriParameters = new TreeMap ();
   public Map theHeader = new TreeMap ();
   public List theUploadedFiles = null;
   public String theBody = "";
   public String theResponseStr = "";

   // control variables
   private boolean lastCR = false;

   public httpRequestData (String memFileName4reqBody)
   {
      memFileName4RequestBody = memFileName4reqBody;
   }

   public int getUploadedFileCount ()
   {
      return theUploadedFiles != null ? theUploadedFiles.size (): 0;
   }

   public String getUploadedFilePath (int indx)
   {
      if (indx < getUploadedFileCount ())
         return (String) theUploadedFiles.get (indx);
      return "";
   }

   // NOTE: parameters "Socket sok, int socketTimeout" are to allow
   //       changing socket timeout when uploading a file (multipart content)
   public void processRequest (httpStreamReader inStream, String responseStrId, Socket sok, int socketTimeout)
   {
      theResponseStr = responseStrId;
      theUploadedFiles = null;

      // Read HTTP request header
      //
      headerTextReader firstHead = new headerTextReader (true);

      firstHead.readHeader (inStream);

      parseRequestURI (firstHead);
      theHeader = firstHead.parseFromRow (1);

      // Procede with body
      //
      String conLen  = (String) theHeader.get ("Content-Length");
      String conType = (String) theHeader.get ("Content-Type");
      boolean isMultiPart = conType != null && conType.startsWith ("multipart/form-data");

      if (conLen == null)
         return;

      int bodyLen = stdlib.atoi (conLen);
      out (10, "header read, body length = " + bodyLen);
      inStream.setLeftBodyToRead (bodyLen);

      if (isMultiPart)
      {
         try { sok.setSoTimeout(socketTimeout); } catch (Exception e) { };
         parseMultiPartBody (inStream);
      }
      else
      {
         parseOtherwiseBody (inStream);
      }
   }

   public byte [] RT_LF = new byte [] { 13, 10 };
   public boolean fRET_LF = false;
   public TextFile fileBodyMPart = null;

   public void doConsume (byte [] arr, int from, int to)
   {
      if (to-from <= 0) return;
      if (fileBodyMPart != null)
      {
         out (8, "write " + (to - from) + " bytes into file");
         fileBodyMPart.writeBytes (arr, from, to - from);
      }
      else
      {
         out (8, "consume part [" + new String (arr, 0, to - from) + "]");
      }
   }

   //(o) devnote_algorithms_reading manually RT LF

   public int consumeMulti (byte [] arr, int from, int to)
   {
      if (to-from <= 0) return 0;

      // check to remove the very last return line feed!
      //
      if (fRET_LF) doConsume (RT_LF, 0, 2);
      fRET_LF = (to - from >= 2) && arr[to-2] == 13 && arr[to-1] == 10;

      if (fRET_LF)
          to -= 2;                     // avoid copying the last two bytes
      else if (arr[to-1] == 13) to --; // not consume (to be pushed back) last byte

      // ok now write "from" "to"
      doConsume (arr, from, to - from);
      return to - from + (fRET_LF ? 2: 0); // if set fRET_LF we have to actually consume (but not copy yet) the 2 bytes
   }

   public void parseMultiPartBody (httpStreamReader inStream)
   {
      headerTextReader partHead = new headerTextReader (false);

      int partNo = 0;
      int BONDLEN = 0;
      boolean endMultipart = false;
      int paciMinus1 = 20;

      do
      {
         partNo ++;

         partHead.readHeader (inStream);

         // first line has to be boundary!
         //
         String boundStr = partHead.getLine(0);
         BONDLEN = boundStr.length ();
         if (BONDLEN == 0)
         {
            out (0, "error : boundary expected but empty line found!");
            return;
         }

         out (8, "part " +  partNo + "  boundary [" + boundStr + "]");

         TreeMap attsPart = partHead.parseFromRow (1);
         String conDispo = (String) attsPart.get ("Content-Disposition");

         String fileName = null;
         if (conDispo != null)
         {
            int indxF1 = conDispo.indexOf ("filename=");
            int indxF2 = conDispo.indexOf ("filename*=");

            if (indxF1 > 0)
               fileName = conDispo.substring(indxF1 + "filename=".length ());
            if (indxF2 > 0)
               fileName = conDispo.substring(indxF2 + "filename*=".length ());

            if (fileName != null && fileName.length () > 0)
            {
               fileName = fileName.replaceAll ("'", "");
               fileName = fileName.replaceAll ("\"", "");

               // convert it to something like a final file name
               fileName = "filesUpload/" + theResponseStr + "_" + naming.toNameISO_9660Joliet (fileUtil.getJustNameAndExtension (fileName));

               // collect new the name of the new file uploaded
               //
               if (theUploadedFiles == null)
                  theUploadedFiles = new Vector ();
               theUploadedFiles.add (fileName);

               fileBodyMPart = new TextFile ();

               if (fileBodyMPart.fopen (fileName, "wb"))
               {
                  out (2, "uploading file " + fileName + " ...");
               }
               else
               {
                  out (0, "cannot open target file " +  fileName + " !!");
                  fileBodyMPart = null;
               }
            }
         }


         // filename="$filename"

         out (10, "CONTENT-DISPOSITION EN ESTE CASO [" + conDispo + "]");

         /// Content-Disposition: attachment; filename*=UTF-8''Na%C3%AFve%20file.txt
         /// Content-Disposition: attachment; filename=Na�ve file.txt
         /// Content-Disposition: attachment; filename=Na%C3%AFve%20file.txt

         // IE envia todo el path !!!
         //  Content-Disposition: form-data; name="fileselect[]"; filename="C:\myfolder\subfolder\myfile.txt"

         boolean boundaryFound = false;
         //int lenToConsume = 0;
         boolean endOfArray = false;
         //boolean fRetLFeed = false;
         boolean sigue = false;
         byte [] boundBytes = boundStr.getBytes ();
         fRET_LF = false;
         do
         {
            endOfArray = false;
            inStream.readBytes ();
            byte [] reaB = inStream.getByteArray ();
            int reaL = inStream.getArrayLength ();
            out (8, "read part bytes " + reaL + " consumed so far " + inStream.getConsumedBytes () + " more bytes pending " + inStream.areBytesToConsume ());

            if (reaL == -1)
            {
               if (paciMinus1-- > 0) continue;
               out (0, "too many failures reading input stream, still " + inStream.areBytesToConsume () + " bytes to cosume");
               break;
            }
            else paciMinus1 = 20;

            int b1 = -1;
            int b2 = 0;
            do
            {
               // NOTE: in one iteration the only thing that we can say is that b1 WAS NOT the start of a boundary
               //       example
               //            boundary "---MIO"
               //            line     " this is ----MIO"
               //                               ^ b1
               b1 ++;
               while (b1 < reaL && reaB[b1] != boundBytes[0]) b1 ++;
               b2 = b1;
               while (b2 < reaL && (b2-b1 < boundBytes.length) && reaB[b2] == boundBytes[b2-b1]) b2 ++;
               boundaryFound = (b2-b1 == boundBytes.length);
               endOfArray = (b2 >= reaL);
               // either we found the boundary or we reach the endof array
            } while (!boundaryFound && !endOfArray);

            endMultipart = (boundaryFound && (b2+1 < reaL) && reaB[b2] == '-' && reaB[b2+1] == '-');
            b1 = consumeMulti (reaB, 0, b1);
            if (!endMultipart)
            {
               if (reaL-b1 > 0)
                   out (8, "pack back not consumed bytes .. " + (reaL-b1));
               inStream.pushBackBytesFrom (b1);
            }
         } while (!boundaryFound && paciMinus1 > 0);

         if (fileBodyMPart != null)
         {
            fileBodyMPart.fclose ();
            //Note: not needed to garbage collection, the problem with taking so long
            //      after uploading a large file was probably due to the zConsole (text too big!)
            //System.gc ();
         }
      } while (!inStream.streamExhausted () && !endMultipart && paciMinus1 > 0);
   }

   public void parseOtherwiseBody (httpStreamReader inStream)
   {
      out (8, "parseOtherwiseBody (consumed header " + inStream.getConsumedBytes () + ")");

      // Saving the body into a memory file ":mem server httpRequestBody"
      //
      //  - saving it into a file directly in addBytes would be more efficient but it is more complicated to be coded.
      //
      theBody = "";
      TextFile fileBody = new TextFile ();
      if (fileBody.fopen (memFileName4RequestBody, "wb"))
      {
         while (inStream.readBytes ())
         {
            out (8, "read body's " + inStream.getArrayLength () +  " bytes (consumed " + inStream.getConsumedBytes () + ", more bytes pending " + inStream.areBytesToConsume () + ")");
            fileBody.writeBytes (inStream.getByteArray (), inStream.getArrayLength ());
            theBody += new String (inStream.getByteArray (), 0, inStream.getArrayLength ());
         }
      }
      fileBody.fclose ();
   }

   public void parseRequestURI (headerTextReader hea)
   {
      //for http we expect three things:
      //    theMethod, theUri (with parameters) and the http version
      //    but keep tolerant for other uses and accept empty values for uri and http version
      //
      theMethod = "";
      theUri = "";
      theHttpVersion = "";

      String lin0 = hea.getLine(0);
      int sep = lin0.indexOf(' ');
      if (sep >= 0)
      {
         theMethod = lin0.substring (0, sep);
         lin0 = lin0.substring (sep + 1);
      }
      else
      {
         theMethod = lin0;
         lin0 = "";
      }

      sep = lin0.indexOf(' ');
      if (sep >= 0)
      {
         theUri = lin0.substring (0, sep);
         theHttpVersion = lin0.substring (sep + 1);
      }
      else
      {
         theUri = lin0;
         lin0 = "";
      }

      // extract parameters from Uri
      //
      sep = theUri.indexOf ('?');
      if (sep > -1)
      {
         // theUri parameters if any
         theUriParameters = decodeUriParameters (theUri.substring (sep + 1));
         theUri = theUri.substring (0, sep);
      }
      else theUriParameters = new TreeMap ();
   }

   private Map decodeUriParameters (String parms)
   {
      Map mapa = new TreeMap ();
      StringTokenizer stoke = new StringTokenizer (parms, "&");
      while (stoke.hasMoreTokens ())
      {
          String e = stoke.nextToken();
          int sep = e.indexOf('=');
          if (sep >= 0)
          {
              mapa.put (utilEscapeStr.desEscapeStr (e.substring(0, sep), "UTF8").trim(),
                        utilEscapeStr.desEscapeStr (e.substring(sep + 1), "UTF8"));
          }
          else
          {
              mapa.put (utilEscapeStr.desEscapeStr(e, "UTF8").trim(), "");
          }
      }
      return mapa;
   }

   private void out (String sa)
   {
      out (5, sa);
   }

   private void out (int level, String sa)
   {
      micoHttpServer.out (level, sa);
   }

   public boolean isMethodGET ()
   {
      return theMethod.equalsIgnoreCase ("GET");
   }

   public boolean isMethodPOST ()
   {
      return theMethod.equalsIgnoreCase ("POST");
   }

   public void dump (String title)
   {
      out (title);

      //for (int pp = 0; pp < getLines ().size (); pp ++)
      //   out (pp + ") \"" + (String) getLines ().get(pp) + "\"");

      out ("--- METHOD = " + theMethod);
      out ("--- URI    = " + theUri);
      out ("--- HTTP   = " + theHttpVersion);

      out ("--- PARAMS");
      Iterator ite = theUriParameters.keySet().iterator();
      while (ite.hasNext ())
      {
         String key   = (String) ite.next();
         String value = (String) theUriParameters.get(key);
         out ("   " + key + " = " + value);
      }

      out ("--- HEADER");
      ite = theHeader.keySet().iterator();
      while (ite.hasNext ())
      {
         String key   = (String) ite.next();
         String value = (String) theHeader.get(key);
         out ("   " + key + " = " + value);
      }

      out ("--- REQUEST BODY");
      out (theBody);
      out ("---");
   }
}
