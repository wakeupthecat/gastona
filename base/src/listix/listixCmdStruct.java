/*
library listix (www.listix.org)
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

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

package listix;

import java.util.*;
import de.elxala.Eva.*;
import de.elxala.zServices.logger;
import listix.cmds.commander;


/**
   @class listixCmdStruct
   @brief Describes the general listix command structure

   @desc
   Describes the general listix command structure and provides methods
   to extract normalized commnad name, command arguments and commnand options
   A listix command structure looks like

      commandName, arg0, arg1, ...
                 , opt0, par1opt0, par2opt0, ...
                 , ...
                 , optN, par1optN, par2optN, ...
*/
public class listixCmdStruct
{
   // save constructor parameters
   private listix  listixPtr = null;
   private Eva     cmdEvaPtr = null;
   private int     baseIndx = 0;
   private int     optOffset = 1;
   private boolean doNormOptions = true;
   private int     maxIndx = 0;
   private int     maxArguments = -1;

   public String commandName;
   public String [] arguments = null;

   // store the given options with its names normalized
   // - the index in the list (+ offset) is the same as in the Eva variable
   // - the list may contain null elements if the option has been taken
   //
   public List givenOptions = null;

   /**
      constructor for a listixCmdStruct

      @param that lisitix structure used for logg and to solve arguments and option parameters
      @param commandEva Eva variable containing the whole commnad
      @param indxComm row index of commandEva where the commnad starts
      @param offsetOptions offset where does the options begin (per default 1)
   */
   public listixCmdStruct (listix that, Eva commandEva, int indxComm, int offsetOptions, boolean normalizeOpts)
   {
      construct (that, commandEva, indxComm, offsetOptions, normalizeOpts);
   }

   public listixCmdStruct (listix that, Eva commandEva, int indxComm)
   {
      construct (that, commandEva, indxComm, 1, true);
   }


   protected String getNormalized (String strname)
   {
      return strname.toUpperCase().replaceAll (" ", "");
   }

   protected String getOptionID (String optName)
   {
      return doNormOptions ? getNormalized (optName): optName;
   }

   public void construct (listix that, Eva commandEva, int indxComm, int offsetOptions, boolean normalizeOpts)
   {
      givenOptions = new Vector ();
      listixPtr = that;
      cmdEvaPtr = commandEva;
      baseIndx = indxComm;
      optOffset = offsetOptions;
      doNormOptions = normalizeOpts;

      // command must be found
      if (baseIndx >= cmdEvaPtr.rows())
      {
         listixPtr.log().err ("listixCmdStruct", "Eva [" + cmdEvaPtr.getName() + "] does not contain any command at row " + baseIndx);
         return;
      }

      // command must have at least one parameter
      if (cmdEvaPtr.cols(baseIndx) < 2)
      {
         listixPtr.log().err ("listixCmdStruct", "Eva [" + cmdEvaPtr.getName() + "] does not contain a valid command at row " + baseIndx);
         return;
      }

      // command name normalized
      //
      commandName = getNormalized (cmdEvaPtr.getValue (baseIndx, 0));

      // get and solve command arguments
      //
      arguments = new String [cmdEvaPtr.cols(baseIndx)-1];
      for (int ii = 1; ii < cmdEvaPtr.cols(baseIndx); ii ++)
      {
         //arguments[ii-1] = listixPtr.solveStrAsString (cmdEvaPtr.getValue (baseIndx, ii));
         arguments[ii-1] = cmdEvaPtr.getValue (baseIndx, ii);
      }

      // get given options
      //
      reloadOptions ();
   }

   public listix getListix()
   {
      return listixPtr;
   }

   public logger getLog()
   {
      return listixPtr.log ();
   }

   public int getArgSize ()
   {
      return arguments.length;
   }

   public String getArg(int indx)
   {
      return getArg(indx, true);
   }

   public String getArg(int indx, boolean solve)
   {
      if (indx >= arguments.length)
         return "";

      return (solve) ? listixPtr.solveStrAsString (arguments[indx]): arguments[indx] ;
   }

   public String [] getArgs (boolean solve)
   {
      String [] aa = new String [arguments.length];
      for (int ii = 0; ii < arguments.length; ii ++)
         aa[ii] = getArg(ii, solve);
      return aa;
   }

