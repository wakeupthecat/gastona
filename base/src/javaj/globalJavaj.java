/*
packages de.elxala
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

package javaj;

import java.awt.Component;
import java.awt.Color;
//import java.awt.event.*;

import javax.swing.UIManager;
import java.util.Vector;
import java.util.List;

import de.elxala.langutil.*;
import de.elxala.zServices.logger;

// import de.elxala.zServices.*;

public class globalJavaj
{
   private static logger log_widgetRegister = new logger (null, "javaj_widgetRegister", new String [] { "name", "class" });

   private static List listOfWidgetNames = null;
   private static boolean paintingLayout = false;

   private static boolean have_javajUI_text_done = false;
   public static void ensureDefRes_javajUI_text ()
   {
      if (have_javajUI_text_done) return;
      have_javajUI_text_done = true;

      //(o) javaj_global default general text colors (edit field + Text areas)
      //
      UIManager.put ("javajUI.text.dirtyBackColor", new Color (234, 234, 213));
      UIManager.put ("javajUI.text.normalBackColor", new Color (222, 222, 239));
      UIManager.put ("javajUI.text.disableBackColor", new Color (200, 200, 200));
      UIManager.put ("javajUI.text.normalForeColor", new Color (20, 20, 20));

      //(o) javaj_global default console colors
      //
//      UIManager.put ("javajUI.text.consoleOutBackColor", Color.BLACK);
//      UIManager.put ("javajUI.text.consoleOutForeColor", new Color(255, 156, 50));
      UIManager.put ("javajUI.text.consoleOutBackColor", new Color(  1,  41,  26));
      UIManager.put ("javajUI.text.consoleOutForeColor", new Color(241, 251, 210));

      UIManager.put ("javajUI.text.consoleErrBackColor", new Color(120,  10, 15)); //dark red
      UIManager.put ("javajUI.text.consoleErrForeColor", new Color(235, 229, 164)); // amarillo claro

//      UIManager.put ("javajUI.text.consoleBothBackColor", new Color(  0,  0, 85));   // blue
//      UIManager.put ("javajUI.text.consoleBothForeColor", Color.WHITE);
      UIManager.put ("javajUI.text.consoleBothBackColor", new Color( 53, 69, 91));   // deep blue
      //UIManager.put ("javajUI.text.consoleBothForeColor", new Color(241, 244, 248));   // casi blanco
      UIManager.put ("javajUI.text.consoleBothForeColor", new Color(255, 255, 255));
   }

   private static boolean have_javajUI_tree_done = false;
   public static void ensureDefRes_javajUI_tree ()
   {
      if (have_javajUI_tree_done) return;
      have_javajUI_tree_done = true;

      //(o) TOSEE_javaj_global change to javajUI.tree ? (use "javajUI.tree." instead of "Tree.")
      //          Tree.x are the java native resources (except "Tree.rootIcon" and "Tree.secondRootIcon")

      //(o) javaj_global default tree icons
      //
      UIManager.put ("Tree.leafIcon"      , javaLoad.getSomeHowImageIcon ("javaj/img/leaf.png"   ));
      UIManager.put ("Tree.closedIcon"    , javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
      UIManager.put ("Tree.openIcon"      , javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
      UIManager.put ("Tree.collapsedIcon" , javaLoad.getSomeHowImageIcon ("javaj/img/node1.png"  ));
      UIManager.put ("Tree.expandedIcon"  , javaLoad.getSomeHowImageIcon ("javaj/img/node2.png"  ));
      UIManager.put ("Tree.rootIcon"      , javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
      UIManager.put ("Tree.secondRootIcon", javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));

      // harcoded Tree UI resources
      //
      UIManager.put("Tree.scrollsHorizontallyAndVertically",  new Boolean(true));
      UIManager.put("Tree.scrollsOnExpand", null); // only setting to false does not work on linux and Mac
   }

   public static void setPaintingLayout (boolean on)
   {
      paintingLayout = on;
   }

   public static boolean getPaintingLayout ()
   {
      return paintingLayout;
   }

   public static List getListOfWidgetNames ()
   {
      if (listOfWidgetNames == null)
      {
         listOfWidgetNames = new Vector ();
         UIManager.put ("javaj.List<widgets>", listOfWidgetNames);
      }
      return listOfWidgetNames;
   }

   public static void registerWidgetByName (String name, Component widget)
   {
      UIManager.put ("javaj." + name, widget);
      getListOfWidgetNames ().add (name);
      if (log_widgetRegister.isDebugging (2))
      {
         log_widgetRegister.dbg (2, "register", "add", new String [] { name, widget.getClass().toString () });
      }
   }

   public static Component getWidgetByName (String name)
   {
      return (Component) UIManager.get ("javaj." + name);
   }
}