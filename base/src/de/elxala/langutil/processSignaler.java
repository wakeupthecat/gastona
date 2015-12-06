/*
library de.elxala
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

package de.elxala.langutil;

import de.elxala.mensaka.*;
import de.elxala.zServices.*;


/*
   31.12.2009 02:53

   SQL solver (old clientCaller)

*/
public class processSignaler
{
   private static MessageHandle TX_ERROR         = new MessageHandle (); // "_lib processExecuter.callError"
   private static MessageHandle TX_START_BATCH   = new MessageHandle (); // "_lib processExecuter.callStart"
   private static MessageHandle TX_END_BATCH     = new MessageHandle (); // "_lib processExecuter.callEnd"

   private static void onceInit()
   {
      if (TX_START_BATCH != null) return;

      processSignaler me = new processSignaler (); // object for registering the messages

      TX_START_BATCH   = new MessageHandle ();
      TX_ERROR         = new MessageHandle ();
      TX_END_BATCH     = new MessageHandle ();
      Mensaka.declare (me, TX_ERROR       , "_lib processExecuter.callError"  , logServer.LOG_DEBUG_0);
      Mensaka.declare (me, TX_START_BATCH , "_lib processExecuter.callStart"  , logServer.LOG_DEBUG_0);
      Mensaka.declare (me, TX_END_BATCH   , "_lib processExecuter.callEnd"    , logServer.LOG_DEBUG_0);
   }


   public static void signalStart ()
   {
      onceInit();
      Mensaka.sendPacket (TX_START_BATCH, null);
   }

   public static void signalError ()
   {
      onceInit();
      Mensaka.sendPacket (TX_ERROR, null);
   }

   public static void signalEnd ()
   {
      onceInit();
      Mensaka.sendPacket (TX_END_BATCH, null);
   }
}
