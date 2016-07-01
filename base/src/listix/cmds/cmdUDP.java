/*
library listix (www.listix.org)
Copyright (C) 2005 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command UDP

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       UDP
   <groupInfo>  internet
   <javaClass>  listix.cmds.cmdSocketUDP
   <importance> 3
   <desc>       //Send or receive datagrams via UDP protocol


   <help>
      //
      // Experimental : Send or receive datagrams via UDP protocol
      //

   <aliases>
      alias
      UDP


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Send a datagram to an IP Port once without expecting any response
         2   ,    3      , //Opens an UDP client or send-first which will establish a communication with some UDP server or listen-first
         3   ,    3      , //Sends a datagram via the UDP client, responses will be received through the message "<clientName> udp"
         4   ,    3      , //Opens an UDP server or listen-first that first will listen for incomming communications (via message "<serverName> udp") being able to respond to them
         5   ,    3      , //Closes either a UDP client or server

   <syntaxParams>
      synIndx, name         , defVal    , desc
         1   , SEND ONCE    ,
         1   , IPAddress    , localhost , //target IP address
         1   , Port         , 18110     , //target port
         1   , datagram     ,           , //datagram to send

         2   , SET CLIENT
         2   , clientName   ,           , //Name associated with the UDP client, response messages will be received via the message "<clientName> udp"
         2   , IPAddress    , localhost , //target IP address
         2   , Port         , 18110     , //target port

         3   , AGENT SEND
         3   , agentName   ,           , //UDP client name previously created with UDP, SET CLIENT command
         3   , datagram     ,           , //datagram to send
         3   , IPAddress    , localhost , //A client udp agent should not specify this, a server only if is different from the last received datagram IP
         3   , Port         , 18110     , //A client udp agent should not specify this, a server only if is different from the last received datagram Port

         4   , SET SERVER
         4   , serverName   ,           , //Name associated with the UDP server, it will start inmediately to listen for udp datagrams that will receive via the message "<clientName> udp" with the parameters <datagram> <IPsender> <PORTsender>
         4   , Port         , 18110     , //port to listen to

         5   , CLOSE
         5   , agentName     ,           , //either a udp server or client name to be closed (cease to listening to messages)


   <options>
      synIndx, optionName  , parameters, defVal, desc
         1   , BODY       , datagram  ,       , //part of the datagram to send
         3   , BODY       , datagram  ,       , //part of the datagram to send

   <examples>
      gastSample

      udpClientServer

   <udpClientServer>
      //#javaj#
      //
      //    <frames> oConso
      //
      //#listix#
      //
      //    <main>
      //       UDP, SET SERVER, cucho, 1866
      //       UDP, SET CLIENT, sendo, localhost, 1866
      //       UDP, SET CLIENT, vicens, localhost, 1866
      //       UDP, SEND ONCE, localhost, 1866, //first datagram from anonymus!
      //       UDP, AGENT SEND, sendo, //first datagram from sendo!
      //       UDP, AGENT SEND, vicens, //my vicens datagram!
      //
      //    <-- cucho udp>
      //       //Cucho: Rx "@<p1>" from @<p2>:@<p3>
      //       //
      //       UDP, AGENT SEND, cucho, //GOT IT port @<p3>!
      //
      //    <-- sendo udp>
      //       //Sendo: Rx "@<p1>" from @<p2>:@<p3>
      //       //
      //       UDP, CLOSE, sendo
      //
      //    <-- vicens udp>
      //       //Vicens: Rx "@<p1>" from @<p2>:@<p3>
      //       //
      //       UDP, CLOSE, vicens
      //

#**FIN_EVA#
*/

package listix.cmds;

import java.util.*;
import java.net.*;
import java.io.*;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import org.gastona.net.udp.*;


