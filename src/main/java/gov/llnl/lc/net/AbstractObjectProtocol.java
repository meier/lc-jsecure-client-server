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
 *
 *        file: AbstractObjectProtocol.java
 *
 *  Created on: Jun 17, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;


/**********************************************************************
 * Describe purpose and responsibility of AbstractObjectProtocol
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 17, 2011 3:38:23 PM
 **********************************************************************/
public abstract class AbstractObjectProtocol implements SerialObjectProtocol
{
  private volatile static MultiThreadSSLServer server;
  private long threadId;

  /************************************************************
   * Method Name:
   *  getObjectProtocol
  **/
  /**
   * Dynamically determine the desired type of protocol, and 
   * return a new instance of it.  A Network Properties file is
   * used to specify the desired type.  This is basically a
   * protocol factory.
   *
   * @see     describe related java objects
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  public static SerialObjectProtocol getObjectProtocol()
  {
    NetworkProperties nProp = new NetworkProperties();
    SerialObjectProtocol proto = null;

    /* only interested in the class name of the protocol used by the service */    
    String protoClassName = nProp.getProperty(SERV_PROTO_TYPE_KEY, SERV_PROTO_DEFAULT_TYPE);
    
    Class<? extends AbstractObjectProtocol> protoA = null;
    try
    {
      protoA = Class.forName(protoClassName).asSubclass(AbstractObjectProtocol.class);
      proto = (SerialObjectProtocol)protoA.newInstance();
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
    return proto;
  }
  /************************************************************
   * Method Name:
   *  getServer
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.SerialObjectProtocol#getServer()
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @return
   ***********************************************************/
  
  @Override
  public MultiThreadSSLServer getServer()
  {
    // TODO Auto-generated method stub
    return server;
  }

  /************************************************************
   * Method Name:
   *  setServer
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.SerialObjectProtocol#setServer(gov.llnl.lc.net.MultiThreadSSLServer)
  
   * @param   describe the parameters
   *
   * @return  describe the value returned
   * @param server
   ***********************************************************/
  
  @Override
  public void setServer(MultiThreadSSLServer server)
  {
    this.server = server;
    
  }
  /************************************************************
   * Method Name:
   *  getId
   **/
  /**
   * Returns the value of threadId
   *
   * @return the threadId
   *
   ***********************************************************/
  
  public long getId()
  {
    return threadId;
  }
  /************************************************************
   * Method Name:
   *  setId
   **/
  /**
   * Sets the value of threadId
   *
   * @param threadId the threadId to set
   *
   ***********************************************************/
  public void setId(long threadId)
  {
    this.threadId = threadId;
  }


}
