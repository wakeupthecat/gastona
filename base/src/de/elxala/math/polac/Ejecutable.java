/*
packagede.elxala.math.polac;
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


import java.util.Stack;
import de.elxala.zServices.logger;

/** Clase para hacer operaciones en notacio'n polaca inversa
*/
public class Ejecutable extends fyPrimitivas
{
   private static logger log = new logger (null, "elxala.math.polac.Ejecutable", null);

   // public boolean TRACIX = false;
   public int [] addrFunc   = new int [0];  // direcciones donde empiezan las distintas funciones
   public int [] ProgramExe = new int [0];
   public double [] constantes = new double [0];
   public Stack variableStack = new Stack ();

   private int PC = 0;
   private PilaCab pPil = null;
   private PilaCab defPil = null;

   private double fact_log10 = 1 / Math.log(10);

   public void clear ()
   {
      constantes = new double [0];
      ProgramExe = new int [0];
   }


   /**
      TODO TODO (nvar !!)
      checks that is a function of 'nvar' variables
   */
   public boolean check (int indxFunc)
   {
      if (! checkCyclicFunction (indxFunc)) return false;

      PilaCab pil = new PilaCab ();

      pil.push (0.1);
      run (indxFunc, pil);

      return pil.ok () && (pil.depth () == 1);
   }

   public boolean validExe ()
   {
      return ProgramExe.length > 0;
   }

   public void setProgram (double [] constdata, int [] code, int [] addFunc)
   {
      constantes = constdata;
      ProgramExe = code;
      addrFunc = addFunc;
   }

   public void assignStack (PilaCab pila)
   {
      defPil = pila;
   }

   public void run (int funIndx)
   {
      run (funIndx, null);
   }

   public void run (PilaCab pila)
   {
      run (0, pila);
   }

   public void run (int funIndx, PilaCab pila)
   {
      if (!validExe () || funIndx < 0 || funIndx > addrFunc.length) return;

      if (pila == null && defPil == null)
      {
         log.severe("run", "call to run but no stack provided");
         return;
      }
      pPil = (pila == null) ? defPil: pila;

      PC = addrFunc[funIndx];
      variableStack = new Stack ();

      if (log.isDebugging(2))
         log.dbg (2, "run", "Ejecutabe -> " +  toString ());
      runAddress (PC);
   }

   public double eval (double x)
   {
      PilaCab myPil = new PilaCab ();
      myPil.push (x);

      run (myPil);

      return myPil.pop ();
   }

   public double eval (int funIndx, double x)
   {
      PilaCab myPil = new PilaCab ();
      myPil.push (x);

      run (funIndx, myPil);

      return myPil.pop ();
   }

