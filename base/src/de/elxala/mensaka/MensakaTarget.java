/*
packages de.elxala
Copyright (C) 2005 Alejandro Xalabarder Aulet

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

package de.elxala.mensaka;

/**	======== de.elxala.langutil.MensakaPaket ==========================================
	@author Alejandro Xalabarder 25.02.2003 16:39

	06.08.2004 21:57: nuevo package de.elxala.mensaka
*/

import de.elxala.Eva.*;

public interface MensakaTarget
{
	public boolean takePacket (int mappedMsg, EvaUnit pk, String [] parameters);
};

