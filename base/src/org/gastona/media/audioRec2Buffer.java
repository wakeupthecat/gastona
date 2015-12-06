package org.gastona.media;

//
// from
//    http://www.java-tips.org/java-se-tips/javax.sound/capturing-audio-with-java-sound-api.html

import de.elxala.langutil.stdlib;
import de.elxala.zServices.logger;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.*;


public class audioRec2Buffer
{
   private static logger log = new logger (null, "org.gastona.media.audioRec2Buffer", null);

   public static boolean recRunning = false;
   public static boolean recRecording = false;

   public static boolean playRunning = false;
   public static boolean playPlaying = false;

   public static ByteArrayOutputStream outSamples;
   public static byte [] pxData = null;
   public static AudioFormat audioFormatRec  = new AudioFormat(44100 /* Hz */, 8 /* bits */ , 1 /* channels */, true /* signed */ , true /* bigEndian */);
   public static AudioFormat audioFormatPlay = new AudioFormat(44100 /* Hz */, 8 /* bits */ , 1 /* channels */, true /* signed */ , true /* bigEndian */);

   private static DataLine.Info info = null;
   private static TargetDataLine tDataLine = null;


   public static int ms2Samples (int milliseconds)
   {
      return (int) ((milliseconds / 1000.f) * audioFormatPlay.getFrameRate());
   }

   public static void process (String [] args)
   {
      if (pxData == null)
         pxData = outSamples.toByteArray();

      if (args == null || args.length == 0) return;

      log.dbg (2, "process [" + args[0] + "]");

      // PROCESS RESET
      //      RESET
      //
      if (args[0].equalsIgnoreCase ("reset"))
      {
         pxData = outSamples.toByteArray();
         audioFormatPlay = audioFormatRec;
         return;
      }

      // PROCESS ECHO
      //      ECO, delay ms, atenuation, delay ms, atenuation, ....
      //
      if (args[0].equalsIgnoreCase ("eco") || args[0].equalsIgnoreCase ("echo"))
      {
         float [] ecos = new float [args.length - 1];
         for (int ee = 1; ee < args.length; ee += 2)
         {
            // echoes are given in args in ms, rate is Hz = samples / second
            ecos[ee-1] = ms2Samples(stdlib.atoi (args[ee+0]));
            ecos[ee+0] = (float) stdlib.atof (args[ee+1]);

            //System.out.println ("echo de " + args[ee+0] + " ms (" + ecos[ee-1] + " samples) atenua " + ecos[ee+0]);
            //System.out.println ("tengamos presente una frame rate de " + audioFormatRec.getFrameRate());
         }

         for (int ii = 0; ii < pxData.length; ii ++)
            for (int se = 0; se < ecos.length; se += 2)
               if (se+1 < ecos.length)
               {
                  int shift = (int) ecos[se];
                  float amp = ecos[se+1];
                  if (shift != 0 && amp != 0f && (ii - shift) >= 0 && (ii - shift) < pxData.length)
                     pxData[ii] += amp * pxData[ii - shift];
                     //!! pxData[ii] += amp * (pxData[ii - shift]-128);
                     //pxData[ii] = (byte) 128 + (byte) (pxData[ii]-128) + (byte) (amp * (pxData[ii - shift]-128));
               }
      }

      // PROCESS RATE
      //      RATE, input rate, output rate
      //
      if (args[0].equalsIgnoreCase ("rate") || args[0].equalsIgnoreCase ("play rate"))
      {
         int ratio = stdlib.atoi (args[1]);

         log.dbg (2, "reproduce at " + ratio + " samples per second");
         if (ratio > 0)
            audioFormatPlay = new AudioFormat(ratio, 8 /* bits */ , 1 /* channels */, true /* signed */ , true /* bigEndian */);
      }

      // PROCESS HALF
      //      HALF
      //
      if (args[0].equalsIgnoreCase ("half"))
      {
         if (pxData.length < 2) return;

         byte [] era = pxData;
         pxData = new byte[pxData.length / 2];
         for (int ii = 0; ii < era.length; ii += 2)
         {
            if (ii+1 < era.length)
               pxData[ii/2] = (byte) ((era[ii] + era[ii+1]) / 2);
         }
         return;
      }

      if (args[0].equalsIgnoreCase ("reverse"))
      {
         for (int ii = 0; ii < pxData.length / 2; ii ++)
         {
            byte poffer = pxData[pxData.length-ii-1];
            pxData[pxData.length-ii-1] = pxData[ii];
            pxData[ii] = poffer;
         }
         return;
      }

      // PROCESS RATE
      //      REPEAT, interv ms
      //
      if (args[0].equalsIgnoreCase ("repeat"))
      {
         int silabLen = ms2Samples(stdlib.atoi (args[1]));
         int indx = 0;

         byte [] era = pxData;
         pxData = new byte[pxData.length * 2];

         for (int tt = 0; tt < pxData.length / silabLen; tt ++)
         {
            if (indx >= pxData.length) break;
            for (int ii = 0; ii < silabLen; ii ++)
               if (indx < pxData.length)
                  pxData[indx++] = era[tt* silabLen + ii];

            if (indx >= pxData.length) break;
            for (int ii = 0; ii < silabLen; ii ++)
               if (indx < pxData.length)
                  pxData[indx++] = era[tt * silabLen + ii];
         }
         return;
      }

      // PROCESS FIR
      //      FIR, val, val, val ...
      //
      if (args[0].equalsIgnoreCase ("fir")||args[0].equalsIgnoreCase ("filter")||args[0].equalsIgnoreCase ("linear"))
      {
         int T = args.length - 1;
         float [] acu = new float [T];
         float [] h = new float [T];
         for (int nn = 1; nn < args.length; nn ++)
            h[nn-1] = (float) stdlib.atof (args[nn]);

         System.out.println ("convoluixo ! " + h.length);
         // convolution not considering the filter length T (aprox 0)
         int indxAcu = 0;
         for (int nn = 0; nn < pxData.length; nn ++)
         {
            acu[indxAcu] = 0;
            for (int tt = 0; tt < T; tt ++)
               if (nn-tt > 0 && nn-tt < pxData.length)
                  acu[indxAcu] += (pxData[nn-tt]-128) * h[tt];

            indxAcu = (indxAcu++) % T;
            if (nn+1-T >= 0)
            {
               pxData[nn+1-T] = (byte) (128 + acu[indxAcu]);
            }
         }
         return;
      }
   }

