/*
packages de.elxala
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

package de.elxala.zServices;

/**
   @date 10.04.2008 21:54
   @name logger
   @author Alejandro Xalabarder

   @brief
   Provides logging service

   A module or any java class has to use an instance of logger (de.elxala.zServices.logger) to produce log.

   for example
         logger log = new logger (this, "moduleName")

*/
public class logger
{
   protected logClient cli = new logClient();

   public logger (Object clientObj, String client, String [] fields4ExtraInfo, String [][] ownConnections)
   {
      init (clientObj, client, fields4ExtraInfo, ownConnections);
   }

   public logger (Object clientObj, String client, String [] fields4ExtraInfo)
   {
      init (clientObj, client, fields4ExtraInfo, null);
   }

   public logger (String client, String [] fields4ExtraInfo)
   {
      init (null, client, fields4ExtraInfo, null);
   }

   public logger (String client)
   {
      init (null, client, null, null);
   }

   private void init (Object clientObj, String client, String [] fields4ExtraInfo, String [][] ownConnections)
   {
      cli.clientObj = clientObj;
      cli.clientStr = client;
      cli.arrExtraFields = fields4ExtraInfo;
      cli.ownConnections = ownConnections;

      logServer.registerClient (cli);
   }

   private String [] NOEXTRA = null;

   /**
      facility to avoid unnecesary loops or preparation of extra data for log if this is not active
   */
   public boolean isMsgLevelActive (int msgLevel)
   {
      return logServer.isLogging (cli, msgLevel);
   }

   public boolean isDebugging (int dbgLevel)
   {
      return logServer.isLogging (cli, logServer.LOG_DEBUG_0 + dbgLevel);
   }

   public String getLogDirectory ()
   {
      return logServer.getLogDirectory ();
   }

   //(o) TODO_elxala_logServer rename this method to storeClienzMessage or something like this
   public void msg (int msgAbsolutLevel, String context, String message, String [] extraInfo)
   {
      logServer.storeMessage (cli, msgAbsolutLevel, context, message, extraInfo, null);
   }

   public void msg (String context, String message)
   {
      logServer.storeMessage (cli, logServer.LOG_MESSAGE, context, message, null, null);
   }

   public void fatal (String context, String message, StackTraceElement [] stackElems)
   {
      logServer.storeMessage (cli, logServer.LOG_FATAL, context, message, null, stackElems);
   }

   public void fatal (String context, String message)
   {
      logServer.storeMessage (cli, logServer.LOG_FATAL, context, message, null, null);
   }

   public void severe (String context, String message)
   {
      logServer.storeMessage (cli, logServer.LOG_SEVERE_ERROR, context, message, null, null);
   }

   public void err (String context, String message)
   {
      logServer.storeMessage (cli, logServer.LOG_ERROR, context, message, null, null);
   }

   public void warn (String context, String message)
   {
      logServer.storeMessage (cli, logServer.LOG_WARNING, context, message, null, null);
   }

   public void dbg (int level, String context, String message, String [] extraInfo)
   {
      //(o) TOSEE_elxala_logServer (?) Allow free level for custom debug messages ...
      // Allow free level for custom messages ...

      //if (level < 0) level = 0;
      //if (level > 9) level = 9;
      logServer.storeMessage (cli, logServer.LOG_DEBUG_0 + level, context, message, extraInfo, null);
   }

   public void dbg (int level, String context, String message)
   {
      if (level < 0) level = 0;
      if (level > 9) level = 9;
      logServer.storeMessage (cli, logServer.LOG_DEBUG_0 + level, context, message, NOEXTRA, null);
   }

   public void dbg (int level, String message)
   {
//      level = Math.max (0, level);
//      level = Math.min (9, level);
      if (level < 0) level = 0;
      if (level > 9) level = 9;
      logServer.storeMessage (cli, logServer.LOG_DEBUG_0 + level, "", message, NOEXTRA, null);
   }

   public void msg (String message)
   {
      logServer.storeMessage (cli, logServer.LOG_FATAL, "", message, null, null);
   }

   public void fatal (String message)
   {
      logServer.storeMessage (cli, logServer.LOG_FATAL, "", message, null, null);
   }

   public void severe (String message)
   {
      logServer.storeMessage (cli, logServer.LOG_SEVERE_ERROR, "", message, null, null);
   }

   public void err (String message)
   {
      logServer.storeMessage (cli, logServer.LOG_ERROR, "", message, null, null);
   }

   public void warn (String message)
   {
      logServer.storeMessage (cli, logServer.LOG_WARNING, "", message, null, null);
   }
}
