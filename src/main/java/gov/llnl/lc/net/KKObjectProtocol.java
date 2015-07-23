/************************************************************
 * Copyright (c) 2015, Lawrence Livermore National Security, LLC.
 * Produced at the Lawrence Livermore National Laboratory.
 * Written by Timothy Meier, meier3@llnl.gov, All rights reserved.
 * LLNL-CODE-673346
 *
 * This file is part of the OpenSM Monitoring Service (OMS) package.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (as published by
 * the Free Software Foundation) version 2.1 dated February 1999.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * OUR NOTICE AND TERMS AND CONDITIONS OF THE GNU GENERAL PUBLIC LICENSE
 *
 * Our Preamble Notice
 *
 * A. This notice is required to be provided under our contract with the U.S.
 * Department of Energy (DOE). This work was produced at the Lawrence Livermore
 * National Laboratory under Contract No.  DE-AC52-07NA27344 with the DOE.
 *
 * B. Neither the United States Government nor Lawrence Livermore National
 * Security, LLC nor any of their employees, makes any warranty, express or
 * implied, or assumes any liability or responsibility for the accuracy,
 * completeness, or usefulness of any information, apparatus, product, or
 * process disclosed, or represents that its use would not infringe privately-
 * owned rights.
 *
 * C. Also, reference herein to any specific commercial products, process, or
 * services by trade name, trademark, manufacturer or otherwise does not
 * necessarily constitute or imply its endorsement, recommendation, or favoring
 * by the United States Government or Lawrence Livermore National Security,
 * LLC. The views and opinions of authors expressed herein do not necessarily
 * state or reflect those of the United States Government or Lawrence Livermore
 * National Security, LLC, and shall not be used for advertising or product
 * endorsement purposes.
 *
 *        file: KKObjectProtocol.java
 *
 *  Created on: Jun 17, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

/**********************************************************************
 * Describe purpose and responsibility of KKObjectProtocol
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 17, 2011 2:28:13 PM
 **********************************************************************/
public class KKObjectProtocol extends AbstractObjectProtocol implements SerialObjectProtocol
{
  private static final int WAITING        = 0;
  private static final int SENTKNOCKKNOCK = 1;
  private static final int SENTCLUE       = 2;
  private static final int ANOTHER        = 3;

  private static final int NUMJOKES       = 5;

  private int              state          = WAITING;
  private int              currentJoke    = 0;

  private String[]         clues          = { "Turnip", "Little Old Lady", "Atch", "Who", "Who" };
  private String[]         answers        = { "Turnip the heat, it's cold in here!",
      "I didn't know you could yodel!", "Bless you!", "Is there an owl in here?",
      "Is there an echo in here?"        };

  public Object processInput(Object inObj)
  {
    String theOutput = null;
    String theInput = null;
    if (inObj instanceof String)
    {
      theInput = new String((String) inObj);
    }
 
    if (state == WAITING)
    {
      theOutput = "Knock! Knock!";
      state = SENTKNOCKKNOCK;
    }
    else if (state == SENTKNOCKKNOCK)
    {
      if (theInput.equalsIgnoreCase("Who's there?"))
      {
        theOutput = clues[currentJoke];
        state = SENTCLUE;
      }
      else
      {
        theOutput = "You're supposed to say \"Who's there?\"! " + "Try again. Knock! Knock!";
      }
    }
    else if (state == SENTCLUE)
    {
      if (theInput.equalsIgnoreCase(clues[currentJoke] + " who?"))
      {
        theOutput = answers[currentJoke] + " Want another? (y/n)";
        state = ANOTHER;
      }
      else
      {
        theOutput = "You're supposed to say \"" + clues[currentJoke] + " who?\""
            + "! Try again. Knock! Knock!";
        state = SENTKNOCKKNOCK;
      }
    }
    else if (state == ANOTHER)
    {
      if (theInput.equalsIgnoreCase("y"))
      {
        theOutput = "Knock! Knock!";
        if (currentJoke == (NUMJOKES - 1))
          currentJoke = 0;
        else
          currentJoke++;
        state = SENTKNOCKKNOCK;
      }
      else
      {
        theOutput = "Bye.";
        state = WAITING;
      }
    }
    return theOutput;
  }

  /************************************************************
   * Method Name:
   *  getClientProtocolName
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.SerialObjectProtocol#getClientProtocolName()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public String getClientProtocolName()
  {
    return new KKObjectClient().getClass().getCanonicalName();
  }

}

