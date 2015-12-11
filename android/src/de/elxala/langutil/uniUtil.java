/*
package de.elxala.langutil
(c) Copyright 2013 Wakeupthecat UG, Alejandro Xalabarder Aulet

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

package de.elxala.langutil;

import java.net.*;
import java.util.*;


/**
   Specific utilities that each system (java, android) has to implement natively
*/
public class uniUtil
{
   public static String HOST_NAME = null;
   public static String HOST_IPADDRESS = null;
   
   // avoids that printing on the console causes a recursive paint chain
   // for instance when the caller is called during a paint call.
   //
   public static void printLater (String str)
   {
      // not yet implemented for android,  we have to find alternative to invokeLater, see uniUtil.printLater in pc/de/elxala/uniUtil.java
      System.out.println (str);
   }

   public static String getThisHostName ()
   {
      if (HOST_NAME == null) obtainHostInfo ();
      return HOST_NAME;
   }
   
   public static String getThisIpAddress ()
   {
      if (HOST_IPADDRESS == null) obtainHostInfo ();
      return HOST_IPADDRESS;
   }
   
   protected static void obtainHostInfo ()
   {
      HOST_IPADDRESS = "127.0.0.1";
      HOST_NAME = "localhost";

      try
      {
         for (Enumeration<NetworkInterface> netArr = NetworkInterface.getNetworkInterfaces(); netArr.hasMoreElements();)
         {
            NetworkInterface netInte = netArr.nextElement ();
            for (Enumeration<InetAddress> addArr = netInte.getInetAddresses (); addArr.hasMoreElements ();)
            {
               InetAddress laAdd = addArr.nextElement ();
               String ipstring = laAdd.getHostAddress ();
               String hostName = laAdd.getHostName ();

               if (laAdd.isLoopbackAddress()) continue;
               if (hostName.equalsIgnoreCase ("localhost")) continue;
               if (ipstring.indexOf (':') >= 0) continue;

               HOST_IPADDRESS = ipstring;
               HOST_NAME = hostName;
               break;
            }
         }
      } catch (Exception ex) {}
   }
}


// output of for my HTC android
//       addstr += "\n\n" + laAdd.getCanonicalHostName() + "\n" + laAdd.getHostName () + "\n" + laAdd.getHostAddress ();
//       try
//       {
//           addstr += " local " + laAdd.getLocalHost ().getHostAddress ();
//       }
//
// 
// fe80::3401:9bff:feb0:cfc7%usb0
// fe80::3401:9bff:feb0:cfc7%usb0 
// fe80::3401:9bff:feb0:cfc7%usb0 local 127.0.0.1
// 
// localhost / localhost 192.168.42.129 local 127.0.0.1
// 
// fe80::9a0d:2eff:fe38:b342%wlan0
// fe80::9a0d:2eff:fe38:b342%wlan0 
// fe80::9a0d:2eff:fe38:b342%wlan0 local 127.0.0.1
// 
// android-b7b6a364bc7f0f56.fritz.box
// android-b7b6a364bc7f0f56.fritz.box 192.168.178.20 local 127.0.0.1
// 
// localhost
// localhost 10.249.68.59 local 127.0.0.1