   private void runAddress (int addr)
   {
      int OP = ProgramExe[addr];
      if (OP < Espec_FuncArgBase || OP > Espec_FuncArgEND)
      {
         log.err ("runAddress", "math formula: invalid start of compiled function " + OP + " in address " + addr);
         return;
      }

      // push variables of this function from the stack
      // note that variables are set in reverse order in  variableStack !
      int Nvar = OP - Espec_FuncArgBase;
      for (int vv = 0; vv < Nvar; vv ++)
         variableStack.push (new double [] { pPil.pop () });

      // get valueX to accelerate
      double valueX = 0.;
      if (variableStack.size () > 0)
         valueX = ((double []) variableStack.get (variableStack.size () - 1)) [0];

      // save PC
      int oldPC = PC;
      PC = addr + 1;
      boolean allOk = true;

      // BIG LOOP : execute step by step
      //
      while (pPil.ok () && PC < ProgramExe.length)   // and allOk
      {
         OP = ProgramExe [PC ++];

         if (log.isDebugging(8))
            log.dbg (8, "runAddress", "op:" +  OP + ":");

         allOk = true;

         // ------------------------------------- begin big switch
         switch (OP)
         {
            // exit current function
            //
            case Espec_ReturnCall:
               PC = ProgramExe.length;
               break;

            // constants (treat here only the 5 first ones)
            //
            case Espec_ConstantBase + 0:
            case Espec_ConstantBase + 1:
            case Espec_ConstantBase + 2:
            case Espec_ConstantBase + 3:
            case Espec_ConstantBase + 4:
            case Espec_ConstantBase + 5:
               pPil.push (constantes [OP - Espec_ConstantBase]);
               break;

            // variables (treat here only the 3 first ones)
            //
            case Espec_VarMIN + 0:
               pPil.push (valueX);  // small acceleration
               break;
            case Espec_VarMIN + 1:
            case Espec_VarMIN + 2:
               int nvar = variableStack.size () - 1 - (OP - Espec_VarMIN);
               if (nvar < 0 || nvar > variableStack.size ())
               {
                  log.err ("runAddress", "math formula: invalid access to variable " + OP + " number of variables is " + variableStack.size ());
                  allOk = false;
               }
               else
               {
                  pPil.push (((double []) variableStack.get (nvar)) [0]);
               }
               break;

            // primitive constants
            //
            case Cte_Pi:   pPil.push (Math.PI);          break;
            case Cte_HPi:  pPil.push (Math.PI / 2);      break;
            case Cte_TPi:  pPil.push (Math.PI * 2);      break;
            case Cte_c:    pPil.push (30000000000.);      break;
            case Cte_h:    pPil.push (6.62E-34);         break;
            case Cte_e:    pPil.push (2.71828182845905); break;
            case Cte_k:    pPil.push (1.38E-23);         break;
            case Cte_q:    pPil.push (1.6E-19);          break;

            // operators I
            //
            case Ope_Mas:     pPil.push (pPil.pop () + pPil.pop ());    break;
            case Ope_Menos:   pPil.push (- pPil.pop () + pPil.pop ());    break;
            case Ope_Mult:    pPil.push (pPil.pop () * pPil.pop ());    break;
            case Ope_Div:
               {
                  double op2 = pPil.pop ();
                  pPil.push (pPil.pop () / op2);
               }
               break;
            case Ope_DivEnt:
               {
                  double op2 = pPil.pop ();
                  pPil.push (0.0 + ((int) (pPil.pop () / op2)));
               }
               break;
            case Ope_Elev:
               {
                  double op2 = pPil.pop ();
                  pPil.push (Math.pow (pPil.pop (), op2));
               }
               break;
            case Ope_Mayor:     pPil.push ((pPil.pop () < pPil.pop ()) ? 1.0: 0.0);    break;
            case Ope_Menor:     pPil.push ((pPil.pop () > pPil.pop ()) ? 1.0: 0.0);    break;
            case Ope_Igual:     pPil.push ((pPil.pop () == pPil.pop ()) ? 1.0: 0.0);   break;
            case Ope_MenIg:     pPil.push ((pPil.pop () >= pPil.pop ()) ? 1.0: 0.0);   break;
            case Ope_MayIg:     pPil.push ((pPil.pop () <= pPil.pop ()) ? 1.0: 0.0);   break;
            case Ope_Mod:
               {
                  double op2 = pPil.pop ();
                  pPil.push (pPil.pop () % op2);
               }
               break;
            case Ope_And:  pPil.push (((pPil.pop () != 0.) && (pPil.pop () != 0.)) ? 1.0: 0.0);    break;
            case Ope_Or:   pPil.push (((pPil.pop () != 0.) || (pPil.pop () != 0.)) ? 1.0: 0.0);    break;
            case Ope_Xor:  pPil.push (((pPil.pop () != 0.) ^ (pPil.pop () != 0.)) ? 1.0: 0.0);     break;

            case Ope2_Not: pPil.push ((pPil.pop () == 0.0) ? 1.0: 0.0);    break;

            // functions
            //
            case Fun_Exp:  pPil.push (Math.exp (pPil.pop ()));             break;
            case Fun_Log:  pPil.push (Math.log(pPil.pop ()) * fact_log10); break;
            case Fun_Ln:   pPil.push (Math.log(pPil.pop ()));              break;
            case Fun_sq:
               {
                  double op1 = pPil.pop ();
                  pPil.push (op1 * op1);
               }
               break;
            case Fun_sqr:  pPil.push (Math.sqrt (pPil.pop ()));             break;
            case Fun_sin:  pPil.push (Math.sin  (pPil.pop ()));              break;
            case Fun_cos:  pPil.push (Math.cos  (pPil.pop ()));              break;
            case Fun_tan:  pPil.push (Math.tan  (pPil.pop ()));              break;
            case Fun_atan: pPil.push (Math.atan (pPil.pop ()));             break;
            case Fun_acos: pPil.push (Math.acos (pPil.pop ()));             break;
            case Fun_asin: pPil.push (Math.asin (pPil.pop ()));             break;
            case Fun_inv:  pPil.push (1 / pPil.pop ());                    break;
            case Fun_abs:  pPil.push (Math.abs (pPil.pop ()));             break;

            case Fun_int:  pPil.push (0.0 + (int) pPil.pop ());            break;
            case Fun_chs:  pPil.push (-1.0 * pPil.pop ());                 break;

            case Fun_deg_rad: pPil.push (Math.PI * pPil.pop () / 180.);    break;
            case Fun_rad_deg: pPil.push (180. * pPil.pop () / Math.PI);    break;

            case Fun2_min:
               {
                  double op2 = pPil.pop ();
                  double op1 = pPil.pop ();
                  pPil.push ((op1 < op2) ? op1: op2);
               }
               break;

            case Fun2_max:
               {
                  double op2 = pPil.pop ();
                  double op1 = pPil.pop ();
                  pPil.push ((op1 > op2) ? op1: op2);
               }
               break;

            case Fun2_atan2:
               {
                  double op2 = pPil.pop ();
                  pPil.push (Math.atan2 (pPil.pop (), op2));
               }
               break;

            case Fun2_r_p:
               {
                  double op2 = pPil.pop ();
                  double op1 = pPil.pop ();
                  pPil.push (Math.sqrt (op1 * op1 + op2 * op2));    // radio
                  pPil.push (Math.atan2 (op1, op2));                // phase
               }
               break;

            case Fun2_p_r:
               {
                  double op2 = pPil.pop ();
                  double op1 = pPil.pop ();
                  pPil.push (op1 * Math.cos (op2));      // x
                  pPil.push (op1 * Math.sin (op2));      // y
               }
               break;

            case Fun3_rnd:    pPil.push (Math.random ());          break;
            case Fun3_depth:  pPil.push ((double) pPil.depth ());  break;
            case Fun3_dup:    pPil.push (pPil.eval ());            break;
            case Fun3_dup2:
              {
                  double op2 = pPil.pop ();
                  double op1 = pPil.pop ();

                  pPil.push (op1);    // retornar par
                  pPil.push (op2);

                  pPil.push (op1);    // nuevo par
                  pPil.push (op2);
               }
               break;

            case Fun3_drop:   pPil.pop ();            break;
            case Fun3_swap:
               {
                  double op2 = pPil.pop ();
                  double op1 = pPil.pop ();

                  pPil.push (op2);
                  pPil.push (op1);
               }
               break;

            default:
               allOk = otherOperation (OP);
               break;
         }
         // ------------------------------------- end big switch

         if (log.isDebugging(8))
         {
            boolean inOneLine = log.isDebugging(8) && !log.isDebugging(9);
            if (! inOneLine )
               log.dbg (8, "runAddress", "Stack (size " + pPil.Tope + ") :");

            String stackLine = "";
            for (int ii = 0; ii < pPil.Tope; ii ++)
            {
               stackLine += "[" + pPil.Physic [ii] + "] ";
               log.dbg (9, "runAddress", ii + ") [" + pPil.Physic [ii] + "]");
            }
            if (inOneLine)
               log.dbg (8, "runAddress", "Stack (" + pPil.Tope + ") : " + stackLine + "]");
         }
      }

      // clean variable's stack
      for (int vv = 0; vv < Nvar; vv ++)
         variableStack.pop ();

      // restore Program counter
      PC = oldPC;
   }

