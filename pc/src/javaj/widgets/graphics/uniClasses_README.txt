2013.06.23
Alejandro Xalabarder


   Why uniClasses
   ---------------------------------
   
   uniClasses are thought to unify some structures and classes between java compilers, specifically
   between java Sun (for pc, windows, linux etc) and java Android. Tipically structures like
   Color, Rectangle etc are different implemented in both compilers but still they are 
   basically the same thing.

   uniClasses implement natively the functionality of these structures and offer unified
   methods so a generic code written for both Pc and Android can use them.
   
   The approach might cause some confusion since allows a generic code to use a native class,
   the trick is that the generic code is only allowed to use methods and members that not
   use any specific native type, let's call them "uni" methods. Compiler errors will trigger
   any wrong use of the uniClass.
   
   
   existing uniClasses
   ---------------------------------
   uniColor, uniRect, uniPaint, uniPath, uniCanvas, uniMotion

   
   writting uniClasses
   ---------------------------------

   A uniClass is needed for each compiler (PC, Android)
   it might contain methods to be used only for the specific compiler, so it can be used by specific native classes
   but the idea of "uni" is to have some funcionality using the same method signatures, this is not imposed by
   extending any java interface (although it could be done), simply the compiler will complain if a method that is
   suposed to be generic is not implemented by the specific uniClass
   
   For example:
   
      the class Color in Pc is a specific class java.awt.Color while in Android it is simply an integer
      
      so we can think about implementing following class in both compilers
      
         public class uniColor
         {
            ...
            public uniColor ();
            public uniColor (int rgb);
            public uniColor (int red, int green, int blue);
            public void parseColor (String str);
         }

      so a generic code written for both compilers can use uniColor as common class
      and use all these uniColor methods, 
      
      but some native java classes (e.g. widgets) might want to have more native information
      about the uniColor, in this case each uniColor can implement compiler specific methods (not "uni")
      
         // for java PC
         public class uniColor 
         {
            ...
            public java.awt.Color getAwtColor ();
         }
      
         // for java Android
         public class uniColor 
         {
            ...
            public int getNativeColor ();
         }
      
      so long the generic classes don't use these methods, the use of uniClasses are consistent.

      A native code has no problem with any method, whereas a generic code will not compile in
      the compiler where the specific uniClass does not implement some method used by the generic code.

      
      