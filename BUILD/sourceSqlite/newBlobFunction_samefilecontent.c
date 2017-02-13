// Proposal:
//
//    a new built function "samefilecontent(filename, blobfield)" in slite
//
//
// Motivation:  
//    
//    The useful SQL functions "readfile" and "writefile"
//    allow transfering files into blobs and viceversa.
//
//    Having files in blob columns allows file comparation
//    in sql queries by simply comparing values of its blob columns.
//    But in order to do so, we need that both files (blob values) are 
//    loaded in the database.
//    
//    A new function to compare a blob value directly with the content 
//    of a file without having to load the second one into the database
//    may be convenient in many applications.
//
//    For example, we want to know if the content of a file is already 
//    in the database and only if not found the load it into the database.
//    So using "samefilecontent" function we can avoid expensive disc operations
//    specially if the files are big.
//
//    EXAMPLE: if we have a table fileData (id, size, fcontent) a query 
//    to search for a file 'mybigfile.mpg' of size XXXX in the database could be 
//
//    SELECT id FROM fileData WHERE size+0 = XXXX AND samefilecontent('mybigfile.mpg', fcontent);
//
//
// Note:
//
//    The file is read in blocks of size MAXBLOCKREAD to prevent out of memory when comparing huge files.
//
// Alejandro Xalabarder (the author) says: this function is public domain

/**
   samefilecontent(filename, blobfield)
*/
static void samefilecontent (
  sqlite3_context *context,
  int argc,
  sqlite3_value **argv
 )
{
   const char * zFileName = 0;
   const char * zBlobBuff;
   FILE * fileIn = 0;
   sqlite3_int64 rcSame = 0;
   long fileSize = 0;
   long MAXBLOCKREAD = 10001024;

   UNUSED_PARAMETER(argc);
   
   // open the file to read
   //
   zFileName = (const char*) sqlite3_value_text (argv[0]);
   if (zFileName == 0) return;
   fileIn = fopen (zFileName, "rb");
   if (fileIn == 0) return;
   fseek (fileIn, 0, SEEK_END);
   fileSize = ftell (fileIn);
   rewind (fileIn);
   
   rcSame = 0;
   if (sqlite3_value_bytes(argv[1]) != fileSize)
   {
     fclose(fileIn);
     sqlite3_result_int64(context, rcSame);
     return;
   }

   // prepare file buffer (max 10MB)
   //
   rcSame = 1; // assume true
   
   long fileOffset = 0;
   long fileBuffSize = (fileSize < MAXBLOCKREAD ? fileSize : MAXBLOCKREAD);
   char * zFileBuff = sqlite3_malloc(fileBuffSize);
   if (zFileBuff == 0) rcSame = 0;

   // load the blob content :(
   //
   if (rcSame)
   {
       zBlobBuff = (const char*)sqlite3_value_blob(argv[1]);
       if (zBlobBuff == 0) rcSame = 0;
   }

   while (rcSame && fileOffset < fileSize)
   {
      if (fileOffset + fileBuffSize > fileSize)
      {
         fileBuffSize = fileSize - fileOffset;
      }
      if (1 != fread(zFileBuff, fileBuffSize, 1, fileIn))
      {
         fprintf(stderr,"Error: samefilecontent cannot read %d bytes at offset %d from file \"%s\"\n", fileBuffSize, fileOffset, zFileName);
         rcSame = 0;
         break; // error            
      }         
      rcSame = 0 == memcmp(zBlobBuff + fileOffset, zFileBuff, fileBuffSize);
      fileOffset += fileBuffSize;
   }
   
   sqlite3_free(zFileBuff);
   fclose(fileIn);
   sqlite3_result_int64(context, rcSame);
}


/*
   ***** TO ENABLE THE NEW FUNCION THE CORRESPONDING sqlite3_create_function 
         HAS TO BE ADDED IN THE FUNCTION open_db

static void open_db(ShellState *p, int keepAlive){
  if( p->db==0 ){
    ...
    ...
    sqlite3_create_function(p->db, "samefilecontent", 2, SQLITE_UTF8, 0, samefilecontent, 0, 0);
  }
}


-------- Complete open_db ---------------------------

static void open_db(ShellState *p, int keepAlive){
  if( p->db==0 ){
    sqlite3_initialize();
    sqlite3_open(p->zDbFilename, &p->db);
    globalDb = p->db;
    if( p->db && sqlite3_errcode(p->db)==SQLITE_OK ){
      sqlite3_create_function(p->db, "shellstatic", 0, SQLITE_UTF8, 0,
          shellstaticFunc, 0, 0);
    }
    if( p->db==0 || SQLITE_OK!=sqlite3_errcode(p->db) ){
      utf8_printf(stderr,"Error: unable to open database \"%s\": %s\n", 
          p->zDbFilename, sqlite3_errmsg(p->db));
      if( keepAlive ) return;
      exit(1);
    }
#ifndef SQLITE_OMIT_LOAD_EXTENSION
    sqlite3_enable_load_extension(p->db, 1);
#endif
    sqlite3_create_function(p->db, "readfile", 1, SQLITE_UTF8, 0,
                            readfileFunc, 0, 0);
    sqlite3_create_function(p->db, "writefile", 2, SQLITE_UTF8, 0,
                            writefileFunc, 0, 0);
    sqlite3_create_function(p->db, "samefilecontent", 2, SQLITE_UTF8, 0, samefilecontent, 0, 0);
  }
}

*/
