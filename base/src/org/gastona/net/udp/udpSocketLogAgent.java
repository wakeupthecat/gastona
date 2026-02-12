/*
package org.gastona.net.udp
(c) Copyright 2014-2022 Alejandro Xalabarder Aulet

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

package org.gastona.net.udp;

import java.net.*;
import java.io.*;

import listix.*;
import de.elxala.mensaka.*;
import de.elxala.langutil.*;

import de.elxala.zServices.logServer;
import de.elxala.zServices.logClient;
import de.elxala.db.utilEscapeStr;

/**
   Specialized udpSocketAgent for being used by logServer

   NOTE: no debug messages are allowed in this class!
*/
public class udpSocketLogAgent extends Thread
{
   public static final int GASTONA_STANDARD_LOG_UDP_PORT = 11882;
   public static final int DEFAULT_DATAGRAMA_MAX_LENGTH = 5120;   // 5k ... whatever ...

   public static final int STATE_AWAKE = 10;
   public static final int STATE_SLEEP = 20;
   public static final int STATE_ZOMBIE = 30;

   public static final String SEPA = "|";

   protected int state       = STATE_ZOMBIE;
   protected int TxPort      = -1;
   protected int RxPort      = -1;
   protected int maxDatagramLen = DEFAULT_DATAGRAMA_MAX_LENGTH;

   protected String myName = null;
   protected DatagramSocket theSocket = null;
   protected DatagramPacket datagPacket = null;

   //protected InetAddress TxIP = null;
   protected InetAddress TxIP = InetAddress.getLoopbackAddress(); //  127.0.0.1  suppose local device has initiate the communication

   protected logClient thisLogCli = new logClient ();

   public udpSocketLogAgent (String name, int listenPort)
   {
      thisLogCli.clientID = -1;
      thisLogCli.clientStr = "gastona.net.udp.udpSocketLogAgent";
      myName = name;
      TxPort   = -1;
      RxPort   = listenPort;
      // log.dbg (2, "configure", "configured udp agent " + myName + " as server");
      init ();
      start (); // thread::start
   }

   public boolean canEmitMessages ()
   {
      return theSocket != null && TxIP != null;
   }

   public void emitMessage (int msgLevel, String context, String message)
   {
      emitMessage (-1, thisLogCli, msgLevel, context, message, null, null);
   }

   public void emitMessage (long millis, logClient cli, int msgLevel, String context, String message, String [] extraInfo, StackTraceElement[] stackElements)
   {
      //System.out.println ("SENDA: " + myName + " ip " + ipAddress + ":" + port );
      //if (targetIPaddress != null)
      //   System.out.println ("SENDA: " + myName + " tg ip " + targetIPaddress.getHostAddress() + ":" + TxPort );

      if (!canEmitMessages ()) return;

      byte[] buffer = ((millis < 0 ? "": millis + SEPA) +
                       cli.clientID + SEPA +
                       cli.clientStr + SEPA +
                       msgLevel + SEPA +
                       utilEscapeStr.escapeStr (context) + SEPA +
                       utilEscapeStr.escapeStr (message)
                       ).getBytes ();

      try
      {
         theSocket.send (new DatagramPacket (buffer, buffer.length, TxIP, TxPort));
         //System.out.println ("udpSocketLogAgent: sent datagram of length " + buffer.length + " to " + ip + ":" + portNr);
      }
      catch (Exception e)
      {
         System.err.println ("udpSocketLogAgent: error exception " + e);
         return;
      }
   }

   protected void init ()
   {
      state = STATE_AWAKE;
      byte [] buffer = new byte[maxDatagramLen];

      try
      {
         theSocket = new DatagramSocket (RxPort);
         // log.dbg (2, "run", "created server udp agent " + myName + " listening through port " + RxPort);
         //System.out.println ("RUN: created server udp agent " + myName + " listening through port " + RxPort);

         datagPacket = new DatagramPacket (buffer, maxDatagramLen);
      }
      catch (Exception e)
      {
         //example
         //java.net.BindException ...java.net.BindException: Address already in use: Cannot bind

         System.err.println ("udpSocketLogAgent: exception creating socket for port " + RxPort + " [" + e + "]");
         state = STATE_ZOMBIE;
         if (theSocket != null) theSocket.close ();
         return;
      }
   }

   public void run ()
   {
      if (theSocket == null)
      {
         System.err.println ("udpSocketLogAgent: cannot run udpSocketAgent, socket not initialized!");
         state = STATE_ZOMBIE;
         try { this.join(); } catch (Exception e) {};
         return;
      }

      InetAddress senderIP = null;
      String senderIPStr = "";
      int senderPortNr = -1;

      do
      {
         try
         {
            theSocket.receive (datagPacket);
            senderIP = datagPacket.getAddress ();
            senderIPStr = (senderIP == null ? "IP?": senderIP.getHostAddress());
            senderPortNr = datagPacket.getPort();
            System.out.println ("udpSocketLogAgent: receive " + datagPacket.getLength () + " bytes from " + senderIPStr + " port " + senderPortNr );
         }
         catch (Exception e)
         {
            System.err.println ("udpSocketLogAgent: receiveSocket exception reading socket for port " + RxPort + " [" + e + "]");
            state = STATE_ZOMBIE;
         }

         if (state == STATE_AWAKE)
         {
            // remember last sender IP/port
            TxIP = senderIP;
            TxPort = senderPortNr;

            String str = new String (datagPacket.getData (), 0, datagPacket.getLength ());

            System.out.println ("udpSocketLogAgent: receiveSocket packet [" + str + "]");

            if (str.startsWith ("SETLEVEL "))
            {
               int level = stdlib.atoi (str.substring ("SETLEVEL ".length ()));
               logServer.configureGlobalLogLevel (level);
               emitMessage (0, myName, "debug level set to " + level);
            }
            else if (str.startsWith ("SHOWLEVEL "))
            {
               emitMessage (0, myName, "global log level is " + logServer.getGlobalLogLevel ());
            }
            else if (str.startsWith ("ON"))
            {
               emitMessage (0, myName, "switched ON");
            }
            else if (str.startsWith ("OFF"))
            {
               emitMessage (0, myName, "switched OFF");
               TxIP = null;
               TxPort = -1;
            }
            else
            {
               emitMessage (0, myName, "no te sigo [" + str + "]");
            }
         }
         else
         {
            String str = new String (datagPacket.getData (), 0, datagPacket.getLength ());
            System.err.println ("udpSocketLogAgent: receiveSocket " +  myName + " ignoring datagram while sleeping port " + RxPort + " [" + str + "]");
         }
      } while (state == STATE_AWAKE);

      state = STATE_ZOMBIE;
      System.err.println ("udpSocketLogAgent: receiveSocket " +  myName + " will be joined and closed, port " + RxPort + " will be freed");

      if (theSocket != null) theSocket.close ();
      try { this.join(); } catch (Exception e) {};
   }
}
