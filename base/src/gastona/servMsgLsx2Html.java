/*
library de.elxala
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

package gastona;

import listix.*;
import listix.util.*;
import listix.cmds.commandable;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/**
   This module allows using lsx2Html from listix (note that listix knows nothing about mensaka!)

   This is done using the external listix command MSG with following messages

      "listix2Html targetHtmlFile",  pass in <targetHtmlFile> the target dir
      "listix2Html data",            pass the data
      "listix2Html formats",         pass the formats
      "listix2Html doit",            generates the file

   thus in listix (GUI) the following listix commands would create and show an html file
   of the internal data and formats

      MSG, "listix2Html data",     D
      MSG, "listix2Html formats",  F
      SET VAR, targetHtmlFile, tmpIntern.html
      MSG, "listix2Html targetHtmlFile", D

      MSG, "listix2Html doit"
      CALL, //CMD /C start "" "tmpIntern.html"
*/
public class servMsgLsx2Html implements MensakaTarget
{
   private static final int RX_FILENAME = 10;
   private static final int RX_DATA = 11;
   private static final int RX_FORMATS = 12;
   private static final int RX_GO = 13;

   private EvaUnit theData = null;
   private EvaUnit theFormats = null;
   private String fileName = "listix2HtmlFile.html";

   public servMsgLsx2Html ()
   {
      Mensaka.suscribe (this, RX_FILENAME, "listix2Html targetHtmlFile");
      Mensaka.suscribe (this, RX_DATA,     "listix2Html data");
      Mensaka.suscribe (this, RX_FORMATS,  "listix2Html formats");
      Mensaka.suscribe (this, RX_GO,       "listix2Html doit");
   }

   public boolean takePacket (int map, EvaUnit data)
   {
      switch (map)
      {
         case RX_FILENAME:
            fileName = data.getSomeHowEva ("targetHtmlFile").getValue (0,0);
            break;
         case RX_DATA:
            theData = data;
            break;
         case RX_FORMATS:
            theFormats = data;
            break;
         case RX_GO:
            {
               lsx2Html amigo = new lsx2Html ();
               amigo.start ("intern listix", theData, theFormats, fileName);
            }
            break;
      }

      return true;
   }
}
