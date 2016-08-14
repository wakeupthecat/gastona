/*
package de.elxala.zWidgets
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

package javaj.widgets;

import android.webkit.WebView;
import android.content.Context;
import android.text.TextWatcher;
import android.text.Editable;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.graph.sysFonts;

import javaj.widgets.basics.*;
import javaj.widgets.text.*;

import javaj.*;
import android.os.Handler;
import android.os.Message;


/*
   //(o) WelcomeGastona_source_javaj_widgets (w) zWebkit

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zWebkit
   <groupInfo>  android_internet
   <javaClass>  javaj.widgets.zWebit
   <prefix>     w
   <importance> 8
   <desc>       //Web browser based on webkit (only android)

   <help>
      // NOTE: zWebkit is only an android widget
      //
      // Web browser based on Webkit - actually a android.webkit.WebView object - to display static and/or
      // dynamic html5, css and javascript contents.
      //
      // Listix command MICOHTTP can be used as http server to provide content to the zWebkit
      // widget(s) making possible developing actual Apps based on html5, CSS and javascript.
      //
      //--- Basic usage
      //
      //---- Setting url
      //
      // Using the attribute url
      //
      //      <myWebkit url> www.wakeupthecat.com
      //      ...
      //      <myWebkit url> http://localhost:7171
      //
      // Or sending a message
      //
      //      -->, myWebkit loadUrl, www.wakeupthecat.com
      //
      //---- Setting directly html/css/javascript content
      //
      // Using the value attribute
      //
      //     #data#
      //       ...
      //       <myWebkit> www.wakeupthecat.com
      //
      // Or sending a message
      //
      //      -->, myWebkit data!,, //<html><h1> Hello! </h1></html>
      //
      //---- Loading contents from a file
      //
      // Using the attribute fileName
      //
      //     #data#
      //       ...
      //       <myWebkit fileName> myapp/web/index.html
      //
      // Or sending a message
      //
      //      -->, myWebkit loadFile, myapp/web/index.html
      //
      //
      //--- Advanced
      //
      // The listix command MICOHTTP can be used as http server for the zWebkit content.
      //
      // This might be done with or without enabling javascript. The last option is needed
      // of course if want to use ajax requests agains MICO server.
      //
      // If javascript is enabled, then javascript code can be called from the host and viceversa
      // javascript code can interact with the host script by using the jsAction mechanism
      //
      //---- runJs
      //
      // The host script can call an available javascript function sending a list of strings as parameters
      // for example
      //
      //        -->, myWeb runJs, myFunction, par1, par2, etc
      //
      // where "myFunction" has to be an existing function in the zWebkit "myWeb". The function might return
      // a value using ajax or the jsAction mechanism. For example
      //
      //
      //       function myFunction (reqtype, param1)
      //       {
      //           var out = processRequest (reqtype, param1);
      //           javaj.jsAction (["response", reqtype, out]);
      //       }
      //
      //---- jsAction activation
      //
      // If javascript is enabled then jsAction is activated by default. But if desired it can be explicitly
      // deactivated using, for example
      //
      //          <myWeb enableJS> 1
      //          <myWeb enableJSAction> 0
      //
      // Note: jsAction use webview addJavascriptInterface mechanism. For security issues gastona permits
      //       only jsAction if the android version of the device is JELLY_BEAN_MR1 or later and only
      //       allows during its activation loading local URLs or files.
      //
      //---- jsAction activation
      //
      // Once activated an extra method is available in javascript which is javaj.jsAction. This method
      // admits a string array as parameter and it returns a string. In java notation
      //
      //        String jsAction (String [] args);
      //
      // An example of a javascript code calling the method
      //
      //        var result = javaj.jsAction (["myPeticion", "myName", "myTelephone"]);
      //
      // When the method is called from javascript then it produces a message called jsAction, the
      // gastona script may then do whatever it wants and when finish the value of the variable/attribute
      // jsActionResponse will be returned to the javascript caller.
      //
      // So an example of handling a jsAction call can be
      //
      //       <-- myWeb jsAction>
      //           LSX, doSomeStuff, @<p1>
      //           LSX, do more stuff, @<p1>, @<p2>
      //           VAR=, myWeb jsActionResponse, "ok!"

   <prefix>  w

   <attributes>
      name             , in_out, possibleValues , desc

                       , in    , text           , //Html/css/javascript content
      visible          , in    , 0 / 1          , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1          , //Value 0 to disable the widget
      enableJS         , in    , 0 / 1          , //Enables javascript
      enableJSAction   , in    , 1 / 0          , //Enables in javascript the call javaj.jsAction (see jsAction). Default value is 1 (it requires enableJS to 1 as well)
      url              , in    , string         , //Source url to render
      fileName         , in    , 0 / 1          , //Enable javascript
      mimeType         , in    , text           , //mimeType for loadUrl, default value is text/html
      encoding         , in    , text           , //encoding for loadUrl
      jsActionResponse , in    , text           , //The content of this attribute will be send to zWebkit as the return of the javascript call jsAction

   <messages>

      msg, in_out, desc

      data!         , in  , update data
      control!      , in  , update control
      runJs         , in  , Run a given javascript function with parameters in the Webkit component. To return a value the js function might use javaj.jsAction function
      jsAction      , out , A call of javaj.jsAction has been trigered, with the parameters of the message


   <examples>
      gastSample

      basic webkits
      webkit dialog

   <basic webkits>
       //#javaj#
       //
       //   <frames> main
       //
       //   <layout of main>
       //       EVA,
       //          , X
       //          , lURL
       //       X  , wSuela
       //          , lHTML
       //       X  , wGuela
       //
       //#data#
       //
       //   <wSuela url> http://www.wakeupthecat.com
       //
       //   <wGuela>
       //       //<html><body>
       //       //    <h1>Hola webkit!</h1>
       //       //</body></html>


   <webkit dialog>
      //#javaj#
      //
      //   <frames> main,
      //
      //   <layout of main>
      //      EVA, 4, 4, 3, 3
      //         , X
      //       X , wEby
      //         , bSet to client
      //       X , oSal
      //
      //#data#
      //
      //   <wEby>
      //      //<html><body>
      //      //    <h1>Test zWebkit widget</h1>
      //      //
      //      //    <button id="askHost" onclick="setText (javaj.jsAction (['primer', 'segon']));"> askHost </button>
      //      //
      //      //    <h2 id="salida1">nontiendo</h2>
      //      //
      //      //    <script>
      //      //       function setText (str)
      //      //       {
      //      //           document.getElementById('salida1').innerHTML = str;
      //      //       }
      //      //       function paya(str)
      //      //       {
      //      //           setText (str);
      //      //           javaj.jsAction (["contentSet", str ]);
      //      //       }
      //      //    </script>
      //      //
      //      //</body></html>
      //
      //   <wEby enableJS> 1
      //   <wEby enableJSAction> 1
      //
      //#listix#
      //
      //   <main>
      //
      //   <-- wEby jsAction>
      //      //
      //      //jsAction from zWebkit!
      //      //   p1 = [@<p1>]
      //      //   p2 = [@<p2>]
      //      VAR=, wEby jsActionResponse, //acina quan es menja ?
      //
      //   <-- bSet to client>
      //      //
      //      //Call from script
      //      //
      //      -->, wEby runJs, paya, Esto te lo pongo yo!
      //      //
       
       
       
#**FIN_EVA#

*/


