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
      NOTE: 2013.07 THIS method should be deprecated since LOOP feature "inline after options" SHOULD be deprecated
      
         LOOP, TYPE, xxx
             ,, standard inline format
             ,, ...
         //very old inline format ("inline after options" format)
         //etc
         ANYCOMAND, xxx
         
      This is a method used by cmdSetLoopTable and cmdRunLoopTable.
      it is defined directly in listix for convenience
   */
   public static int runLoopTableInlineAfterOptions (listix that, String lsxFormat, Eva lsxDirect, int fromRow)
   {
      int passRows = 0;
      boolean secondOrMore = false;
      boolean thisFormat = (lsxFormat == null || lsxFormat.length () == 0);

      that.loopStarts ();
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

         if (that.loopBroken ())
         {
            that.log().dbg (2, "runLoopTable", "quiting loop due to a break");
            break;
         }
         if (! that.getTableCursorStack ().increment_RUNTABLE ())
         {
            that.log().dbg (2, "runLoopTable", "loop finished");
            break;
         }
         secondOrMore = true;
      }

      return passRows;
   }

   // /**
      // This is a method used by cmdSetLoopTable and cmdRunLoopTable.
      // it is defined directly in listix for convenience
   // */
   // public static void runLoopTable (listix that, Eva lsxFormat, Eva if0RowLsxFormat)
   // {
      // boolean secondOrMore = false;

      // //System.out.println ("\n--");
      // //System.out.println (lsxFormat);
      // //System.out.println ("\n--");

      // // loop with the table !
      // //
      // that.loopStarts ();
      // if (that.getTableCursorStack ().isCurrentTableEOT () && if0RowLsxFormat.rows () > 0)
      // {
         // that.log().dbg (4, "runLoopTable", "executing 'if 0 row' subcommand");
         // that.doFormat (if0RowLsxFormat);
      // }
      // while (! that.getTableCursorStack ().isCurrentTableEOT ())
      // {
         // //
         // if (secondOrMore)
            // that.printTextLsx (that.getTableCursorStack ().getLinkString ());

         // //**todo adding following debug message should not affect performance in any way 
         // //that.log().dbg (4, "runLoopTable", "executing format in loop");
         // that.doFormat (lsxFormat);
         // if (that.loopBroken ())
         // {
            // that.log().dbg (2, "runLoopTable", "quiting loop due to a break");
            // break;
         // }
         // if (! that.getTableCursorStack ().increment_RUNTABLE ())
         // {
            // that.log().dbg (2, "runLoopTable", "loop finished");
            // break;
         // }
         // secondOrMore = true;
      // }
   // }

   /**
      This is a method used by cmdSetLoopTable and cmdRunLoopTable.
      it is defined directly in listix for convenience
   */
   public static void runLoopTable (listix that, Eva headerFormat, Eva bodyFormat, Eva tailFormat, Eva if0RowLsxFormat)
   {
      boolean secondOrMore = false;

      //System.out.println ("\n--");
      //System.out.println (bodyFormat);
      //System.out.println ("\n--");

      // loop with the table !
      //
      that.loopStarts ();
      if (that.getTableCursorStack ().isCurrentTableEOT () && if0RowLsxFormat.rows () > 0)
      {
         that.log().dbg (4, "runLoopTable", "executing 'if 0 row' subcommand");
         that.doFormat (if0RowLsxFormat);
      }
      while (! that.getTableCursorStack ().isCurrentTableEOT ())
      {
         that.doFormat (headerFormat);
         headerFormat = null;

         //
         if (secondOrMore)
            that.printTextLsx (that.getTableCursorStack ().getLinkString ());

         //**todo adding following debug message should not affect performance in any way 
         //that.log().dbg (4, "runLoopTable", "executing format in loop");
         that.doFormat (bodyFormat);
         if (that.loopBroken ())
         {
            that.log().dbg (2, "runLoopTable", "quiting loop due to a break");
            break;
         }
         if (! that.getTableCursorStack ().increment_RUNTABLE ())
         {
            that.log().dbg (2, "runLoopTable", "loop finished");
            that.doFormat (tailFormat);
            break;
         }
         secondOrMore = true;
      }
   }
}
