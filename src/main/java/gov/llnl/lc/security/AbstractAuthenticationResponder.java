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
 *        file: AbstractAuthenticationResponder.java
 *
 *  Created on: Jun 16, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.security;

import gov.llnl.lc.net.ObjectSession;


/**********************************************************************
 * Describe purpose and responsibility of AbstractAuthenticationResponder
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 16, 2011 2:54:50 PM
 **********************************************************************/
public abstract class AbstractAuthenticationResponder implements AuthenticationConstants, AuthenticationResponder
{
  ObjectSession ClientSession = null;  
  protected boolean authenticated = false;


  /************************************************************
   * Method Name:
   *  isAuthenticated
   **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.security.AuthenticationResponder#isAuthenticated()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  public boolean isAuthenticated()
  {
    return authenticated;
  }

  public static AuthenticationResponder getAuthenticationResponder()
  {
    AuthenticationProperties prop = new AuthenticationProperties();
    AuthenticationResponder auth = null;
    /* only interested in the class name of the auth responder */    
    String authClassName = prop.getProperty(AUTH_RSPND_TYPE_KEY, AUTH_RSPND_DEFAULT_TYPE);
    
    Class<? extends AbstractAuthenticationResponder> authR = null;
    try
    {
      authR = Class.forName(authClassName).asSubclass(AbstractAuthenticationResponder.class);
      auth = (AuthenticationResponder)authR.newInstance();
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
   *  setClientSession
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.security.AuthenticationResponder#setClientSession(gov.llnl.lc.net.ObjectSession)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param ClientSession
   * @return
   ***********************************************************/
  
  @Override
  public void setClientSession(ObjectSession ClientSession)
  {
    this.ClientSession = ClientSession;
    return;
  }


}
