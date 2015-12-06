/*
package de.elxala.math.prob;
(c) Copyright 2002 Alejandro Xalabarder Aulet

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

/*
	16.07.2002 20:54	Escrito en java
*/

package de.elxala.math.prob;

/** Class that gives all the combinations of a set of N objects take in sets of n
  */
public class Combina {

	private int Melem=0, Ntoma=0;
	private boolean [] Mplas;

	public Combina () {
		start (1, 1);
	}

	public Combina (int M, int N) {
		start (M, N);
	}

     public int getMelements () {
     	return Melem;
     }

     public int getNtaken () {
     	return Ntoma;
     }

	public long Combinations (int M, int N) {
		double tot = 1.;

		//    m       m!
		//   C  = -----------
		//    n    n! (m-n)!
		//
		for (int mm = M; mm > (M - N); mm --) tot *= mm;
		for (int dd = 2; dd <= N;      dd ++) tot /= dd;
		return (long) tot;
	}

	public long Combinations () {
		return Combinations (Melem, Ntoma);
	}

	public void start (int M, int N)
	{
		// M,N = 10 4 => Mplas = [ 1111000000 ]
		Mplas = new boolean [M];

		Melem = M;
		Ntoma = N;

		for (int ii = 0; ii < Melem; ii ++)
			Mplas [ii] = (ii < Ntoma);
	}

	public boolean nextOne ()
	{
		int bb, nn;

		// en busca del blanco
		//  [ 1101000001 ]
		//         bb ^
		for (bb = Melem-1; bb >= 0 && Mplas[bb]; bb --);

		//  [ 1101000001 ]
		//          nn ^
		nn = bb + 1;

		// el primero de los blancos
		//  [ 1101000001 ]
		//     bb ^
		for (;bb > 0 && !Mplas[bb-1]; bb--);

		// FIN! se acabaron las combinaciones
		if (bb == 0) return false;

		// avanzar
		//  antes   [ 1101000001 ]
		//  despues [ 1100100001 ]
		//             bb ^
		Mplas[bb] = true;
		Mplas[bb-1] = false;

		// mover el bloque derecho de unos (si existe) atra's
		for (bb ++; nn < Melem && bb < nn; nn ++, bb++) {
			Mplas[bb] = true;
			Mplas[nn] = false;
		}
		return true;
	}

	/**
		Returns if the given element is in the current combination
	*/
	public boolean isElem (int elem) {
		if (elem < 0 || elem >= Melem) return false;
		return Mplas[elem];
	}

	/**
		Returns the set of objects in the current combination
	*/
	public Object [] currentCombination (Object [] allSet) {
		Object [] resul = new Object [Ntoma];
		int cc = 0;

		for (int ee = 0; ee < Melem && ee < allSet.length; ee ++) {
			if (Mplas[ee])
				resul[cc ++] = allSet[ee];
		}

		if (cc != Ntoma) {
			// Error!
			System.out.print ("de.elxala.math.prob.Combina ERROR bad combination!");
			System.out.println ("M = " + Melem + " N = " + Ntoma + " [ " + toString () + " ] ");
		}
		return resul;
	}

	/**
		Returns a string representing the current combination
	*/
	public String toString () {
		String ss = "";

		for (int pp = 0; pp < Melem; pp ++) {
			if (Mplas[pp])
				ss += "1";
			else ss += "0";
		}
		return ss;
	}
/*
     public static void main (String [] args) {
     	Combina eso = new Combina();
     	int cc = 0;

     	eso.start (10, 4);
     	System.out.println ("Combinations of " + eso.getMelements () + " taken " + eso.getNtaken () + " by " +  eso.getNtaken ());
     	System.out.println ("amount = " + eso.Combinations ());
     	System.out.println ("-------------------------------");
     	do
     		System.out.println ("Combination  [ " + eso.toString () + " ]  (" + (cc++) + ")");
     	while (eso.nextOne ());


     	eso.start (49, 6);
     	System.out.println ("Combinations of " + eso.getMelements () + " taken " + eso.getNtaken () + " by " +  eso.getNtaken ());
     	System.out.println ("amount = " + eso.Combinations ());
     	System.out.println ("-------------------------------");
     }
*/
};