   private boolean otherOperation (int OP)
   {
      // NOTE!: Espec_CallBase > Espec_ValueBase > Espec_VarBase
      //
      if (OP >= Espec_CallBase)
      {
         if ((OP - Espec_CallBase) < 0 || (OP - Espec_CallBase) > ProgramExe.length)
         {
            log.err ("otherOperation", "math formula: invalid call operation " + OP);
            return false;
         }
         // call function: p.e. la OP 812 make a goto(call) 12
         // save PC -> set new PC -> run -> restore PC
         runAddress (OP - Espec_CallBase);
         return true;
      }

      if (OP >= Espec_ConstantBase)
      {
         if (OP - Espec_ConstantBase >= constantes.length)
         {
            log.err ("otherOperation", "math formula: invalid access to constant in operation " + OP);
            return false;
         }
         pPil.push (constantes [OP - Espec_ConstantBase]);
         return true;
      }

      if (OP >= Espec_VarMIN && OP <= Espec_VarMAX)
      {
         int nvar = variableStack.size () - 1 - (OP - Espec_VarMIN);
         if (nvar < 0 || nvar > variableStack.size ())
         {
            log.err ("otherOperation", "math formula: invalid access to variable " + OP + " number of variables is " + variableStack.size ());
            return false;
         }
         pPil.push (((double []) variableStack.get (nvar)) [0]);
         return true;
      }

      log.err ("otherOperation", "math formula: invalid operation " + OP);
      return false;
   }


