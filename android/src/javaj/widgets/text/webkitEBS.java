/*
package javaj.widgets
Copyright (C) 2016 Alejandro Xalabarder Aulet

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

package javaj.widgets.text;

import de.elxala.langutil.*;      // for attributes of type "int"
import de.elxala.Eva.*;
import javaj.widgets.*;
import javaj.widgets.basics.*;
import de.elxala.zServices.*;

/**
  Generic text EvaBasedStructure (EBS) used in all text based zWidgets
*/
public class webkitEBS extends smallTextEBS
{
   // Attributes
   //
   public static final String sATTR_URL      = "url";
   public static final String sATTR_FILENAME = "fileName";
   public static final String sATTR_MIMETYPE = "mimeType";
   public static final String sATTR_ENCODING = "encoding";
   public static final String sATTR_ENABLEJS = "enableJS";
   public static final String sATTR_ENABLEJSACTION = "enableJSAction";


   // Messages
   //
   public static final String sMSG_LOAD_URL  = "loadUrl";
   public static final String sMSG_LOAD_FILE = "loadFile";
   public static final String sMSG_RUN_JS    = "runJs";

   // Signals
   //
   public static final String sSIGNAL_JS_ACTION  = "jsAction";  // injected javascript calls jsAction
   public static final String sSIGNAL_RUN_JS_FINISH = "runJsFinished";

   private static logger logStatic = new logger (null, "javaj.widgets.webkit", null);
   public logger log = logStatic;

   /// Constructor
   //
   public webkitEBS (String nameWidget, EvaUnit pData, EvaUnit pControl)
   {
      super (nameWidget, pData, pControl);

      // ensure default "javajUI.text.*" resources
      javaj.globalJavaj.ensureDefRes_javajUI_text ();
   }


   public String getAttStr (String attribute)
   {
      return getSimpleAttribute (DATA, attribute, null);
   }

   public void setAttStr (String attribute, String value)
   {
      setSimpleAttribute (DATA, attribute, value);
   }

   public String getUrl       () { return getSimpleAttribute (DATA, sATTR_URL, null);       }
   public String getFileName  () { return getSimpleAttribute (DATA, sATTR_FILENAME, null);  }
   public String getMimeType  () { return getSimpleAttribute (DATA, sATTR_MIMETYPE, "text/html");  }
   public String getEncoding  () { return getSimpleAttribute (DATA, sATTR_ENCODING, null);  }

   //DEFAULT 0 (no javascript enabled by default)
   public boolean getEnableJS () { return "1".equals (getSimpleAttribute (DATA, sATTR_ENABLEJS, "0"));  }

   //DEFAULT 1 (if javascript enabled then default is enabling JSAction)
   //
   //  by enabling JSAction basically the javascript code has access to the object "javaj"
   //  and can call javaj.jsAction (String []) which returns a single string in turn
   //  but also fills the variable <"webkitname" jsActionResponse>
   //
   public boolean getEnableJSAction () { return "1".equals (getSimpleAttribute (DATA, sATTR_ENABLEJSACTION, "1"));  }


   public void setUrl        (String value) { setSimpleAttribute (CONTROL, sATTR_URL, value); }
   public void setFileName   (String value) { setSimpleAttribute (DATA, sATTR_FILENAME, value); }
   public void setMimeType   (String value) { setSimpleAttribute (DATA, sATTR_MIMETYPE, value); }
   public void setEncoding   (String value) { setSimpleAttribute (DATA, sATTR_ENCODING, value); }
}
