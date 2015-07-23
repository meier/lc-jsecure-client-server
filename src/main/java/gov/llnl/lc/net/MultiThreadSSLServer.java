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
 *        file: MultiThreadSSLServer.java
 *
 *  Created on: Jun 17, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import gov.llnl.lc.logging.CommonLogger;
import gov.llnl.lc.security.KeyStoreTools;
import gov.llnl.lc.time.TimeStamp;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

/**********************************************************************
 * A Secure Server capable of handling multiple connections, each in
 * its own thread.  The server itself runs in its own thread, such that
 * it can be created and started thusly;
 *       new MultiThreadSSLServer().start();
 * <p>
 * It
 * @see  NetworkProperties
 * @see  KeyStoreTools
 * @see  SSLServerSocket
 * @see  ServerSocketFactory
 * @see  ObjectServerThread
 * @see  TimeStamp
 *
 * @author meier3
 * 
 * @version Jun 17, 2011 4:29:04 PM
 **********************************************************************/
public class MultiThreadSSLServer extends Thread implements CommonLogger, ObjectSessionUpdater
{
  /** the data synchronization object **/
  private static Boolean semaphore            = new Boolean( true );
  
  /** the one and only <code>MultiThreadSSLServer</code> Singleton **/
  private volatile static MultiThreadSSLServer globalServer  = null;

  private static int PortNum = 0;
  private static TimeStamp StartTime   = new TimeStamp();

  /** boolean specifying whether the thread should continue **/
  private volatile boolean Continue_Listening = true;
  
  /** a list of sessions **/
  private volatile static java.util.ArrayList <ObjectServerThread> Current_Threads = new java.util.ArrayList<ObjectServerThread>();
  private volatile static java.util.ArrayList <ObjectSession> Current_Sessions = new java.util.ArrayList<ObjectSession>();
  private volatile static java.util.ArrayList <ObjectSession> Historical_Sessions = new java.util.ArrayList<ObjectSession>();
  
  /** a list of Listeners, interested in knowing when a message gets posted **/
  private volatile static java.util.ArrayList <ObjectSessionListener> Session_Listeners =
    new java.util.ArrayList<ObjectSessionListener>();

  /** logger for the class **/
  private static final java.util.logging.Logger classLogger =
      java.util.logging.Logger.getLogger( "MultiThreadSSLServer" );

  private MultiThreadSSLServer()
  {
    super("MultiThreadSSLServer");
//    Current_Threads     = new java.util.ArrayList<ObjectServerThread>();
//    Current_Sessions    = new java.util.ArrayList<ObjectSession>();
//    Historical_Sessions = new java.util.ArrayList<ObjectSession>();
    StartTime.setTime(new Date());
  }

  /**************************************************************************
   *** Method Name:
   ***     init
   **/
   /**
   *** Summary_Description_Of_What_init_Does.
   *** <p>
   ***
   *** @see          Method_related_to_this_method
   ***
   *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
   ***
   *** @return       Description_of_method_return_value__Delete_if_none
   ***
   *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
   **************************************************************************/

   public synchronized boolean init()
   {
     boolean success = false;
     
     logger.info("Initializing the MultiThreadSSLServer");
     
     /* do whatever it takes to initialize the service */
     
     this.start();
     
     return success;
   }
   /*-----------------------------------------------------------------------*/
   /**************************************************************************
    *** Method Name:
    ***     destroy
    **/
    /**
    *** Summary_Description_Of_What_startThread_Does.
    *** <p>
    ***
    *** @see          Method_related_to_this_method
    ***
    *** @param        Parameter_name  Description_of_method_parameter__Delete_if_none
    ***
    *** @return       Description_of_method_return_value__Delete_if_none
    ***
    *** @throws       Class_name  Description_of_exception_thrown__Delete_if_none
    **************************************************************************/

    public void destroy()
    {
      logger.info("Stopping the MultiThreadSSLServer");
      Continue_Listening = false;
    }
    /*-----------------------------------------------------------------------*/

   
  public SSLServerSocket getSSLServerSocket() throws Exception
  {
    NetworkProperties prop = new NetworkProperties();
    PortNum = prop.getPortNumber();
    SSLServerSocket SserverSocket = null;

    KeyStore ks = KeyStoreTools.getJKS_KeyStore(false, null);
    SSLContext sslcontext = SSLContext.getInstance("TLS");
    sslcontext.init(KeyStoreTools.getKeyManagers(false, ks, null), null, null);
    ServerSocketFactory ssf = sslcontext.getServerSocketFactory();

    try
    {
      SserverSocket = (SSLServerSocket) ssf.createServerSocket(PortNum);
    }
    catch (IOException e)
    {
      logger.severe("Exception: " + e.getMessage());
      logger.severe("Could not create socket on port: " + PortNum);
      classLogger.severe("Exception: " + e.getMessage());
      classLogger.severe("Could not create socket on port: " + PortNum);
      throw new IOException("Could not create socket on port: " + PortNum);
    }
    return SserverSocket;
  }
  
