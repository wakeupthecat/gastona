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

import javaj.widgets.graphics.*;

import de.elxala.langutil.*;
import de.elxala.zServices.*;

/**
   Facility to parse svg style tag into a android.graphics.Paint object
*/
public class svgStyle2GraphicsPaint
{
   private logger log = svgPath2GraphicsPath.log;

   /*
      style="fill:#ffffff;fill-opacity:1;fill-rule:evenodd;stroke:none"
   */

   private String svgStyleStr = "";
   private int indxOffset = 0;
   private String svgAttName = "";
   private String svgAttValue = "";

   private boolean fillInfo = false;
   private boolean strokeInfo = true;  // default

   private boolean extractPair ()
   {
      if (svgStyleStr == null || indxOffset >= svgStyleStr.length ()) return false;
      // extract Name
      while (indxOffset < svgStyleStr.length() &&
             (svgStyleStr.charAt (indxOffset) == ';' ||
              svgStyleStr.charAt (indxOffset) == ' '))
         indxOffset ++;

      svgAttName = "";
      while (indxOffset < svgStyleStr.length() && svgStyleStr.charAt (indxOffset ++) != ':')
         svgAttName += svgStyleStr.charAt (indxOffset - 1);

      // extract Value
      svgAttValue = "";
      while (indxOffset < svgStyleStr.length() && svgStyleStr.charAt (indxOffset ++) != ';')
         svgAttValue += svgStyleStr.charAt (indxOffset - 1);

      return true;
   }

   public boolean hasFillInfo () { return fillInfo; }
   public boolean hasStrokeInfo () { return strokeInfo; }

   /**
      parses a svg style (path's style) and fills the corresponding Paint
      objects paintForFill and paintForStroke
   */
   public void parseStyle (String svgStyle, uniPaint paintForFill, uniPaint paintForStroke)
   {
      fillInfo = false;
      strokeInfo = true;  // default

      float fillOpacity = 1.f;      // opcacity can be superposed
      float strokeOpacity = 1.f;    // opcacity can be superposed

      indxOffset = 0;
      svgStyleStr = svgStyle;
      while (extractPair ())
      {
         // !! too much debug !!
         if (log.isDebugging (6))
            log.dbg (6, "parseStyle", "attribute pair [" + svgAttName + "] : [" + svgAttValue + "]");

         if (svgAttName.equals ("opacity") || svgAttName.equals ("op"))
         {
            // affects both stroke and fill
            fillOpacity   *= stdlib.atof (svgAttValue);
            strokeOpacity *= stdlib.atof (svgAttValue);

            paintForFill.setAlpha ((int) (255 * fillOpacity));
            paintForStroke.setAlpha ((int) (255 * strokeOpacity));
         }
         if (svgAttName.equals ("fill") || svgAttName.equals ("fc"))
         {
            fillInfo = ! svgAttValue.equals ("none");
            if (fillInfo)
               paintForFill.setColorRGB (intParseColor (svgAttValue));
            continue;
         }
         if (svgAttName.equals ("fill-opacity") || svgAttName.equals ("fo"))
         {
            fillInfo = true;
            fillOpacity *= stdlib.atof (svgAttValue);
            // 0.0 to 1.0 ===> the range 0.0 (fully transparent) to 1.0 (fully opaque)
            //  0  to 255 ===> where 0 represents a fully transparent color, and 255 represents a fully opaque color.
            paintForFill.setAlpha ((int) (255 * stdlib.atof (svgAttValue)));
            continue;
         }
         if (svgAttName.equals ("fill-rule"))
         {
            fillInfo = true;
            // evenodd etc
            //(o) TODO implement fill-rule if supported by Paint
            continue;
         }
         if (svgAttName.equals ("stroke") || svgAttName.equals ("sc"))
         {
            strokeInfo = ! svgAttValue.equals ("none");
            if (strokeInfo)
               paintForStroke.setColorRGB (intParseColor (svgAttValue));
            continue;
         }
         if (svgAttName.equals ("stroke-width") || svgAttName.equals ("sw"))
         {
            strokeInfo = true;
            paintForStroke.setStrokeWidth ((float) stdlib.atof (svgAttValue));
            continue;
         }
         if (svgAttName.equals ("stroke-opacity") || svgAttName.equals ("so"))
         {
            strokeInfo = true;
            strokeOpacity *= stdlib.atof (svgAttValue);
            // 0.0 to 1.0 ===> the range 0.0 (fully transparent) to 1.0 (fully opaque)
            //  0  to 255 ===> where 0 represents a fully transparent color, and 255 represents a fully opaque color.
            paintForStroke.setAlpha ((int) (255 * strokeOpacity));
            continue;
         }
         if (svgAttName.equals ("font-size") || svgAttName.equals ("fs"))
         {
            strokeInfo = true;
            paintForStroke.setTextSize ((float) stdlib.atof (svgAttValue));
            continue;
         }
      }
   }

   public float parseTransformationFromStyle (String svgStyle)
   {
      float rotation = 0.f;

      indxOffset = 0;
      svgStyleStr = svgStyle;
      while (extractPair ())
      {
         //System.out.println ("VISESO " + svgAttName + "[" + svgAttValue+ "]");
         if (svgAttName.equals ("rotate") || svgAttName.equals ("rotation"))
         {
            rotation = (float) stdlib.atof (svgAttValue);
         }
      }
      return rotation;
   }

   public int intParseColor (String colorStr)
   {
      uniColor theCol = new uniColor ();

      try
      {
         theCol.parseColor (colorStr);
      }
      catch (Exception e)
      {
         log.err ("parseStyle", "Exception parsing color (" + colorStr + ") " + e);
      }

      return theCol.toInt ();
   }
}
