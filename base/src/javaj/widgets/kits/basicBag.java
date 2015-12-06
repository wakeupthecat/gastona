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

package javaj.widgets.kits;

import javaj.*;
import javaj.widgets.basics.*;

import de.elxala.langutil.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import java.util.*;

/**
   bolsita

   Convenient class to group a set of basicMando of widgets (proxies to widgets)
   and make with them a minimal and standard handling. Namely :

      - create the zWidgetEBS itself giving the right owner to them
      - suscribe to the action of the widget (for buttons, radio and check boxes)
      - listen to the standard message of javaj "frames mounted" and provide the
        widgets with the control and data received in the message (usually take from javaj file
        or harcoded initialization)

   Thus a controller have only to fill this "bolsita" once and wait for the notifications.
   Example
   <pre>

      public class SusoController extends MensakaTarget
      {
         private basicBag plastico = null;

         private nameMap [] junto = new nameMap [] {
               new nameMap ("bLinterna",     100),
               new nameMap ("bSalvar",       101),
               new nameMap ("rLinux",        102),
               new nameMap ("rWindows",      103),
               new nameMap ("kUseJavaj",     104),
            };

         public SusoController ()
         {
            plastico = new basicBag (this, "", junto);
         }

         public boolean takePacket (int mappedID, EvaUnit euData)
         {
            switch (mappedID)
            {
               case 100:   // here bLinterna was clicked ...
                  break;

               case 101:   // here bSalvar was clicked ...
                  break;
               ...
            }
         }
      }
   </pre>


   16.10.2005 18:24 Change to a more convenient name (old name was mugardos!)


*/
public class basicBag implements MensakaTarget
{
   public static final int FRAMES_OK = 0;

   private basicMando [] wichettos  = null;
   private nameMap [] info = null;

   public basicBag (MensakaTarget este, String prename, nameMap [] petar)
   {
      Mensaka.suscribe (this, FRAMES_OK, javajEBS.msgFRAMES_MOUNTED);
      info = petar;

      wichettos = new basicMando [petar.length];
      for (int ii = 0; ii < petar.length; ii ++)
      {
         wichettos[ii] = new basicMando (este, new widgetEBS (prename + petar[ii].nombre, new EvaUnit (), new EvaUnit ()));
         wichettos[ii].suscribeAction (petar[ii].mappa);
      }
   }

   public basicMando getWidgetEBS (String name)
   {
      for (int ii = 0; ii < info.length; ii ++)
      {
         if (info[ii].nombre.equals (name))
            return  wichettos[ii];
      }
      return null;
   }

   public basicMando getWidgetEBS (int indx)
   {
      if (indx < 0 || indx >= info.length)
         return null;

      return wichettos[indx];
   }

   public int size ()
   {
      if (wichettos == null) return 0;
      return wichettos.length;
   }

   public boolean takePacket (int mappedID, EvaUnit controlLine)
   {
      switch (mappedID)
      {
         case FRAMES_OK:
            //(o) TODO_mensaka Review Mensaka.unsuscribe ! seems that the line below unsuscribe other listeners as well !!
            // Mensaka.unsuscribe (javajEBS.msgFRAMES_MOUNTED, this);
            for (int ii = 0; ii < wichettos.length; ii ++)
               wichettos[ii].ebs ().setNameDataAndControl (null, controlLine, controlLine);
            break;

         default:
            Mensaka.sendPacket ("DBG MensakaTarget not handled");
            return false;
      }

      return true;
   }
}
