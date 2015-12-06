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


package javaj.widgets.table;

import de.elxala.mensaka.*;

import javaj.*;
import javaj.widgets.basics.*;
import de.elxala.zServices.*;

/*
   Repeat functionallity of basicMando (see note in tableWidgetBaseEBS.java) but with
   tableEBS instead of widgetEBS and add some mando specific func. for tables.
*/
public class basicTableMando
{
   // for controllers (xxxMando)
   public static final int RX_JAVAJ_FRAMES_MOUNTED = -10;

   protected Object myOwner = null;
   protected tableEBS pEBS = null;

   private MessageHandle HMSG_updateControl    = null;
   private MessageHandle HMSG_updateData       = null;
   private MessageHandle HMSG_RevertData       = null;

   private static logger logStatic = new logger (null, "javaj.widgets.table", null);
   protected logger log = logStatic;

   public basicTableMando (Object objController, tableEBS pDataAndControl)
   {
      myOwner = objController;
      pEBS    = pDataAndControl;

      HMSG_updateControl = new MessageHandle (objController, ebsTable ().evaName (widgetEBS.sMSG_UPDATE_CONTROL));
      HMSG_updateData    = new MessageHandle (objController, ebsTable ().evaName (widgetEBS.sMSG_UPDATE_DATA));
      HMSG_RevertData    = new MessageHandle (objController, ebsTable ().evaName (widgetEBS.sMSG_REVERT_DATA));
   }

   public tableEBS ebsTable ()
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
      Mensaka.subscribe ((MensakaTarget) myOwner, mapAction, ebsTable().evaName (""));
   }

   //
   //    CONTROL
   //

   // as it is not possible to inherit from widgetEBS we have to
   // forward these functions
   //
   public boolean getEnabled ()      { return ebsTable ().getEnabled (); }
   public boolean getVisible ()      { return ebsTable ().getVisible (); }

   // set enabled in the ebs and send the message to be updated in the widget
   // (set enabled in the controlled widget)
   public void setEnabled (boolean valor)
   {
      ebsTable ().setEnabled (valor);

      signalUpdateControl ();
   }

   // set visible in the ebs and send the message to be updated in the widget
   // (set visible in the controlled widget)
   public void setVisible (boolean valor)
   {
      ebsTable ().setVisible (valor);

      signalUpdateControl ();
   }

   public void signalUpdateData ()
   {
      Mensaka.sendPacket (HMSG_updateData, ebsTable ().getData ());
   }

   public void signalUpdateControl ()
   {
      Mensaka.sendPacket (HMSG_updateControl, ebsTable ().getControl ());
   }


   // ----------------------

   public void setDBConnection (String sqliteDBName, String SelectSQL)
   {
      ebsTable ().setDBConnection (sqliteDBName, SelectSQL);
      signalUpdateData ();
   }

   public void setVisibleColumns (String [] columNames)
   {
      ebsTable ().setVisibleColumns (columNames);
      signalUpdateControl ();
   }

   //
   public String [] getVisibleColumns ()
   {
      return ebsTable ().getVisibleColumns ();
   }

   public String [] getVisibleColumnShowNames ()
   {
      return ebsTable ().getVisibleColumnShowNames ();
   }
}
