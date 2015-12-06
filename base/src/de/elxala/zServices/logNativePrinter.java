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
   TODO:

   [] deprecar, comprobar que no se usan
         public static final int LOG_START = 20;
         public static final int LOG_STEP  = 21;
         public static final int LOG_END   = 30;

         // start index of custom logs
         public static final int LOG_CUSTOM_FIRST = 100;

   [] Implementar custom logs en javaj y mensaka, por ejemplo...

      CREATE TABLE logCustom_javaj_flow     (msgCounter, milliStamp, level, context, message);
      CREATE TABLE logCustom_mensaka_agents (msgCounter, milliStamp, level, agIndex, agName, agObjString);
      CREATE TABLE logCustom_mensaka_msgs   (msgCounter, milliStamp, level, agIndex, type(suscribe/declare/send/received/sendEnd), msgindx);

*/

import java.io.File;
import java.util.List;
import java.util.Vector;
import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.mensaka.*;

/**
   @date 10.04.2008 21:54
   @name logger
   @author Alejandro Xalabarder

   @brief
   Provides logging service


   Notes:
         - a module or any java class has to use an instance of logger (de.elxala.zServices.logger) to produce log

*/
public class logNativePrinter
{
   public static void message (String context, String msg2print)
   {
      System.out.println (context + " / " + msg2print);
   }

   public static void warning (String context, String msg2print)
   {
      System.out.println (context + " / " + msg2print);
   }

   public static void error (String context, String msg2print)
   {
      System.err.println (context + " / " + msg2print);
   }

   public static void errorBox (String msg2print, String title)
   {
      javax.swing.JOptionPane.showMessageDialog (
            null,
            msg2print,
            title,
            javax.swing.JOptionPane.ERROR_MESSAGE);
   }
}
