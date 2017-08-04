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
 *        file: ObjectSession.java
 *
 *  Created on: Jun 29, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import java.io.Serializable;
import java.util.Date;

import gov.llnl.lc.time.TimeStamp;
import gov.llnl.lc.util.SystemConstants;

/**********************************************************************
 * This Class maintains all of the relevant information about a
 * transaction (command/response) based session.  Abstract Objects are
 * sent as both commands and responses.
 * <p>
 * 
 * @author meier3
 * 
 * @version Jun 29, 2011 11:06:01 AM
 **********************************************************************/
public class ObjectSession implements Serializable, SystemConstants
{
  /** describe serialVersionUID here **/
  private static final long serialVersionUID = -2703741618886239963L;

  private Thread            sessionThread;
  private String            sessionName;
  private String            host;
  private String            user;
  private String            protocol;
  private String            clientProtocol;
  private String            authenticator;
  private int               port;
  private long              threadId;
  private TimeStamp         openTime         = new TimeStamp();
  private TimeStamp         closeTime        = new TimeStamp();

  /************************************************************
   * Method Name:
   *  ObjectSession
  **/
  /**
   * Constructs the object by populating it with required information.
   *
   * @param sessionName a name, or tag
   * @param host  the hosts name
   * @param port  the port number
   * @param threadId the process id for the sessions thread
   ***********************************************************/
  public ObjectSession(String sessionName, String host, int port, long threadId)
  {
    super();
    initSession(sessionName, host, null, null, port, threadId);
    openTime.setTime(new Date());
    closeTime.setTime(new Date()); // reset this when actually closed
  }
  
  /************************************************************
   * Method Name:
   *  ObjectSession
  **/
  /**
   * The default constructor.
    ***********************************************************/
  public ObjectSession()
  {
    this("session_name", "host_name", 0, 0);
  }
  
  private void initSession(String SessionName, String HostName, String UserName, String Protocol, int PortNum, long ThreadId)
  {
    this.sessionName = SessionName;
    this.host = HostName;
    this.user = UserName;
    this.port = PortNum;
    this.protocol = Protocol;
    this.threadId = ThreadId;
  }

  /************************************************************
   * Method Name:
   *  getSessionThread
  **/
  /**
   * The session runs in its own thread which can be obtained via this method
   *
   * @return  this sessions thread
   ***********************************************************/
  public Thread getSessionThread()
  {
    return sessionThread;
  }

  /************************************************************
   * Method Name:
   *  getUser
  **/
  /**
   * Obtains the users name.
   *
   * @return  the users name
    ***********************************************************/
  public String getUser()
  {
    return user;
  }

  /************************************************************
   * Method Name:
   *  setUser
  **/
  /**
   * Defines the users name.
   *
   * @param user
   ***********************************************************/
  public void setUser(String user)
  {
    this.user = user;
  }

  /************************************************************
   * Method Name:
   *  getAuthenticator
  **/
  /**
   * Returns the name of the transaction authenticator.
   *
   * @return the name of the authenticator
   ***********************************************************/
  public String getAuthenticator()
  {
    return authenticator;
  }

  /************************************************************
   * Method Name:
   *  setAuthenticator
  **/
  /**
   * Defines the name of the transaction authenticator.
   *
   * @param protocol
   ***********************************************************/
  public void setAuthenticator(String authenticator)
  {
    this.authenticator = authenticator;
  }

  /************************************************************
   * Method Name:
   *  getProtocol
  **/
  /**
   * Returns the name of the transaction protocol.
   *
   * @return the name of the protocol
   ***********************************************************/
  public String getProtocol()
  {
    return protocol;
  }

  /************************************************************
   * Method Name:
   *  setProtocol
  **/
  /**
   * Defines the name of the transaction protocol.
   *
   * @param protocol
   ***********************************************************/
  public void setProtocol(String protocol)
  {
    this.protocol = protocol;
  }

  /************************************************************
   * Method Name:
   *  getClientProtocol
  **/
  /**
   * Returns the name of the transaction protocol.
   *
   * @return the name of the protocol
   ***********************************************************/
  public String getClientProtocol()
  {
    return clientProtocol;
  }

  /************************************************************
   * Method Name:
   *  setClientProtocol
  **/
  /**
   * Defines the name of the transaction protocol.
   *
   * @param protocol
   ***********************************************************/
  public void setClientProtocol(String protocol)
  {
    clientProtocol = protocol;
  }

  /************************************************************
   * Method Name:
   *  getCloseTime
  **/
  /**
   * Returns the time a session was closed.  This may be useful for
   * maintaining record of how long a session was used.
   *
   * @see     #getOpenTime()
   *
   * @return  the time the connection was closed.
   ***********************************************************/
  public TimeStamp getCloseTime()
  {
    return closeTime;
  }

  /************************************************************
   * Method Name:
   *  setCloseTime
  **/
  /**
   * Defines the time this session was closed.
   *
   * @param closeTime
   ***********************************************************/
  public void setCloseTime(TimeStamp closeTime)
  {
    this.closeTime = closeTime;
  }

  /************************************************************
   * Method Name:
   *  getSessionName
  **/
  /**
   * A session can be named, and this method returns the
   * name.
   *
   * @return  the name given to the session (if any)
   ***********************************************************/
  public String getSessionName()
  {
    return sessionName;
  }

  /************************************************************
   * Method Name:
   *  getHost
  **/
  /**
   * Returns the servers name.
   *
   * @return  the servers name
   ***********************************************************/
  public String getHost()
  {
    return host;
  }

  /************************************************************
   * Method Name:
   *  setHost
  **/
  /**
   * Sets the value of host
   *
   * @param host the host to set
   *
   ***********************************************************/
  public void setHost(String host)
  {
    this.host = host;
  }

  /************************************************************
   * Method Name:
   *  getPort
  **/
  /**
   * Returns the servers port number that is supporting this
   * session.
   *
   * @return  servers port number for this session
   ***********************************************************/
  public int getPort()
  {
    return port;
  }

  /************************************************************
   * Method Name:
   *  getThreadId
  **/
  /**
   * Returns the thread id of this session on the server.  Useful
   * to help uniquely identify this session.
   * 
   * @return thread id value
   ***********************************************************/
  public long getThreadId()
  {
    return threadId;
  }

  /************************************************************
   * Method Name:
   *  getOpenTime
  **/
  /**
   * Returns the time the session was open.
   * 
   * @see     #getCloseTime()
   *
   * @return  a timestamp
   ***********************************************************/
  public TimeStamp getOpenTime()
  {
    return openTime;
  }

  @Override
  public String toString()
  {
    StringBuffer stringValue = new StringBuffer();

    stringValue.append("ObjectSession:  " + NEW_LINE);
    stringValue.append("  host:         " + host + NEW_LINE);
    stringValue.append("  user:         " + user + NEW_LINE);
    stringValue.append("  port:         " + port + NEW_LINE);
    stringValue.append("  threadId:     " + threadId + NEW_LINE);
    stringValue.append("  session name: " + sessionName + NEW_LINE);
    stringValue.append("  start time:   " + openTime + NEW_LINE);
    stringValue.append("  end time:     " + closeTime + NEW_LINE);
    return stringValue.toString();
  }

}
