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
import de.elxala.zServices.logger;

public class calcFormulas
{
   public static void badFormulaError (logger log, String context, String formula)
   {
      // this error is important since it is very easy to make a mistake
      // writing a formula that contains a comma, for instance
      // Wrong would be:
      //    SET VAR, var1, =, min(price, offer)
      // instead of :
      //    SET VAR, var1, =, "min(price, offer)"
      //
      log.err (context, "bad formula [" + formula + "], bad number of arguments!");
   }

   private static java.text.DecimalFormat decFormat1 = new java.text.DecimalFormat ("0.################");

   //
   public static String calculaFormula (listix that, String expresionAljbr)
   {
      String formulae = expresionAljbr.trim ();
      String expre    = "";
      if (formulae.length () == 0)
         return "";

      double retDou = calcFormula (that, expresionAljbr);

      // format to 0.########### but always with '.' (not comma!)
      String str = decFormat1.format(retDou);
      str = str.replaceAll (",", ".");       // avoid localization (, instead of .)

      return str;
   }

   //
   public static double calcFormula (listix that, String expresionAljbr)
   {
      String formulae = expresionAljbr.trim ();
      String expre    = "";
      if (formulae.length () == 0)
         return 0.;

      aljbr2polaca alg2pol = new aljbr2polaca ();
      String [] partes = alg2pol.convertToArray (formulae);

      for (int ii = 0; ii < partes.length; ii ++)
      {
         String atom = partes[ii];

         if (utilParse.isNumber (atom) || fyPrimitivas.indxPrimitiva (atom) != -1)
         {
            expre += " " + partes[ii];
         }
         else
         {
            Eva [] ret = new Eva [] { new Eva () };
            if (that.formatIsValue(atom, ret) || that.formatIsListixFormat(atom, ret))
            {
               Eva sol = that.solveLsxFormatAsEva (atom);
               expre += " " + stdlib.atof (sol.getValue ());
            }
            else expre += " " + partes[ii];
         }
      }

//      System.out.println ("");
//      System.out.println ("original1 = [ " + expresionAljbr + " ]");
//      System.out.println ("original2 = [ " + formulae + " ]");
//      System.out.println ("polaca    = [ " + expre + " ]");
//      System.out.println ("polaca2   = [ " + alg2pol.convert (formulae) + " ]");

      Compilator compi = new Compilator ();
      Ejecutable ejecut = new Ejecutable ();

      compi.cl (ejecut, "basica", ".", expre, Compilator.VARLIST_X);

      PilaCab pil = new PilaCab ();
      pil.push (0);
      ejecut.run (pil);

      if (pil.depth () != 1)
         that.log().err ("calculaFormula", "bad formula [" + expresionAljbr + "], stack is not empty at end of computation!");

      return pil.pop ();
   }
}
