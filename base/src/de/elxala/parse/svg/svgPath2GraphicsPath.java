/*
package javaj.widgets.graphics;
Copyright (C) 2011 Alejandro Xalabarder Aulet

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

package de.elxala.parse.svg;

import java.util.List;
import java.util.Vector;

import javaj.widgets.graphics.*;

import de.elxala.zServices.*;
import de.elxala.langutil.*;

/**
   parses a svg path string building a android.graphics.Path
   for example

      "M 10 10 c 8 2 4 5"

 */
public class svgPath2GraphicsPath
{
   protected static logger log = new logger (null, "de.elxala.parse.svg", null);

   private final int MAX_NUM_DEPTH = 8;

   private int   numStackSize = 0;
   private float numStack [] = new float [MAX_NUM_DEPTH];

   private uniPath  currPath  = null;

   private void clearNumberStack ()
   {
      numStackSize = 0;
   }

   private void pushNumber (String number)
   {
      if (number.length () == 0) return;
      if (numStackSize >= MAX_NUM_DEPTH)
      {
         log.err ("pushNumber", "pushNumber over stack limit!");
         return;
      }
      numStack [numStackSize ++] = (float) stdlib.atof (number);

      if (log.isDebugging (6))
         log.dbg (6, "pushNumber", "pushNumber[ " + (numStackSize-1) + "] = " + numStack [numStackSize-1]);
   }

   private float popNumber ()
   {
      if (numStackSize <= 0)
      {
         log.err ("popNumber", "popNumber exhausted!");
         return 0f;
      }
      return numStack [-- numStackSize];
   }

   private float lastX = 0.f;
   private float lastY = 0.f;
   private float lastCtrlPt_x = 0.f;
   private float lastCtrlPt_y = 0.f;
   private char lastOperation = 'l';
   private char currOperation = 'l';

   private void tryOperation ()
   {
      boolean relative = currOperation >= 'a';
      char lowOper = relative ? currOperation : (char) (currOperation + ('a'-'A'));

      if (log.isDebugging (4))
         log.dbg (4, "tryOperation", "currOperation [" + currOperation + "] relative " + relative + " stack size " + numStackSize);

      switch (lowOper)
      {
         case 'a':
            //rx ry x-axis-rotation large-arc-flag sweep-flag x y
            //addArc(RectF oval, float startAngle, float sweepAngle)
            if (numStackSize >= 7)
            {
               float rx  = popNumber ();
               float ry  = popNumber ();
               float xaxisrot = popNumber ();
               float largearc = popNumber ();
               float sweep = popNumber ();
               float x = popNumber ();
               float y = popNumber ();

               //TODO: descubrir como dibujar el "arquito"
               // usar ... lastX y lastY
               currPath.addArc (new uniRect (x, y, x+rx, y+ry), largearc, sweep);
               lastX = x;
               lastY = y;
            }
            break;
         case 'm':
            if (numStackSize >= 2)
            {
               float y = popNumber ();
               float x = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "move to " + x + ", " + y);

               if (relative)
                    currPath.rMoveTo (x, y);
               else currPath.moveTo (x, y);

               lastX = x;
               lastY = y;

               // change of current operation ONLY in Moveto !
               //    to support both
               //        <path d="m 250 150 -100 200 200 0 z" />   first move relative to 0 0 (?) next lineto's relative !
               //        <path d="M 250 150 150 350 350 350 Z " /> first move absolute next lineto's absolute !
               //
               // from documentation of moveTo http://www.w3.org/TR/SVG/paths.html
               // moveTo: Start a new sub-path at the given (x,y) coordinate. M (uppercase) indicates that absolute
               //         coordinates will follow; m (lowercase) indicates that relative coordinates will follow.
               //         If a moveto is followed by multiple pairs of coordinates, the subsequent pairs are treated
               //         as implicit lineto commands. Hence, implicit lineto commands will be relative if the moveto
               //         is relative, and absolute if the moveto is absolute. If a relative moveto (m) appears as the
               //         first element of the path, then it is treated as a pair of absolute coordinates. In this case,
               //         subsequent pairs of coordinates are treated as relative even though the initial moveto is interpreted
               //         as an absolute moveto.
               currOperation = (relative ? 'l': 'L');
            }
            break;
         case 'l':
            if (numStackSize >= 2)
            {
               float y = popNumber ();
               float x = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "line to " + x + ", " + y);

               if (relative)
                    currPath.rLineTo (x, y);
               else currPath.lineTo (x, y);
               lastX = x;
               lastY = y;
            }
            break;
         case 'h':
            if (numStackSize >= 1)
            {
               float x = popNumber ();
               if (relative)
                    currPath.rLineTo (x, lastY);
               else currPath.lineTo (x, lastY);
               lastX = x;
            }
            break;
         case 'v':
            if (numStackSize >= 1)
            {
               float y = popNumber ();
               if (relative)
                    currPath.rLineTo (lastX, y);
               else currPath.lineTo  (lastX, y);
               lastY = y;
            }
            break;

         case 'c':   //  Belzier curve (x1 y1 x2 y2 x y)
            if (numStackSize >= 6)
            {
               float y  = popNumber ();
               float x  = popNumber ();
               float y2 = popNumber ();
               float x2 = popNumber ();
               float y1 = popNumber ();
               float x1 = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "cubic to (" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + x + ", " + y + ")");

