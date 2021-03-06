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
 *        file: NetworkProperties.java
 *
 *  Created on: Jun 20, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import gov.llnl.lc.logging.CommonLogger;

import java.util.Properties;

/**********************************************************************
 * Describe purpose and responsibility of NetworkProperties
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 20, 2011 12:42:24 PM
 **********************************************************************/
public class NetworkProperties extends Properties implements NetworkConstants, CommonLogger
{

  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 671124420105515277L;

  public NetworkProperties()
  {
    this(NETWORK_DEFAULT_FILENAME);
  }

  public NetworkProperties(String fileName)
  {
    super();
    String propFile = System.getProperty(NETWORK_PROP_FILE, fileName);
    try
    {
      java.io.InputStream in = new java.io.FileInputStream(propFile);
      load(in);
      in.close();
    }
    catch (Exception e)
    {
      logger.severe("Error reading Network Properties File [" + propFile + "]");
    }
  }

  public String getHostName()
  {
    return getProperty(NET_SERVICE_HOSTNAME_KEY);
  }
  
  public int getPortNumber()
  {
    java.lang.Integer value = new java.lang.Integer(getProperty(NET_SERVICE_PORTNUM_KEY, NET_SERVICE_DEFAULT_PORTNUM));
    return value.intValue();
  }
  
  public int getSocketTimeout()
  {
    java.lang.Integer value = new java.lang.Integer(getProperty(NET_SOCK_READ_TIMEOUT_KEY, NET_SOCK_READ_DEFAULT_TIMEOUT));
    return value.intValue();
  }
  
  public String getDebugLevel()
  {
    return getProperty(NET_DEBUG_LEVEL_KEY, NET_DEBUG_DEFAULT_LEVEL);
  }
}
