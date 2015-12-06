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

package javaj.widgets.basics;

import de.elxala.zServices.*;
import de.elxala.Eva.*;

public class widgetLogger
{
   private static logger logObj = new logger (null, "javaj.widgets", null);

   public static logger log ()
   {
      return logObj;
   }

   // usually to be called by a widget when received update data message
   //
   public static boolean updateContainerWarning (String containerType, String widgetType, String widgetName, EvaUnit originalUnit, EvaUnit newUnit)
   {
      // log new container
      //
      log().dbg (2, widgetType + ":" + widgetName, "update " + containerType + " received");
      if (originalUnit != null && originalUnit != newUnit)
      {
         //(o) gastona_ARCH Supporting parameters in messages
         //       Note
         //       - Right now each Activity has its own listix data and formats units
         //       - A message to a widget of other activity is not supported (it does not work with the proper data!)
         //       - This method is thought to be used by widgets only
         //
         // Therefore, he we simply avoid such message
         //
         log().err (widgetType + ":" + widgetName, "ignore " + containerType + " message comming from another activity");
         return false;
      }
      return false;
   }
}
