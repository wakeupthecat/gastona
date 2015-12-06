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


/**     Pila con parte fija (cabecera de pila de 0 a Cero).
*/
public class PilaCab
{
   private static final int MAX_STACK = 50;

   public double [] Physic;

   public int     Tope, Cero;
   public boolean err_petada, err_vacia;


   public PilaCab ()
   {
      Physic = new double [MAX_STACK];
      Tope = Cero = 0;
      err_petada = err_vacia = false;
   }

   /** Limpia la pila y la cabecera
   */
   public void clear ()
   {
      Tope = Cero = 0;
      err_vacia = err_petada = false;
   }

   /** Limpia so'lo la pila y deja la cabecera
   */
   public void reset ()
   {
      Tope = Cero;
      err_vacia = err_petada = false;
   }

   public boolean vacia  ()
   {
      return (err_vacia || Tope == Cero);
   }

   public boolean Petada ()
   {
      return err_petada;
   }

   public boolean ok ()
   {
      return (! err_petada && ! err_vacia);
   }

   /** pop de la pila
   */
   public double pop ()
   {
      // if (Tope <= Cero)
      //    System.err.println ("de.elxala.math.polac.PilaCab : Error atempt to pop an empty stack!");
      // else System.out.println ("popo (" + Physic[Tope-1] + ")");

      if (err_petada || err_vacia) return 1.;
      if (Tope > Cero)
         return Physic[--Tope];

      err_vacia = true;
      return 1.;
   }

   /** push en la pila
   */
   public void push (double num)
   {
      // System.out.println ("pusho (" + num + ")");
      if (err_petada || err_vacia) return;
      if (Tope < MAX_STACK)
         Physic[Tope++] = num;
      else
         err_petada = true;
   }

   /** evalua el elemento a sacar (pop) sin sacarlo
   */
   public double eval ()
   {
      if (err_petada || err_vacia) return 1.;
      if (Tope > Cero)
         return Physic[Tope-1];

      err_vacia = true;
      return 1.;
   }

   /** push en cabecera. Adema's resetea la pila incondicionalmente.
   *   retorna la ubicacio'n en la pila del elemento (para posterior referencia)
   */
   public int pushCab (double num)
   {
      if (err_petada || err_vacia) return 0;
      if (Cero < MAX_STACK)
      {
         Physic[Cero++] = num;
         Tope = Cero;   // Reset!
      }
      else err_petada = true;
      return (Cero-1);
   }

   public int depth ()
   {
      return (Tope - Cero);
   }
}
