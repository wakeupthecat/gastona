/*
package de.elxala
(c) Copyright 2019 Alejandro Xalabarder Aulet

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

package de.elxala.math.hash;

import java.io.*;
import java.io.InputStream;   // for files from jar file (resources)
import java.util.zip.CRC32;
import java.security.MessageDigest;
import java.math.BigInteger;

// Tiempos con buffer de 50000
//    se tarda unos 5 min en hacer el crc de un fichero de 5 GB
//
//
// Anyadir un parametro de limit en bytes
//    con eso los hashes no seran reales de todo el fichero pero alguna función hacen!
//    y son mucho mas rápidos claro!
//
// En general si hacemos un hash de 300 MB como máximo, puede tardar 2 s
//
// F:\PAPERS\UNIs>java -cp . hashoman crc 300000000  F:\PAPERS\PERSONAL\Personal.papV032
// crc32 [2959061910]
// 
// F:\PAPERS\UNIs>java -cp . hashoman md5 300000000  F:\PAPERS\PERSONAL\Personal.papV032
// md5 [9973f5ec3ab7a3f43343e74de824dffd]
// 
// F:\PAPERS\UNIs>java -cp . hashoman md5 300000000  F:\PAPERS\PERSONAL\Personal.papV032
// md5 [9973f5ec3ab7a3f43343e74de824dffd]
// 
// F:\PAPERS\UNIs>java -cp . hashoman md5 300000000  F:\PAPERS\PERSONAL\Personal.papV032
// md5 [3bf627eb5ffb9f6c4589a8a42e6329c5]

 
public class hashos
{
   private static int B_LIMIT = 0;
   private static int B_READ = 1;
   private static int BUFFER_SIZE = 2500000;

   public static double atof (String sDob)
   {
      double reto = 0.;

      try {
         reto = Double.parseDouble(sDob);
      }
      catch (Exception e) {}

      return reto;
   }

   public static FileInputStream openFileBinary (String fileName)
   {
      FileInputStream m_fis = null;
      
      try
      {
         m_fis = new FileInputStream (fileName);
      }
      catch (Exception e)
      {
      }
      return m_fis;
   }

   public static void closeFile (FileInputStream m_fis)
   {
      if (m_fis != null)
         try { m_fis.close (); } catch (Exception e) {}
   }
   
   public static int readBytes (FileInputStream m_fis, byte [] cbuf, int [] byteState)
   {
      if (m_fis == null) return -1;
      if (byteState[B_LIMIT] != 0 && byteState[B_READ] >= byteState[B_LIMIT]) return -1;
     
      
      int quant;
      try
      {
         quant = m_fis.read (cbuf);
      }
      catch (Exception e)
      {
         //log.err ("readBytes", "exception reading the file [" +  m_FileName  + "] " + e.toString ());
         // System.out.print ("Erroures!");
         return -1;
      }

      if (byteState[B_LIMIT] != 0)
         byteState[B_READ] += quant;
      
      int massa = byteState[B_READ] > byteState[B_LIMIT] ? byteState[B_READ] - byteState[B_LIMIT]: 0;

      return quant - massa;
   }

   public static String md5 (String filename)
   {
      return md5 (filename, 0);
   }
   
   public static String md5 (String filename, int limitMB)
   {
      MessageDigest md5 = null;
      int [] bytesCtrl = { limitMB * 1000000, 0 };
      
      try {
         md5 = MessageDigest.getInstance("MD5");
      } catch (Exception e) { return ""; }
         
      md5.reset();
      
      int tants = 0;
      byte [] paquet = new byte[BUFFER_SIZE];
      FileInputStream fitxer = openFileBinary (filename);
      if (fitxer != null)
      {
         do {
            tants = readBytes(fitxer, paquet, bytesCtrl);
            if (tants >= 0)
               md5.update (paquet, 0, tants);
         } while (tants != -1);
         closeFile (fitxer);
         BigInteger bigInt = new BigInteger(1, md5.digest());
         return bigInt.toString(16);
      }
      return "";
   }
      
   public static String crc32 (String filename)
   {
      return crc32 (filename, 0);
   }
   
   public static String crc32 (String filename, int limitMB)
   {
      int [] bytesCtrl = { limitMB * 1000000, 0 };
      CRC32 crc = new CRC32();
         
      int tants = 0;
      byte [] paquet = new byte[BUFFER_SIZE];
      FileInputStream fitxer = openFileBinary (filename);
      if (fitxer != null)
      {
         do {
            tants = readBytes(fitxer, paquet, bytesCtrl);
            if (tants >= 0)
               crc.update (paquet, 0, tants);
         } while (tants != -1);
         closeFile (fitxer);
         return "" + crc.getValue();
      }
      return "";
   }
 
   public static void main(String[] aa)
   {
      if (aa.length != 3)
      {
         System.out.println("hashos method(md5/crc32) firstMBytes(0,N) filename");
         return;
      }
      String algo = aa[0];
      int limit = (int) atof (aa[1]);
      String file = aa[2];
      
      if (algo.equalsIgnoreCase ("md5"))
         System.out.println("md5 [" + md5 (file, limit) + "]");
      else if (algo.equalsIgnoreCase ("crc") || algo.equalsIgnoreCase ("crc32"))
         System.out.println("crc32 [" + crc32 (file, limit) + "]");
      else 
         System.out.println("easyhash [campeon!]");
    }
}