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

public class stdlib
{
   public static boolean isOSLinux ()    { return java.io.File.separatorChar == '/'; }
   public static boolean isOSWindows ()  { return java.io.File.separatorChar == '\\'; }

   public static int unsigned (byte val)
   {
      return (val >= 0) ? val: 256 + val;
   }

 	public static int atoi (String sInt)
 	{
//		int reto = 0;
//
//		try {
//			Integer inte = new Integer (sInt);
//			reto = inte.intValue ();
//		}
//		catch (Exception e) {}
//
//		return reto;

      // atoi (9.2) should give 9 !!
 	   return (int) atof (sInt);
 	}

 	public static long atol (String sLong)
 	{
 	   return (long) atof (sLong);
 	}

 	public static double atof (String sDob)
 	{
		double reto = 0.;

		try {
			Double xx = new Double (sDob);
			reto = xx.doubleValue ();
		}
		catch (Exception e) {}

		return reto;
 	}
}