  public void run()
  {
//    MultiThreadSSLServer server = new MultiThreadSSLServer();
//    MultiThreadSSLServer server = this;
    SSLServerSocket SserverSocket = null;
    
    try
    {
      SserverSocket = getSSLServerSocket();
    }
    catch (IOException e)
    {
      logger.severe("IOException: " + e.getMessage());
      classLogger.severe("IOException: " + e.getMessage());
//      throw new IOException("2 Could not create socket on port: " + PortNum);
    }
    catch (Exception e)
    {
      logger.severe("Exception: " + e.getMessage());
      classLogger.severe("Exception: " + e.getMessage());
    }

    classLogger.info("Server up since: " + MultiThreadSSLServer.getStartTime());
    
    while (Continue_Listening)
    {
      classLogger.info("The number of current clients: " + MultiThreadSSLServer.numActiveSessions() + ", cummulative: " + MultiThreadSSLServer.numCumulativeSessions());
      try
      {
        /* keep track of these threads, id's should match those in session */
        new ObjectServerThread(SserverSocket.accept(), this).start();
      }
      catch (IOException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      // rate limit new connections by forcing a pause
      try
      {
        Thread.sleep(5);
      }
      catch (InterruptedException e)
      {
      }
    }
    try
    {
      SserverSocket.close();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException
  {
      new MultiThreadSSLServer().start();
  }

   public synchronized void addSession(ObjectSession session, ObjectServerThread thread )
   {
      classLogger.info("adding a Session Thread " + thread.getId() + " " + session.getThreadId() );
      Current_Threads.add(thread);
      Current_Sessions.add(session);
      Historical_Sessions.add(session);
      updateAllListeners(new ObjectSessionEvent(new Long(session.getThreadId()), SessionEvent.SESSION_EVENT_ADD));
   }
   
   public synchronized boolean removeSession(ObjectSession session, ObjectServerThread thread)
   {
     classLogger.info("removing a Session Thread" + thread.getId() + " " + session.getThreadId() );
     TimeStamp closeTime    = new TimeStamp();
     closeTime.setTime(new Date());
     session.setCloseTime(closeTime);
     Current_Threads.remove(thread);
     updateAllListeners(new ObjectSessionEvent(new Long(session.getThreadId()), SessionEvent.SESSION_EVENT_REMOVE));
      return Current_Sessions.remove(session);
   }

   public synchronized ObjectSession getSession(long id)
   {
     classLogger.info("finding a Session Thread (id: " + id + ")");
     /* not indexed by this id, so just iterage through the list */
     for (ObjectSession session : Current_Sessions) 
     {
       if(session.getThreadId() == id)
         return session;
     }
     classLogger.warning("could not find a Session Thread (id: " + id + ")");
     return null;
   }

   public synchronized boolean removeHistory()
   {
     /* clear the array, then copy current back in */
     Historical_Sessions.clear();
     return Historical_Sessions.addAll(Current_Sessions);
   }

   public synchronized boolean killSession(long id)
   {
     boolean killAttempt = false;
     ObjectServerThread sessionToKill;
     
     /* stop the thread, and then remove the Session */
     logger.info("request to kill a Session Thread (id: " + id + ")");
     classLogger.info("request to kill a Session Thread (id: " + id + ")");
     /* not indexed by this id, so just iterate through the list */
     for (ObjectServerThread session : Current_Threads) 
     {
       if(session.getId() == id)
       {
         logger.info("Killing Thread (id: " + id + ")");
         classLogger.info("Killing Thread (id: " + id + ")");
         killAttempt = true;
         sessionToKill = session;
         updateAllListeners(new ObjectSessionEvent(new Long(id), SessionEvent.SESSION_EVENT_KILL));
         sessionToKill.interrupt();
         return killAttempt;
       }
     }
     logger.warning("kill request failed: Session Thread (id: " + id + ") not found");
     classLogger.warning("kill request failed: Session Thread (id: " + id + ") not found");
     return killAttempt;
   }

   public static synchronized int numActiveSessions()
   {
     return Current_Sessions.size();
   }
   
   public static synchronized int numCumulativeSessions()
   {
     return Historical_Sessions.size();
   }
   
  public static synchronized TimeStamp getStartTime()
  {
    return StartTime;
  }

  public synchronized int getPortNum()
  {
    return PortNum;
  }
  
  public boolean isAllowLocalHost()
  {
    boolean allow = false;
    
    // FIXME - should not be dependent on authentication mechanism
    if(MultiSSLServerThread.otpAuth != null)
    {
      allow = MultiSSLServerThread.otpAuth.isAllowLocalHost();
    }
    return allow;
  }

  public static synchronized  java.util.ArrayList<ObjectSession> getCurrent_Sessions()
  {
    ArrayList<ObjectSession> rtnList = new java.util.ArrayList<ObjectSession>();
    for(ObjectSession s: Current_Sessions)
    {
      rtnList.add(s);      
      classLogger.info("Current Session ID: " + s.getThreadId());
    }
    return rtnList;
  }
  
  public static synchronized ArrayList<Long> getCurrentSessionIds()
  {
    ArrayList<Long> rtnList = new java.util.ArrayList<Long>();
    classLogger.info("Number of Current sessions: " + Current_Sessions.size());
    if (Current_Sessions.size() > 0)
    {
      // build up the array list
      for (ObjectSession s : Current_Sessions)
      {
        rtnList.add(new Long(s.getThreadId()));
        classLogger.info("Session ID: " + s.getThreadId());
      }
      classLogger.info("All of the session ids: " + rtnList);
    }
    return rtnList;
  }
    

  public  static synchronized java.util.ArrayList<ObjectSession> getHistorical_Sessions()
  {
    ArrayList<ObjectSession> rtnList = new java.util.ArrayList<ObjectSession>();
    for(ObjectSession s: Historical_Sessions)
    {
      rtnList.add(s);      
      classLogger.info("Historical Session ID: " + s.getThreadId());
    }
    return rtnList;
  }
  
  
  /**************************************************************************
   *** Method Name:
   ***     updateAllListeners
   ***
   **/
   /**
   *** Notifies all listeners that some session event has occurred.
   *** NOTE:  Must be synchronized, so only call from within a synchronized
   *** method.
   *** <p>
   ***
   **************************************************************************/
   private synchronized void updateAllListeners(ObjectSessionEvent sessionEvent)
   {
     int count = 0;
     classLogger.info("updating all session event listeners");
     if(Session_Listeners.size() < 1)
       classLogger.severe("there are no session event listeners to update!");
       
     for(ObjectSessionListener listener: Session_Listeners)
     {
       if(listener != null)
       {
         classLogger.info("ObjectSessionEvent listener #" + count++);
        listener.sessionUpdate(sessionEvent);
       }
     }
   }
   /*-----------------------------------------------------------------------*/

  /************************************************************
   * Method Name:
   *  addListener
  **/
  /**
   * Adds a listener object to this Updaters list, to be notified when
   * a session event occurs.  Generally, a listener invokes this method
   * to add itself to the list.
   *
   * @see gov.llnl.lc.net.ObjectSessionUpdater#addListener(gov.llnl.lc.net.ObjectSessionListener)
   *
   * @param listener
   ***********************************************************/
  
  @Override
  public synchronized void addListener(ObjectSessionListener listener)
  {
    classLogger.info("attempting to add session event listener");
    // add the listener
    if(listener != null)
    {
      classLogger.info("adding session event listener");
      if(!Session_Listeners.add(listener))
      {
        logger.severe("Could not add session event listener");
        classLogger.severe("Could not add session event listener");
      }
    }
  }

  /************************************************************
   * Method Name:
   *  removeListener
  **/
  /**
   * Describe the method here
   *
   * @see gov.llnl.lc.net.ObjectSessionUpdater#removeListener(gov.llnl.lc.net.ObjectSessionListener)
   *
   * @param listener
   * @return
   ***********************************************************/
  
  @Override
  public synchronized boolean removeListener(ObjectSessionListener listener)
  {
    classLogger.info("removing session event listener");
    if (Session_Listeners.remove(listener))
    {
     }
    return true;
  }
  
  /**************************************************************************
   *** Method Name:
   ***     getInstance
   **/
   /**
   *** Get the singleton MultiThreadSSLServer. This can be used if the application wants
   *** to share one server across the whole JVM.  Currently I am not sure
   *** how this ought to be used.
   *** <p>
   ***
   *** @return       the GLOBAL (or shared) MultiThreadSSLServer
   **************************************************************************/

   public static MultiThreadSSLServer getInstance()
   {
     synchronized( MultiThreadSSLServer.semaphore )
     {
       if ( globalServer == null )
       {
         globalServer = new MultiThreadSSLServer( );
       }
       return globalServer;
     }
   }
   /*-----------------------------------------------------------------------*/
 
   public Object clone() throws CloneNotSupportedException 
   {
     throw new CloneNotSupportedException(); 
   }
   

}
