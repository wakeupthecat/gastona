/*
package de.elxala.langutil
(c) Copyright 2005,2009 Alejandro Xalabarder Aulet

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

package de.elxala.langutil.graph;

import java.awt.*;
import javax.swing.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

/**
   @file   sysDefaults.java
   @author Alejandro Xalabarder Aulet
   @date   24.11.2001 16:08
   @project de.elxala.langutil.graph

   Default fonts for widgets, look & feel

   Example:

      sysDefaults.setDefaultFonts ("Tahoma");
      sysDefaults.setLookAndFeel (sysDefaults.LOOK_MOTIF);


*/
public class sysDefaults
{
   protected static logger log = new logger (null, "de.elxala.langutil.graph.sysDefaults", null);

   public static final int LOOK_METAL   = sysLookAndFeel.LOOK_METAL;
   public static final int LOOK_WINDOWS = sysLookAndFeel.LOOK_WINDOWS;
   public static final int LOOK_MOTIF   = sysLookAndFeel.LOOK_MOTIF;

   /**
   */
   public static boolean setLookAndFeel (int nLook)
   {
      return sysLookAndFeel.setLookAndFeel (nLook);
   }

   public static boolean setLookAndFeel (String StrLookAndFeel)
   {
      return sysLookAndFeel.setLookAndFeel (StrLookAndFeel);
   }

   public static void setDefaultFonts ()
   {
      sysFonts.setDefaultFonts ();
   }

   public static void setDefaultFonts (String fontname, int size)
   {
      sysFonts.setDefaultFonts (fontname, size);
   }

   public static void setDefaultFonts (String fontname, int size, String [] componentNames)
   {
      sysFonts.setDefaultFonts (fontname, size, componentNames);
   }

   /**
      see de.elxala.langutil.graph.sysFonts.setDefaultFonts (Eva eva)
   */
   public static void setDefaultFonts (Eva eva)
   {
      sysFonts.setDefaultFonts (eva);
   }

   /**
      see de.elxala.langutil.graph.sysImages.setDefaultFonts (Eva eva)
   */
   public static void setDefaultImages (Eva defaultImages)
   {
      sysImages.setDefaultImages (defaultImages);
   }
}
   