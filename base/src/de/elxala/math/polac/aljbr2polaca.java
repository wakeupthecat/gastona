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

import java.util.*;

/**
 *   Contiene el algoritmo para pasar de una expresio'n algebraica a polaca
 *   (ver AljbrToPolaca)
 */
public class aljbr2polaca
{
   private Stack pilaStr = new Stack ();

   // variable necesaria para llamar recursivamente a RecurrAlToPol
   // debido a que Java no admite argumentos de tipo int por referencia!.
   private int recorro = 0;

   /* Rutinas auxiliares para AljbrToPolaco
   */
   private boolean EsTexto (char aa)
   {
      // if ( InStr("+-/\*^()", Left(aa, 1))) return false;
      return ( ((aa >= 'a') && (aa <= 'z')) ||
               ((aa >= 'A') && (aa <= 'Z')) ||
               ((aa >= '0') && (aa <= '9')) ||
               (aa == '.') ||
               (aa == 249) || (aa == 212) || // enye minu'scula y mayu'scula
               (aa == 237) || (aa == 200) || // ce trancada min y may
               (aa == 95));  // guio'n bajo
   }

   private int NivelOperador (String sOper)
   {
      if (sOper.length () == 0) return 0; // just to be sure...
      switch (sOper.charAt(0))
      {
         case '+':
         case '-': return 0;
         case '*':
         case '/': return 1;
         case '^': return 3;
         default:
            if (sOper.equals ("chs")) return 2;
      }
      return 0;
   }

   public static String convertExpresion (String sAljbr)
   {
      aljbr2polaca notas = new aljbr2polaca ();

      return notas.convert (sAljbr);
   }

   public String convert (String sAljbr)
   {
      // CLEAR PILA : increi'ble que no exista el clear !!
      while (!pilaStr.empty ()) pilaStr.pop ();

      sAljbr.toLowerCase ();
      sAljbr.trim ();

      recorro = 0;
      RecurrAlToPol(sAljbr);

      String ss = "";

      while (!pilaStr.empty ())
         ss = pilaStr.pop () + " " + ss;

      return ss;
   }

   public String [] convertToArray (String sAljbr)
   {
      // CLEAR PILA : increi'ble que no exista el clear !!
      while (!pilaStr.empty ()) pilaStr.pop ();

      sAljbr.toLowerCase ();
      sAljbr.trim ();

      recorro = 0;
      RecurrAlToPol(sAljbr);

      String [] reto = new String [pilaStr.size ()];
      for (int ii = pilaStr.size ()-1; ii >= 0 ; ii --)
      {
         reto[ii] = "" + pilaStr.pop ();
      }

      return reto;
   }

   private void PonOperadorSiHay (Stack pilaOper)
   {
      while (!pilaOper.empty ())
         pilaStr.push (pilaOper.pop ());
   }

   private void GuardaOperador(Stack pilaOper, String sOper)
   {
      int l1, l2;

      while (!pilaOper.empty ())
      { // ' hay operadores
         String yo = "" + pilaOper.peek ();

         l1 = NivelOperador(yo);
         l2 = NivelOperador(sOper);
         if (l2 > l1) break;
         pilaStr.push (pilaOper.pop ()); // ' hacer la operacio'n anterior
      }
      pilaOper.push (sOper);
   }

   private void RecurrAlToPol (String sAljbr)
   {
      int Dedo;
      boolean EraFuncion=false, EraInicioAlgo=true;

      Stack pilaOper = new Stack ();

      // Workaround due to a parser problem "int (x)" was not right parsed
      // because of the space. Now we replace " (" with "("
      do {
         int le = sAljbr.length();
         sAljbr = sAljbr.replaceAll("\\s\\(", "\\("); //  \s  A whitespace character: [ \t\n\x0B\f\r]
         if (le == sAljbr.length()) break;
      } while (true);

      while (recorro < sAljbr.length ())
      {
         Dedo = recorro;

         // saltar nombres
         //
         while ( (Dedo < sAljbr.length ()) &&
                 (EsTexto(sAljbr.charAt (Dedo))) )  Dedo++;

         if (Dedo - recorro > 0)
         {
            //
            // es : Nombre de funcion, variable, nu'mero etc
            //
            pilaStr.push (sAljbr.substring (recorro, Dedo));
            EraFuncion = (Dedo < sAljbr.length ()) && (sAljbr.charAt(Dedo) == '(');
            recorro = Dedo;
            EraInicioAlgo = false;
         }
         else {
            //
            // es : Separador, operador o parentesis
            //
            switch (sAljbr.charAt(Dedo))
            {
                case ' ':
                     recorro++;
                     EraInicioAlgo = false;
                     break;
                case ',':
                case ';':
                     recorro ++;
                     EraInicioAlgo = true;
                     break;
                case '(':
                     // 'EraInicioAlgo = True
                     if (EraFuncion)
                     {
                          String sFunc;

                          sFunc = "" + pilaStr.pop ();
                          recorro++;
                          RecurrAlToPol(sAljbr);

                          pilaStr.push(sFunc);
                          EraFuncion = false;
                     }
                     else
                     {
                          recorro ++;
                          RecurrAlToPol(sAljbr);
                     }
                     EraInicioAlgo = false; // '!! puesto que ya va a RecurrAltoPol
                     break;

                case ')':
                     EraInicioAlgo = false;
                     PonOperadorSiHay(pilaOper);
                     recorro ++;
                     // >>>> NOT BREAK BUT RETURN !!! >>>>
                     return ;

                case '<':
                case '>':
                     String sOper = "" + sAljbr.charAt(recorro);
                     recorro ++;
                     if ((sAljbr.length () > recorro) && sAljbr.charAt(recorro) == '=')
                     {
                        sOper += "=";
                        recorro ++;
                     }
                     GuardaOperador(pilaOper, "" + sOper);
                     EraInicioAlgo = true;
                     break;

                default:
                     // ' Cualquier otro operador
                     char cOper = sAljbr.charAt(recorro);
                     if (EraInicioAlgo && (cOper == '-' || cOper == '+'))
                     {
                         // ' caso muy especial nu'mero negativo o expresio'n negativa
                         if (cOper == '-') GuardaOperador(pilaOper, "chs");
                     }
                     else
                     {
                        GuardaOperador(pilaOper, "" + cOper);
                     }
                     recorro ++;
                     EraInicioAlgo = true;
                     break;
            }
         }
      }
      PonOperadorSiHay(pilaOper);
   }


   public static void main (String [] args)
   {
      String jal = "";
      String peich = "nada";

      for (int ii = 0; ii < args.length; ii ++)
         jal += args[ii] + " ";

      if (jal.length () == 0)
         jal = "cos((5-3/8)/((a+b)-(c+d)))-sin(3*4+8)";

      peich = convertExpresion (jal);

      System.out.println ("original = [ " + jal + " ]");
      System.out.println ("polaca   = [ " + peich + " ]");
   }
}

