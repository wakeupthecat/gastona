/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package de.elxala.langutil;


import android.content.Context;
import de.elxala.langutil.androidSysUtil;

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;
import java.util.jar.*;
import android.graphics.drawable.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;


//(o) Android_TODO replace javaLoad with some android equivalent

public class javaLoad
{
   private static logger log = new logger (null, "de.elxala.langutil.javaLoad", null);

   /**
         Instancia una clase de la que previamente se supone esta' cargada las libreri'as necesarias

         ejemplo:

            Component boton = javaInstanciator ("javax.swing.JButton");
   */
   public static Object javaInstanciator (String nombreClase)
   {
      log.err ("javaInstanciator", "NOT IMPLEMENTED IN de.elxala ANDROID");
      return "";
   }

   //---
   // do not instanciate them if not needed (does it worth ?)
   //
   static javaLoad       singleComponent = null;
   static URLClassLoader singleUrlLoader = null;

   private static void getSingleComponent ()
   {
      if (singleComponent == null)
      {
         singleComponent = new javaLoad ();
         singleUrlLoader = (URLClassLoader) singleComponent.getClass().getClassLoader();
      }
   }
   //---

   private static final Class[] param_addURL = new Class[] { URL.class };

   /*
      This is some like a "remote class path method" that makes
      possible to add a class path to program that runs within a jar file !!!
   */
   public static boolean addClassPath (String onePathOrJar)
   {
      log.err ("addClassPath", "NOT IMPLEMENTED IN de.elxala ANDROID");
      return false;
   }

   /*
      variant of 'callStaticMethodArgs (String, String, String [], String [])'
      where the last argument (return value of the method) is ignored, either because the method
      does not return any value or simply because not interested in
   */
   public static boolean callStaticMethodArgs (String className, String staticMethod, String [] args)
   {
      log.err ("callStaticMethodArgs", "NOT IMPLEMENTED IN de.elxala ANDROID");
      //return false;
      return callStaticMethodArgs (className, staticMethod, args, null);
   }

   /*
      tries to call the static method 'staticMethod' in the class 'className'

      @param className
      @param staticMethod
      @param args either a String array with parameters (if method (String[])) or null (if method())
      @param refReturnObj if given (not null) and having at least 1 element, the first element will be
             set to the return value of the method (if the method has no return value, then with null)
   */
   public static boolean callStaticMethodArgs (String className, String staticMethod, String [] args, Object [] refReturnObj)
   {
      log.err ("callStaticMethodArgs", "PROBABLY NOT IMPLEMENTED IN de.elxala ANDROID");
      getSingleComponent ();

      boolean ok = true;
      Class ella = null;
      Object returnValue = null;

      // obtain the class ?
      //
      try
      {
         ella = Class.forName (className);
      }
      catch (Exception e)
      {
         log.err ("callStaticMethodArgs", "problem in classForName \"" + className + "\" " + e);
         return false;
      }

      if (ella == null)
      {
         log.err ("callStaticMethodArgs", "\"" + className + "\" not obtained with forName!");
         return false;
      }

      // obtain the method and then invoke it
      try
      {
         // System.out.println ("ALLORO! arrggsslen es " + ((args == null) ? "nullo": ""+args.length));
         Method method;
         if (args != null)
              method = ella.getDeclaredMethod (staticMethod, new Class[] { args.getClass () });
         else method = ella.getDeclaredMethod (staticMethod);
         method.setAccessible (true);

         // from java documentation method invoke
         //
         //    public Object invoke(Object obj, Object[] args)
         //...
         //If the underlying method is static, then the specified obj argument is ignored. It may be null.
         //if the number of formal parameters required by the underlying method is 0, the supplied args array may be of length 0 or null.

         //returnValue = method.invoke (ella, new Object [] { args });

         if (args != null)
              returnValue = method.invoke (ella, new Object [] { args });
         else returnValue = method.invoke (ella);
      }
      catch (Exception t)
      {
         log.err ("callStaticMethodArgs", "executing method \"" + staticMethod + "\" " + t);
         ok = false;
      }

      if (refReturnObj != null && refReturnObj.length >= 1)
      {
         refReturnObj[0] = returnValue;
      }

      return ok;
   }


