/*
package org.gastona.net.http
(c) Copyright 2015-2021 Alejandro Xalabarder Aulet

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
import listix.*;
import de.elxala.zServices.logger;

/**
   read a http header until empty line
   the header can be either the request header as well as the
   multipart body header, content-disposition header etc

*/
public class headerTextReader
{
   private String serverName = "";
   private String lastLine = "";
   private List linesBuffer = new Vector ();
   private boolean vEOH = false; // End Of Header

   // control variables
   private boolean lastCR = false;
   private boolean ignoreFirstEmptyLines;

   public headerTextReader (String srvName, boolean ignorePreliminarEmptyLines)
   {
      serverName = srvName;
      ignoreFirstEmptyLines = ignorePreliminarEmptyLines;
   }

   public List getLines ()
   {
      return linesBuffer;
   }

   public boolean EOH ()
   {
      return vEOH;
   }

   private void clear ()
   {
      lastLine = "";
      linesBuffer = new Vector ();
      vEOH = false;
      lastCR = false;
   }

   public int readHeader (httpStreamReader inStream)
   {
      clear ();
      int alreadyRead = inStream.getConsumedBytes ();
      do
      {
         if (isDebugging (10)) out (10, "insReadBytes");
         inStream.readBytes ();
         if (isDebugging (10)) out (10, "insReadBytes length " + inStream.getArrayLength ());
         if (inStream.getArrayLength () <= 0) break;

         if (isDebugging (6))
            out (6, "reading header " + inStream.getArrayLength () +  " bytes");

         inStream.pushBackBytesFrom (consumeBytes (inStream.getByteArray (), inStream.getArrayLength ()));
         //out (10, "BRUTOX [" + new String (fuente) + " ]");
      }
      while (false == EOH ());
      out (10, "readerHeader ended = " + EOH ());
      return inStream.getConsumedBytes () - alreadyRead;
   }

   public TreeMap parseFromRow (int fromRow)
   {
      TreeMap heaMap = new TreeMap ();
      for (int indx = fromRow; indx < getLines ().size (); indx ++)
      {
         String lin = getLine(indx);
         if (lin.length () == 0) break; // end of header and start of the body if any

         int sep = lin.indexOf (':');
         if (sep > 0)
         {
            // NO LOWERCASE!
            //String attName = lin.substring (0, sep).toLowerCase ();

            String attName = lin.substring (0, sep);
            String attValue = lin.substring (sep+1);
            attValue = attValue.replaceAll("^[\\s\\t]+","");
            attValue = attValue.replaceAll("[\\s\\t]+$","");
            heaMap.put (attName, attValue);
         }
      }
      return heaMap;
   }

   public String getLine (int indx)
   {
      if (indx >= 0 && indx < linesBuffer.size ())
         return (String) linesBuffer.get (indx);

      return "";
   }

   //(o) devnote_algorithms_reading manually RT LF

   private static final byte CR = 13;
   private static final byte LF = 10;

   /// consume the given bytes then return null or not consume all them
   /// and return an array of not consumed bytes
   public int consumeBytes (byte [] data, int len)
   {
      if (data.length == 0 || len <= 0) return 0;

      // refuse to read more data!
      if (EOH ()) return len;

      int tok = 0;

      //handle CR at the end of data
      //
      if (lastCR)
      {
         addToLines ("", true);
         tok = data[0] == LF ? 1: 0;
      }
      lastCR = data[len-1] == CR; // only once! if we have for instance [CR][CR][CR] is a wrong text!
      if (lastCR) len --;

      // fill lines list
      //
      do
      {
         int hasta = tok;
         while (hasta < len && data[hasta] != CR && data[hasta] != LF) hasta ++;

         if (isDebugging (16))
            out (16, "consumeBytes [" + new String (data, tok, hasta-tok) + "]");

         addToLines (new String (data, tok, hasta-tok), (hasta < len));

         if (isDebugging (16))
            out (16, "tok " + tok + " hasta " + hasta);

         boolean wasRTLF = (hasta+1 < len && data[hasta] == CR && data[hasta+1] == LF);
         tok = hasta + (wasRTLF ? 2 : 1);
      }
      while (!EOH () && tok < len);
      return tok;
   }

   private void addToLines (String line, boolean addNewLine)
   {
      lastLine += line;

      if (! addNewLine) return;

      boolean emptyLine = lastLine.length () == 0;

      if (ignoreFirstEmptyLines && emptyLine && linesBuffer.size () == 0)
      {
         // ignore all CRLF previous to request line
         // only for fist Header of http request!!!!
         return;
      }

      // add the line to the lines buffer
      //
      linesBuffer.add (lastLine);

      if (isDebugging (14))
         out (14, "addLine [" + lastLine + "] ");
      lastLine = "";

      vEOH = emptyLine;
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
      micoHttpServer.out (serverName, sa);
   }

   private void out (int level, String sa)
   {
      micoHttpServer.out (serverName, level, sa);
   }
}
