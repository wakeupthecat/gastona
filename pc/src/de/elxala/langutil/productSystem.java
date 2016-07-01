/*
package de.elxala.langutil
(c) Copyright 2012 Alejandro Xalabarder Aulet

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
*/
public class productSystem
{
   public static boolean isAndroid ()
   {
      return false;
   }
   
   protected static void launchOpenFile (String fileName)
   {
      String command = utilSys.isOSWindows () ? "cmd /C start \"\"": "xdg-open";
      String DQUOT   = utilSys.isOSLinux () ? "": "\"";
      String launchCommand = command + " " + DQUOT + fileName + DQUOT;
      utilSys.log.dbg (4, "launchOpenFile", "launch command [" + launchCommand + "]");
      javaRun.executePreShell (null, launchCommand , false);
   }

   public static void launchBrowser (String laurl)
   {
      // for pc BROWSER === OPEN FILE
      launchOpenFile (laurl);
   }
}
