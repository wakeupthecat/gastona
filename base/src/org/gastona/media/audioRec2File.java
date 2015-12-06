package org.gastona.media;

//
// from
//    http://www.java-tips.org/java-se-tips/javax.sound/capturing-audio-with-java-sound-api.html

import java.io.*;
import javax.sound.sampled.*;


public class audioRec2File
{
   public static boolean RunningRec = false;
   public static boolean Recording = false;

   public static boolean RunningPlay = false;
   public static boolean Playing = false;

   public static AudioFormat audioFormatRec  = new AudioFormat(44100 /* Hz */, 8 /* bits */ , 1 /* channels */, true /* signed */ , true /* bigEndian */);
   public static AudioFormat audioFormatPlay = new AudioFormat(44100 /* Hz */, 8 /* bits */ , 1 /* channels */, true /* signed */ , true /* bigEndian */);

   private static DataLine.Info info = null;
   private static TargetDataLine tDataLine = null;

   public static void pause ()
   {
      Recording = false;
   }

   public static void resume ()
   {
      Recording = true;
   }

   public static void start ()
   {
      Running = true;
      Recording = true;

      if (info == null)
         try {
            info = new DataLine.Info (TargetDataLine.class, audioFormatRec);
            tDataLine = (TargetDataLine) AudioSystem.getLine(info);
         }
         catch (Exception e) {}

      try
      {
         tDataLine.open(audioFormatRec);
         tDataLine.start();
      }
      catch (LineUnavailableException e)
      {
         System.err.println("Line unavailable: " + e);
         System.exit(-2);
      }

      Runnable runner = new Runnable()
      {
         int bufferSize = (int) formatRec.getSampleRate() * formatRec.getFrameSize();
         byte buffer[] = new byte[bufferSize];

         public void run()
         {
            try
            {
               AudioSystem.write(new AudioInputStream(tDataLine), AudioFileFormat.Type.WAVE, new File ("audio.wav"));
               System.err.println("Pecadorl!");
            }
            catch (Exception e) {}
         }
      };

      Thread captureThread = new Thread(runner);
      captureThread.start();
   }

   public static void stop ()
   {
      System.err.println("Por mi audio que me paro!");
      Running = false;
      Recording = false;
      tDataLine.stop();
      tDataLine.close();
   }
}
