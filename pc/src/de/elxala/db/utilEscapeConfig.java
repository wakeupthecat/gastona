/*
library de.elxala
Copyright (C) 2014 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package de.elxala.db;

/**
   To allow different default behaviour for Android (apk) than for PC (jar)

   PC prefer native encode (~1 ~2 etc)
   and Android use better UTF-8 to support International characters
   Idea is also migrate PC to UTF-8 , inclusive gastona welcome documentation database

*/
public class utilEscapeConfig
{
   public static final char SEPARATOR = '|';
   public static String ENCODE_MODEL_NAME = ""; // "UTF-8" used in Android
}
