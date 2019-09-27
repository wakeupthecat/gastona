/*
library listix (www.listix.org)
Copyright (C) 2005-2019 Alejandro Xalabarder Aulet

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
   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       JAVAJ
   <groupInfo>  lang_comm
   <javaClass>  gastona.cmds.CmdJavaj
   <importance> 3
   <desc>       //Communication to javaj module

   <pc-android info> //The command is not yet implemented in gastona-Android

   <help>
      //
      // (*** Not implemented yet in gastona-Android ***)
      // Sends command to javaj. At present only one command possible : MASK LAYOUT
      //
      // This command allows to change dynamically the layout of a frame or a part of it. Although is
      // not very usual changing the layout of a frame there are some well known examples of that: the
      // called application perspectives (i.e. eclipse framework) and the wizards where usually only
      // few buttons are fixed (e.g. previous, next) and the rest changes accordling to the navigation
      // within the wizard. Also it is very helpful to build atractive GUI applications.
      //
      // The command has just two arguments: the masked layout and the layout that masks. When masking
      // a layout this is replaced with the one that masks it, therefore it disapears. To restore the old
      // layout it is enough to mask again the original giving an empty string. Masking is not limited
      // just to layouts but also single widgets can be masked and mask other widgets or layouts.
      //

   <aliases>
         alias

   <syntaxHeader>
      synIndx, groupInfo, importance, desc
         2   , gui      ,    3      , //replace components in layout

   <syntaxParams>
      synIndx, name         , defVal      , desc

         1   , MASK LAYOUT  ,             , //
         1   , layoutToMask ,             , //Layout or widget to mask
         1   , masklayout   , (unmask)    , //Layout or widget that masks, if nothing given then the mask is removed and the original layout or widget will be shown
         1   , maskAlternative, (unmask)  , //Layout or widget that will be used if found that layoutToMask is already masked by maskLayout. Setting it to '' will perform a toggle (mask/unmask)

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample
      javaj masking
      javaj masking II
      javaj mask perspectives

   <javaj masking>
      //#javaj#
      //
      //   <frames> Fmain , Javaj MASK sample
      //
      //   <layout of Fmain>
      //       EVA, 10, 10, 6, 6
      //       ---,        ,  X
      //          , bMask1 ,  lay1
      //          , bMask2 ,  +
      //          , bHide  ,  +
      //        X ,        ,  +
      //
      //   <layout of lay1>
      //       EVA, 0, 0, 6, 6
      //       ---,    ,    , X
      //          , bB1, bB2
      //        X , xText, -, -
      //
      //   <layout of lay2>
      //       EVA, 0, 0, 6, 6
      //       ---,   X   ,
      //        X , xText ,
      //          ,   +   , bB1
      //          , eField, bB2
      //
      //   <layout of others>
      //       PANEL, X
      //       bCover
      //
      //#data#
      //
      //   <xText>
      //       //Some text
      //       //here.
      //
      //   <eField>  //some search text
      //   <bCover>  //Where is my text?
      //
      //#listix#
      //
      //   <-- bMask1>  JAVAJ, MASK, lay1,
      //   <-- bMask2>  JAVAJ, MASK, lay1, lay2
      //   <-- bHide>   JAVAJ, MASK, xText, bCover
      //   <-- bCover>  JAVAJ, MASK, xText

   <javaj masking II>
      //
      //   In this sample three features of EvaLayoutManager are shown:
      //
      //   1) Layout composition
      //      Layout "main" contain 4 buttons plus a reference to another layout ("form1")
      //      So the layout "from1" will be placed inside the layout "main". Composition can be
      //      deeper, for instance layout "form1" could contain a reference to another layout and so on.
      //
      //   2) Switching the whole layout.
      //      We change the layout of the entire window between layout "main" and "second"
      //      when pressing "bSwitchMain" and viceversa when "bSwitchBack" is pressed
      //
      //   3) Masking a component
      //      We can change selectively a part of a layout by means of the masking mechanism
      //      Specifically in layout "main"
      //
      //       bMask1 masks "form1" with "form2" ==> when it is time to show "form1" form2 will be shown instead
      //       bMask2 masks "form1" with "xMemo" ==> when it is time to show "form1" xMemo will be shown instead
      //       bUnmask unmasks "form1"
      //
      //
      //#gastona#
      //
      //   <!PAINT LAYOUT>
      //
      //#javaj#
      //
      //   <layout of main>
      //      EVALAYOUT, 7, 7, 3, 3
      //
      //         ,            ,    X
      //         , bSwitchMain, form1
      //         , bMask1     ,   +
      //         , bMask2     ,   +
      //         , bUnmask    ,   +
      //        X,            ,   +
      //
      //   <layout of second>
      //      EVALAYOUT, 7, 7, 3, 3
      //
      //      ZZZ,            ,    X     ,
      //         , lLabel1    , eEdit1   , -
      //         , lLabel2    , xMemo    , lLabel3
      //         , lLabel4    ,    +     , iLista
      //         , bSwitchBack,    +     , +
      //         ,            ,    +     , +
      //       X ,            ,    +     , +
      //         , cCombo     ,    -     , +
      //
      //   <layout of form1>
      //      EVALAYOUT, 7, 7, 3, 3
      //
      //         ,           ,    X     ,
      //         , lLabel1   , eEdit1   , -
      //         , lLabel2   , eEdit2   , lLabel3
      //       X , xMemo     , -        , -
      //
      //   <layout of form2>
      //      EVALAYOUT, 7, 7, 3, 3
      //
      //         ,           ,    X
      //         , lLabel4   , cCombo
      //       X ,           , iLista
      //
      //#listix#
      //
      //   <-- bSwitchMain>   JAVAJ, MASK, main, second
      //   <-- bSwitchBack>   JAVAJ, MASK, main
      //   <-- bMask1>        JAVAJ, MASK, form1, form2
      //   <-- bMask2>        JAVAJ, MASK, form1, xMemo
      //   <-- bUnmask>       JAVAJ, MASK, form1

   <javaj mask perspectives>
      //#javaj#
      //
      //   <frames>
      //      Fmain , The main frame, 700, 400
      //
      //  <layout of Fmain>
      //     EVA, 7, 7, 4, 4
      //
      //   ---,         ,  X
      //      , menu    ,  -
      //    X , theTree , theLayout
      //
      //  <layout of menu>
      //       MENU
      //       Frame, Exit , menuExit
      //       Layout, Input  , switch to input layout
      //             , Search , switch to search layout
      //             , Default, switch to default layout
      //
      //  <layout of theLayout>
      //       EVA
      //       ---, X
      //        X , tTable
      //
      //  <layout of theTree>
      //       EVA
      //       ---, X
      //        X , aTree
      //
      //  <layout of layInputTable>
      //       EVA, 4, 4, 2, 2
      //
      //       ---,  X      ,
      //          , tTable  , bNew
      //          ,  +      , bEdit
      //          ,  +      , bDelete
      //        X ,  +      ,
      //          , lNotes  ,
      //        X , xNotes  ,
      //
      //  <layout of laySearchTable>
      //       EVA, 4, 4, 2, 2
      //
      //       ---, X
      //        X , sTable,
      //
      //  <layout of laySearchTree>
      //       EVA, 5, 5, 5, 5
      //
      //       ---,
      //          , eFilter
      //        X , aTree
      //          , radios
      //
      //   <layout of radios>
      //       RADIO, Y
      //
      //       rAll, rMost important, rRest
      //
      //#data#
      //
      //   <tTable>
      //     id, name  , more
      //     19, javaj , The one who place and arrange widgets
      //     20, listix, The one who makes things work
      //
      //    <aTree>
      //       node
      //       some/more/one
      //       some/more/two
      //       any/way/three
      //
      //#listix#
      //
      //   <-- menuExit>   MESSAGE, javaj doExit
      //
      //   <-- switch to input layout>
      //      JAVAJ, MASK, theLayout, layInputTable
      //      JAVAJ, MASK, theTree
      //
      //   <-- switch to search layout>
      //      JAVAJ, MASK, theLayout, laySearchTable
      //      JAVAJ, MASK, theTree  , laySearchTree
      //
      //   <-- switch to default layout>
      //       JAVAJ, MASK, theLayout
      //       JAVAJ, MASK, theTree


#**FIN_EVA#
*/

