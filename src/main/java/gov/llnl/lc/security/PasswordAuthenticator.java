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
 *        file: PasswordAuthenticator.java
 *
 *  Created on: Jun 16, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.logging.CommonLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**********************************************************************
 * This authenticator requires the client to provide a matching password.
 * The password is contained in a properties file specified by;
 * 
 * "-DAuthenticationProperties.file=/nfs/g0/meier3/jarBin/Authentication.properties"
 * 
 * Within the properties file, the password key is;
 * 
 * Authentication.password
 * 
 * @see gov.llnl.lc.security.AuthenticationConstants
 * @see #getPassword()
 * 
 * @author meier3
 *
 **********************************************************************/
public class PasswordAuthenticator  extends AbstractAuthenticator implements Authenticator, AuthenticationConstants, CommonLogger 
{

  /* (non-Javadoc)
   * @see gov.llnl.lc.security.Authenticator#isClientAuthenticated(java.io.BufferedReader, java.io.PrintWriter)
   */
  @Override
  public boolean isClientAuthenticated(BufferedReader in, PrintWriter out) throws IOException
  {
    boolean authenticated = false;
    String passcode = null;
    String inputLine;
    
    // immediately authenticate for localhost condition
    setUsername("local user");
    if(this.isAllowLocalHost() && this.isClientLocal())
    {
      logger.severe("Allowing LocalHost Authentication");
      out.println(AUTHENTICATED);
      return true;
    }

    setUsername("password user");

    /* get the password */
    out.println(PASSWORD_PROMPT);
    if ((inputLine = in.readLine()) != null)
      passcode = new String(inputLine);
    
    
    /* compare it against the systems static password */
    authenticated = passcode.equals(getPassword());

    out.println(authenticated ? AUTHENTICATED : AUTHENTICATION_DENIED);

    return authenticated;
  }

  /************************************************************
   * Method Name:
   *  getPassword
  **/
  /**
   * Gets a password from the systems authentication properties file
   *
   * @return  the password from the file, or null

   ************************************************************/
  protected String getPassword()
  {
    AuthenticationProperties prop = new AuthenticationProperties();
    /* only interested in the password */
    return prop.getProperty(AUTH_PASSWORD_KEY, null);
  }
}