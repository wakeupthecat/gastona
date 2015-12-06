/*
package javaj.widgets
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

// NOTA 29.06.2008 13:53: quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c

/*
       -- SOLO DATA --      --- COMUMICACIONES ---------

        Data&Control          comm proxy       comm stub

         widgetEBS            basicMando        basicAparato

            ^                   ^                   ^
            |                   |                   |

         tableEBS            tableMando       tableAparato


         Es bastante limpio lo u/nico que para el proxy debemos
         reescribir todos los me'todos set/get puesto que no podemos
         heredar de widgetEBS


*/


package javaj.widgets.table;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.*;
import javaj.widgets.*;
import javaj.widgets.basics.*;
import de.elxala.zServices.*;

/*
*/
public class basicTableAparato extends widgetConsts implements MensakaTarget
{
   protected MensakaTarget myOwner = null;
   protected tableEBS pEBS = null;

   protected EvaUnit baseJavajContext = null;

   private MessageHandle HMSG_Action = null;
   private MessageHandle HMSG_2Action = null;

   private static logger logStatic = new logger (null, "javaj.widgets.table", null);
   public logger log = logStatic; // Note: it cannot be "protected"

   public basicTableAparato (MensakaTarget objWidget, tableEBS pDataAndControl)
   {
      myOwner = objWidget;
      pEBS = pDataAndControl;

      // Important : the mesage ONCE_CONTEXT is not forwarded to the widget owner
      //             and it is handled here in the base class transparently
      Mensaka.subscribe (this, RX_ONCE_CONTEXT, javajEBS.msgCONTEXT_BASE);

      Mensaka.subscribe (objWidget, RX_UPDATE_CONTROL, ebsTable().evaName (widgetEBS.sMSG_UPDATE_CONTROL));
      Mensaka.subscribe (objWidget, RX_UPDATE_DATA,    ebsTable().evaName (widgetEBS.sMSG_UPDATE_DATA));
      Mensaka.subscribe (objWidget, RX_SELECT_DATA,    ebsTable().evaName (widgetEBS.sMSG_SELECT_DATA));
   }

   public tableEBS ebsTable ()
   {
      return pEBS;
   }

   public boolean isBaseContexCall ()
   {
      return baseJavajContext == null;
   }

   private boolean signaling = false;

   public void signalAction ()
   {
      // not all widgets uses this signal
      if (HMSG_Action == null)
      {
         HMSG_Action = new MessageHandle (myOwner, ebsTable().evaName (""));
      }

      if (signaling)
      {
         widgetLogger.log ().dbg (4, "basicTableAparato", "AVOID signaling \"" + ebsTable().evaName ("") + "\" twice!");
         return;
      }
      signaling = true;
      // send the message
      Mensaka.sendPacket (HMSG_Action, ebsTable().getData ());
      signaling = false;
   }

   public void signalDoubleAction ()
   {
      // not all widgets uses this signal
      if (HMSG_2Action == null)
      {
         HMSG_2Action = new MessageHandle (myOwner, ebsTable().evaName ("2"));
      }

      // send the message
      Mensaka.sendPacket (HMSG_2Action, ebsTable().getData ());
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_ONCE_CONTEXT:
            if (isBaseContexCall ())
            {
               // react only once to this message, this is the time
               // the message has been sent by our javaj instance (the javaj instance that
               // has this xxxAparato instanciate)
               //
               myOwner.takePacket (widgetConsts.RX_UPDATE_DATA,    euData, pars);
               myOwner.takePacket (widgetConsts.RX_UPDATE_CONTROL, euData, pars);

               // now will be anymore isBaseContexCall true!
               baseJavajContext = euData;
            }
            break;

         default:
            return false;
      }

      return true;
   }
}
