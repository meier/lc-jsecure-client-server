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
 *        file: AuthenticationConstants.java
 *
 *  Created on: Jun 16, 2011
 *      Author: meier3
 ********************************************************************/package gov.llnl.lc.security;

public interface AuthenticationConstants
{
  static final String OUN_PROMPT                 = "Enter your OUN: ";
  static final String PASSCODE_PROMPT            = "Enter passcode (PIN + Token Code): ";
  static final String PASSWORD_PROMPT            = "Enter password: ";
  static final String HOST_PROMPT                = "Enter your Host name: ";
  static final String PORT_PROMPT                = "Enter your Port number: ";
  static final String THREAD_ID_PROMPT           = "Enter your Thread ID: ";
  static final String NULL_STRING                = "NULL_STRING";
  static final String AUTHENTICATED              = "Authenticated";
  static final String AUTHENTICATION_DENIED      = "Authentication Denied";

  static final String KEYSTORE_PROP_FILE         = "KeyStoreProperties.file";
  static final String KEYSTORE_DEFAULT_FILENAME  = "./KeyStore.properties";
  static final String KS_CLIENT_FILENAME_KEY     = "KeyStore.client.file";
  static final String KS_CLIENT_DEFAULT_FILENAME = "/.keystore";
  static final String KS_SERVER_FILENAME_KEY     = "KeyStore.server.file";
  static final String KS_SERVER_DEFAULT_FILENAME = "/jssecacerts";
  static final String KS_CLIENT_PASSCODE_KEY     = "KeyStore.client.passcode";
  static final String KS_SERVER_PASSCODE_KEY     = "KeyStore.server.passcode";
  static final String KS_DEFAULT_PASSCODE        = "changeit";
  static final String AUTH_PROP_FILE             = "AuthenticationProperties.file";
  static final String AUTH_DEFAULT_FILENAME      = "./Authentication.properties";
  static final String AUTH_PASSWORD_KEY          = "Authentication.password";
  static final String AUTH_TYPE_KEY              = "Authenticator.classname";
  static final String AUTH_LOCALHOST_ALLOW       = "Authenticator.localhost.allow";
  static final String AUTH_DEFAULT_TYPE          = "gov.llnl.lc.security.LdapOtpAuthenticator";
  static final String AUTH_RSPND_TYPE_KEY        = "AuthenticationResponder.classname";
  static final String AUTH_RSPND_DEFAULT_TYPE    = "gov.llnl.lc.security.LdapOtpAuthenticationResponder";
}
