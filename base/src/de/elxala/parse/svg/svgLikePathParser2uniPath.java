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
import de.elxala.math.space.*;   // vect3f
import de.elxala.math.space.curve.*;   // 


/**
   Facility to parse a SVG - or SVG like - path tag into an uniPath which is an
   abstraction of the classes

      android.graphics.Path  for android
      java.awt.Shape for standard java

   for instance, given the path

      path="M 10 10 c 8 2 4 5"

   the contents will be parsed and an uniPath returned

   Note that, as the name indicates, it is not strictly a SVG path parser

      - it may not support path elements from the SVG specification
      - it may admit other path elements not supported by the SVG specification (i.e. 'j' Casteljau-Bezier curve with auto control points)
*/
public class svgLikePathParser2uniPath
{
   //30.01.2012 Experimental effect, found by chance, there has to be much more possibilities
   //           changing slightly parameters, adding random values etc..
   //
   public static boolean DRAWDRAFT_EFFECT_ENABLED = false;

   protected static logger log = new logger (null, "de.elxala.parse.svg.svgLikePathParser2uniPath", null);

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
   
   private static final char ASSIGNED_OP_RECT = '5';
   private static final char ASSIGNED_OP_CIRC = '6';
   private static final char ASSIGNED_OP_OVAL = '7';
   private static final char ASSIGNED_OP_ARC = '8';
   

   private void tryOperation ()
   {
      boolean relative = currOperation >= 'a' || (currOperation >= '0' && currOperation <= '9');
      char lowOper = relative ? currOperation : (char) (currOperation + ('a'-'A'));

      if (log.isDebugging (4))
         log.dbg (4, "tryOperation", "currOperation [" + currOperation + "] relative " + relative + " stack size " + numStackSize);

      switch (lowOper)
      {
         case 'a': // eliptic arc
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
               currPath.getEdiPaths ().addArc (new uniRect (x, y, x+rx, y+ry), largearc, sweep);
               lastX = x;
               lastY = y;
            }
            break;
         case 'm': // move to
            if (numStackSize >= 2)
            {
               float y = popNumber ();
               float x = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "move to " + x + ", " + y);

               if (relative)
                  currPath.getEdiPaths ().rMoveTo (x, y);
               else currPath.getEdiPaths ().moveTo (x, y);

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
         case 'l': // lineto
            if (numStackSize >= 2)
            {
               float y = popNumber ();
               float x = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "line to " + x + ", " + y);

               if (relative)
                    currPath.getEdiPaths ().rLineTo (x, y);
               else currPath.getEdiPaths ().lineTo (x, y);
               lastX = x;
               lastY = y;
            }
            break;
         case 'h': //horizontal lineto
            if (numStackSize >= 1)
            {
               float x = popNumber ();
               if (relative)
                    currPath.getEdiPaths ().rLineTo (x, lastY);
               else currPath.getEdiPaths ().lineTo (x, lastY);
               lastX = x;
            }
            break;
         case 'v': //vertical lineto
            if (numStackSize >= 1)
            {
               float y = popNumber ();
               if (relative)
                    currPath.getEdiPaths ().rLineTo (lastX, y);
               else currPath.getEdiPaths ().lineTo  (lastX, y);
               lastY = y;
            }
            break;

         case 'c': // curveto,  Casteljau-Bezier curve (x1 y1 x2 y2 x y) (also known as Bezier curve but actually invented by Paul de Casteljau in Citroen)
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
                    currPath.getEdiPaths ().rCubicTo(x1, y1, x2, y2, x, y);
               else currPath.getEdiPaths ().cubicTo (x1, y1, x2, y2, x, y);

