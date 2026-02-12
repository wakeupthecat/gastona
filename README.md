#  What is Gastona

Gastona is a scripting language to make applications easily. Among other
features it has out of the box things like

- rapid GUI building
- SQL (sqlite) and javascript (Rhino) integrated
- powerful text generator (i.e. HTML)
- scanning directories and parsing files
- communications using integrated UDP and HTTP servers

All these are quite powerful tools for building application and using
them with Gastona is really straightforward.

It is a GPLv3 open source project implemented in java in two variants (*)

- Desktop/PC (gastona.jar) that runs in Windows, Linux, Raspberry Pi and Mac OSX
- Android App (gastona.apk) for android smart phones and tablets

Both use exactly the same script language so it is possible to develop an
application and run it in all of these systems.

(*) the two variants in this repository, there are still two other gastona ports in C++ and Python on the way

## Is Gastona different ?

Yes, it is. It has no similarities to any programming or scriptimg language. Actually the main goal
is precisely to save programming effort as much as possible.

This can be achieved with a very pragmatic approach, simplification if you want,
following the saying "a button is a button". For example if I want an app with a
label a list and a button, do I need much more than just three words (for instance "lPeople", "iPeople" and "bAction") 
to put that components on the user interface ? 
The gastona approach tries to be the closest to that.

Also very important, using for all purposes of the language an unsophisticated 
but flexible enough data structure. This much more simple structure avoid trees (used in json, xml, html etc)
and this "stepping out of the tree" results in a much more writable, readable and in the end understandable code.

The best way to learn it is to run the desktop application gastona.jar which comes with
a complete documentation of the language, widgets and commands. Almost every element in the
documentation has one or several related sample scripts that can be modified and run immediately.

## Getting Gastona

This Github repository contains the source code for both Desktop (or PC) and Android. Actually many
java sources are shared by the two variants, specifically all what is inside the folder "base".

Binaries (old ones) for both desktop and android apk can be found at
<a href="https://sourceforge.net/projects/gastona/files/Gastona%20v1.10/">sourceforge.net/projects/gastona/</a>.

For running the interactive documentation

    java -jar gastona.jar
    
For running a gastona script within a file like for example myapp.gast

    java -jar gastona.jar myapp.gast
    

### Hello world 

There are two possible hello world applications: command line or with GUI

Without GUI the code involves only listix component

        ---- helloworld_1.gast
        #listix#

            <main>
                //Hello world!
                
running 

        java -jar gastona.jar helloworld_1.gast
        Hello world!

Using a minimal GUI only containing a console

        ---- helloworld_2.gast
        #javaj#
            
            <frames> ooConsole

        #listix#

            <main>
                //Hello world!

running 

        java -jar gastona.jar helloworld_2.gast

These two examples work directly because these files are contained inside gastona.jar and gastona search 
for files also inside its own jar


## Small demo

Probably the best introduction even before a tutorial and start learning rules is to see
a quick demo to have the feeling of how the things are done and what you can get from this language.

### The problem to solve

Suppose we want

- a list of contacts with some data
- show more information about the contact on selecting an entry
- do some action over the selected entry when a button is pressed

we have expressed the requirements using few words, would not be nice to
code it in few words as well?

### The coding

We separate GUI from logic and also from data. The first two are personalized: javaj is the
responsible guy for the GUI and listix for the logic. Both do their job according to variables
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

the script would also work on Android, but if the device is a smart-phone we better use
this code for the action

         <-- bAction>
            CHECK, VAR, iPeople selected.phone, BOX, I, No entry selected!
            INTENT, DIAL, tel:@<iPeople selected.phone>

and this would be the result in Android

![demoandroid60](https://cloud.githubusercontent.com/assets/12417703/18233855/a94f8786-72f3-11e6-802c-0f980d8fb96e.png)

### What about the web?

I start saying there are two variants of gastona, well a third variant is growing right now
and it is a javascript library.

If the gastona language turns out to be useful and convenient, could it be used for rendering our application in a browser as well?

The answer is yes, jGastona.js and the family Eva.js, EvaLayout.js and LayoutManager.js can do that. These are
pure javascript libraries that implement Gastona for a browser. While respect to javaj (GUI) and data there are
clear resemblances, the listix commands are actually not implemented in js, instead all listix formats only on javascript code.

It would be possible to do a dynamic page with jGastona using AJAX for instance driven by the gastona integrated HTTTP server MICOHTTP. 
But for the demo we just make a static html

      <html><body>
      <script src="jGastonaEva-min.js"> </script>
      <script id="mainJast" type="jast">
            #javaj#

               <layout of main>
                  EVALAYOUT, 4, 4, 3, 3

                           , X
                           , lPeople
                        X  , iPeople
                           , bAction

            #data#

               <iPeople>
                  name    , address    , phone
                  Marvin  , Bonasera   , 888
                  Salma   , Hauptstr   , 555

            #listix#

               <-- iPeople>
                  //alert ("You have selected " + jast.getData ('iPeople selected.name'));

               <-- bAction>
                  //alert ("Calling " + jast.getData ('iPeople selected.phone'));
      </script>

      <script>

         var jast = jGastona (evaFileStr2obj (document.getElementById ("mainJast").text));
         jast.start ();

      </script></body></html>

having <a href="https://github.com/wakeupthecat/gastona/blob/master/META-GASTONA/js/jGastonaEva-min.js">jGastonaEva-min.js</a> in the same directory the result in the browser is

![demojgastona](https://cloud.githubusercontent.com/assets/12417703/18233827/8d05749c-72f2-11e6-9f18-baa451e913a4.PNG)

Note that we haven't specify any html element for the body, jGastona will create all them on the fly according to the given script.
