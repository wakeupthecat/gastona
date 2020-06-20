/*
packages de.elxala
Copyright (C) 2016  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.langutil.filedir;

import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.io.*;
import java.util.*;

/**
   Wrapper for TextFile Define a Scri
 */
public class goFile extends ScriptableObject
{
   private static final long serialVersionUID = 1578910199771811828L;
   private TextFile theFile = new TextFile ();

   /// The zero-parameter constructor.
   public goFile()
   {
   }

   /// The Java method defining the JavaScript File constructor.
   //
   @JSConstructor
   public static Scriptable goFile (Context cx, Object[] aa, Function ctorObj, boolean inNewExpr)
   {
      goFile result = new goFile ();
      if (aa.length > 0 && aa[0] != Context.getUndefinedValue())
      {
         result.fopen (Context.toString (aa[0]), aa.length > 1 ? Context.toString (aa [1]): "r");
      }
      return result;
   }


    /// Returns the name of this JavaScript class, "goFile".
    //
   @Override
   public String getClassName ()
   {
      return "goFile";
   }

   @JSGetter
   public String getFileName ()
   {
      return theFile.getFileName ();
   }

   @JSFunction
   public boolean feof ()
   {
      return theFile.feof ();
   }

   @JSFunction
   public boolean isMemoryFile ()
   {
      return theFile.isMemoryFile ();
   }

   @JSFunction
   public boolean isFromJarOrResources()
   {
      return theFile.isFromJarOrResources ();
   }

   @JSFunction
   public boolean isStream()
   {
      return theFile.isStream ();
   }

   @JSFunction
   public boolean fopen (String name, String modus)
   {
      if (modus == Context.getUndefinedValue())
          modus = "r";
      return theFile.fopen (name, modus);
   }

   @JSFunction
   public boolean writeLine (final String line)
   {
      return theFile.writeLine (line);
   }

   @JSFunction
   public boolean writeString (final String line)
   {
      return theFile.writeString (line);
   }

   @JSFunction
   public void fclose() throws IOException
   {
      theFile.fclose ();
   }

   @JSFunction
   public Object readLines () throws IOException
   {
      List lineas = new Vector ();
      while (theFile.readLine ())
         lineas.add (theFile.TheLine ());
      theFile.fclose ();

      String [] Contenido = new String [lineas.size ()];
      for (int ii = 0; ii < lineas.size (); ii++)
         Contenido[ii] = (String) lineas.get (ii);

      Scriptable scope = ScriptableObject.getTopLevelScope(this);
      Context cx = Context.getCurrentContext();
      return cx.newObject(scope, "Array", Contenido);
   }

   @JSFunction
   public String readLine() throws IOException
   {
      return theFile.readLine () ? theFile.TheLine (): null;
   }
   
   // @TODO: with these two functions we would be able to read and write binary files as well
   //
   // @JSFunction
   // public Object readBytes (byte [] bytes) throws IOException
   // {
   //    int res = theFile.readBytes (bytes
   //
   //    Scriptable scope = ScriptableObject.getTopLevelScope(this);
   //    Context cx = Context.getCurrentContext();
   //    return cx.newObject(scope, "Int", res);
   // }
   //
   // @JSFunction
   // public Object writeBytes (byte [] data, int offset, int len) throws IOException
   // {
   //    boolean res = theFile.writeBytes (data, offset, len);
   //
   //    Scriptable scope = ScriptableObject.getTopLevelScope(this);
   //    Context cx = Context.getCurrentContext();
   //    return cx.newObject(scope, "Int", res);
   // }

    /**
     * Finalizer.
     *
     * Close the file when this object is collected.
     */
   @Override
   protected void finalize ()
   {
      theFile.fclose ();
   }

   //(Documentation of checkInstance from Rhino example File.java)
   /**
   * Perform the instanceof check and return the downcasted File object.
   *
   * This is necessary since methods may reside in the File.prototype
   * object and scripts can dynamically alter prototype chains. For example:
   * <pre>
   * js> defineClass("goFile");
   * js> o = {};
   * [object Object]
   * js> o.__proto__ = goFile.prototype;
   * [object goFile]
   * js> o.write("hi");
   * js: called on incompatible object
   * </pre>
   * The runtime will take care of such checks when non-static Java methods
   * are defined as JavaScript functions.
   */
   private static goFile checkInstance (Scriptable obj)
   {
      if (obj == null || !(obj instanceof goFile))
      {
         throw Context.reportRuntimeError("called on incompatible object");
      }
      return (goFile) obj;
   }
}

//
// Another alternative ?
//
// importPackage (de.elxala.langutil.filedir);
// importClass (de.elxala.langutil.filedir.TextFile);
//
// var fix = new TextFile ();
//
// etc ..


/**
 * Class ImporterTopLevel
 *
 * This class defines a ScriptableObject that can be instantiated
 * as a top-level ("global") object to provide functionality similar
 * to Java's "import" statement.
 * <p>
 * This class can be used to create a top-level scope using the following code:
 * <pre>
 *  Scriptable scope = new ImporterTopLevel(cx);
 * </pre>
 * Then JavaScript code will have access to the following methods:
 * <ul>
 * <li>importClass - will "import" a class by making its unqualified name
 *                   available as a property of the top-level scope
 * <li>importPackage - will "import" all the classes of the package by
 *                     searching for unqualified names as classes qualified
 *                     by the given package.
 * </ul>
 * The following code from the shell illustrates this use:
 * <pre>
 * js> importClass(java.io.File)
 * js> f = new File('help.txt')
 * help.txt
 * js> importPackage(java.util)
 * js> v = new Vector()
 * []
 *
 * @author Norris Boyd
 */
