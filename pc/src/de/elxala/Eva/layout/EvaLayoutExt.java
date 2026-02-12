/*
package de.elxala.Eva.layout
(c) Copyright 2025-2026 Alejandro Xalabarder Aulet

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


package de.elxala.Eva.layout;

import de.elxala.Eva.*;
import de.elxala.zServices.*;


/**
   @author    Alejandro Xalabarder
   @date      2025-03-08 13:55:49

    EvaLayoutExt class is a way of supporting <evalay xxx> format
    and in that case transform the input eva "<layeva xxx>" into "<lay xx>"

    in order to do that a configuration for the layeva is needed, it can be changed by the application
    for instance declaring <layeva-config> variable

    for examle if we have

        <layeva-config> 10, 10, 4, 2
        <layeva xx>
            , X
            , bA, bB

    we transform it into

        <lay xx>
          EVALAYOUT, 10, 10, 4, 2
              , X
              , bA, bB

    Use: Actually all instances of EvaLayout created with some configuation data unit should use EvaLayoutExt

    for example:

        EvaLayout mylay = new EvaLayoutExt (elaData, "4,4,2,1");

    or alternatively

        EvaLayout mylay = new EvaLayout (EvaLayoutExt.normalizeEvalayoutVar (elaData, "4,4,2,1");

*/
public class EvaLayoutExt extends EvaLayout
{
    protected static final String LAYEVA_ROW0_FALLBACK = "EVALAYOUT,5,5,3,3";
    private static logger log = new logger (null, "de.elxala.Eva.EvaLayoutExt", null);


    //  evalay the Eva variable containing either the EVALAYOUT in <lay xxxx> or <layeva xxxx> format
    //
    //  layevaConfig is an Eva of one line having the configuration for the evalayout grid
    //  that is MarginX, marginY , gapX, gapY
    //
    //  example:
    //
    //      EvaLayoutExt (myEvalayout, new Eva ("6,6,4,4"));
    //
    public EvaLayoutExt (Eva evalay, Eva layevaConfig)
    {
        super (EvaLayoutExt.normalizeEvalayoutVar (evalay, layevaConfig));
    }

    static public Eva normalizeEvalayoutVar (Eva evalay, Eva layevaConfig)
    {
        Eva evalay2 = evalay;
        if (evalay.getName ().startsWith ("layeva "))
        {
            log.dbg (2, "EvaLayoutExt", "need to transorm layeva to EvaLayout form " + evalay.getName ());

            String ename = "lay " + evalay.getName ().substring("layeva ".length ());
            evalay2 = new Eva (ename);

            // add the first line (std EVALAYOUT ...)
            //
            evalay2.addLine (new EvaLine (LAYEVA_ROW0_FALLBACK));

            // if config margx,margy,gapx,gapy is given then change the values
            //
            if (layevaConfig != null && layevaConfig.rows () > 0)
            {
                // copy only the columns of row 0 shifted by 1
                for (int cc = 0; cc < layevaConfig.cols(0); cc ++)
                    evalay2.setValue (layevaConfig.getValue (0, cc), 0, cc+1);
            }

            // copy the rest of the evalayout
            //
            for (int rr = 0; rr < evalay.rows(); rr ++)
                evalay2.addLine (evalay.get (rr));
        }

        return evalay2;
    }
}
