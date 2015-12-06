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
import java.io.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

/** Clase para hacer operaciones en notacio'n polaca inversa
*/
public class Compilator extends fyPrimitivas
{
   public static final boolean virtualFunEnabled = true;

   public static final String UNIEVA_DATA_FUNCTION = "data";

   public static String [] VARLIST_X = new String [] { "x" };
   public static String [] VARLIST_XY = new String [] { "x", "y" };
   public static String [] VARLIST_XYZ = new String [] { "x", "y", "z" };
   public static String [] VARLIST_UV = new String [] { "u", "v" };
   public static String [] VARLIST_T = new String [] { "t" };

   protected class FuncHeader
   {
      public FuncHeader (String name)
      {
         fname = name;
      }

      public String   fname;
      public int      NParams = 1;
      public int      dirInicio = -1;
      public boolean  endLoading = false;
      public boolean  okLoading = true;
      public String   MsgError = "";

      public int      funcIndx;  // should correspond with the index in the list of FuncHeader's
                                 // therefore is somehow redundant, but we want it anyway

      public String toString ()
      {
         return "Name[" + fname + "] #par " + NParams + " @Start " + dirInicio +
                " endLoad " + endLoading  + " okLoad " + okLoading + " errmsg[" + MsgError + "]";
      }
   }

   protected Vector listFunctions    = new Vector ();   // <FuncHeader>
   protected Vector listProgramCode  = new Vector ();   // <int [1]>
   protected Vector listProgramData  = new Vector ();   // <double [1]>   // for constants

   public void initCompilator ()
   {
      listFunctions    = new Vector ();   // <FuncHeader>
      listProgramCode  = new Vector ();   // <int [1]>
      listProgramData  = new Vector ();   // <double [1]>   // for constants
   }

   /**
      compile and link the expresion 'sExpress'
   */
   public boolean cl (Ejecutable exe, String name, String dirBase, String sExpress, String [] variableNames)
   {
      cargaFuncion (indxFunction (name), dirBase, sExpress, variableNames, true);

      // verify that all functions are ok loaded
      //
      exe.clear ();
      for (int ff = 0; ff < listFunctions.size (); ff ++)
      {
         FuncHeader fun = (FuncHeader) listFunctions.get (ff);
         if (fun.okLoading && fun.MsgError.length () == 0) continue;
         // Error cannot compile!
         return false;
      }

      return linkAll (exe, dirBase);
   }

   /**
      return function index (0 ... N functions)
   */
   public int loadFunction (String name, String dirBase, String sExpress, String [] variableNames)
   {
      int indx = indxFunction (name);

      cargaFuncion (indx, dirBase, sExpress, variableNames, false);
      // System.out.println (toString ());
      // while (loadNewFunctions_HPPath (0, dirBase) > 0);
      // System.out.println (toString ());

      return indx;
   }

   public boolean linkAll (Ejecutable exe, String dirBase)
   {
      // load the rest of unresolved functions (Note that is needed in case the
      // functions has been loaded using cargaFuncion (...., false);
      //
      while (loadNewFunctions_HPPath (0, dirBase) > 0);

      // pass lists to arrays
      //
      int [] Acode = new int [listProgramCode.size ()];
      int [] Afunc = new int [listFunctions.size ()];
      double [] Adata = new double [listProgramData.size ()];

      for (int cc = 0; cc < Acode.length; cc ++) Acode[cc] = ((int []) listProgramCode.get (cc))[0];
      for (int dd = 0; dd < Adata.length; dd ++) Adata[dd] = ((double []) listProgramData.get (dd))[0];

      // solve function addresses
      //    now are in Acode[xx] > Espec_CallBase the function index (0, 1, 2)
      //    we want to set the program address
      //
      for (int pp = 0; pp < Acode.length; pp ++)
      {
         if (Acode[pp] >= Espec_CallBase)
         {
            int findx = Acode[pp]-Espec_CallBase;
            FuncHeader fun = (FuncHeader) listFunctions.get (findx);

            // control errores
            if (findx < 0 || findx >= Afunc.length)
            {
               System.err.println ("ERROR linkAll illegal number of function " + findx + " in code address " + pp + " !");
               // Acode[pp] = Espec_ReturnCall;
               Acode[pp] = UnresolvedCall;
            }
            else if (fun == null)
            {
               // Acode[pp] = Espec_ReturnCall;
               Acode[pp] = UnresolvedCall;
               System.err.println ("ERROR linkAll function index " + findx + " not found in code address " + pp + " !");
               // exe.clear ();
               // return false;
            }
            else
            {
               Acode[pp] = Espec_CallBase + fun.dirInicio;
            }
         }
      }

      for (int pp = 0; pp < Afunc.length; pp ++)
      {
         FuncHeader fun = (FuncHeader) listFunctions.get (pp);
         Afunc[pp] = fun.dirInicio;
      }

      // set the program
      //
      exe.setProgram (Adata, Acode, Afunc);
      return true;
   }


