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

   <help>
      //
      // Sends command to javaj. At present only one command possible : MASK LAYOUT
      //
      // This command allows to change dynamically the layout of a frame or a part of it. Although is
      // not very usual changing the layout of a frame there are some well known examples of that: the
      // called application perspectives (i.e. eclipse framework) and the wizards where usually only
      // few buttons are fixed (e.g. previous, next) and the rest changes accordling to the navigation
      // within the wizard. Furthermore masking layouts is a very good resource for building atractive
      // GUI applications and, not less important, it is straightforward!
      //
      // The command has just two arguments: the masked layout and the layout that masks. When masking
      // a layout it is replaced with the one that masks, therefore it disapears. To restore the old
      // layout it is enough to mask again the original layout with nothing. And what is more, masking
      // is not limited just to layouts but even single widgets can be masked and mask other widgets
      // or layouts.
      //

   <aliases>
         alias

   <syntaxHeader>
      synIndx, groupInfo, importance, desc
         1   , gui      ,    2      , //open or hide a frame
         2   , gui      ,    3      , //replace components in layout

   <syntaxParams>
      synIndx, name         , defVal      , desc

<! DEPRECATED
<!         1   , FRAME        ,             , //
<!         1   , frameName    ,             , //Name of the frame, it has to be a layout
<!         1   , visible      , 1           , //Makes the frame visible or invisible
<!            1   , posX  , -1 , //
<!            1   , posY  , -1 , //
<!            1   , sizeX  , -1 , //
<!            1   , sizeY  , -1 , //

         1   , MASK LAYOUT  ,             , //
         1   , layoutToMask ,             , //Layout or widget to mask
         1   , masklayout   , (default db), //Layout or widget that masks, if nothing given then the mask is removed and the original layout or widget will be shown

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample
      javaj masking
      <!javaj mask perspectives
      <!javaj frame and mask2

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

   <javaj frame and mask2>
      //#javaj#
      //
      //   <frames>
      //
      //      Fmain , The main frame
      //      frame2, A second frame
      //
      //  <layout of Fmain>
      //
      //     EVA, 10, 10, 5, 5
      //
      //   ---,            ,   X
      //      , bShowFrame2, bPlace
      //      , bHideFrame2,    +
      //      , bMask1,         +
      //      , bMask2,         +
      //      , bUnmaskAll ,    +
      //    X , originalLayout, -
      //
      //    <layout of originalLayout>
      //
      //       EVA, 4, 4, 2, 2
      //
      //       ---, X
      //          , tTable,
      //        X ,   +
      //
      //    <layout of secondLayout>
      //
      //       EVA, 4, 4, 2, 2
      //
      //       ---,         , X
      //          , bButton3, -
      //        X , bButton4, xAText
      //
      //    <layout of frame2>
      //
      //       EVA, 4, 4, 2, 2
      //
      //       ---,     X
      //        X , xFrame2Text
      //
      //#data#
      //
      //   <xAText>
      //     This is a sample
      //     text
      //
      //#listix#
      //
      //   <-- bShowFrame2>   JAVAJ, FRAME, frame2, 1
      //   <-- bHideFrame2>   JAVAJ, FRAME, frame2, 0
      //
      //   <-- bMask1>
      //      JAVAJ, MASK, bPlace, originalLayout
      //      JAVAJ, MASK, originalLayout, secondLayout
      //
      //   <-- bMask2>
      //      JAVAJ, MASK, originalLayout
      //      JAVAJ, MASK, bPlace, secondLayout
      //
      //   <-- bUnmaskAll>
      //       JAVAJ, MASK, originalLayout
      //       JAVAJ, MASK, bPlace

#**FIN_EVA#
*/

package gastona.cmds;

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

//   private String oper        = null;
//   private String fileSource  = null;
//   private String dbName      = null;
//   private String tablePrefix = null;
//   private aLineParsons parsons = null;
//
//   private int fileID = -1;

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

         (evaU.getSomeHowEva ("frameName")).setValue (cmd.getArg(1));
         (evaU.getSomeHowEva ("visible")).setValue (cmd.getArg(2));

         Mensaka.sendPacket (":gastona javaj FRAME", evaU);
      }
      else if (oper.equals("MASK") || oper.equals("MASKLAYOUT"))
      {
         EvaUnit evaU = new EvaUnit("4javajCmd"); // any name

         (evaU.getSomeHowEva ("layoutToMask")).setValue (cmd.getArg(1));
         (evaU.getSomeHowEva ("maskLayout")).setValue (cmd.getArg(2));

         Mensaka.sendPacket (":gastona javaj MASK", evaU);
      }
      else
      {
         that.log().err ("JAVAJ", "wrong parameter " + oper + " to JAVAJ, posible values FRAME, MASK or MASK LAYOUT");
      }

      return 1;
   }
}
