/*
package de.elxala.langutil
(c) Copyright 2009 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_javaj_variables LookAndFeel

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_ variables
   <name>       LookAndFeel
   <groupInfo>  look
   <javaClass>  de.elxala.langutil.graph.sysLookAndFeel
   <importance> 3
   <desc>       //Set the java look & feel for the application

   <help>
      //
      //  This variable permits setting the java look & feel
      //
      //  Syntax:
      //
      //      <LookAndFeel>
      //         shortName, look & feel java class, decorated
      //
      //  sortName is not used at present, decorated might be 0 or 1
      //
      //  Example:
      //
      //      <LookAndFeel>
      //         metal, javax.swing.plaf.metal.MetalLookAndFeel, 1
      //
      //  Two more typical look and feels are
      //
      //       motif, com.sun.java.swing.plaf.motif.MotifLookAndFeel
      //       win  , com.sun.java.swing.plaf.windows.WindowsLookAndFeel
      //
      //  If the look and feel is provided in an external jar file, then
      //  remember to include it using the javaj variable <extern_libraries>.
      //

   <examples>
      gastSample

      example look & feel

   <example look & feel>

      //#javaj#
      //
      //   <frames>
      //      F, "Look and feel example"
      //
      //   <layout of F>
      //
      //     EVA, 10, 10, 5, 5
      //
      //        ,		  ,  200
      //        , bButton, eTextField,
      //
      //    <LookAndFeel>
      //       metal, javax.swing.plaf.metal.MetalLookAndFeel, 1
      //     <!  motif, com.sun.java.swing.plaf.motif.MotifLookAndFeel
      //     <!  win  , com.sun.java.swing.plaf.windows.WindowsLookAndFeel
      //
      //#data#
      //
      //   <eTextField> //this is the text of an edit field
#**FIN_EVA#

*/

package de.elxala.langutil.graph;

import java.awt.*;
import javax.swing.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

public class sysLookAndFeel
{
   // another log client of ...sysDefaults
   private static logger log ()
   {
      return sysDefaults.log;
   }

   public static final int LOOK_METAL   = 0;
   public static final int LOOK_WINDOWS = 1;
   public static final int LOOK_MOTIF   = 2;

   /**
   */
   public static boolean setLookAndFeel (int nLook)
   {
      switch (nLook)
      {
         case LOOK_WINDOWS:
               return setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         case LOOK_METAL:
               return setLookAndFeel ("javax.swing.plaf.metal.MetalLookAndFeel");
         case LOOK_MOTIF:
               return setLookAndFeel ("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
      }
      return false;
   }


   public static boolean setLookAndFeel (String StrLookAndFeel)
   {
      boolean ok = false;

      try
      {
         log().dbg (2, "setLookAndFeel", "setting look & feel [" + StrLookAndFeel + "]");
         UIManager.setLookAndFeel (StrLookAndFeel);
         ok = true;

         //System.out.println ("puesto el look : " + lnfName);
         //SwingUtilities.updateComponentTreeUI(frame);
      }
      catch (UnsupportedLookAndFeelException ex1)
      {
         log().err ("setLookAndFeel", "Unsupported LookAndFeel: " + StrLookAndFeel + " " + ex1);
      }
      catch (ClassNotFoundException ex2)
      {
         log().err ("setLookAndFeel", "LookAndFeel class not found: " + StrLookAndFeel + " " + ex2);
      }
      catch (InstantiationException ex3)
      {
         log().err ("setLookAndFeel", "Could not load LookAndFeel: " + StrLookAndFeel + " " + ex3);
      }
      catch (IllegalAccessException ex4)
      {
         log().err ("setLookAndFeel", "Cannot use LookAndFeel: " + StrLookAndFeel + " " + ex4);
      }
      catch (Exception ex5)
      {
         log().err ("setLookAndFeel", "Exception by LookAndFeel: " + StrLookAndFeel + " " + ex5);
      }

      return ok;
   }

}