   /**
      Facility to print out standad error messages if the given parameters does not fit
      into the range ['minParamSize' - 'maxParSize']
      Returns true if fits into the range
   */
   public boolean checkParamSize (int minParamSize, int maxParSize)
   {
      if (getArgSize () >= minParamSize && getArgSize () <= maxParSize) return true;

      if (getArgSize () < minParamSize)
         getLog().err (commandName, "too few parameters given, only " + getArgSize () + " are given but it requires at least " + minParamSize + "");

      if (getArgSize () > maxParSize)
         getLog().err (commandName, "too many parameters given, last recognized parameter [" + getArg(maxParSize-1) + "]");
      return false;
   }

   /**
      normalize a string using upper case and removing all blanks
      (same as listix.cmd.commander.meantCommand)
   */
   public static boolean meantConstantString (String relaxedString, String [] possibilities)
   {
      //return listix.cmds.commander.meantCommand (relaxedString, possibilities);
      return commander.meantCommand (relaxedString, possibilities);
   }

   /**
      calling this method a search of given options can be re-initialized. For a unique search of
      options there is no need to call this method because it is always called in constructor
   */
   public void reloadOptions ()
   {
      givenOptions = new Vector();

      // Example: collect "OPT1" and "OPT2" and also "PAR" if optOffset is 0
      //
      //              ...
      //   baseIndx-> CMD, PAR, par, par
      //                 , OPT1, opar, opar
      //                 , OPT2, opar, opar
      //                 Other

      // collect all option names in a list givenOptions
      // NOTE1: We don't copy all parameters but access them using the same index as the option has in the list!
      // NOTE2: We detect the end of options by the column 0 which has to be empty (except if ii == baseIndx !)
      //
      for (int ii = optOffset + baseIndx;
           ii < cmdEvaPtr.rows () &&
           cmdEvaPtr.cols(ii) > 1 &&
           (ii == baseIndx || cmdEvaPtr.getValue (ii, 0).length () == 0); // check if it is a void listix command
           ii ++)
      {
         String optStr = getOptionID (cmdEvaPtr.getValue (ii, 1));
         //System.out.println ("coll1 opt [" + optStr + "]");
         givenOptions.add (optStr);
         maxIndx = ii;
      }
   }

   /**
      Search 'optName' in the given options, if found the option is removed for the next search (methods take..)
      and the first parameter of the option is returned solved. Otherwise return an empty string ""
   */
   public String takeOptionString (String optName)
   {
      return takeOptionString (new String [] { optName }, "");
   }

   /**
      Search 'optName' in the given options, if found the option is removed for the next search (methods take..)
      and the first parameter of the option is returned solved. Otherwise return 'defaultValue'
   */
   public String takeOptionString (String optName, String defaultValue)
   {
      return takeOptionString (new String [] { optName }, defaultValue);
   }

   public String takeOptionString (String [] optNames, String defaultValue)
   {
      return takeOptionString (optNames, defaultValue, true);
   }

   /**
      Search any of the option names given in the array 'optNames' in the given options,
      if one is found then the option is removed for the next search (methods take..)
      and the first parameter of the option is returned solved. Otherwise return 'defaultValue'
   */
   public String takeOptionString (String [] optNames, String defaultValue, boolean solve)
   {
      String [] resp = takeOptionParameters (optNames, solve);

      if (resp != null && resp.length > 0)
           return resp[0];
      else return defaultValue;
   }

   /// takeOptionParameters with one string instead of an array of them and default solved to true
   public String [] takeOptionParameters (String optName)
   {
      return takeOptionParameters (new String [] { optName }, true);
   }

   /// takeOptionParameters with default solved to true
   public String [] takeOptionParameters (String [] optNames)
   {
      return takeOptionParameters (optNames, true);
   }

   /// takeOptionParameters with default of nparam_solved to -1
   public String [] takeOptionParameters (String [] optNames, boolean solved)
   {
      return takeOptionParameters (optNames, solved, -1);
   }

