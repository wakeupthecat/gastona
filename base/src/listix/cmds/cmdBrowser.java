/*
Copyright (C) 2015 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command BROWSER

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       BROWSER
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdBrowser
   <importance> 3
   <desc>       //Opens the given url or html file with the browser

   <help>
      //
      //  Opens the browser
      //

   <aliases>
      alias
      LAUNCHBROWSER
      BROWSER
      NAVIGATOR
      NAV
      WEB

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Opens the given url or html file with the browser

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , fileName ,    , //filename to open

   <options>
      synIndx, optionName  , parameters     , defVal    , desc

   <examples>
      gastSample

      browse local
      browse calcRoute

   <browse local>
      //#javaj#
      //
      //   <frames> oSal
      //
      //#listix#
      //
      //   <helloContent>
      //       //<html>Hello!</html>
      //
      //   <main>
      //      VAR=, tmphtml, @<:lsx tmp html>
      //      GEN, @<tmphtml>, helloContent
      //      //generated temporary html file @<tmphtml>
      //      //
      //      BROWSER, @<tmphtml>
      //      //browser launched.
      //
      //

   <browse calcRoute>
      //#javaj#
      //
      //   <frames>
      //      main, To google maps, 300
      //
      //   <layout of main>
      //      EVA, 4, 4, 2, 2
      //
      //         ,             , X
      //         , lOrigin     , eOrigin       , bOriginToMap
      //         , lDestination, eDestination  , bDestToMap
      //         , bRoute
      //
      //#data#
      //
      //   <eOrigin>         //48.777641,9.164349
      //   <eDestination>    //48.742323,9.309448
      //
      //   <bOriginToMap> to Map
      //   <bDestToMap> to Map
      //
      //#listix#
      //
      //   <-- bOriginToMap> LSX, MAPS1, @<eOrigin>
      //   <-- bDestToMap>  LSX, MAPS1, @<eDestination>
      //   <-- bRoute>
      //      LSX, MAPS2, @<eOrigin>, @<eDestination>
      //
      //   <MAPS1>
      //      BROWSER, http://maps.google.de/maps?q=@<p1>&t=m&z=19
      //
      //   <MAPS2>
      //      BROWSER, http://maps.google.de/maps?saddr=@<p1>&daddr=@<p2>&t=m

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

/**
*/
public class cmdBrowser implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "LAUNCHBROWSER",
          "BROWSER",
          "NAVIGATOR",
          "NAV",
          "WEB",
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
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      cmd.getLog().dbg (4, "cmdLanchBrowser", "launch browser [" + cmd.getArg(0) + "]");
      utilSys.launchBrowser (cmd.getArg(0));

      cmd.checkRemainingOptions ();
      return 1;
   }
}
