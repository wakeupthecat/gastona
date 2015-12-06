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

/**
   Specific utilities that each system (java, android) has to implement natively
*/
public class uniUtil
{
   public static String HOST_NAME = null;
   public static String HOST_IPADDRESS = null;

   // avoids that printing on the console causes a recursive paint chain
   //
   // NOTE: it is strange that we have to declare the class static to be compiled!
   protected static class runPrintLater implements Runnable
   {
      private String str;
      public runPrintLater(String que)
      {
         str = que;
      }

      public void run() 
      {
         System.out.println (str);
      }
   }

   // avoids that printing on the console causes a recursive paint chain
   // for instance when the caller is called during a paint call.
   //
   public static void printLater (String str)
   {
       javax.swing.SwingUtilities.invokeLater(new runPrintLater(str));
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
      
      try {
         HOST_IPADDRESS = "" + java.net.InetAddress.getLocalHost().getHostAddress();
         HOST_NAME = "" + java.net.InetAddress.getLocalHost().getHostName();
      }
      catch (Exception e) {}
      
   }
}