   // insert a function entry
   //
   public int indxFunction (String sNomF)
   {
      // mirar si ya existe
      //
      for (int ii = 0; ii < listFunctions.size (); ii ++)
      {
         FuncHeader fun = (FuncHeader) listFunctions.get (ii);
         if (sNomF.equalsIgnoreCase (fun.fname))
            return ii;
      }

      // function not there
      //
      FuncHeader fh = new FuncHeader (sNomF);
      listFunctions.add (fh);
      fh.funcIndx = listFunctions.size () - 1; // somehow redundant but needed
      return fh.funcIndx;
   }

   // write code
   //
   private void writeProgram (int NInstruc)
   {
      listProgramCode.add (new int [] { NInstruc });
   }

   // save a constant in constant list
   //
   private int saveConstant (double value)
   {
      for (int ii = 0; ii < listProgramData.size (); ii ++)
         if (value == ((double []) listProgramData.get (ii))[0])
            return ii;

      listProgramData.add (new double [] { value });
      return listProgramData.size () - 1;
   }


//   /*
//      NOTE: call this function only with code linked!!!
//         (calls are 800 + address while before linking calls are 800 + funtion index)
//   */
//   private boolean checkCyclicCall (List addForbid, int add)
//   {
//      if (add < 0 || add > listProgramCode.size ())
//      {
//         System.err.println ("Error! checkCyclic add is " + add);
//         return false;
//      }
//
//      do
//      {
//         int instr = (int) listProgramCode.get (add ++);
//         if (instr == Espec_ReturnCall) return true;  // end of Call
//         if (instr < Espec_CallBase) continue; // another operation
//
//         // it is a call
//         int called = instr - Espec_CallBase;
//
//         // ckeck that is not a verbotene Adresse
//         for (int vv = 0; vv < addForbid.size (); vv ++)
//            if (called == (int) addForbid.get (vv))
//            {
//               System.err.println ("Error! Call to " + called + " forbidden at address " + (add-1));
//               return false;
//            }
//
//         // ok not called, go through it (recursive call to checkCyclic!)
//         //
//         addForbid.add (called);
//         if (! checkCyclic (addForbid, called)) return false;
//         addForbid.remove (addForbid.size ()-1);
//      }
//      while (add < listProgramCode.size ());
//
//      return true;
//   }
//
//   /**
//      NOTE: call this function only with code linked!!!
//         (calls are 800 + address while before linking calls are 800 + funtion index)
//   */
//   public boolean checkCyclicFunction (int indxFunction)
//   {
//      FuncHeader fun = (FuncHeader) listFunctions.get (ii);
//
//      if (fun.dirInicio == -1) contiunue; // another problem ...
//
//      List addNo = new Vector ();
//      addNo.add (fun.dirInicio);
//      return checkCyclicCall (addNo, fun.dirInicio);
//   }
//
//   public boolean checkCyclic ()
//   {
//      for (int ii = 0; ii < listFunctions.size (); ii ++)
//      {
//         // pasearse por el co'digo dando saltos
//         if (! checkCyclicFunction ()) return false;
//      }
//      return true;
//   }

   /**
         solveUndef: si esta a true se intentara'n cargar de fichero todas las funciones indefinidas
                     es decir convierte la llamada en recursiva.
                     NOTAR: en caso de cargar un grupo de funciones que puedan referenciarse unas a otras
                     debe llamarse a esta funcio'n con false y luego proceder
   */
   private void cargaFuncion (int NumFunc, String dirFile, String sExpress, String [] variableNames, boolean solveUndef)
   {
      String sEle = "";
      Cadena sToa = new Cadena (sExpress);
      sToa.setSepar (" ,;\r\n");

      // Cargar la funcio'n
      //
      ((FuncHeader) listFunctions.get (NumFunc)).dirInicio = listProgramCode.size ();
      ((FuncHeader) listFunctions.get (NumFunc)).NParams = variableNames.length;
      // System.out.println ("me demanan recu " + listProgramCode.size () + " estamos");


      writeProgram (Espec_FuncArgBase + variableNames.length);
      while (sToa.getToken ())
      {
         sEle = sToa.lastToken ();
         //System.out.println ("TOKEN [" + sEle + "]");

         if (sEle.length () == 0) continue;

         int indx = utilParse.indxOP (sEle, variableNames);
         switch (indx)
         {
            case Not_Primitive: writeProgram (Espec_CallBase + indxFunction (sEle)); break;
            case Numeric_Cte:   writeProgram (Espec_ConstantBase + saveConstant (stdlib.atof (sEle))); break;
            default:            writeProgram (indx);  break;
         }
      }
      writeProgram (Espec_ReturnCall);

      if (solveUndef)
      {
         loadNewFunctions_HPPath (NumFunc, dirFile);
      }
   }


