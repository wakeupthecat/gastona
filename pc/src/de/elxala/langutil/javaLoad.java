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

import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.awt.Component;
import javax.swing.ImageIcon;
import de.elxala.zServices.*;
//import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

public class javaLoad extends Component
{
   private static logger log = new logger (null, "de.elxala.langutil.javaLoad", null);

   /**
         Instancia una clase de la que previamente se supone esta' cargada las libreri'as necesarias

         ejemplo:

            Component boton = javaInstanciator ("javax.swing.JButton");
   */
   public static Object javaInstanciator (String nombreClase)
   {
      Class ella = null;

      // obtain the class ?
      //
      try
      {
         ella = Class.forName (nombreClase);
      }
      catch (Exception e)
      {
         log.err ("javaInstanciator", "problem in Class.ForName \"" + nombreClase + "\"");
         return null;
      }

      // instance an object of this class ?
      //
      Object cosa;
      try
      {
         cosa = ella.newInstance ();
      }
      //catch (InstantiationException e)
      catch (Exception e)
      {
         log.err ("javaInstanciator", "Some InstantiationException .. (" + nombreClase + ") maybe this class has no default constructor");
         e.printStackTrace ();
         return null;
      }

      return cosa;
   }


//   /**
//         Instancia una clase cuyo constructor acepta un String (nombre identificativo del widget)
//         previamente se supone esta' cargada las libreri'as necesarias
//
//         ejemplo:
//
//            Component boton = javaInstanciator ("javaj.widgets.zLeds", "losLeds");
//   */
//   public static Object javaInstanciator4zWidget (String nombreClase, String nombreWidget)
//   {
//      Component ella = null;
//
//      // obtain the class ?
//      //
//      try
//      {
//         ella = Class.forName (nombreClase);
//      }
//      catch (Exception e)
//      {
//         System.err.println ("ERROR javaInstanciator problem in classForName \"" + nombreClase + "\"");
//         e.printStackTrace ();
//         return null;
//      }
//
//      // instance an object of this class ?
//      //
//      Object cosa;
//      try
//      {
//         cosa = ella.newInstance (nombreWidget);
//      }
//      //catch (InstantiationException e)
//      catch (Exception e)
//      {
//         System.err.println ("" + e);
//         System.err.println ("Some InstantiationException .. (" + nombreClase + ") maybe this class has constructor with one String as parameter");
//         e.printStackTrace ();
//         return null;
//      }
//
//      return cosa;
//   }

   private static final Class[] param_addURL = new Class[] { URL.class };

   /*
      This is some like a "remote class path method" that makes
      possible to add a class path to program that runs within a jar file !!!
   */
   public static boolean addClassPath (String onePathOrJar)
   {
      // --
      // convert path into file (e.g. d:/xala/devjava or ./allxala.jar ...)
      //
      boolean ok = true;

      File afile = new File (onePathOrJar);

      // convert file into url
      //
      URL aUrl = null;

      try { aUrl = afile.toURL (); } catch (Exception t) { ok = false; }

      if (! ok) return ok;
      // --


      // NOTE : is not possible the direct way ...
      //
      // someURLLoaderClass.addURL (aUrl);
      // because of the Error compiling : addURL(java.net.URL) has protected access in java.net.URLClassLoader
      //

      Class sysclass = URLClassLoader.class;
      try
      {
         Method method = sysclass.getDeclaredMethod ("addURL", param_addURL);
         method.setAccessible (true);
         method.invoke (ClassLoader.getSystemClassLoader(), new Object[]{ aUrl });
      }
      catch (Exception t) { ok = false; }

      return ok;

   }

   /*
      variant of 'callStaticMethodArgs (String, String, String [], String [])'
      where the last argument (return value of the method) is ignored, either because the method
      does not return any value or simply because not interested in
   */
   public static boolean callStaticMethodArgs (String className, String staticMethod, String [] args)
   {
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
         else method = ella.getDeclaredMethod (staticMethod, null);
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
         else returnValue = method.invoke (ella, null);
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
      ClassLoader clo = Thread.currentThread ().getContextClassLoader ();

      if (clo == null)
      {
         return null;
      }
      
      String resourceNorm = resourceName.replace('\\', '/');
      return clo.getResource (resourceNorm);
   }

   public static boolean existsResource (String resourceName)
   {
      return getResourceURL (resourceName) != null;
   }

   public static InputStream openResource (String resourceName)
   {
      URL suUrl = getResourceURL (resourceName);
      if (suUrl == null) return null;

      try { return suUrl.openStream (); }
      catch (Exception e) { }

      return null;
   }

   /*
      Gets a ImageIcon either from the given path 'resourceName' or from own jar or resource paths
      (NOTE THAT IN THE LAST CASE THE NAME HAS TO MATCH THE UPPER/LOWER CASE)

      returns always a valid ImageIcon object!
   */
   public static ImageIcon getSomeHowImageIcon (String resourceName)
   {
      // to avoid NullPointer exception at java.io.File.<init>(File.java:180)
      if (resourceName == null || resourceName.length() == 0) return null;

      // try to find the icon in a file
      //
      File fio = fileUtil.getNewFile (resourceName);
      if (fio.exists ())
      {
         log.dbg (2, "getSomeHowImageIcon", "load image file \"" + resourceName + "\"");
         //try {
         //URL u = new URL(resourceName);
         //URLConnection con = u.openConnection();
         //con.setUseCaches(false);
         //Image img = Toolkit.getDefaultToolkit().createImage(new URLImageSource(u, con));         
         //    return img;
         //}
         //catch (Exception)
         //{
         //    return null;
         //}
         
         return new ImageIcon (resourceName);
      }
//      else System.out.println ("image file " + resourceName + " NOT found!");

      // try to find the icon in a jar (or url resource)
      //
      URL urlim = getResourceURL (resourceName);
      if (urlim != null)
      {
         log.dbg (2, "getSomeHowImageIcon", "load image resource \"" + resourceName + "\"");
         return new ImageIcon (urlim);
      }
//      else System.out.println ("resource " + resourceName + " NOT found!");

      // 3nd chance!, search as pure URL
      //
      if (urlim == null)
      {
         try { urlim = new URL (resourceName); }
         catch (Exception e)
         {
            // log.dbg (5, "getSomeHowImageIcon", "url exception " + e.toString ());
         }
         if (urlim != null)
         {
            log.dbg (2, "getSomeHowImageIcon", "load image url \"" + resourceName + "\"");
            return new ImageIcon (urlim);
         }
      }

      log.dbg (2, "getSomeHowImageIcon", "image \"" + resourceName + "\" not found");
      // Icon not found return null (note that "new ImageIcon ("notExistingFile");" returns always something NOT null !!!
      //
      return null;
   }
}


/**

04.10.2009 20:58

   Curiosidad: see documentation of java.lang.Class.getDeclaredMethod

      ... and the parameterTypes parameter is an array of Class objects that identify the
      method's formal parameter types, in declared order.
      If more than one method with the same parameter types is declared in a class,
      and one of these methods has a return type that is more specific than any of the others,
                                                         -------------?
      that method is returned; otherwise one of the methods is chosen arbitrarily. ..."
                                                               ---------------------?

   osea que si tenemos

      String getGala (int, String)
      int    getGala (int, String)

   cual es más especifico ?
   y si tenemos .. ?

      char  getGala (int, String)
      int   getGala (int, String)


   pero, bien es cierto, que programar así no es muy práctico que digamos! (por ser fino...)

*/