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
   //(o) WelcomeGastona_source_listix_command TIMER

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       TIMER
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdTimer
   <importance> 3
   <desc>       //Programmable timer

   <help>
      //
      //Timer to do periodically a task (NOTE: currently only one timer at a time supported!)
      //

   <aliases>
      alias
      TICK
      CLOCK
      PULSE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Starts a named timer, as a result the format 'name' will be called on each tick
         2   ,    3      , //Stops a named timer

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , START        ,       ,
         1   , name         ,       , //Name of the timer and of the listix format to execute
         1   , periodMilli  ,       , //Period in milliseconds between ticks
         1   , repetitions  ,       , //Maximum number of repetitions
         2   , STOP         ,       ,
         2   , name         ,       , //Name of the timer and of the listix format to execute

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      Timer print

   <Timer print>
      //#javaj#
      //
      //   <frames> Fmain, Timer sample
      //   <layout of Fmain>
      //       EVA, 6, 6, 3, 3
      //       ---,  , X
      //        22, lMilliseconds, eMilli, bStart, bStop
      //         X, oConsola     , -, -, -
      //
      //#data#
      //
      //    <eMilli> 500
      //
      //#listix#
      //
      //    <-- bStart>
      //          TIMER, START, MiTarea, @<eMilli>, 100
      //
      //    <-- bStop>
      //          TIMER, STOP, MiTarea
      //
      //    <MiTarea>
      //          //tick @<:lsx date2>
      //          //

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import java.util.*;

public class cmdTimer implements commandable
{
   static class miTimer
   {
      public listix ptrListix = null;
      public String name = "noname";
      public int periodMilli = 1000;
      public int maxRepeat = 5;
      public int count = 0;

      public void start ()
      {
         count = 0;
         TimerTask tasca = new TimerTask ()
         {
            public void run ()
            {
               if (count >= maxRepeat) 
               {
                  timo.cancel(); // end the thread
                  return;
               }
               count ++;
               if (ptrListix != null)
               {
                  ptrListix.printLsxFormat (name, new String [] { "" + (count-1), "" + (count-1)*periodMilli });
               }
            }
         };

         timo = new Timer ();
         timo.scheduleAtFixedRate (tasca, 0, periodMilli);
      }

      public void stop ()
      {
         if (timo != null)
            timo.cancel ();
      }
   }

   //(o) DOC/listix/adding a new command/2 the command class 
   //    A class implementing the commandable interface
   //    that is the methods "getNames" and "execute"
   //

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "TIMER",
          "TICK",
          "CLOCK",
          "PULSE"
       };
   }

   private static Timer timo = null;
   private static miTimer theTimer = new miTimer ();

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      String oper = cmd.getArg(0).toUpperCase ();
      String name = cmd.getArg(1);
      int period = stdlib.atoi (cmd.getArg(2));
      int repet  = stdlib.atoi (cmd.getArg(3));

      theTimer.stop ();

      if (oper.equals ("START"))
      {
         try
         {
            theTimer.ptrListix = that;
            theTimer.name = name;
            theTimer.periodMilli = period;
            theTimer.maxRepeat = repet;
            theTimer.start ();
         }
         catch (Exception e)
         {
            //log.warn ("TIMER", e.toString ());
         }
      }

      return 1;
   }
}
