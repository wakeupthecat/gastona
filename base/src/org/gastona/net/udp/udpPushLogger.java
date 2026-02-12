/*
package org.gastona.net.udp
(c) Copyright 2022 Alejandro Xalabarder Aulet

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
   Agent that is configured with an IP (default 127.0.0.1) and a port (default 11883)
   and is ready to send content through it without waiting for a client setting anything

   The difference between udpSocketLogAgent and udpPushLogger can be depicted as follows


      udpSocketLogAgent
            listen to 11882
            when a client send something to this port get the IP and client Port
            and use it to send messages

      udpPushLogger
            don't listen to any port (no thread)
            use 127.0.0.1:11883 to send messages

*/
public class udpPushLogger
{
   public static final int STANDARD_PUSH_LOG_UDP_PORT = 11883;

   public static final String SEPA = ",";

   protected DatagramSocket theSocket = null;
   protected InetAddress TxIP = InetAddress.getLoopbackAddress();
                                // InetAddress.getByName("localhost");
   protected int TxPort      = STANDARD_PUSH_LOG_UDP_PORT;

   public udpPushLogger ()
   {
      configure ("localhost", STANDARD_PUSH_LOG_UDP_PORT);
   }

   public udpPushLogger (String targetIP, int targetPort)
   {
      configure (targetIP, targetPort);
   }

   public void configure (String targetIP, int targetPort)
   {
      try
      {
         if (theSocket == null)
            theSocket = new DatagramSocket ();

         if (targetIP != null && targetIP.length () > 0)
            TxIP = InetAddress.getByName(targetIP);
         if (targetPort > 0)
            TxPort = targetPort;
      }
      catch (Exception e)
      {
         System.err.println ("udpPushLogger::configure: exception targetIP " + targetIP + " targetPort " + targetPort + " !\n" + e);
         return;
      }
   }

   protected void sendSocket (String message)
   {
      byte [] buff = message.getBytes ();
      try
      {
         theSocket.send (new DatagramPacket (buff, buff.length, TxIP, TxPort));
      }
      catch (Exception e)
      {
         System.err.println ("udpSocketLogAgent: error exception " + e);
         return;
      }
   }

   public void emitMessage (long millis, logClient cli, int msgLevel, String context, String message, String [] extraInfo, StackTraceElement[] stackElements)
   {
      if (theSocket == null || TxIP == null) return;

      StringBuffer text = new StringBuffer ();

      text.append (message);
      text.append (":" + logServer.formatValues (extraInfo, " / "));

      sendSocket (millis + SEPA +
                  cli.clientID + SEPA +
                  cli.clientStr + SEPA +
                  msgLevel + SEPA +
                  utilEscapeStr.escapeStr (context) + SEPA +
                  "\"" + utilEscapeStr.escapeStr (text.toString ()) + "\""
                  );

      if (msgLevel <= logServer.LOG_SEVERE_ERROR)
      {
         boolean fromapp = stackElements != null;
         StackTraceElement [] arrStkElem = fromapp ? stackElements: jsys.getNowStackTrace();

         // ========== NEW Stack log
         for (int ii = 0; ii < arrStkElem.length; ii ++)
         {
            sendSocket (millis + SEPA +
                        cli.clientID + SEPA +
                        cli.clientStr + SEPA +
                        msgLevel + SEPA +
                        (fromapp ? "appStackTrace": "sysStackTrace") + SEPA +
                        "\"" + utilEscapeStr.escapeStr (arrStkElem[ii].toString ()) + "\""
                       );
         }
      }
   }
}