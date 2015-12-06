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
public class GPSListener implements LocationListener
{
   private LocationManager mlocManager = null;


   // NOTE: If we made an extra class implementing LocationListener and having
   //       this variable nTimesLeft, then it would be possible to call the command GPS
   //       more than one time to work on paralell
   private int nTimesLeft = 0;
   private String msgName = "";

   public GPSListener(String messageToSend, int maxMeasures, LocationManager locMan)
   {
      msgName = messageToSend;
      nTimesLeft = maxMeasures;
      mlocManager = locMan;

      System.out.println("ME ESTANCIAN LISTENOR CON " + nTimesLeft + " veces");
      //mlocManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 0, 0, this);
      //boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      //System.out.println("cmdGPS " + (gpsEnabled ? "enabled": "NOT ENABLED!!"));
   }


   //@Override
   public void onProviderEnabled(String provider)
   {
      //System.out.println("ProviderEnabled " + provName);
   }

   //@Override
   public void onProviderDisabled(String provName)
   {
      //System.out.println("ProviderDisabled " + provName);
   }

   //@Override
   public void onStatusChanged(String na, int un, Bundle bu)
   {
      //System.out.println("StatusChanged " + na + " int " + un);
   }

   //@Override
   public void onLocationChanged(Location loca)
   {
      //cmd.getLog().dbg (2, "onLocationChanged", "POSITION " + loca.getLongitude() + ",  " + loca.getLatitude());
      sendPosition (loca.getLongitude(), loca.getLatitude());
      nTimesLeft --;

      System.out.println("ME PEGAN TOQUE (quedan " + nTimesLeft + ") LA POSICIÓN! " + loca.getLongitude() +  ", " + loca.getLatitude());
      if (nTimesLeft <= 0)
      {
         System.out.println("ME REMUEVEN LAS TRIPAS!");
         mlocManager.removeUpdates(this);
      }
   }


   protected void sendPosition (double longitude, double latitude)
   {
      Mensaka.sendPacket (msgName, null, new String [] { "" + logServer.elapsedMillis (), "" + longitude, "" + latitude } );
   }
}