   /**
      Load unresolved functions from .fun files.
      The function are resolved (HP calculator like ?) in the following way
         - first look into the giver path ('dirFile')
         - if not found look in the parent directory and so on until a directory
           containing a file .rootFunLibrary is found
   */
   private int loadNewFunctions_HPPath (int indxFrom, String dirFile)
   {
      // CARGAR TODAS LAS FUNCIONES NUEVAS

      // ensure canonical file (if not getParent gets wrong!)
      {
         String cano = "";
         File fil = fileUtil.getNewFile (dirFile);
         try { cano = fil.getCanonicalPath (); } catch (Exception e) {};
         dirFile = cano;
      }
      int carga = 0;

      if (indxFrom >= listFunctions.size ()) return 0;

      // function parent
      FuncHeader funPa = (FuncHeader) listFunctions.get (indxFrom);

      for (int ii = indxFrom + 1; ii < listFunctions.size (); ii ++)
      {
         FuncHeader fun = (FuncHeader) listFunctions.get (ii);
         if (fun.dirInicio != -1) continue;

         // mirar en que sub-sub directorio esta
         String baseRoot = dirFile;
         File funfile = null;
         do
         {
            // is it in this directory ?
            //
            funfile = fileUtil.getNewFile (baseRoot + "/" + fun.fname + ".fun");
            if (funfile.exists ()) break;  // found ! ok go on

            // is this directory, the root of the fun library ?
            //
            File rootMark = fileUtil.getNewFile (baseRoot + "/.rootFunLibrary");
            if (rootMark.exists ()) break; // is not in the library (root of linrary reached!)

            // try with the parent directory
            //
            File pap = funfile.getParentFile ();
            baseRoot = (pap == null) ? "": pap.getParent ();
         }
         while (baseRoot != null && baseRoot.length () > 0);

         if (! funfile.exists ())
         {
            fun.okLoading = false;
            fun.MsgError += "Funcio'n externa " + fun.fname + " no encontrada en " + funfile.getPath ();
            // CabeceraFunc(NumFunc).okCarga = False
            // CabeceraFunc(NumFunc).MsgError = "Funcio'n externa " & CabeceraFunc(Nf).name & " no encontrada"
            continue;
         }

         // load from fun file
         //
         EvaUnit Efunc = EvaFile.loadEvaUnit (funfile.getPath (), UNIEVA_DATA_FUNCTION);
         if (Efunc == null)
         {
            fun.okLoading = false;
            fun.MsgError = "#function# not found! in " + fun.fname;
            continue;
         }

         String [] varList = Efunc.getSomeHowEva ("varNames").getStrArray (0);
         if (varList == null)
            // varList = new String [0];
            varList = VARLIST_X;


         //(o) functionator Truco para virtuosismo !!!
         String fromDir = (virtualFunEnabled) ?
                                 dirFile :               // look from the initial function directory
                                 funfile.getParent ();   // look from the current function directory

         cargaFuncion (ii, fromDir, Efunc.getValue ("polac"), varList, true);
         carga ++;
      }

      funPa.endLoading = true;
      return carga;
   }
/*
   public boolean Compila (Ejecutable p_eje, String prog_polac)
   {
   }

   public String [] Desensambla ()
   {
   }
*/



   public String toString ()
   {
      String RET = "\n";
      String str = "============FUNCTIONS" + RET;

      for (int ff = 0; ff < listFunctions.size (); ff ++)
      {
         FuncHeader fun = (FuncHeader) listFunctions.get (ff);
         str += "" + fun + RET;
      }

      str += "============DATA" + RET;
      for (int dd = 0; dd < listProgramData.size (); dd ++)
      {
         str += (dd + "] " + ((double []) listProgramData.get (dd))[0]) + RET;
      }

      str += "============CODE" + RET;
      for (int pp = 0; pp < listProgramCode.size (); pp ++)
      {
         str += (pp + ") " + ((int []) listProgramCode.get (pp))[0]) + RET;
      }
      return str;
   }
}





   /*
         Example:

            Compilator jamon = new Compilator ();
            ..
            jamon.initCompilator ();
            int fu0 = jamon.compileFunction ("seno",   "x sin");
            int fu1 = jamon.compileFunction ("coseno", "x cos");
            int fu2 = jamon.compileFunction ("trigo",  "x seno coseno x x * coseno seno +");

            Ejecutable miExe = new Ejecutable ();
            jamon.link (miExe);

            int call0 = miExe.funcIndx2ProgAddress (fu0);
            int call1 = miExe.funcIndx2ProgAddress (fu1);
            int call2 = miExe.funcIndx2ProgAddress (fu2);

            PilaCab pila = new PilaCab ();
            pila.push (3.233);
            miExe.run (call2, pila);

   */
