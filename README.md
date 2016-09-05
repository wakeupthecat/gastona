#  What is Gastona

Gastona is a scripting language to make applications easily. Among other
features it has out of the box things like 

- rapid GUI building
- SQL integrated
- scaning directories and parsing files
- powerful text generator
- communications using UDP and an amazing HTTP server integrated

All these are quite powerful tools for building application and using
them with Gastona is really straighforward.

It is a GPLv3 open source project implemented in java in two variants
   
- Desktop/PC (gastona.jar) that runs in Windows, Linux, Raspberry Pi and Mac OSX
- Android App (gastona.apk) for android smart phones and tablets

Both use exactly the same script language so it is possible to develop an
application and run it in all of these systems.

## Is Gastona different ?

Yes, it is. It has no similarity to any programming language. The main goal
is precisely to save programming effort as much as possible. 

This can be achieved with a very pragmatic approach, simplification if you want, 
like to say "a button is a button" etc. And also very important, using an unsophisticated
but flexible enough data structure for all purposes of the language.

The best way to learn it is to run the desktop application gastona.jar which comes with
a complete documentation of the language, widgets and commands. Almost every element in the
documentation has one or several related sample scripts that can be modified and run immediately.

## Getting Gastona

This Github repository contains the source code for both Desktop (or PC) and Android. Actually many
sources are shared by the two variants, specifically all what is inside the folder "base".

The App for Android is available at the Google market for aproximately 1 Euro which represents
a support for the project. It is also available as apk to be installed manually.

Binaries for both desktop and android can be found at 
<a href="https://sourceforge.net/projects/gastona/files/Gastona%20v1.10/">sourceforge.net/projects/gastona/</a>.

## Small demo

Probably the best introduction even before a tutorial and start learnig rules is to see 
a quick demo to have the feeling of how the things are done and what you can get from this language.

### The problem

Suppose we want 

- a list of contacts with some data
- show more information about the contact on selecting an entry 
- do some action over the selected entry when a button is pressed

we have expressed the requirements using few words, would not be nice to
code it in few words as well?

### The coding

We separate GUI from logic and also from data. The first two are personalized: javaj is the
responsible guy for the GUI and listix for the logic. Both do their job acording to variables
that we set to them. In this case the script could be

      #javaj#
         
         <layout of main>
            EVALAYOUT, 4, 4, 3, 3
               
                     , X
                     , lPeople
                  X  , iPeople
                     , bAction
                  X  , oSalida

      #data#
      
         <iPeople visibleColumns> name
         <iPeople>
            name    , address    , phone
            Marvin  , Bonasera   , 888
            Salma   , Hauptstr   , 555

      #listix#
      
         <-- iPeople>   
            //Selected @<iPeople selected.name> who lives in @<iPeople selected.address>
            //Call now ?
            //

         <-- bAction>
            //Ok calling @<iPeople selected.phone> ...
            //

   
executing this script with gastona.jar will result in the desktop application

![demopc](https://cloud.githubusercontent.com/assets/12417703/18233823/7d653f90-72f2-11e6-848e-6eb8acfff821.png)

the script would also work on Android, but if the device is a phone better use
this code for the action

         <-- bAction>
            CHECK, VAR, iPeople selected.phone, BOX, I, No entry selected!
            INTENT, DIAL, tel:@<iPeople selected.phone>

and this would be the result in Android

![demoandroid60](https://cloud.githubusercontent.com/assets/12417703/18233855/a94f8786-72f3-11e6-802c-0f980d8fb96e.png)

### What about the web?

I start saying there are two variants of gastona, well a third variant is growing right now
and it is a javascript library.

If the scripting language turns out to be useful and convenient, could we use it also 
for rendering our application in a browser?

The answser is yes,  jGastona.js and the family Eva.js, EvaLayout.js and LayoutManager.js can do that. These are
pure javascript libraries that implement Gastona for a browser. Not thought to be one to one 
compatible with the java variants, which is not possible and also not needed, but just as
an alternative for developing browser applications.

As any other javascript library jGastona can be used either in a static html 
or served via http with some Http server. But since gastona has also an http server, let's finish 
the demo showing jGastona launched by the Gastona http server called MICO.

This is a possible compact script to do that

     #javaj#
   
      <frames> oConsola
    
     #listix#
    
      <main>
         micohttp, start, myMico
   
     <GET />
       //<html><body>
       //
       //<script>
       //
       @<:infile META-GASTONA/js/jGastonaEva-min.js>
       //
       //   var jgas = new jGastona (evaFileUTF82obj ("@<:encode-utf-8 MAIN_JGAST>"));
       //
       //</script></body></html>

     <MAIN_JGAST>
         // #javaj#
         //    
         //    <layout of main>
         //       EVALAYOUT, 4, 4, 3, 3
         //          
         //                , X
         //                , lPeople
         //             X  , iPeople
         //                , bAction
         // 
         // #data#
         // 
         //    <iPeople>
         //       name    , address    , phone
         //       Marvin  , Bonasera   , 888
         //       Salma   , Hauptstr   , 555
         // 
         // #listix#
         // 
         //    <-- iPeople>   
         //       //alert ("You have selected " + jgas.getData ('iPeople selected.value'));
         // 
         //    <-- bAction>
         //       //alert ("Calling " + jgas.getData ('iPeople selected.label'));

the given script will work without any aditional file, though it is possible also to have the browser client side, the variable MAIN_GAST, into a separate file.

![demojgastona](https://cloud.githubusercontent.com/assets/12417703/18233827/8d05749c-72f2-11e6-9f18-baa451e913a4.PNG)

The server is sending the jGastona libraries, which are included in gastona.jar, and loading the 
particular script the rest happens in the browser handled by jGastona.

As you can see, "Gastona is different"!
