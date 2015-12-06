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

import android.view.View;
import javaj.widgets.graphics.uniColor;

import javaj.widgets.zDialogDoing;

import java.util.Vector;
import java.util.List;

import de.elxala.langutil.*;
import de.elxala.zServices.*;

public class globalJavaj
{
   private static logger log_widgetRegister = new logger (null, "javaj_widgetRegister", new String [] { "name", "class" });

   private static List listOfWidgetNames = null;
   private static boolean paintingLayout = false;
   public static logWakeup logCat = new logWakeup ();
   
   public static zDialogDoing diagDoing = new zDialogDoing ();

   public static void destroyStatic ()
   {
      have_javajUI_text_done = false;
      have_javajUI_tree_done = false;
      paintingLayout = false;
      listOfWidgetNames = null;
      log_widgetRegister = null;
   }

   private static boolean have_javajUI_text_done = false;
   public static void ensureDefRes_javajUI_text ()
   {
      if (have_javajUI_text_done) return;
      have_javajUI_text_done = true;

      //(o) android_TODO System colors
      //(o) javaj_global default general text colors (edit field + Text areas)
      //
      utilSys.objectSacPut ("javajUI.text.dirtyBackColor", new uniColor(234, 234, 213));
      utilSys.objectSacPut ("javajUI.text.normalBackColor", new uniColor(222, 222, 239));
      utilSys.objectSacPut ("javajUI.text.disableBackColor", new uniColor(200, 200, 200));
      utilSys.objectSacPut ("javajUI.text.normalForeColor", new uniColor(20, 20, 20));

      //(o) javaj_global default console colors
      //
      utilSys.objectSacPut ("javajUI.text.consoleOutBackColor", new uniColor(  1,  41,  26));
      utilSys.objectSacPut ("javajUI.text.consoleOutForeColor", new uniColor(241, 251, 210));

      utilSys.objectSacPut ("javajUI.text.consoleErrBackColor", new uniColor(120,  10, 15)); //dark red
      utilSys.objectSacPut ("javajUI.text.consoleErrForeColor", new uniColor(235, 229, 164)); // amarillo claro

      utilSys.objectSacPut ("javajUI.text.consoleBothBackColor", new uniColor( 53, 69, 91));   // deep blue
      utilSys.objectSacPut ("javajUI.text.consoleBothForeColor", new uniColor(255, 255, 255));
   }

   private static boolean have_javajUI_tree_done = false;
   public static void ensureDefRes_javajUI_tree ()
   {
      if (have_javajUI_tree_done) return;
      have_javajUI_tree_done = true;

      //(o) android_TODO System colors
      //(o) TOSEE_javaj_global change to javajUI.tree ? (use "javajUI.tree." instead of "Tree.")
      //          Tree.x are the java native resources (except "Tree.rootIcon" and "Tree.secondRootIcon")

      //(o) javaj_global default tree icons
      //
//      utilSys.objectSacPut ("Tree.leafIcon"      , javaLoad.getSomeHowImageIcon ("javaj/img/leaf.png"   ));
//      utilSys.objectSacPut ("Tree.closedIcon"    , javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
//      utilSys.objectSacPut ("Tree.openIcon"      , javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
//      utilSys.objectSacPut ("Tree.collapsedIcon" , javaLoad.getSomeHowImageIcon ("javaj/img/node1.png"  ));
//      utilSys.objectSacPut ("Tree.expandedIcon"  , javaLoad.getSomeHowImageIcon ("javaj/img/node2.png"  ));
//      utilSys.objectSacPut ("Tree.rootIcon"      , javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
//      utilSys.objectSacPut ("Tree.secondRootIcon", javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));

      // harcoded Tree UI resources
      //
//      utilSys.objectSacPut("Tree.scrollsHorizontallyAndVertically",  new Boolean(true));
//      utilSys.objectSacPut("Tree.scrollsOnExpand", null); // only setting to false does not work on linux and Mac
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
         utilSys.objectSacPut ("javaj.List<widgets>", listOfWidgetNames);
      }
      return listOfWidgetNames;
   }

   public static void registerWidgetByName (String name, View widget)
   {
      utilSys.objectSacPut ("javaj." + name, widget);
      getListOfWidgetNames ().add (name);
      if (log_widgetRegister.isDebugging (2))
      {
         log_widgetRegister.dbg (2, "register", "add", new String [] { name, widget.getClass().toString () });
      }
   }

   public static View getWidgetByName (String name)
   {
      return (View) utilSys.objectSacGet ("javaj." + name);
   }
}