/*
package de.elxala.math.polac;
(c) Copyright 2006 Alejandro Xalabarder Aulet

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

package de.elxala.math.polac;

import de.elxala.langutil.*;

/**
 */
public class utilParse extends fyPrimitivas
{
   public static boolean isNumber (String str)
   {
      Cadena ss = new Cadena (str);
      ss.setStr (ss.o_str.trim ());

      if (ss.indexOf ("+-.0123456789", 0) != 0) return false;
      if (ss.indexOf (".0123456789", 0) == 0) return true;
      if ((ss.indexOf ("+-", 0) == 0) &&
        (ss.indexOf (".0123456789", 0) == 1)) return true;
      return false;
   }

   public static int QueEs (Cadena sstr)
   {
      if (isNumber(sstr.o_str))
         return EsValor;

      if ((sstr.length () == 1) &&
        (sstr.indexOf ("xyz", 0) >= 0)) return EsVariable;

      int n_inc = indxPrimitiva(sstr.o_str);

      if (n_inc < 0) return EsFuncionExt;

      if (n_inc <= HastaCte) return   EsConstante;
      if (n_inc <= HastaOpe2) return  EsOperador;
      if (n_inc <= HastaFun3) return  EsFuncionInt;

      return  EsFuncionExt;
   }

   public static final int OP_IS_NUMBER = -2;

   /*
      indxOP returns

         Numeric_Cte (-2) if str is a number
         primitive indx
   */
   public static int indxOP (String str, String [] variableNames)
   {
      if (isNumber(str))
         return Numeric_Cte;

      // detect variables (e.g. x, y, z)
      //
      for (int vv = 0; vv < variableNames.length; vv ++)
      {
         if (str.equals (variableNames[vv]))
            return Espec_VarMIN + vv;
      }

      // deep hardcoded x is always a first variable (if don't else specified in variableNames)
      // if (str.equalsIgnoreCase ("x")) return Espec_Var_X;
      return indxPrimitiva (str);
   }

}
