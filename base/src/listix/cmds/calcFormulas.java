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


/**
   12.08.2009 23:59

   Flipo pepinos:

      Notar que a veces se traga atan2(2,2) sin problemas

               polaca   = [ 2 2 atan2  ]
               0.7853981633974483original = [ atan2(2,2)  ]
               polaca   = [ 2 2 atan2  ]
               0.7853981633974483original = [ atan2(2)  ]

      Y a veces se flippa!!!! y da error!!!!

               original = [ atan2(2,2)  ]
               polaca   = [ 2 2 atan2  ]
               0.7853981633974483original = [ atan(2,1)  ]
               polaca   = [ 2 1 atan  ]
               ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,1)], stack is not empty at end of computation!
               0.7853981633974483original = [ atan(2,2)  ]
               polaca   = [ 2 2 atan  ]
               ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,2)], stack is not empty at end of computation!


riginal = [ sin (x) / x  ]
polaca   = [ x sin x /  ]
0.8414709848078965original = [ atan(2)  ]
polaca   = [ 2 atan  ]
1.1071487177940904original = [ atan(2,s)  ]
polaca   = [ 2 s atan  ]
ERROR(4) in listix : formatIsListixFormat : the eva variable [s] not found!
0.0original = [ atan(2,1)  ]
polaca   = [ 2 1 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,1)], stack is not empty at end of computation!
0.7853981633974483original = [ atan(2,2)  ]
polaca   = [ 2 2 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,2)], stack is not empty at end of computation!
1.1071487177940904original = [ atan(2,22)  ]
polaca   = [ 2 22 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,22)], stack is not empty at end of computation!
1.5253730473733196original = [ sin(22)  ]
polaca   = [ 22 sin  ]
-0.008851309290403876original = [ sin(22)/x  ]
polaca   = [ 22 sin x /  ]
-0.008851309290403876original = [ 1 and 2  ]
polaca   = [ 1 and 2  ]
ERROR(4) in listix_command : calculaFormula : bad formula [1 and 2], stack is not empty at end of computation!
1.0original = [ 1 >= 2  ]
polaca   = [ 1 2 >=  ]
0.0original = [ 1<= 2  ]
polaca   = [ 1 2 <=  ]
1.0original = [ 1< = 2  ]
polaca   = [ 1 < 2 =  ]
ERROR(4) in listix_command : calculaFormula : bad formula [1< = 2], stack is not empty at end of computation!
1.0original = [ 1<= 2  ]
polaca   = [ 1 2 <=  ]
1.0original = [ atan2(x,1)  ]
polaca   = [ x 1 atan2  ]
0.7853981633974483original = [ 2  ]
polaca   = [ 2  ]
2.0original = [ 2 = 3  ]
polaca   = [ 2 3 =  ]
0.0original = [ sin(2)  ]
polaca   = [ 2 sin  ]
0.9092974268256817original = [ sin(2,2)  ]
polaca   = [ 2 2 sin  ]
ERROR(4) in listix_command : calculaFormula : bad formula [sin(2,2)], stack is not empty at end of computation!
0.9092974268256817original = [ atan(2,2)  ]
polaca   = [ 2 2 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,2)], stack is not empty at end of computation!
1.1071487177940904original = [ atan(x,2)  ]
polaca   = [ x 2 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(x,2)], stack is not empty at end of computation!
1.1071487177940904original = [ atan(x)  ]
polaca   = [ x atan  ]
0.7853981633974483original = [ atan(x,2)  ]
polaca   = [ x 2 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(x,2)], stack is not empty at end of computation!
1.1071487177940904original = [ atan2(x,2)  ]
polaca   = [ x 2 atan2  ]
0.4636476090008061original = [ atan2(1,2)  ]
polaca   = [ 1 2 atan2  ]
0.4636476090008061original = [ atan2(2,2)  ]
polaca   = [ 2 2 atan2  ]
0.7853981633974483original = [ atan2(2,2)  ]
polaca   = [ 2 2 atan2  ]
0.7853981633974483original = [ atan2(2)  ]
polaca   = [ 2 atan2  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan2(2)], stack is not empty at end of computation!
1.0
original = [ atan2(2,2)  ]
polaca   = [ 2 2 atan2  ]
0.7853981633974483original = [ atan(2,1)  ]
polaca   = [ 2 1 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,1)], stack is not empty at end of computation!
0.7853981633974483original = [ atan(2,2)  ]
polaca   = [ 2 2 atan  ]
ERROR(4) in listix_command : calculaFormula : bad formula [atan(2,2)], stack is not empty at end of computation!
1.1071487177940904original = [ sin(x)  ]
polaca   = [ x sin  ]
0.8414709848078965original = [ sin(2)  ]
polaca   = [ 2 sin  ]
0.9092974268256817original = [ sin(1)  ]
polaca   = [ 1 sin  ]
0.8414709848078965

*/