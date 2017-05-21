/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2017  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.Eva;

import java.util.*;

/**
   class deepEvaUnit
   @author Alejandro Xalabarder Aulet
   @date   2017

   *** CLASS PREPARATION *****
   
   deepEvaUnit is a stack EvaUnits where each element of the stack masks the variables
   of the previous ones.

   <pre>
   Example:

         // create the EvaUnit
         //
         deepEvaUnit deuni = new deepEvaUnit ("myUnit");
         
         ...
         deuni.push (eu01);
         deuni.push (eu02);
         
         while (deuni.pop () != null);
   </pre>

*/
public class deepEvaUnit
{
   public deepEvaUnit ()
   {
   }
}