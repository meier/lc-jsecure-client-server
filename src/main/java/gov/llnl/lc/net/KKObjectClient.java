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
 *        file: KKObjectClient.java
 *
 *  Created on: Jun 17, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.security.AbstractAuthenticationResponder;
import gov.llnl.lc.security.AuthenticationResponder;
import gov.llnl.lc.security.KeyStoreTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**********************************************************************
 * Describe purpose and responsibility of KKObjectClient
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 17, 2011 11:45:25 AM
 **********************************************************************/
public class KKObjectClient implements NetworkConstants, CommonLogger
{
  static String HostName = null;
  static int PortNum = 0;
  
  public static SSLSocket getSSLSocket() throws Exception
  {
    NetworkProperties prop = new NetworkProperties();
    /* get the Hostname and portnumber for the service */    
    String hostName = prop.getHostName();
    PortNum = prop.getPortNumber();
    
    SSLSocket sslSock = null;
    boolean connected = false;
    int num_tries = 0;
    
    if(hostName != null)
      HostName = hostName;

    logger.info("HostName: " + HostName + ", and PortNum: " + PortNum);


    while (!connected && (num_tries < 2))
    {
      num_tries++;
      KeyStore ks = KeyStoreTools.getJKS_KeyStore(true, null);
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(KeyStoreTools.getKeyManagers(true, ks, null), KeyStoreTools.getTrustManagers(ks), null);
      SSLSocketFactory factory = (SSLSocketFactory) sslcontext.getSocketFactory();

      try
      {
        sslSock = (SSLSocket) factory.createSocket(HostName, PortNum);
      }
      catch (IOException e)
      {
        logger.severe("1Exception: " + e.getMessage());
        logger.severe("Could not listen on port:" + PortNum);
        System.exit(-1);
      }

      try
      {
        sslSock.setSoTimeout(10000);
        sslSock.startHandshake();
        connected = true;
      }
      catch (SSLHandshakeException e)
      {  
        // why am I here, timeout fail or handshake
        logger.severe("Some sort of handshake exception (" + e.getLocalizedMessage() + ")");
        logger.warning("Assuming I need to install the servers certificates, getting now");
        /* the handshake failed, so the certs must need to be installed */
        KeyStoreTools.installCerts(ks, HostName, null);
      }
    }
    return sslSock;
  }

  public static void main(String[] args) throws Exception
  {
    SSLSocket sslSock = null;
    PrintWriter out = null;
    BufferedReader in = null;
    java.io.ObjectOutputStream os = null;
    java.io.ObjectInputStream is = null;
    boolean authenticated = false;
    AuthenticationResponder optAuthRspnd = AbstractAuthenticationResponder.getAuthenticationResponder();

    try
    {
      sslSock = getSSLSocket();
      out = new PrintWriter(sslSock.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(sslSock.getInputStream()));
      
      os = new java.io.ObjectOutputStream(sslSock.getOutputStream());
      is = new java.io.ObjectInputStream(sslSock.getInputStream());

    }
    catch (UnknownHostException e)
    {
      logger.severe("Don't know about host: " + HostName);
      System.exit(1);
    }
    catch (IOException e)
    {
      logger.severe("IOException: " + HostName + " ; " + e.getMessage());
      logger.severe("Couldn't get I/O for the connection to: " + HostName);
      System.exit(1);
    }

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    Object inObj;
    String fromServer;
    String fromUser;
    
    /* secure connection is established, now authenticate with user id and password */
    authenticated = optAuthRspnd.requestAuthentication(in, out);
    
    while (authenticated && (inObj = is.readObject()) != null)
    {
      if (inObj instanceof String)
      {
        fromServer = new String((String) inObj);

        System.out.println("Server: " + fromServer);
        if (fromServer.equals("Bye."))
          break;

        fromUser = stdIn.readLine(); // keyboard input from user
        if (fromUser != null)
        {
          System.out.println("Client: " + fromUser);
          os.writeObject(new String(fromUser));
        }
      }
      else
      {
        logger.severe("Did not recieve a string as expected");
      }
    }
    os.close();
    is.close();

    out.close();
    in.close();
    stdIn.close();
    sslSock.close();
  }
}
