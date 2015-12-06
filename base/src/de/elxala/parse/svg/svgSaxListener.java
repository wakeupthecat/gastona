/*
de.elxala.parse.xml
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

//import javax.xml.parsers.*;
import org.xml.sax.*;
//import org.xml.sax.helpers.*;


public interface svgSaxListener
{
   public void processSvgSetDimension (String swidth, String sheight);
   public void processSvgPath (String pathData, String pathStyle, boolean polygonOrPolyline, boolean closePath);
   public void processSvgRect (float x, float y, float dx, float dy, float rx, float ry, String pathStyle);
   public void processSvgCircle  (float xcenter, float ycenter, float radio, String pathStyle);
   public void processSvgEllipse (float cx, float cy, float rx, float ry, String pathStyle);
   public void processSvgText (String strData, Attributes textAttributes);
}


/*

void 	addCircle(float x, float y, float radius, Path.Direction dir)
void 	addOval(RectF oval, Path.Direction dir)

*/



