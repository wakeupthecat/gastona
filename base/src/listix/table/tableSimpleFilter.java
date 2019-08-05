/*
library listix (www.listix.org)
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

package listix.table;


/**
     opt table     opt params
      ---------     ------------
OPT TABLE, FILE FILTER, DIR ROOT, D:\Camon

      operator:  =, ==     equal
                 <>, !=    different
                 <         less than
                 >         great than
                 <=        less or equal
                 >=        great or equal
*/
public class tableSimpleFilter
{
   private static final int OP_INVALID = -1;
   private static final int OP_EQU = 0;
   private static final int OP_EQU_LESS = 1;
   private static final int OP_EQU_GREAT = 2;
   private static final int OP_LESS = 3;
   private static final int OP_GREAT = 4;
   private static final int OP_DIFFERENT = 5;

   private int indexField = -1;
   private int ioperation = OP_INVALID;
   private String valRef = null;
   private boolean isCaseSensitive = true;

   /**
      @see init(...)
   */
   public tableSimpleFilter (String operation, String operandVal)
   {
      init (operation, operandVal, -1, true);
   }

   public tableSimpleFilter (String operation, String operandVal, boolean caseSensitive)
   {
      init (operation, operandVal, -1, caseSensitive);
   }

   public tableSimpleFilter (String operation, String operandVal, int associatedIndx)
   {
      init (operation, operandVal, associatedIndx, true);
   }

   public tableSimpleFilter (String operation, String operandVal, int associatedIndx, boolean caseSensitive)
   {
      init (operation, operandVal, associatedIndx, caseSensitive);
   }

   /**
      @param operation  One of the following constant strings "=", "==", "<>", "!=", "<=", "<", ">", ">="
      @param operandVal The value of one of the operands, it can be the left or the right operand (first or second)
                        depending on that the correct function for the evaluation has to be used. If we give in the
                        constructor the first operand the function passOperand2(String) has to be used giving the
                        second operand and viceversa.
      @param associatedIndx additionally an index may be associated with the filter object, for instance a column index
                             this index can be later retrieved with the function getAssociatedIndex (). This value has no other
                             influence in the filter.
   */
   public void init (String operation, String operandVal, int associatedIndx, boolean caseSensitive)
   {
      indexField = associatedIndx;
      if (operation.equals("=") || operation.equals("=="))
         ioperation = OP_EQU;
      else if (operation.equals("<>") || operation.equals("!="))
         ioperation = OP_DIFFERENT;
      else if (operation.equals("<="))
         ioperation = OP_EQU_LESS;
      else if (operation.equals(">="))
         ioperation = OP_EQU_GREAT;
      else if (operation.equals("<"))
         ioperation = OP_LESS;
      else if (operation.equals(">"))
         ioperation = OP_GREAT;

      valRef = operandVal;
      isCaseSensitive = caseSensitive;
   }

   public int getAssociatedIndex ()
   {
      return indexField;
   }

   public boolean isValidOperation ()
   {
      return ioperation != OP_INVALID;
   }

   public boolean passOperand1 (String firstOperand)
   {
      return pass(firstOperand, valRef);
   }

   public boolean passOperand2 (String secondOperand)
   {
      return pass(valRef, secondOperand);
   }

   private boolean pass (String firstOperand, String secondOperand)
   {
      int que = isCaseSensitive ? firstOperand.compareTo (secondOperand): firstOperand.compareToIgnoreCase (secondOperand);

//System.out.println (valRef + " compared to " + valueField + " rekornoskastas " + que + " operasao was " + ioperation);

      switch (ioperation)
      {
         case OP_EQU:        return que == 0;
         case OP_EQU_LESS:   return que <= 0;
         case OP_EQU_GREAT:  return que >= 0;
         case OP_LESS:       return que < 0;
         case OP_GREAT:      return que > 0;
         case OP_DIFFERENT:  return que != 0;
         default:
            break;
      }
      return false;
   }
}
