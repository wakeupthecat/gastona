/*
java packages for gastona
Copyright (C) 2020 Alejandro Xalabarder Aulet

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

import java.io.*;

/**
   class File2
   @author Alejandro Xalabarder Aulet
   @date   2020

   Fixed java.io.File
*/
public class File2 extends File
{
   public File2 (String name)
   {
      //https://stackoverflow.com/questions/18646731/java-absolute-path-adds-user-home-property-when-adding-quotes-linux/62457476#62457476
      super ((name.length () >= 2 && name.charAt (0) == '"' && name.charAt (name.length ()-1) == '"') ?
             name.substring (1, name.length ()-1):
             name);
   }
}

/*

      String rename;
      boolean ya;
      
      File f1 = new File("C:/UNI2");     // given that exists and it is a directory
      ya = f1.exists();                  // true
      ya = f1.isFile();                  // false
      ya = f1.isDirectory();             // true
      rename = f1.getAbsolutePath();     // "C:\\UNI2"
      

      f1 = new File("\"C:/UNI2\"");      // in windows this should be the same directory!!
      ya = f1.exists();                  // false
      ya = f1.isFile();                  // false
      ya = f1.isDirectory();             // false
      rename = f1.getAbsolutePath();     // "C:\tmp\"C:\UNI2""

      File f1 = new File2 ("C:/UNI2");  // (same as with File)
      ya = f1.exists();                  // true
      ya = f1.isFile();                  // false
      ya = f1.isDirectory();             // true
      rename = f1.getAbsolutePath();     // "C:\\UNI2"
      

      f1 = new File2 ("\"C:/UNI2\"");    // Fixed!
      ya = f1.exists();                  // true
      ya = f1.isFile();                  // false
      ya = f1.isDirectory();             // true
      rename = f1.getAbsolutePath();     // "C:\\UNI2"
*/
