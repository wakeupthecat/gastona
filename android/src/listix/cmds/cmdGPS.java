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
   //(o) WelcomeGastona_source_listix_command PARSONS

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       GPS
   <groupInfo>  system_sensor
   <javaClass>  listix.cmds.cmdGPS
   <importance> 4
   <desc>       //Povide access to GPS data

   <help>
      //
      //
      //    GPS, GET LONGITUDE
      //


   <aliases>
      alias
      GPS

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Get the current position

   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , GET POSITION ,             , //
         1   , messagePos   ,             , //Message through which the position will be sent
         1   , N times      , 1           , //Number of positions to send
         1   , intervalSec  , 1           , //Interval in seconds between messages
         1   , meters       , 0           , //Meters

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample

      #**FIN EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;

/**
*/
public class cmdGPS implements commandable
{
   //private final SensorManager mSensorManager;

   // NOTE: If we made an extra class implementing LocationListener and having
   //       this variable nTimesLeft, then it would be possible to call the command GPS
   //       more than one time to work on paralell
   private int nTimesLeft = 0;
   private String msgName = "";

   public cmdGPS()
   {
      //mlocManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 0, 0, this);
      //boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      //System.out.println("cmdGPS " + (gpsEnabled ? "enabled": "NOT ENABLED!!"));
   }


   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "GPS",
      };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper    = cmd.getArg(0);
      String msgName = cmd.getArg(1);
      int nTimesLeft  = stdlib.atoi (cmd.getArg(2));
      int intervalSec = stdlib.atoi (cmd.getArg(3));
      int meters      = stdlib.atoi (cmd.getArg(4));

      //... String  customFile = cmd.takeOptionString(new String [] { "FROMFILE", "FILE" }, "" );

      //... startLine = stdlib.atoi (cmd.takeOptionString(new String [] {"STARTLINE", "START", "BEGIN", "BEGINLINE" }, "1"));

      cmd.getLog().dbg (2, "GPS", "operation [" + oper + "]");
      System.out.println("TESCUCHO...");

      if (oper.equalsIgnoreCase ("GET POSITION"))
      {
         System.out.println("OK! programo para " + intervalSec + "segundos y tiempo " + nTimesLeft);

         LocationManager locMan = (LocationManager)androidSysUtil.getMainAppContext().getSystemService(Context.LOCATION_SERVICE);

         GPSListener lis = new GPSListener (msgName, nTimesLeft, locMan);

         locMan.requestLocationUpdates (LocationManager.GPS_PROVIDER, intervalSec * 1000, meters, lis);

/*
         Location loca = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         if (loca != null)
            lis.onLocationChanged (loca);
         else
         {
            cmd.getLog().dbg (2, "GET POSITION cannot obtain last location");
            lis.sendPosition (0., 0.);
         }
*/
      }
      else
      {
         cmd.getLog().err ("GPS", "Operation [" + oper + "] unknown");
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }

}
