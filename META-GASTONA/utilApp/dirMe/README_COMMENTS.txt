

06.01.2010 14:46


   scan_roots


   rootID   rootLabel      pathRoot    rootType    timeLastScan

   1002     local          C:\AMIRROR  D           2010-01-06 12:01:23
   1003     local          A:\java\src D           2010-01-06 12:01:40

   1010     paka           C:\AMIRROR  D


   disenyo dirMePAKA


      [] Puede paka empaquetar ficheros de más de un pathRoot ?

          si si

            + gran flexibilidad para agrupar cosas de varios proyectos
              (p.e. PapeleraDePaka/sospechoRepetidos/tanda1.zip etc)

            - en el zip debería guardarse la información de a que root
              pertenecía (p.e. C:\AMIRROR o A:\java\src), aunque esta información
              no sería relevante para extraer (se puede extraer en cualquier directorio)
              si es necesaria para distinguir ficheros con el mismo subpath pero de
              distintos ficheros.
              Una solución sería zipear empezando con rootID p.e.
              1002/gantonchis/mifichero.txt
              1003/gantonchis/mifichero.txt

              un poco "tricky" pero funcionaría

           si no

            + facilita mucho la organización y los queries de la applicación
               p.e.
                  desvanDePaka/1002/MiPiapielieria/20100114_124311.zip
                  desvanDePaka/1002/FilesInTheAttik/20100114_124311.zip

               impuesto "desvanDePaka/1002/..." y ".../20100114_124311.zip"

      [] Organisatorisch

            rootID   rootLabel      pathRoot    rootType    timeLastScan

            1002     local          C:\AMIRROR  D           2010-01-06 12:01:23
            1003     local          A:\java\src D           2010-01-06 12:01:40

            1010     paka           1002/MiPiapielieria/20100114_124311.zip Z xxxxxx
            1011     paka           1002/bufferCollect  X   xxxxxxxxxx
               o simplemente
            1011     paka           1002/



            todos los ficheros del rootID 1002 by paka

               WHERE rootLabel='paka' AND pathRoot LIKE '1002/%'


            colectar ficheros

               INSERT INTO scan_files VALUES (@<pakCollect1002rootID>, 'paka', '1002'||"/bufferCollect"||


            enpackar ficheros

               WHERE rootID = @<pakCollect1002rootID>


         <pakCollectRootID>
            SELECT rootID FROM scan_roots WHERE rootLabel = 'paka' AND pathRoot = '@<selected rootID>/@<pakaFile>'


               