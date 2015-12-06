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

package javaj.widgets.text;

import javaj.widgets.basics.*;
import de.elxala.mensaka.*;

/*
*/
public class textMando extends basicMando
{
   public textMando (Object objController, widgetEBS pDataAndControl)
   {
      super (objController, pDataAndControl);
   }

   public textEBS ebsText ()
   {
      return (textEBS) pEBS;
   }

   //
   //    DATA
   //

   public String getText ()
   {
      // check if data is dirty and if so then send the message "revert data"
      // to the widget, the widget will then update the data in ebs with the one
      // of the native widgets

      if (ebsText().getIsDirty())
      {
         signalRevertData ();
      }
      return ebsText ().mustGetEvaData ().getValue (0, 0);
   }

   public void setText (String newText)
   {
      ebsText ().mustGetEvaData ().setValue (newText, 0, 0);

      signalUpdateData ();
      signalUpdateControl (); //(o) TODO_widgets review if this is really needed!
   }
}
