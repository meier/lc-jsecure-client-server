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
 *        file: AbstractAuthenticator.java
 *
 *  Created on: Jun 16, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.net.ObjectServerThread;


/**********************************************************************
 * Describe purpose and responsibility of AbstractAuthenticator
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 16, 2011 1:05:37 PM
 **********************************************************************/
public abstract class AbstractAuthenticator implements Authenticator, AuthenticationConstants
{
  ObjectServerThread ServerThread = null;
  String Username = null;
  static boolean allowLocalHost = false;  // common for all

  public String getUsername()
  {
    return Username;
  }

  protected void setUsername(String username)
  {
    Username = username;
  }

  public boolean isClientLocal()
  {
    boolean local = false;
    if((ServerThread != null) && (ServerThread.getSession() != null) && (ServerThread.getSession().getHost() != null))
      local = "localhost".compareTo(ServerThread.getSession().getHost())== 0;
    return local;
  }



  /************************************************************
   * Method Name:
   *  getAuthenticator
  **/
  /**
   * Looks in the properties file for the class name of the desired
   * type of authenticator
   *
   * @see     describe related java objects
   *
   * @return  returns the Class name of the authenticator
   ***********************************************************/
  public static Authenticator getAuthenticator()
  {
    AuthenticationProperties prop = new AuthenticationProperties();
    Authenticator auth = null;
    /* only interested in the class name of the authenticator */    
    String authClassName = prop.getProperty(AUTH_TYPE_KEY, AUTH_DEFAULT_TYPE);
    Boolean allowLocal = new java.lang.Boolean(prop.getProperty(AUTH_LOCALHOST_ALLOW, "false"));
    setAllowLocalHost( allowLocal.booleanValue());
    
    Class<? extends AbstractAuthenticator> authR = null;
    try
    {
      authR = Class.forName(authClassName).asSubclass(AbstractAuthenticator.class);
      auth = (Authenticator)authR.newInstance();
    }
    catch (ClassNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (InstantiationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return auth;
  }

  /************************************************************
   * Method Name:
   *  isAllowLocalHost
   **/
  /**
   * Returns the value of allowLocalHost.  If true, connections
   * from localhost are automatically allowed.  No user name
   * and password are required.
   *
   * @return the allowLocalHost
   *
   ***********************************************************/
  
  public boolean isAllowLocalHost()
  {
    return allowLocalHost;
  }

  /************************************************************
   * Method Name:
   *  setAllowLocalHost
   **/
  /**
   * Sets the value of allowLocalHost
   *
   * @param allowLocalHost the allowLocalHost to set
   *
   ***********************************************************/
  protected static void setAllowLocalHost(boolean AllowLocalHost)
  {
    allowLocalHost = AllowLocalHost;
  }

  /************************************************************
   * Method Name:
   *  setServer
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.security.Authenticator#setServer(gov.llnl.lc.net.MultiThreadSSLServer)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param server
   ***********************************************************/
  
  @Override
  public void setServerThread(ObjectServerThread server)
  {
    ServerThread = server;    
  }

}
