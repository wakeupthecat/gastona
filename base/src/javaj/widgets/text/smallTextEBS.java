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


package javaj.widgets.text;

import javaj.widgets.basics.*;
import de.elxala.langutil.*;
import de.elxala.Eva.*;

/*
*/
public class smallTextEBS extends textEBS
{
   public smallTextEBS (String nameWidget, EvaUnit pData, EvaUnit pControl)
   {
      super (nameWidget, pData, pControl);
   }

   /**
      get text from the model
   */
   public String getText ()
   {
      Eva dat = mustGetEvaData ();
      StringBuffer str = new StringBuffer ();

      for (int ii = 0; ii < dat.rows (); ii ++)
      {
         str.append (((ii == 0) ? "": "\n") + dat.getValue (ii, 0));
      }
      return str.toString ();
   }

   /**
      set text into the model
   */
   public void setText (String newText)
   {
      Eva dat = mustGetEvaData ();
      dat.clear ();

      // unperformant text to Eva !
      Cadena ca = new Cadena (newText);
      ca.setSepar ("\n");

      while (ca.getToken ())
         dat.addLine (new EvaLine (new String [] { ca.lastToken () }));
   }
}
