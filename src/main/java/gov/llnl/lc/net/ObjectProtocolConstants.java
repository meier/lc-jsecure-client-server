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
 *        file: ObjectProtocolConstants.java
 *
 *  Created on: Jun 27, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

/**********************************************************************
 * Describe purpose and responsibility of ObjectProtocolConstants
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Jun 27, 2011 10:02:35 AM
 **********************************************************************/
public interface ObjectProtocolConstants extends NetworkConstants
{
  /* the Servers, or Protocols STATE */
  static final int WAITING      = 0;
  static final int ACTIVE       = 1;
  static final int STRING_MODE  = 2;
  static final int CMD_ARGS_MODE= 3;  
  static final int OBJECT_MODE  = 4;  

  static final String SERV_PROTO_TYPE_KEY       = "Server.SerialObjectProtocol.classname";
  static final String SERV_PROTO_DEFAULT_TYPE   = "gov.llnl.lc.net.KKObjectProtocol";

  /* standard protocol constants */
  static final String INITIAL_PROMPT            = "Ready";
  static final String FINAL_PROMPT              = "Bye";
  static final String UNRECOGNIZED_PROMPT       = "Unrecognized Command";

  static final String CLOSE_SESSION             = "closeSession";
  static final String BAD_CONNECTION            = "Connection not established";

  static final String GET_SERVER_STATUS         = "getServerStatus";
  static final String GET_SESSION_STATUS        = "getSessionStatus";
  static final String CLEAR_SESSION_HISTORY     = "clearSessionHistory";
  static final String KILL_SESSION              = "killSession";
  
  static final String CONNECTION_TIMEOUT        = "Connection read timed out";
  static final String CONNECTION_CLOSED         = "Connection closed by remote host";
}

