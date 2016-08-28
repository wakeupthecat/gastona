/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

/**   ======== de.elxala.langutil.utilSys ==========================================
   @author Alejandro Xalabarder 04.07.2009 13:04

*/

import java.io.*;
import de.elxala.langutil.filedir.*;
import java.util.*;
import de.elxala.zServices.*;

/**
*/
public class utilSys
{
   protected static logger log = new logger (null, "de.elxala.langutil.utilSys", null);

   public static final String dirSeparator = File.separator;
   public static final boolean isSysUnix = dirSeparator.equals ("/");

   public static boolean isOSLinux ()    { return java.io.File.separatorChar == '/'; }
   public static boolean isOSWindows ()  { return java.io.File.separatorChar == '\\'; }

   public static boolean isAndroid ()    { return productSystem.isAndroid (); }
   public static boolean isNotAndroid () { return !isAndroid (); }

   private static Map objectMap = new TreeMap ();

   private static String sysOSname = null;
   private static String sysOSarch = null;
   private static String sysOSversion = null;
   private static String sysOSstring = null;
   private static boolean isWindows = true;
   private static boolean isLinux = false;
   private static boolean isARM = false;
   private static boolean isMacOX = false;
   private static boolean is64bit = false;

   // return winOS, linuxOS, linuxOS64 or linuxArm
   //        we are not interested in winOS64 because sqlite 32bits can be executed in this machines
   //        but not in linux
   public static String getSysString ()
   {
      if (sysOSname == null) getSysInfo ();
      return sysOSstring;
   }

   public static boolean isOSNameWindows () { getSysInfo (); return isWindows; }
   public static boolean isOSNameLinux   () { getSysInfo (); return isLinux; }
   public static boolean isOSArchArm     () { getSysInfo (); return isARM; }
   public static boolean isOSNameMacOX   () { getSysInfo (); return isMacOX; }

   public static void launchOpenFile (String fileName)
   {
      productSystem.launchOpenFile (fileName);
   }

   public static void launchBrowser (String url)
   {
      // NOTE: for pc launchOpenFile === launchBrowser but not for android !!
      //
      productSystem.launchBrowser (url);
   }

   private static void getSysInfo ()
   {
      if (sysOSname != null) return;
      sysOSname = System.getProperty ("os.name", "").toLowerCase ();
      sysOSarch = System.getProperty ("os.arch", "").toLowerCase ();
      sysOSversion = System.getProperty ("os.version", "").toLowerCase ();

      isWindows = sysOSname.indexOf("windows") >= 0;
      isLinux   = sysOSname.indexOf("linux") >= 0;
      isARM     = sysOSarch.startsWith("arm");
      isMacOX   = sysOSname.indexOf("mac ox") >= 0;

      //import com.sun.servicetag.SystemEnvironment;
      //...
      //SystemEnvironment env = SystemEnvironment.getSystemEnvironment();
      //final String envArch = env.getOsArchitecture();
      // String envArch = SystemEnvironment.getSystemEnvironment().getOsArchitecture();

      //remember: property "os.arch" returns bitness of JRE not of the machine!! but it is a good fallback
      //
      // is64bit = envArch != null ? envArch.indexOf ("64") >= 0: sysOSarch.endsWith("64");

      // we rely on the JRE bitness! in general it should work
      is64bit = sysOSarch.endsWith("64");

      sysOSstring = isSysUnix ? "linuxOS": "winOS";

      if (isWindows) sysOSstring = "winOS";
      if (isLinux || isMacOX) sysOSstring = "linuxOS" + (is64bit ? "64": "");
      if (isLinux && isARM) sysOSstring = "linuxArm";

      // ! file separator taken from File.separator instead of property "file.separator"
      //   (no special reason)
   }

   public static void objectSacPut (String idString, Object obj)
   {
      objectMap.put (idString, obj);
   }

   public static Object objectSacGet (String idString)
   {
      return objectMap.get (idString);
   }

   public static void destroySac ()
   {
      objectMap = new TreeMap ();
   }

   public static int getSacSize ()
   {
      return objectMap == null ? 0: ((TreeMap) objectMap).size ();
   }
}
