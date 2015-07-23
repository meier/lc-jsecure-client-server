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
 *        file: SessionAuthenticationResponder.java
 *
 *  Created on: Jul 6, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.logging.CommonLogger;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;

/**********************************************************************
 * Describe purpose and responsibility of SessionAuthenticationResponder
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 6, 2011 10:36:57 AM
 **********************************************************************/
public class SessionAuthenticationResponder extends AbstractAuthenticationResponder implements AuthenticationResponder, AuthenticationConstants, CommonLogger
{

  /************************************************************
   * Method Name:
   *  requestAuthentication
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.security.AuthenticationResponder#requestAuthentication(java.io.BufferedReader, java.io.PrintWriter)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param in
   * @param out
   * @return
   * @throws IOException
   ***********************************************************/

  @Override
  public boolean requestAuthentication(BufferedReader in, PrintWriter out) throws IOException
  {
    /* FIXME break out if Server sends something other than expected response */
    boolean done = false;
    String fromServer;
    
    /* initially attempt to authenticate with our session info */
    Console con = System.console();
    while (!done && (fromServer = in.readLine()) != null)
    {
      String uname = null;
      String tmp = null;
      char[] passcode;

      if (fromServer.equals(OUN_PROMPT))
      {
        /* get from the session first, or console second */
        if((ClientSession != null) && (ClientSession.getUser() != null))
          uname = ClientSession.getUser();
        else
          uname = con.readLine(OUN_PROMPT);
        out.println(uname);
      }
      if (fromServer.equals(HOST_PROMPT))
      {
        if((ClientSession != null) && (ClientSession.getHost() != null))
          tmp = ClientSession.getHost();
        else
          tmp = NULL_STRING;
        out.println(tmp);
      }
      if (fromServer.equals(PORT_PROMPT))
      {
        if((ClientSession != null) && (ClientSession.getPort() != 0))
          tmp = Integer.toString(ClientSession.getPort());
        else
          tmp = NULL_STRING;
        out.println(tmp);
      }
      if (fromServer.equals(THREAD_ID_PROMPT))
      {
        if((ClientSession != null) && (ClientSession.getThreadId() != 0))
          tmp = Long.toString(ClientSession.getThreadId());
        else
          tmp = NULL_STRING;
        out.println(tmp);
      }
      if (fromServer.equals(PASSCODE_PROMPT))
      {
        passcode = con.readPassword(PASSCODE_PROMPT);
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

  /************************************************************
   * Method Name:
   *  requestAuthentication
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.security.AuthenticationResponder#requestAuthentication(java.io.BufferedReader, java.io.PrintWriter, java.lang.String, java.lang.String)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param in
   * @param out
   * @param uname
   * @param passcode
   * @return
   * @throws IOException
   ***********************************************************/

  @Override
  public boolean requestAuthentication(BufferedReader in, PrintWriter out, String user_name, String password) throws IOException
  {
    /* FIXME break out if Server sends something other than expected response */
    boolean done = false;
    String fromServer;

    /* initially attempt to authenticate with our session info */
    Console con = System.console();
    while (!done && (fromServer = in.readLine()) != null)
    {
      String uname = null;
      String tmp = null;
      char[] passcode;

      if (fromServer.equals(OUN_PROMPT))
      {
        /* use the arg first, then from the session first, finally the console */
        if(user_name != null)
          uname = user_name;
        else if((ClientSession != null) && (ClientSession.getUser() != null))
          uname = ClientSession.getUser();
        else
          uname = con.readLine(OUN_PROMPT);
        out.println(uname);
      }
      if (fromServer.equals(HOST_PROMPT))
      {
        if((ClientSession != null) && (ClientSession.getHost() != null))
          tmp = ClientSession.getHost();
        else
          tmp = NULL_STRING;
        out.println(tmp);
      }
      if (fromServer.equals(PORT_PROMPT))
      {
        if((ClientSession != null) && (ClientSession.getPort() != 0))
          tmp = Integer.toString(ClientSession.getPort());
        else
          tmp = NULL_STRING;
        out.println(tmp);
      }
      if (fromServer.equals(THREAD_ID_PROMPT))
      {
        if((ClientSession != null) && (ClientSession.getThreadId() != 0))
          tmp = Long.toString(ClientSession.getThreadId());
        else
          tmp = NULL_STRING;
        out.println(tmp);
      }
      if (fromServer.equals(PASSCODE_PROMPT))
      {
        String pw = null;
        if(password == null)
        {
          passcode = con.readPassword(PASSCODE_PROMPT);
          pw = new String(passcode);
        }
        else
          pw = password;
        out.println(pw);
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
