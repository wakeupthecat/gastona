/*
package javaj.widgets.graphics;
Copyright (C) 2022 Alejandro Xalabarder Aulet

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
package javaj.widgets.graphics.objects;

import java.util.*;
import de.elxala.zServices.*;
import de.elxala.langutil.*;
import javaj.widgets.graphics.*;
import de.elxala.Eva.*;

/**
 */
public class styleSet
{
   // private static logger log = new logger (null, "javaj.widgets.graphics.styleSet", null);

   protected TreeMap styleArray = new TreeMap ();  // <String, styleObject> map for the defined styles (defstyle)
   protected TreeMap noDefCache = new TreeMap ();  // <String, styleObject> map for the not defined ones to parse them only once

   public void clear ()
   {
      styleArray = new TreeMap ();
      noDefCache = new TreeMap ();
   }

   public void addStyle (String logicName, String styleStr)
   {
      if (logicName == null || styleStr == null) return;

      // log.dbg (2, "addStyle", "new style \"" + logicName + "\", string [" + styleStr + "]");
      styleArray.put (logicName, new styleObject (styleStr));
   }

   public styleObject getStyle (String logicNameOrStyle)
   {
      if (logicNameOrStyle == null) return getStyle ("");
      styleObject obj = (styleObject) styleArray.get (logicNameOrStyle);
      if (obj == null)
      {
         obj = new styleObject (logicNameOrStyle);
         noDefCache.put (logicNameOrStyle, obj);
      }
      return obj;
   }

   public void dumpIntoEva (Eva eva)
   {
      List claus = new ArrayList(styleArray.keySet ());
      for (int cc = 0; cc < claus.size (); cc ++)
      {
         String skey = (String) claus.get (cc);
         styleObject sobj = (styleObject) styleArray.get (skey);
         if (sobj != null)
            eva.addLine (new EvaLine ("defstyle," +  skey + ",\"" + sobj.getStyleString () + "\""));
      }
   }

   public String toString ()
   {
      StringBuffer sbuf = new StringBuffer ();

      List claus = new ArrayList(styleArray.keySet ());
      for (int cc = 0; cc < claus.size (); cc ++)
      {
         String skey = (String) claus.get (cc);
         styleObject sobj = (styleObject) styleArray.get (skey);
         if (sobj != null)
            sbuf.append ("defstyle, " + skey + ", \"" + sobj.getStyleString () + "\"\n");
      }

      return sbuf.toString ();
   }

   // public void appendFromText (String text)
   // {
   // }
   //
   // public void loadFromText ()
   // {
   // }
}