   public static void recPause ()
   {
      recRecording = false;
   }

   public static void recResume ()
   {
      recRecording = true;
   }

   public static void recStart ()
   {
      log.dbg (2, "recStart", "");

      recRunning = true;
      recRecording = true;

      pxData = null;

      if (info == null)
         try
         {
            info = new DataLine.Info (TargetDataLine.class, audioFormatRec);
            tDataLine = (TargetDataLine) AudioSystem.getLine(info);
         }
         catch (Exception e)
         {
            log.err ("recStart", "Exception opening rec line: " + e);
            return;
         }

      try
      {
         tDataLine.open(audioFormatRec);
         tDataLine.start();

         Runnable runner = new Runnable()
         {
            int bufferSize = (int)audioFormatRec.getSampleRate() * audioFormatRec.getFrameSize();
            byte buffer[] = new byte[bufferSize];

            public void run()
            {
               outSamples = new ByteArrayOutputStream();

               recRunning = true;
               try
               {
                  while (recRunning)
                  {
                     int count = tDataLine.read(buffer, 0, buffer.length);
                     int pp = 0;
                     do
                     {
                        outSamples.write(buffer, pp ++, 1);
                     }
                     while (pp < count && recRecording);
                     while (!recRecording && recRunning)
                     {
                        tDataLine.read(buffer, 0, buffer.length);
                        try { Thread.sleep (100); } catch (Exception e) {}
                     }

// alternativa ...
//                     int count = tDataLine.read(buffer, 0, buffer.length);
//                     if (count > 0 && recRecording)
//                     {
//                        outSamples.write(buffer, 0, count);
//                     }
                  }
                  outSamples.close();
                  tDataLine.stop ();
                  tDataLine.close ();
               }
               catch (Exception e)
               {
                  log.err ("recStart", "Exception while recording: " + e);
                  return;
               }
            }
         };
         Thread captureThread = new Thread(runner);
         captureThread.start();
      }
      catch (Exception e)
      {
         log.err ("recStart", "Exception opening recording line: " + e);
         return;
      }
   }

   public static void recStop ()
   {
      log.dbg (2, "recStop", "");
      recRunning = false;
      recRecording = false;
   }

   public static void playStop ()
   {
      log.dbg (2, "playStop", "");
      playRunning = false;
      playPlaying = false;
   }

   public static void playPause ()
   {
      log.dbg (2, "playPause", "");
      playPlaying = false;
   }

   public static void playResume ()
   {
      log.dbg (2, "playStop", "");
      playPlaying = true;
   }

   public static void playStart ()
   {
      log.dbg (2, "playStart", "");
      playRunning = true;
      playPlaying = true;

      try
      {
         process (null);
         final AudioInputStream playAis = new AudioInputStream(new ByteArrayInputStream(pxData),
                                        audioFormatPlay,
                                        pxData.length / audioFormatPlay.getFrameSize());
         /*
         NOTE:
            For some bizarre reason playLine as well as playInfo has to be
            local variables, if we try to use static global variables the
            exception

            java.lang.ClassCastException: com.sun.media.sound.DirectAudioDevice$DirectTDL cannot be cast to javax.sound.sampled.SourceDataLine

            will be launched (?!) reason unknown

            (playAis does not have to be local but we do it anyway)
         */

         DataLine.Info playInfo = new DataLine.Info (SourceDataLine.class, audioFormatPlay);
         final SourceDataLine playLine = (SourceDataLine) AudioSystem.getLine(playInfo);

         playLine.open(audioFormatPlay);
         playLine.start();

         Runnable runner = new Runnable()
         {
            int bufferSize = (int) audioFormatPlay.getSampleRate() * audioFormatPlay.getFrameSize();
            byte buffer[] = new byte[bufferSize];

            public void run()
            {
               try
               {
                  while (playRunning)
                  {
                     int count = playAis.read(buffer, 0, buffer.length);
                     if (count <= 0) break;
                     int pp = 0;
                     do
                     {
                        while (playPlaying && pp < count)
                           playLine.write(buffer, pp++, 1);

                        while (! playPlaying && playRunning)
                           try { Thread.sleep (100); } catch (Exception e) {}

                     } while (playRunning && pp < count);
                  }
                  playLine.drain();
                  playLine.close();
                  playRunning = false;
                  playPlaying = false;
               }
               catch (Exception e)
               {
                  log.err ("playStart", "Exception while playing: " + e);
                  return;
               }
            }
         };
         Thread playThread = new Thread(runner);
         playThread.start();
      }
      catch (Exception e)
      {
         log.err ("playStart", "Exception starting play: " + e);
         return;
      }
   }
}
