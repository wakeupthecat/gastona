/*
package javaj.widgets.graphics;
Copyright (C) 2011 Alejandro Xalabarder Aulet

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
package javaj.widgets.graphics.objects;

import de.elxala.zServices.*;
import de.elxala.langutil.*;
import javaj.widgets.graphics.*;

/**
   Global container for styles, map of styleObjects
   ...//(o) TODO_doc

 */
public class styleGlobalContainer
{
   private static logger log = new logger (null, "javaj.widgets.graphics.styleGlobalContainer", null);

   // Note : we better use the "Sac" provided by utilSys, therefore we dont need to destroy the static object
   //        by ourselves (destruction is needed for instance in Android and it is handled in gastona MainActor)
   //

   private static String mapName (String styleLogicName)
   {
      return "GSTYLE:" + styleLogicName;
   }

   public static styleObject addOrChangeStyle (String logicName, String styleStr)
   {
      if (logicName == null || styleStr == null) return new styleObject(""); // Error!

      log.dbg (2, "addOrChangeStyle", "add new style \"" + logicName + "\", string [" + styleStr + "]");

      styleObject obj = new styleObject (styleStr);
      utilSys.objectSacPut (mapName (logicName), obj);
      return obj;
   }

   /**
      Always return an object
         the name has to be either a logic style name like "fledySkin" or a style string like "fc:black"

      some use cases

         getStyleObjectByName ("myColor");
         getStyleObjectByName ("myColor", "sc:black;fc:+100200300");
         getStyleObjectByName ("sc:black;fc:+100200300");
   */
   public static styleObject getStyleObjectByName (String logicName)
   {
      return getStyleObjectByName (logicName, null);
   }

   public static styleObject getStyleObjectByName (String logicName, String fallback)
   {
      if (logicName == null) return new styleObject (""); // Error!

      styleObject obj = (styleObject) utilSys.objectSacGet (mapName (logicName));

      if (obj == null)
      {
         log.dbg (2, "getStyleObjectByName", "add anonymus style [" + logicName + "]");
         // ok, then we assume that it is a style string to be parsed, just do it once
         // its logic name will be the style string as well
         obj = addOrChangeStyle (logicName, (fallback != null) ? fallback: logicName);
      }
      return obj;
   }

   /**
      Tries to solve 'logicName' as style string. If 'logicName' is not present in the container
      then it is not added but just directly returned without parsing  or checking it.
   */
   public static String getStyleStringByName (String logicName)
   {
      if (logicName == null) return "";

      styleObject obj = (styleObject) utilSys.objectSacGet (mapName (logicName));
      return (obj != null) ? obj.getStyleString (): logicName;
   }
}
