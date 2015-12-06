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

/**	======== de.elxala.langutil.theStackTrace ==========================================
	@author Alejandro Xalabarder 04.07.2004 16:28

	El u'nico acceso a la stack trace es el me'todo getStackTrace de Throwable
	(este usa Throwable.getStackTraceDepth () y Throwable.getStackTraceElement (int i) que son
	privados y nativos !!)

	Adema's no se puede obtener desde un entorno esta'tico por lo cual necesitaremos
	un singleton (SysSingleton.java)
*/

public class jsys extends Throwable
{
   // returns the caller of the caller to this method ...
   // NOTE: in case of building more methods in jsys class do not call this method inside jsys
   //
   // example:
   //
   //       System.out.println ("the class who called this method was " + (jsys.getCallerButNotMe ()).getName ());
   //
   public static Class getCallerButNotMe ()
   {
      return getCallerButNotMe (0);
   }

   /**
      explores the stack of callers starting on "the caller of the method that calls this function"
      and returns the class find at position plusDeep

      Example:
         public class X
         {
            public void myFunction ()
            {
               System.out.println ("the class who called this method was " + (jsys.getCallerButNotMe (0)).getName ());
            }
         }
   */
   public static Class getCallerButNotMe (int plusDeep)
   {
      Class callerNo = sun.reflect.Reflection.getCallerClass (3 + plusDeep);
      // System.out.println ("\nJSYS: carolo " + callerNo.getName ());

      // el elemento 0 siempre sera' sun.reflect.Reflection,
      // el 1 de.elxala.langutil.jsys
      // y el 2 el del llamador (que no nos interesa ... ButNotMe !)
      //
      int stackLev = 3 + plusDeep;
      do
      {
         Class cla = sun.reflect.Reflection.getCallerClass (stackLev);
         if (cla == null) return callerNo;
         if (callerNo != cla) return cla;
         // System.out.println ("\nJSYS: i throw " + cla.getName ());
         stackLev ++;
      } while (true);
   }

   // returns the stack trace array (including this method in position 0)
   //
   public static StackTraceElement [] getNowStackTrace ()
   {
      //   	El u'nico acceso a la stack trace es el me'todo getStackTrace de Throwable
      //   	(este usa Throwable.getStackTraceDepth () y Throwable.getStackTraceElement (int i) que son
      //   	privados y nativos !!)
      //
      //   	Adema's no se puede obtener desde un entorno esta'tico por lo cual necesitaremos
      //   	una instancia

      return (new jsys ()).lastStack;
   }

   public StackTraceElement [] lastStack = getStackTrace ();
}
