/*
package javaj.widgets.graphics;
Copyright (C) 2005-2020 Alejandro Xalabarder Aulet

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

package javaj.widgets;

//import java.awt.image.BufferedImage;
//import javax.swing.Icon;
import java.awt.*;

import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;


import javax.swing.JPanel;
import java.awt.event.*;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;


import de.elxala.math.space.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (k) z2DMathFunc

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       z2DMathFunc
   <groupInfo>  2D
   <javaClass>  javaj.widgets.z2DMathFunc
   <importance> 8
   <desc>       //2D Math functions

   <help>
      //
      // Testing for 2D Math functions
      //
      //

   <prefix>  2Dmath

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , (String)                   , //Caption of the checkbox
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      selected         , inout , 0 | 1                      , //Value 0 not checked, 1 checked

   <messages>

      msg     , in_out , desc

              ,  out   , check box has been pressed
      data!   ,  in    , update data
      control!,  in    , update control


   <examples>
      gastSample

      Demo z2DMath

   <Demo z2DMath>
      //#javaj#
      //
      //    <frames> 2DmathColko, Demo z2DMathFunc, 600, 500
      //

#**FIN_EVA#

*/


/**
   Implements
      - handling of fucntions
      - drawing of them
      - gesture handling
*/
public class z2DMathFunc extends mathFunctionView implements MouseListener, MouseMotionListener
{
   public z2DMathFunc ()
   {
   }
}