               if (DRAWDRAFT_EFFECT_ENABLED)
               {
                  if (relative)
                       currPath.getEdiPaths ().rCubicTo(x, y, x2, y2, x1, y1);
                  else currPath.getEdiPaths ().cubicTo (x, y, x2, y2, x1, y1);
               }
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x2;
               lastCtrlPt_y = y2;
            }
            break;

         case 's':   //  Casteljau-Bezier curve short and smooth  (x2 y2 x y)
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
                    currPath.getEdiPaths ().rCubicTo(x1, y1, x2, y2, x, y);
               else currPath.getEdiPaths ().cubicTo (x1, y1, x2, y2, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x2;
               lastCtrlPt_y = y2;
            }
            break;

         case 'q':   //  quadratic Casteljau curve
            if (numStackSize >= 4)
            {
               float y = popNumber ();
               float x = popNumber ();
               float y1 = popNumber ();
               float x1 = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "quad to (" + x1 + ", " + y1 + ", " + x + ", " + y + ")");

               if (relative)
                    currPath.getEdiPaths ().rQuadTo (x1, y1, x, y);
               else currPath.getEdiPaths ().quadTo  (x1, y1, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x1;
               lastCtrlPt_y = y1;
            }
            break;

         case 't':   // "smooth"  quadratic Casteljau-Bezier curve (x, y)
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
                    currPath.getEdiPaths ().rQuadTo (x1, y1, x, y);
               else currPath.getEdiPaths ().quadTo  (x1, y1, x, y);
               lastX = x;
               lastY = y;
               lastCtrlPt_x = x1;
               lastCtrlPt_y = y1;
            }
            break;

         case 'z':   //  close path
            currPath.getEdiPaths ().close ();
            break;

         //
         //===== NOT SVG OPERATIONS BUT EXTENSIONS
         //
            
         case 'x':   // 
         case 'j':   //  Casteljau-Bezier curve with automatic control points (~ powerpoint free curves)
            if (numStackSize >= 2)
            {
               float y  = popNumber ();
               float x  = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "Casteljau-Bezier curve with auto control points to (" + x + ", " + y + ")");

               if (relative)
                    currPath.getEdiPaths ().rAutoCasteljauPoint (x, y);
               else currPath.getEdiPaths ().autoCasteljauPoint (x, y);
               
               lastX = x;
               lastY = y;
            }
            break;
            
          case ASSIGNED_OP_RECT:
            if (numStackSize >= 4)
            {
               float ry  = popNumber ();
               float rx  = popNumber ();
               float dy = popNumber ();
               float dx = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "round rectangle (" + dx + ", " + dy + "), (" + rx + ", " + ry + ")");

               currPath.getEdiPaths ().rRoundRectTo (dx, dy, rx, ry);
            }
            break;

          case ASSIGNED_OP_CIRC:
            if (numStackSize >= 1)
            {
               float radio  = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "circle radius " + radio);

               currPath.getEdiPaths ().rCircleTo (radio);
            }
            break;

         case ASSIGNED_OP_OVAL:
            if (numStackSize >= 2)
            {
               float dy = popNumber ();
               float dx = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "oval to (" + dx + ", " + dy + ")");

               //uniUtil.printLater ("ASSIGNED_OP_OVAL...oval to (" + dx + ", " + dy + ")");
               currPath.getEdiPaths ().rOvalTo (dx, dy);
            }
            break;

         case ASSIGNED_OP_ARC:
            if (numStackSize >= 4)
            {
               float sweepAng = popNumber ();
               float startAng = popNumber ();
               float dy = popNumber ();
               float dx = popNumber ();

               if (log.isDebugging (4))
                  log.dbg (4, "tryOperation", "arc to (" + dx + ", " + dy + ") start angle " + startAng + ", sweep angle " + sweepAng);

               currPath.getEdiPaths ().rArcTo (dx, dy, startAng, sweepAng);
            }
            break;

        default:
            log.err ("tryOperation", "unknown operation [" + currOperation + "] nothing done!");
            break;
      }
      lastOperation = currOperation;
   }

   private void clear ()
   {
      clearNumberStack ();
      lastX = 0.f;
      lastY = 0.f;

      lastCtrlPt_x = 0.f;
      lastCtrlPt_y = 0.f;
   }

   public uniPath parsePath (String pathData)
   {
      return parsePath (pathData, 'l');
   }

   /**
      Parses a string containing a svg path and returns a new uniPath object
      where uniPath is a java.awt.Shape in standard java
      or android.graphics.Path in android's java
   */
   public uniPath parsePath (String pathData, char defaultOperation)
   {
      currPath = new uniPath ();
      currOperation = defaultOperation;
      lastOperation = defaultOperation;
      parsePathStringOnUnipath (currPath, pathData);
      return currPath;
   }

   /**
      Parses a string containing a svg path into a uniPath object
      where uniPath is a java.awt.Shape in standard java
      or android.graphics.Path in android's java
   */
   public void parsePathStringOnUnipath (uniPath unip, String pathData)
   {
      currPath = unip;
      if (log.isDebugging (4))
         log.dbg (4, "parsePath", "path  [" + pathData + "]");

      char what = ' ';
      boolean relative = true;
      int pt = 0;
      String strNum = "";

      clear ();

      //Trazo compatibility: CALL THIS after reseting the current path!
      //
      pt = trazoStarted (pathData);

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
               pushNumber (strNum);
               strNum = "";
               tryOperation ();
               currOperation = what;
               break;
         }
      }

      // remanent number and operation
      pushNumber (strNum);
      tryOperation ();
   }
   
   // trick to admit trazo paths as well
   //  e.g.
   //       "jauz 10 2 -1 20 5 5"
   //
   protected int trazoStarted (String strpath)
   {
      boolean isClosed = false;
      if (strpath.startsWith ("rec"))
      {
         currOperation = lastOperation = ASSIGNED_OP_RECT;
      }
      else
      if (strpath.startsWith ("cir"))
      {
         currOperation = lastOperation = ASSIGNED_OP_CIRC;
      }
      else  
      if (strpath.startsWith ("ova"))
      {
         currOperation = lastOperation = ASSIGNED_OP_OVAL;
      }
      else
      if (strpath.startsWith ("arc"))
      {
         currOperation = lastOperation = ASSIGNED_OP_ARC;
         isClosed = strpath.startsWith ("arcz");
      }
      else
      if (strpath.startsWith ("pol"))
      {
         currOperation = lastOperation = 'l';
         isClosed = strpath.startsWith ("polz");
      }
      else
      if (strpath.startsWith ("cub"))
      {
         currOperation = lastOperation = 'c';
         isClosed = strpath.startsWith ("cubz");
      }
      else
      if (strpath.startsWith ("qua"))
      {
         currOperation = lastOperation = 'q';
         isClosed = strpath.startsWith ("quaz");
      }
      else
      if (strpath.startsWith ("jau"))
      {
         currOperation = lastOperation = 'j';
         isClosed = strpath.startsWith ("jauz");
      }
      else
      {
         return 0;
      }
      //currPath.getEdiPaths ().moveTo (0.f, 0.f);
      if (isClosed)
         currPath.getEdiPaths ().close ();
      return 3 + (isClosed ? 1: 0);
   }
}
