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
 *        file: MultiSSLServerStatus.java
 *
 *  Created on: Jun 29, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.SystemConstants;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**********************************************************************
 * An object that contains information about a <code>MultiThreadSSLServer</code>.
 * <p>
 * @see  MultiThreadSSLServer
 * @see  ObjectSession
 * @see  TimeStamp
 *
 * @author meier3
 * 
 * @version Jun 29, 2011 12:41:04 PM
 **********************************************************************/
public class MultiSSLServerStatus  implements Serializable, CommonLogger, SystemConstants
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 4139830188528413083L;
  
  private String serverName; /* name of thread */
  private String host;
  private long threadId;
  private int PortNum = 0;
  private TimeStamp StartTime   = new TimeStamp();

  /** a list of sessions **/
  private java.util.ArrayList <ObjectSession> Current_Sessions;
  private java.util.ArrayList <ObjectSession> Historical_Sessions;
  
  /************************************************************
   * Method Name:
   *  MultiSSLServerStatus
  **/
  /**
   * Creates an object that contains information about a 
   * <code>MultiThreadSSLServer</code>.  It does this by
   * interrogating the service and populating its members.
   *
   * @param server the server to interrogate
   ***********************************************************/
  public MultiSSLServerStatus(MultiThreadSSLServer server)
  {
    PortNum = server.getPortNum();
    StartTime = MultiThreadSSLServer.getStartTime();    
    serverName = server.getName();
    try
    {
      host =  InetAddress.getLocalHost().getCanonicalHostName();
    }
    catch (UnknownHostException e)
    {
      // TODO Auto-generated catch block
      logger.severe(e.getStackTrace().toString());
    }
    threadId = server.getId();
    Current_Sessions = MultiThreadSSLServer.getCurrent_Sessions();
    Historical_Sessions = MultiThreadSSLServer.getHistorical_Sessions();
  }
  
  public String toString()
  {
    StringBuffer stringValue = new StringBuffer();

    stringValue.append("MultiSSLServerStatus:  " + NEW_LINE);
    stringValue.append("  host:         " + host + NEW_LINE);
    stringValue.append("  port:         " + PortNum + NEW_LINE);
    stringValue.append("  threadId:     " + threadId + NEW_LINE);
    stringValue.append("  service name: " + serverName + NEW_LINE);
    stringValue.append("  start time:   " + StartTime + NEW_LINE);
    stringValue.append( NEW_LINE);

    stringValue.append("Current Sessions: (" + Current_Sessions.size() + ")" + NEW_LINE);
    for (ObjectSession session : Current_Sessions) 
    {
      stringValue.append(session.toString()+ NEW_LINE);
    }

    stringValue.append("Historical Sessions: (" + Historical_Sessions.size() + ")"+ NEW_LINE);
    for (ObjectSession session : Historical_Sessions) 
    {
      stringValue.append(session.toString()+ NEW_LINE);
    }
    return stringValue.toString();
  }

  /************************************************************
   * Method Name:
   *  getServerName
   **/
  /**
   * Returns the value of serverName
   *
   * @return the serverName
   *
   ***********************************************************/
  
  public String getServerName()
  {
    return serverName;
  }

  /************************************************************
   * Method Name:
   *  getHost
   **/
  /**
   * Returns the value of host
   *
   * @return the host
   *
   ***********************************************************/
  
  public String getHost()
  {
    return host;
  }

  /************************************************************
   * Method Name:
   *  getThreadId
   **/
  /**
   * Returns the value of threadId
   *
   * @return the threadId
   *
   ***********************************************************/
  
  public long getThreadId()
  {
    return threadId;
  }

  /************************************************************
   * Method Name:
   *  getPortNum
   **/
  /**
   * Returns the value of portNum
   *
   * @return the portNum
   *
   ***********************************************************/
  
  public int getPortNum()
  {
    return PortNum;
  }

  /************************************************************
   * Method Name:
   *  getStartTime
   **/
  /**
   * Returns the value of startTime
   *
   * @return the startTime
   *
   ***********************************************************/
  
  public TimeStamp getStartTime()
  {
    return StartTime;
  }

  /************************************************************
   * Method Name:
   *  getCurrent_Sessions
   **/
  /**
   * Returns the value of current_Sessions
   *
   * @return the current_Sessions
   *
   ***********************************************************/
  
  public java.util.ArrayList<ObjectSession> getCurrent_Sessions()
  {
    return Current_Sessions;
  }

  /************************************************************
   * Method Name:
   *  getHistorical_Sessions
   **/
  /**
   * Returns the value of historical_Sessions
   *
   * @return the historical_Sessions
   *
   ***********************************************************/
  
  public java.util.ArrayList<ObjectSession> getHistorical_Sessions()
  {
    return Historical_Sessions;
  }
   

}
