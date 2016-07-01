/*
package org.gastona.net.http
(c) Copyright 2015 Alejandro Xalabarder Aulet

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
   read a http stream and keep track of read and consumed bytes
*/
public class httpStreamReader
{
   private static final int NO_SHIFT = -1;
   private static final int UNKNOWN = -1;

   private InputStream inStream = null;

   //(o) REVIEW_sockets buffer size
   // I thik it has no influence, I've observed that in server the socket read in chunks of 1452 bytes
   // for instance on uploading a file
   //   inStream read available 0 mayToread 102400
   //   inStream read 1452 bytes
   //
   private int BUFFER_SIZE = 40*1024;

   private byte [] buff = new byte[BUFFER_SIZE];
   private int decala = NO_SHIFT;
   private int lastLength = 0;
   private boolean EOS = false;
   private int totalBytesRead = 0;
   private int maxBytesToRead = UNKNOWN;

   public static final int BOUNDARY_MAXEXPECTEDLEN = 100;

   // RFC1521 says that a boundary "must be no longer than 70 characters, not counting the two leading hyphens".

   public httpStreamReader (InputStream inputStream)
   {
      inStream = inputStream;
      maxBytesToRead = UNKNOWN;
   }

   // NOTE! end of stream does not mean that there is no more
   public boolean EndOfStream ()
   {
      return EOS;
   }

   public byte [] getByteArray ()
   {
      return buff;
   }

   public int getArrayLength ()
   {
      return lastLength;
   }

   // set this value just before reading the body givin exactly the body length
   // this is necessary to prevent reading after the body length which would cause
   // the stream reader to block
   //
   public void setLeftBodyToRead (int lenBytes)
   {
      maxBytesToRead = getConsumedBytes () + lenBytes;
   }

   public boolean areBytesToConsume ()
   {
      // NOTE: stream can be exhausted and still have bytes to read (not consumed) !!!
      //
      if (EndOfStream ()) return false;
      if (maxBytesToRead == UNKNOWN) return true; // we don't know yet!
      return getConsumedBytes () < maxBytesToRead;
   }

   protected boolean streamExhausted ()
   {
      return (maxBytesToRead != UNKNOWN && totalBytesRead >= maxBytesToRead);
   }

   public boolean readBytes ()
   {
      if (!areBytesToConsume ()) return false;

      if (decala != NO_SHIFT && decala >= 0)
      {
         //shift not consumed bytes to the begining
         if (decala >= 0 && decala < lastLength) // extra check ...
         {
            lastLength -= decala;
            System.arraycopy (buff, decala, buff, 0, lastLength);
         }
         decala = NO_SHIFT; // important to reset it here!
      }
      else lastLength = 0;

     int maxToRead = buff.length - lastLength;
     if (!streamExhausted () && maxToRead > 0)
     {
         int leoBytes = -1;
         try {
            out (10, "inStream read available " + inStream.available () + " mayToread " + maxToRead);
            leoBytes = inStream.read (buff, lastLength, maxToRead);
            out (10, "inStream read " + leoBytes + " bytes");
          } catch (Exception e) { out (0, e+""); }

         //(o) TODO_httpStreamReader should not be added only if not eos ?
         totalBytesRead += leoBytes;
         lastLength += leoBytes;

         EOS = leoBytes == -1;
     }

     return lastLength > 0;
   }

   public void pushBackBytesFrom (int fromIndx)
   {
      decala = (fromIndx >= 0 && fromIndx < lastLength) ? fromIndx: NO_SHIFT;
   }

   public int getConsumedBytes ()
   {
      return totalBytesRead - (decala >= 0 ? (lastLength-decala): 0);
   }

   public static void out (String sa)
   {
      micoHttpServer.out (sa);
   }

   public static void out (int level, String sa)
   {
      micoHttpServer.out (level, sa);
   }
}
