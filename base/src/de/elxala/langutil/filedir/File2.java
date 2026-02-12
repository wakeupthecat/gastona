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
   @date   2020-2026

   Fixed java.io.File
*/
public class File2 extends File
{
    public File2 (String name)
    {
        //https://stackoverflow.com/questions/1099300/whats-the-difference-between-getpath-getabsolutepath-and-getcanonicalpath
        //https://stackoverflow.com/questions/18646731/java-absolute-path-adds-user-home-property-when-adding-quotes-linux/62457476#62457476
        super ((name.length () >= 2 && name.charAt (0) == '"' && name.charAt (name.length ()-1) == '"') ?
                name.substring (1, name.length ()-1):
                name);
    }

    public String getAbsPath2 ()
    {
        String absPath;
        try
        {
            absPath = getCanonicalPath ();
        }
        catch (Exception e)
        {
            // silently return getAbsolutePath as before using getAbsPath2
            absPath = getAbsolutePath ();
        }
        return absPath;
    }
}

/*
------------------- file pathos.java
import java.io.*;

class pathos
{
    public static void main (String [] aa)
    {
        if (aa.length == 0)
        {
            System.out.println ("No paths given in arguments, show three default examples");
            aa = new String [] {"c:\\ansolutor.txt", "tengo blanco.txt", "../relato/kas.txt", "\"c:\\asumer\\castanyo.txt\"" };
        }
        for (int ii = 0; ii < aa.length; ii ++)
        {
            try {
                File f1 = new File (aa[ii]);
                System.out.println ("File [" + aa[ii] + "]");
                System.out.println ("exists " + f1.exists());
                System.out.println ("isFile " + f1.isFile());
                System.out.println ("isDir  " + f1.isDirectory());
                System.out.println ("AbsolutePath " + f1.getAbsolutePath());
                System.out.println ("CanonicalPath " + f1.getCanonicalPath());
                System.out.println ("");
            }
            catch (Exception e)
            {
                System.out.println ("*** EXCEPTION!! " + e);
            }
        }
        System.out.print ("fi.");
    }
}
------------------- OUTPUT of pathos.java

C:\testIssue\subfol> java pathos

No paths given in arguments, show three default examples
File [c:\ansolutor.txt]
exists false
isFile false
isDir  false
AbsolutePath c:\ansolutor.txt
CanonicalPath C:\ansolutor.txt

File [tengo blanco.txt]
exists false
isFile false
isDir  false
AbsolutePath C:\testIssue\subfol\tengo blanco.txt
CanonicalPath C:\testIssue\subfol\tengo blanco.txt

File [../relato/kas.txt]
exists false
isFile false
isDir  false
AbsolutePath C:\testIssue\subfol\..\relato\kas.txt
CanonicalPath C:\testIssue\relato\kas.txt

File ["c:\asumer\castanyo.txt"]
exists false
isFile false
isDir  false
AbsolutePath C:\testIssue\subfol\"c:\asumer\castanyo.txt"

*** EXCEPTION!! java.io.IOException: The filename, directory name, or volume label syntax is incorrect
fi.


*/