   /*
      Gets a resource (usually a file) : that permits accessing files in own jar file for example

      returns the URL of the resource if found (NOTE the search is CASE SENSITIVE !!!)
      otherwise returns null

      IMPORTANT NOTE! resourceName is the name of the resource to find BUT MUST MATCH THE CASE !!
   */
   public static URL getResourceURL (String resourceName)
   {
      log.err ("getResource", "NOT IMPLEMENTED IN de.elxala ANDROID");
      return null;
   }

   // Note : a gastona resource IT IS NOT an android resource but an android asset !!
   //        originally files content in jar file or class path
   //
   public static boolean existsResource (String resourceName)
   {
      InputStream is = androidSysUtil.openAssetFile (resourceName);
      if (is != null)
      {
         try { is.close (); } catch (Exception e) {}
         return true;
      }

      return false;
   }

   // Note : a gastona resource IT IS NOT an android resource but an android asset !!
   //        originally files content in jar file or class path
   //
   public static InputStream openResource (String resourceName)
   {
      return androidSysUtil.openAssetFile (resourceName);
   }

   // (o) TODO_remove Android Resource stuff ... not really needed!
   //

   // Note: do it general, since possibly more paths that raw can be used ...
   //
   public static boolean existsAndroidResource (String resourceName, String rootDir)
   {
      return androidSysUtil.getResourceId (resourceName, rootDir) != 0;
   }

   public static InputStream openAndroidResource (String resourceName)
   {
      return openAndroidResource (resourceName, "raw");
   }

   // Note: do it general, since possibly more paths that raw can be used ...
   //
   public static InputStream openAndroidResource (String resourceName, String rootDir)
   {
      int resId = androidSysUtil.getResourceId (resourceName, rootDir); // raw/.....
      if (resId != 0)
      {
         try
         {
            return androidSysUtil.getResources().openRawResource (resId);
         }
         catch (Exception e) {}
      }
      return null;
   }


   /*
      Gets a Drawable either from a file or a url

      returns a Drawable if image is found and null otherwise
   */
   public static Drawable getSomeHowDrawable (String imgFileName, boolean cacheable)
   {
      Drawable drawable = null;

      if (imgFileName.length() == 0) return null;
      if (cacheable)
      {
         Object obj = utilSys.objectSacGet ("imgCache." + imgFileName);
         if (obj != null && obj instanceof Drawable)
            return (Drawable) obj;
      }

      log.dbg (2, "getSomeHowDrawable", "loading image " + imgFileName);
      if (fileUtil.looksLikeUrl (imgFileName))
      {
         URL aurl = null;
         InputStream inpStream = null;
         try
         {
            aurl = new URL(imgFileName);
            inpStream = (InputStream) aurl.getContent();
         }
         catch (Exception e)
         {
            log.err ("getSomeHowDrawable", "exception loading image url [" + imgFileName + "] + " + e);
         }
         if (inpStream != null)
         {
            log.dbg (2, "getSomeHowDrawable", "loaded as url");
            drawable = Drawable.createFromStream(inpStream, "src");
         }
         else log.dbg (2, "getSomeHowDrawable", "image not found as url");
      }
      else
      {
         File fim = fileUtil.getNewFile (imgFileName);
         if (fim.exists ())
            drawable = new BitmapDrawable (fim.getPath ());

         if (drawable != null)
            log.dbg (2, "getSomeHowDrawable", "image loaded from file [" + fim.getPath () + "]");
         else
         {
            log.dbg (2, "getSomeHowDrawable", "file not found [" + fim.getPath () + "], try as resource");
            // ups file does not exist ? try fallback resources!

            if (imgFileName.endsWith (".png"))
               imgFileName = imgFileName.substring (0, imgFileName.length ()-4);

            int id = androidSysUtil.getResourceId ("drawable/" + imgFileName);
            if (id != 0)
               drawable = androidSysUtil.getResources().getDrawable(id);
            if (drawable != null)
                 log.dbg (2, "getSomeHowDrawable", "image loaded from resources");
            else log.dbg (2, "getSomeHowDrawable", "image not found at all!");
         }
      }

      //if (cacheable && drawable != null)
      if (cacheable)
      {
         utilSys.objectSacPut ("imgCache." + imgFileName, drawable);
      }

      return drawable;
   }
}
