/*
library listix (www.listix.org)
Copyright (C) 2005-2016 Alejandro Xalabarder Aulet

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

package listix.table;

import listix.*;
import de.elxala.Eva.*;

import de.elxala.langutil.*;
import de.elxala.math.polac.*;

public class tableRunner
{
   protected listixCmdStruct cmdLsx = null;

   protected Eva headerFormat = null;
   protected Eva bodyFormat = null;
   protected String bodyFormatName = null;
   protected Eva tailFormat = null;
   protected Eva if0RowLsxFormat = null;
   //(o) TODO/preparing Loop option UNIFORM or STEP
   // protected String stepVar = null;
   // protected float stepIncr = 1.f;

   public tableRunner (listixCmdStruct cmd)
   {
      cmdLsx = cmd;
      bodyFormat      = cmdLsx.takeOptionAsEva (new String [] { "", "BODY" });
      headerFormat    = cmdLsx.takeOptionAsEva (new String [] { "HEAD", "HEADER" });
      tailFormat      = cmdLsx.takeOptionAsEva (new String [] { "TAIL" });
      if0RowLsxFormat = cmdLsx.takeOptionAsEva (new String [] { "IFNORECORD", "IFEMPTY", "EMPTY", "IF0ROWS" });

      //(o) TODO/preparing Loop option UNIFORM or STEP
      // Option Step or Uniform
      //    will do the iterations at constant steps of the given variable and interval
      //    the rest of the columns will be linearly interpolated
      //    useful for example to compare measures taken at specific and irregular timestamps
      //
      // String [] stepOption = takeOptionParameters (new String [] { "STEP BY", "STEP", "UNIFORM" });
      // stepVar = stepOption != null && stepOption.length > 0 ? stepOption[0]: null;
      // stepIncr = stepVar != null && stepOption != null && stepOption.length > 1 ? stdlib.atof (stepOption[1]): 1.f;
   }

   // only for "deprecatable" RUN LOOP
   //
   public void setBodyFormatName (String bodyFormatStr)
   {
      if (bodyFormatStr == null || bodyFormatStr.length () == 0)
           bodyFormatName = null;
      else bodyFormatName = bodyFormatStr;
   }


   public boolean hasContents ()
   {
      return bodyFormat != null ||
             bodyFormatName != null ||
             headerFormat != null ||
             tailFormat != null ||
             if0RowLsxFormat != null;
   }

   /**
      This is a method used by cmdSetLoopTable and cmdRunLoopTable.
   */
   public void doLoopTable ()
   {
      boolean firstIteration = true;

      //System.out.println ("\n--");
      //System.out.println (bodyFormat);
      //System.out.println ("\n--");

      tableCursorStack cur = cmdLsx.getListix ().getTableCursorStack ();

      // loop with the table !
      //
      cmdLsx.getListix ().loopStarts ();
      if (cur.isCurrentTableEOT ())
      {
         cmdLsx.getListix ().log().dbg (4, "runLoopTable", "executing 'if 0 row' subcommand");
         cmdLsx.getListix ().doFormat (if0RowLsxFormat);
      }
      while (! cur.isCurrentTableEOT ())
      {
         if (firstIteration)
            cmdLsx.getListix ().doFormat (headerFormat);
         else
            cmdLsx.getListix ().printTextLsx (cur.getLinkString ());
         firstIteration = false;

         //**todo adding following debug message should not affect performance in any way
         cmdLsx.getListix ().log().dbg (4, "tableRunner", "loopIteration");

         if (bodyFormatName != null)
              cmdLsx.getListix ().printLsxFormat (bodyFormatName);
         else cmdLsx.getListix ().doFormat (bodyFormat);

         if (cmdLsx.getListix ().loopBroken ())
         {
            cmdLsx.getListix ().log().dbg (2, "tableRunner", "quiting loop due to a break");
            break;
         }
         if (! cur.increment_RUNTABLE ())
         {
            cmdLsx.getListix ().log().dbg (2, "tableRunner", "loop finished");
            cmdLsx.getListix ().doFormat (tailFormat);
            break;
         }
      }
   }
}
