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

        Data&Control    comm proxy       comm stub

         widgetEBS    basicMando        basicAparato

            ^           ^                       ^
            |           |                       |

         tableEBS    tableMando       tableAparato

*/


package javaj.widgets.basics;

import de.elxala.mensaka.*;
import javaj.*;

/*
*/
public class basicMando extends widgetConsts
{
   protected Object myOwner = null;
   protected widgetEBS pEBS = null;

   private MessageHandle HMSG_updateControl    = null;
   private MessageHandle HMSG_updateData       = null;
   private MessageHandle HMSG_RevertData       = null;

   public basicMando (Object objController, widgetEBS pDataAndControl)
   {
      myOwner = objController;
      pEBS    = pDataAndControl;

      HMSG_updateControl = new MessageHandle (objController, ebs ().evaName (widgetEBS.sMSG_UPDATE_CONTROL));
      HMSG_updateData    = new MessageHandle (objController, ebs ().evaName (widgetEBS.sMSG_UPDATE_DATA));
      HMSG_RevertData    = new MessageHandle (objController, ebs ().evaName (widgetEBS.sMSG_REVERT_DATA));
   }

   public widgetEBS ebs ()
   {
      return pEBS;
   }

   public void subscribeJavajFramesMounted ()
   {
      Mensaka.subscribe ((MensakaTarget) myOwner, RX_JAVAJ_FRAMES_MOUNTED, javajEBS.msgFRAMES_MOUNTED);
   }

   public void subscribeAction (int mapAction)
   {
      // the message action is just the name of the widget
      Mensaka.subscribe ((MensakaTarget) myOwner, mapAction, ebs().evaName (""));
   }

   //
   //    DATA
   //

   public boolean isChecked  ()
   {
      return ebs ().isChecked ();
   }

   public void setChecked (boolean on)
   {
      ebs ().setChecked (on);

      signalUpdateData ();
   }


   public String getImageFile ()
   {
      return ebs().getImageFile ();
   }

   public void setImageFile (String imageName)
   {
      ebs().setImageFile (imageName);
   }



   //
   //    CONTROL
   //

   // as it is not possible to inherit from widgetEBS we have to
   // forward these functions
   //
   public boolean getEnabled ()      { return ebs ().getEnabled (); }
   public boolean getVisible ()      { return ebs ().getVisible (); }

   // set enabled in the ebs and send the message to be updated in the widget
   // (set enabled in the controlled widget)
   public void setEnabled (boolean valor)
   {
      ebs ().setEnabled (valor);

      signalUpdateControl ();
   }

   // set visible in the ebs and send the message to be updated in the widget
   // (set visible in the controlled widget)
   public void setVisible (boolean valor)
   {
      ebs ().setVisible (valor);

      signalUpdateControl ();
   }

   public void signalUpdateData ()
   {
      Mensaka.sendPacket (HMSG_updateData, ebs ().getData ());
   }

   public void signalUpdateControl ()
   {
      Mensaka.sendPacket (HMSG_updateControl, ebs ().getControl ());
   }

   public void signalRevertData ()
   {
      Mensaka.sendPacket (HMSG_RevertData, ebs ().getData ());
   }
}
