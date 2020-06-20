/*
package de.elxala
(c) Copyright 2019-2020 Alejandro Xalabarder Aulet

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
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import de.elxala.langutil.*;


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
   public static String crc32 (String filename)
   {
      return crc32 (filename, 0);
      }

   public static String crc32 (String filename, int limitMB)
   {
      CRC32 crc = new CRC32();
      
      try
      {
         FileInputStream fis = new FileInputStream(filename);

         if (fis == null)
            return "";

         byte[] data = new byte[1024*1024];
         int read = 0;
         while ((--limitMB != 0) && (read = fis.read(data)) != -1)
            crc.update(data, 0, read);
         fis.close ();
      }
      catch (Exception ex)
      {
      }

      return "" + crc.getValue();
   }

   
   public static StringBuffer hashStrBuff (String alg, String file, int limitMB) throws NoSuchAlgorithmException, IOException
   {
      MessageDigest hashi = MessageDigest.getInstance(alg.toUpperCase ());
      FileInputStream fis = new FileInputStream(file);
     
      if (fis == null)
         return new StringBuffer ();
      
      byte[] data = new byte[1024*1024];
      int read = 0;
      while ((--limitMB != 0) && (read = fis.read(data)) != -1)
         hashi.update(data, 0, read);

      fis.close ();
      byte[] hashBytes = hashi.digest();
      
      StringBuffer sb = new StringBuffer();
      for (int ii = 0; ii < hashBytes.length; ii ++)
         sb.append(Integer.toString((hashBytes[ii] & 0xff) + 0x100, 16).substring(1));

      return sb;
   }

   public static String hash (String algo, String fileName)
   {
      return hash (algo, fileName, 0);
   }
   
   public static String getDefaultAlgo ()
   {
      // faster than sha1 and sha256
      return "MD5";   
   }

   public static String hash (String algo, String fileName, int limitMB)
   {
      String ALGO = algo.toUpperCase ();
      if (ALGO.equals (""))       ALGO = getDefaultAlgo ();
      if (ALGO.equals ("SHA1"))   ALGO = "SHA-1";
      if (ALGO.equals ("SHA256")) ALGO = "SHA-256";
      if (ALGO.equals ("CRC"))    ALGO = "CRC-32";
      if (ALGO.equals ("CRC32"))  ALGO = "CRC-32";
      
      if (ALGO.equals ("CRC-32"))
         return crc32 (fileName, limitMB);
         
      StringBuffer sbuf = new StringBuffer  ();
      try
      {
         sbuf = hashStrBuff (ALGO, fileName, limitMB);
      }
      catch (Exception ex)
   {
   }
   
      return sbuf.toString();
   }
 
   public static void main(String[] aa)
   {
      if (aa.length != 3)
      {
         System.out.println("hashos method(SHA-1, SHA-256, MD5, CRC-32) firstMBytes(0,N) filename");
         return;
      }
      String algo = aa[0];
      int limit = (int) stdlib.atof (aa[1]);
      String file = aa[2];
      
      System.out.println(algo + " [" + hash (algo, file, limit) + "]");
    }
}