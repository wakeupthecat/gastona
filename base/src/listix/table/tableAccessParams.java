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

package listix.table;

import listix.*;


import java.util.*;
import de.elxala.langutil.*;
import de.elxala.Eva.*;

/**
   Special tableAccessBase only for option PARAMS of listix, it is not related
   directly with any LOOP syntax

   <listixParams>
      p1   , p2    , ...
      val1 , val2  , ...
*/
public class tableAccessParams extends tableAccessEva
{
   public tableAccessParams (String [] params)
   {
      String [] columns = new String [params.length];
      for (int ii = 0; ii < params.length; ii ++)
      {
         columns[ii] = "p" + (ii + 1);
      }

      //use evaData from tableAccessBase
      evaData = new Eva("(listix)Params");
      evaData.addLine (new EvaLine (columns));
      evaData.addLine (new EvaLine (params));

      currRow = zeroRow ();
   }

   public boolean setCommand (listixCmdStruct cmdData)
   {
      cmdData.getLog().severe  ("tableParms", "Wrong use of tableParams!");
      return false;
   }

   public int getParamCount()
   {
      return evaData.cols (0);
   }
}