/**
*/
public class zWebkit extends WebView
                     implements MensakaTarget, izWidget
{
   private webkitAparato helper = null;

   public zWebkit (Context co)
   {
      super(co);
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zWebkit (Context co, String map_name)
   {
      super(co);
      setName (map_name);
   }

   private boolean isAndroidSafeVersion ()
   {
      // see android developer documentation about WebView and addJavascriptInterface
      //
      // return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
      return android.os.Build.VERSION.SDK_INT >= 0x00000011; // (no JELLY_BEAN_MR1) I am using a old sdk to develop :o !
   }

   private boolean isJSEnabled = false;
   private boolean isJSActionActivated = false;

   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return androidSysUtil.getHeightChars(5.0f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (30); }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
      build (map_name);
   }
   //---

   private void build (String map_name)
   {
      helper = new webkitAparato (this, new webkitEBS (map_name, null, null));
   }

   protected static final int HANDLER_OP_SETURL   = 0;
   protected static final int HANDLER_OP_SETTEXT  = 1;

   // (see note in zConsole Handler)
   //
   protected final Handler UIhandler = new Handler() {
      public void handleMessage(Message msg)
      {
         int op = msg.getData().getInt("operation");
         String value = msg.getData().getString("value");

         switch (op)
         {
            case HANDLER_OP_SETURL:
               loadUrl (value);
               break;
            case HANDLER_OP_SETTEXT:
               loadData (value, helper.ebs ().getMimeType (), helper.ebs ().getEncoding ());
               break;

            default:
               break;
         }
      }
   };

   protected void attakWidget (int op, String value)
   {
      utilHandler.sendToWidget (UIhandler, op, value);
   }

   //-i- interface MensakaTarget ---------------------------------------
   //
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (!widgetLogger.updateContainerWarning ("data", "zWebkit",
                                             helper.ebs ().getName (),
                                             helper.ebs ().getData (),
                                             euData))
            {
               helper.ebs ().setDataControlAttributes (euData, null, pars);
               tryAttackWidget ();
            }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (!widgetLogger.updateContainerWarning ("control", "zWebkit",
                                             helper.ebs ().getName (),
                                             helper.ebs ().getControl (),
                                             euData))
            {
               helper.ebs ().setDataControlAttributes (null, euData, pars);
               if (helper.ebs().firstTimeHavingDataAndControl ())
               {
                  tryAttackWidget ();
               }

               updateControl ();
            }
            break;

         case webkitAparato.RX_COMMNAD_LOAD_FILE:
            if (checkCommandAndParams (helper.ebs().sMSG_LOAD_FILE, pars, 0, 1))
               commandLoadFile (pars == null ? null: pars[0]);
            break;

         case webkitAparato.RX_COMMNAD_LOAD_URL:
            if (checkCommandAndParams (helper.ebs().sMSG_LOAD_URL, pars, 0, 1))
               commandLoadUrl (pars == null ? null: pars[0]);
            break;

         case webkitAparato.RX_COMMNAD_RUN_JS:
            if (checkCommandAndParams (helper.ebs().sMSG_RUN_JS, pars, 1, 99))
               commandRunJs (pars);
            break;


         default:
            return false;
      }

      return true;
   }
   //-i- -------------------------------------------------------


   private void updateControl ()
   {
      helper.updateControl (this);
   }

   private void tryAttackWidget ()
   {
      if (helper.ebs().hasAll ())
      {
         configureWebkit ();
         if (helper.ebs ().getUrl () != null)
            attakWidget (HANDLER_OP_SETURL, helper.ebs ().getUrl ());
         else
         if (helper.ebs ().getFileName () != null)
            commandLoadFile (null);
         else
            attakWidget (HANDLER_OP_SETTEXT, helper.ebs ().getText ());
      }
   }


   private void configureWebkit ()
   {
      //(o) TODO/zWebkit allow disabling this ?
      //getWindow().requestFeature(Window.FEATURE_PROGRESS);

      isJSEnabled = false;
      isJSActionActivated = false;
      if (helper.ebs ().getEnableJS ())
      {
         isJSEnabled = true;
         helper.log.dbg (2, "configureWebkit",
                         helper.ebs().evaName ("") +
                         " enableJS " + helper.ebs ().getEnableJS () +
                         ", enableJSAction " + helper.ebs ().getEnableJSAction ());
         getSettings().setJavaScriptEnabled (true);
         if (helper.ebs ().getEnableJSAction ())
         {
            if (isAndroidSafeVersion ())
            {
               //(o) Android/javaj/zWebkit Allow JSAction (JavascriptInterface) only if android version of device is greater that KITKAT for safety reasons
               //
               isJSActionActivated = true;
               addJavascriptInterface(new jsActionInterface (), "javaj");
            }
            else
               helper.log.err ("configureWebkit", helper.ebs().evaName ("") +
                              " enableJSAction is true but android version of device (" + android.os.Build.VERSION.SDK_INT + ")" +
                              " is not safe enough for using addJavascriptInterface");
         }
      }
      else helper.log.dbg (2, "configureWebkit", helper.ebs().evaName ("") + " enableJS " + helper.ebs ().getEnableJS ());
   }

   //public class wvClient extends WebViewClient {
   // @Override
   // public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
   //
   //         Toast.makeText(androidSysUtil.getMainActivity (), message, Toast.LENGTH_SHORT).show();
   //
   //         Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
   //         super.onReceivedError(view, errorCode, description, failingUrl);
   // }
   //});

   public class jsActionInterface
   {
     jsActionInterface () {}

     //@JavascriptInterface
     public String jsAction (String [] argsgs)
     {
         helper.log.dbg (2, "zWebkit.jsAction", "actions called with " +  (argsgs != null ? argsgs.length: -1) + " arguments");
         helper.signalJsAction (argsgs);

         String strReto = helper.ebs ().getSimpleAttribute (helper.ebs ().DATA, "jsActionResponse", "");
         helper.log.dbg (2, "zWebkit.jsAction", "return value [" +  strReto + "]");
         return strReto;
     }
   }

   private boolean checkCommandAndParams (String cmdName, String [] params, int minPar, int maxPar)
   {
      String reason = "";

      if (! helper.ebs().hasAll ())
      {
         reason = "zWidget has no data nor control";
      }
      else if ((params == null && minPar > 0) || (params != null && (params.length < minPar || params.length > maxPar)))
      {
         reason = "wrong number of parameters (" + (params == null ? 0: params.length) + ")";
      }

      if (reason.length() > 0)
         helper.log.err ("zWebkit.command" + cmdName, reason + ".Message [" + helper.ebs().evaName (cmdName) + "] ignored");
      return reason.length() == 0;
   }


   private void commandLoadFile (String fileName)
   {
      if (fileName != null)
         helper.ebs ().setFileName (fileName);

      helper.ebs ().setText ("");

      // has fileName ?
      fileName = helper.ebs ().getFileName ();
      if (fileName == null)
      {
         helper.log.err ("zWebkit.commandLoadFile", "widget " +  helper.ebs().getName() + " has no attribute fileName. Message loadFile ignored");
         return;
      }

      if (fileName.length () == 0)
      {
         helper.log.dbg (2, "zWebkit.commandLoadFile", "content cleared");
         return;
      }

      String [] lines = TextFile.readFile (fileName);
      if (lines == null)
      {
         // the file does not exist
         //(o) TODO_widgets_text use a new control variable "fileStatus" or "error" to indicate the error
         //helper.ebsText ().err ("reading from file " + fileName);
         return;
      }

      StringBuffer alltext = new StringBuffer ();
      for (int ii = 0; ii < lines.length; ii ++)
      {
         alltext.append (lines[ii] + "\n");
      }

      // no need of setting the variable ...
      // helper.ebsText ().setText (alltext.toString ());
      helper.log.dbg (2, "zWebkit.commandLoadFile", "file \"" +  fileName + "\" of length " + alltext.length () + " loaded");
      attakWidget (HANDLER_OP_SETTEXT, alltext.toString ());
   }

   private void commandLoadUrl (String urlStr)
   {
      if (urlStr != null)
         helper.ebs ().setUrl (urlStr);

      helper.ebs ().setText ("");
      // has fileName ?
      urlStr = helper.ebs ().getUrl ();
      if (urlStr == null)
      {
         helper.log.err ("zWebkit.commandLoadUrl", "widget " +  helper.ebs().getName() + " has no attribute url. Message loadUrl ignored");
         return;
      }
      if (urlStr.length () > 0)
      {
         if (checkUrlPolicy (urlStr))
         {
            helper.log.dbg (2, "zWebkit.commandLoadUrl", "usr to load \"" +  urlStr + "\"");
            attakWidget (HANDLER_OP_SETURL, urlStr);
         }
      }
      helper.log.dbg (2, "zWebkit.commandLoadUrl", "content cleared");
   }

   private boolean checkUrlPolicy (String url)
   {
      //(o) Android/javaj/zWebkit checkUrlPolicy. Allow only local http if JSAction is activated
      //    Even in a android safe version allowing JSAction (WebView addJavascriptInterface) might be dangerous
      //    and there is no need of allowing such fine control of the device to an external http server even
      //    if it is trusted. At least running in a pure gastona app.
      //
      if (!isJSActionActivated ||
          url == null || url.length () == 0 ||
          miscUtil.startsWithIgnoreCase (url, "http://localhost:") ||
          miscUtil.startsWithIgnoreCase (url, "http://127.0.0.1:"))
      {
         return true;
      }
      helper.log.err ("zWebkit.checkUrlPolicy", "widget " +  helper.ebs().getName() + ", JSAction is activated and url \"" + url + "\"is not safe enough!");
      return false;
   }


   //private void commandRunJs (String jsCode)
   //{
   //   if (!isJSEnabled)
   //   {
   //      helper.log.err ("zWebkit.commandRunJs", "widget " +  helper.ebs().getName() + " is not JS enabled!");
   //      return;
   //   }
   //
   //   helper.log.dbg (2, "zWebkit.commandRunJs", jsCode.substring (0, 30));
   //   helper.log.warn ("zWebkit.commandRunJs", "this implementation will not send message JsRunFinished !!!");
   //   loadUrl ("javascript: jsRun(\"" + jsCode + "\")");
   //
   //   //(o) Android/javaj/zWebkit implementing evaluate javascript
   //
   //   // IMPLEMENTATION USING evaluateJavascript and ValueCallback (from API level 7)
   //   //
   //   // evaluateJavascript (jsCode,
   //   //                     new ValueCallback<String>()
   //   //                     {
   //   //                         @Override
   //   //                         public void onReceiveValue(String value)
   //   //                         {
   //   //                            helper.signalJsRunFinish (value);
   //   //                         }
   //   //                     });
   //
   //}

   private void commandRunJs (String [] jsCall)
   {
      if (!isJSEnabled)
      {
         helper.log.err ("zWebkit.commandRunJs", "widget " +  helper.ebs().getName() + " is not JS enabled!");
         return;
      }

      String callstr = jsCall[0] + "(";
      for (int jj = 1; jj < jsCall.length; jj++)
      {
         if (jj != 1)
            callstr += ", ";
         callstr += ("\"" + jsCall[jj] + "\"");
      }
      callstr += ") ;";

      helper.log.dbg (2, "zWebkit.commandRunJs", "calling js function " + callstr);
      loadUrl ("javascript: " + callstr);
   }
}
