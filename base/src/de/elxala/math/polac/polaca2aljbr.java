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

//   -------------------
//   xala: 09.06.1 18:46
//   -------------------
/*

          FALTA (09.06.1 18:53)

          Algun me'todo que, en caso de no ser posible la conversio'n, retorne motivo:

               falta de operadores (pila no vaci'a al final)
               falta operandos (pila vaci'a antes (err_pila))
               operacio'n no va'lida (drop, swap, etc)
*/

import de.elxala.langutil.*;
import java.util.*;

/**
 *   Contiene el algoritmo para pasar de una expresio'n polaca a algebraica
 *   (ver PolacaToAljbr)
 */
public class polaca2aljbr
{
   private Stack pilaStr = new Stack ();
   private boolean err_pila = false;

   private String Aplaude (String ss, boolean esFunc)
   {
      // Esta rutina puede ahorrar un 50% de los pare'ntesis (35% de la long. del string)
      // ejemplo:
      // sin rutina = ((1)+((cos(((x)*(2))))+((cos(((x)*(4))))+(cos(((x)*(6)))))))
      // con rutina = 1+((cos(x*2))+((cos(x*4))+(cos(x*6))))

      if (!esFunc && ss.length () <= 1) return ss;        // sin tocar
      if (ss.charAt(0) != '(') return ("(" + ss + ")");   // tocado

      // si el primer pare'ntesis  se cierra al final
      // entonces no es necesario "aplaudirlo"
      // Ej1: ((x+y)-(3*cos(x)))  no es necesario
      // Ej2: (x+2)-(x+4)         SI ES NECESARIO !!!
      int kk=0, kopen=0;
      do
      {
         if (ss.charAt(kk) == '(') kopen++; // cuenta los que se abren
         if (ss.charAt(kk) == ')') kopen--; // cuenta los que se cierran
      }
      while ((++kk < ss.length ()) && (kopen > 0)); // condition must be in this order !!!

      if (kk < ss.length ())
         return "(" + ss + ")";   // tocado
      else return ss;               // sin tocar
   }

   private String PopSpecial ()
   {
      err_pila = err_pila || pilaStr.empty ();
      if (!err_pila)
         return Aplaude((String) pilaStr.pop (), false);
      else return "";
   }

   public static boolean convertExpresion (String sPolaca, Cadena sAljbr)
   {
      polaca2aljbr notas = new polaca2aljbr ();

      return notas.convert (sPolaca, sAljbr);
   }

   public boolean convert (String sPolaco, Cadena sAljbr)
   {
      Cadena sEle = new Cadena ();
      Cadena sToa = new Cadena (sPolaco);
      sToa.setSepar (" ,;");   //  FALTA !!!! + chr(13) + chr(10) FALTA !!!! FALTA !!!! FALTA !!!!

      err_pila = false;
      // CLEAR PILA : increi'ble que no exista el clear !!
      pilaStr = new Stack ();

      while (!err_pila && sToa.getToken ())
      {
         sEle.setStr (sToa.lastToken());

         if (sEle.length () == 0) continue;
         switch (utilParse.QueEs(sEle))
         {
            case fyPrimitivas.EsValor:
                //System.out.println ("el tema [" + sEle.o_str + "] es valor");
                pilaStr.push (sEle.o_str);
                break;

            case fyPrimitivas.EsConstante:
            case fyPrimitivas.EsParametro:
            case fyPrimitivas.EsVariable:
                //System.out.println ("el tema [" + sEle.o_str + "] es cte param o variable");
                pilaStr.push (sEle.o_str);
                break;

            case fyPrimitivas.EsFuncionExt:
            case fyPrimitivas.EsFuncionInt:
            case fyPrimitivas.EsOperador:
                //System.out.println ("el tema [" + sEle.o_str + "] es operacion o funcion int/ext");
                OperaS(sEle.o_str);
                break;

            default:
                pilaStr.push (sEle.o_str + "?");
         }
      }
      if (err_pila || pilaStr.empty ()) return false;  // comprobar pila

      sAljbr.setStr ((String) pilaStr.pop ());            // valor de pila es la solucio'n
      return (pilaStr.empty ());
   }

   private void OperaS(String sOper)
   {
      // ALIAS
      if (sOper.equals ("xY")) sOper = "^";
      if (sOper.equals ("+-")) sOper = "chs";

      int npar = -1;
      boolean opemed = false;

      Cadena cOper = new Cadena (sOper);

      if (cOper.indexOf ("+-*^/\\><=", 0) >= 0)
      {
         String V1 = new String (PopSpecial ());
         String V2 = new String (PopSpecial ());
         pilaStr.push (V2 + sOper + V1);
         return;
      }

      if (sOper.equals ("mod") || sOper.equals ("atan2"))
      {
         String V1 = new String (PopSpecial ());
         String V2 = new String (PopSpecial ());
         pilaStr.push (sOper + "(" + V2 + "," + V1 + ")");
         return;
      }

      if (sOper.equals ("chs"))          pilaStr.push ("-" + PopSpecial ());
      else if (sOper.equals ("x"))       pilaStr.push ("x");
      else if (sOper.equals ("y"))       pilaStr.push ("y");
      else if (sOper.equals ("sq"))      pilaStr.push (PopSpecial () + "^2");
      else if (sOper.equals ("inv"))     pilaStr.push ("1/" + PopSpecial ());
      else
      {
         // Cualquier funcio'n
         if (!pilaStr.empty ())
              pilaStr.push (sOper + Aplaude((String) pilaStr.pop (), true));
         else err_pila = true;
      }
   }

   public static void main (String [] args)
   {
      String exp_polaca = "";

      for (int ii = 0; ii < args.length; ii ++)
         exp_polaca += args[ii] + " ";

      if (exp_polaca.length () == 0)
         exp_polaca = "5 3 8 / - 32 54 + 70 16 + - / cos 3 4 * 8 + sin -";

      Cadena sout = new Cadena ();

      if (! convertExpresion (exp_polaca, sout))
         sout.setStr ("** Unable to convert!!");

      System.out.println ("formato polaca     = [" + exp_polaca + "]");
      System.out.println ("formato algebraico = [" + sout.o_str + "]");
   }
}
