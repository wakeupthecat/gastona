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

import android.net.*;
import android.content.Intent;
import android.app.Activity;

/**
*/
public class productSystem
{
   public static boolean isAndroid ()
   {
      return true;
   }

   public static void launchOpenFile (String fileName)
   {
      utilSys.log.dbg (2, "launchOpenFile", "try intent view [file://" + fileName + "]");

      // NOTE: in android generic open file is implemented as INTENT, VIEW
      //       but there is no mime by parameter, if want so use the command
      //       INTENT, VIEW instead
      try
      {
         Intent intent = new Intent (Intent.ACTION_VIEW);
         Uri lauri = Uri.parse("file://" + fileName);
         intent.setData (lauri);
         androidSysUtil.getMainActivity ().startActivity (intent);
      }
      catch (Exception e)
      {
         utilSys.log.err ("launchOpenFile", "exception launching intent view [" + e + "]");
      }
   }

   public static void launchBrowser (String laurl)
   {
      try
      {
         Intent intent = new Intent (Intent.ACTION_VIEW);
         Uri lauri = Uri.parse (laurl);
         intent.setDataAndType (lauri, "text/html");
         androidSysUtil.getMainActivity ().startActivity (intent);
      }
      catch (Exception e)
      {
         utilSys.log.err ("launchBrowser", "exception launching browser intent [" + e + "]");
      }
   }
}
