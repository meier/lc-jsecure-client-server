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
 *        file: ObjectServerThread.java
 *
 *  Created on: Jun 17, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import gov.llnl.lc.logging.CommonLogger;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLException;


/**********************************************************************
 * The server side thread that services the client requests.  This is an
 * instance of a MultiSSLServerThread, and each session gets one of these.
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 17, 2011 11:13:14 AM
 **********************************************************************/
public class ObjectServerThread extends Thread implements MultiSSLServerThread, ObjectProtocolConstants, CommonLogger
{
  /**  describe serialVersionUID here **/
  private static final long serialVersionUID = 7542610756680029688L;
  
  /** logger for the class **/
  private final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( getClass().getName() );

  private ObjectSession session;
  private MultiThreadSSLServer parentServer = null;
  private Socket socket = null;
  public SerialObjectProtocol protocol = AbstractObjectProtocol.getObjectProtocol();
  
 public ObjectServerThread(Socket socket, MultiThreadSSLServer server)
  {
    super("ObjectServerThread");
    this.parentServer = server;
    this.socket = socket;
    this.protocol.setServer(this.parentServer);
    this.protocol.setId(this.getId());
    this.otpAuth.setServerThread(this);
    session = new ObjectSession(this.getName(), socket.getInetAddress().getCanonicalHostName(), socket.getPort(), this.getId());
  }
  
  /************************************************************
 * Method Name:
 *  getSession
 **/
/**
 * Returns the value of session
 *
 * @return the session
 *
 ***********************************************************/

public ObjectSession getSession()
{
  return session;
}

  public void run()
  {
    java.io.ObjectOutputStream os = null;
    java.io.ObjectInputStream is = null;

    PrintWriter out = null;
    BufferedReader in = null;
    
    boolean initialized = false;
    
    // connection accepted, running this thread, so looks like a new client
    parentServer.addSession(session, this);
    try
    {
      os = new java.io.ObjectOutputStream(socket.getOutputStream());
      is = new java.io.ObjectInputStream(socket.getInputStream());

      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      
      initialized = true;

      Object inObj;
      Object outObj;
      
      classLogger.info("The socket address is: " + socket.getInetAddress() + " port: " + socket.getPort());

      if(otpAuth.isClientAuthenticated(in, out))
      {
        session.setUser(otpAuth.getUsername());
        session.setAuthenticator(otpAuth.getClass().getCanonicalName());
        session.setProtocol(protocol.getClass().getCanonicalName());
        session.setClientProtocol(protocol.getClientProtocolName());
        
        /* protocol came from runtime examination of properties file */
        outObj = protocol.processInput(null);
        os.writeObject(outObj);
        while ((!isInterrupted()) && ((inObj = is.readObject()) != null))
        {
          outObj = protocol.processInput(inObj);
          os.writeObject(outObj);
          if((outObj instanceof String) && (FINAL_PROMPT.equals((String)outObj)))
            break;
        }
      }
      os.close();
      is.close();
      out.close();
      in.close();

      /* signal the parent that this connection is closing */
      classLogger.info("The socket address: " + socket.getInetAddress() + " port: " + socket.getPort() + " is closing");
      parentServer.removeSession(session, this);
      socket.close();
    }
    catch (InterruptedIOException e)
    {
      if(initialized)
      {
        /* close down the socket */
          try
          {
            os.close();
          is.close();
          out.close();
          in.close();
          /* signal the parent that this connection is closing */
          classLogger.info("The socket address: " + socket.getInetAddress() + " port: " + socket.getPort() + " is closing");
          parentServer.removeSession(session, this);
          socket.close();
          }
          catch (IOException e1)
          {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }          
      }
      /* Propagate the interrupt, in case of nesting */
      Thread.currentThread().interrupt();
      logger.severe("Interrupted via InterruptedIOException");
      classLogger.severe("Interrupted via InterruptedIOException");
    }
    catch (EOFException e)
    {
      /* this happens when the client closes the object stream, which is normal */
      /* safe to trap and ignore? */
      parentServer.removeSession(session, this);
    }
    catch (SSLException e)
    {
      /* the handshake failed, so the certs must need to be installed - the clients responsibility */
      /* safe to trap and ignore */
      parentServer.removeSession(session, this);
    }
    catch (IOException e)
    {
      if (!isInterrupted())
      {
        logger.severe("Caught IO Exception");
        logger.severe(e.getStackTrace().toString());
        logger.severe(e.getMessage());
        classLogger.severe("Caught IO Exception");
        classLogger.severe(e.getStackTrace().toString());
        classLogger.severe(e.getMessage());
        e.printStackTrace();
      }
      else
      {
        logger.severe("Interrupted");
      }
//      parentServer.removeSession(session, this);
    }
    catch (Exception e)
    {
      /* should not happen, so give me a dump */
      logger.severe("Caught Generic Exception");
      logger.severe("Exception is: (" + e.toString() + ")");
      logger.severe(e.getMessage());
      logger.severe(e.getStackTrace().toString());
      classLogger.severe("Caught Generic Exception");
      classLogger.severe("Exception is: (" + e.toString() + ")");
      classLogger.severe(e.getMessage());
      classLogger.severe(e.getStackTrace().toString());
      parentServer.removeSession(session, this);
      e.printStackTrace();
    }
  }
}
