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
   public static Class getCallerButNotMe ()
   {
      return getCallerButNotMe ("");
   }


   // returns the caller of the caller to this method ...
   // NOTE: in case of building more methods in jsys class do not call this method inside jsys
   //
   // example:
   //
   //       System.out.println ("the class who called this method was " + (jsys.getCallerButNotMe ()).getName ());
   //
   public static Class getCallerButNotMe (Object me)
   {
      return getCallerButNotMe (me, 0);
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
   public static Class getCallerButNotMe (Object me, int plusDeep)
   {
      return me.getClass ();
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
