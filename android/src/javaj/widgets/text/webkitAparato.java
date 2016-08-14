/*
package javaj.widgets
Copyright (C) 2016 Alejandro Xalabarder Aulet

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


package javaj.widgets.text;

import android.widget.TextView;
import javaj.widgets.basics.*;
import de.elxala.mensaka.*;

import javax.swing.text.*;
import de.elxala.zServices.*;
import android.webkit.WebView;

/*
*/
public class webkitAparato extends basicAparato
{
   private MessageHandle HMSG_JSAction = null;
   private MessageHandle HMSG_RunJSFinish = null;

   public static final int RX_COMMNAD_LOAD_URL  = RX_MIN_CUSTOM_MAP - 0;
   public static final int RX_COMMNAD_LOAD_FILE = RX_MIN_CUSTOM_MAP - 1;
   public static final int RX_COMMNAD_RUN_JS    = RX_MIN_CUSTOM_MAP - 2;

   private static logger logStatic = new logger (null, "javaj.widgets.webkit", null);
   public logger log = logStatic;

   public webkitAparato (MensakaTarget objWidget, webkitEBS pDataAndControl)
   {
      super (objWidget, pDataAndControl);

      Mensaka.subscribe (objWidget, RX_COMMNAD_LOAD_URL,   ebs().evaName (ebs().sMSG_LOAD_URL));
      Mensaka.subscribe (objWidget, RX_COMMNAD_LOAD_FILE,  ebs().evaName (ebs().sMSG_LOAD_FILE));
      Mensaka.subscribe (objWidget, RX_COMMNAD_RUN_JS,     ebs().evaName (ebs().sMSG_RUN_JS));
   }

   public webkitEBS ebs ()
   {
      return (webkitEBS) super.ebs ();
   }

   public void updateControl (WebView co)
   {
      co.setVisibility (ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
   }

   public void signalJsAction (String [] argumentales)
   {
      if (HMSG_JSAction == null)
      {
         HMSG_JSAction = new MessageHandle (myOwner, ebs().evaName (ebs().sSIGNAL_JS_ACTION));
      }

      Mensaka.sendPacket (HMSG_JSAction, ebs().getData (), argumentales);
   }

   public void signalJsRunFinish (String value)
   {
      if (HMSG_RunJSFinish == null)
      {
         HMSG_RunJSFinish = new MessageHandle (myOwner, ebs().evaName (ebs().sSIGNAL_RUN_JS_FINISH));
      }

      Mensaka.sendPacket (HMSG_RunJSFinish, ebs().getData (), new String [] { value });
   }
}
