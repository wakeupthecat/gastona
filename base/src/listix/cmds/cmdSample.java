package myCommands;

/**
   class: cmdSample

*/
public class cmdSample implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "SAMPLE",
          "MY SAMPLE",
      };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);


      /* ============= Getting arguments

          int nArg = cmd.getArgSize ();

          String arg0 = cmd.getArg(0);
          String arg1 = cmd.getArg(1);
      */

      /* ============= Getting options simple

          String myOption1 = cmd.takeOptionString("OPTION1", "1");
      */

      /* ============= Accesing data and listix formats

          Eva theVar = cmd.getListix ().getVarEva ("Variable Name");

          if (theVar == null)

      */

      // ============= Logging messages
      //
      //    cmd.getLog().dbg (2, "SAMPLE", "something as debug message");
      //    cmd.getLog().err ("SAMPLE", "some error message (demo cmdSample)");


      //cmd.checkRemainingOptions (true);
      return 1;
   }
}

