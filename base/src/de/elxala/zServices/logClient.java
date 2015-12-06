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

   logger

   client structure, an object of this type will be associated with each logger


*/
public class logClient
{
   public Object clientObj = null;
   public String clientStr = "";
   public int clientID = -1;     // has to be set by registering or not set if no log session
   public String [] arrExtraFields = null;
   public boolean extraTableCreated = false;
   public String [][] ownConnections = null;

   public int myMaxLevel = -1; //logServer.getDefaultMaxLogLevel ();

   public int levelValidStamp = -1; // to be used internally by logServer

   public boolean unknownLevel ()
   {
      return myMaxLevel == -1;
   }

   //(o) TODO_elxala_logServer (?) Decide if a finer array of level is needed
   //   public int [] arrLevelsOn = null;
}
