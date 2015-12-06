/*
package javaj.widgets
Copyright (C) 2007 Alejandro Xalabarder Aulet

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

import de.elxala.langutil.*;      // for attributes of type "int"
import de.elxala.Eva.*;
import javaj.widgets.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.uniColor;
import de.elxala.zServices.*;

/**
  Generic text EvaBasedStructure (EBS) used in all text based zWidgets
*/
public class textEBS extends generatedEBS4Text
{
   private static logger logStatic = new logger (null, "javaj.widgets.text", null);
   public logger log = logStatic;   // Note: public instead of protected, because of the "bizarre" protection java concept!

   /// Constructor
   //
   public textEBS (String nameWidget, EvaUnit pData, EvaUnit pControl)
   {
      super (nameWidget, pData, pControl);

      // ensure default "javajUI.text.*" resources
      javaj.globalJavaj.ensureDefRes_javajUI_text ();
   }

   private static int normalBackColor = -99;
   private static int normalForeColor = -99;
   private static int disableBackColor = -99;
   private static int dirtyBackColor = -99;

   private static void cacheColors ()
   {
      if (normalBackColor == -99)
      {
         uniColor uco = (uniColor) utilSys.objectSacGet ("javajUI.text.normalBackColor");
         normalBackColor = (uco != null) ? uco.toInt (): uniColor.WHITE;

         uco = (uniColor) utilSys.objectSacGet ("javajUI.text.normalForeColor");
         normalForeColor = (uco != null) ? uco.toInt (): uniColor.BLACK;

         uco = (uniColor) utilSys.objectSacGet ("javajUI.text.disableBackColor");
         disableBackColor = (uco != null) ? uco.toInt (): uniColor.WHITE;

         uco = (uniColor) utilSys.objectSacGet ("javajUI.text.dirtyBackColor");
         dirtyBackColor = (uco != null) ? uco.toInt (): uniColor.WHITE;
      }
   }

   public int getBackgroundColor ()
   {
      cacheColors ();
      if (! getEnabled ())
         return disableBackColor;

      if (getIsDirty ())
         return dirtyBackColor;

      return normalBackColor;
   }

   public int getForegroundColor ()
   {
      cacheColors ();
      return normalForeColor;
   }
}
/*

[TextArea.background]
[TextArea.border]
[TextArea.caretBlinkRate]
[TextArea.caretForeground]
[TextArea.focusInputMap]
[TextArea.font]
[TextArea.foreground]

*/