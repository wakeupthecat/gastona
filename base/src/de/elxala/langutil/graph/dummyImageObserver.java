package de.elxala.langutil.graph;

import java.awt.Image;
import java.awt.image.*;

public class dummyImageObserver implements ImageObserver
{
   public boolean imageUpdate(Image img,
                              int infoflags,
                              int x,
                              int y,
                              int width,
                              int height)
   {
      // well, I see ...
      return true;
   }
}
