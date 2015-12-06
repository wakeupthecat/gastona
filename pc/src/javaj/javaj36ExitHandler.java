/*
packages de.elxala
Copyright (C) 2011,2012 Alejandro Xalabarder Aulet

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

package javaj;

import java.awt.*;
import java.awt.event.*;   // WindowListener

import javax.swing.*;
import java.util.Vector;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.graph.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import de.elxala.mensaka.*;
import javaj.widgets.basics.*;
import javaj.widgets.panels.*;

public class javaj36ExitHandler implements MensakaTarget
{
   // special messages because finalizeJavaj must to be static
   private static MessageHandle TX_EXIT_JAVAJ_QUESTION = null;
   private static MessageHandle TX_EXIT_JAVAJ_NOW   = null;
   private static MessageHandle TX_EXIT_JAVAJ_NOW2  = null;
   private static MessageHandle TX_EXIT_JAVAJ_DONE  = null;
   private static MessageHandle TX_EXIT_JAVAJ_EXIT  = null;

   private static final int RX_DOEXIT = 66;
   private static final int RX_DOEXIT_YES = 67;
   private static boolean allowEXITQuestion = true;

   private static javaj36ExitHandler theInstance = null;

   private javaj36ExitHandler ()
   {
      Mensaka.subscribe (this, RX_DOEXIT    ,  javajEBS.msgEXIT_JAVAJ_DOWN);
      Mensaka.subscribe (this, RX_DOEXIT_YES,  javajEBS.msgEXIT_JAVAJ_DOWN_CONTINUE);

      if (TX_EXIT_JAVAJ_QUESTION == null)
      {
         TX_EXIT_JAVAJ_QUESTION = new MessageHandle ();
         TX_EXIT_JAVAJ_NOW      = new MessageHandle ();
         TX_EXIT_JAVAJ_NOW2     = new MessageHandle ();
         TX_EXIT_JAVAJ_DONE     = new MessageHandle ();
         TX_EXIT_JAVAJ_EXIT     = new MessageHandle ();
         Mensaka.declare (this, TX_EXIT_JAVAJ_QUESTION, javajEBS.msgEXIT_JAVAJ_QUESTION, logServer.LOG_DEBUG_0);
         Mensaka.declare (this, TX_EXIT_JAVAJ_NOW,      javajEBS.msgEXIT_JAVAJ         , logServer.LOG_DEBUG_0);
         Mensaka.declare (this, TX_EXIT_JAVAJ_NOW2,     javajEBS.msgEXIT_JAVAJ         , logServer.LOG_DEBUG_0);
         Mensaka.declare (this, TX_EXIT_JAVAJ_DONE,     javajEBS.msgEXIT_JAVAJ_DONE    , logServer.LOG_DEBUG_0);
         Mensaka.declare (this, TX_EXIT_JAVAJ_EXIT,     javajEBS.msgEXIT_JAVAJ_EXIT    , logServer.LOG_DEBUG_0);
      }
   }

   public static void ensurePresence ()
   {
      if (theInstance == null)
         theInstance = new javaj36ExitHandler ();
   }


   /**
      javaj listen to some events
   */
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case RX_DOEXIT:
            finalizeJavaj ();
            break;

         case RX_DOEXIT_YES:
            confirmedExitSequence ();
            break;

         default:
            return false;
      }

      return true;
   }

   public void disableEXITQuestion ()
   {
      allowEXITQuestion = false;
   }

   public static void finalizeJavaj ()
   {
      //System.gc ();  // is it really useful here?

      //...log.dbg (2, "finalizeJavaj", "exit started");
      // let's see if abort Exit
      //
      if (allowEXITQuestion)
      {
         //...log.dbg (2, "finalizeJavaj", "exit question allowed");
         int nlisten = Mensaka.sendPacket (TX_EXIT_JAVAJ_QUESTION, null);
         if (nlisten > 0)
         {
            // exit question catched
            // either aborted or it will continue with "javaj doExitYes"
            //...log.dbg (2, "finalizeJavaj", "exit question catched, now only the message [javaj doExitYes] would perform the exit sequence");
            return;
         }
      }

      //...log.dbg (2, "finalizeJavaj", "do continue exit sequence");
      confirmedExitSequence ();
   }

   public static void confirmedExitSequence ()
   {
      //System.gc ();  // is it really useful here?

      // to all controllers for preparation to exit (exit or exit_now it should not differ much)
      //
      Mensaka.sendPacket (TX_EXIT_JAVAJ_NOW, null);
      Mensaka.sendPacket (TX_EXIT_JAVAJ_NOW2, null);

      // all controllers have finalize, now no message should be send anymore
      // this message might be used by some logServer or similar mechanism
      //
      Mensaka.sendPacket (TX_EXIT_JAVAJ_DONE, null);

      //NOTE: from here no log will be stored! so do write log or debug messages

      // message to allow a kind of "launch after exit"
      //
      Mensaka.sendPacket (TX_EXIT_JAVAJ_EXIT, null);

      if (! globalJavaj.logCat.isShowing ())
         System.exit (0);
      else
      {
         globalJavaj.logCat.message ("javaj", "NOTE: now log Wakeup is deativated to allow exiting the application next time");
         globalJavaj.logCat.deactivate ();
      }
   }

}
