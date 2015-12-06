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
*/


package javaj.widgets.panels;

import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import javaj.*;
import javaj.widgets.basics.*;

/*
   //(o) widget_Aparatos frameAparato
   //(o) widget_versionReviewed_5.00 21.01.2007 11:10
*/
public class frameAparato extends basicAparato
{
   public static final int RX_WILD_UPDATE   = 11;
   public static final int RX_SHOW_FRAMES   = 12;
   public static final int RX_SHOW          = 13;
   public static final int RX_HIDE          = 14;
   public static final int RX_TOGGLE_VISIBLE = 15;
   public static final int RX_TELL_POSANDSIZE = 16;

   private static MessageHandle TX_POSITION_CHANGED  = new MessageHandle ();
   private static MessageHandle TX_RESIZED           = new MessageHandle ();

   public frameAparato (MensakaTarget objWidget, frameEBS pDataAndControl)
   {
      super (objWidget, pDataAndControl);

      Mensaka.subscribe (objWidget, RX_SHOW_FRAMES, javajEBS.msgSHOW_FRAMES);
      Mensaka.subscribe (objWidget, RX_WILD_UPDATE,  "_sys Look&FeelChanged");

      Mensaka.subscribe (objWidget, RX_SHOW, ebsFrame().evaName (ebsFrame().sMSG_SHOW));
      Mensaka.subscribe (objWidget, RX_HIDE, ebsFrame().evaName (ebsFrame().sMSG_HIDE));
      Mensaka.subscribe (objWidget, RX_TOGGLE_VISIBLE, ebsFrame().evaName (ebsFrame().sMSG_TOGGLE_VISIBLE));
      Mensaka.subscribe (objWidget, RX_TELL_POSANDSIZE, ebsFrame().evaName (ebsFrame().sMSG_TELL_POSANDSIZE));

      Mensaka.declare (objWidget, TX_POSITION_CHANGED  , ebsFrame().evaName ("position")  , logServer.LOG_DEBUG_0);
      Mensaka.declare (objWidget, TX_RESIZED           , ebsFrame().evaName ("resize")    , logServer.LOG_DEBUG_0);
   }

   public frameEBS ebsFrame ()
   {
      return (frameEBS) ebs ();
   }

   public void signalPosition (int x, int y)
   {
      ebsFrame().setPos(x, y);
      Mensaka.sendPacket (TX_POSITION_CHANGED, ebsFrame().getData ());
   }

   public void signalResize (int dx, int dy)
   {
      ebsFrame().setSize(dx, dy);
      Mensaka.sendPacket (TX_RESIZED, ebsFrame().getData ());
   }
}
