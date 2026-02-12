/*
library listix (www.listix.org)
Copyright (C) 2005-2021 Alejandro Xalabarder Aulet

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

package listix.cmds;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import java.awt.image.*;
import de.elxala.langutil.graph.*;

public class cmdStrconvert extends baseCmdStrconvert
{
   protected void dosD2File (String graffitiFormat, Eva source, String fileName, String fileformat, String sdx, String sdy)
   {
      int dx = stdlib.atoi (sdx);
      int dy = stdlib.atoi (sdy);

      String fileFormat = fileformat.equals ("") ? "png": fileformat;
      BufferedImage ima = null;
      if (dx > 0)
           ima = uniUtilImage.graffitiToBufferedImage (source, graffitiFormat, dx, dy == 0 ? dx: dy, null);
      else ima = uniUtilImage.graffitiToBufferedImage (source, graffitiFormat);

      uniUtilImage.saveBufferedImageTofile (ima, fileName, fileFormat);
   }
}
