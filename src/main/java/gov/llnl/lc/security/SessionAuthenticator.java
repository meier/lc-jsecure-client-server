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
 *        file: SessionAuthenticator.java
 *
 *  Created on: Jul 6, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.net.ObjectSession;
import gov.llnl.lc.stg.ldapotp.LdapOtpInterface;
import gov.llnl.lc.stg.ldapotp.LdapOtpNativeInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**********************************************************************
 * Describe purpose and responsibility of SessionAuthenticator
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jul 6, 2011 11:31:05 AM
 **********************************************************************/
public class SessionAuthenticator extends AbstractAuthenticator implements Authenticator, AuthenticationConstants, CommonLogger
{
  private static final LdapOtpInterface otpInt = new LdapOtpNativeInterface();
  

  /************************************************************
   * Method Name:
   *  isClientAuthenticated
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.security.Authenticator#isClientAuthenticated(java.io.BufferedReader, java.io.PrintWriter)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param in
   * @param out
   * @return
   * @throws IOException
   ***********************************************************/

  @Override
  public boolean isClientAuthenticated(BufferedReader in, PrintWriter out) throws IOException
  {
    // use the supplied I/O to get user id and passcode for authentication via
    // LDAP/OTP
    //
    // return true if user is authenticated, false otherwise
    // exit the server if we can't use LDAP/OTP

    boolean authenticated = false;
    String uname = null;
    String passcode = null;
    String inputLine;
    String parent_host = null;
    String parent_port = null;
    String parent_id = null;
    
    
    // immediately authenticate for localhost condition
    setUsername("local user");
    if(this.isAllowLocalHost() && this.isClientLocal())
    {
      logger.severe("Allowing LocalHost Authentication");
      out.println(AUTHENTICATED);
      return true;
    }

    /* get the OUN */
    out.println(OUN_PROMPT);
    if ((inputLine = in.readLine()) != null)
      setUsername(uname = new String(inputLine));
    
    /* get the parent session host name */
    out.println(HOST_PROMPT);
    if ((inputLine = in.readLine()) != null)
      parent_host = new String(inputLine);
    
    /* get the parent session port number */
    out.println(PORT_PROMPT);
    if ((inputLine = in.readLine()) != null)
      parent_port = new String(inputLine);
    
    /* get the parent session id number */
    out.println(THREAD_ID_PROMPT);
    if ((inputLine = in.readLine()) != null)
      parent_id = new String(inputLine);
    
    
    /* compare it against known authenticated sessions */
    authenticated = hasAuthenticatedSession(uname, parent_host, parent_port, parent_id);
        
   /* if not authenticated, then try ldapotp */
    if(!authenticated)
    {
      logger.info("Not authenticated using previous session, trying LdapOtp");
      if (!otpInt.isInterfaceAvailable())
      {
        logger.severe("The ldapotp interface (" + otpInt.getInterfaceName() + ") is not available");
        throw new IOException("The ldapotp interface (" + otpInt.getInterfaceName() + ") is not available");
      }

      /* get the OUN, if I don't already have it */
      if(this.getUsername() == null)
      {
      out.println(OUN_PROMPT);
      if ((inputLine = in.readLine()) != null)
        setUsername(uname = new String(inputLine));
      }
      else
        uname = this.getUsername();

      /* get the passcode */
      out.println(PASSCODE_PROMPT);
      if ((inputLine = in.readLine()) != null)
        passcode = new String(inputLine);
      
      /* don't pass in nulls */
      if (uname != null && passcode != null)
        try
        {
          authenticated = otpInt.OTP_Authenticate(uname, passcode);
        }
        catch (Exception e)
        {
          logger.severe(e.getMessage());
        }
    }

    out.println(authenticated ? AUTHENTICATED : AUTHENTICATION_DENIED);

    return authenticated;
  }
  
  private boolean hasAuthenticatedSession(String user_name, String parent_host, String parent_port, String parent_id)
  {
    int portNum = 0;
    long id = 0;
    boolean match = false;
    
   try
   {
    id = Long.parseLong(parent_id);
    portNum = Integer.parseInt(parent_port);
   }
   catch (NumberFormatException e)
   {
   }
    /* loop through the sessions, and find one that matches */
    ObjectSession os = this.ServerThread.protocol.getServer().getSession(id);
    if(os != null)
    {
      /* now make sure the rest match exactly */
      if((os.getHost().equals(parent_host)) && (os.getPort() == portNum) && (os.getUser().equals(user_name)))
        match = true;
      else
      {
        logger.severe("Id matched, but the rest didn't");
        logger.severe("user_name: " + os.getUser() + " vs " + user_name);
        logger.severe("parent_host: " + os.getHost() + " vs " + parent_host);
        logger.severe("parent_port: " + os.getPort() + " vs " + portNum);
        logger.severe("parent id: " + os.getThreadId() + " vs " + id);
      }
    }
    else
    {
      logger.info("could not find a matching thread id: " + id);
    }
    
    return match;
  }


}