package org.gastona.cmds;

import listix.*;
import listix.cmds.commandable;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/**
*/
public class CmdJavaj implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "JAVAJ",
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
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper = cmd.getArg(0);


      // prepar variables with the parameters and send the message to gastona (mensaka4listix)
      //
      if (oper.equals("FRAME"))
      {
         EvaUnit evaU = new EvaUnit("4javajCmd"); // any name

         (evaU.getSomeHowEva ("frameName")).setValueVar (cmd.getArg(1));
         (evaU.getSomeHowEva ("visible")).setValueVar (cmd.getArg(2));

         Mensaka.sendPacket (":gastona javaj FRAME", evaU);
      }
      else if (oper.equals("MASK") || oper.equals("MASKLAYOUT"))
      {
         EvaUnit evaU = new EvaUnit("4javajCmd"); // any name

         (evaU.getSomeHowEva ("layoutToMask")).setValueVar (cmd.getArg(1));
         (evaU.getSomeHowEva ("maskLayout")).setValueVar (cmd.getArg(2));
         if (cmd.getArgSize () > 2)
            (evaU.getSomeHowEva ("alternativelayout")).setValueVar (cmd.getArg(3));

         Mensaka.sendPacket (":gastona javaj MASK", evaU);
      }
      else
      {
         that.log().err ("JAVAJ", "wrong parameter " + oper + " to JAVAJ, posible values FRAME, MASK or MASK LAYOUT");
      }

      return 1;
   }
}
