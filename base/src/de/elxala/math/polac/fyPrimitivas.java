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

/** Clase para que contiene las constantes, operadores y funciones primitivas
 *  de la calculadora polaca.
 */
public class fyPrimitivas
{
   private static final String[] mat_FuncInc =
   {
      "pi", "hpi", "tpi", "c", "h", "e", "k", "q",
      "+", "-", "*", "/", "\\", "^", ">", "<", "=", "<=", ">=", "mod", "and", "or", "xor", "not",
      "exp", "log", "ln", "sq", "sqr", "sin", "cos", "tan", "atan", "acos", "asin", "inv",
      "abs", "int", "chs", "deg_rad", "rad_deg",
      "min", "max", "atan2", "r_p", "p_r",
      "rnd", "depth", "dup", "dup2", "drop", "swap"
   };

   public static final int
      UnresolvedCall = -3,
      Numeric_Cte = -2,
      Not_Primitive = -1,
      Cte_Pi = 0,
      Cte_HPi = 1,
      Cte_TPi = 2,
      Cte_c = 3,
      Cte_h = 4,
      Cte_e = 5,
      Cte_k = 6,
      Cte_q = 7,
      HastaCte = 7,

      Ope_Mas = 8,
      Ope_Menos = 9,
      Ope_Mult = 10,
      Ope_Div = 11,
      Ope_DivEnt = 12,
      Ope_Elev = 13,
      Ope_Mayor = 14,
      Ope_Menor = 15,
      Ope_Igual = 16,
      Ope_MenIg = 17,
      Ope_MayIg = 18,
      Ope_Mod = 19,
      Ope_And = 20,
      Ope_Or = 21,
      Ope_Xor = 22,
      HastaOpe = 22,

      Ope2_Not = 23,
      HastaOpe2 = 23,

      Fun_Exp = 24,
      Fun_Log = 25,
      Fun_Ln = 26,
      Fun_sq = 27,
      Fun_sqr = 28,
      Fun_sin = 29,
      Fun_cos = 30,
      Fun_tan = 31,
      Fun_atan = 32,
      Fun_acos = 33,
      Fun_asin = 34,
      Fun_inv = 35,
      Fun_abs = 36,
      Fun_int = 37,
      Fun_chs = 38,
      Fun_deg_rad = 39,
      Fun_rad_deg = 40,
      HastaFun = 40,

      Fun2_min = 41,
      Fun2_max = 42,
      Fun2_atan2 = 43,
      Fun2_r_p = 44,
      Fun2_p_r = 45,
      HastaFun2 = 40,

      Fun3_rnd = 46,
      Fun3_depth = 47,
      Fun3_dup = 48,
      Fun3_dup2 = 49,
      Fun3_drop = 50,
      Fun3_swap = 51,
      HastaFun3 = 51;

   public static final int
      Especial_MIN = 500,
      Espec_VarMIN = 500,
      Espec_Var_X = 500,         // represent variable x
      Espec_Var_Y = 501,
      Espec_Var_Z = 502,
      Espec_VarMAX = 598,
      Espec_ReturnCall = 599,
      Espec_ConstantBase = 600,  // to represent constants (e.g. 600 = constant 0, 601 = constant 1, etc)
      Espec_FuncArgBase = 700,   // start of a function with n arguments (700= 0, 701= 1 etc)
      Espec_FuncArgEND = 799,
      Espec_CallBase = 800;      // to represent call functions (e.g. 855 = jump to 55)

   public static final int
       EsValor = 0,
       EsVariable = 1,
       EsParametro = 2,
       EsConstante = 3,
       EsOperador = 4,
       EsFuncionInt = 5,
       EsFuncionExt = 6,
       EsInstruccion = 10;

   /**
   *    Retorna el i'ndice (id) de la primitiva
   */
   public static int indxPrimitiva (String atom)
   {
      for (int ii = 0; ii < mat_FuncInc.length; ii ++)
         if (atom.equals (mat_FuncInc[ii])) return ii;
      return Not_Primitive;
   }

   /**
   *    Mira si existe la primitiva con dicho id
   */
   public static boolean existsPrimitiva (int indx)
   {
      return (indx > Not_Primitive && indx < mat_FuncInc.length);
   }

   /**
   *    Retorna el string de la primitiva con dicho id
   */
   public static String strPrimitiva (int indx)
   {
      return (existsPrimitiva (indx)) ? mat_FuncInc[indx]: "func???";
   }
}