public class cmdUDP implements commandable
{
   protected static Map udpAgents = new TreeMap ();

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] 
      {
          "UDP",
          "SOCKETUDP",
          "DATAGRAM"
      };
   }

   // global socket for SEND ONCE, just use one socket for all calls to "send once"
   //
   private DatagramSocket anonimusUDPsocket = null; 
   
   
   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      String oper = cmd.getArg(0);

      boolean optSendOnce   = cmd.meantConstantString (oper, new String [] { "SENDONCE" });
      boolean optSetClient  = cmd.meantConstantString (oper, new String [] { "SETCLIENT", "CLIENT", "CREATECLIENT" });
      boolean optSetServer  = cmd.meantConstantString (oper, new String [] { "SETSERVER", "SERVER", "CREATESERVER" });
      boolean optAgentSend  = cmd.meantConstantString (oper, new String [] { "AGENTSEND", "SENDAGENT", "AGENTTX", "SEND" });
      boolean optClose      = cmd.meantConstantString (oper, new String [] { "CLOSE", "FIN", "BASTA", "STOP" });
      
      if (optSendOnce)
      {
         // UDP, SEND, IP, Port, datagram
         //
         String ipAddress = cmd.getArg (1);
         int portNr = stdlib.atoi (cmd.getArg (2));

         if (ipAddress.length () == 0)
            ipAddress = "localhost";
         if (portNr == 0)
            portNr = udpSocketAgent.GASTONA_STANDARD_UDP_PORT;

         InetAddress targetIPadd = null;
         DatagramSocket udpSocket = null;

         try
         {
            targetIPadd = InetAddress.getByName (ipAddress);
            if (anonimusUDPsocket == null)
               anonimusUDPsocket = new DatagramSocket ();
         }
         catch (Exception e)
         {
            cmd.getLog().err ("socketUDP", "exception opening a socket for " + ipAddress + " [" + e + "]");
            return 1;
         }

         String message = cmd.getArg (3);
         do
         {
            if (message.length () > 0)
            {
               byte[] buffer = message.getBytes ();

               try
               {
                  cmd.getLog().dbg (4, "socketUDP", "send datagram [" + message + "] to " + ipAddress + ":" + portNr);
                  anonimusUDPsocket.send (new DatagramPacket (buffer, buffer.length, targetIPadd, portNr));
               }
               catch (Exception e)
               {
                  cmd.getLog().err ("socketUDP", "exception sending datagram [" + message + "] to " + ipAddress + ":" + portNr + " [" + e + "]");
                  return 1;
               }
            }
            message = cmd.takeOptionString(new String [] {"MESSAGE", "DATAGRAM", "BODY", "" }, null);
         }
         while (message != null);

         // let the socket open ...
         // anonimusUDPsocket.close ();
         // anonimusUDPsocket = null;
      }
      else if (optSetClient)
      {
         // UDP, SET CLIENT, name, IP, Port
         //
         String cliName = cmd.getArg (1);
         String ipAddress = cmd.getArg (2);
         int portNr = stdlib.atoi (cmd.getArg (3));

         if (ipAddress.length () == 0)  ipAddress = "localhost";
         if (portNr == 0)               portNr = udpSocketAgent.GASTONA_STANDARD_UDP_PORT;

         // NOTE : a thread cannot be restarted, simply create a new one
         closeReceiver (cmd, cliName);

         udpSocketAgent so = new udpSocketAgent (cliName);
         so.configureClient (ipAddress, portNr);
         so.doRun ();

         udpAgents.put (cliName, so);
         cmd.getLog().dbg (4, "socketUDP", "start client " + cliName + " ip/port " + ipAddress + " / " + portNr);
      }
      else if (optAgentSend)
      {
         // UDP, AGENT SEND, agentName, datagram, [ IP, Port ]
         //
         String agentName = cmd.getArg (1);
         String datagram = cmd.getArg (2);
         String ipAddress = cmd.getArg (3);
         int portNr = stdlib.atoi (cmd.getArg (4));

         udpSocketAgent ol = (udpSocketAgent) udpAgents.get (agentName);
         if (ol == null)
         {
            cmd.getLog().err ("UDP", "cannot find udp agent " + agentName + ", it is not active!");
            return 1;
         }
         ol.sendDatagram (ipAddress, portNr, datagram);
         cmd.getLog().dbg (4, "socketUDP", "agent " + agentName + " sent datagram of length " + datagram.length () );
      }
      else if (optSetServer)
      {
         // UDP, SET SERVER, name, Port
         //
         String serverName = cmd.getArg (1);
         int portNr = stdlib.atoi (cmd.getArg (2));

         // NOTE : a thread cannot be restarted, simply create a new one
         closeReceiver (cmd, serverName);

         udpSocketAgent so = new udpSocketAgent (serverName);
         so.configureServer (portNr);
         so.doRun ();

         udpAgents.put (serverName, so);
         cmd.getLog().dbg (4, "socketUDP", "start udp server " + serverName + " for port " + portNr);
      }
      else if (optClose)
      {
         // UDP, CLOSE, agentName
         //
         String agentName = cmd.getArg (1);
         closeReceiver (cmd, agentName);
      }
      else
      {
         cmd.getLog().err ("socketUDP", "operation not recognized [" + oper + "]!");
         return 1;
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

   private void closeReceiver (listixCmdStruct cmd, String name)
   {
      udpSocketAgent ol = (udpSocketAgent) udpAgents.get (name);
      if (ol != null)
      {
         cmd.getLog().dbg (4, "socketUDP", "stoping udp agent " + name);
         ol.doSleep ();
         udpAgents.remove (name);
      }
   }
}
