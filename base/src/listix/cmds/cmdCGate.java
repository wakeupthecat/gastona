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


/*
   //(o) WelcomeGastona_source_listix_command CGATE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       CGATE
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdCGate
   <importance> 3
   <desc>       //JNI caller

   <help>
      //
      // Uses JNI. It loads the library "cgate" and calls its predefined method callCevaUnitSerial.
      // The native library itself is not part of gastona and it has to be found in an accessible file 
      // like cgate.dll for Windows or libcgate.so for linux based systems. 
      //
      // The function callCevaUnitSerial accepts an string and returns another strihg which is 
      // send to the current listix target (e.g. default console), in C the jni signature looks like:
      //
      //    JNIEXPORT jstring JNICALL Java_listix_cmds_cgate_callCevaUnitSerial(JNIEnv *env, jobject thisObj, jstring inJNIStr)
      //
      // For complex functionalities it is enough to encode the input and output strings the information
      // using EvaUnit, JSON or even XML if desired. In the native part this formats has to be supported while
      // on the gastona side is enough to use the JNI call to generate a file (i.e. a memory file ":mem xxx") and then parse it
      // using the command LOAD, JSON or XMELON depending on the format chosen.
      //

   <aliases>
      alias
      CGATE
      CPPGATE
      C++GATE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Calls the method callCevaUnitSerial from the library cgate which has to be available at cgate.dll or libcgate.so

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , CALL         , //
         1   , [ body ]     , //The body or its first line

   <options>
      synIndx, optionName  , parameters, defVal, desc
         1   , BODY        , text      ,  0    , //Is also the default option, add the text to the body

   <examples>
      gastSample

      cgateSample1

   <cgateSample1>
      //#javaj#
      //
      //   <frames> oFmain, Sample calling C++ through cgate
      //
      //#listix#
      //
      //    <main>
      //       CGATE, CALL
      //            ,, //  <var1> my value
      //            ,, //  <table1>
      //            ,, //      id, name, tel
      //            ,, //      22, Gema, 00912312
      //            ,, //      42, Nila, 88717122


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

public class cmdCGate implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "CGATE",
          "CPPGATE",
          "C++GATE"
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      // ::execute - some helpful comment for all the possible syntaxes
      //
      //      comm____   , oper______    par1_____
      //      CGATE      , CALL, text
      //                 , BODY, text
      //                 , BODY, text

      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String method = cmd.getArg(0).toUpperCase ();
      String body0 = cmd.getArg(1);

      boolean methodOk = cmd.meantConstantString (method, new String [] { "CALL", "RUN", "REQUEST" });
      if (! methodOk)
      {
         cmd.getLog().warn ("CGATE", "first parameter not recognized \"" + method + "\" (use for example CALL)");
         return 1;
      }


      // read body
      //
      StringBuffer body = new StringBuffer (body0);
      do
      {
         String [] line = cmd.takeOptionParameters(new String [] { "BODY", "" } );
         if (line == null) break;
         if (line.length == 1)
         {
            body.append (line[0] + "\n");
         }
         else cmd.getLog().err ("CGATE", "option BODY has length " + line.length + " but it has to have just one parameter!");
      } while (true);

      cmd.getLog().dbg (2, "CGATE", "calling c++ native method");
      String result = new cgate().callCevaUnitSerial(body.toString ());
      cmd.getLog().dbg (2, "CGATE", "return from call c++ native method, string size " + result.length ());

      // give the result
      //
      cmd.getListix ().printTextLsx (result);

      cmd.checkRemainingOptions ();
      return 1;
   }
}
