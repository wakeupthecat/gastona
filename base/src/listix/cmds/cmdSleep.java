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
   //(o) WelcomeGastona_source_listix_command SLEEP

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       SLEEP
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdSleep
   <importance> 3
   <desc>       //Makes the application to wait or sleep for a period of time

   <help>
      //
      // It might be used for example to show dias or whatever
      //

   <aliases>
      alias
      WAIT

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Sleep the main thread for delayMilli milliseconds

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , delayMilli   ,       , //Delay in milliseconds

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      Sleep button

   <Sleep button>
      //#javaj#
      //
      //   <frames> Fmain, Sleep button sample
      //   <layout of Fmain>
      //       EVA, 6, 6, 3, 3
      //       ---,  , X
      //        22, lMilliseconds, eMilli, bPressMe
      //
      //#data#
      //
      //    <eMilli> 1000
      //
      //#listix#
      //
      //    <-- bPressMe> SLEEP, @<eMilli>

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

public class cmdSleep implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "SLEEP",
          "WAIT"
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      int millis = stdlib.atoi (that.solveStrAsString (commandEva.getValue (indxComm, 1)));
      try
      {
         Thread.currentThread().sleep (millis);
      }
      catch (Exception e) {}

      return 1;
   }
}


/*
   //(o) TOSEE_listix_cmds SLEEP Variante tick

   23.06.2009 20:22
   Another idea is to use another thread to sleep and send a synchronized message trhough Mensaka

      "TICK", "START", "message", "delayMilli", "[ times (1) ]"
      "TICK", "STOP", "message"
*/