   /*
      NOTE: call this function only with code linked!!!
         (calls are 800 + address while before linking calls are 800 + funtion index)
   */
   private boolean checkCyclicCall (java.util.List addForbid, int add)
   {
      if (add < 0 || add > ProgramExe.length)
      {
         log.err ("checkCyclicCall", "math formula: bad cyclus add = " + add);
         return false;
      }

      do
      {
         int instr = ProgramExe[add ++];
         if (instr == Espec_ReturnCall) return true;  // end of Call
         if (instr < Espec_CallBase) continue; // another operation

         // it is a call
         int called = instr - Espec_CallBase;

         // ckeck that is not a verbotene Adresse
         for (int vv = 0; vv < addForbid.size (); vv ++)
         {
            int prohib = ((int []) addForbid.get (vv))[0];
            if (called == prohib)
            {
               log.err ("checkCyclicCall", "math formula: call to " + called + "forbidden at address " + (add-1));
               return false;
            }
         }

         // ok not called, go through it (recursive call to checkCyclicCall!)
         //
         addForbid.add (new int [] { called });
         if (! checkCyclicCall (addForbid, called)) return false;
         addForbid.remove (addForbid.size ()-1);
      }
      while (add < ProgramExe.length);

      return true;
   }

   /**
      NOTE: call this function only with code linked!!!
         (calls are 800 + address while before linking calls are 800 + funtion index)
   */
   public boolean checkCyclicFunction (int indxFunction)
   {
      if (indxFunction < 0 || indxFunction > addrFunc.length)
      {
         log.err ("checkCyclicFunction", "math formula: cyclic check, bad argument function indx " + indxFunction);
         return false; // or true ?? here the problem is not cyclic or not cyclic ...
      }

      int addr = addrFunc[indxFunction];
      if (addr == -1) return true; // another problem but not cyclic...

      java.util.List addNoList = new java.util.Vector ();
      addNoList.add (new int [] { addr });
      return checkCyclicCall (addNoList, addr);
   }

   public boolean checkCyclic ()
   {
      for (int ii = 0; ii < addrFunc.length; ii ++)
      {
         // pasearse por el co'digo dando saltos
         if (! checkCyclicFunction (ii)) return false;
      }
      return true;
   }


   public String toString ()
   {
      String str = "Constants [";

      for (int xx = 0; xx < constantes.length; xx ++)
         str += constantes[xx] + "";

      str += "]\naddrFunc [";
      for (int xx = 0; xx < addrFunc.length; xx ++)
         str += addrFunc[xx] + ", ";

      str += "]\nProgramExe [";
      for (int xx = 0; xx < ProgramExe.length; xx ++)
         str += ProgramExe[xx] + ", ";

      return str;
   }
}


/*
Function Division(ByVal nu As Double, ByVal de As Double) As Double
    If de = 0 Then
        Division = Infinitus
        errDivideCero = True
    Else
        Division = nu / de
    End If
End Function

Private Function Logaritmo(ByVal nu As Double) As Double
    If nu <= 0 Then
        Logaritmo = 0
        'errDivideCero = True
    Else
        Logaritmo = Log(nu)
    End If
End Function
*/