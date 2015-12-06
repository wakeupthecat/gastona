/*
library listix (www.listix.org)
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

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

import de.elxala.langutil.*;
import de.elxala.math.polac.*;

public class runningTables
{
   /**
      This is a method used by cmdSetLoopTable and cmdRunLoopTable.
      it is defined directly in listix for convenience
   */
   public static int runLoopTable (listix that, String lsxFormat, Eva lsxDirect, int fromRow)
   {
      int passRows = 0;
      boolean secondOrMore = false;
      boolean thisFormat = (lsxFormat == null || lsxFormat.length () == 0);


      if (thisFormat)
      {
         passRows = that.countLsxFormatWhileText (lsxDirect, fromRow);
      }

      // loop with the table !
      //
      while (! that.getTableCursorStack ().isCurrentTableEOT ())
      {
         //
         if (secondOrMore)
            that.printTextLsx (that.getTableCursorStack ().getLinkString ());

         if (thisFormat)
         {
            // use lsxDirect
            that.printLsxFormatWhileText (lsxDirect, fromRow);

            // check if link valid
            //
            int supLink = fromRow + passRows;
            if (lsxDirect.cols (supLink) > 1 && lsxDirect.getValue (supLink, 1).equalsIgnoreCase ("LINK"))
            {
               //(o) TOSEE_listix_cmds 07.04.2008 runningTables error, why error ? (i just don't understand my code!)
               that.log().err ("RUNTABLE", "LINK at " + supLink);
            }
         }
         else if (! that.printLsxFormat (lsxFormat))
         {
            that.log().err ("RUNTABLE", "listix format [" + lsxFormat + "] not found while running " + lsxDirect.getName ());
         }

         if (! that.getTableCursorStack ().increment_RUNTABLE ())
         {
            break;
         }
         secondOrMore = true;
      }

      return passRows;
   }

   /**
      This is a method used by cmdSetLoopTable and cmdRunLoopTable.
      it is defined directly in listix for convenience
   */
   public static void runLoopTable (listix that, Eva lsxFormat)
   {
      boolean secondOrMore = false;

      //System.out.println ("\n--");
      //System.out.println (lsxFormat);
      //System.out.println ("\n--");

      // loop with the table !
      //
      while (! that.getTableCursorStack ().isCurrentTableEOT ())
      {
         //
         if (secondOrMore)
            that.printTextLsx (that.getTableCursorStack ().getLinkString ());

         that.doFormat (lsxFormat);
         if (! that.getTableCursorStack ().increment_RUNTABLE ()) break;
         secondOrMore = true;
      }
   }
}
