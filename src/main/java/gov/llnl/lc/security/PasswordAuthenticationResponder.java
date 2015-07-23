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
 *        file: PasswordAuthenticationResponder.java
 *
 *  Created on: Jun 16, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.logging.CommonLogger;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;

/**********************************************************************
 * Provides a simple password authentication handshake.
 * <p>
 * @see  gov.llnl.lc.security.PasswordAuthenticator
 *
 * @author meier3
 * 
 * @version Jun 16, 2011 9:01:26 AM
 **********************************************************************/
public class PasswordAuthenticationResponder extends AbstractAuthenticationResponder implements AuthenticationResponder, AuthenticationConstants, CommonLogger
{
  /*
   * (non-Javadoc)
   * 
   * @see
   * gov.llnl.lc.security.AuthenticationResponder#requestAuthentication(java
   * .io.BufferedReader, java.io.PrintWriter)
   */
  public boolean requestAuthentication(BufferedReader in, PrintWriter out) throws IOException
  {
    /* FIXME break out if Server sends something other than expected response */
    boolean done = false;
    String fromServer;

    /* first two exchanges are for OUN and passcode */
    Console con = System.console();
    while (!done && (fromServer = in.readLine()) != null)
    {
      char[] passcode;

      if (fromServer.equals(PASSWORD_PROMPT))
      {
        passcode = con.readPassword(PASSWORD_PROMPT);
        out.println(new String(passcode));
      }
      if (fromServer.equals(AUTHENTICATED))
      {
        done = true;
        authenticated = true;
      }
      if (fromServer.equals(AUTHENTICATION_DENIED))
      {
        done = true;
        logger.warning(AUTHENTICATION_DENIED);
      }
    }
    return authenticated;
  }

  public boolean requestAuthentication(BufferedReader in, PrintWriter out, String uname,
      String passcode) throws IOException
  {
    /* doesn't use uname, so it can be null */
    /* FIXME break out if Server sends something other than expected response */
    boolean done = false;
    String fromServer;

    /* first two exchanges are for OUN and passcode */
    while (!done && (fromServer = in.readLine()) != null)
    {
      if (fromServer.equals(PASSWORD_PROMPT))
      {
        out.println(passcode);
      }
      if (fromServer.equals(AUTHENTICATED))
      {
        done = true;
        authenticated = true;
      }
      if (fromServer.equals(AUTHENTICATION_DENIED))
      {
        done = true;
        logger.warning(AUTHENTICATION_DENIED);
      }
    }
    return authenticated;
  }
}