   /**
      Search any of the option names given in the array 'optNames' in the given options,
      if one is found then the option is removed for the next search (methods take..)
      and all parameters of the option are returned solving them if 'solved' is true. Otherwise return null
      
      if solved is true and it will solve all parameters if ncolumn_solved is -1 (default)
      or only until nparam_solved if =! -1
   */
   public String [] takeOptionParameters (String [] optNames, boolean solved, int nparam_solved)
   {
      if (optNames == null || optNames.length == 0) return null;

      for (int aa = 0; aa < optNames.length; aa ++)
      {
         // normalize option name upper case without spaces!
         //
         String strOpt = getOptionID (optNames[aa]);
         for (int oo = 0; oo < givenOptions.size (); oo ++)
         {
            String laopt = (String) givenOptions.get(oo);
            if (laopt == null) continue;
            if (strOpt.equals (laopt))
            {
               givenOptions.set(oo, null); // mark it as taken
               int evaindx = baseIndx + optOffset + oo;

               if (evaindx > maxIndx) continue; // actually not possible, sanity check

               // an option in "its place"
               //

               // retrieve and solve, if needed, all the parameters
               EvaLine el = cmdEvaPtr.get(evaindx);
               String [] resp = new String [el.cols()-2];
               for (int ii = 2; ii < el.cols(); ii ++)
               {
                  resp[ii-2] = el.get(ii);
                  if (solved && (nparam_solved == -1 || ii-2 <  nparam_solved))
                     resp[ii-2] = listixPtr.solveStrAsString (resp[ii-2]);
                  //System.out.println ("resp[" + ii + "-2]  [" + resp[ii-2] + "]" );
               }

               // >>>> return parameters
               return resp;
            }
         }
      }
      return null;
   }

   public Eva takeOptionAsEva (String [] optNames)
   {
      return takeOptionAsEva (optNames, "", false);
   }

   // get all rows of the option found as an unique eva
   //
   public Eva takeOptionAsEva (String [] optNames, String evaName2set, boolean solved)
   {
      if (optNames == null || optNames.length == 0) return null;

      Eva target = new Eva (evaName2set);

      for (int aa = 0; aa < optNames.length; aa ++)
      {
         // normalize option name upper case without spaces!
         //
         String optName = getOptionID (optNames[aa]);
         for (int oo = 0; oo < givenOptions.size (); oo ++)
         {
            String laopt = (String) givenOptions.get(oo);
            if (laopt == null) continue;
            if (optName.equals (laopt))
            {
               givenOptions.set(oo, null); // mark it as taken
               int evaindx = baseIndx + optOffset + oo;

               if (evaindx > maxIndx) continue; // actually not possible, sanity check

               // retrieve line
               int row = target.rows ();
               EvaLine el = cmdEvaPtr.get(evaindx);
               for (int ii = 2; ii < el.cols(); ii ++)
               {
                  String val = solved ? listixPtr.solveStrAsString (el.get(ii)): el.get(ii);
                  target.setValue (val, row, ii - 2);
                  //System.out.println ("resp[" + ii + "-2]  [" + resp[ii-2] + "]" );
               }
            }
         }
      }

      return target.rows () > 0 ? target: null;
   }

   /**
      return the not yet evaluated options without altering them, that is, they can
      still be retrieved using takeOptionParameters
   */
   public String [] getRemainingOptionNames ()
   {
      int nn = 0;
      for (int ii = 0; ii < givenOptions.size (); ii ++)
         if (givenOptions.get (ii) != null) nn ++;

      String [] reto = new String [nn];
      nn = 0;
      for (int ii = 0; ii < givenOptions.size (); ii ++)
      {
         if (givenOptions.get (ii) != null)
         {
            if (nn >= reto.length) getLog().severe ("getRemainingOptionNames", "wrong index");
            reto[nn++] = (String) givenOptions.get (ii);
         }
      }

      return reto;
   }

   /**
      Facility to check and optionally print out standard error messages for the remaining (not taken)
      options. If 'logError' is true, one error message will be printed out for each remaining option

      @param forgive array of possible remaining optiona that don't has to be considered an error to ignore them

      Returns the count of remaining options.
   */
   public int checkRemainingOptions (boolean logError, String [] forgive)
   {
      while (forgive != null && null != takeOptionParameters(forgive, false));
      //      if (forgive != null)
      //         for (int ii = 0; ii < forgive.length; ii ++)
      //            while (null != takeOptionParameters(new String [] { forgive[ii] }, false));

      int count = 0;
      //System.out.println ("NOS TOPAMOS CON " + givenOptions.size ());
      for (int ii = 0; ii < givenOptions.size (); ii ++)
      {
         String rem = (String) givenOptions.get (ii);
         //System.out.println ("checo rem [" + rem + "]");
         if (rem == null || rem.length () == 0) continue;

         //System.out.println ("azertamos!");
         count ++;
         if (logError)
            getLog().err (commandName, "option [" +  rem + "] has been ignored!");
      }
      return count;
   }

   public int checkRemainingOptions (boolean logError)
   {
      return checkRemainingOptions (logError, new String [0]);
   }

   public int checkRemainingOptions ()
   {
      return checkRemainingOptions (true, new String [0]);
   }
}
