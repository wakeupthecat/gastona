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

import javax.swing.text.*;
import de.elxala.zServices.*;

/*
*/
public class textAparato extends basicAparato
{
   public static final int RX_COMMNAD_LOAD  = RX_MIN_CUSTOM_MAP - 0;
   public static final int RX_COMMNAD_SAVE  = RX_MIN_CUSTOM_MAP - 1;
   public static final int RX_COMMNAD_CLEAR = RX_MIN_CUSTOM_MAP - 2;
   public static final int RX_COMMNAD_GOTO  = RX_MIN_CUSTOM_MAP - 3;
   public static final int RX_COMMNAD_UNDO  = RX_MIN_CUSTOM_MAP - 4;
   public static final int RX_COMMNAD_REDO  = RX_MIN_CUSTOM_MAP - 5;
   public static final int RX_COMMNAD_INSERTTEXT = RX_MIN_CUSTOM_MAP - 6;
   public static final int RX_COMMNAD_NEWLINE = RX_MIN_CUSTOM_MAP - 7;

   public static final int RX_LAST_COMMNAD  = RX_COMMNAD_REDO;

   private boolean isDirtyCached = false;

   private static logger logStatic = new logger (null, "javaj.widgets.text", null);
   public logger log = logStatic;   // Note: public instead of protected, because of the "bizarre" protection java concept!

   public textAparato (MensakaTarget objWidget, textEBS pDataAndControl)
   {
      super (objWidget, pDataAndControl);

      Mensaka.subscribe (objWidget, RX_REVERT_DATA, ebs().evaName (ebs().sMSG_REVERT_DATA));
      Mensaka.subscribe (objWidget, RX_REVERT_DATA, widgetEBS.sMSG_FORECAST_DATA_FROM_WIDGET_TO_MODEL);

      Mensaka.subscribe (objWidget, RX_COMMNAD_LOAD,  ebsText().evaName (ebsText().sMSG_LOAD));
      Mensaka.subscribe (objWidget, RX_COMMNAD_SAVE,  ebsText().evaName (ebsText().sMSG_SAVE));
      Mensaka.subscribe (objWidget, RX_COMMNAD_CLEAR, ebsText().evaName (ebsText().sMSG_CLEAR));
      Mensaka.subscribe (objWidget, RX_COMMNAD_GOTO, ebsText().evaName (ebsText().sMSG_GOTOLINE));
      Mensaka.subscribe (objWidget, RX_COMMNAD_UNDO, ebsText().evaName (ebsText().sMSG_UNDO));
      Mensaka.subscribe (objWidget, RX_COMMNAD_REDO, ebsText().evaName (ebsText().sMSG_REDO));
      Mensaka.subscribe (objWidget, RX_COMMNAD_INSERTTEXT, ebsText().evaName (ebsText().sMSG_INSERT));
      Mensaka.subscribe (objWidget, RX_COMMNAD_NEWLINE, ebsText().evaName (ebsText().sMSG_NEWLINE));
   }

   public textEBS ebsText ()
   {
      return (textEBS) ebs ();
   }

   // return true if there is a change
   public boolean setIsDirty (boolean isDirty)
   {
      if (isDirtyCached ^ isDirty)  // if changed!
      {
         isDirtyCached = isDirty;
         ebsText ().setIsDirty (isDirty);
         return true;
      }
      return false;
   }

   public boolean getIsDirty ()
   {
      return isDirtyCached;
   }

   // widget helper
   public void updateControl (JTextComponent co)
   {
      co.setBackground (ebsText ().getBackgroundColor ());
      co.setForeground (ebsText ().getForegroundColor ());
      co.setCaretColor (ebsText ().getForegroundColor ());
      co.setEnabled    (ebsText ().getEnabled ());

      //(o) TODO_REVIEW visibility issue
      // avoid setVisible (false) when the component is not visible (for the first time ?)
      boolean visible = ebsText ().getVisible ();
      if (visible || co.isShowing ())
         co.setVisible  (visible);
   }
}

/**

      //(o) TODO_REVIEW visibility issue

20.05.2012 03:16

   RE-FIX (first fix on zButton on 2011.09.11, but not good enough!)

   [] TODO!
   STILL UNCLEAR WHY WE NEED TO CARE ABOUT isShowing !!! probably a problem of
   initialization that should be investigated


   Problem random visibility

the sample script of welcome gastona "dislaimer and license" fails to show the text area (scroll is needed)
or simply the script

--------------
#javaj#

   <frames> eText
--------------

would fail without the re-fix

the simple solution

      if (co.isShowing ())
         co.setVisible  (visible);

only works on the above samples but would fail in the next one: pressing
bEncendells would not have any effect

--------------
#javaj#

   <layout of main>
      EVA, 10, 10, 3, 3

      , X
      , eLicense
      , bApagarls
      , bEncendells

#listix#

   <-- bApagarls>    -->, eLicense control!, visible, 0
   <-- bEncendells>  -->, eLicense control!, visible, 1
--------------

*/