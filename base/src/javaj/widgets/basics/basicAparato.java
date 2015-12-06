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



/*
       -- SOLO DATA --      --- COMUMICACIONES ---------

        Data&Control          comm proxy       comm stub

         widgetEBS            basicMando        basicAparato

            ^                   ^                   ^
            |                   |                   |

         tableEBS            tableMando       tableAparato


         Es bastante limpio lo u'nico que para el proxy debemos
         reescribir todos los me'todos set/get puesto que no podemos
         heredar de widgetEBS


*/


package javaj.widgets.basics;

import javaj.*;

import de.elxala.langutil.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;

/*
*/
public class basicAparato extends widgetConsts implements MensakaTarget
{
   protected MensakaTarget myOwner = null;
   protected widgetEBS pEBS = null;

   protected EvaUnit baseJavajContext = null;

   private MessageHandle HMSG_Action = null;
   private MessageHandle HMSG_DoubleAction = null;

   public basicAparato (MensakaTarget objWidget, widgetEBS pDataAndControl)
   {
      myOwner = objWidget;
      pEBS = pDataAndControl;

      // Important : the mesage ONCE_CONTEXT is not forwarded to the widget owner
      //             and it is handled here in the base class transparently
      Mensaka.subscribe (this, RX_ONCE_CONTEXT, javajEBS.msgCONTEXT_BASE);

      Mensaka.subscribe (objWidget, RX_UPDATE_CONTROL, ebs().evaName (ebs().sMSG_UPDATE_CONTROL));
      Mensaka.subscribe (objWidget, RX_UPDATE_DATA,    ebs().evaName (ebs().sMSG_UPDATE_DATA));
   }

   public widgetEBS ebs ()
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
         HMSG_Action = new MessageHandle (myOwner, ebs().evaName (""));
      }

      if (signaling)
      {
         widgetLogger.log ().dbg (4, "basicAparato", "AVOID signaling \"" + ebs().evaName ("") + "\" twice!");
         return;
      }
      signaling = true;
      // send the message
      Mensaka.sendPacket (HMSG_Action, ebs().getData ());
      signaling = false;
   }

   public void signalDoubleAction ()
   {
      // not all widgets uses this signal
      if (HMSG_DoubleAction == null)
      {
         HMSG_DoubleAction = new MessageHandle (myOwner, ebs().evaName ("2"));
      }

      // send the message
      Mensaka.sendPacket (HMSG_DoubleAction, ebs().getData ());
   }

   // to help setting the label in spite of data with no label (only at the begining)
   //
   public String decideLabel (String defaultOne)
   {
      boolean nodata = (null == ebs ().getDataAttribute (""));

      String label = "";
      if (nodata && isBaseContexCall())
      {
         // set the default value to the one the widget already had
         label = defaultOne;
         ebs ().setText (label);
      }
      else label = ebs ().getText ();
      return label;
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

               // up to now isBaseContexCall will be false!
               baseJavajContext = euData;
            }
            break;

         default:
            return false;
      }

      return true;
   }
}