               if (relative)
                    currPath.rCubicTo(x1, y1, x2, y2, x, y);
               else currPath.cubicTo (x1, y1, x2, y2, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x2;
               lastCtrlPt_y = y2;
            }
            break;

         case 's':   //  Belzier curve short and smooth  (x2 y2 x y)
            if (numStackSize >= 4)
            {
               float y  = popNumber ();
               float x  = popNumber ();
               float y2 = popNumber ();
               float x2 = popNumber ();

               // smoth smart! we have to deduce the first control point x1 y1
               // either as reflection of a previous second control point x2 y2
               //
               float x1 = 0.f;
               float y1 = 0.f;
               if ("sScC".indexOf (lastOperation) != -1)
               {
                  // vRef = v0 + (v0 - vCtrl) = 2 * v0 - vCtrl
                  //
                  x1 = lastX + lastX - lastCtrlPt_x;
                  y1 = lastY + lastY - lastCtrlPt_y;
               }
               else
               {
                  x1 = lastX;
                  y1 = lastY;
               }

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "cubic to (" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + x + ", " + y + ")");

               if (relative)
                    currPath.rCubicTo(x1, y1, x2, y2, x, y);
               else currPath.cubicTo (x1, y1, x2, y2, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x2;
               lastCtrlPt_y = y2;
            }
            break;

         case 'q':   //  quadratic Belzier curve
            if (numStackSize >= 4)
            {
               float y = popNumber ();
               float x = popNumber ();
               float y1 = popNumber ();
               float x1 = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "quad to (" + x1 + ", " + y1 + ", " + x + ", " + y + ")");

               if (relative)
                    currPath.rQuadTo (x1, y1, x, y);
               else currPath.quadTo  (x1, y1, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x1;
               lastCtrlPt_y = y1;
            }
            break;

         case 't':   // "smooth"  quadratic Belzier curve (x, y)
            if (numStackSize >= 2)
            {
               float y = popNumber ();
               float x = popNumber ();

               // smoth smart! we have to deduce the first control point x1 y1
               // either as reflection of a previous second control point x2 y2
               //
               float x1 = 0.f;
               float y1 = 0.f;
               if ("tTqQ".indexOf (lastOperation) != -1)
               {
                  // vRef = v0 + (v0 - vCtrl) = 2 * v0 - vCtrl
                  //
                  x1 = lastX + lastX - lastCtrlPt_x;
                  y1 = lastY + lastY - lastCtrlPt_y;
               }
               else
               {
                  x1 = lastX;
                  y1 = lastY;
               }

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "quad to (" + x1 + ", " + y1 + ", " + x + ", " + y + ")");

               if (relative)
                    currPath.rQuadTo (x1, y1, x, y);
               else currPath.quadTo  (x1, y1, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x1;
               lastCtrlPt_y = y1;
            }
            break;

         case 'z':   //  close path
            currPath.close ();
            break;

         default:
            log.err ("tryOperation", "unknown operation [" + currOperation + "] nothing done!");
            break;
      }
      lastOperation = currOperation;
   }

   private void clear (char defaultOperation)
   {
      clearNumberStack ();
      currPath = new uniPath ();
      lastX = 0.f;
      lastY = 0.f;

      lastCtrlPt_x = 0.f;
      lastCtrlPt_y = 0.f;
      currOperation = defaultOperation;
      lastOperation = defaultOperation;
   }

   public uniPath parsePath (String pathData)
   {
      return parsePath (pathData, 'l');
   }

   /**
      Parses a string containing a svg path and returns a new android.graphics.Path object
   */
   public uniPath parsePath (String pathData, char defaultOperation)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgPath", "path  [" + pathData + "]");

      char what = ' ';
      boolean relative = true;
      int pt = 0;
      String strNum = "";

      clear (defaultOperation);

      int plen = pathData.length ();

      while (pt < plen)
      {
         what = pathData.charAt (pt ++);
         switch (what)
         {
            case '\t':
            case ' ':
            case ',':
            case '+':
               pushNumber (strNum);
               strNum = "";
               tryOperation ();
               break;

            case '-':
               // minus ("-") acts as separator as well
               // plus it has to be added
               pushNumber (strNum);
               strNum = "";
               tryOperation ();
               strNum = "" + what;
               break;


            case 'e':  case 'E':
               strNum += what;
               // treat special cases!
               //    123e-3 456e+4  ==> [123e-3]  [456e+4]
               //    123-456        ==> [123]     [-456]
               //    123e-9-456     ==> [123e-9]  [-456]     !!
               //    123e -9-456    ==> [123e -9] [-456]     (is that possible ?)
               if (pt < plen)
                  strNum += pathData.charAt (pt ++);
               break;
            case '0':  case '.':
            case '1':  case '2':  case '3':
            case '4':  case '5':  case '6':
            case '7':  case '8':  case '9':
               strNum += what;
               break;
            default:
               currOperation = what;
               break;
         }
      }

      // remanent number and operation
      pushNumber (strNum);
      tryOperation ();

      return currPath;
   }
}
