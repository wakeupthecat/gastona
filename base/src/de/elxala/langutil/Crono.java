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

import java.util.*;

/**
      Example of use:

         Crono rolex = new Crono ();
         ...
         rolex.start ();
         ... some process
         rolex.stop ();
         System.out.println ("first process took " + rolex.elapsedMillis () + " milliseconds");

         rolex.start ();
         ... some other process
         rolex.stop ();
         System.out.println ("second process took " + rolex.elapsedMillis () + " milliseconds");


         or

         rolex.start ();
         ...
         rolex.pause ();
         ...
         rolex.start ();
         ...
         rolex.pause ();
         ...
         System.out.println ("total elapsed time " + rolex.elapsedMillis () + " milliseconds");

         or

         rolex.On ();
         ...
         rolex.Off ();
         ...
         rolex.On ();
         ...
         rolex.Off ();
         ...
         System.out.println ("total elapsed time " + rolex.elapsedMillis () + " milliseconds");


*/
public class Crono
{
   private Calendar inicio = null;
   private long lapsoMillis = 0;
   private boolean resetOnStart = false;

   public Crono ()
   {
   }

   public Crono (int year, int month, int day, int hour, int minute, int second, int millis)
   {
      lapsoMillis -= (second * 1000 +  millis);
      inicio = new GregorianCalendar (year, month-1, day, hour, minute);
   }

   public Crono (Crono refe)
   {
      reset (refe);
   }

   public Crono (Calendar refe)
   {
      lapsoMillis = 0;
      inicio = refe;
   }

   public void reset ()
   {
      lapsoMillis = 0;
      inicio = null;
   }

   public void reset (Crono refe)
   {
      lapsoMillis = 0;
      inicio = refe.inicio;
   }

   public void copy (Crono refe)
   {
      lapsoMillis = refe.lapsoMillis;
      inicio = refe.inicio;
   }

   public void start ()
   {
      inicio = new GregorianCalendar ();
      if (resetOnStart)
      {
         lapsoMillis = 0;
         resetOnStart = false;
      }
   }

   public void pause ()
   {
      lapsoMillis += currentElapsedMillis ();
      inicio = null;
   }

   public void stop ()
   {
      pause ();
      resetOnStart = true;
   }

   public void On  ()
   {
      start ();
   }

   public void Off ()
   {
      pause ();
   }

   public long elapsedMillis ()
   {
      return lapsoMillis + currentElapsedMillis ();
   }

   private long currentElapsedMillis ()
   {
      if (inicio == null) return 0;
      return System.currentTimeMillis () - inicio.getTimeInMillis ();
   }

   /*
      days
      hours
      minutes
      seconds
      milliseconds
   */
   public long [] elapsedTime (int separ)
   {
      long total = lapsoMillis + currentElapsedMillis ();

      long [] result = new long [separ];

      if (separ >= 5)   // days
      {
         result[4] = total / (24*3600*1000);
         total -= result[4] * (24*3600*1000);
      }
      if (separ >= 4)   // hours
      {
         result[3] = total / (3600*1000);
         total -= result[3] * (3600*1000);
      }
      if (separ >= 3)   // minutes
      {
         result[2] = total / (60*1000);
         total -= result[2] * (60*1000);
      }
      if (separ >= 2)   // seconds
      {
         result[1] = total / (1000);
         total -= result[1] * (1000);
      }
      if (separ >= 1)   // ms
      {
         result[0] = total;
      }
      return result;
   }

   public static void main (String [] aa)
   {
      int year  = (aa.length > 0) ? stdlib.atoi (aa[0]): 2005;
      int month = (aa.length > 1) ? stdlib.atoi (aa[1]): 1;
      int day   = (aa.length > 2) ? stdlib.atoi (aa[2]): 1;
      int hour  = (aa.length > 3) ? stdlib.atoi (aa[3]): 0;
      int minu  = (aa.length > 4) ? stdlib.atoi (aa[4]): 0;
      int sec   = (aa.length > 5) ? stdlib.atoi (aa[5]): 0;
      int mili  = (aa.length > 6) ? stdlib.atoi (aa[6]): 0;

      Crono pass = null;
      if (aa.length == 0)
           pass = new Crono (2005, 5, 27, 16, 56, 0, 0);
      else pass = new Crono (year, month, day, hour, minu, sec, mili);

      pass.stop ();
      long [] times = pass.elapsedTime (5);
      System.out.println("this happened " + times[4] + " days, " + times[3] + " hours, " + times[2] + " minutes, " + times[1] + " seconds and " + times[0] + " milliseconds ago!");
      System.out.println("that's " + pass.elapsedMillis ()/1000 + " seconds ago!");
   }
}
