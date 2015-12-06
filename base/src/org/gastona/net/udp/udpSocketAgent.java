/*
package org.gastona.net.udp
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

package org.gastona.net.udp;

import java.net.*;
import java.io.*;

import listix.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.logger;


public class udpSocketAgent extends Thread
{
   private static logger log = new logger (null, "org.gastona.net.udp", null);

   public static final int GASTONA_STANDARD_UDP_PORT = 18110;     // E.Galois birth
   public static final int DEFAULT_DATAGRAMA_MAX_LENGTH = 5120;   // 5k ... whatever ...

   public static final int STATE_AWAKE = 10;
   public static final int STATE_SLEEP = 20;
   public static final int STATE_ZOMBIE = 30;

   protected boolean agentIsClient = false;
   protected int state       = STATE_ZOMBIE;
   protected int TxPort      = -1;
   protected int RxPort      = -1;
   protected int maxDatagramLen = DEFAULT_DATAGRAMA_MAX_LENGTH;

   protected String myName = null;
   protected DatagramSocket theSocket = null;
   protected DatagramPacket datagPacket = null;
   protected InetAddress targetIPaddress = null;

   public udpSocketAgent (String name)
   {
      myName = name;
   }

   public boolean isClient ()
   {
      return agentIsClient;
   }

   private boolean isConfigured ()
   {
      return RxPort != -1 ^ TxPort != -1;
   }

   public void configureClient (String targetIP, int targetPort)
   {
      agentIsClient = true;
      TxPort = targetPort;
      RxPort = -1;
      if (targetIP == null || targetIP.length () == 0 || targetPort <= 0)
      {
         log.severe ("configureClient", "bad configuration no valid ip and port specified!");
         return;
      }
      try
      {
         targetIPaddress = InetAddress.getByName (targetIP);
      }
      catch (Exception e)
      {
         log.err ("configureClient", "exception getting Ip address from " + targetIP + " !");
         return;
      }

      if (targetIPaddress != null)
         log.dbg (2, "configureClient", "client " + myName + " ready to send to " + targetIPaddress + ":" + TxPort);
      else
         log.err ("configureClient", "client " + myName + " didn't find IP address " + targetIPaddress );
      //System.out.println ("COCLI: client " + myName + " ready to send to " + targetIPaddress + ":" + TxPort);
      init ();
   }

   public void configureServer (int listenPort)
   {
      agentIsClient = false;
      if (listenPort <= 0)
      {
         log.severe ("configure", "bad configuration of udpSocketAgent no port specified!");
         return;
      }

      TxPort   = -1;
      RxPort   = listenPort;
      log.dbg (2, "configure", "configured udp agent " + myName + " as server");
      init ();
   }

   public void sendDatagram (String ipAddress, int port, String datagram)
   {
      //System.out.println ("SENDA: " + myName + " ip " + ipAddress + ":" + port );
      //if (targetIPaddress != null)
      //   System.out.println ("SENDA: " + myName + " tg ip " + targetIPaddress.getHostAddress() + ":" + TxPort );

      InetAddress ip = targetIPaddress;

      if (theSocket == null)
      {
         log.err ("sendDatagram",  myName + " txSocket is not initialized, cannot send datagram!");
         return;
      }
      if (ipAddress != null && ipAddress.length () > 0 && port > 0) // if specified address, port as well
      {
         try
         {
            //System.out.println ("SENDA: getting " + ipAddress);
            ip = InetAddress.getByName (ipAddress);
         }
         catch (Exception e)
         {
            log.err ("sendDatagram", "exception getting ip by name from " + ipAddress + " [" + e + "]");
            return;
         }
      }
      int portNr = (port > 0 ? port: TxPort);
      if (ip == null || portNr <= 0)
      {
         log.err ("sendDatagram", myName + " cannot send datagram, ip and/or port not specified!");
         return;
      }

      byte[] buffer = datagram.getBytes ();

      try
      {
         theSocket.send (new DatagramPacket (buffer, buffer.length, ip, portNr));
         log.dbg (2, "sendDatagram", "sent datagram of length " + buffer.length + " to " + ip + ":" + portNr);
      }
      catch (Exception e)
      {
         log.err ("sendDatagram", "exception sending datagram [" + e + "]");
         return;
      }
   }



   public void doSleep ()
   {
      if (state == STATE_AWAKE)
         state = STATE_SLEEP;
   }

   public void doRun ()
   {
      if (state == STATE_SLEEP)
      {
         state = STATE_AWAKE;
         return;
      }
      start (); // thread::start
   }


   public boolean hasFinished ()
   {
      return state == STATE_ZOMBIE;
   }

   protected void init ()
   {
      state = STATE_AWAKE;
      byte [] buffer = new byte[maxDatagramLen];

      try
      {
         if (isClient ())
         {
            theSocket = new DatagramSocket ();
            RxPort = theSocket.getLocalPort ();
            log.dbg (2, "run", "created client udp agent " + myName + " sending to port " + TxPort + " and listening through port " + RxPort);
            //System.out.println ("RUN: created client udp agent " + myName + " sending to port " + TxPort + " and listening through port " + RxPort);
         }
         else
         {
            theSocket = new DatagramSocket (RxPort);
            log.dbg (2, "run", "created server udp agent " + myName + " listening through port " + RxPort);
            //System.out.println ("RUN: created server udp agent " + myName + " listening through port " + RxPort);
         }

         datagPacket = new DatagramPacket (buffer, maxDatagramLen);
      }
      catch (Exception e)
      {
         //example
         //java.net.BindException ...java.net.BindException: Address already in use: Cannot bind

         log.err ("createSocket", "exception creating socket for port " + RxPort + " [" + e + "]");
         state = STATE_ZOMBIE;
         if (theSocket != null) theSocket.close ();
         return;
      }
   }

   public void run ()
   {
      if (theSocket == null)
      {
         log.severe ("configure", "cannot run udpSocketAgent, socket not initialized!");
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
            log.dbg (2, "receiveSocket", "receive " + datagPacket.getLength () + " bytes from " + senderIPStr + " port " + senderPortNr );
         }
         catch (Exception e)
         {
            log.err ("receiveSocket", "exception reading socket for port " + RxPort + " [" + e + "]");
            state = STATE_ZOMBIE;
         }

         if (state == STATE_AWAKE)
         {
            if (isClient ())
            {
            }
            else
            {
               // remember last sender IP/port
               targetIPaddress = senderIP;
               TxPort = senderPortNr;
            }

            String str = new String (datagPacket.getData (), 0, datagPacket.getLength ());
            Mensaka.sendPacket (myName + " udp", null, new String [] { str, senderIPStr, "" + senderPortNr });
         }
         else
         {
            String str = new String (datagPacket.getData (), 0, datagPacket.getLength ());
            log.err ("receiveSocket", myName + " ignoring datagram while sleeping port " + RxPort + " [" + str + "]");
         }
      } while (state == STATE_AWAKE);

      state = STATE_ZOMBIE;
      log.dbg (2, "receiveSocket", myName + " will be joined and closed, port " + RxPort + " will be freed");

      if (theSocket != null) theSocket.close ();
      try { this.join(); } catch (Exception e) {};
   }
}


/*
cliente


   abre txSocket

      txSocket .getLocalAddress ()
      abre rxSocket (txSocket .getLocalPort ())

   send ()
      txSocket.send (new datagram ())

   try rxSocket.receive ()

       Mensaka.sendPacket (actionMensakaMessage, null, new String [] { senderIP, senderPort, str });

servidor

      abre rxSocket (port)

   send ()
      ??

   try rxSocket.receive ()

       Mensaka.sendPacket (actionMensakaMessage, null, new String [] { senderIP, senderPort, str });


*/